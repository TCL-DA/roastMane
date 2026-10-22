package com.phidget22;

public enum ChannelClass {
   ACCELEROMETER(1) {
      @Override
      public String getMessage() {
         return "PhidgetAccelerometer";
      }
   },
   CURRENT_INPUT(2) {
      @Override
      public String getMessage() {
         return "PhidgetCurrentInput";
      }
   },
   DC_MOTOR(4) {
      @Override
      public String getMessage() {
         return "PhidgetDCMotor";
      }
   },
   DIGITAL_INPUT(5) {
      @Override
      public String getMessage() {
         return "PhidgetDigitalInput";
      }
   },
   DIGITAL_OUTPUT(6) {
      @Override
      public String getMessage() {
         return "PhidgetDigitalOutput";
      }
   },
   DISTANCE_SENSOR(7) {
      @Override
      public String getMessage() {
         return "PhidgetDistanceSensor";
      }
   },
   ENCODER(8) {
      @Override
      public String getMessage() {
         return "PhidgetEncoder";
      }
   },
   FREQUENCY_COUNTER(9) {
      @Override
      public String getMessage() {
         return "PhidgetFrequencyCounter";
      }
   },
   GPS(10) {
      @Override
      public String getMessage() {
         return "PhidgetGPS";
      }
   },
   LCD(11) {
      @Override
      public String getMessage() {
         return "PhidgetLCD";
      }
   },
   GYROSCOPE(12) {
      @Override
      public String getMessage() {
         return "PhidgetGyroscope";
      }
   },
   HUB(13) {
      @Override
      public String getMessage() {
         return "PhidgetHub";
      }
   },
   CAPACITIVE_TOUCH(14) {
      @Override
      public String getMessage() {
         return "PhidgetCapacitiveTouch";
      }
   },
   HUMIDITY_SENSOR(15) {
      @Override
      public String getMessage() {
         return "PhidgetHumiditySensor";
      }
   },
   IR(16) {
      @Override
      public String getMessage() {
         return "PhidgetIR";
      }
   },
   LIGHT_SENSOR(17) {
      @Override
      public String getMessage() {
         return "PhidgetLightSensor";
      }
   },
   MAGNETOMETER(18) {
      @Override
      public String getMessage() {
         return "PhidgetMagnetometer";
      }
   },
   PHSENSOR(37) {
      @Override
      public String getMessage() {
         return "PhidgetPHSensor";
      }
   },
   POWER_GUARD(20) {
      @Override
      public String getMessage() {
         return "PhidgetPowerGuard";
      }
   },
   PRESSURE_SENSOR(21) {
      @Override
      public String getMessage() {
         return "PhidgetPressureSensor";
      }
   },
   RC_SERVO(22) {
      @Override
      public String getMessage() {
         return "PhidgetRCServo";
      }
   },
   RESISTANCE_INPUT(23) {
      @Override
      public String getMessage() {
         return "PhidgetResistanceInput";
      }
   },
   RFID(24) {
      @Override
      public String getMessage() {
         return "PhidgetRFID";
      }
   },
   SOUND_SENSOR(25) {
      @Override
      public String getMessage() {
         return "PhidgetSoundSensor";
      }
   },
   SPATIAL(26) {
      @Override
      public String getMessage() {
         return "PhidgetSpatial";
      }
   },
   STEPPER(27) {
      @Override
      public String getMessage() {
         return "PhidgetStepper";
      }
   },
   TEMPERATURE_SENSOR(28) {
      @Override
      public String getMessage() {
         return "PhidgetTemperatureSensor";
      }
   },
   VOLTAGE_INPUT(29) {
      @Override
      public String getMessage() {
         return "PhidgetVoltageInput";
      }
   },
   VOLTAGE_OUTPUT(30) {
      @Override
      public String getMessage() {
         return "PhidgetVoltageOutput";
      }
   },
   VOLTAGE_RATIO_INPUT(31) {
      @Override
      public String getMessage() {
         return "PhidgetVoltageRatioInput";
      }
   },
   MOTOR_POSITION_CONTROLLER(34) {
      @Override
      public String getMessage() {
         return "PhidgetMotorPositionController";
      }
   },
   BLDC_MOTOR(35) {
      @Override
      public String getMessage() {
         return "PhidgetBLDCMotor";
      }
   },
   DICTIONARY(36) {
      @Override
      public String getMessage() {
         return "PhidgetDictionary";
      }
   },
   CURRENT_OUTPUT(38) {
      @Override
      public String getMessage() {
         return "PhidgetCurrentOutput";
      }
   };

   private final int code;

   public static ChannelClass getEnum(int var0) {
      switch (var0) {
         case 1:
            return ACCELEROMETER;
         case 2:
            return CURRENT_INPUT;
         case 3:
         case 19:
         case 32:
         case 33:
         default:
            return null;
         case 4:
            return DC_MOTOR;
         case 5:
            return DIGITAL_INPUT;
         case 6:
            return DIGITAL_OUTPUT;
         case 7:
            return DISTANCE_SENSOR;
         case 8:
            return ENCODER;
         case 9:
            return FREQUENCY_COUNTER;
         case 10:
            return GPS;
         case 11:
            return LCD;
         case 12:
            return GYROSCOPE;
         case 13:
            return HUB;
         case 14:
            return CAPACITIVE_TOUCH;
         case 15:
            return HUMIDITY_SENSOR;
         case 16:
            return IR;
         case 17:
            return LIGHT_SENSOR;
         case 18:
            return MAGNETOMETER;
         case 20:
            return POWER_GUARD;
         case 21:
            return PRESSURE_SENSOR;
         case 22:
            return RC_SERVO;
         case 23:
            return RESISTANCE_INPUT;
         case 24:
            return RFID;
         case 25:
            return SOUND_SENSOR;
         case 26:
            return SPATIAL;
         case 27:
            return STEPPER;
         case 28:
            return TEMPERATURE_SENSOR;
         case 29:
            return VOLTAGE_INPUT;
         case 30:
            return VOLTAGE_OUTPUT;
         case 31:
            return VOLTAGE_RATIO_INPUT;
         case 34:
            return MOTOR_POSITION_CONTROLLER;
         case 35:
            return BLDC_MOTOR;
         case 36:
            return DICTIONARY;
         case 37:
            return PHSENSOR;
         case 38:
            return CURRENT_OUTPUT;
      }
   }

   private ChannelClass(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
