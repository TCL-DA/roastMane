package com.phidget22;

public enum IRCodeEncoding {
   UNKNOWN(1) {
      @Override
      public String getMessage() {
         return "Unknown";
      }
   },
   SPACE(2) {
      @Override
      public String getMessage() {
         return "Space";
      }
   },
   PULSE(3) {
      @Override
      public String getMessage() {
         return "Pulse";
      }
   },
   BI_PHASE(4) {
      @Override
      public String getMessage() {
         return "BiPhase";
      }
   },
   RC5(5) {
      @Override
      public String getMessage() {
         return "RC5";
      }
   },
   RC6(6) {
      @Override
      public String getMessage() {
         return "RC6";
      }
   };

   private final int code;

   public static IRCodeEncoding getEnum(int var0) {
      switch (var0) {
         case 1:
            return UNKNOWN;
         case 2:
            return SPACE;
         case 3:
            return PULSE;
         case 4:
            return BI_PHASE;
         case 5:
            return RC5;
         case 6:
            return RC6;
         default:
            return null;
      }
   }

   private IRCodeEncoding(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
