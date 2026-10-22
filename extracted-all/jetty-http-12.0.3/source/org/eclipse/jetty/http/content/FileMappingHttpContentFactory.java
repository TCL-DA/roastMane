package org.eclipse.jetty.http.content;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMappingHttpContentFactory implements HttpContent.Factory {
   private static final Logger LOG = LoggerFactory.getLogger(FileMappingHttpContentFactory.class);
   private static final int DEFAULT_MIN_FILE_SIZE = 16384;
   private final HttpContent.Factory _factory;
   private final int _minFileSize;

   public FileMappingHttpContentFactory(HttpContent.Factory factory) {
      this(factory, 16384);
   }

   public FileMappingHttpContentFactory(HttpContent.Factory factory, int minFileSize) {
      this._factory = Objects.requireNonNull(factory);
      this._minFileSize = minFileSize;
   }

   @Override
   public HttpContent getContent(String path) throws IOException {
      HttpContent content = this._factory.getContent(path);
      if (content != null) {
         long contentLength = content.getContentLengthValue();
         if (contentLength > (long)this._minFileSize && contentLength < 2147483647L) {
            return new FileMappingHttpContentFactory.FileMappedHttpContent(content);
         }
      }

      return content;
   }

   private static class FileMappedHttpContent extends HttpContent.Wrapper {
      private static final ByteBuffer SENTINEL_BUFFER = BufferUtil.allocate(0);
      private final AutoLock _lock = new AutoLock();
      private final HttpContent _content;
      private volatile ByteBuffer _buffer;

      public FileMappedHttpContent(HttpContent content) {
         super(content);
         this._content = content;
      }

      @Override
      public ByteBuffer getByteBuffer() {
         ByteBuffer buffer = this._buffer;
         if (buffer != null) {
            return buffer == SENTINEL_BUFFER ? super.getByteBuffer() : buffer.asReadOnlyBuffer();
         } else {
            AutoLock lock = this._lock.lock();

            ByteBuffer var3;
            try {
               if (this._buffer == null) {
                  this._buffer = this.getMappedByteBuffer();
               }

               var3 = this._buffer == SENTINEL_BUFFER ? super.getByteBuffer() : this._buffer.asReadOnlyBuffer();
            } catch (Throwable var6) {
               if (lock != null) {
                  try {
                     lock.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (lock != null) {
               lock.close();
            }

            return var3;
         }
      }

      @Override
      public long getBytesOccupied() {
         ByteBuffer buffer = this._buffer;
         if (buffer != null) {
            return buffer == SENTINEL_BUFFER ? super.getBytesOccupied() : 0L;
         } else {
            AutoLock lock = this._lock.lock();

            long var3;
            try {
               if (this._buffer == null) {
                  this._buffer = this.getMappedByteBuffer();
               }

               var3 = this._buffer == SENTINEL_BUFFER ? super.getBytesOccupied() : 0L;
            } catch (Throwable var6) {
               if (lock != null) {
                  try {
                     lock.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (lock != null) {
               lock.close();
            }

            return var3;
         }
      }

      private ByteBuffer getMappedByteBuffer() {
         try {
            ByteBuffer byteBuffer = BufferUtil.toMappedBuffer(this._content.getResource().getPath());
            return byteBuffer == null ? SENTINEL_BUFFER : byteBuffer;
         } catch (Throwable var2) {
            if (FileMappingHttpContentFactory.LOG.isDebugEnabled()) {
               FileMappingHttpContentFactory.LOG.debug("Error getting Mapped Buffer", var2);
            }

            return SENTINEL_BUFFER;
         }
      }
   }
}
