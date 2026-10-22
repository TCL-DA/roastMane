package org.eclipse.jetty.util;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class URIUtil {
   private static final Logger LOG = LoggerFactory.getLogger(URIUtil.class);
   private static final String UNRESERVED = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-._~";
   private static final String SUBDELIMS = "!$&'()*+,;=";
   private static final String REGNAME = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-._~!$&'()*+,;=";
   private static final boolean[] REGNAME_ALLOWED = new boolean[128];
   private static final boolean[] URI_SUPPORTED_CHARACTERS;
   private static final boolean[] ENCODE_PATH_NEEDS_ENCODING;

   private URIUtil() {
   }

   public static String encodePath(String path) {
      if (StringUtil.isEmpty(path)) {
         return path;
      } else {
         boolean needsByteEncoding = false;
         boolean needsEncoding = false;
         int length = path.length();

         for (int i = 0; i < length; i++) {
            char c = path.charAt(i);
            if (c > 127) {
               needsByteEncoding = true;
               break;
            }

            if (ENCODE_PATH_NEEDS_ENCODING[c]) {
               needsEncoding = true;
            }
         }

         if (needsByteEncoding) {
            return encodePathBytes(path);
         } else {
            return needsEncoding ? encodePathString(path) : path;
         }
      }
   }

   private static String encodePathString(String path) {
      StringBuilder buf = new StringBuilder(path.length() * 2);
      int length = path.length();

      for (int i = 0; i < length; i++) {
         char c = path.charAt(i);
         if (ENCODE_PATH_NEEDS_ENCODING[c]) {
            buf.append('%');
            TypeUtil.toHex((byte)c, buf);
         } else {
            buf.append(c);
         }
      }

      return buf.toString();
   }

   private static String encodePathBytes(String path) {
      StringBuilder buf = new StringBuilder(path.length() * 2);
      byte[] pathBytes = path.getBytes(StandardCharsets.UTF_8);

      for (byte b : pathBytes) {
         if (b >= 0 && !ENCODE_PATH_NEEDS_ENCODING[b]) {
            buf.append((char)b);
         } else {
            buf.append('%');
            TypeUtil.toHex(b, buf);
         }
      }

      return buf.toString();
   }

   public static String encodeSpecific(String str, String charsToEncode) {
      if (str != null && str.length() != 0) {
         if (charsToEncode != null && charsToEncode.length() != 0) {
            char[] find = charsToEncode.toCharArray();
            int len = str.length();
            StringBuilder ret = new StringBuilder((int)((double)len * 0.2));

            for (int i = 0; i < len; i++) {
               char c = str.charAt(i);
               boolean escaped = false;

               for (char f : find) {
                  if (c == f) {
                     escaped = true;
                     ret.append('%');
                     int d = 15 & (240 & c) >> 4;
                     ret.append((char)((d > 9 ? 55 : 48) + d));
                     d = 15 & c;
                     ret.append((char)((d > 9 ? 55 : 48) + d));
                     break;
                  }
               }

               if (!escaped) {
                  ret.append(c);
               }
            }

            return ret.toString();
         } else {
            return str;
         }
      } else {
         return null;
      }
   }

   public static String decodeSpecific(String str, String charsToDecode) {
      if (str != null && str.length() != 0) {
         if (charsToDecode != null && charsToDecode.length() != 0) {
            int idx = str.indexOf(37);
            if (idx == -1) {
               return str;
            } else {
               char[] find = charsToDecode.toCharArray();
               int len = str.length();
               Utf8StringBuilder ret = new Utf8StringBuilder(len);
               ret.append(str, 0, idx);

               for (int i = idx; i < len; i++) {
                  char c = str.charAt(i);
                  if (c != '%') {
                     ret.append(c);
                  } else {
                     if (i + 2 >= len) {
                        throw new IllegalArgumentException("Bad URI % encoding");
                     }

                     char u = str.charAt(i + 1);
                     char l = str.charAt(i + 2);
                     char result = (char)(0xFF & TypeUtil.convertHexDigit(u) * 16 + TypeUtil.convertHexDigit(l));
                     boolean decoded = false;

                     for (char f : find) {
                        if (f == result) {
                           ret.append(result);
                           decoded = true;
                           break;
                        }
                     }

                     if (decoded) {
                        i += 2;
                     } else {
                        ret.append(c);
                     }
                  }
               }

               return ret.toCompleteString();
            }
         } else {
            return str;
         }
      } else {
         return null;
      }
   }

   public static StringBuilder encodeString(StringBuilder buf, String path, String encode) {
      if (buf == null) {
         for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '%' || encode.indexOf(c) >= 0) {
               buf = new StringBuilder(path.length() << 1);
               break;
            }
         }

         if (buf == null) {
            return null;
         }
      }

      for (int ix = 0; ix < path.length(); ix++) {
         char c = path.charAt(ix);
         if (c != '%' && encode.indexOf(c) < 0) {
            buf.append(c);
         } else {
            buf.append('%');
            StringUtil.append(buf, (byte)(255 & c), 16);
         }
      }

      return buf;
   }

   public static String decodePath(String path) {
      return decodePath(path, 0, path.length());
   }

   public static String decodePath(String path, int offset, int length) {
      try {
         Utf8StringBuilder builder = null;
         int end = offset + length;

         label73:
         for (int i = offset; i < end; i++) {
            char c = path.charAt(i);
            switch (c) {
               case '%':
                  if (builder == null) {
                     builder = new Utf8StringBuilder(length);
                     builder.append(path, offset, i - offset);
                  }

                  if (i + 2 >= end) {
                     throw new IllegalArgumentException("Bad URI % encoding");
                  }

                  char u = path.charAt(i + 1);
                  if (u != 'u') {
                     byte b = (byte)(0xFF & TypeUtil.convertHexDigit(u) * 16 + TypeUtil.convertHexDigit(path.charAt(i + 2)));
                     builder.append(b);
                     i += 2;
                     break;
                  }

                  int[] codePoints = new int[]{65535 & TypeUtil.parseInt(path, i + 2, 4, 16)};
                  String str = new String(codePoints, 0, 1);
                  byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

                  for (byte b : bytes) {
                     builder.append(b);
                  }

                  i += 5;
                  break;
               case ';':
                  if (builder == null) {
                     builder = new Utf8StringBuilder(path.length());
                     builder.append(path, offset, i - offset);
                  }

                  do {
                     if (++i >= end) {
                        continue label73;
                     }
                  } while (path.charAt(i) != '/');

                  builder.append('/');
                  break;
               default:
                  if (builder != null) {
                     builder.append(c);
                  }
            }
         }

         if (builder != null) {
            return builder.toCompleteString();
         } else {
            return offset == 0 && length == path.length() ? path : path.substring(offset, end);
         }
      } catch (IllegalArgumentException var15) {
         throw var15;
      } catch (Exception var16) {
         throw new IllegalArgumentException("cannot decode URI", var16);
      }
   }

   public static boolean isPathValid(String path) {
      if (path == null) {
         return true;
      } else {
         int end = path.length();

         for (int i = 0; i < end; i++) {
            char c = path.charAt(i);
            switch (c) {
               case '#':
               case '?':
                  return false;
            }
         }

         return true;
      }
   }

   private static boolean isSafe(int code) {
      return code >= URI_SUPPORTED_CHARACTERS.length || URI_SUPPORTED_CHARACTERS[code];
   }

   private static boolean isSafeElseEncode(int code, Utf8StringBuilder builder) {
      if (isSafe(code)) {
         return true;
      } else {
         encodeCodepoint(code, builder);
         return false;
      }
   }

   private static void encodeCodepoint(int code, Utf8StringBuilder builder) {
      if (code <= 127) {
         builder.append('%');
         appendHexValue(builder, (byte)code);
      } else {
         int[] codePoints = new int[]{code};
         String str = new String(codePoints, 0, 1);
         byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

         for (byte b : bytes) {
            builder.append('%');
            appendHexValue(builder, b);
         }
      }
   }

   private static void appendHexValue(Utf8StringBuilder builder, byte value) {
      byte d = (byte)((240 & value) >> 4);
      builder.append((char)((d > 9 ? 55 : 48) + d));
      d = (byte)(15 & value);
      builder.append((char)((d > 9 ? 55 : 48) + d));
   }

   public static String canonicalPath(String encodedPath) {
      return canonicalPath(encodedPath, null);
   }

   public static <X extends Throwable> String canonicalPath(String encodedPath, Supplier<X> onBadUtf8) throws X {
      if (encodedPath == null) {
         return null;
      } else {
         Utf8StringBuilder builder = null;
         int end = encodedPath.length();
         boolean slash = true;
         boolean normal = true;

         for (int i = 0; i < end; i++) {
            char c;
            c = encodedPath.charAt(i);
            label94:
            switch (c) {
               case '%':
                  if (builder == null) {
                     builder = new Utf8StringBuilder(encodedPath.length());
                     builder.append(encodedPath, 0, i);
                  }

                  if (i + 2 >= end) {
                     throw new IllegalArgumentException("Bad URI % encoding");
                  }

                  char u = encodedPath.charAt(i + 1);
                  if (u != 'u') {
                     int code = TypeUtil.convertHexDigit(u) * 16 + TypeUtil.convertHexDigit(encodedPath.charAt(i + 2));
                     if (isSafeElseEncode(code, builder)) {
                        builder.append((byte)(0xFF & code));
                        if (slash && code == 46) {
                           normal = false;
                        }
                     }

                     i += 2;
                  } else {
                     int code = TypeUtil.parseInt(encodedPath, i + 2, 4, 16);
                     if (isSafeElseEncode(code, builder)) {
                        char[] chars = Character.toChars(code);

                        for (char ch : chars) {
                           builder.append(ch);
                           if (slash && ch == '.') {
                              normal = false;
                           }

                           slash = false;
                        }
                     }

                     i += 5;
                  }
                  break;
               case '.':
                  if (slash) {
                     normal = false;
                  }

                  if (builder != null) {
                     builder.append(c);
                  }
                  break;
               case '/':
                  if (builder != null) {
                     builder.append(c);
                  }
                  break;
               case ';':
                  if (builder == null) {
                     builder = new Utf8StringBuilder(encodedPath.length());
                     builder.append(encodedPath, 0, i);
                  }

                  do {
                     if (++i >= end) {
                        break label94;
                     }
                  } while (encodedPath.charAt(i) != '/');

                  builder.append('/');
                  break;
               default:
                  if (builder == null && !isSafe(c)) {
                     builder = new Utf8StringBuilder(encodedPath.length());
                     builder.append(encodedPath, 0, i);
                  }

                  if (builder != null && isSafeElseEncode(c, builder)) {
                     builder.append(c);
                  }
            }

            slash = c == '/';
         }

         String canonical = builder != null ? (onBadUtf8 == null ? builder.toCompleteString() : builder.takeCompleteString(onBadUtf8)) : encodedPath;
         return normal ? canonical : normalizePath(canonical);
      }
   }

   public static String addEncodedPaths(String p1, String p2) {
      if (p1 != null && p1.length() != 0) {
         if (p2 != null && p2.length() != 0) {
            int split = p1.indexOf(59);
            if (split < 0) {
               split = p1.indexOf(63);
            }

            if (split == 0) {
               return p2 + p1;
            } else {
               if (split < 0) {
                  split = p1.length();
               }

               StringBuilder buf = new StringBuilder(p1.length() + p2.length() + 2);
               buf.append(p1);
               if (buf.charAt(split - 1) == '/') {
                  if (p2.startsWith("/")) {
                     buf.deleteCharAt(split - 1);
                     buf.insert(split - 1, p2);
                  } else {
                     buf.insert(split, p2);
                  }
               } else if (p2.startsWith("/")) {
                  buf.insert(split, p2);
               } else {
                  buf.insert(split, '/');
                  buf.insert(split + 1, p2);
               }

               return buf.toString();
            }
         } else {
            return p1;
         }
      } else {
         return p1 != null && p2 == null ? p1 : p2;
      }
   }

   public static String addPaths(String p1, String p2) {
      if (p1 != null && p1.length() != 0) {
         if (p2 != null && p2.length() != 0) {
            boolean p1EndsWithSlash = p1.endsWith("/");
            boolean p2StartsWithSlash = p2.startsWith("/");
            if (p1EndsWithSlash && p2StartsWithSlash) {
               if (p2.length() == 1) {
                  return p1;
               }

               if (p1.length() == 1) {
                  return p2;
               }
            }

            StringBuilder buf = new StringBuilder(p1.length() + p2.length() + 2);
            buf.append(p1);
            if (p1.endsWith("/")) {
               if (p2.startsWith("/")) {
                  buf.setLength(buf.length() - 1);
               }
            } else if (!p2.startsWith("/")) {
               buf.append("/");
            }

            buf.append(p2);
            return buf.toString();
         } else {
            return p1;
         }
      } else {
         return p1 != null && p2 == null ? p1 : p2;
      }
   }

   public static String addPathQuery(String path, String query) {
      if (StringUtil.isBlank(query)) {
         return path;
      } else {
         return path.indexOf(63) >= 0 ? path + "&" + query : path + "?" + query;
      }
   }

   public static String getUriLastPathSegment(URI uri) {
      String ssp = uri.getSchemeSpecificPart();
      int idx = ssp.indexOf("!/");
      if (idx != -1) {
         ssp = ssp.substring(0, idx);
      }

      if (ssp.endsWith("/")) {
         ssp = ssp.substring(0, ssp.length() - 1);
      }

      idx = ssp.lastIndexOf(47);
      if (idx != -1) {
         ssp = ssp.substring(idx + 1);
      }

      return ssp;
   }

   public static String parentPath(String p) {
      if (p != null && !"/".equals(p)) {
         int slash = p.lastIndexOf(47, p.length() - 2);
         return slash >= 0 ? p.substring(0, slash + 1) : null;
      } else {
         return null;
      }
   }

   public static String normalizePathQuery(String pathQuery) {
      if (pathQuery != null && !pathQuery.isEmpty()) {
         boolean slash = true;
         int end = pathQuery.length();
         int i = 0;

         while (true) {
            label84: {
               if (i < end) {
                  char c = pathQuery.charAt(i);
                  switch (c) {
                     case '#':
                     case '?':
                        return pathQuery;
                     case '.':
                        if (!slash) {
                           break label84;
                        }
                        break;
                     case '/':
                        slash = true;
                        break label84;
                     default:
                        slash = false;
                        break label84;
                  }
               }

               if (i == end) {
                  return pathQuery;
               }

               StringBuilder canonical = new StringBuilder(pathQuery.length());
               canonical.append(pathQuery, 0, i);
               int dots = 1;
               i++;

               label65:
               for (; i < end; i++) {
                  char c = pathQuery.charAt(i);
                  switch (c) {
                     case '#':
                     case '?':
                        break label65;
                     case '.':
                        if (dots > 0) {
                           dots++;
                        } else if (slash) {
                           dots = 1;
                        } else {
                           canonical.append('.');
                        }

                        slash = false;
                        continue;
                     case '/':
                        if (doDotsSlash(canonical, dots)) {
                           return null;
                        }

                        slash = true;
                        dots = 0;
                        continue;
                  }

                  while (dots-- > 0) {
                     canonical.append('.');
                  }

                  canonical.append(c);
                  dots = 0;
                  slash = false;
               }

               if (doDots(canonical, dots)) {
                  return null;
               }

               if (i < end) {
                  canonical.append(pathQuery, i, end);
               }

               return canonical.toString();
            }

            i++;
         }
      } else {
         return pathQuery;
      }
   }

   public static boolean isNotNormalWithinSelf(String path) {
      return normalizePath(path) == null;
   }

   public static String normalizePath(String path) {
      if (path != null && !path.isEmpty()) {
         boolean slash = true;
         int end = path.length();

         int i;
         label65:
         for (i = 0; i < end; i++) {
            char c = path.charAt(i);
            switch (c) {
               case '.':
                  if (slash) {
                     break label65;
                  }
                  break;
               case '/':
                  slash = true;
                  break;
               default:
                  slash = false;
            }
         }

         if (i == end) {
            return path;
         } else {
            StringBuilder canonical = new StringBuilder(path.length());
            canonical.append(path, 0, i);
            int dots = 1;
            i++;

            for (; i < end; i++) {
               char c = path.charAt(i);
               switch (c) {
                  case '.':
                     if (dots > 0) {
                        dots++;
                     } else if (slash) {
                        dots = 1;
                     } else {
                        canonical.append('.');
                     }

                     slash = false;
                     continue;
                  case '/':
                     if (doDotsSlash(canonical, dots)) {
                        return null;
                     }

                     slash = true;
                     dots = 0;
                     continue;
               }

               while (dots-- > 0) {
                  canonical.append('.');
               }

               canonical.append(c);
               dots = 0;
               slash = false;
            }

            return doDots(canonical, dots) ? null : canonical.toString();
         }
      } else {
         return path;
      }
   }

   private static boolean doDots(StringBuilder canonical, int dots) {
      switch (dots) {
         case 0:
         case 1:
            break;
         case 2:
            if (canonical.length() < 2) {
               return true;
            }

            canonical.setLength(canonical.length() - 1);
            canonical.setLength(canonical.lastIndexOf("/") + 1);
            break;
         default:
            while (dots-- > 0) {
               canonical.append('.');
            }
      }

      return false;
   }

   private static boolean doDotsSlash(StringBuilder canonical, int dots) {
      switch (dots) {
         case 0:
            canonical.append('/');
            return false;
         case 1:
            return false;
         case 2:
            if (canonical.length() < 2) {
               return true;
            }

            canonical.setLength(canonical.length() - 1);
            canonical.setLength(canonical.lastIndexOf("/") + 1);
            return false;
         default:
            while (dots-- > 0) {
               canonical.append('.');
            }

            canonical.append('/');
            return false;
      }
   }

   public static String compactPath(String path) {
      if (path != null && path.length() != 0) {
         int state = 0;
         int end = path.length();
         int i = 0;

         while (true) {
            label53: {
               if (i < end) {
                  char c = path.charAt(i);
                  switch (c) {
                     case '/':
                        if (++state != 2) {
                           break label53;
                        }
                        break;
                     case '?':
                        return path;
                     default:
                        state = 0;
                        break label53;
                  }
               }

               if (state < 2) {
                  return path;
               }

               StringBuilder buf = new StringBuilder(path.length());
               buf.append(path, 0, i);

               for (; i < end; i++) {
                  char c = path.charAt(i);
                  switch (c) {
                     case '/':
                        if (state++ == 0) {
                           buf.append(c);
                        }
                        break;
                     case '?':
                        buf.append(path, i, end);
                        return buf.toString();
                     default:
                        state = 0;
                        buf.append(c);
                  }
               }

               return buf.toString();
            }

            i++;
         }
      } else {
         return path;
      }
   }

   public static boolean hasScheme(String uri) {
      for (int i = 0; i < uri.length(); i++) {
         char c = uri.charAt(i);
         if (c == ':') {
            return true;
         }

         if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (i <= 0 || (c < '0' || c > '9') && c != '.' && c != '+' && c != '-')) {
            break;
         }
      }

      return false;
   }

   public static boolean isValidHostRegisteredName(String token) {
      if (token == null) {
         return true;
      } else {
         int length = token.length();

         for (int i = 0; i < length; i++) {
            char c = token.charAt(i);
            if (c > 127) {
               return false;
            }

            if (!REGNAME_ALLOWED[c]) {
               if (c != '%') {
                  return false;
               }

               if (!StringUtil.isHex(token, i + 1, 2)) {
                  return false;
               }

               i += 2;
            }
         }

         return true;
      }
   }

   public static String newURI(String scheme, String server, int port, String path, String query) {
      StringBuilder builder = newURIBuilder(scheme, server, port);
      builder.append(path);
      if (query != null && query.length() > 0) {
         builder.append('?').append(query);
      }

      return builder.toString();
   }

   public static StringBuilder newURIBuilder(String scheme, String server, int port) {
      StringBuilder builder = new StringBuilder();
      appendSchemeHostPort(builder, scheme, server, port);
      return builder;
   }

   public static void appendSchemeHostPort(StringBuilder url, String scheme, String server, int port) {
      url.append(scheme).append("://").append(HostPort.normalizeHost(server));
      if (port > 0) {
         switch (scheme) {
            case "http":
               if (port != 80) {
                  url.append(':').append(port);
               }
               break;
            case "https":
               if (port != 443) {
                  url.append(':').append(port);
               }
               break;
            default:
               url.append(':').append(port);
         }
      }
   }

   public static void appendSchemeHostPort(StringBuffer url, String scheme, String server, int port) {
      url.append(scheme).append("://").append(HostPort.normalizeHost(server));
      if (port > 0) {
         switch (scheme) {
            case "http":
               if (port != 80) {
                  url.append(':').append(port);
               }
               break;
            case "https":
               if (port != 443) {
                  url.append(':').append(port);
               }
               break;
            default:
               url.append(':').append(port);
         }
      }
   }

   public static String encodePathSafeEncoding(String path) {
      if (path == null) {
         return null;
      } else if (!"".equals(path) && !"/".equals(path)) {
         int offset = 0;
         int length = path.length();

         try {
            Utf8StringBuilder builder = null;
            int end = offset + length;

            for (int i = offset; i < end; i++) {
               char c = path.charAt(i);
               if (c == '%') {
                  if (builder == null) {
                     builder = new Utf8StringBuilder(path.length());
                     builder.append(path, offset, i - offset);
                  }

                  if (i + 2 < end) {
                     char u = path.charAt(i + 1);
                     if (u == 'u') {
                        if (TypeUtil.isHex(path, i + 2, 4)) {
                           int codepoint = 65535 & TypeUtil.parseInt(path, i + 2, 4, 16);
                           encodeCodepoint(codepoint, builder);
                           i += 5;
                        } else {
                           builder.append("%25");
                        }
                     } else if (TypeUtil.isHex(path, i + 1, 2)) {
                        byte b = (byte)(0xFF & TypeUtil.convertHexDigit(u) * 16 + TypeUtil.convertHexDigit(path.charAt(i + 2)));
                        if (!mustBeEncoded(b) && b != 47) {
                           builder.append(b);
                        } else {
                           encodeCodepoint(b, builder);
                        }

                        i += 2;
                     } else {
                        builder.append("%25");
                     }
                  } else {
                     builder.append("%25");
                  }
               } else if (mustBeEncoded(c)) {
                  if (builder == null) {
                     builder = new Utf8StringBuilder(path.length());
                     builder.append(path, offset, i - offset);
                  }

                  encodeCodepoint(c, builder);
               } else if (builder != null) {
                  builder.append(c);
               }
            }

            return builder != null ? builder.toCompleteString() : path;
         } catch (IllegalArgumentException var9) {
            throw var9;
         } catch (Exception var10) {
            throw new IllegalArgumentException("cannot decode URI", var10);
         }
      } else {
         return path;
      }
   }

   private static boolean mustBeEncoded(int codepoint) {
      if (codepoint > 127) {
         return true;
      } else if (codepoint <= 31 || codepoint == 127) {
         return true;
      } else if (codepoint == 34
         || codepoint == 60
         || codepoint == 62
         || codepoint == 37
         || codepoint == 123
         || codepoint == 125
         || codepoint == 124
         || codepoint == 92
         || codepoint == 94
         || codepoint == 96) {
         return true;
      } else {
         return codepoint != 32 && codepoint != 91 && codepoint != 93 ? codepoint == 63 || codepoint == 35 : true;
      }
   }

   public static URI addPath(URI uri, String path) {
      Objects.requireNonNull(uri, "URI");
      if (path != null && !"".equals(path)) {
         path = compactPath(path);
         int pathLen = path.length();
         if (pathLen == 0) {
            return uri;
         } else {
            String base = correctFileURI(uri).toASCIIString();
            path = encodePathSafeEncoding(path);
            pathLen = path.length();
            if (base.length() == 0) {
               return URI.create(path);
            } else {
               StringBuilder buf = new StringBuilder(base.length() + pathLen * 3);
               buf.append(base);
               if (buf.charAt(base.length() - 1) != '/') {
                  buf.append('/');
               }

               int offset = path.charAt(0) == '/' ? 1 : 0;
               buf.append(path, offset, pathLen);
               return URI.create(buf.toString());
            }
         }
      } else {
         return uri;
      }
   }

   public static String addQueries(String query1, String query2) {
      if (StringUtil.isEmpty(query1)) {
         return query2;
      } else {
         return StringUtil.isEmpty(query2) ? query1 : query1 + "&" + query2;
      }
   }

   public static URI correctFileURI(URI uri) {
      if (uri != null && uri.getScheme() != null) {
         if (!uri.getScheme().equalsIgnoreCase("file") && !uri.getScheme().equalsIgnoreCase("jar")) {
            return uri;
         } else if (uri.getRawAuthority() != null) {
            return uri;
         } else if (!uri.isAbsolute()) {
            return uri;
         } else {
            String rawURI = uri.toASCIIString();
            int colon = rawURI.indexOf(":/");
            if (colon < 0) {
               return uri;
            } else {
               int end = -1;
               if (rawURI.charAt(colon + 2) != '/') {
                  end = colon + 2;
               }

               return end >= 0 ? URI.create(rawURI.substring(0, colon) + ":///" + rawURI.substring(end)) : uri;
            }
         }
      } else {
         return uri;
      }
   }

   public static List<URI> split(String str) {
      List<URI> uris = new ArrayList<>();
      StringTokenizer tokenizer = new StringTokenizer(str, ",;|");

      while (tokenizer.hasMoreTokens()) {
         String reference = tokenizer.nextToken();

         try {
            if (!reference.endsWith("/*") && !reference.endsWith("\\*")) {
               URI refUri = toURI(reference);
               uris.add(toJarFileUri(refUri));
            } else {
               String dir = reference.substring(0, reference.length() - 2);
               Path pathDir = Paths.get(dir);
               if (Files.exists(pathDir) && Files.isDirectory(pathDir)) {
                  try (Stream<Path> listStream = Files.list(pathDir)) {
                     listStream.filter(x$0 -> Files.isRegularFile(x$0))
                        .filter(FileID::isLibArchive)
                        .sorted(Comparator.naturalOrder())
                        .forEach(path -> uris.add(toJarFileUri(path.toUri())));
                  } catch (IOException var11) {
                     throw new RuntimeException("Unable to process directory glob listing: " + reference, var11);
                  }
               }
            }
         } catch (Exception var12) {
            LOG.warn("Invalid Resource Reference: " + reference);
            throw var12;
         }
      }

      return uris;
   }

   public static URI toJarFileUri(URI uri) {
      Objects.requireNonNull(uri, "URI");
      String scheme = Objects.requireNonNull(uri.getScheme(), "URI scheme");
      boolean hasInternalReference = uri.getRawSchemeSpecificPart().indexOf("!/") > 0;
      if (scheme.equalsIgnoreCase("jar")) {
         if (uri.getRawSchemeSpecificPart().startsWith("file:")) {
            if (hasInternalReference) {
               return uri;
            }

            return URI.create(uri.toASCIIString() + "!/");
         }
      } else if (scheme.equalsIgnoreCase("file")) {
         String rawUri = uri.toASCIIString();
         if (rawUri.endsWith("/")) {
            return uri;
         }

         if (hasInternalReference) {
            return URI.create("jar:" + rawUri);
         }

         return URI.create("jar:" + rawUri + "!/");
      }

      throw new IllegalArgumentException("Cannot make %s into `jar:file:` URI".formatted(uri));
   }

   public static URI toURI(String resource) {
      Objects.requireNonNull(resource);
      return ResourceFactory.isSupported(resource) ? correctFileURI(URI.create(resource)) : Paths.get(resource).toUri();
   }

   public static URI unwrapContainer(URI uri) {
      Objects.requireNonNull(uri);
      String scheme = uri.getScheme();
      if (scheme != null && scheme.equalsIgnoreCase("jar")) {
         String spec = uri.getRawSchemeSpecificPart();
         int sep = spec.indexOf("!/");
         if (sep != -1) {
            spec = spec.substring(0, sep);
         }

         return URI.create(spec);
      } else {
         return uri;
      }
   }

   public static URI uriJarPrefix(URI uri, String encodedSuffix) {
      if (uri == null) {
         throw new IllegalArgumentException("URI must not be null");
      } else if (encodedSuffix == null) {
         throw new IllegalArgumentException("Encoded Suffix must not be null");
      } else if (!encodedSuffix.startsWith("!/")) {
         throw new IllegalArgumentException("Suffix must start with \"!/\"");
      } else {
         String uriString = uri.toASCIIString();
         int bangSlash = uriString.indexOf("!/");
         if (bangSlash >= 0) {
            uriString = uriString.substring(0, bangSlash);
         }

         if (uri.getScheme().equalsIgnoreCase("jar")) {
            return URI.create(uriString + encodedSuffix);
         } else if (uri.getScheme().equalsIgnoreCase("file")) {
            return URI.create("jar:" + uriString + encodedSuffix);
         } else {
            throw new IllegalArgumentException("Unsupported URI scheme: " + uri);
         }
      }
   }

   public static Stream<URI> streamOf(URLClassLoader urlClassLoader) {
      URL[] urls = urlClassLoader.getURLs();
      return Stream.of(urls).filter(Objects::nonNull).map(URL::toString).map(URI::create).map(URIUtil::unwrapContainer).map(URIUtil::correctFileURI);
   }

   static {
      Arrays.fill(REGNAME_ALLOWED, false);

      for (char c : "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-._~!$&'()*+,;=".toCharArray()) {
         REGNAME_ALLOWED[c] = true;
      }

      URI_SUPPORTED_CHARACTERS = new boolean[]{
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         true,
         false,
         false,
         true,
         false,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         false,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         false,
         false,
         true,
         false,
         false,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         false,
         false,
         false,
         false,
         true,
         false,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         true,
         false,
         false,
         false,
         true,
         false
      };
      ENCODE_PATH_NEEDS_ENCODING = new boolean[128];

      for (char c : "%?;#\"'<> [\\]^`{|}".toCharArray()) {
         ENCODE_PATH_NEEDS_ENCODING[c] = true;
      }

      ENCODE_PATH_NEEDS_ENCODING[127] = true;

      for (int i = 0; i < 32; i++) {
         ENCODE_PATH_NEEDS_ENCODING[i] = true;
      }
   }
}
