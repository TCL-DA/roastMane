package nonapi.io.github.classgraph.utils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FastPathResolver {
   private static final Pattern percentMatcher = Pattern.compile("([%][0-9a-fA-F][0-9a-fA-F])+");
   private static final Pattern schemeTwoSlashMatcher = Pattern.compile("^[a-zA-Z+\\-.]+://");
   private static final Pattern schemeOneSlashMatcher = Pattern.compile("^[a-zA-Z+\\-.]+:/");

   private FastPathResolver() {
   }

   private static void translateSeparator(String path, int startIdx, int endIdx, boolean stripFinalSeparator, StringBuilder buf) {
      for (int i = startIdx; i < endIdx; i++) {
         char c = path.charAt(i);
         if (c != '\\' && c != '/') {
            buf.append(c);
         } else if (i < endIdx - 1 || !stripFinalSeparator) {
            char prevChar = buf.length() == 0 ? 0 : buf.charAt(buf.length() - 1);
            if (prevChar != '/') {
               buf.append('/');
            }
         }
      }
   }

   private static int hexCharToInt(char c) {
      return c >= 48 && c <= 57 ? c - 48 : (c >= 97 && c <= 102 ? c - 97 + 10 : c - 65 + 10);
   }

   private static void unescapePercentEncoding(String path, int startIdx, int endIdx, StringBuilder buf) {
      if (endIdx - startIdx == 3 && path.charAt(startIdx + 1) == '2' && path.charAt(startIdx + 2) == '0') {
         buf.append(' ');
      } else {
         byte[] bytes = new byte[(endIdx - startIdx) / 3];
         int i = startIdx;

         for (int j = 0; i < endIdx; j++) {
            char c1 = path.charAt(i + 1);
            char c2 = path.charAt(i + 2);
            int digit1 = hexCharToInt(c1);
            int digit2 = hexCharToInt(c2);
            bytes[j] = (byte)(digit1 << 4 | digit2);
            i += 3;
         }

         String str = new String(bytes, StandardCharsets.UTF_8);
         String var12 = str.replace("/", "%2F").replace("\\", "%5C");
         buf.append(var12);
      }
   }

   public static String normalizePath(String path, boolean isFileOrJarURL) {
      boolean hasPercent = path.indexOf(37) >= 0;
      if (!hasPercent && path.indexOf(92) < 0 && !path.endsWith("/")) {
         return path;
      } else {
         int len = path.length();
         StringBuilder buf = new StringBuilder();
         if (hasPercent && isFileOrJarURL) {
            int prevEndMatchIdx = 0;
            Matcher matcher = percentMatcher.matcher(path);

            while (matcher.find()) {
               int startMatchIdx = matcher.start();
               int endMatchIdx = matcher.end();
               translateSeparator(path, prevEndMatchIdx, startMatchIdx, false, buf);
               unescapePercentEncoding(path, startMatchIdx, endMatchIdx, buf);
               prevEndMatchIdx = endMatchIdx;
            }

            translateSeparator(path, prevEndMatchIdx, len, true, buf);
            return buf.toString();
         } else {
            translateSeparator(path, 0, len, true, buf);
            return buf.toString();
         }
      }
   }

   public static String resolve(String resolveBasePath, String relativePath) {
      if (relativePath != null && !relativePath.isEmpty()) {
         String prefix = "";
         boolean isAbsolutePath = false;
         boolean isFileOrJarURL = false;
         int startIdx = 0;

         boolean matchedPrefix;
         do {
            matchedPrefix = false;
            if (relativePath.regionMatches(true, startIdx, "jar:", 0, 4)) {
               matchedPrefix = true;
               startIdx = 4;
               isFileOrJarURL = true;
            } else if (relativePath.regionMatches(true, startIdx, "http://", 0, 7)) {
               matchedPrefix = true;
               startIdx += 7;
               prefix = prefix + "http://";
               isAbsolutePath = true;
            } else if (relativePath.regionMatches(true, startIdx, "https://", 0, 8)) {
               matchedPrefix = true;
               startIdx += 8;
               prefix = prefix + "https://";
               isAbsolutePath = true;
            } else if (relativePath.regionMatches(true, startIdx, "jrt:", 0, 5)) {
               matchedPrefix = true;
               startIdx += 4;
               prefix = prefix + "jrt:";
               isAbsolutePath = true;
            } else if (relativePath.regionMatches(true, startIdx, "file:", 0, 5)) {
               matchedPrefix = true;
               startIdx += 5;
               isFileOrJarURL = true;
            } else {
               String relPath = startIdx == 0 ? relativePath : relativePath.substring(startIdx);
               Matcher m2 = schemeTwoSlashMatcher.matcher(relPath);
               if (m2.find()) {
                  matchedPrefix = true;
                  String m2Match = m2.group();
                  startIdx += m2Match.length();
                  prefix = prefix + m2Match;
                  isAbsolutePath = true;
               } else {
                  Matcher m1 = schemeOneSlashMatcher.matcher(relPath);
                  if (m1.find()) {
                     matchedPrefix = true;
                     String m1Match = m1.group();
                     startIdx += m1Match.length();
                     prefix = prefix + m1Match;
                     isAbsolutePath = true;
                  }
               }
            }
         } while (matchedPrefix);

         if (VersionFinder.OS == VersionFinder.OperatingSystem.Windows) {
            if (!relativePath.startsWith("//", startIdx) && !relativePath.startsWith("\\\\", startIdx)) {
               if (relativePath.length() - startIdx > 2 && Character.isLetter(relativePath.charAt(startIdx)) && relativePath.charAt(startIdx + 1) == ':') {
                  isAbsolutePath = true;
               } else if (relativePath.length() - startIdx > 3
                  && (relativePath.charAt(startIdx) == '/' || relativePath.charAt(startIdx) == '\\')
                  && Character.isLetter(relativePath.charAt(startIdx + 1))
                  && relativePath.charAt(startIdx + 2) == ':') {
                  isAbsolutePath = true;
                  startIdx++;
               }
            } else {
               startIdx += 2;
               prefix = prefix + "//";
               isAbsolutePath = true;
            }
         }

         if (relativePath.length() - startIdx > 1 && (relativePath.charAt(startIdx) == '/' || relativePath.charAt(startIdx) == '\\')) {
            isAbsolutePath = true;
         }

         String pathStr = normalizePath(startIdx == 0 ? relativePath : relativePath.substring(startIdx), isFileOrJarURL);
         if (!pathStr.equals("/")) {
            if (pathStr.endsWith("/")) {
               pathStr = pathStr.substring(0, pathStr.length() - 1);
            }

            if (pathStr.endsWith("!")) {
               pathStr = pathStr.substring(0, pathStr.length() - 1);
            }

            if (pathStr.endsWith("/")) {
               pathStr = pathStr.substring(0, pathStr.length() - 1);
            }

            if (pathStr.isEmpty()) {
               pathStr = "/";
            }
         }

         String pathResolved;
         if (!isAbsolutePath && resolveBasePath != null && !resolveBasePath.isEmpty()) {
            pathResolved = FileUtils.sanitizeEntryPath(resolveBasePath + (resolveBasePath.endsWith("/") ? "" : "/") + pathStr, false, true);
         } else {
            pathResolved = FileUtils.sanitizeEntryPath(pathStr, false, true);
         }

         return prefix.isEmpty() ? pathResolved : prefix + pathResolved;
      } else {
         return resolveBasePath == null ? "" : resolveBasePath;
      }
   }

   public static String resolve(String pathStr) {
      return resolve(null, pathStr);
   }
}
