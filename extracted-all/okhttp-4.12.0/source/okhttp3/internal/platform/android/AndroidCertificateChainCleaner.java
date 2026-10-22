package okhttp3.internal.platform.android

import android.net.http.X509TrustManagerExtensions
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.SuppressSignatureCheck
import okhttp3.internal.tls.CertificateChainCleaner

@SuppressSignatureCheck
@SourceDebugExtension(["SMAP\nAndroidCertificateChainCleaner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AndroidCertificateChainCleaner.kt\nokhttp3/internal/platform/android/AndroidCertificateChainCleaner\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,74:1\n37#2,2:75\n1#3:77\n*S KotlinDebug\n*F\n+ 1 AndroidCertificateChainCleaner.kt\nokhttp3/internal/platform/android/AndroidCertificateChainCleaner\n*L\n43#1:75,2\n*E\n"])
internal class AndroidCertificateChainCleaner(trustManager: X509TrustManager, x509TrustManagerExtensions: X509TrustManagerExtensions) : CertificateChainCleaner {
   @JvmStatic
   public AndroidCertificateChainCleaner.Companion Companion = new AndroidCertificateChainCleaner.Companion(null);

   private final val trustManager: X509TrustManager
   private final val x509TrustManagerExtensions: X509TrustManagerExtensions

   init {
      this.trustManager = trustManager;
      this.x509TrustManagerExtensions = x509TrustManagerExtensions;
   }

   @SuppressSignatureCheck
   @Throws(javax/net/ssl/SSLPeerUnverifiedException::class)
   public override fun clean(chain: List<Certificate>, hostname: String): List<Certificate> {
      var certificates: Array<X509Certificate> = chain.toArray(new X509Certificate[0]);

      try {
         var var10000: java.util.List = this.x509TrustManagerExtensions.checkServerTrusted(certificates, "RSA", hostname);
         return var10000;
      } catch (CertificateException var8) {
         var var9: SSLPeerUnverifiedException = new SSLPeerUnverifiedException(var8.getMessage());
         var9.initCause(var8);
         throw var9 as java.lang.Throwable;
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is AndroidCertificateChainCleaner && (other as AndroidCertificateChainCleaner).trustManager === this.trustManager;
   }

   public override fun hashCode(): Int {
      return System.identityHashCode(this.trustManager);
   }

   companion object Companion private constructor() {
      @SuppressSignatureCheck
      public fun buildIfSupported(trustManager: X509TrustManager): AndroidCertificateChainCleaner? {
         var var3: X509TrustManagerExtensions;
         try {
            var3 = new X509TrustManagerExtensions(trustManager);
         } catch (IllegalArgumentException var5) {
            var3 = null;
         }

         return if (var3 != null) new AndroidCertificateChainCleaner(trustManager, var3) else null;
      }
   }
}
