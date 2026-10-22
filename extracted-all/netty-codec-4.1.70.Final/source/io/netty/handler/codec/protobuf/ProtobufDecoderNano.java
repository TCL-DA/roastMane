package io.netty.handler.codec.protobuf;

import com.google.protobuf.nano.MessageNano;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

@Sharable
public class ProtobufDecoderNano extends MessageToMessageDecoder<ByteBuf> {
   private final Class<? extends MessageNano> clazz;

   public ProtobufDecoderNano(Class<? extends MessageNano> clazz) {
      this.clazz = (Class<? extends MessageNano>)ObjectUtil.checkNotNull(clazz, "You must provide a Class");
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
      int length = msg.readableBytes();
      byte[] array;
      int offset;
      if (msg.hasArray()) {
         array = msg.array();
         offset = msg.arrayOffset() + msg.readerIndex();
      } else {
         array = ByteBufUtil.getBytes(msg, msg.readerIndex(), length, false);
         offset = 0;
      }

      MessageNano prototype = this.clazz.getConstructor().newInstance();
      out.add(MessageNano.mergeFrom(prototype, array, offset, length));
   }
}
