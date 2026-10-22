package com.phidget22;

public enum Endianness {
   MSB_FIRST(1) {
      @Override
      public String getMessage() {
         return "MSB First";
      }
   },
   LSB_FIRST(2) {
      @Override
      public String getMessage() {
         return "LSB First";
      }
   };

   private final int code;

   public static Endianness getEnum(int var0) {
      switch (var0) {
         case 1:
            return MSB_FIRST;
         case 2:
            return LSB_FIRST;
         default:
            return null;
      }
   }

   private Endianness(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
