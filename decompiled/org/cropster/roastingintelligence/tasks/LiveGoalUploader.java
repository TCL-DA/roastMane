package org.cropster.roastingintelligence.tasks;

import com.cropster.roastingintelligence.config.FileSystemConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.listener.AbstractMessagePublisher;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveGoalUploader extends AbstractMessagePublisher implements Runnable {
   private final Logger logger = LoggerFactory.getLogger(LiveGoalUploader.class);
   private final AppModel appModel;
   private final CsarConnection connection;

   public LiveGoalUploader(AppModel appModel, CsarConnection connection) {
      this.appModel = appModel;
      this.connection = connection;
   }

   @Override
   public void run() {
      FileSystemConfiguration.Directories directories = this.appModel.getDirectories();
      File liveGoalsFile = new File(directories.outboxDirectory(), "livegoals.json");
      if (liveGoalsFile.exists()) {
         this.uploadLiveGoals(liveGoalsFile);
      }
   }

   private void uploadLiveGoals(File file) {
      Path path = Paths.get(file.getAbsolutePath());

      try (BufferedReader br = Files.newBufferedReader(path, Defaults.CHARSET)) {
         ObjectMapper objectMapper = new ObjectMapper();
         JsonNode rootNode = objectMapper.readTree(br);
         this.logger.info("Uploading live goals for user {}", this.appModel.getUserName());
         this.connection.storeLiveGoalPreferences(objectMapper.writeValueAsString(rootNode));
         FileSystemConfiguration.Directories directories = this.appModel.getDirectories();
         if (!file.renameTo(new File(directories.sentDirectory(), file.getName()))) {
            this.logger
               .warn("Failed to move live goals file\n\t{}\nto sent directory\n\t{}", file.getAbsolutePath(), directories.sentDirectory().getAbsolutePath());
         }
      } catch (IOException var9) {
         this.logger.error("Error uploading live goals preferences.", var9);
      }
   }
}
