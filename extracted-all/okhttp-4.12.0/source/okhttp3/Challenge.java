package okhttp3

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Collections
import java.util.LinkedHashMap
import java.util.Locale
import java.util.Map.Entry

class Challenge(scheme: String, authParams: Map<String?, String>) {
   public final val authParams: Map<String?, String>
      public final get() {
         return this.authParams;
      }

   public final val charset: Charset
      public final get() {
         var charset: java.lang.String = this.authParams.get("charset");
         if (charset != null) {
            try {
               var var4: Charset = Charset.forName(charset);
               return var4;
            } catch (Exception var3) {
            }
         }

         var var10000: Charset = StandardCharsets.ISO_8859_1;
         return var10000;
      }

   public final val realm: String?
      public final get() {
         return this.authParams.get("realm");
      }

   public final val scheme: String

   init {
      this.scheme = scheme;
      var newAuthParams: java.util.Map = new LinkedHashMap();

      for (Entry var5 : authParams.entrySet()) {
         var key: java.lang.String = var5.getKey() as java.lang.String;
         var value: java.lang.String = var5.getValue() as java.lang.String;
         var var10: java.lang.String;
         if (key != null) {
            var var10000: Locale = Locale.US;
            var10 = key.toLowerCase(var10000);
         } else {
            var10 = null;
         }

         newAuthParams.put(var10, value);
      }

      var var10001: java.util.Map = Collections.unmodifiableMap(newAuthParams);
      this.authParams = var10001;
   }

   public constructor(scheme: String, realm: String)  {
      var var10002: java.util.Map = Collections.singletonMap("realm", realm);
      this(scheme, var10002);
   }

   public fun withCharset(charset: Charset): Challenge {
      var authParams: java.util.Map = MapsKt.toMutableMap(this.authParams);
      var var10000: java.lang.String = charset.name();
      authParams.put("charset", var10000);
      return new Challenge(this.scheme, authParams);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "scheme", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_scheme")
   public fun scheme(): String {
      return this.scheme;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "authParams", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_authParams")
   public fun authParams(): Map<String?, String> {
      return this.authParams;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "realm", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_realm")
   public fun realm(): String? {
      return this.realm();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "charset", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_charset")
   public fun charset(): Charset {
      return this.charset();
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is Challenge && (other as Challenge).scheme == this.scheme && (other as Challenge).authParams == this.authParams;
   }

   public override fun hashCode(): Int {
      return 31 * (31 * 29 + this.scheme.hashCode()) + this.authParams.hashCode();
   }

   public override fun toString(): String {
      return this.scheme + " authParams=" + this.authParams;
   }
}
