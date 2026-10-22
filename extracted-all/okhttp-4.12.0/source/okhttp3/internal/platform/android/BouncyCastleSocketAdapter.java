package okhttp3.internal.platform.android

import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Protocol
import okhttp3.internal.platform.BouncyCastlePlatform
import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.android.DeferredSocketAdapter.Factory
import org.bouncycastle.jsse.BCSSLParameters
import org.bouncycastle.jsse.BCSSLSocket
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nBouncyCastleSocketAdapter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BouncyCastleSocketAdapter.kt\nokhttp3/internal/platform/android/BouncyCastleSocketAdapter\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,68:1\n37#2,2:69\n*S KotlinDebug\n*F\n+ 1 BouncyCastleSocketAdapter.kt\nokhttp3/internal/platform/android/BouncyCastleSocketAdapter\n*L\n53#1:69,2\n*E\n"])
class BouncyCastleSocketAdapter : SocketAdapter {
   @JvmStatic
   public BouncyCastleSocketAdapter.Companion Companion = new BouncyCastleSocketAdapter.Companion(null);
   @JvmStatic
   private DeferredSocketAdapter.Factory factory = new DeferredSocketAdapter.Factory() {
      @Override
      public boolean matchesSocket(@NotNull SSLSocket sslSocket) {
         return BouncyCastlePlatform.Companion.isSupported() && sslSocket is BCSSLSocket;
      }

      @NotNull
      @Override
      public SocketAdapter create(@NotNull SSLSocket sslSocket) {
         return new BouncyCastleSocketAdapter();
      }
   };


   public override fun matchesSocket(sslSocket: SSLSocket): Boolean {
      return sslSocket is BCSSLSocket;
   }

   public override fun isSupported(): Boolean {
      return BouncyCastlePlatform.Companion.isSupported();
   }

   public override fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      var protocol: java.lang.String = (sslSocket as BCSSLSocket).getApplicationProtocol();
      return if (protocol == null || protocol == "") null else protocol;
   }

   public override fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
      if (this.matchesSocket(sslSocket)) {
         var bcSocket: BCSSLSocket = sslSocket as BCSSLSocket;
         var sslParameters: BCSSLParameters = (sslSocket as BCSSLSocket).getParameters();
         sslParameters.setApplicationProtocols(Platform.Companion.alpnProtocolNames(protocols).toArray(new java.lang.String[0]));
         bcSocket.setParameters(sslParameters);
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
