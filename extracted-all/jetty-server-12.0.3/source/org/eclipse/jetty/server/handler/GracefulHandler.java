package org.eclipse.jetty.server.handler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.CountingCallback;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.component.Graceful;
import org.eclipse.jetty.util.component.Graceful.Shutdown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GracefulHandler extends Handler.Wrapper implements Graceful {
   private static final Logger LOG = LoggerFactory.getLogger(GracefulHandler.class);
   private final LongAdder _requests = new LongAdder();
   private final Shutdown _shutdown = new Shutdown(this) {
      public boolean isShutdownDone() {
         long count = GracefulHandler.this.getCurrentRequestCount();
         if (GracefulHandler.LOG.isDebugEnabled()) {
            GracefulHandler.LOG.debug("isShutdownDone: count {}", count);
         }

         return count == 0L;
      }
   };

   public GracefulHandler() {
      this(null);
   }

   public GracefulHandler(Handler handler) {
      super(handler);
   }

   @ManagedAttribute("number of requests being currently handled")
   public long getCurrentRequestCount() {
      return this._requests.sum();
   }

   public boolean isShutdown() {
      return this._shutdown.isShutdown();
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      Handler handler = this.getHandler();
      if (handler != null && this.isStarted()) {
         GracefulHandler.ShutdownTrackingCallback shutdownCallback = new GracefulHandler.ShutdownTrackingCallback(request, response, callback);
         if (this.isShutdown()) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Service Unavailable: {}", request.getHttpURI());
            }

            Response.writeError(request, response, shutdownCallback, 503);
            return true;
         } else {
            boolean var7;
            try {
               boolean handled = super.handle(request, response, shutdownCallback);
               if (!handled) {
                  shutdownCallback.completed();
               }

               return handled;
            } catch (Throwable var11) {
               Response.writeError(request, response, shutdownCallback, var11);
               var7 = true;
            } finally {
               if (this.isShutdown()) {
                  this._shutdown.check();
               }
            }

            return var7;
         }
      } else {
         return false;
      }
   }

   public CompletableFuture<Void> shutdown() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Shutdown requested");
      }

      return this._shutdown.shutdown();
   }

   private class ShutdownTrackingCallback extends CountingCallback {
      final Request request;
      final Response response;

      public ShutdownTrackingCallback(Request request, Response response, Callback callback) {
         super(callback, 1);
         this.request = request;
         this.response = response;
         GracefulHandler.this._requests.increment();
      }

      public void completed() {
         GracefulHandler.this._requests.decrement();
         if (GracefulHandler.this.isShutdown()) {
            GracefulHandler.this._shutdown.check();
         }
      }
   }
}
