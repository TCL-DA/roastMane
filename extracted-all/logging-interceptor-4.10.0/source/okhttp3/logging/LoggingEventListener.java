package okhttp3.logging

import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.Intrinsics
import okhttp3.Call
import okhttp3.Connection
import okhttp3.EventListener
import okhttp3.Handshake
import okhttp3.HttpUrl
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor.Logger

class LoggingEventListener private constructor(logger: Logger) : EventListener {
   private final val logger: Logger
   private final var startNs: Long

   init {
      this.logger = logger;
   }

   public open fun callStart(call: Call) {
      this.startNs = System.nanoTime();
      this.logWithTime(Intrinsics.stringPlus("callStart: ", call.request()));
   }

   public open fun proxySelectStart(call: Call, url: HttpUrl) {
      this.logWithTime(Intrinsics.stringPlus("proxySelectStart: ", url));
   }

   public open fun proxySelectEnd(call: Call, url: HttpUrl, proxies: List<Proxy>) {
      this.logWithTime(Intrinsics.stringPlus("proxySelectEnd: ", proxies));
   }

   public open fun dnsStart(call: Call, domainName: String) {
      this.logWithTime(Intrinsics.stringPlus("dnsStart: ", domainName));
   }

   public open fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
      this.logWithTime(Intrinsics.stringPlus("dnsEnd: ", inetAddressList));
   }

   public open fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
      this.logWithTime("connectStart: " + inetSocketAddress + ' ' + proxy);
   }

   public open fun secureConnectStart(call: Call) {
      this.logWithTime("secureConnectStart");
   }

   public open fun secureConnectEnd(call: Call, handshake: Handshake?) {
      this.logWithTime(Intrinsics.stringPlus("secureConnectEnd: ", handshake));
   }

   public open fun connectEnd(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?) {
      this.logWithTime(Intrinsics.stringPlus("connectEnd: ", protocol));
   }

   public open fun connectFailed(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?, ioe: IOException) {
      this.logWithTime("connectFailed: " + protocol + ' ' + ioe);
   }

   public open fun connectionAcquired(call: Call, connection: Connection) {
      this.logWithTime(Intrinsics.stringPlus("connectionAcquired: ", connection));
   }

   public open fun connectionReleased(call: Call, connection: Connection) {
      this.logWithTime("connectionReleased");
   }

   public open fun requestHeadersStart(call: Call) {
      this.logWithTime("requestHeadersStart");
   }

   public open fun requestHeadersEnd(call: Call, request: Request) {
      this.logWithTime("requestHeadersEnd");
   }

   public open fun requestBodyStart(call: Call) {
      this.logWithTime("requestBodyStart");
   }

   public open fun requestBodyEnd(call: Call, byteCount: Long) {
      this.logWithTime(Intrinsics.stringPlus("requestBodyEnd: byteCount=", byteCount));
   }

   public open fun requestFailed(call: Call, ioe: IOException) {
      this.logWithTime(Intrinsics.stringPlus("requestFailed: ", ioe));
   }

   public open fun responseHeadersStart(call: Call) {
      this.logWithTime("responseHeadersStart");
   }

   public open fun responseHeadersEnd(call: Call, response: Response) {
      this.logWithTime(Intrinsics.stringPlus("responseHeadersEnd: ", response));
   }

   public open fun responseBodyStart(call: Call) {
      this.logWithTime("responseBodyStart");
   }

   public open fun responseBodyEnd(call: Call, byteCount: Long) {
      this.logWithTime(Intrinsics.stringPlus("responseBodyEnd: byteCount=", byteCount));
   }

   public open fun responseFailed(call: Call, ioe: IOException) {
      this.logWithTime(Intrinsics.stringPlus("responseFailed: ", ioe));
   }

   public open fun callEnd(call: Call) {
      this.logWithTime("callEnd");
   }

   public open fun callFailed(call: Call, ioe: IOException) {
      this.logWithTime(Intrinsics.stringPlus("callFailed: ", ioe));
   }

   public open fun canceled(call: Call) {
      this.logWithTime("canceled");
   }

   public open fun satisfactionFailure(call: Call, response: Response) {
      this.logWithTime(Intrinsics.stringPlus("satisfactionFailure: ", response));
   }

   public open fun cacheHit(call: Call, response: Response) {
      this.logWithTime(Intrinsics.stringPlus("cacheHit: ", response));
   }

   public open fun cacheMiss(call: Call) {
      this.logWithTime("cacheMiss");
   }

   public open fun cacheConditionalHit(call: Call, cachedResponse: Response) {
      this.logWithTime(Intrinsics.stringPlus("cacheConditionalHit: ", cachedResponse));
   }

   private fun logWithTime(message: String) {
      this.logger.log("" + '[' + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.startNs) + " ms] " + message);
   }

   open class Factory @JvmOverloads  public constructor(logger: Logger) : okhttp3.EventListener.Factory {
      private final val logger: Logger

      init {
         this.logger = logger;
      }

      public open fun create(call: Call): EventListener {
         return new LoggingEventListener(this.logger, null);
      }

      @JvmOverloads
      open fun Factory() {
         this(null, 1, null);
      }
   }
}
