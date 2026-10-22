package org.eclipse.jetty.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class RFC9110QuotedStringTokenizer implements QuotedStringTokenizer {
   private final String _delim;
   private final boolean _optionalWhiteSpace;
   private final boolean _returnDelimiters;
   private final boolean _returnQuotes;
   private final boolean _embeddedQuotes;
   private final boolean _escapeOnlyQuote;

   RFC9110QuotedStringTokenizer(
      String delim, boolean optionalWhiteSpace, boolean returnDelimiters, boolean returnQuotes, boolean embeddedQuotes, boolean escapeOnlyQuote
   ) {
      this._delim = Objects.requireNonNull(delim);
      this._optionalWhiteSpace = optionalWhiteSpace;
      this._returnDelimiters = returnDelimiters;
      this._returnQuotes = returnQuotes;
      this._embeddedQuotes = embeddedQuotes;
      this._escapeOnlyQuote = escapeOnlyQuote;
      if (this._delim.indexOf(34) >= 0) {
         throw new IllegalArgumentException("Can't use quote as delimiters: " + this._delim);
      } else if (this._optionalWhiteSpace && this._delim.indexOf(32) >= 0) {
         throw new IllegalArgumentException("Can't delimit with space with optional white space");
      }
   }

   protected boolean isOptionalWhiteSpace(char c) {
      return Character.isWhitespace(c);
   }

   @Override
   public Iterator<String> tokenize(final String string) {
      Objects.requireNonNull(string);
      return new Iterator<String>() {
         private final StringBuilder _token = new StringBuilder();
         State _state = State.START;
         private boolean _hasToken;
         private int _ows = -1;
         private int _i = 0;

         @Override
         public boolean hasNext() {
            if (this._hasToken) {
               return true;
            } else {
               boolean escape = false;

               while (this._i < string.length()) {
                  char c = string.charAt(this._i++);
                  switch (this._state) {
                     case START:
                        if (RFC9110QuotedStringTokenizer.this._delim.indexOf(c) >= 0) {
                           if (RFC9110QuotedStringTokenizer.this._returnDelimiters) {
                              this._token.append(c);
                              return this._hasToken = true;
                           }
                        } else if (c == '"') {
                           if (RFC9110QuotedStringTokenizer.this._returnQuotes) {
                              this._token.append(c);
                           }

                           this._ows = -1;
                           this._state = State.QUOTE;
                        } else if (!RFC9110QuotedStringTokenizer.this._optionalWhiteSpace || !RFC9110QuotedStringTokenizer.this.isOptionalWhiteSpace(c)) {
                           this._token.append(c);
                           this._hasToken = true;
                           this._ows = -1;
                           this._state = State.TOKEN;
                        }
                        break;
                     case TOKEN:
                        this._hasToken = true;
                        if (RFC9110QuotedStringTokenizer.this._delim.indexOf(c) >= 0) {
                           if (RFC9110QuotedStringTokenizer.this._returnDelimiters) {
                              this._i--;
                           }

                           this._state = State.START;
                           if (this._ows >= 0) {
                              this._token.setLength(this._ows);
                           }

                           return this._hasToken;
                        }

                        if (RFC9110QuotedStringTokenizer.this._embeddedQuotes && c == '"') {
                           if (RFC9110QuotedStringTokenizer.this._returnQuotes) {
                              this._token.append(c);
                           }

                           this._ows = -1;
                           this._state = State.QUOTE;
                        } else {
                           if (RFC9110QuotedStringTokenizer.this._optionalWhiteSpace && RFC9110QuotedStringTokenizer.this.isOptionalWhiteSpace(c)) {
                              if (this._ows < 0) {
                                 this._ows = this._token.length();
                              }

                              this._token.append(c);
                              break;
                           }

                           this._ows = -1;
                           this._token.append(c);
                        }
                        break;
                     case QUOTE:
                        this._hasToken = true;
                        if (escape) {
                           escape = false;
                           this._token.append(c);
                        } else if (c == '"') {
                           if (RFC9110QuotedStringTokenizer.this._returnQuotes) {
                              this._token.append(c);
                           }

                           if (!RFC9110QuotedStringTokenizer.this._embeddedQuotes) {
                              this._state = State.END;
                              return this._hasToken;
                           }

                           this._ows = -1;
                           this._state = State.TOKEN;
                        } else if (c != '\\') {
                           this._token.append(c);
                        } else {
                           if (!RFC9110QuotedStringTokenizer.this._escapeOnlyQuote || this._i < string.length() && string.charAt(this._i) == '"') {
                              if (RFC9110QuotedStringTokenizer.this._returnQuotes) {
                                 this._token.append(c);
                              }

                              escape = true;
                              continue;
                           }

                           this._token.append(c);
                        }
                        break;
                     case END:
                        if (RFC9110QuotedStringTokenizer.this._delim.indexOf(c) >= 0) {
                           this._state = State.START;
                           if (RFC9110QuotedStringTokenizer.this._returnDelimiters) {
                              this._token.append(c);
                              return this._hasToken = true;
                           }
                           break;
                        } else {
                           if (RFC9110QuotedStringTokenizer.this._optionalWhiteSpace && RFC9110QuotedStringTokenizer.this.isOptionalWhiteSpace(c)) {
                              break;
                           }

                           throw new IllegalArgumentException("characters after end quote");
                        }
                     default:
                        throw new IllegalStateException();
                  }
               }

               if (this._state == State.QUOTE) {
                  throw new IllegalArgumentException("unterminated quote");
               } else {
                  if (this._ows >= 0 && this._hasToken) {
                     this._token.setLength(this._ows);
                  }

                  return this._hasToken;
               }
            }
         }

         public String next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               String t = this._token.toString();
               this._token.setLength(0);
               this._hasToken = false;
               return t;
            }
         }

         private static enum State {
            START,
            TOKEN,
            QUOTE,
            END;
         }
      };
   }

   @Override
   public void quote(Appendable buffer, String input) {
      if (input != null) {
         try {
            buffer.append('"');

            for (int i = 0; i < input.length(); i++) {
               char c = input.charAt(i);
               if (c != '"' && c != '\\') {
                  buffer.append(c);
               } else {
                  buffer.append('\\').append(c);
               }
            }

            buffer.append('"');
         } catch (IOException var5) {
            throw new RuntimeException(var5);
         }
      }
   }

   @Override
   public String quoteIfNeeded(String s) {
      return this.quoteIfNeededImpl(null, s);
   }

   @Override
   public void quoteIfNeeded(StringBuilder buf, String str) {
      this.quoteIfNeededImpl(buf, str);
   }

   @Override
   public boolean needsQuoting(char c) {
      return c == '\\' || c == '"' || this._optionalWhiteSpace && Character.isWhitespace(c) || this._delim.indexOf(c) >= 0;
   }

   private String quoteIfNeededImpl(StringBuilder buf, String str) {
      if (str == null) {
         return null;
      } else if (str.length() == 0) {
         if (buf == null) {
            return "\"\"";
         } else {
            buf.append("\"\"");
            return null;
         }
      } else {
         for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (this.needsQuoting(c)) {
               if (buf == null) {
                  return this.quote(str);
               }

               this.quote(buf, str);
               return null;
            }
         }

         if (buf == null) {
            return str;
         } else {
            buf.append(str);
            return null;
         }
      }
   }

   @Override
   public String unquote(String s) {
      if (s == null) {
         return null;
      } else if (s.length() < 2) {
         return s;
      } else {
         char first = s.charAt(0);
         char last = s.charAt(s.length() - 1);
         if (first == '"' && last == '"') {
            StringBuilder b = new StringBuilder(s.length() - 2);
            boolean escape = false;

            for (int i = 1; i < s.length() - 1; i++) {
               char c = s.charAt(i);
               if (escape) {
                  escape = false;
                  b.append(c);
               } else if (c == '\\') {
                  escape = true;
               } else {
                  b.append(c);
               }
            }

            return b.toString();
         } else {
            return s;
         }
      }
   }

   @Override
   public String toString() {
      StringBuilder out = new StringBuilder();
      out.append(this.getClass().getSimpleName()).append('@').append(Long.toHexString((long)this.hashCode())).append("{'").append(this._delim).append('\'');
      if (this._optionalWhiteSpace) {
         out.append(",optionalWhiteSpace");
      }

      if (this._returnDelimiters) {
         out.append(",returnDelimiters");
      }

      if (this._returnQuotes) {
         out.append(",returnQuotes");
      }

      if (this._embeddedQuotes) {
         out.append(",embeddedQuotes");
      }

      if (this._escapeOnlyQuote) {
         out.append(",escapeOnlyQuote");
      }

      out.append('}');
      return out.toString();
   }
}
