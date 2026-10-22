package org.eclipse.jetty.client.transport;

import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.Connection;
import org.eclipse.jetty.client.ConnectionPool;
import org.eclipse.jetty.client.Destination;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.ProxyConfiguration;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.CyclicTimeouts;
import org.eclipse.jetty.io.ClientConnectionFactory.Decorator;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory.Client;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.Sweeper;
import org.eclipse.jetty.util.thread.Sweeper.Sweepable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class HttpDestination extends ContainerLifeCycle implements Destination, Callback, Dumpable, Sweepable {
   private static final Logger LOG = LoggerFactory.getLogger(HttpDestination.class);
   private final HttpClient client;
   private final Origin origin;
   private final Queue<HttpExchange> exchanges;
   private final ProxyConfiguration.Proxy proxy;
   private final ClientConnectionFactory connectionFactory;
   private final HttpField hostField;
   private final HttpDestination.RequestTimeouts requestTimeouts;
   private final AutoLock staleLock = new AutoLock();
   private ConnectionPool connectionPool;
   private boolean stale;
   private long activeNanoTime;

   public HttpDestination(HttpClient client, Origin origin, boolean intrinsicallySecure) {
      this.client = client;
      this.origin = origin;
      this.exchanges = this.newExchangeQueue(client);
      this.requestTimeouts = new HttpDestination.RequestTimeouts(client.getScheduler());
      String host = HostPort.normalizeHost(this.getHost());
      int port = this.getPort();
      if (port != HttpScheme.getDefaultPort(this.getScheme())) {
         host = host + ":" + port;
      }

      this.hostField = new HttpField(HttpHeader.HOST, host);
      ProxyConfiguration proxyConfig = client.getProxyConfiguration();
      this.proxy = proxyConfig.match(origin);
      ClientConnectionFactory connectionFactory = client.getTransport();
      if (this.proxy != null) {
         connectionFactory = this.proxy.newClientConnectionFactory(connectionFactory);
         if (!intrinsicallySecure && this.proxy.isSecure()) {
            connectionFactory = this.newSslClientConnectionFactory(this.proxy.getSslContextFactory(), connectionFactory);
         }
      } else if (!intrinsicallySecure && this.isSecure()) {
         connectionFactory = this.newSslClientConnectionFactory(null, connectionFactory);
      }

      Object tag = origin.getTag();
      if (tag instanceof Decorator) {
         connectionFactory = ((Decorator)tag).apply(connectionFactory);
      }

      this.connectionFactory = connectionFactory;
   }

   public void accept(Connection connection) {
      this.connectionPool.accept(connection);
   }

   public boolean stale() {
      AutoLock ignored = this.staleLock.lock();

      boolean var3;
      try {
         boolean stale = this.stale;
         if (!stale) {
            this.activeNanoTime = NanoTime.now();
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Stale check done with result {} on {}", stale, this);
         }

         var3 = stale;
      } catch (Throwable var5) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var3;
   }

   public boolean sweep() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Sweep check in progress on {}", this);
      }

      boolean remove = false;
      AutoLock ignored = this.staleLock.lock();

      try {
         boolean stale = this.exchanges.isEmpty() && this.connectionPool.isEmpty();
         if (!stale) {
            this.activeNanoTime = NanoTime.now();
         } else if (NanoTime.millisSince(this.activeNanoTime) >= this.getHttpClient().getDestinationIdleTimeout()) {
            this.stale = true;
            remove = true;
         }
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }

      if (remove) {
         this.getHttpClient().removeDestination(this);
         LifeCycle.stop(this);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Sweep check done with result {} on {}", remove, this);
      }

      return remove;
   }

   protected void doStart() throws Exception {
      this.connectionPool = this.newConnectionPool(this.client);
      this.addBean(this.connectionPool, true);
      super.doStart();
      Sweeper connectionPoolSweeper = (Sweeper)this.client.getBean(Sweeper.class);
      if (connectionPoolSweeper != null && this.connectionPool instanceof Sweepable) {
         connectionPoolSweeper.offer((Sweepable)this.connectionPool);
      }
   }

   protected void doStop() throws Exception {
      this.requestTimeouts.destroy();
      this.abort(new AsynchronousCloseException());
      Sweeper connectionPoolSweeper = (Sweeper)this.client.getBean(Sweeper.class);
      if (connectionPoolSweeper != null && this.connectionPool instanceof Sweepable) {
         connectionPoolSweeper.remove((Sweepable)this.connectionPool);
      }

      super.doStop();
      this.removeBean(this.connectionPool);
   }

   protected ConnectionPool newConnectionPool(HttpClient client) {
      return client.getTransport().getConnectionPoolFactory().newConnectionPool(this);
   }

   protected Queue<HttpExchange> newExchangeQueue(HttpClient client) {
      int maxCapacity = client.getMaxRequestsQueuedPerDestination();
      return maxCapacity > 32 ? new BlockingArrayQueue(32, 32, maxCapacity) : new BlockingArrayQueue(maxCapacity);
   }

   private ClientConnectionFactory newSslClientConnectionFactory(Client sslContextFactory, ClientConnectionFactory connectionFactory) {
      return this.client.newSslClientConnectionFactory(sslContextFactory, connectionFactory);
   }

   @Override
   public boolean isSecure() {
      return HttpScheme.isSecure(this.getScheme());
   }

   @Override
   public HttpClient getHttpClient() {
      return this.client;
   }

   @Override
   public Origin getOrigin() {
      return this.origin;
   }

   public Queue<HttpExchange> getHttpExchanges() {
      return this.exchanges;
   }

   @Override
   public ProxyConfiguration.Proxy getProxy() {
      return this.proxy;
   }

   public ClientConnectionFactory getClientConnectionFactory() {
      return this.connectionFactory;
   }

   @ManagedAttribute(
      value = "The destination scheme",
      readonly = true
   )
   public String getScheme() {
      return this.getOrigin().getScheme();
   }

   @ManagedAttribute(
      value = "The destination host",
      readonly = true
   )
   public String getHost() {
      return this.getOrigin().getAddress().getHost();
   }

   @ManagedAttribute(
      value = "The destination port",
      readonly = true
   )
   public int getPort() {
      return this.getOrigin().getAddress().getPort();
   }

   @ManagedAttribute(
      value = "The number of queued requests",
      readonly = true
   )
   public int getQueuedRequestCount() {
      return this.exchanges.size();
   }

   public HttpField getHostField() {
      return this.hostField;
   }

   @ManagedAttribute(
      value = "The connection pool",
      readonly = true
   )
   @Override
   public ConnectionPool getConnectionPool() {
      return this.connectionPool;
   }

   public void succeeded() {
      this.send(false);
   }

   public void failed(Throwable x) {
      this.abort(x);
   }

   @Override
   public void send(Request request, Response.CompleteListener listener) {
      ((HttpRequest)request).sendAsync(this, listener);
   }

   void send(HttpRequest request) {
      this.send(new HttpExchange(this, request));
   }

   public void send(HttpExchange exchange) {
      HttpRequest request = exchange.getRequest();
      if (this.client.isRunning()) {
         if (this.enqueue(this.exchanges, exchange)) {
            request.sent();
            this.requestTimeouts.schedule(exchange);
            if (!this.client.isRunning() && this.exchanges.remove(exchange)) {
               request.abort(new RejectedExecutionException(this.client + " is stopping"));
            } else {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Queued {} for {}", request, this);
               }

               request.notifyQueued();
               this.send();
            }
         } else {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Max queue size {} exceeded by {} for {}", new Object[]{this.client.getMaxRequestsQueuedPerDestination(), request, this});
            }

            request.abort(
               new RejectedExecutionException(
                  "Max requests queued per destination " + this.client.getMaxRequestsQueuedPerDestination() + " exceeded for " + this
               )
            );
         }
      } else {
         request.abort(new RejectedExecutionException(this.client + " is stopped"));
      }
   }

   protected boolean enqueue(Queue<HttpExchange> queue, HttpExchange exchange) {
      return queue.offer(exchange);
   }

   public void send() {
      this.send(true);
   }

   private void send(boolean create) {
      if (!this.getHttpExchanges().isEmpty()) {
         this.process(create);
      }
   }

   private void process(boolean create) {
      while (true) {
         Connection connection = this.connectionPool.acquire(create);
         if (connection != null) {
            boolean proceed = this.process(connection);
            if (proceed) {
               create = false;
               continue;
            }
         }

         return;
      }
   }

   private boolean process(Connection connection) {
      HttpClient client = this.getHttpClient();
      HttpExchange exchange = this.getHttpExchanges().poll();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Processing exchange {} on {} of {}", new Object[]{exchange, connection, this});
      }

      if (exchange == null) {
         if (!this.connectionPool.release(connection)) {
            connection.close();
         }

         if (!client.isRunning()) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} is stopping", client);
            }

            connection.close();
         }

         return false;
      } else {
         Request request = exchange.getRequest();
         Throwable cause = request.getAbortCause();
         if (cause != null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Aborted before processing {}: {}", exchange, cause);
            }

            boolean released = this.connectionPool.release(connection);
            if (!released) {
               connection.close();
            }

            exchange.abort(cause, Promise.noop());
            return this.getQueuedRequestCount() > 0;
         } else {
            SendFailure failure = this.send((IConnection)connection, exchange);
            if (failure == null) {
               return this.getQueuedRequestCount() > 0;
            } else {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Send failed {} for {}", failure, exchange);
               }

               if (failure.retry) {
                  this.send(exchange);
                  return false;
               } else {
                  request.abort(failure.failure);
                  return this.getQueuedRequestCount() > 0;
               }
            }
         }
      }
   }

   protected SendFailure send(IConnection connection, HttpExchange exchange) {
      return connection.send(exchange);
   }

   @Override
   public void newConnection(Promise<Connection> promise) {
      this.createConnection(promise);
   }

   protected void createConnection(Promise<Connection> promise) {
      this.client.newConnection(this, promise);
   }

   public boolean remove(HttpExchange exchange) {
      return this.exchanges.remove(exchange);
   }

   public void release(Connection connection) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Released {}", connection);
      }

      HttpClient client = this.getHttpClient();
      if (client.isRunning()) {
         if (this.connectionPool.isActive(connection)) {
            if (this.connectionPool.release(connection)) {
               this.send(false);
            } else {
               connection.close();
               this.send(true);
            }
         } else if (LOG.isDebugEnabled()) {
            LOG.debug("Released explicit {}", connection);
         }
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} is stopped", client);
         }

         connection.close();
      }
   }

   public boolean remove(Connection connection) {
      boolean removed = this.connectionPool.remove(connection);
      if (removed) {
         this.send(true);
      }

      return removed;
   }

   public void abort(Throwable cause) {
      for (HttpExchange exchange : new ArrayList<>(this.exchanges)) {
         exchange.getRequest().abort(cause);
      }
   }

   public void dump(Appendable out, String indent) throws IOException {
      this.dumpObjects(out, indent, new Object[]{new DumpableCollection("exchanges", this.exchanges)});
   }

   public String asString() {
      return this.getOrigin().asString();
   }

   @ManagedAttribute("For how long this destination has been idle in ms")
   public long getIdle() {
      if (this.getHttpClient().getDestinationIdleTimeout() <= 0L) {
         return -1L;
      } else {
         AutoLock ignored = this.staleLock.lock();

         long var2;
         try {
            var2 = NanoTime.millisSince(this.activeNanoTime);
         } catch (Throwable var5) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var2;
      }
   }

   @ManagedAttribute("Whether this destinations is stale")
   public boolean isStale() {
      AutoLock ignored = this.staleLock.lock();

      boolean var2;
      try {
         var2 = this.stale;
      } catch (Throwable var5) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var2;
   }

   @Override
   public String toString() {
      return String.format(
         "%s[%s]@%x%s,state=%s,queue=%d,pool=%s,stale=%b,idle=%d",
         HttpDestination.class.getSimpleName(),
         this.getOrigin(),
         this.hashCode(),
         this.proxy == null ? "" : "(via " + this.proxy + ")",
         this.getState(),
         this.getQueuedRequestCount(),
         this.getConnectionPool(),
         this.isStale(),
         this.getIdle()
      );
   }

   private class RequestTimeouts extends CyclicTimeouts<HttpExchange> {
      private RequestTimeouts(Scheduler scheduler) {
         super(scheduler);
      }

      protected Iterator<HttpExchange> iterator() {
         return HttpDestination.this.exchanges.iterator();
      }

      protected boolean onExpired(HttpExchange exchange) {
         HttpRequest request = exchange.getRequest();
         request.abort(new TimeoutException("Total timeout " + request.getConversation().getTimeout() + " ms elapsed"));
         return false;
      }
   }
}
