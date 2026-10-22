package org.eclipse.jetty.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.io.ssl.SslHandshakeListener;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

public class SslConnectionFactory extends AbstractConnectionFactory implements ConnectionFactory.Detecting, ConnectionFactory.Configuring {
   private static final int TLS_ALERT_FRAME_TYPE = 21;
   private static final int TLS_HANDSHAKE_FRAME_TYPE = 22;
   private static final int TLS_MAJOR_VERSION = 3;
   private final org.eclipse.jetty.util.ssl.SslContextFactory.Server _sslContextFactory;
   private final String _nextProtocol;
   private boolean _directBuffersForEncryption = false;
   private boolean _directBuffersForDecryption = false;
   private boolean _ensureSecureRequestCustomizer = true;

   public SslConnectionFactory() {
      this(HttpVersion.HTTP_1_1.asString());
   }

   public SslConnectionFactory(@Name("next") String nextProtocol) {
      this(null, nextProtocol);
   }

   public SslConnectionFactory(@Name("sslContextFactory") org.eclipse.jetty.util.ssl.SslContextFactory.Server factory, @Name("next") String nextProtocol) {
      super("SSL");
      this._sslContextFactory = factory == null ? new org.eclipse.jetty.util.ssl.SslContextFactory.Server() : factory;
      this._nextProtocol = nextProtocol;
      this.addBean(this._sslContextFactory);
   }

   public org.eclipse.jetty.util.ssl.SslContextFactory.Server getSslContextFactory() {
      return this._sslContextFactory;
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

   public String getNextProtocol() {
      return this._nextProtocol;
   }

   public boolean isEnsureSecureRequestCustomizer() {
      return this._ensureSecureRequestCustomizer;
   }

   public void setEnsureSecureRequestCustomizer(boolean ensureSecureRequestCustomizer) {
      this._ensureSecureRequestCustomizer = ensureSecureRequestCustomizer;
   }

   protected void doStart() throws Exception {
      super.doStart();
      SSLEngine engine = this._sslContextFactory.newSSLEngine();
      engine.setUseClientMode(false);
      SSLSession session = engine.getSession();
      if (session.getPacketBufferSize() > this.getInputBufferSize()) {
         this.setInputBufferSize(session.getPacketBufferSize());
      }
   }

   @Override
   public void configure(Connector connector) {
      if (this.isEnsureSecureRequestCustomizer()) {
         connector.getContainedBeans(HttpConfiguration.class).forEach(configuration -> {
            if (configuration.getCustomizer(SecureRequestCustomizer.class) == null) {
               configuration.addCustomizer(new SecureRequestCustomizer());
            }
         });
      }
   }

   @Override
   public ConnectionFactory.Detecting.Detection detect(ByteBuffer buffer) {
      if (buffer.remaining() < 2) {
         return ConnectionFactory.Detecting.Detection.NEED_MORE_BYTES;
      } else {
         int tlsFrameType = buffer.get(0) & 255;
         int tlsMajorVersion = buffer.get(1) & 255;
         boolean seemsSsl = (tlsFrameType == 22 || tlsFrameType == 21) && tlsMajorVersion == 3;
         return seemsSsl ? ConnectionFactory.Detecting.Detection.RECOGNIZED : ConnectionFactory.Detecting.Detection.NOT_RECOGNIZED;
      }
   }

   @Override
   public Connection newConnection(Connector connector, EndPoint endPoint) {
      SocketAddress remoteSocketAddress = endPoint.getRemoteSocketAddress();
      SSLEngine engine = remoteSocketAddress instanceof InetSocketAddress
         ? this._sslContextFactory.newSSLEngine((InetSocketAddress)remoteSocketAddress)
         : this._sslContextFactory.newSSLEngine();
      engine.setUseClientMode(false);
      SslConnection sslConnection = this.newSslConnection(connector, endPoint, engine);
      sslConnection.setRenegotiationAllowed(this._sslContextFactory.isRenegotiationAllowed());
      sslConnection.setRenegotiationLimit(this._sslContextFactory.getRenegotiationLimit());
      this.configure(sslConnection, connector, endPoint);
      ConnectionFactory next = connector.getConnectionFactory(this._nextProtocol);
      EndPoint sslEndPoint = sslConnection.getSslEndPoint();
      Connection connection = next.newConnection(connector, sslEndPoint);
      sslEndPoint.setConnection(connection);
      return sslConnection;
   }

   protected SslConnection newSslConnection(Connector connector, EndPoint endPoint, SSLEngine engine) {
      ByteBufferPool byteBufferPool = connector.getByteBufferPool();
      return new SslConnection(
         byteBufferPool, connector.getExecutor(), endPoint, engine, this.isDirectBuffersForEncryption(), this.isDirectBuffersForDecryption()
      );
   }

   @Override
   protected AbstractConnection configure(AbstractConnection connection, Connector connector, EndPoint endPoint) {
      if (connection instanceof SslConnection sslConnection) {
         if (connector instanceof ContainerLifeCycle container) {
            container.getBeans(SslHandshakeListener.class).forEach(sslConnection::addHandshakeListener);
         }

         this.getBeans(SslHandshakeListener.class).forEach(sslConnection::addHandshakeListener);
      }

      return super.configure(connection, connector, endPoint);
   }

   @Override
   public String toString() {
      return String.format("%s@%x{%s->%s}", this.getClass().getSimpleName(), this.hashCode(), this.getProtocol(), this._nextProtocol);
   }
}
