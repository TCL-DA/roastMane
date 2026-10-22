package org.apache.logging.log4j.core.util;

import org.apache.logging.log4j.util.Strings;

public final class Transform {
   private static final String CDATA_START = "<![CDATA[";
   private static final String CDATA_END = "]]>";
   private static final String CDATA_PSEUDO_END = "]]&gt;";
   private static final String CDATA_EMBEDED_END = "]]>]]&gt;<![CDATA[";
   private static final int CDATA_END_LEN = "]]>".length();
   private static final char REPLACEMENT_CHAR = '�';

   private Transform() {
   }

   public static String escapeHtmlTags(final String input) {
      if (Strings.isEmpty(input)) {
         return input;
      } else {
         int length = input.length();
         int i = 0;

         while (i < length) {
            int cp = input.codePointAt(i);
            if (!isValidXml10(cp) || isHtmlTagCharacter(cp)) {
               StringBuilder out = new StringBuilder(length);
               out.append(input, 0, i);
               appendEscapingHtmlTags(input, i, length, out);
               return out.toString();
            }

            i += Character.charCount(cp);
         }

         return input;
      }
   }

   public static void appendEscapingCData(final StringBuilder buf, final String str) {
      if (str != null) {
         int end = str.indexOf("]]>");
         if (end < 0) {
            appendSanitizedXml10(str, 0, str.length(), buf);
         } else {
            int start;
            for (start = 0; end > -1; end = str.indexOf("]]>", start)) {
               appendSanitizedXml10(str, start, end, buf);
               buf.append("]]>]]&gt;<![CDATA[");
               start = end + CDATA_END_LEN;
               if (start >= str.length()) {
                  return;
               }
            }

            appendSanitizedXml10(str, start, str.length(), buf);
         }
      }
   }

   public static String escapeJsonControlCharacters(final String input) {
      if (!Strings.isEmpty(input)
         && (
            input.indexOf(34) != -1
               || input.indexOf(92) != -1
               || input.indexOf(47) != -1
               || input.indexOf(8) != -1
               || input.indexOf(12) != -1
               || input.indexOf(10) != -1
               || input.indexOf(13) != -1
               || input.indexOf(9) != -1
         )) {
         StringBuilder buf = new StringBuilder(input.length() + 6);
         int len = input.length();

         for (int i = 0; i < len; i++) {
            char ch = input.charAt(i);
            String escBs = "\\";
            switch (ch) {
               case '\b':
                  buf.append("\\");
                  buf.append('b');
                  break;
               case '\t':
                  buf.append("\\");
                  buf.append('t');
                  break;
               case '\n':
                  buf.append("\\");
                  buf.append('n');
                  break;
               case '\f':
                  buf.append("\\");
                  buf.append('f');
                  break;
               case '\r':
                  buf.append("\\");
                  buf.append('r');
                  break;
               case '"':
                  buf.append("\\");
                  buf.append(ch);
                  break;
               case '/':
                  buf.append("\\");
                  buf.append(ch);
                  break;
               case '\\':
                  buf.append("\\");
                  buf.append(ch);
                  break;
               default:
                  buf.append(ch);
            }
         }

         return buf.toString();
      } else {
         return input;
      }
   }

   private static void appendEscapingHtmlTags(final String input, int i, final int length, final StringBuilder buf) {
      while (i < length) {
         int ch = input.codePointAt(i);
         switch (ch) {
            case 34:
               buf.append("&quot;");
               break;
            case 38:
               buf.append("&amp;");
               break;
            case 39:
               buf.append("&#39;");
               break;
            case 60:
               buf.append("&lt;");
               break;
            case 62:
               buf.append("&gt;");
               break;
            default:
               buf.appendCodePoint(isValidXml10(ch) ? ch : '�');
         }

         i += Character.charCount(ch);
      }
   }

   private static boolean isHtmlTagCharacter(final int cp) {
      return cp == 60 || cp == 62 || cp == 38 || cp == 34 || cp == 39;
   }

   private static void appendSanitizedXml10(final String input, final int start, final int end, final StringBuilder out) {
      int i = start;

      while (i < end) {
         int cp = input.codePointAt(i);
         out.appendCodePoint(isValidXml10(cp) ? cp : '�');
         i += Character.charCount(cp);
      }
   }

   private static boolean isValidXml10(final int codePoint) {
      assert codePoint >= 0 && codePoint <= 1114111;

      return codePoint >= 32 && codePoint < 55296
         || codePoint == 9
         || codePoint == 10
         || codePoint == 13
         || codePoint > 57343 && codePoint <= 65533
         || codePoint >= 65536;
   }
}
