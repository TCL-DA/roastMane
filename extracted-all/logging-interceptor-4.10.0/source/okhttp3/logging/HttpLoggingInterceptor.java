package okhttp3.logging

import java.io.Closeable
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.TreeSet
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.StringCompanionObject
import okhttp3.Connection
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.Interceptor.Chain
import okhttp3.internal.http.HttpHeaders
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.GzipSource
import okio.Source

class HttpLoggingInterceptor @JvmOverloads  public constructor(logger: okhttp3.logging.HttpLoggingInterceptor.Logger) : Interceptor {
   private final var headersToRedact: Set<String>
   public final var level: okhttp3.logging.HttpLoggingInterceptor.Level
      internal final set(<set-?>) {
         this.level = var1;
      }

   private final val logger: okhttp3.logging.HttpLoggingInterceptor.Logger

   init {
      this.logger = logger;
      this.headersToRedact = SetsKt.emptySet();
      this.level = HttpLoggingInterceptor.Level.NONE;
   }

   public fun redactHeader(name: String) {
      var newHeadersToRedact: TreeSet = new TreeSet(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE));
      CollectionsKt.addAll(newHeadersToRedact, this.headersToRedact);
      newHeadersToRedact.add(name);
      this.headersToRedact = newHeadersToRedact;
   }

   public fun setLevel(level: okhttp3.logging.HttpLoggingInterceptor.Level): HttpLoggingInterceptor {
      this.level(level);
      return this;
   }

   @Deprecated(message = "moved to var", replaceWith = @ReplaceWith(expression = "level", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_level")
   public fun getLevel(): okhttp3.logging.HttpLoggingInterceptor.Level {
      return this.level;
   }

   @Throws(java/io/IOException::class)
   public open fun intercept(chain: Chain): Response {
      var level: HttpLoggingInterceptor.Level = this.level;
      var request: Request = chain.request();
      if (level === HttpLoggingInterceptor.Level.NONE) {
         return chain.proceed(request);
      } else {
         label284: {
            var logBody: Boolean = level === HttpLoggingInterceptor.Level.BODY;
            var logHeaders: Boolean = level === HttpLoggingInterceptor.Level.BODY || level === HttpLoggingInterceptor.Level.HEADERS;
            var requestBody: RequestBody = request.body();
            var connection: Connection = chain.connection();
            var requestStartMessage: java.lang.String = "--> "
               + request.method()
               + ' '
               + request.url()
               + (if (connection != null) Intrinsics.stringPlus(" ", connection.protocol()) else "");
            if (!logHeaders && requestBody != null) {
               requestStartMessage = requestStartMessage + " (" + requestBody.contentLength() + "-byte body)";
            }

            this.logger.log(requestStartMessage);
            if (logHeaders) {
               var startNs: Headers = request.headers();
               if (requestBody != null) {
                  var var10000: MediaType = requestBody.contentType();
                  if (var10000 != null) {
                     if (startNs.get("Content-Type") == null) {
                        this.logger.log(Intrinsics.stringPlus("Content-Type: ", var10000));
                     }
                  }

                  if (requestBody.contentLength() != -1L && startNs.get("Content-Length") == null) {
                     this.logger.log(Intrinsics.stringPlus("Content-Length: ", requestBody.contentLength()));
                  }
               }

               var buffer: Int = 0;
               var response: Int = startNs.size();

               while (buffer < response) {
                  this.logHeader(startNs, buffer++);
               }

               if (!logBody || requestBody == null) {
                  this.logger.log(Intrinsics.stringPlus("--> END ", request.method()));
               } else if (this.bodyHasUnknownEncoding(request.headers())) {
                  this.logger.log("--> END " + request.method() + " (encoded body omitted)");
               } else if (requestBody.isDuplex()) {
                  this.logger.log("--> END " + request.method() + " (duplex request body omitted)");
               } else if (requestBody.isOneShot()) {
                  this.logger.log("--> END " + request.method() + " (one-shot body omitted)");
               } else {
                  var var34: Buffer = new Buffer();
                  requestBody.writeTo(var34 as BufferedSink);
                  var var35: MediaType = requestBody.contentType();
                  var var52: Charset = if (var35 == null) null else var35.charset(StandardCharsets.UTF_8);
                  if (var52 == null) {
                     var responseBody: Charset = StandardCharsets.UTF_8;
                     var52 = responseBody;
                  }

                  this.logger.log("");
                  if (Utf8Kt.isProbablyUtf8(var34)) {
                     this.logger.log(var34.readString(var52));
                     this.logger.log("--> END " + request.method() + " (" + requestBody.contentLength() + "-byte body)");
                  } else {
                     this.logger.log("--> END " + request.method() + " (binary " + requestBody.contentLength() + "-byte body omitted)");
                  }
               }
            }

            var var37: Response;
            try {
               var37 = chain.proceed(request);
            } catch (Exception var29) {
               this.logger.log(Intrinsics.stringPlus("<-- HTTP FAILED: ", var29));
               throw var29;
            }

            var var40: Long = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - System.nanoTime());
            var var53: ResponseBody = var37.body();
            var contentLength: Long = var53.contentLength();
            this.logger
               .log(
                  "<-- "
                     + var37.code()
                     + (if (var37.message().length() == 0) "" else 32 + var37.message())
                     + ' '
                     + var37.request().url()
                     + " ("
                     + var40
                     + "ms"
                     + (if (!logHeaders) ", " + (if (contentLength != -1L) contentLength + "-byte" else "unknown-length") + " body" else "")
                     + ')'
               );
            if (logHeaders) {
               var headersx: Headers = var37.headers();
               var var43: Int = 0;
               var buffer: Int = headersx.size();

               while (var43 < buffer) {
                  this.logHeader(headersx, var43++);
               }

               if (!logBody || !HttpHeaders.promisesBody(var37)) {
                  this.logger.log("<-- END HTTP");
               } else if (this.bodyHasUnknownEncoding(var37.headers())) {
                  this.logger.log("<-- END HTTP (encoded body omitted)");
               } else {
                  var var44: BufferedSource = var53.source();
                  var44.request(java.lang.Long.MAX_VALUE);
                  var var46: Buffer = var44.getBuffer();
                  var var47: java.lang.Long = null;
                  if (StringsKt.equals("gzip", headersx.get("Content-Encoding"), true)) {
                     var47 = var46.size();
                     var contentType: Closeable = (new GzipSource(var46.clone() as Source)) as Closeable;
                     var charset: java.lang.Throwable = null;

                     try {
                        try {
                           var gzippedResponseBody: GzipSource = contentType as GzipSource;
                           var46 = new Buffer();
                           var var50: Long = var46.writeAll(gzippedResponseBody as Source);
                        } catch (java.lang.Throwable var27) {
                           charset = var27;
                           throw var27;
                        }
                     } catch (java.lang.Throwable var28) {
                        CloseableKt.closeFinally(contentType, charset);
                     }

                     CloseableKt.closeFinally(contentType, null);
                  }

                  var contentTypex: MediaType = var53.contentType();
                  var var55: Charset = if (contentTypex == null) null else contentTypex.charset(StandardCharsets.UTF_8);
                  if (var55 == null) {
                     var var51: Charset = StandardCharsets.UTF_8;
                     var55 = var51;
                  }

                  if (!Utf8Kt.isProbablyUtf8(var46)) {
                     this.logger.log("");
                     this.logger.log("<-- END HTTP (binary " + var46.size() + "-byte body omitted)");
                     return var37;
                  }

                  if (contentLength != 0L) {
                     this.logger.log("");
                     this.logger.log(var46.clone().readString(var55));
                  }

                  if (var47 != null) {
                     this.logger.log("<-- END HTTP (" + var46.size() + "-byte, " + var47 + "-gzipped-byte body)");
                  } else {
                     this.logger.log("<-- END HTTP (" + var46.size() + "-byte body)");
                  }
               }
            }

            return var37;
         }
      }
   }

   private fun logHeader(headers: Headers, i: Int) {
      this.logger.log(headers.name(i) + ": " + (if (this.headersToRedact.contains(headers.name(i))) "██" else headers.value(i)));
   }

   private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
      var var10000: java.lang.String = headers.get("Content-Encoding");
      if (var10000 == null) {
         return false;
      } else {
         return !StringsKt.equals(var10000, "identity", true) && !StringsKt.equals(var10000, "gzip", true);
      }
   }

   @JvmOverloads
   fun HttpLoggingInterceptor() {
      this(null, 1, null);
   }

   enum class Level {
      @JvmStatic
      NONE,
      @JvmStatic
      BASIC,
      @JvmStatic
      HEADERS,
      @JvmStatic
      BODY;

   }

   fun interface Logger {
      @JvmStatic
      HttpLoggingInterceptor.Logger.Companion Companion = HttpLoggingInterceptor.Logger.Companion.$$INSTANCE;
      @JvmField
      @JvmStatic
      HttpLoggingInterceptor.Logger DEFAULT = new HttpLoggingInterceptor.Logger.Companion.DefaultLogger();

      public abstract fun log(message: String) {
      }

      companion object Companion private constructor() {
         public final val DEFAULT: okhttp3.logging.HttpLoggingInterceptor.Logger


         private class DefaultLogger : HttpLoggingInterceptor.Logger {
            public override fun log(message: String) {
               Platform.log$default(Platform.Companion.get(), message, 0, null, 6, null);
            }
         }
      }
   }
}
