package okhttp3.internal.platform

import javax.net.ssl.SSLParameters
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Protocol
import okhttp3.internal.SuppressSignatureCheck

@SourceDebugExtension(["SMAP\nJdk9Platform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Jdk9Platform.kt\nokhttp3/internal/platform/Jdk9Platform\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,89:1\n37#2,2:90\n*S KotlinDebug\n*F\n+ 1 Jdk9Platform.kt\nokhttp3/internal/platform/Jdk9Platform\n*L\n36#1:90,2\n*E\n"])
open class Jdk9Platform : Platform {
   @JvmStatic
   public Jdk9Platform.Companion Companion = new Jdk9Platform.Companion(null);
   @JvmStatic
   private boolean isAvailable;


   @SuppressSignatureCheck
   public override fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
      var sslParameters: SSLParameters = sslSocket.getSSLParameters();
      sslParameters.setApplicationProtocols(Platform.Companion.alpnProtocolNames(protocols).toArray(new java.lang.String[0]));
      sslSocket.setSSLParameters(sslParameters);
   }

   @SuppressSignatureCheck
   public override fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      try {
         var e: java.lang.String = sslSocket.getApplicationProtocol();
         return if (e == null || e == "") null else e;
      } catch (UnsupportedOperationException var3) {
         return null;
      }
   }

   public override fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
      throw new UnsupportedOperationException("clientBuilder.sslSocketFactory(SSLSocketFactory) not supported on JDK 9+");
   }

   @JvmStatic
   fun {
      var jdkVersion: java.lang.String = System.getProperty("java.specification.version");
      var majorVersion: Int = if (jdkVersion != null) StringsKt.toIntOrNull(jdkVersion) else null;
      var var10000: Boolean;
      if (majorVersion != null) {
         var10000 = majorVersion >= 9;
      } else {
         var var2: Boolean;
         try {
            SSLSocket::class.java.getMethod("getApplicationProtocol");
            var2 = true;
         } catch (NoSuchMethodException var4) {
            var2 = false;
         }

         var10000 = var2;
      }

      isAvailable = var10000;
   }

   companion object Companion private constructor() {
      public final val isAvailable: Boolean


      public fun buildIfSupported(): Jdk9Platform? {
         return if (this.isAvailable()) new Jdk9Platform() else null;
      }
   }
}
