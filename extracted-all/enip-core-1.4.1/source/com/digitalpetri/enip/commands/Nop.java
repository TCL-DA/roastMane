package com.digitalpetri.enip.commands;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;

public final class Nop extends Command {
   private final byte[] data;

   public Nop() {
      this(new byte[0]);
   }

   public Nop(byte[] data) {
      super(CommandCode.Nop);
      this.data = data;
   }

   public byte[] getData() {
      return this.data;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Nop nop = (Nop)o;
         return Arrays.equals(this.data, nop.data);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public static ByteBuf encode(Nop command, ByteBuf buffer) {
      buffer.writeBytes(command.getData());
      return buffer;
   }

   public static Nop decode(ByteBuf buffer) {
      int size = Math.min(buffer.readableBytes(), 65511);
      byte[] data = new byte[size];
      buffer.readBytes(data);
      return new Nop(data);
   }
}
