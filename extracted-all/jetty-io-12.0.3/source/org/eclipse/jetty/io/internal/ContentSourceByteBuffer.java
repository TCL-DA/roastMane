package org.eclipse.jetty.io.internal;

import java.nio.ByteBuffer;
import org.eclipse.jetty.io.ByteBufferAccumulator;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.Promise;

public class ContentSourceByteBuffer implements Runnable {
   private final ByteBufferAccumulator accumulator = new ByteBufferAccumulator();
   private final Content.Source source;
   private final Promise<ByteBuffer> promise;

   public ContentSourceByteBuffer(Content.Source source, Promise<ByteBuffer> promise) {
      this.source = source;
      this.promise = promise;
   }

   @Override
   public void run() {
      Content.Chunk chunk;
      do {
         chunk = this.source.read();
         if (chunk == null) {
            this.source.demand(this);
            return;
         }

         if (Content.Chunk.isFailure(chunk)) {
            this.promise.failed(chunk.getFailure());
            return;
         }

         this.accumulator.copyBuffer(chunk.getByteBuffer());
         chunk.release();
      } while (!chunk.isLast());

      this.promise.succeeded(this.accumulator.takeByteBuffer());
   }
}
