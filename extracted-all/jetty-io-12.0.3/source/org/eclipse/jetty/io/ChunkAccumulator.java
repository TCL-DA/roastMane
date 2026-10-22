package org.eclipse.jetty.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.CompletableTask;

public class ChunkAccumulator {
   private static final ByteBufferPool NON_POOLING = new ByteBufferPool.NonPooling();
   private final List<Content.Chunk> _chunks = new ArrayList<>();
   private int _length;

   public boolean add(Content.Chunk chunk) {
      if (chunk.hasRemaining()) {
         this._length = Math.addExact(this._length, chunk.remaining());
         if (chunk.canRetain()) {
            chunk.retain();
            return this._chunks.add(chunk);
         } else {
            return this._chunks.add(Content.Chunk.from(BufferUtil.copy(chunk.getByteBuffer()), chunk.isLast(), () -> {
            }));
         }
      } else if (Content.Chunk.isFailure(chunk)) {
         throw new IllegalArgumentException("chunk is failure");
      } else {
         return false;
      }
   }

   public int length() {
      return this._length;
   }

   public byte[] take() {
      if (this._length == 0) {
         return BufferUtil.EMPTY_BUFFER.array();
      } else {
         byte[] bytes = new byte[this._length];
         int offset = 0;

         for (Content.Chunk chunk : this._chunks) {
            offset += chunk.get(bytes, offset, chunk.remaining());
            chunk.release();
         }

         assert offset == this._length;

         this._chunks.clear();
         this._length = 0;
         return bytes;
      }
   }

   public RetainableByteBuffer take(ByteBufferPool pool, boolean direct) {
      if (this._length == 0) {
         return RetainableByteBuffer.EMPTY;
      } else {
         if (this._chunks.size() == 1) {
            Content.Chunk chunk = this._chunks.get(0);
            ByteBuffer byteBuffer = chunk.getByteBuffer();
            if (direct == byteBuffer.isDirect()) {
               this._chunks.clear();
               this._length = 0;
               return RetainableByteBuffer.wrap(byteBuffer, chunk);
            }
         }

         RetainableByteBuffer buffer = Objects.requireNonNullElse(pool, NON_POOLING).acquire(this._length, direct);
         int offset = 0;

         for (Content.Chunk chunk : this._chunks) {
            offset += chunk.remaining();
            BufferUtil.append(buffer.getByteBuffer(), chunk.getByteBuffer());
            chunk.release();
         }

         assert offset == this._length;

         this._chunks.clear();
         this._length = 0;
         return buffer;
      }
   }

   public void close() {
      this._chunks.forEach(Retainable::release);
      this._chunks.clear();
      this._length = 0;
   }

   public CompletableFuture<byte[]> readAll(Content.Source source) {
      return this.readAll(source, -1);
   }

   public CompletableFuture<byte[]> readAll(Content.Source source, int maxSize) {
      CompletableTask<byte[]> task = new ChunkAccumulator.AccumulatorTask<byte[]>(source, maxSize) {
         protected byte[] take(ChunkAccumulator accumulator) {
            return accumulator.take();
         }
      };
      return task.start();
   }

   public CompletableFuture<RetainableByteBuffer> readAll(Content.Source source, final ByteBufferPool pool, final boolean direct, int maxSize) {
      CompletableTask<RetainableByteBuffer> task = new ChunkAccumulator.AccumulatorTask<RetainableByteBuffer>(source, maxSize) {
         protected RetainableByteBuffer take(ChunkAccumulator accumulator) {
            return accumulator.take(pool, direct);
         }
      };
      return task.start();
   }

   private abstract static class AccumulatorTask<T> extends CompletableTask<T> {
      private final Content.Source _source;
      private final ChunkAccumulator _accumulator = new ChunkAccumulator();
      private final int _maxLength;

      private AccumulatorTask(Content.Source source, int maxLength) {
         this._source = source;
         this._maxLength = maxLength;
      }

      public void run() {
         while (true) {
            Content.Chunk chunk = this._source.read();
            if (chunk == null) {
               this._source.demand(this);
            } else if (Content.Chunk.isFailure(chunk)) {
               this.completeExceptionally(chunk.getFailure());
            } else {
               try {
                  this._accumulator.add(chunk);
                  if (this._maxLength > 0 && this._accumulator._length > this._maxLength) {
                     throw new IOException("accumulation too large");
                  }
               } catch (Throwable var3) {
                  chunk.release();
                  this._accumulator.close();
                  this._source.fail(var3);
                  this.completeExceptionally(var3);
                  return;
               }

               chunk.release();
               if (!chunk.isLast()) {
                  continue;
               }

               this.complete(this.take(this._accumulator));
            }

            return;
         }
      }

      protected abstract T take(ChunkAccumulator var1);
   }
}
