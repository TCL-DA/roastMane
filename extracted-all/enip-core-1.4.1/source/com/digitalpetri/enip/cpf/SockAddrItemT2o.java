package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;

public final class SockAddrItemT2o extends CpfItem {
   public static final int TYPE_ID = 32769;
   private final SockAddr sockAddr;
   private static final int ITEM_LENGTH = 16;

   public SockAddrItemT2o(SockAddr sockAddr) {
      super(32769);
      this.sockAddr = sockAddr;
   }

   public SockAddr getSockAddr() {
      return this.sockAddr;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SockAddrItemT2o that = (SockAddrItemT2o)o;
         return this.sockAddr.equals(that.sockAddr);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.sockAddr.hashCode();
   }

   public static ByteBuf encode(SockAddrItemT2o item, ByteBuf buffer) {
      buffer.writeShort(32769);
      buffer.writeShort(16);
      return SockAddr.encode(item.getSockAddr(), buffer);
   }

   public static SockAddrItemT2o decode(ByteBuf buffer) {
      int typeId = buffer.readUnsignedShort();
      int length = buffer.readUnsignedShort();

      assert typeId == 32769;

      assert length == 16;

      SockAddr sockAddr = SockAddr.decode(buffer);
      return new SockAddrItemT2o(sockAddr);
   }
}
