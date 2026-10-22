package com.cropster.roastingintelligence.config;

import java.io.File;
import java.util.prefs.Preferences;
import local.OfflineBootstrap;

public class FileSystemConfiguration {
   @Deprecated
   static final String PREF_DATA_DIRECTORY = "dataDirectory";

   public static String createDataDirectory(String instance) {
      String suffix = File.separator + (instance != null ? instance : "default");
      return System.getProperty("user.home") + File.separator + "CropsterRoastingIntelligence" + suffix;
   }

   public FileSystemConfiguration.Directories directories(String var1, Preferences var2) {
      return OfflineBootstrap.directories();
   }

   public static record Directories(File dataDirectory) {
      private File getSubDirectory(String subDirName) {
         File subDirectory = new File(this.dataDirectory, subDirName);
         if (!subDirectory.exists()) {
            subDirectory.mkdirs();
         }

         return subDirectory;
      }

      public File outboxDirectory() {
         return this.getSubDirectory("outbox");
      }

      public File sentDirectory() {
         return this.getSubDirectory("sent");
      }

      public File logDirectory() {
         return this.getSubDirectory("log");
      }
   }
}
