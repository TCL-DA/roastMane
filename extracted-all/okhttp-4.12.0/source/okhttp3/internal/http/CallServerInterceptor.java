package okhttp3.internal.http

import java.io.IOException
import java.net.ProtocolException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.Interceptor.Chain
import okhttp3.internal.Util
import okhttp3.internal.connection.Exchange
import okhttp3.internal.http2.ConnectionShutdownException
import okio.BufferedSink
import okio.Okio

class CallServerInterceptor(forWebSocket: Boolean) : Interceptor {
   private final val forWebSocket: Boolean

   init {
      this.forWebSocket = forWebSocket;
   }

   @Throws(java/io/IOException::class)
   public override fun intercept(chain: Chain): Response {
      var realChain: RealInterceptorChain = chain as RealInterceptorChain;
      var var10000: Exchange = (chain as RealInterceptorChain).getExchange$okhttp();
      var exchange: Exchange = var10000;
      var request: Request = realChain.getRequest$okhttp();
      var requestBody: RequestBody = request.body();
      var sentRequestMillis: Long = System.currentTimeMillis();
      var invokeStartEvent: Boolean = true;
      var responseBuilder: Response.Builder = null;
      var sendRequestException: IOException = null;

      try {
         exchange.writeRequestHeaders(request);
         if (HttpMethod.permitsRequestBody(request.method()) && requestBody != null) {
            if (StringsKt.equals("100-continue", request.header("Expect"), true)) {
               exchange.flushRequest();
               responseBuilder = exchange.readResponseHeaders(true);
               exchange.responseHeadersStart();
               invokeStartEvent = false;
            }

            if (responseBuilder == null) {
               if (requestBody.isDuplex()) {
                  exchange.flushRequest();
                  requestBody.writeTo(Okio.buffer(exchange.createRequestBody(request, true)));
               } else {
                  var var16: BufferedSink = Okio.buffer(exchange.createRequestBody(request, false));
                  requestBody.writeTo(var16);
                  var16.close();
               }
            } else {
               exchange.noRequestBody();
               if (!exchange.getConnection$okhttp().isMultiplexed$okhttp()) {
                  exchange.noNewExchangesOnConnection();
               }
            }
         } else {
            exchange.noRequestBody();
         }

         if (requestBody == null || !requestBody.isDuplex()) {
            exchange.finishRequest();
         }
      } catch (IOException var14) {
         if (var14 is ConnectionShutdownException) {
            throw var14;
         }

         if (!var10000.getHasFailure$okhttp()) {
            throw var14;
         }

         sendRequestException = var14;
      }

      try {
         if (responseBuilder == null) {
            var var19: Response.Builder = exchange.readResponseHeaders(false);
            responseBuilder = var19;
            if (invokeStartEvent) {
               exchange.responseHeadersStart();
               invokeStartEvent = false;
            }
         }

         var var17: Response = responseBuilder.request(request)
            .handshake(exchange.getConnection$okhttp().handshake())
            .sentRequestAtMillis(sentRequestMillis)
            .receivedResponseAtMillis(System.currentTimeMillis())
            .build();
         var code: Int = var17.code();
         if (this.shouldIgnoreAndWaitForRealResponse(code)) {
            var var20: Response.Builder = exchange.readResponseHeaders(false);
            if (invokeStartEvent) {
               exchange.responseHeadersStart();
            }

            var17 = var20.request(request)
               .handshake(exchange.getConnection$okhttp().handshake())
               .sentRequestAtMillis(sentRequestMillis)
               .receivedResponseAtMillis(System.currentTimeMillis())
               .build();
            code = var17.code();
         }

         exchange.responseHeadersEnd(var17);
         var17 = if (this.forWebSocket && code == 101)
            var17.newBuilder().body(Util.EMPTY_RESPONSE).build()
            else
            var17.newBuilder().body(exchange.openResponseBody(var17)).build();
         if (StringsKt.equals("close", var17.request().header("Connection"), true)
            || StringsKt.equals("close", Response.header$default(var17, "Connection", null, 2, null), true)) {
            exchange.noNewExchangesOnConnection();
         }

         if (code == 204 || code == 205) {
            var var21: ResponseBody = var17.body();
            if ((if (var21 != null) var21.contentLength() else -1L) > 0L) {
               var var10002: StringBuilder = new StringBuilder().append("HTTP ").append(code).append(" had non-zero Content-Length: ");
               var var10003: ResponseBody = var17.body();
               throw new ProtocolException(var10002.append(if (var10003 != null) var10003.contentLength() else null).toString());
            }
         }

         return var17;
      } catch (IOException var13) {
         if (sendRequestException != null) {
            ExceptionsKt.addSuppressed(sendRequestException, var13);
            throw sendRequestException;
         } else {
            throw var13;
         }
      }
   }

   private fun shouldIgnoreAndWaitForRealResponse(code: Int): Boolean {
      return code == 100 || 102 <= code && code < 200;
   }
}
