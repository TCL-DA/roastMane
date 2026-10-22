package okhttp3

import java.net.Proxy
import java.net.ProxySelector
import java.util.Objects
import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import okhttp3.internal.Util

class Address(uriHost: String,
   uriPort: Int,
   dns: Dns,
   socketFactory: SocketFactory,
   sslSocketFactory: SSLSocketFactory?,
   hostnameVerifier: HostnameVerifier?,
   certificatePinner: CertificatePinner?,
   proxyAuthenticator: Authenticator,
   proxy: Proxy?,
   protocols: List<Protocol>,
   connectionSpecs: List<ConnectionSpec>,
   proxySelector: ProxySelector
) {
   public final val certificatePinner: CertificatePinner?
   public final val connectionSpecs: List<ConnectionSpec>
      public final get() {
         return this.connectionSpecs;
      }

   public final val dns: Dns
   public final val hostnameVerifier: HostnameVerifier?
   public final val protocols: List<Protocol>
      public final get() {
         return this.protocols;
      }

   public final val proxy: Proxy?
   public final val proxyAuthenticator: Authenticator
   public final val proxySelector: ProxySelector
   public final val socketFactory: SocketFactory
   public final val sslSocketFactory: SSLSocketFactory?
   public final val url: HttpUrl
      public final get() {
         return this.url;
      }


   init {
      this.dns = dns;
      this.socketFactory = socketFactory;
      this.sslSocketFactory = sslSocketFactory;
      this.hostnameVerifier = hostnameVerifier;
      this.certificatePinner = certificatePinner;
      this.proxyAuthenticator = proxyAuthenticator;
      this.proxy = proxy;
      this.proxySelector = proxySelector;
      this.url = new HttpUrl.Builder().scheme(if (this.sslSocketFactory != null) "https" else "http").host(uriHost).port(uriPort).build();
      this.protocols = Util.toImmutableList(protocols);
      this.connectionSpecs = Util.toImmutableList(connectionSpecs);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "url", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_url")
   public fun url(): HttpUrl {
      return this.url;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "dns", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_dns")
   public fun dns(): Dns {
      return this.dns;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "socketFactory", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_socketFactory")
   public fun socketFactory(): SocketFactory {
      return this.socketFactory;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "proxyAuthenticator", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_proxyAuthenticator")
   public fun proxyAuthenticator(): Authenticator {
      return this.proxyAuthenticator;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "protocols", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_protocols")
   public fun protocols(): List<Protocol> {
      return this.protocols;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "connectionSpecs", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_connectionSpecs")
   public fun connectionSpecs(): List<ConnectionSpec> {
      return this.connectionSpecs;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "proxySelector", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_proxySelector")
   public fun proxySelector(): ProxySelector {
      return this.proxySelector;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "proxy", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_proxy")
   public fun proxy(): Proxy? {
      return this.proxy;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "sslSocketFactory", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_sslSocketFactory")
   public fun sslSocketFactory(): SSLSocketFactory? {
      return this.sslSocketFactory;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "hostnameVerifier", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_hostnameVerifier")
   public fun hostnameVerifier(): HostnameVerifier? {
      return this.hostnameVerifier;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "certificatePinner", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_certificatePinner")
   public fun certificatePinner(): CertificatePinner? {
      return this.certificatePinner;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is Address && this.url == (other as Address).url && this.equalsNonHost$okhttp(other as Address);
   }

   public override fun hashCode(): Int {
      return 31
            * (
               31
                     * (
                        31
                              * (
                                 31
                                       * (
                                          31
                                                * (
                                                   31
                                                         * (
                                                            31
                                                                  * (
                                                                     31 * (31 * (31 * 17 + this.url.hashCode()) + this.dns.hashCode())
                                                                        + this.proxyAuthenticator.hashCode()
                                                                  )
                                                               + this.protocols.hashCode()
                                                         )
                                                      + this.connectionSpecs.hashCode()
                                                )
                                             + this.proxySelector.hashCode()
                                       )
                                    + Objects.hashCode(this.proxy)
                              )
                           + Objects.hashCode(this.sslSocketFactory)
                     )
                  + Objects.hashCode(this.hostnameVerifier)
            )
         + Objects.hashCode(this.certificatePinner);
   }

   internal fun equalsNonHost(that: Address): Boolean {
      return this.dns == that.dns
         && this.proxyAuthenticator == that.proxyAuthenticator
         && this.protocols == that.protocols
         && this.connectionSpecs == that.connectionSpecs
         && this.proxySelector == that.proxySelector
         && this.proxy == that.proxy
         && this.sslSocketFactory == that.sslSocketFactory
         && this.hostnameVerifier == that.hostnameVerifier
         && this.certificatePinner == that.certificatePinner
         && this.url.port() == that.url.port();
   }

   public override fun toString(): String {
      return "Address{"
         + this.url.host()
         + ':'
         + this.url.port()
         + ", "
         + (if (this.proxy != null) "proxy=" + this.proxy else "proxySelector=" + this.proxySelector)
         + '}';
   }
}
