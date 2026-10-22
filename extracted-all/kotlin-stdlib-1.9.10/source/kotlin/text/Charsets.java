package kotlin.text

import java.nio.charset.Charset

object Charsets private constructor() {
   @JvmStatic
   public Charsets INSTANCE = new Charsets();

   public final val ISO_8859_1: Charset
   public final val US_ASCII: Charset
   public final val UTF_16: Charset
   public final val UTF_16BE: Charset
   public final val UTF_16LE: Charset
   public final val UTF_32: Charset
      public final get() {
         var var10000: Charset = utf_32;
         if (utf_32 == null) {
            var `$this$_get_UTF_32__u24lambda_u240`: Charsets = this;
            var10000 = Charset.forName("UTF-32");
            utf_32 = var10000;
            var10000 = var10000;
         }

         return var10000;
      }

   public final val UTF_32BE: Charset
      public final get() {
         var var10000: Charset = utf_32be;
         if (utf_32be == null) {
            var `$this$_get_UTF_32BE__u24lambda_u242`: Charsets = this;
            var10000 = Charset.forName("UTF-32BE");
            utf_32be = var10000;
            var10000 = var10000;
         }

         return var10000;
      }

   public final val UTF_32LE: Charset
      public final get() {
         var var10000: Charset = utf_32le;
         if (utf_32le == null) {
            var `$this$_get_UTF_32LE__u24lambda_u241`: Charsets = this;
            var10000 = Charset.forName("UTF-32LE");
            utf_32le = var10000;
            var10000 = var10000;
         }

         return var10000;
      }

   public final val UTF_8: Charset
   private final var utf_32: Charset?
   private final var utf_32be: Charset?
   private final var utf_32le: Charset?


   @JvmStatic
   fun {
      var var10000: Charset = Charset.forName("UTF-8");
      UTF_8 = var10000;
      var10000 = Charset.forName("UTF-16");
      UTF_16 = var10000;
      var10000 = Charset.forName("UTF-16BE");
      UTF_16BE = var10000;
      var10000 = Charset.forName("UTF-16LE");
      UTF_16LE = var10000;
      var10000 = Charset.forName("US-ASCII");
      US_ASCII = var10000;
      var10000 = Charset.forName("ISO-8859-1");
      ISO_8859_1 = var10000;
   }
}
