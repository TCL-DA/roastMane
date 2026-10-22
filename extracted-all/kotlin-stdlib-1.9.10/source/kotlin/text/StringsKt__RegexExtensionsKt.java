package kotlin.text

import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class StringsKt__RegexExtensionsKt : StringsKt__RegexExtensionsJVMKt {
   @InlineOnly
   @JvmStatic
   public inline fun String.toRegex(): Regex {
      return new Regex(`$this$toRegex`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.toRegex(option: RegexOption): Regex {
      return new Regex(`$this$toRegex`, option);
   }

   @InlineOnly
   @JvmStatic
   public inline fun String.toRegex(options: Set<RegexOption>): Regex {
      return new Regex(`$this$toRegex`, options);
   }

   open fun StringsKt__RegexExtensionsKt() {
   }
}
