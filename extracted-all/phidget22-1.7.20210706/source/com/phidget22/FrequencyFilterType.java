package com.phidget22;

public enum FrequencyFilterType {
   ZERO_CROSSING(1) {
      @Override
      public String getMessage() {
         return "Zero Crossing";
      }
   },
   LOGIC_LEVEL(2) {
      @Override
      public String getMessage() {
         return "Logic Level";
      }
   };

   private final int code;

   public static FrequencyFilterType getEnum(int var0) {
      switch (var0) {
         case 1:
            return ZERO_CROSSING;
         case 2:
            return LOGIC_LEVEL;
         default:
            return null;
      }
   }

   private FrequencyFilterType(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
