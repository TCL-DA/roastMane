package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageDecoder<I> extends ChannelInboundHandlerAdapter {
   private final TypeParameterMatcher matcher;

   protected MessageToMessageDecoder() {
      this.matcher = TypeParameterMatcher.find(this, MessageToMessageDecoder.class, "I");
   }

   protected MessageToMessageDecoder(Class<? extends I> inboundMessageType) {
      this.matcher = TypeParameterMatcher.get(inboundMessageType);
   }

   public boolean acceptInboundMessage(Object msg) throws Exception {
      return this.matcher.match(msg);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      CodecOutputList out = CodecOutputList.newInstance();
      boolean var29 = false /* VF: Semaphore variable */;

      try {
         var29 = true;
         if (this.acceptInboundMessage(msg)) {
            I cast = (I)msg;

            try {
               this.decode(ctx, cast, out);
            } finally {
               ReferenceCountUtil.release(msg);
            }

            var29 = false;
         } else {
            out.add(msg);
            var29 = false;
         }
      } catch (DecoderException var37) {
         throw var37;
      } catch (Exception var38) {
         throw new DecoderException(var38);
      } finally {
         if (var29) {
            try {
               int size = out.size();

               for (int i = 0; i < size; i++) {
                  ctx.fireChannelRead(out.getUnsafe(i));
               }
            } finally {
               out.recycle();
            }
         }
      }

      try {
         int size = out.size();

         for (int i = 0; i < size; i++) {
            ctx.fireChannelRead(out.getUnsafe(i));
         }
      } finally {
         out.recycle();
      }
   }

   protected abstract void decode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}
