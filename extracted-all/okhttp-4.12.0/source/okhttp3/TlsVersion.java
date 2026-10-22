package okhttp3

enum class TlsVersion {
   @JvmStatic
   TLS_1_3("TLSv1.3"),
   @JvmStatic
   TLS_1_2("TLSv1.2"),
   @JvmStatic
   TLS_1_1("TLSv1.1"),
   @JvmStatic
   TLS_1_0("TLSv1"),
   @JvmStatic
   SSL_3_0("SSLv3");
   @JvmStatic
   public TlsVersion.Companion Companion = new TlsVersion.Companion(null);
   private java.lang.String javaName;

   public final val javaName: String

   init {
      this.javaName = javaName;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "javaName", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_javaName")
   public fun javaName(): String {
      return this.javaName;
   }

   @JvmStatic
   fun forJavaName(javaName: java.lang.String): TlsVersion {
      return Companion.forJavaName(javaName);
   }

   companion object Companion private constructor() {
      public fun forJavaName(javaName: String): TlsVersion {
         var var10000: TlsVersion;
         switch (javaName.hashCode()) {
            case -503070503:
               if (!javaName.equals("TLSv1.1")) {
                  throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
               }

               var10000 = TlsVersion.TLS_1_1;
               break;
            case -503070502:
               if (!javaName.equals("TLSv1.2")) {
                  throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
               }

               var10000 = TlsVersion.TLS_1_2;
               break;
            case -503070501:
               if (!javaName.equals("TLSv1.3")) {
                  throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
               }

               var10000 = TlsVersion.TLS_1_3;
               break;
            case 79201641:
               if (!javaName.equals("SSLv3")) {
                  throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
               }

               var10000 = TlsVersion.SSL_3_0;
               break;
            case 79923350:
               if (javaName.equals("TLSv1")) {
                  var10000 = TlsVersion.TLS_1_0;
                  break;
               }

               throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
            default:
               throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
         }

         return var10000;
      }
   }
}
