package com.phidget22;

public enum RCServoVoltage {
   VOLTS_5_0(1) {
      @Override
      public String getMessage() {
         return "5.0 V";
      }
   },
   VOLTS_6_0(2) {
      @Override
      public String getMessage() {
         return "6.0 V";
      }
   },
   VOLTS_7_4(3) {
      @Override
      public String getMessage() {
         return "7.4 V";
      }
   };

   private final int code;

   public static RCServoVoltage getEnum(int var0) {
      switch (var0) {
         case 1:
            return VOLTS_5_0;
         case 2:
            return VOLTS_6_0;
         case 3:
            return VOLTS_7_4;
         default:
            return null;
      }
   }

   private RCServoVoltage(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
