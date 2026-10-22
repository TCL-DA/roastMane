package com.phidget22;

public enum Parity {
   NONE(1) {
      @Override
      public String getMessage() {
         return "No Parity Bit";
      }
   },
   EVEN(2) {
      @Override
      public String getMessage() {
         return "Even Parity";
      }
   },
   ODD(3) {
      @Override
      public String getMessage() {
         return "Odd Parity";
      }
   };

   private final int code;

   public static Parity getEnum(int var0) {
      switch (var0) {
         case 1:
            return NONE;
         case 2:
            return EVEN;
         case 3:
            return ODD;
         default:
            return null;
      }
   }

   private Parity(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
