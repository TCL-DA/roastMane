package okhttp3.internal.platform

import android.os.Build.VERSION
import android.security.NetworkSecurityPolicy
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.InetSocketAddress
import java.net.Socket
import java.security.cert.TrustAnchor
import java.security.cert.X509Certificate
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Protocol
import okhttp3.internal.SuppressSignatureCheck
import okhttp3.internal.platform.android.AndroidCertificateChainCleaner
import okhttp3.internal.platform.android.CloseGuard
import okhttp3.internal.platform.android.SocketAdapter
import okhttp3.internal.tls.CertificateChainCleaner
import okhttp3.internal.tls.TrustRootIndex

@SuppressSignatureCheck
@SourceDebugExtension(["SMAP\nAndroidPlatform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AndroidPlatform.kt\nokhttp3/internal/platform/AndroidPlatform\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,163:1\n766#2:164\n857#2,2:165\n1#3:167\n*S KotlinDebug\n*F\n+ 1 AndroidPlatform.kt\nokhttp3/internal/platform/AndroidPlatform\n*L\n52#1:164\n52#1:165,2\n*E\n"])
class AndroidPlatform : Platform {
   @JvmStatic
   public AndroidPlatform.Companion Companion = new AndroidPlatform.Companion(null);
   @JvmStatic
   private boolean isSupported;

   private final val closeGuard: CloseGuard
   private final val socketAdapters: List<SocketAdapter>


   @Throws(java/io/IOException::class)
   public override fun connectSocket(socket: Socket, address: InetSocketAddress, connectTimeout: Int) {
      try {
         socket.connect(address, connectTimeout);
      } catch (ClassCastException var5) {
         if (VERSION.SDK_INT == 26) {
            throw new IOException("Exception in connect", var5);
         } else {
            throw var5;
         }
      }
   }

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

   public override fun getStackTraceForCloseable(closer: String): Any? {
      return this.closeGuard.createAndOpen(closer);
   }

   public override fun logCloseableLeak(message: String, stackTrace: Any?) {
      if (!this.closeGuard.warnIfOpen(stackTrace)) {
         Platform.log$default(this, message, 5, null, 4, null);
      }
   }

   public override fun isCleartextTrafficPermitted(hostname: String): Boolean {
      return if (VERSION.SDK_INT >= 24)
         NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted(hostname)
         else
         VERSION.SDK_INT < 23 || NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted();
   }

   public override fun buildCertificateChainCleaner(trustManager: X509TrustManager): CertificateChainCleaner {
      var var10000: AndroidCertificateChainCleaner = AndroidCertificateChainCleaner.Companion.buildIfSupported(trustManager);
      return if (var10000 != null) var10000 else super.buildCertificateChainCleaner(trustManager);
   }

   public override fun buildTrustRootIndex(trustManager: X509TrustManager): TrustRootIndex {
      var method: TrustRootIndex;
      try {
         var var5: Method = trustManager.getClass().getDeclaredMethod("findTrustAnchorByIssuerAndSignature", X509Certificate.class);
         var5.setAccessible(true);
         method = new AndroidPlatform.CustomTrustRootIndex(trustManager, var5);
      } catch (NoSuchMethodException var4) {
         method = super.buildTrustRootIndex(trustManager);
      }

      return method;
   }

   @JvmStatic
   fun {
      var var10000: Boolean;
      if (!Platform.Companion.isAndroid()) {
         var10000 = false;
      } else if (VERSION.SDK_INT >= 30) {
         var10000 = false;
      } else {
         if (VERSION.SDK_INT < 21) {
            throw new IllegalStateException(("Expected Android API level 21+ but was " + VERSION.SDK_INT).toString());
         }

         var10000 = true;
      }

      isSupported = var10000;
   }

   companion object Companion private constructor() {
      public final val isSupported: Boolean


      public fun buildIfSupported(): Platform? {
         return if (this.isSupported()) new AndroidPlatform() else null;
      }
   }

   internal data class CustomTrustRootIndex(trustManager: X509TrustManager, findByIssuerAndSignatureMethod: Method) : TrustRootIndex {
      private final val findByIssuerAndSignatureMethod: Method
      private final val trustManager: X509TrustManager

      init {
         this.trustManager = trustManager;
         this.findByIssuerAndSignatureMethod = findByIssuerAndSignatureMethod;
      }

      public override fun findByIssuerAndSignature(cert: X509Certificate): X509Certificate? {
         var trustAnchor: X509Certificate;
         try {
            var var8: Any = this.findByIssuerAndSignatureMethod.invoke(this.trustManager, cert);
            trustAnchor = (var8 as TrustAnchor).getTrustedCert();
         } catch (IllegalAccessException var5) {
            throw new AssertionError("unable to get issues and signature", var5);
         } catch (InvocationTargetException var6) {
            trustAnchor = null;
         }

         return trustAnchor;
      }

      private operator fun component1(): X509TrustManager {
         return this.trustManager;
      }

      private operator fun component2(): Method {
         return this.findByIssuerAndSignatureMethod;
      }

      public fun copy(trustManager: X509TrustManager, findByIssuerAndSignatureMethod: Method): okhttp3.internal.platform.AndroidPlatform.CustomTrustRootIndex {
         return new AndroidPlatform.CustomTrustRootIndex(trustManager, findByIssuerAndSignatureMethod);
      }

      public override fun toString(): String {
         return "CustomTrustRootIndex(trustManager=" + this.trustManager + ", findByIssuerAndSignatureMethod=" + this.findByIssuerAndSignatureMethod + ')';
      }

      public override fun hashCode(): Int {
         return this.trustManager.hashCode() * 31 + this.findByIssuerAndSignatureMethod.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is AndroidPlatform.CustomTrustRootIndex) {
            return false;
         } else {
            var var2: AndroidPlatform.CustomTrustRootIndex = other as AndroidPlatform.CustomTrustRootIndex;
            if (!(this.trustManager == (other as AndroidPlatform.CustomTrustRootIndex).trustManager)) {
               return false;
            } else {
               return this.findByIssuerAndSignatureMethod == var2.findByIssuerAndSignatureMethod;
            }
         }
      }
   }
}
