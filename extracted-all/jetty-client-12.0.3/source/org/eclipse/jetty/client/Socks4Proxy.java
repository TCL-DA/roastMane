package org.eclipse.jetty.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Socks4Proxy extends ProxyConfiguration.Proxy {
   public Socks4Proxy(String host, int port) {
      this(new Origin.Address(host, port), false);
   }

   public Socks4Proxy(Origin.Address address, boolean secure) {
      super(address, secure, null, null);
   }

   @Override
   public ClientConnectionFactory newClientConnectionFactory(ClientConnectionFactory connectionFactory) {
      return new Socks4Proxy.Socks4ProxyClientConnectionFactory(connectionFactory);
   }

   public static class Socks4ProxyClientConnectionFactory implements ClientConnectionFactory {
      private final ClientConnectionFactory connectionFactory;

      public Socks4ProxyClientConnectionFactory(ClientConnectionFactory connectionFactory) {
         this.connectionFactory = connectionFactory;
      }

      public org.eclipse.jetty.io.Connection newConnection(EndPoint endPoint, Map<String, Object> context) {
         Destination destination = (Destination)context.get("org.eclipse.jetty.client.destination");
         Executor executor = destination.getHttpClient().getExecutor();
         Socks4Proxy.Socks4ProxyConnection connection = new Socks4Proxy.Socks4ProxyConnection(endPoint, executor, this.connectionFactory, context);
         return this.customize(connection, context);
      }
   }

   private static class Socks4ProxyConnection extends AbstractConnection implements Callback {
      private static final Pattern IPv4_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
      private static final Logger LOG = LoggerFactory.getLogger(Socks4Proxy.Socks4ProxyConnection.class);
      private final Socks4Proxy.Socks4ProxyConnection.Socks4Parser parser = new Socks4Proxy.Socks4ProxyConnection.Socks4Parser();
      private final ClientConnectionFactory connectionFactory;
      private final Map<String, Object> context;

      public Socks4ProxyConnection(EndPoint endPoint, Executor executor, ClientConnectionFactory connectionFactory, Map<String, Object> context) {
         super(endPoint, executor);
         this.connectionFactory = connectionFactory;
         this.context = context;
      }

      public void onOpen() {
         super.onOpen();
         this.writeSocks4Connect();
      }

      private void writeSocks4Connect() {
         Destination destination = (Destination)this.context.get("org.eclipse.jetty.client.destination");
         Origin.Address address = destination.getOrigin().getAddress();
         String host = address.getHost();
         short port = (short)address.getPort();
         Matcher matcher = IPv4_PATTERN.matcher(host);
         if (matcher.matches()) {
            ByteBuffer buffer = ByteBuffer.allocate(9);
            buffer.put((byte)4).put((byte)1).putShort(port);

            for (int i = 1; i <= 4; i++) {
               buffer.put((byte)Integer.parseInt(matcher.group(i)));
            }

            buffer.put((byte)0);
            buffer.flip();
            this.getEndPoint().write(this, new ByteBuffer[]{buffer});
         } else {
            byte[] hostBytes = host.getBytes(StandardCharsets.UTF_8);
            ByteBuffer buffer = ByteBuffer.allocate(9 + hostBytes.length + 1);
            buffer.put((byte)4).put((byte)1).putShort(port);
            buffer.put((byte)0).put((byte)0).put((byte)0).put((byte)1).put((byte)0);
            buffer.put(hostBytes).put((byte)0);
            buffer.flip();
            this.getEndPoint().write(this, new ByteBuffer[]{buffer});
         }
      }

      public void succeeded() {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Written SOCKS4 connect request");
         }

         this.fillInterested();
      }

      public void failed(Throwable x) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("SOCKS4 failure", x);
         }

         this.getEndPoint().close(x);
         Promise<Connection> promise = (Promise<Connection>)this.context.get("org.eclipse.jetty.client.connection.promise");
         promise.failed(x);
      }

      public boolean onIdleExpired(TimeoutException timeout) {
         this.failed(timeout);
         return false;
      }

      public void onFillable() {
         try {
            ByteBuffer buffer;
            do {
               buffer = BufferUtil.allocate(this.parser.expected());
               int filled = this.getEndPoint().fill(buffer);
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Read SOCKS4 connect response, {} bytes", filled);
               }

               if (filled < 0) {
                  throw new IOException("SOCKS4 tunnel failed, connection closed");
               }

               if (filled == 0) {
                  this.fillInterested();
                  return;
               }
            } while (!this.parser.parse(buffer));
         } catch (Throwable var3) {
            this.failed(var3);
         }
      }

      private void onSocks4Response(int responseCode) throws IOException {
         if (responseCode == 90) {
            this.tunnel();
         } else {
            throw new IOException("SOCKS4 tunnel failed with code " + responseCode);
         }
      }

      private void tunnel() {
         try {
            Destination destination = (Destination)this.context.get("org.eclipse.jetty.client.destination");
            Origin.Address address = destination.getOrigin().getAddress();
            InetSocketAddress inet = InetSocketAddress.createUnresolved(address.getHost(), address.getPort());
            this.context.put("org.eclipse.jetty.client.connector.remoteSocketAddress", inet);
            ClientConnectionFactory connectionFactory = this.connectionFactory;
            if (destination.isSecure()) {
               connectionFactory = destination.getHttpClient().newSslClientConnectionFactory(null, connectionFactory);
            }

            org.eclipse.jetty.io.Connection newConnection = connectionFactory.newConnection(this.getEndPoint(), this.context);
            this.getEndPoint().upgrade(newConnection);
            if (LOG.isDebugEnabled()) {
               LOG.debug("SOCKS4 tunnel established: {} over {}", this, newConnection);
            }
         } catch (Throwable var6) {
            this.failed(var6);
         }
      }

      private class Socks4Parser {
         private static final int EXPECTED_LENGTH = 8;
         private int cursor;
         private int response;

         private boolean parse(ByteBuffer buffer) throws IOException {
            while (buffer.hasRemaining()) {
               byte current = buffer.get();
               if (this.cursor == 1) {
                  this.response = current & 255;
               }

               this.cursor++;
               if (this.cursor == 8) {
                  Socks4ProxyConnection.this.onSocks4Response(this.response);
                  return true;
               }
            }

            return false;
         }

         private int expected() {
            return 8 - this.cursor;
         }
      }
   }
}
