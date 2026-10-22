package com.digitalpetri.enip;

import com.digitalpetri.enip.commands.Command;
import com.digitalpetri.enip.commands.CommandCode;
import com.digitalpetri.enip.commands.ListIdentity;
import com.digitalpetri.enip.commands.ListInterfaces;
import com.digitalpetri.enip.commands.ListServices;
import com.digitalpetri.enip.commands.Nop;
import com.digitalpetri.enip.commands.RegisterSession;
import com.digitalpetri.enip.commands.SendRRData;
import com.digitalpetri.enip.commands.SendUnitData;
import com.digitalpetri.enip.commands.UnRegisterSession;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;

public final class EnipPacket {
   private final CommandCode commandCode;
   private final long sessionHandle;
   private final EnipStatus status;
   private final long senderContext;
   @Nullable
   private final Command command;

   public EnipPacket(CommandCode commandCode, long sessionHandle, EnipStatus status, long senderContext, @Nullable Command command) {
      this.commandCode = commandCode;
      this.sessionHandle = sessionHandle;
      this.status = status;
      this.senderContext = senderContext;
      this.command = command;
   }

   public CommandCode getCommandCode() {
      return this.commandCode;
   }

   public long getSessionHandle() {
      return this.sessionHandle;
   }

   public EnipStatus getStatus() {
      return this.status;
   }

   public long getSenderContext() {
      return this.senderContext;
   }

   @Nullable
   public Command getCommand() {
      return this.command;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         EnipPacket that = (EnipPacket)o;
         return this.senderContext == that.senderContext
            && this.sessionHandle == that.sessionHandle
            && (this.command != null ? this.command.equals(that.command) : that.command == null)
            && this.commandCode == that.commandCode
            && this.status == that.status;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.commandCode.hashCode();
      result = 31 * result + (int)(this.sessionHandle ^ this.sessionHandle >>> 32);
      result = 31 * result + this.status.hashCode();
      result = 31 * result + (int)(this.senderContext ^ this.senderContext >>> 32);
      return 31 * result + (this.command != null ? this.command.hashCode() : 0);
   }

   public static ByteBuf encode(EnipPacket packet, ByteBuf buffer) {
      buffer.writeShort(packet.getCommandCode().getCode());
      int lengthStartIndex = buffer.writerIndex();
      buffer.writeShort(0);
      buffer.writeInt((int)packet.getSessionHandle());
      buffer.writeInt(packet.getStatus().getStatus());
      buffer.writeLong(packet.getSenderContext());
      buffer.writeInt(0);
      int dataStartIndex = buffer.writerIndex();
      if (packet.getCommand() != null) {
         encodeCommand(packet.getCommand(), buffer);
      }

      int bytesWritten = buffer.writerIndex() - dataStartIndex;
      buffer.markWriterIndex();
      buffer.writerIndex(lengthStartIndex);
      buffer.writeShort(bytesWritten);
      buffer.resetWriterIndex();
      return buffer;
   }

   public static EnipPacket decode(ByteBuf buffer) {
      CommandCode commandCode = CommandCode.decode(buffer);
      buffer.skipBytes(2);
      long sessionHandle = buffer.readUnsignedInt();
      EnipStatus status = EnipStatus.decode(buffer);
      long senderContext = buffer.readLong();
      buffer.skipBytes(4);
      Command command = status == EnipStatus.EIP_SUCCESS ? decodeCommand(commandCode, buffer) : null;
      return new EnipPacket(commandCode, sessionHandle, status, senderContext, command);
   }

   private static ByteBuf encodeCommand(Command command, ByteBuf buffer) {
      switch (command.getCommandCode()) {
         case ListIdentity:
            return ListIdentity.encode((ListIdentity)command, buffer);
         case ListInterfaces:
            return ListInterfaces.encode((ListInterfaces)command, buffer);
         case ListServices:
            return ListServices.encode((ListServices)command, buffer);
         case Nop:
            return Nop.encode((Nop)command, buffer);
         case RegisterSession:
            return RegisterSession.encode((RegisterSession)command, buffer);
         case SendRRData:
            return SendRRData.encode((SendRRData)command, buffer);
         case SendUnitData:
            return SendUnitData.encode((SendUnitData)command, buffer);
         case UnRegisterSession:
            return UnRegisterSession.encode((UnRegisterSession)command, buffer);
         default:
            throw new RuntimeException(String.format("unhandled command: %s", command.getCommandCode()));
      }
   }

   private static Command decodeCommand(CommandCode commandCode, ByteBuf buffer) {
      switch (commandCode) {
         case ListIdentity:
            return ListIdentity.decode(buffer);
         case ListInterfaces:
            return ListInterfaces.decode(buffer);
         case ListServices:
            return ListServices.decode(buffer);
         case Nop:
            return Nop.decode(buffer);
         case RegisterSession:
            return RegisterSession.decode(buffer);
         case SendRRData:
            return SendRRData.decode(buffer);
         case SendUnitData:
            return SendUnitData.decode(buffer);
         case UnRegisterSession:
            return UnRegisterSession.decode(buffer);
         default:
            throw new RuntimeException(String.format("unhandled command: %s", commandCode));
      }
   }
}
