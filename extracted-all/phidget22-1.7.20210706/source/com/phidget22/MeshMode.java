package com.phidget22;

public enum MeshMode {
   ROUTER(1) {
      @Override
      public String getMessage() {
         return "Router mode";
      }
   },
   SLEEPY_END_DEVICE(2) {
      @Override
      public String getMessage() {
         return "Sleepy end device mode";
      }
   };

   private final int code;

   public static MeshMode getEnum(int var0) {
      switch (var0) {
         case 1:
            return ROUTER;
         case 2:
            return SLEEPY_END_DEVICE;
         default:
            return null;
      }
   }

   private MeshMode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
