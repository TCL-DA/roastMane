package okio

/** @deprecated */
@Deprecated(message = "changed in Okio 2.x")
object `-DeprecatedUtf8` private constructor() {
   @JvmStatic
   public -DeprecatedUtf8 INSTANCE = new -DeprecatedUtf8();


   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "string.utf8Size()", imports = ["okio.utf8Size"]), level = DeprecationLevel.ERROR)
   public fun size(string: String): Long {
      return Utf8.size$default(string, 0, 0, 3, null);
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "string.utf8Size(beginIndex, endIndex)", imports = ["okio.utf8Size"]), level = DeprecationLevel.ERROR)
   public fun size(string: String, beginIndex: Int, endIndex: Int): Long {
      return Utf8.size(string, beginIndex, endIndex);
   }
}
