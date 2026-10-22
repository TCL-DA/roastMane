package okhttp3.internal.cache

import java.util.Date
import java.util.concurrent.TimeUnit
import okhttp3.CacheControl
import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.Util
import okhttp3.internal.http.DatesKt

class CacheStrategy internal constructor(networkRequest: Request?, cacheResponse: Response?) {
   @JvmStatic
   public CacheStrategy.Companion Companion = new CacheStrategy.Companion(null);

   public final val cacheResponse: Response?
   public final val networkRequest: Request?

   init {
      this.networkRequest = networkRequest;
      this.cacheResponse = cacheResponse;
   }

   companion object Companion private constructor() {
      public fun isCacheable(response: Response, request: Request): Boolean {
         switch (response.code()) {
            case 200:
            case 203:
            case 204:
            case 300:
            case 301:
            case 308:
            case 404:
            case 405:
            case 410:
            case 414:
            case 501:
               break;
            case 302:
            case 307:
               if (Response.header$default(response, "Expires", null, 2, null) == null
                  && response.cacheControl().maxAgeSeconds() == -1
                  && !response.cacheControl().isPublic()
                  && !response.cacheControl().isPrivate()) {
                  return false;
               }
               break;
            default:
               return false;
         }

         return !response.cacheControl().noStore() && !request.cacheControl().noStore();
      }
   }

   class Factory(nowMillis: Long, request: Request, cacheResponse: Response?) {
      private final var ageSeconds: Int
      private final val cacheResponse: Response?
      private final var etag: String?
      private final var expires: Date?
      private final var lastModified: Date?
      private final var lastModifiedString: String?
      private final val nowMillis: Long
      private final var receivedResponseMillis: Long
      internal final val request: Request
      private final var sentRequestMillis: Long
      private final var servedDate: Date?
      private final var servedDateString: String?

      init {
         this.nowMillis = nowMillis;
         this.request = request;
         this.cacheResponse = cacheResponse;
         this.ageSeconds = -1;
         if (this.cacheResponse != null) {
            this.sentRequestMillis = this.cacheResponse.sentRequestAtMillis();
            this.receivedResponseMillis = this.cacheResponse.receivedResponseAtMillis();
            var headers: Headers = this.cacheResponse.headers();
            var i: Int = 0;

            for (int var7 = headers.size(); i < var7; i++) {
               var fieldName: java.lang.String = headers.name(i);
               var value: java.lang.String = headers.value(i);
               if (StringsKt.equals(fieldName, "Date", true)) {
                  this.servedDate = DatesKt.toHttpDateOrNull(value);
                  this.servedDateString = value;
               } else if (StringsKt.equals(fieldName, "Expires", true)) {
                  this.expires = DatesKt.toHttpDateOrNull(value);
               } else if (StringsKt.equals(fieldName, "Last-Modified", true)) {
                  this.lastModified = DatesKt.toHttpDateOrNull(value);
                  this.lastModifiedString = value;
               } else if (StringsKt.equals(fieldName, "ETag", true)) {
                  this.etag = value;
               } else if (StringsKt.equals(fieldName, "Age", true)) {
                  this.ageSeconds = Util.toNonNegativeInt(value, -1);
               }
            }
         }
      }

      private fun isFreshnessLifetimeHeuristic(): Boolean {
         var var10000: Response = this.cacheResponse;
         return var10000.cacheControl().maxAgeSeconds() == -1 && this.expires == null;
      }

      public fun compute(): CacheStrategy {
         var candidate: CacheStrategy = this.computeCandidate();
         return if (candidate.getNetworkRequest() != null && this.request.cacheControl().onlyIfCached()) new CacheStrategy(null, null) else candidate;
      }

      private fun computeCandidate(): CacheStrategy {
         if (this.cacheResponse == null) {
            return new CacheStrategy(this.request, null);
         } else if (this.request.isHttps() && this.cacheResponse.handshake() == null) {
            return new CacheStrategy(this.request, null);
         } else if (!CacheStrategy.Companion.isCacheable(this.cacheResponse, this.request)) {
            return new CacheStrategy(this.request, null);
         } else {
            var requestCaching: CacheControl = this.request.cacheControl();
            if (!requestCaching.noCache() && !this.hasConditions(this.request)) {
               var responseCaching: CacheControl = this.cacheResponse.cacheControl();
               var ageMillis: Long = this.cacheResponseAge();
               var freshMillis: Long = this.computeFreshnessLifetime();
               if (requestCaching.maxAgeSeconds() != -1) {
                  freshMillis = Math.min(freshMillis, TimeUnit.SECONDS.toMillis((long)requestCaching.maxAgeSeconds()));
               }

               var minFreshMillis: Long = 0L;
               if (requestCaching.minFreshSeconds() != -1) {
                  minFreshMillis = TimeUnit.SECONDS.toMillis((long)requestCaching.minFreshSeconds());
               }

               var maxStaleMillis: Long = 0L;
               if (!responseCaching.mustRevalidate() && requestCaching.maxStaleSeconds() != -1) {
                  maxStaleMillis = TimeUnit.SECONDS.toMillis((long)requestCaching.maxStaleSeconds());
               }

               if (!responseCaching.noCache() && ageMillis + minFreshMillis < freshMillis + maxStaleMillis) {
                  var var16: Response.Builder = this.cacheResponse.newBuilder();
                  if (ageMillis + minFreshMillis >= freshMillis) {
                     var16.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
                  }

                  if (ageMillis > 86400000L && this.isFreshnessLifetimeHeuristic()) {
                     var16.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
                  }

                  return new CacheStrategy(null, var16.build());
               } else {
                  var var15: java.lang.String;
                  var var17: java.lang.String;
                  if (this.etag != null) {
                     var15 = "If-None-Match";
                     var17 = this.etag;
                  } else if (this.lastModified != null) {
                     var15 = "If-Modified-Since";
                     var17 = this.lastModifiedString;
                  } else {
                     if (this.servedDate == null) {
                        return new CacheStrategy(this.request, null);
                     }

                     var15 = "If-Modified-Since";
                     var17 = this.servedDateString;
                  }

                  var conditionalRequestHeaders: Headers.Builder = this.request.headers().newBuilder();
                  conditionalRequestHeaders.addLenient$okhttp(var15, var17);
                  return new CacheStrategy(this.request.newBuilder().headers(conditionalRequestHeaders.build()).build(), this.cacheResponse);
               }
            } else {
               return new CacheStrategy(this.request, null);
            }
         }
      }

      private fun computeFreshnessLifetime(): Long {
         var var10000: Response = this.cacheResponse;
         var responseCaching: CacheControl = var10000.cacheControl();
         if (responseCaching.maxAgeSeconds() != -1) {
            return TimeUnit.SECONDS.toMillis((long)responseCaching.maxAgeSeconds());
         } else if (this.expires != null) {
            var var7: Long = if (this.servedDate != null) this.servedDate.getTime() else this.receivedResponseMillis;
            var var8: Long = this.expires.getTime() - var7;
            return if (var8 > 0L) var8 else 0L;
         } else if (this.lastModified != null && this.cacheResponse.request().url().query() == null) {
            var servedMillis: Long = if (this.servedDate != null) this.servedDate.getTime() else this.sentRequestMillis;
            var var10001: Date = this.lastModified;
            var delta: Long = servedMillis - var10001.getTime();
            return if (delta > 0L) delta / (long)10 else 0L;
         } else {
            return 0L;
         }
      }

      private fun cacheResponseAge(): Long {
         var apparentReceivedAge: Long = if (this.servedDate != null) Math.max(0L, this.receivedResponseMillis - this.servedDate.getTime()) else 0L;
         return (if (this.ageSeconds != -1) Math.max(apparentReceivedAge, TimeUnit.SECONDS.toMillis((long)this.ageSeconds)) else apparentReceivedAge)
            + (this.receivedResponseMillis - this.sentRequestMillis)
            + (this.nowMillis - this.receivedResponseMillis);
      }

      private fun hasConditions(request: Request): Boolean {
         return request.header("If-Modified-Since") != null || request.header("If-None-Match") != null;
      }
   }
}
