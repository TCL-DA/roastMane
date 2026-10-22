package com.phidget22;

public enum PowerSupply {
   OFF(1) {
      @Override
      public String getMessage() {
         return "Off";
      }
   },
   VOLTS_12(2) {
      @Override
      public String getMessage() {
         return "12 V";
      }
   },
   VOLTS_24(3) {
      @Override
      public String getMessage() {
         return "24 V";
      }
   };

   private final int code;

   public static PowerSupply getEnum(int var0) {
      switch (var0) {
         case 1:
            return OFF;
         case 2:
            return VOLTS_12;
         case 3:
            return VOLTS_24;
         default:
            return null;
      }
   }

   private PowerSupply(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
