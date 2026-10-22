package com.phidget22;

public enum LCDScreenSize {
   NO_SCREEN(1) {
      @Override
      public String getMessage() {
         return "No Screen";
      }
   },
   DIMENSIONS_1X8(2) {
      @Override
      public String getMessage() {
         return "1x8";
      }
   },
   DIMENSIONS_2X8(3) {
      @Override
      public String getMessage() {
         return "2x8";
      }
   },
   DIMENSIONS_1X16(4) {
      @Override
      public String getMessage() {
         return "1x16";
      }
   },
   DIMENSIONS_2X16(5) {
      @Override
      public String getMessage() {
         return "2x16";
      }
   },
   DIMENSIONS_4X16(6) {
      @Override
      public String getMessage() {
         return "4x16";
      }
   },
   DIMENSIONS_2X20(7) {
      @Override
      public String getMessage() {
         return "2x20";
      }
   },
   DIMENSIONS_4X20(8) {
      @Override
      public String getMessage() {
         return "4x20";
      }
   },
   DIMENSIONS_2X24(9) {
      @Override
      public String getMessage() {
         return "2x24";
      }
   },
   DIMENSIONS_1X40(10) {
      @Override
      public String getMessage() {
         return "1x40";
      }
   },
   DIMENSIONS_2X40(11) {
      @Override
      public String getMessage() {
         return "2x40";
      }
   },
   DIMENSIONS_4X40(12) {
      @Override
      public String getMessage() {
         return "4x40";
      }
   },
   DIMENSIONS_64X128(13) {
      @Override
      public String getMessage() {
         return "64x128";
      }
   };

   private final int code;

   public static LCDScreenSize getEnum(int var0) {
      switch (var0) {
         case 1:
            return NO_SCREEN;
         case 2:
            return DIMENSIONS_1X8;
         case 3:
            return DIMENSIONS_2X8;
         case 4:
            return DIMENSIONS_1X16;
         case 5:
            return DIMENSIONS_2X16;
         case 6:
            return DIMENSIONS_4X16;
         case 7:
            return DIMENSIONS_2X20;
         case 8:
            return DIMENSIONS_4X20;
         case 9:
            return DIMENSIONS_2X24;
         case 10:
            return DIMENSIONS_1X40;
         case 11:
            return DIMENSIONS_2X40;
         case 12:
            return DIMENSIONS_4X40;
         case 13:
            return DIMENSIONS_64X128;
         default:
            return null;
      }
   }

   private LCDScreenSize(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
