package okhttp3.internal.connection

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.Interceptor.Chain
import okhttp3.internal.http.RealInterceptorChain

object ConnectInterceptor private constructor() : Interceptor {
   @JvmStatic
   public ConnectInterceptor INSTANCE = new ConnectInterceptor();


   @Throws(java/io/IOException::class)
   public override fun intercept(chain: Chain): Response {
      return RealInterceptorChain.copy$okhttp$default(
            chain as RealInterceptorChain,
            0,
            (chain as RealInterceptorChain).getCall$okhttp().initExchange$okhttp(chain as RealInterceptorChain),
            null,
            0,
            0,
            0,
            61,
            null
         )
         .proceed((chain as RealInterceptorChain).getRequest$okhttp());
   }
}
