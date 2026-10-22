package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;

public final class CpfPacket {
   private final CpfItem[] items;

   public CpfPacket(CpfItem... items) {
      this.items = items;
   }

   public CpfItem[] getItems() {
      return this.items;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CpfPacket cpfPacket = (CpfPacket)o;
         return Arrays.equals((Object[])this.items, (Object[])cpfPacket.items);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode((Object[])this.items);
   }

   public static ByteBuf encode(CpfPacket packet, ByteBuf buffer) {
      buffer.writeShort(packet.getItems().length);

      for (CpfItem item : packet.getItems()) {
         CpfItem.encode(item, buffer);
      }

      return buffer;
   }

   public static CpfPacket decode(ByteBuf buffer) {
      int itemCount = buffer.readUnsignedShort();
      CpfItem[] items = new CpfItem[itemCount];

      for (int i = 0; i < itemCount; i++) {
         items[i] = CpfItem.decode(buffer);
      }

      return new CpfPacket(items);
   }
}
