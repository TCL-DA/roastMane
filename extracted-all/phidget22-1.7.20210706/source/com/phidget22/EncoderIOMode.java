package com.phidget22;

public enum EncoderIOMode {
   PUSH_PULL(1) {
      @Override
      public String getMessage() {
         return "Push-Pull";
      }
   },
   LINE_DRIVER_2K2(2) {
      @Override
      public String getMessage() {
         return "Line Driver 2.2K";
      }
   },
   LINE_DRIVER_10K(3) {
      @Override
      public String getMessage() {
         return "Line Driver 10K";
      }
   },
   OPEN_COLLECTOR_2K2(4) {
      @Override
      public String getMessage() {
         return "Open Collector 2.2K";
      }
   },
   OPEN_COLLECTOR_10K(5) {
      @Override
      public String getMessage() {
         return "Open Collector 10K";
      }
   };

   private final int code;

   public static EncoderIOMode getEnum(int var0) {
      switch (var0) {
         case 1:
            return PUSH_PULL;
         case 2:
            return LINE_DRIVER_2K2;
         case 3:
            return LINE_DRIVER_10K;
         case 4:
            return OPEN_COLLECTOR_2K2;
         case 5:
            return OPEN_COLLECTOR_10K;
         default:
            return null;
      }
   }

   private EncoderIOMode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
