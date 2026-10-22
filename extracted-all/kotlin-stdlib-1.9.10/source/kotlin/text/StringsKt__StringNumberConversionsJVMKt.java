package kotlin.text

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nStringNumberConversionsJVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StringNumberConversionsJVM.kt\nkotlin/text/StringsKt__StringNumberConversionsJVMKt\n*L\n1#1,274:1\n265#1,7:275\n265#1,7:282\n265#1,7:289\n265#1,7:296\n*S KotlinDebug\n*F\n+ 1 StringNumberConversionsJVM.kt\nkotlin/text/StringsKt__StringNumberConversionsJVMKt\n*L\n142#1:275,7\n149#1:282,7\n229#1:289,7\n240#1:296,7\n*E\n"])
// $VF: Class flags could not be determined
internal class StringsKt__StringNumberConversionsJVMKt : StringsKt__StringBuilderKt {
   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun Byte.toString(radix: Int): String {
      var var10000: java.lang.String = Integer.toString(`$this$toString`, CharsKt.checkRadix(CharsKt.checkRadix(radix)));
      return var10000;
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun Short.toString(radix: Int): String {
      var var10000: java.lang.String = Integer.toString(`$this$toString`, CharsKt.checkRadix(CharsKt.checkRadix(radix)));
      return var10000;
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun Int.toString(radix: Int): String {
      var var10000: java.lang.String = Integer.toString(`$this$toString`, CharsKt.checkRadix(radix));
      return var10000;
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun Long.toString(radix: Int): String {
      var var10000: java.lang.String = java.lang.Long.toString(`$this$toString`, CharsKt.checkRadix(radix));
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun String?.toBoolean(): Boolean {
      return java.lang.Boolean.parseBoolean(`$this$toBoolean`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.toByte(): Byte {
      return java.lang.Byte.parseByte(`$this$toByte`);
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun String.toByte(radix: Int): Byte {
      return java.lang.Byte.parseByte(`$this$toByte`, CharsKt.checkRadix(radix));
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.toShort(): Short {
      return java.lang.Short.parseShort(`$this$toShort`);
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun String.toShort(radix: Int): Short {
      return java.lang.Short.parseShort(`$this$toShort`, CharsKt.checkRadix(radix));
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.toInt(): Int {
      return Integer.parseInt(`$this$toInt`);
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun String.toInt(radix: Int): Int {
      return Integer.parseInt(`$this$toInt`, CharsKt.checkRadix(radix));
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.toLong(): Long {
      return java.lang.Long.parseLong(`$this$toLong`);
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun String.toLong(radix: Int): Long {
      return java.lang.Long.parseLong(`$this$toLong`, CharsKt.checkRadix(radix));
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.toFloat(): Float {
      return java.lang.Float.parseFloat(`$this$toFloat`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.toDouble(): Double {
      return java.lang.Double.parseDouble(`$this$toDouble`);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun String.toFloatOrNull(): Float? {
      var p0: java.lang.Float;
      try {
         p0 = if (ScreenFloatValueRegEx.value.matches(`$this$toFloatOrNull`)) java.lang.Float.parseFloat(`$this$toFloatOrNull`) else null;
      } catch (NumberFormatException var4) {
         p0 = null;
      }

      return p0;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun String.toDoubleOrNull(): Double? {
      var p0: java.lang.Double;
      try {
         p0 = if (ScreenFloatValueRegEx.value.matches(`$this$toDoubleOrNull`)) java.lang.Double.parseDouble(`$this$toDoubleOrNull`) else null;
      } catch (NumberFormatException var4) {
         p0 = null;
      }

      return p0;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun String.toBigInteger(): BigInteger {
      return new BigInteger(`$this$toBigInteger`);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun String.toBigInteger(radix: Int): BigInteger {
      return new BigInteger(`$this$toBigInteger`, CharsKt.checkRadix(radix));
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun String.toBigIntegerOrNull(): BigInteger? {
      return StringsKt.toBigIntegerOrNull(`$this$toBigIntegerOrNull`, 10);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun String.toBigIntegerOrNull(radix: Int): BigInteger? {
      CharsKt.checkRadix(radix);
      var length: Int = `$this$toBigIntegerOrNull`.length();
      switch (length) {
         case 0:
            return null;
         case 1:
            if (CharsKt.digitOf(`$this$toBigIntegerOrNull`.charAt(0), radix) < 0) {
               return null;
            }
            break;
         default:
            for (int index = $this$toBigIntegerOrNull.charAt(0) == '-' ? 1 : 0; index < length; index++) {
               if (CharsKt.digitOf(`$this$toBigIntegerOrNull`.charAt(index), radix) < 0) {
                  return null;
               }
            }
      }

      return new BigInteger(`$this$toBigIntegerOrNull`, CharsKt.checkRadix(radix));
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun String.toBigDecimal(): BigDecimal {
      return new BigDecimal(`$this$toBigDecimal`);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun String.toBigDecimal(mathContext: MathContext): BigDecimal {
      return new BigDecimal(`$this$toBigDecimal`, mathContext);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun String.toBigDecimalOrNull(): BigDecimal? {
      var it: BigDecimal;
      try {
         it = if (ScreenFloatValueRegEx.value.matches(`$this$toBigDecimalOrNull`)) new BigDecimal(`$this$toBigDecimalOrNull`) else null;
      } catch (NumberFormatException var4) {
         it = null;
      }

      return it;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun String.toBigDecimalOrNull(mathContext: MathContext): BigDecimal? {
      var var5: BigDecimal;
      try {
         var5 = if (ScreenFloatValueRegEx.value.matches(`$this$toBigDecimalOrNull`)) new BigDecimal(`$this$toBigDecimalOrNull`, mathContext) else null;
      } catch (NumberFormatException var6) {
         var5 = null;
      }

      return var5;
   }

   @JvmStatic
   private inline fun <T> screenFloatValue(str: String, parse: (String) -> T): T? {
      var var3: Any;
      try {
         var3 = if (ScreenFloatValueRegEx.value.matches(str)) parse.invoke(str) else null;
      } catch (NumberFormatException var5) {
         var3 = null;
      }

      return (T)var3;
   }

   open fun StringsKt__StringNumberConversionsJVMKt() {
   }
}
