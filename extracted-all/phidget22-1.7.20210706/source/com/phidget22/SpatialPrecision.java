package com.phidget22;

public enum SpatialPrecision {
   HYBRID(0) {
      @Override
      public String getMessage() {
         return "Hybrid";
      }
   },
   HIGH(1) {
      @Override
      public String getMessage() {
         return "High";
      }
   },
   LOW(2) {
      @Override
      public String getMessage() {
         return "Low";
      }
   };

   private final int code;

   public static SpatialPrecision getEnum(int var0) {
      switch (var0) {
         case 0:
            return HYBRID;
         case 1:
            return HIGH;
         case 2:
            return LOW;
         default:
            return null;
      }
   }

   private SpatialPrecision(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
