package org.eclipse.jetty.client.transport;

import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Result;
import org.eclipse.jetty.io.CyclicTimeouts.Expirable;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpExchange implements Expirable {
   private static final Logger LOG = LoggerFactory.getLogger(HttpExchange.class);
   private final AutoLock lock = new AutoLock();
   private final HttpDestination destination;
   private final HttpRequest request;
   private final ResponseListeners listeners;
   private final HttpResponse response;
   private HttpExchange.State requestState = HttpExchange.State.PENDING;
   private HttpExchange.State responseState = HttpExchange.State.PENDING;
   private HttpChannel _channel;
   private Throwable requestFailure;
   private Throwable responseFailure;

   public HttpExchange(HttpDestination destination, HttpRequest request) {
      this(destination, request, request.getResponseListeners());
   }

   public HttpExchange(HttpDestination destination, HttpRequest request, ResponseListeners listeners) {
      this.destination = destination;
      this.request = request;
      this.listeners = listeners;
      this.response = new HttpResponse(request);
      HttpConversation conversation = request.getConversation();
      conversation.getExchanges().offer(this);
      conversation.updateResponseListeners(null);
   }

   public HttpDestination getHttpDestination() {
      return this.destination;
   }

   public HttpConversation getConversation() {
      return this.request.getConversation();
   }

   public HttpRequest getRequest() {
      return this.request;
   }

   public Throwable getRequestFailure() {
      AutoLock l = this.lock.lock();

      Throwable var2;
      try {
         var2 = this.requestFailure;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public ResponseListeners getResponseListeners() {
      return this.listeners;
   }

   public HttpResponse getResponse() {
      return this.response;
   }

   public Throwable getResponseFailure() {
      AutoLock l = this.lock.lock();

      Throwable var2;
      try {
         var2 = this.responseFailure;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public long getExpireNanoTime() {
      return this.request.getTimeoutNanoTime();
   }

   boolean associate(HttpChannel channel) {
      boolean result = false;
      boolean abort = false;
      AutoLock l = this.lock.lock();

      try {
         if (this.requestState == HttpExchange.State.PENDING && this.responseState == HttpExchange.State.PENDING) {
            abort = this._channel != null;
            if (!abort) {
               this._channel = channel;
               result = true;
            }
         }
      } catch (Throwable var8) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (l != null) {
         l.close();
      }

      if (abort) {
         this.request.abort(new IllegalStateException(this.toString()));
      }

      return result;
   }

   void disassociate(HttpChannel channel) {
      boolean abort = false;
      AutoLock l = this.lock.lock();

      try {
         if (this._channel != channel || this.requestState != HttpExchange.State.TERMINATED || this.responseState != HttpExchange.State.TERMINATED) {
            abort = true;
         }

         this._channel = null;
      } catch (Throwable var7) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (l != null) {
         l.close();
      }

      if (abort) {
         this.request.abort(new IllegalStateException(this.toString()));
      }
   }

   private HttpChannel getHttpChannel() {
      AutoLock l = this.lock.lock();

      HttpChannel var2;
      try {
         var2 = this._channel;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public boolean requestComplete(Throwable failure) {
      AutoLock l = this.lock.lock();

      boolean var3;
      try {
         var3 = this.completeRequest(failure);
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }

      return var3;
   }

   private boolean completeRequest(Throwable failure) {
      if (this.requestState == HttpExchange.State.PENDING) {
         this.requestState = HttpExchange.State.COMPLETED;
         this.requestFailure = failure;
         return true;
      } else {
         return false;
      }
   }

   public boolean isResponseComplete() {
      AutoLock l = this.lock.lock();

      boolean var2;
      try {
         var2 = this.responseState == HttpExchange.State.COMPLETED;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public boolean responseComplete(Throwable failure) {
      AutoLock l = this.lock.lock();

      boolean var3;
      try {
         var3 = this.completeResponse(failure);
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }

      return var3;
   }

   private boolean completeResponse(Throwable failure) {
      if (this.responseState == HttpExchange.State.PENDING) {
         this.responseState = HttpExchange.State.COMPLETED;
         this.responseFailure = failure;
         return true;
      } else {
         return false;
      }
   }

   public Result terminateRequest() {
      Result result = null;
      AutoLock l = this.lock.lock();

      try {
         if (this.requestState == HttpExchange.State.COMPLETED) {
            this.requestState = HttpExchange.State.TERMINATED;
         }

         if (this.requestState == HttpExchange.State.TERMINATED && this.responseState == HttpExchange.State.TERMINATED) {
            result = new Result(this.getRequest(), this.requestFailure, this.getResponse(), this.responseFailure);
         }
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Terminated request for {}, result: {}", this, result);
      }

      return result;
   }

   public Result terminateResponse() {
      Result result = null;
      AutoLock l = this.lock.lock();

      try {
         if (this.responseState == HttpExchange.State.COMPLETED) {
            this.responseState = HttpExchange.State.TERMINATED;
         }

         if (this.requestState == HttpExchange.State.TERMINATED && this.responseState == HttpExchange.State.TERMINATED) {
            result = new Result(this.getRequest(), this.requestFailure, this.getResponse(), this.responseFailure);
         }
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Terminated response for {}, result: {}", this, result);
      }

      return result;
   }

   public void abort(Throwable failure, Promise<Boolean> promise) {
      AutoLock l = this.lock.lock();

      boolean abortRequest;
      boolean abortResponse;
      try {
         abortRequest = this.completeRequest(failure);
         abortResponse = this.completeResponse(failure);
      } catch (Throwable var9) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (l != null) {
         l.close();
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Failed {}: req={}/rsp={}", new Object[]{this, abortRequest, abortResponse, failure});
      }

      if (!abortRequest && !abortResponse) {
         promise.succeeded(false);
      } else {
         Request.Content body = this.request.getBody();
         if (abortRequest && body != null) {
            body.fail(failure);
         }

         if (this.destination.remove(this)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Aborting while queued {}: {}", this, failure);
            }

            this.notifyFailureComplete(failure);
            promise.succeeded(true);
         } else {
            HttpChannel channel = this.getHttpChannel();
            if (channel == null) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Aborting before association {}: {}", this, failure);
               }

               this.notifyFailureComplete(failure);
               promise.succeeded(true);
            } else {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Aborting while active {}: {}", this, failure);
               }

               channel.abort(this, abortRequest ? failure : null, abortResponse ? failure : null, promise);
            }
         }
      }
   }

   private void notifyFailureComplete(Throwable failure) {
      this.request.notifyFailure(failure);
      ResponseListeners listeners = this.getConversation().getResponseListeners();
      listeners.notifyFailure(this.response, failure);
      listeners.notifyComplete(new Result(this.request, failure, this.response, failure));
   }

   public void resetResponse() {
      AutoLock l = this.lock.lock();

      try {
         this.responseState = HttpExchange.State.PENDING;
         this.responseFailure = null;
         this.response.clearHeaders();
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }
   }

   public void proceed(Throwable failure) {
      HttpChannel channel = this.getHttpChannel();
      if (channel != null) {
         channel.proceed(this, failure);
      }
   }

   @Override
   public String toString() {
      AutoLock l = this.lock.lock();

      String var2;
      try {
         var2 = String.format(
            "%s@%x{req=%s[%s/%s] res=%s[%s/%s]}",
            HttpExchange.class.getSimpleName(),
            this.hashCode(),
            this.request,
            this.requestState,
            this.requestFailure,
            this.response,
            this.responseState,
            this.responseFailure
         );
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   private static enum State {
      PENDING,
      COMPLETED,
      TERMINATED;
   }
}
