package com.phidget22;

public enum VoltageOutputRange {
   VOLTS_10(1) {
      @Override
      public String getMessage() {
         return "±10V DC";
      }
   },
   VOLTS_5(2) {
      @Override
      public String getMessage() {
         return "0-5V DC";
      }
   };

   private final int code;

   public static VoltageOutputRange getEnum(int var0) {
      switch (var0) {
         case 1:
            return VOLTS_10;
         case 2:
            return VOLTS_5;
         default:
            return null;
      }
   }

   private VoltageOutputRange(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
