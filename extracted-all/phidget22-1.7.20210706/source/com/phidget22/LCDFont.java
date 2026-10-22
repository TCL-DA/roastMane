package com.phidget22;

public enum LCDFont {
   USER1(1) {
      @Override
      public String getMessage() {
         return "User 1";
      }
   },
   USER2(2) {
      @Override
      public String getMessage() {
         return "User 2";
      }
   },
   DIMENSIONS_6X10(3) {
      @Override
      public String getMessage() {
         return "6x10";
      }
   },
   DIMENSIONS_5X8(4) {
      @Override
      public String getMessage() {
         return "5x8";
      }
   },
   DIMENSIONS_6X12(5) {
      @Override
      public String getMessage() {
         return "6x12";
      }
   };

   private final int code;

   public static LCDFont getEnum(int var0) {
      switch (var0) {
         case 1:
            return USER1;
         case 2:
            return USER2;
         case 3:
            return DIMENSIONS_6X10;
         case 4:
            return DIMENSIONS_5X8;
         case 5:
            return DIMENSIONS_6X12;
         default:
            return null;
      }
   }

   private LCDFont(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
