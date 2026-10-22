package com.phidget22;

public enum PacketErrorCode {
   OK(0) {
      @Override
      public String getMessage() {
         return "No error";
      }
   },
   UNKNOWN(1) {
      @Override
      public String getMessage() {
         return "Unknown Error";
      }
   },
   TIMEOUT(2) {
      @Override
      public String getMessage() {
         return "The response packet timed out";
      }
   },
   FORMATERR(3) {
      @Override
      public String getMessage() {
         return "Something about the received transmission didn't match the expected format.";
      }
   },
   INVALID(4) {
      @Override
      public String getMessage() {
         return "The input lines are invalid. This likely means a cable has been unplugged.";
      }
   },
   OVERRUN(5) {
      @Override
      public String getMessage() {
         return "Data is being received fater than it can be processed. Some has been lost.";
      }
   },
   CORRUPT(6) {
      @Override
      public String getMessage() {
         return "Something behind the scenes got out of sequence.";
      }
   };

   private final int code;

   public static PacketErrorCode getEnum(int var0) {
      switch (var0) {
         case 0:
            return OK;
         case 1:
            return UNKNOWN;
         case 2:
            return TIMEOUT;
         case 3:
            return FORMATERR;
         case 4:
            return INVALID;
         case 5:
            return OVERRUN;
         case 6:
            return CORRUPT;
         default:
            return null;
      }
   }

   private PacketErrorCode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
