package io.netty.handler.ssl;

import io.netty.internal.tcnative.CertificateCallback;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.internal.SuppressJava6Requirement;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map.Entry;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public final class ReferenceCountedOpenSslClientContext extends ReferenceCountedOpenSslContext {
   private static final Set<String> SUPPORTED_KEY_TYPES = Collections.unmodifiableSet(
      new LinkedHashSet<>(Arrays.asList("RSA", "DH_RSA", "EC", "EC_RSA", "EC_EC"))
   );
   private final OpenSslSessionContext sessionContext;

   ReferenceCountedOpenSslClientContext(
      X509Certificate[] trustCertCollection,
      TrustManagerFactory trustManagerFactory,
      X509Certificate[] keyCertChain,
      PrivateKey key,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      Iterable<String> ciphers,
      CipherSuiteFilter cipherFilter,
      ApplicationProtocolConfig apn,
      String[] protocols,
      long sessionCacheSize,
      long sessionTimeout,
      boolean enableOcsp,
      String keyStore,
      Entry<SslContextOption<?>, Object>... options
   ) throws SSLException {
      super(ciphers, cipherFilter, toNegotiator(apn), 0, keyCertChain, ClientAuth.NONE, protocols, false, enableOcsp, true, options);
      boolean success = false;

      try {
         this.sessionContext = newSessionContext(
            this,
            this.ctx,
            this.engineMap,
            trustCertCollection,
            trustManagerFactory,
            keyCertChain,
            key,
            keyPassword,
            keyManagerFactory,
            keyStore,
            sessionCacheSize,
            sessionTimeout
         );
         success = true;
      } finally {
         if (!success) {
            this.release();
         }
      }
   }

   @Override
   public OpenSslSessionContext sessionContext() {
      return this.sessionContext;
   }

   static OpenSslSessionContext newSessionContext(
      ReferenceCountedOpenSslContext thiz,
      long ctx,
      OpenSslEngineMap engineMap,
      X509Certificate[] trustCertCollection,
      TrustManagerFactory trustManagerFactory,
      X509Certificate[] keyCertChain,
      PrivateKey key,
      String keyPassword,
      KeyManagerFactory keyManagerFactory,
      String keyStore,
      long sessionCacheSize,
      long sessionTimeout
   ) throws SSLException {
      if ((key != null || keyCertChain == null) && (key == null || keyCertChain != null)) {
         OpenSslKeyMaterialProvider keyMaterialProvider = null;

         ReferenceCountedOpenSslClientContext.OpenSslClientSessionContext var29;
         try {
            try {
               if (!OpenSsl.useKeyManagerFactory()) {
                  if (keyManagerFactory != null) {
                     throw new IllegalArgumentException("KeyManagerFactory not supported");
                  }

                  if (keyCertChain != null) {
                     setKeyMaterial(ctx, keyCertChain, key, keyPassword);
                  }
               } else {
                  if (keyManagerFactory == null && keyCertChain != null) {
                     char[] keyPasswordChars = keyStorePassword(keyPassword);
                     KeyStore ks = buildKeyStore(keyCertChain, key, keyPasswordChars, keyStore);
                     if (ks.aliases().hasMoreElements()) {
                        keyManagerFactory = new OpenSslX509KeyManagerFactory();
                     } else {
                        keyManagerFactory = new OpenSslCachingX509KeyManagerFactory(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()));
                     }

                     keyManagerFactory.init(ks, keyPasswordChars);
                     keyMaterialProvider = providerFor(keyManagerFactory, keyPassword);
                  } else if (keyManagerFactory != null) {
                     keyMaterialProvider = providerFor(keyManagerFactory, keyPassword);
                  }

                  if (keyMaterialProvider != null) {
                     OpenSslKeyMaterialManager materialManager = new OpenSslKeyMaterialManager(keyMaterialProvider);
                     SSLContext.setCertificateCallback(
                        ctx, new ReferenceCountedOpenSslClientContext.OpenSslClientCertificateCallback(engineMap, materialManager)
                     );
                  }
               }
            } catch (Exception var23) {
               throw new SSLException("failed to set certificate and key", var23);
            }

            SSLContext.setVerify(ctx, 1, 10);

            try {
               if (trustCertCollection != null) {
                  trustManagerFactory = buildTrustManagerFactory(trustCertCollection, trustManagerFactory, keyStore);
               } else if (trustManagerFactory == null) {
                  trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                  trustManagerFactory.init((KeyStore)null);
               }

               X509TrustManager manager = chooseTrustManager(trustManagerFactory.getTrustManagers());
               setVerifyCallback(ctx, engineMap, manager);
            } catch (Exception var22) {
               if (keyMaterialProvider != null) {
                  keyMaterialProvider.destroy();
               }

               throw new SSLException("unable to setup trustmanager", var22);
            }

            ReferenceCountedOpenSslClientContext.OpenSslClientSessionContext context = new ReferenceCountedOpenSslClientContext.OpenSslClientSessionContext(
               thiz, keyMaterialProvider
            );
            context.setSessionCacheEnabled(CLIENT_ENABLE_SESSION_CACHE);
            if (sessionCacheSize > 0L) {
               context.setSessionCacheSize((int)Math.min(sessionCacheSize, 2147483647L));
            }

            if (sessionTimeout > 0L) {
               context.setSessionTimeout((int)Math.min(sessionTimeout, 2147483647L));
            }

            if (CLIENT_ENABLE_SESSION_TICKET) {
               context.setTicketKeys(new OpenSslSessionTicketKey[0]);
            }

            keyMaterialProvider = null;
            var29 = context;
         } finally {
            if (keyMaterialProvider != null) {
               keyMaterialProvider.destroy();
            }
         }

         return var29;
      } else {
         throw new IllegalArgumentException("Either both keyCertChain and key needs to be null or none of them");
      }
   }

   @SuppressJava6Requirement(
      reason = "Guarded by java version check"
   )
   private static void setVerifyCallback(long ctx, OpenSslEngineMap engineMap, X509TrustManager manager) {
      if (useExtendedTrustManager(manager)) {
         SSLContext.setCertVerifyCallback(
            ctx, new ReferenceCountedOpenSslClientContext.ExtendedTrustManagerVerifyCallback(engineMap, (X509ExtendedTrustManager)manager)
         );
      } else {
         SSLContext.setCertVerifyCallback(ctx, new ReferenceCountedOpenSslClientContext.TrustManagerVerifyCallback(engineMap, manager));
      }
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   private static final class ExtendedTrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
      private final X509ExtendedTrustManager manager;

      ExtendedTrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509ExtendedTrustManager manager) {
         super(engineMap);
         this.manager = manager;
      }

      @Override
      void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
         this.manager.checkServerTrusted(peerCerts, auth, engine);
      }
   }

   private static final class OpenSslClientCertificateCallback implements CertificateCallback {
      private final OpenSslEngineMap engineMap;
      private final OpenSslKeyMaterialManager keyManagerHolder;

      OpenSslClientCertificateCallback(OpenSslEngineMap engineMap, OpenSslKeyMaterialManager keyManagerHolder) {
         this.engineMap = engineMap;
         this.keyManagerHolder = keyManagerHolder;
      }

      public void handle(long ssl, byte[] keyTypeBytes, byte[][] asn1DerEncodedPrincipals) throws Exception {
         ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
         if (engine != null) {
            try {
               Set<String> keyTypesSet = supportedClientKeyTypes(keyTypeBytes);
               String[] keyTypes = keyTypesSet.toArray(new String[0]);
               X500Principal[] issuers;
               if (asn1DerEncodedPrincipals == null) {
                  issuers = null;
               } else {
                  issuers = new X500Principal[asn1DerEncodedPrincipals.length];

                  for (int i = 0; i < asn1DerEncodedPrincipals.length; i++) {
                     issuers[i] = new X500Principal(asn1DerEncodedPrincipals[i]);
                  }
               }

               this.keyManagerHolder.setKeyMaterialClientSide(engine, keyTypes, issuers);
            } catch (Throwable var10) {
               engine.initHandshakeException(var10);
               if (var10 instanceof Exception) {
                  throw (Exception)var10;
               } else {
                  throw new SSLException(var10);
               }
            }
         }
      }

      private static Set<String> supportedClientKeyTypes(byte[] clientCertificateTypes) {
         if (clientCertificateTypes == null) {
            return ReferenceCountedOpenSslClientContext.SUPPORTED_KEY_TYPES;
         } else {
            Set<String> result = new HashSet<>(clientCertificateTypes.length);

            for (byte keyTypeCode : clientCertificateTypes) {
               String keyType = clientKeyType(keyTypeCode);
               if (keyType != null) {
                  result.add(keyType);
               }
            }

            return result;
         }
      }

      private static String clientKeyType(byte clientCertificateType) {
         switch (clientCertificateType) {
            case 1:
               return "RSA";
            case 3:
               return "DH_RSA";
            case 64:
               return "EC";
            case 65:
               return "EC_RSA";
            case 66:
               return "EC_EC";
            default:
               return null;
         }
      }
   }

   static final class OpenSslClientSessionContext extends OpenSslSessionContext {
      OpenSslClientSessionContext(ReferenceCountedOpenSslContext context, OpenSslKeyMaterialProvider provider) {
         super(context, provider, SSL.SSL_SESS_CACHE_CLIENT, new OpenSslClientSessionCache(context.engineMap));
      }
   }

   private static final class TrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
      private final X509TrustManager manager;

      TrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509TrustManager manager) {
         super(engineMap);
         this.manager = manager;
      }

      @Override
      void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
         this.manager.checkServerTrusted(peerCerts, auth);
      }
   }
}
