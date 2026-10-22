package org.cropster.roastingintelligence.tasks;

import com.cropster.roastingintelligence.config.FileSystemConfiguration;
import com.cropster.util.Collect;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.listener.AbstractMessagePublisher;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotConsistencyCheck;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.service.RoastPersister;
import org.cropster.roastingintelligence.util.BetweenBatchLogFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoastUploader extends AbstractMessagePublisher implements Runnable {
   private final Logger logger = LoggerFactory.getLogger(RoastUploader.class);
   private static final AtomicInteger _syncedCounter = new AtomicInteger();
   private final AppModel _appModel;
   private final CsarConnection _connection;
   private final RoastPersister _roastPersister;
   private final File[] _files;

   public RoastUploader(AppModel appModel, CsarConnection connection, RoastPersister roastPersister, File... files) {
      this._appModel = appModel;
      this._connection = connection;
      this._roastPersister = roastPersister;
      this._files = files;
   }

   private void uploadPreferences(File file) {
      try {
         ObjectMapper objectMapper = new ObjectMapper();
         JsonNode rootNode = objectMapper.readTree(new FileReader(file));
         this.logger.info("Uploading preferences for user {}", this._appModel.getUserName());
         this._connection.storeUserPreferences(objectMapper.writeValueAsString(rootNode));
         FileSystemConfiguration.Directories directories = this._appModel.getDirectories();
         file.renameTo(new File(directories.sentDirectory(), file.getName()));
      } catch (Exception var5) {
         this.logger.error("Error uploading user preferences", var5);
      }
   }

   private void uploadChannelPreferences(File file) {
      try {
         ObjectMapper objectMapper = new ObjectMapper();
         JsonNode rootNode = objectMapper.readTree(new FileReader(file));
         long machineId = rootNode.get("selectedMachineId").asLong();
         this.logger.info("Uploading preferences for machine {}", machineId);
         this._connection.storeMachinePreferences(machineId, objectMapper.writeValueAsString(rootNode));
         FileSystemConfiguration.Directories directories = this._appModel.getDirectories();
         file.renameTo(new File(directories.sentDirectory(), file.getName()));
      } catch (Exception var7) {
         this.logger.error("Error uploading machine preferences", var7);
      }
   }

   @Override
   public void run() {
      FileSystemConfiguration.Directories directories = this._appModel.getDirectories();
      File preferencesFile = new File(directories.outboxDirectory(), "preferences.json");
      if (preferencesFile.exists()) {
         this.uploadPreferences(preferencesFile);
      }

      File machinePreferencesFile = new File(directories.outboxDirectory(), "machine.json");
      if (machinePreferencesFile.exists()) {
         this.uploadChannelPreferences(machinePreferencesFile);
      }

      int counter = 0;

      try {
         for (File file : this._files) {
            if (Thread.interrupted()) {
               return;
            }

            Roast roast = this._roastPersister.load(file);
            if (roast.getLot().getId() == null) {
               this.logger.info("Uploading new file {}", file.getName());
               Lot roastedLot = this._connection.startRoast(roast);
               roast.setLot(roastedLot);
               this._roastPersister.save(roast);
            }

            if (Thread.interrupted()) {
               return;
            }

            if (roast.getLot().getId() != null && roast.getEndDate() != null) {
               this.logger.info("Uploading file {}", file.getName());
               Iterator<Measure> it = roast.getMeasures().values().iterator();

               while (it.hasNext()) {
                  Measure measure = it.next();
                  Double amount = measure.getAmount();
                  if (amount.isNaN() || amount.isInfinite()) {
                     this.logger.warn("Skipping invalid measure: {}", measure);
                     it.remove();
                  }
               }

               this._connection.finishRoast(roast);
               if (roast.getScheduledId() != null) {
                  Scheduled scheduled = new Scheduled();
                  scheduled.setId(roast.getScheduledId());
                  scheduled.setDone(true);
                  this._connection.updateScheduled(scheduled, roast.getLot());
               }

               this.logger.info("Uploaded {} successfully to C-sar with ID {}", file.getName(), roast.getLot().getIdTag());

               try {
                  this._roastPersister.move(roast);
               } catch (Exception var24) {
                  this.logger.error("Error moving roast", var24);
               }

               try {
                  this._roastPersister.compact(roast);
               } catch (Exception var23) {
                  this.logger.error("Error compacting roast", var23);
               }

               for (LotTransaction transaction : roast.getLot().getSources()) {
                  Lot lot = this._appModel.getData().getLots().get(transaction.getLot());
                  if (lot != null) {
                     this._connection.updateLot(lot);
                  }
               }

               try {
                  Map<Long, LotConsistencyCheck> checks = new LinkedHashMap<>(this._appModel.getData().getChecks());
                  Collection<LotConsistencyCheck> roastChecks = this._connection
                     .getLotConsistencyChecks(
                        this._appModel.getData().getMachines().get(roast.getMachine()).getGroup().getCode(), Arrays.asList(roast.getLot().getId())
                     );
                  checks.put(roast.getLot().getId(), (LotConsistencyCheck)Collect.first(roastChecks));
                  this._appModel.getData().setChecks(checks);
               } catch (Exception var22) {
                  this.logger.error("Error fetching checks", var22);
               }

               counter++;
               _syncedCounter.incrementAndGet();
            }
         }

         try {
            this._appModel.setRecentRoasts(this._roastPersister.loadRecentRoasts(this._appModel.getPreferences().getMaxRoastCount() + _syncedCounter.get()));
         } catch (Exception var21) {
            this.logger.error("Error loading roasts", var21);
         }

         this._appModel.fireRoastsUploaded(counter, null);

         try {
            File[] betweenBatchLogFiles = this._roastPersister.getFilesToUpload(BetweenBatchLogFilters.FILE_FILTER);
            if (betweenBatchLogFiles != null) {
               for (File betweenBatchLogFile : betweenBatchLogFiles) {
                  this.logger.info("Uploading file {}", betweenBatchLogFile.getName());
                  UnsavedBetweenBatchLog betweenBatchLog = this._roastPersister.loadBetweenBatchLog(betweenBatchLogFile);
                  if (!betweenBatchLog.getPreviousBatch().equals(betweenBatchLog.getNextBatch())) {
                     this._connection.storeBetweenBatchLog(betweenBatchLog);
                  } else {
                     this.logger.warn("Discarding empty between batch log");
                  }

                  betweenBatchLogFile.delete();
               }
            }
         } catch (Exception var25) {
            this.logger.error("Error uploading between batch log", var25);
         }
      } catch (Exception var26) {
         this._appModel.fireRoastsUploaded(-1, var26);
         throw new RuntimeException("Error uploading roasts", var26);
      } finally {
         if (counter <= 0 && this._files.length <= 0) {
            this.logger.info("Outbox empty. No files uploaded.");
         } else {
            this.logger.info("Uploaded {} of {} files from the outbox", counter, this._files.length);
         }
      }
   }
}
