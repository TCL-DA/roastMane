package kotlin.text

import kotlin.enums.EnumEntries

enum class CharCategory {
   @JvmStatic
   UNASSIGNED(0, "Cn"),
   @JvmStatic
   UPPERCASE_LETTER(1, "Lu"),
   @JvmStatic
   LOWERCASE_LETTER(2, "Ll"),
   @JvmStatic
   TITLECASE_LETTER(3, "Lt"),
   @JvmStatic
   MODIFIER_LETTER(4, "Lm"),
   @JvmStatic
   OTHER_LETTER(5, "Lo"),
   @JvmStatic
   NON_SPACING_MARK(6, "Mn"),
   @JvmStatic
   ENCLOSING_MARK(7, "Me"),
   @JvmStatic
   COMBINING_SPACING_MARK(8, "Mc"),
   @JvmStatic
   DECIMAL_DIGIT_NUMBER(9, "Nd"),
   @JvmStatic
   LETTER_NUMBER(10, "Nl"),
   @JvmStatic
   OTHER_NUMBER(11, "No"),
   @JvmStatic
   SPACE_SEPARATOR(12, "Zs"),
   @JvmStatic
   LINE_SEPARATOR(13, "Zl"),
   @JvmStatic
   PARAGRAPH_SEPARATOR(14, "Zp"),
   @JvmStatic
   CONTROL(15, "Cc"),
   @JvmStatic
   FORMAT(16, "Cf"),
   @JvmStatic
   PRIVATE_USE(18, "Co"),
   @JvmStatic
   SURROGATE(19, "Cs"),
   @JvmStatic
   DASH_PUNCTUATION(20, "Pd"),
   @JvmStatic
   START_PUNCTUATION(21, "Ps"),
   @JvmStatic
   END_PUNCTUATION(22, "Pe"),
   @JvmStatic
   CONNECTOR_PUNCTUATION(23, "Pc"),
   @JvmStatic
   OTHER_PUNCTUATION(24, "Po"),
   @JvmStatic
   MATH_SYMBOL(25, "Sm"),
   @JvmStatic
   CURRENCY_SYMBOL(26, "Sc"),
   @JvmStatic
   MODIFIER_SYMBOL(27, "Sk"),
   @JvmStatic
   OTHER_SYMBOL(28, "So"),
   @JvmStatic
   INITIAL_QUOTE_PUNCTUATION(29, "Pi"),
   @JvmStatic
   FINAL_QUOTE_PUNCTUATION(30, "Pf");
   @JvmStatic
   public CharCategory.Companion Companion = new CharCategory.Companion(null);
   private int value;
   private java.lang.String code;

   public final val code: String
   public final val value: Int

   init {
      this.value = value;
      this.code = code;
   }

   public operator fun contains(char: Char): Boolean {
      return Character.getType(var1) == this.value;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<CharCategory> {
      return $ENTRIES;
   }

   companion object Companion private constructor() {
      public fun valueOf(category: Int): CharCategory {
         var var10000: CharCategory;
         if (new IntRange(0, 16).contains(category)) {
            var10000 = CharCategory.values()[category];
         } else {
            if (!new IntRange(18, 30).contains(category)) {
               throw new IllegalArgumentException("Category #" + category + " is not defined.");
            }

            var10000 = CharCategory.values()[category - 1];
         }

         return var10000;
      }
   }
}
