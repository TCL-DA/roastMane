package com.phidget22;

public enum RTDWireSetup {
   WIRES_2(1) {
      @Override
      public String getMessage() {
         return "2 Wire";
      }
   },
   WIRES_3(2) {
      @Override
      public String getMessage() {
         return "3 Wire";
      }
   },
   WIRES_4(3) {
      @Override
      public String getMessage() {
         return "4 Wire";
      }
   };

   private final int code;

   public static RTDWireSetup getEnum(int var0) {
      switch (var0) {
         case 1:
            return WIRES_2;
         case 2:
            return WIRES_3;
         case 3:
            return WIRES_4;
         default:
            return null;
      }
   }

   private RTDWireSetup(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
