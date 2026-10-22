package org.eclipse.jetty.client.internal;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;

public class HttpContentResponse implements ContentResponse {
   private final Response response;
   private final byte[] content;
   private final String mediaType;
   private final String encoding;

   public HttpContentResponse(Response response, byte[] content, String mediaType, String encoding) {
      this.response = response;
      this.content = content;
      this.mediaType = mediaType;
      this.encoding = encoding;
   }

   @Override
   public Request getRequest() {
      return this.response.getRequest();
   }

   @Override
   public HttpVersion getVersion() {
      return this.response.getVersion();
   }

   @Override
   public int getStatus() {
      return this.response.getStatus();
   }

   @Override
   public String getReason() {
      return this.response.getReason();
   }

   @Override
   public HttpFields getHeaders() {
      return this.response.getHeaders();
   }

   @Override
   public HttpFields getTrailers() {
      return this.response.getTrailers();
   }

   @Override
   public CompletableFuture<Boolean> abort(Throwable cause) {
      return this.response.abort(cause);
   }

   @Override
   public String getMediaType() {
      return this.mediaType;
   }

   @Override
   public String getEncoding() {
      return this.encoding;
   }

   @Override
   public byte[] getContent() {
      return this.content;
   }

   @Override
   public String getContentAsString() {
      String encoding = this.encoding;
      if (encoding == null) {
         return new String(this.getContent(), StandardCharsets.UTF_8);
      } else {
         try {
            return new String(this.getContent(), encoding);
         } catch (UnsupportedEncodingException var3) {
            throw new UnsupportedCharsetException(encoding);
         }
      }
   }

   @Override
   public String toString() {
      return String.format(
         "%s[%s %d %s - %d bytes]", HttpContentResponse.class.getSimpleName(), this.getVersion(), this.getStatus(), this.getReason(), this.getContent().length
      );
   }
}
