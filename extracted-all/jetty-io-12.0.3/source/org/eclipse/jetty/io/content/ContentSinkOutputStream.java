package org.eclipse.jetty.io.content;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.Blocker.Callback;
import org.eclipse.jetty.util.Blocker.Shared;

public class ContentSinkOutputStream extends OutputStream {
   private final Shared _blocking = new Shared();
   private final Content.Sink sink;

   public ContentSinkOutputStream(Content.Sink sink) {
      this.sink = sink;
   }

   @Override
   public void write(int b) throws IOException {
      this.write(new byte[]{(byte)b}, 0, 1);
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException {
      try {
         Callback callback = this._blocking.callback();

         try {
            this.sink.write(false, ByteBuffer.wrap(b, off, len), callback);
            callback.block();
         } catch (Throwable var8) {
            if (callback != null) {
               try {
                  callback.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (callback != null) {
            callback.close();
         }
      } catch (Throwable var9) {
         throw IO.rethrow(var9);
      }
   }

   @Override
   public void flush() throws IOException {
      try {
         Callback callback = this._blocking.callback();

         try {
            this.sink.write(false, BufferedContentSink.FLUSH_BUFFER, callback);
            callback.block();
         } catch (Throwable var5) {
            if (callback != null) {
               try {
                  callback.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (callback != null) {
            callback.close();
         }
      } catch (Throwable var6) {
         throw IO.rethrow(var6);
      }
   }

   @Override
   public void close() throws IOException {
      try {
         Callback callback = this._blocking.callback();

         try {
            this.close(callback);
            callback.block();
         } catch (Throwable var5) {
            if (callback != null) {
               try {
                  callback.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (callback != null) {
            callback.close();
         }
      } catch (Throwable var6) {
         throw IO.rethrow(var6);
      }
   }

   public void close(org.eclipse.jetty.util.Callback callback) throws IOException {
      this.sink.write(true, null, callback);
   }
}
