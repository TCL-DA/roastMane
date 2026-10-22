package kotlin.text

import java.util.Locale
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class CharsKt__CharJVMKt {
   public final val category: CharCategory
      public final get() {
         return CharCategory.Companion.valueOf(Character.getType(`$this$category`));
      }

   public final val directionality: CharDirectionality
      public final get() {
         return CharDirectionality.Companion.valueOf(Character.getDirectionality(`$this$directionality`));
      }


   @InlineOnly
   @JvmStatic
   public inline fun Char.isDefined(): Boolean {
      return Character.isDefined(`$this$isDefined`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isLetter(): Boolean {
      return Character.isLetter(`$this$isLetter`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isLetterOrDigit(): Boolean {
      return Character.isLetterOrDigit(`$this$isLetterOrDigit`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isDigit(): Boolean {
      return Character.isDigit(`$this$isDigit`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isIdentifierIgnorable(): Boolean {
      return Character.isIdentifierIgnorable(`$this$isIdentifierIgnorable`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isISOControl(): Boolean {
      return Character.isISOControl(`$this$isISOControl`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isJavaIdentifierPart(): Boolean {
      return Character.isJavaIdentifierPart(`$this$isJavaIdentifierPart`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isJavaIdentifierStart(): Boolean {
      return Character.isJavaIdentifierStart(`$this$isJavaIdentifierStart`);
   }

   @JvmStatic
   public fun Char.isWhitespace(): Boolean {
      return Character.isWhitespace(`$this$isWhitespace`) || Character.isSpaceChar(`$this$isWhitespace`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isUpperCase(): Boolean {
      return Character.isUpperCase(`$this$isUpperCase`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isLowerCase(): Boolean {
      return Character.isLowerCase(`$this$isLowerCase`);
   }

   @Deprecated(message = "Use uppercaseChar() instead.", replaceWith = @ReplaceWith(expression = "uppercaseChar()", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun Char.toUpperCase(): Char {
      return Character.toUpperCase(`$this$toUpperCase`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Char.uppercaseChar(): Char {
      return Character.toUpperCase(`$this$uppercaseChar`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Char.uppercase(): String {
      var var10000: java.lang.String = java.lang.String.valueOf(`$this$uppercase`);
      var10000 = var10000.toUpperCase(Locale.ROOT);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Char.uppercase(locale: Locale): String {
      var var10000: java.lang.String = java.lang.String.valueOf(`$this$uppercase`);
      var10000 = var10000.toUpperCase(locale);
      return var10000;
   }

   @Deprecated(message = "Use lowercaseChar() instead.", replaceWith = @ReplaceWith(expression = "lowercaseChar()", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun Char.toLowerCase(): Char {
      return Character.toLowerCase(`$this$toLowerCase`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Char.lowercaseChar(): Char {
      return Character.toLowerCase(`$this$lowercaseChar`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Char.lowercase(): String {
      var var10000: java.lang.String = java.lang.String.valueOf(`$this$lowercase`);
      var10000 = var10000.toLowerCase(Locale.ROOT);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Char.lowercase(locale: Locale): String {
      var var10000: java.lang.String = java.lang.String.valueOf(`$this$lowercase`);
      var10000 = var10000.toLowerCase(locale);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isTitleCase(): Boolean {
      return Character.isTitleCase(`$this$isTitleCase`);
   }

   @Deprecated(message = "Use titlecaseChar() instead.", replaceWith = @ReplaceWith(expression = "titlecaseChar()", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun Char.toTitleCase(): Char {
      return Character.toTitleCase(`$this$toTitleCase`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Char.titlecaseChar(): Char {
      return Character.toTitleCase(`$this$titlecaseChar`);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Char.titlecase(locale: Locale): String {
      var localizedUppercase: java.lang.String = CharsKt.uppercase(`$this$titlecase`, locale);
      if (localizedUppercase.length() > 1) {
         var var10000: java.lang.String;
         if (`$this$titlecase` == 329) {
            var10000 = localizedUppercase;
         } else {
            var var3: Char = localizedUppercase.charAt(0);
            var10000 = localizedUppercase.substring(1);
            var10000 = var10000.toLowerCase(Locale.ROOT);
            var10000 = var3 + var10000;
         }

         return var10000;
      } else {
         var var10001: java.lang.String = java.lang.String.valueOf(`$this$titlecase`);
         var10001 = var10001.toUpperCase(Locale.ROOT);
         return if (!(localizedUppercase == var10001)) localizedUppercase else java.lang.String.valueOf(Character.toTitleCase(`$this$titlecase`));
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isHighSurrogate(): Boolean {
      return Character.isHighSurrogate(`$this$isHighSurrogate`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Char.isLowSurrogate(): Boolean {
      return Character.isLowSurrogate(`$this$isLowSurrogate`);
   }

   @JvmStatic
   internal fun digitOf(char: Char, radix: Int): Int {
      return Character.digit((int)var0, radix);
   }

   @PublishedApi
   @JvmStatic
   internal fun checkRadix(radix: Int): Int {
      if (!new IntRange(2, 36).contains(radix)) {
         throw new IllegalArgumentException("radix " + radix + " was not in valid range " + new IntRange(2, 36));
      } else {
         return radix;
      }
   }

   open fun CharsKt__CharJVMKt() {
   }
}
