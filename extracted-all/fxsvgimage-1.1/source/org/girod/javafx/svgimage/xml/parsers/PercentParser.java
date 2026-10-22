package org.girod.javafx.svgimage.xml.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PercentParser {
   private static final Pattern NUMBER = Pattern.compile("\\d+(\\.\\d+)?");
   private static final Pattern PERCENT_UNIT = Pattern.compile("(\\d+)(\\.\\d+)?%");

   private PercentParser() {
   }

   public static double parseValue(XMLNode node, String attrName, boolean allowAbsolute) {
      String valueAsString = node.getAttributeValue(attrName);
      return valueAsString != null ? parseValue(valueAsString, allowAbsolute) : 0.0;
   }

   public static double parseValue(XMLNode node, String attrName) {
      return parseValue(node, attrName, false);
   }

   public static double parseValue(String value) {
      return parseValue(value, false);
   }

   public static double parseValue(String value, boolean allowAbsolute) {
      value = value.trim();
      Matcher m = NUMBER.matcher(value);
      if (m.matches()) {
         double parsedValue = Double.parseDouble(value);
         if (parsedValue < 0.0) {
            parsedValue = 0.0;
         } else if (!allowAbsolute && parsedValue > 1.0) {
            parsedValue = 1.0;
         }

         return parsedValue;
      } else {
         m = PERCENT_UNIT.matcher(value);
         if (m.matches()) {
            String startDigits = m.group(1);
            String endDigit = null;
            if (m.groupCount() > 1) {
               endDigit = m.group(2);
            }

            double parsedValue;
            if (endDigit == null) {
               parsedValue = Double.parseDouble(startDigits);
            } else {
               parsedValue = Double.parseDouble(startDigits + "." + endDigit);
            }

            parsedValue /= 100.0;
            if (parsedValue < 0.0) {
               parsedValue = 0.0;
            } else if (parsedValue > 1.0) {
               parsedValue = 1.0;
            }

            return parsedValue;
         } else {
            return 0.0;
         }
      }
   }
}
