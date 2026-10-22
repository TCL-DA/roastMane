package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteBufferAggregator {
   private static final Logger LOG = LoggerFactory.getLogger(ByteBufferAggregator.class);
   private final ByteBufferPool _bufferPool;
   private final boolean _direct;
   private final int _maxSize;
   private RetainableByteBuffer _retainableByteBuffer;
   private int _aggregatedSize;
   private int _currentSize;

   public ByteBufferAggregator(ByteBufferPool bufferPool, boolean direct, int startSize, int maxSize) {
      if (maxSize <= 0) {
         throw new IllegalArgumentException("maxSize must be > 0, was: " + maxSize);
      } else if (startSize <= 0) {
         throw new IllegalArgumentException("startSize must be > 0, was: " + startSize);
      } else if (startSize > maxSize) {
         throw new IllegalArgumentException("maxSize (" + maxSize + ") must be >= startSize (" + startSize + ")");
      } else {
         this._bufferPool = (ByteBufferPool)(bufferPool == null ? new ByteBufferPool.NonPooling() : bufferPool);
         this._direct = direct;
         this._maxSize = maxSize;
         this._currentSize = startSize;
      }
   }

   public int length() {
      return this._aggregatedSize;
   }

   public boolean aggregate(ByteBuffer buffer) {
      this.tryExpandBufferCapacity(buffer.remaining());
      if (this._retainableByteBuffer == null) {
         this._retainableByteBuffer = this._bufferPool.acquire(this._currentSize, this._direct);
         BufferUtil.flipToFill(this._retainableByteBuffer.getByteBuffer());
      }

      int copySize = Math.min(this._currentSize - this._aggregatedSize, buffer.remaining());
      ByteBuffer byteBuffer = this._retainableByteBuffer.getByteBuffer();
      byteBuffer.put(byteBuffer.position(), buffer, buffer.position(), copySize);
      byteBuffer.position(byteBuffer.position() + copySize);
      buffer.position(buffer.position() + copySize);
      this._aggregatedSize += copySize;
      return this._aggregatedSize == this._maxSize;
   }

   private void tryExpandBufferCapacity(int remaining) {
      if (LOG.isDebugEnabled()) {
         LOG.debug(
            "tryExpandBufferCapacity remaining: {} _currentSize: {} _accumulatedSize={}", new Object[]{remaining, this._currentSize, this._aggregatedSize}
         );
      }

      if (this._currentSize != this._maxSize) {
         int capacityLeft = this._currentSize - this._aggregatedSize;
         if (remaining > capacityLeft) {
            int need = remaining - capacityLeft;
            this._currentSize = Math.min(this._maxSize, ceilToNextPowerOfTwo(this._currentSize + need));
            if (this._retainableByteBuffer != null) {
               BufferUtil.flipToFlush(this._retainableByteBuffer.getByteBuffer(), 0);
               RetainableByteBuffer newBuffer = this._bufferPool.acquire(this._currentSize, this._direct);
               BufferUtil.flipToFill(newBuffer.getByteBuffer());
               newBuffer.getByteBuffer().put(this._retainableByteBuffer.getByteBuffer());
               this._retainableByteBuffer.release();
               this._retainableByteBuffer = newBuffer;
            }
         }
      }
   }

   private static int ceilToNextPowerOfTwo(int val) {
      int result = 1 << 32 - Integer.numberOfLeadingZeros(val - 1);
      return result > 0 ? result : Integer.MAX_VALUE;
   }

   public RetainableByteBuffer takeRetainableByteBuffer() {
      if (this._retainableByteBuffer == null) {
         return null;
      } else {
         BufferUtil.flipToFlush(this._retainableByteBuffer.getByteBuffer(), 0);
         RetainableByteBuffer result = this._retainableByteBuffer;
         this._retainableByteBuffer = null;
         this._aggregatedSize = 0;
         return result;
      }
   }

   @Override
   public String toString() {
      return "%s@%x{a=%d c=%d m=%d b=%s}"
         .formatted(this.getClass().getSimpleName(), this.hashCode(), this._aggregatedSize, this._currentSize, this._maxSize, this._retainableByteBuffer);
   }
}
