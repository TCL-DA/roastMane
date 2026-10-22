package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;

public class UnconnectedDataItemRequest extends CpfItem {
   public static final int TYPE_ID = 178;
   private final Consumer<ByteBuf> encoder;

   public UnconnectedDataItemRequest(Consumer<ByteBuf> encoder) {
      super(178);
      this.encoder = encoder;
   }

   public Consumer<ByteBuf> getEncoder() {
      return this.encoder;
   }

   public static ByteBuf encode(UnconnectedDataItemRequest item, ByteBuf buffer) {
      buffer.writeShort(item.getTypeId());
      int lengthStartIndex = buffer.writerIndex();
      buffer.writeShort(0);
      int dataStartIndex = buffer.writerIndex();
      item.getEncoder().accept(buffer);
      int bytesWritten = buffer.writerIndex() - dataStartIndex;
      buffer.markWriterIndex();
      buffer.writerIndex(lengthStartIndex);
      buffer.writeShort(bytesWritten);
      buffer.resetWriterIndex();
      return buffer;
   }
}
