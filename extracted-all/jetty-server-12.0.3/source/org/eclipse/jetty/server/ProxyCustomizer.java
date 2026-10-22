package org.eclipse.jetty.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.io.EndPoint;

public class ProxyCustomizer implements HttpConfiguration.Customizer {
   public static final String REMOTE_ADDRESS_ATTRIBUTE_NAME = "org.eclipse.jetty.proxy.remote.address";
   public static final String REMOTE_PORT_ATTRIBUTE_NAME = "org.eclipse.jetty.proxy.remote.port";
   public static final String LOCAL_ADDRESS_ATTRIBUTE_NAME = "org.eclipse.jetty.proxy.local.address";
   public static final String LOCAL_PORT_ATTRIBUTE_NAME = "org.eclipse.jetty.proxy.local.port";

   @Override
   public Request customize(Request request, Mutable responseHeaders) {
      EndPoint endPoint = request.getConnectionMetaData().getConnection().getEndPoint();
      if (endPoint instanceof ProxyConnectionFactory.ProxyEndPoint) {
         EndPoint underlyingEndpoint = ((ProxyConnectionFactory.ProxyEndPoint)endPoint).unwrap();
         request = new ProxyCustomizer.ProxyRequest(request, underlyingEndpoint.getLocalSocketAddress(), underlyingEndpoint.getRemoteSocketAddress());
      }

      return request;
   }

   private static class ProxyRequest extends Request.Wrapper {
      private final String _remoteAddress;
      private final String _localAddress;
      private final int _remotePort;
      private final int _localPort;

      private ProxyRequest(Request request, SocketAddress local, SocketAddress remote) {
         super(request);
         InetSocketAddress inetLocal = local instanceof InetSocketAddress ? (InetSocketAddress)local : null;
         InetSocketAddress inetRemote = remote instanceof InetSocketAddress ? (InetSocketAddress)remote : null;
         this._localAddress = inetLocal == null ? null : inetLocal.getAddress().getHostAddress();
         this._remoteAddress = inetRemote == null ? null : inetRemote.getAddress().getHostAddress();
         this._localPort = inetLocal == null ? 0 : inetLocal.getPort();
         this._remotePort = inetRemote == null ? 0 : inetRemote.getPort();
      }

      public Object getAttribute(String name) {
         return switch (name) {
            case "org.eclipse.jetty.proxy.remote.address" -> this._remoteAddress;
            case "org.eclipse.jetty.proxy.remote.port" -> this._remotePort;
            case "org.eclipse.jetty.proxy.local.address" -> this._localAddress;
            case "org.eclipse.jetty.proxy.local.port" -> this._localPort;
            default -> super.getAttribute(name);
         };
      }

      public Set<String> getAttributeNameSet() {
         Set<String> names = new HashSet<>(super.getAttributeNameSet());
         names.remove("org.eclipse.jetty.proxy.remote.address");
         names.remove("org.eclipse.jetty.proxy.local.address");
         if (this._remoteAddress != null) {
            names.add("org.eclipse.jetty.proxy.remote.address");
         }

         if (this._localAddress != null) {
            names.add("org.eclipse.jetty.proxy.local.address");
         }

         names.add("org.eclipse.jetty.proxy.remote.port");
         names.add("org.eclipse.jetty.proxy.local.port");
         return names;
      }
   }
}
