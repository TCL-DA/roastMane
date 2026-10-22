package com.digitalpetri.enip.commands;

import io.netty.buffer.ByteBuf;

public final class RegisterSession extends Command {
   public static final int DEFAULT_PROTOCOL_VERSION = 1;
   public static final int DEFAULT_OPTION_FLAGS = 0;
   private final int protocolVersion;
   private final int optionFlags;

   public RegisterSession() {
      this(1, 0);
   }

   public RegisterSession(int protocolVersion, int optionFlags) {
      super(CommandCode.RegisterSession);
      this.protocolVersion = protocolVersion;
      this.optionFlags = optionFlags;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public int getOptionFlags() {
      return this.optionFlags;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         RegisterSession that = (RegisterSession)o;
         return this.optionFlags == that.optionFlags && this.protocolVersion == that.protocolVersion;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.protocolVersion;
      return 31 * result + this.optionFlags;
   }

   public static ByteBuf encode(RegisterSession command, ByteBuf buffer) {
      buffer.writeShort(command.getProtocolVersion());
      buffer.writeShort(command.getOptionFlags());
      return buffer;
   }

   public static RegisterSession decode(ByteBuf buffer) {
      return new RegisterSession(buffer.readUnsignedShort(), buffer.readUnsignedShort());
   }
}
