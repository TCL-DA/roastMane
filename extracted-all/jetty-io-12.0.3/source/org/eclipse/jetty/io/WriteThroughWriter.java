package org.eclipse.jetty.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.eclipse.jetty.util.ByteArrayOutputStream2;

public abstract class WriteThroughWriter extends Writer {
   static final int DEFAULT_MAX_WRITE_SIZE = 1024;
   private final int _maxWriteSize;
   final OutputStream _out;
   final ByteArrayOutputStream2 _bytes;

   protected WriteThroughWriter(OutputStream out) {
      this(out, 0);
   }

   protected WriteThroughWriter(OutputStream out, int maxWriteSize) {
      this._maxWriteSize = maxWriteSize <= 0 ? 1024 : maxWriteSize;
      this._out = out;
      this._bytes = new ByteArrayOutputStream2(this._maxWriteSize);
   }

   public static WriteThroughWriter newWriter(OutputStream outputStream, String charset) throws IOException {
      if (StandardCharsets.ISO_8859_1.name().equalsIgnoreCase(charset)) {
         return new WriteThroughWriter.Iso88591Writer(outputStream);
      } else {
         return (WriteThroughWriter)(StandardCharsets.UTF_8.name().equalsIgnoreCase(charset)
            ? new WriteThroughWriter.Utf8Writer(outputStream)
            : new WriteThroughWriter.EncodingWriter(outputStream, charset));
      }
   }

   public static WriteThroughWriter newWriter(OutputStream outputStream, Charset charset) throws IOException {
      if (StandardCharsets.ISO_8859_1 == charset) {
         return new WriteThroughWriter.Iso88591Writer(outputStream);
      } else {
         return (WriteThroughWriter)(StandardCharsets.UTF_8.equals(charset)
            ? new WriteThroughWriter.Utf8Writer(outputStream)
            : new WriteThroughWriter.EncodingWriter(outputStream, charset));
      }
   }

   public int getMaxWriteSize() {
      return this._maxWriteSize;
   }

   @Override
   public void close() throws IOException {
      this._out.close();
   }

   @Override
   public void flush() throws IOException {
      this._out.flush();
   }

   public abstract WriteThroughWriter append(CharSequence var1) throws IOException;

   @Override
   public void write(String string, int offset, int length) throws IOException {
      while (length > this._maxWriteSize) {
         this.append(subSequence(string, offset, this._maxWriteSize));
         offset += this._maxWriteSize;
         length -= this._maxWriteSize;
      }

      this.append(subSequence(string, offset, length));
   }

   @Override
   public void write(char[] chars, int offset, int length) throws IOException {
      while (length > this._maxWriteSize) {
         this.append(subSequence(chars, offset, this._maxWriteSize));
         offset += this._maxWriteSize;
         length -= this._maxWriteSize;
      }

      this.append(subSequence(chars, offset, length));
   }

   static CharSequence subSequence(final String string, final int offset, final int length) {
      Objects.requireNonNull(string);
      if (offset == 0 && string.length() == length) {
         return string;
      } else if (length == 0) {
         return "";
      } else {
         int end = offset + length;
         if (offset >= 0 && offset <= end && end <= string.length()) {
            return new CharSequence() {
               @Override
               public int length() {
                  return length;
               }

               @Override
               public char charAt(int index) {
                  return string.charAt(offset + index);
               }

               @Override
               public CharSequence subSequence(int start, int end) {
                  return WriteThroughWriter.subSequence(string, offset + start, end - start);
               }

               @Override
               public String toString() {
                  return string.substring(offset, offset + length);
               }
            };
         } else {
            throw new IndexOutOfBoundsException("offset and/or length out of range");
         }
      }
   }

   static CharSequence subSequence(char[] chars, int offset, int length) {
      return (CharSequence)(length == 0 ? "" : CharBuffer.wrap(chars, offset, length));
   }

   private static class EncodingWriter extends WriteThroughWriter {
      final Writer _converter;

      public EncodingWriter(OutputStream out, String encoding) throws IOException {
         super(out);
         this._converter = new OutputStreamWriter(this._bytes, encoding);
      }

      public EncodingWriter(OutputStream out, Charset charset) throws IOException {
         super(out);
         this._converter = new OutputStreamWriter(this._bytes, charset);
      }

      @Override
      public WriteThroughWriter append(CharSequence charSequence) throws IOException {
         assert charSequence.length() <= this.getMaxWriteSize();

         this._bytes.reset();
         this._converter.append(charSequence);
         this._converter.flush();
         this._bytes.writeTo(this._out);
         return this;
      }
   }

   private static class Iso88591Writer extends WriteThroughWriter {
      private Iso88591Writer(OutputStream out) {
         super(out);
      }

      @Override
      public WriteThroughWriter append(CharSequence charSequence) throws IOException {
         assert charSequence.length() <= this.getMaxWriteSize();

         if (charSequence.length() == 1) {
            int c = charSequence.charAt(0);
            this._out.write(c < 256 ? c : 63);
            return this;
         } else {
            this._bytes.reset();
            int bytes = 0;
            byte[] buffer = this._bytes.getBuf();
            int length = charSequence.length();

            for (int offset = 0; offset < length; offset++) {
               int c = charSequence.charAt(offset);
               buffer[bytes++] = (byte)(c < 256 ? c : 63);
            }

            if (bytes >= 0) {
               this._bytes.setCount(bytes);
            }

            this._bytes.writeTo(this._out);
            return this;
         }
      }
   }

   private static class Utf8Writer extends WriteThroughWriter {
      int _surrogate = 0;

      private Utf8Writer(OutputStream out) {
         super(out);
      }

      @Override
      public WriteThroughWriter append(CharSequence charSequence) throws IOException {
         assert charSequence.length() <= this.getMaxWriteSize();

         int length = charSequence.length();
         int offset = 0;

         while (length > 0) {
            this._bytes.reset();
            int chars = Math.min(length, this.getMaxWriteSize());
            byte[] buffer = this._bytes.getBuf();
            int bytes = this._bytes.getCount();
            if (bytes + chars > buffer.length) {
               chars = buffer.length - bytes;
            }

            for (int i = 0; i < chars; i++) {
               int code = charSequence.charAt(offset + i);
               if (this._surrogate == 0) {
                  if (Character.isHighSurrogate((char)code)) {
                     this._surrogate = code;
                     continue;
                  }
               } else if (Character.isLowSurrogate((char)code)) {
                  code = Character.toCodePoint((char)this._surrogate, (char)code);
               } else {
                  code = this._surrogate;
                  this._surrogate = 0;
                  i--;
               }

               if ((code & -128) == 0) {
                  if (bytes >= buffer.length) {
                     chars = i;
                     break;
                  }

                  buffer[bytes++] = (byte)code;
               } else {
                  if ((code & -2048) == 0) {
                     if (bytes + 2 > buffer.length) {
                        chars = i;
                        break;
                     }

                     buffer[bytes++] = (byte)(192 | code >> 6);
                     buffer[bytes++] = (byte)(128 | code & 63);
                  } else if ((code & -65536) == 0) {
                     if (bytes + 3 > buffer.length) {
                        chars = i;
                        break;
                     }

                     buffer[bytes++] = (byte)(224 | code >> 12);
                     buffer[bytes++] = (byte)(128 | code >> 6 & 63);
                     buffer[bytes++] = (byte)(128 | code & 63);
                  } else if ((code & -14680064) == 0) {
                     if (bytes + 4 > buffer.length) {
                        chars = i;
                        break;
                     }

                     buffer[bytes++] = (byte)(240 | code >> 18);
                     buffer[bytes++] = (byte)(128 | code >> 12 & 63);
                     buffer[bytes++] = (byte)(128 | code >> 6 & 63);
                     buffer[bytes++] = (byte)(128 | code & 63);
                  } else if ((code & -201326592) == 0) {
                     if (bytes + 5 > buffer.length) {
                        chars = i;
                        break;
                     }

                     buffer[bytes++] = (byte)(248 | code >> 24);
                     buffer[bytes++] = (byte)(128 | code >> 18 & 63);
                     buffer[bytes++] = (byte)(128 | code >> 12 & 63);
                     buffer[bytes++] = (byte)(128 | code >> 6 & 63);
                     buffer[bytes++] = (byte)(128 | code & 63);
                  } else if ((code & -2147483648) == 0) {
                     if (bytes + 6 > buffer.length) {
                        chars = i;
                        break;
                     }

                     buffer[bytes++] = (byte)(252 | code >> 30);
                     buffer[bytes++] = (byte)(128 | code >> 24 & 63);
                     buffer[bytes++] = (byte)(128 | code >> 18 & 63);
                     buffer[bytes++] = (byte)(128 | code >> 12 & 63);
                     buffer[bytes++] = (byte)(128 | code >> 6 & 63);
                     buffer[bytes++] = (byte)(128 | code & 63);
                  } else {
                     buffer[bytes++] = 63;
                  }

                  this._surrogate = 0;
                  if (bytes == buffer.length) {
                     chars = i + 1;
                     break;
                  }
               }
            }

            this._bytes.setCount(bytes);
            this._bytes.writeTo(this._out);
            length -= chars;
            offset += chars;
         }

         return this;
      }
   }
}
