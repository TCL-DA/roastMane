package nonapi.io.github.classgraph.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;

final class JSONParser extends Parser {
   private JSONParser(String string) throws ParseException {
      super(string);
   }

   private int getAndParseHexChar() throws ParseException {
      char hexChar = this.getc();
      if (hexChar >= '0' && hexChar <= '9') {
         return hexChar - 48;
      } else if (hexChar >= 'a' && hexChar <= 'f') {
         return hexChar - 97 + 10;
      } else if (hexChar >= 'A' && hexChar <= 'F') {
         return hexChar - 65 + 10;
      } else {
         throw new ParseException(this, "Invalid character in Unicode escape sequence: " + hexChar);
      }
   }

   private CharSequence parseString() throws ParseException {
      this.skipWhitespace();
      if (this.peek() != '"') {
         return null;
      } else {
         this.next();
         int startIdx = this.getPosition();
         boolean hasEscape = false;

         while (this.hasMore()) {
            char c = this.getc();
            if (c == '\\') {
               switch (this.getc()) {
                  case '"':
                  case '\'':
                  case '/':
                  case '\\':
                  case 'b':
                  case 'f':
                  case 'n':
                  case 'r':
                  case 't':
                     hasEscape = true;
                     break;
                  case 'u':
                     hasEscape = true;
                     this.advance(4);
                     break;
                  default:
                     throw new ParseException(this, "Invalid escape sequence: \\" + c);
               }
            } else if (c == '"') {
               break;
            }
         }

         int endIdx = this.getPosition() - 1;
         if (!hasEscape) {
            return this.getSubsequence(startIdx, endIdx);
         } else {
            this.setPosition(startIdx);
            StringBuilder buf = new StringBuilder();

            while (this.hasMore()) {
               char c = this.getc();
               if (c == '\\') {
                  char c2 = this.getc();
                  switch (c2) {
                     case '"':
                     case '\'':
                     case '/':
                     case '\\':
                        buf.append(c2);
                        break;
                     case 'b':
                        buf.append('\b');
                        break;
                     case 'f':
                        buf.append('\f');
                        break;
                     case 'n':
                        buf.append('\n');
                        break;
                     case 'r':
                        buf.append('\r');
                        break;
                     case 't':
                        buf.append('\t');
                        break;
                     case 'u':
                        int charVal = 0;
                        charVal = this.getAndParseHexChar() << 12;
                        charVal |= this.getAndParseHexChar() << 8;
                        charVal |= this.getAndParseHexChar() << 4;
                        charVal |= this.getAndParseHexChar();
                        buf.append((char)charVal);
                        break;
                     default:
                        throw new ParseException(this, "Invalid escape sequence: \\" + c);
                  }
               } else {
                  if (c == '"') {
                     break;
                  }

                  buf.append(c);
               }
            }

            this.skipWhitespace();
            return buf.toString();
         }
      }
   }

   private Number parseNumber() throws ParseException {
      int startIdx = this.getPosition();
      if (this.peekMatches("Infinity")) {
         this.advance(8);
         return Double.POSITIVE_INFINITY;
      } else if (this.peekMatches("-Infinity")) {
         this.advance(9);
         return Double.NEGATIVE_INFINITY;
      } else if (this.peekMatches("NaN")) {
         this.advance(3);
         return Double.NaN;
      } else {
         if (this.peek() == '-') {
            this.next();
         }

         int integralStartIdx = this.getPosition();

         while (this.hasMore()) {
            char c = this.peek();
            if (c < '0' || c > '9') {
               break;
            }

            this.next();
         }

         int integralEndIdx = this.getPosition();
         int numIntegralDigits = integralEndIdx - integralStartIdx;
         if (numIntegralDigits == 0) {
            throw new ParseException(this, "Expected a number");
         } else {
            boolean hasFractionalPart = this.peek() == '.';
            if (hasFractionalPart) {
               this.next();

               while (this.hasMore()) {
                  char c = this.peek();
                  if (c < '0' || c > '9') {
                     break;
                  }

                  this.next();
               }

               if (this.getPosition() - (integralEndIdx + 1) == 0) {
                  throw new ParseException(this, "Expected digits after decimal point");
               }
            }

            boolean hasExponentPart = this.peek() == 'e' || this.peek() == 'E';
            if (hasExponentPart) {
               this.next();
               char sign = this.peek();
               if (sign == '-' || sign == '+') {
                  this.next();
               }

               int exponentStart = this.getPosition();

               while (this.hasMore()) {
                  char c = this.peek();
                  if (c < '0' || c > '9') {
                     break;
                  }

                  this.next();
               }

               if (this.getPosition() - exponentStart == 0) {
                  throw new ParseException(this, "Expected an exponent");
               }
            }

            int endIdx = this.getPosition();
            String numberStr = this.getSubstring(startIdx, endIdx);
            if (hasFractionalPart || hasExponentPart) {
               return Double.valueOf(numberStr);
            } else if (numIntegralDigits < 10) {
               return Integer.valueOf(numberStr);
            } else if (numIntegralDigits == 10) {
               long longVal = Long.parseLong(numberStr);
               return (Number)(longVal >= -2147483648L && longVal <= 2147483647L ? (int)longVal : longVal);
            } else {
               return Long.valueOf(numberStr);
            }
         }
      }
   }

   private JSONArray parseJSONArray() throws ParseException {
      this.expect('[');
      this.skipWhitespace();
      if (this.peek() == ']') {
         this.next();
         return new JSONArray(Collections.emptyList());
      } else {
         List<Object> elements = new ArrayList<>();

         for (boolean first = true; this.peek() != ']'; elements.add(this.parseJSON())) {
            if (first) {
               first = false;
            } else {
               this.expect(',');
            }
         }

         this.expect(']');
         return new JSONArray(elements);
      }
   }

   private JSONObject parseJSONObject() throws ParseException {
      this.expect('{');
      this.skipWhitespace();
      if (this.peek() == '}') {
         this.next();
         return new JSONObject(Collections.emptyList());
      } else {
         List<Entry<String, Object>> kvPairs = new ArrayList<>();
         JSONObject jsonObject = new JSONObject(kvPairs);
         boolean first = true;

         while (this.peek() != '}') {
            if (first) {
               first = false;
            } else {
               this.expect(',');
            }

            CharSequence key = this.parseString();
            if (key == null) {
               throw new ParseException(this, "Object keys must be strings");
            }

            if (this.peek() != ':') {
               return null;
            }

            this.expect(':');
            Object value = this.parseJSON();
            if (key.equals("__ID")) {
               if (value == null) {
                  throw new ParseException(this, "Got null value for \"__ID\" key");
               }

               jsonObject.objectId = (CharSequence)value;
            } else {
               kvPairs.add(new SimpleEntry<>(key.toString(), value));
            }
         }

         this.expect('}');
         return jsonObject;
      }
   }

   private Object parseJSON() throws ParseException {
      this.skipWhitespace();
      char c = this.peek();
      if (c == '{') {
         JSONObject obj = this.parseJSONObject();
         this.skipWhitespace();
         return obj;
      } else if (c == '[') {
         JSONArray arr = this.parseJSONArray();
         this.skipWhitespace();
         return arr;
      } else if (c == '"') {
         CharSequence charSequence = this.parseString();
         this.skipWhitespace();
         if (charSequence == null) {
            throw new ParseException(this, "Invalid string");
         } else {
            return charSequence;
         }
      } else if (this.peekMatches("true")) {
         this.advance(4);
         this.skipWhitespace();
         return Boolean.TRUE;
      } else if (this.peekMatches("false")) {
         this.advance(5);
         this.skipWhitespace();
         return Boolean.FALSE;
      } else if (this.peekMatches("null")) {
         this.advance(4);
         this.skipWhitespace();
         return null;
      } else {
         Number num = this.parseNumber();
         this.skipWhitespace();
         return num;
      }
   }

   static Object parseJSON(String str) throws ParseException {
      return new JSONParser(str).parseJSON();
   }
}
