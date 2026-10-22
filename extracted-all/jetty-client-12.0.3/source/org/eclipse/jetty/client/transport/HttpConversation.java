package org.eclipse.jetty.client.transport;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.Attributes.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConversation extends Lazy {
   private static final Logger LOG = LoggerFactory.getLogger(HttpConversation.class);
   private final Deque<HttpExchange> exchanges = new ConcurrentLinkedDeque<>();
   private volatile ResponseListeners listeners;

   public Deque<HttpExchange> getExchanges() {
      return this.exchanges;
   }

   public ResponseListeners getResponseListeners() {
      return this.listeners;
   }

   public void updateResponseListeners(Response.Listener overrideListener) {
      HttpExchange firstExchange = this.exchanges.peekFirst();
      HttpExchange lastExchange = this.exchanges.peekLast();
      ResponseListeners listeners;
      if (firstExchange == lastExchange) {
         if (overrideListener != null) {
            listeners = new ResponseListeners(overrideListener);
         } else {
            listeners = new ResponseListeners(firstExchange.getResponseListeners());
         }
      } else {
         listeners = new ResponseListeners(lastExchange.getResponseListeners());
         if (overrideListener != null) {
            listeners.addListener(overrideListener);
         } else {
            listeners.addResponseListeners(firstExchange.getResponseListeners());
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Exchanges in conversation {}, override={}, listeners={}", new Object[]{this.exchanges.size(), overrideListener, listeners});
      }

      this.listeners = listeners;
   }

   public long getTimeout() {
      HttpExchange firstExchange = this.exchanges.peekFirst();
      return firstExchange == null ? 0L : firstExchange.getRequest().getTimeout();
   }

   public void abort(Throwable cause, Promise<Boolean> promise) {
      HttpExchange exchange = this.exchanges.peekLast();
      if (exchange != null) {
         exchange.abort(cause, promise);
      } else {
         promise.succeeded(false);
      }
   }

   public String toString() {
      return String.format("%s[%x]", HttpConversation.class.getSimpleName(), this.hashCode());
   }
}
