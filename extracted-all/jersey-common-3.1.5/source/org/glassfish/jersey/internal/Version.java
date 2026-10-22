package org.glassfish.jersey.internal;

import java.io.InputStream;
import java.util.Properties;

public final class Version {
   private static String buildId;
   private static String version = null;

   private Version() {
      throw new AssertionError("Instantiation not allowed.");
   }

   private static void _initiateProperties() {
      InputStream in = getIntputStream();
      if (in != null) {
         try {
            Properties p = new Properties();
            p.load(in);
            String timestamp = p.getProperty("Build-Timestamp");
            version = p.getProperty("Build-Version");
            buildId = String.format("Jersey: %s %s", version, timestamp);
         } catch (Exception var6) {
            buildId = "Jersey";
         } finally {
            close(in);
         }
      }
   }

   private static void close(InputStream in) {
      try {
         in.close();
      } catch (Exception var2) {
      }
   }

   private static InputStream getIntputStream() {
      try {
         return Version.class.getResourceAsStream("build.properties");
      } catch (Exception var1) {
         return null;
      }
   }

   public static String getBuildId() {
      return buildId;
   }

   public static String getVersion() {
      return version;
   }

   static {
      _initiateProperties();
   }
}
