package org.apache.logging.log4j.core.net.ssl;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Plugin(
   name = "Ssl",
   category = "Core",
   printObject = true
)
public class SslConfiguration {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private final String protocol;
   private final boolean verifyHostName;
   @Nullable
   private final KeyStoreConfiguration keyStoreConfig;
   @Nullable
   private final TrustStoreConfiguration trustStoreConfig;
   @Nullable
   private final transient SSLContext sslContext;

   private SslConfiguration(
      @Nullable final String protocol,
      final boolean verifyHostName,
      @Nullable final KeyStoreConfiguration keyStoreConfig,
      @Nullable final TrustStoreConfiguration trustStoreConfig
   ) {
      this.keyStoreConfig = keyStoreConfig;
      this.trustStoreConfig = trustStoreConfig;
      String effectiveProtocol = protocol == null ? "TLS" : protocol;
      this.protocol = effectiveProtocol;
      this.verifyHostName = verifyHostName;
      this.sslContext = createSslContext(effectiveProtocol, keyStoreConfig, trustStoreConfig);
   }

   public void clearSecrets() {
      if (this.keyStoreConfig != null) {
         this.keyStoreConfig.clearSecrets();
      }

      if (this.trustStoreConfig != null) {
         this.trustStoreConfig.clearSecrets();
      }
   }

   @Deprecated
   @Nullable
   public SSLSocketFactory getSslSocketFactory() {
      return this.sslContext != null ? this.sslContext.getSocketFactory() : null;
   }

   @Deprecated
   @Nullable
   public SSLServerSocketFactory getSslServerSocketFactory() {
      return this.sslContext != null ? this.sslContext.getServerSocketFactory() : null;
   }

   @Nullable
   private static SSLContext createDefaultSslContext(final String protocol) {
      try {
         return SSLContext.getDefault();
      } catch (NoSuchAlgorithmException var4) {
         LOGGER.error("Failed to create an `SSLContext` using the default configuration, falling back to creating an empty one", var4);

         try {
            SSLContext emptyContext = SSLContext.getInstance(protocol);
            emptyContext.init(new KeyManager[0], new TrustManager[0], null);
            return emptyContext;
         } catch (Exception var3) {
            LOGGER.error("Failed to create an empty `SSLContext`", var3);
            return null;
         }
      }
   }

   private static SSLContext createSslContext(final String protocol, final KeyStoreConfiguration keyStoreConfig, final TrustStoreConfiguration trustStoreConfig) {
      try {
         SSLContext sslContext = SSLContext.getInstance(protocol);
         KeyManager[] keyManagers = loadKeyManagers(keyStoreConfig);
         TrustManager[] trustManagers = loadTrustManagers(trustStoreConfig);
         sslContext.init(keyManagers, trustManagers, null);
         return sslContext;
      } catch (Exception var6) {
         LOGGER.error("Failed to create an `SSLContext` using the provided configuration, falling back to a default instance", var6);
         return createDefaultSslContext(protocol);
      }
   }

   @NullUnmarked
   private static KeyManager[] loadKeyManagers(@Nullable final KeyStoreConfiguration config) throws Exception {
      if (config == null) {
         return null;
      } else {
         KeyManagerFactory factory = KeyManagerFactory.getInstance(config.getKeyManagerFactoryAlgorithm());
         char[] password = config.getPasswordAsCharArray();

         try {
            factory.init(config.getKeyStore(), password);
         } finally {
            config.clearSecrets();
         }

         return factory.getKeyManagers();
      }
   }

   @NullUnmarked
   private static TrustManager[] loadTrustManagers(@Nullable final TrustStoreConfiguration config) throws Exception {
      if (config == null) {
         return null;
      } else {
         TrustManagerFactory factory = TrustManagerFactory.getInstance(config.getTrustManagerFactoryAlgorithm());
         factory.init(config.getKeyStore());
         return factory.getTrustManagers();
      }
   }

   @NullUnmarked
   public static SslConfiguration createSSLConfiguration(
      final String protocol, final KeyStoreConfiguration keyStoreConfig, final TrustStoreConfiguration trustStoreConfig
   ) {
      return new SslConfiguration(protocol, false, keyStoreConfig, trustStoreConfig);
   }

   @NullUnmarked
   @PluginFactory
   public static SslConfiguration createSSLConfiguration(
      @PluginAttribute("protocol") final String protocol,
      @PluginElement("KeyStore") final KeyStoreConfiguration keyStoreConfig,
      @PluginElement("TrustStore") final TrustStoreConfiguration trustStoreConfig,
      @PluginAttribute("verifyHostName") final boolean verifyHostName
   ) {
      return new SslConfiguration(protocol, verifyHostName, keyStoreConfig, trustStoreConfig);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.keyStoreConfig, this.protocol, this.sslContext, this.trustStoreConfig);
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         SslConfiguration other = (SslConfiguration)obj;
         if (!Objects.equals(this.protocol, other.protocol)) {
            return false;
         } else if (!Objects.equals(this.verifyHostName, other.verifyHostName)) {
            return false;
         } else {
            return !Objects.equals(this.keyStoreConfig, other.keyStoreConfig) ? false : Objects.equals(this.trustStoreConfig, other.trustStoreConfig);
         }
      }
   }

   public String getProtocol() {
      return this.protocol;
   }

   public boolean isVerifyHostName() {
      return this.verifyHostName;
   }

   @Nullable
   public KeyStoreConfiguration getKeyStoreConfig() {
      return this.keyStoreConfig;
   }

   @Nullable
   public TrustStoreConfiguration getTrustStoreConfig() {
      return this.trustStoreConfig;
   }

   @Nullable
   public SSLContext getSslContext() {
      return this.sslContext;
   }
}
