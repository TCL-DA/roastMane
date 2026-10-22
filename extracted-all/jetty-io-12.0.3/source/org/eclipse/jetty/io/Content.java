package org.eclipse.jetty.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.function.Consumer;
import org.eclipse.jetty.io.content.BufferedContentSink;
import org.eclipse.jetty.io.content.ContentSinkOutputStream;
import org.eclipse.jetty.io.content.ContentSinkSubscriber;
import org.eclipse.jetty.io.content.ContentSourceInputStream;
import org.eclipse.jetty.io.content.ContentSourcePublisher;
import org.eclipse.jetty.io.internal.ByteBufferChunk;
import org.eclipse.jetty.io.internal.ContentCopier;
import org.eclipse.jetty.io.internal.ContentSourceByteBuffer;
import org.eclipse.jetty.io.internal.ContentSourceConsumer;
import org.eclipse.jetty.io.internal.ContentSourceString;
import org.eclipse.jetty.util.Blocker;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FutureCallback;
import org.eclipse.jetty.util.FuturePromise;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.Promise.Completable;

public class Content {
   private Content() {
   }

   public static void copy(Content.Source source, Content.Sink sink, Callback callback) {
      copy(source, sink, null, callback);
   }

   public static void copy(Content.Source source, Content.Sink sink, Content.Chunk.Processor chunkProcessor, Callback callback) {
      new ContentCopier(source, sink, chunkProcessor, callback).iterate();
   }

   public interface Chunk extends Retainable {
      Content.Chunk EMPTY = new Content.Chunk() {
         @Override
         public ByteBuffer getByteBuffer() {
            return BufferUtil.EMPTY_BUFFER;
         }

         @Override
         public boolean isLast() {
            return false;
         }

         @Override
         public String toString() {
            return "EMPTY";
         }
      };
      Content.Chunk EOF = new Content.Chunk() {
         @Override
         public ByteBuffer getByteBuffer() {
            return BufferUtil.EMPTY_BUFFER;
         }

         @Override
         public boolean isLast() {
            return true;
         }

         @Override
         public String toString() {
            return "EOF";
         }
      };

      static Content.Chunk from(ByteBuffer byteBuffer, boolean last) {
         if (byteBuffer.hasRemaining()) {
            return new ByteBufferChunk.WithReferenceCount(byteBuffer, last);
         } else {
            return last ? EOF : EMPTY;
         }
      }

      static Content.Chunk from(ByteBuffer byteBuffer, boolean last, Runnable releaser) {
         if (byteBuffer.hasRemaining()) {
            return new ByteBufferChunk.ReleasedByRunnable(byteBuffer, last, Objects.requireNonNull(releaser));
         } else {
            releaser.run();
            return last ? EOF : EMPTY;
         }
      }

      static Content.Chunk from(ByteBuffer byteBuffer, boolean last, Consumer<ByteBuffer> releaser) {
         if (byteBuffer.hasRemaining()) {
            return new ByteBufferChunk.ReleasedByConsumer(byteBuffer, last, Objects.requireNonNull(releaser));
         } else {
            releaser.accept(byteBuffer);
            return last ? EOF : EMPTY;
         }
      }

      static Content.Chunk asChunk(ByteBuffer byteBuffer, boolean last, Retainable retainable) {
         if (!retainable.canRetain()) {
            throw new IllegalArgumentException("Cannot create chunk from non-retainable " + retainable);
         } else if (byteBuffer.hasRemaining()) {
            return new ByteBufferChunk.WithRetainable(byteBuffer, last, Objects.requireNonNull(retainable));
         } else {
            retainable.release();
            return last ? EOF : EMPTY;
         }
      }

      static Content.Chunk from(Throwable failure) {
         return from(failure, true);
      }

      static Content.Chunk from(final Throwable failure, final boolean last) {
         return new Content.Chunk() {
            @Override
            public Throwable getFailure() {
               return failure;
            }

            @Override
            public ByteBuffer getByteBuffer() {
               return BufferUtil.EMPTY_BUFFER;
            }

            @Override
            public boolean isLast() {
               return last;
            }

            @Override
            public String toString() {
               return String.format("Chunk@%x{c=%s,l=%b}", this.hashCode(), failure, last);
            }
         };
      }

      static Content.Chunk next(Content.Chunk chunk) {
         if (chunk == null) {
            return null;
         } else if (isFailure(chunk)) {
            return chunk.isLast() ? chunk : null;
         } else {
            return chunk.isLast() ? EOF : null;
         }
      }

      static boolean isFailure(Content.Chunk chunk) {
         return chunk != null && chunk.getFailure() != null;
      }

      static boolean isFailure(Content.Chunk chunk, boolean last) {
         return chunk != null && chunk.getFailure() != null && chunk.isLast() == last;
      }

      ByteBuffer getByteBuffer();

      default Throwable getFailure() {
         return null;
      }

      boolean isLast();

      default int remaining() {
         return this.getByteBuffer().remaining();
      }

      default boolean hasRemaining() {
         return this.getByteBuffer().hasRemaining();
      }

      default int get(byte[] bytes, int offset, int length) {
         ByteBuffer b = this.getByteBuffer();
         if (b != null && b.hasRemaining()) {
            length = Math.min(length, b.remaining());
            b.get(bytes, offset, length);
            return length;
         } else {
            return 0;
         }
      }

      default int skip(int length) {
         if (length == 0) {
            return 0;
         } else {
            ByteBuffer byteBuffer = this.getByteBuffer();
            length = Math.min(byteBuffer.remaining(), length);
            byteBuffer.position(byteBuffer.position() + length);
            return length;
         }
      }

      default Content.Chunk asReadOnly() {
         return !this.canRetain() ? this : asChunk(this.getByteBuffer().asReadOnlyBuffer(), this.isLast(), this);
      }

      public interface Processor {
         boolean process(Content.Chunk var1, Callback var2);
      }
   }

   public interface Sink {
      static Content.Sink asBuffered(Content.Sink sink, ByteBufferPool bufferPool, boolean direct, int maxAggregationSize, int maxBufferSize) {
         return new BufferedContentSink(sink, bufferPool, direct, maxAggregationSize, maxBufferSize);
      }

      static OutputStream asOutputStream(Content.Sink sink) {
         return new ContentSinkOutputStream(sink);
      }

      static Subscriber<Content.Chunk> asSubscriber(Content.Sink sink, Callback callback) {
         return new ContentSinkSubscriber(sink, callback);
      }

      static void write(Content.Sink sink, boolean last, ByteBuffer byteBuffer) throws IOException {
         org.eclipse.jetty.util.Blocker.Callback callback = Blocker.callback();

         try {
            sink.write(last, byteBuffer, callback);
            callback.block();
         } catch (Throwable var7) {
            if (callback != null) {
               try {
                  callback.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (callback != null) {
            callback.close();
         }
      }

      static void write(Content.Sink sink, boolean last, String utf8Content, Callback callback) {
         sink.write(last, StandardCharsets.UTF_8.encode(utf8Content), callback);
      }

      void write(boolean var1, ByteBuffer var2, Callback var3);
   }

   public interface Source {
      static void asByteBuffer(Content.Source source, Promise<ByteBuffer> promise) {
         new ContentSourceByteBuffer(source, promise).run();
      }

      static ByteBuffer asByteBuffer(Content.Source source) throws IOException {
         try {
            FuturePromise<ByteBuffer> promise = new FuturePromise();
            asByteBuffer(source, promise);
            return (ByteBuffer)promise.get();
         } catch (Throwable var2) {
            throw IO.rethrow(var2);
         }
      }

      static CompletableFuture<byte[]> asByteArrayAsync(Content.Source source, int maxSize) {
         return new ChunkAccumulator().readAll(source, maxSize);
      }

      static CompletableFuture<ByteBuffer> asByteBufferAsync(Content.Source source) {
         return asByteBufferAsync(source, -1);
      }

      static CompletableFuture<ByteBuffer> asByteBufferAsync(Content.Source source, int maxSize) {
         return asByteArrayAsync(source, maxSize).thenApply(ByteBuffer::wrap);
      }

      static CompletableFuture<RetainableByteBuffer> asRetainableByteBuffer(Content.Source source, ByteBufferPool pool, boolean direct, int maxSize) {
         return new ChunkAccumulator().readAll(source, pool, direct, maxSize);
      }

      static void asString(Content.Source source, Charset charset, Promise<String> promise) {
         new ContentSourceString(source, charset, promise).convert();
      }

      static String asString(Content.Source source) throws IOException {
         return asString(source, StandardCharsets.UTF_8);
      }

      static String asString(Content.Source source, Charset charset) throws IOException {
         try {
            return asStringAsync(source, charset).get();
         } catch (Throwable var3) {
            throw IO.rethrow(var3);
         }
      }

      static CompletableFuture<String> asStringAsync(Content.Source source, Charset charset) {
         Completable<String> completable = new Completable();
         asString(source, charset, completable);
         return completable;
      }

      static InputStream asInputStream(Content.Source source) {
         return new ContentSourceInputStream(source);
      }

      static Publisher<Content.Chunk> asPublisher(Content.Source source) {
         return new ContentSourcePublisher(source);
      }

      static void consumeAll(Content.Source source, Callback callback) {
         new ContentSourceConsumer(source, callback).run();
      }

      static void consumeAll(Content.Source source) throws IOException {
         try {
            FutureCallback callback = new FutureCallback();
            consumeAll(source, callback);
            callback.get();
         } catch (Throwable var2) {
            throw IO.rethrow(var2);
         }
      }

      default long getLength() {
         return -1L;
      }

      Content.Chunk read();

      void demand(Runnable var1);

      void fail(Throwable var1);

      default void fail(Throwable failure, boolean last) {
         this.fail(failure);
      }

      default boolean rewind() {
         return false;
      }
   }
}
