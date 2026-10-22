package com.digitalpetri.enip.codec;

import com.digitalpetri.enip.EnipPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.nio.ByteOrder;
import java.util.List;

public class EnipCodec extends ByteToMessageCodec<EnipPacket> {
   private static final int HEADER_SIZE = 24;
   private static final int LENGTH_OFFSET = 2;

   protected void encode(ChannelHandlerContext ctx, EnipPacket packet, ByteBuf out) throws Exception {
      EnipPacket.encode(packet, out.order(ByteOrder.LITTLE_ENDIAN));
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      ByteBuf buffer = in.order(ByteOrder.LITTLE_ENDIAN);

      for (int startIndex = buffer.readerIndex();
         buffer.readableBytes() >= 24 && buffer.readableBytes() >= 24 + this.getLength(buffer, startIndex);
         startIndex = buffer.readerIndex()
      ) {
         out.add(EnipPacket.decode(buffer));
      }
   }

   private int getLength(ByteBuf buffer, int startIndex) {
      return buffer.getUnsignedShort(startIndex + 2);
   }
}
