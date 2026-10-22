package org.eclipse.jetty.websocket.core;

public final class OpCode {
   public static final byte CONTINUATION = 0;
   public static final byte TEXT = 1;
   public static final byte BINARY = 2;
   public static final byte CLOSE = 8;
   public static final byte PING = 9;
   public static final byte PONG = 10;
   public static final byte UNDEFINED = -1;

   public static byte getOpCode(byte firstByte) {
      return (byte)(firstByte & 15);
   }

   public static boolean isControlFrame(byte opcode) {
      switch (opcode) {
         case 8:
         case 9:
         case 10:
            return true;
         default:
            return false;
      }
   }

   public static boolean isDataFrame(byte opcode) {
      switch (opcode) {
         case 0:
         case 1:
         case 2:
            return true;
         default:
            return false;
      }
   }

   public static boolean isKnown(byte opcode) {
      switch (opcode) {
         case 0:
         case 1:
         case 2:
         case 8:
         case 9:
         case 10:
            return true;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            return false;
      }
   }

   public static String name(byte opcode) {
      switch (opcode) {
         case -1:
            return "NO-OP";
         case 0:
            return "CONTINUATION";
         case 1:
            return "TEXT";
         case 2:
            return "BINARY";
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            return "NON-SPEC[" + opcode + "]";
         case 8:
            return "CLOSE";
         case 9:
            return "PING";
         case 10:
            return "PONG";
      }
   }
}
