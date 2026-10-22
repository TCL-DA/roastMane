package org.eclipse.jetty.http;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.util.StringUtil;

public class HttpField {
   public static final QuotedStringTokenizer PARAMETER_TOKENIZER = QuotedStringTokenizer.builder()
      .delimiters(";")
      .ignoreOptionalWhiteSpace()
      .allowEmbeddedQuotes()
      .returnQuotes()
      .build();
   public static final QuotedStringTokenizer NAME_VALUE_TOKENIZER = QuotedStringTokenizer.builder().delimiters("=").build();
   private static final String ZERO_QUALITY = "q=0";
   private final HttpHeader _header;
   private final String _name;
   private final String _value;
   private int _hash = 0;

   public HttpField(HttpHeader header, String name, String value) {
      this._header = header;
      if (this._header != null && name == null) {
         this._name = this._header.asString();
      } else {
         this._name = Objects.requireNonNull(name, "name");
      }

      this._value = value != null ? value : "";
   }

   public HttpField(HttpHeader header, String value) {
      this(header, header.asString(), value);
   }

   public HttpField(HttpHeader header, HttpHeaderValue value) {
      this(header, header.asString(), value != null ? value.asString() : null);
   }

   public HttpField(String name, String value) {
      this((HttpHeader)HttpHeader.CACHE.get(name), name, value);
   }

   public static String getValueParameters(String valueParams, Map<String, String> parameters) {
      if (valueParams == null) {
         return null;
      } else {
         Iterator<String> tokens = PARAMETER_TOKENIZER.tokenize(valueParams);
         if (!tokens.hasNext()) {
            return null;
         } else {
            String value = tokens.next();
            if (parameters != null) {
               while (tokens.hasNext()) {
                  String token = tokens.next();
                  Iterator<String> nameValue = NAME_VALUE_TOKENIZER.tokenize(token);
                  if (nameValue.hasNext()) {
                     String paramName = nameValue.next();
                     String paramVal = null;
                     if (nameValue.hasNext()) {
                        paramVal = nameValue.next();
                     }

                     parameters.put(paramName, paramVal);
                  }
               }
            }

            return value;
         }
      }
   }

   public static String stripParameters(String value) {
      return getValueParameters(value, null);
   }

   @Deprecated
   public static String valueParameters(String value, Map<String, String> parameters) {
      return getValueParameters(value, parameters);
   }

   public boolean contains(String search) {
      return contains(this.getValue(), search);
   }

   public static boolean contains(String value, String search) {
      if (search == null) {
         return value == null;
      } else if (search.isEmpty()) {
         return false;
      } else if (value == null) {
         return false;
      } else if (search.equalsIgnoreCase(value)) {
         return true;
      } else {
         int state = 0;
         int match = 0;
         int param = 0;

         for (int i = 0; i < value.length(); i++) {
            char c = StringUtil.asciiToLowerCase(value.charAt(i));
            switch (state) {
               case 0:
                  switch (c) {
                     case '\t':
                     case ' ':
                     case ',':
                        continue;
                     case '"':
                        match = 0;
                        state = 2;
                        continue;
                     case ';':
                        param = -1;
                        match = -1;
                        state = 5;
                        continue;
                     default:
                        match = c == StringUtil.asciiToLowerCase(search.charAt(0)) ? 1 : -1;
                        state = 1;
                        continue;
                  }
               case 1:
                  switch (c) {
                     case ',':
                        if (match == search.length()) {
                           return true;
                        }

                        state = 0;
                        continue;
                     case ';':
                        param = match >= 0 ? 0 : -1;
                        state = 5;
                        continue;
                     default:
                        if (match > 0) {
                           if (match < search.length()) {
                              match = c == StringUtil.asciiToLowerCase(search.charAt(match)) ? match + 1 : -1;
                           } else if (c != ' ' && c != '\t') {
                              match = -1;
                           }
                        }
                        continue;
                  }
               case 2:
                  switch (c) {
                     case '"':
                        state = 4;
                        continue;
                     case '\\':
                        state = 3;
                        continue;
                     default:
                        if (match >= 0) {
                           if (match < search.length()) {
                              match = c == StringUtil.asciiToLowerCase(search.charAt(match)) ? match + 1 : -1;
                           } else {
                              match = -1;
                           }
                        }
                        continue;
                  }
               case 3:
                  if (match >= 0) {
                     if (match < search.length()) {
                        match = c == StringUtil.asciiToLowerCase(search.charAt(match)) ? match + 1 : -1;
                     } else {
                        match = -1;
                     }
                  }

                  state = 2;
                  break;
               case 4:
                  switch (c) {
                     case '\t':
                     case ' ':
                        continue;
                     case ',':
                        if (match == search.length()) {
                           return true;
                        }

                        state = 0;
                        continue;
                     case ';':
                        state = 5;
                        continue;
                     default:
                        match = -1;
                        continue;
                  }
               case 5:
                  switch (c) {
                     case '\t':
                     case ' ':
                        continue;
                     case ',':
                        if (param != "q=0".length() && match == search.length()) {
                           return true;
                        }

                        param = 0;
                        state = 0;
                        continue;
                     default:
                        if (param >= 0) {
                           if (param < "q=0".length()) {
                              param = c == "q=0".charAt(param) ? param + 1 : -1;
                           } else if (c != '0' && c != '.') {
                              param = -1;
                           }
                        }
                        continue;
                  }
               default:
                  throw new IllegalStateException();
            }
         }

         return param != "q=0".length() && match == search.length();
      }
   }

   public boolean containsLast(String search) {
      return containsLast(this.getValue(), search);
   }

   public static boolean containsLast(String value, String search) {
      if (search == null) {
         return value == null;
      } else if (search.isEmpty()) {
         return false;
      } else if (value == null) {
         return false;
      } else if (search.equalsIgnoreCase(value)) {
         return true;
      } else if (value.endsWith(search)) {
         int i = value.length() - search.length() - 1;

         while (i >= 0) {
            char c = value.charAt(i--);
            if (c == ',') {
               return true;
            }

            if (c != ' ') {
               return false;
            }
         }

         return true;
      } else {
         QuotedCSV csv = new QuotedCSV(false, value);
         List<String> values = csv.getValues();
         return !values.isEmpty() && search.equalsIgnoreCase(values.get(values.size() - 1));
      }
   }

   @Override
   public int hashCode() {
      int vhc = Objects.hashCode(this.getValue());
      return this._header == null ? vhc ^ this.nameHashCode() : vhc ^ this._header.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o instanceof HttpField field) {
         if (this._header != field.getHeader()) {
            return false;
         } else {
            return !this._name.equalsIgnoreCase(field.getName()) ? false : Objects.equals(this.getValue(), field.getValue());
         }
      } else {
         return false;
      }
   }

   public HttpHeader getHeader() {
      return this._header;
   }

   public int getIntValue() {
      return Integer.parseInt(this.getValue());
   }

   public long getLongValue() {
      return Long.parseLong(this.getValue());
   }

   public String getLowerCaseName() {
      return this._header != null ? this._header.lowerCaseName() : StringUtil.asciiToLowerCase(this._name);
   }

   public String getName() {
      return this._name;
   }

   public String getValue() {
      return this._value;
   }

   public String[] getValues() {
      List<String> values = this.getValueList();
      return values == null ? null : values.toArray(String[]::new);
   }

   public List<String> getValueList() {
      String value = this.getValue();
      if (value == null) {
         return null;
      } else {
         QuotedCSV list = new QuotedCSV(false, value);
         return list.getValues();
      }
   }

   public boolean isSameName(HttpField field) {
      if (field == null) {
         return false;
      } else if (field == this) {
         return true;
      } else {
         return this._header != null && this._header == field.getHeader() ? true : this.is(field.getName());
      }
   }

   public boolean is(String name) {
      return this._name.equalsIgnoreCase(name);
   }

   private int nameHashCode() {
      int h = this._hash;
      int len = this._name.length();
      if (h == 0 && len > 0) {
         for (int i = 0; i < len; i++) {
            char c = this._name.charAt(i);
            if (c >= 'a' && c <= 'z') {
               c = (char)(c - ' ');
            }

            h = 31 * h + c;
         }

         this._hash = h;
      }

      return h;
   }

   @Override
   public String toString() {
      String v = this.getValue();
      return this.getName() + ": " + (v == null ? "" : v);
   }

   public static class IntValueHttpField extends HttpField {
      private final int _int;

      public IntValueHttpField(HttpHeader header, String name, String value, int intValue) {
         super(header, name, value);
         this._int = intValue;
      }

      public IntValueHttpField(HttpHeader header, String name, String value) {
         this(header, name, value, Integer.parseInt(value));
      }

      public IntValueHttpField(HttpHeader header, String name, int intValue) {
         this(header, name, Integer.toString(intValue), intValue);
      }

      public IntValueHttpField(HttpHeader header, int value) {
         this(header, header.asString(), value);
      }

      public IntValueHttpField(String header, int value) {
         this((HttpHeader)HttpHeader.CACHE.get(header), header, value);
      }

      @Override
      public int getIntValue() {
         return this._int;
      }

      @Override
      public long getLongValue() {
         return (long)this._int;
      }
   }

   public static class LongValueHttpField extends HttpField {
      private final long _long;

      public LongValueHttpField(HttpHeader header, String name, String value, long longValue) {
         super(header, name, value);
         this._long = longValue;
      }

      public LongValueHttpField(HttpHeader header, String name, String value) {
         this(header, name, value, Long.parseLong(value));
      }

      public LongValueHttpField(HttpHeader header, String name, long value) {
         this(header, name, Long.toString(value), value);
      }

      public LongValueHttpField(HttpHeader header, long value) {
         this(header, header.asString(), value);
      }

      public LongValueHttpField(String header, long value) {
         this((HttpHeader)HttpHeader.CACHE.get(header), header, value);
      }

      @Override
      public int getIntValue() {
         return Math.toIntExact(this._long);
      }

      @Override
      public long getLongValue() {
         return this._long;
      }
   }
}
