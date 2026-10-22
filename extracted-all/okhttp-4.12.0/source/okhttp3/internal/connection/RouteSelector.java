package okhttp3.internal.connection

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.SocketAddress
import java.net.SocketException
import java.net.URI
import java.net.UnknownHostException
import java.net.Proxy.Type
import java.util.ArrayList
import java.util.NoSuchElementException
import okhttp3.Address
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.HttpUrl
import okhttp3.Route
import okhttp3.internal.Util

class RouteSelector(address: Address, routeDatabase: RouteDatabase, call: Call, eventListener: EventListener) {
   @JvmStatic
   public RouteSelector.Companion Companion = new RouteSelector.Companion(null);

   private final val address: Address
   private final val call: Call
   private final val eventListener: EventListener
   private final var inetSocketAddresses: List<InetSocketAddress>
   private final var nextProxyIndex: Int
   private final val postponedRoutes: MutableList<Route>
   private final var proxies: List<Proxy>
   private final val routeDatabase: RouteDatabase

   init {
      this.address = address;
      this.routeDatabase = routeDatabase;
      this.call = call;
      this.eventListener = eventListener;
      this.proxies = CollectionsKt.emptyList();
      this.inetSocketAddresses = CollectionsKt.emptyList();
      this.postponedRoutes = new ArrayList<>();
      this.resetNextProxy(this.address.url(), this.address.proxy());
   }

   public operator fun hasNext(): Boolean {
      return this.hasNextProxy() || !this.postponedRoutes.isEmpty();
   }

   @Throws(java/io/IOException::class)
   public operator fun next(): okhttp3.internal.connection.RouteSelector.Selection {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var routes: java.util.List = new ArrayList();

         while (this.hasNextProxy()) {
            var proxy: Proxy = this.nextProxy();

            for (InetSocketAddress inetSocketAddress : this.inetSocketAddresses) {
               var route: Route = new Route(this.address, proxy, inetSocketAddress);
               if (this.routeDatabase.shouldPostpone(route)) {
                  this.postponedRoutes.add(route);
               } else {
                  routes.add(route);
               }
            }

            if (!routes.isEmpty()) {
               break;
            }
         }

         if (routes.isEmpty()) {
            CollectionsKt.addAll(routes, this.postponedRoutes);
            this.postponedRoutes.clear();
         }

         return new RouteSelector.Selection(routes);
      }
   }

   private fun resetNextProxy(url: HttpUrl, proxy: Proxy?) {
      this.eventListener.proxySelectStart(this.call, url);
      this.proxies = resetNextProxy$selectProxies(proxy, url, this);
      this.nextProxyIndex = 0;
      this.eventListener.proxySelectEnd(this.call, url, this.proxies);
   }

   private fun hasNextProxy(): Boolean {
      return this.nextProxyIndex < this.proxies.size();
   }

   @Throws(java/io/IOException::class)
   private fun nextProxy(): Proxy {
      if (!this.hasNextProxy()) {
         throw new SocketException("No route to " + this.address.url().host() + "; exhausted proxy configurations: " + this.proxies);
      } else {
         var result: Proxy = this.proxies.get(this.nextProxyIndex++);
         this.resetNextInetSocketAddress(result);
         return result;
      }
   }

   @Throws(java/io/IOException::class)
   private fun resetNextInetSocketAddress(proxy: Proxy) {
      var mutableInetSocketAddresses: java.util.List = new ArrayList();
      this.inetSocketAddresses = mutableInetSocketAddresses;
      var var8: java.lang.String;
      var var9: Int;
      if (proxy.type() != Type.DIRECT && proxy.type() != Type.SOCKS) {
         var addresses: SocketAddress = proxy.address();
         if (addresses !is InetSocketAddress) {
            throw new IllegalArgumentException(("Proxy.address() is not an InetSocketAddress: " + addresses.getClass()).toString());
         }

         var var10000: RouteSelector.Companion = Companion;
         var8 = var10000.getSocketHost(addresses as InetSocketAddress);
         var9 = (addresses as InetSocketAddress).getPort();
      } else {
         var8 = this.address.url().host();
         var9 = this.address.url().port();
      }

      if (1 > var9 || var9 >= 65536) {
         throw new SocketException("No route to " + var8 + ':' + var9 + "; port is out of range");
      } else {
         if (proxy.type() === Type.SOCKS) {
            mutableInetSocketAddresses.add(InetSocketAddress.createUnresolved(var8, var9));
         } else {
            var var14: java.util.List;
            if (Util.canParseAsIpAddress(var8)) {
               var14 = CollectionsKt.listOf(InetAddress.getByName(var8));
            } else {
               this.eventListener.dnsStart(this.call, var8);
               var result: java.util.List = this.address.dns().lookup(var8);
               if (result.isEmpty()) {
                  throw new UnknownHostException(this.address.dns() + " returned no addresses for " + var8);
               }

               this.eventListener.dnsEnd(this.call, var8, result);
               var14 = result;
            }

            for (InetAddress inetAddress : var14) {
               mutableInetSocketAddresses.add(new InetSocketAddress(inetAddress, var9));
            }
         }
      }
   }

   @JvmStatic
   fun `resetNextProxy$selectProxies`(`$proxy`: Proxy, `$url`: HttpUrl, `this$0`: RouteSelector): MutableList<Proxy> {
      if (`$proxy` != null) {
         return CollectionsKt.listOf(`$proxy`);
      } else {
         var uri: URI = `$url`.uri();
         if (uri.getHost() == null) {
            return Util.immutableListOf(Proxy.NO_PROXY);
         } else {
            var proxiesOrNull: java.util.List = `this$0`.address.proxySelector().select(uri);
            if (proxiesOrNull == null || proxiesOrNull.isEmpty()) {
               return Util.immutableListOf(Proxy.NO_PROXY);
            } else {
               return Util.toImmutableList(proxiesOrNull);
            }
         }
      }
   }

   companion object Companion private constructor() {
      public final val socketHost: String
         public final get() {
            var var10000: InetAddress = `$this$socketHost`.getAddress();
            if (var10000 == null) {
               var var4: java.lang.String = `$this$socketHost`.getHostName();
               return var4;
            } else {
               var var3: java.lang.String = var10000.getHostAddress();
               return var3;
            }
         }


   }

   class Selection(routes: List<Route>) {
      private final var nextRouteIndex: Int
      public final val routes: List<Route>

      init {
         this.routes = routes;
      }

      public operator fun hasNext(): Boolean {
         return this.nextRouteIndex < this.routes.size();
      }

      public operator fun next(): Route {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.routes.get(this.nextRouteIndex++);
         }
      }
   }
}
