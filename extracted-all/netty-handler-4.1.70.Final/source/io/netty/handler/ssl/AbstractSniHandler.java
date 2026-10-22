package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import java.util.Locale;

public abstract class AbstractSniHandler<T> extends SslClientHelloHandler<T> {
   private String hostname;

   private static String extractSniHostname(ByteBuf in) {
      int offset = in.readerIndex();
      int endOffset = in.writerIndex();
      offset += 34;
      if (endOffset - offset >= 6) {
         int sessionIdLength = in.getUnsignedByte(offset);
         offset += sessionIdLength + 1;
         int cipherSuitesLength = in.getUnsignedShort(offset);
         offset += cipherSuitesLength + 2;
         int compressionMethodLength = in.getUnsignedByte(offset);
         offset += compressionMethodLength + 1;
         int extensionsLength = in.getUnsignedShort(offset);
         offset += 2;
         int extensionsLimit = offset + extensionsLength;
         if (extensionsLimit <= endOffset) {
            while (extensionsLimit - offset >= 4) {
               int extensionType = in.getUnsignedShort(offset);
               offset += 2;
               int extensionLength = in.getUnsignedShort(offset);
               offset += 2;
               if (extensionsLimit - offset < extensionLength) {
                  break;
               }

               if (extensionType == 0) {
                  offset += 2;
                  if (extensionsLimit - offset >= 3) {
                     int serverNameType = in.getUnsignedByte(offset);
                     offset++;
                     if (serverNameType == 0) {
                        int serverNameLength = in.getUnsignedShort(offset);
                        offset += 2;
                        if (extensionsLimit - offset >= serverNameLength) {
                           String hostname = in.toString(offset, serverNameLength, CharsetUtil.US_ASCII);
                           return hostname.toLowerCase(Locale.US);
                        }
                     }
                  }
                  break;
               }

               offset += extensionLength;
            }
         }
      }

      return null;
   }

   @Override
   protected Future<T> lookup(ChannelHandlerContext ctx, ByteBuf clientHello) throws Exception {
      this.hostname = clientHello == null ? null : extractSniHostname(clientHello);
      return this.lookup(ctx, this.hostname);
   }

   @Override
   protected void onLookupComplete(ChannelHandlerContext ctx, Future<T> future) throws Exception {
      try {
         this.onLookupComplete(ctx, this.hostname, future);
      } finally {
         fireSniCompletionEvent(ctx, this.hostname, future);
      }
   }

   protected abstract Future<T> lookup(ChannelHandlerContext var1, String var2) throws Exception;

   protected abstract void onLookupComplete(ChannelHandlerContext var1, String var2, Future<T> var3) throws Exception;

   private static void fireSniCompletionEvent(ChannelHandlerContext ctx, String hostname, Future<?> future) {
      Throwable cause = future.cause();
      if (cause == null) {
         ctx.fireUserEventTriggered(new SniCompletionEvent(hostname));
      } else {
         ctx.fireUserEventTriggered(new SniCompletionEvent(hostname, cause));
      }
   }
}
