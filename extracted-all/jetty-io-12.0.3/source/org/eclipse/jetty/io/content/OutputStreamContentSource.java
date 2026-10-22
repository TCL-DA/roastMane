package org.eclipse.jetty.io.content;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.FutureCallback;
import org.eclipse.jetty.util.IO;

public class OutputStreamContentSource implements Content.Source, Closeable {
   private final AsyncContent async = new AsyncContent();
   private final OutputStreamContentSource.AsyncOutputStream output = new OutputStreamContentSource.AsyncOutputStream();

   public OutputStream getOutputStream() {
      return this.output;
   }

   @Override
   public long getLength() {
      return this.async.getLength();
   }

   @Override
   public Content.Chunk read() {
      return this.async.read();
   }

   @Override
   public void demand(Runnable demandCallback) {
      this.async.demand(demandCallback);
   }

   @Override
   public void fail(Throwable failure) {
      this.async.fail(failure);
   }

   @Override
   public void close() {
      this.output.close();
   }

   private class AsyncOutputStream extends OutputStream {
      @Override
      public void write(int b) throws IOException {
         this.write(new byte[]{(byte)b}, 0, 1);
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException {
         try {
            FutureCallback callback = new FutureCallback();
            OutputStreamContentSource.this.async.write(false, ByteBuffer.wrap(b, off, len), callback);
            callback.get();
         } catch (Throwable var5) {
            throw IO.rethrow(var5);
         }
      }

      @Override
      public void flush() throws IOException {
         OutputStreamContentSource.this.async.flush();
      }

      @Override
      public void close() {
         OutputStreamContentSource.this.async.close();
      }
   }
}
