package okhttp3

import java.net.Proxy
import java.net.ProxySelector
import java.time.Duration
import java.util.ArrayList
import java.util.Collections
import java.util.Random
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.EventListener.Factory
import okhttp3.Interceptor.Chain
import okhttp3.internal.Util
import okhttp3.internal.concurrent.TaskRunner
import okhttp3.internal.connection.RealCall
import okhttp3.internal.connection.RouteDatabase
import okhttp3.internal.platform.Platform
import okhttp3.internal.proxy.NullProxySelector
import okhttp3.internal.tls.CertificateChainCleaner
import okhttp3.internal.tls.OkHostnameVerifier
import okhttp3.internal.ws.RealWebSocket
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nOkHttpClient.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OkHttpClient.kt\nokhttp3/OkHttpClient\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1079:1\n2624#2,3:1080\n2624#2,3:1083\n1#3:1086\n*S KotlinDebug\n*F\n+ 1 OkHttpClient.kt\nokhttp3/OkHttpClient\n*L\n225#1:1080,3\n255#1:1083,3\n*E\n"])
open class OkHttpClient internal constructor(builder: okhttp3.OkHttpClient.Builder) : Cloneable, Call.Factory, WebSocket.Factory {
   @JvmStatic
   public OkHttpClient.Companion Companion = new OkHttpClient.Companion(null);
   @JvmStatic
   private java.util.List<Protocol> DEFAULT_PROTOCOLS = Util.immutableListOf(Protocol.HTTP_2, Protocol.HTTP_1_1);
   @JvmStatic
   private java.util.List<ConnectionSpec> DEFAULT_CONNECTION_SPECS = Util.immutableListOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT);

   public final val authenticator: Authenticator
      public final get() {
         return this.authenticator;
      }

   public final val cache: Cache?
      public final get() {
         return this.cache;
      }

   public final val callTimeoutMillis: Int
      public final get() {
         return this.callTimeoutMillis;
      }

   public final val certificateChainCleaner: CertificateChainCleaner?
      public final get() {
         return this.certificateChainCleaner;
      }

   public final val certificatePinner: CertificatePinner
      public final get() {
         return this.certificatePinner;
      }

   public final val connectTimeoutMillis: Int
      public final get() {
         return this.connectTimeoutMillis;
      }

   public final val connectionPool: ConnectionPool
      public final get() {
         return this.connectionPool;
      }

   public final val connectionSpecs: List<ConnectionSpec>
      public final get() {
         return this.connectionSpecs;
      }

   public final val cookieJar: CookieJar
      public final get() {
         return this.cookieJar;
      }

   public final val dispatcher: Dispatcher
      public final get() {
         return this.dispatcher;
      }

   public final val dns: Dns
      public final get() {
         return this.dns;
      }

   public final val eventListenerFactory: Factory
      public final get() {
         return this.eventListenerFactory;
      }

   public final val followRedirects: Boolean
      public final get() {
         return this.followRedirects;
      }

   public final val followSslRedirects: Boolean
      public final get() {
         return this.followSslRedirects;
      }

   public final val hostnameVerifier: HostnameVerifier
      public final get() {
         return this.hostnameVerifier;
      }

   public final val interceptors: List<Interceptor>
      public final get() {
         return this.interceptors;
      }

   public final val minWebSocketMessageToCompress: Long
      public final get() {
         return this.minWebSocketMessageToCompress;
      }

   public final val networkInterceptors: List<Interceptor>
      public final get() {
         return this.networkInterceptors;
      }

   public final val pingIntervalMillis: Int
      public final get() {
         return this.pingIntervalMillis;
      }

   public final val protocols: List<Protocol>
      public final get() {
         return this.protocols;
      }

   public final val proxy: Proxy?
      public final get() {
         return this.proxy;
      }

   public final val proxyAuthenticator: Authenticator
      public final get() {
         return this.proxyAuthenticator;
      }

   public final val proxySelector: ProxySelector
      public final get() {
         return this.proxySelector;
      }

   public final val readTimeoutMillis: Int
      public final get() {
         return this.readTimeoutMillis;
      }

   public final val retryOnConnectionFailure: Boolean
      public final get() {
         return this.retryOnConnectionFailure;
      }

   public final val routeDatabase: RouteDatabase
   public final val socketFactory: SocketFactory
      public final get() {
         return this.socketFactory;
      }

   public final val sslSocketFactory: SSLSocketFactory
      public final get() {
         if (this.sslSocketFactoryOrNull == null) {
            throw new IllegalStateException("CLEARTEXT-only client");
         } else {
            return this.sslSocketFactoryOrNull;
         }
      }

   private final val sslSocketFactoryOrNull: SSLSocketFactory?
   public final val writeTimeoutMillis: Int
      public final get() {
         return this.writeTimeoutMillis;
      }

   public final val x509TrustManager: X509TrustManager?
      public final get() {
         return this.x509TrustManager;
      }


   init {
      this.dispatcher = builder.getDispatcher$okhttp();
      this.connectionPool = builder.getConnectionPool$okhttp();
      this.interceptors = Util.toImmutableList(builder.getInterceptors$okhttp());
      this.networkInterceptors = Util.toImmutableList(builder.getNetworkInterceptors$okhttp());
      this.eventListenerFactory = builder.getEventListenerFactory$okhttp();
      this.retryOnConnectionFailure = builder.getRetryOnConnectionFailure$okhttp();
      this.authenticator = builder.getAuthenticator$okhttp();
      this.followRedirects = builder.getFollowRedirects$okhttp();
      this.followSslRedirects = builder.getFollowSslRedirects$okhttp();
      this.cookieJar = builder.getCookieJar$okhttp();
      this.cache = builder.getCache$okhttp();
      this.dns = builder.getDns$okhttp();
      this.proxy = builder.getProxy$okhttp();
      var var10001: ProxySelector;
      if (builder.getProxy$okhttp() != null) {
         var10001 = NullProxySelector.INSTANCE;
      } else {
         var10001 = builder.getProxySelector$okhttp();
         if (var10001 == null) {
            var10001 = ProxySelector.getDefault();
         }

         if (var10001 == null) {
            var10001 = NullProxySelector.INSTANCE;
         }
      }

      this.proxySelector = var10001;
      this.proxyAuthenticator = builder.getProxyAuthenticator$okhttp();
      this.socketFactory = builder.getSocketFactory$okhttp();
      this.connectionSpecs = builder.getConnectionSpecs$okhttp();
      this.protocols = builder.getProtocols$okhttp();
      this.hostnameVerifier = builder.getHostnameVerifier$okhttp();
      this.callTimeoutMillis = builder.getCallTimeout$okhttp();
      this.connectTimeoutMillis = builder.getConnectTimeout$okhttp();
      this.readTimeoutMillis = builder.getReadTimeout$okhttp();
      this.writeTimeoutMillis = builder.getWriteTimeout$okhttp();
      this.pingIntervalMillis = builder.getPingInterval$okhttp();
      this.minWebSocketMessageToCompress = builder.getMinWebSocketMessageToCompress$okhttp();
      var var8: RouteDatabase = builder.getRouteDatabase$okhttp();
      if (var8 == null) {
         var8 = new RouteDatabase();
      }

      this.routeDatabase = var8;
      var `$this$none$iv`: java.lang.Iterable = this.connectionSpecs;
      var var10000: Boolean;
      if (this.connectionSpecs is java.util.Collection && this.connectionSpecs.isEmpty()) {
         var10000 = true;
      } else {
         var var4: java.util.Iterator = `$this$none$iv`.iterator();

         while (true) {
            if (!var4.hasNext()) {
               var10000 = true;
               break;
            }

            if ((var4.next() as ConnectionSpec).isTls()) {
               var10000 = false;
               break;
            }
         }
      }

      if (var10000) {
         this.sslSocketFactoryOrNull = null;
         this.certificateChainCleaner = null;
         this.x509TrustManager = null;
         this.certificatePinner = CertificatePinner.DEFAULT;
      } else if (builder.getSslSocketFactoryOrNull$okhttp() != null) {
         this.sslSocketFactoryOrNull = builder.getSslSocketFactoryOrNull$okhttp();
         var var9: CertificateChainCleaner = builder.getCertificateChainCleaner$okhttp();
         this.certificateChainCleaner = var9;
         var var10: X509TrustManager = builder.getX509TrustManagerOrNull$okhttp();
         this.x509TrustManager = var10;
         var var11: CertificatePinner = builder.getCertificatePinner$okhttp();
         var var10002: CertificateChainCleaner = this.certificateChainCleaner;
         this.certificatePinner = var11.withCertificateChainCleaner$okhttp(var10002);
      } else {
         this.x509TrustManager = Platform.Companion.get().platformTrustManager();
         var var12: Platform = Platform.Companion.get();
         var var15: X509TrustManager = this.x509TrustManager;
         this.sslSocketFactoryOrNull = var12.newSslSocketFactory(var15);
         var var13: CertificateChainCleaner.Companion = CertificateChainCleaner.Companion;
         var15 = this.x509TrustManager;
         this.certificateChainCleaner = var13.get(var15);
         var var14: CertificatePinner = builder.getCertificatePinner$okhttp();
         var var17: CertificateChainCleaner = this.certificateChainCleaner;
         this.certificatePinner = var14.withCertificateChainCleaner$okhttp(var17);
      }

      this.verifyClientState();
   }

   public constructor() : this(new OkHttpClient.Builder())
   private fun verifyClientState() {
      var var10000: java.util.List = this.interceptors;
      if (var10000.contains(null)) {
         throw new IllegalStateException(("Null interceptor: " + this.interceptors).toString());
      } else {
         var10000 = this.networkInterceptors;
         if (var10000.contains(null)) {
            throw new IllegalStateException(("Null network interceptor: " + this.networkInterceptors).toString());
         } else {
            var `$this$none$iv`: java.lang.Iterable = this.connectionSpecs;
            var var22: Boolean;
            if (this.connectionSpecs is java.util.Collection && this.connectionSpecs.isEmpty()) {
               var22 = true;
            } else {
               var var3: java.util.Iterator = `$this$none$iv`.iterator();

               while (true) {
                  if (!var3.hasNext()) {
                     var22 = true;
                     break;
                  }

                  if ((var3.next() as ConnectionSpec).isTls()) {
                     var22 = false;
                     break;
                  }
               }
            }

            if (var22) {
               if (this.sslSocketFactoryOrNull != null) {
                  throw new IllegalStateException("Check failed.".toString());
               }

               if (this.certificateChainCleaner != null) {
                  throw new IllegalStateException("Check failed.".toString());
               }

               if (this.x509TrustManager != null) {
                  throw new IllegalStateException("Check failed.".toString());
               }

               if (!(this.certificatePinner == CertificatePinner.DEFAULT)) {
                  throw new IllegalStateException("Check failed.".toString());
               }
            } else {
               if (this.sslSocketFactoryOrNull == null) {
                  throw new IllegalStateException("sslSocketFactory == null".toString());
               }

               if (this.certificateChainCleaner == null) {
                  throw new IllegalStateException("certificateChainCleaner == null".toString());
               }

               if (this.x509TrustManager == null) {
                  throw new IllegalStateException("x509TrustManager == null".toString());
               }
            }
         }
      }
   }

   public override fun newCall(request: Request): Call {
      return new RealCall(this, request, false);
   }

   public override fun newWebSocket(request: Request, listener: WebSocketListener): WebSocket {
      var webSocket: RealWebSocket = new RealWebSocket(
         TaskRunner.INSTANCE, request, listener, new Random(), (long)this.pingIntervalMillis, null, this.minWebSocketMessageToCompress
      );
      webSocket.connect(this);
      return webSocket;
   }

   public open fun newBuilder(): okhttp3.OkHttpClient.Builder {
      return new OkHttpClient.Builder(this);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "dispatcher", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_dispatcher")
   public fun dispatcher(): Dispatcher {
      return this.dispatcher;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "connectionPool", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_connectionPool")
   public fun connectionPool(): ConnectionPool {
      return this.connectionPool;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "interceptors", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_interceptors")
   public fun interceptors(): List<Interceptor> {
      return this.interceptors;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "networkInterceptors", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_networkInterceptors")
   public fun networkInterceptors(): List<Interceptor> {
      return this.networkInterceptors;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "eventListenerFactory", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_eventListenerFactory")
   public fun eventListenerFactory(): Factory {
      return this.eventListenerFactory;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "retryOnConnectionFailure", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_retryOnConnectionFailure")
   public fun retryOnConnectionFailure(): Boolean {
      return this.retryOnConnectionFailure;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "authenticator", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_authenticator")
   public fun authenticator(): Authenticator {
      return this.authenticator;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "followRedirects", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_followRedirects")
   public fun followRedirects(): Boolean {
      return this.followRedirects;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "followSslRedirects", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_followSslRedirects")
   public fun followSslRedirects(): Boolean {
      return this.followSslRedirects;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "cookieJar", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_cookieJar")
   public fun cookieJar(): CookieJar {
      return this.cookieJar;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "cache", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_cache")
   public fun cache(): Cache? {
      return this.cache;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "dns", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_dns")
   public fun dns(): Dns {
      return this.dns;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "proxy", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_proxy")
   public fun proxy(): Proxy? {
      return this.proxy;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "proxySelector", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_proxySelector")
   public fun proxySelector(): ProxySelector {
      return this.proxySelector;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "proxyAuthenticator", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_proxyAuthenticator")
   public fun proxyAuthenticator(): Authenticator {
      return this.proxyAuthenticator;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "socketFactory", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_socketFactory")
   public fun socketFactory(): SocketFactory {
      return this.socketFactory;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "sslSocketFactory", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_sslSocketFactory")
   public fun sslSocketFactory(): SSLSocketFactory {
      return this.sslSocketFactory();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "connectionSpecs", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_connectionSpecs")
   public fun connectionSpecs(): List<ConnectionSpec> {
      return this.connectionSpecs;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "protocols", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_protocols")
   public fun protocols(): List<Protocol> {
      return this.protocols;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "hostnameVerifier", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_hostnameVerifier")
   public fun hostnameVerifier(): HostnameVerifier {
      return this.hostnameVerifier;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "certificatePinner", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_certificatePinner")
   public fun certificatePinner(): CertificatePinner {
      return this.certificatePinner;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "callTimeoutMillis", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_callTimeoutMillis")
   public fun callTimeoutMillis(): Int {
      return this.callTimeoutMillis;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "connectTimeoutMillis", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_connectTimeoutMillis")
   public fun connectTimeoutMillis(): Int {
      return this.connectTimeoutMillis;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "readTimeoutMillis", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_readTimeoutMillis")
   public fun readTimeoutMillis(): Int {
      return this.readTimeoutMillis;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "writeTimeoutMillis", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_writeTimeoutMillis")
   public fun writeTimeoutMillis(): Int {
      return this.writeTimeoutMillis;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "pingIntervalMillis", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_pingIntervalMillis")
   public fun pingIntervalMillis(): Int {
      return this.pingIntervalMillis;
   }

   override fun clone(): Any {
      return super.clone();
   }

   @SourceDebugExtension(["SMAP\nOkHttpClient.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OkHttpClient.kt\nokhttp3/OkHttpClient$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1079:1\n1#2:1080\n*E\n"])
   class Builder {
      internal final var authenticator: Authenticator = Authenticator.NONE
      internal final var cache: Cache?
      internal final var callTimeout: Int
      internal final var certificateChainCleaner: CertificateChainCleaner?
      internal final var certificatePinner: CertificatePinner
      internal final var connectTimeout: Int
      internal final var connectionPool: ConnectionPool = new ConnectionPool()
      internal final var connectionSpecs: List<ConnectionSpec>
      internal final var cookieJar: CookieJar = CookieJar.NO_COOKIES
      internal final var dispatcher: Dispatcher = new Dispatcher()
      internal final var dns: Dns = Dns.SYSTEM
      internal final var eventListenerFactory: Factory = Util.asFactory(EventListener.NONE)
      internal final var followRedirects: Boolean = true
      internal final var followSslRedirects: Boolean = true
      internal final var hostnameVerifier: HostnameVerifier
      internal final val interceptors: MutableList<Interceptor> = (new ArrayList()) as java.util.List
      internal final var minWebSocketMessageToCompress: Long
      internal final val networkInterceptors: MutableList<Interceptor> = (new ArrayList()) as java.util.List
      internal final var pingInterval: Int
      internal final var protocols: List<Protocol>
      internal final var proxy: Proxy?
      internal final var proxyAuthenticator: Authenticator = Authenticator.NONE
      internal final var proxySelector: ProxySelector?
      internal final var readTimeout: Int
      internal final var retryOnConnectionFailure: Boolean = true
      internal final var routeDatabase: RouteDatabase?
      internal final var socketFactory: SocketFactory
      internal final var sslSocketFactoryOrNull: SSLSocketFactory?
      internal final var writeTimeout: Int
      internal final var x509TrustManagerOrNull: X509TrustManager?

      init {
         var var10001: SocketFactory = SocketFactory.getDefault();
         this.socketFactory = var10001;
         this.connectionSpecs = OkHttpClient.Companion.getDEFAULT_CONNECTION_SPECS$okhttp();
         this.protocols = OkHttpClient.Companion.getDEFAULT_PROTOCOLS$okhttp();
         this.hostnameVerifier = OkHostnameVerifier.INSTANCE;
         this.certificatePinner = CertificatePinner.DEFAULT;
         this.connectTimeout = 10000;
         this.readTimeout = 10000;
         this.writeTimeout = 10000;
         this.minWebSocketMessageToCompress = 1024L;
      }

      internal constructor(okHttpClient: OkHttpClient) : this() {
         this.dispatcher = okHttpClient.dispatcher();
         this.connectionPool = okHttpClient.connectionPool();
         CollectionsKt.addAll(this.interceptors, okHttpClient.interceptors());
         CollectionsKt.addAll(this.networkInterceptors, okHttpClient.networkInterceptors());
         this.eventListenerFactory = okHttpClient.eventListenerFactory();
         this.retryOnConnectionFailure = okHttpClient.retryOnConnectionFailure();
         this.authenticator = okHttpClient.authenticator();
         this.followRedirects = okHttpClient.followRedirects();
         this.followSslRedirects = okHttpClient.followSslRedirects();
         this.cookieJar = okHttpClient.cookieJar();
         this.cache = okHttpClient.cache();
         this.dns = okHttpClient.dns();
         this.proxy = okHttpClient.proxy();
         this.proxySelector = okHttpClient.proxySelector();
         this.proxyAuthenticator = okHttpClient.proxyAuthenticator();
         this.socketFactory = okHttpClient.socketFactory();
         this.sslSocketFactoryOrNull = OkHttpClient.access$getSslSocketFactoryOrNull$p(okHttpClient);
         this.x509TrustManagerOrNull = okHttpClient.x509TrustManager();
         this.connectionSpecs = okHttpClient.connectionSpecs();
         this.protocols = okHttpClient.protocols();
         this.hostnameVerifier = okHttpClient.hostnameVerifier();
         this.certificatePinner = okHttpClient.certificatePinner();
         this.certificateChainCleaner = okHttpClient.certificateChainCleaner();
         this.callTimeout = okHttpClient.callTimeoutMillis();
         this.connectTimeout = okHttpClient.connectTimeoutMillis();
         this.readTimeout = okHttpClient.readTimeoutMillis();
         this.writeTimeout = okHttpClient.writeTimeoutMillis();
         this.pingInterval = okHttpClient.pingIntervalMillis();
         this.minWebSocketMessageToCompress = okHttpClient.minWebSocketMessageToCompress();
         this.routeDatabase = okHttpClient.getRouteDatabase();
      }

      public fun dispatcher(dispatcher: Dispatcher): okhttp3.OkHttpClient.Builder {
         this.dispatcher = dispatcher;
         return this;
      }

      public fun connectionPool(connectionPool: ConnectionPool): okhttp3.OkHttpClient.Builder {
         this.connectionPool = connectionPool;
         return this;
      }

      public fun interceptors(): MutableList<Interceptor> {
         return this.interceptors;
      }

      public fun addInterceptor(interceptor: Interceptor): okhttp3.OkHttpClient.Builder {
         this.interceptors.add(interceptor);
         return this;
      }

      @JvmName(name = "-addInterceptor")
      public inline fun addInterceptor(crossinline block: (Chain) -> Response): okhttp3.OkHttpClient.Builder {
         return this.addInterceptor(new Interceptor(block) {
            {
               this.$block = `$block`;
            }

            @NotNull
            @Override
            public final Response intercept(@NotNull Interceptor.Chain chain) {
               return this.$block.invoke(chain) as Response;
            }
         });
      }

      public fun networkInterceptors(): MutableList<Interceptor> {
         return this.networkInterceptors;
      }

      public fun addNetworkInterceptor(interceptor: Interceptor): okhttp3.OkHttpClient.Builder {
         this.networkInterceptors.add(interceptor);
         return this;
      }

      @JvmName(name = "-addNetworkInterceptor")
      public inline fun addNetworkInterceptor(crossinline block: (Chain) -> Response): okhttp3.OkHttpClient.Builder {
         return this.addNetworkInterceptor(new Interceptor(block) {
            {
               this.$block = `$block`;
            }

            @NotNull
            @Override
            public final Response intercept(@NotNull Interceptor.Chain chain) {
               return this.$block.invoke(chain) as Response;
            }
         });
      }

      public fun eventListener(eventListener: EventListener): okhttp3.OkHttpClient.Builder {
         this.eventListenerFactory = Util.asFactory(eventListener);
         return this;
      }

      public fun eventListenerFactory(eventListenerFactory: Factory): okhttp3.OkHttpClient.Builder {
         this.eventListenerFactory = eventListenerFactory;
         return this;
      }

      public fun retryOnConnectionFailure(retryOnConnectionFailure: Boolean): okhttp3.OkHttpClient.Builder {
         this.retryOnConnectionFailure = retryOnConnectionFailure;
         return this;
      }

      public fun authenticator(authenticator: Authenticator): okhttp3.OkHttpClient.Builder {
         this.authenticator = authenticator;
         return this;
      }

      public fun followRedirects(followRedirects: Boolean): okhttp3.OkHttpClient.Builder {
         this.followRedirects = followRedirects;
         return this;
      }

      public fun followSslRedirects(followProtocolRedirects: Boolean): okhttp3.OkHttpClient.Builder {
         this.followSslRedirects = followProtocolRedirects;
         return this;
      }

      public fun cookieJar(cookieJar: CookieJar): okhttp3.OkHttpClient.Builder {
         this.cookieJar = cookieJar;
         return this;
      }

      public fun cache(cache: Cache?): okhttp3.OkHttpClient.Builder {
         this.cache = cache;
         return this;
      }

      public fun dns(dns: Dns): okhttp3.OkHttpClient.Builder {
         var `$this$dns_u24lambda_u2412`: OkHttpClient.Builder = this;
         if (!(dns == this.dns)) {
            `$this$dns_u24lambda_u2412`.routeDatabase = null;
         }

         `$this$dns_u24lambda_u2412`.dns = dns;
         return this;
      }

      public fun proxy(proxy: Proxy?): okhttp3.OkHttpClient.Builder {
         var `$this$proxy_u24lambda_u2413`: OkHttpClient.Builder = this;
         if (!(proxy == this.proxy)) {
            `$this$proxy_u24lambda_u2413`.routeDatabase = null;
         }

         `$this$proxy_u24lambda_u2413`.proxy = proxy;
         return this;
      }

      public fun proxySelector(proxySelector: ProxySelector): okhttp3.OkHttpClient.Builder {
         var `$this$proxySelector_u24lambda_u2414`: OkHttpClient.Builder = this;
         if (!(proxySelector == this.proxySelector)) {
            `$this$proxySelector_u24lambda_u2414`.routeDatabase = null;
         }

         `$this$proxySelector_u24lambda_u2414`.proxySelector = proxySelector;
         return this;
      }

      public fun proxyAuthenticator(proxyAuthenticator: Authenticator): okhttp3.OkHttpClient.Builder {
         var `$this$proxyAuthenticator_u24lambda_u2415`: OkHttpClient.Builder = this;
         if (!(proxyAuthenticator == this.proxyAuthenticator)) {
            `$this$proxyAuthenticator_u24lambda_u2415`.routeDatabase = null;
         }

         `$this$proxyAuthenticator_u24lambda_u2415`.proxyAuthenticator = proxyAuthenticator;
         return this;
      }

      public fun socketFactory(socketFactory: SocketFactory): okhttp3.OkHttpClient.Builder {
         var `$this$socketFactory_u24lambda_u2417`: OkHttpClient.Builder = this;
         if (socketFactory is SSLSocketFactory) {
            throw new IllegalArgumentException("socketFactory instanceof SSLSocketFactory".toString());
         } else {
            if (!(socketFactory == `$this$socketFactory_u24lambda_u2417`.socketFactory)) {
               `$this$socketFactory_u24lambda_u2417`.routeDatabase = null;
            }

            `$this$socketFactory_u24lambda_u2417`.socketFactory = socketFactory;
            return this;
         }
      }

      @Deprecated(message = "Use the sslSocketFactory overload that accepts a X509TrustManager.", level = DeprecationLevel.ERROR)
      public fun sslSocketFactory(sslSocketFactory: SSLSocketFactory): okhttp3.OkHttpClient.Builder {
         var `$this$sslSocketFactory_u24lambda_u2418`: OkHttpClient.Builder = this;
         if (!(sslSocketFactory == this.sslSocketFactoryOrNull)) {
            `$this$sslSocketFactory_u24lambda_u2418`.routeDatabase = null;
         }

         `$this$sslSocketFactory_u24lambda_u2418`.sslSocketFactoryOrNull = sslSocketFactory;
         var var10001: X509TrustManager = Platform.Companion.get().trustManager(sslSocketFactory);
         if (var10001 == null) {
            throw new IllegalStateException(
               "Unable to extract the trust manager on " + Platform.Companion.get() + ", sslSocketFactory is " + sslSocketFactory.getClass()
            );
         } else {
            `$this$sslSocketFactory_u24lambda_u2418`.x509TrustManagerOrNull = var10001;
            var var5: Platform = Platform.Companion.get();
            var var10002: X509TrustManager = `$this$sslSocketFactory_u24lambda_u2418`.x509TrustManagerOrNull;
            `$this$sslSocketFactory_u24lambda_u2418`.certificateChainCleaner = var5.buildCertificateChainCleaner(var10002);
            return this;
         }
      }

      public fun sslSocketFactory(sslSocketFactory: SSLSocketFactory, trustManager: X509TrustManager): okhttp3.OkHttpClient.Builder {
         var `$this$sslSocketFactory_u24lambda_u2419`: OkHttpClient.Builder = this;
         if (!(sslSocketFactory == this.sslSocketFactoryOrNull) || !(trustManager == this.x509TrustManagerOrNull)) {
            `$this$sslSocketFactory_u24lambda_u2419`.routeDatabase = null;
         }

         `$this$sslSocketFactory_u24lambda_u2419`.sslSocketFactoryOrNull = sslSocketFactory;
         `$this$sslSocketFactory_u24lambda_u2419`.certificateChainCleaner = CertificateChainCleaner.Companion.get(trustManager);
         `$this$sslSocketFactory_u24lambda_u2419`.x509TrustManagerOrNull = trustManager;
         return this;
      }

      public fun connectionSpecs(connectionSpecs: List<ConnectionSpec>): okhttp3.OkHttpClient.Builder {
         var `$this$connectionSpecs_u24lambda_u2420`: OkHttpClient.Builder = this;
         if (!(connectionSpecs == this.connectionSpecs)) {
            `$this$connectionSpecs_u24lambda_u2420`.routeDatabase = null;
         }

         `$this$connectionSpecs_u24lambda_u2420`.connectionSpecs = Util.toImmutableList(connectionSpecs);
         return this;
      }

      public fun protocols(protocols: List<Protocol>): okhttp3.OkHttpClient.Builder {
         var `$this$protocols_u24lambda_u2425`: OkHttpClient.Builder = this;
         var protocolsCopy: java.util.List = CollectionsKt.toMutableList(protocols);
         if (!protocolsCopy.contains(Protocol.H2_PRIOR_KNOWLEDGE) && !protocolsCopy.contains(Protocol.HTTP_1_1)) {
            throw new IllegalArgumentException(("protocols must contain h2_prior_knowledge or http/1.1: " + protocolsCopy).toString());
         } else if (protocolsCopy.contains(Protocol.H2_PRIOR_KNOWLEDGE) && protocolsCopy.size() > 1) {
            throw new IllegalArgumentException(("protocols containing h2_prior_knowledge cannot use other protocols: " + protocolsCopy).toString());
         } else if (protocolsCopy.contains(Protocol.HTTP_1_0)) {
            throw new IllegalArgumentException(("protocols must not contain http/1.0: " + protocolsCopy).toString());
         } else if (protocolsCopy.contains(null)) {
            throw new IllegalArgumentException("protocols must not contain null".toString());
         } else {
            protocolsCopy.remove(Protocol.SPDY_3);
            if (!(protocolsCopy == `$this$protocols_u24lambda_u2425`.protocols)) {
               `$this$protocols_u24lambda_u2425`.routeDatabase = null;
            }

            var var10001: java.util.List = Collections.unmodifiableList(protocolsCopy);
            `$this$protocols_u24lambda_u2425`.protocols = var10001;
            return this;
         }
      }

      public fun hostnameVerifier(hostnameVerifier: HostnameVerifier): okhttp3.OkHttpClient.Builder {
         var `$this$hostnameVerifier_u24lambda_u2426`: OkHttpClient.Builder = this;
         if (!(hostnameVerifier == this.hostnameVerifier)) {
            `$this$hostnameVerifier_u24lambda_u2426`.routeDatabase = null;
         }

         `$this$hostnameVerifier_u24lambda_u2426`.hostnameVerifier = hostnameVerifier;
         return this;
      }

      public fun certificatePinner(certificatePinner: CertificatePinner): okhttp3.OkHttpClient.Builder {
         var `$this$certificatePinner_u24lambda_u2427`: OkHttpClient.Builder = this;
         if (!(certificatePinner == this.certificatePinner)) {
            `$this$certificatePinner_u24lambda_u2427`.routeDatabase = null;
         }

         `$this$certificatePinner_u24lambda_u2427`.certificatePinner = certificatePinner;
         return this;
      }

      public fun callTimeout(timeout: Long, unit: TimeUnit): okhttp3.OkHttpClient.Builder {
         this.callTimeout = Util.checkDuration("timeout", timeout, unit);
         return this;
      }

      @IgnoreJRERequirement
      public fun callTimeout(duration: Duration): okhttp3.OkHttpClient.Builder {
         this.callTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      public fun connectTimeout(timeout: Long, unit: TimeUnit): okhttp3.OkHttpClient.Builder {
         this.connectTimeout = Util.checkDuration("timeout", timeout, unit);
         return this;
      }

      @IgnoreJRERequirement
      public fun connectTimeout(duration: Duration): okhttp3.OkHttpClient.Builder {
         this.connectTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      public fun readTimeout(timeout: Long, unit: TimeUnit): okhttp3.OkHttpClient.Builder {
         this.readTimeout = Util.checkDuration("timeout", timeout, unit);
         return this;
      }

      @IgnoreJRERequirement
      public fun readTimeout(duration: Duration): okhttp3.OkHttpClient.Builder {
         this.readTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      public fun writeTimeout(timeout: Long, unit: TimeUnit): okhttp3.OkHttpClient.Builder {
         this.writeTimeout = Util.checkDuration("timeout", timeout, unit);
         return this;
      }

      @IgnoreJRERequirement
      public fun writeTimeout(duration: Duration): okhttp3.OkHttpClient.Builder {
         this.writeTimeout(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      public fun pingInterval(interval: Long, unit: TimeUnit): okhttp3.OkHttpClient.Builder {
         this.pingInterval = Util.checkDuration("interval", interval, unit);
         return this;
      }

      @IgnoreJRERequirement
      public fun pingInterval(duration: Duration): okhttp3.OkHttpClient.Builder {
         this.pingInterval(duration.toMillis(), TimeUnit.MILLISECONDS);
         return this;
      }

      public fun minWebSocketMessageToCompress(bytes: Long): okhttp3.OkHttpClient.Builder {
         var `$this$minWebSocketMessageToCompress_u24lambda_u2439`: OkHttpClient.Builder = this;
         if (bytes < 0L) {
            throw new IllegalArgumentException(("minWebSocketMessageToCompress must be positive: " + bytes).toString());
         } else {
            `$this$minWebSocketMessageToCompress_u24lambda_u2439`.minWebSocketMessageToCompress = bytes;
            return this;
         }
      }

      public fun build(): OkHttpClient {
         return new OkHttpClient(this);
      }
   }

   companion object Companion private constructor() {
      internal final val DEFAULT_CONNECTION_SPECS: List<ConnectionSpec>
      internal final val DEFAULT_PROTOCOLS: List<Protocol>

   }
}
