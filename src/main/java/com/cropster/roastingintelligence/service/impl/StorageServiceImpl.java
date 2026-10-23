package com.cropster.roastingintelligence.service.impl;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.livegoals.LiveGoal;
import com.cropster.roastingintelligence.service.AlarmService;
import com.cropster.roastingintelligence.service.LiveGoalService;
import com.cropster.roastingintelligence.service.StorageService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import local.OfflineBootstrap;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.service.impl.JsonReader;
import org.cropster.roastingintelligence.service.impl.JsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageServiceImpl implements StorageService {
   private final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);
   private JsonReader jsonReader = new JsonReader();
   private JsonWriter jsonWriter;
   private File cacheDirectory;
   private File preferencesFile;
   private File preferencesOutboxFile;
   private File machinePreferencesFile;
   private File machineOutboxFile;
   private File cacheFile;
   private File alarmsFile;
   private File alarmsOutboxFile;
   private File liveGoalsFile;
   private File liveGoalsOutboxFile;

   public StorageServiceImpl(Version version, File rootDirectory, File outboxDirectory) {
      this.jsonWriter = new JsonWriter(version);
      this.cacheDirectory = new File(rootDirectory, "cache");
      this.preferencesFile = new File(rootDirectory, "preferences.json");
      this.preferencesOutboxFile = new File(outboxDirectory, "preferences.json");
      this.machinePreferencesFile = new File(rootDirectory, "machine.json");
      this.machineOutboxFile = new File(outboxDirectory, "machine.json");
      this.cacheFile = new File(this.cacheDirectory, "cache.json");
      this.alarmsFile = new File(rootDirectory, "alarms.json");
      this.alarmsOutboxFile = new File(outboxDirectory, "alarms.json");
      this.liveGoalsFile = new File(rootDirectory, "livegoals.json");
      this.liveGoalsOutboxFile = new File(outboxDirectory, "livegoals.json");
   }

   @Override
   public boolean isPreferencesAvailable() {
      return this.preferencesFile.exists();
   }

   @Override
   public Preferences loadPreferences() throws IOException {
      if (this.preferencesFile.exists()) {
         Preferences var2;
         try (FileReader fileReader = new FileReader(this.preferencesFile, Defaults.CHARSET)) {
            var2 = this.jsonReader.readPreferences(fileReader);
         }

         return var2;
      } else {
         return null;
      }
   }

   @Override
   public Preferences loadMachinePreferences() throws IOException {
      if (this.machinePreferencesFile.exists()) {
         Preferences var2;
         try (FileReader fileReader = new FileReader(this.machinePreferencesFile, Defaults.CHARSET)) {
            var2 = this.jsonReader.readPreferences(fileReader);
         }

         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void savePreferences(Preferences preferences) throws IOException {
      this.jsonWriter.write(new FileWriter(this.preferencesFile, Defaults.CHARSET), preferences);
      this.jsonWriter.writeMachinePreferences(new FileWriter(this.machinePreferencesFile, Defaults.CHARSET), preferences);
      Files.copy(this.preferencesFile.toPath(), this.preferencesOutboxFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      Files.copy(this.machinePreferencesFile.toPath(), this.machineOutboxFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
   }

   @Override
   public Data loadDataCache() {
      return OfflineBootstrap.data();
   }

   @Override
   public void saveDataCache(Data data) {
      try {
         this.logger.info("Saving cache");
         this.cacheDirectory.mkdirs();
         this.jsonWriter.write(new FileWriter(this.cacheFile, Defaults.CHARSET), data);
      } catch (Exception var3) {
         this.logger.error("Error saving cache", var3);
      }
   }

   @Override
   public List<Alarm> loadAlarms() {
      List<Alarm> read = null;

      try {
         read = this.jsonReader.readAlarms(new FileReader(this.alarmsFile, Defaults.CHARSET));
         this.logger.debug("Alarms loaded from file [{}].", this.alarmsFile.getAbsolutePath());
      } catch (IOException var6) {
         this.logger.warn("Could not load alarms from file [{}]. Trying backup...", var6.getMessage());
         File backupFile = new File(this.alarmsFile.getAbsolutePath() + ".bak");

         try {
            read = this.jsonReader.readAlarms(new FileReader(backupFile, Defaults.CHARSET));
            this.logger.debug("Alarms loaded from backup file [{}].", backupFile.getAbsolutePath());
         } catch (IOException var5) {
            this.logger.warn("Could not load alarms from backup file [{}].", var6.getMessage());
         }
      }

      return read;
   }

   @Override
   public void saveAlarms(List<Alarm> alarms) throws IOException {
      File temporaryAlarmsFile = File.createTempFile(AlarmService.class.getSimpleName() + ".alarms-", ".json");
      Path path = Paths.get(temporaryAlarmsFile.getAbsolutePath());

      try (BufferedWriter wr = Files.newBufferedWriter(path, Defaults.CHARSET)) {
         this.jsonWriter.writeAlarms(wr, alarms);
         this.logger.debug("Alarms saved to temporary file [{}].", temporaryAlarmsFile.getAbsolutePath());
      } catch (Exception var13) {
         this.logger.error("Error saving temporary file [{}].", temporaryAlarmsFile.getAbsolutePath(), var13);
         throw var13;
      }

      File backupFile = new File(this.alarmsFile.getAbsolutePath() + ".bak");
      if (backupFile.exists()) {
         try {
            Files.delete(backupFile.toPath());
         } catch (IOException var11) {
            this.logger.warn("Error deleting alarms backup file [{}].", backupFile.getAbsolutePath(), var11);
         }
      }

      if (this.alarmsFile.exists()) {
         try {
            Files.move(this.alarmsFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.logger.debug("Previous alarms file backed up to [{}].", backupFile.getAbsolutePath());
         } catch (IOException var10) {
            this.logger
               .warn("Error copying alarms file to backup [{} -> {}]. ", new Object[]{this.alarmsFile.getAbsolutePath(), backupFile.getAbsolutePath(), var10});
         }
      }

      try {
         Files.move(temporaryAlarmsFile.toPath(), this.alarmsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         this.logger.debug("Temporary alarms file moved to [{}].", this.alarmsFile.getAbsolutePath());
      } catch (IOException var9) {
         this.logger
            .error(
               "Error copying saved data from temporary file to the alarms one [{} -> {}].",
               new Object[]{temporaryAlarmsFile.getAbsolutePath(), this.alarmsFile.getAbsolutePath(), var9}
            );
         throw var9;
      }

      try {
         Files.copy(this.alarmsFile.toPath(), this.alarmsOutboxFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         this.logger.debug("Alarms file copied to [{}].", this.alarmsOutboxFile.getAbsolutePath());
      } catch (IOException var8) {
         this.logger
            .error(
               "Error copying alarms file to the outbox one [{} -> {}]. ",
               new Object[]{this.alarmsFile.getAbsolutePath(), this.alarmsOutboxFile.getAbsolutePath(), var8}
            );
         throw var8;
      }
   }

   @Override
   public List<LiveGoal> loadLiveGoals() {
      List<LiveGoal> read = null;
      Path path = Paths.get(this.liveGoalsFile.getAbsolutePath());

      try (BufferedReader br = Files.newBufferedReader(path, Defaults.CHARSET)) {
         read = this.jsonReader.readLiveGoals(br);
         this.logger.debug("Live goals loaded from file [{}].", this.liveGoalsFile.getAbsolutePath());
      } catch (IOException var13) {
         this.logger.warn("Could not load live goals from file [{}]. Trying backup...", var13.getMessage());
         File backupFile = new File(this.liveGoalsFile.getAbsolutePath() + ".bak");
         path = Paths.get(this.liveGoalsFile.getAbsolutePath());

         try (BufferedReader brx = Files.newBufferedReader(path, Defaults.CHARSET)) {
            read = this.jsonReader.readLiveGoals(brx);
            this.logger.debug("Live goals loaded from backup file [{}].", backupFile.getAbsolutePath());
         } catch (IOException var11) {
            this.logger.warn("Could not load live goals from backup file [{}].", var13.getMessage());
         }
      }

      return read;
   }

   @Override
   public void saveLiveGoals(List<LiveGoal> liveGoals) throws IOException {
      File temporaryLiveGoalsFile = File.createTempFile(LiveGoalService.class.getSimpleName() + ".livegoals-", ".json");
      this.writeLiveGoalsToTemporaryFile(liveGoals, temporaryLiveGoalsFile);
      File backupFile = new File(this.liveGoalsFile.getAbsolutePath() + ".bak");
      this.deleteLiveGoalsBackupFile(backupFile);
      this.backupCurrentLiveGoalsFile(backupFile);
      this.moveLiveGoalsTemporaryFileToFinal(temporaryLiveGoalsFile);
      this.copyLiveGoalsFileToOutbox();
   }

   private void writeLiveGoalsToTemporaryFile(List<LiveGoal> liveGoals, File temporaryLiveGoalsFile) throws IOException {
      Path path = Paths.get(temporaryLiveGoalsFile.getAbsolutePath());

      try {
         try (BufferedWriter wr = Files.newBufferedWriter(path, Defaults.CHARSET)) {
            this.jsonWriter.writeLiveGoals(wr, liveGoals);
            this.logger.debug("Live goals saved to temporary file [{}].", temporaryLiveGoalsFile.getAbsolutePath());
         }
      } catch (Exception var9) {
         this.logger.error("Error saving temporary file [{}].", temporaryLiveGoalsFile.getAbsolutePath(), var9);
         throw var9;
      }
   }

   private void deleteLiveGoalsBackupFile(File backupFile) {
      if (backupFile.exists()) {
         try {
            Files.delete(backupFile.toPath());
         } catch (IOException var3) {
            this.logger.warn("Error deleting live goals backup file [{}].", backupFile.getAbsolutePath(), var3);
         }
      }
   }

   private void backupCurrentLiveGoalsFile(File backupFile) {
      if (this.liveGoalsFile.exists()) {
         try {
            Files.move(this.liveGoalsFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.logger.debug("Previous live goals file backed up to [{}].", backupFile.getAbsolutePath());
         } catch (IOException var3) {
            this.logger
               .warn(
                  "Error copying live goals file to backup [{} -> {}]. ",
                  new Object[]{this.liveGoalsFile.getAbsolutePath(), backupFile.getAbsolutePath(), var3}
               );
         }
      }
   }

   private void moveLiveGoalsTemporaryFileToFinal(File temporaryLiveGoalsFile) throws IOException {
      try {
         Files.move(temporaryLiveGoalsFile.toPath(), this.liveGoalsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         this.logger.debug("Temporary live goals file moved to [{}].", this.liveGoalsFile.getAbsolutePath());
      } catch (IOException var3) {
         this.logger
            .error(
               "Error copying saved data from temporary file to the live goals one [{} -> {}].",
               new Object[]{temporaryLiveGoalsFile.getAbsolutePath(), this.liveGoalsFile.getAbsolutePath(), var3}
            );
         throw var3;
      }
   }

   private void copyLiveGoalsFileToOutbox() throws IOException {
      try {
         Files.copy(this.liveGoalsFile.toPath(), this.liveGoalsOutboxFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         this.logger.debug("Live goals file copied to [{}].", this.liveGoalsOutboxFile.getAbsolutePath());
      } catch (IOException var2) {
         this.logger
            .error(
               "Error copying live goals file to the outbox one [{} -> {}]. ",
               new Object[]{this.liveGoalsFile.getAbsolutePath(), this.liveGoalsOutboxFile.getAbsolutePath(), var2}
            );
         throw var2;
      }
   }
}
