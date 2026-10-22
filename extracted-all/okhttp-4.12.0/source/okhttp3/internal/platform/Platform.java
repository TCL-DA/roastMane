package okhttp3.internal.platform

import java.net.InetSocketAddress
import java.net.Socket
import java.security.GeneralSecurityException
import java.security.Security
import java.security.cert.X509Certificate
import java.util.ArrayList
import java.util.Arrays
import java.util.logging.Level
import java.util.logging.Logger
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.internal.Util
import okhttp3.internal.platform.android.AndroidLog
import okhttp3.internal.tls.BasicCertificateChainCleaner
import okhttp3.internal.tls.BasicTrustRootIndex
import okhttp3.internal.tls.CertificateChainCleaner
import okhttp3.internal.tls.TrustRootIndex
import okio.Buffer

open class Platform {
   @JvmStatic
   public Platform.Companion Companion = new Platform.Companion(null);
   @Volatile
   @JvmStatic
   private Platform platform = Platform.Companion.access$findPlatform(Companion);
   @JvmStatic
   public int INFO = 4;
   @JvmStatic
   public int WARN = 5;
   @JvmStatic
   private Logger logger = Logger.getLogger(OkHttpClient.class.getName());


   public fun getPrefix(): String {
      return "OkHttp";
   }

   public open fun newSSLContext(): SSLContext {
      var var10000: SSLContext = SSLContext.getInstance("TLS");
      return var10000;
   }

   public open fun platformTrustManager(): X509TrustManager {
      var factory: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
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

   public open fun trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
      var sslContextClass: X509TrustManager;
      try {
         var var6: Class = Class.forName("sun.security.ssl.SSLContextImpl");
         var var10000: Any = Util.readFieldOrNull(sslSocketFactory, var6, "context");
         if (var10000 == null) {
            return null;
         }

         sslContextClass = Util.readFieldOrNull(var10000, X509TrustManager.class, "trustManager");
      } catch (ClassNotFoundException var4) {
         sslContextClass = null;
      } catch (RuntimeException var5) {
         if (!(var5.getClass().getName() == "java.lang.reflect.InaccessibleObjectException")) {
            throw var5;
         }

         sslContextClass = null;
      }

      return sslContextClass;
   }

   public open fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
   }

   public open fun afterHandshake(sslSocket: SSLSocket) {
   }

   public open fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      return null;
   }

   @Throws(java/io/IOException::class)
   public open fun connectSocket(socket: Socket, address: InetSocketAddress, connectTimeout: Int) {
      socket.connect(address, connectTimeout);
   }

   public open fun log(message: String, level: Int, t: Throwable?) {
      logger.log(if (level == 5) Level.WARNING else Level.INFO, message, t);
   }

   public open fun isCleartextTrafficPermitted(hostname: String): Boolean {
      return true;
   }

   public open fun getStackTraceForCloseable(closer: String): Any? {
      return if (logger.isLoggable(Level.FINE)) new java.lang.Throwable(closer) else null;
   }

   public open fun logCloseableLeak(message: String, stackTrace: Any?) {
      var logMessage: java.lang.String = message;
      if (stackTrace == null) {
         logMessage = message
            + " To see where this was allocated, set the OkHttpClient logger level to FINE: Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);";
      }

      this.log(logMessage, 5, stackTrace as java.lang.Throwable);
   }

   public open fun buildCertificateChainCleaner(trustManager: X509TrustManager): CertificateChainCleaner {
      return new BasicCertificateChainCleaner(this.buildTrustRootIndex(trustManager));
   }

   public open fun buildTrustRootIndex(trustManager: X509TrustManager): TrustRootIndex {
      var var10002: Array<X509Certificate> = trustManager.getAcceptedIssuers();
      return new BasicTrustRootIndex(Arrays.copyOf(var10002, var10002.length));
   }

   public open fun newSslSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
      try {
         var var3: SSLContext = this.newSSLContext();
         var3.init(null, new TrustManager[]{trustManager}, null);
         var var10000: SSLSocketFactory = var3.getSocketFactory();
         return var10000;
      } catch (GeneralSecurityException var7) {
         throw new AssertionError("No System TLS: " + var7, var7);
      }
   }

   public override fun toString(): String {
      var var10000: java.lang.String = this.getClass().getSimpleName();
      return var10000;
   }

   @JvmStatic
   fun get(): Platform {
      return Companion.get();
   }

   @SourceDebugExtension(["SMAP\nPlatform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Platform.kt\nokhttp3/internal/platform/Platform$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,286:1\n766#2:287\n857#2,2:288\n1549#2:290\n1620#2,3:291\n*S KotlinDebug\n*F\n+ 1 Platform.kt\nokhttp3/internal/platform/Platform$Companion\n*L\n193#1:287\n193#1:288,2\n193#1:290\n193#1:291,3\n*E\n"])
   companion object Companion private constructor() {
      public const val INFO: Int
      public const val WARN: Int
      public final val isAndroid: Boolean
         public final get() {
            return "Dalvik" == System.getProperty("java.vm.name");
         }

      private final val isBouncyCastlePreferred: Boolean
         private final get() {
            return "BC" == Security.getProviders()[0].getName();
         }

      private final val isConscryptPreferred: Boolean
         private final get() {
            return "Conscrypt" == Security.getProviders()[0].getName();
         }

      private final val isOpenJSSEPreferred: Boolean
         private final get() {
            return "OpenJSSE" == Security.getProviders()[0].getName();
         }

      private final val logger: Logger
      private final var platform: Platform


      public fun get(): Platform {
         return Platform.access$getPlatform$cp();
      }

      public fun resetForTests(platform: Platform) {
         Platform.access$setPlatform$cp(platform);
      }

      public fun alpnProtocolNames(protocols: List<Protocol>): List<String> {
         var `$this$map$iv`: java.lang.Iterable = protocols;
         var `destination$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$filter$iv) {
            if (`item$iv$iv` as Protocol != Protocol.HTTP_1_0) {
               `destination$iv$iv`.add(`item$iv$iv`);
            }
         }

         `$this$map$iv` = `destination$iv$iv` as java.util.List;
         `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`destination$iv$iv` as java.util.List, 10));

         for (Object item$iv$iv : $this$filter$iv) {
            `destination$iv$iv`.add((var17 as Protocol).toString());
         }

         return `destination$iv$iv` as java.util.List<java.lang.String>;
      }

      private fun findPlatform(): Platform {
         return if (this.isAndroid()) this.findAndroidPlatform() else this.findJvmPlatform();
      }

      private fun findAndroidPlatform(): Platform {
         AndroidLog.INSTANCE.enable();
         var var10000: Platform = Android10Platform.Companion.buildIfSupported();
         if (var10000 == null) {
            var10000 = AndroidPlatform.Companion.buildIfSupported();
         }

         return var10000;
      }

      private fun findJvmPlatform(): Platform {
         if (this.isConscryptPreferred()) {
            var jdk9: ConscryptPlatform = ConscryptPlatform.Companion.buildIfSupported();
            if (jdk9 != null) {
               return jdk9;
            }
         }

         if (this.isBouncyCastlePreferred()) {
            var var3: BouncyCastlePlatform = BouncyCastlePlatform.Companion.buildIfSupported();
            if (var3 != null) {
               return var3;
            }
         }

         if (this.isOpenJSSEPreferred()) {
            var var4: OpenJSSEPlatform = OpenJSSEPlatform.Companion.buildIfSupported();
            if (var4 != null) {
               return var4;
            }
         }

         var var5: Jdk9Platform = Jdk9Platform.Companion.buildIfSupported();
         if (var5 != null) {
            return var5;
         } else {
            var jdkWithJettyBoot: Platform = Jdk8WithJettyBootPlatform.Companion.buildIfSupported();
            return jdkWithJettyBoot ?: new Platform();
         }
      }

      public fun concatLengthPrefixed(protocols: List<Protocol>): ByteArray {
         var result: Buffer = new Buffer();

         for (java.lang.String protocol : this.alpnProtocolNames(protocols)) {
            result.writeByte(protocol.length());
            result.writeUtf8(protocol);
         }

         return result.readByteArray();
      }
   }
}
