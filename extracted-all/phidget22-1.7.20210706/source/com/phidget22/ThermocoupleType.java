package com.phidget22;

public enum ThermocoupleType {
   J(1) {
      @Override
      public String getMessage() {
         return "J-Type";
      }
   },
   K(2) {
      @Override
      public String getMessage() {
         return "K-Type";
      }
   },
   E(3) {
      @Override
      public String getMessage() {
         return "E-Type";
      }
   },
   T(4) {
      @Override
      public String getMessage() {
         return "T-Type";
      }
   };

   private final int code;

   public static ThermocoupleType getEnum(int var0) {
      switch (var0) {
         case 1:
            return J;
         case 2:
            return K;
         case 3:
            return E;
         case 4:
            return T;
         default:
            return null;
      }
   }

   private ThermocoupleType(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
