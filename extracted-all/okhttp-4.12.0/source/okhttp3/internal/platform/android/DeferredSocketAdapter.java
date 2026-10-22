package okhttp3.internal.platform.android

import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import okhttp3.Protocol

class DeferredSocketAdapter(socketAdapterFactory: okhttp3.internal.platform.android.DeferredSocketAdapter.Factory) : SocketAdapter {
   private final var delegate: SocketAdapter?
   private final val socketAdapterFactory: okhttp3.internal.platform.android.DeferredSocketAdapter.Factory

   init {
      this.socketAdapterFactory = socketAdapterFactory;
   }

   public override fun isSupported(): Boolean {
      return true;
   }

   public override fun matchesSocket(sslSocket: SSLSocket): Boolean {
      return this.socketAdapterFactory.matchesSocket(sslSocket);
   }

   public override fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
      var var10000: SocketAdapter = this.getDelegate(sslSocket);
      if (var10000 != null) {
         var10000.configureTlsExtensions(sslSocket, hostname, protocols);
      }
   }

   public override fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      var var10000: SocketAdapter = this.getDelegate(sslSocket);
      return if (var10000 != null) var10000.getSelectedProtocol(sslSocket) else null;
   }

   @Synchronized
   private fun getDelegate(sslSocket: SSLSocket): SocketAdapter? {
      if (this.delegate == null && this.socketAdapterFactory.matchesSocket(sslSocket)) {
         this.delegate = this.socketAdapterFactory.create(sslSocket);
      }

      return this.delegate;
   }

   override fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
      return SocketAdapter.DefaultImpls.trustManager(this, sslSocketFactory);
   }

   override fun matchesSocketFactory(sslSocketFactory: SSLSocketFactory): Boolean {
      return SocketAdapter.DefaultImpls.matchesSocketFactory(this, sslSocketFactory);
   }

   interface Factory {
      public abstract fun matchesSocket(sslSocket: SSLSocket): Boolean {
      }

      public abstract fun create(sslSocket: SSLSocket): SocketAdapter {
      }
   }
}
