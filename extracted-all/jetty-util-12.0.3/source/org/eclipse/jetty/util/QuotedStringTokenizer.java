package org.eclipse.jetty.util;

import java.util.Iterator;

public interface QuotedStringTokenizer {
   QuotedStringTokenizer CSV = builder().delimiters(",").ignoreOptionalWhiteSpace().build();

   static QuotedStringTokenizer.Builder builder() {
      return new QuotedStringTokenizer.Builder();
   }

   static boolean isQuoted(String s) {
      return s != null && s.length() > 0 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"';
   }

   default String quote(String s) {
      if (s == null) {
         return null;
      } else if (s.length() == 0) {
         return "\"\"";
      } else {
         StringBuffer b = new StringBuffer(s.length() + 8);
         this.quote(b, s);
         return b.toString();
      }
   }

   void quote(Appendable var1, String var2);

   String unquote(String var1);

   Iterator<String> tokenize(String var1);

   boolean needsQuoting(char var1);

   String quoteIfNeeded(String var1);

   void quoteIfNeeded(StringBuilder var1, String var2);

   public static class Builder {
      private String _delim;
      private boolean _returnQuotes;
      private boolean _returnDelimiters;
      private boolean _optionalWhiteSpace;
      private boolean _embeddedQuotes;
      private boolean _singleQuotes;
      private boolean _escapeOnlyQuote;
      private boolean _legacy;

      private Builder() {
      }

      public QuotedStringTokenizer.Builder delimiters(String delim) {
         this._delim = delim;
         return this;
      }

      public QuotedStringTokenizer.Builder returnQuotes() {
         this._returnQuotes = true;
         return this;
      }

      public QuotedStringTokenizer.Builder returnDelimiters() {
         this._returnDelimiters = true;
         return this;
      }

      public QuotedStringTokenizer.Builder ignoreOptionalWhiteSpace() {
         this._optionalWhiteSpace = true;
         return this;
      }

      public QuotedStringTokenizer.Builder allowEmbeddedQuotes() {
         this._embeddedQuotes = true;
         return this;
      }

      public QuotedStringTokenizer.Builder allowSingleQuote() {
         this._singleQuotes = true;
         return this;
      }

      public QuotedStringTokenizer.Builder allowEscapeOnlyForQuotes() {
         this._escapeOnlyQuote = true;
         return this;
      }

      public QuotedStringTokenizer.Builder legacy() {
         this._legacy = true;
         this._embeddedQuotes = true;
         return this;
      }

      public QuotedStringTokenizer build() {
         if (this._legacy) {
            if (this._optionalWhiteSpace) {
               throw new IllegalArgumentException("OWS not supported by legacy");
            } else if (this._escapeOnlyQuote) {
               throw new IllegalArgumentException("EscapeOnlyQuote not supported by legacy");
            } else if (!this._embeddedQuotes) {
               throw new IllegalArgumentException("EmbeddedQuotes must be used with legacy");
            } else {
               return new LegacyQuotedStringTokenizer(this._delim, this._returnDelimiters, this._returnQuotes, this._singleQuotes);
            }
         } else if (StringUtil.isEmpty(this._delim)) {
            throw new IllegalArgumentException("Delimiters must be provided");
         } else if (this._singleQuotes) {
            throw new IllegalArgumentException("Single quotes not supported by RFC9110");
         } else {
            return new RFC9110QuotedStringTokenizer(
               this._delim, this._optionalWhiteSpace, this._returnDelimiters, this._returnQuotes, this._embeddedQuotes, this._escapeOnlyQuote
            );
         }
      }
   }
}
