package org.eclipse.jetty.client;

import java.util.ListIterator;
import org.eclipse.jetty.client.transport.HttpResponse;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBuffer;

public class GZIPContentDecoder extends org.eclipse.jetty.http.GZIPContentDecoder implements ContentDecoder {
   public static final int DEFAULT_BUFFER_SIZE = 8192;
   private long decodedLength;

   public GZIPContentDecoder() {
      this(8192);
   }

   public GZIPContentDecoder(int bufferSize) {
      this(null, bufferSize);
   }

   public GZIPContentDecoder(ByteBufferPool byteBufferPool, int bufferSize) {
      super(byteBufferPool, bufferSize);
   }

   @Override
   public void beforeDecoding(Response response) {
      HttpResponse httpResponse = (HttpResponse)response;
      httpResponse.headers(headers -> {
         boolean seenContentEncoding = false;
         ListIterator<HttpField> iterator = headers.listIterator(headers.size());

         while (iterator.hasPrevious()) {
            HttpField field = iterator.previous();
            HttpHeader header = field.getHeader();
            if (header == HttpHeader.CONTENT_LENGTH) {
               iterator.remove();
            } else if (header == HttpHeader.CONTENT_ENCODING && !seenContentEncoding) {
               seenContentEncoding = true;
               String value = field.getValue();
               int comma = value.lastIndexOf(",");
               if (comma < 0) {
                  iterator.remove();
               } else {
                  iterator.set(new HttpField(HttpHeader.CONTENT_ENCODING, value.substring(0, comma)));
               }
            }
         }
      });
   }

   protected boolean decodedChunk(RetainableByteBuffer chunk) {
      this.decodedLength = this.decodedLength + (long)chunk.remaining();
      super.decodedChunk(chunk);
      return true;
   }

   @Override
   public void afterDecoding(Response response) {
      HttpResponse httpResponse = (HttpResponse)response;
      httpResponse.headers(headers -> {
         headers.remove(HttpHeader.TRANSFER_ENCODING);
         headers.put(HttpHeader.CONTENT_LENGTH, this.decodedLength);
      });
   }

   public static class Factory extends ContentDecoder.Factory {
      private final ByteBufferPool byteBufferPool;
      private final int bufferSize;

      public Factory() {
         this(8192);
      }

      public Factory(int bufferSize) {
         this(null, bufferSize);
      }

      public Factory(ByteBufferPool byteBufferPool) {
         this(byteBufferPool, 8192);
      }

      public Factory(ByteBufferPool byteBufferPool, int bufferSize) {
         super("gzip");
         this.byteBufferPool = byteBufferPool;
         this.bufferSize = bufferSize;
      }

      @Override
      public ContentDecoder newContentDecoder() {
         return new GZIPContentDecoder(this.byteBufferPool, this.bufferSize);
      }
   }
}
