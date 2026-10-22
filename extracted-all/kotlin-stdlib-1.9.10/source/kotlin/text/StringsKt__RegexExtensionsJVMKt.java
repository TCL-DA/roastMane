package kotlin.text

import java.util.regex.Pattern
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class StringsKt__RegexExtensionsJVMKt : StringsKt__IndentKt {
   @InlineOnly
   @JvmStatic
   public inline fun Pattern.toRegex(): Regex {
      return new Regex(`$this$toRegex`);
   }

   open fun StringsKt__RegexExtensionsJVMKt() {
   }
}
