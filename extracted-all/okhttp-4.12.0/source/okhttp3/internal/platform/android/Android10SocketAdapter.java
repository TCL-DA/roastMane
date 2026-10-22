package okhttp3.internal.platform.android

import android.annotation.SuppressLint
import android.net.ssl.SSLSockets
import android.os.Build.VERSION
import java.io.IOException
import javax.net.ssl.SSLParameters
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Protocol
import okhttp3.internal.SuppressSignatureCheck
import okhttp3.internal.platform.Platform

@SuppressLint(["NewApi"])
@SuppressSignatureCheck
@SourceDebugExtension(["SMAP\nAndroid10SocketAdapter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Android10SocketAdapter.kt\nokhttp3/internal/platform/android/Android10SocketAdapter\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,77:1\n37#2,2:78\n*S KotlinDebug\n*F\n+ 1 Android10SocketAdapter.kt\nokhttp3/internal/platform/android/Android10SocketAdapter\n*L\n60#1:78,2\n*E\n"])
class Android10SocketAdapter : SocketAdapter {
   @JvmStatic
   public Android10SocketAdapter.Companion Companion = new Android10SocketAdapter.Companion(null);


   public override fun matchesSocket(sslSocket: SSLSocket): Boolean {
      return SSLSockets.isSupportedSocket(sslSocket);
   }

   public override fun isSupported(): Boolean {
      return Companion.isSupported();
   }

   @SuppressLint(["NewApi"])
   public override fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      var protocol: java.lang.String = sslSocket.getApplicationProtocol();
      return if (protocol == null || protocol == "") null else protocol;
   }

   @SuppressLint(["NewApi"])
   public override fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
      try {
         SSLSockets.setUseSessionTickets(sslSocket, true);
         var iae: SSLParameters = sslSocket.getSSLParameters();
         iae.setApplicationProtocols(Platform.Companion.alpnProtocolNames(protocols).toArray(new java.lang.String[0]));
         sslSocket.setSSLParameters(iae);
      } catch (IllegalArgumentException var8) {
         throw new IOException("Android internal error", var8);
      }
   }

   override fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
      return SocketAdapter.DefaultImpls.trustManager(this, sslSocketFactory);
   }

   override fun matchesSocketFactory(sslSocketFactory: SSLSocketFactory): Boolean {
      return SocketAdapter.DefaultImpls.matchesSocketFactory(this, sslSocketFactory);
   }

   @SuppressSignatureCheck
   companion object Companion private constructor() {
      public fun buildIfSupported(): SocketAdapter? {
         return if (this.isSupported()) new Android10SocketAdapter() else null;
      }

      public fun isSupported(): Boolean {
         return Platform.Companion.isAndroid() && VERSION.SDK_INT >= 29;
      }
   }
}
