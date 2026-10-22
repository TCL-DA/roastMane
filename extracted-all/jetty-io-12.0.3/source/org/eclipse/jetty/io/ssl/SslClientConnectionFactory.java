package org.eclipse.jetty.io.ssl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class SslClientConnectionFactory implements ClientConnectionFactory {
   public static final String SSL_ENGINE_CONTEXT_KEY = "org.eclipse.jetty.client.ssl.engine";
   private final SslContextFactory sslContextFactory;
   private final ByteBufferPool byteBufferPool;
   private final Executor executor;
   private final ClientConnectionFactory connectionFactory;
   private boolean _directBuffersForEncryption = true;
   private boolean _directBuffersForDecryption = true;
   private boolean _requireCloseMessage;

   public SslClientConnectionFactory(
      SslContextFactory sslContextFactory, ByteBufferPool byteBufferPool, Executor executor, ClientConnectionFactory connectionFactory
   ) {
      this.sslContextFactory = Objects.requireNonNull(sslContextFactory, "Missing SslContextFactory");
      this.byteBufferPool = byteBufferPool;
      this.executor = executor;
      this.connectionFactory = connectionFactory;
   }

   public ClientConnectionFactory getClientConnectionFactory() {
      return this.connectionFactory;
   }

   public void setDirectBuffersForEncryption(boolean useDirectBuffers) {
      this._directBuffersForEncryption = useDirectBuffers;
   }

   public void setDirectBuffersForDecryption(boolean useDirectBuffers) {
      this._directBuffersForDecryption = useDirectBuffers;
   }

   public boolean isDirectBuffersForDecryption() {
      return this._directBuffersForDecryption;
   }

   public boolean isDirectBuffersForEncryption() {
      return this._directBuffersForEncryption;
   }

   public boolean isRequireCloseMessage() {
      return this._requireCloseMessage;
   }

   public void setRequireCloseMessage(boolean requireCloseMessage) {
      this._requireCloseMessage = requireCloseMessage;
   }

   @Override
   public Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
      SocketAddress remote = (SocketAddress)context.get("org.eclipse.jetty.client.connector.remoteSocketAddress");
      SSLEngine engine;
      if (remote instanceof InetSocketAddress inetRemote) {
         String host = inetRemote.getHostString();
         int port = inetRemote.getPort();
         engine = this.sslContextFactory instanceof SslClientConnectionFactory.SslEngineFactory
            ? ((SslClientConnectionFactory.SslEngineFactory)this.sslContextFactory).newSslEngine(host, port, context)
            : this.sslContextFactory.newSSLEngine(host, port);
      } else {
         engine = this.sslContextFactory.newSSLEngine();
      }

      engine.setUseClientMode(true);
      context.put("org.eclipse.jetty.client.ssl.engine", engine);
      SslConnection sslConnection = this.newSslConnection(this.byteBufferPool, this.executor, endPoint, engine);
      EndPoint appEndPoint = sslConnection.getSslEndPoint();
      appEndPoint.setConnection(this.connectionFactory.newConnection(appEndPoint, context));
      sslConnection.addHandshakeListener(new SslClientConnectionFactory.HTTPSHandshakeListener(context));
      this.customize(sslConnection, context);
      return sslConnection;
   }

   protected SslConnection newSslConnection(ByteBufferPool byteBufferPool, Executor executor, EndPoint endPoint, SSLEngine engine) {
      return new SslConnection(byteBufferPool, executor, endPoint, engine, this.isDirectBuffersForEncryption(), this.isDirectBuffersForDecryption());
   }

   @Override
   public Connection customize(Connection connection, Map<String, Object> context) {
      if (connection instanceof SslConnection sslConnection) {
         sslConnection.setRenegotiationAllowed(this.sslContextFactory.isRenegotiationAllowed());
         sslConnection.setRenegotiationLimit(this.sslContextFactory.getRenegotiationLimit());
         sslConnection.setRequireCloseMessage(this.isRequireCloseMessage());
         ContainerLifeCycle client = (ContainerLifeCycle)context.get("org.eclipse.jetty.client");
         if (client != null) {
            client.getBeans(SslHandshakeListener.class).forEach(sslConnection::addHandshakeListener);
         }
      }

      return ClientConnectionFactory.super.customize(connection, context);
   }

   private class HTTPSHandshakeListener implements SslHandshakeListener {
      private final Map<String, Object> context;

      private HTTPSHandshakeListener(Map<String, Object> context) {
         this.context = context;
      }

      @Override
      public void handshakeSucceeded(SslHandshakeListener.Event event) throws SSLException {
         HostnameVerifier verifier = SslClientConnectionFactory.this.sslContextFactory.getHostnameVerifier();
         if (verifier != null) {
            SocketAddress address = (SocketAddress)this.context.get("org.eclipse.jetty.client.connector.remoteSocketAddress");
            if (address instanceof InetSocketAddress) {
               String host = ((InetSocketAddress)address).getHostString();

               try {
                  if (!verifier.verify(host, event.getSSLEngine().getSession())) {
                     throw new SSLPeerUnverifiedException("Host name verification failed for host: " + host);
                  }
               } catch (SSLException var6) {
                  throw var6;
               } catch (Throwable var7) {
                  throw (SSLException)new SSLPeerUnverifiedException("Host name verification failed for host: " + host).initCause(var7);
               }
            }
         }
      }
   }

   public interface SslEngineFactory {
      SSLEngine newSslEngine(String var1, int var2, Map<String, Object> var3);
   }
}
