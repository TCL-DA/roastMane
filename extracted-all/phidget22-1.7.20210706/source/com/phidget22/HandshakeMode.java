package com.phidget22;

public enum HandshakeMode {
   DISABLED(1) {
      @Override
      public String getMessage() {
         return "RTS is never set, and CTS is ignored.";
      }
   },
   REQUEST_TO_SEND(2) {
      @Override
      public String getMessage() {
         return "RTS Pin requests to transmit data. CTS input confirms we can send data.";
      }
   },
   READY_TO_RECEIVE(3) {
      @Override
      public String getMessage() {
         return "RTS signals this device can receive data. CTS confirms we can send data.";
      }
   };

   private final int code;

   public static HandshakeMode getEnum(int var0) {
      switch (var0) {
         case 1:
            return DISABLED;
         case 2:
            return REQUEST_TO_SEND;
         case 3:
            return READY_TO_RECEIVE;
         default:
            return null;
      }
   }

   private HandshakeMode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
