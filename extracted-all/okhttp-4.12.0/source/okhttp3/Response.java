package okhttp3

import java.io.Closeable
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.connection.Exchange
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import okio.BufferedSource
import okio.Source

@SourceDebugExtension(["SMAP\nResponse.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Response.kt\nokhttp3/Response\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,455:1\n1#2:456\n*E\n"])
class Response internal constructor(request: Request,
      protocol: Protocol,
      message: String,
      code: Int,
      handshake: Handshake?,
      headers: Headers,
      body: ResponseBody?,
      networkResponse: Response?,
      cacheResponse: Response?,
      priorResponse: Response?,
      sentRequestAtMillis: Long,
      receivedResponseAtMillis: Long,
      exchange: Exchange?
   ) :
   Closeable {
   public final val body: ResponseBody?
   public final val cacheControl: CacheControl
      public final get() {
         var result: CacheControl = this.lazyCacheControl;
         if (this.lazyCacheControl == null) {
            result = CacheControl.Companion.parse(this.headers);
            this.lazyCacheControl = result;
         }

         return result;
      }

   public final val cacheResponse: Response?
   public final val code: Int
   internal final val exchange: Exchange?
   public final val handshake: Handshake?
   public final val headers: Headers
   public final val isRedirect: Boolean
      public final get() {
         var var10000: Boolean;
         switch (this.code) {
            case 300:
            case 301:
            case 302:
            case 303:
            case 307:
            case 308:
               var10000 = true;
               break;
            case 304:
            case 305:
            case 306:
            default:
               var10000 = false;
         }

         return var10000;
      }

   public final val isSuccessful: Boolean
      public final get() {
         return 200 <= this.code && this.code < 300;
      }

   private final var lazyCacheControl: CacheControl?
   public final val message: String
   public final val networkResponse: Response?
   public final val priorResponse: Response?
   public final val protocol: Protocol
   public final val receivedResponseAtMillis: Long
   public final val request: Request
   public final val sentRequestAtMillis: Long

   init {
      this.request = request;
      this.protocol = protocol;
      this.message = message;
      this.code = code;
      this.handshake = handshake;
      this.headers = headers;
      this.body = body;
      this.networkResponse = networkResponse;
      this.cacheResponse = cacheResponse;
      this.priorResponse = priorResponse;
      this.sentRequestAtMillis = sentRequestAtMillis;
      this.receivedResponseAtMillis = receivedResponseAtMillis;
      this.exchange = exchange;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "request", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_request")
   public fun request(): Request {
      return this.request;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "protocol", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_protocol")
   public fun protocol(): Protocol {
      return this.protocol;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "code", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_code")
   public fun code(): Int {
      return this.code;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "message", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_message")
   public fun message(): String {
      return this.message;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "handshake", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_handshake")
   public fun handshake(): Handshake? {
      return this.handshake;
   }

   public fun headers(name: String): List<String> {
      return this.headers.values(name);
   }

   @JvmOverloads
   public fun header(name: String, defaultValue: String?): String? {
      var var10000: java.lang.String = this.headers.get(name);
      if (var10000 == null) {
         var10000 = defaultValue;
      }

      return var10000;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "headers", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_headers")
   public fun headers(): Headers {
      return this.headers;
   }

   @Throws(java/io/IOException::class)
   public fun trailers(): Headers {
      if (this.exchange == null) {
         throw new IllegalStateException("trailers not available".toString());
      } else {
         return this.exchange.trailers();
      }
   }

   @Throws(java/io/IOException::class)
   public fun peekBody(byteCount: Long): ResponseBody {
      var var10000: ResponseBody = this.body;
      var peeked: BufferedSource = var10000.source().peek();
      var buffer: Buffer = new Buffer();
      peeked.request(byteCount);
      buffer.write(peeked as Source, Math.min(byteCount, peeked.getBuffer().size()));
      return ResponseBody.Companion.create(buffer as BufferedSource, this.body.contentType(), buffer.size());
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "body", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_body")
   public fun body(): ResponseBody? {
      return this.body;
   }

   public fun newBuilder(): okhttp3.Response.Builder {
      return new Response.Builder(this);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "networkResponse", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_networkResponse")
   public fun networkResponse(): Response? {
      return this.networkResponse;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "cacheResponse", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_cacheResponse")
   public fun cacheResponse(): Response? {
      return this.cacheResponse;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "priorResponse", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_priorResponse")
   public fun priorResponse(): Response? {
      return this.priorResponse;
   }

   public fun challenges(): List<Challenge> {
      var var10001: java.lang.String;
      switch (this.code) {
         case 401:
            var10001 = "WWW-Authenticate";
            break;
         case 407:
            var10001 = "Proxy-Authenticate";
            break;
         default:
            return CollectionsKt.emptyList();
      }

      return HttpHeaders.parseChallenges(this.headers, var10001);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "cacheControl", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_cacheControl")
   public fun cacheControl(): CacheControl {
      return this.cacheControl();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "sentRequestAtMillis", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_sentRequestAtMillis")
   public fun sentRequestAtMillis(): Long {
      return this.sentRequestAtMillis;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "receivedResponseAtMillis", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_receivedResponseAtMillis")
   public fun receivedResponseAtMillis(): Long {
      return this.receivedResponseAtMillis;
   }

   public override fun close() {
      if (this.body == null) {
         throw new IllegalStateException("response is not eligible for a body and must not be closed".toString());
      } else {
         this.body.close();
      }
   }

   public override fun toString(): String {
      return "Response{protocol=" + this.protocol + ", code=" + this.code + ", message=" + this.message + ", url=" + this.request.url() + '}';
   }

   @JvmOverloads
   fun header(name: java.lang.String): java.lang.String? {
      return header$default(this, name, null, 2, null);
   }

   @SourceDebugExtension(["SMAP\nResponse.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Response.kt\nokhttp3/Response$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,455:1\n1#2:456\n*E\n"])
   open class Builder {
      internal final var body: ResponseBody?
      internal final var cacheResponse: Response?
      internal final var code: Int = -1
      internal final var exchange: Exchange?
      internal final var handshake: Handshake?
      internal final var headers: Headers.Builder
      internal final var message: String?
      internal final var networkResponse: Response?
      internal final var priorResponse: Response?
      internal final var protocol: Protocol?
      internal final var receivedResponseAtMillis: Long
      internal final var request: Request?
      internal final var sentRequestAtMillis: Long

      public constructor()  {
         this.headers = new Headers.Builder();
      }

      internal constructor(response: Response)  {
         this.request = response.request();
         this.protocol = response.protocol();
         this.code = response.code();
         this.message = response.message();
         this.handshake = response.handshake();
         this.headers = response.headers().newBuilder();
         this.body = response.body();
         this.networkResponse = response.networkResponse();
         this.cacheResponse = response.cacheResponse();
         this.priorResponse = response.priorResponse();
         this.sentRequestAtMillis = response.sentRequestAtMillis();
         this.receivedResponseAtMillis = response.receivedResponseAtMillis();
         this.exchange = response.exchange();
      }

      public open fun request(request: Request): okhttp3.Response.Builder {
         this.request = request;
         return this;
      }

      public open fun protocol(protocol: Protocol): okhttp3.Response.Builder {
         this.protocol = protocol;
         return this;
      }

      public open fun code(code: Int): okhttp3.Response.Builder {
         this.code = code;
         return this;
      }

      public open fun message(message: String): okhttp3.Response.Builder {
         this.message = message;
         return this;
      }

      public open fun handshake(handshake: Handshake?): okhttp3.Response.Builder {
         this.handshake = handshake;
         return this;
      }

      public open fun header(name: String, value: String): okhttp3.Response.Builder {
         this.headers.set(name, value);
         return this;
      }

      public open fun addHeader(name: String, value: String): okhttp3.Response.Builder {
         this.headers.add(name, value);
         return this;
      }

      public open fun removeHeader(name: String): okhttp3.Response.Builder {
         this.headers.removeAll(name);
         return this;
      }

      public open fun headers(headers: Headers): okhttp3.Response.Builder {
         this.headers = headers.newBuilder();
         return this;
      }

      public open fun body(body: ResponseBody?): okhttp3.Response.Builder {
         this.body = body;
         return this;
      }

      public open fun networkResponse(networkResponse: Response?): okhttp3.Response.Builder {
         var `$this$networkResponse_u24lambda_u2410`: Response.Builder = this;
         this.checkSupportResponse("networkResponse", networkResponse);
         `$this$networkResponse_u24lambda_u2410`.networkResponse = networkResponse;
         return this;
      }

      public open fun cacheResponse(cacheResponse: Response?): okhttp3.Response.Builder {
         var `$this$cacheResponse_u24lambda_u2411`: Response.Builder = this;
         this.checkSupportResponse("cacheResponse", cacheResponse);
         `$this$cacheResponse_u24lambda_u2411`.cacheResponse = cacheResponse;
         return this;
      }

      private fun checkSupportResponse(name: String, response: Response?) {
         if (response != null) {
            if (response.body() != null) {
               throw new IllegalArgumentException((name + ".body != null").toString());
            }

            if (response.networkResponse() != null) {
               throw new IllegalArgumentException((name + ".networkResponse != null").toString());
            }

            if (response.cacheResponse() != null) {
               throw new IllegalArgumentException((name + ".cacheResponse != null").toString());
            }

            if (response.priorResponse() != null) {
               throw new IllegalArgumentException((name + ".priorResponse != null").toString());
            }
         }
      }

      public open fun priorResponse(priorResponse: Response?): okhttp3.Response.Builder {
         var `$this$priorResponse_u24lambda_u2417`: Response.Builder = this;
         this.checkPriorResponse(priorResponse);
         `$this$priorResponse_u24lambda_u2417`.priorResponse = priorResponse;
         return this;
      }

      private fun checkPriorResponse(response: Response?) {
         if (response != null) {
            if (response.body() != null) {
               throw new IllegalArgumentException("priorResponse.body != null".toString());
            }
         }
      }

      public open fun sentRequestAtMillis(sentRequestAtMillis: Long): okhttp3.Response.Builder {
         this.sentRequestAtMillis = sentRequestAtMillis;
         return this;
      }

      public open fun receivedResponseAtMillis(receivedResponseAtMillis: Long): okhttp3.Response.Builder {
         this.receivedResponseAtMillis = receivedResponseAtMillis;
         return this;
      }

      internal fun initExchange(deferredTrailers: Exchange) {
         this.exchange = deferredTrailers;
      }

      public open fun build(): Response {
         if (this.code < 0) {
            throw new IllegalStateException(("code < 0: " + this.code).toString());
         } else if (this.request == null) {
            throw new IllegalStateException("request == null".toString());
         } else if (this.protocol == null) {
            var var26: Request = this.request;
            throw new IllegalStateException("protocol == null".toString());
         } else if (this.message == null) {
            var var3: Protocol = this.protocol;
            var var2: Request = this.request;
            throw new IllegalStateException("message == null".toString());
         } else {
            return new Response(
               this.request,
               this.protocol,
               this.message,
               this.code,
               this.handshake,
               this.headers.build(),
               this.body,
               this.networkResponse,
               this.cacheResponse,
               this.priorResponse,
               this.sentRequestAtMillis,
               this.receivedResponseAtMillis,
               this.exchange
            );
         }
      }
   }
}
