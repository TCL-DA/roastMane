package org.glassfish.jersey.server.internal;

public class ContainerUtils {
   private static final String[] TOKENS = new String[]{"{", "}", "\\", "^", "|", "`"};
   private static final String[] REPLACEMENTS = new String[]{"%7B", "%7D", "%5C", "%5E", "%7C", "%60"};

   public static String encodeUnsafeCharacters(String originalQueryString) {
      if (originalQueryString == null) {
         return null;
      } else {
         String result = originalQueryString;

         for (int i = 0; i < TOKENS.length; i++) {
            if (originalQueryString.contains(TOKENS[i])) {
               result = result.replace(TOKENS[i], REPLACEMENTS[i]);
            }
         }

         return result;
      }
   }

   public static String reduceLeadingSlashes(String path) {
      int length;
      if (path != null && (length = path.length()) != 0) {
         int start = 0;

         while (start != length && "/".indexOf(path.charAt(start)) != -1) {
            start++;
         }

         return path.substring(start > 0 ? start - 1 : 0);
      } else {
         return path;
      }
   }

   public static String getHandlerPath(String uri) {
      return uri != null && uri.length() != 0 && uri.contains("?") ? uri.substring(0, uri.indexOf("?")) : uri;
   }
}
