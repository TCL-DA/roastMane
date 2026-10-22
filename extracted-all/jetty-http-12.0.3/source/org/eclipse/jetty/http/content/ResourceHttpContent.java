package org.eclipse.jetty.http.content;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Set;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.DateGenerator;
import org.eclipse.jetty.http.EtagUtils;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.resource.Resource;

public class ResourceHttpContent implements HttpContent {
   final Resource _resource;
   final Path _path;
   final String _contentType;
   final HttpField _etag;

   public ResourceHttpContent(Resource resource, String contentType) {
      this._resource = resource;
      this._path = resource.getPath();
      this._contentType = contentType;
      this._etag = EtagUtils.createWeakEtagField(resource);
   }

   @Override
   public String getContentTypeValue() {
      return this._contentType;
   }

   @Override
   public HttpField getContentType() {
      return this._contentType == null ? null : new HttpField(HttpHeader.CONTENT_TYPE, this._contentType);
   }

   @Override
   public HttpField getContentEncoding() {
      return null;
   }

   @Override
   public String getContentEncodingValue() {
      return null;
   }

   @Override
   public String getCharacterEncoding() {
      return this._contentType == null ? null : MimeTypes.getCharsetFromContentType(this._contentType);
   }

   @Override
   public MimeTypes.Type getMimeType() {
      return this._contentType == null ? null : (MimeTypes.Type)MimeTypes.CACHE.get(MimeTypes.getContentTypeWithoutCharset(this._contentType));
   }

   @Override
   public Instant getLastModifiedInstant() {
      return this._resource.lastModified();
   }

   @Override
   public HttpField getLastModified() {
      Instant lm = this._resource.lastModified();
      return new HttpField(HttpHeader.LAST_MODIFIED, DateGenerator.formatDate(lm));
   }

   @Override
   public String getLastModifiedValue() {
      Instant lm = this._resource.lastModified();
      return DateGenerator.formatDate(lm);
   }

   @Override
   public HttpField getETag() {
      return this._etag;
   }

   @Override
   public String getETagValue() {
      return this._etag == null ? null : this._etag.getValue();
   }

   @Override
   public HttpField getContentLength() {
      long l = this.getContentLengthValue();
      return l == -1L ? null : new HttpField.LongValueHttpField(HttpHeader.CONTENT_LENGTH, l);
   }

   @Override
   public long getContentLengthValue() {
      return this._resource.length();
   }

   @Override
   public Resource getResource() {
      return this._resource;
   }

   @Override
   public String toString() {
      return String.format("%s@%x{r=%s,ct=%s}", this.getClass().getSimpleName(), this.hashCode(), this._resource, this._contentType);
   }

   @Override
   public ByteBuffer getByteBuffer() {
      return null;
   }

   @Override
   public Set<CompressedContentFormat> getPreCompressedContentFormats() {
      return null;
   }

   @Override
   public void release() {
   }
}
