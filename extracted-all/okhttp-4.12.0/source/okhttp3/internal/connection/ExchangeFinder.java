package okhttp3.internal.connection

import java.io.IOException
import okhttp3.Address
import okhttp3.EventListener
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Route
import okhttp3.internal.Util
import okhttp3.internal.connection.RouteSelector.Selection
import okhttp3.internal.http.ExchangeCodec
import okhttp3.internal.http.RealInterceptorChain
import okhttp3.internal.http2.ConnectionShutdownException
import okhttp3.internal.http2.ErrorCode
import okhttp3.internal.http2.StreamResetException

class ExchangeFinder(connectionPool: RealConnectionPool, address: Address, call: RealCall, eventListener: EventListener) {
   internal final val address: Address
   private final val call: RealCall
   private final val connectionPool: RealConnectionPool
   private final var connectionShutdownCount: Int
   private final val eventListener: EventListener
   private final var nextRouteToTry: Route?
   private final var otherFailureCount: Int
   private final var refusedStreamCount: Int
   private final var routeSelection: Selection?
   private final var routeSelector: RouteSelector?

   init {
      this.connectionPool = connectionPool;
      this.address = address;
      this.call = call;
      this.eventListener = eventListener;
   }

   public fun find(client: OkHttpClient, chain: RealInterceptorChain): ExchangeCodec {
      try {
         return this.findHealthyConnection(
               chain.getConnectTimeoutMillis$okhttp(),
               chain.getReadTimeoutMillis$okhttp(),
               chain.getWriteTimeoutMillis$okhttp(),
               client.pingIntervalMillis(),
               client.retryOnConnectionFailure(),
               !(chain.getRequest$okhttp().method() == "GET")
            )
            .newCodec$okhttp(client, chain);
      } catch (RouteException var4) {
         this.trackFailure(var4.getLastConnectException());
         throw var4;
      } catch (IOException var5) {
         this.trackFailure(var5);
         throw new RouteException(var5);
      }
   }

   @Throws(java/io/IOException::class)
   private fun findHealthyConnection(
      connectTimeout: Int,
      readTimeout: Int,
      writeTimeout: Int,
      pingIntervalMillis: Int,
      connectionRetryEnabled: Boolean,
      doExtensiveHealthChecks: Boolean
   ): RealConnection {
      while (true) {
         var candidate: RealConnection = this.findConnection(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled);
         if (candidate.isHealthy(doExtensiveHealthChecks)) {
            return candidate;
         }

         candidate.noNewExchanges$okhttp();
         if (this.nextRouteToTry == null
            && this.routeSelection != null
            && !this.routeSelection.hasNext()
            && this.routeSelector != null
            && !this.routeSelector.hasNext()) {
            throw new IOException("exhausted all routes");
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun findConnection(connectTimeout: Int, readTimeout: Int, writeTimeout: Int, pingIntervalMillis: Int, connectionRetryEnabled: Boolean): RealConnection {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 004: invokevirtual okhttp3/internal/connection/RealCall.isCanceled ()Z
      // 007: ifeq 014
      // 00a: new java/io/IOException
      // 00d: dup
      // 00e: ldc "Canceled"
      // 010: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 013: athrow
      // 014: aload 0
      // 015: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 018: invokevirtual okhttp3/internal/connection/RealCall.getConnection ()Lokhttp3/internal/connection/RealConnection;
      // 01b: astore 6
      // 01d: aload 6
      // 01f: ifnull 0b2
      // 022: aconst_null
      // 023: astore 7
      // 025: aload 6
      // 027: astore 8
      // 029: aload 8
      // 02b: monitorenter
      // 02c: nop
      // 02d: bipush 0
      // 02e: istore 9
      // 030: aload 6
      // 032: invokevirtual okhttp3/internal/connection/RealConnection.getNoNewExchanges ()Z
      // 035: ifne 04a
      // 038: aload 0
      // 039: aload 6
      // 03b: invokevirtual okhttp3/internal/connection/RealConnection.route ()Lokhttp3/Route;
      // 03e: invokevirtual okhttp3/Route.address ()Lokhttp3/Address;
      // 041: invokevirtual okhttp3/Address.url ()Lokhttp3/HttpUrl;
      // 044: invokevirtual okhttp3/internal/connection/ExchangeFinder.sameHostAndPort (Lokhttp3/HttpUrl;)Z
      // 047: ifne 053
      // 04a: aload 0
      // 04b: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 04e: invokevirtual okhttp3/internal/connection/RealCall.releaseConnectionNoEvents$okhttp ()Ljava/net/Socket;
      // 051: astore 7
      // 053: nop
      // 054: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 057: astore 9
      // 059: aload 8
      // 05b: monitorexit
      // 05c: goto 067
      // 05f: astore 9
      // 061: aload 8
      // 063: monitorexit
      // 064: aload 9
      // 066: athrow
      // 067: aload 0
      // 068: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 06b: invokevirtual okhttp3/internal/connection/RealCall.getConnection ()Lokhttp3/internal/connection/RealConnection;
      // 06e: ifnull 092
      // 071: aload 7
      // 073: ifnonnull 07a
      // 076: bipush 1
      // 077: goto 07b
      // 07a: bipush 0
      // 07b: ifne 08f
      // 07e: ldc "Check failed."
      // 080: astore 9
      // 082: new java/lang/IllegalStateException
      // 085: dup
      // 086: aload 9
      // 088: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 08b: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 08e: athrow
      // 08f: aload 6
      // 091: areturn
      // 092: aload 7
      // 094: dup
      // 095: ifnull 09e
      // 098: invokestatic okhttp3/internal/Util.closeQuietly (Ljava/net/Socket;)V
      // 09b: goto 09f
      // 09e: pop
      // 09f: aload 0
      // 0a0: getfield okhttp3/internal/connection/ExchangeFinder.eventListener Lokhttp3/EventListener;
      // 0a3: aload 0
      // 0a4: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 0a7: checkcast okhttp3/Call
      // 0aa: aload 6
      // 0ac: checkcast okhttp3/Connection
      // 0af: invokevirtual okhttp3/EventListener.connectionReleased (Lokhttp3/Call;Lokhttp3/Connection;)V
      // 0b2: aload 0
      // 0b3: bipush 0
      // 0b4: putfield okhttp3/internal/connection/ExchangeFinder.refusedStreamCount I
      // 0b7: aload 0
      // 0b8: bipush 0
      // 0b9: putfield okhttp3/internal/connection/ExchangeFinder.connectionShutdownCount I
      // 0bc: aload 0
      // 0bd: bipush 0
      // 0be: putfield okhttp3/internal/connection/ExchangeFinder.otherFailureCount I
      // 0c1: aload 0
      // 0c2: getfield okhttp3/internal/connection/ExchangeFinder.connectionPool Lokhttp3/internal/connection/RealConnectionPool;
      // 0c5: aload 0
      // 0c6: getfield okhttp3/internal/connection/ExchangeFinder.address Lokhttp3/Address;
      // 0c9: aload 0
      // 0ca: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 0cd: aconst_null
      // 0ce: bipush 0
      // 0cf: invokevirtual okhttp3/internal/connection/RealConnectionPool.callAcquirePooledConnection (Lokhttp3/Address;Lokhttp3/internal/connection/RealCall;Ljava/util/List;Z)Z
      // 0d2: ifeq 0f8
      // 0d5: aload 0
      // 0d6: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 0d9: invokevirtual okhttp3/internal/connection/RealCall.getConnection ()Lokhttp3/internal/connection/RealConnection;
      // 0dc: dup
      // 0dd: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 0e0: astore 7
      // 0e2: aload 0
      // 0e3: getfield okhttp3/internal/connection/ExchangeFinder.eventListener Lokhttp3/EventListener;
      // 0e6: aload 0
      // 0e7: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 0ea: checkcast okhttp3/Call
      // 0ed: aload 7
      // 0ef: checkcast okhttp3/Connection
      // 0f2: invokevirtual okhttp3/EventListener.connectionAcquired (Lokhttp3/Call;Lokhttp3/Connection;)V
      // 0f5: aload 7
      // 0f7: areturn
      // 0f8: aconst_null
      // 0f9: astore 7
      // 0fb: aconst_null
      // 0fc: astore 8
      // 0fe: aload 0
      // 0ff: getfield okhttp3/internal/connection/ExchangeFinder.nextRouteToTry Lokhttp3/Route;
      // 102: ifnull 11a
      // 105: aconst_null
      // 106: astore 7
      // 108: aload 0
      // 109: getfield okhttp3/internal/connection/ExchangeFinder.nextRouteToTry Lokhttp3/Route;
      // 10c: dup
      // 10d: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 110: astore 8
      // 112: aload 0
      // 113: aconst_null
      // 114: putfield okhttp3/internal/connection/ExchangeFinder.nextRouteToTry Lokhttp3/Route;
      // 117: goto 1dc
      // 11a: aload 0
      // 11b: getfield okhttp3/internal/connection/ExchangeFinder.routeSelection Lokhttp3/internal/connection/RouteSelector$Selection;
      // 11e: ifnull 142
      // 121: aload 0
      // 122: getfield okhttp3/internal/connection/ExchangeFinder.routeSelection Lokhttp3/internal/connection/RouteSelector$Selection;
      // 125: dup
      // 126: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 129: invokevirtual okhttp3/internal/connection/RouteSelector$Selection.hasNext ()Z
      // 12c: ifeq 142
      // 12f: aconst_null
      // 130: astore 7
      // 132: aload 0
      // 133: getfield okhttp3/internal/connection/ExchangeFinder.routeSelection Lokhttp3/internal/connection/RouteSelector$Selection;
      // 136: dup
      // 137: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 13a: invokevirtual okhttp3/internal/connection/RouteSelector$Selection.next ()Lokhttp3/Route;
      // 13d: astore 8
      // 13f: goto 1dc
      // 142: aload 0
      // 143: getfield okhttp3/internal/connection/ExchangeFinder.routeSelector Lokhttp3/internal/connection/RouteSelector;
      // 146: astore 9
      // 148: aload 9
      // 14a: ifnonnull 175
      // 14d: new okhttp3/internal/connection/RouteSelector
      // 150: dup
      // 151: aload 0
      // 152: getfield okhttp3/internal/connection/ExchangeFinder.address Lokhttp3/Address;
      // 155: aload 0
      // 156: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 159: invokevirtual okhttp3/internal/connection/RealCall.getClient ()Lokhttp3/OkHttpClient;
      // 15c: invokevirtual okhttp3/OkHttpClient.getRouteDatabase ()Lokhttp3/internal/connection/RouteDatabase;
      // 15f: aload 0
      // 160: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 163: checkcast okhttp3/Call
      // 166: aload 0
      // 167: getfield okhttp3/internal/connection/ExchangeFinder.eventListener Lokhttp3/EventListener;
      // 16a: invokespecial okhttp3/internal/connection/RouteSelector.<init> (Lokhttp3/Address;Lokhttp3/internal/connection/RouteDatabase;Lokhttp3/Call;Lokhttp3/EventListener;)V
      // 16d: astore 9
      // 16f: aload 0
      // 170: aload 9
      // 172: putfield okhttp3/internal/connection/ExchangeFinder.routeSelector Lokhttp3/internal/connection/RouteSelector;
      // 175: aload 9
      // 177: invokevirtual okhttp3/internal/connection/RouteSelector.next ()Lokhttp3/internal/connection/RouteSelector$Selection;
      // 17a: astore 10
      // 17c: aload 0
      // 17d: aload 10
      // 17f: putfield okhttp3/internal/connection/ExchangeFinder.routeSelection Lokhttp3/internal/connection/RouteSelector$Selection;
      // 182: aload 10
      // 184: invokevirtual okhttp3/internal/connection/RouteSelector$Selection.getRoutes ()Ljava/util/List;
      // 187: astore 7
      // 189: aload 0
      // 18a: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 18d: invokevirtual okhttp3/internal/connection/RealCall.isCanceled ()Z
      // 190: ifeq 19d
      // 193: new java/io/IOException
      // 196: dup
      // 197: ldc "Canceled"
      // 199: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 19c: athrow
      // 19d: aload 0
      // 19e: getfield okhttp3/internal/connection/ExchangeFinder.connectionPool Lokhttp3/internal/connection/RealConnectionPool;
      // 1a1: aload 0
      // 1a2: getfield okhttp3/internal/connection/ExchangeFinder.address Lokhttp3/Address;
      // 1a5: aload 0
      // 1a6: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 1a9: aload 7
      // 1ab: bipush 0
      // 1ac: invokevirtual okhttp3/internal/connection/RealConnectionPool.callAcquirePooledConnection (Lokhttp3/Address;Lokhttp3/internal/connection/RealCall;Ljava/util/List;Z)Z
      // 1af: ifeq 1d5
      // 1b2: aload 0
      // 1b3: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 1b6: invokevirtual okhttp3/internal/connection/RealCall.getConnection ()Lokhttp3/internal/connection/RealConnection;
      // 1b9: dup
      // 1ba: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 1bd: astore 11
      // 1bf: aload 0
      // 1c0: getfield okhttp3/internal/connection/ExchangeFinder.eventListener Lokhttp3/EventListener;
      // 1c3: aload 0
      // 1c4: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 1c7: checkcast okhttp3/Call
      // 1ca: aload 11
      // 1cc: checkcast okhttp3/Connection
      // 1cf: invokevirtual okhttp3/EventListener.connectionAcquired (Lokhttp3/Call;Lokhttp3/Connection;)V
      // 1d2: aload 11
      // 1d4: areturn
      // 1d5: aload 10
      // 1d7: invokevirtual okhttp3/internal/connection/RouteSelector$Selection.next ()Lokhttp3/Route;
      // 1da: astore 8
      // 1dc: new okhttp3/internal/connection/RealConnection
      // 1df: dup
      // 1e0: aload 0
      // 1e1: getfield okhttp3/internal/connection/ExchangeFinder.connectionPool Lokhttp3/internal/connection/RealConnectionPool;
      // 1e4: aload 8
      // 1e6: invokespecial okhttp3/internal/connection/RealConnection.<init> (Lokhttp3/internal/connection/RealConnectionPool;Lokhttp3/Route;)V
      // 1e9: astore 9
      // 1eb: aload 0
      // 1ec: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 1ef: aload 9
      // 1f1: invokevirtual okhttp3/internal/connection/RealCall.setConnectionToCancel (Lokhttp3/internal/connection/RealConnection;)V
      // 1f4: nop
      // 1f5: aload 9
      // 1f7: iload 1
      // 1f8: iload 2
      // 1f9: iload 3
      // 1fa: iload 4
      // 1fc: iload 5
      // 1fe: aload 0
      // 1ff: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 202: checkcast okhttp3/Call
      // 205: aload 0
      // 206: getfield okhttp3/internal/connection/ExchangeFinder.eventListener Lokhttp3/EventListener;
      // 209: invokevirtual okhttp3/internal/connection/RealConnection.connect (IIIIZLokhttp3/Call;Lokhttp3/EventListener;)V
      // 20c: aload 0
      // 20d: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 210: aconst_null
      // 211: invokevirtual okhttp3/internal/connection/RealCall.setConnectionToCancel (Lokhttp3/internal/connection/RealConnection;)V
      // 214: goto 224
      // 217: astore 10
      // 219: aload 0
      // 21a: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 21d: aconst_null
      // 21e: invokevirtual okhttp3/internal/connection/RealCall.setConnectionToCancel (Lokhttp3/internal/connection/RealConnection;)V
      // 221: aload 10
      // 223: athrow
      // 224: aload 0
      // 225: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 228: invokevirtual okhttp3/internal/connection/RealCall.getClient ()Lokhttp3/OkHttpClient;
      // 22b: invokevirtual okhttp3/OkHttpClient.getRouteDatabase ()Lokhttp3/internal/connection/RouteDatabase;
      // 22e: aload 9
      // 230: invokevirtual okhttp3/internal/connection/RealConnection.route ()Lokhttp3/Route;
      // 233: invokevirtual okhttp3/internal/connection/RouteDatabase.connected (Lokhttp3/Route;)V
      // 236: aload 0
      // 237: getfield okhttp3/internal/connection/ExchangeFinder.connectionPool Lokhttp3/internal/connection/RealConnectionPool;
      // 23a: aload 0
      // 23b: getfield okhttp3/internal/connection/ExchangeFinder.address Lokhttp3/Address;
      // 23e: aload 0
      // 23f: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 242: aload 7
      // 244: bipush 1
      // 245: invokevirtual okhttp3/internal/connection/RealConnectionPool.callAcquirePooledConnection (Lokhttp3/Address;Lokhttp3/internal/connection/RealCall;Ljava/util/List;Z)Z
      // 248: ifeq 27c
      // 24b: aload 0
      // 24c: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 24f: invokevirtual okhttp3/internal/connection/RealCall.getConnection ()Lokhttp3/internal/connection/RealConnection;
      // 252: dup
      // 253: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 256: astore 10
      // 258: aload 0
      // 259: aload 8
      // 25b: putfield okhttp3/internal/connection/ExchangeFinder.nextRouteToTry Lokhttp3/Route;
      // 25e: aload 9
      // 260: invokevirtual okhttp3/internal/connection/RealConnection.socket ()Ljava/net/Socket;
      // 263: invokestatic okhttp3/internal/Util.closeQuietly (Ljava/net/Socket;)V
      // 266: aload 0
      // 267: getfield okhttp3/internal/connection/ExchangeFinder.eventListener Lokhttp3/EventListener;
      // 26a: aload 0
      // 26b: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 26e: checkcast okhttp3/Call
      // 271: aload 10
      // 273: checkcast okhttp3/Connection
      // 276: invokevirtual okhttp3/EventListener.connectionAcquired (Lokhttp3/Call;Lokhttp3/Connection;)V
      // 279: aload 10
      // 27b: areturn
      // 27c: aload 9
      // 27e: astore 10
      // 280: aload 10
      // 282: monitorenter
      // 283: nop
      // 284: bipush 0
      // 285: istore 11
      // 287: aload 0
      // 288: getfield okhttp3/internal/connection/ExchangeFinder.connectionPool Lokhttp3/internal/connection/RealConnectionPool;
      // 28b: aload 9
      // 28d: invokevirtual okhttp3/internal/connection/RealConnectionPool.put (Lokhttp3/internal/connection/RealConnection;)V
      // 290: aload 0
      // 291: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 294: aload 9
      // 296: invokevirtual okhttp3/internal/connection/RealCall.acquireConnectionNoEvents (Lokhttp3/internal/connection/RealConnection;)V
      // 299: nop
      // 29a: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 29d: astore 11
      // 29f: aload 10
      // 2a1: monitorexit
      // 2a2: goto 2ad
      // 2a5: astore 11
      // 2a7: aload 10
      // 2a9: monitorexit
      // 2aa: aload 11
      // 2ac: athrow
      // 2ad: aload 0
      // 2ae: getfield okhttp3/internal/connection/ExchangeFinder.eventListener Lokhttp3/EventListener;
      // 2b1: aload 0
      // 2b2: getfield okhttp3/internal/connection/ExchangeFinder.call Lokhttp3/internal/connection/RealCall;
      // 2b5: checkcast okhttp3/Call
      // 2b8: aload 9
      // 2ba: checkcast okhttp3/Connection
      // 2bd: invokevirtual okhttp3/EventListener.connectionAcquired (Lokhttp3/Call;Lokhttp3/Connection;)V
      // 2c0: aload 9
      // 2c2: areturn
   }

   public fun trackFailure(e: IOException) {
      this.nextRouteToTry = null;
      if (e is StreamResetException && (e as StreamResetException).errorCode === ErrorCode.REFUSED_STREAM) {
         var var4: Int = this.refusedStreamCount++;
      } else if (e is ConnectionShutdownException) {
         var var2: Int = this.connectionShutdownCount++;
      } else {
         var var3: Int = this.otherFailureCount++;
      }
   }

   public fun retryAfterFailure(): Boolean {
      if (this.refusedStreamCount == 0 && this.connectionShutdownCount == 0 && this.otherFailureCount == 0) {
         return false;
      } else if (this.nextRouteToTry != null) {
         return true;
      } else {
         var retryRoute: Route = this.retryRoute();
         if (retryRoute != null) {
            this.nextRouteToTry = retryRoute;
            return true;
         } else {
            label24:
            if (this.routeSelection != null && this.routeSelection.hasNext()) {
               return true;
            } else {
               return this.routeSelector == null || this.routeSelector.hasNext();
            }
         }
      }
   }

   private fun retryRoute(): Route? {
      if (this.refusedStreamCount <= 1 && this.connectionShutdownCount <= 1 && this.otherFailureCount <= 0) {
         var var10000: RealConnection = this.call.getConnection();
         if (var10000 == null) {
            return null;
         } else {
            label74: {
               var connection: RealConnection = var10000;
               synchronized (var10000){} // $VF: monitorenter 

               label36: {
                  label35: {
                     try {
                        if (connection.getRouteFailureCount$okhttp() != 0) {
                           break label36;
                        }

                        if (!Util.canReuseConnectionFor(connection.route().address().url(), this.address.url())) {
                           break label35;
                        }

                        var var4: Route = connection.route();
                     } catch (java.lang.Throwable var7) {
                        // $VF: monitorexit
                     }

                     // $VF: monitorexit
                  }

                  // $VF: monitorexit
               }

               // $VF: monitorexit
            }
         }
      } else {
         return null;
      }
   }

   public fun sameHostAndPort(url: HttpUrl): Boolean {
      var routeUrl: HttpUrl = this.address.url();
      return url.port() == routeUrl.port() && url.host() == routeUrl.host();
   }
}
