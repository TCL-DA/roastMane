package org.eclipse.jetty.io.internal;

import java.nio.ByteBuffer;
import org.eclipse.jetty.io.RetainableByteBuffer;

public class NonRetainableByteBuffer implements RetainableByteBuffer {
   private final ByteBuffer byteBuffer;

   public NonRetainableByteBuffer(ByteBuffer byteBuffer) {
      this.byteBuffer = byteBuffer;
   }

   @Override
   public boolean isRetained() {
      return false;
   }

   @Override
   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }
}
