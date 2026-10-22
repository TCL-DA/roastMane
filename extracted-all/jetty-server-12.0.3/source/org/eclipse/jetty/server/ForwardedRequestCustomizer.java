package org.eclipse.jetty.server;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.QuotedCSVParser;
import org.eclipse.jetty.server.internal.HttpConnection;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.Index.Builder;
import org.eclipse.jetty.util.Index.Mutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForwardedRequestCustomizer implements HttpConfiguration.Customizer {
   private static final Logger LOG = LoggerFactory.getLogger(HttpConnection.class);
   private HostPortHttpField _forcedHost;
   private boolean _proxyAsAuthority = false;
   private boolean _forwardedPortAsAuthority = true;
   private String _forwardedHeader = HttpHeader.FORWARDED.toString();
   private String _forwardedHostHeader = HttpHeader.X_FORWARDED_HOST.toString();
   private String _forwardedServerHeader = HttpHeader.X_FORWARDED_SERVER.toString();
   private String _forwardedProtoHeader = HttpHeader.X_FORWARDED_PROTO.toString();
   private String _forwardedForHeader = HttpHeader.X_FORWARDED_FOR.toString();
   private String _forwardedPortHeader = HttpHeader.X_FORWARDED_PORT.toString();
   private String _forwardedHttpsHeader = "X-Proxied-Https";
   private String _forwardedCipherSuiteHeader = "Proxy-auth-cert";
   private String _forwardedSslSessionIdHeader = "Proxy-ssl-id";
   private boolean _sslIsSecure = true;
   private final Mutable<MethodHandle> _handles = new Builder().caseSensitive(false).mutable().build();

   public ForwardedRequestCustomizer() {
      this.updateHandles();
   }

   public boolean getProxyAsAuthority() {
      return this._proxyAsAuthority;
   }

   public void setProxyAsAuthority(boolean proxyAsAuthority) {
      this._proxyAsAuthority = proxyAsAuthority;
   }

   public void setForwardedOnly(boolean rfc7239only) {
      if (rfc7239only) {
         if (this._forwardedHeader == null) {
            this._forwardedHeader = HttpHeader.FORWARDED.toString();
         }

         this._forwardedHostHeader = null;
         this._forwardedServerHeader = null;
         this._forwardedForHeader = null;
         this._forwardedPortHeader = null;
         this._forwardedProtoHeader = null;
         this._forwardedHttpsHeader = null;
      } else {
         if (this._forwardedHostHeader == null) {
            this._forwardedHostHeader = HttpHeader.X_FORWARDED_HOST.toString();
         }

         if (this._forwardedServerHeader == null) {
            this._forwardedServerHeader = HttpHeader.X_FORWARDED_SERVER.toString();
         }

         if (this._forwardedForHeader == null) {
            this._forwardedForHeader = HttpHeader.X_FORWARDED_FOR.toString();
         }

         if (this._forwardedPortHeader == null) {
            this._forwardedPortHeader = HttpHeader.X_FORWARDED_PORT.toString();
         }

         if (this._forwardedProtoHeader == null) {
            this._forwardedProtoHeader = HttpHeader.X_FORWARDED_PROTO.toString();
         }

         if (this._forwardedHttpsHeader == null) {
            this._forwardedHttpsHeader = "X-Proxied-Https";
         }
      }

      this.updateHandles();
   }

   public String getForcedHost() {
      return this._forcedHost.getValue();
   }

   public void setForcedHost(String hostAndPort) {
      this._forcedHost = new HostPortHttpField(hostAndPort);
   }

   public String getForwardedHeader() {
      return this._forwardedHeader;
   }

   public void setForwardedHeader(String forwardedHeader) {
      if (this._forwardedHeader == null || !this._forwardedHeader.equals(forwardedHeader)) {
         this._forwardedHeader = forwardedHeader;
         this.updateHandles();
      }
   }

   public String getForwardedHostHeader() {
      return this._forwardedHostHeader;
   }

   public void setForwardedHostHeader(String forwardedHostHeader) {
      if (this._forwardedHostHeader == null || !this._forwardedHostHeader.equalsIgnoreCase(forwardedHostHeader)) {
         this._forwardedHostHeader = forwardedHostHeader;
         this.updateHandles();
      }
   }

   public String getForwardedServerHeader() {
      return this._forwardedServerHeader;
   }

   public void setForwardedServerHeader(String forwardedServerHeader) {
      if (this._forwardedServerHeader == null || !this._forwardedServerHeader.equalsIgnoreCase(forwardedServerHeader)) {
         this._forwardedServerHeader = forwardedServerHeader;
         this.updateHandles();
      }
   }

   public String getForwardedForHeader() {
      return this._forwardedForHeader;
   }

   public void setForwardedForHeader(String forwardedRemoteAddressHeader) {
      if (this._forwardedForHeader == null || !this._forwardedForHeader.equalsIgnoreCase(forwardedRemoteAddressHeader)) {
         this._forwardedForHeader = forwardedRemoteAddressHeader;
         this.updateHandles();
      }
   }

   public String getForwardedPortHeader() {
      return this._forwardedPortHeader;
   }

   public void setForwardedPortHeader(String forwardedPortHeader) {
      if (this._forwardedPortHeader == null || !this._forwardedPortHeader.equalsIgnoreCase(forwardedPortHeader)) {
         this._forwardedPortHeader = forwardedPortHeader;
         this.updateHandles();
      }
   }

   public boolean getForwardedPortAsAuthority() {
      return this._forwardedPortAsAuthority;
   }

   public void setForwardedPortAsAuthority(boolean forwardedPortAsAuthority) {
      this._forwardedPortAsAuthority = forwardedPortAsAuthority;
   }

   public String getForwardedProtoHeader() {
      return this._forwardedProtoHeader;
   }

   public void setForwardedProtoHeader(String forwardedProtoHeader) {
      if (this._forwardedProtoHeader == null || !this._forwardedProtoHeader.equalsIgnoreCase(forwardedProtoHeader)) {
         this._forwardedProtoHeader = forwardedProtoHeader;
         this.updateHandles();
      }
   }

   public String getForwardedCipherSuiteHeader() {
      return this._forwardedCipherSuiteHeader;
   }

   public void setForwardedCipherSuiteHeader(String forwardedCipherSuiteHeader) {
      if (this._forwardedCipherSuiteHeader == null || !this._forwardedCipherSuiteHeader.equalsIgnoreCase(forwardedCipherSuiteHeader)) {
         this._forwardedCipherSuiteHeader = forwardedCipherSuiteHeader;
         this.updateHandles();
      }
   }

   public String getForwardedSslSessionIdHeader() {
      return this._forwardedSslSessionIdHeader;
   }

   public void setForwardedSslSessionIdHeader(String forwardedSslSessionIdHeader) {
      if (this._forwardedSslSessionIdHeader == null || !this._forwardedSslSessionIdHeader.equalsIgnoreCase(forwardedSslSessionIdHeader)) {
         this._forwardedSslSessionIdHeader = forwardedSslSessionIdHeader;
         this.updateHandles();
      }
   }

   public String getForwardedHttpsHeader() {
      return this._forwardedHttpsHeader;
   }

   public void setForwardedHttpsHeader(String forwardedHttpsHeader) {
      if (this._forwardedHttpsHeader == null || !this._forwardedHttpsHeader.equalsIgnoreCase(forwardedHttpsHeader)) {
         this._forwardedHttpsHeader = forwardedHttpsHeader;
         this.updateHandles();
      }
   }

   public boolean isSslIsSecure() {
      return this._sslIsSecure;
   }

   public void setSslIsSecure(boolean sslIsSecure) {
      this._sslIsSecure = sslIsSecure;
   }

   @Override
   public Request customize(Request request, org.eclipse.jetty.http.HttpFields.Mutable responseHeaders) {
      HttpConfiguration httpConfig = request.getConnectionMetaData().getHttpConfiguration();
      HttpFields httpFields = request.getHeaders();
      ForwardedRequestCustomizer.Forwarded forwarded = new ForwardedRequestCustomizer.Forwarded(request, httpConfig);
      boolean match = false;

      for (HttpField field : httpFields) {
         try {
            MethodHandle handle = (MethodHandle)this._handles.get(field.getName());
            if (handle != null) {
               match = true;
               handle.invoke((ForwardedRequestCustomizer.Forwarded)forwarded, (HttpField)field);
            }
         } catch (Throwable var15) {
            this.onError(field, var15);
         }
      }

      if (!match) {
         return request;
      } else {
         org.eclipse.jetty.http.HttpURI.Mutable builder = HttpURI.build(request.getHttpURI());
         boolean httpUriChanged = false;
         if (LOG.isDebugEnabled()) {
            LOG.debug("forwarded {} {}", builder, forwarded);
         }

         final boolean secure = forwarded.isSecure();
         if (forwarded._proto != null) {
            builder.scheme(forwarded._proto);
            httpUriChanged = true;
         } else if (forwarded._secureScheme) {
            builder.scheme(httpConfig.getSecureScheme());
            httpUriChanged = true;
         }

         final HostPortHttpField authority;
         if (forwarded._authority != null) {
            String host = forwarded._authority._host;
            int port = forwarded._authority._port;
            if (host == null) {
               host = builder.getHost();
            }

            if (port == -1) {
               port = builder.getPort();
            }

            if (request.getHttpURI().getPort() == 0 && port > 0 && port == ((HttpScheme)HttpScheme.CACHE.get(httpConfig.getSecureScheme())).getDefaultPort()) {
               port = 0;
            }

            if (host.equalsIgnoreCase(builder.getHost()) && port == builder.getPort()) {
               authority = null;
            } else {
               authority = new HostPortHttpField(host, port);
               builder.authority(host, port);
               httpUriChanged = true;
            }
         } else {
            authority = null;
         }

         final HttpURI uri = (HttpURI)(httpUriChanged ? builder.asImmutable() : request.getHttpURI());
         final InetSocketAddress remote;
         if (forwarded.hasFor()) {
            int forPort = forwarded._for._port;
            if (forPort <= 0) {
               SocketAddress addr = request.getConnectionMetaData().getRemoteSocketAddress();
               if (addr instanceof InetSocketAddress) {
                  forPort = ((InetSocketAddress)addr).getPort();
               }
            }

            remote = InetSocketAddress.createUnresolved(forwarded._for._host, forPort);
         } else {
            remote = null;
         }

         final ConnectionMetaData connectionMetaData = new ConnectionMetaData.Wrapper(request.getConnectionMetaData()) {
            @Override
            public SocketAddress getRemoteSocketAddress() {
               return (SocketAddress)(remote != null ? remote : super.getRemoteSocketAddress());
            }

            @Override
            public HostPort getServerAuthority() {
               return authority != null ? authority.getHostPort() : super.getServerAuthority();
            }

            @Override
            public String toString() {
               return "%s@%x{id=%s,remote=%s,authority=%s,%s}"
                  .formatted(TypeUtil.toShortName(this.getClass()), this.hashCode(), this.getId(), remote, authority, this.getWrapped());
            }
         };
         final HttpFields headers = (HttpFields)(authority == null ? request.getHeaders() : HttpFields.build(request.getHeaders(), authority));
         return new Request.Wrapper(request) {
            @Override
            public HttpURI getHttpURI() {
               return uri;
            }

            @Override
            public HttpFields getHeaders() {
               return headers;
            }

            @Override
            public boolean isSecure() {
               return secure || super.isSecure();
            }

            @Override
            public ConnectionMetaData getConnectionMetaData() {
               return connectionMetaData;
            }
         };
      }
   }

   protected static int getSecurePort(HttpConfiguration config) {
      return config.getSecurePort() > 0 ? config.getSecurePort() : 443;
   }

   protected void onError(HttpField field, Throwable t) {
      throw new BadMessageException("Bad header value for " + field.getName(), t);
   }

   protected static String getLeftMost(String headerValue) {
      if (headerValue == null) {
         return null;
      } else {
         int commaIndex = headerValue.indexOf(44);
         return commaIndex == -1 ? headerValue : headerValue.substring(0, commaIndex).trim();
      }
   }

   @Override
   public String toString() {
      return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
   }

   public String getHostHeader() {
      return this._forcedHost.getValue();
   }

   public void setHostHeader(String hostHeader) {
      this._forcedHost = new HostPortHttpField(hostHeader);
   }

   private void updateHandles() {
      this._handles.clear();
      Lookup lookup = MethodHandles.lookup();

      try {
         this.updateForwardedHandle(lookup, this.getForwardedHeader(), "handleRFC7239");
         this.updateForwardedHandle(lookup, this.getForwardedHostHeader(), "handleForwardedHost");
         this.updateForwardedHandle(lookup, this.getForwardedForHeader(), "handleForwardedFor");
         this.updateForwardedHandle(lookup, this.getForwardedPortHeader(), "handleForwardedPort");
         this.updateForwardedHandle(lookup, this.getForwardedProtoHeader(), "handleProto");
         this.updateForwardedHandle(lookup, this.getForwardedHttpsHeader(), "handleHttps");
         this.updateForwardedHandle(lookup, this.getForwardedServerHeader(), "handleForwardedServer");
         this.updateForwardedHandle(lookup, this.getForwardedCipherSuiteHeader(), "handleCipherSuite");
         this.updateForwardedHandle(lookup, this.getForwardedSslSessionIdHeader(), "handleSslSessionId");
      } catch (IllegalAccessException | NoSuchMethodException var3) {
         throw new IllegalStateException(var3);
      }
   }

   private void updateForwardedHandle(Lookup lookup, String headerName, String forwardedMethodName) throws NoSuchMethodException, IllegalAccessException {
      MethodType type = MethodType.methodType(void.class, HttpField.class);
      if (!StringUtil.isBlank(headerName)) {
         this._handles.put(headerName, lookup.findVirtual(ForwardedRequestCustomizer.Forwarded.class, forwardedMethodName, type));
      }
   }

   private class Forwarded extends QuotedCSVParser {
      HttpConfiguration _config;
      Request _request;
      ForwardedRequestCustomizer.MutableHostPort _authority;
      ForwardedRequestCustomizer.MutableHostPort _for;
      String _proto;
      ForwardedRequestCustomizer.Source _protoSource = ForwardedRequestCustomizer.Source.UNSET;
      Boolean _secure;
      boolean _secureScheme = false;

      public Forwarded(Request request, HttpConfiguration config) {
         super(false);
         this._request = request;
         this._config = config;
         if (ForwardedRequestCustomizer.this._forcedHost != null) {
            this.getAuthority()
               .setHostPort(
                  ForwardedRequestCustomizer.this._forcedHost.getHostPort().getHost(),
                  ForwardedRequestCustomizer.this._forcedHost.getHostPort().getPort(),
                  ForwardedRequestCustomizer.Source.FORCED
               );
         }
      }

      public boolean isSecure() {
         return this._secure != null && this._secure;
      }

      public boolean hasFor() {
         return this._for != null && this._for._host != null;
      }

      private ForwardedRequestCustomizer.MutableHostPort getAuthority() {
         if (this._authority == null) {
            this._authority = new ForwardedRequestCustomizer.MutableHostPort();
         }

         return this._authority;
      }

      private ForwardedRequestCustomizer.MutableHostPort getFor() {
         if (this._for == null) {
            this._for = new ForwardedRequestCustomizer.MutableHostPort();
         }

         return this._for;
      }

      public void handleCipherSuite(HttpField field) {
         this._request.setAttribute("jakarta.servlet.request.cipher_suite", field.getValue());
         if (ForwardedRequestCustomizer.this.isSslIsSecure()) {
            this._secure = true;
            this._secureScheme = true;
         }
      }

      public void handleSslSessionId(HttpField field) {
         this._request.setAttribute("jakarta.servlet.request.ssl_session_id", field.getValue());
         if (ForwardedRequestCustomizer.this.isSslIsSecure()) {
            this._secure = true;
            this._secureScheme = true;
         }
      }

      public void handleForwardedHost(HttpField field) {
         this.updateAuthority(ForwardedRequestCustomizer.getLeftMost(field.getValue()), ForwardedRequestCustomizer.Source.XFORWARDED_HOST);
      }

      public void handleForwardedFor(HttpField field) {
         HostPort hostField = new HostPort(ForwardedRequestCustomizer.getLeftMost(field.getValue()));
         this.getFor().setHostPort(hostField, ForwardedRequestCustomizer.Source.XFORWARDED_FOR);
      }

      public void handleForwardedServer(HttpField field) {
         if (!ForwardedRequestCustomizer.this.getProxyAsAuthority()) {
            this.updateAuthority(ForwardedRequestCustomizer.getLeftMost(field.getValue()), ForwardedRequestCustomizer.Source.XFORWARDED_SERVER);
         }
      }

      public void handleForwardedPort(HttpField field) {
         int port = HostPort.parsePort(ForwardedRequestCustomizer.getLeftMost(field.getValue()));
         this.updatePort(port, ForwardedRequestCustomizer.Source.XFORWARDED_PORT);
      }

      public void handleProto(HttpField field) {
         this.updateProto(ForwardedRequestCustomizer.getLeftMost(field.getValue()), ForwardedRequestCustomizer.Source.XFORWARDED_PROTO);
      }

      public void handleHttps(HttpField field) {
         if (!"on".equalsIgnoreCase(field.getValue()) && !"true".equalsIgnoreCase(field.getValue())) {
            if (!"off".equalsIgnoreCase(field.getValue()) && !"false".equalsIgnoreCase(field.getValue())) {
               throw new BadMessageException("Invalid value for " + field.getName());
            }

            this._secure = false;
            this.updateProto(HttpScheme.HTTP.asString(), ForwardedRequestCustomizer.Source.XPROXIED_HTTPS);
            this.updatePort(0, ForwardedRequestCustomizer.Source.XPROXIED_HTTPS);
         } else {
            this._secure = true;
            this.updateProto(HttpScheme.HTTPS.asString(), ForwardedRequestCustomizer.Source.XPROXIED_HTTPS);
            this.updatePort(ForwardedRequestCustomizer.getSecurePort(this._config), ForwardedRequestCustomizer.Source.XPROXIED_HTTPS);
         }
      }

      public void handleRFC7239(HttpField field) {
         this.addValue(field.getValue());
      }

      protected void parsedParam(StringBuffer buffer, int valueLength, int paramName, int paramValue) {
         if (valueLength == 0 && paramValue > paramName) {
            String name = StringUtil.asciiToLowerCase(buffer.substring(paramName, paramValue - 1));
            String value = buffer.substring(paramValue);
            switch (name) {
               case "by":
                  if (ForwardedRequestCustomizer.this.getProxyAsAuthority() && !value.startsWith("_") && !"unknown".equals(value)) {
                     HostPort hostField = new HostPort(value);
                     this.getAuthority().setHostPort(hostField.getHost(), hostField.getPort(), ForwardedRequestCustomizer.Source.FORWARDED);
                  }
                  break;
               case "for":
                  if (!value.startsWith("_") && !"unknown".equals(value)) {
                     HostPort hostField = new HostPort(value);
                     this.getFor().setHostPort(hostField.getHost(), hostField.getPort(), ForwardedRequestCustomizer.Source.FORWARDED);
                  }
                  break;
               case "host":
                  if (!value.startsWith("_") && !"unknown".equals(value)) {
                     HostPort hostField = new HostPort(value);
                     this.getAuthority().setHostPort(hostField.getHost(), hostField.getPort(), ForwardedRequestCustomizer.Source.FORWARDED);
                  }
                  break;
               case "proto":
                  this.updateProto(value, ForwardedRequestCustomizer.Source.FORWARDED);
            }
         }
      }

      private void updateAuthority(String value, ForwardedRequestCustomizer.Source source) {
         HostPort hostField = new HostPort(value);
         this.getAuthority().setHostPort(hostField, source);
      }

      private void updatePort(int port, ForwardedRequestCustomizer.Source source) {
         if (ForwardedRequestCustomizer.this.getForwardedPortAsAuthority()) {
            this.getAuthority().setPort(port, source);
         } else {
            this.getFor().setPort(port, source);
         }
      }

      private void updateProto(String proto, ForwardedRequestCustomizer.Source source) {
         if (source.priority() > this._protoSource.priority()) {
            this._proto = proto;
            this._protoSource = source;
            if (this._proto.equalsIgnoreCase(this._config.getSecureScheme())) {
               this._secure = true;
            }
         }
      }

      public String toString() {
         return String.format(
            "Forwarded@%x[req=%s,auth=%s,for=%s,proto=%s,sec=%s/%s]",
            this.hashCode(),
            this._request,
            this._authority,
            this._for,
            this._proto,
            this._secure,
            this._secureScheme
         );
      }
   }

   private static class MutableHostPort {
      public static final int UNSET = -1;
      public static final int IMPLIED = 0;
      String _host;
      ForwardedRequestCustomizer.Source _hostSource = ForwardedRequestCustomizer.Source.UNSET;
      int _port = -1;
      ForwardedRequestCustomizer.Source _portSource = ForwardedRequestCustomizer.Source.UNSET;

      public void setHostPort(String host, int port, ForwardedRequestCustomizer.Source source) {
         this.setHost(host, source);
         this.setPort(port, source);
      }

      public void setHost(String host, ForwardedRequestCustomizer.Source source) {
         if (source.priority() > this._hostSource.priority()) {
            this._host = host;
            this._hostSource = source;
         }
      }

      public void setPort(int port, ForwardedRequestCustomizer.Source source) {
         if (source.priority() > this._portSource.priority()) {
            this._port = port;
            this._portSource = source;
         }
      }

      public void setHostPort(HostPort hostPort, ForwardedRequestCustomizer.Source source) {
         if (source.priority() > this._hostSource.priority()) {
            this._host = hostPort.getHost();
            this._hostSource = source;
         }

         int port = hostPort.getPort();
         if (port > 0 && source.priority() > this._portSource.priority()) {
            this._port = hostPort.getPort();
            this._portSource = source;
         } else if (this._port == -1) {
            this._port = 0;
         }
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder("MutableHostPort{");
         sb.append("host='").append(this._host).append("'/").append(this._hostSource);
         sb.append(", port=").append(this._port);
         sb.append("/").append(this._portSource);
         sb.append('}');
         return sb.toString();
      }
   }

   public static enum Source {
      UNSET,
      XPROXIED_HTTPS,
      XFORWARDED_PROTO,
      XFORWARDED_SERVER,
      XFORWARDED_PORT,
      XFORWARDED_FOR,
      XFORWARDED_HOST,
      FORWARDED,
      FORCED;

      int priority() {
         return this.ordinal();
      }
   }
}
