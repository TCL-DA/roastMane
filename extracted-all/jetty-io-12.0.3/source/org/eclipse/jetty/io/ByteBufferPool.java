package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.util.BufferUtil;

public interface ByteBufferPool {
   RetainableByteBuffer acquire(int var1, boolean var2);

   void clear();

   public static class Accumulator {
      private final List<RetainableByteBuffer> buffers = new ArrayList<>();
      private final List<ByteBuffer> byteBuffers = new ArrayList<>();

      public void append(RetainableByteBuffer buffer) {
         this.buffers.add(buffer);
         this.byteBuffers.add(buffer.getByteBuffer());
      }

      public void insert(int index, RetainableByteBuffer buffer) {
         this.buffers.add(index, buffer);
         this.byteBuffers.add(index, buffer.getByteBuffer());
      }

      public List<ByteBuffer> getByteBuffers() {
         return this.byteBuffers;
      }

      public long getTotalLength() {
         long length = 0L;

         for (ByteBuffer buffer : this.byteBuffers) {
            length += (long)buffer.remaining();
         }

         return length;
      }

      public int getSize() {
         return this.byteBuffers.size();
      }

      public void release() {
         this.buffers.forEach(Retainable::release);
         this.buffers.clear();
         this.byteBuffers.clear();
      }
   }

   public static class NonPooling implements ByteBufferPool {
      @Override
      public RetainableByteBuffer acquire(int size, boolean direct) {
         return new ByteBufferPool.NonPooling.Buffer(BufferUtil.allocate(size, direct));
      }

      @Override
      public void clear() {
      }

      private static class Buffer extends AbstractRetainableByteBuffer {
         private Buffer(ByteBuffer byteBuffer) {
            super(byteBuffer);
            this.acquire();
         }
      }
   }

   public static class Wrapper implements ByteBufferPool {
      private final ByteBufferPool wrapped;

      public Wrapper(ByteBufferPool wrapped) {
         this.wrapped = wrapped;
      }

      public ByteBufferPool getWrapped() {
         return this.wrapped;
      }

      @Override
      public RetainableByteBuffer acquire(int size, boolean direct) {
         return this.getWrapped().acquire(size, direct);
      }

      @Override
      public void clear() {
         this.getWrapped().clear();
      }
   }
}
