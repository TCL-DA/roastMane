package com.fasterxml.jackson.dataformat.yaml;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.ref.SoftReference;

public final class UTF8Reader extends Reader {
   private static final int DEFAULT_BUFFER_SIZE = 8000;
   protected static final ThreadLocal<SoftReference<byte[][]>> _bufferRecycler = new ThreadLocal<>();
   protected final byte[][] _bufferHolder;
   private InputStream _inputSource;
   private final boolean _autoClose;
   protected byte[] _inputBuffer;
   protected int _inputPtr;
   protected int _inputEnd;
   protected int _surrogate = -1;
   int _charCount = 0;
   int _byteCount = 0;
   private char[] _tmpBuffer = null;

   public UTF8Reader(InputStream in, boolean autoClose) {
      super(in == null ? new Object() : in);
      this._inputSource = in;
      this._bufferHolder = _findBufferHolder();
      byte[] buffer = this._bufferHolder[0];
      if (buffer == null) {
         buffer = new byte[8000];
      } else {
         this._bufferHolder[0] = null;
      }

      this._inputBuffer = buffer;
      this._inputPtr = 0;
      this._inputEnd = 0;
      this._autoClose = autoClose;
   }

   public UTF8Reader(byte[] buf, int ptr, int len, boolean autoClose) {
      super(new Object());
      this._inputSource = null;
      this._inputBuffer = buf;
      this._inputPtr = ptr;
      this._inputEnd = ptr + len;
      this._autoClose = autoClose;
      this._bufferHolder = (byte[][])null;
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

   protected final boolean canModifyBuffer() {
      return this._bufferHolder != null;
   }

   @Override
   public void close() throws IOException {
      InputStream in = this._inputSource;
      if (in != null) {
         this._inputSource = null;
         if (this._autoClose) {
            in.close();
         }
      }

      this.freeBuffers();
   }

   @Override
   public int read() throws IOException {
      if (this._tmpBuffer == null) {
         this._tmpBuffer = new char[1];
      }

      return this.read(this._tmpBuffer, 0, 1) < 1 ? -1 : this._tmpBuffer[0];
   }

   @Override
   public int read(char[] cbuf) throws IOException {
      return this.read(cbuf, 0, cbuf.length);
   }

   @Override
   public int read(char[] cbuf, int start, int len) throws IOException {
      if (this._inputBuffer == null) {
         return -1;
      } else {
         len += start;
         int outPtr = start;
         if (this._surrogate >= 0) {
            outPtr = start + 1;
            cbuf[start] = (char)this._surrogate;
            this._surrogate = -1;
            if (this._inputPtr >= this._inputEnd) {
               this._charCount++;
               return 1;
            }
         } else {
            int left = this._inputEnd - this._inputPtr;
            if (left < 4 && (left < 1 || this._inputBuffer[this._inputPtr] < 0) && !this.loadMore(left)) {
               return -1;
            }
         }

         byte[] buf = this._inputBuffer;
         int inPtr = this._inputPtr;
         int inBufLen = this._inputEnd;

         while (outPtr < len) {
            int c;
            c = buf[inPtr++];
            label80:
            if (c >= 0) {
               cbuf[outPtr++] = (char)c;
               int outMax = len - outPtr;
               int inMax = inBufLen - inPtr;

               for (int inEnd = inPtr + (inMax < outMax ? inMax : outMax); inPtr < inEnd; cbuf[outPtr++] = (char)c) {
                  c = buf[inPtr++];
                  if (c < 0) {
                     break label80;
                  }
               }
               break;
            }

            int needed;
            if ((c & 224) == 192) {
               c &= 31;
               needed = 1;
            } else if ((c & 240) == 224) {
               c &= 15;
               needed = 2;
            } else if ((c & 248) == 240) {
               c &= 15;
               needed = 3;
            } else {
               this.reportInvalidInitial(c & 0xFF, outPtr - start);
               needed = 1;
            }

            if (inBufLen - inPtr < needed) {
               inPtr--;
               break;
            }

            int d = buf[inPtr++];
            if ((d & 192) != 128) {
               this.reportInvalidOther(d & 0xFF, outPtr - start);
            }

            c = c << 6 | d & 63;
            if (needed > 1) {
               int var20 = buf[inPtr++];
               if ((var20 & 192) != 128) {
                  this.reportInvalidOther(var20 & 255, outPtr - start);
               }

               c = c << 6 | var20 & 63;
               if (needed > 2) {
                  var20 = buf[inPtr++];
                  if ((var20 & 192) != 128) {
                     this.reportInvalidOther(var20 & 255, outPtr - start);
                  }

                  c = c << 6 | var20 & 63;
                  c -= 65536;
                  cbuf[outPtr++] = (char)(55296 + (c >> 10));
                  c = 56320 | c & 1023;
                  if (outPtr >= len) {
                     this._surrogate = c;
                     break;
                  }
               }
            }

            cbuf[outPtr++] = (char)c;
            if (inPtr >= inBufLen) {
               break;
            }
         }

         this._inputPtr = inPtr;
         int actualLen = outPtr - start;
         this._charCount += actualLen;
         return actualLen;
      }
   }

   protected final InputStream getStream() {
      return this._inputSource;
   }

   protected final int readBytes() throws IOException {
      this._inputPtr = 0;
      this._inputEnd = 0;
      if (this._inputSource != null) {
         int count = this._inputSource.read(this._inputBuffer, 0, this._inputBuffer.length);
         if (count > 0) {
            this._inputEnd = count;
         }

         return count;
      } else {
         return -1;
      }
   }

   protected final int readBytesAt(int offset) throws IOException {
      if (this._inputSource != null) {
         int count = this._inputSource.read(this._inputBuffer, offset, this._inputBuffer.length - offset);
         if (count > 0) {
            this._inputEnd += count;
         }

         return count;
      } else {
         return -1;
      }
   }

   public final void freeBuffers() {
      if (this._bufferHolder != null) {
         byte[] buf = this._inputBuffer;
         if (buf != null) {
            this._inputBuffer = null;
            this._bufferHolder[0] = buf;
         }
      }
   }

   private void reportInvalidInitial(int mask, int offset) throws IOException {
      int bytePos = this._byteCount + this._inputPtr - 1;
      int charPos = this._charCount + offset + 1;
      throw new CharConversionException("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask) + " (at char #" + charPos + ", byte #" + bytePos + ")");
   }

   private void reportInvalidOther(int mask, int offset) throws IOException {
      int bytePos = this._byteCount + this._inputPtr - 1;
      int charPos = this._charCount + offset;
      throw new CharConversionException("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask) + " (at char #" + charPos + ", byte #" + bytePos + ")");
   }

   private void reportUnexpectedEOF(int gotBytes, int needed) throws IOException {
      int bytePos = this._byteCount + gotBytes;
      int charPos = this._charCount;
      throw new CharConversionException(
         "Unexpected EOF in the middle of a multi-byte char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")"
      );
   }

   private boolean loadMore(int available) throws IOException {
      this._byteCount = this._byteCount + (this._inputEnd - available);
      if (available > 0) {
         if (this._inputPtr > 0) {
            if (!this.canModifyBuffer() && this._inputSource == null) {
               throw new IOException(String.format("End-of-input after first %d byte(s) of a UTF-8 character: needed at least one more", available));
            }

            for (int i = 0; i < available; i++) {
               this._inputBuffer[i] = this._inputBuffer[this._inputPtr + i];
            }

            this._inputPtr = 0;
            this._inputEnd = available;
         }
      } else {
         int count = this.readBytes();
         if (count < 1) {
            this.freeBuffers();
            if (count < 0) {
               return false;
            }

            this.reportStrangeStream();
         }
      }

      int c = this._inputBuffer[this._inputPtr];
      if (c >= 0) {
         return true;
      } else {
         int needed;
         if ((c & 224) == 192) {
            needed = 2;
         } else if ((c & 240) == 224) {
            needed = 3;
         } else {
            if ((c & 248) != 240) {
               return true;
            }

            needed = 4;
         }

         while (this._inputPtr + needed > this._inputEnd) {
            int countx = this.readBytesAt(this._inputEnd);
            if (countx < 1) {
               if (countx < 0) {
                  this.freeBuffers();
                  this.reportUnexpectedEOF(this._inputEnd, needed);
               }

               this.reportStrangeStream();
            }
         }

         return true;
      }
   }

   protected void reportBounds(char[] cbuf, int start, int len) throws IOException {
      throw new ArrayIndexOutOfBoundsException("read(buf," + start + "," + len + "), cbuf[" + cbuf.length + "]");
   }

   protected void reportStrangeStream() throws IOException {
      throw new IOException("Strange I/O stream, returned 0 bytes on read");
   }
}
