package okhttp3

import java.io.IOException

enum class Protocol {
   @JvmStatic
   HTTP_1_0("http/1.0"),
   @JvmStatic
   HTTP_1_1("http/1.1"),
   /** @deprecated */
   @Deprecated(message = "OkHttp has dropped support for SPDY. Prefer {@link #HTTP_2}.")
   @JvmStatic
   SPDY_3("spdy/3.1"),
   @JvmStatic
   HTTP_2("h2"),
   @JvmStatic
   H2_PRIOR_KNOWLEDGE("h2_prior_knowledge"),
   @JvmStatic
   QUIC("quic");
   @JvmStatic
   public Protocol.Companion Companion = new Protocol.Companion(null);
   private java.lang.String protocol;

   private final val protocol: String

   init {
      this.protocol = protocol;
   }

   public override fun toString(): String {
      return this.protocol;
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   fun get(protocol: java.lang.String): Protocol {
      return Companion.get(protocol);
   }

   companion object Companion private constructor() {
      @Throws(java/io/IOException::class)
      public fun get(protocol: String): Protocol {
         var var10000: Protocol;
         if (protocol == Protocol.access$getProtocol$p(Protocol.HTTP_1_0)) {
            var10000 = Protocol.HTTP_1_0;
         } else if (protocol == Protocol.access$getProtocol$p(Protocol.HTTP_1_1)) {
            var10000 = Protocol.HTTP_1_1;
         } else if (protocol == Protocol.access$getProtocol$p(Protocol.H2_PRIOR_KNOWLEDGE)) {
            var10000 = Protocol.H2_PRIOR_KNOWLEDGE;
         } else if (protocol == Protocol.access$getProtocol$p(Protocol.HTTP_2)) {
            var10000 = Protocol.HTTP_2;
         } else if (protocol == Protocol.access$getProtocol$p(Protocol.SPDY_3)) {
            var10000 = Protocol.SPDY_3;
         } else {
            if (!(protocol == Protocol.access$getProtocol$p(Protocol.QUIC))) {
               throw new IOException("Unexpected protocol: " + protocol);
            }

            var10000 = Protocol.QUIC;
         }

         return var10000;
      }
   }
}
