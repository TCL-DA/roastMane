package org.glassfish.jersey.client;

import java.security.KeyStore;
import java.util.Iterator;
import java.util.function.Supplier;
import javax.net.ssl.SSLContext;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.client.spi.DefaultSslContextProvider;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.internal.util.collection.Values;

public final class SslContextClientBuilder implements Supplier<SSLContext> {
   private SslConfigurator sslConfigurator = null;
   private SSLContext sslContext = null;
   private DefaultSslContextProvider defaultSslContextProvider = null;
   private final Supplier<SSLContext> suppliedValue = Values.lazy(() -> this.supply());
   private static final DefaultSslContextProvider DEFAULT_SSL_CONTEXT_PROVIDER = new DefaultSslContextProvider() {
      @Override
      public SSLContext getDefaultSslContext() {
         return SslConfigurator.getDefaultContext();
      }
   };

   public SslContextClientBuilder sslContext(SSLContext sslContext) {
      if (sslContext == null) {
         throw new NullPointerException(LocalizationMessages.NULL_SSL_CONTEXT());
      } else {
         this.sslContext = sslContext;
         this.sslConfigurator = null;
         return this;
      }
   }

   public SslContextClientBuilder keyStore(KeyStore keyStore, char[] password) {
      if (keyStore == null) {
         throw new NullPointerException(LocalizationMessages.NULL_KEYSTORE());
      } else if (password == null) {
         throw new NullPointerException(LocalizationMessages.NULL_KEYSTORE_PASWORD());
      } else {
         if (this.sslConfigurator == null) {
            this.sslConfigurator = SslConfigurator.newInstance();
         }

         this.sslConfigurator.keyStore(keyStore);
         this.sslConfigurator.keyPassword(password);
         this.sslContext = null;
         return this;
      }
   }

   public SslContextClientBuilder trustStore(KeyStore trustStore) {
      if (trustStore == null) {
         throw new NullPointerException(LocalizationMessages.NULL_TRUSTSTORE());
      } else {
         if (this.sslConfigurator == null) {
            this.sslConfigurator = SslConfigurator.newInstance();
         }

         this.sslConfigurator.trustStore(trustStore);
         this.sslContext = null;
         return this;
      }
   }

   public SslContextClientBuilder keyStore(KeyStore keyStore, String password) {
      return this.keyStore(keyStore, password.toCharArray());
   }

   public boolean isDefaultSslContext() {
      return this.sslContext == null && this.sslConfigurator == null;
   }

   public SSLContext get() {
      return this.suppliedValue.get();
   }

   public SSLContext build() {
      return this.suppliedValue.get();
   }

   protected SslContextClientBuilder defaultSslContextProvider(DefaultSslContextProvider defaultSslContextProvider) {
      this.defaultSslContextProvider = defaultSslContextProvider;
      return this;
   }

   private SSLContext supply() {
      SSLContext providedValue;
      if (this.sslContext != null) {
         providedValue = this.sslContext;
      } else if (this.sslConfigurator != null) {
         SslConfigurator sslConfiguratorCopy = this.sslConfigurator.copy();
         providedValue = sslConfiguratorCopy.createSSLContext();
      } else {
         providedValue = null;
      }

      SSLContext returnValue;
      if (providedValue == null) {
         if (this.defaultSslContextProvider != null) {
            returnValue = this.defaultSslContextProvider.getDefaultSslContext();
         } else {
            Iterator<DefaultSslContextProvider> iterator = ServiceFinder.find(DefaultSslContextProvider.class).iterator();
            DefaultSslContextProvider lookedUpSslContextProvider;
            if (iterator.hasNext()) {
               lookedUpSslContextProvider = iterator.next();
            } else {
               lookedUpSslContextProvider = DEFAULT_SSL_CONTEXT_PROVIDER;
            }

            returnValue = lookedUpSslContextProvider.getDefaultSslContext();
         }
      } else {
         returnValue = providedValue;
      }

      return returnValue;
   }
}
