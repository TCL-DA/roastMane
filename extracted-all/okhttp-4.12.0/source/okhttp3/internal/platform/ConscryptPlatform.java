package okhttp3.internal.platform

import java.security.Provider
import java.security.cert.X509Certificate
import java.util.Arrays
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Protocol
import org.conscrypt.Conscrypt
import org.conscrypt.ConscryptHostnameVerifier
import org.conscrypt.Conscrypt.Version

@SourceDebugExtension(["SMAP\nConscryptPlatform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConscryptPlatform.kt\nokhttp3/internal/platform/ConscryptPlatform\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,141:1\n37#2,2:142\n*S KotlinDebug\n*F\n+ 1 ConscryptPlatform.kt\nokhttp3/internal/platform/ConscryptPlatform\n*L\n89#1:142,2\n*E\n"])
class ConscryptPlatform private constructor() : Platform {
   @JvmStatic
   public ConscryptPlatform.Companion Companion = new ConscryptPlatform.Companion(null);
   @JvmStatic
   private boolean isSupported;

   private final val provider: Provider

   init {
      var var10001: Provider = Conscrypt.newProvider();
      this.provider = var10001;
   }

   public override fun newSSLContext(): SSLContext {
      var var10000: SSLContext = SSLContext.getInstance("TLS", this.provider);
      return var10000;
   }

   public override fun platformTrustManager(): X509TrustManager {
      var x509TrustManager: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      x509TrustManager.init(null);
      var var10000: Array<TrustManager> = x509TrustManager.getTrustManagers();
      if (var10000.length != 1 || var10000[0] !is X509TrustManager) {
         var var8: StringBuilder = new StringBuilder().append("Unexpected default trust managers: ");
         var var10001: java.lang.String = Arrays.toString((Object[])var10000);
         throw new IllegalStateException(var8.append(var10001).toString().toString());
      } else {
         var var7: TrustManager = var10000[0];
         var var5: X509TrustManager = var7 as X509TrustManager;
         Conscrypt.setHostnameVerifier(var7 as X509TrustManager, ConscryptPlatform.DisabledHostnameVerifier.INSTANCE);
         return var5;
      }
   }

   public override fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
      return null;
   }

   public override fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
      if (Conscrypt.isConscrypt(sslSocket)) {
         Conscrypt.setUseSessionTickets(sslSocket, true);
         Conscrypt.setApplicationProtocols(sslSocket, Platform.Companion.alpnProtocolNames(protocols).toArray(new java.lang.String[0]));
      } else {
         super.configureTlsExtensions(sslSocket, hostname, protocols);
      }
   }

   public override fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      return if (Conscrypt.isConscrypt(sslSocket)) Conscrypt.getApplicationProtocol(sslSocket) else super.getSelectedProtocol(sslSocket);
   }

   public override fun newSslSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
      var var2: SSLContext = this.newSSLContext();
      var2.init(null, new TrustManager[]{trustManager}, null);
      var var10000: SSLSocketFactory = var2.getSocketFactory();
      return var10000;
   }

   @JvmStatic
   fun {
      var var0: Boolean;
      try {
         Class.forName("org.conscrypt.Conscrypt$Version", false, Companion.getClass().getClassLoader());
         var0 = Conscrypt.isAvailable() && Companion.atLeastVersion(2, 1, 0);
      } catch (NoClassDefFoundError var2) {
         var0 = false;
      } catch (ClassNotFoundException var3) {
         var0 = false;
      }

      isSupported = var0;
   }

   companion object Companion private constructor() {
      public final val isSupported: Boolean


      public fun buildIfSupported(): ConscryptPlatform? {
         return if (this.isSupported()) new ConscryptPlatform(null) else null;
      }

      public fun atLeastVersion(major: Int, minor: Int, patch: Int): Boolean {
         var conscryptVersion: Version = Conscrypt.version();
         if (conscryptVersion.major() != major) {
            return conscryptVersion.major() > major;
         } else if (conscryptVersion.minor() != minor) {
            return conscryptVersion.minor() > minor;
         } else {
            return conscryptVersion.patch() >= patch;
         }
      }
   }

   internal object DisabledHostnameVerifier private constructor() : ConscryptHostnameVerifier {
      @JvmStatic
      public ConscryptPlatform.DisabledHostnameVerifier INSTANCE = new ConscryptPlatform.DisabledHostnameVerifier();


      public fun verify(hostname: String?, session: SSLSession?): Boolean {
         return true;
      }

      public open fun verify(certs: Array<out X509Certificate>?, hostname: String?, session: SSLSession?): Boolean {
         return true;
      }
   }
}
