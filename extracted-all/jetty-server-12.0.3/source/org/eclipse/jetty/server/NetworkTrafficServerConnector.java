package org.eclipse.jetty.server;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.io.NetworkTrafficListener;
import org.eclipse.jetty.io.NetworkTrafficSocketChannelEndPoint;
import org.eclipse.jetty.io.SocketChannelEndPoint;
import org.eclipse.jetty.util.thread.Scheduler;

public class NetworkTrafficServerConnector extends ServerConnector {
   private volatile NetworkTrafficListener listener;

   public NetworkTrafficServerConnector(Server server) {
      this(server, null, null, null, 0, 0, new HttpConnectionFactory());
   }

   public NetworkTrafficServerConnector(
      Server server, ConnectionFactory connectionFactory, org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory
   ) {
      super(server, sslContextFactory, connectionFactory);
   }

   public NetworkTrafficServerConnector(Server server, ConnectionFactory connectionFactory) {
      super(server, connectionFactory);
   }

   public NetworkTrafficServerConnector(
      Server server, Executor executor, Scheduler scheduler, ByteBufferPool bufferPool, int acceptors, int selectors, ConnectionFactory... factories
   ) {
      super(server, executor, scheduler, bufferPool, acceptors, selectors, factories);
   }

   public NetworkTrafficServerConnector(Server server, org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory) {
      super(server, sslContextFactory);
   }

   public void setNetworkTrafficListener(NetworkTrafficListener listener) {
      this.listener = listener;
   }

   public NetworkTrafficListener getNetworkTrafficListener() {
      return this.listener;
   }

   @Override
   protected SocketChannelEndPoint newEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey key) {
      return new NetworkTrafficSocketChannelEndPoint(channel, selectSet, key, this.getScheduler(), this.getIdleTimeout(), this.getNetworkTrafficListener());
   }
}
