package kotlin.text

import java.util.Locale

internal fun Char.titlecaseImpl(): String {
   var var10000: java.lang.String = java.lang.String.valueOf(`$this$titlecaseImpl`);
   var10000 = var10000.toUpperCase(Locale.ROOT);
   if (var10000.length() > 1) {
      if (`$this$titlecaseImpl` == 329) {
         var10000 = var10000;
      } else {
         var var2: Char = var10000.charAt(0);
         var10000 = var10000.substring(1);
         var10000 = var10000.toLowerCase(Locale.ROOT);
         var10000 = var2 + var10000;
      }

      return var10000;
   } else {
      return java.lang.String.valueOf(Character.toTitleCase(`$this$titlecaseImpl`));
   }
}

