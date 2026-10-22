package okhttp3

import java.util.concurrent.TimeUnit
import org.jetbrains.annotations.NotNull

fun interface Interceptor {
   @JvmStatic
   Interceptor.Companion Companion = Interceptor.Companion.$$INSTANCE;

   @Throws(java/io/IOException::class)
   public abstract fun intercept(chain: okhttp3.Interceptor.Chain): Response {
   }

   interface Chain {
      public abstract fun request(): Request {
      }

      @Throws(java/io/IOException::class)
      public abstract fun proceed(request: Request): Response {
      }

      public abstract fun connection(): Connection? {
      }

      public abstract fun call(): Call {
      }

      public abstract fun connectTimeoutMillis(): Int {
      }

      public abstract fun withConnectTimeout(timeout: Int, unit: TimeUnit): okhttp3.Interceptor.Chain {
      }

      public abstract fun readTimeoutMillis(): Int {
      }

      public abstract fun withReadTimeout(timeout: Int, unit: TimeUnit): okhttp3.Interceptor.Chain {
      }

      public abstract fun writeTimeoutMillis(): Int {
      }

      public abstract fun withWriteTimeout(timeout: Int, unit: TimeUnit): okhttp3.Interceptor.Chain {
      }
   }

   companion object Companion private constructor() {
      public inline operator fun invoke(crossinline block: (okhttp3.Interceptor.Chain) -> Response): Interceptor {
         return new Interceptor(block) {
            {
               this.$block = `$block`;
            }

            @NotNull
            @Override
            public final Response intercept(@NotNull Interceptor.Chain it) {
               return this.$block.invoke(it) as Response;
            }
         };
      }
   }
}
