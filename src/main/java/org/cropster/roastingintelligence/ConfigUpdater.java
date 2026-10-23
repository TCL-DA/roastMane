package org.cropster.roastingintelligence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.util.Smoothing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUpdater implements Runnable {
   static final String LEGACY_REMEMBERED_SECRET_PREF_KEY = "password";
   private final Logger logger = LoggerFactory.getLogger(ConfigUpdater.class);
   private static final String KILL_SWITCH_KEY = "roastingMachineIDs";
   private static final String PREF_CLEANING_FILES_DONE = "cleaningFilesDone";
   private static final long MAX_CLEANING_TIME = 60000L;
   private final Version _version;
   private final Preferences _preferences;
   private final File _dataDirectory;
   private final ObjectMapper _preferencesMapper;

   private static String[] decodeStringArray(String encoded) {
      String[] parts = encoded.split("___");

      for (int i = 0; i < parts.length; i++) {
         if (parts[i] != null && (parts[i].equals("null") || parts[i].trim().length() == 0)) {
            parts[i] = null;
         }
      }

      return parts;
   }

   private static Color[] decodeColorArray(String encoded) {
      String[] parts = encoded.split("___");
      if (parts != null) {
         Color[] result = new Color[parts.length];

         for (int i = 0; i < parts.length; i++) {
            String[] colParts = parts[i].split(";");
            if (colParts != null && colParts.length == 3) {
               result[i] = new Color(Integer.parseInt(colParts[0]), Integer.parseInt(colParts[1]), Integer.parseInt(colParts[2]));
            }
         }

         return result;
      } else {
         return null;
      }
   }

   private static boolean[] decodeBooleanArray(String encoded) {
      String[] parts = encoded.split("___");
      if (parts == null) {
         return null;
      } else {
         boolean[] vals = new boolean[parts.length];

         for (int i = 0; i < parts.length; i++) {
            try {
               vals[i] = Boolean.parseBoolean(parts[i]);
            } catch (NumberFormatException var5) {
            }
         }

         return vals;
      }
   }

   private static int[] decodeIntArray(String encoded) {
      String[] parts = encoded.split("___");
      if (parts == null) {
         return null;
      } else {
         int[] vals = new int[parts.length];

         for (int i = 0; i < parts.length; i++) {
            try {
               vals[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException var5) {
            }
         }

         return vals;
      }
   }

   private static double[] decodeDoubleArray(String encoded) {
      String[] parts = encoded.split("___");
      if (parts == null) {
         return null;
      } else {
         double[] vals = new double[parts.length];

         for (int i = 0; i < parts.length; i++) {
            try {
               vals[i] = Double.parseDouble(parts[i]);
            } catch (NumberFormatException var5) {
            }
         }

         return vals;
      }
   }

   public ConfigUpdater(Version version, Preferences preferences, File dataDirectory) {
      this._version = version;
      this._preferences = preferences;
      this._dataDirectory = dataDirectory;
      this._preferencesMapper = new ObjectMapper();
      this._preferencesMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      this._preferencesMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
   }

   @Override
   public void run() {
      try {
         this.updateConfig();
      } catch (Exception var3) {
         this.logger.error("Error updating configuration", var3);
      }

      try {
         this.updatePreferences();
      } catch (Exception var2) {
         this.logger.error("Error updating preferences", var2);
      }
   }

   private void updateConfig() {
      String preferencesVersion = this._preferences.get("version", "0");
      if (Version.PATTERN.matcher(preferencesVersion).matches()) {
         Version version = new Version(preferencesVersion);
         if (version.compareTo(new Version("4.0-beta-1")) < 0) {
            try {
               ObjectMapper objectMapper = new ObjectMapper();
               String jsonChannels = this._preferences.get("channels", null);
               if (jsonChannels != null && jsonChannels.matches("\\[.+\\]")) {
                  String[] channels = (String[])objectMapper.readValue(jsonChannels, String[].class);

                  for (String channel : channels) {
                     String key = String.format("Channel.%s.ror", channel);
                     int ror = this._preferences.getInt(key, 0);
                     if (ror == 2) {
                        this._preferences.putInt(key, 5);
                     } else if (ror == 20) {
                        this._preferences.putInt(key, 15);
                     } else if (ror == 25) {
                        this._preferences.putInt(key, 30);
                     }
                  }
               }
            } catch (Exception var14) {
               this.logger.error("Error migrating RoR preferences", var14);
            }
         }

         if (version.compareTo(new Version("4.0-beta-1")) < 0) {
            this._preferences.putLong("syncTime", 0L);
         }

         if (version.compareTo(new Version("4.1.8")) < 0
            && this._preferences.get("ImfPlcSourceFactory.host", null) != null
            && this._preferences.get("ImfPlcSourceFactory.legacyPlc", null) == null) {
            this._preferences.put("ImfPlcSourceFactory.legacyPlc", "true");
         }

         if (version.compareTo(new Version("4.2.7-beta-12")) < 0) {
            try {
               Path logConfigFile = Paths.get(this._dataDirectory.getAbsolutePath(), "logging.properties");
               if (Files.exists(logConfigFile)) {
                  Path logConfigFileBak = logConfigFile.resolveSibling("logging.properties.bak");
                  Files.copy(logConfigFile, logConfigFileBak, StandardCopyOption.REPLACE_EXISTING);
                  new File(logConfigFile.toString()).deleteOnExit();
               }
            } catch (IOException var13) {
               this.logger.error("Error backing up logging.properties", var13);
            }
         }

         if (version.compareTo(new Version("4.4-beta-12")) < 0) {
            String allEvents = "[\"TEXT_COMMENT\",\"airflow\",\"colorChange\",\"firstCrack\",\"gas\",\"secondCrack\"]";
            this._preferences.put("enabledEventsChart", allEvents);
            this._preferences.put("enabledEventsSidebar", allEvents);
         }

         if (version.compareTo(new Version("4.5")) < 0) {
            int ror = this._preferences.getInt("Channel.beanTemperature.ror", 60);
            String rorSmoothing = this._preferences.get("rorSmoothing", Smoothing.STANDARD.name());
            if ("MEDIUM".equals(rorSmoothing)) {
               rorSmoothing = Smoothing.STANDARD.name();
            }

            if (version.compareTo(new Version("4.0")) < 0) {
               switch (ror) {
                  case 2:
                     ror = 5;
                     rorSmoothing = Smoothing.LOW.name();
                     break;
                  case 5:
                  case 10:
                     rorSmoothing = Smoothing.LOW.name();
                     break;
                  case 60:
                     rorSmoothing = Smoothing.HIGH.name();
               }
            }

            this._preferences.putInt("ror", ror);
            this._preferences.put("rorSmoothing", rorSmoothing);
         }

         if (version.compareTo(new Version("4.8")) < 0) {
            long tempTime = this._preferences.getLong("tempTime", -1L);
            long time = this._preferences.getLong("time", -1L);
            if (tempTime > 0L && time < 0L) {
               this._preferences.putLong("time", tempTime * 60L);
            }

            this._preferences.remove("tempTime");
         }

         if (version.compareTo(new Version("4.11.2")) < 0) {
            this._preferences.putLong("syncTime", 0L);
         }

         if (version.compareTo(new Version("5.8-beta-4")) < 0) {
            this._preferences.remove("clientId");
         }
      }

      this.removeLegacyPasswordPreferences();

      try {
         this._preferences.put("version", this._version.toString());
         this._preferences.flush();
         this._preferences.sync();
      } catch (BackingStoreException var12) {
         this.logger.error("Failed updating Java prefs", var12);
      }
   }

   private void updatePreferences() throws IOException {
      Collection<Runnable> updates = new ArrayList<>();
      File preferencesFile = new File(this._dataDirectory, "preferences.json");
      if (preferencesFile.exists()) {
         ObjectNode rootNode = (ObjectNode)this._preferencesMapper.readValue(new FileReader(preferencesFile), ObjectNode.class);
         Version version = new Version(rootNode.get("version").asText());
         if (version.compareTo(new Version("4.15-beta-8")) < 0) {
            try {
               JsonNode printQrCodePositionNode = rootNode.get("printQrCodePosition");
               if (printQrCodePositionNode != null) {
                  if ("BOTTOM_RIGHT".equals(printQrCodePositionNode.asText())) {
                     updates.add(() -> rootNode.put("printQrCodePosition", "BOTTOM"));
                  } else if ("TOP_LEFT".equals(printQrCodePositionNode.asText())) {
                     updates.add(() -> rootNode.put("printQrCodePosition", "TOP"));
                  }
               }
            } catch (Exception var7) {
               this.logger.error("Error updating preferences", var7);
            }
         }

         if (!updates.isEmpty()) {
            this.logger.info("Applying {} updates to preferences.json", updates.size());

            for (Runnable runnable : updates) {
               runnable.run();
            }

            rootNode.put("version", this._version.toString());
            this._preferencesMapper.writeValue(new FileWriter(preferencesFile, Defaults.CHARSET), rootNode);
            File outboxDirectory = new File(this._dataDirectory, "outbox");
            File preferencesOutboxFile = new File(outboxDirectory, "preferences.json");
            Files.copy(preferencesFile.toPath(), preferencesOutboxFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
         }
      }
   }

   private void removeLegacyPasswordPreferences() {
      try {
         if (this._preferences.get("password", null) == null) {
            return;
         }

         this._preferences.remove("password");
         this._preferences.flush();
         this._preferences.sync();
         this.logger.info("Legacy remembered-password preference has been removed.");
      } catch (BackingStoreException var2) {
         this.logger.error("Error removing legacy password preferences", var2);
      }
   }
}
