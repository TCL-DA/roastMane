package okhttp3.internal.platform.android

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import okhttp3.Protocol
import okhttp3.internal.platform.AndroidPlatform
import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.android.DeferredSocketAdapter.Factory
import org.jetbrains.annotations.NotNull

open class AndroidSocketAdapter(sslSocketClass: Class<in SSLSocket>) : SocketAdapter {
   @JvmStatic
   public AndroidSocketAdapter.Companion Companion = new AndroidSocketAdapter.Companion(null);
   @JvmStatic
   private DeferredSocketAdapter.Factory playProviderFactory = Companion.factory("com.google.android.gms.org.conscrypt");

   private final val getAlpnSelectedProtocol: Method
   private final val setAlpnProtocols: Method
   private final val setHostname: Method
   private final val setUseSessionTickets: Method
   private final val sslSocketClass: Class<in SSLSocket>

   init {
      this.sslSocketClass = sslSocketClass;
      var var5: Method = this.sslSocketClass.getDeclaredMethod("setUseSessionTickets", boolean.class);
      this.setUseSessionTickets = var5;
      this.setHostname = this.sslSocketClass.getMethod("setHostname", java.lang.String.class);
      this.getAlpnSelectedProtocol = this.sslSocketClass.getMethod("getAlpnSelectedProtocol");
      this.setAlpnProtocols = this.sslSocketClass.getMethod("setAlpnProtocols", byte[].class);
   }

   public override fun isSupported(): Boolean {
      return AndroidPlatform.Companion.isSupported();
   }

   public override fun matchesSocket(sslSocket: SSLSocket): Boolean {
      return this.sslSocketClass.isInstance(sslSocket);
   }

   public override fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
      if (this.matchesSocket(sslSocket)) {
         try {
            this.setUseSessionTickets.invoke(sslSocket, true);
            if (hostname != null) {
               this.setHostname.invoke(sslSocket, hostname);
            }

            this.setAlpnProtocols.invoke(sslSocket, Platform.Companion.concatLengthPrefixed(protocols));
         } catch (IllegalAccessException var5) {
            throw new AssertionError(var5);
         } catch (InvocationTargetException var6) {
            throw new AssertionError(var6);
         }
      }
   }

   public override fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      if (!this.matchesSocket(sslSocket)) {
         return null;
      } else {
         var alpnResult: java.lang.String;
         try {
            var var7: ByteArray = this.getAlpnSelectedProtocol.invoke(sslSocket) as byte[];
            alpnResult = if (var7 != null) new java.lang.String(var7, Charsets.UTF_8) else null;
         } catch (IllegalAccessException var5) {
            throw new AssertionError(var5);
         } catch (InvocationTargetException var6) {
            var cause: java.lang.Throwable = var6.getCause();
            if (cause !is NullPointerException || !((cause as NullPointerException).getMessage() == "ssl == null")) {
               throw new AssertionError(var6);
            }

            alpnResult = null;
         }

         return alpnResult;
      }
   }

   override fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
      return SocketAdapter.DefaultImpls.trustManager(this, sslSocketFactory);
   }

   override fun matchesSocketFactory(sslSocketFactory: SSLSocketFactory): Boolean {
      return SocketAdapter.DefaultImpls.matchesSocketFactory(this, sslSocketFactory);
   }

   companion object Companion private constructor() {
      public final val playProviderFactory: Factory


      private fun build(actualSSLSocketClass: Class<in SSLSocket>): AndroidSocketAdapter {
         var possibleClass: Class = actualSSLSocketClass;

         while (possibleClass != null && !(possibleClass.getSimpleName() == "OpenSSLSocketImpl")) {
            possibleClass = possibleClass.getSuperclass();
            if (possibleClass == null) {
               throw new AssertionError("No OpenSSLSocketImpl superclass of socket of type " + actualSSLSocketClass);
            }
         }

         return new AndroidSocketAdapter(possibleClass);
      }

      public fun factory(packageName: String): Factory {
         return new DeferredSocketAdapter.Factory(packageName) {
            {
               this.$packageName = `$packageName`;
            }

            @Override
            public boolean matchesSocket(@NotNull SSLSocket sslSocket) {
               var var10000: java.lang.String = sslSocket.getClass().getName();
               return StringsKt.startsWith$default(var10000, this.$packageName + '.', false, 2, null);
            }

            @NotNull
            @Override
            public SocketAdapter create(@NotNull SSLSocket sslSocket) {
               return AndroidSocketAdapter.Companion.access$build(AndroidSocketAdapter.Companion, sslSocket.getClass());
            }
         };
      }
   }
}
