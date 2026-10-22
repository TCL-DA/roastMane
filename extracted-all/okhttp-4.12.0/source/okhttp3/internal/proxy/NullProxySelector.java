package okhttp3.internal.proxy

import java.io.IOException
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nNullProxySelector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NullProxySelector.kt\nokhttp3/internal/proxy/NullProxySelector\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,36:1\n1#2:37\n*E\n"])
object NullProxySelector private constructor() : ProxySelector {
   @JvmStatic
   public NullProxySelector INSTANCE = new NullProxySelector();


   public override fun select(uri: URI?): List<Proxy> {
      if (uri == null) {
         throw new IllegalArgumentException("uri must not be null".toString());
      } else {
         return CollectionsKt.listOf(Proxy.NO_PROXY);
      }
   }

   public override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
   }
}
