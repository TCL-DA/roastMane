package com.phidget22;

public enum FanMode {
   OFF(1) {
      @Override
      public String getMessage() {
         return "Off";
      }
   },
   ON(2) {
      @Override
      public String getMessage() {
         return "On";
      }
   },
   AUTO(3) {
      @Override
      public String getMessage() {
         return "Automatic";
      }
   };

   private final int code;

   public static FanMode getEnum(int var0) {
      switch (var0) {
         case 1:
            return OFF;
         case 2:
            return ON;
         case 3:
            return AUTO;
         default:
            return null;
      }
   }

   private FanMode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
