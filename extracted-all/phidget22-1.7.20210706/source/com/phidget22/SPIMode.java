package com.phidget22;

public enum SPIMode {
   MODE_0(1) {
      @Override
      public String getMessage() {
         return "CPOL = 0 CPHA = 0";
      }
   },
   MODE_1(2) {
      @Override
      public String getMessage() {
         return "CPOL = 0 CPHA = 1";
      }
   },
   MODE_2(3) {
      @Override
      public String getMessage() {
         return "CPOL = 1 CPHA = 0";
      }
   },
   MODE_3(4) {
      @Override
      public String getMessage() {
         return "CPOL = 1 CPHA = 1";
      }
   };

   private final int code;

   public static SPIMode getEnum(int var0) {
      switch (var0) {
         case 1:
            return MODE_0;
         case 2:
            return MODE_1;
         case 3:
            return MODE_2;
         case 4:
            return MODE_3;
         default:
            return null;
      }
   }

   private SPIMode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
