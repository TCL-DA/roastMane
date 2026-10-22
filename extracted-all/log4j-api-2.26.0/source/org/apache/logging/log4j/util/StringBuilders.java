package org.apache.logging.log4j.util;

import java.time.temporal.Temporal;
import java.util.Map.Entry;

@InternalApi
public final class StringBuilders {
   private static final char REPLACEMENT_CHAR = '�';
   private static final Class<?> timeClass;
   private static final Class<?> dateClass;

   private StringBuilders() {
   }

   public static StringBuilder appendDqValue(final StringBuilder sb, final Object value) {
      return sb.append('"').append(value).append('"');
   }

   public static StringBuilder appendKeyDqValue(final StringBuilder sb, final Entry<String, String> entry) {
      return appendKeyDqValue(sb, entry.getKey(), entry.getValue());
   }

   public static StringBuilder appendKeyDqValue(final StringBuilder sb, final String key, final Object value) {
      return sb.append(key).append('=').append('"').append(value).append('"');
   }

   public static void appendValue(final StringBuilder stringBuilder, final Object obj) {
      if (!appendSpecificTypes(stringBuilder, obj)) {
         stringBuilder.append(obj);
      }
   }

   public static boolean appendSpecificTypes(final StringBuilder stringBuilder, final Object obj) {
      if (obj == null || obj instanceof String) {
         stringBuilder.append((String)obj);
      } else if (obj instanceof StringBuilderFormattable) {
         ((StringBuilderFormattable)obj).formatTo(stringBuilder);
      } else if (obj instanceof CharSequence) {
         stringBuilder.append((CharSequence)obj);
      } else if (obj instanceof Integer) {
         stringBuilder.append(((Integer)obj).intValue());
      } else if (obj instanceof Long) {
         stringBuilder.append(((Long)obj).longValue());
      } else if (obj instanceof Double) {
         stringBuilder.append(((Double)obj).doubleValue());
      } else if (obj instanceof Boolean) {
         stringBuilder.append(((Boolean)obj).booleanValue());
      } else if (obj instanceof Character) {
         stringBuilder.append(((Character)obj).charValue());
      } else if (obj instanceof Short) {
         stringBuilder.append(((Short)obj).shortValue());
      } else if (obj instanceof Float) {
         stringBuilder.append(((Float)obj).floatValue());
      } else if (obj instanceof Byte) {
         stringBuilder.append(((Byte)obj).byteValue());
      } else {
         if (!isTime(obj) && !isDate(obj) && !(obj instanceof Temporal)) {
            return false;
         }

         stringBuilder.append(obj);
      }

      return true;
   }

   private static boolean isTime(final Object obj) {
      return timeClass != null && timeClass.isAssignableFrom(obj.getClass());
   }

   private static boolean isDate(final Object obj) {
      return dateClass != null && dateClass.isAssignableFrom(obj.getClass());
   }

   public static boolean equals(
      final CharSequence left, final int leftOffset, final int leftLength, final CharSequence right, final int rightOffset, final int rightLength
   ) {
      if (leftLength == rightLength) {
         for (int i = 0; i < rightLength; i++) {
            if (left.charAt(i + leftOffset) != right.charAt(i + rightOffset)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean equalsIgnoreCase(
      final CharSequence left, final int leftOffset, final int leftLength, final CharSequence right, final int rightOffset, final int rightLength
   ) {
      if (leftLength == rightLength) {
         for (int i = 0; i < rightLength; i++) {
            if (Character.toLowerCase(left.charAt(i + leftOffset)) != Character.toLowerCase(right.charAt(i + rightOffset))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static void trimToMaxSize(final StringBuilder stringBuilder, final int maxSize) {
      if (stringBuilder != null && stringBuilder.capacity() > maxSize) {
         stringBuilder.setLength(maxSize);
         stringBuilder.trimToSize();
      }
   }

   public static void escapeJson(final StringBuilder toAppendTo, final int start) {
      int escapeCount = 0;

      for (int i = start; i < toAppendTo.length(); i++) {
         char c = toAppendTo.charAt(i);
         switch (c) {
            case '\b':
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case '"':
            case '\\':
               escapeCount++;
               break;
            default:
               if (Character.isISOControl(c)) {
                  escapeCount += 5;
               }
         }
      }

      int lastChar = toAppendTo.length() - 1;
      toAppendTo.setLength(toAppendTo.length() + escapeCount);
      int lastPos = toAppendTo.length() - 1;

      for (int i = lastChar; lastPos > i; i--) {
         char c = toAppendTo.charAt(i);
         switch (c) {
            case '\b':
               lastPos = escapeAndDecrement(toAppendTo, lastPos, 'b');
               break;
            case '\t':
               lastPos = escapeAndDecrement(toAppendTo, lastPos, 't');
               break;
            case '\n':
               lastPos = escapeAndDecrement(toAppendTo, lastPos, 'n');
               break;
            case '\f':
               lastPos = escapeAndDecrement(toAppendTo, lastPos, 'f');
               break;
            case '\r':
               lastPos = escapeAndDecrement(toAppendTo, lastPos, 'r');
               break;
            case '"':
            case '\\':
               lastPos = escapeAndDecrement(toAppendTo, lastPos, c);
               break;
            default:
               if (Character.isISOControl(c)) {
                  toAppendTo.setCharAt(lastPos--, Chars.getUpperCaseHex(c & 15));
                  toAppendTo.setCharAt(lastPos--, Chars.getUpperCaseHex((c & 240) >> 4));
                  toAppendTo.setCharAt(lastPos--, '0');
                  toAppendTo.setCharAt(lastPos--, '0');
                  toAppendTo.setCharAt(lastPos--, 'u');
                  toAppendTo.setCharAt(lastPos--, '\\');
               } else {
                  toAppendTo.setCharAt(lastPos, c);
                  lastPos--;
               }
         }
      }
   }

   private static int escapeAndDecrement(final StringBuilder toAppendTo, int lastPos, final char c) {
      toAppendTo.setCharAt(lastPos--, c);
      toAppendTo.setCharAt(lastPos--, '\\');
      return lastPos;
   }

   public static void escapeXml(final StringBuilder toAppendTo, final int start) {
      int escapeCount = 0;
      int i = start;

      while (i < toAppendTo.length()) {
         int c = toAppendTo.codePointAt(i);
         switch (c) {
            case 34:
            case 39:
               escapeCount += 5;
               break;
            case 38:
               escapeCount += 4;
               break;
            case 60:
            case 62:
               escapeCount += 3;
         }

         i += Character.charCount(c);
      }

      i = toAppendTo.length() - 1;
      if (i >= 0) {
         toAppendTo.setLength(toAppendTo.length() + escapeCount);
         int lastPos = toAppendTo.length() - 1;

         for (int ix = i; lastPos >= start; ix--) {
            char c = toAppendTo.charAt(ix);
            if (ix > 0 && Character.isLowSurrogate(c)) {
               char previous = toAppendTo.charAt(ix - 1);
               if (!Character.isHighSurrogate(previous)) {
                  toAppendTo.setCharAt(lastPos--, '�');
               } else {
                  toAppendTo.setCharAt(lastPos--, c);
                  toAppendTo.setCharAt(lastPos--, previous);
                  ix--;
               }
            } else {
               switch (c) {
                  case '"':
                     toAppendTo.setCharAt(lastPos--, ';');
                     toAppendTo.setCharAt(lastPos--, 't');
                     toAppendTo.setCharAt(lastPos--, 'o');
                     toAppendTo.setCharAt(lastPos--, 'u');
                     toAppendTo.setCharAt(lastPos--, 'q');
                     toAppendTo.setCharAt(lastPos--, '&');
                     break;
                  case '&':
                     toAppendTo.setCharAt(lastPos--, ';');
                     toAppendTo.setCharAt(lastPos--, 'p');
                     toAppendTo.setCharAt(lastPos--, 'm');
                     toAppendTo.setCharAt(lastPos--, 'a');
                     toAppendTo.setCharAt(lastPos--, '&');
                     break;
                  case '\'':
                     toAppendTo.setCharAt(lastPos--, ';');
                     toAppendTo.setCharAt(lastPos--, 's');
                     toAppendTo.setCharAt(lastPos--, 'o');
                     toAppendTo.setCharAt(lastPos--, 'p');
                     toAppendTo.setCharAt(lastPos--, 'a');
                     toAppendTo.setCharAt(lastPos--, '&');
                     break;
                  case '<':
                     toAppendTo.setCharAt(lastPos--, ';');
                     toAppendTo.setCharAt(lastPos--, 't');
                     toAppendTo.setCharAt(lastPos--, 'l');
                     toAppendTo.setCharAt(lastPos--, '&');
                     break;
                  case '>':
                     toAppendTo.setCharAt(lastPos--, ';');
                     toAppendTo.setCharAt(lastPos--, 't');
                     toAppendTo.setCharAt(lastPos--, 'g');
                     toAppendTo.setCharAt(lastPos--, '&');
                     break;
                  default:
                     toAppendTo.setCharAt(lastPos--, isValidXml10(c) ? c : '�');
               }
            }
         }
      }
   }

   private static boolean isValidXml10(final char ch) {
      return ch >= ' ' && ch < '\ud800' || ch == '\t' || ch == '\n' || ch == '\r' || ch > '\udfff' && ch <= '�';
   }

   static {
      Class<?> clazz;
      try {
         clazz = Class.forName("java.sql.Time");
      } catch (ClassNotFoundException var3) {
         clazz = null;
      }

      timeClass = clazz;

      try {
         clazz = Class.forName("java.sql.Date");
      } catch (ClassNotFoundException var2) {
         clazz = null;
      }

      dateClass = clazz;
   }
}
