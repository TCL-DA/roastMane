package com.digitalpetri.enip;

import io.netty.buffer.ByteBuf;

public enum EnipStatus {
   EIP_SUCCESS(0),
   INVALID_UNSUPPORTED(1),
   INSUFFICIENT_MEMORY(2),
   MALFORMED_DATA(3),
   INVALID_SESSION_HANDLE(100),
   INVALID_LENGTH(101),
   UNSUPPORTED_PROTOCOL_VERSION(105);

   private final int status;

   private EnipStatus(int status) {
      this.status = status;
   }

   public int getStatus() {
      return this.status;
   }

   public static ByteBuf encode(EnipStatus status, ByteBuf buffer) {
      buffer.writeInt(status.getStatus());
      return buffer;
   }

   public static EnipStatus decode(ByteBuf buffer) {
      int status = buffer.readInt();
      switch (status) {
         case 0:
            return EIP_SUCCESS;
         case 1:
            return INVALID_UNSUPPORTED;
         case 2:
            return INSUFFICIENT_MEMORY;
         case 3:
            return MALFORMED_DATA;
         case 100:
            return INVALID_SESSION_HANDLE;
         case 101:
            return INVALID_LENGTH;
         case 105:
            return UNSUPPORTED_PROTOCOL_VERSION;
         default:
            throw new RuntimeException(String.format("unrecognized status: 0x%02X", status));
      }
   }
}
