package org.eclipse.jetty.http.content;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Set;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.EtagUtils;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.resource.Resource;

public class PreCompressedHttpContent implements HttpContent {
   private final HttpContent _content;
   private final HttpContent _precompressedContent;
   private final CompressedContentFormat _format;
   private final HttpField _etag;

   public PreCompressedHttpContent(HttpContent content, HttpContent precompressedContent, CompressedContentFormat format) {
      if (content == null) {
         throw new IllegalArgumentException("Null HttpContent");
      } else if (precompressedContent == null) {
         throw new IllegalArgumentException("Null Precompressed HttpContent");
      } else if (format == null) {
         throw new IllegalArgumentException("Null Compressed Content Format");
      } else {
         this._content = content;
         this._precompressedContent = precompressedContent;
         this._format = format;
         this._etag = new HttpField(HttpHeader.ETAG, EtagUtils.rewriteWithSuffix(this._content.getETagValue(), this._format.getEtagSuffix()));
      }
   }

   @Override
   public Resource getResource() {
      return this._precompressedContent.getResource();
   }

   @Override
   public HttpField getETag() {
      return this._etag;
   }

   @Override
   public String getETagValue() {
      return this.getETag().getValue();
   }

   @Override
   public Instant getLastModifiedInstant() {
      return this._precompressedContent.getLastModifiedInstant();
   }

   @Override
   public HttpField getLastModified() {
      return this._precompressedContent.getLastModified();
   }

   @Override
   public String getLastModifiedValue() {
      return this._precompressedContent.getLastModifiedValue();
   }

   @Override
   public HttpField getContentType() {
      return this._content.getContentType();
   }

   @Override
   public String getContentTypeValue() {
      return this._content.getContentTypeValue();
   }

   @Override
   public HttpField getContentEncoding() {
      return this._format.getContentEncoding();
   }

   @Override
   public String getContentEncodingValue() {
      return this._format.getContentEncoding().getValue();
   }

   @Override
   public String getCharacterEncoding() {
      return this._content.getCharacterEncoding();
   }

   @Override
   public MimeTypes.Type getMimeType() {
      return this._content.getMimeType();
   }

   @Override
   public HttpField getContentLength() {
      return this._precompressedContent.getContentLength();
   }

   @Override
   public long getContentLengthValue() {
      return this._precompressedContent.getContentLengthValue();
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x{e=%s,r=%s|%s,lm=%s|%s,ct=%s}",
         this.getClass().getSimpleName(),
         this.hashCode(),
         this._format,
         this._content.getResource().lastModified(),
         this._precompressedContent.getResource().lastModified(),
         0L,
         0L,
         this.getContentType()
      );
   }

   @Override
   public ByteBuffer getByteBuffer() {
      return this._precompressedContent.getByteBuffer();
   }

   @Override
   public Set<CompressedContentFormat> getPreCompressedContentFormats() {
      return this._content.getPreCompressedContentFormats();
   }

   @Override
   public void release() {
      this._precompressedContent.release();
   }
}
