package com.digitalpetri.enip.commands;

import com.digitalpetri.enip.cpf.CpfPacket;
import io.netty.buffer.ByteBuf;

public final class SendUnitData extends Command {
   public static final long DEFAULT_INTERFACE_HANDLE = 0L;
   public static final int DEFAULT_TIMEOUT = 0;
   private final long interfaceHandle;
   private final int timeout;
   private final CpfPacket packet;

   public SendUnitData(CpfPacket packet) {
      this(0L, 0, packet);
   }

   public SendUnitData(long interfaceHandle, int timeout, CpfPacket packet) {
      super(CommandCode.SendUnitData);
      this.interfaceHandle = interfaceHandle;
      this.timeout = timeout;
      this.packet = packet;
   }

   public long getInterfaceHandle() {
      return this.interfaceHandle;
   }

   public int getTimeout() {
      return this.timeout;
   }

   public CpfPacket getPacket() {
      return this.packet;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SendUnitData that = (SendUnitData)o;
         return this.interfaceHandle == that.interfaceHandle && this.timeout == that.timeout && this.packet.equals(that.packet);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = (int)(this.interfaceHandle ^ this.interfaceHandle >>> 32);
      result = 31 * result + this.timeout;
      return 31 * result + this.packet.hashCode();
   }

   public static ByteBuf encode(SendUnitData command, ByteBuf buffer) {
      buffer.writeInt((int)command.getInterfaceHandle());
      buffer.writeShort(command.getTimeout());
      CpfPacket.encode(command.getPacket(), buffer);
      return buffer;
   }

   public static SendUnitData decode(ByteBuf buffer) {
      return new SendUnitData(buffer.readUnsignedInt(), buffer.readUnsignedShort(), CpfPacket.decode(buffer));
   }
}
