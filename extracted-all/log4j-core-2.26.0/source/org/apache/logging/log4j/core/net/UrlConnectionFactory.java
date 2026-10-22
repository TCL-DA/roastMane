package org.apache.logging.log4j.core.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.core.net.ssl.LaxHostnameVerifier;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.net.ssl.SslConfigurationFactory;
import org.apache.logging.log4j.core.util.AuthorizationProvider;
import org.apache.logging.log4j.core.util.internal.SystemUtils;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public class UrlConnectionFactory {
   private static final int DEFAULT_TIMEOUT = 60000;
   private static final int connectTimeoutMillis = 60000;
   private static final int readTimeoutMillis = 60000;
   private static final String JSON = "application/json";
   private static final String XML = "application/xml";
   private static final String PROPERTIES = "text/x-java-properties";
   private static final String TEXT = "text/plain";
   private static final String HTTP = "http";
   private static final String HTTPS = "https";
   private static final String JAR = "jar";
   private static final String DEFAULT_ALLOWED_PROTOCOLS = SystemUtils.isGraalVm() ? "file, https, jar, resource" : "file, https, jar";
   private static final String NO_PROTOCOLS = "_none";
   public static final String ALLOWED_PROTOCOLS = "log4j2.Configuration.allowedProtocols";

   @SuppressFBWarnings(
      value = {"URLCONNECTION_SSRF_FD"},
      justification = "The URL parameter originates only from secure sources."
   )
   public static <T extends URLConnection> T createConnection(
      final URL url, final long lastModifiedMillis, final SslConfiguration sslConfiguration, final AuthorizationProvider authorizationProvider
   ) throws IOException {
      PropertiesUtil props = PropertiesUtil.getProperties();
      List<String> allowed = Arrays.asList(
         Strings.splitList(Strings.toRootLowerCase(props.getStringProperty("log4j2.Configuration.allowedProtocols", DEFAULT_ALLOWED_PROTOCOLS)))
      );
      if (allowed.size() == 1 && "_none".equals(allowed.get(0))) {
         throw new ProtocolException("No external protocols have been enabled");
      } else {
         String protocol = url.getProtocol();
         if (protocol == null) {
            throw new ProtocolException("No protocol was specified on " + url.toString());
         } else if (!allowed.contains(protocol)) {
            throw new ProtocolException("Protocol " + protocol + " has not been enabled as an allowed protocol");
         } else {
            URLConnection urlConnection;
            if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
               HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
               if (authorizationProvider != null) {
                  authorizationProvider.addAuthorization(httpURLConnection);
               }

               httpURLConnection.setAllowUserInteraction(false);
               httpURLConnection.setDoOutput(true);
               httpURLConnection.setDoInput(true);
               httpURLConnection.setRequestMethod("GET");
               httpURLConnection.setConnectTimeout(60000);
               httpURLConnection.setReadTimeout(60000);
               String[] fileParts = url.getFile().split("\\.");
               String type = fileParts[fileParts.length - 1].trim();
               String contentType = isXml(type)
                  ? "application/xml"
                  : (isJson(type) ? "application/json" : (isProperties(type) ? "text/x-java-properties" : "text/plain"));
               httpURLConnection.setRequestProperty("Content-Type", contentType);
               if (lastModifiedMillis > 0L) {
                  httpURLConnection.setIfModifiedSince(lastModifiedMillis);
               }

               if (url.getProtocol().equals("https") && sslConfiguration != null) {
                  SSLContext sslContext = sslConfiguration.getSslContext();
                  HttpsURLConnection httpsURLConnection = (HttpsURLConnection)httpURLConnection;
                  if (sslContext != null) {
                     httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                  }

                  if (!sslConfiguration.isVerifyHostName()) {
                     httpsURLConnection.setHostnameVerifier(LaxHostnameVerifier.INSTANCE);
                  }
               }

               urlConnection = httpURLConnection;
            } else if (url.getProtocol().equals("jar")) {
               urlConnection = url.openConnection();
               urlConnection.setUseCaches(false);
            } else {
               urlConnection = url.openConnection();
            }

            return (T)urlConnection;
         }
      }
   }

   @SuppressFBWarnings(
      value = {"URLCONNECTION_SSRF_FD"},
      justification = "This method sanitizes the usage of the provided URL."
   )
   public static URLConnection createConnection(final URL url) throws IOException {
      URLConnection urlConnection = null;
      if (!url.getProtocol().equals("https") && !url.getProtocol().equals("http")) {
         urlConnection = url.openConnection();
         if (urlConnection instanceof JarURLConnection) {
            urlConnection.setUseCaches(false);
         }
      } else {
         AuthorizationProvider provider = ConfigurationFactory.authorizationProvider(PropertiesUtil.getProperties());
         urlConnection = createConnection(url, 0L, SslConfigurationFactory.getSslConfiguration(), provider);
      }

      return urlConnection;
   }

   private static boolean isXml(final String type) {
      return type.equalsIgnoreCase("xml");
   }

   private static boolean isJson(final String type) {
      return type.equalsIgnoreCase("json") || type.equalsIgnoreCase("jsn");
   }

   private static boolean isProperties(final String type) {
      return type.equalsIgnoreCase("properties");
   }
}
