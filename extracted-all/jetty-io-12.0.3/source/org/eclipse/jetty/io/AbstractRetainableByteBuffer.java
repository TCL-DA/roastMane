package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import java.util.Objects;
import org.eclipse.jetty.util.BufferUtil;

public abstract class AbstractRetainableByteBuffer implements RetainableByteBuffer {
   private final Retainable.ReferenceCounter refCount = new Retainable.ReferenceCounter(0);
   private final ByteBuffer byteBuffer;

   public AbstractRetainableByteBuffer(ByteBuffer byteBuffer) {
      this.byteBuffer = Objects.requireNonNull(byteBuffer);
   }

   protected void acquire() {
      this.refCount.acquire();
   }

   @Override
   public boolean canRetain() {
      return this.refCount.canRetain();
   }

   @Override
   public void retain() {
      this.refCount.retain();
   }

   @Override
   public boolean release() {
      return this.refCount.release();
   }

   @Override
   public boolean isRetained() {
      return this.refCount.isRetained();
   }

   @Override
   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   @Override
   public String toString() {
      return "%s@%x[rc=%d,%s]".formatted(this.getClass().getSimpleName(), this.hashCode(), this.refCount.get(), BufferUtil.toDetailString(this.byteBuffer));
   }
}
