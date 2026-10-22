package com.phidget22;

public enum DeviceClass {
   ACCELEROMETER(1) {
      @Override
      public String getMessage() {
         return "PhidgetAccelerometer";
      }
   },
   ADVANCED_SERVO(2) {
      @Override
      public String getMessage() {
         return "PhidgetAdvancedServo";
      }
   },
   ANALOG(3) {
      @Override
      public String getMessage() {
         return "PhidgetAnalog";
      }
   },
   BRIDGE(4) {
      @Override
      public String getMessage() {
         return "PhidgetBridge";
      }
   },
   ENCODER(5) {
      @Override
      public String getMessage() {
         return "PhidgetEncoder";
      }
   },
   FREQUENCY_COUNTER(6) {
      @Override
      public String getMessage() {
         return "PhidgetFrequencyCounter";
      }
   },
   GPS(7) {
      @Override
      public String getMessage() {
         return "PhidgetGPS";
      }
   },
   HUB(8) {
      @Override
      public String getMessage() {
         return "PhidgetHub";
      }
   },
   INTERFACE_KIT(9) {
      @Override
      public String getMessage() {
         return "PhidgetInterfaceKit";
      }
   },
   IR(10) {
      @Override
      public String getMessage() {
         return "PhidgetIR";
      }
   },
   LED(11) {
      @Override
      public String getMessage() {
         return "PhidgetLED";
      }
   },
   MOTOR_CONTROL(13) {
      @Override
      public String getMessage() {
         return "PhidgetMotorControl";
      }
   },
   PH_SENSOR(14) {
      @Override
      public String getMessage() {
         return "PhidgetPHSensor";
      }
   },
   RFID(15) {
      @Override
      public String getMessage() {
         return "PhidgetRFID";
      }
   },
   SERVO(16) {
      @Override
      public String getMessage() {
         return "PhidgetServo";
      }
   },
   SPATIAL(17) {
      @Override
      public String getMessage() {
         return "PhidgetSpatial";
      }
   },
   STEPPER(18) {
      @Override
      public String getMessage() {
         return "PhidgetStepper";
      }
   },
   TEMPERATURE_SENSOR(19) {
      @Override
      public String getMessage() {
         return "PhidgetTemperatureSensor";
      }
   },
   TEXT_LCD(20) {
      @Override
      public String getMessage() {
         return "PhidgetTextLCD";
      }
   },
   VINT(21) {
      @Override
      public String getMessage() {
         return "PhidgetVINT";
      }
   },
   DICTIONARY(24) {
      @Override
      public String getMessage() {
         return "PhidgetDictionary";
      }
   },
   DATA_ADAPTER(25) {
      @Override
      public String getMessage() {
         return "PhidgetDataAdapter";
      }
   };

   private final int code;

   public static DeviceClass getEnum(int var0) {
      switch (var0) {
         case 1:
            return ACCELEROMETER;
         case 2:
            return ADVANCED_SERVO;
         case 3:
            return ANALOG;
         case 4:
            return BRIDGE;
         case 5:
            return ENCODER;
         case 6:
            return FREQUENCY_COUNTER;
         case 7:
            return GPS;
         case 8:
            return HUB;
         case 9:
            return INTERFACE_KIT;
         case 10:
            return IR;
         case 11:
            return LED;
         case 12:
         case 22:
         case 23:
         default:
            return null;
         case 13:
            return MOTOR_CONTROL;
         case 14:
            return PH_SENSOR;
         case 15:
            return RFID;
         case 16:
            return SERVO;
         case 17:
            return SPATIAL;
         case 18:
            return STEPPER;
         case 19:
            return TEMPERATURE_SENSOR;
         case 20:
            return TEXT_LCD;
         case 21:
            return VINT;
         case 24:
            return DICTIONARY;
         case 25:
            return DATA_ADAPTER;
      }
   }

   private DeviceClass(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
