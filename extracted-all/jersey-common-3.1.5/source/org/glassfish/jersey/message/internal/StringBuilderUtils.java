package org.glassfish.jersey.message.internal;

public class StringBuilderUtils {
   public static void appendQuotedIfNonToken(StringBuilder b, String value) {
      if (value != null) {
         boolean quote = !GrammarUtil.isTokenString(value);
         if (quote) {
            b.append('"');
         }

         appendEscapingQuotes(b, value);
         if (quote) {
            b.append('"');
         }
      }
   }

   public static void appendQuotedIfWhitespace(StringBuilder b, String value) {
      if (value != null) {
         boolean quote = GrammarUtil.containsWhiteSpace(value);
         if (quote) {
            b.append('"');
         }

         appendEscapingQuotes(b, value);
         if (quote) {
            b.append('"');
         }
      }
   }

   public static void appendQuoted(StringBuilder b, String value) {
      b.append('"');
      appendEscapingQuotes(b, value);
      b.append('"');
   }

   public static void appendEscapingQuotes(StringBuilder b, String value) {
      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         if (c == '"') {
            b.append('\\');
         }

         b.append(c);
      }
   }

   private StringBuilderUtils() {
   }
}
