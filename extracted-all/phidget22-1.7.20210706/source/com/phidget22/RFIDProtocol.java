package com.phidget22;

public enum RFIDProtocol {
   EM4100(1) {
      @Override
      public String getMessage() {
         return "EM4100";
      }
   },
   ISO11785_FDX_B(2) {
      @Override
      public String getMessage() {
         return "ISO11785 FDX B";
      }
   },
   PHIDGET_TAG(3) {
      @Override
      public String getMessage() {
         return "PhidgetTAG";
      }
   };

   private final int code;

   public static RFIDProtocol getEnum(int var0) {
      switch (var0) {
         case 1:
            return EM4100;
         case 2:
            return ISO11785_FDX_B;
         case 3:
            return PHIDGET_TAG;
         default:
            return null;
      }
   }

   private RFIDProtocol(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
