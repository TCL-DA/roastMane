package org.cropster.roastingintelligence.service.impl;

import com.cropster.util.model.Unit;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import local.OfflinePersistence;
import org.apache.commons.io.FileUtils;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.service.RoastPersister;
import org.cropster.roastingintelligence.util.FileComparator;
import org.cropster.roastingintelligence.util.NanoTime;
import org.cropster.roastingintelligence.util.RoastFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePersister implements RoastPersister {
   public static final String BETWEEN_BATCH_LOG_PREFIX = "betweenbatchlog-";
   private final Logger logger = LoggerFactory.getLogger(FilePersister.class);
   private final Map<Roast, File> _filesMap = new WeakHashMap<>();
   private final File _outboxDir;
   private final File _sentDirectory;
   private final JsonReader _jsonReader;
   private final JsonWriter _jsonWriter;
   private final DateFormat _fileNameDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
   private volatile boolean isAtomicMoveSupported = true;

   public FilePersister(Version version, File outboxDir, File sentDirectory) {
      if (outboxDir.isDirectory() && sentDirectory.isDirectory()) {
         this._outboxDir = outboxDir;
         this._sentDirectory = sentDirectory;
         this._jsonReader = new JsonReader();
         this._jsonWriter = new JsonWriter(version);
      } else {
         throw new IllegalArgumentException("Just specify directories!");
      }
   }

   private String createRoastFileName(Date date) {
      return String.format(Locale.US, "%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_%1$tL.json", date);
   }

   private String createFileName(Roast roast) {
      return this.createRoastFileName(roast.getStartDate());
   }

   private String createBetweenBatchLogFileName(Date date) {
      return String.format(Locale.US, "betweenbatchlog-%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_%1$tL.json", date);
   }

   @Override
   public File[] getFilesToUpload(FileFilter var1) {
      return new File[0];
   }

   @Override
   public synchronized Roast load(File file) throws IOException {
      FileReader fileReader = new FileReader(file, Defaults.CHARSET);
      Roast roast = this._jsonReader.read(fileReader);
      this._filesMap.put(roast, file);
      return roast;
   }

   @Override
   public synchronized List<Roast> loadRecentRoasts(int maxRoasts) throws Exception {
      Set<File> syncedFiles = new HashSet<>();
      List<File> fileList = new ArrayList<>();
      File[] files = this._sentDirectory.listFiles(RoastFilters.FILE_FILTER);
      if (files != null) {
         Collections.addAll(fileList, files);
         Collections.addAll(syncedFiles, files);
      }

      files = this._outboxDir.listFiles(RoastFilters.FILE_FILTER);
      if (files != null) {
         Collections.addAll(fileList, files);
      }

      Collections.sort(fileList, Collections.reverseOrder(new FileComparator()));
      List<Roast> recentRoasts = new ArrayList<>();

      for (File file : fileList) {
         if (recentRoasts.size() >= maxRoasts || recentRoasts.size() >= 100) {
            break;
         }

         Roast roast;
         try {
            roast = this.load(file);
         } catch (Exception var11) {
            this.logger.error("Error loading file {}", file.getName(), var11);
            Date date = this._fileNameDateFormat.parse(file.getName().substring(0, 15));
            roast = new Roast();
            roast.getLot().setIdTag(org.cropster.roastingintelligence.util.Messages.m_("Error"));
            roast.setStartDate(date);
         }

         roast.setSynced(syncedFiles.contains(file));
         recentRoasts.add(roast);
      }

      return recentRoasts;
   }

   @Override
   public synchronized File save(Roast var1) throws IOException {
      return OfflinePersistence.save(this, var1, false);
   }

   @Override
   public synchronized File saveNew(Roast var1) throws IOException {
      return OfflinePersistence.save(this, var1, true);
   }

   @Override
   public synchronized File saveAsDemoRoast(Curve curve, String suffix, long multiplier) throws IOException {
      Curve convertedCurve = new Curve(curve.getUnit());

      for (Entry<Long, Double> entry : curve.entrySet()) {
         convertedCurve.put(Long.valueOf(entry.getKey() * multiplier), entry.getValue());
      }

      Lot lot = new Lot();
      lot.setIdTag("DEMO-" + suffix);
      Roast roast = new Roast();
      roast.setStartDate(new Date(NanoTime.currentTimeMillis()));
      roast.setStartWeight(new Measure(33.0, Unit.KG));
      roast.getCurves().put("beanTemperature", convertedCurve);
      File file = new File(FileUtils.getUserDirectory(), "demo" + suffix + ".json");
      this._jsonWriter.write(new PrintWriter(new FileWriter(file, Defaults.CHARSET), true), roast);
      return file;
   }

   @Override
   public synchronized File move(Roast roast) throws IOException {
      File file = this._filesMap.get(roast);
      File newFile = new File(this._sentDirectory, file.getName());

      try {
         if (this.isAtomicMoveSupported) {
            try {
               Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException var5) {
               this.logger.warn("Atomic move not supported, falling back to non-atomic version: {}", var5.getMessage());
               this.isAtomicMoveSupported = false;
               Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException var6) {
               this.logger.warn("Atomic move failed, trying non-atomic version: {}", var6.getMessage());
               Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
         } else {
            Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (IOException var7) {
         throw new IOException(MessageFormat.format("Unable moving {0} to {1}", file.getPath(), newFile.getPath()), var7);
      }

      this._filesMap.put(roast, newFile);
      roast.setSynced(true);
      return newFile;
   }

   @Override
   public synchronized boolean compact(Roast roast) throws IOException {
      if (!roast.getCurves().isEmpty()) {
         roast.getCurves().clear();
         this.save(roast);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public UnsavedBetweenBatchLog loadBetweenBatchLog(File file) throws IOException {
      UnsavedBetweenBatchLog betweenBatchLog = this._jsonReader.readBetweenBatchLog(new FileReader(file, Defaults.CHARSET));
      File previousFile = new File(this._sentDirectory, this.createRoastFileName(betweenBatchLog.getPreviousBatchDate()));
      Roast previousBatch = this.load(previousFile);
      betweenBatchLog.setPreviousBatch(previousBatch.getLot().getId());
      File nextFile = new File(this._sentDirectory, this.createRoastFileName(betweenBatchLog.getNextBatchDate()));
      Roast nextBatch = this.load(nextFile);
      betweenBatchLog.setNextBatch(nextBatch.getLot().getId());
      return betweenBatchLog;
   }

   @Override
   public synchronized File saveBetweenBatchLog(UnsavedBetweenBatchLog betweenBatchLog) throws IOException {
      File file = new File(this._outboxDir, this.createBetweenBatchLogFileName(betweenBatchLog.getNextBatchDate()));
      this._jsonWriter.writeBetweenBatchLog(new FileWriter(file), betweenBatchLog);
      return file;
   }
}
