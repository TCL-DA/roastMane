package com.phidget22;

public enum DeviceID {
   NONE(0) {
      @Override
      public String getMessage() {
         return "Unknown device";
      }
   },
   PN_INTERFACE_KIT488(1) {
      @Override
      public String getMessage() {
         return "PhidgetInterfaceKit 4/8/8";
      }
   },
   PN_1000(2) {
      @Override
      public String getMessage() {
         return "PhidgetServo 1-Motor (1000)";
      }
   },
   PN_1001(3) {
      @Override
      public String getMessage() {
         return "PhidgetServo 4-Motor (1001)";
      }
   },
   PN_1002(4) {
      @Override
      public String getMessage() {
         return "PhidgetAnalog 4-Output (1002)";
      }
   },
   PN_1008(5) {
      @Override
      public String getMessage() {
         return "PhidgetAccelerometer 2-Axis (1008)";
      }
   },
   PN_1010_1013_1018_1019(6) {
      @Override
      public String getMessage() {
         return "PhidgetInterfaceKit 8/8/8 (1010, 1013, 1018, 1019)";
      }
   },
   PN_1011(7) {
      @Override
      public String getMessage() {
         return "PhidgetInterfaceKit 2/2/2 (1011)";
      }
   },
   PN_1012(8) {
      @Override
      public String getMessage() {
         return "PhidgetInterfaceKit 0/16/16 (1012)";
      }
   },
   PN_1014(9) {
      @Override
      public String getMessage() {
         return "PhidgetInterfaceKit 0/0/4 (1014)";
      }
   },
   PN_1015(10) {
      @Override
      public String getMessage() {
         return "PhidgetLinearTouch (1015)";
      }
   },
   PN_1016(11) {
      @Override
      public String getMessage() {
         return "PhidgetCircularTouch (1016)";
      }
   },
   PN_1017(12) {
      @Override
      public String getMessage() {
         return "PhidgetInterfaceKit 0/0/8 (1017)";
      }
   },
   PN_1023(13) {
      @Override
      public String getMessage() {
         return "PhidgetRFID (1023)";
      }
   },
   PN_1024(14) {
      @Override
      public String getMessage() {
         return "PhidgetRFID Read-Write (1024)";
      }
   },
   PN_1030(15) {
      @Override
      public String getMessage() {
         return "PhidgetLED-64 (1030)";
      }
   },
   PN_1031(16) {
      @Override
      public String getMessage() {
         return "PhidgetLED-64 Advanced (1031)";
      }
   },
   PN_1032(17) {
      @Override
      public String getMessage() {
         return "PhidgetLED-64 Advanced (1032)";
      }
   },
   PN_1040(18) {
      @Override
      public String getMessage() {
         return "PhidgetGPS (1040)";
      }
   },
   PN_1041(19) {
      @Override
      public String getMessage() {
         return "PhidgetSpatial 0/0/3 Basic (1041)";
      }
   },
   PN_1042(20) {
      @Override
      public String getMessage() {
         return "PhidgetSpatial 3/3/3 Basic (1042)";
      }
   },
   PN_1043(21) {
      @Override
      public String getMessage() {
         return "PhidgetSpatial Precision 0/0/3 High Resolution (1043)";
      }
   },
   PN_1044(22) {
      @Override
      public String getMessage() {
         return "PhidgetSpatial Precision 3/3/3 High Resolution (1044)";
      }
   },
   PN_1045(23) {
      @Override
      public String getMessage() {
         return "PhidgetTemperatureSensor IR (1045)";
      }
   },
   PN_1046(24) {
      @Override
      public String getMessage() {
         return "PhidgetBridge 4-Input (1046)";
      }
   },
   PN_1047(25) {
      @Override
      public String getMessage() {
         return "PhidgetEncoder HighSpeed 4-Input (1047)";
      }
   },
   PN_1048(26) {
      @Override
      public String getMessage() {
         return "PhidgetTemperatureSensor 4-input (1048)";
      }
   },
   PN_1049(27) {
      @Override
      public String getMessage() {
         return "PhidgetSpatial 0/0/3 (1049)";
      }
   },
   PN_1051(28) {
      @Override
      public String getMessage() {
         return "PhidgetTemperatureSensor 1-Input (1051)";
      }
   },
   PN_1052(29) {
      @Override
      public String getMessage() {
         return "PhidgetEncoder Mechanical (1052)";
      }
   },
   PN_1053(30) {
      @Override
      public String getMessage() {
         return "PhidgetAccelerometer 2-Axis (1053)";
      }
   },
   PN_1054(31) {
      @Override
      public String getMessage() {
         return "PhidgetFrequencyCounter (1054)";
      }
   },
   PN_1055(32) {
      @Override
      public String getMessage() {
         return "PhidgetIR (1055)";
      }
   },
   PN_1056(33) {
      @Override
      public String getMessage() {
         return "PhidgetSpatial 3/3/3 (1056)";
      }
   },
   PN_1057(34) {
      @Override
      public String getMessage() {
         return "PhidgetEncoder HighSpeed (1057)";
      }
   },
   PN_1058(35) {
      @Override
      public String getMessage() {
         return "PhidgetPHSensor (1058)";
      }
   },
   PN_1059(36) {
      @Override
      public String getMessage() {
         return "PhidgetAccelerometer 3-Axis (1059)";
      }
   },
   PN_1060(37) {
      @Override
      public String getMessage() {
         return "PhidgetMotorControl LV (1060)";
      }
   },
   PN_1061(38) {
      @Override
      public String getMessage() {
         return "PhidgetAdvancedServo 8-Motor (1061)";
      }
   },
   PN_1062(39) {
      @Override
      public String getMessage() {
         return "PhidgetStepper Unipolar 4-Motor (1062)";
      }
   },
   PN_1063(40) {
      @Override
      public String getMessage() {
         return "PhidgetStepper Bipolar 1-Motor (1063)";
      }
   },
   PN_1064(41) {
      @Override
      public String getMessage() {
         return "PhidgetMotorControl HC (1064)";
      }
   },
   PN_1065(42) {
      @Override
      public String getMessage() {
         return "PhidgetMotorControl 1-Motor (1065)";
      }
   },
   PN_1066(43) {
      @Override
      public String getMessage() {
         return "PhidgetAdvancedServo 1-Motor (1066)";
      }
   },
   PN_1067(44) {
      @Override
      public String getMessage() {
         return "PhidgetStepper Bipolar HC (1067)";
      }
   },
   PN_1202_1203(45) {
      @Override
      public String getMessage() {
         return "PhidgetTextLCD 20x2 with PhidgetInterfaceKit 8/8/8 (1201, 1202, 1203)";
      }
   },
   PN_1204(46) {
      @Override
      public String getMessage() {
         return "PhidgetTextLCD Adapter (1204)";
      }
   },
   PN_1215__1218(47) {
      @Override
      public String getMessage() {
         return "PhidgetTextLCD 20x2 (1215/1216/1217/1218)";
      }
   },
   PN_1219__1222(48) {
      @Override
      public String getMessage() {
         return "PhidgetTextLCD 20x2 with PhidgetInterfaceKit 0/8/8 (1219, 1220, 1221, 1222)";
      }
   },
   PN_ADP1000(49) {
      @Override
      public String getMessage() {
         return "pH Adapter";
      }
   },
   PN_DAQ1000(51) {
      @Override
      public String getMessage() {
         return "Analog Input Module x8";
      }
   },
   PN_DAQ1200(52) {
      @Override
      public String getMessage() {
         return "Digital Input 4";
      }
   },
   PN_DAQ1300(53) {
      @Override
      public String getMessage() {
         return "Digital Input 4 Isolated";
      }
   },
   PN_DAQ1301(54) {
      @Override
      public String getMessage() {
         return "Digital Input 16";
      }
   },
   PN_DAQ1400(55) {
      @Override
      public String getMessage() {
         return "Versatile Input";
      }
   },
   PN_DAQ1500(56) {
      @Override
      public String getMessage() {
         return "Bridge";
      }
   },
   PN_DCC1000(57) {
      @Override
      public String getMessage() {
         return "DC Motor Controller with PID";
      }
   },
   PN_DST1000(58) {
      @Override
      public String getMessage() {
         return "200mm Distance Sensor";
      }
   },
   PN_DST1200(59) {
      @Override
      public String getMessage() {
         return "Sonar Distance Sensor";
      }
   },
   PN_ENC1000(60) {
      @Override
      public String getMessage() {
         return "Encoder";
      }
   },
   PN_HIN1000(61) {
      @Override
      public String getMessage() {
         return "Capacitive Touch Sensor";
      }
   },
   PN_HIN1001(62) {
      @Override
      public String getMessage() {
         return "Capacitive Scroll";
      }
   },
   PN_HIN1100(63) {
      @Override
      public String getMessage() {
         return "Joystick";
      }
   },
   PN_HUB0000(64) {
      @Override
      public String getMessage() {
         return "Phidget USB VINT Hub with 6 ports";
      }
   },
   PN_HUB0004(67) {
      @Override
      public String getMessage() {
         return "Phidget SPI VINT Hub with 6 ports";
      }
   },
   PN_HUM1000(69) {
      @Override
      public String getMessage() {
         return "Humidity Sensor";
      }
   },
   PN_LCD1100(70) {
      @Override
      public String getMessage() {
         return "LCD";
      }
   },
   PN_LED1000(71) {
      @Override
      public String getMessage() {
         return "LED Driver 32";
      }
   },
   PN_LUX1000(72) {
      @Override
      public String getMessage() {
         return "Light Sensor";
      }
   },
   PN_MOT1100(73) {
      @Override
      public String getMessage() {
         return "Accelerometer 0/0/3";
      }
   },
   PN_MOT1101(74) {
      @Override
      public String getMessage() {
         return "Spatial 3/3/3";
      }
   },
   PN_OUT1000(75) {
      @Override
      public String getMessage() {
         return "Analog Output 0-5V";
      }
   },
   PN_OUT1001(76) {
      @Override
      public String getMessage() {
         return "Analog Output (+/-)10V";
      }
   },
   PN_OUT1002(77) {
      @Override
      public String getMessage() {
         return "Analog Output (+/-)10V - 16 bit";
      }
   },
   PN_OUT1100(78) {
      @Override
      public String getMessage() {
         return "Digital Output 4";
      }
   },
   PN_PRE1000(79) {
      @Override
      public String getMessage() {
         return "Barometer";
      }
   },
   PN_RCC1000(80) {
      @Override
      public String getMessage() {
         return "8-Servo Controller";
      }
   },
   PN_REL1000(81) {
      @Override
      public String getMessage() {
         return "Power Relay 4";
      }
   },
   PN_REL1100(82) {
      @Override
      public String getMessage() {
         return "Digital Output 4 Isolated";
      }
   },
   PN_REL1101(83) {
      @Override
      public String getMessage() {
         return "Digital Output 16 Isolated";
      }
   },
   PN_SAF1000(84) {
      @Override
      public String getMessage() {
         return "Power Supply Protector";
      }
   },
   PN_SND1000(85) {
      @Override
      public String getMessage() {
         return "Sound Pressure Level Sensor";
      }
   },
   PN_STC1000(86) {
      @Override
      public String getMessage() {
         return "Bipolar Stepper Motor Controller";
      }
   },
   PN_TMP1000(87) {
      @Override
      public String getMessage() {
         return "Integrated Temperature Sensor";
      }
   },
   PN_TMP1100(88) {
      @Override
      public String getMessage() {
         return "Thermocouple 1";
      }
   },
   PN_TMP1101(89) {
      @Override
      public String getMessage() {
         return "Thermocouple 4";
      }
   },
   PN_TMP1200(90) {
      @Override
      public String getMessage() {
         return "RTD";
      }
   },
   PN_VCP1000(92) {
      @Override
      public String getMessage() {
         return "Voltage Sensor High Precision";
      }
   },
   PN_VCP1001(93) {
      @Override
      public String getMessage() {
         return "Voltage Sensor Large";
      }
   },
   PN_VCP1002(94) {
      @Override
      public String getMessage() {
         return "Voltage Sensor Small";
      }
   },
   DIGITAL_INPUT_PORT(95) {
      @Override
      public String getMessage() {
         return "Hub Port in Digital Input mode";
      }
   },
   DIGITAL_OUTPUT_PORT(96) {
      @Override
      public String getMessage() {
         return "Hub Port in Digital Output mode";
      }
   },
   VOLTAGE_INPUT_PORT(97) {
      @Override
      public String getMessage() {
         return "Hub Port in Voltage Input mode";
      }
   },
   VOLTAGE_RATIO_INPUT_PORT(98) {
      @Override
      public String getMessage() {
         return "Hub Port in Voltage Ratio Input mode";
      }
   },
   PN_VCP1100(105) {
      @Override
      public String getMessage() {
         return "30A Current Sensor";
      }
   },
   PN_DCC1100(108) {
      @Override
      public String getMessage() {
         return "BLDC Motor Controller";
      }
   },
   PN_HIN1101(109) {
      @Override
      public String getMessage() {
         return "Dial Encoder";
      }
   },
   PN_DCC1001(110) {
      @Override
      public String getMessage() {
         return "Small DC Motor Controller";
      }
   },
   PN_DICTIONARY(111) {
      @Override
      public String getMessage() {
         return "Dictionary";
      }
   },
   PN_STC1001(115) {
      @Override
      public String getMessage() {
         return "Bipolar Stepper Motor SmallController";
      }
   },
   PN_DCC1002(117) {
      @Override
      public String getMessage() {
         return "4A Small DC Motor Controller";
      }
   },
   PN_STC1002(118) {
      @Override
      public String getMessage() {
         return "8A Bipolar Stepper Motor Controller";
      }
   },
   PN_STC1003(119) {
      @Override
      public String getMessage() {
         return "4A Bipolar Stepper Motor SmallController";
      }
   },
   PN_DCC1003(120) {
      @Override
      public String getMessage() {
         return "2 Channel DC Motor Controller";
      }
   },
   PN_DST1001(121) {
      @Override
      public String getMessage() {
         return "650mm distance sensor";
      }
   },
   PN_HUB5000(123) {
      @Override
      public String getMessage() {
         return "Phidget Network Hub with 6 ports";
      }
   },
   PN_RCC0004(124) {
      @Override
      public String getMessage() {
         return "PhidgetAdvancedServo 8-Motor (RCC0004)";
      }
   },
   UNKNOWN(125) {
      @Override
      public String getMessage() {
         return "Unknown device";
      }
   },
   PN_DST1002(126) {
      @Override
      public String getMessage() {
         return "1200mm distance sensor";
      }
   },
   PN_HUM1001(127) {
      @Override
      public String getMessage() {
         return "Humidity Sensor";
      }
   };

   private final int code;

   public static DeviceID getEnum(int var0) {
      switch (var0) {
         case 0:
            return NONE;
         case 1:
            return PN_INTERFACE_KIT488;
         case 2:
            return PN_1000;
         case 3:
            return PN_1001;
         case 4:
            return PN_1002;
         case 5:
            return PN_1008;
         case 6:
            return PN_1010_1013_1018_1019;
         case 7:
            return PN_1011;
         case 8:
            return PN_1012;
         case 9:
            return PN_1014;
         case 10:
            return PN_1015;
         case 11:
            return PN_1016;
         case 12:
            return PN_1017;
         case 13:
            return PN_1023;
         case 14:
            return PN_1024;
         case 15:
            return PN_1030;
         case 16:
            return PN_1031;
         case 17:
            return PN_1032;
         case 18:
            return PN_1040;
         case 19:
            return PN_1041;
         case 20:
            return PN_1042;
         case 21:
            return PN_1043;
         case 22:
            return PN_1044;
         case 23:
            return PN_1045;
         case 24:
            return PN_1046;
         case 25:
            return PN_1047;
         case 26:
            return PN_1048;
         case 27:
            return PN_1049;
         case 28:
            return PN_1051;
         case 29:
            return PN_1052;
         case 30:
            return PN_1053;
         case 31:
            return PN_1054;
         case 32:
            return PN_1055;
         case 33:
            return PN_1056;
         case 34:
            return PN_1057;
         case 35:
            return PN_1058;
         case 36:
            return PN_1059;
         case 37:
            return PN_1060;
         case 38:
            return PN_1061;
         case 39:
            return PN_1062;
         case 40:
            return PN_1063;
         case 41:
            return PN_1064;
         case 42:
            return PN_1065;
         case 43:
            return PN_1066;
         case 44:
            return PN_1067;
         case 45:
            return PN_1202_1203;
         case 46:
            return PN_1204;
         case 47:
            return PN_1215__1218;
         case 48:
            return PN_1219__1222;
         case 49:
            return PN_ADP1000;
         case 50:
         case 65:
         case 66:
         case 68:
         case 91:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 106:
         case 107:
         case 112:
         case 113:
         case 114:
         case 116:
         case 122:
         default:
            return null;
         case 51:
            return PN_DAQ1000;
         case 52:
            return PN_DAQ1200;
         case 53:
            return PN_DAQ1300;
         case 54:
            return PN_DAQ1301;
         case 55:
            return PN_DAQ1400;
         case 56:
            return PN_DAQ1500;
         case 57:
            return PN_DCC1000;
         case 58:
            return PN_DST1000;
         case 59:
            return PN_DST1200;
         case 60:
            return PN_ENC1000;
         case 61:
            return PN_HIN1000;
         case 62:
            return PN_HIN1001;
         case 63:
            return PN_HIN1100;
         case 64:
            return PN_HUB0000;
         case 67:
            return PN_HUB0004;
         case 69:
            return PN_HUM1000;
         case 70:
            return PN_LCD1100;
         case 71:
            return PN_LED1000;
         case 72:
            return PN_LUX1000;
         case 73:
            return PN_MOT1100;
         case 74:
            return PN_MOT1101;
         case 75:
            return PN_OUT1000;
         case 76:
            return PN_OUT1001;
         case 77:
            return PN_OUT1002;
         case 78:
            return PN_OUT1100;
         case 79:
            return PN_PRE1000;
         case 80:
            return PN_RCC1000;
         case 81:
            return PN_REL1000;
         case 82:
            return PN_REL1100;
         case 83:
            return PN_REL1101;
         case 84:
            return PN_SAF1000;
         case 85:
            return PN_SND1000;
         case 86:
            return PN_STC1000;
         case 87:
            return PN_TMP1000;
         case 88:
            return PN_TMP1100;
         case 89:
            return PN_TMP1101;
         case 90:
            return PN_TMP1200;
         case 92:
            return PN_VCP1000;
         case 93:
            return PN_VCP1001;
         case 94:
            return PN_VCP1002;
         case 95:
            return DIGITAL_INPUT_PORT;
         case 96:
            return DIGITAL_OUTPUT_PORT;
         case 97:
            return VOLTAGE_INPUT_PORT;
         case 98:
            return VOLTAGE_RATIO_INPUT_PORT;
         case 105:
            return PN_VCP1100;
         case 108:
            return PN_DCC1100;
         case 109:
            return PN_HIN1101;
         case 110:
            return PN_DCC1001;
         case 111:
            return PN_DICTIONARY;
         case 115:
            return PN_STC1001;
         case 117:
            return PN_DCC1002;
         case 118:
            return PN_STC1002;
         case 119:
            return PN_STC1003;
         case 120:
            return PN_DCC1003;
         case 121:
            return PN_DST1001;
         case 123:
            return PN_HUB5000;
         case 124:
            return PN_RCC0004;
         case 125:
            return UNKNOWN;
         case 126:
            return PN_DST1002;
         case 127:
            return PN_HUM1001;
      }
   }

   private DeviceID(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
