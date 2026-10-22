package nonapi.io.github.classgraph.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class URLPathEncoder {
   private static boolean[] safe = new boolean[256];
   private static final char[] HEXADECIMAL;
   private static final String[] SCHEME_PREFIXES;

   private URLPathEncoder() {
   }

   private static void unescapeChars(String str, boolean isQuery, ByteArrayOutputStream buf) {
      if (!str.isEmpty()) {
         int chrIdx = 0;

         for (int len = str.length(); chrIdx < len; chrIdx++) {
            char c = str.charAt(chrIdx);
            if (c == '%') {
               if (chrIdx <= len - 3) {
                  char c1 = str.charAt(++chrIdx);
                  int digit1 = c1 >= '0' && c1 <= '9' ? c1 - '0' : (c1 >= 'a' && c1 <= 'f' ? c1 - 'a' + 10 : (c1 >= 'A' && c1 <= 'F' ? c1 - 'A' + 10 : -1));
                  char c2 = str.charAt(++chrIdx);
                  int digit2 = c2 >= '0' && c2 <= '9' ? c2 - '0' : (c2 >= 'a' && c2 <= 'f' ? c2 - 'a' + 10 : (c2 >= 'A' && c2 <= 'F' ? c2 - 'A' + 10 : -1));
                  if (digit1 >= 0 && digit2 >= 0) {
                     buf.write((byte)(digit1 << 4 | digit2));
                  } else {
                     try {
                        buf.write(str.substring(chrIdx - 2, chrIdx + 1).getBytes(StandardCharsets.UTF_8));
                     } catch (IOException var12) {
                     }
                  }
               }
            } else if (isQuery && c == '+') {
               buf.write(32);
            } else if (c <= 127) {
               buf.write((byte)c);
            } else {
               try {
                  buf.write(Character.toString(c).getBytes(StandardCharsets.UTF_8));
               } catch (IOException var11) {
               }
            }
         }
      }
   }

   public static String decodePath(String str) {
      int queryIdx = str.indexOf(63);
      String partBeforeQuery = queryIdx < 0 ? str : str.substring(0, queryIdx);
      String partFromQuery = queryIdx < 0 ? "" : str.substring(queryIdx);
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      unescapeChars(partBeforeQuery, false, buf);
      unescapeChars(partFromQuery, true, buf);
      return new String(buf.toByteArray(), StandardCharsets.UTF_8);
   }

   public static String encodePath(String path) {
      int validColonPrefixLen = 0;

      for (String scheme : SCHEME_PREFIXES) {
         if (path.startsWith(scheme)) {
            validColonPrefixLen = scheme.length();
            break;
         }
      }

      if (VersionFinder.OS == VersionFinder.OperatingSystem.Windows) {
         int i = validColonPrefixLen;
         if (validColonPrefixLen < path.length() && path.charAt(validColonPrefixLen) == '/') {
            i = validColonPrefixLen + 1;
         }

         if (i < path.length() - 1 && Character.isLetter(path.charAt(i)) && path.charAt(i + 1) == ':') {
            validColonPrefixLen = i + 2;
         }
      }

      byte[] pathBytes = path.getBytes(StandardCharsets.UTF_8);
      StringBuilder encodedPath = new StringBuilder(pathBytes.length * 3);

      for (int ix = 0; ix < pathBytes.length; ix++) {
         byte pathByte = pathBytes[ix];
         int b = pathByte & 255;
         if (!safe[b] && (b != 58 || ix >= validColonPrefixLen)) {
            encodedPath.append('%');
            encodedPath.append(HEXADECIMAL[(b & 240) >> 4]);
            encodedPath.append(HEXADECIMAL[b & 15]);
         } else {
            encodedPath.append((char)b);
         }
      }

      return encodedPath.toString();
   }

   public static String normalizeURLPath(String urlPath) {
      String urlPathNormalized = urlPath;
      if (!urlPath.startsWith("jrt:") && !urlPath.startsWith("http://") && !urlPath.startsWith("https://")) {
         if (urlPath.startsWith("jar:")) {
            urlPathNormalized = urlPath.substring(4);
         }

         if (urlPathNormalized.startsWith("file:")) {
            urlPathNormalized = urlPathNormalized.substring(4);
         }

         String windowsDrivePrefix = "";
         if (VersionFinder.OS == VersionFinder.OperatingSystem.Windows) {
            if (urlPathNormalized.length() >= 2 && Character.isLetter(urlPathNormalized.charAt(0)) && urlPathNormalized.charAt(1) == ':') {
               windowsDrivePrefix = urlPathNormalized.substring(0, 2);
               urlPathNormalized = urlPathNormalized.substring(2);
            } else if (urlPathNormalized.length() >= 3
               && urlPathNormalized.charAt(0) == '/'
               && Character.isLetter(urlPathNormalized.charAt(1))
               && urlPathNormalized.charAt(2) == ':') {
               windowsDrivePrefix = urlPathNormalized.substring(1, 3);
               urlPathNormalized = urlPathNormalized.substring(3);
            }
         }

         urlPathNormalized = urlPathNormalized.replace("/!", "!").replace("!/", "!").replace("!", "!/");
         if (windowsDrivePrefix.isEmpty()) {
            urlPathNormalized = urlPathNormalized.startsWith("/") ? "file:" + urlPathNormalized : "file:/" + urlPathNormalized;
         } else {
            urlPathNormalized = "file:/" + windowsDrivePrefix + (urlPathNormalized.startsWith("/") ? urlPathNormalized : "/" + urlPathNormalized);
         }

         if (urlPathNormalized.contains("!") && !urlPathNormalized.startsWith("jar:")) {
            urlPathNormalized = "jar:" + urlPathNormalized;
         }
      }

      return encodePath(urlPathNormalized);
   }

   static {
      for (int i = 97; i <= 122; i++) {
         safe[i] = true;
      }

      for (int i = 65; i <= 90; i++) {
         safe[i] = true;
      }

      for (int i = 48; i <= 57; i++) {
         safe[i] = true;
      }

      safe[36] = safe[45] = safe[95] = safe[46] = safe[43] = true;
      safe[33] = safe[42] = safe[39] = safe[40] = safe[41] = safe[44] = true;
      safe[47] = true;
      HEXADECIMAL = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
      SCHEME_PREFIXES = new String[]{"jrt:", "file:", "jar:file:", "jar:", "http:", "https:"};
   }
}
