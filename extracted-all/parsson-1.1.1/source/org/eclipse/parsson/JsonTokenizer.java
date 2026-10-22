package org.eclipse.parsson;

import jakarta.json.JsonException;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParsingException;
import jakarta.json.stream.JsonParser.Event;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Arrays;
import org.eclipse.parsson.api.BufferPool;

final class JsonTokenizer implements Closeable {
   private static final int[] HEX = new int[128];
   private static final int HEX_LENGTH;
   private final BufferPool bufferPool;
   private final Reader reader;
   private char[] buf;
   private int readBegin;
   private int readEnd;
   private int storeBegin;
   private int storeEnd;
   private long lineNo = 1L;
   private long lastLineOffset = 0L;
   private long bufferOffset = 0L;
   private boolean minus;
   private boolean fracOrExp;
   private BigDecimal bd;

   JsonTokenizer(Reader reader, BufferPool bufferPool) {
      this.reader = reader;
      this.bufferPool = bufferPool;
      this.buf = bufferPool.take();
   }

   private void readString() {
      boolean inPlace = true;
      this.storeBegin = this.storeEnd = this.readBegin;

      while (true) {
         if (inPlace) {
            int ch;
            while (this.readBegin < this.readEnd && (ch = this.buf[this.readBegin]) >= 32 && ch != 92) {
               if (ch == 34) {
                  this.storeEnd = this.readBegin++;
                  return;
               }

               this.readBegin++;
            }

            this.storeEnd = this.readBegin;
         }

         int ch = this.read();
         if (ch >= 32 && ch != 34 && ch != 92) {
            if (!inPlace) {
               this.buf[this.storeEnd] = (char)ch;
            }

            this.storeEnd++;
         } else {
            switch (ch) {
               case 34:
                  return;
               case 92:
                  inPlace = false;
                  this.unescape();
                  break;
               default:
                  throw this.unexpectedChar(ch);
            }
         }
      }
   }

   private void unescape() {
      int ch = this.read();
      switch (ch) {
         case 34:
         case 47:
         case 92:
            this.buf[this.storeEnd++] = (char)ch;
            break;
         case 98:
            this.buf[this.storeEnd++] = '\b';
            break;
         case 102:
            this.buf[this.storeEnd++] = '\f';
            break;
         case 110:
            this.buf[this.storeEnd++] = '\n';
            break;
         case 114:
            this.buf[this.storeEnd++] = '\r';
            break;
         case 116:
            this.buf[this.storeEnd++] = '\t';
            break;
         case 117:
            int unicode = 0;

            for (int i = 0; i < 4; i++) {
               int ch3 = this.read();
               int digit = ch3 >= 0 && ch3 < HEX_LENGTH ? HEX[ch3] : -1;
               if (digit < 0) {
                  throw this.unexpectedChar(ch3);
               }

               unicode = unicode << 4 | digit;
            }

            this.buf[this.storeEnd++] = (char)unicode;
            break;
         default:
            throw this.unexpectedChar(ch);
      }
   }

   private int readNumberChar() {
      if (this.readBegin < this.readEnd) {
         return this.buf[this.readBegin++];
      } else {
         this.storeEnd = this.readBegin;
         return this.read();
      }
   }

   private void readNumber(int ch) {
      this.storeBegin = this.storeEnd = this.readBegin - 1;
      if (ch == 45) {
         this.minus = true;
         ch = this.readNumberChar();
         if (ch < 48 || ch > 57) {
            throw this.unexpectedChar(ch);
         }
      }

      if (ch == 48) {
         ch = this.readNumberChar();
      } else {
         do {
            ch = this.readNumberChar();
         } while (ch >= 48 && ch <= 57);
      }

      if (ch == 46) {
         this.fracOrExp = true;
         int count = 0;

         do {
            ch = this.readNumberChar();
            count++;
         } while (ch >= 48 && ch <= 57);

         if (count == 1) {
            throw this.unexpectedChar(ch);
         }
      }

      if (ch == 101 || ch == 69) {
         this.fracOrExp = true;
         ch = this.readNumberChar();
         if (ch == 43 || ch == 45) {
            ch = this.readNumberChar();
         }

         int count;
         for (count = 0; ch >= 48 && ch <= 57; count++) {
            ch = this.readNumberChar();
         }

         if (count == 0) {
            throw this.unexpectedChar(ch);
         }
      }

      if (ch != -1) {
         this.readBegin--;
         this.storeEnd = this.readBegin;
      }
   }

   private void readTrue() {
      int ch1 = this.read();
      if (ch1 != 114) {
         throw this.expectedChar(ch1, 'r');
      } else {
         int ch2 = this.read();
         if (ch2 != 117) {
            throw this.expectedChar(ch2, 'u');
         } else {
            int ch3 = this.read();
            if (ch3 != 101) {
               throw this.expectedChar(ch3, 'e');
            }
         }
      }
   }

   private void readFalse() {
      int ch1 = this.read();
      if (ch1 != 97) {
         throw this.expectedChar(ch1, 'a');
      } else {
         int ch2 = this.read();
         if (ch2 != 108) {
            throw this.expectedChar(ch2, 'l');
         } else {
            int ch3 = this.read();
            if (ch3 != 115) {
               throw this.expectedChar(ch3, 's');
            } else {
               int ch4 = this.read();
               if (ch4 != 101) {
                  throw this.expectedChar(ch4, 'e');
               }
            }
         }
      }
   }

   private void readNull() {
      int ch1 = this.read();
      if (ch1 != 117) {
         throw this.expectedChar(ch1, 'u');
      } else {
         int ch2 = this.read();
         if (ch2 != 108) {
            throw this.expectedChar(ch2, 'l');
         } else {
            int ch3 = this.read();
            if (ch3 != 108) {
               throw this.expectedChar(ch3, 'l');
            }
         }
      }
   }

   JsonTokenizer.JsonToken nextToken() {
      this.reset();
      int ch = this.read();

      while (ch == 32 || ch == 9 || ch == 10 || ch == 13) {
         if (ch == 13) {
            this.lineNo++;
            ch = this.read();
            if (ch != 10) {
               this.lastLineOffset = this.bufferOffset + (long)this.readBegin - 1L;
               continue;
            }

            this.lastLineOffset = this.bufferOffset + (long)this.readBegin;
         } else if (ch == 10) {
            this.lineNo++;
            this.lastLineOffset = this.bufferOffset + (long)this.readBegin;
         }

         ch = this.read();
      }

      switch (ch) {
         case -1:
            return JsonTokenizer.JsonToken.EOF;
         case 34:
            this.readString();
            return JsonTokenizer.JsonToken.STRING;
         case 44:
            return JsonTokenizer.JsonToken.COMMA;
         case 45:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
            this.readNumber(ch);
            return JsonTokenizer.JsonToken.NUMBER;
         case 58:
            return JsonTokenizer.JsonToken.COLON;
         case 91:
            return JsonTokenizer.JsonToken.SQUAREOPEN;
         case 93:
            return JsonTokenizer.JsonToken.SQUARECLOSE;
         case 102:
            this.readFalse();
            return JsonTokenizer.JsonToken.FALSE;
         case 110:
            this.readNull();
            return JsonTokenizer.JsonToken.NULL;
         case 116:
            this.readTrue();
            return JsonTokenizer.JsonToken.TRUE;
         case 123:
            return JsonTokenizer.JsonToken.CURLYOPEN;
         case 125:
            return JsonTokenizer.JsonToken.CURLYCLOSE;
         default:
            throw this.unexpectedChar(ch);
      }
   }

   boolean hasNextToken() {
      this.reset();
      int ch = this.peek();

      while (ch == 32 || ch == 9 || ch == 10 || ch == 13) {
         if (ch == 13) {
            this.lineNo++;
            this.readBegin++;
            ch = this.peek();
            if (ch != 10) {
               this.lastLineOffset = this.bufferOffset + (long)this.readBegin;
               continue;
            }

            this.lastLineOffset = this.bufferOffset + (long)this.readBegin + 1L;
         } else if (ch == 10) {
            this.lineNo++;
            this.lastLineOffset = this.bufferOffset + (long)this.readBegin + 1L;
         }

         this.readBegin++;
         ch = this.peek();
      }

      return ch != -1;
   }

   private int peek() {
      try {
         if (this.readBegin == this.readEnd) {
            int len = this.fillBuf();
            if (len == -1) {
               return -1;
            }

            assert len != 0;

            this.readBegin = this.storeEnd;
            this.readEnd = this.readBegin + len;
         }

         return this.buf[this.readBegin];
      } catch (IOException var2) {
         throw new JsonException(JsonMessages.TOKENIZER_IO_ERR(), var2);
      }
   }

   JsonLocation getLastCharLocation() {
      return new JsonLocationImpl(this.lineNo, this.bufferOffset + (long)this.readBegin - this.lastLineOffset, this.bufferOffset + (long)this.readBegin - 1L);
   }

   JsonLocation getLocation() {
      return new JsonLocationImpl(this.lineNo, this.bufferOffset + (long)this.readBegin - this.lastLineOffset + 1L, this.bufferOffset + (long)this.readBegin);
   }

   private int read() {
      try {
         if (this.readBegin == this.readEnd) {
            int len = this.fillBuf();
            if (len == -1) {
               return -1;
            }

            assert len != 0;

            this.readBegin = this.storeEnd;
            this.readEnd = this.readBegin + len;
         }

         return this.buf[this.readBegin++];
      } catch (IOException var2) {
         throw new JsonException(JsonMessages.TOKENIZER_IO_ERR(), var2);
      }
   }

   private int fillBuf() throws IOException {
      if (this.storeEnd != 0) {
         int storeLen = this.storeEnd - this.storeBegin;
         if (storeLen > 0) {
            if (storeLen == this.buf.length) {
               char[] doubleBuf = Arrays.copyOf(this.buf, 2 * this.buf.length);
               this.bufferPool.recycle(this.buf);
               this.buf = doubleBuf;
            } else {
               System.arraycopy(this.buf, this.storeBegin, this.buf, 0, storeLen);
               this.storeEnd = storeLen;
               this.storeBegin = 0;
               this.bufferOffset = this.bufferOffset + (long)(this.readBegin - this.storeEnd);
            }
         } else {
            this.storeBegin = this.storeEnd = 0;
            this.bufferOffset = this.bufferOffset + (long)this.readBegin;
         }
      } else {
         this.bufferOffset = this.bufferOffset + (long)this.readBegin;
      }

      return this.reader.read(this.buf, this.storeEnd, this.buf.length - this.storeEnd);
   }

   private void reset() {
      if (this.storeEnd != 0) {
         this.storeBegin = 0;
         this.storeEnd = 0;
         this.bd = null;
         this.minus = false;
         this.fracOrExp = false;
      }
   }

   String getValue() {
      return new String(this.buf, this.storeBegin, this.storeEnd - this.storeBegin);
   }

   CharSequence getCharSequence() {
      int len = this.storeEnd - this.storeBegin;
      return new StringBuilder(len).append(this.buf, this.storeBegin, len);
   }

   BigDecimal getBigDecimal() {
      if (this.bd == null) {
         this.bd = new BigDecimal(this.buf, this.storeBegin, this.storeEnd - this.storeBegin);
      }

      return this.bd;
   }

   int getInt() {
      int storeLen = this.storeEnd - this.storeBegin;
      if (!this.fracOrExp && (storeLen <= 9 || this.minus && storeLen <= 10)) {
         int num = 0;

         for (int i = this.minus ? 1 : 0; i < storeLen; i++) {
            num = num * 10 + (this.buf[this.storeBegin + i] - '0');
         }

         return this.minus ? -num : num;
      } else {
         return this.getBigDecimal().intValue();
      }
   }

   long getLong() {
      int storeLen = this.storeEnd - this.storeBegin;
      if (!this.fracOrExp && (storeLen <= 18 || this.minus && storeLen <= 19)) {
         long num = 0L;

         for (int i = this.minus ? 1 : 0; i < storeLen; i++) {
            num = num * 10L + (long)(this.buf[this.storeBegin + i] - '0');
         }

         return this.minus ? -num : num;
      } else {
         return this.getBigDecimal().longValue();
      }
   }

   boolean isDefinitelyInt() {
      int storeLen = this.storeEnd - this.storeBegin;
      return !this.fracOrExp && (storeLen <= 9 || this.minus && storeLen <= 10);
   }

   boolean isDefinitelyLong() {
      int storeLen = this.storeEnd - this.storeBegin;
      return !this.fracOrExp && (storeLen <= 18 || this.minus && storeLen <= 19);
   }

   boolean isIntegral() {
      return !this.fracOrExp || this.getBigDecimal().scale() == 0;
   }

   @Override
   public void close() throws IOException {
      this.reader.close();
      this.bufferPool.recycle(this.buf);
   }

   private JsonParsingException unexpectedChar(int ch) {
      JsonLocation location = this.getLastCharLocation();
      return new JsonParsingException(JsonMessages.TOKENIZER_UNEXPECTED_CHAR(ch, location), location);
   }

   private JsonParsingException expectedChar(int unexpected, char expected) {
      JsonLocation location = this.getLastCharLocation();
      return new JsonParsingException(JsonMessages.TOKENIZER_EXPECTED_CHAR(unexpected, location, expected), location);
   }

   static {
      Arrays.fill(HEX, -1);

      for (int i = 48; i <= 57; i++) {
         HEX[i] = i - 48;
      }

      for (int i = 65; i <= 70; i++) {
         HEX[i] = 10 + i - 65;
      }

      for (int i = 97; i <= 102; i++) {
         HEX[i] = 10 + i - 97;
      }

      HEX_LENGTH = HEX.length;
   }

   static enum JsonToken {
      CURLYOPEN(Event.START_OBJECT, false),
      SQUAREOPEN(Event.START_ARRAY, false),
      COLON(null, false),
      COMMA(null, false),
      STRING(Event.VALUE_STRING, true),
      NUMBER(Event.VALUE_NUMBER, true),
      TRUE(Event.VALUE_TRUE, true),
      FALSE(Event.VALUE_FALSE, true),
      NULL(Event.VALUE_NULL, true),
      CURLYCLOSE(Event.END_OBJECT, false),
      SQUARECLOSE(Event.END_ARRAY, false),
      EOF(null, false);

      private final Event event;
      private final boolean value;

      private JsonToken(Event event, boolean value) {
         this.event = event;
         this.value = value;
      }

      Event getEvent() {
         return this.event;
      }

      boolean isValue() {
         return this.value;
      }
   }
}
