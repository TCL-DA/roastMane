package okhttp3.internal.platform.android

import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import okhttp3.Protocol

interface SocketAdapter {
   public abstract fun isSupported(): Boolean {
   }

   public open fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
   }

   public abstract fun matchesSocket(sslSocket: SSLSocket): Boolean {
   }

   public open fun matchesSocketFactory(sslSocketFactory: SSLSocketFactory): Boolean {
   }

   public abstract fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
   }

   public abstract fun getSelectedProtocol(sslSocket: SSLSocket): String? {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun trustManager(`$this`: SocketAdapter, sslSocketFactory: SSLSocketFactory): X509TrustManager? {
         return null;
      }

      @JvmStatic
      fun matchesSocketFactory(`$this`: SocketAdapter, sslSocketFactory: SSLSocketFactory): Boolean {
         return false;
      }
   }
}
