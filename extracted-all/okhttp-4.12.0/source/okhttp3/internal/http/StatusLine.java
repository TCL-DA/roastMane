package okhttp3.internal.http

import java.net.ProtocolException
import okhttp3.Protocol
import okhttp3.Response

class StatusLine(protocol: Protocol, code: Int, message: String) {
   @JvmStatic
   public StatusLine.Companion Companion = new StatusLine.Companion(null);
   @JvmStatic
   public int HTTP_TEMP_REDIRECT = 307;
   @JvmStatic
   public int HTTP_PERM_REDIRECT = 308;
   @JvmStatic
   public int HTTP_MISDIRECTED_REQUEST = 421;
   @JvmStatic
   public int HTTP_CONTINUE = 100;

   public final val code: Int
   public final val message: String
   public final val protocol: Protocol

   init {
      this.protocol = protocol;
      this.code = code;
      this.message = message;
   }

   public override fun toString(): String {
      var var1: StringBuilder = new StringBuilder();
      if (this.protocol === Protocol.HTTP_1_0) {
         var1.append("HTTP/1.0");
      } else {
         var1.append("HTTP/1.1");
      }

      var1.append(' ').append(this.code);
      var1.append(' ').append(this.message);
      var var10000: java.lang.String = var1.toString();
      return var10000;
   }

   companion object Companion private constructor() {
      public const val HTTP_CONTINUE: Int
      public const val HTTP_MISDIRECTED_REQUEST: Int
      public const val HTTP_PERM_REDIRECT: Int
      public const val HTTP_TEMP_REDIRECT: Int


      public fun get(response: Response): StatusLine {
         return new StatusLine(response.protocol(), response.code(), response.message());
      }

      @Throws(java/io/IOException::class)
      public fun parse(statusLine: String): StatusLine {
         var var8: Byte;
         var var9: Protocol;
         if (StringsKt.startsWith$default(statusLine, "HTTP/1.", false, 2, null)) {
            if (statusLine.length() < 9 || statusLine.charAt(8) != ' ') {
               throw new ProtocolException("Unexpected status line: " + statusLine);
            }

            var code: Int = statusLine.charAt(7) - '0';
            var8 = 9;
            var var10000: Protocol;
            switch (httpMinorVersion) {
               case 0:
                  var10000 = Protocol.HTTP_1_0;
                  break;
               case 1:
                  var10000 = Protocol.HTTP_1_1;
                  break;
               default:
                  throw new ProtocolException("Unexpected status line: " + statusLine);
            }

            var9 = var10000;
         } else {
            if (!StringsKt.startsWith$default(statusLine, "ICY ", false, 2, null)) {
               throw new ProtocolException("Unexpected status line: " + statusLine);
            }

            var9 = Protocol.HTTP_1_0;
            var8 = 4;
         }

         if (statusLine.length() < var8 + 3) {
            throw new ProtocolException("Unexpected status line: " + statusLine);
         } else {
            var message: Int;
            try {
               var var12: java.lang.String = statusLine.substring(var8, var8 + 3);
               message = Integer.parseInt(var12);
            } catch (NumberFormatException var7) {
               throw new ProtocolException("Unexpected status line: " + statusLine);
            }

            var var11: java.lang.String = "";
            if (statusLine.length() > var8 + 3) {
               if (statusLine.charAt(var8 + 3) != ' ') {
                  throw new ProtocolException("Unexpected status line: " + statusLine);
               }

               var var13: java.lang.String = statusLine.substring(var8 + 4);
               var11 = var13;
            }

            return new StatusLine(var9, message, var11);
         }
      }
   }
}
