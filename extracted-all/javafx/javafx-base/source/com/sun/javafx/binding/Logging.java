package com.sun.javafx.binding;

import com.sun.javafx.logging.PlatformLogger;

public class Logging {
   public static Logging.ErrorLogger getLogger() {
      return Logging.ErrorLogger.INSTANCE;
   }

   public static class ErrorLogger extends PlatformLogger {
      private static final Logging.ErrorLogger INSTANCE = new Logging.ErrorLogger();
      private Logging.ErrorLogger.ErrorLogRecord errorLogRecord;

      ErrorLogger() {
         super(System.getLogger("javafx.beans"));
      }

      public Logging.ErrorLogger.ErrorLogRecord getErrorLogRecord() {
         return this.errorLogRecord;
      }

      public void setErrorLogRecord(Logging.ErrorLogger.ErrorLogRecord var1) {
         this.errorLogRecord = var1;
      }

      @Override
      public void warning(String var1, Throwable var2) {
         super.warning(var1, var2);
         this.errorLogRecord = new Logging.ErrorLogger.ErrorLogRecord(PlatformLogger.Level.WARNING, var2);
      }

      @Override
      public void fine(String var1, Throwable var2) {
         super.fine(var1, var2);
         this.errorLogRecord = new Logging.ErrorLogger.ErrorLogRecord(PlatformLogger.Level.FINE, var2);
      }

      public static class ErrorLogRecord {
         private final PlatformLogger.Level level;
         private final Throwable thrown;

         public ErrorLogRecord(PlatformLogger.Level var1, Throwable var2) {
            this.level = var1;
            this.thrown = var2;
         }

         public Throwable getThrown() {
            return this.thrown;
         }

         public PlatformLogger.Level getLevel() {
            return this.level;
         }
      }
   }
}
