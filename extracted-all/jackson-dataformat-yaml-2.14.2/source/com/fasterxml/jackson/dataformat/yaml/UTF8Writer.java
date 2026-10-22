package com.fasterxml.jackson.dataformat.yaml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.ref.SoftReference;

public final class UTF8Writer extends Writer {
   static final int SURR1_FIRST = 55296;
   static final int SURR1_LAST = 56319;
   static final int SURR2_FIRST = 56320;
   static final int SURR2_LAST = 57343;
   private static final int DEFAULT_BUFFER_SIZE = 8000;
   protected static final ThreadLocal<SoftReference<byte[][]>> _bufferRecycler = new ThreadLocal<>();
   protected final byte[][] _bufferHolder;
   private OutputStream _out;
   private byte[] _outBuffer;
   private final int _outBufferEnd;
   private int _outPtr;
   int _surrogate = 0;

   public UTF8Writer(OutputStream out) {
      this._out = out;
      this._bufferHolder = _findBufferHolder();
      byte[] buffer = this._bufferHolder[0];
      if (buffer == null) {
         buffer = new byte[8000];
      } else {
         this._bufferHolder[0] = null;
      }

      this._outBuffer = buffer;
      this._outBufferEnd = this._outBuffer.length - 4;
      this._outPtr = 0;
   }

   private static byte[][] _findBufferHolder() {
      byte[][] bufs = (byte[][])null;
      SoftReference<byte[][]> ref = _bufferRecycler.get();
      if (ref != null) {
         bufs = ref.get();
      }

      if (bufs == null) {
         bufs = new byte[1][];
         _bufferRecycler.set(new SoftReference<>(bufs));
      }

      return bufs;
   }

   @Override
   public Writer append(char c) throws IOException {
      this.write(c);
      return this;
   }

   @Override
   public void close() throws IOException {
      if (this._out != null) {
         if (this._outPtr > 0) {
            this._out.write(this._outBuffer, 0, this._outPtr);
            this._outPtr = 0;
         }

         OutputStream out = this._out;
         this._out = null;
         if (this._bufferHolder != null) {
            byte[] buf = this._outBuffer;
            if (buf != null) {
               this._outBuffer = null;
               this._bufferHolder[0] = buf;
            }
         }

         out.close();
         int code = this._surrogate;
         this._surrogate = 0;
         if (code > 0) {
            this.throwIllegal(code);
         }
      }
   }

   @Override
   public void flush() throws IOException {
      if (this._out != null) {
         if (this._outPtr > 0) {
            this._out.write(this._outBuffer, 0, this._outPtr);
            this._outPtr = 0;
         }

         this._out.flush();
      }
   }

   @Override
   public void write(char[] cbuf) throws IOException {
      this.write(cbuf, 0, cbuf.length);
   }

   @Override
   public void write(char[] cbuf, int off, int len) throws IOException {
      if (len < 2) {
         if (len == 1) {
            this.write(cbuf[off]);
         }
      } else {
         if (this._surrogate > 0) {
            char second = cbuf[off++];
            len--;
            this.write(this.convertSurrogate(second));
         }

         int outPtr = this._outPtr;
         byte[] outBuf = this._outBuffer;
         int outBufLast = this._outBufferEnd;
         len += off;

         while (off < len) {
            if (outPtr >= outBufLast) {
               this._out.write(outBuf, 0, outPtr);
               outPtr = 0;
            }

            int c;
            c = cbuf[off++];
            label67:
            if (c < 128) {
               outBuf[outPtr++] = (byte)c;
               int maxInCount = len - off;
               int maxOutCount = outBufLast - outPtr;
               if (maxInCount > maxOutCount) {
                  maxInCount = maxOutCount;
               }

               for (int var19 = maxInCount + off; off < var19; outBuf[outPtr++] = (byte)c) {
                  c = cbuf[off++];
                  if (c >= 128) {
                     break label67;
                  }
               }
               continue;
            }

            if (c < 2048) {
               outBuf[outPtr++] = (byte)(192 | c >> 6);
               outBuf[outPtr++] = (byte)(128 | c & 63);
            } else if (c >= 55296 && c <= 57343) {
               if (c > 56319) {
                  this._outPtr = outPtr;
                  this.throwIllegal(c);
               }

               this._surrogate = c;
               if (off >= len) {
                  break;
               }

               c = this.convertSurrogate(cbuf[off++]);
               if (c > 1114111) {
                  this._outPtr = outPtr;
                  this.throwIllegal(c);
               }

               outBuf[outPtr++] = (byte)(240 | c >> 18);
               outBuf[outPtr++] = (byte)(128 | c >> 12 & 63);
               outBuf[outPtr++] = (byte)(128 | c >> 6 & 63);
               outBuf[outPtr++] = (byte)(128 | c & 63);
            } else {
               outBuf[outPtr++] = (byte)(224 | c >> 12);
               outBuf[outPtr++] = (byte)(128 | c >> 6 & 63);
               outBuf[outPtr++] = (byte)(128 | c & 63);
            }
         }

         this._outPtr = outPtr;
      }
   }

   @Override
   public void write(int c) throws IOException {
      if (this._surrogate > 0) {
         c = this.convertSurrogate(c);
      } else if (c >= 55296 && c <= 57343) {
         if (c > 56319) {
            this.throwIllegal(c);
         }

         this._surrogate = c;
         return;
      }

      if (this._outPtr >= this._outBufferEnd) {
         this._out.write(this._outBuffer, 0, this._outPtr);
         this._outPtr = 0;
      }

      if (c < 128) {
         this._outBuffer[this._outPtr++] = (byte)c;
      } else {
         int ptr = this._outPtr;
         if (c < 2048) {
            this._outBuffer[ptr++] = (byte)(192 | c >> 6);
            this._outBuffer[ptr++] = (byte)(128 | c & 63);
         } else if (c <= 65535) {
            this._outBuffer[ptr++] = (byte)(224 | c >> 12);
            this._outBuffer[ptr++] = (byte)(128 | c >> 6 & 63);
            this._outBuffer[ptr++] = (byte)(128 | c & 63);
         } else {
            if (c > 1114111) {
               this.throwIllegal(c);
            }

            this._outBuffer[ptr++] = (byte)(240 | c >> 18);
            this._outBuffer[ptr++] = (byte)(128 | c >> 12 & 63);
            this._outBuffer[ptr++] = (byte)(128 | c >> 6 & 63);
            this._outBuffer[ptr++] = (byte)(128 | c & 63);
         }

         this._outPtr = ptr;
      }
   }

   @Override
   public void write(String str) throws IOException {
      this.write(str, 0, str.length());
   }

   @Override
   public void write(String str, int off, int len) throws IOException {
      if (len < 2) {
         if (len == 1) {
            this.write(str.charAt(off));
         }
      } else {
         if (this._surrogate > 0) {
            char second = str.charAt(off++);
            len--;
            this.write(this.convertSurrogate(second));
         }

         int outPtr = this._outPtr;
         byte[] outBuf = this._outBuffer;
         int outBufLast = this._outBufferEnd;
         len += off;

         while (off < len) {
            if (outPtr >= outBufLast) {
               this._out.write(outBuf, 0, outPtr);
               outPtr = 0;
            }

            int c;
            c = str.charAt(off++);
            label67:
            if (c < 128) {
               outBuf[outPtr++] = (byte)c;
               int maxInCount = len - off;
               int maxOutCount = outBufLast - outPtr;
               if (maxInCount > maxOutCount) {
                  maxInCount = maxOutCount;
               }

               for (int var19 = maxInCount + off; off < var19; outBuf[outPtr++] = (byte)c) {
                  c = str.charAt(off++);
                  if (c >= 128) {
                     break label67;
                  }
               }
               continue;
            }

            if (c < 2048) {
               outBuf[outPtr++] = (byte)(192 | c >> 6);
               outBuf[outPtr++] = (byte)(128 | c & 63);
            } else if (c >= 55296 && c <= 57343) {
               if (c > 56319) {
                  this._outPtr = outPtr;
                  this.throwIllegal(c);
               }

               this._surrogate = c;
               if (off >= len) {
                  break;
               }

               c = this.convertSurrogate(str.charAt(off++));
               if (c > 1114111) {
                  this._outPtr = outPtr;
                  this.throwIllegal(c);
               }

               outBuf[outPtr++] = (byte)(240 | c >> 18);
               outBuf[outPtr++] = (byte)(128 | c >> 12 & 63);
               outBuf[outPtr++] = (byte)(128 | c >> 6 & 63);
               outBuf[outPtr++] = (byte)(128 | c & 63);
            } else {
               outBuf[outPtr++] = (byte)(224 | c >> 12);
               outBuf[outPtr++] = (byte)(128 | c >> 6 & 63);
               outBuf[outPtr++] = (byte)(128 | c & 63);
            }
         }

         this._outPtr = outPtr;
      }
   }

   private int convertSurrogate(int secondPart) throws IOException {
      int firstPart = this._surrogate;
      this._surrogate = 0;
      if (secondPart >= 56320 && secondPart <= 57343) {
         return 65536 + (firstPart - 55296 << 10) + (secondPart - 56320);
      } else {
         throw new IOException(
            "Broken surrogate pair: first char 0x" + Integer.toHexString(firstPart) + ", second 0x" + Integer.toHexString(secondPart) + "; illegal combination"
         );
      }
   }

   private void throwIllegal(int code) throws IOException {
      if (code > 1114111) {
         throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 4627");
      } else if (code >= 55296) {
         if (code <= 56319) {
            throw new IOException("Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")");
         } else {
            throw new IOException("Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")");
         }
      } else {
         throw new IOException("Illegal character point (0x" + Integer.toHexString(code) + ") to output");
      }
   }
}
