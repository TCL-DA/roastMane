package org.eclipse.jetty.client.transport;

import org.eclipse.jetty.client.Result;
import org.eclipse.jetty.io.CyclicTimeouts.Expirable;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.Promise.Completable;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpChannel implements Expirable {
   private static final Logger LOG = LoggerFactory.getLogger(HttpChannel.class);
   private final AutoLock _lock = new AutoLock();
   private final HttpDestination _destination;
   private HttpExchange _exchange;

   protected HttpChannel(HttpDestination destination) {
      this._destination = destination;
   }

   public void destroy() {
   }

   public HttpDestination getHttpDestination() {
      return this._destination;
   }

   public boolean associate(HttpExchange exchange) {
      boolean result = false;
      boolean abort = true;
      AutoLock l = this._lock.lock();

      try {
         if (this._exchange == null) {
            abort = false;
            result = exchange.associate(this);
            if (result) {
               this._exchange = exchange;
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
         exchange.getRequest().abort(new UnsupportedOperationException("Pipelined requests not supported"));
      } else if (LOG.isDebugEnabled()) {
         LOG.debug("{} associated {} to {}", new Object[]{exchange, result, this});
      }

      return result;
   }

   public boolean disassociate(HttpExchange exchange) {
      boolean result = false;
      AutoLock l = this._lock.lock();

      try {
         HttpExchange existing = this._exchange;
         this._exchange = null;
         if (existing == exchange) {
            existing.disassociate(this);
            result = true;
         }
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

      if (LOG.isDebugEnabled()) {
         LOG.debug("{} disassociated {} from {}", new Object[]{exchange, result, this});
      }

      return result;
   }

   public HttpExchange getHttpExchange() {
      AutoLock l = this._lock.lock();

      HttpExchange var2;
      try {
         var2 = this._exchange;
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
      HttpExchange exchange = this.getHttpExchange();
      return exchange != null ? exchange.getExpireNanoTime() : Long.MAX_VALUE;
   }

   protected abstract HttpSender getHttpSender();

   protected abstract HttpReceiver getHttpReceiver();

   public void send() {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange != null) {
         this.send(exchange);
      }
   }

   public abstract void send(HttpExchange var1);

   public abstract void release();

   public void proceed(HttpExchange exchange, Throwable failure) {
      this.getHttpSender().proceed(exchange, failure);
   }

   public void abort(HttpExchange exchange, Throwable requestFailure, Throwable responseFailure, Promise<Boolean> promise) {
      Completable<Boolean> requestPromise = new Completable();
      if (requestFailure != null) {
         this.getHttpSender().abort(exchange, requestFailure, requestPromise);
      } else {
         requestPromise.succeeded(false);
      }

      Completable<Boolean> responsePromise = new Completable();
      if (responseFailure != null) {
         this.abortResponse(exchange, responseFailure, responsePromise);
      } else {
         responsePromise.succeeded(false);
      }

      requestPromise.thenAcceptBoth(responsePromise, (requestAborted, responseAborted) -> promise.succeeded(requestAborted || responseAborted));
   }

   public void abortResponse(HttpExchange exchange, Throwable failure, Promise<Boolean> promise) {
      this.getHttpReceiver().abort(exchange, failure, promise);
   }

   public Result exchangeTerminating(HttpExchange exchange, Result result) {
      return result;
   }

   public void exchangeTerminated(HttpExchange exchange, Result result) {
      this.disassociate(exchange);
   }

   @Override
   public String toString() {
      return String.format("%s@%x(exchange=%s)", this.getClass().getSimpleName(), this.hashCode(), this.getHttpExchange());
   }
}
