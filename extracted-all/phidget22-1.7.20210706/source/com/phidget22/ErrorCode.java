package com.phidget22;

public enum ErrorCode {
   SUCCESS(0) {
      @Override
      public String getMessage() {
         return "Success";
      }
   },
   NOT_PERMITTED(1) {
      @Override
      public String getMessage() {
         return "Not Permitted";
      }
   },
   NO_SUCH_ENTITY(2) {
      @Override
      public String getMessage() {
         return "No Such Entity";
      }
   },
   TIMEOUT(3) {
      @Override
      public String getMessage() {
         return "Timed Out";
      }
   },
   KEEP_ALIVE(58) {
      @Override
      public String getMessage() {
         return "Keep Alive Failure";
      }
   },
   INTERRUPTED(4) {
      @Override
      public String getMessage() {
         return "Op Interrupted";
      }
   },
   IO(5) {
      @Override
      public String getMessage() {
         return "IO Issue";
      }
   },
   NO_MEMORY(6) {
      @Override
      public String getMessage() {
         return "Memory Issue";
      }
   },
   ACCESS(7) {
      @Override
      public String getMessage() {
         return "Access (Permission) Issue";
      }
   },
   FAULT(8) {
      @Override
      public String getMessage() {
         return "Address Issue";
      }
   },
   BUSY(9) {
      @Override
      public String getMessage() {
         return "Resource Busy";
      }
   },
   EXISTS(10) {
      @Override
      public String getMessage() {
         return "Object Exists";
      }
   },
   IS_NOT_DIRECTORY(11) {
      @Override
      public String getMessage() {
         return "Object is not a directory";
      }
   },
   IS_DIRECTORY(12) {
      @Override
      public String getMessage() {
         return "Object is a directory";
      }
   },
   INVALID(13) {
      @Override
      public String getMessage() {
         return "Invalid";
      }
   },
   TOO_MANY_FILES_SYSTEM(14) {
      @Override
      public String getMessage() {
         return "Too many open files in system";
      }
   },
   TOO_MANY_FILES(15) {
      @Override
      public String getMessage() {
         return "Too many open files";
      }
   },
   NO_SPACE(16) {
      @Override
      public String getMessage() {
         return "Not enough space";
      }
   },
   FILE_TOO_BIG(17) {
      @Override
      public String getMessage() {
         return "File too Big";
      }
   },
   READ_ONLY_FILESYSTEM(18) {
      @Override
      public String getMessage() {
         return "Read Only Filesystem";
      }
   },
   READ_ONLY(19) {
      @Override
      public String getMessage() {
         return "Read Only Object";
      }
   },
   UNSUPPORTED(20) {
      @Override
      public String getMessage() {
         return "Operation Not Supported";
      }
   },
   INVALID_ARGUMENT(21) {
      @Override
      public String getMessage() {
         return "Invalid Argument";
      }
   },
   TRY_AGAIN(22) {
      @Override
      public String getMessage() {
         return "Try again";
      }
   },
   NOT_EMPTY(26) {
      @Override
      public String getMessage() {
         return "Not Empty";
      }
   },
   UNEXPECTED(28) {
      @Override
      public String getMessage() {
         return "Unexpected Error";
      }
   },
   DUPLICATE(27) {
      @Override
      public String getMessage() {
         return "Duplicate";
      }
   },
   BAD_PASSWORD(37) {
      @Override
      public String getMessage() {
         return "Bad Credential";
      }
   },
   NETWORK_UNAVAILABLE(45) {
      @Override
      public String getMessage() {
         return "Network Unavailable";
      }
   },
   CONNECTION_REFUSED(35) {
      @Override
      public String getMessage() {
         return "Connection Refused";
      }
   },
   CONNECTION_RESET(46) {
      @Override
      public String getMessage() {
         return "Connection Reset";
      }
   },
   HOST_UNREACHABLE(48) {
      @Override
      public String getMessage() {
         return "No route to host";
      }
   },
   NO_SUCH_DEVICE(40) {
      @Override
      public String getMessage() {
         return "No Such Device";
      }
   },
   WRONG_DEVICE(50) {
      @Override
      public String getMessage() {
         return "Wrong Device";
      }
   },
   BROKEN_PIPE(41) {
      @Override
      public String getMessage() {
         return "Broken Pipe";
      }
   },
   NAME_RESOLUTION_FAILURE(44) {
      @Override
      public String getMessage() {
         return "Name Resolution Failure";
      }
   },
   UNKNOWN_VALUE(51) {
      @Override
      public String getMessage() {
         return "Unknown or Invalid Value";
      }
   },
   NOT_ATTACHED(52) {
      @Override
      public String getMessage() {
         return "Device not Attached";
      }
   },
   INVALID_PACKET(53) {
      @Override
      public String getMessage() {
         return "Invalid or Unexpected Packet";
      }
   },
   TOO_BIG(54) {
      @Override
      public String getMessage() {
         return "Argument List Too Long";
      }
   },
   BAD_VERSION(55) {
      @Override
      public String getMessage() {
         return "Bad Version";
      }
   },
   CLOSED(56) {
      @Override
      public String getMessage() {
         return "Closed";
      }
   },
   NOT_CONFIGURED(57) {
      @Override
      public String getMessage() {
         return "Not Configured";
      }
   },
   END_OF_FILE(31) {
      @Override
      public String getMessage() {
         return "End of File";
      }
   },
   FAILSAFE(59) {
      @Override
      public String getMessage() {
         return "Failsafe Triggered";
      }
   };

   private final int code;

   public static ErrorCode getEnum(int var0) {
      switch (var0) {
         case 0:
            return SUCCESS;
         case 1:
            return NOT_PERMITTED;
         case 2:
            return NO_SUCH_ENTITY;
         case 3:
            return TIMEOUT;
         case 4:
            return INTERRUPTED;
         case 5:
            return IO;
         case 6:
            return NO_MEMORY;
         case 7:
            return ACCESS;
         case 8:
            return FAULT;
         case 9:
            return BUSY;
         case 10:
            return EXISTS;
         case 11:
            return IS_NOT_DIRECTORY;
         case 12:
            return IS_DIRECTORY;
         case 13:
            return INVALID;
         case 14:
            return TOO_MANY_FILES_SYSTEM;
         case 15:
            return TOO_MANY_FILES;
         case 16:
            return NO_SPACE;
         case 17:
            return FILE_TOO_BIG;
         case 18:
            return READ_ONLY_FILESYSTEM;
         case 19:
            return READ_ONLY;
         case 20:
            return UNSUPPORTED;
         case 21:
            return INVALID_ARGUMENT;
         case 22:
            return TRY_AGAIN;
         case 23:
         case 24:
         case 25:
         case 29:
         case 30:
         case 32:
         case 33:
         case 34:
         case 36:
         case 38:
         case 39:
         case 42:
         case 43:
         case 47:
         case 49:
         default:
            return null;
         case 26:
            return NOT_EMPTY;
         case 27:
            return DUPLICATE;
         case 28:
            return UNEXPECTED;
         case 31:
            return END_OF_FILE;
         case 35:
            return CONNECTION_REFUSED;
         case 37:
            return BAD_PASSWORD;
         case 40:
            return NO_SUCH_DEVICE;
         case 41:
            return BROKEN_PIPE;
         case 44:
            return NAME_RESOLUTION_FAILURE;
         case 45:
            return NETWORK_UNAVAILABLE;
         case 46:
            return CONNECTION_RESET;
         case 48:
            return HOST_UNREACHABLE;
         case 50:
            return WRONG_DEVICE;
         case 51:
            return UNKNOWN_VALUE;
         case 52:
            return NOT_ATTACHED;
         case 53:
            return INVALID_PACKET;
         case 54:
            return TOO_BIG;
         case 55:
            return BAD_VERSION;
         case 56:
            return CLOSED;
         case 57:
            return NOT_CONFIGURED;
         case 58:
            return KEEP_ALIVE;
         case 59:
            return FAILSAFE;
      }
   }

   private ErrorCode(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
