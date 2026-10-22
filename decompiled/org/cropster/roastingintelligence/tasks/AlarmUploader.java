package org.cropster.roastingintelligence.tasks;

import com.cropster.roastingintelligence.config.FileSystemConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileReader;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.listener.AbstractMessagePublisher;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmUploader extends AbstractMessagePublisher implements Runnable {
   private final Logger logger = LoggerFactory.getLogger(AlarmUploader.class);
   private final AppModel appModel;
   private final CsarConnection connection;

   public AlarmUploader(AppModel appModel, CsarConnection connection) {
      this.appModel = appModel;
      this.connection = connection;
   }

   @Override
   public void run() {
      FileSystemConfiguration.Directories directories = this.appModel.getDirectories();
      File alarmsFile = new File(directories.outboxDirectory(), "alarms.json");
      if (alarmsFile.exists()) {
         this.uploadAlarms(alarmsFile);
      }
   }

   private void uploadAlarms(File file) {
      try {
         ObjectMapper objectMapper = new ObjectMapper();
         JsonNode rootNode = objectMapper.readTree(new FileReader(file));
         this.logger.info("Uploading alarms for user {}", this.appModel.getUserName());
         this.connection.storeAlarmPreferences(objectMapper.writeValueAsString(rootNode));
         FileSystemConfiguration.Directories directories = this.appModel.getDirectories();
         file.renameTo(new File(directories.sentDirectory(), file.getName()));
      } catch (Exception var5) {
         this.logger.error("Error uploading alarms preferences.", var5);
      }
   }
}
