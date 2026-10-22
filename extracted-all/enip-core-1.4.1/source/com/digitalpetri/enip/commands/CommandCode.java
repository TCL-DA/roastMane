package com.digitalpetri.enip.commands;

import io.netty.buffer.ByteBuf;

public enum CommandCode {
   Nop(0),
   ListServices(4),
   ListIdentity(99),
   ListInterfaces(100),
   RegisterSession(101),
   UnRegisterSession(102),
   SendRRData(111),
   SendUnitData(112);

   private final int code;

   private CommandCode(int code) {
      this.code = code;
   }

   public int getCode() {
      return this.code;
   }

   public static ByteBuf encode(CommandCode commandCode, ByteBuf buffer) {
      buffer.writeShort(commandCode.getCode());
      return buffer;
   }

   public static CommandCode decode(ByteBuf buffer) {
      int code = buffer.readUnsignedShort();
      switch (code) {
         case 0:
            return Nop;
         case 4:
            return ListServices;
         case 99:
            return ListIdentity;
         case 100:
            return ListInterfaces;
         case 101:
            return RegisterSession;
         case 102:
            return UnRegisterSession;
         case 111:
            return SendRRData;
         case 112:
            return SendUnitData;
         default:
            throw new RuntimeException(String.format("unrecognized command code: 0x%02X", code));
      }
   }
}
