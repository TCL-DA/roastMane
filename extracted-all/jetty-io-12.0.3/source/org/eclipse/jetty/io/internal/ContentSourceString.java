package org.eclipse.jetty.io.internal;

import java.nio.charset.Charset;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.CharsetStringBuilder;
import org.eclipse.jetty.util.Promise;

public class ContentSourceString {
   private final Content.Source content;
   private final CharsetStringBuilder text;
   private final Promise<String> promise;

   public ContentSourceString(Content.Source content, Charset charset, Promise<String> promise) {
      this.content = content;
      this.text = CharsetStringBuilder.forCharset(charset);
      this.promise = promise;
   }

   public void convert() {
      Content.Chunk chunk;
      do {
         chunk = this.content.read();
         if (chunk == null) {
            this.content.demand(this::convert);
            return;
         }

         if (Content.Chunk.isFailure(chunk)) {
            this.promise.failed(chunk.getFailure());
            return;
         }

         this.text.append(chunk.getByteBuffer());
         chunk.release();
      } while (!chunk.isLast());

      this.succeed();
   }

   private void succeed() {
      try {
         String result = this.text.build();
         this.promise.succeeded(result);
      } catch (Throwable var2) {
         this.promise.failed(var2);
      }
   }
}
