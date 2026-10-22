package com.phidget22;

public enum LEDForwardVoltage {
   VOLTS_1_7(1) {
      @Override
      public String getMessage() {
         return "1.7 V";
      }
   },
   VOLTS_2_75(2) {
      @Override
      public String getMessage() {
         return "2.75 V";
      }
   },
   VOLTS_3_2(3) {
      @Override
      public String getMessage() {
         return "3.2 V";
      }
   },
   VOLTS_3_9(4) {
      @Override
      public String getMessage() {
         return "3.9 V";
      }
   },
   VOLTS_4_0(5) {
      @Override
      public String getMessage() {
         return "4.0 V";
      }
   },
   VOLTS_4_8(6) {
      @Override
      public String getMessage() {
         return "4.8 V";
      }
   },
   VOLTS_5_0(7) {
      @Override
      public String getMessage() {
         return "5.0 V";
      }
   },
   VOLTS_5_6(8) {
      @Override
      public String getMessage() {
         return "5.6 V";
      }
   };

   private final int code;

   public static LEDForwardVoltage getEnum(int var0) {
      switch (var0) {
         case 1:
            return VOLTS_1_7;
         case 2:
            return VOLTS_2_75;
         case 3:
            return VOLTS_3_2;
         case 4:
            return VOLTS_3_9;
         case 5:
            return VOLTS_4_0;
         case 6:
            return VOLTS_4_8;
         case 7:
            return VOLTS_5_0;
         case 8:
            return VOLTS_5_6;
         default:
            return null;
      }
   }

   private LEDForwardVoltage(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
