package okhttp3.internal.http

import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Interceptor.Chain
import okhttp3.internal.Util
import okhttp3.internal.connection.Exchange
import okhttp3.internal.connection.RealCall

@SourceDebugExtension(["SMAP\nRealInterceptorChain.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealInterceptorChain.kt\nokhttp3/internal/http/RealInterceptorChain\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,123:1\n1#2:124\n*E\n"])
class RealInterceptorChain(call: RealCall,
      interceptors: List<Interceptor>,
      index: Int,
      exchange: Exchange?,
      request: Request,
      connectTimeoutMillis: Int,
      readTimeoutMillis: Int,
      writeTimeoutMillis: Int
   ) :
   Interceptor.Chain {
   internal final val call: RealCall
   private final var calls: Int
   internal final val connectTimeoutMillis: Int
   internal final val exchange: Exchange?
   private final val index: Int
   private final val interceptors: List<Interceptor>
   internal final val readTimeoutMillis: Int
   internal final val request: Request
   internal final val writeTimeoutMillis: Int

   init {
      this.call = call;
      this.interceptors = interceptors;
      this.index = index;
      this.exchange = exchange;
      this.request = request;
      this.connectTimeoutMillis = connectTimeoutMillis;
      this.readTimeoutMillis = readTimeoutMillis;
      this.writeTimeoutMillis = writeTimeoutMillis;
   }

   internal fun copy(index: Int, exchange: Exchange?, request: Request, connectTimeoutMillis: Int, readTimeoutMillis: Int, writeTimeoutMillis: Int): RealInterceptorChain {
      return new RealInterceptorChain(this.call, this.interceptors, index, exchange, request, connectTimeoutMillis, readTimeoutMillis, writeTimeoutMillis);
   }

   public override fun connection(): Connection? {
      return if (this.exchange != null) this.exchange.getConnection$okhttp() else null;
   }

   public override fun connectTimeoutMillis(): Int {
      return this.connectTimeoutMillis;
   }

   public override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Chain {
      if (this.exchange != null) {
         throw new IllegalStateException("Timeouts can't be adjusted in a network interceptor".toString());
      } else {
         return copy$okhttp$default(this, 0, null, null, Util.checkDuration("connectTimeout", (long)timeout, unit), 0, 0, 55, null);
      }
   }

   public override fun readTimeoutMillis(): Int {
      return this.readTimeoutMillis;
   }

   public override fun withReadTimeout(timeout: Int, unit: TimeUnit): Chain {
      if (this.exchange != null) {
         throw new IllegalStateException("Timeouts can't be adjusted in a network interceptor".toString());
      } else {
         return copy$okhttp$default(this, 0, null, null, 0, Util.checkDuration("readTimeout", (long)timeout, unit), 0, 47, null);
      }
   }

   public override fun writeTimeoutMillis(): Int {
      return this.writeTimeoutMillis;
   }

   public override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Chain {
      if (this.exchange != null) {
         throw new IllegalStateException("Timeouts can't be adjusted in a network interceptor".toString());
      } else {
         return copy$okhttp$default(this, 0, null, null, 0, 0, Util.checkDuration("writeTimeout", (long)timeout, unit), 31, null);
      }
   }

   public override fun call(): Call {
      return this.call;
   }

   public override fun request(): Request {
      return this.request;
   }

   @Throws(java/io/IOException::class)
   public override fun proceed(request: Request): Response {
      if (this.index >= this.interceptors.size()) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         var next: Int = this.calls++;
         if (this.exchange != null) {
            if (!this.exchange.getFinder$okhttp().sameHostAndPort(request.url())) {
               throw new IllegalStateException(
                  ("network interceptor " + this.interceptors.get(this.index - 1) + " must retain the same host and port").toString()
               );
            }

            if (this.calls != 1) {
               throw new IllegalStateException(
                  ("network interceptor " + this.interceptors.get(this.index - 1) + " must call proceed() exactly once").toString()
               );
            }
         }

         var var6: RealInterceptorChain = copy$okhttp$default(this, this.index + 1, null, request, 0, 0, 0, 58, null);
         var interceptor: Interceptor = this.interceptors.get(this.index);
         var var10000: Response = interceptor.intercept(var6);
         if (var10000 == null) {
            throw new NullPointerException("interceptor " + interceptor + " returned null");
         } else if (this.exchange != null && this.index + 1 < this.interceptors.size() && var6.calls != 1) {
            throw new IllegalStateException(("network interceptor " + interceptor + " must call proceed() exactly once").toString());
         } else if (var10000.body() == null) {
            throw new IllegalStateException(("interceptor " + interceptor + " returned a response with no body").toString());
         } else {
            return var10000;
         }
      }
   }
}
