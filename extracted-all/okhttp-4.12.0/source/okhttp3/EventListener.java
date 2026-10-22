package okhttp3

import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

abstract class EventListener {
   @JvmStatic
   public EventListener.Companion Companion = new EventListener.Companion(null);
   @JvmField
   @JvmStatic
   public EventListener NONE = new EventListener() {
   };

   public open fun callStart(call: Call) {
   }

   public open fun proxySelectStart(call: Call, url: HttpUrl) {
   }

   public open fun proxySelectEnd(call: Call, url: HttpUrl, proxies: List<Proxy>) {
   }

   public open fun dnsStart(call: Call, domainName: String) {
   }

   public open fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
   }

   public open fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
   }

   public open fun secureConnectStart(call: Call) {
   }

   public open fun secureConnectEnd(call: Call, handshake: Handshake?) {
   }

   public open fun connectEnd(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?) {
   }

   public open fun connectFailed(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?, ioe: IOException) {
   }

   public open fun connectionAcquired(call: Call, connection: Connection) {
   }

   public open fun connectionReleased(call: Call, connection: Connection) {
   }

   public open fun requestHeadersStart(call: Call) {
   }

   public open fun requestHeadersEnd(call: Call, request: Request) {
   }

   public open fun requestBodyStart(call: Call) {
   }

   public open fun requestBodyEnd(call: Call, byteCount: Long) {
   }

   public open fun requestFailed(call: Call, ioe: IOException) {
   }

   public open fun responseHeadersStart(call: Call) {
   }

   public open fun responseHeadersEnd(call: Call, response: Response) {
   }

   public open fun responseBodyStart(call: Call) {
   }

   public open fun responseBodyEnd(call: Call, byteCount: Long) {
   }

   public open fun responseFailed(call: Call, ioe: IOException) {
   }

   public open fun callEnd(call: Call) {
   }

   public open fun callFailed(call: Call, ioe: IOException) {
   }

   public open fun canceled(call: Call) {
   }

   public open fun satisfactionFailure(call: Call, response: Response) {
   }

   public open fun cacheHit(call: Call, response: Response) {
   }

   public open fun cacheMiss(call: Call) {
   }

   public open fun cacheConditionalHit(call: Call, cachedResponse: Response) {
   }

   companion object Companion private constructor() {
      public final val NONE: EventListener

   }

   fun interface Factory {
      public abstract fun create(call: Call): EventListener {
      }
   }
}
