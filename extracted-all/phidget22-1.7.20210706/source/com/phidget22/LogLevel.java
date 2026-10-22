package com.phidget22;

public enum LogLevel {
   CRITICAL(1) {
      @Override
      public String getMessage() {
         return "Critical";
      }
   },
   ERROR(2) {
      @Override
      public String getMessage() {
         return "Error";
      }
   },
   WARNING(3) {
      @Override
      public String getMessage() {
         return "Warning";
      }
   },
   INFO(4) {
      @Override
      public String getMessage() {
         return "Info";
      }
   },
   DEBUG(5) {
      @Override
      public String getMessage() {
         return "Debug";
      }
   },
   VERBOSE(6) {
      @Override
      public String getMessage() {
         return "Verbose";
      }
   };

   private final int code;

   public static LogLevel getEnum(int var0) {
      switch (var0) {
         case 1:
            return CRITICAL;
         case 2:
            return ERROR;
         case 3:
            return WARNING;
         case 4:
            return INFO;
         case 5:
            return DEBUG;
         case 6:
            return VERBOSE;
         default:
            return null;
      }
   }

   private LogLevel(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
