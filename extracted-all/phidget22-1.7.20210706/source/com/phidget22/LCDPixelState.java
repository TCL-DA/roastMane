package com.phidget22;

public enum LCDPixelState {
   OFF(0) {
      @Override
      public String getMessage() {
         return "Off";
      }
   },
   ON(1) {
      @Override
      public String getMessage() {
         return "On";
      }
   },
   INVERT(2) {
      @Override
      public String getMessage() {
         return "Invert";
      }
   };

   private final int code;

   public static LCDPixelState getEnum(int var0) {
      switch (var0) {
         case 0:
            return OFF;
         case 1:
            return ON;
         case 2:
            return INVERT;
         default:
            return null;
      }
   }

   private LCDPixelState(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
