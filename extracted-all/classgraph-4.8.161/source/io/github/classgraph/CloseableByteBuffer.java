package io.github.classgraph;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CloseableByteBuffer implements Closeable {
   private ByteBuffer byteBuffer;
   private Runnable onClose;

   CloseableByteBuffer(ByteBuffer byteBuffer, Runnable onClose) {
      this.byteBuffer = byteBuffer;
      this.onClose = onClose;
   }

   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   @Override
   public void close() throws IOException {
      if (this.onClose != null) {
         try {
            this.onClose.run();
         } catch (Exception var2) {
         }

         this.onClose = null;
      }

      this.byteBuffer = null;
   }
}
