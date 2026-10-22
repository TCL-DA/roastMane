package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.decoder.DecoderJNI.Wrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;

public final class BrotliDecoder extends ByteToMessageDecoder {
   private final int inputBufferSize;
   private Wrapper decoder;
   private boolean destroyed;

   public BrotliDecoder() {
      this(8192);
   }

   public BrotliDecoder(int inputBufferSize) {
      this.inputBufferSize = ObjectUtil.checkPositive(inputBufferSize, "inputBufferSize");
   }

   private ByteBuf pull(ByteBufAllocator alloc) {
      ByteBuffer nativeBuffer = this.decoder.pull();
      ByteBuf copy = alloc.buffer(nativeBuffer.remaining());
      copy.writeBytes(nativeBuffer);
      return copy;
   }

   private BrotliDecoder.State decompress(ByteBuf input, List<Object> output, ByteBufAllocator alloc) {
      while (true) {
         switch (this.decoder.getStatus()) {
            case DONE:
               return BrotliDecoder.State.DONE;
            case OK:
               this.decoder.push(0);
               break;
            case NEEDS_MORE_INPUT:
               if (this.decoder.hasOutput()) {
                  output.add(this.pull(alloc));
               }

               if (!input.isReadable()) {
                  return BrotliDecoder.State.NEEDS_MORE_INPUT;
               }

               ByteBuffer decoderInputBuffer = this.decoder.getInputBuffer();
               ((Buffer)decoderInputBuffer).clear();
               int readBytes = readBytes(input, decoderInputBuffer);
               this.decoder.push(readBytes);
               break;
            case NEEDS_MORE_OUTPUT:
               output.add(this.pull(alloc));
               break;
            default:
               return BrotliDecoder.State.ERROR;
         }
      }
   }

   private static int readBytes(ByteBuf in, ByteBuffer dest) {
      int limit = Math.min(in.readableBytes(), dest.remaining());
      ByteBuffer slice = dest.slice();
      ((Buffer)slice).limit(limit);
      in.readBytes(slice);
      ((Buffer)dest).position(dest.position() + limit);
      return limit;
   }

   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.decoder = new Wrapper(this.inputBufferSize);
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      if (this.destroyed) {
         in.skipBytes(in.readableBytes());
      } else if (in.isReadable()) {
         try {
            BrotliDecoder.State state = this.decompress(in, out, ctx.alloc());
            if (state == BrotliDecoder.State.DONE) {
               this.destroy();
            } else if (state == BrotliDecoder.State.ERROR) {
               throw new DecompressionException("Brotli stream corrupted");
            }
         } catch (Exception var5) {
            this.destroy();
            throw var5;
         }
      }
   }

   private void destroy() {
      if (!this.destroyed) {
         this.destroyed = true;
         this.decoder.destroy();
      }
   }

   @Override
   protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
      try {
         this.destroy();
      } finally {
         super.handlerRemoved0(ctx);
      }
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      try {
         this.destroy();
      } finally {
         super.channelInactive(ctx);
      }
   }

   static {
      try {
         Brotli.ensureAvailability();
      } catch (Throwable var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }

   private static enum State {
      DONE,
      NEEDS_MORE_INPUT,
      ERROR;
   }
}
