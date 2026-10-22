package org.eclipse.jetty.server.handler;

import java.nio.ByteBuffer;
import java.util.function.Supplier;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpStream;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventsHandler extends Handler.Wrapper {
   private static final Logger LOG = LoggerFactory.getLogger(EventsHandler.class);

   public EventsHandler() {
      this(null);
   }

   public EventsHandler(Handler handler) {
      super(handler);
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      Request roRequest = Request.asReadOnly(request);
      this.notifyOnBeforeHandling(roRequest);

      try {
         EventsHandler.EventsRequest wrappedRequest = new EventsHandler.EventsRequest(request, roRequest);
         EventsHandler.EventsResponse wrappedResponse = new EventsHandler.EventsResponse(roRequest, response);
         request.addHttpStreamWrapper(stream -> new HttpStream.Wrapper(stream) {
               @Override
               public void succeeded() {
                  EventsHandler.this.notifyOnResponseBegin(roRequest, wrappedResponse);
                  EventsHandler.this.notifyOnResponseTrailersComplete(roRequest, wrappedResponse);
                  EventsHandler.this.notifyOnComplete(roRequest, null);
                  super.succeeded();
               }

               @Override
               public void failed(Throwable x) {
                  EventsHandler.this.notifyOnResponseBegin(roRequest, wrappedResponse);
                  EventsHandler.this.notifyOnResponseTrailersComplete(roRequest, wrappedResponse);
                  EventsHandler.this.notifyOnComplete(roRequest, x);
                  super.failed(x);
               }
            });
         boolean handled = super.handle(wrappedRequest, wrappedResponse, callback);
         this.notifyOnAfterHandling(roRequest, handled, null);
         return handled;
      } catch (Throwable var8) {
         this.notifyOnAfterHandling(roRequest, false, var8);
         throw var8;
      }
   }

   private void notifyOnBeforeHandling(Request request) {
      try {
         this.onBeforeHandling(request);
      } catch (Throwable var3) {
         LOG.info("Error firing onBeforeHandling", var3);
      }
   }

   private void notifyOnRequestRead(Request wrapped, Chunk chunk) {
      try {
         this.onRequestRead(wrapped, chunk == null ? null : chunk.asReadOnly());
      } catch (Throwable var4) {
         LOG.info("Error firing onRequestRead", var4);
      }
   }

   private void notifyOnAfterHandling(Request request, boolean handled, Throwable failure) {
      try {
         this.onAfterHandling(request, handled, failure);
      } catch (Throwable var5) {
         LOG.info("Error firing onAfterHandling", var5);
      }
   }

   private void notifyOnResponseBegin(Request request, EventsHandler.EventsResponse response) {
      try {
         if (!response.notifiedOnResponseBegin) {
            this.onResponseBegin(request, response.getStatus(), response.getHeaders().asImmutable());
            response.notifiedOnResponseBegin = true;
         }
      } catch (Throwable var4) {
         LOG.info("Error firing onResponseBegin", var4);
      }
   }

   private void notifyOnResponseWrite(Request request, boolean last, ByteBuffer content) {
      try {
         this.onResponseWrite(request, last, content == null ? null : content.asReadOnlyBuffer());
      } catch (Throwable var5) {
         LOG.info("Error firing onResponseWrite", var5);
      }
   }

   private void notifyOnResponseWriteComplete(Request request, Throwable failure) {
      try {
         this.onResponseWriteComplete(request, failure);
      } catch (Throwable var4) {
         LOG.info("Error firing onResponseWriteComplete", var4);
      }
   }

   private void notifyOnResponseTrailersComplete(Request request, EventsHandler.EventsResponse response) {
      try {
         if (response.suppliedTrailers != null) {
            this.onResponseTrailersComplete(request, response.suppliedTrailers);
         }
      } catch (Throwable var4) {
         LOG.info("Error firing onResponseTrailersComplete", var4);
      }
   }

   private void notifyOnComplete(Request request, Throwable failure) {
      try {
         this.onComplete(request, failure);
      } catch (Throwable var4) {
         LOG.info("Error firing onComplete", var4);
      }
   }

   protected void onBeforeHandling(Request request) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onBeforeHandling of {}", request);
      }
   }

   protected void onRequestRead(Request request, Chunk chunk) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onRequestRead of {} and {}", request, chunk);
      }
   }

   protected void onAfterHandling(Request request, boolean handled, Throwable failure) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onAfterHandling of {} handled={}", new Object[]{request, handled, failure});
      }
   }

   protected void onResponseBegin(Request request, int status, HttpFields headers) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onResponseBegin of {} status={} headers={}", new Object[]{request, status, headers});
      }
   }

   protected void onResponseWrite(Request request, boolean last, ByteBuffer content) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onResponseWrite of {} last={} content={}", new Object[]{request, last, BufferUtil.toDetailString(content)});
      }
   }

   protected void onResponseWriteComplete(Request request, Throwable failure) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onResponseWriteComplete of {}", request, failure);
      }
   }

   protected void onResponseTrailersComplete(Request request, HttpFields trailers) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onResponseTrailersComplete of {}, trailers={}", request, trailers);
      }
   }

   protected void onComplete(Request request, Throwable failure) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onComplete of {}", request, failure);
      }
   }

   private class EventsRequest extends Request.Wrapper {
      private final Request roRequest;

      public EventsRequest(Request request, Request roRequest) {
         super(request);
         this.roRequest = roRequest;
      }

      @Override
      public Chunk read() {
         Chunk chunk = super.read();
         EventsHandler.this.notifyOnRequestRead(this.roRequest, chunk);
         return chunk;
      }
   }

   private class EventsResponse extends Response.Wrapper {
      private boolean notifiedOnResponseBegin;
      private HttpFields suppliedTrailers;

      public EventsResponse(Request roRequest, Response response) {
         super(roRequest, response);
      }

      @Override
      public void write(boolean last, ByteBuffer byteBuffer, Callback callback) {
         EventsHandler.this.notifyOnResponseBegin(this.getRequest(), this);
         EventsHandler.this.notifyOnResponseWrite(this.getRequest(), last, byteBuffer);
         super.write(last, byteBuffer, Callback.from(callback, x -> EventsHandler.this.notifyOnResponseWriteComplete(this.getRequest(), x)));
      }

      @Override
      public void setTrailersSupplier(Supplier<HttpFields> trailers) {
         super.setTrailersSupplier(trailers == null ? null : () -> this.suppliedTrailers = trailers.get());
      }
   }
}
