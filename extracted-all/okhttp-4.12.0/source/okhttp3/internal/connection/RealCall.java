package okhttp3.internal.connection

import java.io.IOException
import java.io.InterruptedIOException
import java.lang.ref.Reference
import java.lang.ref.WeakReference
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Address
import okhttp3.Call
import okhttp3.Callback
import okhttp3.CertificatePinner
import okhttp3.EventListener
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.Util
import okhttp3.internal.http.RealInterceptorChain
import okhttp3.internal.platform.Platform
import okio.AsyncTimeout

@SourceDebugExtension(["SMAP\nRealCall.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealCall.kt\nokhttp3/internal/connection/RealCall\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokhttp3/internal/Util\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,551:1\n1#2:552\n608#3,4:553\n615#3,4:557\n615#3,4:561\n608#3,4:565\n350#4,7:569\n*S KotlinDebug\n*F\n+ 1 RealCall.kt\nokhttp3/internal/connection/RealCall\n*L\n269#1:553,4\n344#1:557,4\n348#1:561,4\n375#1:565,4\n378#1:569,7\n*E\n"])
class RealCall(client: OkHttpClient, originalRequest: Request, forWebSocket: Boolean) : Call {
   private final var callStackTrace: Any?
   private final var canceled: Boolean
   public final val client: OkHttpClient
   public final var connection: RealConnection?
      private set
   private final val connectionPool: RealConnectionPool
   public final var connectionToCancel: RealConnection?
      internal set
   internal final val eventListener: EventListener
   private final var exchange: Exchange?
   private final var exchangeFinder: ExchangeFinder?
   private final val executed: AtomicBoolean
   private final var expectMoreExchanges: Boolean
   public final val forWebSocket: Boolean
   internal final var interceptorScopedExchange: Exchange?
      private set
   public final val originalRequest: Request
   private final var requestBodyOpen: Boolean
   private final var responseBodyOpen: Boolean
   private final val timeout: <unrepresentable>
   private final var timeoutEarlyExit: Boolean

   init {
      this.client = client;
      this.originalRequest = originalRequest;
      this.forWebSocket = forWebSocket;
      this.connectionPool = this.client.connectionPool().getDelegate$okhttp();
      this.eventListener = this.client.eventListenerFactory().create(this);
      var var4: AsyncTimeout = new AsyncTimeout(this) {
         {
            this.this$0 = `$receiver`;
         }

         protected void timedOut() {
            this.this$0.cancel();
         }
      };
      var4.timeout((long)this.client.callTimeoutMillis(), TimeUnit.MILLISECONDS);
      this.timeout = var4;
      this.executed = new AtomicBoolean();
      this.expectMoreExchanges = true;
   }

   public open fun timeout(): AsyncTimeout {
      return this.timeout;
   }

   public open fun clone(): RealCall {
      return new RealCall(this.client, this.originalRequest, this.forWebSocket);
   }

   public override fun request(): Request {
      return this.originalRequest;
   }

   public override fun cancel() {
      if (!this.canceled) {
         this.canceled = true;
         if (this.exchange != null) {
            this.exchange.cancel();
         }

         if (this.connectionToCancel != null) {
            this.connectionToCancel.cancel();
         }

         this.eventListener.canceled(this);
      }
   }

   public override fun isCanceled(): Boolean {
      return this.canceled;
   }

   public override fun execute(): Response {
      if (!this.executed.compareAndSet(false, true)) {
         throw new IllegalStateException("Already Executed".toString());
      } else {
         label28: {
            this.timeout.enter();
            this.callStart();

            try {
               this.client.dispatcher().executed$okhttp(this);
               var var1: Response = this.getResponseWithInterceptorChain$okhttp();
            } catch (java.lang.Throwable var3) {
               this.client.dispatcher().finished$okhttp(this);
            }

            this.client.dispatcher().finished$okhttp(this);
         }
      }
   }

   public override fun enqueue(responseCallback: Callback) {
      if (!this.executed.compareAndSet(false, true)) {
         throw new IllegalStateException("Already Executed".toString());
      } else {
         this.callStart();
         this.client.dispatcher().enqueue$okhttp(new RealCall.AsyncCall(this, responseCallback));
      }
   }

   public override fun isExecuted(): Boolean {
      return this.executed.get();
   }

   private fun callStart() {
      this.callStackTrace = Platform.Companion.get().getStackTraceForCloseable("response.body().close()");
      this.eventListener.callStart(this);
   }

   @Throws(java/io/IOException::class)
   internal fun getResponseWithInterceptorChain(): Response {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 000: new java/util/ArrayList
      // 003: dup
      // 004: invokespecial java/util/ArrayList.<init> ()V
      // 007: checkcast java/util/List
      // 00a: astore 1
      // 00b: aload 1
      // 00c: checkcast java/util/Collection
      // 00f: aload 0
      // 010: getfield okhttp3/internal/connection/RealCall.client Lokhttp3/OkHttpClient;
      // 013: invokevirtual okhttp3/OkHttpClient.interceptors ()Ljava/util/List;
      // 016: checkcast java/lang/Iterable
      // 019: invokestatic kotlin/collections/CollectionsKt.addAll (Ljava/util/Collection;Ljava/lang/Iterable;)Z
      // 01c: pop
      // 01d: aload 1
      // 01e: checkcast java/util/Collection
      // 021: new okhttp3/internal/http/RetryAndFollowUpInterceptor
      // 024: dup
      // 025: aload 0
      // 026: getfield okhttp3/internal/connection/RealCall.client Lokhttp3/OkHttpClient;
      // 029: invokespecial okhttp3/internal/http/RetryAndFollowUpInterceptor.<init> (Lokhttp3/OkHttpClient;)V
      // 02c: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 031: pop
      // 032: aload 1
      // 033: checkcast java/util/Collection
      // 036: new okhttp3/internal/http/BridgeInterceptor
      // 039: dup
      // 03a: aload 0
      // 03b: getfield okhttp3/internal/connection/RealCall.client Lokhttp3/OkHttpClient;
      // 03e: invokevirtual okhttp3/OkHttpClient.cookieJar ()Lokhttp3/CookieJar;
      // 041: invokespecial okhttp3/internal/http/BridgeInterceptor.<init> (Lokhttp3/CookieJar;)V
      // 044: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 049: pop
      // 04a: aload 1
      // 04b: checkcast java/util/Collection
      // 04e: new okhttp3/internal/cache/CacheInterceptor
      // 051: dup
      // 052: aload 0
      // 053: getfield okhttp3/internal/connection/RealCall.client Lokhttp3/OkHttpClient;
      // 056: invokevirtual okhttp3/OkHttpClient.cache ()Lokhttp3/Cache;
      // 059: invokespecial okhttp3/internal/cache/CacheInterceptor.<init> (Lokhttp3/Cache;)V
      // 05c: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 061: pop
      // 062: aload 1
      // 063: checkcast java/util/Collection
      // 066: getstatic okhttp3/internal/connection/ConnectInterceptor.INSTANCE Lokhttp3/internal/connection/ConnectInterceptor;
      // 069: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 06e: pop
      // 06f: aload 0
      // 070: getfield okhttp3/internal/connection/RealCall.forWebSocket Z
      // 073: ifne 088
      // 076: aload 1
      // 077: checkcast java/util/Collection
      // 07a: aload 0
      // 07b: getfield okhttp3/internal/connection/RealCall.client Lokhttp3/OkHttpClient;
      // 07e: invokevirtual okhttp3/OkHttpClient.networkInterceptors ()Ljava/util/List;
      // 081: checkcast java/lang/Iterable
      // 084: invokestatic kotlin/collections/CollectionsKt.addAll (Ljava/util/Collection;Ljava/lang/Iterable;)Z
      // 087: pop
      // 088: aload 1
      // 089: checkcast java/util/Collection
      // 08c: new okhttp3/internal/http/CallServerInterceptor
      // 08f: dup
      // 090: aload 0
      // 091: getfield okhttp3/internal/connection/RealCall.forWebSocket Z
      // 094: invokespecial okhttp3/internal/http/CallServerInterceptor.<init> (Z)V
      // 097: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 09c: pop
      // 09d: new okhttp3/internal/http/RealInterceptorChain
      // 0a0: dup
      // 0a1: aload 0
      // 0a2: aload 1
      // 0a3: bipush 0
      // 0a4: aconst_null
      // 0a5: aload 0
      // 0a6: getfield okhttp3/internal/connection/RealCall.originalRequest Lokhttp3/Request;
      // 0a9: aload 0
      // 0aa: getfield okhttp3/internal/connection/RealCall.client Lokhttp3/OkHttpClient;
      // 0ad: invokevirtual okhttp3/OkHttpClient.connectTimeoutMillis ()I
      // 0b0: aload 0
      // 0b1: getfield okhttp3/internal/connection/RealCall.client Lokhttp3/OkHttpClient;
      // 0b4: invokevirtual okhttp3/OkHttpClient.readTimeoutMillis ()I
      // 0b7: aload 0
      // 0b8: getfield okhttp3/internal/connection/RealCall.client Lokhttp3/OkHttpClient;
      // 0bb: invokevirtual okhttp3/OkHttpClient.writeTimeoutMillis ()I
      // 0be: invokespecial okhttp3/internal/http/RealInterceptorChain.<init> (Lokhttp3/internal/connection/RealCall;Ljava/util/List;ILokhttp3/internal/connection/Exchange;Lokhttp3/Request;III)V
      // 0c1: astore 2
      // 0c2: bipush 0
      // 0c3: istore 3
      // 0c4: nop
      // 0c5: aload 2
      // 0c6: aload 0
      // 0c7: getfield okhttp3/internal/connection/RealCall.originalRequest Lokhttp3/Request;
      // 0ca: invokevirtual okhttp3/internal/http/RealInterceptorChain.proceed (Lokhttp3/Request;)Lokhttp3/Response;
      // 0cd: astore 4
      // 0cf: aload 0
      // 0d0: invokevirtual okhttp3/internal/connection/RealCall.isCanceled ()Z
      // 0d3: ifeq 0e9
      // 0d6: aload 4
      // 0d8: checkcast java/io/Closeable
      // 0db: invokestatic okhttp3/internal/Util.closeQuietly (Ljava/io/Closeable;)V
      // 0de: new java/io/IOException
      // 0e1: dup
      // 0e2: ldc_w "Canceled"
      // 0e5: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 0e8: athrow
      // 0e9: aload 4
      // 0eb: astore 5
      // 0ed: nop
      // 0ee: aload 0
      // 0ef: aconst_null
      // 0f0: invokevirtual okhttp3/internal/connection/RealCall.noMoreExchanges$okhttp (Ljava/io/IOException;)Ljava/io/IOException;
      // 0f3: pop
      // 0f4: aload 5
      // 0f6: areturn
      // 0f7: astore 4
      // 0f9: bipush 1
      // 0fa: istore 3
      // 0fb: aload 0
      // 0fc: aload 4
      // 0fe: invokevirtual okhttp3/internal/connection/RealCall.noMoreExchanges$okhttp (Ljava/io/IOException;)Ljava/io/IOException;
      // 101: dup
      // 102: ldc_w "null cannot be cast to non-null type kotlin.Throwable"
      // 105: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;Ljava/lang/String;)V
      // 108: checkcast java/lang/Throwable
      // 10b: athrow
      // 10c: astore 4
      // 10e: iload 3
      // 10f: ifne 118
      // 112: aload 0
      // 113: aconst_null
      // 114: invokevirtual okhttp3/internal/connection/RealCall.noMoreExchanges$okhttp (Ljava/io/IOException;)Ljava/io/IOException;
      // 117: pop
      // 118: aload 4
      // 11a: athrow
   }

   public fun enterNetworkInterceptorExchange(request: Request, newExchangeFinder: Boolean) {
      if (this.interceptorScopedExchange != null) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         synchronized (this) {
            if (this.responseBodyOpen) {
               throw new IllegalStateException("cannot make a new request because the previous response is still open: please call response.close()".toString());
            }

            if (this.requestBodyOpen) {
               throw new IllegalStateException("Check failed.".toString());
            }
         }

         if (newExchangeFinder) {
            this.exchangeFinder = new ExchangeFinder(this.connectionPool, this.createAddress(request.url()), this, this.eventListener);
         }
      }
   }

   internal fun initExchange(chain: RealInterceptorChain): Exchange {
      synchronized (this) {
         if (!this.expectMoreExchanges) {
            throw new IllegalStateException("released".toString());
         }

         if (this.responseBodyOpen) {
            throw new IllegalStateException("Check failed.".toString());
         }

         if (this.requestBodyOpen) {
            throw new IllegalStateException("Check failed.".toString());
         }
      }

      var var10000: ExchangeFinder = this.exchangeFinder;
      var result: Exchange = new Exchange(this, this.eventListener, var10000, var10000.find(this.client, chain));
      this.interceptorScopedExchange = result;
      this.exchange = result;
      synchronized (this) {
         this.requestBodyOpen = true;
         this.responseBodyOpen = true;
      }

      if (this.canceled) {
         throw new IOException("Canceled");
      } else {
         return result;
      }
   }

   public fun acquireConnectionNoEvents(connection: RealConnection) {
      if (Util.assertionsEnabled && !Thread.holdsLock(connection)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + connection);
      } else if (this.connection != null) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         this.connection = connection;
         connection.getCalls().add(new RealCall.CallReference(this, this.callStackTrace));
      }
   }

   internal fun <E : IOException?> messageDone(exchange: Exchange, requestDone: Boolean, responseDone: Boolean, e: E): E {
      if (!(exchange == this.exchange)) {
         return (E)e;
      } else {
         var bothStreamsDone: Boolean = false;
         var callDone: Boolean = false;
         synchronized (this) {
            if (requestDone && this.requestBodyOpen || responseDone && this.responseBodyOpen) {
               if (requestDone) {
                  this.requestBodyOpen = false;
               }

               if (responseDone) {
                  this.responseBodyOpen = false;
               }

               bothStreamsDone = !this.requestBodyOpen && !this.responseBodyOpen;
               callDone = !this.requestBodyOpen && !this.responseBodyOpen && !this.expectMoreExchanges;
            }
         }

         if (bothStreamsDone) {
            this.exchange = null;
            if (this.connection != null) {
               this.connection.incrementSuccessCount$okhttp();
            }
         }

         return (E)(if (callDone) this.callDone(e) else e);
      }
   }

   internal fun noMoreExchanges(e: IOException?): IOException? {
      var callDone: Boolean = false;
      synchronized (this) {
         if (this.expectMoreExchanges) {
            this.expectMoreExchanges = false;
            callDone = !this.requestBodyOpen && !this.responseBodyOpen;
         }
      }

      return if (callDone) this.callDone(e) else e;
   }

   private fun <E : IOException?> callDone(e: E): E {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         var connection: RealConnection = this.connection;
         if (this.connection != null) {
            if (Util.assertionsEnabled && Thread.holdsLock(this.connection)) {
               throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + connection);
            }

            var var10000: Socket;
            synchronized (this.connection) {
               var10000 = this.releaseConnectionNoEvents$okhttp();
            }

            if (this.connection == null) {
               if (var10000 != null) {
                  Util.closeQuietly(var10000);
               }

               this.eventListener.connectionReleased(this, connection);
            } else if (var10000 != null) {
               throw new IllegalStateException("Check failed.".toString());
            }
         }

         var var8: IOException = this.timeoutExit(e);
         if (e != null) {
            var var11: EventListener = this.eventListener;
            var var10001: Call = this;
            var11.callFailed(var10001, var8);
         } else {
            this.eventListener.callEnd(this);
         }

         return (E)var8;
      }
   }

   internal fun releaseConnectionNoEvents(): Socket? {
      var var10000: RealConnection = this.connection;
      if (Util.assertionsEnabled && !Thread.holdsLock(var10000)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + var10000);
      } else {
         var calls: java.util.List = var10000.getCalls();
         var `index$iv`: Int = 0;
         var var7: java.util.Iterator = calls.iterator();

         while (true) {
            if (!var7.hasNext()) {
               var13 = -1;
               break;
            }

            if ((var7.next() as Reference).get() == this) {
               var13 = `index$iv`;
               break;
            }

            `index$iv`++;
         }

         if (var13 == -1) {
            throw new IllegalStateException("Check failed.".toString());
         } else {
            calls.remove(var13);
            this.connection = null;
            if (calls.isEmpty()) {
               var10000.setIdleAtNs$okhttp(System.nanoTime());
               if (this.connectionPool.connectionBecameIdle(var10000)) {
                  return var10000.socket();
               }
            }

            return null;
         }
      }
   }

   private fun <E : IOException?> timeoutExit(cause: E): E {
      if (this.timeoutEarlyExit) {
         return (E)cause;
      } else if (!this.timeout.exit()) {
         return (E)cause;
      } else {
         var e: InterruptedIOException = new InterruptedIOException("timeout");
         if (cause != null) {
            e.initCause(cause);
         }

         return (E)(e as IOException);
      }
   }

   public fun timeoutEarlyExit() {
      if (this.timeoutEarlyExit) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         this.timeoutEarlyExit = true;
         this.timeout.exit();
      }
   }

   internal fun exitNetworkInterceptorExchange(closeExchange: Boolean) {
      synchronized (this) {
         if (!this.expectMoreExchanges) {
            throw new IllegalStateException("released".toString());
         }
      }

      if (closeExchange) {
         if (this.exchange != null) {
            this.exchange.detachWithViolence();
         }
      }

      this.interceptorScopedExchange = null;
   }

   private fun createAddress(url: HttpUrl): Address {
      var sslSocketFactory: SSLSocketFactory = null;
      var hostnameVerifier: HostnameVerifier = null;
      var certificatePinner: CertificatePinner = null;
      if (url.isHttps()) {
         sslSocketFactory = this.client.sslSocketFactory();
         hostnameVerifier = this.client.hostnameVerifier();
         certificatePinner = this.client.certificatePinner();
      }

      return new Address(
         url.host(),
         url.port(),
         this.client.dns(),
         this.client.socketFactory(),
         sslSocketFactory,
         hostnameVerifier,
         certificatePinner,
         this.client.proxyAuthenticator(),
         this.client.proxy(),
         this.client.protocols(),
         this.client.connectionSpecs(),
         this.client.proxySelector()
      );
   }

   public fun retryAfterFailure(): Boolean {
      var var10000: ExchangeFinder = this.exchangeFinder;
      return var10000.retryAfterFailure();
   }

   private fun toLoggableString(): String {
      return (if (this.isCanceled()) "canceled " else "") + (if (this.forWebSocket) "web socket" else "call") + " to " + this.redactedUrl$okhttp();
   }

   internal fun redactedUrl(): String {
      return this.originalRequest.url().redact();
   }

   @SourceDebugExtension(["SMAP\nRealCall.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealCall.kt\nokhttp3/internal/connection/RealCall$AsyncCall\n+ 2 Util.kt\nokhttp3/internal/Util\n*L\n1#1,551:1\n615#2,4:552\n409#2,9:556\n*S KotlinDebug\n*F\n+ 1 RealCall.kt\nokhttp3/internal/connection/RealCall$AsyncCall\n*L\n494#1:552,4\n513#1:556,9\n*E\n"])
   internal inner class AsyncCall(responseCallback: Callback) : Runnable {
      public final val call: RealCall
         public final get() {
            return this.this$0;
         }

      public final var callsPerHost: AtomicInteger
         private set
      public final val host: String
         public final get() {
            return this.this$0.getOriginalRequest().url().host();
         }

      public final val request: Request
         public final get() {
            return this.this$0.getOriginalRequest();
         }

      private final val responseCallback: Callback

      init {
         this.this$0 = `this$0`;
         this.responseCallback = responseCallback;
         this.callsPerHost = new AtomicInteger(0);
      }

      public fun reuseCallsPerHostFrom(other: okhttp3.internal.connection.RealCall.AsyncCall) {
         this.callsPerHost = other.callsPerHost;
      }

      public fun executeOn(executorService: ExecutorService) {
         var success: Any = this.this$0.getClient().dispatcher();
         if (Util.assertionsEnabled && Thread.holdsLock(success)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + success);
         } else {
            label52: {
               try {
                  try {
                     return;
                  } catch (RejectedExecutionException var5) {
                     var ioException: InterruptedIOException = new InterruptedIOException("executor rejected");
                     ioException.initCause(var5);
                     this.this$0.noMoreExchanges$okhttp(ioException);
                     this.responseCallback.onFailure(this.this$0, ioException);
                  }
               } catch (java.lang.Throwable var6) {
                  this.this$0.getClient().dispatcher().finished$okhttp(this);
               }

               this.this$0.getClient().dispatcher().finished$okhttp(this);
            }
         }
      }

      public override fun run() {
         label48: {
            var `name$iv`: java.lang.String = "OkHttp " + this.this$0.redactedUrl$okhttp();
            var var2: RealCall = this.this$0;
            var `currentThread$iv`: Thread = Thread.currentThread();
            var `oldName$iv`: java.lang.String = `currentThread$iv`.getName();
            `currentThread$iv`.setName(`name$iv`);

            try {
               label94: {
                  var signalledCallback: Boolean = false;
                  RealCall.access$getTimeout$p(var2).enter();

                  label41: {
                     try {
                        try {
                           var t: Response = var2.getResponseWithInterceptorChain$okhttp();
                           signalledCallback = true;
                           this.responseCallback.onResponse(var2, t);
                           break label41;
                        } catch (IOException var10) {
                           if (signalledCallback) {
                              Platform.Companion.get().log("Callback failure for " + RealCall.access$toLoggableString(var2), 4, var10);
                           } else {
                              this.responseCallback.onFailure(var2, var10);
                           }
                        } catch (java.lang.Throwable var11) {
                           var2.cancel();
                           if (!signalledCallback) {
                              var canceledException: IOException = new IOException("canceled due to " + var11);
                              ExceptionsKt.addSuppressed(canceledException, var11);
                              this.responseCallback.onFailure(var2, canceledException);
                           }

                           throw var11;
                        }
                     } catch (java.lang.Throwable var12) {
                        var2.getClient().dispatcher().finished$okhttp(this);
                     }

                     var2.getClient().dispatcher().finished$okhttp(this);
                     break label94;
                  }

                  var2.getClient().dispatcher().finished$okhttp(this);
               }
            } catch (java.lang.Throwable var13) {
               `currentThread$iv`.setName(`oldName$iv`);
            }

            `currentThread$iv`.setName(`oldName$iv`);
         }
      }
   }

   internal class CallReference(referent: RealCall, callStackTrace: Any?) : WeakReference(referent) {
      public final val callStackTrace: Any?

      init {
         this.callStackTrace = callStackTrace;
      }
   }
}
