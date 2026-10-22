package org.eclipse.jetty.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utf8StringBuilder implements CharsetStringBuilder {
   protected static final Logger LOG = LoggerFactory.getLogger(Utf8StringBuilder.class);
   public static final char REPLACEMENT = '�';
   private static final int UTF8_ACCEPT = 0;
   private static final int UTF8_REJECT = 12;
   protected int _state = 0;
   private static final byte[] BYTE_TABLE = new byte[]{
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      9,
      9,
      9,
      9,
      9,
      9,
      9,
      9,
      9,
      9,
      9,
      9,
      9,
      9,
      9,
      9,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      8,
      8,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      10,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      4,
      3,
      3,
      11,
      6,
      6,
      6,
      5,
      8,
      8,
      8,
      8,
      8,
      8,
      8,
      8,
      8,
      8,
      8
   };
   private static final byte[] TRANS_TABLE = new byte[]{
      0,
      12,
      24,
      36,
      60,
      96,
      84,
      12,
      12,
      12,
      48,
      72,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      0,
      12,
      12,
      12,
      12,
      12,
      0,
      12,
      0,
      12,
      12,
      12,
      24,
      12,
      12,
      12,
      12,
      12,
      24,
      12,
      24,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      24,
      12,
      12,
      12,
      12,
      12,
      24,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      24,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      36,
      12,
      36,
      12,
      12,
      12,
      36,
      12,
      12,
      12,
      12,
      12,
      36,
      12,
      36,
      12,
      12,
      12,
      36,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12,
      12
   };
   final StringBuilder _buffer;
   private int _codep;
   private boolean _codingErrors;

   public Utf8StringBuilder() {
      this._buffer = new StringBuilder();
   }

   public Utf8StringBuilder(int capacity) {
      this(new StringBuilder(capacity));
   }

   protected Utf8StringBuilder(StringBuilder buffer) {
      this._buffer = buffer;
   }

   public int length() {
      return this._buffer.length();
   }

   public boolean hasCodingErrors() {
      return this._codingErrors;
   }

   @Override
   public void reset() {
      this._state = 0;
      this._codep = 0;
      this._codingErrors = false;
      this.bufferReset();
   }

   public void partialReset() {
      this._codingErrors = false;
      this.bufferReset();
   }

   protected void checkCharAppend() {
      if (this._state != 0) {
         this.bufferAppend('�');
         this._state = 0;
         this._codingErrors = true;
      }
   }

   @Override
   public void append(char c) {
      this.checkCharAppend();
      this._buffer.append(c);
   }

   public void append(String s) {
      this.checkCharAppend();
      this._buffer.append(s);
   }

   public void append(String s, int offset, int length) {
      this.checkCharAppend();
      this._buffer.append(s, offset, offset + length);
   }

   @Override
   public void append(byte b) {
      try {
         this.appendByte(b);
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   @Override
   public void append(ByteBuffer buf) {
      try {
         while (buf.remaining() > 0) {
            this.appendByte(buf.get());
         }
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   @Override
   public void append(byte[] b) {
      this.append(b, 0, b.length);
   }

   @Override
   public void append(byte[] b, int offset, int length) {
      try {
         int end = offset + length;

         for (int i = offset; i < end; i++) {
            this.appendByte(b[i]);
         }
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      }
   }

   public boolean append(byte[] b, int offset, int length, int maxChars) {
      try {
         int end = offset + length;

         for (int i = offset; i < end; i++) {
            if (this.length() > maxChars) {
               return false;
            }

            this.appendByte(b[i]);
         }

         return true;
      } catch (IOException var7) {
         throw new RuntimeException(var7);
      }
   }

   protected void bufferAppend(char c) {
      this._buffer.append(c);
   }

   protected void bufferReset() {
      this._buffer.setLength(0);
   }

   public void appendByte(byte b) throws IOException {
      if (b > 0 && this._state == 0) {
         this.bufferAppend((char)(b & 255));
      } else {
         int i = b & 255;
         int type = BYTE_TABLE[i];
         this._codep = this._state == 0 ? 255 >> type & i : i & 63 | this._codep << 6;
         int next = TRANS_TABLE[this._state + type];
         if (LOG.isDebugEnabled()) {
            LOG.debug(
               "decode(state={}, b={}: {}) _codep={}, i={}, type={}, s={}",
               new Object[]{
                  String.format("%2d", this._state),
                  String.format("0x%02X", b & 255),
                  String.format("%8s", Integer.toBinaryString(b & 255)),
                  this._codep,
                  i,
                  type,
                  next == 12 ? "REJECT" : (next == 0 ? "ACCEPT" : next)
               }
            );
         }

         switch (next) {
            case 0:
               if (this._codep < 55296) {
                  this.bufferAppend((char)this._codep);
               } else {
                  for (char c : Character.toChars(this._codep)) {
                     this.bufferAppend(c);
                  }
               }

               this._codep = 0;
               this._state = next;
               break;
            case 12:
               this._codep = 0;
               this.bufferAppend('�');
               this._codingErrors = true;
               if (this._state != 0) {
                  this._state = 0;
                  this.appendByte(b);
               }
               break;
            default:
               this._state = next;
         }
      }
   }

   public boolean isComplete() {
      return this._state == 0;
   }

   public void complete() {
      if (!this.isComplete()) {
         this._codep = 0;
         this._state = 0;
         this._codingErrors = true;
         this.bufferAppend('�');
      }
   }

   @Override
   public String toString() {
      return "%s@%x{b=%s,s=%d,cp=%d,e=%b"
         .formatted(Utf8StringBuilder.class.getSimpleName(), this.hashCode(), this._buffer, this._state, this._codep, this._codingErrors);
   }

   public String toPartialString() {
      return this._buffer.toString();
   }

   public String toCompleteString() {
      this.complete();
      return this._buffer.toString();
   }

   public <X extends Throwable> String takeCompleteString(Supplier<X> onCodingError) throws X {
      this.complete();
      return this.takePartialString(onCodingError);
   }

   public <X extends Throwable> String takePartialString(Supplier<X> onCodingError) throws X {
      if (onCodingError != null && this.hasCodingErrors()) {
         X x = (X)onCodingError.get();
         if (x != null) {
            throw x;
         }
      }

      String string = this._buffer.toString();
      this.bufferReset();
      return string;
   }

   @Override
   public String build() throws CharacterCodingException {
      return this.takeCompleteString(Utf8StringBuilder.Utf8CharacterCodingException::new);
   }

   public static class Utf8CharacterCodingException extends CharacterCodingException {
      @Override
      public String getMessage() {
         return "Invalid UTF-8";
      }

      @Override
      public String toString() {
         return "%s@%x: Invalid UTF-8".formatted(CharacterCodingException.class.getSimpleName(), this.hashCode());
      }
   }

   public static class Utf8IllegalArgumentException extends IllegalArgumentException {
      public Utf8IllegalArgumentException() {
         super(new Utf8StringBuilder.Utf8CharacterCodingException());
      }
   }
}
