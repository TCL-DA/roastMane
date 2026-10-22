package okhttp3.internal.platform

import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.Arrays
import javax.net.ssl.SSLSocket
import okhttp3.Protocol

class Jdk8WithJettyBootPlatform(putMethod: Method, getMethod: Method, removeMethod: Method, clientProviderClass: Class<*>, serverProviderClass: Class<*>)
   : Platform {
   @JvmStatic
   public Jdk8WithJettyBootPlatform.Companion Companion = new Jdk8WithJettyBootPlatform.Companion(null);

   private final val clientProviderClass: Class<*>
   private final val getMethod: Method
   private final val putMethod: Method
   private final val removeMethod: Method
   private final val serverProviderClass: Class<*>

   init {
      this.putMethod = putMethod;
      this.getMethod = getMethod;
      this.removeMethod = removeMethod;
      this.clientProviderClass = clientProviderClass;
      this.serverProviderClass = serverProviderClass;
   }

   public override fun configureTlsExtensions(sslSocket: SSLSocket, hostname: String?, protocols: List<Protocol>) {
      var names: java.util.List = Platform.Companion.alpnProtocolNames(protocols);

      try {
         this.putMethod
            .invoke(
               null,
               sslSocket,
               Proxy.newProxyInstance(
                  Platform.class.getClassLoader(),
                  new Class[]{this.clientProviderClass, this.serverProviderClass},
                  new Jdk8WithJettyBootPlatform.AlpnProvider(names)
               )
            );
      } catch (InvocationTargetException var7) {
         throw new AssertionError("failed to set ALPN", var7);
      } catch (IllegalAccessException var8) {
         throw new AssertionError("failed to set ALPN", var8);
      }
   }

   public override fun afterHandshake(sslSocket: SSLSocket) {
      try {
         this.removeMethod.invoke(null, sslSocket);
      } catch (IllegalAccessException var3) {
         throw new AssertionError("failed to remove ALPN", var3);
      } catch (InvocationTargetException var4) {
         throw new AssertionError("failed to remove ALPN", var4);
      }
   }

   public override fun getSelectedProtocol(sslSocket: SSLSocket): String? {
      try {
         var var6: InvocationHandler = Proxy.getInvocationHandler(this.getMethod.invoke(null, sslSocket));
         var e: Jdk8WithJettyBootPlatform.AlpnProvider = var6 as Jdk8WithJettyBootPlatform.AlpnProvider;
         if (!(var6 as Jdk8WithJettyBootPlatform.AlpnProvider).getUnsupported() && (var6 as Jdk8WithJettyBootPlatform.AlpnProvider).getSelected() == null) {
            Platform.log$default(this, "ALPN callback dropped: HTTP/2 is disabled. Is alpn-boot on the boot class path?", 0, null, 6, null);
            return null;
         } else {
            return if (e.getUnsupported()) null else e.getSelected();
         }
      } catch (InvocationTargetException var4) {
         throw new AssertionError("failed to get ALPN selected protocol", var4);
      } catch (IllegalAccessException var5) {
         throw new AssertionError("failed to get ALPN selected protocol", var5);
      }
   }

   private class AlpnProvider(protocols: List<String>) : InvocationHandler {
      private final val protocols: List<String>
      public final var selected: String?
         internal set
      public final var unsupported: Boolean
         internal set

      init {
         this.protocols = protocols;
      }

      @Throws(java/lang/Throwable::class)
      public override operator fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
         var var10000: Array<Any> = args;
         if (args == null) {
            var10000 = new Object[0];
         }

         var methodName: java.lang.String = method.getName();
         var returnType: Class = method.getReturnType();
         if (methodName == "supports" && java.lang.Boolean::class.javaPrimitiveType == returnType) {
            return true;
         } else if (methodName == "unsupported" && Void::class.javaPrimitiveType == returnType) {
            this.unsupported = true;
            return null;
         } else if (methodName == "protocols" && var10000.length == 0) {
            return this.protocols;
         } else if ((methodName == "selectProtocol" || methodName == "select")
            && java.lang.String::class.java == returnType
            && var10000.length == 1
            && var10000[0] is java.util.List) {
            var var11: Any = var10000[0];
            var peerProtocols: java.util.List = var11 as java.util.List;
            var i: Int = 0;
            var var9: Int = peerProtocols.size();
            if (0 <= var9) {
               while (true) {
                  var10000 = (Object[])peerProtocols.get(i);
                  var protocol: java.lang.String = var10000 as java.lang.String;
                  if (this.protocols.contains(var10000 as java.lang.String)) {
                     this.selected = protocol;
                     return this.selected;
                  }

                  if (i == var9) {
                     break;
                  }

                  i++;
               }
            }

            this.selected = this.protocols.get(0);
            return this.selected;
         } else if ((methodName == "protocolSelected" || methodName == "selected") && var10000.length == 1) {
            var var10001: Any = var10000[0];
            this.selected = var10001 as java.lang.String;
            return null;
         } else {
            return method.invoke(this, Arrays.copyOf(var10000, var10000.length));
         }
      }
   }

   companion object Companion private constructor() {
      public fun buildIfSupported(): Platform? {
         var jvmVersion: java.lang.String = System.getProperty("java.specification.version", "unknown");

         try {
            if (Integer.parseInt(jvmVersion) >= 9) {
               return null;
            }
         } catch (NumberFormatException var13) {
         }

         try {
            var alpnClass: Class = Class.forName("org.eclipse.jetty.alpn.ALPN", true, null);
            var providerClass: Class = Class.forName("org.eclipse.jetty.alpn.ALPN" + "$Provider", true, null);
            var clientProviderClass: Class = Class.forName("org.eclipse.jetty.alpn.ALPN" + "$ClientProvider", true, null);
            var serverProviderClass: Class = Class.forName("org.eclipse.jetty.alpn.ALPN" + "$ServerProvider", true, null);
            var putMethod: Method = alpnClass.getMethod("put", SSLSocket.class, providerClass);
            var var15: Method = alpnClass.getMethod("get", SSLSocket.class);
            var var16: Method = alpnClass.getMethod("remove", SSLSocket.class);
            return new Jdk8WithJettyBootPlatform(putMethod, var15, var16, clientProviderClass, serverProviderClass);
         } catch (ClassNotFoundException var11) {
         } catch (NoSuchMethodException var12) {
         }

         return null;
      }
   }
}
