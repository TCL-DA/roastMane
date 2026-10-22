package org.apache.logging.log4j.core.appender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.core.net.ssl.LaxHostnameVerifier;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;

public class HttpURLConnectionManager extends HttpManager {
   private static final Charset CHARSET = StandardCharsets.US_ASCII;
   private final URL url;
   private final boolean isHttps;
   private final String method;
   private final int connectTimeoutMillis;
   private final int readTimeoutMillis;
   private final Property[] headers;
   private final SslConfiguration sslConfiguration;
   private final boolean verifyHostname;

   public HttpURLConnectionManager(
      final Configuration configuration,
      final LoggerContext loggerContext,
      final String name,
      final URL url,
      final String method,
      final int connectTimeoutMillis,
      final int readTimeoutMillis,
      final Property[] headers,
      final SslConfiguration sslConfiguration,
      final boolean verifyHostname
   ) {
      super(configuration, loggerContext, name);
      this.url = url;
      if (!url.getProtocol().equalsIgnoreCase("http") && !url.getProtocol().equalsIgnoreCase("https")) {
         throw new ConfigurationException("URL must have scheme http or https");
      } else {
         this.isHttps = this.url.getProtocol().equalsIgnoreCase("https");
         this.method = Objects.requireNonNull(method, "method");
         this.connectTimeoutMillis = connectTimeoutMillis;
         this.readTimeoutMillis = readTimeoutMillis;
         this.headers = headers != null ? headers : Property.EMPTY_ARRAY;
         this.sslConfiguration = sslConfiguration;
         if (this.sslConfiguration != null && !this.isHttps) {
            throw new ConfigurationException("SSL configuration can only be specified with URL scheme https");
         } else {
            this.verifyHostname = verifyHostname;
         }
      }
   }

   @SuppressFBWarnings(
      value = {"URLCONNECTION_SSRF_FD"},
      justification = "This connection URL is specified in a configuration file."
   )
   @Override
   public void send(final Layout<?> layout, final LogEvent event) throws IOException {
      HttpURLConnection urlConnection = (HttpURLConnection)this.url.openConnection();
      urlConnection.setAllowUserInteraction(false);
      urlConnection.setDoOutput(true);
      urlConnection.setDoInput(true);
      urlConnection.setRequestMethod(this.method);
      if (this.connectTimeoutMillis > 0) {
         urlConnection.setConnectTimeout(this.connectTimeoutMillis);
      }

      if (this.readTimeoutMillis > 0) {
         urlConnection.setReadTimeout(this.readTimeoutMillis);
      }

      if (layout.getContentType() != null) {
         urlConnection.setRequestProperty("Content-Type", layout.getContentType());
      }

      for (Property header : this.headers) {
         urlConnection.setRequestProperty(header.getName(), header.evaluate(this.getConfiguration().getStrSubstitutor()));
      }

      if (this.sslConfiguration != null) {
         SSLContext sslContext = this.sslConfiguration.getSslContext();
         if (sslContext != null) {
            ((HttpsURLConnection)urlConnection).setSSLSocketFactory(sslContext.getSocketFactory());
         }
      }

      if (this.isHttps && !this.verifyHostname) {
         ((HttpsURLConnection)urlConnection).setHostnameVerifier(LaxHostnameVerifier.INSTANCE);
      }

      byte[] msg = layout.toByteArray(event);
      urlConnection.setFixedLengthStreamingMode(msg.length);
      urlConnection.connect();
      OutputStream os = urlConnection.getOutputStream();

      try {
         os.write(msg);
      } catch (Throwable var15) {
         if (os != null) {
            try {
               os.close();
            } catch (Throwable var13) {
               var15.addSuppressed(var13);
            }
         }

         throw var15;
      }

      if (os != null) {
         os.close();
      }

      byte[] buffer = new byte[1024];

      try {
         InputStream is = urlConnection.getInputStream();

         try {
            while (-1 != is.read(buffer)) {
            }
         } catch (Throwable var16) {
            if (is != null) {
               try {
                  is.close();
               } catch (Throwable var12) {
                  var16.addSuppressed(var12);
               }
            }

            throw var16;
         }

         if (is != null) {
            is.close();
         }
      } catch (IOException var17) {
         StringBuilder errorMessage = new StringBuilder();
         InputStream es = urlConnection.getErrorStream();

         try {
            errorMessage.append(urlConnection.getResponseCode());
            if (urlConnection.getResponseMessage() != null) {
               errorMessage.append(' ').append(urlConnection.getResponseMessage());
            }

            if (es != null) {
               errorMessage.append(" - ");

               int n;
               while (-1 != (n = es.read(buffer))) {
                  errorMessage.append(new String(buffer, 0, n, CHARSET));
               }
            }
         } catch (Throwable var14) {
            if (es != null) {
               try {
                  es.close();
               } catch (Throwable var11) {
                  var14.addSuppressed(var11);
               }
            }

            throw var14;
         }

         if (es != null) {
            es.close();
         }

         if (urlConnection.getResponseCode() > -1) {
            throw new IOException(errorMessage.toString());
         } else {
            throw var17;
         }
      }
   }
}
