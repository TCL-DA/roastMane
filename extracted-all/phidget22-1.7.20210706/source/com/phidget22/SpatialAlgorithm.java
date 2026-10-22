package com.phidget22;

public enum SpatialAlgorithm {
   NONE(0) {
      @Override
      public String getMessage() {
         return "None";
      }
   },
   AHRS(1) {
      @Override
      public String getMessage() {
         return "AHRS";
      }
   },
   IMU(2) {
      @Override
      public String getMessage() {
         return "IMU";
      }
   };

   private final int code;

   public static SpatialAlgorithm getEnum(int var0) {
      switch (var0) {
         case 0:
            return NONE;
         case 1:
            return AHRS;
         case 2:
            return IMU;
         default:
            return null;
      }
   }

   private SpatialAlgorithm(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
