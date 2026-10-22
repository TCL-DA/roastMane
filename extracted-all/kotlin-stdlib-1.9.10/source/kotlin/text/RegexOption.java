package kotlin.text

import kotlin.enums.EnumEntries

enum class RegexOption : FlagEnum {
   @JvmStatic
   IGNORE_CASE(2, 0, 2, null),
   @JvmStatic
   MULTILINE(8, 0, 2, null),
   @JvmStatic
   LITERAL(16, 0, 2, null),
   @JvmStatic
   UNIX_LINES(1, 0, 2, null),
   @JvmStatic
   COMMENTS(4, 0, 2, null),
   @JvmStatic
   DOT_MATCHES_ALL(32, 0, 2, null),
   @JvmStatic
   CANON_EQ(128, 0, 2, null);
   private int value;
   private int mask;

   public open val mask: Int
   public open val value: Int

   init {
      this.value = value;
      this.mask = mask;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<RegexOption> {
      return $ENTRIES;
   }
}
