package okhttp3

import java.net.InetAddress
import java.net.UnknownHostException

interface Dns {
   @JvmStatic
   Dns.Companion Companion = Dns.Companion.$$INSTANCE;
   @JvmField
   @JvmStatic
   Dns SYSTEM = new Dns.Companion.DnsSystem();

   @Throws(java/net/UnknownHostException::class)
   public abstract fun lookup(hostname: String): List<InetAddress> {
   }

   companion object Companion private constructor() {
      public final val SYSTEM: Dns


      private class DnsSystem : Dns {
         public override fun lookup(hostname: String): List<InetAddress> {
            try {
               var var10000: Array<InetAddress> = InetAddress.getAllByName(hostname);
               return ArraysKt.toList(var10000);
            } catch (NullPointerException var6) {
               var var3: UnknownHostException = new UnknownHostException("Broken system behaviour for dns lookup of " + hostname);
               var3.initCause(var6);
               throw var3 as java.lang.Throwable;
            }
         }
      }
   }
}
