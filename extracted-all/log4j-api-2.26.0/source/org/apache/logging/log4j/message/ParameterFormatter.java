package org.apache.logging.log4j.message;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StringBuilders;

final class ParameterFormatter {
   static final String RECURSION_PREFIX = "[...";
   static final String RECURSION_SUFFIX = "...]";
   static final String ERROR_PREFIX = "[!!!";
   static final String ERROR_SEPARATOR = "=>";
   static final String ERROR_MSG_SEPARATOR = ":";
   static final String ERROR_SUFFIX = "!!!]";
   private static final char DELIM_START = '{';
   private static final char DELIM_STOP = '}';
   private static final char ESCAPE_CHAR = '\\';
   private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZone(ZoneId.systemDefault());
   private static final Logger STATUS_LOGGER = StatusLogger.getLogger();

   private ParameterFormatter() {
   }

   static ParameterFormatter.MessagePatternAnalysis analyzePattern(final String pattern, final int argCount) {
      ParameterFormatter.MessagePatternAnalysis analysis = new ParameterFormatter.MessagePatternAnalysis();
      analyzePattern(pattern, argCount, analysis);
      return analysis;
   }

   static void analyzePattern(final String pattern, final int argCount, final ParameterFormatter.MessagePatternAnalysis analysis) {
      int l;
      if (pattern != null && (l = pattern.length()) >= 2) {
         boolean escaped = false;
         analysis.placeholderCount = 0;
         analysis.escapedCharFound = false;

         for (int i = 0; i < l - 1; i++) {
            char c = pattern.charAt(i);
            if (c == '\\') {
               analysis.escapedCharFound = true;
               escaped = !escaped;
            } else if (escaped) {
               escaped = false;
            } else if (c == '{' && pattern.charAt(i + 1) == '}') {
               if (argCount >= 0 && analysis.placeholderCount >= argCount) {
                  analysis.placeholderCount++;
                  i++;
               } else {
                  analysis.ensurePlaceholderCharIndicesCapacity(argCount);
                  analysis.placeholderCharIndices[analysis.placeholderCount++] = i++;
               }
            }
         }
      } else {
         analysis.placeholderCount = 0;
      }
   }

   static String format(final String pattern, final Object[] args, int argCount) {
      StringBuilder result = new StringBuilder();
      ParameterFormatter.MessagePatternAnalysis analysis = analyzePattern(pattern, argCount);
      formatMessage(result, pattern, args, argCount, analysis);
      return result.toString();
   }

   static void formatMessage(
      final StringBuilder buffer, final String pattern, final Object[] args, final int argCount, final ParameterFormatter.MessagePatternAnalysis analysis
   ) {
      if (pattern != null && args != null && analysis.placeholderCount != 0) {
         if (analysis.placeholderCount != argCount) {
            int noThrowableArgCount = argCount < 1 ? 0 : argCount - (isLastArgumentThrowable(args, argCount) ? 1 : 0);
            if (analysis.placeholderCount != noThrowableArgCount) {
               STATUS_LOGGER.warn("found {} argument placeholders, but provided {} for pattern `{}`", analysis.placeholderCount, argCount, pattern);
            }
         }

         if (analysis.escapedCharFound) {
            formatMessageContainingEscapes(buffer, pattern, args, argCount, analysis);
         } else {
            formatMessageContainingNoEscapes(buffer, pattern, args, argCount, analysis);
         }
      } else {
         buffer.append(pattern);
      }
   }

   private static boolean isLastArgumentThrowable(final Object[] args, final int argCount) {
      Object lastArgument = args[argCount - 1];
      return lastArgument == null || lastArgument instanceof Throwable;
   }

   private static void formatMessageContainingNoEscapes(
      final StringBuilder buffer, final String pattern, final Object[] args, final int argCount, final ParameterFormatter.MessagePatternAnalysis analysis
   ) {
      int precedingTextStartIndex = 0;
      int argLimit = Math.min(analysis.placeholderCount, argCount);

      for (int argIndex = 0; argIndex < argLimit; argIndex++) {
         int placeholderCharIndex = analysis.placeholderCharIndices[argIndex];
         buffer.append(pattern, precedingTextStartIndex, placeholderCharIndex);
         recursiveDeepToString(args[argIndex], buffer);
         precedingTextStartIndex = placeholderCharIndex + 2;
      }

      buffer.append(pattern, precedingTextStartIndex, pattern.length());
   }

   private static void formatMessageContainingEscapes(
      final StringBuilder buffer, final String pattern, final Object[] args, final int argCount, final ParameterFormatter.MessagePatternAnalysis analysis
   ) {
      int precedingTextStartIndex = 0;
      int argLimit = Math.min(analysis.placeholderCount, argCount);

      for (int argIndex = 0; argIndex < argLimit; argIndex++) {
         int placeholderCharIndex = analysis.placeholderCharIndices[argIndex];
         copyMessagePatternContainingEscapes(buffer, pattern, precedingTextStartIndex, placeholderCharIndex);
         recursiveDeepToString(args[argIndex], buffer);
         precedingTextStartIndex = placeholderCharIndex + 2;
      }

      copyMessagePatternContainingEscapes(buffer, pattern, precedingTextStartIndex, pattern.length());
   }

   private static void copyMessagePatternContainingEscapes(final StringBuilder buffer, final String pattern, final int startIndex, final int endIndex) {
      boolean escaped = false;

      for (int i = startIndex; i < endIndex; i++) {
         char c = pattern.charAt(i);
         if (c == '\\') {
            if (escaped) {
               escaped = false;
            } else {
               escaped = true;
               buffer.append(c);
            }
         } else if (escaped) {
            if (c == '{' && pattern.charAt(i + 1) == '}') {
               buffer.setLength(buffer.length() - 1);
               buffer.append("{}");
               i++;
            } else {
               buffer.append(c);
            }

            escaped = false;
         } else {
            buffer.append(c);
         }
      }
   }

   static String deepToString(final Object o) {
      if (o == null) {
         return null;
      } else if (o instanceof String) {
         return (String)o;
      } else if (o instanceof Integer) {
         return Integer.toString((Integer)o);
      } else if (o instanceof Long) {
         return Long.toString((Long)o);
      } else if (o instanceof Double) {
         return Double.toString((Double)o);
      } else if (o instanceof Boolean) {
         return Boolean.toString((Boolean)o);
      } else if (o instanceof Character) {
         return Character.toString((Character)o);
      } else if (o instanceof Short) {
         return Short.toString((Short)o);
      } else if (o instanceof Float) {
         return Float.toString((Float)o);
      } else if (o instanceof Byte) {
         return Byte.toString((Byte)o);
      } else {
         StringBuilder str = new StringBuilder();
         recursiveDeepToString(o, str);
         return str.toString();
      }
   }

   static void recursiveDeepToString(final Object o, final StringBuilder str) {
      recursiveDeepToString(o, str, null);
   }

   private static void recursiveDeepToString(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
      if (!appendSpecialTypes(o, str)) {
         if (isMaybeRecursive(o)) {
            appendPotentiallyRecursiveValue(o, str, dejaVu);
         } else {
            tryObjectToString(o, str);
         }
      }
   }

   private static boolean appendSpecialTypes(final Object o, final StringBuilder str) {
      return StringBuilders.appendSpecificTypes(str, o) || appendDate(o, str);
   }

   private static boolean appendDate(final Object o, final StringBuilder str) {
      if (!(o instanceof Date)) {
         return false;
      } else {
         DATE_FORMATTER.formatTo(((Date)o).toInstant(), str);
         return true;
      }
   }

   private static boolean isMaybeRecursive(final Object o) {
      return o.getClass().isArray() || o instanceof Map || o instanceof Collection;
   }

   private static void appendPotentiallyRecursiveValue(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
      Class<?> oClass = o.getClass();
      if (oClass.isArray()) {
         appendArray(o, str, dejaVu, oClass);
      } else if (o instanceof Map) {
         appendMap(o, str, dejaVu);
      } else {
         if (!(o instanceof Collection)) {
            throw new IllegalArgumentException("was expecting a container, found " + oClass);
         }

         appendCollection(o, str, dejaVu);
      }
   }

   private static void appendArray(final Object o, final StringBuilder str, final Set<Object> dejaVu, final Class<?> oClass) {
      if (oClass == byte[].class) {
         appendArray((byte[])o, str);
      } else if (oClass == short[].class) {
         appendArray((short[])o, str);
      } else if (oClass == int[].class) {
         appendArray((int[])o, str);
      } else if (oClass == long[].class) {
         appendArray((long[])o, str);
      } else if (oClass == float[].class) {
         appendArray((float[])o, str);
      } else if (oClass == double[].class) {
         appendArray((double[])o, str);
      } else if (oClass == boolean[].class) {
         appendArray((boolean[])o, str);
      } else if (oClass == char[].class) {
         appendArray((char[])o, str);
      } else {
         Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
         boolean seen = !effectiveDejaVu.add(o);
         if (seen) {
            String id = identityToString(o);
            str.append("[...").append(id).append("...]");
         } else {
            Object[] oArray = (Object[])o;
            str.append('[');
            boolean first = true;

            for (Object current : oArray) {
               if (first) {
                  first = false;
               } else {
                  str.append(", ");
               }

               recursiveDeepToString(current, str, cloneDejaVu(effectiveDejaVu));
            }

            str.append(']');
         }
      }
   }

   private static void appendMap(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
      Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
      boolean seen = !effectiveDejaVu.add(o);
      if (seen) {
         String id = identityToString(o);
         str.append("[...").append(id).append("...]");
      } else {
         Map<?, ?> oMap = (Map<?, ?>)o;
         str.append('{');
         boolean isFirst = true;

         for (Entry<?, ?> entry : oMap.entrySet()) {
            if (isFirst) {
               isFirst = false;
            } else {
               str.append(", ");
            }

            Object key = entry.getKey();
            Object value = entry.getValue();
            recursiveDeepToString(key, str, cloneDejaVu(effectiveDejaVu));
            str.append('=');
            recursiveDeepToString(value, str, cloneDejaVu(effectiveDejaVu));
         }

         str.append('}');
      }
   }

   private static void appendCollection(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
      Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
      boolean seen = !effectiveDejaVu.add(o);
      if (seen) {
         String id = identityToString(o);
         str.append("[...").append(id).append("...]");
      } else {
         Collection<?> oCol = (Collection<?>)o;
         str.append('[');
         boolean isFirst = true;

         for (Object anOCol : oCol) {
            if (isFirst) {
               isFirst = false;
            } else {
               str.append(", ");
            }

            recursiveDeepToString(anOCol, str, cloneDejaVu(effectiveDejaVu));
         }

         str.append(']');
      }
   }

   private static Set<Object> getOrCreateDejaVu(final Set<Object> dejaVu) {
      return dejaVu == null ? createDejaVu() : dejaVu;
   }

   private static Set<Object> createDejaVu() {
      return Collections.newSetFromMap(new IdentityHashMap<>());
   }

   private static Set<Object> cloneDejaVu(final Set<Object> dejaVu) {
      Set<Object> clonedDejaVu = createDejaVu();
      clonedDejaVu.addAll(dejaVu);
      return clonedDejaVu;
   }

   private static void tryObjectToString(final Object o, final StringBuilder str) {
      try {
         str.append(o.toString());
      } catch (Throwable var3) {
         handleErrorInObjectToString(o, str, var3);
      }
   }

   private static void handleErrorInObjectToString(final Object o, final StringBuilder str, final Throwable t) {
      str.append("[!!!");
      str.append(identityToString(o));
      str.append("=>");
      String msg = t.getMessage();
      String className = t.getClass().getName();
      str.append(className);
      if (!className.equals(msg)) {
         str.append(":");
         str.append(msg);
      }

      str.append("!!!]");
   }

   static String identityToString(final Object obj) {
      return obj == null ? null : obj.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(obj));
   }

   private static void appendArray(final byte[] a, final StringBuilder str) {
      int len = a.length;
      if (len == 0) {
         str.append("[]");
      } else {
         str.append('[').append(a[0]);

         for (int i = 1; i < len; i++) {
            str.append(", ").append(a[i]);
         }

         str.append(']');
      }
   }

   private static void appendArray(final short[] a, final StringBuilder str) {
      int len = a.length;
      if (len == 0) {
         str.append("[]");
      } else {
         str.append('[').append(a[0]);

         for (int i = 1; i < len; i++) {
            str.append(", ").append(a[i]);
         }

         str.append(']');
      }
   }

   static void appendArray(final int[] a, final StringBuilder str) {
      int len = a.length;
      if (len == 0) {
         str.append("[]");
      } else {
         str.append('[').append(a[0]);

         for (int i = 1; i < len; i++) {
            str.append(", ").append(a[i]);
         }

         str.append(']');
      }
   }

   private static void appendArray(final long[] a, final StringBuilder str) {
      int len = a.length;
      if (len == 0) {
         str.append("[]");
      } else {
         str.append('[').append(a[0]);

         for (int i = 1; i < len; i++) {
            str.append(", ").append(a[i]);
         }

         str.append(']');
      }
   }

   private static void appendArray(final float[] a, final StringBuilder str) {
      int len = a.length;
      if (len == 0) {
         str.append("[]");
      } else {
         str.append('[').append(a[0]);

         for (int i = 1; i < len; i++) {
            str.append(", ").append(a[i]);
         }

         str.append(']');
      }
   }

   private static void appendArray(final double[] a, final StringBuilder str) {
      int len = a.length;
      if (len == 0) {
         str.append("[]");
      } else {
         str.append('[').append(a[0]);

         for (int i = 1; i < len; i++) {
            str.append(", ").append(a[i]);
         }

         str.append(']');
      }
   }

   private static void appendArray(final boolean[] a, final StringBuilder str) {
      int len = a.length;
      if (len == 0) {
         str.append("[]");
      } else {
         str.append('[').append(a[0]);

         for (int i = 1; i < len; i++) {
            str.append(", ").append(a[i]);
         }

         str.append(']');
      }
   }

   private static void appendArray(char[] a, final StringBuilder str) {
      int len = a.length;
      if (len == 0) {
         str.append("[]");
      } else {
         str.append('[').append(a[0]);

         for (int i = 1; i < len; i++) {
            str.append(", ").append(a[i]);
         }

         str.append(']');
      }
   }

   static final class MessagePatternAnalysis implements Serializable {
      private static final long serialVersionUID = -5974082575968329887L;
      private static final int PLACEHOLDER_CHAR_INDEX_BUFFER_INITIAL_SIZE = 8;
      private static final int PLACEHOLDER_CHAR_INDEX_BUFFER_SIZE_INCREMENT = 8;
      int placeholderCount;
      int[] placeholderCharIndices;
      boolean escapedCharFound;

      private void ensurePlaceholderCharIndicesCapacity(final int argCount) {
         if (this.placeholderCharIndices == null) {
            int length = Math.max(argCount, 8);
            this.placeholderCharIndices = new int[length];
         } else if (this.placeholderCount >= this.placeholderCharIndices.length) {
            int newLength = argCount > 0 ? argCount : Math.addExact(this.placeholderCharIndices.length, 8);
            int[] newPlaceholderCharIndices = new int[newLength];
            System.arraycopy(this.placeholderCharIndices, 0, newPlaceholderCharIndices, 0, this.placeholderCount);
            this.placeholderCharIndices = newPlaceholderCharIndices;
         }
      }
   }
}
