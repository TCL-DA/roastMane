package okhttp3.internal.tls

import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.Arrays
import javax.net.ssl.X509TrustManager
import okhttp3.internal.platform.Platform

abstract class CertificateChainCleaner {
   @JvmStatic
   public CertificateChainCleaner.Companion Companion = new CertificateChainCleaner.Companion(null);

   @Throws(javax/net/ssl/SSLPeerUnverifiedException::class)
   public abstract fun clean(chain: List<Certificate>, hostname: String): List<Certificate> {
   }

   companion object Companion private constructor() {
      public fun get(trustManager: X509TrustManager): CertificateChainCleaner {
         return Platform.Companion.get().buildCertificateChainCleaner(trustManager);
      }

      public fun get(vararg caCerts: X509Certificate): CertificateChainCleaner {
         return new BasicCertificateChainCleaner(new BasicTrustRootIndex(Arrays.copyOf(caCerts, caCerts.length)));
      }
   }
}
