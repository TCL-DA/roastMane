package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;

public final class SockAddrItemO2t extends CpfItem {
   public static final int TYPE_ID = 32768;
   private final SockAddr sockAddr;
   private static final int ITEM_LENGTH = 16;

   public SockAddrItemO2t(SockAddr sockAddr) {
      super(32768);
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
         SockAddrItemO2t that = (SockAddrItemO2t)o;
         return this.sockAddr.equals(that.sockAddr);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.sockAddr.hashCode();
   }

   public static ByteBuf encode(SockAddrItemO2t item, ByteBuf buffer) {
      buffer.writeShort(32768);
      buffer.writeShort(16);
      return SockAddr.encode(item.getSockAddr(), buffer);
   }

   public static SockAddrItemO2t decode(ByteBuf buffer) {
      int typeId = buffer.readUnsignedShort();
      int length = buffer.readUnsignedShort();

      assert typeId == 32768;

      assert length == 16;

      SockAddr sockAddr = SockAddr.decode(buffer);
      return new SockAddrItemO2t(sockAddr);
   }
}
