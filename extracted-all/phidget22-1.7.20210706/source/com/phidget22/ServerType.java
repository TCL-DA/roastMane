package com.phidget22;

public enum ServerType {
   DEVICE_REMOTE(3) {
      @Override
      public String getMessage() {
         return "Phidget22 Server";
      }
   },
   WWWREMOTE(6) {
      @Override
      public String getMessage() {
         return "Phidget22 Web server";
      }
   },
   SBC(7) {
      @Override
      public String getMessage() {
         return "Phidget SBC";
      }
   };

   private final int code;

   public static ServerType getEnum(int var0) {
      switch (var0) {
         case 3:
            return DEVICE_REMOTE;
         case 4:
         case 5:
         default:
            return null;
         case 6:
            return WWWREMOTE;
         case 7:
            return SBC;
      }
   }

   private ServerType(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
