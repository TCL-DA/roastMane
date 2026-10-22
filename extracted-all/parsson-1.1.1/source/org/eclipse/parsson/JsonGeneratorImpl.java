package org.eclipse.parsson;

import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonGenerationException;
import jakarta.json.stream.JsonGenerator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map.Entry;
import org.eclipse.parsson.api.BufferPool;

class JsonGeneratorImpl implements JsonGenerator {
   private static final char[] INT_MIN_VALUE_CHARS = "-2147483648".toCharArray();
   private static final int[] INT_CHARS_SIZE_TABLE = new int[]{9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
   private static final char[] DIGIT_TENS = new char[]{
      '0',
      '0',
      '0',
      '0',
      '0',
      '0',
      '0',
      '0',
      '0',
      '0',
      '1',
      '1',
      '1',
      '1',
      '1',
      '1',
      '1',
      '1',
      '1',
      '1',
      '2',
      '2',
      '2',
      '2',
      '2',
      '2',
      '2',
      '2',
      '2',
      '2',
      '3',
      '3',
      '3',
      '3',
      '3',
      '3',
      '3',
      '3',
      '3',
      '3',
      '4',
      '4',
      '4',
      '4',
      '4',
      '4',
      '4',
      '4',
      '4',
      '4',
      '5',
      '5',
      '5',
      '5',
      '5',
      '5',
      '5',
      '5',
      '5',
      '5',
      '6',
      '6',
      '6',
      '6',
      '6',
      '6',
      '6',
      '6',
      '6',
      '6',
      '7',
      '7',
      '7',
      '7',
      '7',
      '7',
      '7',
      '7',
      '7',
      '7',
      '8',
      '8',
      '8',
      '8',
      '8',
      '8',
      '8',
      '8',
      '8',
      '8',
      '9',
      '9',
      '9',
      '9',
      '9',
      '9',
      '9',
      '9',
      '9',
      '9'
   };
   private static final char[] DIGIT_ONES = new char[]{
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9'
   };
   private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
   private final BufferPool bufferPool;
   private final Writer writer;
   private JsonGeneratorImpl.Context currentContext = new JsonGeneratorImpl.Context(JsonGeneratorImpl.Scope.IN_NONE);
   private final Deque<JsonGeneratorImpl.Context> stack = new ArrayDeque<>();
   private final char[] buf;
   private int len = 0;
   private boolean closed = false;

   JsonGeneratorImpl(Writer writer, BufferPool bufferPool) {
      this.writer = writer;
      this.bufferPool = bufferPool;
      this.buf = bufferPool.take();
   }

   JsonGeneratorImpl(OutputStream out, BufferPool bufferPool) {
      this(out, StandardCharsets.UTF_8, bufferPool);
   }

   JsonGeneratorImpl(OutputStream out, Charset encoding, BufferPool bufferPool) {
      this(new OutputStreamWriter(out, encoding), bufferPool);
   }

   public void flush() {
      this.flushBuffer();

      try {
         this.writer.flush();
      } catch (IOException var2) {
         throw new JsonException(JsonMessages.GENERATOR_FLUSH_IO_ERR(), var2);
      }
   }

   public JsonGenerator writeStartObject() {
      if (this.currentContext.scope == JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else if (this.currentContext.scope == JsonGeneratorImpl.Scope.IN_NONE && !this.currentContext.first) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_MULTIPLE_TEXT());
      } else {
         this.writeComma();
         this.writeChar('{');
         this.stack.push(this.currentContext);
         this.currentContext = new JsonGeneratorImpl.Context(JsonGeneratorImpl.Scope.IN_OBJECT);
         return this;
      }
   }

   public JsonGenerator writeStartObject(String name) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         this.writeName(name);
         this.writeChar('{');
         this.stack.push(this.currentContext);
         this.currentContext = new JsonGeneratorImpl.Context(JsonGeneratorImpl.Scope.IN_OBJECT);
         return this;
      }
   }

   private JsonGenerator writeName(String name) {
      this.writeComma();
      this.writeEscapedString(name);
      this.writeColon();
      return this;
   }

   public JsonGenerator write(String name, String fieldValue) {
      this.write(name, (CharSequence)fieldValue);
      return this;
   }

   public JsonGenerator write(String name, int value) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         this.writeName(name);
         this.writeInt(value);
         return this;
      }
   }

   public JsonGenerator write(String name, long value) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         this.writeName(name);
         this.writeString(String.valueOf(value));
         return this;
      }
   }

   public JsonGenerator write(String name, double value) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else if (!Double.isInfinite(value) && !Double.isNaN(value)) {
         this.writeName(name);
         this.writeString(String.valueOf(value));
         return this;
      } else {
         throw new NumberFormatException(JsonMessages.GENERATOR_DOUBLE_INFINITE_NAN());
      }
   }

   public JsonGenerator write(String name, BigInteger value) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         this.writeName(name);
         this.writeString(String.valueOf(value));
         return this;
      }
   }

   public JsonGenerator write(String name, BigDecimal value) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         this.writeName(name);
         this.writeString(String.valueOf(value));
         return this;
      }
   }

   public JsonGenerator write(String name, boolean value) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         this.writeName(name);
         this.writeString(value ? "true" : "false");
         return this;
      }
   }

   public JsonGenerator writeNull(String name) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         this.writeName(name);
         this.writeString("null");
         return this;
      }
   }

   public JsonGenerator write(JsonValue value) {
      this.checkContextForValue();
      switch (value.getValueType()) {
         case ARRAY:
            JsonArray array = (JsonArray)value;
            this.writeStartArray();

            for (JsonValue child : array) {
               this.write(child);
            }

            this.writeEnd();
            break;
         case OBJECT:
            JsonObject object = (JsonObject)value;
            this.writeStartObject();

            for (Entry<String, JsonValue> member : object.entrySet()) {
               this.write(member.getKey(), member.getValue());
            }

            this.writeEnd();
            break;
         case STRING:
            JsonString str = (JsonString)value;
            this.write(str.getString());
            break;
         case NUMBER:
            JsonNumber number = (JsonNumber)value;
            this.writeValue(number.toString());
            this.popFieldContext();
            break;
         case TRUE:
            this.write(true);
            break;
         case FALSE:
            this.write(false);
            break;
         case NULL:
            this.writeNull();
      }

      return this;
   }

   public JsonGenerator writeStartArray() {
      if (this.currentContext.scope == JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else if (this.currentContext.scope == JsonGeneratorImpl.Scope.IN_NONE && !this.currentContext.first) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_MULTIPLE_TEXT());
      } else {
         this.writeComma();
         this.writeChar('[');
         this.stack.push(this.currentContext);
         this.currentContext = new JsonGeneratorImpl.Context(JsonGeneratorImpl.Scope.IN_ARRAY);
         return this;
      }
   }

   public JsonGenerator writeStartArray(String name) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         this.writeName(name);
         this.writeChar('[');
         this.stack.push(this.currentContext);
         this.currentContext = new JsonGeneratorImpl.Context(JsonGeneratorImpl.Scope.IN_ARRAY);
         return this;
      }
   }

   public JsonGenerator write(String name, JsonValue value) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         switch (value.getValueType()) {
            case ARRAY:
               JsonArray array = (JsonArray)value;
               this.writeStartArray(name);

               for (JsonValue child : array) {
                  this.write(child);
               }

               this.writeEnd();
               break;
            case OBJECT:
               JsonObject object = (JsonObject)value;
               this.writeStartObject(name);

               for (Entry<String, JsonValue> member : object.entrySet()) {
                  this.write(member.getKey(), member.getValue());
               }

               this.writeEnd();
               break;
            case STRING:
               JsonString str = (JsonString)value;
               this.write(name, str.getChars());
               break;
            case NUMBER:
               JsonNumber number = (JsonNumber)value;
               this.writeValue(name, number.toString());
               break;
            case TRUE:
               this.write(name, true);
               break;
            case FALSE:
               this.write(name, false);
               break;
            case NULL:
               this.writeNull(name);
         }

         return this;
      }
   }

   public JsonGenerator write(String value) {
      this.checkContextForValue();
      this.writeComma();
      this.writeEscapedString(value);
      this.popFieldContext();
      return this;
   }

   public JsonGenerator write(int value) {
      this.checkContextForValue();
      this.writeComma();
      this.writeInt(value);
      this.popFieldContext();
      return this;
   }

   public JsonGenerator write(long value) {
      this.checkContextForValue();
      this.writeValue(String.valueOf(value));
      this.popFieldContext();
      return this;
   }

   public JsonGenerator write(double value) {
      this.checkContextForValue();
      if (!Double.isInfinite(value) && !Double.isNaN(value)) {
         this.writeValue(String.valueOf(value));
         this.popFieldContext();
         return this;
      } else {
         throw new NumberFormatException(JsonMessages.GENERATOR_DOUBLE_INFINITE_NAN());
      }
   }

   public JsonGenerator write(BigInteger value) {
      this.checkContextForValue();
      this.writeValue(value.toString());
      this.popFieldContext();
      return this;
   }

   private void checkContextForValue() {
      if (!this.currentContext.first
            && this.currentContext.scope != JsonGeneratorImpl.Scope.IN_ARRAY
            && this.currentContext.scope != JsonGeneratorImpl.Scope.IN_FIELD
         || this.currentContext.first && this.currentContext.scope == JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      }
   }

   public JsonGenerator write(BigDecimal value) {
      this.checkContextForValue();
      this.writeValue(value.toString());
      this.popFieldContext();
      return this;
   }

   private void popFieldContext() {
      if (this.currentContext.scope == JsonGeneratorImpl.Scope.IN_FIELD) {
         this.currentContext = this.stack.pop();
      }
   }

   public JsonGenerator write(boolean value) {
      this.checkContextForValue();
      this.writeComma();
      this.writeString(value ? "true" : "false");
      this.popFieldContext();
      return this;
   }

   public JsonGenerator writeNull() {
      this.checkContextForValue();
      this.writeComma();
      this.writeString("null");
      this.popFieldContext();
      return this;
   }

   private void writeValue(String value) {
      this.writeComma();
      this.writeString(value);
   }

   private void writeValue(String name, String value) {
      this.writeComma();
      this.writeEscapedString(name);
      this.writeColon();
      this.writeString(value);
   }

   public JsonGenerator writeKey(String name) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         this.writeName(name);
         this.stack.push(this.currentContext);
         this.currentContext = new JsonGeneratorImpl.Context(JsonGeneratorImpl.Scope.IN_FIELD);
         this.currentContext.first = false;
         return this;
      }
   }

   public JsonGenerator writeEnd() {
      if (this.currentContext.scope == JsonGeneratorImpl.Scope.IN_NONE) {
         throw new JsonGenerationException("writeEnd() cannot be called in no context");
      } else {
         this.writeChar((char)(this.currentContext.scope == JsonGeneratorImpl.Scope.IN_ARRAY ? ']' : '}'));
         this.currentContext = this.stack.pop();
         this.popFieldContext();
         return this;
      }
   }

   void write(String name, CharSequence fieldValue) {
      if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_OBJECT) {
         throw new JsonGenerationException(JsonMessages.GENERATOR_ILLEGAL_METHOD(this.currentContext.scope));
      } else {
         this.writeName(name);
         this.writeEscapedString(fieldValue);
      }
   }

   protected void writeComma() {
      if (this.isCommaAllowed()) {
         this.writeChar(',');
      }

      this.currentContext.first = false;
   }

   protected boolean inNone() {
      return this.currentContext.scope == JsonGeneratorImpl.Scope.IN_NONE;
   }

   boolean isCommaAllowed() {
      return !this.currentContext.first && this.currentContext.scope != JsonGeneratorImpl.Scope.IN_FIELD;
   }

   protected void writeColon() {
      this.writeChar(':');
   }

   public void close() {
      if (!this.closed) {
         if (this.currentContext.scope != JsonGeneratorImpl.Scope.IN_NONE || this.currentContext.first) {
            throw new JsonGenerationException(JsonMessages.GENERATOR_INCOMPLETE_JSON());
         }

         this.flushBuffer();

         try {
            this.writer.close();
         } catch (IOException var2) {
            throw new JsonException(JsonMessages.GENERATOR_CLOSE_IO_ERR(), var2);
         }

         this.bufferPool.recycle(this.buf);
         this.closed = true;
      }
   }

   void writeEscapedString(CharSequence string) {
      this.writeChar('"');
      int len = string.length();

      for (int i = 0; i < len; i++) {
         int begin = i;
         int end = i;

         char c;
         for (c = string.charAt(i); c >= ' ' && c <= 1114111 && c != '"' && c != '\\'; c = string.charAt(i)) {
            end = ++i;
            if (i >= len) {
               break;
            }
         }

         if (begin < end) {
            this.writeString(string, begin, end);
            if (i == len) {
               break;
            }
         }

         switch (c) {
            case '\b':
               this.writeChar('\\');
               this.writeChar('b');
               break;
            case '\t':
               this.writeChar('\\');
               this.writeChar('t');
               break;
            case '\n':
               this.writeChar('\\');
               this.writeChar('n');
               break;
            case '\f':
               this.writeChar('\\');
               this.writeChar('f');
               break;
            case '\r':
               this.writeChar('\\');
               this.writeChar('r');
               break;
            case '"':
            case '\\':
               this.writeChar('\\');
               this.writeChar(c);
               break;
            default:
               String hex = "000" + Integer.toHexString(c);
               this.writeString("\\u" + hex.substring(hex.length() - 4));
         }
      }

      this.writeChar('"');
   }

   void writeString(CharSequence str, int begin, int end) {
      while (begin < end) {
         int no = Math.min(this.buf.length - this.len, end - begin);
         if (str instanceof String) {
            ((String)str).getChars(begin, begin + no, this.buf, this.len);
         } else {
            this.getChars(str, begin, begin + no, this.buf, this.len);
         }

         begin += no;
         this.len += no;
         if (this.len >= this.buf.length) {
            this.flushBuffer();
         }
      }
   }

   void writeString(CharSequence str) {
      this.writeString(str, 0, str.length());
   }

   void writeChar(char c) {
      if (this.len >= this.buf.length) {
         this.flushBuffer();
      }

      this.buf[this.len++] = c;
   }

   void writeInt(int num) {
      int size;
      if (num == Integer.MIN_VALUE) {
         size = INT_MIN_VALUE_CHARS.length;
      } else {
         size = num < 0 ? stringSize(-num) + 1 : stringSize(num);
      }

      if (this.len + size >= this.buf.length) {
         this.flushBuffer();
      }

      if (num == Integer.MIN_VALUE) {
         System.arraycopy(INT_MIN_VALUE_CHARS, 0, this.buf, this.len, size);
      } else {
         fillIntChars(num, this.buf, this.len + size);
      }

      this.len += size;
   }

   void flushBuffer() {
      try {
         if (this.len > 0) {
            this.writer.write(this.buf, 0, this.len);
            this.len = 0;
         }
      } catch (IOException var2) {
         throw new JsonException(JsonMessages.GENERATOR_WRITE_IO_ERR(), var2);
      }
   }

   private static int stringSize(int x) {
      int i = 0;

      while (x > INT_CHARS_SIZE_TABLE[i]) {
         i++;
      }

      return i + 1;
   }

   void getChars(CharSequence str, int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      int length = srcEnd - srcBegin;

      for (int i = 0; i < length; i++) {
         int srcIdx = srcBegin + i;
         int dstIdx = dstBegin + i;
         dst[dstIdx] = str.charAt(srcIdx);
      }
   }

   private static void fillIntChars(int i, char[] buf, int index) {
      int charPos = index;
      char sign = 0;
      if (i < 0) {
         sign = '-';
         i = -i;
      }

      while (i >= 65536) {
         int q = i / 100;
         int r = i - ((q << 6) + (q << 5) + (q << 2));
         i = q;
         charPos--;
         buf[charPos] = DIGIT_ONES[r];
         charPos--;
         buf[charPos] = DIGIT_TENS[r];
      }

      int q;
      do {
         q = i * 52429 >>> 19;
         int r = i - ((q << 3) + (q << 1));
         charPos--;
         buf[charPos] = DIGITS[r];
         i = q;
      } while (q != 0);

      if (sign != 0) {
         charPos--;
         buf[charPos] = sign;
      }
   }

   private static class Context {
      boolean first = true;
      final JsonGeneratorImpl.Scope scope;

      Context(JsonGeneratorImpl.Scope scope) {
         this.scope = scope;
      }
   }

   private static enum Scope {
      IN_NONE,
      IN_OBJECT,
      IN_FIELD,
      IN_ARRAY;
   }
}
