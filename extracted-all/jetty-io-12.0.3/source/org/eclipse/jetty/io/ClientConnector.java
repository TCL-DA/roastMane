package org.eclipse.jetty.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.JavaVersion;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory.Client;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class ClientConnector extends ContainerLifeCycle {
   public static final String CLIENT_CONNECTOR_CONTEXT_KEY = "org.eclipse.jetty.client.connector";
   public static final String REMOTE_SOCKET_ADDRESS_CONTEXT_KEY = "org.eclipse.jetty.client.connector.remoteSocketAddress";
   public static final String CLIENT_CONNECTION_FACTORY_CONTEXT_KEY = "org.eclipse.jetty.client.connector.clientConnectionFactory";
   public static final String CONNECTION_PROMISE_CONTEXT_KEY = "org.eclipse.jetty.client.connector.connectionPromise";
   public static final String APPLICATION_PROTOCOLS_CONTEXT_KEY = "org.eclipse.jetty.client.connector.applicationProtocols";
   private static final Logger LOG = LoggerFactory.getLogger(ClientConnector.class);
   private final ClientConnector.Configurator configurator;
   private Executor executor;
   private Scheduler scheduler;
   private ByteBufferPool byteBufferPool;
   private Client sslContextFactory;
   private SelectorManager selectorManager;
   private int selectors = 1;
   private boolean connectBlocking;
   private Duration connectTimeout = Duration.ofSeconds(5L);
   private Duration idleTimeout = Duration.ofSeconds(30L);
   private SocketAddress bindAddress;
   private boolean tcpNoDelay = true;
   private boolean reuseAddress = true;
   private boolean reusePort;
   private int receiveBufferSize = -1;
   private int sendBufferSize = -1;

   public static ClientConnector forUnixDomain(Path path) {
      return new ClientConnector(ClientConnector.Configurator.forUnixDomain(path));
   }

   public ClientConnector() {
      this(new ClientConnector.Configurator());
   }

   public ClientConnector(ClientConnector.Configurator configurator) {
      this.configurator = Objects.requireNonNull(configurator);
      this.addBean(configurator);
      configurator.addBean(this, false);
   }

   public boolean isIntrinsicallySecure(SocketAddress address) {
      return this.configurator.isIntrinsicallySecure(this, address);
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public void setExecutor(Executor executor) {
      if (this.isStarted()) {
         throw new IllegalStateException();
      } else {
         this.updateBean(this.executor, executor);
         this.executor = executor;
      }
   }

   public Scheduler getScheduler() {
      return this.scheduler;
   }

   public void setScheduler(Scheduler scheduler) {
      if (this.isStarted()) {
         throw new IllegalStateException();
      } else {
         this.updateBean(this.scheduler, scheduler);
         this.scheduler = scheduler;
      }
   }

   public ByteBufferPool getByteBufferPool() {
      return this.byteBufferPool;
   }

   public void setByteBufferPool(ByteBufferPool byteBufferPool) {
      if (this.isStarted()) {
         throw new IllegalStateException();
      } else {
         this.updateBean(this.byteBufferPool, byteBufferPool);
         this.byteBufferPool = byteBufferPool;
      }
   }

   public Client getSslContextFactory() {
      return this.sslContextFactory;
   }

   public void setSslContextFactory(Client sslContextFactory) {
      if (this.isStarted()) {
         throw new IllegalStateException();
      } else {
         this.updateBean(this.sslContextFactory, sslContextFactory);
         this.sslContextFactory = sslContextFactory;
      }
   }

   @ManagedAttribute("The number of NIO selectors")
   public int getSelectors() {
      return this.selectors;
   }

   public void setSelectors(int selectors) {
      if (this.isStarted()) {
         throw new IllegalStateException();
      } else {
         this.selectors = selectors;
      }
   }

   @ManagedAttribute("Whether connect operations are performed in blocking mode")
   public boolean isConnectBlocking() {
      return this.connectBlocking;
   }

   public void setConnectBlocking(boolean connectBlocking) {
      this.connectBlocking = connectBlocking;
   }

   @ManagedAttribute("The timeout of connect operations")
   public Duration getConnectTimeout() {
      return this.connectTimeout;
   }

   public void setConnectTimeout(Duration connectTimeout) {
      this.connectTimeout = connectTimeout;
      if (this.selectorManager != null) {
         this.selectorManager.setConnectTimeout(connectTimeout.toMillis());
      }
   }

   @ManagedAttribute("The duration for which a connection can be idle")
   public Duration getIdleTimeout() {
      return this.idleTimeout;
   }

   public void setIdleTimeout(Duration idleTimeout) {
      this.idleTimeout = idleTimeout;
   }

   @ManagedAttribute("The socket address to bind sockets to before the connect operation")
   public SocketAddress getBindAddress() {
      return this.bindAddress;
   }

   public void setBindAddress(SocketAddress bindAddress) {
      this.bindAddress = bindAddress;
   }

   @ManagedAttribute("Whether small TCP packets are sent without delay")
   public boolean isTCPNoDelay() {
      return this.tcpNoDelay;
   }

   public void setTCPNoDelay(boolean tcpNoDelay) {
      this.tcpNoDelay = tcpNoDelay;
   }

   @ManagedAttribute("Whether rebinding is allowed with sockets in tear-down states")
   public boolean getReuseAddress() {
      return this.reuseAddress;
   }

   public void setReuseAddress(boolean reuseAddress) {
      this.reuseAddress = reuseAddress;
   }

   @ManagedAttribute("Whether binding to same host and port is allowed")
   public boolean isReusePort() {
      return this.reusePort;
   }

   public void setReusePort(boolean reusePort) {
      this.reusePort = reusePort;
   }

   @ManagedAttribute("The receive buffer size in bytes")
   public int getReceiveBufferSize() {
      return this.receiveBufferSize;
   }

   public void setReceiveBufferSize(int receiveBufferSize) {
      this.receiveBufferSize = receiveBufferSize;
   }

   @ManagedAttribute("The send buffer size in bytes")
   public int getSendBufferSize() {
      return this.sendBufferSize;
   }

   public void setSendBufferSize(int sendBufferSize) {
      this.sendBufferSize = sendBufferSize;
   }

   protected void doStart() throws Exception {
      if (this.executor == null) {
         QueuedThreadPool clientThreads = new QueuedThreadPool();
         clientThreads.setName(String.format("client-pool@%x", this.hashCode()));
         this.setExecutor(clientThreads);
      }

      if (this.scheduler == null) {
         this.setScheduler(new ScheduledExecutorScheduler(String.format("client-scheduler@%x", this.hashCode()), false));
      }

      if (this.byteBufferPool == null) {
         this.setByteBufferPool(new ArrayByteBufferPool());
      }

      if (this.sslContextFactory == null) {
         this.setSslContextFactory(this.newSslContextFactory());
      }

      this.selectorManager = this.newSelectorManager();
      this.selectorManager.setConnectTimeout(this.getConnectTimeout().toMillis());
      this.addBean(this.selectorManager);
      super.doStart();
   }

   protected void doStop() throws Exception {
      super.doStop();
      this.removeBean(this.selectorManager);
   }

   protected Client newSslContextFactory() {
      Client sslContextFactory = new Client(false);
      sslContextFactory.setEndpointIdentificationAlgorithm("HTTPS");
      return sslContextFactory;
   }

   protected SelectorManager newSelectorManager() {
      return new ClientConnector.ClientSelectorManager(this.getExecutor(), this.getScheduler(), this.getSelectors());
   }

   public void connect(SocketAddress address, Map<String, Object> context) {
      SelectableChannel channel = null;

      try {
         if (context == null) {
            context = new ConcurrentHashMap<>();
         }

         context.put("org.eclipse.jetty.client.connector", this);
         context.putIfAbsent("org.eclipse.jetty.client.connector.remoteSocketAddress", address);
         ClientConnector.Configurator.ChannelWithAddress channelWithAddress = this.configurator.newChannelWithAddress(this, address, context);
         channel = channelWithAddress.getSelectableChannel();
         address = channelWithAddress.getSocketAddress();
         this.configure(channel);
         SocketAddress bindAddress = this.getBindAddress();
         if (bindAddress != null && channel instanceof NetworkChannel) {
            this.bind((NetworkChannel)channel, bindAddress);
         }

         boolean connected = true;
         if (!(channel instanceof SocketChannel socketChannel)) {
            channel.configureBlocking(false);
         } else {
            boolean blocking = this.isConnectBlocking() && address instanceof InetSocketAddress;
            if (LOG.isDebugEnabled()) {
               LOG.debug("Connecting {} to {}", blocking ? "blocking" : "non-blocking", address);
            }

            if (blocking) {
               socketChannel.socket().connect(address, (int)this.getConnectTimeout().toMillis());
               socketChannel.configureBlocking(false);
            } else {
               socketChannel.configureBlocking(false);
               connected = socketChannel.connect(address);
            }
         }

         if (connected) {
            this.selectorManager.accept(channel, context);
         } else {
            this.selectorManager.connect(channel, context);
         }
      } catch (Throwable var9) {
         Throwable x = var9;
         if (var9.getClass() == SocketException.class) {
            x = new SocketException("Could not connect to " + address).initCause(var9);
         }

         IO.close(channel);
         this.connectFailed(x, context);
      }
   }

   public void accept(SelectableChannel selectable, Map<String, Object> context) {
      try {
         SocketChannel channel = (SocketChannel)selectable;
         context.put("org.eclipse.jetty.client.connector", this);
         if (!channel.isConnected()) {
            throw new IllegalStateException("SocketChannel must be connected");
         }

         this.configure(channel);
         channel.configureBlocking(false);
         this.selectorManager.accept(channel, context);
      } catch (Throwable var5) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Could not accept {}", selectable);
         }

         IO.close(selectable);
         Promise<?> promise = (Promise<?>)context.get("org.eclipse.jetty.client.connector.connectionPromise");
         if (promise != null) {
            promise.failed(var5);
         }
      }
   }

   private void bind(NetworkChannel channel, SocketAddress bindAddress) throws IOException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Binding {} to {}", channel, bindAddress);
      }

      channel.bind(bindAddress);
   }

   protected void configure(SelectableChannel selectable) throws IOException {
      if (selectable instanceof NetworkChannel channel) {
         this.setSocketOption(channel, StandardSocketOptions.TCP_NODELAY, this.isTCPNoDelay());
         this.setSocketOption(channel, StandardSocketOptions.SO_REUSEADDR, this.getReuseAddress());
         this.setSocketOption(channel, StandardSocketOptions.SO_REUSEPORT, this.isReusePort());
         int receiveBufferSize = this.getReceiveBufferSize();
         if (receiveBufferSize >= 0) {
            this.setSocketOption(channel, StandardSocketOptions.SO_RCVBUF, receiveBufferSize);
         }

         int sendBufferSize = this.getSendBufferSize();
         if (sendBufferSize >= 0) {
            this.setSocketOption(channel, StandardSocketOptions.SO_SNDBUF, sendBufferSize);
         }
      }
   }

   private <T> void setSocketOption(NetworkChannel channel, SocketOption<T> option, T value) {
      try {
         channel.setOption(option, value);
      } catch (Throwable var5) {
         if (LOG.isTraceEnabled()) {
            LOG.trace("Could not configure {} to {} on {}", new Object[]{option, value, channel, var5});
         }
      }
   }

   protected EndPoint newEndPoint(SelectableChannel selectable, ManagedSelector selector, SelectionKey selectionKey) {
      Map<String, Object> context = (Map<String, Object>)selectionKey.attachment();
      SocketAddress address = (SocketAddress)context.get("org.eclipse.jetty.client.connector.remoteSocketAddress");
      return this.configurator.newEndPoint(this, address, selectable, selector, selectionKey);
   }

   protected Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
      SocketAddress address = (SocketAddress)context.get("org.eclipse.jetty.client.connector.remoteSocketAddress");
      return this.configurator.newConnection(this, address, endPoint, context);
   }

   protected void connectFailed(Throwable failure, Map<String, Object> context) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Could not connect to {}", context.get("org.eclipse.jetty.client.connector.remoteSocketAddress"));
      }

      Promise<?> promise = (Promise<?>)context.get("org.eclipse.jetty.client.connector.connectionPromise");
      if (promise != null) {
         promise.failed(failure);
      }
   }

   protected class ClientSelectorManager extends SelectorManager {
      public ClientSelectorManager(Executor executor, Scheduler scheduler, int selectors) {
         super(executor, scheduler, selectors);
      }

      @Override
      protected EndPoint newEndPoint(SelectableChannel channel, ManagedSelector selector, SelectionKey selectionKey) {
         EndPoint endPoint = ClientConnector.this.newEndPoint(channel, selector, selectionKey);
         endPoint.setIdleTimeout(ClientConnector.this.getIdleTimeout().toMillis());
         return endPoint;
      }

      @Override
      public Connection newConnection(SelectableChannel channel, EndPoint endPoint, Object attachment) throws IOException {
         Map<String, Object> context = (Map<String, Object>)attachment;
         return ClientConnector.this.newConnection(endPoint, context);
      }

      @Override
      public void connectionOpened(Connection connection, Object context) {
         super.connectionOpened(connection, context);
         Map<String, Object> contextMap = (Map<String, Object>)context;
         Promise<Connection> promise = (Promise<Connection>)contextMap.get("org.eclipse.jetty.client.connector.connectionPromise");
         if (promise != null) {
            promise.succeeded(connection);
         }
      }

      @Override
      protected void connectionFailed(SelectableChannel channel, Throwable failure, Object attachment) {
         Map<String, Object> context = (Map<String, Object>)attachment;
         ClientConnector.this.connectFailed(failure, context);
      }
   }

   public static class Configurator extends ContainerLifeCycle {
      public boolean isIntrinsicallySecure(ClientConnector clientConnector, SocketAddress address) {
         return false;
      }

      public ClientConnector.Configurator.ChannelWithAddress newChannelWithAddress(
         ClientConnector clientConnector, SocketAddress address, Map<String, Object> context
      ) throws IOException {
         return new ClientConnector.Configurator.ChannelWithAddress(SocketChannel.open(), address);
      }

      public EndPoint newEndPoint(
         ClientConnector clientConnector, SocketAddress address, SelectableChannel selectable, ManagedSelector selector, SelectionKey selectionKey
      ) {
         return new SocketChannelEndPoint((SocketChannel)selectable, selector, selectionKey, clientConnector.getScheduler());
      }

      public Connection newConnection(ClientConnector clientConnector, SocketAddress address, EndPoint endPoint, Map<String, Object> context) throws IOException {
         ClientConnectionFactory factory = (ClientConnectionFactory)context.get("org.eclipse.jetty.client.connector.clientConnectionFactory");
         return factory.newConnection(endPoint, context);
      }

      private static ClientConnector.Configurator forUnixDomain(final Path path) {
         return new ClientConnector.Configurator() {
            @Override
            public ClientConnector.Configurator.ChannelWithAddress newChannelWithAddress(
               ClientConnector clientConnector, SocketAddress address, Map<String, Object> context
            ) {
               try {
                  ProtocolFamily family = Enum.valueOf(StandardProtocolFamily.class, "UNIX");
                  SocketChannel socketChannel = (SocketChannel)SocketChannel.class.getMethod("open", ProtocolFamily.class).invoke(null, family);
                  Class<?> addressClass = Class.forName("java.net.UnixDomainSocketAddress");
                  SocketAddress socketAddress = (SocketAddress)addressClass.getMethod("of", Path.class).invoke(null, path);
                  return new ClientConnector.Configurator.ChannelWithAddress(socketChannel, socketAddress);
               } catch (Throwable var8) {
                  String message = "Unix-Domain SocketChannels are available starting from Java 16, your Java version is: " + JavaVersion.VERSION;
                  throw new UnsupportedOperationException(message, var8);
               }
            }
         };
      }

      public static class ChannelWithAddress {
         private final SelectableChannel channel;
         private final SocketAddress address;

         public ChannelWithAddress(SelectableChannel channel, SocketAddress address) {
            this.channel = channel;
            this.address = address;
         }

         public SelectableChannel getSelectableChannel() {
            return this.channel;
         }

         public SocketAddress getSocketAddress() {
            return this.address;
         }
      }
   }
}
