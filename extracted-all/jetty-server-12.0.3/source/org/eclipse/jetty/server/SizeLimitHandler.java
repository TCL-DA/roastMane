package org.eclipse.jetty.server;

import java.nio.ByteBuffer;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpException.RuntimeException;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.util.Callback;

public class SizeLimitHandler extends Handler.Wrapper {
   private final long _requestLimit;
   private final long _responseLimit;
   private long _read = 0L;
   private long _written = 0L;

   public SizeLimitHandler(long requestLimit, long responseLimit) {
      this._requestLimit = requestLimit;
      this._responseLimit = responseLimit;
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      HttpField contentLengthField = request.getHeaders().getField(HttpHeader.CONTENT_LENGTH);
      if (contentLengthField != null) {
         long contentLength = contentLengthField.getLongValue();
         if (this._requestLimit >= 0L && contentLength > this._requestLimit) {
            String s = "Request body is too large: " + contentLength + ">" + this._requestLimit;
            Response.writeError(request, response, callback, 413, s);
            return true;
         }
      }

      final org.eclipse.jetty.http.HttpFields.Mutable.Wrapper httpFields = new org.eclipse.jetty.http.HttpFields.Mutable.Wrapper(response.getHeaders()) {
         public HttpField onAddField(HttpField field) {
            if (field.getHeader().is(HttpHeader.CONTENT_LENGTH.asString())) {
               long contentLength = field.getLongValue();
               if (SizeLimitHandler.this._responseLimit >= 0L && contentLength > SizeLimitHandler.this._responseLimit) {
                  throw new RuntimeException(500, "Response body is too large: " + contentLength + ">" + SizeLimitHandler.this._responseLimit);
               }
            }

            return super.onAddField(field);
         }
      };
      Response var8 = new Response.Wrapper(request, response) {
         @Override
         public Mutable getHeaders() {
            return httpFields;
         }
      };
      request.addHttpStreamWrapper(
         httpStream -> new HttpStream.Wrapper(httpStream) {
               @Override
               public Chunk read() {
                  Chunk chunk = super.read();
                  if (chunk == null) {
                     return null;
                  } else if (chunk.getFailure() != null) {
                     return chunk;
                  } else {
                     ByteBuffer content = chunk.getByteBuffer();
                     if (content != null && content.remaining() > 0) {
                        SizeLimitHandler.this._read = SizeLimitHandler.this._read + (long)content.remaining();
                        if (SizeLimitHandler.this._requestLimit >= 0L && SizeLimitHandler.this._read > SizeLimitHandler.this._requestLimit) {
                           BadMessageException e = new BadMessageException(
                              413, "Request body is too large: " + SizeLimitHandler.this._read + ">" + SizeLimitHandler.this._requestLimit
                           );
                           request.fail(e);
                           return null;
                        }
                     }

                     return chunk;
                  }
               }

               @Override
               public void send(
                  org.eclipse.jetty.http.MetaData.Request requestx,
                  org.eclipse.jetty.http.MetaData.Response response,
                  boolean last,
                  ByteBuffer content,
                  Callback callback
               ) {
                  if (content != null && content.remaining() > 0) {
                     if (SizeLimitHandler.this._responseLimit >= 0L
                        && SizeLimitHandler.this._written + (long)content.remaining() > SizeLimitHandler.this._responseLimit) {
                        callback.failed(
                           new RuntimeException(
                              500,
                              "Response body is too large: "
                                 + SizeLimitHandler.this._written
                                 + content.remaining()
                                 + ">"
                                 + SizeLimitHandler.this._responseLimit
                           )
                        );
                        return;
                     }

                     SizeLimitHandler.this._written = SizeLimitHandler.this._written + (long)content.remaining();
                  }

                  super.send(request, response, last, content, callback);
               }
            }
      );
      return super.handle(request, var8, callback);
   }
}
