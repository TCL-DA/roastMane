package com.phidget22;

public enum HubPortMode {
   VINT(0) {
      @Override
      public String getMessage() {
         return "VINT";
      }
   },
   DIGITAL_INPUT(1) {
      @Override
      public String getMessage() {
         return "Digital Input";
      }
   },
   DIGITAL_OUTPUT(2) {
      @Override
      public String getMessage() {
         return "Digital Output";
      }
   },
   VOLTAGE_INPUT(3) {
      @Override
      public String getMessage() {
         return "Voltage Input";
      }
   },
   VOLTAGE_RATIO_INPUT(4) {
      @Override
      public String getMessage() {
         return "Voltage Ratio Input";
      }
   };

   private final int code;

   public static HubPortMode getEnum(int var0) {
      switch (var0) {
         case 0:
            return VINT;
         case 1:
            return DIGITAL_INPUT;
         case 2:
            return DIGITAL_OUTPUT;
         case 3:
            return VOLTAGE_INPUT;
         case 4:
            return VOLTAGE_RATIO_INPUT;
         default:
            return null;
      }
   }

   private HubPortMode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
