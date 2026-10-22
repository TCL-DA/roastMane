package okhttp3.internal.http1

import java.io.EOFException
import java.io.IOException
import java.net.ProtocolException
import java.net.Proxy.Type
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.CookieJar
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Response.Builder
import okhttp3.internal.Util
import okhttp3.internal.connection.RealConnection
import okhttp3.internal.http.ExchangeCodec
import okhttp3.internal.http.HttpHeaders
import okhttp3.internal.http.RequestLine
import okhttp3.internal.http.StatusLine
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ForwardingTimeout
import okio.Sink
import okio.Source
import okio.Timeout

@SourceDebugExtension(["SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,505:1\n1#2:506\n*E\n"])
class Http1ExchangeCodec(client: OkHttpClient?, connection: RealConnection, source: BufferedSource, sink: BufferedSink) : ExchangeCodec {
   @JvmStatic
   public Http1ExchangeCodec.Companion Companion = new Http1ExchangeCodec.Companion(null);
   @JvmStatic
   private long NO_CHUNK_YET = -1L;
   @JvmStatic
   private int STATE_IDLE = 0;
   @JvmStatic
   private int STATE_OPEN_REQUEST_BODY = 1;
   @JvmStatic
   private int STATE_WRITING_REQUEST_BODY = 2;
   @JvmStatic
   private int STATE_READ_RESPONSE_HEADERS = 3;
   @JvmStatic
   private int STATE_OPEN_RESPONSE_BODY = 4;
   @JvmStatic
   private int STATE_READING_RESPONSE_BODY = 5;
   @JvmStatic
   private int STATE_CLOSED = 6;

   private final val client: OkHttpClient?
   public open val connection: RealConnection
   private final val headersReader: HeadersReader
   public final val isClosed: Boolean
      public final get() {
         return this.state == 6;
      }

   private final val sink: BufferedSink
   private final val source: BufferedSource
   private final var state: Int
   private final var trailers: Headers?
   private final val isChunked: Boolean
      private final get() {
         return StringsKt.equals("chunked", `$this$isChunked`.header("Transfer-Encoding"), true);
      }

   private final val isChunked: Boolean
      private final get() {
         return StringsKt.equals("chunked", Response.header$default(`$this$isChunked`, "Transfer-Encoding", null, 2, null), true);
      }


   init {
      this.client = client;
      this.connection = connection;
      this.source = source;
      this.sink = sink;
      this.headersReader = new HeadersReader(this.source);
   }

   public override fun createRequestBody(request: Request, contentLength: Long): Sink {
      if (request.body() != null && request.body().isDuplex()) {
         throw new ProtocolException("Duplex connections are not supported for HTTP/1");
      } else {
         var var10000: Sink;
         if (this.isChunked(request)) {
            var10000 = this.newChunkedSink();
         } else {
            if (contentLength == -1L) {
               throw new IllegalStateException("Cannot stream a request body without chunked encoding or a known content length!");
            }

            var10000 = this.newKnownLengthSink();
         }

         return var10000;
      }
   }

   public override fun cancel() {
      this.getConnection().cancel();
   }

   public override fun writeRequestHeaders(request: Request) {
      var var10000: RequestLine = RequestLine.INSTANCE;
      var var10002: Type = this.getConnection().route().proxy().type();
      this.writeRequest(request.headers(), var10000.get(request, var10002));
   }

   public override fun reportedContentLength(response: Response): Long {
      return if (!HttpHeaders.promisesBody(response)) 0L else (if (this.isChunked(response)) -1L else Util.headersContentLength(response));
   }

   public override fun openResponseBodySource(response: Response): Source {
      var var10000: Source;
      if (!HttpHeaders.promisesBody(response)) {
         var10000 = this.newFixedLengthSource(0L);
      } else if (this.isChunked(response)) {
         var10000 = this.newChunkedSource(response.request().url());
      } else {
         var contentLength: Long = Util.headersContentLength(response);
         var10000 = if (contentLength != -1L) this.newFixedLengthSource(contentLength) else this.newUnknownLengthSource();
      }

      return var10000;
   }

   public override fun trailers(): Headers {
      if (this.state != 6) {
         throw new IllegalStateException("too early; can't read the trailers yet".toString());
      } else {
         var var10000: Headers = this.trailers;
         if (this.trailers == null) {
            var10000 = Util.EMPTY_HEADERS;
         }

         return var10000;
      }
   }

   public override fun flushRequest() {
      this.sink.flush();
   }

   public override fun finishRequest() {
      this.sink.flush();
   }

   public fun writeRequest(headers: Headers, requestLine: String) {
      if (this.state != 0) {
         throw new IllegalStateException(("state: " + this.state).toString());
      } else {
         this.sink.writeUtf8(requestLine).writeUtf8("\r\n");
         var i: Int = 0;

         for (int var4 = headers.size(); i < var4; i++) {
            this.sink.writeUtf8(headers.name(i)).writeUtf8(": ").writeUtf8(headers.value(i)).writeUtf8("\r\n");
         }

         this.sink.writeUtf8("\r\n");
         this.state = 1;
      }
   }

   public override fun readResponseHeaders(expectContinue: Boolean): Builder? {
      if (this.state != 1 && this.state != 2 && this.state != 3) {
         throw new IllegalStateException(("state: " + this.state).toString());
      } else {
         try {
            var e: StatusLine = StatusLine.Companion.parse(this.headersReader.readLine());
            var var6: Response.Builder = new Response.Builder().protocol(e.protocol).code(e.code).message(e.message).headers(this.headersReader.readHeaders());
            var var10000: Response.Builder;
            if (expectContinue && e.code == 100) {
               var10000 = null;
            } else if (e.code == 100) {
               this.state = 3;
               var10000 = var6;
            } else if (102 <= e.code && e.code < 200) {
               this.state = 3;
               var10000 = var6;
            } else {
               this.state = 4;
               var10000 = var6;
            }

            return var10000;
         } catch (EOFException var5) {
            throw new IOException("unexpected end of stream on " + this.getConnection().route().address().url().redact(), var5);
         }
      }
   }

   private fun newChunkedSink(): Sink {
      if (this.state != 1) {
         throw new IllegalStateException(("state: " + this.state).toString());
      } else {
         this.state = 2;
         return new Http1ExchangeCodec.ChunkedSink(this);
      }
   }

   private fun newKnownLengthSink(): Sink {
      if (this.state != 1) {
         throw new IllegalStateException(("state: " + this.state).toString());
      } else {
         this.state = 2;
         return new Http1ExchangeCodec.KnownLengthSink(this);
      }
   }

   private fun newFixedLengthSource(length: Long): Source {
      if (this.state != 4) {
         throw new IllegalStateException(("state: " + this.state).toString());
      } else {
         this.state = 5;
         return new Http1ExchangeCodec.FixedLengthSource((long)this, length);
      }
   }

   private fun newChunkedSource(url: HttpUrl): Source {
      if (this.state != 4) {
         throw new IllegalStateException(("state: " + this.state).toString());
      } else {
         this.state = 5;
         return new Http1ExchangeCodec.ChunkedSource(this, url);
      }
   }

   private fun newUnknownLengthSource(): Source {
      if (this.state != 4) {
         throw new IllegalStateException(("state: " + this.state).toString());
      } else {
         this.state = 5;
         this.getConnection().noNewExchanges$okhttp();
         return new Http1ExchangeCodec.UnknownLengthSource(this);
      }
   }

   private fun detachTimeout(timeout: ForwardingTimeout) {
      var oldDelegate: Timeout = timeout.delegate();
      timeout.setDelegate(Timeout.NONE);
      oldDelegate.clearDeadline();
      oldDelegate.clearTimeout();
   }

   public fun skipConnectBody(response: Response) {
      var contentLength: Long = Util.headersContentLength(response);
      if (contentLength != -1L) {
         var body: Source = this.newFixedLengthSource(contentLength);
         Util.skipAll(body, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
         body.close();
      }
   }

   private abstract inner class AbstractSource : Source {
      protected final var closed: Boolean
         internal set
      protected final val timeout: ForwardingTimeout

      open fun AbstractSource(`this$0`: Http1ExchangeCodec) {
         this.this$0 = `this$0`;
         this.timeout = new ForwardingTimeout(Http1ExchangeCodec.access$getSource$p(this.this$0).timeout());
      }

      public open fun timeout(): Timeout {
         return this.timeout as Timeout;
      }

      public open fun read(sink: Buffer, byteCount: Long): Long {
         try {
            return Http1ExchangeCodec.access$getSource$p(this.this$0).read(sink, byteCount);
         } catch (IOException var7) {
            this.this$0.getConnection().noNewExchanges$okhttp();
            this.responseBodyComplete();
            throw var7;
         }
      }

      public fun responseBodyComplete() {
         if (Http1ExchangeCodec.access$getState$p(this.this$0) != 6) {
            if (Http1ExchangeCodec.access$getState$p(this.this$0) != 5) {
               throw new IllegalStateException("state: " + Http1ExchangeCodec.access$getState$p(this.this$0));
            } else {
               Http1ExchangeCodec.access$detachTimeout(this.this$0, this.timeout);
               Http1ExchangeCodec.access$setState$p(this.this$0, 6);
            }
         }
      }
   }

   @SourceDebugExtension(["SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec$ChunkedSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,505:1\n1#2:506\n*E\n"])
   private inner class ChunkedSink : Sink {
      private final var closed: Boolean
      private final val timeout: ForwardingTimeout

      init {
         this.this$0 = `this$0`;
         this.timeout = new ForwardingTimeout(Http1ExchangeCodec.access$getSink$p(this.this$0).timeout());
      }

      public open fun timeout(): Timeout {
         return this.timeout as Timeout;
      }

      public open fun write(source: Buffer, byteCount: Long) {
         if (this.closed) {
            throw new IllegalStateException("closed".toString());
         } else if (byteCount != 0L) {
            Http1ExchangeCodec.access$getSink$p(this.this$0).writeHexadecimalUnsignedLong(byteCount);
            Http1ExchangeCodec.access$getSink$p(this.this$0).writeUtf8("\r\n");
            Http1ExchangeCodec.access$getSink$p(this.this$0).write(source, byteCount);
            Http1ExchangeCodec.access$getSink$p(this.this$0).writeUtf8("\r\n");
         }
      }

      @Synchronized
      public open fun flush() {
         if (!this.closed) {
            Http1ExchangeCodec.access$getSink$p(this.this$0).flush();
         }
      }

      @Synchronized
      public open fun close() {
         if (!this.closed) {
            this.closed = true;
            Http1ExchangeCodec.access$getSink$p(this.this$0).writeUtf8("0\r\n\r\n");
            Http1ExchangeCodec.access$detachTimeout(this.this$0, this.timeout);
            Http1ExchangeCodec.access$setState$p(this.this$0, 3);
         }
      }
   }

   @SourceDebugExtension(["SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec$ChunkedSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,505:1\n1#2:506\n*E\n"])
   private inner class ChunkedSource(url: HttpUrl) : Http1ExchangeCodec.AbstractSource(`this$0`) {
      private final var bytesRemainingInChunk: Long
      private final var hasMoreChunks: Boolean
      private final val url: HttpUrl

      init {
         this.this$0 = `this$0`;
         this.url = url;
         this.bytesRemainingInChunk = -1L;
         this.hasMoreChunks = true;
      }

      public override fun read(sink: Buffer, byteCount: Long): Long {
         if (byteCount < 0L) {
            throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
         } else if (this.getClosed()) {
            throw new IllegalStateException("closed".toString());
         } else if (!this.hasMoreChunks) {
            return -1L;
         } else {
            if (this.bytesRemainingInChunk == 0L || this.bytesRemainingInChunk == -1L) {
               this.readChunkSize();
               if (!this.hasMoreChunks) {
                  return -1L;
               }
            }

            var read: Long = super.read(sink, Math.min(byteCount, this.bytesRemainingInChunk));
            if (read == -1L) {
               this.this$0.getConnection().noNewExchanges$okhttp();
               var e: ProtocolException = new ProtocolException("unexpected end of stream");
               this.responseBodyComplete();
               throw e;
            } else {
               this.bytesRemainingInChunk -= read;
               return read;
            }
         }
      }

      private fun readChunkSize() {
         if (this.bytesRemainingInChunk != -1L) {
            Http1ExchangeCodec.access$getSource$p(this.this$0).readUtf8LineStrict();
         }

         try {
            this.bytesRemainingInChunk = Http1ExchangeCodec.access$getSource$p(this.this$0).readHexadecimalUnsignedLong();
            var e: java.lang.String = StringsKt.trim(Http1ExchangeCodec.access$getSource$p(this.this$0).readUtf8LineStrict()).toString();
            if (this.bytesRemainingInChunk < 0L || e.length() > 0 && !StringsKt.startsWith$default(e, ";", false, 2, null)) {
               throw new ProtocolException("expected chunk size and optional extensions but was \"" + this.bytesRemainingInChunk + e + '"');
            }
         } catch (NumberFormatException var2) {
            throw new ProtocolException(var2.getMessage());
         }

         if (this.bytesRemainingInChunk == 0L) {
            this.hasMoreChunks = false;
            Http1ExchangeCodec.access$setTrailers$p(this.this$0, Http1ExchangeCodec.access$getHeadersReader$p(this.this$0).readHeaders());
            var var10000: OkHttpClient = Http1ExchangeCodec.access$getClient$p(this.this$0);
            var var3: CookieJar = var10000.cookieJar();
            var var10001: HttpUrl = this.url;
            var var10002: Headers = Http1ExchangeCodec.access$getTrailers$p(this.this$0);
            HttpHeaders.receiveHeaders(var3, var10001, var10002);
            this.responseBodyComplete();
         }
      }

      public open fun close() {
         if (!this.getClosed()) {
            if (this.hasMoreChunks && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
               this.this$0.getConnection().noNewExchanges$okhttp();
               this.responseBodyComplete();
            }

            this.setClosed(true);
         }
      }
   }

   companion object Companion private constructor() {
      private const val NO_CHUNK_YET: Long
      private const val STATE_CLOSED: Int
      private const val STATE_IDLE: Int
      private const val STATE_OPEN_REQUEST_BODY: Int
      private const val STATE_OPEN_RESPONSE_BODY: Int
      private const val STATE_READING_RESPONSE_BODY: Int
      private const val STATE_READ_RESPONSE_HEADERS: Int
      private const val STATE_WRITING_REQUEST_BODY: Int

   }

   @SourceDebugExtension(["SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec$FixedLengthSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,505:1\n1#2:506\n*E\n"])
   private inner class FixedLengthSource(bytesRemaining: Long) : Http1ExchangeCodec.AbstractSource(`this$0`) {
      private final var bytesRemaining: Long

      init {
         this.this$0 = `this$0`;
         this.bytesRemaining = bytesRemaining;
         if (this.bytesRemaining == 0L) {
            this.responseBodyComplete();
         }
      }

      public override fun read(sink: Buffer, byteCount: Long): Long {
         if (byteCount < 0L) {
            throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
         } else if (this.getClosed()) {
            throw new IllegalStateException("closed".toString());
         } else if (this.bytesRemaining == 0L) {
            return -1L;
         } else {
            var read: Long = super.read(sink, Math.min(this.bytesRemaining, byteCount));
            if (read == -1L) {
               this.this$0.getConnection().noNewExchanges$okhttp();
               var e: ProtocolException = new ProtocolException("unexpected end of stream");
               this.responseBodyComplete();
               throw e;
            } else {
               this.bytesRemaining -= read;
               if (this.bytesRemaining == 0L) {
                  this.responseBodyComplete();
               }

               return read;
            }
         }
      }

      public open fun close() {
         if (!this.getClosed()) {
            if (this.bytesRemaining != 0L && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
               this.this$0.getConnection().noNewExchanges$okhttp();
               this.responseBodyComplete();
            }

            this.setClosed(true);
         }
      }
   }

   @SourceDebugExtension(["SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec$KnownLengthSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,505:1\n1#2:506\n*E\n"])
   private inner class KnownLengthSink : Sink {
      private final var closed: Boolean
      private final val timeout: ForwardingTimeout

      init {
         this.this$0 = `this$0`;
         this.timeout = new ForwardingTimeout(Http1ExchangeCodec.access$getSink$p(this.this$0).timeout());
      }

      public open fun timeout(): Timeout {
         return this.timeout as Timeout;
      }

      public open fun write(source: Buffer, byteCount: Long) {
         if (this.closed) {
            throw new IllegalStateException("closed".toString());
         } else {
            Util.checkOffsetAndCount(source.size(), 0L, byteCount);
            Http1ExchangeCodec.access$getSink$p(this.this$0).write(source, byteCount);
         }
      }

      public open fun flush() {
         if (!this.closed) {
            Http1ExchangeCodec.access$getSink$p(this.this$0).flush();
         }
      }

      public open fun close() {
         if (!this.closed) {
            this.closed = true;
            Http1ExchangeCodec.access$detachTimeout(this.this$0, this.timeout);
            Http1ExchangeCodec.access$setState$p(this.this$0, 3);
         }
      }
   }

   @SourceDebugExtension(["SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec$UnknownLengthSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,505:1\n1#2:506\n*E\n"])
   private inner class UnknownLengthSource : Http1ExchangeCodec.AbstractSource(`this$0`) {
      private final var inputExhausted: Boolean

      init {
         this.this$0 = `this$0`;
      }

      public override fun read(sink: Buffer, byteCount: Long): Long {
         if (byteCount < 0L) {
            throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
         } else if (this.getClosed()) {
            throw new IllegalStateException("closed".toString());
         } else if (this.inputExhausted) {
            return -1L;
         } else {
            var read: Long = super.read(sink, byteCount);
            if (read == -1L) {
               this.inputExhausted = true;
               this.responseBodyComplete();
               return -1L;
            } else {
               return read;
            }
         }
      }

      public open fun close() {
         if (!this.getClosed()) {
            if (!this.inputExhausted) {
               this.responseBodyComplete();
            }

            this.setClosed(true);
         }
      }
   }
}
