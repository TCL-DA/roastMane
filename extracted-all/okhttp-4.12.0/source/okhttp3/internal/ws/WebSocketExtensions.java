package okhttp3.internal.ws

import okhttp3.Headers
import okhttp3.internal.Util

data class WebSocketExtensions(perMessageDeflate: Boolean,
   clientMaxWindowBits: Int?,
   clientNoContextTakeover: Boolean,
   serverMaxWindowBits: Int?,
   serverNoContextTakeover: Boolean,
   unknownValues: Boolean
) {
   @JvmStatic
   public WebSocketExtensions.Companion Companion = new WebSocketExtensions.Companion(null);
   @JvmStatic
   private java.lang.String HEADER_WEB_SOCKET_EXTENSION = "Sec-WebSocket-Extensions";

   public final val clientMaxWindowBits: Int?
   public final val clientNoContextTakeover: Boolean
   public final val perMessageDeflate: Boolean
   public final val serverMaxWindowBits: Int?
   public final val serverNoContextTakeover: Boolean
   public final val unknownValues: Boolean

   init {
      this.perMessageDeflate = perMessageDeflate;
      this.clientMaxWindowBits = clientMaxWindowBits;
      this.clientNoContextTakeover = clientNoContextTakeover;
      this.serverMaxWindowBits = serverMaxWindowBits;
      this.serverNoContextTakeover = serverNoContextTakeover;
      this.unknownValues = unknownValues;
   }

   public fun noContextTakeover(clientOriginated: Boolean): Boolean {
      return if (clientOriginated) this.clientNoContextTakeover else this.serverNoContextTakeover;
   }

   public operator fun component1(): Boolean {
      return this.perMessageDeflate;
   }

   public operator fun component2(): Int? {
      return this.clientMaxWindowBits;
   }

   public operator fun component3(): Boolean {
      return this.clientNoContextTakeover;
   }

   public operator fun component4(): Int? {
      return this.serverMaxWindowBits;
   }

   public operator fun component5(): Boolean {
      return this.serverNoContextTakeover;
   }

   public operator fun component6(): Boolean {
      return this.unknownValues;
   }

   public fun copy(
      perMessageDeflate: Boolean,
      clientMaxWindowBits: Int?,
      clientNoContextTakeover: Boolean,
      serverMaxWindowBits: Int?,
      serverNoContextTakeover: Boolean,
      unknownValues: Boolean
   ): WebSocketExtensions {
      return new WebSocketExtensions(
         perMessageDeflate, clientMaxWindowBits, clientNoContextTakeover, serverMaxWindowBits, serverNoContextTakeover, unknownValues
      );
   }

   public override fun toString(): String {
      return "WebSocketExtensions(perMessageDeflate="
         + this.perMessageDeflate
         + ", clientMaxWindowBits="
         + this.clientMaxWindowBits
         + ", clientNoContextTakeover="
         + this.clientNoContextTakeover
         + ", serverMaxWindowBits="
         + this.serverMaxWindowBits
         + ", serverNoContextTakeover="
         + this.serverNoContextTakeover
         + ", unknownValues="
         + this.unknownValues
         + ')';
   }

   public override fun hashCode(): Int {
      var var10000: Int = this.perMessageDeflate;
      if (this.perMessageDeflate) {
         var10000 = 1;
      }

      var10000 = (var10000 * 31 + (if (this.clientMaxWindowBits == null) 0 else this.clientMaxWindowBits.hashCode())) * 31;
      var var10001: Byte = this.clientNoContextTakeover;
      if (this.clientNoContextTakeover) {
         var10001 = 1;
      }

      var10000 = ((var10000 + var10001) * 31 + (if (this.serverMaxWindowBits == null) 0 else this.serverMaxWindowBits.hashCode())) * 31;
      var10001 = this.serverNoContextTakeover;
      if (this.serverNoContextTakeover) {
         var10001 = 1;
      }

      var10000 = (var10000 + var10001) * 31;
      var10001 = this.unknownValues;
      if (this.unknownValues) {
         var10001 = 1;
      }

      return var10000 + var10001;
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is WebSocketExtensions) {
         return false;
      } else {
         var var2: WebSocketExtensions = other as WebSocketExtensions;
         if (this.perMessageDeflate != (other as WebSocketExtensions).perMessageDeflate) {
            return false;
         } else if (!(this.clientMaxWindowBits == var2.clientMaxWindowBits)) {
            return false;
         } else if (this.clientNoContextTakeover != var2.clientNoContextTakeover) {
            return false;
         } else if (!(this.serverMaxWindowBits == var2.serverMaxWindowBits)) {
            return false;
         } else if (this.serverNoContextTakeover != var2.serverNoContextTakeover) {
            return false;
         } else {
            return this.unknownValues == var2.unknownValues;
         }
      }
   }

   fun WebSocketExtensions() {
      this(false, null, false, null, false, false, 63, null);
   }

   companion object Companion private constructor() {
      private const val HEADER_WEB_SOCKET_EXTENSION: String


      @Throws(java/io/IOException::class)
      public fun parse(responseHeaders: Headers): WebSocketExtensions {
         var compressionEnabled: Boolean = false;
         var clientMaxWindowBits: Int = null;
         var clientNoContextTakeover: Boolean = false;
         var serverMaxWindowBits: Int = null;
         var serverNoContextTakeover: Boolean = false;
         var unexpectedValues: Boolean = false;
         var i: Int = 0;

         for (int var9 = responseHeaders.size(); i < var9; i++) {
            if (StringsKt.equals(responseHeaders.name(i), "Sec-WebSocket-Extensions", true)) {
               var header: java.lang.String = responseHeaders.value(i);
               var pos: Int = 0;

               while (pos < header.length()) {
                  var extensionEnd: Int = Util.delimiterOffset$default(header, ',', pos, 0, 4, null);
                  var extensionTokenEnd: Int = Util.delimiterOffset(header, ';', pos, extensionEnd);
                  var extensionToken: java.lang.String = Util.trimSubstring(header, pos, extensionTokenEnd);
                  pos = extensionTokenEnd + 1;
                  if (StringsKt.equals(extensionToken, "permessage-deflate", true)) {
                     if (compressionEnabled) {
                        unexpectedValues = true;
                     }

                     compressionEnabled = true;

                     while (pos < extensionEnd) {
                        var parameterEnd: Int = Util.delimiterOffset(header, ';', pos, extensionEnd);
                        var equals: Int = Util.delimiterOffset(header, '=', pos, parameterEnd);
                        var name: java.lang.String = Util.trimSubstring(header, pos, equals);
                        var value: java.lang.String = if (equals < parameterEnd)
                           StringsKt.removeSurrounding(Util.trimSubstring(header, equals + 1, parameterEnd), "\"")
                           else
                           null;
                        pos = parameterEnd + 1;
                        if (StringsKt.equals(name, "client_max_window_bits", true)) {
                           if (clientMaxWindowBits != null) {
                              unexpectedValues = true;
                           }

                           clientMaxWindowBits = if (value != null) StringsKt.toIntOrNull(value) else null;
                           if (clientMaxWindowBits == null) {
                              unexpectedValues = true;
                           }
                        } else if (StringsKt.equals(name, "client_no_context_takeover", true)) {
                           if (clientNoContextTakeover) {
                              unexpectedValues = true;
                           }

                           if (value != null) {
                              unexpectedValues = true;
                           }

                           clientNoContextTakeover = true;
                        } else if (StringsKt.equals(name, "server_max_window_bits", true)) {
                           if (serverMaxWindowBits != null) {
                              unexpectedValues = true;
                           }

                           serverMaxWindowBits = if (value != null) StringsKt.toIntOrNull(value) else null;
                           if (serverMaxWindowBits == null) {
                              unexpectedValues = true;
                           }
                        } else if (StringsKt.equals(name, "server_no_context_takeover", true)) {
                           if (serverNoContextTakeover) {
                              unexpectedValues = true;
                           }

                           if (value != null) {
                              unexpectedValues = true;
                           }

                           serverNoContextTakeover = true;
                        } else {
                           unexpectedValues = true;
                        }
                     }
                  } else {
                     unexpectedValues = true;
                  }
               }
            }
         }

         return new WebSocketExtensions(
            compressionEnabled, clientMaxWindowBits, clientNoContextTakeover, serverMaxWindowBits, serverNoContextTakeover, unexpectedValues
         );
      }
   }
}
