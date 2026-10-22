package kotlin.text

private object ScreenFloatValueRegEx private constructor() {
   @JvmStatic
   public ScreenFloatValueRegEx INSTANCE = new ScreenFloatValueRegEx();

   public final val value: Regex


   @JvmStatic
   fun {
      var var0: ScreenFloatValueRegEx = INSTANCE;
      var Exp: java.lang.String = "[eE][+-]?" + "(\\p{Digit}+)";
      value = new Regex(
         "[\\x00-\\x20]*[+-]?(NaN|Infinity|(("
            + '('
            + "(\\p{Digit}+)"
            + "(\\.)?("
            + "(\\p{Digit}+)"
            + "?)("
            + Exp
            + ")?)|(\\.("
            + "(\\p{Digit}+)"
            + ")("
            + Exp
            + ")?)|(("
            + "(0[xX]"
            + "(\\p{XDigit}+)"
            + "(\\.)?)|(0[xX]"
            + "(\\p{XDigit}+)"
            + "?(\\.)"
            + "(\\p{XDigit}+)"
            + ')'
            + ")[pP][+-]?"
            + "(\\p{Digit}+)"
            + ')'
            + ")[fFdD]?))[\\x00-\\x20]*"
      );
   }
}
