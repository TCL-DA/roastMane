package okhttp3.internal.cache

import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.Cache
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.Interceptor.Chain
import okhttp3.internal.Util
import okhttp3.internal.connection.RealCall
import okhttp3.internal.http.HttpHeaders
import okhttp3.internal.http.HttpMethod
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import okio.Sink
import okio.Source
import okio.Timeout
import org.jetbrains.annotations.NotNull

class CacheInterceptor(cache: Cache?) : Interceptor {
   @JvmStatic
   public CacheInterceptor.Companion Companion = new CacheInterceptor.Companion(null);

   internal final val cache: Cache?

   init {
      this.cache = cache;
   }

   @Throws(java/io/IOException::class)
   public override fun intercept(chain: Chain): Response {
      var call: Call = chain.call();
      var cacheCandidate: Response = if (this.cache != null) this.cache.get$okhttp(chain.request()) else null;
      var strategy: CacheStrategy = new CacheStrategy.Factory(System.currentTimeMillis(), chain.request(), cacheCandidate).compute();
      var networkRequest: Request = strategy.getNetworkRequest();
      var cacheResponse: Response = strategy.getCacheResponse();
      if (this.cache != null) {
         this.cache.trackResponse$okhttp(strategy);
      }

      var var26: EventListener;
      label118: {
         var var10000: RealCall = call as? RealCall;
         if ((call as? RealCall) != null) {
            var26 = var10000.getEventListener$okhttp();
            if (var26 != null) {
               break label118;
            }
         }

         var26 = EventListener.NONE;
      }

      if (cacheCandidate != null && cacheResponse == null) {
         var var27: ResponseBody = cacheCandidate.body();
         if (var27 != null) {
            Util.closeQuietly(var27);
         }
      }

      if (networkRequest == null && cacheResponse == null) {
         var var22: Response = new Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(504)
            .message("Unsatisfiable Request (only-if-cached)")
            .body(Util.EMPTY_RESPONSE)
            .sentRequestAtMillis(-1L)
            .receivedResponseAtMillis(System.currentTimeMillis())
            .build();
         var26.satisfactionFailure(call, var22);
         return var22;
      } else if (networkRequest == null) {
         var var21: Response = cacheResponse.newBuilder().cacheResponse(CacheInterceptor.Companion.access$stripBody(Companion, cacheResponse)).build();
         var26.cacheHit(call, var21);
         return var21;
      } else {
         label180: {
            if (cacheResponse != null) {
               var26.cacheConditionalHit(call, cacheResponse);
            } else if (this.cache != null) {
               var26.cacheMiss(call);
            }

            try {
               ;
            } catch (java.lang.Throwable var17) {
               if (cacheCandidate != null) {
                  var var10001: ResponseBody = cacheCandidate.body();
                  if (var10001 != null) {
                     Util.closeQuietly(var10001);
                  }
               }
            }

            if (null == null && cacheCandidate != null) {
               var var28: ResponseBody = cacheCandidate.body();
               if (var28 != null) {
                  Util.closeQuietly(var28);
               }
            }

            if (cacheResponse != null) {
               if (null != null && null.code() == 304) {
                  var var23: Response = cacheResponse.newBuilder()
                     .headers(CacheInterceptor.Companion.access$combine(Companion, cacheResponse.headers(), null.headers()))
                     .sentRequestAtMillis(null.sentRequestAtMillis())
                     .receivedResponseAtMillis(null.receivedResponseAtMillis())
                     .cacheResponse(CacheInterceptor.Companion.access$stripBody(Companion, cacheResponse))
                     .networkResponse(CacheInterceptor.Companion.access$stripBody(Companion, null))
                     .build();
                  var var30: ResponseBody = null.body();
                  var30.close();
                  var var31: Cache = this.cache;
                  var31.trackConditionalCacheHit$okhttp();
                  this.cache.update$okhttp(cacheResponse, var23);
                  var26.cacheHit(call, var23);
                  return var23;
               }

               var var29: ResponseBody = cacheResponse.body();
               if (var29 != null) {
                  Util.closeQuietly(var29);
               }
            }

            var response: Response = null
               .newBuilder()
               .cacheResponse(CacheInterceptor.Companion.access$stripBody(Companion, cacheResponse))
               .networkResponse(CacheInterceptor.Companion.access$stripBody(Companion, null))
               .build();
            if (this.cache != null) {
               if (HttpHeaders.promisesBody(response) && CacheStrategy.Companion.isCacheable(response, networkRequest)) {
                  var it: Response = this.cacheWritingResponse(this.cache.put$okhttp(response), response);
                  if (cacheResponse != null) {
                     var26.cacheMiss(call);
                  }

                  return it;
               }

               if (HttpMethod.INSTANCE.invalidatesCache(networkRequest.method())) {
                  try {
                     this.cache.remove$okhttp(networkRequest);
                  } catch (IOException var16) {
                  }
               }
            }

            return response;
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun cacheWritingResponse(cacheRequest: CacheRequest?, response: Response): Response {
      if (cacheRequest == null) {
         return response;
      } else {
         var cacheBodyUnbuffered: Sink = cacheRequest.body();
         var var10000: ResponseBody = response.body();
         var source: BufferedSource = var10000.source();
         var cacheBody: BufferedSink = Okio.buffer(cacheBodyUnbuffered);
         return response.newBuilder()
            .body(
               new RealResponseBody(
                  Response.header$default(response, "Content-Type", null, 2, null),
                  response.body().contentLength(),
                  Okio.buffer(new Source(source, cacheRequest, cacheBody) {
                     private boolean cacheRequestClosed;

                     {
                        this.$source = `$source`;
                        this.$cacheRequest = `$cacheRequest`;
                        this.$cacheBody = `$cacheBody`;
                     }

                     public long read(@NotNull Buffer sink, long byteCount) throws IOException {
                        var var8: Long;
                        try {
                           var8 = this.$source.read(sink, byteCount);
                        } catch (IOException var7) {
                           if (!this.cacheRequestClosed) {
                              this.cacheRequestClosed = true;
                              this.$cacheRequest.abort();
                           }

                           throw var7;
                        }

                        if (var8 == -1L) {
                           if (!this.cacheRequestClosed) {
                              this.cacheRequestClosed = true;
                              this.$cacheBody.close();
                           }

                           return -1L;
                        } else {
                           sink.copyTo(this.$cacheBody.getBuffer(), sink.size() - var8, var8);
                           this.$cacheBody.emitCompleteSegments();
                           return var8;
                        }
                     }

                     @NotNull
                     public Timeout timeout() {
                        return this.$source.timeout();
                     }

                     public void close() throws IOException {
                        if (!this.cacheRequestClosed && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                           this.cacheRequestClosed = true;
                           this.$cacheRequest.abort();
                        }

                        this.$source.close();
                     }
                  })
               )
            )
            .build();
      }
   }

   companion object Companion private constructor() {
      private fun stripBody(response: Response?): Response? {
         return if ((if (response != null) response.body() else null) != null) response.newBuilder().body(null).build() else response;
      }

      private fun combine(cachedHeaders: Headers, networkHeaders: Headers): Headers {
         var result: Headers.Builder = new Headers.Builder();
         var index: Int = 0;

         for (int var5 = cachedHeaders.size(); index < var5; index++) {
            var fieldName: java.lang.String = cachedHeaders.name(index);
            var value: java.lang.String = cachedHeaders.value(index);
            if ((!StringsKt.equals("Warning", fieldName, true) || !StringsKt.startsWith$default(value, "1", false, 2, null))
               && (this.isContentSpecificHeader(fieldName) || !this.isEndToEnd(fieldName) || networkHeaders.get(fieldName) == null)) {
               result.addLenient$okhttp(fieldName, value);
            }
         }

         index = 0;

         for (int var9 = networkHeaders.size(); index < var9; index++) {
            var var10: java.lang.String = networkHeaders.name(index);
            if (!this.isContentSpecificHeader(var10) && this.isEndToEnd(var10)) {
               result.addLenient$okhttp(var10, networkHeaders.value(index));
            }
         }

         return result.build();
      }

      private fun isEndToEnd(fieldName: String): Boolean {
         return !StringsKt.equals("Connection", fieldName, true)
            && !StringsKt.equals("Keep-Alive", fieldName, true)
            && !StringsKt.equals("Proxy-Authenticate", fieldName, true)
            && !StringsKt.equals("Proxy-Authorization", fieldName, true)
            && !StringsKt.equals("TE", fieldName, true)
            && !StringsKt.equals("Trailers", fieldName, true)
            && !StringsKt.equals("Transfer-Encoding", fieldName, true)
            && !StringsKt.equals("Upgrade", fieldName, true);
      }

      private fun isContentSpecificHeader(fieldName: String): Boolean {
         return StringsKt.equals("Content-Length", fieldName, true)
            || StringsKt.equals("Content-Encoding", fieldName, true)
            || StringsKt.equals("Content-Type", fieldName, true);
      }
   }
}
