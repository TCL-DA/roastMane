package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.encoder.Encoder;
import com.aayushatharva.brotli4j.encoder.Encoder.Parameters;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;

@Sharable
public final class BrotliEncoder extends MessageToByteEncoder<ByteBuf> {
   private final Parameters parameters;

   public BrotliEncoder() {
      this(BrotliOptions.DEFAULT);
   }

   public BrotliEncoder(Parameters parameters) {
      this.parameters = (Parameters)ObjectUtil.checkNotNull(parameters, "Parameters");
   }

   public BrotliEncoder(BrotliOptions brotliOptions) {
      this(brotliOptions.parameters());
   }

   protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
      if (!msg.isReadable()) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         try {
            byte[] uncompressed = ByteBufUtil.getBytes(msg, msg.readerIndex(), msg.readableBytes(), false);
            byte[] compressed = Encoder.compress(uncompressed, this.parameters);
            if (preferDirect) {
               ByteBuf out = ctx.alloc().ioBuffer(compressed.length);
               out.writeBytes(compressed);
               return out;
            } else {
               return Unpooled.wrappedBuffer(compressed);
            }
         } catch (Exception var7) {
            ReferenceCountUtil.release(msg);
            throw var7;
         }
      }
   }
}
