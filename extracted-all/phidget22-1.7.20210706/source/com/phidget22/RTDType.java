package com.phidget22;

public enum RTDType {
   PT100_3850(1) {
      @Override
      public String getMessage() {
         return "PT100 3850";
      }
   },
   PT1000_3850(2) {
      @Override
      public String getMessage() {
         return "PT1000 3850";
      }
   },
   PT100_3920(3) {
      @Override
      public String getMessage() {
         return "PT100 3920";
      }
   },
   PT1000_3920(4) {
      @Override
      public String getMessage() {
         return "PT1000 3920";
      }
   };

   private final int code;

   public static RTDType getEnum(int var0) {
      switch (var0) {
         case 1:
            return PT100_3850;
         case 2:
            return PT1000_3850;
         case 3:
            return PT100_3920;
         case 4:
            return PT1000_3920;
         default:
            return null;
      }
   }

   private RTDType(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
