package org.eclipse.jetty.websocket.core.util;

public final class TextUtils {
   public static String quote(String text) {
      return text == null ? "<null>" : "\"" + text + "\"";
   }

   public static String hint(String text) {
      return text == null ? "<null>" : "\"" + maxStringLength(30, text) + "\"";
   }

   public static String maxStringLength(int max, String raw) {
      int length = raw.length();
      if (length <= max) {
         return raw;
      } else if (max < 9) {
         return raw.substring(0, max);
      } else {
         StringBuilder ret = new StringBuilder();
         int startLen = (int)Math.round((double)max / 3.0);
         ret.append(raw, 0, startLen);
         ret.append("...");
         ret.append(raw.substring(length - (max - startLen - 3)));
         return ret.toString();
      }
   }
}
