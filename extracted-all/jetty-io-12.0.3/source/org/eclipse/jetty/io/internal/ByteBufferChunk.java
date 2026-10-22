package org.eclipse.jetty.io.internal;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.io.Retainable;
import org.eclipse.jetty.util.BufferUtil;

public abstract class ByteBufferChunk implements Content.Chunk {
   private final ByteBuffer byteBuffer;
   private final boolean last;

   public ByteBufferChunk(ByteBuffer byteBuffer, boolean last) {
      this.byteBuffer = Objects.requireNonNull(byteBuffer);
      this.last = last;
   }

   @Override
   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   @Override
   public boolean isLast() {
      return this.last;
   }

   @Override
   public String toString() {
      return "%s@%x[l=%b,b=%s]".formatted(this.getClass().getSimpleName(), this.hashCode(), this.isLast(), BufferUtil.toDetailString(this.getByteBuffer()));
   }

   public static class ReleasedByConsumer extends ByteBufferChunk.WithReferenceCount {
      private final AtomicReference<Consumer<ByteBuffer>> releaser;

      public ReleasedByConsumer(ByteBuffer byteBuffer, boolean last, Consumer<ByteBuffer> releaser) {
         super(byteBuffer, last);
         this.releaser = new AtomicReference<>(releaser);
      }

      @Override
      public boolean release() {
         boolean released = super.release();
         if (released) {
            Consumer<ByteBuffer> consumer = this.releaser.getAndSet(null);
            if (consumer != null) {
               consumer.accept(this.getByteBuffer());
            }
         }

         return released;
      }
   }

   public static class ReleasedByRunnable extends ByteBufferChunk.WithReferenceCount {
      private final AtomicReference<Runnable> releaser;

      public ReleasedByRunnable(ByteBuffer byteBuffer, boolean last, Runnable releaser) {
         super(byteBuffer, last);
         this.releaser = new AtomicReference<>(releaser);
      }

      @Override
      public boolean release() {
         boolean released = super.release();
         if (released) {
            Runnable runnable = this.releaser.getAndSet(null);
            if (runnable != null) {
               runnable.run();
            }
         }

         return released;
      }
   }

   public static class WithReferenceCount extends ByteBufferChunk {
      private final Retainable.ReferenceCounter references = new Retainable.ReferenceCounter();

      public WithReferenceCount(ByteBuffer byteBuffer, boolean last) {
         super(byteBuffer, last);
      }

      @Override
      public boolean canRetain() {
         return true;
      }

      @Override
      public void retain() {
         this.references.retain();
      }

      @Override
      public boolean release() {
         return this.references.release();
      }

      @Override
      public String toString() {
         return "%s[rc=%d]".formatted(super.toString(), this.references.get());
      }
   }

   public static class WithRetainable extends ByteBufferChunk {
      private final Retainable retainable;

      public WithRetainable(ByteBuffer byteBuffer, boolean last, Retainable retainable) {
         super(byteBuffer, last);
         this.retainable = retainable;
      }

      @Override
      public boolean canRetain() {
         return true;
      }

      @Override
      public void retain() {
         this.retainable.retain();
      }

      @Override
      public boolean release() {
         return this.retainable.release();
      }

      @Override
      public String toString() {
         return "%s[%s]".formatted(super.toString(), this.retainable);
      }
   }
}
