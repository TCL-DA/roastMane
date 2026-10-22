package org.eclipse.jetty.client.transport;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.alpn.client.ALPNClientConnection;
import org.eclipse.jetty.alpn.client.ALPNClientConnectionFactory;
import org.eclipse.jetty.client.AbstractConnectorHttpClientTransport;
import org.eclipse.jetty.client.Destination;
import org.eclipse.jetty.client.MultiplexConnectionPool;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ClientConnectionFactory.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientTransportDynamic extends AbstractConnectorHttpClientTransport {
   private static final Logger LOG = LoggerFactory.getLogger(HttpClientTransportDynamic.class);
   private final List<Info> factoryInfos;
   private final List<String> protocols;

   public HttpClientTransportDynamic() {
      this(HttpClientConnectionFactory.HTTP11);
   }

   public HttpClientTransportDynamic(Info... factoryInfos) {
      this(findClientConnector(factoryInfos), factoryInfos);
   }

   public HttpClientTransportDynamic(ClientConnector connector, Info... factoryInfos) {
      super(connector);
      if (factoryInfos.length == 0) {
         factoryInfos = new Info[]{HttpClientConnectionFactory.HTTP11};
      }

      this.factoryInfos = Arrays.asList(factoryInfos);
      this.protocols = Arrays.stream(factoryInfos)
         .flatMap(info -> Stream.concat(info.getProtocols(false).stream(), info.getProtocols(true).stream()))
         .distinct()
         .map(p -> p.toLowerCase(Locale.ENGLISH))
         .collect(Collectors.toList());
      Arrays.stream(factoryInfos).forEach(this::addBean);
      this.setConnectionPoolFactory(destination -> new MultiplexConnectionPool(destination, destination.getHttpClient().getMaxConnectionsPerDestination(), 1));
   }

   private static ClientConnector findClientConnector(Info[] infos) {
      return Arrays.stream(infos).flatMap(info -> info.getContainedBeans(ClientConnector.class).stream()).findFirst().orElseGet(ClientConnector::new);
   }

   @Override
   public Origin newOrigin(Request request) {
      String scheme = request.getScheme();
      boolean secure = HttpScheme.isSecure(scheme);
      String http1 = "http/1.1";
      String http2 = secure ? "h2" : "h2c";
      List<String> protocols = List.of();
      if (((HttpRequest)request).isVersionExplicit()) {
         HttpVersion version = request.getVersion();
         String desired = version == HttpVersion.HTTP_2 ? http2 : http1;
         if (this.protocols.contains(desired)) {
            protocols = List.of(desired);
         }
      } else if (secure) {
         List<String> http = List.of("http/1.1", "h2c", "h2");
         protocols = this.protocols.stream().filter(http::contains).collect(Collectors.toCollection(ArrayList::new));
         if (request.getHeaders().contains(HttpHeader.UPGRADE, "h2c")) {
            protocols.remove("h2");
         }
      } else {
         protocols = List.of(this.protocols.get(0));
      }

      Origin.Protocol protocol = null;
      if (!protocols.isEmpty()) {
         protocol = new Origin.Protocol(protocols, secure && protocols.contains(http2));
      }

      return this.getHttpClient().createOrigin(request, protocol);
   }

   @Override
   public Destination newDestination(Origin origin) {
      SocketAddress address = origin.getAddress().getSocketAddress();
      return new HttpDestination(this.getHttpClient(), origin, this.getClientConnector().isIntrinsicallySecure(address));
   }

   public Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
      HttpDestination destination = (HttpDestination)context.get("org.eclipse.jetty.client.destination");
      Origin.Protocol protocol = destination.getOrigin().getProtocol();
      ClientConnectionFactory factory;
      if (protocol == null) {
         factory = this.factoryInfos.get(0).getClientConnectionFactory();
      } else {
         SocketAddress address = destination.getOrigin().getAddress().getSocketAddress();
         boolean intrinsicallySecure = this.getClientConnector().isIntrinsicallySecure(address);
         if (!intrinsicallySecure && destination.isSecure() && protocol.isNegotiate()) {
            factory = new ALPNClientConnectionFactory(this.getClientConnector().getExecutor(), this::newNegotiatedConnection, protocol.getProtocols());
         } else {
            factory = this.findClientConnectionFactoryInfo(protocol.getProtocols(), destination.isSecure())
               .orElseThrow(() -> new IOException("Cannot find " + ClientConnectionFactory.class.getSimpleName() + " for " + protocol))
               .getClientConnectionFactory();
         }
      }

      return factory.newConnection(endPoint, context);
   }

   public void upgrade(EndPoint endPoint, Map<String, Object> context) {
      HttpDestination destination = (HttpDestination)context.get("org.eclipse.jetty.client.destination");
      Origin.Protocol protocol = destination.getOrigin().getProtocol();
      Info info = this.findClientConnectionFactoryInfo(protocol.getProtocols(), destination.isSecure())
         .orElseThrow(() -> new IllegalStateException("Cannot find " + ClientConnectionFactory.class.getSimpleName() + " to upgrade to " + protocol));
      info.upgrade(endPoint, context);
   }

   protected Connection newNegotiatedConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
      try {
         ALPNClientConnection alpnConnection = (ALPNClientConnection)endPoint.getConnection();
         String protocol = alpnConnection.getProtocol();
         Info factoryInfo;
         if (protocol != null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("ALPN negotiated {} among {}", protocol, alpnConnection.getProtocols());
            }

            List<String> protocols = List.of(protocol);
            factoryInfo = this.findClientConnectionFactoryInfo(protocols, true)
               .orElseThrow(() -> new IOException("Cannot find " + ClientConnectionFactory.class.getSimpleName() + " for negotiated protocol " + protocol));
         } else {
            factoryInfo = this.factoryInfos.get(0);
            if (LOG.isDebugEnabled()) {
               LOG.debug("No ALPN protocol, using {}", factoryInfo);
            }
         }

         return factoryInfo.getClientConnectionFactory().newConnection(endPoint, context);
      } catch (Throwable var7) {
         this.connectFailed(context, var7);
         throw var7;
      }
   }

   private Optional<Info> findClientConnectionFactoryInfo(List<String> protocols, boolean secure) {
      return this.factoryInfos.stream().filter(info -> info.matches(protocols, secure)).findFirst();
   }
}
