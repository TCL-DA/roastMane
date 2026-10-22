package org.eclipse.jetty.server;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.io.ssl.SslConnection.SslEndPoint;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.ssl.X509;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureRequestCustomizer implements HttpConfiguration.Customizer {
   public static final String CIPHER_SUITE_ATTRIBUTE = "org.eclipse.jetty.server.cipher";
   public static final String KEY_SIZE_ATTRIBUTE = "org.eclipse.jetty.server.keySize";
   public static final String SSL_SESSION_ID_ATTRIBUTE = "org.eclipse.jetty.server.sslSessionId";
   public static final String PEER_CERTIFICATES_ATTRIBUTE = "org.eclipse.jetty.server.peerCertificates";
   public static final String X509_ATTRIBUTE = "org.eclipse.jetty.server.x509";
   public static final String DEFAULT_SSL_SESSION_ATTRIBUTE = "org.eclipse.jetty.server.sslSession";
   public static final String DEFAULT_SSL_SESSION_DATA_ATTRIBUTE = newSslSessionDataAttribute("org.eclipse.jetty.server.sslSession");
   private static final Logger LOG = LoggerFactory.getLogger(SecureRequestCustomizer.class);
   private String _sslSessionAttribute = "org.eclipse.jetty.server.sslSession";
   private String _sslSessionDataAttribute = DEFAULT_SSL_SESSION_DATA_ATTRIBUTE;
   private boolean _sniRequired;
   private boolean _sniHostCheck;
   private long _stsMaxAge;
   private boolean _stsIncludeSubDomains;
   private HttpField _stsField;

   public SecureRequestCustomizer() {
      this(true);
   }

   public SecureRequestCustomizer(@Name("sniHostCheck") boolean sniHostCheck) {
      this(sniHostCheck, -1L, false);
   }

   public SecureRequestCustomizer(
      @Name("sniHostCheck") boolean sniHostCheck, @Name("stsMaxAgeSeconds") long stsMaxAgeSeconds, @Name("stsIncludeSubdomains") boolean stsIncludeSubdomains
   ) {
      this(false, sniHostCheck, stsMaxAgeSeconds, stsIncludeSubdomains);
   }

   public SecureRequestCustomizer(
      @Name("sniRequired") boolean sniRequired,
      @Name("sniHostCheck") boolean sniHostCheck,
      @Name("stsMaxAgeSeconds") long stsMaxAgeSeconds,
      @Name("stsIncludeSubdomains") boolean stsIncludeSubdomains
   ) {
      this._sniRequired = sniRequired;
      this._sniHostCheck = sniHostCheck;
      this._stsMaxAge = stsMaxAgeSeconds;
      this._stsIncludeSubDomains = stsIncludeSubdomains;
      this.formatSTS();
   }

   public boolean isSniHostCheck() {
      return this._sniHostCheck;
   }

   public void setSniHostCheck(boolean sniHostCheck) {
      this._sniHostCheck = sniHostCheck;
   }

   public boolean isSniRequired() {
      return this._sniRequired;
   }

   public void setSniRequired(boolean sniRequired) {
      this._sniRequired = sniRequired;
   }

   public long getStsMaxAge() {
      return this._stsMaxAge;
   }

   public void setStsMaxAge(long stsMaxAgeSeconds) {
      this.setStsMaxAge(stsMaxAgeSeconds, TimeUnit.SECONDS);
   }

   public void setStsMaxAge(long period, TimeUnit units) {
      this._stsMaxAge = units.toSeconds(period);
      this.formatSTS();
   }

   public boolean isStsIncludeSubDomains() {
      return this._stsIncludeSubDomains;
   }

   public void setStsIncludeSubDomains(boolean stsIncludeSubDomains) {
      this._stsIncludeSubDomains = stsIncludeSubDomains;
      this.formatSTS();
   }

   private void formatSTS() {
      long stsMaxAge = this.getStsMaxAge();
      if (stsMaxAge < 0L) {
         this._stsField = null;
      } else {
         this._stsField = new PreEncodedHttpField(
            HttpHeader.STRICT_TRANSPORT_SECURITY, String.format("max-age=%d%s", stsMaxAge, this.isStsIncludeSubDomains() ? "; includeSubDomains" : "")
         );
      }
   }

   @Override
   public Request customize(Request request, Mutable responseHeaders) {
      EndPoint endPoint = request.getConnectionMetaData().getConnection().getEndPoint();
      if (endPoint instanceof SslEndPoint sslEndPoint) {
         SslConnection sslConnection = sslEndPoint.getSslConnection();
         SSLEngine sslEngine = sslConnection.getSSLEngine();
         request = this.newSecureRequest(request, sslEngine);
      } else if (endPoint instanceof ProxyConnectionFactory.ProxyEndPoint proxyEndPoint && proxyEndPoint.getAttribute("TLS_VERSION") != null) {
         request = this.newSecureRequest(request, null);
      }

      if (this._stsField != null) {
         responseHeaders.add(this._stsField);
      }

      return request;
   }

   protected Request newSecureRequest(Request request, SSLEngine sslEngine) {
      return (Request)(sslEngine != null
         ? new SecureRequestCustomizer.SecureRequestWithTLSData(request, sslEngine)
         : new SecureRequestCustomizer.SecureRequest(request));
   }

   private X509Certificate[] getCertChain(Connector connector, SSLSession sslSession) {
      SslConnectionFactory sslConnectionFactory = connector.getConnectionFactory(SslConnectionFactory.class);
      if (sslConnectionFactory != null) {
         SslContextFactory sslContextFactory = sslConnectionFactory.getSslContextFactory();
         if (sslContextFactory != null) {
            return sslContextFactory.getX509CertChain(sslSession);
         }
      }

      return SslContextFactory.getCertChain(sslSession);
   }

   public void setSslSessionAttribute(String attribute) {
      Objects.requireNonNull(attribute);
      this._sslSessionAttribute = attribute;
      this._sslSessionDataAttribute = newSslSessionDataAttribute(attribute);
   }

   public String getSslSessionAttribute() {
      return this._sslSessionAttribute;
   }

   public String getSslSessionDataAttribute() {
      return this._sslSessionDataAttribute;
   }

   private static String newSslSessionDataAttribute(String sslSessionAttribute) {
      return sslSessionAttribute + "Data";
   }

   protected void checkSni(Request request, SSLSession session) {
      if (this.isSniRequired() || this.isSniHostCheck()) {
         String sniHost = (String)session.getValue("org.eclipse.jetty.util.ssl.sniHost");
         X509 x509 = this.getX509(session);
         if (x509 == null) {
            throw new BadMessageException(400, "Invalid SNI");
         }

         String serverName = Request.getServerName(request);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Host={}, SNI={}, SNI Certificate={}", new Object[]{serverName, sniHost, x509});
         }

         if (this.isSniRequired() && (sniHost == null || !x509.matches(sniHost))) {
            throw new BadMessageException(400, "Invalid SNI");
         }

         if (this.isSniHostCheck() && !x509.matches(serverName)) {
            throw new BadMessageException(400, "Invalid SNI");
         }
      }
   }

   private X509 getX509(SSLSession session) {
      X509 x509 = (X509)session.getValue("org.eclipse.jetty.server.x509");
      if (x509 == null) {
         Certificate[] certificates = session.getLocalCertificates();
         if (certificates == null || certificates.length == 0 || !(certificates[0] instanceof X509Certificate)) {
            return null;
         }

         x509 = new X509(null, (X509Certificate)certificates[0]);
         session.putValue("org.eclipse.jetty.server.x509", x509);
      }

      return x509;
   }

   @Override
   public String toString() {
      return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
   }

   protected static class SecureRequest extends Request.Wrapper {
      public SecureRequest(Request wrapped) {
         super(wrapped);
      }

      @Override
      public boolean isSecure() {
         return true;
      }
   }

   protected class SecureRequestWithTLSData extends SecureRequestCustomizer.SecureRequest {
      private final SSLSession _sslSession;
      private final SecureRequestCustomizer.SslSessionData _sslSessionData;

      public SecureRequestWithTLSData(Request request, SSLEngine sslEngine) {
         super(request);
         this._sslSession = sslEngine.getSession();
         SecureRequestCustomizer.this.checkSni(request, this._sslSession);
         String key = SecureRequestCustomizer.SslSessionData.class.getName();
         SecureRequestCustomizer.SslSessionData sslSessionData = (SecureRequestCustomizer.SslSessionData)this._sslSession.getValue(key);
         if (sslSessionData == null) {
            try {
               String cipherSuite = this._sslSession.getCipherSuite();
               int keySize = SslContextFactory.deduceKeyLength(cipherSuite);
               X509Certificate[] peerCertificates = SecureRequestCustomizer.this.getCertChain(this.getConnectionMetaData().getConnector(), this._sslSession);
               byte[] bytes = this._sslSession.getId();
               String idStr = StringUtil.toHexString(bytes);
               sslSessionData = new SecureRequestCustomizer.SslSessionData(idStr, cipherSuite, keySize, peerCertificates);
               this._sslSession.putValue(key, sslSessionData);
            } catch (Exception var11) {
               SecureRequestCustomizer.LOG.warn("Unable to get secure details ", var11);
            }
         }

         this._sslSessionData = sslSessionData;
      }

      public Object getAttribute(String name) {
         String sessionAttribute = SecureRequestCustomizer.this.getSslSessionAttribute();
         if (StringUtil.isNotBlank(sessionAttribute) && name.startsWith(sessionAttribute)) {
            if (name.equals(sessionAttribute)) {
               return this._sslSession;
            }

            if (name.equals(SecureRequestCustomizer.this.getSslSessionDataAttribute())) {
               return this._sslSessionData;
            }
         }
         return switch (name) {
            case "org.eclipse.jetty.server.cipher" -> this._sslSessionData != null ? this._sslSessionData.cipherSuite() : null;
            case "org.eclipse.jetty.server.keySize" -> this._sslSessionData != null ? this._sslSessionData.keySize() : null;
            case "org.eclipse.jetty.server.sslSessionId" -> this._sslSessionData != null ? this._sslSessionData.sessionId() : null;
            case "org.eclipse.jetty.server.peerCertificates" -> this._sslSessionData != null ? this._sslSessionData.peerCertificates() : null;
            case "org.eclipse.jetty.server.x509" -> SecureRequestCustomizer.this.getX509(this._sslSession);
            default -> super.getAttribute(name);
         };
      }

      public Set<String> getAttributeNameSet() {
         Set<String> names = new HashSet<>(super.getAttributeNameSet());
         if (SecureRequestCustomizer.this.getX509(this._sslSession) != null) {
            names.add("org.eclipse.jetty.server.x509");
         }

         String sessionAttribute = SecureRequestCustomizer.this.getSslSessionAttribute();
         if (!StringUtil.isNotBlank(sessionAttribute)) {
            names.add(sessionAttribute);
            if (this._sslSessionData != null) {
               names.add(SecureRequestCustomizer.this.getSslSessionDataAttribute());
               names.add("org.eclipse.jetty.server.cipher");
               names.add("org.eclipse.jetty.server.keySize");
               names.add("org.eclipse.jetty.server.sslSessionId");
               names.add("org.eclipse.jetty.server.peerCertificates");
            }
         }

         return names;
      }
   }

   public static record SslSessionData(String sessionId, String cipherSuite, int keySize, X509Certificate[] peerCertificates) {
   }
}
