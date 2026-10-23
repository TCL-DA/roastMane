package com.cropster.roastingintelligence.util;

import java.text.DecimalFormatSymbols;
import org.apache.commons.lang3.StringUtils;

public final class NumberUtils {
   public static boolean isParsable(String string, DecimalFormatSymbols symbols) {
      string = StringUtils.trim(string);
      if (StringUtils.isEmpty(string)) {
         return false;
      } else {
         char decimalSeparator = '.';
         char groupingSeparator = ',';
         char minusSign = '-';
         if (symbols != null) {
            decimalSeparator = symbols.getDecimalSeparator();
            groupingSeparator = symbols.getGroupingSeparator();
            minusSign = symbols.getMinusSign();
         }

         if (string.charAt(string.length() - 1) == decimalSeparator) {
            string = string.substring(0, string.length() - 1);
         }

         string.replace(String.valueOf(groupingSeparator), "");
         if (string.charAt(0) == minusSign) {
            return string.length() == 1 ? false : withDecimalsParsing(string, 1, decimalSeparator);
         } else {
            return withDecimalsParsing(string, 0, decimalSeparator);
         }
      }
   }

   private static boolean withDecimalsParsing(String string, int beginIdx, char decimalSeparator) {
      int decimalSeparators = 0;

      for (int i = beginIdx; i < string.length(); i++) {
         char c = string.charAt(i);
         boolean isDecimalSeparator = c == decimalSeparator;
         if (isDecimalSeparator) {
            decimalSeparators++;
         }

         if (decimalSeparators > 1) {
            return false;
         }

         if (!isDecimalSeparator && !Character.isDigit(c)) {
            return false;
         }
      }

      return true;
   }

   private NumberUtils() {
   }
}
