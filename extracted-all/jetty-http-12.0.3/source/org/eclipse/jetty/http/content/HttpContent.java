package org.eclipse.jetty.http.content;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Set;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.resource.Resource;

public interface HttpContent {
   HttpField getContentType();

   String getContentTypeValue();

   String getCharacterEncoding();

   MimeTypes.Type getMimeType();

   HttpField getContentEncoding();

   String getContentEncodingValue();

   HttpField getContentLength();

   long getContentLengthValue();

   Instant getLastModifiedInstant();

   HttpField getLastModified();

   String getLastModifiedValue();

   HttpField getETag();

   String getETagValue();

   Resource getResource();

   ByteBuffer getByteBuffer();

   default long getBytesOccupied() {
      return this.getContentLengthValue();
   }

   Set<CompressedContentFormat> getPreCompressedContentFormats();

   void release();

   public interface Factory {
      HttpContent getContent(String var1) throws IOException;
   }

   public static class Wrapper implements HttpContent {
      private final HttpContent _delegate;

      public Wrapper(HttpContent content) {
         this._delegate = content;
      }

      public HttpContent getWrapped() {
         return this._delegate;
      }

      @Override
      public HttpField getContentType() {
         return this._delegate.getContentType();
      }

      @Override
      public String getContentTypeValue() {
         return this._delegate.getContentTypeValue();
      }

      @Override
      public String getCharacterEncoding() {
         return this._delegate.getCharacterEncoding();
      }

      @Override
      public MimeTypes.Type getMimeType() {
         return this._delegate.getMimeType();
      }

      @Override
      public HttpField getContentEncoding() {
         return this._delegate.getContentEncoding();
      }

      @Override
      public String getContentEncodingValue() {
         return this._delegate.getContentEncodingValue();
      }

      @Override
      public HttpField getContentLength() {
         return this._delegate.getContentLength();
      }

      @Override
      public long getContentLengthValue() {
         return this._delegate.getContentLengthValue();
      }

      @Override
      public Instant getLastModifiedInstant() {
         return this._delegate.getLastModifiedInstant();
      }

      @Override
      public HttpField getLastModified() {
         return this._delegate.getLastModified();
      }

      @Override
      public String getLastModifiedValue() {
         return this._delegate.getLastModifiedValue();
      }

      @Override
      public HttpField getETag() {
         return this._delegate.getETag();
      }

      @Override
      public String getETagValue() {
         return this._delegate.getETagValue();
      }

      @Override
      public Resource getResource() {
         return this._delegate.getResource();
      }

      @Override
      public ByteBuffer getByteBuffer() {
         return this._delegate.getByteBuffer();
      }

      @Override
      public long getBytesOccupied() {
         return this._delegate.getBytesOccupied();
      }

      @Override
      public Set<CompressedContentFormat> getPreCompressedContentFormats() {
         return this._delegate.getPreCompressedContentFormats();
      }

      @Override
      public void release() {
         this._delegate.release();
      }

      @Override
      public String toString() {
         return "%s@%x[%s]".formatted(this.getClass().getSimpleName(), this.hashCode(), this._delegate);
      }
   }
}
