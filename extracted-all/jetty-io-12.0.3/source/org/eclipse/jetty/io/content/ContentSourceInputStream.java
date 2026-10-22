package org.eclipse.jetty.io.content;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.Blocker.Runnable;
import org.eclipse.jetty.util.Blocker.Shared;

public class ContentSourceInputStream extends InputStream {
   private final Shared blocking = new Shared();
   private final byte[] oneByte = new byte[1];
   private final Content.Source content;
   private Content.Chunk chunk;

   public ContentSourceInputStream(Content.Source content) {
      this.content = content;
   }

   @Override
   public int read() throws IOException {
      int read = this.read(this.oneByte, 0, 1);
      return read < 0 ? -1 : this.oneByte[0] & 0xFF;
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      while (this.chunk == null) {
         this.chunk = this.content.read();
         if (this.chunk == null) {
            Runnable callback = this.blocking.runnable();

            try {
               this.content.demand(callback);
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
         }
      }

      if (Content.Chunk.isFailure(this.chunk)) {
         Content.Chunk c = this.chunk;
         this.chunk = Content.Chunk.next(c);
         throw IO.rethrow(c.getFailure());
      } else {
         ByteBuffer byteBuffer = this.chunk.getByteBuffer();
         if (this.chunk.isLast() && !byteBuffer.hasRemaining()) {
            return -1;
         } else {
            int l = Math.min(byteBuffer.remaining(), len);
            byteBuffer.get(b, off, l);
            if (!byteBuffer.hasRemaining()) {
               this.chunk.release();
               this.chunk = this.chunk.isLast() ? Content.Chunk.EOF : null;
            }

            return l;
         }
      }
   }

   @Override
   public int available() throws IOException {
      ByteBuffer available = this.chunk == null ? null : this.chunk.getByteBuffer();
      return available != null ? available.remaining() : 0;
   }

   @Override
   public void close() throws IOException {
      if (this.chunk != Content.Chunk.EOF && !Content.Chunk.isFailure(this.chunk, true)) {
         boolean contentSkipped = false;
         if (this.chunk != null) {
            contentSkipped = this.chunk.hasRemaining();
            this.chunk.release();
            this.chunk = Content.Chunk.next(this.chunk);
         }

         if (!contentSkipped && this.chunk == null) {
            this.chunk = this.content.read();
            if (this.chunk != null) {
               if (Content.Chunk.isFailure(this.chunk)) {
                  Content.Chunk c = this.chunk;
                  this.chunk = Content.Chunk.next(c);
                  throw IO.rethrow(c.getFailure());
               }

               contentSkipped = this.chunk.hasRemaining();
               this.chunk.release();
               this.chunk = Content.Chunk.next(this.chunk);
            }
         }

         if (contentSkipped || this.chunk == null || !this.chunk.isLast()) {
            Throwable closed = new IOException("closed before EOF");
            this.chunk = Content.Chunk.from(closed);
            this.content.fail(closed);
         }
      }
   }
}
