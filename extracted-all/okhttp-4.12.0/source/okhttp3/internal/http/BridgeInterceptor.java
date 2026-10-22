package okhttp3.internal.http

import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.Interceptor.Chain
import okhttp3.internal.Util
import okio.GzipSource
import okio.Okio
import okio.Source

@SourceDebugExtension(["SMAP\nBridgeInterceptor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BridgeInterceptor.kt\nokhttp3/internal/http/BridgeInterceptor\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,117:1\n1864#2,3:118\n*S KotlinDebug\n*F\n+ 1 BridgeInterceptor.kt\nokhttp3/internal/http/BridgeInterceptor\n*L\n111#1:118,3\n*E\n"])
class BridgeInterceptor(cookieJar: CookieJar) : Interceptor {
   private final val cookieJar: CookieJar

   init {
      this.cookieJar = cookieJar;
   }

   @Throws(java/io/IOException::class)
   public override fun intercept(chain: Chain): Response {
      var userRequest: Request = chain.request();
      var requestBuilder: Request.Builder = userRequest.newBuilder();
      var body: RequestBody = userRequest.body();
      if (body != null) {
         var transparentGzip: MediaType = body.contentType();
         if (transparentGzip != null) {
            requestBuilder.header("Content-Type", transparentGzip.toString());
         }

         var cookies: Long = body.contentLength();
         if (cookies != -1L) {
            requestBuilder.header("Content-Length", java.lang.String.valueOf(cookies));
            requestBuilder.removeHeader("Transfer-Encoding");
         } else {
            requestBuilder.header("Transfer-Encoding", "chunked");
            requestBuilder.removeHeader("Content-Length");
         }
      }

      if (userRequest.header("Host") == null) {
         requestBuilder.header("Host", Util.toHostHeader$default(userRequest.url(), false, 1, null));
      }

      if (userRequest.header("Connection") == null) {
         requestBuilder.header("Connection", "Keep-Alive");
      }

      var var13: Boolean = false;
      if (userRequest.header("Accept-Encoding") == null && userRequest.header("Range") == null) {
         var13 = true;
         requestBuilder.header("Accept-Encoding", "gzip");
      }

      var var14: java.util.List = this.cookieJar.loadForRequest(userRequest.url());
      if (!var14.isEmpty()) {
         requestBuilder.header("Cookie", this.cookieHeader(var14));
      }

      if (userRequest.header("User-Agent") == null) {
         requestBuilder.header("User-Agent", "okhttp/4.12.0");
      }

      var networkResponse: Response = chain.proceed(requestBuilder.build());
      HttpHeaders.receiveHeaders(this.cookieJar, userRequest.url(), networkResponse.headers());
      var responseBuilder: Response.Builder = networkResponse.newBuilder().request(userRequest);
      if (var13
         && StringsKt.equals("gzip", Response.header$default(networkResponse, "Content-Encoding", null, 2, null), true)
         && HttpHeaders.promisesBody(networkResponse)) {
         var responseBody: ResponseBody = networkResponse.body();
         if (responseBody != null) {
            var gzipSource: GzipSource = new GzipSource(responseBody.source() as Source);
            responseBuilder.headers(networkResponse.headers().newBuilder().removeAll("Content-Encoding").removeAll("Content-Length").build());
            responseBuilder.body(
               new RealResponseBody(Response.header$default(networkResponse, "Content-Type", null, 2, null), -1L, Okio.buffer(gzipSource as Source))
            );
         }
      }

      return responseBuilder.build();
   }

   private fun cookieHeader(cookies: List<Cookie>): String {
      var var2: StringBuilder = new StringBuilder();
      var `$this$cookieHeader_u24lambda_u241`: StringBuilder = var2;
      var `$this$forEachIndexed$iv`: java.lang.Iterable = cookies;
      var `index$iv`: Int = 0;

      for (Object item$iv : $this$forEachIndexed$iv) {
         var var10: Int = `index$iv`++;
         if (var10 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         var cookie: Cookie = `item$iv` as Cookie;
         if (var10 > 0) {
            `$this$cookieHeader_u24lambda_u241`.append("; ");
         }

         `$this$cookieHeader_u24lambda_u241`.append(cookie.name()).append('=').append(cookie.value());
      }

      var var14: java.lang.String = var2.toString();
      return var14;
   }
}
