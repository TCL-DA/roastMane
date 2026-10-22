package com.phidget22;

public enum ChannelSubclass {
   NONE(1) {
      @Override
      public String getMessage() {
         return "No subclass";
      }
   },
   DIGITAL_OUTPUT_DUTY_CYCLE(16) {
      @Override
      public String getMessage() {
         return "Digital output duty cycle";
      }
   },
   DIGITAL_OUTPUT_LEDDRIVER(17) {
      @Override
      public String getMessage() {
         return "Digital output LED driver";
      }
   },
   DIGITAL_OUTPUT_FREQUENCY(18) {
      @Override
      public String getMessage() {
         return "Digital output frequency";
      }
   },
   TEMPERATURE_SENSOR_RTD(32) {
      @Override
      public String getMessage() {
         return "Temperature sensor RTD";
      }
   },
   TEMPERATURE_SENSOR_THERMOCOUPLE(33) {
      @Override
      public String getMessage() {
         return "Temperature sensor thermocouple";
      }
   },
   VOLTAGE_INPUT_SENSOR_PORT(48) {
      @Override
      public String getMessage() {
         return "Voltage sensor port";
      }
   },
   VOLTAGE_RATIO_INPUT_SENSOR_PORT(64) {
      @Override
      public String getMessage() {
         return "Voltage ratio sensor port";
      }
   },
   VOLTAGE_RATIO_INPUT_BRIDGE(65) {
      @Override
      public String getMessage() {
         return "Voltage ratio bridge input";
      }
   },
   LCD_GRAPHIC(80) {
      @Override
      public String getMessage() {
         return "Graphic LCD";
      }
   },
   LCD_TEXT(81) {
      @Override
      public String getMessage() {
         return "Text LCD";
      }
   },
   ENCODER_MODE_SETTABLE(96) {
      @Override
      public String getMessage() {
         return "Encoder IO mode settable";
      }
   },
   SPATIAL_AHRS(112) {
      @Override
      public String getMessage() {
         return "Spatial AHRS/IMU";
      }
   };

   private final int code;

   public static ChannelSubclass getEnum(int var0) {
      switch (var0) {
         case 1:
            return NONE;
         case 16:
            return DIGITAL_OUTPUT_DUTY_CYCLE;
         case 17:
            return DIGITAL_OUTPUT_LEDDRIVER;
         case 18:
            return DIGITAL_OUTPUT_FREQUENCY;
         case 32:
            return TEMPERATURE_SENSOR_RTD;
         case 33:
            return TEMPERATURE_SENSOR_THERMOCOUPLE;
         case 48:
            return VOLTAGE_INPUT_SENSOR_PORT;
         case 64:
            return VOLTAGE_RATIO_INPUT_SENSOR_PORT;
         case 65:
            return VOLTAGE_RATIO_INPUT_BRIDGE;
         case 80:
            return LCD_GRAPHIC;
         case 81:
            return LCD_TEXT;
         case 96:
            return ENCODER_MODE_SETTABLE;
         case 112:
            return SPATIAL_AHRS;
         default:
            return null;
      }
   }

   private ChannelSubclass(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
