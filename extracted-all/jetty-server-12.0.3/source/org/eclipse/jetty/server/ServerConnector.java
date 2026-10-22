package org.eclipse.jetty.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.Channel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.EventListener;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.io.SelectorManager;
import org.eclipse.jetty.io.SocketChannelEndPoint;
import org.eclipse.jetty.io.SelectorManager.SelectorManagerListener;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.thread.Scheduler;

@ManagedObject("HTTP connector using NIO ByteChannels and Selectors")
public class ServerConnector extends AbstractNetworkConnector {
   private final SelectorManager _manager;
   private final AtomicReference<Closeable> _acceptor = new AtomicReference<>();
   private volatile ServerSocketChannel _acceptChannel;
   private volatile boolean _inheritChannel = false;
   private volatile int _localPort = -1;
   private volatile int _acceptQueueSize = 0;
   private volatile boolean _reuseAddress = true;
   private volatile boolean _reusePort = false;
   private volatile boolean _acceptedTcpNoDelay = true;
   private volatile int _acceptedReceiveBufferSize = -1;
   private volatile int _acceptedSendBufferSize = -1;

   public ServerConnector(@Name("server") Server server) {
      this(server, null, null, null, -1, -1, new HttpConnectionFactory());
   }

   public ServerConnector(@Name("server") Server server, @Name("acceptors") int acceptors, @Name("selectors") int selectors) {
      this(server, null, null, null, acceptors, selectors, new HttpConnectionFactory());
   }

   public ServerConnector(
      @Name("server") Server server, @Name("acceptors") int acceptors, @Name("selectors") int selectors, @Name("factories") ConnectionFactory... factories
   ) {
      this(server, null, null, null, acceptors, selectors, factories);
   }

   public ServerConnector(@Name("server") Server server, @Name("factories") ConnectionFactory... factories) {
      this(server, null, null, null, -1, -1, factories);
   }

   public ServerConnector(@Name("server") Server server, @Name("sslContextFactory") org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory) {
      this(server, null, null, null, -1, -1, AbstractConnectionFactory.getFactories(sslContextFactory, new HttpConnectionFactory()));
   }

   public ServerConnector(
      @Name("server") Server server,
      @Name("acceptors") int acceptors,
      @Name("selectors") int selectors,
      @Name("sslContextFactory") org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory
   ) {
      this(server, null, null, null, acceptors, selectors, AbstractConnectionFactory.getFactories(sslContextFactory, new HttpConnectionFactory()));
   }

   public ServerConnector(
      @Name("server") Server server,
      @Name("sslContextFactory") org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory,
      @Name("factories") ConnectionFactory... factories
   ) {
      this(server, null, null, null, -1, -1, AbstractConnectionFactory.getFactories(sslContextFactory, factories));
   }

   public ServerConnector(
      @Name("server") Server server,
      @Name("executor") Executor executor,
      @Name("scheduler") Scheduler scheduler,
      @Name("bufferPool") ByteBufferPool bufferPool,
      @Name("acceptors") int acceptors,
      @Name("selectors") int selectors,
      @Name("factories") ConnectionFactory... factories
   ) {
      super(server, executor, scheduler, bufferPool, acceptors, factories);
      this._manager = this.newSelectorManager(this.getExecutor(), this.getScheduler(), selectors);
      this.addBean(this._manager, true);
      this.setAcceptorPriorityDelta(-2);
   }

   protected SelectorManager newSelectorManager(Executor executor, Scheduler scheduler, int selectors) {
      return new ServerConnector.ServerConnectorManager(executor, scheduler, selectors);
   }

   @Override
   protected void doStart() throws Exception {
      for (EventListener l : this.getBeans(SelectorManagerListener.class)) {
         this._manager.addEventListener(l);
      }

      super.doStart();
      if (this.getAcceptors() == 0) {
         this._acceptChannel.configureBlocking(false);
         this._acceptor.set(this._manager.acceptor(this._acceptChannel));
      }
   }

   @Override
   protected void doStop() throws Exception {
      super.doStop();

      for (EventListener l : this.getBeans(EventListener.class)) {
         this._manager.removeEventListener(l);
      }
   }

   @Override
   public boolean isOpen() {
      ServerSocketChannel channel = this._acceptChannel;
      return channel != null && channel.isOpen();
   }

   public boolean isInheritChannel() {
      return this._inheritChannel;
   }

   public void setInheritChannel(boolean inheritChannel) {
      this._inheritChannel = inheritChannel;
   }

   public void open(ServerSocketChannel acceptChannel) throws IOException {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this.updateBean(this._acceptChannel, acceptChannel);
         this._acceptChannel = acceptChannel;
         this._localPort = this._acceptChannel.socket().getLocalPort();
         if (this._localPort <= 0) {
            throw new IOException("Server channel not bound");
         }
      }
   }

   @Override
   public void open() throws IOException {
      if (this._acceptChannel == null) {
         this._acceptChannel = this.openAcceptChannel();
         this._acceptChannel.configureBlocking(true);
         this._localPort = this._acceptChannel.socket().getLocalPort();
         if (this._localPort <= 0) {
            throw new IOException("Server channel not bound");
         }

         this.addBean(this._acceptChannel);
      }
   }

   protected ServerSocketChannel openAcceptChannel() throws IOException {
      ServerSocketChannel serverChannel = null;
      if (this.isInheritChannel()) {
         Channel channel = System.inheritedChannel();
         if (channel instanceof ServerSocketChannel) {
            serverChannel = (ServerSocketChannel)channel;
         } else {
            LOG.warn(
               "Unable to use System.inheritedChannel() [{}]. Trying a new ServerSocketChannel at {}:{}", new Object[]{channel, this.getHost(), this.getPort()}
            );
         }
      }

      if (serverChannel == null) {
         InetSocketAddress bindAddress = this.getHost() == null ? new InetSocketAddress(this.getPort()) : new InetSocketAddress(this.getHost(), this.getPort());
         serverChannel = ServerSocketChannel.open();
         this.setSocketOption(serverChannel, StandardSocketOptions.SO_REUSEADDR, this.getReuseAddress());
         this.setSocketOption(serverChannel, StandardSocketOptions.SO_REUSEPORT, this.isReusePort());

         try {
            serverChannel.bind(bindAddress, this.getAcceptQueueSize());
         } catch (Throwable var4) {
            IO.close(serverChannel);
            throw new IOException("Failed to bind to " + bindAddress, var4);
         }
      }

      return serverChannel;
   }

   private <T> void setSocketOption(ServerSocketChannel channel, SocketOption<T> option, T value) {
      try {
         channel.setOption(option, value);
      } catch (Throwable var5) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Could not configure {} to {} on {}", new Object[]{option, value, channel, var5});
         }
      }
   }

   @Override
   public void close() {
      super.close();
      ServerSocketChannel serverChannel = this._acceptChannel;
      this._acceptChannel = null;
      if (serverChannel != null) {
         this.removeBean(serverChannel);
         if (serverChannel.isOpen()) {
            try {
               serverChannel.close();
            } catch (IOException var3) {
               LOG.warn("Unable to close {}", serverChannel, var3);
            }
         }
      }

      this._localPort = -2;
   }

   @Override
   public void accept(int acceptorID) throws IOException {
      ServerSocketChannel serverChannel = this._acceptChannel;
      if (serverChannel != null && serverChannel.isOpen()) {
         SocketChannel channel = serverChannel.accept();
         this.accepted(channel);
      }
   }

   private void accepted(SocketChannel channel) throws IOException {
      channel.configureBlocking(false);
      Socket socket = channel.socket();
      this.configure(socket);
      this._manager.accept(channel);
   }

   protected void configure(Socket socket) {
      try {
         socket.setTcpNoDelay(this._acceptedTcpNoDelay);
         if (this._acceptedReceiveBufferSize > -1) {
            socket.setReceiveBufferSize(this._acceptedReceiveBufferSize);
         }

         if (this._acceptedSendBufferSize > -1) {
            socket.setSendBufferSize(this._acceptedSendBufferSize);
         }
      } catch (SocketException var3) {
         LOG.trace("IGNORED", var3);
      }
   }

   @ManagedAttribute("The Selector Manager")
   public SelectorManager getSelectorManager() {
      return this._manager;
   }

   @Override
   public Object getTransport() {
      return this._acceptChannel;
   }

   @ManagedAttribute("local port")
   @Override
   public int getLocalPort() {
      return this._localPort;
   }

   protected SocketChannelEndPoint newEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey key) throws IOException {
      SocketChannelEndPoint endpoint = new SocketChannelEndPoint(channel, selectSet, key, this.getScheduler());
      endpoint.setIdleTimeout(this.getIdleTimeout());
      return endpoint;
   }

   @ManagedAttribute("Accept Queue size")
   public int getAcceptQueueSize() {
      return this._acceptQueueSize;
   }

   public void setAcceptQueueSize(int acceptQueueSize) {
      this._acceptQueueSize = acceptQueueSize;
   }

   @ManagedAttribute("Server Socket SO_REUSEADDR")
   public boolean getReuseAddress() {
      return this._reuseAddress;
   }

   public void setReuseAddress(boolean reuseAddress) {
      this._reuseAddress = reuseAddress;
   }

   @ManagedAttribute("Server Socket SO_REUSEPORT")
   public boolean isReusePort() {
      return this._reusePort;
   }

   public void setReusePort(boolean reusePort) {
      this._reusePort = reusePort;
   }

   @ManagedAttribute("Accepted Socket TCP_NODELAY")
   public boolean getAcceptedTcpNoDelay() {
      return this._acceptedTcpNoDelay;
   }

   public void setAcceptedTcpNoDelay(boolean tcpNoDelay) {
      this._acceptedTcpNoDelay = tcpNoDelay;
   }

   @ManagedAttribute("Accepted Socket SO_RCVBUF")
   public int getAcceptedReceiveBufferSize() {
      return this._acceptedReceiveBufferSize;
   }

   public void setAcceptedReceiveBufferSize(int receiveBufferSize) {
      this._acceptedReceiveBufferSize = receiveBufferSize;
   }

   @ManagedAttribute("Accepted Socket SO_SNDBUF")
   public int getAcceptedSendBufferSize() {
      return this._acceptedSendBufferSize;
   }

   public void setAcceptedSendBufferSize(int sendBufferSize) {
      this._acceptedSendBufferSize = sendBufferSize;
   }

   @Override
   public void setAccepting(boolean accepting) {
      super.setAccepting(accepting);
      if (this.getAcceptors() <= 0) {
         try {
            if (accepting) {
               if (this._acceptor.get() == null) {
                  Closeable acceptor = this._manager.acceptor(this._acceptChannel);
                  if (!this._acceptor.compareAndSet(null, acceptor)) {
                     acceptor.close();
                  }
               }
            } else {
               Closeable acceptor = this._acceptor.get();
               if (acceptor != null && this._acceptor.compareAndSet(acceptor, null)) {
                  acceptor.close();
               }
            }
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   protected class ServerConnectorManager extends SelectorManager {
      public ServerConnectorManager(Executor executor, Scheduler scheduler, int selectors) {
         super(executor, scheduler, selectors);
      }

      protected void accepted(SelectableChannel channel) throws IOException {
         ServerConnector.this.accepted((SocketChannel)channel);
      }

      protected SocketChannelEndPoint newEndPoint(SelectableChannel channel, ManagedSelector selector, SelectionKey selectionKey) throws IOException {
         return ServerConnector.this.newEndPoint((SocketChannel)channel, selector, selectionKey);
      }

      public Connection newConnection(SelectableChannel channel, EndPoint endpoint, Object attachment) throws IOException {
         return ServerConnector.this.getDefaultConnectionFactory().newConnection(ServerConnector.this, endpoint);
      }

      protected void endPointOpened(EndPoint endpoint) {
         super.endPointOpened(endpoint);
         ServerConnector.this.onEndPointOpened(endpoint);
      }

      protected void endPointClosed(EndPoint endpoint) {
         ServerConnector.this.onEndPointClosed(endpoint);
         super.endPointClosed(endpoint);
      }

      public String toString() {
         return String.format("SelectorManager@%s", ServerConnector.this);
      }
   }
}
