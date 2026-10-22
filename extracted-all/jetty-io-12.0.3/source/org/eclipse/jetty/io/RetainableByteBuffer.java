package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import org.eclipse.jetty.io.internal.NonRetainableByteBuffer;
import org.eclipse.jetty.util.BufferUtil;

public interface RetainableByteBuffer extends Retainable {
   RetainableByteBuffer EMPTY = wrap(BufferUtil.EMPTY_BUFFER);

   static RetainableByteBuffer wrap(ByteBuffer byteBuffer) {
      return new NonRetainableByteBuffer(byteBuffer);
   }

   static RetainableByteBuffer wrap(final ByteBuffer byteBuffer, final Retainable retainable) {
      return new RetainableByteBuffer() {
         @Override
         public ByteBuffer getByteBuffer() {
            return byteBuffer;
         }

         @Override
         public boolean isRetained() {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean canRetain() {
            return retainable.canRetain();
         }

         @Override
         public void retain() {
            retainable.retain();
         }

         @Override
         public boolean release() {
            return retainable.release();
         }
      };
   }

   boolean isRetained();

   ByteBuffer getByteBuffer();

   default boolean isDirect() {
      return this.getByteBuffer().isDirect();
   }

   default int remaining() {
      return this.getByteBuffer().remaining();
   }

   default boolean hasRemaining() {
      return this.getByteBuffer().hasRemaining();
   }

   default int capacity() {
      return this.getByteBuffer().capacity();
   }

   default void clear() {
      BufferUtil.clear(this.getByteBuffer());
   }

   public static class Wrapper extends Retainable.Wrapper implements RetainableByteBuffer {
      public Wrapper(RetainableByteBuffer wrapped) {
         super(wrapped);
      }

      public RetainableByteBuffer getWrapped() {
         return (RetainableByteBuffer)super.getWrapped();
      }

      @Override
      public boolean isRetained() {
         return this.getWrapped().isRetained();
      }

      @Override
      public ByteBuffer getByteBuffer() {
         return this.getWrapped().getByteBuffer();
      }

      @Override
      public boolean isDirect() {
         return this.getWrapped().isDirect();
      }

      @Override
      public int remaining() {
         return this.getWrapped().remaining();
      }

      @Override
      public boolean hasRemaining() {
         return this.getWrapped().hasRemaining();
      }

      @Override
      public int capacity() {
         return this.getWrapped().capacity();
      }

      @Override
      public void clear() {
         this.getWrapped().clear();
      }
   }
}
