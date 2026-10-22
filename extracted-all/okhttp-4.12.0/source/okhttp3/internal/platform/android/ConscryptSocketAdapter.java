package okhttp3.internal.platform.android

import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Protocol
import okhttp3.internal.platform.ConscryptPlatform
import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.android.DeferredSocketAdapter.Factory
import org.conscrypt.Conscrypt
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nConscryptSocketAdapter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConscryptSocketAdapter.kt\nokhttp3/internal/platform/android/ConscryptSocketAdapter\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,64:1\n37#2,2:65\n*S KotlinDebug\n*F\n+ 1 ConscryptSocketAdapter.kt\nokhttp3/internal/platform/android/ConscryptSocketAdapter\n*L\n51#1:65,2\n*E\n"])
class ConscryptSocketAdapter : SocketAdapter {
   @JvmStatic
   public ConscryptSocketAdapter.Companion Companion = new ConscryptSocketAdapter.Companion(null);
   @JvmStatic
   private DeferredSocketAdapter.Factory factory = new DeferredSocketAdapter.Factory() {
      @Override
      public boolean matchesSocket(@NotNull SSLSocket sslSocket) {
         return ConscryptPlatform.Companion.isSupported() && Conscrypt.isConscrypt(sslSocket);
      }

      @NotNull
      @Override
      public SocketAdapter create(@NotNull SSLSocket sslSocket) {
         return new ConscryptSocketAdapter();
      }
   };


   public override fun matchesSocket(sslSocket: SSLSocket): Boolean {
      return Conscrypt.isConscrypt(sslSocket);
   }

   public override fun isSupported(): Boolean {
      return ConscryptPlatform.Companion.isSupported();
   }

   public override fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      return if (this.matchesSocket(sslSocket)) Conscrypt.getApplicationProtocol(sslSocket) else null;
   }

   public override fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
      if (this.matchesSocket(sslSocket)) {
         Conscrypt.setUseSessionTickets(sslSocket, true);
         Conscrypt.setApplicationProtocols(sslSocket, Platform.Companion.alpnProtocolNames(protocols).toArray(new java.lang.String[0]));
      }
   }

   override fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
      return SocketAdapter.DefaultImpls.trustManager(this, sslSocketFactory);
   }

   override fun matchesSocketFactory(sslSocketFactory: SSLSocketFactory): Boolean {
      return SocketAdapter.DefaultImpls.matchesSocketFactory(this, sslSocketFactory);
   }

   companion object Companion private constructor() {
      public final val factory: Factory

   }
}
