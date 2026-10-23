package org.cropster.roastingintelligence.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {
   public static boolean isBlank(String s) {
      return s == null || s.length() == 0 || s.trim().length() == 0;
   }

   public static String abbreviateString(String source, int maxLength) {
      String result = source;
      if (source != null && source.length() > maxLength && source.length() > 10) {
         int middle = maxLength / 2;
         String part1 = source.substring(0, middle - 1);
         String part2 = source.substring(source.length() - middle + 2);
         result = part1 + "..." + part2;
      }

      return result;
   }

   public static String toHtml(String message) {
      Pattern pattern = Pattern.compile("[\\n\\r\\f]+|https?://\\S+");
      Matcher matcher = pattern.matcher(message);
      StringBuffer html = new StringBuffer();

      while (matcher.find()) {
         String match = matcher.group();
         if (match.startsWith("http")) {
            matcher.appendReplacement(html, String.format("<a href=\"%1$s\">%1$s</a>", match));
         } else {
            matcher.appendReplacement(html, "<br />");
         }
      }

      matcher.appendTail(html);
      return html.toString();
   }

   private StringUtil() {
   }
}
