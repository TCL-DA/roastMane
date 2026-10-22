package okhttp3.internal.platform

import android.annotation.SuppressLint
import android.os.Build.VERSION
import android.security.NetworkSecurityPolicy
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Protocol
import okhttp3.internal.SuppressSignatureCheck
import okhttp3.internal.platform.android.AndroidCertificateChainCleaner
import okhttp3.internal.platform.android.SocketAdapter
import okhttp3.internal.tls.CertificateChainCleaner

@SuppressSignatureCheck
@SourceDebugExtension(["SMAP\nAndroid10Platform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Android10Platform.kt\nokhttp3/internal/platform/Android10Platform\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,72:1\n766#2:73\n857#2,2:74\n1#3:76\n*S KotlinDebug\n*F\n+ 1 Android10Platform.kt\nokhttp3/internal/platform/Android10Platform\n*L\n43#1:73\n43#1:74,2\n*E\n"])
class Android10Platform : Platform {
   @JvmStatic
   public Android10Platform.Companion Companion = new Android10Platform.Companion(null);
   @JvmStatic
   private boolean isSupported = Platform.Companion.isAndroid() && VERSION.SDK_INT >= 29;

   private final val socketAdapters: List<SocketAdapter>


   public override fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
      var var4: java.util.Iterator = this.socketAdapters.iterator();

      var var10000: Any;
      while (true) {
         if (var4.hasNext()) {
            var var5: Any = var4.next();
            if (!(var5 as SocketAdapter).matchesSocketFactory(sslSocketFactory)) {
               continue;
            }

            var10000 = var5;
            break;
         }

         var10000 = null;
         break;
      }

      return if (var10000 as SocketAdapter != null) (var10000 as SocketAdapter).trustManager(sslSocketFactory) else null;
   }

   public override fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
      var var6: java.util.Iterator = this.socketAdapters.iterator();

      var var10000: Any;
      while (true) {
         if (var6.hasNext()) {
            var var7: Any = var6.next();
            if (!(var7 as SocketAdapter).matchesSocket(sslSocket)) {
               continue;
            }

            var10000 = var7;
            break;
         }

         var10000 = null;
         break;
      }

      var var4: SocketAdapter = var10000 as SocketAdapter;
      if (var10000 as SocketAdapter != null) {
         var4.configureTlsExtensions(sslSocket, hostname, protocols);
      }
   }

   public override fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      var var3: java.util.Iterator = this.socketAdapters.iterator();

      var var10000: Any;
      while (true) {
         if (var3.hasNext()) {
            var var4: Any = var3.next();
            if (!(var4 as SocketAdapter).matchesSocket(sslSocket)) {
               continue;
            }

            var10000 = var4;
            break;
         }

         var10000 = null;
         break;
      }

      return if (var10000 as SocketAdapter != null) (var10000 as SocketAdapter).getSelectedProtocol(sslSocket) else null;
   }

   @SuppressLint(["NewApi"])
   public override fun isCleartextTrafficPermitted(hostname: String): Boolean {
      return NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted(hostname);
   }

   public override fun buildCertificateChainCleaner(trustManager: X509TrustManager): CertificateChainCleaner {
      var var10000: AndroidCertificateChainCleaner = AndroidCertificateChainCleaner.Companion.buildIfSupported(trustManager);
      return if (var10000 != null) var10000 else super.buildCertificateChainCleaner(trustManager);
   }

   companion object Companion private constructor() {
      public final val isSupported: Boolean


      public fun buildIfSupported(): Platform? {
         return if (this.isSupported()) new Android10Platform() else null;
      }
   }
}
