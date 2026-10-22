package org.eclipse.jetty.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {
   final ByteBuffer buf;

   public ByteBufferInputStream(ByteBuffer buf) {
      this.buf = buf;
   }

   @Override
   public int available() throws IOException {
      return this.buf.remaining();
   }

   @Override
   public int read() throws IOException {
      return !this.buf.hasRemaining() ? -1 : this.buf.get() & 0xFF;
   }

   @Override
   public int read(byte[] bytes, int off, int len) throws IOException {
      if (!this.buf.hasRemaining()) {
         return -1;
      } else {
         len = Math.min(len, this.buf.remaining());
         this.buf.get(bytes, off, len);
         return len;
      }
   }
}
