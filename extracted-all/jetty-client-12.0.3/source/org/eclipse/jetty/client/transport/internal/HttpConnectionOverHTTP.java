package org.eclipse.jetty.client.transport.internal;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.client.Connection;
import org.eclipse.jetty.client.HttpUpgrader;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.client.internal.TunnelRequest;
import org.eclipse.jetty.client.transport.HttpChannel;
import org.eclipse.jetty.client.transport.HttpConnection;
import org.eclipse.jetty.client.transport.HttpConversation;
import org.eclipse.jetty.client.transport.HttpDestination;
import org.eclipse.jetty.client.transport.HttpExchange;
import org.eclipse.jetty.client.transport.HttpRequest;
import org.eclipse.jetty.client.transport.IConnection;
import org.eclipse.jetty.client.transport.SendFailure;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Connection.UpgradeFrom;
import org.eclipse.jetty.util.Attachable;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.thread.Sweeper.Sweepable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectionOverHTTP extends AbstractConnection implements IConnection, UpgradeFrom, Sweepable, Attachable {
   private static final Logger LOG = LoggerFactory.getLogger(HttpConnectionOverHTTP.class);
   private final AtomicBoolean closed = new AtomicBoolean();
   private final AtomicInteger sweeps = new AtomicInteger();
   private final Promise<Connection> promise;
   private final HttpConnectionOverHTTP.Delegate delegate;
   private final HttpChannelOverHTTP channel;
   private final LongAdder bytesIn = new LongAdder();
   private final LongAdder bytesOut = new LongAdder();
   private long idleTimeout;

   public HttpConnectionOverHTTP(EndPoint endPoint, Map<String, Object> context) {
      this(endPoint, destinationFrom(context), promiseFrom(context));
   }

   private static HttpDestination destinationFrom(Map<String, Object> context) {
      return (HttpDestination)context.get("org.eclipse.jetty.client.destination");
   }

   private static Promise<Connection> promiseFrom(Map<String, Object> context) {
      return (Promise<Connection>)context.get("org.eclipse.jetty.client.connection.promise");
   }

   public HttpConnectionOverHTTP(EndPoint endPoint, HttpDestination destination, Promise<Connection> promise) {
      super(endPoint, destination.getHttpClient().getExecutor());
      this.promise = promise;
      this.delegate = new HttpConnectionOverHTTP.Delegate(destination);
      this.channel = this.newHttpChannel();
   }

   protected HttpChannelOverHTTP newHttpChannel() {
      return new HttpChannelOverHTTP(this);
   }

   public HttpChannelOverHTTP getHttpChannel() {
      return this.channel;
   }

   public HttpDestination getHttpDestination() {
      return this.delegate.getHttpDestination();
   }

   public long getBytesIn() {
      return this.bytesIn.longValue();
   }

   protected void addBytesIn(long bytesIn) {
      this.bytesIn.add(bytesIn);
   }

   public long getBytesOut() {
      return this.bytesOut.longValue();
   }

   protected void addBytesOut(long bytesOut) {
      this.bytesOut.add(bytesOut);
   }

   public long getMessagesIn() {
      return this.getHttpChannel().getMessagesIn();
   }

   public long getMessagesOut() {
      return this.getHttpChannel().getMessagesOut();
   }

   @Override
   public void send(Request request, Response.CompleteListener listener) {
      this.delegate.send(request, listener);
   }

   @Override
   public SendFailure send(HttpExchange exchange) {
      return this.delegate.send(exchange);
   }

   public void onOpen() {
      super.onOpen();
      this.fillInterested();
      this.promise.succeeded(this);
   }

   @Override
   public boolean isClosed() {
      return this.closed.get();
   }

   public void setAttachment(Object obj) {
      this.delegate.setAttachment(obj);
   }

   public Object getAttachment() {
      return this.delegate.getAttachment();
   }

   public boolean onIdleExpired(TimeoutException timeout) {
      long idleTimeout = this.getEndPoint().getIdleTimeout();
      boolean close = this.onIdleTimeout(idleTimeout);
      if (close) {
         this.close(timeout);
      }

      return false;
   }

   protected boolean onIdleTimeout(long idleTimeout) {
      TimeoutException failure = new TimeoutException("Idle timeout " + idleTimeout + " ms");
      return this.delegate.onIdleTimeout(idleTimeout, failure);
   }

   public void onFillable() {
      this.channel.receive();
   }

   public ByteBuffer onUpgradeFrom() {
      HttpReceiverOverHTTP receiver = this.channel.getHttpReceiver();
      return receiver.onUpgradeFrom();
   }

   void onResponseHeaders(HttpExchange exchange) {
      HttpRequest request = exchange.getRequest();
      if (request instanceof TunnelRequest) {
         this.getEndPoint().setIdleTimeout(this.idleTimeout);
      }
   }

   public void release() {
      this.getEndPoint().setIdleTimeout(this.idleTimeout);
      this.getHttpDestination().release(this);
   }

   public void remove() {
      this.getHttpDestination().remove(this);
   }

   @Override
   public void close() {
      this.close(new AsynchronousCloseException());
   }

   protected void close(Throwable failure) {
      if (this.closed.compareAndSet(false, true)) {
         this.getHttpDestination().remove(this);
         this.abort(failure, Promise.noop());
         this.channel.destroy();
         this.getEndPoint().shutdownOutput();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Shutdown {}", this);
         }

         this.getEndPoint().close();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Closed {}", this);
         }

         this.delegate.destroy();
      }
   }

   protected void abort(Throwable failure, Promise<Boolean> promise) {
      HttpExchange exchange = this.channel.getHttpExchange();
      if (exchange != null) {
         promise.completeWith(exchange.getRequest().abort(failure));
      } else {
         promise.succeeded(false);
      }
   }

   public boolean sweep() {
      return !this.closed.get() ? false : this.sweeps.incrementAndGet() > 3;
   }

   public String toConnectionString() {
      return String.format(
         "%s@%x(l:%s <-> r:%s,closed=%b)=>%s",
         this.getClass().getSimpleName(),
         this.hashCode(),
         this.getEndPoint().getLocalSocketAddress(),
         this.getEndPoint().getRemoteSocketAddress(),
         this.closed.get(),
         this.channel
      );
   }

   private class Delegate extends HttpConnection {
      private Delegate(HttpDestination destination) {
         super(destination);
      }

      @Override
      protected Iterator<HttpChannel> getHttpChannels() {
         return Collections.singleton(HttpConnectionOverHTTP.this.channel).iterator();
      }

      @Override
      public SendFailure send(HttpExchange exchange) {
         HttpRequest request = exchange.getRequest();
         this.normalizeRequest(request);
         EndPoint endPoint = HttpConnectionOverHTTP.this.getEndPoint();
         HttpConnectionOverHTTP.this.idleTimeout = endPoint.getIdleTimeout();
         long requestIdleTimeout = request.getIdleTimeout();
         if (requestIdleTimeout >= 0L) {
            endPoint.setIdleTimeout(requestIdleTimeout);
         }

         return this.send(HttpConnectionOverHTTP.this.channel, exchange);
      }

      @Override
      protected void normalizeRequest(HttpRequest request) {
         super.normalizeRequest(request);
         if (request instanceof TunnelRequest) {
            request.idleTimeout(2L * this.getHttpClient().getConnectTimeout(), TimeUnit.MILLISECONDS);
         }

         HttpConversation conversation = request.getConversation();
         HttpUpgrader upgrader = (HttpUpgrader)conversation.getAttribute(HttpUpgrader.class.getName());
         if (upgrader == null) {
            HttpUpgrader.Factory upgraderFactory = (HttpUpgrader.Factory)request.getAttributes().get(HttpUpgrader.Factory.class.getName());
            if (upgraderFactory != null) {
               upgrader = upgraderFactory.newHttpUpgrader(HttpVersion.HTTP_1_1);
               conversation.setAttribute(HttpUpgrader.class.getName(), upgrader);
               upgrader.prepare(request);
            } else {
               String protocol = request.getHeaders().get(HttpHeader.UPGRADE);
               if (protocol != null) {
                  HttpUpgrader var7 = new ProtocolHttpUpgrader(this.getHttpDestination(), protocol);
                  conversation.setAttribute(HttpUpgrader.class.getName(), var7);
                  var7.prepare(request);
               }
            }
         }
      }

      @Override
      public void close() {
         HttpConnectionOverHTTP.this.close();
      }

      @Override
      public boolean isClosed() {
         return HttpConnectionOverHTTP.this.isClosed();
      }

      @Override
      public String toString() {
         return HttpConnectionOverHTTP.this.toString();
      }
   }
}
