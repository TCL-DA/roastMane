package com.phidget22;

public enum IOVoltage {
   EXTERN(1) {
      @Override
      public String getMessage() {
         return "Voltage supplied by external device";
      }
   },
   VOLTS_1_8(2) {
      @Override
      public String getMessage() {
         return "1.8V";
      }
   },
   VOLTS_2_5(3) {
      @Override
      public String getMessage() {
         return "2.5V";
      }
   },
   VOLTS_3_3(4) {
      @Override
      public String getMessage() {
         return "3.3V";
      }
   },
   VOLTS_5(5) {
      @Override
      public String getMessage() {
         return "5.0V";
      }
   };

   private final int code;

   public static IOVoltage getEnum(int var0) {
      switch (var0) {
         case 1:
            return EXTERN;
         case 2:
            return VOLTS_1_8;
         case 3:
            return VOLTS_2_5;
         case 4:
            return VOLTS_3_3;
         case 5:
            return VOLTS_5;
         default:
            return null;
      }
   }

   private IOVoltage(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
