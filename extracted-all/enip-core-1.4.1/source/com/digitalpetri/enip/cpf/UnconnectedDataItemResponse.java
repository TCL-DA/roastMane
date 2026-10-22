package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public final class UnconnectedDataItemResponse extends CpfItem {
   public static final int TYPE_ID = 178;
   private final ByteBuf data;

   public UnconnectedDataItemResponse(ByteBuf data) {
      super(178);
      this.data = data;
   }

   public ByteBuf getData() {
      return this.data;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         UnconnectedDataItemResponse that = (UnconnectedDataItemResponse)o;
         return ByteBufUtil.equals(this.data, that.data);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return ByteBufUtil.hashCode(this.data);
   }

   public static ByteBuf encode(UnconnectedDataItemResponse item, ByteBuf buffer) {
      buffer.writeShort(item.getTypeId());
      int lengthStartIndex = buffer.writerIndex();
      buffer.writeShort(0);
      int dataStartIndex = buffer.writerIndex();
      buffer.writeBytes(item.getData());
      item.getData().release();
      int bytesWritten = buffer.writerIndex() - dataStartIndex;
      buffer.markWriterIndex();
      buffer.writerIndex(lengthStartIndex);
      buffer.writeShort(bytesWritten);
      buffer.resetWriterIndex();
      return buffer;
   }

   public static UnconnectedDataItemResponse decode(ByteBuf buffer) {
      int typeId = buffer.readUnsignedShort();
      int length = buffer.readUnsignedShort();

      assert typeId == 178;

      ByteBuf data = buffer.readSlice(length).retain();
      return new UnconnectedDataItemResponse(data);
   }
}
