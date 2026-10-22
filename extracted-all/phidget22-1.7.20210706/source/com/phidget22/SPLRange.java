package com.phidget22;

public enum SPLRange {
   DB_102(1) {
      @Override
      public String getMessage() {
         return "102 dB";
      }
   },
   DB_82(2) {
      @Override
      public String getMessage() {
         return "82 dB";
      }
   };

   private final int code;

   public static SPLRange getEnum(int var0) {
      switch (var0) {
         case 1:
            return DB_102;
         case 2:
            return DB_82;
         default:
            return null;
      }
   }

   private SPLRange(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
