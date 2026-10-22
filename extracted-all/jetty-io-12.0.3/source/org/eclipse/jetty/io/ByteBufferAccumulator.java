package org.eclipse.jetty.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.util.BufferUtil;

public class ByteBufferAccumulator implements AutoCloseable {
   private final List<RetainableByteBuffer> _buffers = new ArrayList<>();
   private final ByteBufferPool _bufferPool;
   private final boolean _direct;

   public ByteBufferAccumulator() {
      this(null, false);
   }

   public ByteBufferAccumulator(ByteBufferPool bufferPool, boolean direct) {
      this._bufferPool = (ByteBufferPool)(bufferPool == null ? new ByteBufferPool.NonPooling() : bufferPool);
      this._direct = direct;
   }

   public int getLength() {
      int length = 0;

      for (RetainableByteBuffer buffer : this._buffers) {
         length = Math.addExact(length, buffer.remaining());
      }

      return length;
   }

   public RetainableByteBuffer ensureBuffer(int minAllocationSize) {
      return this.ensureBuffer(1, minAllocationSize);
   }

   public RetainableByteBuffer ensureBuffer(int minSize, int minAllocationSize) {
      RetainableByteBuffer buffer = this._buffers.isEmpty() ? null : this._buffers.get(this._buffers.size() - 1);
      if (buffer == null || BufferUtil.space(buffer.getByteBuffer()) < minSize) {
         buffer = this._bufferPool.acquire(minAllocationSize, this._direct);
         this._buffers.add(buffer);
      }

      return buffer;
   }

   public void copyBytes(byte[] buf, int offset, int length) {
      this.copyBuffer(BufferUtil.toBuffer(buf, offset, length));
   }

   public void copyBuffer(ByteBuffer source) {
      while (source.hasRemaining()) {
         RetainableByteBuffer buffer = this.ensureBuffer(source.remaining());
         ByteBuffer byteBuffer = buffer.getByteBuffer();
         int pos = BufferUtil.flipToFill(byteBuffer);
         BufferUtil.put(source, byteBuffer);
         BufferUtil.flipToFlush(byteBuffer, pos);
      }
   }

   public RetainableByteBuffer takeRetainableByteBuffer() {
      if (this._buffers.size() == 1) {
         RetainableByteBuffer combinedBuffer = this._buffers.get(0);
         this._buffers.clear();
         return combinedBuffer;
      } else {
         int length = this.getLength();
         RetainableByteBuffer combinedBuffer = this._bufferPool.acquire(length, this._direct);
         ByteBuffer byteBuffer = combinedBuffer.getByteBuffer();
         BufferUtil.clearToFill(byteBuffer);

         for (RetainableByteBuffer buffer : this._buffers) {
            byteBuffer.put(buffer.getByteBuffer());
            buffer.release();
         }

         BufferUtil.flipToFlush(byteBuffer, 0);
         this._buffers.clear();
         return combinedBuffer;
      }
   }

   public ByteBuffer takeByteBuffer() {
      byte[] bytes = this.toByteArray();
      this.close();
      return ByteBuffer.wrap(bytes);
   }

   public RetainableByteBuffer toRetainableByteBuffer() {
      RetainableByteBuffer combinedBuffer = this.takeRetainableByteBuffer();
      this._buffers.add(combinedBuffer);
      return combinedBuffer;
   }

   public byte[] toByteArray() {
      int length = this.getLength();
      if (length == 0) {
         return new byte[0];
      } else {
         byte[] bytes = new byte[length];
         ByteBuffer buffer = BufferUtil.toBuffer(bytes);
         BufferUtil.clear(buffer);
         this.writeTo(buffer);
         return bytes;
      }
   }

   public void writeTo(ByteBuffer byteBuffer) {
      int pos = BufferUtil.flipToFill(byteBuffer);

      for (RetainableByteBuffer buffer : this._buffers) {
         byteBuffer.put(buffer.getByteBuffer().slice());
      }

      BufferUtil.flipToFlush(byteBuffer, pos);
   }

   public void writeTo(OutputStream out) throws IOException {
      for (RetainableByteBuffer buffer : this._buffers) {
         BufferUtil.writeTo(buffer.getByteBuffer().slice(), out);
      }
   }

   @Override
   public void close() {
      this._buffers.forEach(Retainable::release);
      this._buffers.clear();
   }
}
