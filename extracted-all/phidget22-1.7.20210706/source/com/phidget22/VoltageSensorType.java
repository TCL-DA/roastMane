package com.phidget22;

public enum VoltageSensorType {
   VOLTAGE(0) {
      @Override
      public String getMessage() {
         return "Generic voltage sensor";
      }
   },
   PN_1114(11140) {
      @Override
      public String getMessage() {
         return "1114 - Temperature Sensor";
      }
   },
   PN_1117(11170) {
      @Override
      public String getMessage() {
         return "1117 - Voltage Sensor";
      }
   },
   PN_1123(11230) {
      @Override
      public String getMessage() {
         return "1123 - Precision Voltage Sensor";
      }
   },
   PN_1127(11270) {
      @Override
      public String getMessage() {
         return "1127 - Precision Light Sensor";
      }
   },
   PN_1130_PH(11301) {
      @Override
      public String getMessage() {
         return "1130 - pH Adapter";
      }
   },
   PN_1130_ORP(11302) {
      @Override
      public String getMessage() {
         return "1130 - ORP Adapter";
      }
   },
   PN_1132(11320) {
      @Override
      public String getMessage() {
         return "1132 - 4-20mA Adapter";
      }
   },
   PN_1133(11330) {
      @Override
      public String getMessage() {
         return "1133 - Sound Sensor";
      }
   },
   PN_1135(11350) {
      @Override
      public String getMessage() {
         return "1135 - Precision Voltage Sensor";
      }
   },
   PN_1142(11420) {
      @Override
      public String getMessage() {
         return "1142 - Light Sensor 1000 lux";
      }
   },
   PN_1143(11430) {
      @Override
      public String getMessage() {
         return "1143 - Light Sensor 70000 lux";
      }
   },
   PN_3500(35000) {
      @Override
      public String getMessage() {
         return "3500 - AC Current Sensor 10Amp";
      }
   },
   PN_3501(35010) {
      @Override
      public String getMessage() {
         return "3501 - AC Current Sensor 25Amp";
      }
   },
   PN_3502(35020) {
      @Override
      public String getMessage() {
         return "3502 - AC Current Sensor 50Amp";
      }
   },
   PN_3503(35030) {
      @Override
      public String getMessage() {
         return "3503 - AC Current Sensor 100Amp";
      }
   },
   PN_3507(35070) {
      @Override
      public String getMessage() {
         return "3507 - AC Voltage Sensor 0-250V (50Hz)";
      }
   },
   PN_3508(35080) {
      @Override
      public String getMessage() {
         return "3508 - AC Voltage Sensor 0-250V (60Hz)";
      }
   },
   PN_3509(35090) {
      @Override
      public String getMessage() {
         return "3509 - DC Voltage Sensor 0-200V";
      }
   },
   PN_3510(35100) {
      @Override
      public String getMessage() {
         return "3510 - DC Voltage Sensor 0-75V";
      }
   },
   PN_3511(35110) {
      @Override
      public String getMessage() {
         return "3511 - DC Current Sensor 0-10mA";
      }
   },
   PN_3512(35120) {
      @Override
      public String getMessage() {
         return "3512 - DC Current Sensor 0-100mA";
      }
   },
   PN_3513(35130) {
      @Override
      public String getMessage() {
         return "3513 - DC Current Sensor 0-1A";
      }
   },
   PN_3514(35140) {
      @Override
      public String getMessage() {
         return "3514 - AC Active Power Sensor 0-250V*0-30A (50Hz)";
      }
   },
   PN_3515(35150) {
      @Override
      public String getMessage() {
         return "3515 - AC Active Power Sensor 0-250V*0-30A (60Hz)";
      }
   },
   PN_3516(35160) {
      @Override
      public String getMessage() {
         return "3516 - AC Active Power Sensor 0-250V*0-5A (50Hz)";
      }
   },
   PN_3517(35170) {
      @Override
      public String getMessage() {
         return "3517 - AC Active Power Sensor 0-250V*0-5A (60Hz)";
      }
   },
   PN_3518(35180) {
      @Override
      public String getMessage() {
         return "3518 - AC Active Power Sensor 0-110V*0-5A (60Hz)";
      }
   },
   PN_3519(35190) {
      @Override
      public String getMessage() {
         return "3519 - AC Active Power Sensor 0-110V*0-15A (60Hz)";
      }
   },
   PN_3584(35840) {
      @Override
      public String getMessage() {
         return "3584 - 0-50A DC Current Transducer";
      }
   },
   PN_3585(35850) {
      @Override
      public String getMessage() {
         return "3585 - 0-100A DC Current Transducer";
      }
   },
   PN_3586(35860) {
      @Override
      public String getMessage() {
         return "3586 - 0-250A DC Current Transducer";
      }
   },
   PN_3587(35870) {
      @Override
      public String getMessage() {
         return "3587 - +-50A DC Current Transducer";
      }
   },
   PN_3588(35880) {
      @Override
      public String getMessage() {
         return "3588 - +-100A DC Current Transducer";
      }
   },
   PN_3589(35890) {
      @Override
      public String getMessage() {
         return "3589 - +-250A DC Current Transducer";
      }
   },
   PN_MOT2002_LOW(20020) {
      @Override
      public String getMessage() {
         return "MOT2002 - Motion Sensor Low Sensitivity";
      }
   },
   PN_MOT2002_MED(20021) {
      @Override
      public String getMessage() {
         return "MOT2002 - Motion Sensor Medium Sensitivity";
      }
   },
   PN_MOT2002_HIGH(20022) {
      @Override
      public String getMessage() {
         return "MOT2002 - Motion Sensor High Sensitivity";
      }
   },
   PN_VCP4114(41140) {
      @Override
      public String getMessage() {
         return "VCP4114 - +-25A DC Current Transducer";
      }
   };

   private final int code;

   public static VoltageSensorType getEnum(int var0) {
      switch (var0) {
         case 0:
            return VOLTAGE;
         case 11140:
            return PN_1114;
         case 11170:
            return PN_1117;
         case 11230:
            return PN_1123;
         case 11270:
            return PN_1127;
         case 11301:
            return PN_1130_PH;
         case 11302:
            return PN_1130_ORP;
         case 11320:
            return PN_1132;
         case 11330:
            return PN_1133;
         case 11350:
            return PN_1135;
         case 11420:
            return PN_1142;
         case 11430:
            return PN_1143;
         case 20020:
            return PN_MOT2002_LOW;
         case 20021:
            return PN_MOT2002_MED;
         case 20022:
            return PN_MOT2002_HIGH;
         case 35000:
            return PN_3500;
         case 35010:
            return PN_3501;
         case 35020:
            return PN_3502;
         case 35030:
            return PN_3503;
         case 35070:
            return PN_3507;
         case 35080:
            return PN_3508;
         case 35090:
            return PN_3509;
         case 35100:
            return PN_3510;
         case 35110:
            return PN_3511;
         case 35120:
            return PN_3512;
         case 35130:
            return PN_3513;
         case 35140:
            return PN_3514;
         case 35150:
            return PN_3515;
         case 35160:
            return PN_3516;
         case 35170:
            return PN_3517;
         case 35180:
            return PN_3518;
         case 35190:
            return PN_3519;
         case 35840:
            return PN_3584;
         case 35850:
            return PN_3585;
         case 35860:
            return PN_3586;
         case 35870:
            return PN_3587;
         case 35880:
            return PN_3588;
         case 35890:
            return PN_3589;
         case 41140:
            return PN_VCP4114;
         default:
            return null;
      }
   }

   private VoltageSensorType(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
