package okhttp3.internal.connection

import java.io.IOException
import java.lang.ref.Reference
import java.net.ConnectException
import java.net.ProtocolException
import java.net.Proxy
import java.net.Socket
import java.net.UnknownServiceException
import java.net.Proxy.Type
import java.security.cert.X509Certificate
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLPeerUnverifiedException
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Address
import okhttp3.Call
import okhttp3.CertificatePinner
import okhttp3.CipherSuite
import okhttp3.Connection
import okhttp3.ConnectionSpec
import okhttp3.EventListener
import okhttp3.Handshake
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.internal.Util
import okhttp3.internal.concurrent.TaskRunner
import okhttp3.internal.http.ExchangeCodec
import okhttp3.internal.http.RealInterceptorChain
import okhttp3.internal.http1.Http1ExchangeCodec
import okhttp3.internal.http2.ConnectionShutdownException
import okhttp3.internal.http2.ErrorCode
import okhttp3.internal.http2.Http2Connection
import okhttp3.internal.http2.Http2ExchangeCodec
import okhttp3.internal.http2.Http2Stream
import okhttp3.internal.http2.Settings
import okhttp3.internal.http2.StreamResetException
import okhttp3.internal.platform.Platform
import okhttp3.internal.tls.OkHostnameVerifier
import okhttp3.internal.ws.RealWebSocket
import okhttp3.internal.ws.RealWebSocket.Streams
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio

@SourceDebugExtension(["SMAP\nRealConnection.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealConnection.kt\nokhttp3/internal/connection/RealConnection\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokhttp3/internal/Util\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,765:1\n1#2:766\n608#3,4:767\n608#3,4:774\n615#3,4:778\n1747#4,3:771\n*S KotlinDebug\n*F\n+ 1 RealConnection.kt\nokhttp3/internal/connection/RealConnection\n*L\n529#1:767,4\n582#1:774,4\n648#1:778,4\n574#1:771,3\n*E\n"])
class RealConnection(connectionPool: RealConnectionPool, route: Route) : Http2Connection.Listener, Connection {
   @JvmStatic
   public RealConnection.Companion Companion = new RealConnection.Companion(null);
   @JvmStatic
   private java.lang.String NPE_THROW_WITH_NULL = "throw with null exception";
   @JvmStatic
   private int MAX_TUNNEL_ATTEMPTS = 21;
   @JvmStatic
   public long IDLE_CONNECTION_HEALTHY_NS = 10000000000L;

   private final var allocationLimit: Int
   public final val calls: MutableList<Reference<RealCall>>
   public final val connectionPool: RealConnectionPool
   private final var handshake: Handshake?
   private final var http2Connection: Http2Connection?
   internal final var idleAtNs: Long
   internal final val isMultiplexed: Boolean
      internal final get() {
         return this.http2Connection != null;
      }

   private final var noCoalescedConnections: Boolean
   public final var noNewExchanges: Boolean
      internal set
   private final var protocol: Protocol?
   private final var rawSocket: Socket?
   private final var refusedStreamCount: Int
   private final val route: Route
   internal final var routeFailureCount: Int
   private final var sink: BufferedSink?
   private final var socket: Socket?
   private final var source: BufferedSource?
   private final var successCount: Int

   init {
      this.connectionPool = connectionPool;
      this.route = route;
      this.allocationLimit = 1;
      this.calls = new ArrayList<>();
      this.idleAtNs = java.lang.Long.MAX_VALUE;
   }

   @Synchronized
   internal fun noNewExchanges() {
      this.noNewExchanges = true;
   }

   @Synchronized
   internal fun noCoalescedConnections() {
      this.noCoalescedConnections = true;
   }

   @Synchronized
   internal fun incrementSuccessCount() {
      var var1: Int = this.successCount++;
   }

   public fun connect(
      connectTimeout: Int,
      readTimeout: Int,
      writeTimeout: Int,
      pingIntervalMillis: Int,
      connectionRetryEnabled: Boolean,
      call: Call,
      eventListener: EventListener
   ) {
      if (this.protocol != null) {
         throw new IllegalStateException("already connected".toString());
      } else {
         var routeException: RouteException = null;
         var connectionSpecs: java.util.List = this.route.address().connectionSpecs();
         var connectionSpecSelector: ConnectionSpecSelector = new ConnectionSpecSelector(connectionSpecs);
         if (this.route.address().sslSocketFactory() == null) {
            if (!connectionSpecs.contains(ConnectionSpec.CLEARTEXT)) {
               throw new RouteException(new UnknownServiceException("CLEARTEXT communication not enabled for client"));
            }

            var e: java.lang.String = this.route.address().url().host();
            if (!Platform.Companion.get().isCleartextTrafficPermitted(e)) {
               throw new RouteException(new UnknownServiceException("CLEARTEXT communication to " + e + " not permitted by network security policy"));
            }
         } else if (this.route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
            throw new RouteException(new UnknownServiceException("H2_PRIOR_KNOWLEDGE cannot be used with HTTPS"));
         }

         while (true) {
            try {
               if (this.route.requiresTunnel()) {
                  this.connectTunnel(connectTimeout, readTimeout, writeTimeout, call, eventListener);
                  if (this.rawSocket == null) {
                     break;
                  }
               } else {
                  this.connectSocket(connectTimeout, readTimeout, call, eventListener);
               }

               this.establishProtocol(connectionSpecSelector, pingIntervalMillis, call, eventListener);
               eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), this.protocol);
               break;
            } catch (IOException var12) {
               if (this.socket != null) {
                  Util.closeQuietly(this.socket);
               }

               if (this.rawSocket != null) {
                  Util.closeQuietly(this.rawSocket);
               }

               this.socket = null;
               this.rawSocket = null;
               this.source = null;
               this.sink = null;
               this.handshake = null;
               this.protocol = null;
               this.http2Connection = null;
               this.allocationLimit = 1;
               eventListener.connectFailed(call, this.route.socketAddress(), this.route.proxy(), null, var12);
               if (routeException == null) {
                  routeException = new RouteException(var12);
               } else {
                  routeException.addConnectException(var12);
               }

               if (!connectionRetryEnabled || !connectionSpecSelector.connectionFailed(var12)) {
                  throw routeException;
               }
            }
         }

         if (this.route.requiresTunnel() && this.rawSocket == null) {
            throw new RouteException(new ProtocolException("Too many tunnel connections attempted: 21"));
         } else {
            this.idleAtNs = System.nanoTime();
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun connectTunnel(connectTimeout: Int, readTimeout: Int, writeTimeout: Int, call: Call, eventListener: EventListener) {
      var tunnelRequest: Request = this.createTunnelRequest();
      var url: HttpUrl = tunnelRequest.url();

      for (int i = 0; i < 21; i++) {
         this.connectSocket(connectTimeout, readTimeout, call, eventListener);
         var var10000: Request = this.createTunnel(readTimeout, writeTimeout, tunnelRequest, url);
         if (var10000 == null) {
            break;
         }

         tunnelRequest = var10000;
         if (this.rawSocket != null) {
            Util.closeQuietly(this.rawSocket);
         }

         this.rawSocket = null;
         this.sink = null;
         this.source = null;
         eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), null);
      }
   }

   @Throws(java/io/IOException::class)
   private fun connectSocket(connectTimeout: Int, readTimeout: Int, call: Call, eventListener: EventListener) {
      var proxy: Proxy = this.route.proxy();
      var address: Address = this.route.address();
      var var10000: Type = proxy.type();
      var var14: Socket;
      switch (var10000 == null ? -1 : RealConnection.WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
         case 1:
         case 2:
            var14 = address.socketFactory().createSocket();
            break;
         default:
            var14 = new Socket(proxy);
      }

      var rawSocket: Socket = var14;
      this.rawSocket = var14;
      eventListener.connectStart(call, this.route.socketAddress(), proxy);
      var14.setSoTimeout(readTimeout);

      try {
         Platform.Companion.get().connectSocket(rawSocket, this.route.socketAddress(), connectTimeout);
      } catch (ConnectException var12) {
         var var9: ConnectException = new ConnectException("Failed to connect to " + this.route.socketAddress());
         var9.initCause(var12);
         throw var9 as java.lang.Throwable;
      }

      try {
         this.source = Okio.buffer(Okio.source(rawSocket));
         this.sink = Okio.buffer(Okio.sink(rawSocket));
      } catch (NullPointerException var13) {
         if (var13.getMessage() == "throw with null exception") {
            throw new IOException(var13);
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun establishProtocol(connectionSpecSelector: ConnectionSpecSelector, pingIntervalMillis: Int, call: Call, eventListener: EventListener) {
      if (this.route.address().sslSocketFactory() == null) {
         if (this.route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
            this.socket = this.rawSocket;
            this.protocol = Protocol.H2_PRIOR_KNOWLEDGE;
            this.startHttp2(pingIntervalMillis);
         } else {
            this.socket = this.rawSocket;
            this.protocol = Protocol.HTTP_1_1;
         }
      } else {
         eventListener.secureConnectStart(call);
         this.connectTls(connectionSpecSelector);
         eventListener.secureConnectEnd(call, this.handshake);
         if (this.protocol === Protocol.HTTP_2) {
            this.startHttp2(pingIntervalMillis);
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun startHttp2(pingIntervalMillis: Int) {
      var var10000: Socket = this.socket;
      var var6: BufferedSource = this.source;
      var var7: BufferedSink = this.sink;
      var10000.setSoTimeout(0);
      var http2Connection: Http2Connection = new Http2Connection.Builder(true, TaskRunner.INSTANCE)
         .socket(var10000, this.route.address().url().host(), var6, var7)
         .listener(this)
         .pingIntervalMillis(pingIntervalMillis)
         .build();
      this.http2Connection = http2Connection;
      this.allocationLimit = Http2Connection.Companion.getDEFAULT_SETTINGS().getMaxConcurrentStreams();
      Http2Connection.start$default(http2Connection, false, null, 3, null);
   }

   @Throws(java/io/IOException::class)
   private fun connectTls(connectionSpecSelector: ConnectionSpecSelector) {
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
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield okhttp3/internal/connection/RealConnection.route Lokhttp3/Route;
      // 004: invokevirtual okhttp3/Route.address ()Lokhttp3/Address;
      // 007: astore 2
      // 008: aload 2
      // 009: invokevirtual okhttp3/Address.sslSocketFactory ()Ljavax/net/ssl/SSLSocketFactory;
      // 00c: astore 3
      // 00d: bipush 0
      // 00e: istore 4
      // 010: aconst_null
      // 011: astore 5
      // 013: nop
      // 014: aload 3
      // 015: dup
      // 016: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 019: aload 0
      // 01a: getfield okhttp3/internal/connection/RealConnection.rawSocket Ljava/net/Socket;
      // 01d: aload 2
      // 01e: invokevirtual okhttp3/Address.url ()Lokhttp3/HttpUrl;
      // 021: invokevirtual okhttp3/HttpUrl.host ()Ljava/lang/String;
      // 024: aload 2
      // 025: invokevirtual okhttp3/Address.url ()Lokhttp3/HttpUrl;
      // 028: invokevirtual okhttp3/HttpUrl.port ()I
      // 02b: bipush 1
      // 02c: invokevirtual javax/net/ssl/SSLSocketFactory.createSocket (Ljava/net/Socket;Ljava/lang/String;IZ)Ljava/net/Socket;
      // 02f: dup
      // 030: ldc_w "null cannot be cast to non-null type javax.net.ssl.SSLSocket"
      // 033: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;Ljava/lang/String;)V
      // 036: checkcast javax/net/ssl/SSLSocket
      // 039: astore 5
      // 03b: aload 1
      // 03c: aload 5
      // 03e: invokevirtual okhttp3/internal/connection/ConnectionSpecSelector.configureSecureSocket (Ljavax/net/ssl/SSLSocket;)Lokhttp3/ConnectionSpec;
      // 041: astore 6
      // 043: aload 6
      // 045: invokevirtual okhttp3/ConnectionSpec.supportsTlsExtensions ()Z
      // 048: ifeq 061
      // 04b: getstatic okhttp3/internal/platform/Platform.Companion Lokhttp3/internal/platform/Platform$Companion;
      // 04e: invokevirtual okhttp3/internal/platform/Platform$Companion.get ()Lokhttp3/internal/platform/Platform;
      // 051: aload 5
      // 053: aload 2
      // 054: invokevirtual okhttp3/Address.url ()Lokhttp3/HttpUrl;
      // 057: invokevirtual okhttp3/HttpUrl.host ()Ljava/lang/String;
      // 05a: aload 2
      // 05b: invokevirtual okhttp3/Address.protocols ()Ljava/util/List;
      // 05e: invokevirtual okhttp3/internal/platform/Platform.configureTlsExtensions (Ljavax/net/ssl/SSLSocket;Ljava/lang/String;Ljava/util/List;)V
      // 061: aload 5
      // 063: invokevirtual javax/net/ssl/SSLSocket.startHandshake ()V
      // 066: aload 5
      // 068: invokevirtual javax/net/ssl/SSLSocket.getSession ()Ljavax/net/ssl/SSLSession;
      // 06b: astore 7
      // 06d: getstatic okhttp3/Handshake.Companion Lokhttp3/Handshake$Companion;
      // 070: aload 7
      // 072: ldc_w "sslSocketSession"
      // 075: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 078: aload 7
      // 07a: invokevirtual okhttp3/Handshake$Companion.get (Ljavax/net/ssl/SSLSession;)Lokhttp3/Handshake;
      // 07d: astore 8
      // 07f: aload 2
      // 080: invokevirtual okhttp3/Address.hostnameVerifier ()Ljavax/net/ssl/HostnameVerifier;
      // 083: dup
      // 084: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 087: aload 2
      // 088: invokevirtual okhttp3/Address.url ()Lokhttp3/HttpUrl;
      // 08b: invokevirtual okhttp3/HttpUrl.host ()Ljava/lang/String;
      // 08e: aload 7
      // 090: invokeinterface javax/net/ssl/HostnameVerifier.verify (Ljava/lang/String;Ljavax/net/ssl/SSLSession;)Z 3
      // 095: ifne 156
      // 098: aload 8
      // 09a: invokevirtual okhttp3/Handshake.peerCertificates ()Ljava/util/List;
      // 09d: astore 9
      // 09f: aload 9
      // 0a1: checkcast java/util/Collection
      // 0a4: invokeinterface java/util/Collection.isEmpty ()Z 1
      // 0a9: ifne 0b0
      // 0ac: bipush 1
      // 0ad: goto 0b1
      // 0b0: bipush 0
      // 0b1: ifeq 12e
      // 0b4: aload 9
      // 0b6: bipush 0
      // 0b7: invokeinterface java/util/List.get (I)Ljava/lang/Object; 2
      // 0bc: dup
      // 0bd: ldc_w "null cannot be cast to non-null type java.security.cert.X509Certificate"
      // 0c0: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;Ljava/lang/String;)V
      // 0c3: checkcast java/security/cert/X509Certificate
      // 0c6: astore 10
      // 0c8: new javax/net/ssl/SSLPeerUnverifiedException
      // 0cb: dup
      // 0cc: new java/lang/StringBuilder
      // 0cf: dup
      // 0d0: invokespecial java/lang/StringBuilder.<init> ()V
      // 0d3: ldc_w "\n              |Hostname "
      // 0d6: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 0d9: aload 2
      // 0da: invokevirtual okhttp3/Address.url ()Lokhttp3/HttpUrl;
      // 0dd: invokevirtual okhttp3/HttpUrl.host ()Ljava/lang/String;
      // 0e0: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 0e3: ldc_w " not verified:\n              |    certificate: "
      // 0e6: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 0e9: getstatic okhttp3/CertificatePinner.Companion Lokhttp3/CertificatePinner$Companion;
      // 0ec: aload 10
      // 0ee: checkcast java/security/cert/Certificate
      // 0f1: invokevirtual okhttp3/CertificatePinner$Companion.pin (Ljava/security/cert/Certificate;)Ljava/lang/String;
      // 0f4: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 0f7: ldc_w "\n              |    DN: "
      // 0fa: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 0fd: aload 10
      // 0ff: invokevirtual java/security/cert/X509Certificate.getSubjectDN ()Ljava/security/Principal;
      // 102: invokeinterface java/security/Principal.getName ()Ljava/lang/String; 1
      // 107: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 10a: ldc_w "\n              |    subjectAltNames: "
      // 10d: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 110: getstatic okhttp3/internal/tls/OkHostnameVerifier.INSTANCE Lokhttp3/internal/tls/OkHostnameVerifier;
      // 113: aload 10
      // 115: invokevirtual okhttp3/internal/tls/OkHostnameVerifier.allSubjectAltNames (Ljava/security/cert/X509Certificate;)Ljava/util/List;
      // 118: invokevirtual java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
      // 11b: ldc_w "\n              "
      // 11e: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 121: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 124: aconst_null
      // 125: bipush 1
      // 126: aconst_null
      // 127: invokestatic kotlin/text/StringsKt.trimMargin$default (Ljava/lang/String;Ljava/lang/String;ILjava/lang/Object;)Ljava/lang/String;
      // 12a: invokespecial javax/net/ssl/SSLPeerUnverifiedException.<init> (Ljava/lang/String;)V
      // 12d: athrow
      // 12e: new javax/net/ssl/SSLPeerUnverifiedException
      // 131: dup
      // 132: new java/lang/StringBuilder
      // 135: dup
      // 136: invokespecial java/lang/StringBuilder.<init> ()V
      // 139: ldc_w "Hostname "
      // 13c: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 13f: aload 2
      // 140: invokevirtual okhttp3/Address.url ()Lokhttp3/HttpUrl;
      // 143: invokevirtual okhttp3/HttpUrl.host ()Ljava/lang/String;
      // 146: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 149: ldc_w " not verified (no certificates)"
      // 14c: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 14f: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 152: invokespecial javax/net/ssl/SSLPeerUnverifiedException.<init> (Ljava/lang/String;)V
      // 155: athrow
      // 156: aload 2
      // 157: invokevirtual okhttp3/Address.certificatePinner ()Lokhttp3/CertificatePinner;
      // 15a: dup
      // 15b: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 15e: astore 9
      // 160: aload 0
      // 161: new okhttp3/Handshake
      // 164: dup
      // 165: aload 8
      // 167: invokevirtual okhttp3/Handshake.tlsVersion ()Lokhttp3/TlsVersion;
      // 16a: aload 8
      // 16c: invokevirtual okhttp3/Handshake.cipherSuite ()Lokhttp3/CipherSuite;
      // 16f: aload 8
      // 171: invokevirtual okhttp3/Handshake.localCertificates ()Ljava/util/List;
      // 174: new okhttp3/internal/connection/RealConnection$connectTls$1
      // 177: dup
      // 178: aload 9
      // 17a: aload 8
      // 17c: aload 2
      // 17d: invokespecial okhttp3/internal/connection/RealConnection$connectTls$1.<init> (Lokhttp3/CertificatePinner;Lokhttp3/Handshake;Lokhttp3/Address;)V
      // 180: checkcast kotlin/jvm/functions/Function0
      // 183: invokespecial okhttp3/Handshake.<init> (Lokhttp3/TlsVersion;Lokhttp3/CipherSuite;Ljava/util/List;Lkotlin/jvm/functions/Function0;)V
      // 186: putfield okhttp3/internal/connection/RealConnection.handshake Lokhttp3/Handshake;
      // 189: aload 9
      // 18b: aload 2
      // 18c: invokevirtual okhttp3/Address.url ()Lokhttp3/HttpUrl;
      // 18f: invokevirtual okhttp3/HttpUrl.host ()Ljava/lang/String;
      // 192: new okhttp3/internal/connection/RealConnection$connectTls$2
      // 195: dup
      // 196: aload 0
      // 197: invokespecial okhttp3/internal/connection/RealConnection$connectTls$2.<init> (Lokhttp3/internal/connection/RealConnection;)V
      // 19a: checkcast kotlin/jvm/functions/Function0
      // 19d: invokevirtual okhttp3/CertificatePinner.check$okhttp (Ljava/lang/String;Lkotlin/jvm/functions/Function0;)V
      // 1a0: aload 6
      // 1a2: invokevirtual okhttp3/ConnectionSpec.supportsTlsExtensions ()Z
      // 1a5: ifeq 1b6
      // 1a8: getstatic okhttp3/internal/platform/Platform.Companion Lokhttp3/internal/platform/Platform$Companion;
      // 1ab: invokevirtual okhttp3/internal/platform/Platform$Companion.get ()Lokhttp3/internal/platform/Platform;
      // 1ae: aload 5
      // 1b0: invokevirtual okhttp3/internal/platform/Platform.getSelectedProtocol (Ljavax/net/ssl/SSLSocket;)Ljava/lang/String;
      // 1b3: goto 1b7
      // 1b6: aconst_null
      // 1b7: astore 10
      // 1b9: aload 0
      // 1ba: aload 5
      // 1bc: checkcast java/net/Socket
      // 1bf: putfield okhttp3/internal/connection/RealConnection.socket Ljava/net/Socket;
      // 1c2: aload 0
      // 1c3: aload 5
      // 1c5: checkcast java/net/Socket
      // 1c8: invokestatic okio/Okio.source (Ljava/net/Socket;)Lokio/Source;
      // 1cb: invokestatic okio/Okio.buffer (Lokio/Source;)Lokio/BufferedSource;
      // 1ce: putfield okhttp3/internal/connection/RealConnection.source Lokio/BufferedSource;
      // 1d1: aload 0
      // 1d2: aload 5
      // 1d4: checkcast java/net/Socket
      // 1d7: invokestatic okio/Okio.sink (Ljava/net/Socket;)Lokio/Sink;
      // 1da: invokestatic okio/Okio.buffer (Lokio/Sink;)Lokio/BufferedSink;
      // 1dd: putfield okhttp3/internal/connection/RealConnection.sink Lokio/BufferedSink;
      // 1e0: aload 0
      // 1e1: aload 10
      // 1e3: dup
      // 1e4: ifnull 1f1
      // 1e7: getstatic okhttp3/Protocol.Companion Lokhttp3/Protocol$Companion;
      // 1ea: swap
      // 1eb: invokevirtual okhttp3/Protocol$Companion.get (Ljava/lang/String;)Lokhttp3/Protocol;
      // 1ee: goto 1f5
      // 1f1: pop
      // 1f2: getstatic okhttp3/Protocol.HTTP_1_1 Lokhttp3/Protocol;
      // 1f5: putfield okhttp3/internal/connection/RealConnection.protocol Lokhttp3/Protocol;
      // 1f8: bipush 1
      // 1f9: istore 4
      // 1fb: getstatic okhttp3/internal/platform/Platform.Companion Lokhttp3/internal/platform/Platform$Companion;
      // 1fe: invokevirtual okhttp3/internal/platform/Platform$Companion.get ()Lokhttp3/internal/platform/Platform;
      // 201: aload 5
      // 203: invokevirtual okhttp3/internal/platform/Platform.afterHandshake (Ljavax/net/ssl/SSLSocket;)V
      // 206: nop
      // 207: goto 230
      // 20a: astore 6
      // 20c: aload 5
      // 20e: ifnull 21c
      // 211: getstatic okhttp3/internal/platform/Platform.Companion Lokhttp3/internal/platform/Platform$Companion;
      // 214: invokevirtual okhttp3/internal/platform/Platform$Companion.get ()Lokhttp3/internal/platform/Platform;
      // 217: aload 5
      // 219: invokevirtual okhttp3/internal/platform/Platform.afterHandshake (Ljavax/net/ssl/SSLSocket;)V
      // 21c: nop
      // 21d: aload 5
      // 21f: dup
      // 220: ifnull 22c
      // 223: checkcast java/net/Socket
      // 226: invokestatic okhttp3/internal/Util.closeQuietly (Ljava/net/Socket;)V
      // 229: goto 22d
      // 22c: pop
      // 22d: aload 6
      // 22f: athrow
      // 230: return
   }

   @Throws(java/io/IOException::class)
   private fun createTunnel(readTimeout: Int, writeTimeout: Int, tunnelRequest: Request, url: HttpUrl): Request? {
      var nextRequest: Request = tunnelRequest;
      var requestLine: java.lang.String = "CONNECT " + Util.toHostHeader(url, true) + " HTTP/1.1";

      var var13: Request;
      label24:
      while (true) {
         var var10000: BufferedSource = this.source;
         var var11: BufferedSink = this.sink;
         var tunnelCodec: Http1ExchangeCodec = new Http1ExchangeCodec(null, this, var10000, var11);
         var10000.timeout().timeout((long)readTimeout, TimeUnit.MILLISECONDS);
         var11.timeout().timeout((long)writeTimeout, TimeUnit.MILLISECONDS);
         tunnelCodec.writeRequest(nextRequest.headers(), requestLine);
         tunnelCodec.finishRequest();
         var var12: Response.Builder = tunnelCodec.readResponseHeaders(false);
         var response: Response = var12.request(nextRequest).build();
         tunnelCodec.skipConnectBody(response);
         switch (response.code()) {
            case 200:
               if (var10000.getBuffer().exhausted() && var11.getBuffer().exhausted()) {
                  return null;
               }

               throw new IOException("TLS tunnel buffered too many bytes!");
            case 407:
               var13 = this.route.address().proxyAuthenticator().authenticate(this.route, response);
               if (var13 == null) {
                  throw new IOException("Failed to authenticate with proxy");
               }

               nextRequest = var13;
               if (StringsKt.equals("close", Response.header$default(response, "Connection", null, 2, null), true)) {
                  break label24;
               }
               break;
            default:
               throw new IOException("Unexpected response code for CONNECT: " + response.code());
         }
      }

      return var13;
   }

   @Throws(java/io/IOException::class)
   private fun createTunnelRequest(): Request {
      var proxyConnectRequest: Request = new Request.Builder()
         .url(this.route.address().url())
         .method("CONNECT", null)
         .header("Host", Util.toHostHeader(this.route.address().url(), true))
         .header("Proxy-Connection", "Keep-Alive")
         .header("User-Agent", "okhttp/4.12.0")
         .build();
      var authenticatedRequest: Request = this.route
         .address()
         .proxyAuthenticator()
         .authenticate(
            this.route,
            new Response.Builder()
               .request(proxyConnectRequest)
               .protocol(Protocol.HTTP_1_1)
               .code(407)
               .message("Preemptive Authenticate")
               .body(Util.EMPTY_RESPONSE)
               .sentRequestAtMillis(-1L)
               .receivedResponseAtMillis(-1L)
               .header("Proxy-Authenticate", "OkHttp-Preemptive")
               .build()
         );
      var var10000: Request = authenticatedRequest;
      if (authenticatedRequest == null) {
         var10000 = proxyConnectRequest;
      }

      return var10000;
   }

   internal fun isEligible(address: Address, routes: List<Route>?): Boolean {
      if (Util.assertionsEnabled && !Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
      } else if (this.calls.size() < this.allocationLimit && !this.noNewExchanges) {
         if (!this.route.address().equalsNonHost$okhttp(address)) {
            return false;
         } else if (address.url().host() == this.route().address().url().host()) {
            return true;
         } else if (this.http2Connection == null) {
            return false;
         } else if (routes != null && this.routeMatchesAny(routes)) {
            if (address.hostnameVerifier() != OkHostnameVerifier.INSTANCE) {
               return false;
            } else if (!this.supportsUrl(address.url())) {
               return false;
            } else {
               try {
                  var var10000: CertificatePinner = address.certificatePinner();
                  var var10001: java.lang.String = address.url().host();
                  var var10002: Handshake = this.handshake();
                  var10000.check(var10001, var10002.peerCertificates());
                  return true;
               } catch (SSLPeerUnverifiedException var5) {
                  return false;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private fun routeMatchesAny(candidates: List<Route>): Boolean {
      var `$this$any$iv`: java.lang.Iterable = candidates;
      var var10000: Boolean;
      if (candidates is java.util.Collection && (candidates as java.util.Collection).isEmpty()) {
         var10000 = false;
      } else {
         var var4: java.util.Iterator = `$this$any$iv`.iterator();

         while (true) {
            if (!var4.hasNext()) {
               var10000 = false;
               break;
            }

            var it: Route = var4.next() as Route;
            if (it.proxy().type() === Type.DIRECT && this.route.proxy().type() === Type.DIRECT && this.route.socketAddress() == it.socketAddress()) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   private fun supportsUrl(url: HttpUrl): Boolean {
      if (Util.assertionsEnabled && !Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + this);
      } else {
         var routeUrl: HttpUrl = this.route.address().url();
         if (url.port() != routeUrl.port()) {
            return false;
         } else if (url.host() == routeUrl.host()) {
            return true;
         } else {
            if (!this.noCoalescedConnections && this.handshake != null) {
               var var10002: Handshake = this.handshake;
               if (this.certificateSupportHost(url, var10002)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private fun certificateSupportHost(url: HttpUrl, handshake: Handshake): Boolean {
      var peerCertificates: java.util.List = handshake.peerCertificates();
      if (!peerCertificates.isEmpty()) {
         var var10000: OkHostnameVerifier = OkHostnameVerifier.INSTANCE;
         var var10001: java.lang.String = url.host();
         var var10002: Any = peerCertificates.get(0);
         if (var10000.verify(var10001, var10002 as X509Certificate)) {
            return true;
         }
      }

      return false;
   }

   @Throws(java/net/SocketException::class)
   internal fun newCodec(client: OkHttpClient, chain: RealInterceptorChain): ExchangeCodec {
      var var10000: Socket = this.socket;
      var var7: BufferedSource = this.source;
      var var8: BufferedSink = this.sink;
      var var9: ExchangeCodec;
      if (this.http2Connection != null) {
         var9 = new Http2ExchangeCodec(client, this, chain, this.http2Connection);
      } else {
         var10000.setSoTimeout(chain.readTimeoutMillis());
         var7.timeout().timeout((long)chain.getReadTimeoutMillis$okhttp(), TimeUnit.MILLISECONDS);
         var8.timeout().timeout((long)chain.getWriteTimeoutMillis$okhttp(), TimeUnit.MILLISECONDS);
         var9 = new Http1ExchangeCodec(client, this, var7, var8);
      }

      return var9;
   }

   @Throws(java/net/SocketException::class)
   internal fun newWebSocketStreams(exchange: Exchange): Streams {
      var var10000: Socket = this.socket;
      var var5: BufferedSource = this.source;
      var var6: BufferedSink = this.sink;
      var sink: BufferedSink = var6;
      var10000.setSoTimeout(0);
      this.noNewExchanges$okhttp();
      return new RealWebSocket.Streams(var5, sink, exchange) {
         {
            super(true, `$source`, `$sink`);
            this.$exchange = `$exchange`;
         }

         @Override
         public void close() {
            this.$exchange.bodyComplete(-1L, true, true, null);
         }
      };
   }

   public override fun route(): Route {
      return this.route;
   }

   public fun cancel() {
      if (this.rawSocket != null) {
         Util.closeQuietly(this.rawSocket);
      }
   }

   public override fun socket(): Socket {
      var var10000: Socket = this.socket;
      return var10000;
   }

   public fun isHealthy(doExtensiveChecks: Boolean): Boolean {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         var nowNs: Long = System.nanoTime();
         var var10000: Socket = this.rawSocket;
         var10000 = this.socket;
         var var16: BufferedSource = this.source;
         if (!var10000.isClosed() && !var10000.isClosed() && !var10000.isInputShutdown() && !var10000.isOutputShutdown()) {
            if (this.http2Connection != null) {
               return this.http2Connection.isHealthy(nowNs);
            } else {
               var var17: Long;
               synchronized (this) {
                  var17 = nowNs - this.idleAtNs;
               }

               return var17 < 10000000000L || !doExtensiveChecks || Util.isHealthy(var10000, var16);
            }
         } else {
            return false;
         }
      }
   }

   @Throws(java/io/IOException::class)
   public override fun onStream(stream: Http2Stream) {
      stream.close(ErrorCode.REFUSED_STREAM, null);
   }

   @Synchronized
   public override fun onSettings(connection: Http2Connection, settings: Settings) {
      this.allocationLimit = settings.getMaxConcurrentStreams();
   }

   public override fun handshake(): Handshake? {
      return this.handshake;
   }

   internal fun connectFailed(client: OkHttpClient, failedRoute: Route, failure: IOException) {
      if (failedRoute.proxy().type() != Type.DIRECT) {
         var address: Address = failedRoute.address();
         address.proxySelector().connectFailed(address.url().uri(), failedRoute.proxy().address(), failure);
      }

      client.getRouteDatabase().failed(failedRoute);
   }

   @Synchronized
   internal fun trackFailure(call: RealCall, e: IOException?) {
      if (e is StreamResetException) {
         if ((e as StreamResetException).errorCode === ErrorCode.REFUSED_STREAM) {
            var var3: Int = this.refusedStreamCount++;
            if (this.refusedStreamCount > 1) {
               this.noNewExchanges = true;
               var3 = this.routeFailureCount++;
            }
         } else if ((e as StreamResetException).errorCode != ErrorCode.CANCEL || !call.isCanceled()) {
            this.noNewExchanges = true;
            var var5: Int = this.routeFailureCount++;
         }
      } else if (!this.isMultiplexed$okhttp() || e is ConnectionShutdownException) {
         this.noNewExchanges = true;
         if (this.successCount == 0) {
            if (e != null) {
               this.connectFailed$okhttp(call.getClient(), this.route, e);
            }

            var var6: Int = this.routeFailureCount++;
         }
      }
   }

   public override fun protocol(): Protocol {
      var var10000: Protocol = this.protocol;
      return var10000;
   }

   public override fun toString(): String {
      var var10000: StringBuilder = new StringBuilder()
         .append("Connection{")
         .append(this.route.address().url().host())
         .append(':')
         .append(this.route.address().url().port())
         .append(", proxy=")
         .append(this.route.proxy())
         .append(" hostAddress=")
         .append(this.route.socketAddress())
         .append(" cipherSuite=");
      if (this.handshake != null) {
         var var10001: CipherSuite = this.handshake.cipherSuite();
         if (var10001 != null) {
            return var10000.append(var10001).append(" protocol=").append(this.protocol).append('}').toString();
         }
      }

      return var10000.append("none").append(" protocol=").append(this.protocol).append('}').toString();
   }

   companion object Companion private constructor() {
      internal const val IDLE_CONNECTION_HEALTHY_NS: Long
      private const val MAX_TUNNEL_ATTEMPTS: Int
      private const val NPE_THROW_WITH_NULL: String


      public fun newTestConnection(connectionPool: RealConnectionPool, route: Route, socket: Socket, idleAtNs: Long): RealConnection {
         var result: RealConnection = new RealConnection(connectionPool, route);
         RealConnection.access$setSocket$p(result, socket);
         result.setIdleAtNs$okhttp(idleAtNs);
         return result;
      }
   }
}
