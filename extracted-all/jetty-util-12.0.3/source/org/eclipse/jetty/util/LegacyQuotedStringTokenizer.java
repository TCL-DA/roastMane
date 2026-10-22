package org.eclipse.jetty.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class LegacyQuotedStringTokenizer implements QuotedStringTokenizer {
   private final String _delim;
   private final boolean _returnQuotes;
   private final boolean _returnDelimiters;
   private final boolean _singleQuotes;

   LegacyQuotedStringTokenizer(String delim, boolean returnDelimiters, boolean returnQuotes, boolean singleQuotes) {
      this._delim = delim == null ? "\t\n\r" : delim;
      this._returnDelimiters = returnDelimiters;
      this._returnQuotes = returnQuotes;
      this._singleQuotes = singleQuotes;
   }

   @Override
   public Iterator<String> tokenize(String string) {
      final LegacyQuotedStringTokenizer.LegacyTokenizer tokenizer = new LegacyQuotedStringTokenizer.LegacyTokenizer(string);
      return new Iterator<String>() {
         @Override
         public boolean hasNext() {
            return tokenizer.hasMoreTokens();
         }

         public String next() {
            return tokenizer.nextToken();
         }
      };
   }

   @Override
   public boolean needsQuoting(char c) {
      return LegacyQuotedStringTokenizer.LegacyTokenizer.needsQuoting(c, this._delim);
   }

   @Override
   public String quoteIfNeeded(String s) {
      return LegacyQuotedStringTokenizer.LegacyTokenizer.quoteIfNeeded(s, this._delim);
   }

   @Override
   public void quoteIfNeeded(StringBuilder buf, String str) {
      LegacyQuotedStringTokenizer.LegacyTokenizer.quoteIfNeeded(buf, str, this._delim);
   }

   @Override
   public void quote(Appendable buffer, String input) {
      LegacyQuotedStringTokenizer.LegacyTokenizer.quote(buffer, input);
   }

   @Override
   public String unquote(String s) {
      return LegacyQuotedStringTokenizer.LegacyTokenizer.unquote(s);
   }

   private class LegacyTokenizer extends StringTokenizer {
      private final String _string;
      private final StringBuffer _token;
      private boolean _hasToken = false;
      private int _i = 0;
      private static final char[] escapes = new char[32];

      public LegacyTokenizer(String str) {
         super("");
         this._string = str;
         if (LegacyQuotedStringTokenizer.this._delim.indexOf(39) < 0 && LegacyQuotedStringTokenizer.this._delim.indexOf(34) < 0) {
            this._token = new StringBuffer(this._string.length() > 1024 ? 512 : this._string.length() / 2);
         } else {
            throw new Error("Can't use quotes as delimiters: " + LegacyQuotedStringTokenizer.this._delim);
         }
      }

      @Override
      public boolean hasMoreTokens() {
         if (this._hasToken) {
            return true;
         } else {
            int state = 0;
            boolean escape = false;

            while (this._i < this._string.length()) {
               char c = this._string.charAt(this._i++);
               switch (state) {
                  case 0:
                     if (LegacyQuotedStringTokenizer.this._delim.indexOf(c) >= 0) {
                        if (LegacyQuotedStringTokenizer.this._returnDelimiters) {
                           this._token.append(c);
                           return this._hasToken = true;
                        }
                     } else if (c == '\'' && LegacyQuotedStringTokenizer.this._singleQuotes) {
                        if (LegacyQuotedStringTokenizer.this._returnQuotes) {
                           this._token.append(c);
                        }

                        state = 2;
                     } else if (c == '"') {
                        if (LegacyQuotedStringTokenizer.this._returnQuotes) {
                           this._token.append(c);
                        }

                        state = 3;
                     } else {
                        this._token.append(c);
                        this._hasToken = true;
                        state = 1;
                     }
                     break;
                  case 1:
                     this._hasToken = true;
                     if (LegacyQuotedStringTokenizer.this._delim.indexOf(c) >= 0) {
                        if (LegacyQuotedStringTokenizer.this._returnDelimiters) {
                           this._i--;
                        }

                        return this._hasToken;
                     }

                     if (c == '\'' && LegacyQuotedStringTokenizer.this._singleQuotes) {
                        if (LegacyQuotedStringTokenizer.this._returnQuotes) {
                           this._token.append(c);
                        }

                        state = 2;
                     } else if (c == '"') {
                        if (LegacyQuotedStringTokenizer.this._returnQuotes) {
                           this._token.append(c);
                        }

                        state = 3;
                     } else {
                        this._token.append(c);
                     }
                     break;
                  case 2:
                     this._hasToken = true;
                     if (escape) {
                        escape = false;
                        this._token.append(c);
                     } else if (c == '\'') {
                        if (LegacyQuotedStringTokenizer.this._returnQuotes) {
                           this._token.append(c);
                        }

                        state = 1;
                     } else if (c == '\\') {
                        if (LegacyQuotedStringTokenizer.this._returnQuotes) {
                           this._token.append(c);
                        }

                        escape = true;
                     } else {
                        this._token.append(c);
                     }
                     break;
                  case 3:
                     this._hasToken = true;
                     if (escape) {
                        escape = false;
                        this._token.append(c);
                     } else if (c == '"') {
                        if (LegacyQuotedStringTokenizer.this._returnQuotes) {
                           this._token.append(c);
                        }

                        state = 1;
                     } else if (c == '\\') {
                        if (LegacyQuotedStringTokenizer.this._returnQuotes) {
                           this._token.append(c);
                        }

                        escape = true;
                     } else {
                        this._token.append(c);
                     }
                     break;
                  default:
                     throw new IllegalStateException();
               }
            }

            return this._hasToken;
         }
      }

      @Override
      public String nextToken() throws NoSuchElementException {
         if (this.hasMoreTokens() && this._token != null) {
            String t = this._token.toString();
            this._token.setLength(0);
            this._hasToken = false;
            return t;
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public String nextToken(String delim) throws NoSuchElementException {
         throw new UnsupportedOperationException();
      }

      @Override
      public int countTokens() {
         return -1;
      }

      public static boolean needsQuoting(char c, String delim) {
         return c == '\\' || c == '"' || c == '\'' || Character.isWhitespace(c) || delim.indexOf(c) >= 0;
      }

      public static String quoteIfNeeded(String s, String delim) {
         if (s == null) {
            return null;
         } else if (s.length() == 0) {
            return "\"\"";
         } else {
            for (int i = 0; i < s.length(); i++) {
               char c = s.charAt(i);
               if (needsQuoting(c, delim)) {
                  StringBuffer b = new StringBuffer(s.length() + 8);
                  quote(b, s);
                  return b.toString();
               }
            }

            return s;
         }
      }

      public static void quoteIfNeeded(StringBuilder buf, String str, String delim) {
         if (str != null) {
            int len = str.length();
            if (len != 0) {
               for (int i = 0; i < len; i++) {
                  int ch = str.codePointAt(i);
                  if (delim.indexOf(ch) >= 0) {
                     quote(buf, str);
                     return;
                  }
               }

               buf.append(str);
            }
         }
      }

      public static String quote(String s) {
         if (s == null) {
            return null;
         } else if (s.length() == 0) {
            return "\"\"";
         } else {
            StringBuffer b = new StringBuffer(s.length() + 8);
            quote(b, s);
            return b.toString();
         }
      }

      public static void quote(Appendable buffer, String input) {
         if (input != null) {
            try {
               buffer.append('"');

               for (int i = 0; i < input.length(); i++) {
                  char c = input.charAt(i);
                  if (c >= ' ') {
                     if (c == '"' || c == '\\') {
                        buffer.append('\\');
                     }

                     buffer.append(c);
                  } else {
                     char escape = escapes[c];
                     if (escape == '\uffff') {
                        buffer.append('\\').append('u').append('0').append('0');
                        if (c < 16) {
                           buffer.append('0');
                        }

                        buffer.append(Integer.toString(c, 16));
                     } else {
                        buffer.append('\\').append(escape);
                     }
                  }
               }

               buffer.append('"');
            } catch (IOException var5) {
               throw new RuntimeException(var5);
            }
         }
      }

      public static String unquote(String s) {
         return unquote(s, false);
      }

      public static String unquote(String s, boolean lenient) {
         if (s == null) {
            return null;
         } else if (s.length() < 2) {
            return s;
         } else {
            char first = s.charAt(0);
            char last = s.charAt(s.length() - 1);
            if (first == last && (first == '"' || first == '\'')) {
               StringBuilder b = new StringBuilder(s.length() - 2);
               boolean escape = false;

               for (int i = 1; i < s.length() - 1; i++) {
                  char c = s.charAt(i);
                  if (escape) {
                     escape = false;
                     switch (c) {
                        case '"':
                           b.append('"');
                           break;
                        case '/':
                           b.append('/');
                           break;
                        case '\\':
                           b.append('\\');
                           break;
                        case 'b':
                           b.append('\b');
                           break;
                        case 'f':
                           b.append('\f');
                           break;
                        case 'n':
                           b.append('\n');
                           break;
                        case 'r':
                           b.append('\r');
                           break;
                        case 't':
                           b.append('\t');
                           break;
                        case 'u':
                           b.append(
                              (char)(
                                 (TypeUtil.convertHexDigit((byte)s.charAt(i++)) << 24)
                                    + (TypeUtil.convertHexDigit((byte)s.charAt(i++)) << 16)
                                    + (TypeUtil.convertHexDigit((byte)s.charAt(i++)) << 8)
                                    + TypeUtil.convertHexDigit((byte)s.charAt(i++))
                              )
                           );
                           break;
                        default:
                           if (lenient && !isValidEscaping(c)) {
                              b.append('\\');
                           }

                           b.append(c);
                     }
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

      private static boolean isValidEscaping(char c) {
         return c == 'n' || c == 'r' || c == 't' || c == 'f' || c == 'b' || c == '\\' || c == '/' || c == '"' || c == 'u';
      }

      static {
         Arrays.fill(escapes, '\uffff');
         escapes[8] = 'b';
         escapes[9] = 't';
         escapes[10] = 'n';
         escapes[12] = 'f';
         escapes[13] = 'r';
      }
   }
}
