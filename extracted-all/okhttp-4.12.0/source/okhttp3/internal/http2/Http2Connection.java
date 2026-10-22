package okhttp3.internal.http2

import java.io.Closeable
import java.io.IOException
import java.io.InterruptedIOException
import java.net.Socket
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.concurrent.TimeUnit
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import kotlin.jvm.internal.Ref.ObjectRef
import okhttp3.internal.Util
import okhttp3.internal.concurrent.Task
import okhttp3.internal.concurrent.TaskQueue
import okhttp3.internal.concurrent.TaskRunner
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nHttp2Connection.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection\n+ 2 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue\n+ 3 Util.kt\nokhttp3/internal/Util\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 6 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,1006:1\n84#2,4:1007\n90#2,13:1014\n90#2,13:1027\n90#2,13:1069\n90#2,13:1082\n90#2,13:1095\n90#2,13:1108\n90#2,13:1121\n90#2,13:1134\n563#3:1011\n557#3:1013\n557#3:1040\n615#3,4:1041\n402#3,5:1045\n402#3,5:1053\n402#3,5:1059\n402#3,5:1064\n1#4:1012\n37#5,2:1050\n13309#6:1052\n13310#6:1058\n*S KotlinDebug\n*F\n+ 1 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection\n*L\n152#1:1007,4\n340#1:1014,13\n361#1:1027,13\n506#1:1069,13\n554#1:1082,13\n893#1:1095,13\n911#1:1108,13\n938#1:1121,13\n952#1:1134,13\n183#1:1011\n319#1:1013\n402#1:1040\n446#1:1041,4\n448#1:1045,5\n461#1:1053,5\n467#1:1059,5\n472#1:1064,5\n455#1:1050,2\n460#1:1052\n460#1:1058\n*E\n"])
class Http2Connection internal constructor(builder: okhttp3.internal.http2.Http2Connection.Builder) : Closeable {
   @JvmStatic
   public Http2Connection.Companion Companion = new Http2Connection.Companion(null);
   @JvmStatic
   public int OKHTTP_CLIENT_WINDOW_SIZE = 16777216;
   @JvmStatic
   private Settings DEFAULT_SETTINGS;
   @JvmStatic
   public int INTERVAL_PING = 1;
   @JvmStatic
   public int DEGRADED_PING = 2;
   @JvmStatic
   public int AWAIT_PING = 3;
   @JvmStatic
   public int DEGRADED_PONG_TIMEOUT_NS = 1000000000;

   private final var awaitPingsSent: Long
   private final var awaitPongsReceived: Long
   internal final val client: Boolean
   internal final val connectionName: String
   private final val currentPushRequests: MutableSet<Int>
   private final var degradedPingsSent: Long
   private final var degradedPongDeadlineNs: Long
   private final var degradedPongsReceived: Long
   private final var intervalPingsSent: Long
   private final var intervalPongsReceived: Long
   private final var isShutdown: Boolean
   internal final var lastGoodStreamId: Int
   internal final val listener: okhttp3.internal.http2.Http2Connection.Listener
   internal final var nextStreamId: Int
   public final val okHttpSettings: Settings
   public final var peerSettings: Settings
      internal set
   private final val pushObserver: PushObserver
   private final val pushQueue: TaskQueue
   public final var readBytesAcknowledged: Long
      private set
   public final var readBytesTotal: Long
      private set
   public final val readerRunnable: okhttp3.internal.http2.Http2Connection.ReaderRunnable
   private final val settingsListenerQueue: TaskQueue
   internal final val socket: Socket
   internal final val streams: MutableMap<Int, Http2Stream>
   private final val taskRunner: TaskRunner
   public final var writeBytesMaximum: Long
      private set
   public final var writeBytesTotal: Long
      private set
   public final val writer: Http2Writer
   private final val writerQueue: TaskQueue

   init {
      this.client = builder.getClient$okhttp();
      this.listener = builder.getListener$okhttp();
      this.streams = new LinkedHashMap<>();
      this.connectionName = builder.getConnectionName$okhttp();
      this.nextStreamId = if (builder.getClient$okhttp()) 3 else 2;
      this.taskRunner = builder.getTaskRunner$okhttp();
      this.writerQueue = this.taskRunner.newQueue();
      this.pushQueue = this.taskRunner.newQueue();
      this.settingsListenerQueue = this.taskRunner.newQueue();
      this.pushObserver = builder.getPushObserver$okhttp();
      var pingIntervalNanos: Settings = new Settings();
      if (builder.getClient$okhttp()) {
         pingIntervalNanos.set(7, 16777216);
      }

      this.okHttpSettings = pingIntervalNanos;
      this.peerSettings = DEFAULT_SETTINGS;
      this.writeBytesMaximum = (long)this.peerSettings.getInitialWindowSize();
      this.socket = builder.getSocket$okhttp();
      this.writer = new Http2Writer(builder.getSink$okhttp(), this.client);
      this.readerRunnable = new Http2Connection.ReaderRunnable(this, new Http2Reader(builder.getSource$okhttp(), this.client));
      this.currentPushRequests = new LinkedHashSet<>();
      if (builder.getPingIntervalMillis$okhttp() != 0) {
         var var8: Long = TimeUnit.MILLISECONDS.toNanos((long)builder.getPingIntervalMillis$okhttp());
         this.writerQueue.schedule(new Http2Connection$special$$inlined$schedule$1(this.connectionName + " ping", this, var8), var8);
      }
   }

   @Synchronized
   public fun openStreamCount(): Int {
      return this.streams.size();
   }

   @Synchronized
   public fun getStream(id: Int): Http2Stream? {
      return this.streams.get(id);
   }

   @Synchronized
   internal fun removeStream(streamId: Int): Http2Stream? {
      var stream: Http2Stream = this.streams.remove(streamId);
      this.notifyAll();
      return stream;
   }

   @Synchronized
   internal fun updateConnectionFlowControl(read: Long) {
      this.readBytesTotal += read;
      var readBytesToAcknowledge: Long = this.readBytesTotal - this.readBytesAcknowledged;
      if (this.readBytesTotal - this.readBytesAcknowledged >= (long)(this.okHttpSettings.getInitialWindowSize() / 2)) {
         this.writeWindowUpdateLater$okhttp(0, readBytesToAcknowledge);
         this.readBytesAcknowledged += readBytesToAcknowledge;
      }
   }

   @Throws(java/io/IOException::class)
   public fun pushStream(associatedStreamId: Int, requestHeaders: List<Header>, out: Boolean): Http2Stream {
      if (this.client) {
         throw new IllegalStateException("Client cannot push requests.".toString());
      } else {
         return this.newStream(associatedStreamId, requestHeaders, out);
      }
   }

   @Throws(java/io/IOException::class)
   public fun newStream(requestHeaders: List<Header>, out: Boolean): Http2Stream {
      return this.newStream(0, requestHeaders, out);
   }

   @Throws(java/io/IOException::class)
   private fun newStream(associatedStreamId: Int, requestHeaders: List<Header>, out: Boolean): Http2Stream {
      var outFinished: Boolean = !out;
      var inFinished: Boolean = false;
      var var9: Http2Writer = this.writer;
      var var15: Boolean;
      var var16: Http2Stream;
      synchronized (this.writer) {
         var var17: Int;
         synchronized (this) {
            if (this.nextStreamId > 1073741823) {
               this.shutdown(ErrorCode.REFUSED_STREAM);
            }

            if (this.isShutdown) {
               throw new ConnectionShutdownException();
            }

            var17 = this.nextStreamId;
            this.nextStreamId += 2;
            var16 = new Http2Stream(var17, this, outFinished, inFinished, null);
            var15 = !out || this.writeBytesTotal >= this.writeBytesMaximum || var16.getWriteBytesTotal() >= var16.getWriteBytesMaximum();
            if (var16.isOpen()) {
               this.streams.put(var17, var16);
            }
         }

         if (associatedStreamId == 0) {
            this.writer.headers(outFinished, var17, requestHeaders);
         } else {
            if (this.client) {
               throw new IllegalArgumentException("client streams shouldn't have associated stream IDs".toString());
            }

            this.writer.pushPromise(associatedStreamId, var17, requestHeaders);
         }
      }

      // $VF: monitorexit
      if (var15) {
         this.writer.flush();
      }

      return var16;
   }

   @Throws(java/io/IOException::class)
   internal fun writeHeaders(streamId: Int, outFinished: Boolean, alternating: List<Header>) {
      this.writer.headers(outFinished, streamId, alternating);
   }

   @Throws(java/io/IOException::class)
   public fun writeData(streamId: Int, outFinished: Boolean, buffer: Buffer?, byteCount: Long) {
      if (byteCount == 0L) {
         this.writer.data(outFinished, streamId, buffer, 0);
      } else {
         var var18: Long = byteCount;

         while (var18 > 0L) {
            var var16: Int;
            synchronized (this) {
               try {
                  while (this.writeBytesTotal >= this.writeBytesMaximum) {
                     if (!this.streams.containsKey(streamId)) {
                        throw new IOException("stream closed");
                     }

                     this.wait();
                  }
               } catch (InterruptedException var13) {
                  Thread.currentThread().interrupt();
                  throw new InterruptedIOException();
               }

               var16 = Math.min((int)Math.min(var18, this.writeBytesMaximum - this.writeBytesTotal), this.writer.maxDataLength());
               this.writeBytesTotal += (long)var16;
            }

            // $VF: monitorexit
            var18 -= (long)var16;
            this.writer.data(outFinished && var18 == 0L, streamId, buffer, var16);
         }
      }
   }

   internal fun writeSynResetLater(streamId: Int, errorCode: ErrorCode) {
      this.writerQueue
         .schedule(
            new Http2Connection$writeSynResetLater$$inlined$execute$default$1(
               this.connectionName + '[' + streamId + "] writeSynReset", true, this, streamId, errorCode
            ),
            0L
         );
   }

   @Throws(java/io/IOException::class)
   internal fun writeSynReset(streamId: Int, statusCode: ErrorCode) {
      this.writer.rstStream(streamId, statusCode);
   }

   internal fun writeWindowUpdateLater(streamId: Int, unacknowledgedBytesRead: Long) {
      this.writerQueue
         .schedule(
            new Http2Connection$writeWindowUpdateLater$$inlined$execute$default$1(
               this.connectionName + '[' + streamId + "] windowUpdate", true, this, streamId, unacknowledgedBytesRead
            ),
            0L
         );
   }

   public fun writePing(reply: Boolean, payload1: Int, payload2: Int) {
      try {
         this.writer.ping(reply, payload1, payload2);
      } catch (IOException var5) {
         this.failConnection(var5);
      }
   }

   @Throws(java/lang/InterruptedException::class)
   public fun writePingAndAwaitPong() {
      this.writePing();
      this.awaitPong();
   }

   @Throws(java/lang/InterruptedException::class)
   public fun writePing() {
      synchronized (this) {
         var var4: Int = this.awaitPingsSent++;
      }

      this.writePing(false, 3, 1330343787);
   }

   @Synchronized
   @Throws(java/lang/InterruptedException::class)
   public fun awaitPong() {
      while (this.awaitPongsReceived < this.awaitPingsSent) {
         this.wait();
      }
   }

   @Throws(java/io/IOException::class)
   public fun flush() {
      this.writer.flush();
   }

   @Throws(java/io/IOException::class)
   public fun shutdown(statusCode: ErrorCode) {
      label39: {
         var var2: Http2Writer;
         label36: {
            var2 = this.writer;
            synchronized (this.writer) {
               var lastGoodStreamId: IntRef = new IntRef();
               synchronized (this){} // $VF: monitorenter 

               label31:
               try {
                  if (!this.isShutdown) {
                     this.isShutdown = true;
                     lastGoodStreamId.element = this.lastGoodStreamId;
                     break label31;
                  }
                  break label36;
               } catch (java.lang.Throwable var7) {
                  ;
               }
            }

            // $VF: monitorexit
            return;
         }

         // $VF: monitorexit
      }
   }

   public override fun close() {
      this.close$okhttp(ErrorCode.NO_ERROR, ErrorCode.CANCEL, null);
   }

   internal fun close(connectionCode: ErrorCode, streamCode: ErrorCode, cause: IOException?) {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         try {
            this.shutdown(connectionCode);
         } catch (IOException var19) {
         }

         var var21: Any = null;
         synchronized (this) {
            if (!this.streams.isEmpty()) {
               var21 = this.streams.values().toArray(new Http2Stream[0]);
               this.streams.clear();
            }
         }

         if (var21 as Http2Stream[] != null) {
            var var26: Any;
            for (Object element$iv : var26) {
               var stream: Http2Stream = (Http2Stream)`element$iv`;

               try {
                  stream.close(streamCode, cause);
               } catch (IOException var18) {
               }
            }
         }

         try {
            this.writer.close();
         } catch (IOException var17) {
         }

         try {
            this.socket.close();
         } catch (IOException var16) {
         }

         this.writerQueue.shutdown();
         this.pushQueue.shutdown();
         this.settingsListenerQueue.shutdown();
      }
   }

   private fun failConnection(e: IOException?) {
      this.close$okhttp(ErrorCode.PROTOCOL_ERROR, ErrorCode.PROTOCOL_ERROR, e);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   public fun start(sendConnectionPreface: Boolean, taskRunner: TaskRunner) {
      if (sendConnectionPreface) {
         this.writer.connectionPreface();
         this.writer.settings(this.okHttpSettings);
         var `$this$iv`: Int = this.okHttpSettings.getInitialWindowSize();
         if (`$this$iv` != 65535) {
            this.writer.windowUpdate(0, (long)(`$this$iv` - 65535));
         }
      }

      var var10: TaskQueue = taskRunner.newQueue();
      var `block$iv`: Function0 = this.readerRunnable;
      var `cancelable$iv`: Boolean = true;
      var10.schedule(new Task(this.connectionName, `cancelable$iv`, `block$iv`) {
         {
            super(`$name`, `$cancelable`);
            this.$block = `$block`;
         }

         @Override
         public long runOnce() {
            this.$block.invoke();
            return -1L;
         }
      }, 0L);
   }

   @Throws(java/io/IOException::class)
   public fun setSettings(settings: Settings) {
      var var2: Http2Writer = this.writer;
      synchronized (this.writer) {
         synchronized (this) {
            if (this.isShutdown) {
               throw new ConnectionShutdownException();
            }

            this.okHttpSettings.merge(settings);
         }

         this.writer.settings(settings);
      }

      // $VF: monitorexit
   }

   @Synchronized
   public fun isHealthy(nowNs: Long): Boolean {
      if (this.isShutdown) {
         return false;
      } else {
         return this.degradedPongsReceived >= this.degradedPingsSent || nowNs < this.degradedPongDeadlineNs;
      }
   }

   internal fun sendDegradedPingLater() {
      label23: {
         synchronized (this){} // $VF: monitorenter 

         label20: {
            try {
               if (this.degradedPongsReceived < this.degradedPingsSent) {
                  break label20;
               }

               var `delayNanos$iv`: Int = this.degradedPingsSent++;
               this.degradedPongDeadlineNs = System.nanoTime() + (long)1000000000;
            } catch (java.lang.Throwable var7) {
               // $VF: monitorexit
            }

            // $VF: monitorexit
         }

         // $VF: monitorexit
      }
   }

   internal fun pushedStream(streamId: Int): Boolean {
      return streamId != 0 && (streamId and 1) == 0;
   }

   internal fun pushRequestLater(streamId: Int, requestHeaders: List<Header>) {
      label26: {
         synchronized (this){} // $VF: monitorenter 

         label23: {
            try {
               if (this.currentPushRequests.contains(streamId)) {
                  this.writeSynResetLater$okhttp(streamId, ErrorCode.PROTOCOL_ERROR);
                  break label23;
               }

               var var11: Boolean = this.currentPushRequests.add(streamId);
            } catch (java.lang.Throwable var9) {
               // $VF: monitorexit
            }

            // $VF: monitorexit
         }

         // $VF: monitorexit
      }
   }

   internal fun pushHeadersLater(streamId: Int, requestHeaders: List<Header>, inFinished: Boolean) {
      this.pushQueue
         .schedule(
            new Http2Connection$pushHeadersLater$$inlined$execute$default$1(
               this.connectionName + '[' + streamId + "] onHeaders", true, this, streamId, requestHeaders, inFinished
            ),
            0L
         );
   }

   @Throws(java/io/IOException::class)
   internal fun pushDataLater(streamId: Int, source: BufferedSource, byteCount: Int, inFinished: Boolean) {
      var buffer: Buffer = new Buffer();
      source.require((long)byteCount);
      source.read(buffer, (long)byteCount);
      this.pushQueue
         .schedule(
            new Http2Connection$pushDataLater$$inlined$execute$default$1(
               this.connectionName + '[' + streamId + "] onData", true, this, streamId, buffer, byteCount, inFinished
            ),
            0L
         );
   }

   internal fun pushResetLater(streamId: Int, errorCode: ErrorCode) {
      this.pushQueue
         .schedule(
            new Http2Connection$pushResetLater$$inlined$execute$default$1(this.connectionName + '[' + streamId + "] onReset", true, this, streamId, errorCode),
            0L
         );
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun start(sendConnectionPreface: Boolean) {
      start$default(this, sendConnectionPreface, null, 2, null);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun start() {
      start$default(this, false, null, 3, null);
   }

   @JvmStatic
   fun {
      var var0: Settings = new Settings();
      var0.set(7, 65535);
      var0.set(5, 16384);
      DEFAULT_SETTINGS = var0;
   }

   class Builder(client: Boolean, taskRunner: TaskRunner) {
      internal final var client: Boolean
      internal final lateinit var connectionName: String
      internal final var listener: okhttp3.internal.http2.Http2Connection.Listener
      internal final var pingIntervalMillis: Int
      internal final var pushObserver: PushObserver
      internal final lateinit var sink: BufferedSink
      internal final lateinit var socket: Socket
      internal final lateinit var source: BufferedSource
      internal final val taskRunner: TaskRunner

      init {
         this.client = client;
         this.taskRunner = taskRunner;
         this.listener = Http2Connection.Listener.REFUSE_INCOMING_STREAMS;
         this.pushObserver = PushObserver.CANCEL;
      }

      @JvmOverloads
      @Throws(java/io/IOException::class)
      public fun socket(socket: Socket, peerName: String, source: BufferedSource, sink: BufferedSink): okhttp3.internal.http2.Http2Connection.Builder {
         var `$this$socket_u24lambda_u240`: Http2Connection.Builder = this;
         this.setSocket$okhttp(socket);
         `$this$socket_u24lambda_u240`.setConnectionName$okhttp(
            if (`$this$socket_u24lambda_u240`.client) Util.okHttpName + ' ' + peerName else "MockWebServer " + peerName
         );
         `$this$socket_u24lambda_u240`.setSource$okhttp(source);
         `$this$socket_u24lambda_u240`.setSink$okhttp(sink);
         return this;
      }

      public fun listener(listener: okhttp3.internal.http2.Http2Connection.Listener): okhttp3.internal.http2.Http2Connection.Builder {
         this.listener = listener;
         return this;
      }

      public fun pushObserver(pushObserver: PushObserver): okhttp3.internal.http2.Http2Connection.Builder {
         this.pushObserver = pushObserver;
         return this;
      }

      public fun pingIntervalMillis(pingIntervalMillis: Int): okhttp3.internal.http2.Http2Connection.Builder {
         this.pingIntervalMillis = pingIntervalMillis;
         return this;
      }

      public fun build(): Http2Connection {
         return new Http2Connection(this);
      }

      @JvmOverloads
      @Throws(java/io/IOException::class)
      fun socket(socket: Socket, peerName: java.lang.String, source: BufferedSource): Http2Connection.Builder {
         return socket$default(this, socket, peerName, source, null, 8, null);
      }

      @JvmOverloads
      @Throws(java/io/IOException::class)
      fun socket(socket: Socket, peerName: java.lang.String): Http2Connection.Builder {
         return socket$default(this, socket, peerName, null, null, 12, null);
      }

      @JvmOverloads
      @Throws(java/io/IOException::class)
      fun socket(socket: Socket): Http2Connection.Builder {
         return socket$default(this, socket, null, null, null, 14, null);
      }
   }

   companion object Companion private constructor() {
      public const val AWAIT_PING: Int
      public final val DEFAULT_SETTINGS: Settings
      public const val DEGRADED_PING: Int
      public const val DEGRADED_PONG_TIMEOUT_NS: Int
      public const val INTERVAL_PING: Int
      public const val OKHTTP_CLIENT_WINDOW_SIZE: Int

   }

   abstract class Listener {
      @JvmStatic
      public Http2Connection.Listener.Companion Companion = new Http2Connection.Listener.Companion(null);
      @JvmField
      @JvmStatic
      public Http2Connection.Listener REFUSE_INCOMING_STREAMS = new Http2Connection.Listener() {
         @Override
         public void onStream(@NotNull Http2Stream stream) throws IOException {
            stream.close(ErrorCode.REFUSED_STREAM, null);
         }
      };

      @Throws(java/io/IOException::class)
      public abstract fun onStream(stream: Http2Stream) {
      }

      public open fun onSettings(connection: Http2Connection, settings: Settings) {
      }

      companion object Companion private constructor() {
         public final val REFUSE_INCOMING_STREAMS: okhttp3.internal.http2.Http2Connection.Listener

      }
   }

   @SourceDebugExtension(["SMAP\nHttp2Connection.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection$ReaderRunnable\n+ 2 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 Util.kt\nokhttp3/internal/Util\n*L\n1#1,1006:1\n90#2,13:1007\n90#2,13:1020\n90#2,13:1035\n90#2,13:1049\n37#3,2:1033\n37#3,2:1062\n563#4:1048\n563#4:1064\n*S KotlinDebug\n*F\n+ 1 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection$ReaderRunnable\n*L\n687#1:1007,13\n715#1:1020,13\n758#1:1035,13\n806#1:1049,13\n753#1:1033,2\n824#1:1062,2\n797#1:1048\n841#1:1064\n*E\n"])
   inner class ReaderRunnable internal constructor(reader: Http2Reader) : Http2Reader.Handler, Function0<Unit> {
      internal final val reader: Http2Reader

      init {
         this.this$0 = `this$0`;
         this.reader = reader;
      }

      public open operator fun invoke() {
         label52: {
            var connectionErrorCode: ErrorCode = ErrorCode.INTERNAL_ERROR;
            var streamErrorCode: ErrorCode = ErrorCode.INTERNAL_ERROR;
            var errorException: IOException = null;

            label53: {
               try {
                  try {
                     this.reader.readConnectionPreface(this);

                     while (this.reader.nextFrame(false, this)) {
                     }

                     connectionErrorCode = ErrorCode.NO_ERROR;
                     streamErrorCode = ErrorCode.CANCEL;
                     break label53;
                  } catch (IOException var5) {
                     errorException = var5;
                     connectionErrorCode = ErrorCode.PROTOCOL_ERROR;
                     streamErrorCode = ErrorCode.PROTOCOL_ERROR;
                  }
               } catch (java.lang.Throwable var6) {
                  this.this$0.close$okhttp(connectionErrorCode, streamErrorCode, errorException);
                  Util.closeQuietly(this.reader);
               }

               this.this$0.close$okhttp(connectionErrorCode, streamErrorCode, errorException);
               Util.closeQuietly(this.reader);
               return;
            }

            this.this$0.close$okhttp(connectionErrorCode, streamErrorCode, null);
            Util.closeQuietly(this.reader);
         }
      }

      @Throws(java/io/IOException::class)
      public override fun data(inFinished: Boolean, streamId: Int, source: BufferedSource, length: Int) {
         if (this.this$0.pushedStream$okhttp(streamId)) {
            this.this$0.pushDataLater$okhttp(streamId, source, length, inFinished);
         } else {
            var dataStream: Http2Stream = this.this$0.getStream(streamId);
            if (dataStream == null) {
               this.this$0.writeSynResetLater$okhttp(streamId, ErrorCode.PROTOCOL_ERROR);
               this.this$0.updateConnectionFlowControl$okhttp((long)length);
               source.skip((long)length);
            } else {
               dataStream.receiveData(source, length);
               if (inFinished) {
                  dataStream.receiveHeaders(Util.EMPTY_HEADERS, true);
               }
            }
         }
      }

      public override fun headers(inFinished: Boolean, streamId: Int, associatedStreamId: Int, headerBlock: List<Header>) {
         if (this.this$0.pushedStream$okhttp(streamId)) {
            this.this$0.pushHeadersLater$okhttp(streamId, headerBlock, inFinished);
         } else {
            label81: {
               var var6: Http2Connection = this.this$0;
               var var7: Http2Connection = this.this$0;
               synchronized (this.this$0){} // $VF: monitorenter 

               label46: {
                  label45: {
                     label44: {
                        label43: {
                           try {
                              if (var7.getStream(streamId) == null) {
                                 if (Http2Connection.access$isShutdown$p(var7)) {
                                    break label46;
                                 }

                                 if (streamId <= var7.getLastGoodStreamId$okhttp()) {
                                    break label45;
                                 }

                                 if (streamId % 2 == var7.getNextStreamId$okhttp() % 2) {
                                    break label44;
                                 }

                                 var newStream: Http2Stream = new Http2Stream(streamId, var7, false, inFinished, Util.toHeaders(headerBlock));
                                 var7.setLastGoodStreamId$okhttp(streamId);
                                 var7.getStreams$okhttp().put(streamId, newStream);
                                 Http2Connection.access$getTaskRunner$p(var7)
                                    .newQueue()
                                    .schedule(
                                       new Http2Connection$ReaderRunnable$headers$lambda$2$$inlined$execute$default$1(
                                          var7.getConnectionName$okhttp() + '[' + streamId + "] onStream", true, var7, newStream
                                       ),
                                       0L
                                    );
                                 break label43;
                              }
                           } catch (java.lang.Throwable var18) {
                              // $VF: monitorexit
                           }

                           // $VF: monitorexit
                        }

                        // $VF: monitorexit
                     }

                     // $VF: monitorexit
                  }

                  // $VF: monitorexit
               }

               // $VF: monitorexit
            }
         }
      }

      public override fun rstStream(streamId: Int, errorCode: ErrorCode) {
         if (this.this$0.pushedStream$okhttp(streamId)) {
            this.this$0.pushResetLater$okhttp(streamId, errorCode);
         } else {
            var rstStream: Http2Stream = this.this$0.removeStream$okhttp(streamId);
            if (rstStream != null) {
               rstStream.receiveRstStream(errorCode);
            }
         }
      }

      public override fun settings(clearPrevious: Boolean, settings: Settings) {
         Http2Connection.access$getWriterQueue$p(this.this$0)
            .schedule(
               new Http2Connection$ReaderRunnable$settings$$inlined$execute$default$1(
                  this.this$0.getConnectionName$okhttp() + " applyAndAckSettings", true, this, clearPrevious, settings
               ),
               0L
            );
      }

      public fun applyAndAckSettings(clearPrevious: Boolean, settings: Settings) {
         var newPeerSettings: ObjectRef = new ObjectRef();
         var var5: Http2Writer = this.this$0.getWriter();
         var var6: Http2Connection = this.this$0;
         var var30: Array<Http2Stream>;
         var var41: Long;
         synchronized (var5) {
            synchronized (var6) {
               var previousPeerSettings: Settings = var6.getPeerSettings();
               var var10000: ObjectRef = newPeerSettings;
               var var10001: Settings;
               if (clearPrevious) {
                  var10001 = settings;
               } else {
                  var var12: Settings = new Settings();
                  var12.merge(previousPeerSettings);
                  var12.merge(settings);
                  var10000 = newPeerSettings;
                  var10001 = var12;
               }

               var10000.element = var10001;
               var41 = (long)(newPeerSettings.element as Settings).getInitialWindowSize() - (long)previousPeerSettings.getInitialWindowSize();
               var30 = if (var41 != 0L && !var6.getStreams$okhttp().isEmpty()) var6.getStreams$okhttp().values().toArray(new Http2Stream[0]) else null;
               var6.setPeerSettings(newPeerSettings.element as Settings);
               Http2Connection.access$getSettingsListenerQueue$p(var6)
                  .schedule(
                     new Http2Connection$ReaderRunnable$applyAndAckSettings$lambda$7$lambda$6$$inlined$execute$default$1(
                        var6.getConnectionName$okhttp() + " onSettings", true, var6, newPeerSettings
                     ),
                     0L
                  );
            }

            try {
               var6.getWriter().applyAndAckSettings(newPeerSettings.element as Settings);
            } catch (IOException var27) {
               Http2Connection.access$failConnection(var6, var27);
            }
         }

         // $VF: monitorexit
         if (var30 != null) {
            for (Http2Stream stream : var30) {
               synchronized (stream) {
                  var34.addBytesToWriteWindow(var41);
               }
            }
         }
      }

      public override fun ackSettings() {
      }

      public override fun ping(ack: Boolean, payload1: Int, payload2: Int) {
         if (ack) {
            var `name$iv`: Http2Connection = this.this$0;
            synchronized (this.this$0) {
               switch (payload1) {
                  case 1:
                     var var16: Long = Http2Connection.access$getIntervalPongsReceived$p(`name$iv`);
                     Http2Connection.access$setIntervalPongsReceived$p(`name$iv`, var16 + 1L);
                     var16;
                     break;
                  case 2:
                     var var15: Long = Http2Connection.access$getDegradedPongsReceived$p(`name$iv`);
                     Http2Connection.access$setDegradedPongsReceived$p(`name$iv`, var15 + 1L);
                     var15;
                     break;
                  case 3:
                     Http2Connection.access$setAwaitPongsReceived$p(`name$iv`, Http2Connection.access$getAwaitPongsReceived$p(`name$iv`) + 1L);
                     `name$iv`.notifyAll();
                  default:
               }
            }
         } else {
            Http2Connection.access$getWriterQueue$p(this.this$0)
               .schedule(
                  new Http2Connection$ReaderRunnable$ping$$inlined$execute$default$1(
                     this.this$0.getConnectionName$okhttp() + " ping", true, this.this$0, payload1, payload2
                  ),
                  0L
               );
         }
      }

      public override fun goAway(lastGoodStreamId: Int, errorCode: ErrorCode, debugData: ByteString) {
         if (debugData.size() > 0) {
         }

         var var6: Http2Connection = this.this$0;
         var var13: Array<Any>;
         synchronized (this.this$0) {
            var13 = var6.getStreams$okhttp().values().toArray(new Http2Stream[0]);
            Http2Connection.access$setShutdown$p(var6, true);
         }

         for (Http2Stream http2Stream : (Http2Stream[])var13) {
            if (var17.getId() > lastGoodStreamId && var17.isLocallyInitiated()) {
               var17.receiveRstStream(ErrorCode.REFUSED_STREAM);
               this.this$0.removeStream$okhttp(var17.getId());
            }
         }
      }

      public override fun windowUpdate(streamId: Int, windowSizeIncrement: Long) {
         if (streamId == 0) {
            var var5: Http2Connection = this.this$0;
            synchronized (this.this$0) {
               Http2Connection.access$setWriteBytesMaximum$p(var5, var5.getWriteBytesMaximum() + windowSizeIncrement);
               var5.notifyAll();
            }
         } else {
            var var12: Http2Stream = this.this$0.getStream(streamId);
            if (var12 != null) {
               synchronized (stream) {
                  var12.addBytesToWriteWindow(windowSizeIncrement);
               }
            }
         }
      }

      public override fun priority(streamId: Int, streamDependency: Int, weight: Int, exclusive: Boolean) {
      }

      public override fun pushPromise(streamId: Int, promisedStreamId: Int, requestHeaders: List<Header>) {
         this.this$0.pushRequestLater$okhttp(promisedStreamId, requestHeaders);
      }

      public override fun alternateService(streamId: Int, origin: String, protocol: ByteString, host: String, port: Int, maxAge: Long) {
      }
   }
}
