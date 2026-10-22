package org.eclipse.jetty.client.transport.internal;

import java.nio.ByteBuffer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpRequestException;
import org.eclipse.jetty.client.transport.HttpExchange;
import org.eclipse.jetty.client.transport.HttpRequest;
import org.eclipse.jetty.client.transport.HttpSender;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpGenerator.Result;
import org.eclipse.jetty.http.MetaData.Request;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSenderOverHTTP extends HttpSender {
   private static final Logger LOG = LoggerFactory.getLogger(HttpSenderOverHTTP.class);
   private final IteratingCallback headersCallback = new HttpSenderOverHTTP.HeadersCallback();
   private final IteratingCallback contentCallback = new HttpSenderOverHTTP.ContentCallback();
   private final HttpGenerator generator = new HttpGenerator();
   private HttpExchange exchange;
   private Request metaData;
   private ByteBuffer contentByteBuffer;
   private boolean lastContent;
   private Callback callback;
   private boolean shutdown;

   public HttpSenderOverHTTP(HttpChannelOverHTTP channel) {
      super(channel);
   }

   public HttpChannelOverHTTP getHttpChannel() {
      return (HttpChannelOverHTTP)super.getHttpChannel();
   }

   @Override
   protected void sendHeaders(HttpExchange exchange, ByteBuffer contentBuffer, boolean lastContent, Callback callback) {
      try {
         this.exchange = exchange;
         this.contentByteBuffer = contentBuffer;
         this.lastContent = lastContent;
         this.callback = callback;
         HttpRequest request = exchange.getRequest();
         Source requestContent = request.getBody();
         long contentLength = requestContent == null ? -1L : requestContent.getLength();
         String path = request.getPath();
         String query = request.getQuery();
         if (query != null) {
            path = path + "?" + query;
         }

         this.metaData = new Request(
            request.getMethod(), HttpURI.from(path), request.getVersion(), request.getHeaders(), contentLength, request.getTrailersSupplier()
         );
         if (LOG.isDebugEnabled()) {
            LOG.debug(
               "Sending headers with content {} last={} for {}", new Object[]{BufferUtil.toDetailString(contentBuffer), lastContent, exchange.getRequest()}
            );
         }

         this.headersCallback.iterate();
      } catch (Throwable var11) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Unable to send headers on exchange {}", exchange, var11);
         }

         callback.failed(var11);
      }
   }

   @Override
   protected void sendContent(HttpExchange exchange, ByteBuffer contentBuffer, boolean lastContent, Callback callback) {
      try {
         this.exchange = exchange;
         this.contentByteBuffer = contentBuffer;
         this.lastContent = lastContent;
         this.callback = callback;
         if (LOG.isDebugEnabled()) {
            LOG.debug("Sending content {} last={} for {}", new Object[]{BufferUtil.toDetailString(contentBuffer), lastContent, exchange.getRequest()});
         }

         this.contentCallback.iterate();
      } catch (Throwable var6) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Unable to send content on {}", exchange, var6);
         }

         callback.failed(var6);
      }
   }

   @Override
   protected void reset() {
      this.headersCallback.reset();
      this.contentCallback.reset();
      this.generator.reset();
      super.reset();
   }

   @Override
   protected void dispose() {
      this.generator.abort();
      super.dispose();
      this.shutdownOutput();
   }

   private void shutdownOutput() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Request shutdown output {}", this.getHttpExchange().getRequest());
      }

      this.shutdown = true;
   }

   protected boolean isShutdown() {
      return this.shutdown;
   }

   @Override
   public String toString() {
      return String.format("%s[%s]", super.toString(), this.generator);
   }

   private class ContentCallback extends IteratingCallback {
      private RetainableByteBuffer chunkBuffer;

      public ContentCallback() {
         super(false);
      }

      protected Action process() throws Exception {
         HttpClient httpClient = HttpSenderOverHTTP.this.getHttpChannel().getHttpDestination().getHttpClient();
         ByteBufferPool bufferPool = httpClient.getByteBufferPool();
         boolean useDirectByteBuffers = httpClient.isUseOutputDirectByteBuffers();

         while (true) {
            ByteBuffer chunkByteBuffer = this.chunkBuffer == null ? null : this.chunkBuffer.getByteBuffer();
            Result result = HttpSenderOverHTTP.this.generator
               .generateRequest(null, null, chunkByteBuffer, HttpSenderOverHTTP.this.contentByteBuffer, HttpSenderOverHTTP.this.lastContent);
            if (HttpSenderOverHTTP.LOG.isDebugEnabled()) {
               HttpSenderOverHTTP.LOG
                  .debug(
                     "Generated content ({} bytes, last={}) - {}/{}",
                     new Object[]{
                        HttpSenderOverHTTP.this.contentByteBuffer == null ? -1 : HttpSenderOverHTTP.this.contentByteBuffer.remaining(),
                        HttpSenderOverHTTP.this.lastContent,
                        result,
                        HttpSenderOverHTTP.this.generator
                     }
                  );
            }

            switch (result) {
               case NEED_CHUNK:
                  this.chunkBuffer = bufferPool.acquire(12, useDirectByteBuffers);
                  break;
               case NEED_CHUNK_TRAILER:
                  this.chunkBuffer = bufferPool.acquire(httpClient.getRequestBufferSize(), useDirectByteBuffers);
                  break;
               case FLUSH:
                  EndPoint endPoint = HttpSenderOverHTTP.this.getHttpChannel().getHttpConnection().getEndPoint();
                  if (chunkByteBuffer != null) {
                     endPoint.write(this, new ByteBuffer[]{chunkByteBuffer, HttpSenderOverHTTP.this.contentByteBuffer});
                  } else {
                     endPoint.write(this, new ByteBuffer[]{HttpSenderOverHTTP.this.contentByteBuffer});
                  }

                  return Action.SCHEDULED;
               case SHUTDOWN_OUT:
                  HttpSenderOverHTTP.this.shutdownOutput();
               case CONTINUE:
                  break;
               case DONE:
                  this.release();
                  HttpSenderOverHTTP.this.callback.succeeded();
                  return Action.IDLE;
               default:
                  throw new IllegalStateException(result.toString());
            }
         }
      }

      protected void onCompleteFailure(Throwable cause) {
         this.release();
         HttpSenderOverHTTP.this.callback.failed(cause);
      }

      private void release() {
         if (this.chunkBuffer != null) {
            this.chunkBuffer.release();
         }

         this.chunkBuffer = null;
         HttpSenderOverHTTP.this.contentByteBuffer = null;
      }
   }

   private class HeadersCallback extends IteratingCallback {
      private RetainableByteBuffer headerBuffer;
      private RetainableByteBuffer chunkBuffer;
      private boolean generated;

      private HeadersCallback() {
         super(false);
      }

      protected Action process() throws Exception {
         HttpClient httpClient = HttpSenderOverHTTP.this.getHttpChannel().getHttpDestination().getHttpClient();
         ByteBufferPool bufferPool = httpClient.getByteBufferPool();
         boolean useDirectByteBuffers = httpClient.isUseOutputDirectByteBuffers();

         while (true) {
            ByteBuffer headerByteBuffer = this.headerBuffer == null ? null : this.headerBuffer.getByteBuffer();
            ByteBuffer chunkByteBuffer = this.chunkBuffer == null ? null : this.chunkBuffer.getByteBuffer();
            Result result = HttpSenderOverHTTP.this.generator
               .generateRequest(
                  HttpSenderOverHTTP.this.metaData,
                  headerByteBuffer,
                  chunkByteBuffer,
                  HttpSenderOverHTTP.this.contentByteBuffer,
                  HttpSenderOverHTTP.this.lastContent
               );
            if (HttpSenderOverHTTP.LOG.isDebugEnabled()) {
               HttpSenderOverHTTP.LOG
                  .debug(
                     "Generated headers ({} bytes), chunk ({} bytes), content ({} bytes) - {}/{} for {}",
                     new Object[]{
                        headerByteBuffer == null ? -1 : headerByteBuffer.remaining(),
                        chunkByteBuffer == null ? -1 : chunkByteBuffer.remaining(),
                        HttpSenderOverHTTP.this.contentByteBuffer == null ? -1 : HttpSenderOverHTTP.this.contentByteBuffer.remaining(),
                        result,
                        HttpSenderOverHTTP.this.generator,
                        HttpSenderOverHTTP.this.exchange.getRequest()
                     }
                  );
            }

            switch (result) {
               case NEED_HEADER:
                  this.headerBuffer = bufferPool.acquire(httpClient.getRequestBufferSize(), useDirectByteBuffers);
                  break;
               case HEADER_OVERFLOW:
                  this.headerBuffer.release();
                  this.headerBuffer = null;
                  throw new IllegalArgumentException("Request header too large");
               case NEED_CHUNK:
                  this.chunkBuffer = bufferPool.acquire(12, useDirectByteBuffers);
                  break;
               case NEED_CHUNK_TRAILER:
                  this.chunkBuffer = bufferPool.acquire(httpClient.getRequestBufferSize(), useDirectByteBuffers);
                  break;
               case FLUSH:
                  EndPoint endPoint = HttpSenderOverHTTP.this.getHttpChannel().getHttpConnection().getEndPoint();
                  if (headerByteBuffer == null) {
                     headerByteBuffer = BufferUtil.EMPTY_BUFFER;
                  }

                  if (chunkByteBuffer == null) {
                     chunkByteBuffer = BufferUtil.EMPTY_BUFFER;
                  }

                  if (HttpSenderOverHTTP.this.contentByteBuffer == null) {
                     HttpSenderOverHTTP.this.contentByteBuffer = BufferUtil.EMPTY_BUFFER;
                  }

                  long bytes = (long)(headerByteBuffer.remaining() + chunkByteBuffer.remaining() + HttpSenderOverHTTP.this.contentByteBuffer.remaining());
                  HttpSenderOverHTTP.this.getHttpChannel().getHttpConnection().addBytesOut(bytes);
                  endPoint.write(this, new ByteBuffer[]{headerByteBuffer, chunkByteBuffer, HttpSenderOverHTTP.this.contentByteBuffer});
                  this.generated = true;
                  return Action.SCHEDULED;
               case SHUTDOWN_OUT:
                  HttpSenderOverHTTP.this.shutdownOutput();
                  return Action.SUCCEEDED;
               case CONTINUE:
                  if (this.generated) {
                     return Action.SUCCEEDED;
                  }
                  break;
               case DONE:
                  if (this.generated) {
                     return Action.SUCCEEDED;
                  }

                  throw new HttpRequestException("Could not generate headers", HttpSenderOverHTTP.this.exchange.getRequest());
               default:
                  throw new IllegalStateException(result.toString());
            }
         }
      }

      public void succeeded() {
         this.release();
         super.succeeded();
      }

      public void failed(Throwable x) {
         this.release();
         super.failed(x);
      }

      protected void onCompleteSuccess() {
         super.onCompleteSuccess();
         HttpSenderOverHTTP.this.callback.succeeded();
      }

      protected void onCompleteFailure(Throwable cause) {
         super.onCompleteFailure(cause);
         HttpSenderOverHTTP.this.callback.failed(cause);
      }

      private void release() {
         if (this.headerBuffer != null) {
            this.headerBuffer.release();
         }

         this.headerBuffer = null;
         if (this.chunkBuffer != null) {
            this.chunkBuffer.release();
         }

         this.chunkBuffer = null;
         HttpSenderOverHTTP.this.contentByteBuffer = null;
      }
   }
}
