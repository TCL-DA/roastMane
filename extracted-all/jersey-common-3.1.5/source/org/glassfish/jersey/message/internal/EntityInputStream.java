package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.ProcessingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import org.glassfish.jersey.internal.LocalizationMessages;

public class EntityInputStream extends InputStream {
   private InputStream input;
   private boolean closed = false;

   public static EntityInputStream create(InputStream inputStream) {
      return inputStream instanceof EntityInputStream ? (EntityInputStream)inputStream : new EntityInputStream(inputStream);
   }

   public EntityInputStream(InputStream input) {
      this.input = input;
   }

   @Override
   public int read() throws IOException {
      return this.input.read();
   }

   @Override
   public int read(byte[] b) throws IOException {
      return this.input.read(b);
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      return this.input.read(b, off, len);
   }

   @Override
   public long skip(long n) throws IOException {
      return this.input.skip(n);
   }

   @Override
   public int available() throws IOException {
      return this.input.available();
   }

   @Override
   public void mark(int readLimit) {
      this.input.mark(readLimit);
   }

   @Override
   public boolean markSupported() {
      return this.input.markSupported();
   }

   @Override
   public void reset() {
      try {
         this.input.reset();
      } catch (IOException var2) {
         throw new ProcessingException(LocalizationMessages.MESSAGE_CONTENT_BUFFER_RESET_FAILED(), var2);
      }
   }

   @Override
   public void close() throws ProcessingException {
      InputStream in = this.input;
      if (in != null) {
         if (!this.closed) {
            try {
               in.close();
            } catch (IOException var6) {
               throw new ProcessingException(LocalizationMessages.MESSAGE_CONTENT_INPUT_STREAM_CLOSE_FAILED(), var6);
            } finally {
               this.closed = true;
            }
         }
      }
   }

   public boolean isEmpty() {
      this.ensureNotClosed();
      InputStream in = this.input;
      if (in == null) {
         return true;
      } else {
         try {
            if (in.markSupported()) {
               in.mark(1);
               int i = in.read();
               in.reset();
               return i == -1;
            } else {
               try {
                  if (in.available() > 0) {
                     return false;
                  }
               } catch (IOException var4) {
               }

               int b = in.read();
               if (b == -1) {
                  return true;
               } else {
                  PushbackInputStream pbis;
                  if (in instanceof PushbackInputStream) {
                     pbis = (PushbackInputStream)in;
                  } else {
                     pbis = new PushbackInputStream(in, 1);
                     this.input = pbis;
                  }

                  pbis.unread(b);
                  return false;
               }
            }
         } catch (IOException var5) {
            throw new ProcessingException(var5);
         }
      }
   }

   public void ensureNotClosed() throws IllegalStateException {
      if (this.closed) {
         throw new IllegalStateException(LocalizationMessages.ERROR_ENTITY_STREAM_CLOSED());
      }
   }

   public boolean isClosed() {
      return this.closed;
   }

   public final InputStream getWrappedStream() {
      return this.input;
   }

   public final void setWrappedStream(InputStream wrapped) {
      this.input = wrapped;
   }
}
