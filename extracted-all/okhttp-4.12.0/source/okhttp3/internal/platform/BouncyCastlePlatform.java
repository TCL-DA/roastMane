package okhttp3.internal.platform

import java.security.Provider
import java.util.Arrays
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Protocol
import org.bouncycastle.jsse.BCSSLParameters
import org.bouncycastle.jsse.BCSSLSocket
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider

@SourceDebugExtension(["SMAP\nBouncyCastlePlatform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BouncyCastlePlatform.kt\nokhttp3/internal/platform/BouncyCastlePlatform\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,97:1\n37#2,2:98\n*S KotlinDebug\n*F\n+ 1 BouncyCastlePlatform.kt\nokhttp3/internal/platform/BouncyCastlePlatform\n*L\n65#1:98,2\n*E\n"])
class BouncyCastlePlatform private constructor() : Platform {
   @JvmStatic
   public BouncyCastlePlatform.Companion Companion = new BouncyCastlePlatform.Companion(null);
   @JvmStatic
   private boolean isSupported;

   private final val provider: Provider = (new BouncyCastleJsseProvider()) as Provider


   public override fun newSSLContext(): SSLContext {
      var var10000: SSLContext = SSLContext.getInstance("TLS", this.provider);
      return var10000;
   }

   public override fun platformTrustManager(): X509TrustManager {
      var factory: TrustManagerFactory = TrustManagerFactory.getInstance("PKIX", "BCJSSE");
      factory.init(null);
      var var10000: Array<TrustManager> = factory.getTrustManagers();
      if (var10000.length != 1 || var10000[0] !is X509TrustManager) {
         var var6: StringBuilder = new StringBuilder().append("Unexpected default trust managers: ");
         var var10001: java.lang.String = Arrays.toString((Object[])var10000);
         throw new IllegalStateException(var6.append(var10001).toString().toString());
      } else {
         var var5: TrustManager = var10000[0];
         return var5 as X509TrustManager;
      }
   }

   public override fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
      throw new UnsupportedOperationException("clientBuilder.sslSocketFactory(SSLSocketFactory) not supported with BouncyCastle");
   }

   public override fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
      if (sslSocket is BCSSLSocket) {
         var sslParameters: BCSSLParameters = (sslSocket as BCSSLSocket).getParameters();
         sslParameters.setApplicationProtocols(Platform.Companion.alpnProtocolNames(protocols).toArray(new java.lang.String[0]));
         (sslSocket as BCSSLSocket).setParameters(sslParameters);
      } else {
         super.configureTlsExtensions(sslSocket, hostname, protocols);
      }
   }

   public override fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      var var10000: java.lang.String;
      if (sslSocket is BCSSLSocket) {
         var protocol: java.lang.String = (sslSocket as BCSSLSocket).getApplicationProtocol();
         var10000 = if (protocol == null || protocol == "") null else protocol;
      } else {
         var10000 = super.getSelectedProtocol(sslSocket);
      }

      return var10000;
   }

   @JvmStatic
   fun {
      var var0: Boolean;
      try {
         Class.forName("org.bouncycastle.jsse.provider.BouncyCastleJsseProvider", false, Companion.getClass().getClassLoader());
         var0 = true;
      } catch (ClassNotFoundException var2) {
         var0 = false;
      }

      isSupported = var0;
   }

   companion object Companion private constructor() {
      public final val isSupported: Boolean


      public fun buildIfSupported(): BouncyCastlePlatform? {
         return if (this.isSupported()) new BouncyCastlePlatform(null) else null;
      }
   }
}
