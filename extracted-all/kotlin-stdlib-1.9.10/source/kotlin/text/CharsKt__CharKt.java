package kotlin.text

import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nChar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Char.kt\nkotlin/text/CharsKt__CharKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,344:1\n1#2:345\n*E\n"])
// $VF: Class flags could not be determined
internal class CharsKt__CharKt : CharsKt__CharJVMKt {
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Char.digitToInt(): Int {
      var var1: Int = CharsKt.digitOf(`$this$digitToInt`, 10);
      if (var1 < 0) {
         throw new IllegalArgumentException("Char " + `$this$digitToInt` + " is not a decimal digit");
      } else {
         return var1;
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Char.digitToInt(radix: Int): Int {
      var var10000: Int = CharsKt.digitToIntOrNull(`$this$digitToInt`, radix);
      if (var10000 != null) {
         return var10000;
      } else {
         throw new IllegalArgumentException("Char " + `$this$digitToInt` + " is not a digit in the given radix=" + radix);
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Char.digitToIntOrNull(): Int? {
      var var1: Int = CharsKt.digitOf(`$this$digitToIntOrNull`, 10);
      return if (var1.intValue() >= 0) var1 else null;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Char.digitToIntOrNull(radix: Int): Int? {
      CharsKt.checkRadix(radix);
      var var2: Int = CharsKt.digitOf(`$this$digitToIntOrNull`, radix);
      return if (var2.intValue() >= 0) var2 else null;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Int.digitToChar(): Char {
      if (new IntRange(0, 9).contains(`$this$digitToChar`)) {
         return (char)(48 + `$this$digitToChar`);
      } else {
         throw new IllegalArgumentException("Int " + `$this$digitToChar` + " is not a decimal digit");
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Int.digitToChar(radix: Int): Char {
      if (!new IntRange(2, 36).contains(radix)) {
         throw new IllegalArgumentException("Invalid radix: " + radix + ". Valid radix values are in range 2..36");
      } else if (`$this$digitToChar` >= 0 && `$this$digitToChar` < radix) {
         return if (`$this$digitToChar` < 10) (char)(48 + `$this$digitToChar`) else (char)((char)(65 + `$this$digitToChar`) - '\n');
      } else {
         throw new IllegalArgumentException("Digit " + `$this$digitToChar` + " does not represent a valid digit in radix " + radix);
      }
   }

   @SinceKotlin(version = "1.5")
   @JvmStatic
   public fun Char.titlecase(): String {
      return _OneToManyTitlecaseMappingsKt.titlecaseImpl(`$this$titlecase`);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun Char.plus(other: String): String {
      return `$this$plus` + other;
   }

   @JvmStatic
   public fun Char.equals(other: Char, ignoreCase: Boolean): Boolean {
      if (`$this$equals` == other) {
         return true;
      } else if (!ignoreCase) {
         return false;
      } else {
         var thisUpper: Char = Character.toUpperCase(`$this$equals`);
         var otherUpper: Char = Character.toUpperCase(other);
         return thisUpper == otherUpper || Character.toLowerCase(thisUpper) == Character.toLowerCase(otherUpper);
      }
   }

   @JvmStatic
   public fun Char.isSurrogate(): Boolean {
      return new CharRange('\ud800', '\udfff').contains(`$this$isSurrogate`);
   }

   open fun CharsKt__CharKt() {
   }
}
