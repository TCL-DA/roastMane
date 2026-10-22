package com.phidget22;

public enum ErrorEventCode {
   BAD_VERSION(1) {
      @Override
      public String getMessage() {
         return "Version Mismatch";
      }
   },
   BUSY(2) {
      @Override
      public String getMessage() {
         return "Phidget in Use";
      }
   },
   NETWORK(3) {
      @Override
      public String getMessage() {
         return "Network Error";
      }
   },
   DISPATCH(4) {
      @Override
      public String getMessage() {
         return "Dispatch Error";
      }
   },
   FAILURE(5) {
      @Override
      public String getMessage() {
         return "General Failure";
      }
   },
   SUCCESS(4096) {
      @Override
      public String getMessage() {
         return "Error Cleared";
      }
   },
   OVERRUN(4098) {
      @Override
      public String getMessage() {
         return "Sample Overrun";
      }
   },
   PACKET_LOST(4099) {
      @Override
      public String getMessage() {
         return "Packet Lost";
      }
   },
   WRAP_AROUND(4100) {
      @Override
      public String getMessage() {
         return "Wrap-Around";
      }
   },
   OVER_TEMPERATURE(4101) {
      @Override
      public String getMessage() {
         return "Over-Temperature";
      }
   },
   OVER_CURRENT(4102) {
      @Override
      public String getMessage() {
         return "Over-Current";
      }
   },
   OUT_OF_RANGE(4103) {
      @Override
      public String getMessage() {
         return "Out of Range";
      }
   },
   BAD_POWER(4104) {
      @Override
      public String getMessage() {
         return "Bad Power";
      }
   },
   SATURATION(4105) {
      @Override
      public String getMessage() {
         return "Saturation";
      }
   },
   OVER_VOLTAGE(4107) {
      @Override
      public String getMessage() {
         return "Over-Voltage";
      }
   },
   FAILSAFE_CONDITION(4108) {
      @Override
      public String getMessage() {
         return "Failsafe";
      }
   },
   VOLTAGE_ERROR(4109) {
      @Override
      public String getMessage() {
         return "Voltage Error";
      }
   },
   ENERGY_DUMP_CONDITION(4110) {
      @Override
      public String getMessage() {
         return "Energy Dump";
      }
   },
   MOTOR_STALL_CONDITION(4111) {
      @Override
      public String getMessage() {
         return "Motor Stall";
      }
   },
   INVALID_STATE_CONDITION(4112) {
      @Override
      public String getMessage() {
         return "Invalid State";
      }
   },
   BAD_CONNECTION_CONDITION(4113) {
      @Override
      public String getMessage() {
         return "Bad Connection";
      }
   };

   private final int code;

   public static ErrorEventCode getEnum(int var0) {
      switch (var0) {
         case 1:
            return BAD_VERSION;
         case 2:
            return BUSY;
         case 3:
            return NETWORK;
         case 4:
            return DISPATCH;
         case 5:
            return FAILURE;
         case 4096:
            return SUCCESS;
         case 4098:
            return OVERRUN;
         case 4099:
            return PACKET_LOST;
         case 4100:
            return WRAP_AROUND;
         case 4101:
            return OVER_TEMPERATURE;
         case 4102:
            return OVER_CURRENT;
         case 4103:
            return OUT_OF_RANGE;
         case 4104:
            return BAD_POWER;
         case 4105:
            return SATURATION;
         case 4107:
            return OVER_VOLTAGE;
         case 4108:
            return FAILSAFE_CONDITION;
         case 4109:
            return VOLTAGE_ERROR;
         case 4110:
            return ENERGY_DUMP_CONDITION;
         case 4111:
            return MOTOR_STALL_CONDITION;
         case 4112:
            return INVALID_STATE_CONDITION;
         case 4113:
            return BAD_CONNECTION_CONDITION;
         default:
            return null;
      }
   }

   private ErrorEventCode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
