package okhttp3

import java.net.InetSocketAddress
import java.net.Proxy
import java.net.Proxy.Type

class Route(address: Address, proxy: Proxy, socketAddress: InetSocketAddress) {
   public final val address: Address
   public final val proxy: Proxy
   public final val socketAddress: InetSocketAddress

   init {
      this.address = address;
      this.proxy = proxy;
      this.socketAddress = socketAddress;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "address", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_address")
   public fun address(): Address {
      return this.address;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "proxy", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_proxy")
   public fun proxy(): Proxy {
      return this.proxy;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "socketAddress", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_socketAddress")
   public fun socketAddress(): InetSocketAddress {
      return this.socketAddress;
   }

   public fun requiresTunnel(): Boolean {
      return this.address.sslSocketFactory() != null && this.proxy.type() === Type.HTTP;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is Route
         && (other as Route).address == this.address
         && (other as Route).proxy == this.proxy
         && (other as Route).socketAddress == this.socketAddress;
   }

   public override fun hashCode(): Int {
      return 31 * (31 * (31 * 17 + this.address.hashCode()) + this.proxy.hashCode()) + this.socketAddress.hashCode();
   }

   public override fun toString(): String {
      return "Route{" + this.socketAddress + '}';
   }
}
