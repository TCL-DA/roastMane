package okhttp3.internal.http

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.Proxy.Type
import java.security.cert.CertificateException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.Route
import okhttp3.Interceptor.Chain
import okhttp3.internal.Util
import okhttp3.internal.connection.Exchange
import okhttp3.internal.connection.RealCall
import okhttp3.internal.connection.RealConnection
import okhttp3.internal.connection.RouteException
import okhttp3.internal.http2.ConnectionShutdownException

class RetryAndFollowUpInterceptor(client: OkHttpClient) : Interceptor {
   @JvmStatic
   public RetryAndFollowUpInterceptor.Companion Companion = new RetryAndFollowUpInterceptor.Companion(null);
   @JvmStatic
   private int MAX_FOLLOW_UPS = 20;

   private final val client: OkHttpClient

   init {
      this.client = client;
   }

   @Throws(java/io/IOException::class)
   public override fun intercept(chain: Chain): Response {
      var realChain: RealInterceptorChain = chain as RealInterceptorChain;
      var request: Request = (chain as RealInterceptorChain).getRequest$okhttp();
      var call: RealCall = (chain as RealInterceptorChain).getCall$okhttp();
      var followUpCount: Int = 0;
      var priorResponse: Response = null;
      var newExchangeFinder: Boolean = true;
      var recoveredFailures: java.util.List = CollectionsKt.emptyList();

      while (true) {
         call.enterNetworkInterceptorExchange(request, newExchangeFinder);
         var closeActiveExchange: Boolean = true;

         label98: {
            label97: {
               label96: {
                  label95: {
                     try {
                        if (call.isCanceled()) {
                           throw new IOException("Canceled");
                        }

                        var var21: Response;
                        try {
                           var21 = realChain.proceed(request);
                           newExchangeFinder = true;
                        } catch (RouteException var15) {
                           if (!this.recover(var15.getLastConnectException(), call, request, false)) {
                              throw Util.withSuppressed(var15.getFirstConnectException(), recoveredFailures);
                           }

                           recoveredFailures = CollectionsKt.plus(recoveredFailures, var15.getFirstConnectException());
                           newExchangeFinder = false;
                           break label98;
                        } catch (IOException var16) {
                           if (!this.recover(var16, call, request, var16 !is ConnectionShutdownException)) {
                              throw Util.withSuppressed(var16, recoveredFailures);
                           }

                           recoveredFailures = CollectionsKt.plus(recoveredFailures, var16);
                           newExchangeFinder = false;
                           break label97;
                        }

                        if (priorResponse != null) {
                           var21 = var21.newBuilder().priorResponse(priorResponse.newBuilder().body(null).build()).build();
                        }

                        var exchange: Exchange = call.getInterceptorScopedExchange$okhttp();
                        var followUp: Request = this.followUpRequest(var21, exchange);
                        if (followUp == null) {
                           if (exchange != null && exchange.isDuplex$okhttp()) {
                              call.timeoutEarlyExit();
                           }

                           closeActiveExchange = false;
                           break label96;
                        }

                        var followUpBody: RequestBody = followUp.body();
                        if (followUpBody != null && followUpBody.isOneShot()) {
                           closeActiveExchange = false;
                           break label95;
                        }

                        var var10000: ResponseBody = var21.body();
                        if (var10000 != null) {
                           Util.closeQuietly(var10000);
                        }

                        if (++followUpCount > 20) {
                           throw new ProtocolException("Too many follow-up requests: " + followUpCount);
                        }

                        request = followUp;
                        priorResponse = var21;
                     } catch (java.lang.Throwable var17) {
                        call.exitNetworkInterceptorExchange$okhttp(closeActiveExchange);
                     }

                     call.exitNetworkInterceptorExchange$okhttp(true);
                     continue;
                  }

                  call.exitNetworkInterceptorExchange$okhttp(closeActiveExchange);
               }

               call.exitNetworkInterceptorExchange$okhttp(closeActiveExchange);
            }

            call.exitNetworkInterceptorExchange$okhttp(true);
            continue;
         }

         call.exitNetworkInterceptorExchange$okhttp(true);
      }
   }

   private fun recover(e: IOException, call: RealCall, userRequest: Request, requestSendStarted: Boolean): Boolean {
      if (!this.client.retryOnConnectionFailure()) {
         return false;
      } else if (requestSendStarted && this.requestIsOneShot(e, userRequest)) {
         return false;
      } else if (!this.isRecoverable(e, requestSendStarted)) {
         return false;
      } else {
         return call.retryAfterFailure();
      }
   }

   private fun requestIsOneShot(e: IOException, userRequest: Request): Boolean {
      var requestBody: RequestBody = userRequest.body();
      return requestBody != null && requestBody.isOneShot() || e is FileNotFoundException;
   }

   private fun isRecoverable(e: IOException, requestSendStarted: Boolean): Boolean {
      if (e is ProtocolException) {
         return false;
      } else if (e !is InterruptedIOException) {
         if (e is SSLHandshakeException && e.getCause() is CertificateException) {
            return false;
         } else {
            return e !is SSLPeerUnverifiedException;
         }
      } else {
         return e is SocketTimeoutException && !requestSendStarted;
      }
   }

   @Throws(java/io/IOException::class)
   private fun followUpRequest(userResponse: Response, exchange: Exchange?): Request? {
      var var11: Route;
      label66: {
         if (exchange != null) {
            var var10000: RealConnection = exchange.getConnection$okhttp();
            if (var10000 != null) {
               var11 = var10000.route();
               break label66;
            }
         }

         var11 = null;
      }

      var responseCode: Int = userResponse.code();
      var method: java.lang.String = userResponse.request().method();
      switch (responseCode) {
         case 300:
         case 301:
         case 302:
         case 303:
         case 307:
         case 308:
            return this.buildRedirectRequest(userResponse, method);
         case 401:
            return this.client.authenticator().authenticate(var11, userResponse);
         case 407:
            if (var11.proxy().type() != Type.HTTP) {
               throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
            }

            return this.client.proxyAuthenticator().authenticate(var11, userResponse);
         case 408:
            if (!this.client.retryOnConnectionFailure()) {
               return null;
            } else {
               var requestBodyx: RequestBody = userResponse.request().body();
               if (requestBodyx != null && requestBodyx.isOneShot()) {
                  return null;
               } else {
                  var priorResponsex: Response = userResponse.priorResponse();
                  if (priorResponsex != null && priorResponsex.code() == 408) {
                     return null;
                  } else {
                     if (this.retryAfter(userResponse, 0) > 0) {
                        return null;
                     }

                     return userResponse.request();
                  }
               }
            }
         case 421:
            var var8: RequestBody = userResponse.request().body();
            if (var8 != null && var8.isOneShot()) {
               return null;
            } else {
               if (exchange != null && exchange.isCoalescedConnection$okhttp()) {
                  exchange.getConnection$okhttp().noCoalescedConnections$okhttp();
                  return userResponse.request();
               }

               return null;
            }
         case 503:
            var requestBody: Response = userResponse.priorResponse();
            if (requestBody != null && requestBody.code() == 503) {
               return null;
            } else {
               if (this.retryAfter(userResponse, Integer.MAX_VALUE) == 0) {
                  return userResponse.request();
               }

               return null;
            }
         default:
            return null;
      }
   }

   private fun buildRedirectRequest(userResponse: Response, method: String): Request? {
      if (!this.client.followRedirects()) {
         return null;
      } else {
         var var10000: java.lang.String = Response.header$default(userResponse, "Location", null, 2, null);
         if (var10000 == null) {
            return null;
         } else {
            var var10: HttpUrl = userResponse.request().url().resolve(var10000);
            if (var10 == null) {
               return null;
            } else if (!(var10.scheme() == userResponse.request().url().scheme()) && !this.client.followSslRedirects()) {
               return null;
            } else {
               var requestBuilder: Request.Builder = userResponse.request().newBuilder();
               if (HttpMethod.permitsRequestBody(method)) {
                  var responseCode: Int = userResponse.code();
                  var maintainBody: Boolean = HttpMethod.INSTANCE.redirectsWithBody(method) || responseCode == 308 || responseCode == 307;
                  if (HttpMethod.INSTANCE.redirectsToGet(method) && responseCode != 308 && responseCode != 307) {
                     requestBuilder.method("GET", null);
                  } else {
                     requestBuilder.method(method, if (maintainBody) userResponse.request().body() else null);
                  }

                  if (!maintainBody) {
                     requestBuilder.removeHeader("Transfer-Encoding");
                     requestBuilder.removeHeader("Content-Length");
                     requestBuilder.removeHeader("Content-Type");
                  }
               }

               if (!Util.canReuseConnectionFor(userResponse.request().url(), var10)) {
                  requestBuilder.removeHeader("Authorization");
               }

               return requestBuilder.url(var10).build();
            }
         }
      }
   }

   private fun retryAfter(userResponse: Response, defaultDelay: Int): Int {
      var var10000: java.lang.String = Response.header$default(userResponse, "Retry-After", null, 2, null);
      if (var10000 == null) {
         return defaultDelay;
      } else if (new Regex("\\d+").matches(var10000)) {
         var var5: Int = Integer.valueOf(var10000);
         return var5.intValue();
      } else {
         return Integer.MAX_VALUE;
      }
   }

   companion object Companion private constructor() {
      private const val MAX_FOLLOW_UPS: Int

   }
}
