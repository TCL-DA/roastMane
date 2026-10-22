package com.phidget22;

public enum BridgeGain {
   GAIN_1X(1) {
      @Override
      public String getMessage() {
         return "1x";
      }
   },
   GAIN_2X(2) {
      @Override
      public String getMessage() {
         return "2x";
      }
   },
   GAIN_4X(3) {
      @Override
      public String getMessage() {
         return "4x";
      }
   },
   GAIN_8X(4) {
      @Override
      public String getMessage() {
         return "8x";
      }
   },
   GAIN_16X(5) {
      @Override
      public String getMessage() {
         return "16x";
      }
   },
   GAIN_32X(6) {
      @Override
      public String getMessage() {
         return "32x";
      }
   },
   GAIN_64X(7) {
      @Override
      public String getMessage() {
         return "64x";
      }
   },
   GAIN_128X(8) {
      @Override
      public String getMessage() {
         return "128x";
      }
   };

   private final int code;

   public static BridgeGain getEnum(int var0) {
      switch (var0) {
         case 1:
            return GAIN_1X;
         case 2:
            return GAIN_2X;
         case 3:
            return GAIN_4X;
         case 4:
            return GAIN_8X;
         case 5:
            return GAIN_16X;
         case 6:
            return GAIN_32X;
         case 7:
            return GAIN_64X;
         case 8:
            return GAIN_128X;
         default:
            return null;
      }
   }

   private BridgeGain(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
