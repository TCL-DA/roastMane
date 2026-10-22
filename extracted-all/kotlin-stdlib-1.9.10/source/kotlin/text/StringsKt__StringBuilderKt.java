package kotlin.text

import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class StringsKt__StringBuilderKt : StringsKt__StringBuilderJVMKt {
   @Deprecated(message = "Use append(value: Any?) instead", replaceWith = @ReplaceWith(expression = "append(value = obj)", imports = []), level = DeprecationLevel.WARNING)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.append(obj: Any?): StringBuilder {
      var var10000: StringBuilder = `$this$append`.append(obj);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun buildString(builderAction: (StringBuilder) -> Unit): String {
      contract {
         callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
      }

      var var1: StringBuilder = new StringBuilder();
      builderAction.invoke(var1);
      var var10000: java.lang.String = var1.toString();
      return var10000;
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun buildString(capacity: Int, builderAction: (StringBuilder) -> Unit): String {
      contract {
         callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
      }

      var var2: StringBuilder = new StringBuilder(capacity);
      builderAction.invoke(var2);
      var var10000: java.lang.String = var2.toString();
      return var10000;
   }

   @JvmStatic
   public fun StringBuilder.append(vararg value: String?): StringBuilder {
      for (java.lang.String item : value) {
         `$this$append`.append(item);
      }

      return `$this$append`;
   }

   @JvmStatic
   public fun StringBuilder.append(vararg value: Any?): StringBuilder {
      for (Object item : value) {
         `$this$append`.append(item);
      }

      return `$this$append`;
   }

   @Deprecated(message = "Use appendRange instead.", replaceWith = @ReplaceWith(expression = "this.appendRange(str, offset, offset + len)", imports = []), level = DeprecationLevel.ERROR)
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.append(str: CharArray, offset: Int, len: Int): StringBuilder {
      throw new NotImplementedError(null, 1, null);
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: CharSequence?): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: String?): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: Any?): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: CharArray): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: Char): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun StringBuilder.appendLine(value: Boolean): StringBuilder {
      var var10000: StringBuilder = `$this$appendLine`.append(value);
      var10000 = var10000.append('\n');
      return var10000;
   }

   open fun StringsKt__StringBuilderKt() {
   }
}
