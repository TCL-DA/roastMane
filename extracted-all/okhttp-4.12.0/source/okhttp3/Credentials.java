package okhttp3

import java.nio.charset.Charset
import okio.ByteString

object Credentials private constructor() {
   @JvmStatic
   public Credentials INSTANCE = new Credentials();


   @JvmOverloads
   @JvmStatic
   public fun basic(username: String, password: String, charset: Charset): String {
      return "Basic " + ByteString.Companion.encodeString(username + ':' + password, charset).base64();
   }

   @JvmOverloads
   @JvmStatic
   fun basic(username: java.lang.String, password: java.lang.String): java.lang.String {
      return basic$default(username, password, null, 4, null);
   }
}
