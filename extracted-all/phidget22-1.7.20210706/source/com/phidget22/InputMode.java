package com.phidget22;

public enum InputMode {
   NPN(1) {
      @Override
      public String getMessage() {
         return "NPN";
      }
   },
   PNP(2) {
      @Override
      public String getMessage() {
         return "PNP";
      }
   };

   private final int code;

   public static InputMode getEnum(int var0) {
      switch (var0) {
         case 1:
            return NPN;
         case 2:
            return PNP;
         default:
            return null;
      }
   }

   private InputMode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
