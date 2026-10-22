package com.phidget22;

public enum StopBits {
   ONE(1) {
      @Override
      public String getMessage() {
         return "One Stop Bit";
      }
   },
   TWO(2) {
      @Override
      public String getMessage() {
         return "Two Stop Bits";
      }
   };

   private final int code;

   public static StopBits getEnum(int var0) {
      switch (var0) {
         case 1:
            return ONE;
         case 2:
            return TWO;
         default:
            return null;
      }
   }

   private StopBits(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
