package org.eclipse.jetty.server;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.eclipse.jetty.http.CookieCompliance;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpException;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.Trailers;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.QuietException;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Sink;
import org.eclipse.jetty.io.Content.Chunk.Processor;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.internal.HttpChannelState;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Response extends Sink {
   Request getRequest();

   int getStatus();

   void setStatus(int var1);

   Mutable getHeaders();

   Supplier<HttpFields> getTrailersSupplier();

   void setTrailersSupplier(Supplier<HttpFields> var1);

   boolean isCommitted();

   boolean hasLastWrite();

   boolean isCompletedSuccessfully();

   void reset();

   CompletableFuture<Void> writeInterim(int var1, HttpFields var2);

   void write(boolean var1, ByteBuffer var2, Callback var3);

   static Processor newTrailersChunkProcessor(Response response) {
      Supplier<HttpFields> supplier = response.getTrailersSupplier();
      return supplier == null ? (chunk, callback) -> false : (chunk, callback) -> {
         if (chunk instanceof Trailers trailers) {
            HttpFields requestTrailers = trailers.getTrailers();
            if (requestTrailers != null) {
               HttpFields responseTrailers = supplier.get();
               if (responseTrailers instanceof Mutable mutable) {
                  mutable.add(requestTrailers);
                  callback.succeeded();
                  return true;
               }
            }
         }

         return false;
      };
   }

   static <T> T as(Response response, Class<T> type) {
      while (response != null) {
         if (type.isInstance(response)) {
            return (T)response;
         }

         response = response instanceof Response.Wrapper wrapper ? wrapper.getWrapped() : null;
      }

      return null;
   }

   static void sendRedirect(Request request, Response response, Callback callback, String location) {
      sendRedirect(request, response, callback, location, false);
   }

   static void sendRedirect(Request request, Response response, Callback callback, String location, boolean consumeAvailable) {
      int code = !HttpMethod.GET.is(request.getMethod()) && request.getConnectionMetaData().getHttpVersion().getVersion() >= HttpVersion.HTTP_1_1.getVersion()
         ? 303
         : 302;
      sendRedirect(request, response, callback, code, location, consumeAvailable);
   }

   static void sendRedirect(Request request, Response response, Callback callback, int code, String location, boolean consumeAvailable) {
      if (!HttpStatus.isRedirection(code)) {
         callback.failed(new IllegalArgumentException("Not a 3xx redirect code"));
      } else if (location == null) {
         callback.failed(new IllegalArgumentException("No location"));
      } else if (response.isCommitted()) {
         callback.failed(new IllegalStateException("Committed"));
      } else {
         Chunk chunk;
         if (consumeAvailable) {
            do {
               chunk = response.getRequest().read();
               if (chunk == null) {
                  response.getHeaders().put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE);
                  break;
               }

               chunk.release();
            } while (!chunk.isLast());
         }

         response.getHeaders().put(HttpHeader.LOCATION, Request.toRedirectURI(request, location));
         response.setStatus(code);
         response.write(true, null, callback);
      }
   }

   static void addCookie(Response response, HttpCookie cookie) {
      if (StringUtil.isBlank(cookie.getName())) {
         throw new IllegalArgumentException("Cookie.name cannot be blank/null");
      } else {
         Request request = response.getRequest();
         response.getHeaders()
            .add(
               new HttpCookieUtils.SetCookieHttpField(
                  HttpCookieUtils.checkSameSite(cookie, request.getContext()),
                  request.getConnectionMetaData().getHttpConfiguration().getResponseCookieCompliance()
               )
            );
         response.getHeaders().put(HttpFields.EXPIRES_01JAN1970);
      }
   }

   static void replaceCookie(Response response, HttpCookie cookie) {
      if (StringUtil.isBlank(cookie.getName())) {
         throw new IllegalArgumentException("Cookie.name cannot be blank/null");
      } else {
         Request request = response.getRequest();
         HttpConfiguration httpConfiguration = request.getConnectionMetaData().getHttpConfiguration();
         ListIterator<HttpField> i = response.getHeaders().listIterator();

         while (i.hasNext()) {
            HttpField field = i.next();
            if (field.getHeader() == HttpHeader.SET_COOKIE) {
               CookieCompliance compliance = httpConfiguration.getResponseCookieCompliance();
               if (field instanceof HttpCookieUtils.SetCookieHttpField
                  ? HttpCookieUtils.match(((HttpCookieUtils.SetCookieHttpField)field).getHttpCookie(), cookie.getName(), cookie.getDomain(), cookie.getPath())
                  : HttpCookieUtils.match(field.getValue(), cookie.getName(), cookie.getDomain(), cookie.getPath())) {
                  i.set(new HttpCookieUtils.SetCookieHttpField(HttpCookieUtils.checkSameSite(cookie, request.getContext()), compliance));
                  return;
               }
            }
         }

         addCookie(response, cookie);
      }
   }

   static void writeError(Request request, Response response, Callback callback, Throwable cause) {
      if (cause == null) {
         cause = new Throwable("unknown cause");
      }

      int status = 500;
      String message = cause.toString();
      if (cause instanceof HttpException httpException) {
         status = httpException.getCode();
         message = httpException.getReason();
      }

      writeError(request, response, callback, status, message, cause);
   }

   static void writeError(Request request, Response response, Callback callback, int status) {
      writeError(request, response, callback, status, null, null);
   }

   static void writeError(Request request, Response response, Callback callback, int status, String message) {
      writeError(request, response, callback, status, message, null);
   }

   static void writeError(Request request, Response response, Callback callback, int status, String message, Throwable cause) {
      Logger logger = LoggerFactory.getLogger(Response.class);
      if (logger.isDebugEnabled()) {
         logger.debug("writeError: status={}, message={}, response={}", new Object[]{status, message, response, cause});
      } else if (cause instanceof QuietException || cause instanceof TimeoutException) {
         logger.debug("writeError: status={}, message={}, response={} {}", new Object[]{status, message, response, cause.toString()});
      } else if (cause != null) {
         logger.warn("writeError: status={}, message={}, response={}", new Object[]{status, message, response, cause});
      }

      if (response.isCommitted()) {
         callback.failed((Throwable)(cause == null ? new IllegalStateException(message == null ? "Committed" : message) : cause));
      } else {
         ResponseUtils.ensureConsumeAvailableOrNotPersistent(request, response);
         if (status <= 0) {
            status = 500;
         }

         if (message == null) {
            message = HttpStatus.getMessage(status);
         }

         response.setStatus(status);
         Context context = request.getContext();
         Request.Handler errorHandler = context.getErrorHandler();
         if (errorHandler != null) {
            Request errorRequest = new ErrorHandler.ErrorRequest(request, status, message, cause);

            try {
               if (errorHandler.handle(errorRequest, response, callback)) {
                  return;
               }
            } catch (Exception var11) {
               if (cause != null && cause != var11) {
                  cause.addSuppressed(var11);
               }
            }
         }

         response.getHeaders().put(ErrorHandler.ERROR_CACHE_CONTROL);
         response.write(true, null, callback);
      }
   }

   static Response getOriginalResponse(Response response) {
      while (response instanceof Response.Wrapper) {
         Response.Wrapper wrapped = (Response.Wrapper)response;
         response = wrapped.getWrapped();
      }

      return response;
   }

   static long getContentBytesWritten(Response response) {
      return getOriginalResponse(response) instanceof HttpChannelState.ChannelResponse channelResponse ? channelResponse.getContentBytesWritten() : -1L;
   }

   static OutputStream asBufferedOutputStream(Request request, Response response) {
      return Sink.asOutputStream(asBufferedSink(request, response));
   }

   static Sink asBufferedSink(Request request, Response response) {
      ConnectionMetaData connectionMetaData = request.getConnectionMetaData();
      ByteBufferPool bufferPool = connectionMetaData.getConnector().getByteBufferPool();
      HttpConfiguration httpConfiguration = connectionMetaData.getHttpConfiguration();
      int bufferSize = httpConfiguration.getOutputBufferSize();
      boolean useOutputDirectByteBuffers = httpConfiguration.isUseOutputDirectByteBuffers();
      int outputAggregationSize = httpConfiguration.getOutputAggregationSize();
      return Sink.asBuffered(response, bufferPool, useOutputDirectByteBuffers, outputAggregationSize, bufferSize);
   }

   public static class Wrapper implements Response {
      private final Request _request;
      private final Response _wrapped;

      public Wrapper(Request request, Response wrapped) {
         this._request = request;
         this._wrapped = wrapped;
      }

      public Response getWrapped() {
         return this._wrapped;
      }

      @Override
      public Request getRequest() {
         return this._request;
      }

      @Override
      public int getStatus() {
         return this.getWrapped().getStatus();
      }

      @Override
      public void setStatus(int code) {
         this.getWrapped().setStatus(code);
      }

      @Override
      public Mutable getHeaders() {
         return this.getWrapped().getHeaders();
      }

      @Override
      public Supplier<HttpFields> getTrailersSupplier() {
         return this.getWrapped().getTrailersSupplier();
      }

      @Override
      public void setTrailersSupplier(Supplier<HttpFields> trailers) {
         this.getWrapped().setTrailersSupplier(trailers);
      }

      @Override
      public boolean isCommitted() {
         return this.getWrapped().isCommitted();
      }

      @Override
      public boolean hasLastWrite() {
         return this.getWrapped().hasLastWrite();
      }

      @Override
      public boolean isCompletedSuccessfully() {
         return this.getWrapped().isCompletedSuccessfully();
      }

      @Override
      public void reset() {
         this.getWrapped().reset();
      }

      @Override
      public CompletableFuture<Void> writeInterim(int status, HttpFields headers) {
         return this.getWrapped().writeInterim(status, headers);
      }

      @Override
      public void write(boolean last, ByteBuffer byteBuffer, Callback callback) {
         this.getWrapped().write(last, byteBuffer, callback);
      }
   }
}
