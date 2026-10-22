package com.phidget22;

public enum VoltageRatioSensorType {
   VOLTAGE_RATIO(0) {
      @Override
      public String getMessage() {
         return "Generic ratiometric sensor";
      }
   },
   PN_1101_SHARP2D120X(11011) {
      @Override
      public String getMessage() {
         return "1101 - IR Distance Adapter, with Sharp Distance Sensor 2D120X (4-30cm)";
      }
   },
   PN_1101_SHARP2Y0A21(11012) {
      @Override
      public String getMessage() {
         return "1101 - IR Distance Adapter, with Sharp Distance Sensor 2Y0A21 (10-80cm)";
      }
   },
   PN_1101_SHARP2Y0A02(11013) {
      @Override
      public String getMessage() {
         return "1101 - IR Distance Adapter, with Sharp Distance Sensor 2Y0A02 (20-150cm)";
      }
   },
   PN_1102(11020) {
      @Override
      public String getMessage() {
         return "1102 - IR Reflective Sensor 5mm";
      }
   },
   PN_1103(11030) {
      @Override
      public String getMessage() {
         return "1103 - IR Reflective Sensor 10cm";
      }
   },
   PN_1104(11040) {
      @Override
      public String getMessage() {
         return "1104 - Vibration Sensor";
      }
   },
   PN_1105(11050) {
      @Override
      public String getMessage() {
         return "1105 - Light Sensor";
      }
   },
   PN_1106(11060) {
      @Override
      public String getMessage() {
         return "1106 - Force Sensor";
      }
   },
   PN_1107(11070) {
      @Override
      public String getMessage() {
         return "1107 - Humidity Sensor";
      }
   },
   PN_1108(11080) {
      @Override
      public String getMessage() {
         return "1108 - Magnetic Sensor";
      }
   },
   PN_1109(11090) {
      @Override
      public String getMessage() {
         return "1109 - Rotation Sensor";
      }
   },
   PN_1110(11100) {
      @Override
      public String getMessage() {
         return "1110 - Touch Sensor";
      }
   },
   PN_1111(11110) {
      @Override
      public String getMessage() {
         return "1111 - Motion Sensor";
      }
   },
   PN_1112(11120) {
      @Override
      public String getMessage() {
         return "1112 - Slider 60";
      }
   },
   PN_1113(11130) {
      @Override
      public String getMessage() {
         return "1113 - Mini Joy Stick Sensor";
      }
   },
   PN_1115(11150) {
      @Override
      public String getMessage() {
         return "1115 - Pressure Sensor";
      }
   },
   PN_1116(11160) {
      @Override
      public String getMessage() {
         return "1116 - Multi-turn Rotation Sensor";
      }
   },
   PN_1118_AC(11181) {
      @Override
      public String getMessage() {
         return "1118 - 50Amp Current Sensor AC";
      }
   },
   PN_1118_DC(11182) {
      @Override
      public String getMessage() {
         return "1118 - 50Amp Current Sensor DC";
      }
   },
   PN_1119_AC(11191) {
      @Override
      public String getMessage() {
         return "1119 - 20Amp Current Sensor AC";
      }
   },
   PN_1119_DC(11192) {
      @Override
      public String getMessage() {
         return "1119 - 20Amp Current Sensor DC";
      }
   },
   PN_1120(11200) {
      @Override
      public String getMessage() {
         return "1120 - FlexiForce Adapter";
      }
   },
   PN_1121(11210) {
      @Override
      public String getMessage() {
         return "1121 - Voltage Divider";
      }
   },
   PN_1122_AC(11221) {
      @Override
      public String getMessage() {
         return "1122 - 30 Amp Current Sensor AC";
      }
   },
   PN_1122_DC(11222) {
      @Override
      public String getMessage() {
         return "1122 - 30 Amp Current Sensor DC";
      }
   },
   PN_1124(11240) {
      @Override
      public String getMessage() {
         return "1124 - Precision Temperature Sensor";
      }
   },
   PN_1125_HUMIDITY(11251) {
      @Override
      public String getMessage() {
         return "1125 - Humidity Sensor";
      }
   },
   PN_1125_TEMPERATURE(11252) {
      @Override
      public String getMessage() {
         return "1125 - Temperature Sensor";
      }
   },
   PN_1126(11260) {
      @Override
      public String getMessage() {
         return "1126 - Differential Air Pressure Sensor +- 25kPa";
      }
   },
   PN_1128(11280) {
      @Override
      public String getMessage() {
         return "1128 - MaxBotix EZ-1 Sonar Sensor";
      }
   },
   PN_1129(11290) {
      @Override
      public String getMessage() {
         return "1129 - Touch Sensor";
      }
   },
   PN_1131(11310) {
      @Override
      public String getMessage() {
         return "1131 - Thin Force Sensor";
      }
   },
   PN_1134(11340) {
      @Override
      public String getMessage() {
         return "1134 - Switchable Voltage Divider";
      }
   },
   PN_1136(11360) {
      @Override
      public String getMessage() {
         return "1136 - Differential Air Pressure Sensor +-2 kPa";
      }
   },
   PN_1137(11370) {
      @Override
      public String getMessage() {
         return "1137 - Differential Air Pressure Sensor +-7 kPa";
      }
   },
   PN_1138(11380) {
      @Override
      public String getMessage() {
         return "1138 - Differential Air Pressure Sensor 50 kPa";
      }
   },
   PN_1139(11390) {
      @Override
      public String getMessage() {
         return "1139 - Differential Air Pressure Sensor 100 kPa";
      }
   },
   PN_1140(11400) {
      @Override
      public String getMessage() {
         return "1140 - Absolute Air Pressure Sensor 20-400 kPa";
      }
   },
   PN_1141(11410) {
      @Override
      public String getMessage() {
         return "1141 - Absolute Air Pressure Sensor 15-115 kPa";
      }
   },
   PN_1146(11460) {
      @Override
      public String getMessage() {
         return "1146 - IR Reflective Sensor 1-4mm";
      }
   },
   PN_3120(31200) {
      @Override
      public String getMessage() {
         return "3120 - Compression Load Cell (0-4.5 kg)";
      }
   },
   PN_3121(31210) {
      @Override
      public String getMessage() {
         return "3121 - Compression Load Cell (0-11.3 kg)";
      }
   },
   PN_3122(31220) {
      @Override
      public String getMessage() {
         return "3122 - Compression Load Cell (0-22.7 kg)";
      }
   },
   PN_3123(31230) {
      @Override
      public String getMessage() {
         return "3123 - Compression Load Cell (0-45.3 kg)";
      }
   },
   PN_3130(31300) {
      @Override
      public String getMessage() {
         return "3130 - Relative Humidity Sensor";
      }
   },
   PN_3520(35200) {
      @Override
      public String getMessage() {
         return "3520 - Sharp Distance Sensor (4-30cm)";
      }
   },
   PN_3521(35210) {
      @Override
      public String getMessage() {
         return "3521 - Sharp Distance Sensor (10-80cm)";
      }
   },
   PN_3522(35220) {
      @Override
      public String getMessage() {
         return "3522 - Sharp Distance Sensor (20-150cm)";
      }
   };

   private final int code;

   public static VoltageRatioSensorType getEnum(int var0) {
      switch (var0) {
         case 0:
            return VOLTAGE_RATIO;
         case 11011:
            return PN_1101_SHARP2D120X;
         case 11012:
            return PN_1101_SHARP2Y0A21;
         case 11013:
            return PN_1101_SHARP2Y0A02;
         case 11020:
            return PN_1102;
         case 11030:
            return PN_1103;
         case 11040:
            return PN_1104;
         case 11050:
            return PN_1105;
         case 11060:
            return PN_1106;
         case 11070:
            return PN_1107;
         case 11080:
            return PN_1108;
         case 11090:
            return PN_1109;
         case 11100:
            return PN_1110;
         case 11110:
            return PN_1111;
         case 11120:
            return PN_1112;
         case 11130:
            return PN_1113;
         case 11150:
            return PN_1115;
         case 11160:
            return PN_1116;
         case 11181:
            return PN_1118_AC;
         case 11182:
            return PN_1118_DC;
         case 11191:
            return PN_1119_AC;
         case 11192:
            return PN_1119_DC;
         case 11200:
            return PN_1120;
         case 11210:
            return PN_1121;
         case 11221:
            return PN_1122_AC;
         case 11222:
            return PN_1122_DC;
         case 11240:
            return PN_1124;
         case 11251:
            return PN_1125_HUMIDITY;
         case 11252:
            return PN_1125_TEMPERATURE;
         case 11260:
            return PN_1126;
         case 11280:
            return PN_1128;
         case 11290:
            return PN_1129;
         case 11310:
            return PN_1131;
         case 11340:
            return PN_1134;
         case 11360:
            return PN_1136;
         case 11370:
            return PN_1137;
         case 11380:
            return PN_1138;
         case 11390:
            return PN_1139;
         case 11400:
            return PN_1140;
         case 11410:
            return PN_1141;
         case 11460:
            return PN_1146;
         case 31200:
            return PN_3120;
         case 31210:
            return PN_3121;
         case 31220:
            return PN_3122;
         case 31230:
            return PN_3123;
         case 31300:
            return PN_3130;
         case 35200:
            return PN_3520;
         case 35210:
            return PN_3521;
         case 35220:
            return PN_3522;
         default:
            return null;
      }
   }

   private VoltageRatioSensorType(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
