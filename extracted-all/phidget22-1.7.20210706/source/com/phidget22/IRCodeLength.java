package com.phidget22;

public enum IRCodeLength {
   UNKNOWN(1) {
      @Override
      public String getMessage() {
         return "Unknown";
      }
   },
   CONSTANT(2) {
      @Override
      public String getMessage() {
         return "Constant";
      }
   },
   VARIABLE(3) {
      @Override
      public String getMessage() {
         return "Variable";
      }
   };

   private final int code;

   public static IRCodeLength getEnum(int var0) {
      switch (var0) {
         case 1:
            return UNKNOWN;
         case 2:
            return CONSTANT;
         case 3:
            return VARIABLE;
         default:
            return null;
      }
   }

   private IRCodeLength(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
