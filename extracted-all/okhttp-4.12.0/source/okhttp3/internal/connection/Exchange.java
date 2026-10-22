package okhttp3.internal.connection

import java.io.IOException
import java.net.ProtocolException
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.EventListener
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.Response.Builder
import okhttp3.internal.http.ExchangeCodec
import okhttp3.internal.http.RealResponseBody
import okhttp3.internal.ws.RealWebSocket.Streams
import okio.Buffer
import okio.ForwardingSink
import okio.ForwardingSource
import okio.Okio
import okio.Sink
import okio.Source

class Exchange(call: RealCall, eventListener: EventListener, finder: ExchangeFinder, codec: ExchangeCodec) {
   internal final val call: RealCall
   private final val codec: ExchangeCodec
   internal final val connection: RealConnection
   internal final val eventListener: EventListener
   internal final val finder: ExchangeFinder
   internal final var hasFailure: Boolean
      private set
   internal final val isCoalescedConnection: Boolean
      internal final get() {
         return !(this.finder.getAddress$okhttp().url().host() == this.connection.route().address().url().host());
      }

   internal final var isDuplex: Boolean
      private set

   init {
      this.call = call;
      this.eventListener = eventListener;
      this.finder = finder;
      this.codec = codec;
      this.connection = this.codec.getConnection();
   }

   @Throws(java/io/IOException::class)
   public fun writeRequestHeaders(request: Request) {
      try {
         this.eventListener.requestHeadersStart(this.call);
         this.codec.writeRequestHeaders(request);
         this.eventListener.requestHeadersEnd(this.call, request);
      } catch (IOException var3) {
         this.eventListener.requestFailed(this.call, var3);
         this.trackFailure(var3);
         throw var3;
      }
   }

   @Throws(java/io/IOException::class)
   public fun createRequestBody(request: Request, duplex: Boolean): Sink {
      this.isDuplex = duplex;
      var var10000: RequestBody = request.body();
      var contentLength: Long = var10000.contentLength();
      this.eventListener.requestBodyStart(this.call);
      return (new Exchange.RequestBodySink(this, (long)this.codec.createRequestBody(request, contentLength), contentLength)) as Sink;
   }

   @Throws(java/io/IOException::class)
   public fun flushRequest() {
      try {
         this.codec.flushRequest();
      } catch (IOException var2) {
         this.eventListener.requestFailed(this.call, var2);
         this.trackFailure(var2);
         throw var2;
      }
   }

   @Throws(java/io/IOException::class)
   public fun finishRequest() {
      try {
         this.codec.finishRequest();
      } catch (IOException var2) {
         this.eventListener.requestFailed(this.call, var2);
         this.trackFailure(var2);
         throw var2;
      }
   }

   public fun responseHeadersStart() {
      this.eventListener.responseHeadersStart(this.call);
   }

   @Throws(java/io/IOException::class)
   public fun readResponseHeaders(expectContinue: Boolean): Builder? {
      try {
         var e: Response.Builder = this.codec.readResponseHeaders(expectContinue);
         if (e != null) {
            e.initExchange$okhttp(this);
         }

         return e;
      } catch (IOException var3) {
         this.eventListener.responseFailed(this.call, var3);
         this.trackFailure(var3);
         throw var3;
      }
   }

   public fun responseHeadersEnd(response: Response) {
      this.eventListener.responseHeadersEnd(this.call, response);
   }

   @Throws(java/io/IOException::class)
   public fun openResponseBody(response: Response): ResponseBody {
      try {
         var e: java.lang.String = Response.header$default(response, "Content-Type", null, 2, null);
         var contentLength: Long = this.codec.reportedContentLength(response);
         return new RealResponseBody(
            e, contentLength, Okio.buffer((new Exchange.ResponseBodySource(this, (long)this.codec.openResponseBodySource(response), contentLength)) as Source)
         );
      } catch (IOException var7) {
         this.eventListener.responseFailed(this.call, var7);
         this.trackFailure(var7);
         throw var7;
      }
   }

   @Throws(java/io/IOException::class)
   public fun trailers(): Headers {
      return this.codec.trailers();
   }

   @Throws(java/net/SocketException::class)
   public fun newWebSocketStreams(): Streams {
      this.call.timeoutEarlyExit();
      return this.codec.getConnection().newWebSocketStreams$okhttp(this);
   }

   public fun webSocketUpgradeFailed() {
      this.bodyComplete(-1L, true, true, null);
   }

   public fun noNewExchangesOnConnection() {
      this.codec.getConnection().noNewExchanges$okhttp();
   }

   public fun cancel() {
      this.codec.cancel();
   }

   public fun detachWithViolence() {
      this.codec.cancel();
      this.call.messageDone$okhttp(this, true, true, null);
   }

   private fun trackFailure(e: IOException) {
      this.hasFailure = true;
      this.finder.trackFailure(e);
      this.codec.getConnection().trackFailure$okhttp(this.call, e);
   }

   public fun <E : IOException?> bodyComplete(bytesRead: Long, responseDone: Boolean, requestDone: Boolean, e: E): E {
      if (e != null) {
         this.trackFailure(e);
      }

      if (requestDone) {
         if (e != null) {
            this.eventListener.requestFailed(this.call, e);
         } else {
            this.eventListener.requestBodyEnd(this.call, bytesRead);
         }
      }

      if (responseDone) {
         if (e != null) {
            this.eventListener.responseFailed(this.call, e);
         } else {
            this.eventListener.responseBodyEnd(this.call, bytesRead);
         }
      }

      return (E)this.call.messageDone$okhttp(this, requestDone, responseDone, e);
   }

   public fun noRequestBody() {
      this.call.messageDone$okhttp(this, true, false, null);
   }

   @SourceDebugExtension(["SMAP\nExchange.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Exchange.kt\nokhttp3/internal/connection/Exchange$RequestBodySink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,333:1\n1#2:334\n*E\n"])
   private inner class RequestBodySink(delegate: Sink, contentLength: Long) : ForwardingSink(delegate) {
      private final var bytesReceived: Long
      private final var closed: Boolean
      private final var completed: Boolean
      private final val contentLength: Long

      init {
         this.this$0 = `this$0`;
         this.contentLength = contentLength;
      }

      @Throws(java/io/IOException::class)
      public open fun write(source: Buffer, byteCount: Long) {
         if (this.closed) {
            throw new IllegalStateException("closed".toString());
         } else if (this.contentLength != -1L && this.bytesReceived + byteCount > this.contentLength) {
            throw new ProtocolException("expected " + this.contentLength + " bytes but received " + (this.bytesReceived + byteCount));
         } else {
            try {
               super.write(source, byteCount);
               this.bytesReceived += byteCount;
            } catch (IOException var6) {
               throw this.complete(var6);
            }
         }
      }

      @Throws(java/io/IOException::class)
      public open fun flush() {
         try {
            super.flush();
         } catch (IOException var2) {
            throw this.complete(var2);
         }
      }

      @Throws(java/io/IOException::class)
      public open fun close() {
         if (!this.closed) {
            this.closed = true;
            if (this.contentLength != -1L && this.bytesReceived != this.contentLength) {
               throw new ProtocolException("unexpected end of stream");
            } else {
               try {
                  super.close();
                  this.complete(null);
               } catch (IOException var2) {
                  throw this.complete(var2);
               }
            }
         }
      }

      private fun <E : IOException?> complete(e: E): E {
         if (this.completed) {
            return (E)e;
         } else {
            this.completed = true;
            return (E)this.this$0.bodyComplete(this.bytesReceived, false, true, e);
         }
      }
   }

   @SourceDebugExtension(["SMAP\nExchange.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Exchange.kt\nokhttp3/internal/connection/Exchange$ResponseBodySource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,333:1\n1#2:334\n*E\n"])
   internal inner class ResponseBodySource(delegate: Source, contentLength: Long) : ForwardingSource(delegate) {
      private final var bytesReceived: Long
      private final var closed: Boolean
      private final var completed: Boolean
      private final val contentLength: Long
      private final var invokeStartEvent: Boolean

      init {
         this.this$0 = `this$0`;
         this.contentLength = contentLength;
         this.invokeStartEvent = true;
         if (this.contentLength == 0L) {
            this.complete(null);
         }
      }

      @Throws(java/io/IOException::class)
      public open fun read(sink: Buffer, byteCount: Long): Long {
         if (this.closed) {
            throw new IllegalStateException("closed".toString());
         } else {
            try {
               var e: Long = this.delegate().read(sink, byteCount);
               if (this.invokeStartEvent) {
                  this.invokeStartEvent = false;
                  this.this$0.getEventListener$okhttp().responseBodyStart(this.this$0.getCall$okhttp());
               }

               if (e == -1L) {
                  this.complete(null);
                  return -1L;
               } else {
                  var newBytesReceived: Long = this.bytesReceived + e;
                  if (this.contentLength != -1L && this.bytesReceived + e > this.contentLength) {
                     throw new ProtocolException("expected " + this.contentLength + " bytes but received " + newBytesReceived);
                  } else {
                     this.bytesReceived = newBytesReceived;
                     if (newBytesReceived == this.contentLength) {
                        this.complete(null);
                     }

                     return e;
                  }
               }
            } catch (IOException var8) {
               throw this.complete(var8);
            }
         }
      }

      @Throws(java/io/IOException::class)
      public open fun close() {
         if (!this.closed) {
            this.closed = true;

            try {
               super.close();
               this.complete(null);
            } catch (IOException var2) {
               throw this.complete(var2);
            }
         }
      }

      public fun <E : IOException?> complete(e: E): E {
         if (this.completed) {
            return (E)e;
         } else {
            this.completed = true;
            if (e == null && this.invokeStartEvent) {
               this.invokeStartEvent = false;
               this.this$0.getEventListener$okhttp().responseBodyStart(this.this$0.getCall$okhttp());
            }

            return (E)this.this$0.bodyComplete(this.bytesReceived, true, false, e);
         }
      }
   }
}
