package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;

public final class ConnectedAddressItem extends CpfItem {
   public static final int TYPE_ID = 161;
   private final int connectionId;
   private static final int ITEM_LENGTH = 4;

   public ConnectedAddressItem(int connectionId) {
      super(161);
      this.connectionId = connectionId;
   }

   public int getConnectionId() {
      return this.connectionId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ConnectedAddressItem that = (ConnectedAddressItem)o;
         return this.connectionId == that.connectionId;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.connectionId;
   }

   public static ByteBuf encode(ConnectedAddressItem item, ByteBuf buffer) {
      buffer.writeShort(item.getTypeId());
      buffer.writeShort(4);
      buffer.writeInt(item.getConnectionId());
      return buffer;
   }

   public static ConnectedAddressItem decode(ByteBuf buffer) {
      int typeId = buffer.readUnsignedShort();
      int length = buffer.readUnsignedShort();
      int connectionId = buffer.readInt();

      assert typeId == 161;

      assert length == 4;

      return new ConnectedAddressItem(connectionId);
   }
}
