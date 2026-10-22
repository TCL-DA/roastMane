package okhttp3.internal.http2

import java.io.EOFException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.util.ArrayDeque
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Headers
import okhttp3.internal.Util
import okio.AsyncTimeout
import okio.Buffer
import okio.BufferedSource
import okio.Sink
import okio.Source
import okio.Timeout

@SourceDebugExtension(["SMAP\nHttp2Stream.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokhttp3/internal/Util\n*L\n1#1,688:1\n1#2:689\n615#3,4:690\n615#3,4:694\n563#3:698\n615#3,4:699\n615#3,4:703\n563#3:707\n563#3:708\n615#3,4:709\n563#3:713\n557#3:714\n*S KotlinDebug\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream\n*L\n176#1:690,4\n255#1:694,4\n263#1:698\n274#1:699,4\n281#1:703,4\n295#1:707\n305#1:708\n491#1:709,4\n637#1:713\n657#1:714\n*E\n"])
class Http2Stream internal constructor(id: Int, connection: Http2Connection, outFinished: Boolean, inFinished: Boolean, headers: Headers?) {
   @JvmStatic
   public Http2Stream.Companion Companion = new Http2Stream.Companion(null);
   @JvmStatic
   public long EMIT_BUFFER_SIZE = 16384L;

   public final val connection: Http2Connection
   internal final var errorCode: ErrorCode?
      internal final get() {
         return this.errorCode;
      }

   internal final var errorException: IOException?
   private final var hasResponseHeaders: Boolean
   private final val headersQueue: ArrayDeque<Headers>
   public final val id: Int
   public final val isLocallyInitiated: Boolean
      public final get() {
         var streamIsClient: Boolean = (this.id and 1) == 1;
         return this.connection.getClient$okhttp() == streamIsClient;
      }

   public final val isOpen: Boolean
      public final get() {
         if (this.errorCode != null) {
            return false;
         } else {
            return !this.source.getFinished$okhttp() && !this.source.getClosed$okhttp()
               || !this.sink.getFinished() && !this.sink.getClosed()
               || !this.hasResponseHeaders;
         }
      }

   public final var readBytesAcknowledged: Long
      internal final set(<set-?>) {
         this.readBytesAcknowledged = var1;
      }

   public final var readBytesTotal: Long
      internal final set(<set-?>) {
         this.readBytesTotal = var1;
      }

   internal final val readTimeout: okhttp3.internal.http2.Http2Stream.StreamTimeout
   internal final val sink: okhttp3.internal.http2.Http2Stream.FramingSink
   internal final val source: okhttp3.internal.http2.Http2Stream.FramingSource
   public final var writeBytesMaximum: Long
      internal final set(<set-?>) {
         this.writeBytesMaximum = var1;
      }

   public final var writeBytesTotal: Long
      internal final set(<set-?>) {
         this.writeBytesTotal = var1;
      }

   internal final val writeTimeout: okhttp3.internal.http2.Http2Stream.StreamTimeout

   init {
      this.id = id;
      this.connection = connection;
      this.writeBytesMaximum = (long)this.connection.getPeerSettings().getInitialWindowSize();
      this.headersQueue = new ArrayDeque<>();
      this.source = new Http2Stream.FramingSource((long)this, (boolean)((long)this.connection.getOkHttpSettings().getInitialWindowSize()), inFinished);
      this.sink = new Http2Stream.FramingSink((boolean)this, outFinished);
      this.readTimeout = new Http2Stream.StreamTimeout(this);
      this.writeTimeout = new Http2Stream.StreamTimeout(this);
      if (headers != null) {
         if (this.isLocallyInitiated()) {
            throw new IllegalStateException("locally-initiated streams shouldn't have headers yet".toString());
         }

         this.headersQueue.add(headers);
      } else if (!this.isLocallyInitiated()) {
         throw new IllegalStateException("remotely-initiated streams should have headers".toString());
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun takeHeaders(): Headers {
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
      // 00: aload 0
      // 01: getfield okhttp3/internal/http2/Http2Stream.readTimeout Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
      // 04: invokevirtual okhttp3/internal/http2/Http2Stream$StreamTimeout.enter ()V
      // 07: nop
      // 08: aload 0
      // 09: getfield okhttp3/internal/http2/Http2Stream.headersQueue Ljava/util/ArrayDeque;
      // 0c: invokevirtual java/util/ArrayDeque.isEmpty ()Z
      // 0f: ifeq 20
      // 12: aload 0
      // 13: getfield okhttp3/internal/http2/Http2Stream.errorCode Lokhttp3/internal/http2/ErrorCode;
      // 16: ifnonnull 20
      // 19: aload 0
      // 1a: invokevirtual okhttp3/internal/http2/Http2Stream.waitForIo$okhttp ()V
      // 1d: goto 08
      // 20: aload 0
      // 21: getfield okhttp3/internal/http2/Http2Stream.readTimeout Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
      // 24: invokevirtual okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut ()V
      // 27: goto 34
      // 2a: astore 1
      // 2b: aload 0
      // 2c: getfield okhttp3/internal/http2/Http2Stream.readTimeout Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
      // 2f: invokevirtual okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut ()V
      // 32: aload 1
      // 33: athrow
      // 34: aload 0
      // 35: getfield okhttp3/internal/http2/Http2Stream.headersQueue Ljava/util/ArrayDeque;
      // 38: checkcast java/util/Collection
      // 3b: invokeinterface java/util/Collection.isEmpty ()Z 1
      // 40: ifne 47
      // 43: bipush 1
      // 44: goto 48
      // 47: bipush 0
      // 48: ifeq 5c
      // 4b: aload 0
      // 4c: getfield okhttp3/internal/http2/Http2Stream.headersQueue Ljava/util/ArrayDeque;
      // 4f: invokevirtual java/util/ArrayDeque.removeFirst ()Ljava/lang/Object;
      // 52: dup
      // 53: ldc "headersQueue.removeFirst()"
      // 55: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 58: checkcast okhttp3/Headers
      // 5b: areturn
      // 5c: aload 0
      // 5d: getfield okhttp3/internal/http2/Http2Stream.errorException Ljava/io/IOException;
      // 60: dup
      // 61: ifnull 6a
      // 64: checkcast java/lang/Throwable
      // 67: goto 7d
      // 6a: pop
      // 6b: new okhttp3/internal/http2/StreamResetException
      // 6e: dup
      // 6f: aload 0
      // 70: getfield okhttp3/internal/http2/Http2Stream.errorCode Lokhttp3/internal/http2/ErrorCode;
      // 73: dup
      // 74: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 77: invokespecial okhttp3/internal/http2/StreamResetException.<init> (Lokhttp3/internal/http2/ErrorCode;)V
      // 7a: checkcast java/lang/Throwable
      // 7d: athrow
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun trailers(): Headers {
      if (this.source.getFinished$okhttp() && this.source.getReceiveBuffer().exhausted() && this.source.getReadBuffer().exhausted()) {
         var var1: Headers = this.source.getTrailers();
         if (var1 == null) {
            var1 = Util.EMPTY_HEADERS;
         }

         return var1;
      } else if (this.errorCode != null) {
         var var10000: java.lang.Throwable;
         if (this.errorException != null) {
            var10000 = this.errorException;
         } else {
            var var10002: ErrorCode = this.errorCode;
            var10000 = new StreamResetException(var10002);
         }

         throw var10000;
      } else {
         throw new IllegalStateException("too early; can't read the trailers yet");
      }
   }

   @Throws(java/io/IOException::class)
   public fun writeHeaders(responseHeaders: List<Header>, outFinished: Boolean, flushHeaders: Boolean) {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         var var10: Boolean = flushHeaders;
         synchronized (this) {
            this.hasResponseHeaders = true;
            if (outFinished) {
               this.sink.setFinished(true);
            }
         }

         if (!flushHeaders) {
            synchronized (this.connection) {
               var10 = this.connection.getWriteBytesTotal() >= this.connection.getWriteBytesMaximum();
            }
         }

         this.connection.writeHeaders$okhttp(this.id, outFinished, responseHeaders);
         if (var10) {
            this.connection.flush();
         }
      }
   }

   public fun enqueueTrailers(trailers: Headers) {
      synchronized (this) {
         if (this.sink.getFinished()) {
            throw new IllegalStateException("already finished".toString());
         } else if (trailers.size() == 0) {
            throw new IllegalArgumentException("trailers.size() == 0".toString());
         } else {
            this.sink.setTrailers(trailers);
         }
      }
   }

   public fun readTimeout(): Timeout {
      return this.readTimeout as Timeout;
   }

   public fun writeTimeout(): Timeout {
      return this.writeTimeout as Timeout;
   }

   public fun getSource(): Source {
      return this.source;
   }

   public fun getSink(): Sink {
      synchronized (this) {
         if (!this.hasResponseHeaders && !this.isLocallyInitiated()) {
            throw new IllegalStateException("reply before requesting the sink".toString());
         }
      }

      return this.sink;
   }

   @Throws(java/io/IOException::class)
   public fun close(rstStatusCode: ErrorCode, errorException: IOException?) {
      if (this.closeInternal(rstStatusCode, errorException)) {
         this.connection.writeSynReset$okhttp(this.id, rstStatusCode);
      }
   }

   public fun closeLater(errorCode: ErrorCode) {
      if (this.closeInternal(errorCode, null)) {
         this.connection.writeSynResetLater$okhttp(this.id, errorCode);
      }
   }

   private fun closeInternal(errorCode: ErrorCode, errorException: IOException?): Boolean {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         label69: {
            synchronized (this){} // $VF: monitorenter 

            label37: {
               label36: {
                  try {
                     if (this.errorCode != null) {
                        break label37;
                     }

                     this.errorCode = errorCode;
                     this.errorException = errorException;
                     this.notifyAll();
                     if (this.source.getFinished$okhttp() && this.sink.getFinished()) {
                        break label36;
                     }
                  } catch (java.lang.Throwable var9) {
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

   @Throws(java/io/IOException::class)
   public fun receiveData(source: BufferedSource, length: Int) {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         this.source.receive$okhttp(source, (long)length);
      }
   }

   public fun receiveHeaders(headers: Headers, inFinished: Boolean) {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         var var9: Boolean;
         synchronized (this) {
            if (this.hasResponseHeaders && inFinished) {
               this.source.setTrailers(headers);
            } else {
               this.hasResponseHeaders = true;
               this.headersQueue.add(headers);
            }

            if (inFinished) {
               this.source.setFinished$okhttp(true);
            }

            var9 = this.isOpen();
            this.notifyAll();
         }

         if (!var9) {
            this.connection.removeStream$okhttp(this.id);
         }
      }
   }

   @Synchronized
   public fun receiveRstStream(errorCode: ErrorCode) {
      if (this.errorCode == null) {
         this.errorCode = errorCode;
         this.notifyAll();
      }
   }

   @Throws(java/io/IOException::class)
   internal fun cancelStreamIfNecessary() {
      if (Util.assertionsEnabled && Thread.holdsLock(this)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + this);
      } else {
         var var6: Boolean;
         var var8: Boolean;
         synchronized (this) {
            var8 = !this.source.getFinished$okhttp() && this.source.getClosed$okhttp() && (this.sink.getFinished() || this.sink.getClosed());
            var6 = this.isOpen();
         }

         if (var8) {
            this.close(ErrorCode.CANCEL, null);
         } else if (!var6) {
            this.connection.removeStream$okhttp(this.id);
         }
      }
   }

   public fun addBytesToWriteWindow(delta: Long) {
      this.writeBytesMaximum += delta;
      if (delta > 0L) {
         this.notifyAll();
      }
   }

   @Throws(java/io/IOException::class)
   internal fun checkOutNotClosed() {
      if (this.sink.getClosed()) {
         throw new IOException("stream closed");
      } else if (this.sink.getFinished()) {
         throw new IOException("stream finished");
      } else if (this.errorCode != null) {
         var var10000: java.lang.Throwable;
         if (this.errorException != null) {
            var10000 = this.errorException;
         } else {
            var var10002: ErrorCode = this.errorCode;
            var10000 = new StreamResetException(var10002);
         }

         throw var10000;
      }
   }

   @Throws(java/io/InterruptedIOException::class)
   internal fun waitForIo() {
      try {
         this.wait();
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
         throw new InterruptedIOException();
      }
   }

   companion object Companion private constructor() {
      internal const val EMIT_BUFFER_SIZE: Long

   }

   @SourceDebugExtension(["SMAP\nHttp2Stream.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSink\n+ 2 Util.kt\nokhttp3/internal/Util\n*L\n1#1,688:1\n615#2,4:689\n615#2,4:693\n615#2,4:697\n*S KotlinDebug\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSink\n*L\n528#1:689,4\n573#1:693,4\n589#1:697,4\n*E\n"])
   internal inner class FramingSink(finished: Boolean) : Sink {
      public final var closed: Boolean
         internal set
      public final var finished: Boolean
         internal set
      private final val sendBuffer: Buffer
      public final var trailers: Headers?
         internal set

      init {
         this.this$0 = `this$0`;
         this.finished = finished;
         this.sendBuffer = new Buffer();
      }

      @Throws(java/io/IOException::class)
      public open fun write(source: Buffer, byteCount: Long) {
         var `$this$assertThreadDoesntHoldLock$iv`: Any = this.this$0;
         if (Util.assertionsEnabled && Thread.holdsLock(this.this$0)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + `$this$assertThreadDoesntHoldLock$iv`);
         } else {
            this.sendBuffer.write(source, byteCount);

            while (this.sendBuffer.size() >= 16384L) {
               this.emitFrame(false);
            }
         }
      }

      @Throws(java/io/IOException::class)
      private fun emitFrame(outFinishedOnLastFrame: Boolean) {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
         //
         // Bytecode:
         // 00: lconst_0
         // 01: lstore 8
         // 03: bipush 0
         // 04: istore 2
         // 05: aload 0
         // 06: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 09: astore 3
         // 0a: aload 0
         // 0b: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 0e: astore 4
         // 10: aload 3
         // 11: astore 5
         // 13: aload 5
         // 15: monitorenter
         // 16: nop
         // 17: bipush 0
         // 18: istore 6
         // 1a: aload 4
         // 1c: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteTimeout$okhttp ()Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
         // 1f: invokevirtual okhttp3/internal/http2/Http2Stream$StreamTimeout.enter ()V
         // 22: nop
         // 23: aload 4
         // 25: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteBytesTotal ()J
         // 28: aload 4
         // 2a: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteBytesMaximum ()J
         // 2d: lcmp
         // 2e: iflt 4f
         // 31: aload 0
         // 32: getfield okhttp3/internal/http2/Http2Stream$FramingSink.finished Z
         // 35: ifne 4f
         // 38: aload 0
         // 39: getfield okhttp3/internal/http2/Http2Stream$FramingSink.closed Z
         // 3c: ifne 4f
         // 3f: aload 4
         // 41: invokevirtual okhttp3/internal/http2/Http2Stream.getErrorCode$okhttp ()Lokhttp3/internal/http2/ErrorCode;
         // 44: ifnonnull 4f
         // 47: aload 4
         // 49: invokevirtual okhttp3/internal/http2/Http2Stream.waitForIo$okhttp ()V
         // 4c: goto 23
         // 4f: aload 4
         // 51: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteTimeout$okhttp ()Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
         // 54: invokevirtual okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut ()V
         // 57: goto 67
         // 5a: astore 7
         // 5c: aload 4
         // 5e: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteTimeout$okhttp ()Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
         // 61: invokevirtual okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut ()V
         // 64: aload 7
         // 66: athrow
         // 67: aload 4
         // 69: invokevirtual okhttp3/internal/http2/Http2Stream.checkOutNotClosed$okhttp ()V
         // 6c: aload 4
         // 6e: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteBytesMaximum ()J
         // 71: aload 4
         // 73: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteBytesTotal ()J
         // 76: lsub
         // 77: aload 0
         // 78: getfield okhttp3/internal/http2/Http2Stream$FramingSink.sendBuffer Lokio/Buffer;
         // 7b: invokevirtual okio/Buffer.size ()J
         // 7e: invokestatic java/lang/Math.min (JJ)J
         // 81: lstore 8
         // 83: aload 4
         // 85: aload 4
         // 87: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteBytesTotal ()J
         // 8a: lload 8
         // 8c: ladd
         // 8d: invokevirtual okhttp3/internal/http2/Http2Stream.setWriteBytesTotal$okhttp (J)V
         // 90: iload 1
         // 91: ifeq a5
         // 94: lload 8
         // 96: aload 0
         // 97: getfield okhttp3/internal/http2/Http2Stream$FramingSink.sendBuffer Lokio/Buffer;
         // 9a: invokevirtual okio/Buffer.size ()J
         // 9d: lcmp
         // 9e: ifne a5
         // a1: bipush 1
         // a2: goto a6
         // a5: bipush 0
         // a6: istore 2
         // a7: nop
         // a8: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // ab: astore 6
         // ad: aload 5
         // af: monitorexit
         // b0: goto bb
         // b3: astore 6
         // b5: aload 5
         // b7: monitorexit
         // b8: aload 6
         // ba: athrow
         // bb: aload 0
         // bc: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // bf: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteTimeout$okhttp ()Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
         // c2: invokevirtual okhttp3/internal/http2/Http2Stream$StreamTimeout.enter ()V
         // c5: nop
         // c6: aload 0
         // c7: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // ca: invokevirtual okhttp3/internal/http2/Http2Stream.getConnection ()Lokhttp3/internal/http2/Http2Connection;
         // cd: aload 0
         // ce: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // d1: invokevirtual okhttp3/internal/http2/Http2Stream.getId ()I
         // d4: iload 2
         // d5: aload 0
         // d6: getfield okhttp3/internal/http2/Http2Stream$FramingSink.sendBuffer Lokio/Buffer;
         // d9: lload 8
         // db: invokevirtual okhttp3/internal/http2/Http2Connection.writeData (IZLokio/Buffer;J)V
         // de: aload 0
         // df: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // e2: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteTimeout$okhttp ()Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
         // e5: invokevirtual okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut ()V
         // e8: goto f8
         // eb: astore 3
         // ec: aload 0
         // ed: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // f0: invokevirtual okhttp3/internal/http2/Http2Stream.getWriteTimeout$okhttp ()Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
         // f3: invokevirtual okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut ()V
         // f6: aload 3
         // f7: athrow
         // f8: return
      }

      @Throws(java/io/IOException::class)
      public open fun flush() {
         var `$this$assertThreadDoesntHoldLock$iv`: Any = this.this$0;
         if (Util.assertionsEnabled && Thread.holdsLock(this.this$0)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + `$this$assertThreadDoesntHoldLock$iv`);
         } else {
            var var7: Http2Stream = this.this$0;
            synchronized (this.this$0) {
               var7.checkOutNotClosed$okhttp();
            }

            while (this.sendBuffer.size() > 0L) {
               this.emitFrame(false);
               this.this$0.getConnection().flush();
            }
         }
      }

      public open fun timeout(): Timeout {
         return this.this$0.getWriteTimeout$okhttp() as Timeout;
      }

      @Throws(java/io/IOException::class)
      public open fun close() {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.MonitorExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.MonitorExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
         //
         // Bytecode:
         // 000: aload 0
         // 001: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 004: astore 1
         // 005: bipush 0
         // 006: istore 2
         // 007: getstatic okhttp3/internal/Util.assertionsEnabled Z
         // 00a: ifeq 03d
         // 00d: aload 1
         // 00e: invokestatic java/lang/Thread.holdsLock (Ljava/lang/Object;)Z
         // 011: ifeq 03d
         // 014: new java/lang/AssertionError
         // 017: dup
         // 018: new java/lang/StringBuilder
         // 01b: dup
         // 01c: invokespecial java/lang/StringBuilder.<init> ()V
         // 01f: ldc "Thread "
         // 021: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
         // 024: invokestatic java/lang/Thread.currentThread ()Ljava/lang/Thread;
         // 027: invokevirtual java/lang/Thread.getName ()Ljava/lang/String;
         // 02a: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
         // 02d: ldc " MUST NOT hold lock on "
         // 02f: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
         // 032: aload 1
         // 033: invokevirtual java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
         // 036: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
         // 039: invokespecial java/lang/AssertionError.<init> (Ljava/lang/Object;)V
         // 03c: athrow
         // 03d: nop
         // 03e: bipush 0
         // 03f: istore 1
         // 040: aload 0
         // 041: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 044: astore 2
         // 045: aload 0
         // 046: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 049: astore 3
         // 04a: aload 2
         // 04b: astore 4
         // 04d: aload 4
         // 04f: monitorenter
         // 050: nop
         // 051: bipush 0
         // 052: istore 5
         // 054: aload 0
         // 055: getfield okhttp3/internal/http2/Http2Stream$FramingSink.closed Z
         // 058: ifeq 05f
         // 05b: aload 4
         // 05d: monitorexit
         // 05e: return
         // 05f: aload 3
         // 060: invokevirtual okhttp3/internal/http2/Http2Stream.getErrorCode$okhttp ()Lokhttp3/internal/http2/ErrorCode;
         // 063: ifnonnull 06a
         // 066: bipush 1
         // 067: goto 06b
         // 06a: bipush 0
         // 06b: istore 1
         // 06c: nop
         // 06d: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // 070: astore 5
         // 072: aload 4
         // 074: monitorexit
         // 075: goto 080
         // 078: astore 5
         // 07a: aload 4
         // 07c: monitorexit
         // 07d: aload 5
         // 07f: athrow
         // 080: aload 0
         // 081: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 084: invokevirtual okhttp3/internal/http2/Http2Stream.getSink$okhttp ()Lokhttp3/internal/http2/Http2Stream$FramingSink;
         // 087: getfield okhttp3/internal/http2/Http2Stream$FramingSink.finished Z
         // 08a: ifne 115
         // 08d: aload 0
         // 08e: getfield okhttp3/internal/http2/Http2Stream$FramingSink.sendBuffer Lokio/Buffer;
         // 091: invokevirtual okio/Buffer.size ()J
         // 094: lconst_0
         // 095: lcmp
         // 096: ifle 09d
         // 099: bipush 1
         // 09a: goto 09e
         // 09d: bipush 0
         // 09e: istore 2
         // 09f: aload 0
         // 0a0: getfield okhttp3/internal/http2/Http2Stream$FramingSink.trailers Lokhttp3/Headers;
         // 0a3: ifnull 0aa
         // 0a6: bipush 1
         // 0a7: goto 0ab
         // 0aa: bipush 0
         // 0ab: istore 3
         // 0ac: nop
         // 0ad: iload 3
         // 0ae: ifeq 0e5
         // 0b1: aload 0
         // 0b2: getfield okhttp3/internal/http2/Http2Stream$FramingSink.sendBuffer Lokio/Buffer;
         // 0b5: invokevirtual okio/Buffer.size ()J
         // 0b8: lconst_0
         // 0b9: lcmp
         // 0ba: ifle 0c5
         // 0bd: aload 0
         // 0be: bipush 0
         // 0bf: invokespecial okhttp3/internal/http2/Http2Stream$FramingSink.emitFrame (Z)V
         // 0c2: goto 0b1
         // 0c5: aload 0
         // 0c6: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 0c9: invokevirtual okhttp3/internal/http2/Http2Stream.getConnection ()Lokhttp3/internal/http2/Http2Connection;
         // 0cc: aload 0
         // 0cd: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 0d0: invokevirtual okhttp3/internal/http2/Http2Stream.getId ()I
         // 0d3: iload 1
         // 0d4: aload 0
         // 0d5: getfield okhttp3/internal/http2/Http2Stream$FramingSink.trailers Lokhttp3/Headers;
         // 0d8: dup
         // 0d9: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
         // 0dc: invokestatic okhttp3/internal/Util.toHeaderList (Lokhttp3/Headers;)Ljava/util/List;
         // 0df: invokevirtual okhttp3/internal/http2/Http2Connection.writeHeaders$okhttp (IZLjava/util/List;)V
         // 0e2: goto 115
         // 0e5: iload 2
         // 0e6: ifeq 0fd
         // 0e9: aload 0
         // 0ea: getfield okhttp3/internal/http2/Http2Stream$FramingSink.sendBuffer Lokio/Buffer;
         // 0ed: invokevirtual okio/Buffer.size ()J
         // 0f0: lconst_0
         // 0f1: lcmp
         // 0f2: ifle 115
         // 0f5: aload 0
         // 0f6: bipush 1
         // 0f7: invokespecial okhttp3/internal/http2/Http2Stream$FramingSink.emitFrame (Z)V
         // 0fa: goto 0e9
         // 0fd: iload 1
         // 0fe: ifeq 115
         // 101: aload 0
         // 102: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 105: invokevirtual okhttp3/internal/http2/Http2Stream.getConnection ()Lokhttp3/internal/http2/Http2Connection;
         // 108: aload 0
         // 109: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 10c: invokevirtual okhttp3/internal/http2/Http2Stream.getId ()I
         // 10f: bipush 1
         // 110: aconst_null
         // 111: lconst_0
         // 112: invokevirtual okhttp3/internal/http2/Http2Connection.writeData (IZLokio/Buffer;J)V
         // 115: aload 0
         // 116: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 119: astore 3
         // 11a: aload 3
         // 11b: monitorenter
         // 11c: nop
         // 11d: bipush 0
         // 11e: istore 4
         // 120: aload 0
         // 121: bipush 1
         // 122: putfield okhttp3/internal/http2/Http2Stream$FramingSink.closed Z
         // 125: nop
         // 126: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // 129: astore 4
         // 12b: aload 3
         // 12c: monitorexit
         // 12d: goto 137
         // 130: astore 4
         // 132: aload 3
         // 133: monitorexit
         // 134: aload 4
         // 136: athrow
         // 137: aload 0
         // 138: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 13b: invokevirtual okhttp3/internal/http2/Http2Stream.getConnection ()Lokhttp3/internal/http2/Http2Connection;
         // 13e: invokevirtual okhttp3/internal/http2/Http2Connection.flush ()V
         // 141: aload 0
         // 142: getfield okhttp3/internal/http2/Http2Stream$FramingSink.this$0 Lokhttp3/internal/http2/Http2Stream;
         // 145: invokevirtual okhttp3/internal/http2/Http2Stream.cancelStreamIfNecessary$okhttp ()V
         // 148: return
      }
   }

   @SourceDebugExtension(["SMAP\nHttp2Stream.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokhttp3/internal/Util\n*L\n1#1,688:1\n1#2:689\n615#3,4:690\n615#3,4:694\n563#3:698\n563#3:699\n*S KotlinDebug\n*F\n+ 1 Http2Stream.kt\nokhttp3/internal/http2/Http2Stream$FramingSource\n*L\n407#1:690,4\n418#1:694,4\n458#1:698\n480#1:699\n*E\n"])
   inner class FramingSource internal constructor(maxByteCount: Long, finished: Boolean) : Source {
      internal final var closed: Boolean
      internal final var finished: Boolean
      private final val maxByteCount: Long
      public final val readBuffer: Buffer
      public final val receiveBuffer: Buffer
      public final var trailers: Headers?
         internal set

      init {
         this.this$0 = `this$0`;
         this.maxByteCount = maxByteCount;
         this.finished = finished;
         this.receiveBuffer = new Buffer();
         this.readBuffer = new Buffer();
      }

      @Throws(java/io/IOException::class)
      public open fun read(sink: Buffer, byteCount: Long): Long {
         if (byteCount < 0L) {
            throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
         } else {
            label122: {
               var tryAgain: Boolean;
               var errorExceptionToDeliver: Any;
               var var21: Long;
               do {
                  tryAgain = false;
                  var21 = -1L;
                  errorExceptionToDeliver = null;
                  var var7: Http2Stream = this.this$0;
                  var var8: Http2Stream = this.this$0;
                  synchronized (this.this$0) {
                     var8.getReadTimeout$okhttp().enter();

                     try {
                        if (var8.getErrorCode$okhttp() != null && !this.finished) {
                           var var10000: IOException = var8.getErrorException$okhttp();
                           if (var10000 == null) {
                              var var10002: ErrorCode = var8.getErrorCode$okhttp();
                              var10000 = new StreamResetException(var10002);
                           }

                           errorExceptionToDeliver = var10000;
                        }

                        if (this.closed) {
                           throw new IOException("stream closed");
                        }

                        if (this.readBuffer.size() > 0L) {
                           var21 = this.readBuffer.read(sink, Math.min(byteCount, this.readBuffer.size()));
                           var8.setReadBytesTotal$okhttp(var8.getReadBytesTotal() + var21);
                           var unacknowledgedBytesRead: Long = var8.getReadBytesTotal() - var8.getReadBytesAcknowledged();
                           if (errorExceptionToDeliver == null
                              && unacknowledgedBytesRead >= (long)(var8.getConnection().getOkHttpSettings().getInitialWindowSize() / 2)) {
                              var8.getConnection().writeWindowUpdateLater$okhttp(var8.getId(), unacknowledgedBytesRead);
                              var8.setReadBytesAcknowledged$okhttp(var8.getReadBytesTotal());
                           }
                        } else if (!this.finished && errorExceptionToDeliver == null) {
                           var8.waitForIo$okhttp();
                           tryAgain = true;
                        }
                     } catch (java.lang.Throwable var16) {
                        var8.getReadTimeout$okhttp().exitAndThrowIfTimedOut();
                     }

                     var8.getReadTimeout$okhttp().exitAndThrowIfTimedOut();
                  }

                  // $VF: monitorexit
               } while (tryAgain);

               if (var21 != -1L) {
                  return var21;
               } else if (errorExceptionToDeliver != null) {
                  throw errorExceptionToDeliver as java.lang.Throwable;
               } else {
                  return -1L;
               }
            }
         }
      }

      private fun updateConnectionFlowControl(read: Long) {
         var `$this$assertThreadDoesntHoldLock$iv`: Any = this.this$0;
         if (Util.assertionsEnabled && Thread.holdsLock(this.this$0)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + `$this$assertThreadDoesntHoldLock$iv`);
         } else {
            this.this$0.getConnection().updateConnectionFlowControl$okhttp(read);
         }
      }

      @Throws(java/io/IOException::class)
      internal fun receive(source: BufferedSource, byteCount: Long) {
         var `$this$assertThreadDoesntHoldLock$iv`: Any = this.this$0;
         if (Util.assertionsEnabled && Thread.holdsLock(this.this$0)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + `$this$assertThreadDoesntHoldLock$iv`);
         } else {
            var var26: Long = byteCount;

            while (var26 > 0L) {
               var var21: Boolean;
               var var22: Boolean;
               synchronized (this.this$0) {
                  var21 = this.finished;
                  var22 = var26 + this.readBuffer.size() > this.maxByteCount;
               }

               if (var22) {
                  source.skip(var26);
                  this.this$0.closeLater(ErrorCode.FLOW_CONTROL_ERROR);
                  return;
               }

               if (var21) {
                  source.skip(var26);
                  return;
               }

               var read: Long = source.read(this.receiveBuffer, var26);
               if (read == -1L) {
                  throw new EOFException();
               }

               var26 -= read;
               var var10: Http2Stream = this.this$0;
               synchronized (this.this$0) {
                  if (this.closed) {
                     this.receiveBuffer.clear();
                  } else {
                     var wasEmpty: Boolean = this.readBuffer.size() == 0L;
                     this.readBuffer.writeAll(this.receiveBuffer as Source);
                     if (wasEmpty) {
                        var10.notifyAll();
                     }
                  }
               }
            }

            this.updateConnectionFlowControl(byteCount);
         }
      }

      public open fun timeout(): Timeout {
         return this.this$0.getReadTimeout$okhttp() as Timeout;
      }

      @Throws(java/io/IOException::class)
      public open fun close() {
         var var2: Http2Stream = this.this$0;
         var var11: Long;
         synchronized (this.this$0) {
            this.closed = true;
            var11 = this.readBuffer.size();
            this.readBuffer.clear();
            var2.notifyAll();
         }

         if (var11 > 0L) {
            this.updateConnectionFlowControl(var11);
         }

         this.this$0.cancelStreamIfNecessary$okhttp();
      }
   }

   internal inner class StreamTimeout : AsyncTimeout {
      protected open fun timedOut() {
         this.this$0.closeLater(ErrorCode.CANCEL);
         this.this$0.getConnection().sendDegradedPingLater$okhttp();
      }

      protected open fun newTimeoutException(cause: IOException?): IOException {
         var var2: SocketTimeoutException = new SocketTimeoutException("timeout");
         if (cause != null) {
            var2.initCause(cause);
         }

         return var2;
      }

      @Throws(java/io/IOException::class)
      public fun exitAndThrowIfTimedOut() {
         if (this.exit()) {
            throw this.newTimeoutException(null);
         }
      }
   }
}
