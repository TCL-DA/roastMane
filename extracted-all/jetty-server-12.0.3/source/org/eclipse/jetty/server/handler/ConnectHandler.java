package org.eclipse.jetty.server.handler;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.SelectorManager;
import org.eclipse.jetty.io.SocketChannelEndPoint;
import org.eclipse.jetty.io.Connection.UpgradeTo;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpStream;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.TunnelSupport;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectHandler extends Handler.Wrapper {
   private static final Logger LOG = LoggerFactory.getLogger(ConnectHandler.class);
   private final Set<String> whiteList = new HashSet<>();
   private final Set<String> blackList = new HashSet<>();
   private Executor executor;
   private Scheduler scheduler;
   private ByteBufferPool bufferPool;
   private SelectorManager selector;
   private long connectTimeout = 15000L;
   private long idleTimeout = 30000L;
   private int bufferSize = 4096;

   public ConnectHandler() {
      this(null);
   }

   public ConnectHandler(Handler handler) {
      super(handler);
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public void setExecutor(Executor executor) {
      this.executor = executor;
   }

   public Scheduler getScheduler() {
      return this.scheduler;
   }

   public void setScheduler(Scheduler scheduler) {
      this.updateBean(this.scheduler, scheduler);
      this.scheduler = scheduler;
   }

   public ByteBufferPool getByteBufferPool() {
      return this.bufferPool;
   }

   public void setByteBufferPool(ByteBufferPool bufferPool) {
      this.updateBean(this.bufferPool, bufferPool);
      this.bufferPool = bufferPool;
   }

   public long getConnectTimeout() {
      return this.connectTimeout;
   }

   public void setConnectTimeout(long connectTimeout) {
      this.connectTimeout = connectTimeout;
   }

   public long getIdleTimeout() {
      return this.idleTimeout;
   }

   public void setIdleTimeout(long idleTimeout) {
      this.idleTimeout = idleTimeout;
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public void setBufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
   }

   @Override
   protected void doStart() throws Exception {
      if (this.executor == null) {
         this.executor = this.getServer().getThreadPool();
      }

      if (this.scheduler == null) {
         this.scheduler = this.getServer().getScheduler();
         this.addBean(this.scheduler);
      }

      if (this.bufferPool == null) {
         this.bufferPool = this.getServer().getByteBufferPool();
         this.addBean(this.bufferPool);
      }

      this.addBean(this.selector = this.newSelectorManager());
      this.selector.setConnectTimeout(this.getConnectTimeout());
      super.doStart();
   }

   protected SelectorManager newSelectorManager() {
      return new ConnectHandler.ConnectManager(this.getExecutor(), this.getScheduler(), 1);
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      if (HttpMethod.CONNECT.is(request.getMethod())) {
         TunnelSupport tunnelSupport = request.getTunnelSupport();
         if (tunnelSupport == null) {
            Response.writeError(request, response, callback, 501);
            return true;
         }

         if (tunnelSupport.getProtocol() == null) {
            HttpURI httpURI = request.getHttpURI();
            String serverAddress = httpURI.getAuthority();
            if (LOG.isDebugEnabled()) {
               LOG.debug("CONNECT request for {}", serverAddress);
            }

            this.handleConnect(request, response, callback, serverAddress);
            return true;
         }
      }

      return super.handle(request, response, callback);
   }

   protected void handleConnect(final Request request, final Response response, final Callback callback, String serverAddress) {
      try {
         boolean proceed = this.handleAuthentication(request, response, serverAddress);
         if (!proceed) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Missing proxy authentication");
            }

            this.sendConnectResponse(request, response, callback, 407);
            return;
         }

         HostPort hostPort = new HostPort(serverAddress);
         String host = hostPort.getHost();
         int port = hostPort.getPort(80);
         if (!this.validateDestination(host, port)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Destination {}:{} forbidden", host, port);
            }

            this.sendConnectResponse(request, response, callback, 403);
            return;
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Connecting to {}:{}", host, port);
         }

         this.connectToServer(
            request,
            host,
            port,
            new Promise<SocketChannel>() {
               public void succeeded(SocketChannel channel) {
                  ConnectHandler.ConnectContext connectContext = new ConnectHandler.ConnectContext(
                     request, response, callback, request.getTunnelSupport().getEndPoint()
                  );
                  if (channel.isConnected()) {
                     ConnectHandler.this.selector.accept(channel, connectContext);
                  } else {
                     ConnectHandler.this.selector.connect(channel, connectContext);
                  }
               }

               public void failed(Throwable x) {
                  ConnectHandler.this.onConnectFailure(request, response, callback, x);
               }
            }
         );
      } catch (Exception var9) {
         this.onConnectFailure(request, response, callback, var9);
      }
   }

   protected void connectToServer(Request request, String host, int port, Promise<SocketChannel> promise) {
      SocketChannel channel = null;

      try {
         channel = SocketChannel.open();
         channel.socket().setTcpNoDelay(true);
         channel.configureBlocking(false);
         InetSocketAddress address = this.newConnectAddress(host, port);
         channel.connect(address);
         promise.succeeded(channel);
      } catch (Throwable var7) {
         this.close(channel);
         promise.failed(var7);
      }
   }

   private void close(Closeable closeable) {
      try {
         if (closeable != null) {
            closeable.close();
         }
      } catch (Throwable var3) {
         LOG.trace("IGNORED", var3);
      }
   }

   protected InetSocketAddress newConnectAddress(String host, int port) {
      return new InetSocketAddress(host, port);
   }

   protected void onConnectSuccess(ConnectHandler.ConnectContext connectContext, ConnectHandler.UpstreamConnection upstreamConnection) {
      ConcurrentMap<String, Object> context = connectContext.getContext();
      Request request = connectContext.getRequest();
      this.prepareContext(request, context);
      EndPoint downstreamEndPoint = connectContext.getEndPoint();
      ConnectHandler.DownstreamConnection downstreamConnection = this.newDownstreamConnection(downstreamEndPoint, context);
      downstreamConnection.setInputBufferSize(this.getBufferSize());
      upstreamConnection.setConnection(downstreamConnection);
      downstreamConnection.setConnection(upstreamConnection);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Connection setup completed: {}<->{}", downstreamConnection, upstreamConnection);
      }

      Response response = connectContext.getResponse();
      this.upgradeConnection(request, downstreamConnection);
      this.sendConnectResponse(request, response, connectContext.callback, 200);
   }

   protected void onConnectFailure(Request request, Response response, Callback callback, Throwable failure) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("CONNECT failed", failure);
      }

      this.sendConnectResponse(request, response, callback, 500);
   }

   private void sendConnectResponse(Request request, Response response, Callback callback, int statusCode) {
      try {
         response.getHeaders().put(HttpFields.CONTENT_LENGTH_0);
         if (statusCode != 200) {
            response.getHeaders().put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE);
            Response.writeError(request, response, callback, statusCode);
         } else {
            response.setStatus(200);
            callback.succeeded();
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("CONNECT response sent {} {}", request.getConnectionMetaData().getProtocol(), statusCode);
         }
      } catch (Throwable var6) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Could not send CONNECT response", var6);
         }
      }
   }

   protected boolean handleAuthentication(Request request, Response response, String address) {
      return true;
   }

   protected ConnectHandler.DownstreamConnection newDownstreamConnection(EndPoint endPoint, ConcurrentMap<String, Object> context) {
      return new ConnectHandler.DownstreamConnection(endPoint, this.getExecutor(), this.getByteBufferPool(), context);
   }

   protected ConnectHandler.UpstreamConnection newUpstreamConnection(EndPoint endPoint, ConnectHandler.ConnectContext connectContext) {
      return new ConnectHandler.UpstreamConnection(endPoint, this.getExecutor(), this.getByteBufferPool(), connectContext);
   }

   protected void prepareContext(Request request, ConcurrentMap<String, Object> context) {
   }

   private void upgradeConnection(Request request, Connection connection) {
      request.setAttribute(HttpStream.UPGRADE_CONNECTION_ATTRIBUTE, connection);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Upgraded connection to {}", connection);
      }
   }

   protected int read(EndPoint endPoint, ByteBuffer buffer, ConcurrentMap<String, Object> context) throws IOException {
      return endPoint.fill(buffer);
   }

   protected void write(EndPoint endPoint, ByteBuffer buffer, Callback callback, ConcurrentMap<String, Object> context) {
      endPoint.write(callback, new ByteBuffer[]{buffer});
   }

   public Set<String> getWhiteListHosts() {
      return this.whiteList;
   }

   public Set<String> getBlackListHosts() {
      return this.blackList;
   }

   public boolean validateDestination(String host, int port) {
      String hostPort = host + ":" + port;
      if (!this.whiteList.isEmpty() && !this.whiteList.contains(hostPort)) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Host {}:{} not whitelisted", host, port);
         }

         return false;
      } else if (!this.blackList.isEmpty() && this.blackList.contains(hostPort)) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Host {}:{} blacklisted", host, port);
         }

         return false;
      } else {
         return true;
      }
   }

   protected static class ConnectContext {
      private final ConcurrentMap<String, Object> context = new ConcurrentHashMap<>();
      private final Request request;
      private final Response response;
      private final Callback callback;
      private final EndPoint endPoint;

      public ConnectContext(Request request, Response response, Callback callback, EndPoint endPoint) {
         this.request = request;
         this.response = response;
         this.callback = callback;
         this.endPoint = endPoint;
      }

      public ConcurrentMap<String, Object> getContext() {
         return this.context;
      }

      public Request getRequest() {
         return this.request;
      }

      public Response getResponse() {
         return this.response;
      }

      public Callback getCallback() {
         return this.callback;
      }

      public EndPoint getEndPoint() {
         return this.endPoint;
      }
   }

   protected class ConnectManager extends SelectorManager {
      protected ConnectManager(Executor executor, Scheduler scheduler, int selectors) {
         super(executor, scheduler, selectors);
      }

      protected EndPoint newEndPoint(SelectableChannel channel, ManagedSelector selector, SelectionKey key) {
         SocketChannelEndPoint endPoint = new SocketChannelEndPoint((SocketChannel)channel, selector, key, this.getScheduler());
         endPoint.setIdleTimeout(ConnectHandler.this.getIdleTimeout());
         return endPoint;
      }

      public Connection newConnection(SelectableChannel channel, EndPoint endpoint, Object attachment) throws IOException {
         if (ConnectHandler.LOG.isDebugEnabled()) {
            ConnectHandler.LOG.debug("Connected to {}", ((SocketChannel)channel).getRemoteAddress());
         }

         ConnectHandler.ConnectContext connectContext = (ConnectHandler.ConnectContext)attachment;
         ConnectHandler.UpstreamConnection connection = ConnectHandler.this.newUpstreamConnection(endpoint, connectContext);
         connection.setInputBufferSize(ConnectHandler.this.getBufferSize());
         return connection;
      }

      protected void connectionFailed(SelectableChannel channel, Throwable ex, Object attachment) {
         ConnectHandler.this.close(channel);
         ConnectHandler.ConnectContext connectContext = (ConnectHandler.ConnectContext)attachment;
         ConnectHandler.this.onConnectFailure(connectContext.request, connectContext.response, connectContext.callback, ex);
      }
   }

   public class DownstreamConnection extends ConnectHandler.TunnelConnection implements UpgradeTo {
      private ByteBuffer buffer;

      public DownstreamConnection(EndPoint endPoint, Executor executor, ByteBufferPool bufferPool, ConcurrentMap<String, Object> context) {
         super(endPoint, executor, bufferPool, context);
      }

      public void onUpgradeTo(ByteBuffer buffer) {
         this.buffer = buffer;
      }

      public void onOpen() {
         super.onOpen();
         if (this.buffer == null) {
            this.fillInterested();
         } else {
            final int remaining = this.buffer.remaining();
            this.write(this.getConnection().getEndPoint(), this.buffer, new Callback() {
               public void succeeded() {
                  DownstreamConnection.this.buffer = null;
                  if (ConnectHandler.LOG.isDebugEnabled()) {
                     ConnectHandler.LOG.debug("Wrote initial {} bytes to server {}", remaining, DownstreamConnection.this);
                  }

                  DownstreamConnection.this.fillInterested();
               }

               public void failed(Throwable x) {
                  DownstreamConnection.this.buffer = null;
                  if (ConnectHandler.LOG.isDebugEnabled()) {
                     ConnectHandler.LOG.debug("Failed to write initial {} bytes to server {}", new Object[]{remaining, DownstreamConnection.this, x});
                  }

                  DownstreamConnection.this.close();
                  DownstreamConnection.this.getConnection().close();
               }
            });
         }
      }

      @Override
      protected int read(EndPoint endPoint, ByteBuffer buffer) throws IOException {
         int read = ConnectHandler.this.read(endPoint, buffer, this.getContext());
         if (ConnectHandler.LOG.isDebugEnabled()) {
            ConnectHandler.LOG.debug("Read {} bytes from client {}", read, this);
         }

         return read;
      }

      @Override
      protected void write(EndPoint endPoint, ByteBuffer buffer, Callback callback) {
         if (ConnectHandler.LOG.isDebugEnabled()) {
            ConnectHandler.LOG.debug("Writing {} bytes to server {}", buffer.remaining(), this);
         }

         ConnectHandler.this.write(endPoint, buffer, callback, this.getContext());
      }
   }

   private abstract static class TunnelConnection extends AbstractConnection {
      private final IteratingCallback pipe = new ConnectHandler.TunnelConnection.ProxyIteratingCallback();
      private final ByteBufferPool bufferPool;
      private final ConcurrentMap<String, Object> context;
      private ConnectHandler.TunnelConnection connection;

      protected TunnelConnection(EndPoint endPoint, Executor executor, ByteBufferPool bufferPool, ConcurrentMap<String, Object> context) {
         super(endPoint, executor);
         this.bufferPool = bufferPool;
         this.context = context;
      }

      public ByteBufferPool getByteBufferPool() {
         return this.bufferPool;
      }

      public ConcurrentMap<String, Object> getContext() {
         return this.context;
      }

      public Connection getConnection() {
         return this.connection;
      }

      public void setConnection(ConnectHandler.TunnelConnection connection) {
         this.connection = connection;
      }

      public void onFillable() {
         this.pipe.iterate();
      }

      protected abstract int read(EndPoint var1, ByteBuffer var2) throws IOException;

      protected abstract void write(EndPoint var1, ByteBuffer var2, Callback var3);

      protected void close(Throwable failure) {
         this.getEndPoint().close(failure);
      }

      public String toConnectionString() {
         EndPoint endPoint = this.getEndPoint();
         return String.format(
            "%s@%x[l:%s<=>r:%s]", this.getClass().getSimpleName(), this.hashCode(), endPoint.getLocalSocketAddress(), endPoint.getRemoteSocketAddress()
         );
      }

      private class ProxyIteratingCallback extends IteratingCallback {
         private RetainableByteBuffer buffer;
         private int filled;

         protected Action process() {
            this.buffer = TunnelConnection.this.bufferPool.acquire(TunnelConnection.this.getInputBufferSize(), true);

            try {
               ByteBuffer byteBuffer = this.buffer.getByteBuffer();
               int filled = this.filled = TunnelConnection.this.read(TunnelConnection.this.getEndPoint(), byteBuffer);
               if (filled > 0) {
                  TunnelConnection.this.write(TunnelConnection.this.connection.getEndPoint(), byteBuffer, this);
                  return Action.SCHEDULED;
               } else if (filled == 0) {
                  this.buffer.release();
                  TunnelConnection.this.fillInterested();
                  return Action.IDLE;
               } else {
                  this.buffer.release();
                  TunnelConnection.this.connection.getEndPoint().shutdownOutput();
                  return Action.SUCCEEDED;
               }
            } catch (IOException var3) {
               if (ConnectHandler.LOG.isDebugEnabled()) {
                  ConnectHandler.LOG.debug("Could not fill {}", TunnelConnection.this, var3);
               }

               this.buffer.release();
               this.disconnect(var3);
               return Action.SUCCEEDED;
            }
         }

         public void succeeded() {
            if (ConnectHandler.LOG.isDebugEnabled()) {
               ConnectHandler.LOG.debug("Wrote {} bytes {}", this.filled, TunnelConnection.this);
            }

            this.buffer.release();
            super.succeeded();
         }

         protected void onCompleteSuccess() {
         }

         protected void onCompleteFailure(Throwable x) {
            if (ConnectHandler.LOG.isDebugEnabled()) {
               ConnectHandler.LOG.debug("Failed to write {} bytes {}", new Object[]{this.filled, TunnelConnection.this, x});
            }

            this.buffer.release();
            this.disconnect(x);
         }

         private void disconnect(Throwable x) {
            TunnelConnection.this.close(x);
            TunnelConnection.this.connection.close(x);
         }
      }
   }

   public class UpstreamConnection extends ConnectHandler.TunnelConnection {
      private final ConnectHandler.ConnectContext connectContext;

      public UpstreamConnection(EndPoint endPoint, Executor executor, ByteBufferPool bufferPool, ConnectHandler.ConnectContext connectContext) {
         super(endPoint, executor, bufferPool, connectContext.getContext());
         this.connectContext = connectContext;
      }

      public void onOpen() {
         super.onOpen();
         ConnectHandler.this.onConnectSuccess(this.connectContext, this);
         this.fillInterested();
      }

      @Override
      protected int read(EndPoint endPoint, ByteBuffer buffer) throws IOException {
         int read = ConnectHandler.this.read(endPoint, buffer, this.getContext());
         if (ConnectHandler.LOG.isDebugEnabled()) {
            ConnectHandler.LOG.debug("Read {} bytes from server {}", read, this);
         }

         return read;
      }

      @Override
      protected void write(EndPoint endPoint, ByteBuffer buffer, Callback callback) {
         if (ConnectHandler.LOG.isDebugEnabled()) {
            ConnectHandler.LOG.debug("Writing {} bytes to client {}", buffer.remaining(), this);
         }

         ConnectHandler.this.write(endPoint, buffer, callback, this.getContext());
      }
   }
}
