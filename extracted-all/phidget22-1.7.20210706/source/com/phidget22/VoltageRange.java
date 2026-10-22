package com.phidget22;

public enum VoltageRange {
   MILLIVOLTS_10(1) {
      @Override
      public String getMessage() {
         return "10 mV";
      }
   },
   MILLIVOLTS_40(2) {
      @Override
      public String getMessage() {
         return "40 mV";
      }
   },
   MILLIVOLTS_200(3) {
      @Override
      public String getMessage() {
         return "200 mV";
      }
   },
   MILLIVOLTS_312_5(4) {
      @Override
      public String getMessage() {
         return "312.5 mV";
      }
   },
   MILLIVOLTS_400(5) {
      @Override
      public String getMessage() {
         return "400 mV";
      }
   },
   MILLIVOLTS_1000(6) {
      @Override
      public String getMessage() {
         return "1000 mV";
      }
   },
   VOLTS_2(7) {
      @Override
      public String getMessage() {
         return "2 V";
      }
   },
   VOLTS_5(8) {
      @Override
      public String getMessage() {
         return "5 V";
      }
   },
   VOLTS_15(9) {
      @Override
      public String getMessage() {
         return "15 V";
      }
   },
   VOLTS_40(10) {
      @Override
      public String getMessage() {
         return "40 V";
      }
   },
   AUTO(11) {
      @Override
      public String getMessage() {
         return "Auto";
      }
   };

   private final int code;

   public static VoltageRange getEnum(int var0) {
      switch (var0) {
         case 1:
            return MILLIVOLTS_10;
         case 2:
            return MILLIVOLTS_40;
         case 3:
            return MILLIVOLTS_200;
         case 4:
            return MILLIVOLTS_312_5;
         case 5:
            return MILLIVOLTS_400;
         case 6:
            return MILLIVOLTS_1000;
         case 7:
            return VOLTS_2;
         case 8:
            return VOLTS_5;
         case 9:
            return VOLTS_15;
         case 10:
            return VOLTS_40;
         case 11:
            return AUTO;
         default:
            return null;
      }
   }

   private VoltageRange(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
