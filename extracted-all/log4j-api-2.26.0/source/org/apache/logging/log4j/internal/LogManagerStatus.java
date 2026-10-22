package org.apache.logging.log4j.internal;

public class LogManagerStatus {
   private static boolean initialized = false;

   public static void setInitialized(final boolean managerStatus) {
      initialized = managerStatus;
   }

   public static boolean isInitialized() {
      return initialized;
   }
}
