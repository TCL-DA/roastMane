package com.phidget22;

public enum Protocol {
   RS485(1) {
      @Override
      public String getMessage() {
         return "Basic half-duplex RS485. Always receiving until you send data.";
      }
   },
   RS422(2) {
      @Override
      public String getMessage() {
         return "Uses full-duplex RS422 communication.";
      }
   },
   DMX512(3) {
      @Override
      public String getMessage() {
         return "Allows communication with DMX512-compatible devices, such as stage lighting";
      }
   },
   MODBUS_RTU(4) {
      @Override
      public String getMessage() {
         return "Allows communication with MODBUS RTU compatible devices";
      }
   },
   SPI(5) {
      @Override
      public String getMessage() {
         return "Allows communication with SPI compatible devices";
      }
   },
   I2C(6) {
      @Override
      public String getMessage() {
         return "Allows communication with I2C compatible devices";
      }
   },
   UART(7) {
      @Override
      public String getMessage() {
         return "Allows communication with UART compatible devices";
      }
   },
   RS232(8) {
      @Override
      public String getMessage() {
         return "Allows communication with RS232 compatible devices";
      }
   };

   private final int code;

   public static Protocol getEnum(int var0) {
      switch (var0) {
         case 1:
            return RS485;
         case 2:
            return RS422;
         case 3:
            return DMX512;
         case 4:
            return MODBUS_RTU;
         case 5:
            return SPI;
         case 6:
            return I2C;
         case 7:
            return UART;
         case 8:
            return RS232;
         default:
            return null;
      }
   }

   private Protocol(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
