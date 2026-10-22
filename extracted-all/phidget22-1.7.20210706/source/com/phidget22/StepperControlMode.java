package com.phidget22;

public enum StepperControlMode {
   STEP(0) {
      @Override
      public String getMessage() {
         return "Step";
      }
   },
   RUN(1) {
      @Override
      public String getMessage() {
         return "Run";
      }
   };

   private final int code;

   public static StepperControlMode getEnum(int var0) {
      switch (var0) {
         case 0:
            return STEP;
         case 1:
            return RUN;
         default:
            return null;
      }
   }

   private StepperControlMode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
