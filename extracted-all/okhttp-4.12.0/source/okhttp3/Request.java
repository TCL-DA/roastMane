package okhttp3

import java.net.URL
import java.util.LinkedHashMap
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util
import okhttp3.internal.http.HttpMethod

@SourceDebugExtension(["SMAP\nRequest.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Request.kt\nokhttp3/Request\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,298:1\n1864#2,3:299\n*S KotlinDebug\n*F\n+ 1 Request.kt\nokhttp3/Request\n*L\n119#1:299,3\n*E\n"])
class Request internal constructor(url: HttpUrl, method: String, headers: Headers, body: RequestBody?, tags: Map<Class<*>, Any>) {
   public final val body: RequestBody?
   public final val cacheControl: CacheControl
      public final get() {
         var result: CacheControl = this.lazyCacheControl;
         if (this.lazyCacheControl == null) {
            result = CacheControl.Companion.parse(this.headers);
            this.lazyCacheControl = result;
         }

         return result;
      }

   public final val headers: Headers
   public final val isHttps: Boolean
      public final get() {
         return this.url.isHttps();
      }

   private final var lazyCacheControl: CacheControl?
   public final val method: String
   internal final val tags: Map<Class<*>, Any>
   public final val url: HttpUrl

   init {
      this.url = url;
      this.method = method;
      this.headers = headers;
      this.body = body;
      this.tags = tags;
   }

   public fun header(name: String): String? {
      return this.headers.get(name);
   }

   public fun headers(name: String): List<String> {
      return this.headers.values(name);
   }

   public fun tag(): Any? {
      return this.tag(Object.class);
   }

   public fun <T> tag(type: Class<out T>): T? {
      return (T)type.cast(this.tags.get(type));
   }

   public fun newBuilder(): okhttp3.Request.Builder {
      return new Request.Builder(this);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "url", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_url")
   public fun url(): HttpUrl {
      return this.url;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "method", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_method")
   public fun method(): String {
      return this.method;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "headers", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_headers")
   public fun headers(): Headers {
      return this.headers;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "body", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_body")
   public fun body(): RequestBody? {
      return this.body;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "cacheControl", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_cacheControl")
   public fun cacheControl(): CacheControl {
      return this.cacheControl();
   }

   public override fun toString(): String {
      var var1: StringBuilder = new StringBuilder();
      var `$this$toString_u24lambda_u241`: StringBuilder = var1;
      var1.append("Request{method=");
      var1.append(this.method);
      var1.append(", url=");
      var1.append(this.url);
      if (this.headers.size() != 0) {
         var1.append(", headers=[");
         var `$this$forEachIndexed$iv`: java.lang.Iterable = this.headers;
         var `index$iv`: Int = 0;

         for (Object item$iv : $this$forEachIndexed$iv) {
            var var9: Int = `index$iv`++;
            if (var9 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            var name: java.lang.String = (`item$iv` as Pair).component1() as java.lang.String;
            var value: java.lang.String = (`item$iv` as Pair).component2() as java.lang.String;
            if (var9 > 0) {
               `$this$toString_u24lambda_u241`.append(", ");
            }

            `$this$toString_u24lambda_u241`.append(name);
            `$this$toString_u24lambda_u241`.append(':');
            `$this$toString_u24lambda_u241`.append(value);
         }

         `$this$toString_u24lambda_u241`.append(']');
      }

      if (!this.tags.isEmpty()) {
         `$this$toString_u24lambda_u241`.append(", tags=");
         `$this$toString_u24lambda_u241`.append(this.tags);
      }

      `$this$toString_u24lambda_u241`.append('}');
      var var15: java.lang.String = var1.toString();
      return var15;
   }

   @SourceDebugExtension(["SMAP\nRequest.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Request.kt\nokhttp3/Request$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,298:1\n1#2:299\n*E\n"])
   open class Builder {
      internal final var body: RequestBody?
      internal final var headers: Headers.Builder
      internal final var method: String
      internal final var tags: MutableMap<Class<*>, Any> = (new LinkedHashMap()) as java.util.Map
      internal final var url: HttpUrl?

      public constructor()  {
         this.method = "GET";
         this.headers = new Headers.Builder();
      }

      internal constructor(request: Request)  {
         this.url = request.url();
         this.method = request.method();
         this.body = request.body();
         this.tags = (java.util.Map<Class<?>, Object>)(if (request.getTags$okhttp().isEmpty())
            new LinkedHashMap<>()
            else
            MapsKt.toMutableMap(request.getTags$okhttp()));
         this.headers = request.headers().newBuilder();
      }

      public open fun url(url: HttpUrl): okhttp3.Request.Builder {
         this.url = url;
         return this;
      }

      public open fun url(url: String): okhttp3.Request.Builder {
         var var3: java.lang.String;
         if (StringsKt.startsWith(url, "ws:", true)) {
            var var10000: StringBuilder = new StringBuilder().append("http:");
            var var10001: java.lang.String = url.substring(3);
            var3 = var10000.append(var10001).toString();
         } else if (StringsKt.startsWith(url, "wss:", true)) {
            var var4: StringBuilder = new StringBuilder().append("https:");
            var var5: java.lang.String = url.substring(4);
            var3 = var4.append(var5).toString();
         } else {
            var3 = url;
         }

         return this.url(HttpUrl.Companion.get(var3));
      }

      public open fun url(url: URL): okhttp3.Request.Builder {
         var var10001: HttpUrl.Companion = HttpUrl.Companion;
         var var10002: java.lang.String = url.toString();
         return this.url(var10001.get(var10002));
      }

      public open fun header(name: String, value: String): okhttp3.Request.Builder {
         this.headers.set(name, value);
         return this;
      }

      public open fun addHeader(name: String, value: String): okhttp3.Request.Builder {
         this.headers.add(name, value);
         return this;
      }

      public open fun removeHeader(name: String): okhttp3.Request.Builder {
         this.headers.removeAll(name);
         return this;
      }

      public open fun headers(headers: Headers): okhttp3.Request.Builder {
         this.headers = headers.newBuilder();
         return this;
      }

      public open fun cacheControl(cacheControl: CacheControl): okhttp3.Request.Builder {
         var value: java.lang.String = cacheControl.toString();
         return if (value.length() == 0) this.removeHeader("Cache-Control") else this.header("Cache-Control", value);
      }

      public open fun get(): okhttp3.Request.Builder {
         return this.method("GET", null);
      }

      public open fun head(): okhttp3.Request.Builder {
         return this.method("HEAD", null);
      }

      public open fun post(body: RequestBody): okhttp3.Request.Builder {
         return this.method("POST", body);
      }

      @JvmOverloads
      public open fun delete(body: RequestBody?): okhttp3.Request.Builder {
         return this.method("DELETE", body);
      }

      public open fun put(body: RequestBody): okhttp3.Request.Builder {
         return this.method("PUT", body);
      }

      public open fun patch(body: RequestBody): okhttp3.Request.Builder {
         return this.method("PATCH", body);
      }

      public open fun method(method: String, body: RequestBody?): okhttp3.Request.Builder {
         var `$this$method_u24lambda_u248`: Request.Builder = this;
         if (method.length() <= 0) {
            throw new IllegalArgumentException("method.isEmpty() == true".toString());
         } else {
            if (body == null) {
               if (HttpMethod.requiresRequestBody(method)) {
                  throw new IllegalArgumentException(("method " + method + " must have a request body.").toString());
               }
            } else if (!HttpMethod.permitsRequestBody(method)) {
               throw new IllegalArgumentException(("method " + method + " must not have a request body.").toString());
            }

            `$this$method_u24lambda_u248`.method = method;
            `$this$method_u24lambda_u248`.body = body;
            return this;
         }
      }

      public open fun tag(tag: Any?): okhttp3.Request.Builder {
         return this.tag(Object.class, tag);
      }

      public open fun <T> tag(type: Class<in T>, tag: T?): okhttp3.Request.Builder {
         var `$this$tag_u24lambda_u249`: Request.Builder = this;
         if (tag == null) {
            `$this$tag_u24lambda_u249`.tags.remove(type);
         } else {
            if (`$this$tag_u24lambda_u249`.tags.isEmpty()) {
               `$this$tag_u24lambda_u249`.tags = new LinkedHashMap<>();
            }

            var var10000: java.util.Map = `$this$tag_u24lambda_u249`.tags;
            var var10002: Any = type.cast(tag);
            var10000.put(type, var10002);
         }

         return this;
      }

      public open fun build(): Request {
         if (this.url == null) {
            throw new IllegalStateException("url == null".toString());
         } else {
            return new Request(this.url, this.method, this.headers.build(), this.body, Util.toImmutableMap(this.tags));
         }
      }

      @JvmOverloads
      fun delete(): Request.Builder {
         return delete$default(this, null, 1, null);
      }
   }
}
