package org.glassfish.jersey.message.internal;

import java.io.IOException;
import java.io.OutputStream;
import org.glassfish.jersey.internal.LocalizationMessages;

public class NullOutputStream extends OutputStream {
   private boolean isClosed;

   @Override
   public void write(int b) throws IOException {
      this.checkClosed();
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException {
      this.checkClosed();
      if (b == null) {
         throw new NullPointerException();
      } else if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public void flush() throws IOException {
      this.checkClosed();
   }

   private void checkClosed() throws IOException {
      if (this.isClosed) {
         throw new IOException(LocalizationMessages.OUTPUT_STREAM_CLOSED());
      }
   }

   @Override
   public void close() throws IOException {
      this.isClosed = true;
   }
}
