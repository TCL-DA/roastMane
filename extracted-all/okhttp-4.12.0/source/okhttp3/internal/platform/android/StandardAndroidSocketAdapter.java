package okhttp3.internal.platform.android

import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import okhttp3.internal.Util
import okhttp3.internal.platform.Platform

class StandardAndroidSocketAdapter(sslSocketClass: Class<in SSLSocket>, sslSocketFactoryClass: Class<in SSLSocketFactory>, paramClass: Class<*>) : AndroidSocketAdapter(
      sslSocketClass
   ) {
   @JvmStatic
   public StandardAndroidSocketAdapter.Companion Companion = new StandardAndroidSocketAdapter.Companion(null);

   private final val paramClass: Class<*>
   private final val sslSocketFactoryClass: Class<in SSLSocketFactory>

   init {
      this.sslSocketFactoryClass = sslSocketFactoryClass;
      this.paramClass = paramClass;
   }

   public override fun matchesSocketFactory(sslSocketFactory: SSLSocketFactory): Boolean {
      return this.sslSocketFactoryClass.isInstance(sslSocketFactory);
   }

   public override fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
      var context: Any = Util.readFieldOrNull(sslSocketFactory, this.paramClass, "sslParameters");
      var x509TrustManager: X509TrustManager = Util.readFieldOrNull(context, X509TrustManager.class, "x509TrustManager");
      var var10000: X509TrustManager = x509TrustManager;
      if (x509TrustManager == null) {
         var10000 = Util.readFieldOrNull(context, X509TrustManager.class, "trustManager");
      }

      return var10000;
   }

   companion object Companion private constructor() {
      public fun buildIfSupported(packageName: String): SocketAdapter? {
         var sslSocketClass: SocketAdapter;
         try {
            var var10000: Class = Class.forName(packageName + ".OpenSSLSocketImpl");
            var var7: Class = Class.forName(packageName + ".OpenSSLSocketFactoryImpl");
            var paramsClass: Class = Class.forName(packageName + ".SSLParametersImpl");
            sslSocketClass = new StandardAndroidSocketAdapter(var10000, var7, paramsClass);
         } catch (Exception var5) {
            Platform.Companion.get().log("unable to load android socket classes", 5, var5);
            sslSocketClass = null;
         }

         return sslSocketClass;
      }
   }
}
