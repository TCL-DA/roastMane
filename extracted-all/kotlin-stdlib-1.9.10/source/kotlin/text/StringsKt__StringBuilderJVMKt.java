package kotlin.text

import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nStringBuilderJVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StringBuilderJVM.kt\nkotlin/text/StringsKt__StringBuilderJVMKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,381:1\n1#2:382\n*E\n"])
// $VF: Class flags could not be determined
internal class StringsKt__StringBuilderJVMKt : StringsKt__RegexExtensionsKt {
   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun StringBuilder.clear(): StringBuilder {
      `$this$clear`.setLength(0);
      return `$this$clear`;
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun StringBuilder.set(index: Int, value: Char) {
      `$this$set`.setCharAt(index, value);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.setRange(startIndex: Int, endIndex: Int, value: String): StringBuilder {
      var var10000: StringBuilder = `$this$setRange`.replace(startIndex, endIndex, value);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.deleteAt(index: Int): StringBuilder {
      var var10000: StringBuilder = `$this$deleteAt`.deleteCharAt(index);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.deleteRange(startIndex: Int, endIndex: Int): StringBuilder {
      var var10000: StringBuilder = `$this$deleteRange`.delete(startIndex, endIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.toCharArray(destination: CharArray, destinationOffset: Int, startIndex: Int, endIndex: Int) {
      `$this$toCharArray`.getChars(startIndex, endIndex, destination, destinationOffset);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendRange(value: CharArray, startIndex: Int, endIndex: Int): StringBuilder {
      var var10000: StringBuilder = `$this$appendRange`.append(value, startIndex, endIndex - startIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendRange(value: CharSequence, startIndex: Int, endIndex: Int): StringBuilder {
      var var10000: StringBuilder = `$this$appendRange`.append(value, startIndex, endIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.insertRange(index: Int, value: CharArray, startIndex: Int, endIndex: Int): StringBuilder {
      var var10000: StringBuilder = `$this$insertRange`.insert(index, value, startIndex, endIndex - startIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.insertRange(index: Int, value: CharSequence, startIndex: Int, endIndex: Int): StringBuilder {
      var var10000: StringBuilder = `$this$insertRange`.insert(index, value, startIndex, endIndex);
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: StringBuffer?): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: StringBuilder?): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: Int): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: Short): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append((int)value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: Byte): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append((int)value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: Long): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: Float): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: Double): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine()", imports = []), level = DeprecationLevel.WARNING)
   @JvmStatic
   public fun Appendable.appendln(): Appendable {
      var var10000: Appendable = `$this$appendln`.append(SystemProperties.LINE_SEPARATOR);
      return var10000;
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun Appendable.appendln(value: CharSequence?): Appendable {
      var var10000: Appendable = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun Appendable.appendln(value: Char): Appendable {
      var var10000: Appendable = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine()", imports = []), level = DeprecationLevel.WARNING)
   @JvmStatic
   public fun StringBuilder.appendln(): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(SystemProperties.LINE_SEPARATOR);
      return var10000;
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: StringBuffer?): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: CharSequence?): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: String?): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: Any?): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: StringBuilder?): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: CharArray): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: Char): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: Boolean): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: Int): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: Short): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append((int)value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: Byte): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append((int)value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: Long): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: Float): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   @Deprecated(message = "Use appendLine instead. Note that the new method always appends the line feed character '\\n' regardless of the system line separator.", replaceWith = @ReplaceWith(expression = "appendLine(value)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendln(value: Double): StringBuilder {
      var var10000: StringBuilder = `$this$appendln`.append(value);
      return StringsKt.appendln(var10000);
   }

   open fun StringsKt__StringBuilderJVMKt() {
   }
}
