package okhttp3.internal.authenticator

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy
import java.net.SocketAddress
import java.net.Authenticator.RequestorType
import java.net.Proxy.Type
import okhttp3.Address
import okhttp3.Authenticator
import okhttp3.Challenge
import okhttp3.Credentials
import okhttp3.Dns
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class JavaNetAuthenticator(defaultDns: Dns) : Authenticator {
   private final val defaultDns: Dns

   init {
      this.defaultDns = defaultDns;
   }

   @Throws(java/io/IOException::class)
   public override fun authenticate(route: Route?, response: Response): Request? {
      var challenges: java.util.List;
      var request: Request;
      var url: HttpUrl;
      var proxyAuthorization: Boolean;
      var var10000: Proxy;
      label48: {
         challenges = response.challenges();
         request = response.request();
         url = request.url();
         proxyAuthorization = response.code() == 407;
         if (route != null) {
            var10000 = route.proxy();
            if (var10000 != null) {
               break label48;
            }
         }

         var10000 = Proxy.NO_PROXY;
      }

      var proxy: Proxy = var10000;

      for (Challenge challenge : challenges) {
         if (StringsKt.equals("Basic", challenge.scheme(), true)) {
            label39: {
               if (route != null) {
                  var var16: Address = route.address();
                  if (var16 != null) {
                     var17 = var16.dns();
                     if (var17 != null) {
                        break label39;
                     }
                  }
               }

               var17 = this.defaultDns;
            }

            var var20: PasswordAuthentication;
            if (proxyAuthorization) {
               var var18: SocketAddress = proxy.address();
               var credentialHeader: InetSocketAddress = var18 as InetSocketAddress;
               var var19: java.lang.String = (var18 as InetSocketAddress).getHostName();
               var20 = java.net.Authenticator.requestPasswordAuthentication(
                  var19,
                  this.connectToInetAddress(proxy, url, var17),
                  credentialHeader.getPort(),
                  url.scheme(),
                  challenge.realm(),
                  challenge.scheme(),
                  url.url(),
                  RequestorType.PROXY
               );
            } else {
               var var21: java.lang.String = url.host();
               var20 = java.net.Authenticator.requestPasswordAuthentication(
                  var21,
                  this.connectToInetAddress(proxy, url, var17),
                  url.port(),
                  url.scheme(),
                  challenge.realm(),
                  challenge.scheme(),
                  url.url(),
                  RequestorType.SERVER
               );
            }

            if (var20 != null) {
               var var15: java.lang.String = if (proxyAuthorization) "Proxy-Authorization" else "Authorization";
               var var22: java.lang.String = var20.getUserName();
               var var10001: CharArray = var20.getPassword();
               return request.newBuilder().header(var15, Credentials.basic(var22, new java.lang.String(var10001), challenge.charset())).build();
            }
         }
      }

      return null;
   }

   @Throws(java/io/IOException::class)
   private fun Proxy.connectToInetAddress(url: HttpUrl, dns: Dns): InetAddress {
      var var10000: Type = `$this$connectToInetAddress`.type();
      var var4: InetAddress;
      if ((if (var10000 == null) -1 else JavaNetAuthenticator.WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) == 1) {
         var4 = CollectionsKt.first(dns.lookup(url.host())) as InetAddress;
      } else {
         var var5: SocketAddress = `$this$connectToInetAddress`.address();
         var4 = (var5 as InetSocketAddress).getAddress();
      }

      return var4;
   }

   fun JavaNetAuthenticator() {
      this(null, 1, null);
   }
}
