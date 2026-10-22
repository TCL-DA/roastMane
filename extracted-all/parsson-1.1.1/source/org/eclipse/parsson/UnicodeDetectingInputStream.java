package org.eclipse.parsson;

import jakarta.json.JsonException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class UnicodeDetectingInputStream extends FilterInputStream {
   private static final Charset UTF_32LE = Charset.forName("UTF-32LE");
   private static final Charset UTF_32BE = Charset.forName("UTF-32BE");
   private static final byte FF = -1;
   private static final byte FE = -2;
   private static final byte EF = -17;
   private static final byte BB = -69;
   private static final byte BF = -65;
   private static final byte NUL = 0;
   private final byte[] buf = new byte[4];
   private int bufLen;
   private int curIndex;
   private final Charset charset = this.detectEncoding();

   UnicodeDetectingInputStream(InputStream is) {
      super(is);
   }

   Charset getCharset() {
      return this.charset;
   }

   private void fillBuf() {
      try {
         int b1 = this.in.read();
         if (b1 != -1) {
            int b2 = this.in.read();
            if (b2 == -1) {
               this.bufLen = 1;
               this.buf[0] = (byte)b1;
            } else {
               int b3 = this.in.read();
               if (b3 == -1) {
                  this.bufLen = 2;
                  this.buf[0] = (byte)b1;
                  this.buf[1] = (byte)b2;
               } else {
                  int b4 = this.in.read();
                  if (b4 == -1) {
                     this.bufLen = 3;
                     this.buf[0] = (byte)b1;
                     this.buf[1] = (byte)b2;
                     this.buf[2] = (byte)b3;
                  } else {
                     this.bufLen = 4;
                     this.buf[0] = (byte)b1;
                     this.buf[1] = (byte)b2;
                     this.buf[2] = (byte)b3;
                     this.buf[3] = (byte)b4;
                  }
               }
            }
         }
      } catch (IOException var6) {
         throw new JsonException(JsonMessages.PARSER_INPUT_ENC_DETECT_IOERR(), var6);
      }
   }

   private Charset detectEncoding() {
      this.fillBuf();
      if (this.bufLen < 2) {
         throw new JsonException(JsonMessages.PARSER_INPUT_ENC_DETECT_FAILED());
      } else {
         if (this.bufLen == 4) {
            if (this.buf[0] == 0 && this.buf[1] == 0 && this.buf[2] == -2 && this.buf[3] == -1) {
               this.curIndex = 4;
               return UTF_32BE;
            }

            if (this.buf[0] == -1 && this.buf[1] == -2 && this.buf[2] == 0 && this.buf[3] == 0) {
               this.curIndex = 4;
               return UTF_32LE;
            }

            if (this.buf[0] == -2 && this.buf[1] == -1) {
               this.curIndex = 2;
               return StandardCharsets.UTF_16BE;
            }

            if (this.buf[0] == -1 && this.buf[1] == -2) {
               this.curIndex = 2;
               return StandardCharsets.UTF_16LE;
            }

            if (this.buf[0] == -17 && this.buf[1] == -69 && this.buf[2] == -65) {
               this.curIndex = 3;
               return StandardCharsets.UTF_8;
            }

            if (this.buf[0] == 0 && this.buf[1] == 0 && this.buf[2] == 0) {
               return UTF_32BE;
            }

            if (this.buf[0] == 0 && this.buf[2] == 0) {
               return StandardCharsets.UTF_16BE;
            }

            if (this.buf[1] == 0 && this.buf[2] == 0 && this.buf[3] == 0) {
               return UTF_32LE;
            }

            if (this.buf[1] == 0 && this.buf[3] == 0) {
               return StandardCharsets.UTF_16LE;
            }
         }

         return StandardCharsets.UTF_8;
      }
   }

   @Override
   public int read() throws IOException {
      return this.curIndex < this.bufLen ? this.buf[this.curIndex++] : this.in.read();
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      if (this.curIndex < this.bufLen) {
         if (len == 0) {
            return 0;
         } else if (off >= 0 && len >= 0 && len <= b.length - off) {
            int min = Math.min(this.bufLen - this.curIndex, len);
            System.arraycopy(this.buf, this.curIndex, b, off, min);
            this.curIndex += min;
            return min;
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         return this.in.read(b, off, len);
      }
   }
}
