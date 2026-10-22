package org.eclipse.jetty.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class StringUtil {
   public static final String CRLF = "\r\n";
   public static final String DEFAULT_DELIMS = ",;";
   private static final char[] LOWERCASES = new char[]{
      '\u0000',
      '\u0001',
      '\u0002',
      '\u0003',
      '\u0004',
      '\u0005',
      '\u0006',
      '\u0007',
      '\b',
      '\t',
      '\n',
      '\u000b',
      '\f',
      '\r',
      '\u000e',
      '\u000f',
      '\u0010',
      '\u0011',
      '\u0012',
      '\u0013',
      '\u0014',
      '\u0015',
      '\u0016',
      '\u0017',
      '\u0018',
      '\u0019',
      '\u001a',
      '\u001b',
      '\u001c',
      '\u001d',
      '\u001e',
      '\u001f',
      ' ',
      '!',
      '"',
      '#',
      '$',
      '%',
      '&',
      '\'',
      '(',
      ')',
      '*',
      '+',
      ',',
      '-',
      '.',
      '/',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      ':',
      ';',
      '<',
      '=',
      '>',
      '?',
      '@',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'j',
      'k',
      'l',
      'm',
      'n',
      'o',
      'p',
      'q',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'x',
      'y',
      'z',
      '[',
      '\\',
      ']',
      '^',
      '_',
      '`',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'j',
      'k',
      'l',
      'm',
      'n',
      'o',
      'p',
      'q',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'x',
      'y',
      'z',
      '{',
      '|',
      '}',
      '~',
      '\u007f'
   };
   private static final char[] UPPERCASES = new char[]{
      '\u0000',
      '\u0001',
      '\u0002',
      '\u0003',
      '\u0004',
      '\u0005',
      '\u0006',
      '\u0007',
      '\b',
      '\t',
      '\n',
      '\u000b',
      '\f',
      '\r',
      '\u000e',
      '\u000f',
      '\u0010',
      '\u0011',
      '\u0012',
      '\u0013',
      '\u0014',
      '\u0015',
      '\u0016',
      '\u0017',
      '\u0018',
      '\u0019',
      '\u001a',
      '\u001b',
      '\u001c',
      '\u001d',
      '\u001e',
      '\u001f',
      ' ',
      '!',
      '"',
      '#',
      '$',
      '%',
      '&',
      '\'',
      '(',
      ')',
      '*',
      '+',
      ',',
      '-',
      '.',
      '/',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      ':',
      ';',
      '<',
      '=',
      '>',
      '?',
      '@',
      'A',
      'B',
      'C',
      'D',
      'E',
      'F',
      'G',
      'H',
      'I',
      'J',
      'K',
      'L',
      'M',
      'N',
      'O',
      'P',
      'Q',
      'R',
      'S',
      'T',
      'U',
      'V',
      'W',
      'X',
      'Y',
      'Z',
      '[',
      '\\',
      ']',
      '^',
      '_',
      '`',
      'A',
      'B',
      'C',
      'D',
      'E',
      'F',
      'G',
      'H',
      'I',
      'J',
      'K',
      'L',
      'M',
      'N',
      'O',
      'P',
      'Q',
      'R',
      'S',
      'T',
      'U',
      'V',
      'W',
      'X',
      'Y',
      'Z',
      '{',
      '|',
      '}',
      '~',
      '\u007f'
   };

   public static char asciiToLowerCase(char c) {
      return c < 128 ? LOWERCASES[c] : c;
   }

   public static byte asciiToLowerCase(byte c) {
      return c > 0 ? (byte)LOWERCASES[c] : c;
   }

   public static String asciiToLowerCase(String s) {
      if (s == null) {
         return null;
      } else {
         char[] c = null;
         int i = s.length();

         while (i-- > 0) {
            char c1 = s.charAt(i);
            if (c1 <= 127) {
               char c2 = LOWERCASES[c1];
               if (c1 != c2) {
                  c = s.toCharArray();
                  c[i] = c2;
                  break;
               }
            }
         }

         while (i-- > 0) {
            assert c != null;

            if (c[i] <= 127) {
               c[i] = LOWERCASES[c[i]];
            }
         }

         return c == null ? s : new String(c);
      }
   }

   public static String asciiToUpperCase(String s) {
      if (s == null) {
         return null;
      } else {
         char[] c = null;
         int i = s.length();

         while (i-- > 0) {
            char c1 = s.charAt(i);
            if (c1 <= 127) {
               char c2 = UPPERCASES[c1];
               if (c1 != c2) {
                  c = s.toCharArray();
                  c[i] = c2;
                  break;
               }
            }
         }

         while (i-- > 0) {
            assert c != null;

            if (c[i] <= 127) {
               c[i] = UPPERCASES[c[i]];
            }
         }

         return c == null ? s : new String(c);
      }
   }

   public static String sanitizeFileSystemName(String str) {
      if (str == null) {
         return null;
      } else {
         char[] chars = str.toCharArray();
         int len = chars.length;

         for (int i = 0; i < len; i++) {
            char c = chars[i];
            if (c <= 31
               || c >= 127
               || c == '|'
               || c == '>'
               || c == '<'
               || c == '/'
               || c == '&'
               || c == '\\'
               || c == '.'
               || c == ':'
               || c == '='
               || c == '"'
               || c == ','
               || c == '*'
               || c == '?'
               || c == '!'
               || c == ' ') {
               chars[i] = '_';
            }
         }

         return String.valueOf(chars);
      }
   }

   public static boolean asciiEqualsIgnoreCase(String string, String other) {
      if (string == null) {
         return other == null;
      } else if (other == null) {
         return false;
      } else if (string.length() != other.length()) {
         return false;
      } else {
         for (int i = 0; i < string.length(); i++) {
            char c1 = string.charAt(i);
            char c2 = other.charAt(i);
            if (c1 != c2) {
               if (c1 <= 127) {
                  c1 = LOWERCASES[c1];
               }

               if (c2 <= 127) {
                  c2 = LOWERCASES[c2];
               }

               if (c1 != c2) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   @Deprecated
   public static boolean startsWithIgnoreCase(String string, String prefix) {
      return asciiStartsWithIgnoreCase(string, prefix);
   }

   public static boolean asciiStartsWithIgnoreCase(String string, String prefix) {
      if (isEmpty(prefix)) {
         return true;
      } else if (string != null && string.length() >= prefix.length()) {
         for (int i = 0; i < prefix.length(); i++) {
            char c1 = string.charAt(i);
            char c2 = prefix.charAt(i);
            if (c1 != c2) {
               if (c1 <= 127) {
                  c1 = LOWERCASES[c1];
               }

               if (c2 <= 127) {
                  c2 = LOWERCASES[c2];
               }

               if (c1 != c2) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Deprecated
   public static boolean endsWithIgnoreCase(String string, String suffix) {
      return asciiEndsWithIgnoreCase(string, suffix);
   }

   public static boolean asciiEndsWithIgnoreCase(String string, String suffix) {
      if (isEmpty(suffix)) {
         return true;
      } else if (string == null) {
         return false;
      } else {
         int stringLength = string.length();
         int suffixLength = suffix.length();
         if (stringLength < suffixLength) {
            return false;
         } else {
            int i = suffixLength;

            while (i-- > 0) {
               char c1 = string.charAt(--stringLength);
               char c2 = suffix.charAt(i);
               if (c1 != c2) {
                  if (c1 <= 127) {
                     c1 = LOWERCASES[c1];
                  }

                  if (c2 <= 127) {
                     c2 = LOWERCASES[c2];
                  }

                  if (c1 != c2) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public static int indexFrom(String s, String chars) {
      for (int i = 0; i < s.length(); i++) {
         if (chars.indexOf(s.charAt(i)) >= 0) {
            return i;
         }
      }

      return -1;
   }

   public static String replace(String str, char find, char with) {
      if (str == null) {
         return null;
      } else if (find == with) {
         return str;
      } else {
         int c = 0;
         int idx = str.indexOf(find, c);
         if (idx == -1) {
            return str;
         } else {
            char[] chars = str.toCharArray();
            int len = chars.length;

            for (int i = idx; i < len; i++) {
               if (chars[i] == find) {
                  chars[i] = with;
               }
            }

            return String.valueOf(chars);
         }
      }
   }

   public static String replace(String s, String sub, String with) {
      if (s == null) {
         return null;
      } else {
         int c = 0;
         int i = s.indexOf(sub, c);
         if (i == -1) {
            return s;
         } else {
            StringBuilder buf = new StringBuilder(s.length() + with.length());

            do {
               buf.append(s, c, i);
               buf.append(with);
               c = i + sub.length();
            } while ((i = s.indexOf(sub, c)) != -1);

            if (c < s.length()) {
               buf.append(s.substring(c));
            }

            return buf.toString();
         }
      }
   }

   public static String replaceFirst(String original, String target, String replacement) {
      int idx = original.indexOf(target);
      if (idx == -1) {
         return original;
      } else {
         int offset = 0;
         int originalLen = original.length();
         StringBuilder buf = new StringBuilder(originalLen + replacement.length());
         buf.append(original, offset, idx);
         offset += idx + target.length();
         buf.append(replacement);
         buf.append(original, offset, originalLen);
         return buf.toString();
      }
   }

   public static void append(StringBuilder buf, String s, int offset, int length) {
      int end = offset + length;

      for (int i = offset; i < end && i < s.length(); i++) {
         buf.append(s.charAt(i));
      }
   }

   public static void append(StringBuilder buf, byte b, int base) {
      int bi = 255 & b;
      int c = 48 + bi / base % base;
      if (c > 57) {
         c = 97 + (c - 48 - 10);
      }

      buf.append((char)c);
      c = 48 + bi % base;
      if (c > 57) {
         c = 97 + (c - 48 - 10);
      }

      buf.append((char)c);
   }

   public static void append2digits(StringBuilder buf, int i) {
      if (i < 100) {
         buf.append((char)(i / 10 + 48));
         buf.append((char)(i % 10 + 48));
      }
   }

   public static String nonNull(String s) {
      return s == null ? "" : s;
   }

   public static boolean equals(String s, char[] buf, int offset, int length) {
      if (s.length() != length) {
         return false;
      } else {
         for (int i = 0; i < length; i++) {
            if (buf[offset + i] != s.charAt(i)) {
               return false;
            }
         }

         return true;
      }
   }

   public static String toUTF8String(byte[] b, int offset, int length) {
      return new String(b, offset, length, StandardCharsets.UTF_8);
   }

   @Deprecated(
      since = "10",
      forRemoval = true
   )
   public static String toString(byte[] b, int offset, int length, String charset) {
      try {
         return new String(b, offset, length, charset);
      } catch (UnsupportedEncodingException var5) {
         throw new IllegalArgumentException(var5);
      }
   }

   public static int indexOfControlChars(String str) {
      if (str == null) {
         return -1;
      } else {
         int len = str.length();

         for (int i = 0; i < len; i++) {
            if (Character.isISOControl(str.codePointAt(i))) {
               return i;
            }
         }

         return -1;
      }
   }

   public static boolean isBlank(String str) {
      if (str == null) {
         return true;
      } else {
         int len = str.length();

         for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(str.codePointAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isEmpty(String str) {
      return str == null || str.isEmpty();
   }

   public static int getLength(String s) {
      return s == null ? 0 : s.length();
   }

   public static boolean isNotBlank(String str) {
      if (str == null) {
         return false;
      } else {
         int len = str.length();

         for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(str.codePointAt(i))) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isHex(String str, int offset, int length) {
      if (offset + length > str.length()) {
         return false;
      } else {
         for (int i = offset; i < offset + length; i++) {
            char c = str.charAt(i);
            if ((c < 'a' || c > 'f') && (c < 'A' || c > 'F') && (c < '0' || c > '9')) {
               return false;
            }
         }

         return true;
      }
   }

   public static byte[] fromHexString(String s) {
      if (s.length() % 2 != 0) {
         throw new IllegalArgumentException(s);
      } else {
         byte[] array = new byte[s.length() / 2];

         for (int i = 0; i < array.length; i++) {
            int b = Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
            array[i] = (byte)(0xFF & b);
         }

         return array;
      }
   }

   public static String toHexString(byte b) {
      return toHexString(new byte[]{b}, 0, 1);
   }

   public static String toHexString(byte[] b) {
      return toHexString(Objects.requireNonNull(b, "ByteBuffer cannot be null"), 0, b.length);
   }

   public static String toHexString(byte[] b, int offset, int length) {
      StringBuilder buf = new StringBuilder();

      for (int i = offset; i < offset + length; i++) {
         int bi = 255 & b[i];
         int c = 48 + bi / 16 % 16;
         if (c > 57) {
            c = 65 + (c - 48 - 10);
         }

         buf.append((char)c);
         c = 48 + bi % 16;
         if (c > 57) {
            c = 97 + (c - 48 - 10);
         }

         buf.append((char)c);
      }

      return buf.toString();
   }

   public static String printable(String name) {
      if (name == null) {
         return null;
      } else {
         StringBuilder buf = new StringBuilder(name.length());

         for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isISOControl(c)) {
               buf.append(c);
            }
         }

         return buf.toString();
      }
   }

   public static String printable(byte[] b) {
      StringBuilder buf = new StringBuilder();

      for (byte value : b) {
         char c = (char)value;
         if (!Character.isWhitespace(c) && (c <= ' ' || c >= 127)) {
            buf.append("0x");
            TypeUtil.toHex(value, buf);
         } else {
            buf.append(c);
         }
      }

      return buf.toString();
   }

   public static byte[] getBytes(String s) {
      return s.getBytes(StandardCharsets.ISO_8859_1);
   }

   public static byte[] getBytes(String s, String charset) {
      try {
         return s.getBytes(charset);
      } catch (Exception var3) {
         return s.getBytes();
      }
   }

   public static byte[] getUtf8Bytes(String s) {
      return s.getBytes(StandardCharsets.UTF_8);
   }

   public static int toInt(String string, int from) {
      int val = 0;
      boolean started = false;
      boolean minus = false;

      for (int i = from; i < string.length(); i++) {
         char b = string.charAt(i);
         if (b <= ' ') {
            if (started) {
               break;
            }
         } else if (b >= '0' && b <= '9') {
            val = val * 10 + (b - '0');
            started = true;
         } else {
            if (b != '-' || started) {
               break;
            }

            minus = true;
         }
      }

      if (started) {
         return minus ? -val : val;
      } else {
         throw new NumberFormatException(string);
      }
   }

   public static String truncate(String str, int maxSize) {
      if (str == null) {
         return null;
      } else {
         return str.length() <= maxSize ? str : str.substring(0, maxSize);
      }
   }

   public static String[] arrayFromString(String s) {
      if (s == null) {
         return new String[0];
      } else if (s.startsWith("[") && s.endsWith("]")) {
         return s.length() == 2 ? new String[0] : csvSplit(s, 1, s.length() - 2);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static String[] csvSplit(String s) {
      return s == null ? null : csvSplit(s, 0, s.length());
   }

   public static String[] csvSplit(String s, int off, int len) {
      if (s == null) {
         return null;
      } else if (off >= 0 && len >= 0 && off <= s.length()) {
         List<String> list = new ArrayList<>();
         csvSplit(list, s, off, len);
         return list.toArray(new String[0]);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static List<String> csvSplit(List<String> list, String s, int off, int len) {
      if (list == null) {
         list = new ArrayList<>();
      }

      StringUtil.CsvSplitState state = StringUtil.CsvSplitState.PRE_DATA;
      StringBuilder out = new StringBuilder();
      int last = -1;

      while (len > 0) {
         char ch = s.charAt(off++);
         len--;
         switch (state) {
            case PRE_DATA:
               if ('"' == ch) {
                  state = StringUtil.CsvSplitState.QUOTE;
               } else if (',' == ch) {
                  list.add("");
               } else if (!Character.isWhitespace(ch)) {
                  state = StringUtil.CsvSplitState.DATA;
                  out.append(ch);
               }
               break;
            case QUOTE:
               if ('\\' == ch) {
                  state = StringUtil.CsvSplitState.SLOSH;
               } else if ('"' == ch) {
                  list.add(out.toString());
                  out.setLength(0);
                  state = StringUtil.CsvSplitState.POST_DATA;
               } else {
                  out.append(ch);
               }
               break;
            case SLOSH:
               out.append(ch);
               state = StringUtil.CsvSplitState.QUOTE;
               break;
            case DATA:
               if (Character.isWhitespace(ch)) {
                  last = out.length();
                  out.append(ch);
                  state = StringUtil.CsvSplitState.WHITE;
               } else if (',' == ch) {
                  list.add(out.toString());
                  out.setLength(0);
                  state = StringUtil.CsvSplitState.PRE_DATA;
               } else {
                  out.append(ch);
               }
               break;
            case WHITE:
               if (Character.isWhitespace(ch)) {
                  out.append(ch);
               } else if (',' == ch) {
                  out.setLength(last);
                  list.add(out.toString());
                  out.setLength(0);
                  state = StringUtil.CsvSplitState.PRE_DATA;
               } else {
                  state = StringUtil.CsvSplitState.DATA;
                  out.append(ch);
                  last = -1;
               }
               break;
            case POST_DATA:
               if (',' == ch) {
                  state = StringUtil.CsvSplitState.PRE_DATA;
               }
               break;
            default:
               throw new IllegalStateException(state.toString());
         }
      }

      switch (state) {
         case PRE_DATA:
         case POST_DATA:
            break;
         case QUOTE:
         case SLOSH:
         case DATA:
            list.add(out.toString());
            break;
         case WHITE:
            out.setLength(last);
            list.add(out.toString());
            break;
         default:
            throw new IllegalStateException(state.toString());
      }

      return list;
   }

   public static String sanitizeXmlString(String html) {
      if (html == null) {
         return null;
      } else {
         int i;
         label53:
         for (i = 0; i < html.length(); i++) {
            char c = html.charAt(i);
            switch (c) {
               case '"':
               case '&':
               case '\'':
               case '<':
               case '>':
                  break label53;
               default:
                  if (Character.isISOControl(c) && !Character.isWhitespace(c)) {
                     break label53;
                  }
            }
         }

         if (i == html.length()) {
            return html;
         } else {
            StringBuilder out = new StringBuilder(html.length() * 4 / 3);
            out.append(html, 0, i);

            for (; i < html.length(); i++) {
               char c = html.charAt(i);
               switch (c) {
                  case '"':
                     out.append("&quot;");
                     break;
                  case '&':
                     out.append("&amp;");
                     break;
                  case '\'':
                     out.append("&apos;");
                     break;
                  case '<':
                     out.append("&lt;");
                     break;
                  case '>':
                     out.append("&gt;");
                     break;
                  default:
                     if (Character.isISOControl(c) && !Character.isWhitespace(c)) {
                        out.append('?');
                     } else {
                        out.append(c);
                     }
               }
            }

            return out.toString();
         }
      }
   }

   public static String strip(String str, String find) {
      return replace(str, find, "");
   }

   public static String valueOf(Object object) {
      return object == null ? null : String.valueOf(object);
   }

   public static String randomAlphaNumeric(int digits) {
      String source = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
      return IntStream.range(0, digits)
         .map(i -> ThreadLocalRandom.current().nextInt(source.length()))
         .map(source::charAt)
         .collect(StringBuilder::new, (b, c) -> b.append((char)c), StringBuilder::append)
         .toString();
   }

   private StringUtil() {
   }

   static enum CsvSplitState {
      PRE_DATA,
      QUOTE,
      SLOSH,
      DATA,
      WHITE,
      POST_DATA;
   }
}
