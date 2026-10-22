package okhttp3.internal.http2

import java.io.IOException
import java.net.ProtocolException
import java.util.ArrayList
import java.util.Locale
import java.util.concurrent.TimeUnit
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.Response.Builder
import okhttp3.internal.Util
import okhttp3.internal.connection.RealConnection
import okhttp3.internal.http.ExchangeCodec
import okhttp3.internal.http.HttpHeaders
import okhttp3.internal.http.RealInterceptorChain
import okhttp3.internal.http.RequestLine
import okhttp3.internal.http.StatusLine
import okio.Sink
import okio.Source

class Http2ExchangeCodec(client: OkHttpClient, connection: RealConnection, chain: RealInterceptorChain, http2Connection: Http2Connection) : ExchangeCodec {
   @JvmStatic
   public Http2ExchangeCodec.Companion Companion = new Http2ExchangeCodec.Companion(null);
   @JvmStatic
   private java.lang.String CONNECTION = "connection";
   @JvmStatic
   private java.lang.String HOST = "host";
   @JvmStatic
   private java.lang.String KEEP_ALIVE = "keep-alive";
   @JvmStatic
   private java.lang.String PROXY_CONNECTION = "proxy-connection";
   @JvmStatic
   private java.lang.String TRANSFER_ENCODING = "transfer-encoding";
   @JvmStatic
   private java.lang.String TE = "te";
   @JvmStatic
   private java.lang.String ENCODING = "encoding";
   @JvmStatic
   private java.lang.String UPGRADE = "upgrade";
   @JvmStatic
   private java.util.List<java.lang.String> HTTP_2_SKIPPED_REQUEST_HEADERS = Util.immutableListOf(
      "connection", "host", "keep-alive", "proxy-connection", "te", "transfer-encoding", "encoding", "upgrade", ":method", ":path", ":scheme", ":authority"
   );
   @JvmStatic
   private java.util.List<java.lang.String> HTTP_2_SKIPPED_RESPONSE_HEADERS = Util.immutableListOf(
      "connection", "host", "keep-alive", "proxy-connection", "te", "transfer-encoding", "encoding", "upgrade"
   );

   private final var canceled: Boolean
   private final val chain: RealInterceptorChain
   public open val connection: RealConnection
   private final val http2Connection: Http2Connection
   private final val protocol: Protocol
   private final var stream: Http2Stream?

   init {
      this.connection = connection;
      this.chain = chain;
      this.http2Connection = http2Connection;
      this.protocol = if (client.protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) Protocol.H2_PRIOR_KNOWLEDGE else Protocol.HTTP_2;
   }

   public override fun createRequestBody(request: Request, contentLength: Long): Sink {
      var var10000: Http2Stream = this.stream;
      return var10000.getSink();
   }

   public override fun writeRequestHeaders(request: Request) {
      if (this.stream == null) {
         this.stream = this.http2Connection.newStream(Companion.http2HeadersList(request), request.body() != null);
         if (this.canceled) {
            var var5: Http2Stream = this.stream;
            var5.closeLater(ErrorCode.CANCEL);
            throw new IOException("Canceled");
         } else {
            var var10000: Http2Stream = this.stream;
            var10000.readTimeout().timeout((long)this.chain.getReadTimeoutMillis$okhttp(), TimeUnit.MILLISECONDS);
            var10000 = this.stream;
            var10000.writeTimeout().timeout((long)this.chain.getWriteTimeoutMillis$okhttp(), TimeUnit.MILLISECONDS);
         }
      }
   }

   public override fun flushRequest() {
      this.http2Connection.flush();
   }

   public override fun finishRequest() {
      var var10000: Http2Stream = this.stream;
      var10000.getSink().close();
   }

   public override fun readResponseHeaders(expectContinue: Boolean): Builder? {
      if (this.stream == null) {
         throw new IOException("stream wasn't created");
      } else {
         var responseBuilder: Response.Builder = Companion.readHttp2HeadersList(this.stream.takeHeaders(), this.protocol);
         return if (expectContinue && responseBuilder.getCode$okhttp() == 100) null else responseBuilder;
      }
   }

   public override fun reportedContentLength(response: Response): Long {
      return if (!HttpHeaders.promisesBody(response)) 0L else Util.headersContentLength(response);
   }

   public override fun openResponseBodySource(response: Response): Source {
      var var10000: Http2Stream = this.stream;
      return var10000.getSource$okhttp();
   }

   public override fun trailers(): Headers {
      var var10000: Http2Stream = this.stream;
      return var10000.trailers();
   }

   public override fun cancel() {
      this.canceled = true;
      if (this.stream != null) {
         this.stream.closeLater(ErrorCode.CANCEL);
      }
   }

   companion object Companion private constructor() {
      private const val CONNECTION: String
      private const val ENCODING: String
      private const val HOST: String
      private final val HTTP_2_SKIPPED_REQUEST_HEADERS: List<String>
      private final val HTTP_2_SKIPPED_RESPONSE_HEADERS: List<String>
      private const val KEEP_ALIVE: String
      private const val PROXY_CONNECTION: String
      private const val TE: String
      private const val TRANSFER_ENCODING: String
      private const val UPGRADE: String


      public fun http2HeadersList(request: Request): List<Header> {
         var headers: Headers = request.headers();
         var result: ArrayList = new ArrayList(headers.size() + 4);
         result.add(new Header(Header.TARGET_METHOD, request.method()));
         result.add(new Header(Header.TARGET_PATH, RequestLine.INSTANCE.requestPath(request.url())));
         var host: java.lang.String = request.header("Host");
         if (host != null) {
            result.add(new Header(Header.TARGET_AUTHORITY, host));
         }

         result.add(new Header(Header.TARGET_SCHEME, request.url().scheme()));
         var i: Int = 0;

         for (int var6 = headers.size(); i < var6; i++) {
            var var8: java.lang.String = headers.name(i);
            var var10000: Locale = Locale.US;
            var var9: java.lang.String = var8.toLowerCase(var10000);
            if (!Http2ExchangeCodec.access$getHTTP_2_SKIPPED_REQUEST_HEADERS$cp().contains(var9) || var9 == "te" && headers.value(i) == "trailers") {
               result.add(new Header(var9, headers.value(i)));
            }
         }

         return result;
      }

      public fun readHttp2HeadersList(headerBlock: Headers, protocol: Protocol): Builder {
         var statusLine: StatusLine = null;
         var headersBuilder: Headers.Builder = new Headers.Builder();
         var i: Int = 0;

         for (int var6 = headerBlock.size(); i < var6; i++) {
            var name: java.lang.String = headerBlock.name(i);
            var value: java.lang.String = headerBlock.value(i);
            if (name == ":status") {
               statusLine = StatusLine.Companion.parse("HTTP/1.1 " + value);
            } else if (!Http2ExchangeCodec.access$getHTTP_2_SKIPPED_RESPONSE_HEADERS$cp().contains(name)) {
               headersBuilder.addLenient$okhttp(name, value);
            }
         }

         if (statusLine == null) {
            throw new ProtocolException("Expected ':status' header not present");
         } else {
            return new Response.Builder().protocol(protocol).code(statusLine.code).message(statusLine.message).headers(headersBuilder.build());
         }
      }
   }
}
