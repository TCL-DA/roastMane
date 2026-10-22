package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;

public final class NullAddressItem extends CpfItem {
   public static final int TYPE_ID = 0;
   private static final int ITEM_LENGTH = 0;

   public NullAddressItem() {
      super(0);
   }

   @Override
   public boolean equals(Object o) {
      return this == o ? true : o != null && this.getClass() == o.getClass();
   }

   @Override
   public int hashCode() {
      return 0;
   }

   public static ByteBuf encode(NullAddressItem item, ByteBuf buffer) {
      buffer.writeShort(0);
      buffer.writeShort(0);
      return buffer;
   }

   public static NullAddressItem decode(ByteBuf buffer) {
      int typeId = buffer.readUnsignedShort();
      int length = buffer.readUnsignedShort();

      assert typeId == 0;

      assert length == 0;

      return new NullAddressItem();
   }
}
