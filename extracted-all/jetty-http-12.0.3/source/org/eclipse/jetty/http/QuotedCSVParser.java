package org.eclipse.jetty.http;

import org.eclipse.jetty.util.QuotedStringTokenizer;

public abstract class QuotedCSVParser {
   public static final String DELIMITERS = ",;=";
   public static final QuotedStringTokenizer LIST_TOKENIZER = QuotedStringTokenizer.builder()
      .delimiters(",;=")
      .ignoreOptionalWhiteSpace()
      .allowEmbeddedQuotes()
      .returnDelimiters()
      .returnQuotes()
      .build();
   protected final boolean _keepQuotes;

   public QuotedCSVParser(boolean keepQuotes) {
      this._keepQuotes = keepQuotes;
   }

   public static String quote(String s) {
      return LIST_TOKENIZER.quote(s);
   }

   public static String quoteIfNeeded(String s) {
      return LIST_TOKENIZER.quoteIfNeeded(s);
   }

   public static String unquote(String s) {
      return LIST_TOKENIZER.unquote(s);
   }

   public void addValue(String value) {
      if (value != null) {
         StringBuffer buffer = new StringBuffer();
         int l = value.length();
         QuotedCSVParser.State state = QuotedCSVParser.State.VALUE;
         boolean quoted = false;
         boolean sloshed = false;
         int nwsLength = 0;
         int lastLength = 0;
         int valueLength = -1;
         int paramName = -1;
         int paramValue = -1;

         for (int i = 0; i <= l; i++) {
            char c = i == l ? 0 : value.charAt(i);
            if (quoted && c != 0) {
               if (sloshed) {
                  sloshed = false;
               } else {
                  switch (c) {
                     case '"':
                        quoted = false;
                        if (!this._keepQuotes) {
                           continue;
                        }
                        break;
                     case '\\':
                        sloshed = true;
                        if (!this._keepQuotes) {
                           continue;
                        }
                  }
               }

               buffer.append(c);
               nwsLength = buffer.length();
            } else {
               switch (c) {
                  case '\u0000':
                  case ',':
                     if (nwsLength > 0) {
                        buffer.setLength(nwsLength);
                        switch (state) {
                           case VALUE:
                              this.parsedValue(buffer);
                              valueLength = buffer.length();
                              break;
                           case PARAM_NAME:
                           case PARAM_VALUE:
                              this.parsedParam(buffer, valueLength, paramName, paramValue);
                              break;
                           default:
                              throw new IllegalStateException(state.toString());
                        }

                        this.parsedValueAndParams(buffer);
                     }

                     buffer.setLength(0);
                     lastLength = 0;
                     nwsLength = 0;
                     paramValue = -1;
                     paramName = -1;
                     valueLength = -1;
                     state = QuotedCSVParser.State.VALUE;
                     break;
                  case '\t':
                  case ' ':
                     if (buffer.length() > lastLength) {
                        buffer.append(c);
                     }
                     break;
                  case '"':
                     quoted = true;
                     if (this._keepQuotes) {
                        if (state == QuotedCSVParser.State.PARAM_VALUE && paramValue < 0) {
                           paramValue = nwsLength;
                        }

                        buffer.append(c);
                     } else if (state == QuotedCSVParser.State.PARAM_VALUE && paramValue < 0) {
                        paramValue = nwsLength;
                     }

                     nwsLength = buffer.length();
                     break;
                  case ';':
                     buffer.setLength(nwsLength);
                     if (state == QuotedCSVParser.State.VALUE) {
                        this.parsedValue(buffer);
                        valueLength = buffer.length();
                     } else {
                        this.parsedParam(buffer, valueLength, paramName, paramValue);
                     }

                     nwsLength = buffer.length();
                     paramValue = -1;
                     paramName = -1;
                     buffer.append(c);
                     lastLength = ++nwsLength;
                     state = QuotedCSVParser.State.PARAM_NAME;
                     break;
                  case '=':
                     switch (state) {
                        case VALUE:
                           paramName = 0;
                           buffer.setLength(nwsLength);
                           String param = buffer.toString();
                           buffer.setLength(0);
                           this.parsedValue(buffer);
                           valueLength = buffer.length();
                           buffer.append(param);
                           buffer.append(c);
                           lastLength = ++nwsLength;
                           state = QuotedCSVParser.State.PARAM_VALUE;
                           continue;
                        case PARAM_NAME:
                           buffer.setLength(nwsLength);
                           buffer.append(c);
                           lastLength = ++nwsLength;
                           state = QuotedCSVParser.State.PARAM_VALUE;
                           continue;
                        case PARAM_VALUE:
                           if (paramValue < 0) {
                              paramValue = nwsLength;
                           }

                           buffer.append(c);
                           nwsLength = buffer.length();
                           continue;
                        default:
                           throw new IllegalStateException(state.toString());
                     }
                  default:
                     switch (state) {
                        case VALUE:
                           buffer.append(c);
                           nwsLength = buffer.length();
                           break;
                        case PARAM_NAME:
                           if (paramName < 0) {
                              paramName = nwsLength;
                           }

                           buffer.append(c);
                           nwsLength = buffer.length();
                           break;
                        case PARAM_VALUE:
                           if (paramValue < 0) {
                              paramValue = nwsLength;
                           }

                           buffer.append(c);
                           nwsLength = buffer.length();
                           break;
                        default:
                           throw new IllegalStateException(state.toString());
                     }
               }
            }
         }
      }
   }

   protected void parsedValueAndParams(StringBuffer buffer) {
   }

   protected void parsedValue(StringBuffer buffer) {
   }

   protected void parsedParam(StringBuffer buffer, int valueLength, int paramName, int paramValue) {
   }

   private static enum State {
      VALUE,
      PARAM_NAME,
      PARAM_VALUE;
   }
}
