package org.apache.logging.log4j.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.Nullable;

public class SslSocketManager extends TcpSocketManager {
   public static final int DEFAULT_PORT = 6514;
   private static final SslSocketManager.SslSocketManagerFactory FACTORY = new SslSocketManager.SslSocketManagerFactory();
   private final SslConfiguration sslConfig;

   @Deprecated
   public SslSocketManager(
      final String name,
      final OutputStream os,
      final Socket sock,
      final SslConfiguration sslConfig,
      final InetAddress inetAddress,
      final String host,
      final int port,
      final int connectTimeoutMillis,
      final int reconnectionDelayMillis,
      final boolean immediateFail,
      final Layout<? extends Serializable> layout,
      final int bufferSize
   ) {
      super(name, os, sock, inetAddress, host, port, connectTimeoutMillis, reconnectionDelayMillis, immediateFail, layout, bufferSize, null);
      this.sslConfig = sslConfig;
   }

   public SslSocketManager(
      final String name,
      final OutputStream os,
      final Socket sock,
      final SslConfiguration sslConfig,
      final InetAddress inetAddress,
      final String host,
      final int port,
      final int connectTimeoutMillis,
      final int reconnectionDelayMillis,
      final boolean immediateFail,
      final Layout<? extends Serializable> layout,
      final int bufferSize,
      final SocketOptions socketOptions
   ) {
      super(name, os, sock, inetAddress, host, port, connectTimeoutMillis, reconnectionDelayMillis, immediateFail, layout, bufferSize, socketOptions);
      this.sslConfig = sslConfig;
   }

   @Deprecated
   public static SslSocketManager getSocketManager(
      final SslConfiguration sslConfig,
      final String host,
      final int port,
      final int connectTimeoutMillis,
      final int reconnectDelayMillis,
      final boolean immediateFail,
      final Layout<? extends Serializable> layout,
      final int bufferSize
   ) {
      return getSocketManager(sslConfig, host, port, connectTimeoutMillis, reconnectDelayMillis, immediateFail, layout, bufferSize, null);
   }

   public static SslSocketManager getSocketManager(
      final SslConfiguration sslConfig,
      final String host,
      int port,
      final int connectTimeoutMillis,
      int reconnectDelayMillis,
      final boolean immediateFail,
      final Layout<? extends Serializable> layout,
      final int bufferSize,
      final SocketOptions socketOptions
   ) {
      if (Strings.isEmpty(host)) {
         throw new IllegalArgumentException("A host name is required");
      } else {
         if (port <= 0) {
            port = 6514;
         }

         if (reconnectDelayMillis == 0) {
            reconnectDelayMillis = 30000;
         }

         String sslConfigId = createSslConfigurationId(sslConfig);
         String name = String.format("%s:%s:%d:%s", sslConfig.getProtocol(), host, port, sslConfigId);
         return (SslSocketManager)getManager(
            name,
            new SslSocketManager.SslFactoryData(
               sslConfig, host, port, connectTimeoutMillis, reconnectDelayMillis, immediateFail, layout, bufferSize, socketOptions
            ),
            FACTORY
         );
      }
   }

   private static String createSslConfigurationId(final SslConfiguration sslConfig) {
      return String.valueOf(Stream.of(sslConfig.getKeyStoreConfig(), sslConfig.getTrustStoreConfig()).filter(Objects::nonNull).flatMap(keyStoreConfig -> {
         Enumeration<String> aliases;
         try {
            aliases = keyStoreConfig.getKeyStore().aliases();
         } catch (KeyStoreException var3) {
            LOGGER.error("Failed reading the aliases for the key store located at `{}`", keyStoreConfig.getLocation(), var3);
            return Stream.empty();
         }

         return Collections.list(aliases).stream().sorted().flatMap(alias -> {
            X509Certificate certificate;
            try {
               certificate = (X509Certificate)keyStoreConfig.getKeyStore().getCertificate(alias);
            } catch (KeyStoreException var5) {
               LOGGER.error("Failed reading the certificate of alias `{}` for the key store located at `{}`", alias, keyStoreConfig.getLocation(), var5);
               return Stream.empty();
            }

            String issuer = certificate.getIssuerX500Principal().getName();
            String serialNumber = certificate.getSerialNumber().toString();
            return Stream.of(issuer, serialNumber);
         });
      }).collect(Collectors.toList()).hashCode());
   }

   @Override
   protected Socket createSocket(final InetSocketAddress socketAddress) throws IOException {
      return createSocket(this.getHost(), socketAddress, this.getConnectTimeoutMillis(), this.sslConfig, this.getSocketOptions());
   }

   private static SSLSocketFactory createSslSocketFactory(final SslConfiguration sslConf) {
      if (sslConf != null) {
         SSLContext sslContext = sslConf.getSslContext();
         if (sslContext != null) {
            return sslContext.getSocketFactory();
         }
      }

      return (SSLSocketFactory)SSLSocketFactory.getDefault();
   }

   private static Socket createSocket(
      final String hostName,
      final InetSocketAddress socketAddress,
      final int connectTimeoutMillis,
      final SslConfiguration sslConfiguration,
      final SocketOptions socketOptions
   ) throws IOException {
      SSLSocketFactory socketFactory = createSslSocketFactory(sslConfiguration);
      SSLSocket socket = (SSLSocket)socketFactory.createSocket();
      if (socketOptions != null) {
         socketOptions.apply(socket);
      }

      socket.connect(socketAddress, connectTimeoutMillis);
      if (sslConfiguration.isVerifyHostName()) {
         SSLParameters sslParameters = socket.getSSLParameters();
         sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
         SNIHostName serverName = createSniHostName(hostName);
         if (serverName != null) {
            sslParameters.setServerNames(Collections.singletonList(serverName));
         }

         socket.setSSLParameters(sslParameters);
      }

      socket.startHandshake();
      return socket;
   }

   @Nullable
   static SNIHostName createSniHostName(String hostName) {
      if (!hostName.matches("\\d+[.]\\d+[.]\\d+[.]\\d+")) {
         try {
            return new SNIHostName(hostName);
         } catch (IllegalArgumentException var2) {
         }
      }

      return null;
   }

   private static class SslFactoryData extends TcpSocketManager.FactoryData {
      protected SslConfiguration sslConfiguration;

      public SslFactoryData(
         final SslConfiguration sslConfiguration,
         final String host,
         final int port,
         final int connectTimeoutMillis,
         final int reconnectDelayMillis,
         final boolean immediateFail,
         final Layout<? extends Serializable> layout,
         final int bufferSize,
         final SocketOptions socketOptions
      ) {
         super(host, port, connectTimeoutMillis, reconnectDelayMillis, immediateFail, layout, bufferSize, socketOptions);
         this.sslConfiguration = sslConfiguration;
      }

      @Override
      public String toString() {
         return "SslFactoryData [sslConfiguration="
            + this.sslConfiguration
            + ", host="
            + this.host
            + ", port="
            + this.port
            + ", connectTimeoutMillis="
            + this.connectTimeoutMillis
            + ", reconnectDelayMillis="
            + this.reconnectDelayMillis
            + ", immediateFail="
            + this.immediateFail
            + ", layout="
            + this.layout
            + ", bufferSize="
            + this.bufferSize
            + ", socketOptions="
            + this.socketOptions
            + "]";
      }
   }

   private static class SslSocketManagerFactory extends TcpSocketManager.TcpSocketManagerFactory<SslSocketManager, SslSocketManager.SslFactoryData> {
      private SslSocketManagerFactory() {
      }

      SslSocketManager createManager(
         final String name, final OutputStream os, final Socket socket, final InetAddress inetAddress, final SslSocketManager.SslFactoryData data
      ) {
         return new SslSocketManager(
            name,
            os,
            socket,
            data.sslConfiguration,
            inetAddress,
            data.host,
            data.port,
            data.connectTimeoutMillis,
            data.reconnectDelayMillis,
            data.immediateFail,
            data.layout,
            data.bufferSize,
            data.socketOptions
         );
      }

      Socket createSocket(final SslSocketManager.SslFactoryData data) throws IOException {
         List<InetSocketAddress> socketAddresses = RESOLVER.resolveHost(data.host, data.port);
         IOException ioe = null;

         for (InetSocketAddress socketAddress : socketAddresses) {
            try {
               return SslSocketManager.createSocket(data.host, socketAddress, data.connectTimeoutMillis, data.sslConfiguration, data.socketOptions);
            } catch (IOException var9) {
               String message = String.format("failed create a socket to `%s:%s` that is resolved to address `%s`", data.host, data.port, socketAddress);
               IOException newEx = new IOException(message, var9);
               if (ioe == null) {
                  ioe = newEx;
               } else {
                  ioe.addSuppressed(newEx);
               }
            }
         }

         throw new IOException(this.errorMessage(data, socketAddresses), ioe);
      }
   }
}
