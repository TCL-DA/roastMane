package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.io.NumberInput;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public final class TextBuffer {
   static final char[] NO_CHARS = new char[0];
   static final int MIN_SEGMENT_LEN = 500;
   static final int MAX_SEGMENT_LEN = 65536;
   private final BufferRecycler _allocator;
   private char[] _inputBuffer;
   private int _inputStart;
   private int _inputLen;
   private ArrayList<char[]> _segments;
   private boolean _hasSegments;
   private int _segmentSize;
   private char[] _currentSegment;
   private int _currentSize;
   private String _resultString;
   private char[] _resultArray;

   public TextBuffer(BufferRecycler allocator) {
      this._allocator = allocator;
   }

   protected TextBuffer(BufferRecycler allocator, char[] initialSegment) {
      this._allocator = allocator;
      this._currentSegment = initialSegment;
      this._currentSize = initialSegment.length;
      this._inputStart = -1;
   }

   public static TextBuffer fromInitial(char[] initialSegment) {
      return new TextBuffer(null, initialSegment);
   }

   public void releaseBuffers() {
      this._inputStart = -1;
      this._currentSize = 0;
      this._inputLen = 0;
      this._inputBuffer = null;
      this._resultArray = null;
      if (this._hasSegments) {
         this.clearSegments();
      }

      if (this._allocator != null && this._currentSegment != null) {
         char[] buf = this._currentSegment;
         this._currentSegment = null;
         this._allocator.releaseCharBuffer(2, buf);
      }
   }

   public void resetWithEmpty() {
      this._inputStart = -1;
      this._currentSize = 0;
      this._inputLen = 0;
      this._inputBuffer = null;
      this._resultString = null;
      this._resultArray = null;
      if (this._hasSegments) {
         this.clearSegments();
      }
   }

   public void resetWith(char ch) {
      this._inputStart = -1;
      this._inputLen = 0;
      this._resultString = null;
      this._resultArray = null;
      if (this._hasSegments) {
         this.clearSegments();
      } else if (this._currentSegment == null) {
         this._currentSegment = this.buf(1);
      }

      this._currentSegment[0] = ch;
      this._currentSize = this._segmentSize = 1;
   }

   public void resetWithShared(char[] buf, int offset, int len) {
      this._resultString = null;
      this._resultArray = null;
      this._inputBuffer = buf;
      this._inputStart = offset;
      this._inputLen = len;
      if (this._hasSegments) {
         this.clearSegments();
      }
   }

   public void resetWithCopy(char[] buf, int offset, int len) {
      this._inputBuffer = null;
      this._inputStart = -1;
      this._inputLen = 0;
      this._resultString = null;
      this._resultArray = null;
      if (this._hasSegments) {
         this.clearSegments();
      } else if (this._currentSegment == null) {
         this._currentSegment = this.buf(len);
      }

      this._currentSize = this._segmentSize = 0;
      this.append(buf, offset, len);
   }

   public void resetWithCopy(String text, int start, int len) {
      this._inputBuffer = null;
      this._inputStart = -1;
      this._inputLen = 0;
      this._resultString = null;
      this._resultArray = null;
      if (this._hasSegments) {
         this.clearSegments();
      } else if (this._currentSegment == null) {
         this._currentSegment = this.buf(len);
      }

      this._currentSize = this._segmentSize = 0;
      this.append(text, start, len);
   }

   public void resetWithString(String value) {
      this._inputBuffer = null;
      this._inputStart = -1;
      this._inputLen = 0;
      this._resultString = value;
      this._resultArray = null;
      if (this._hasSegments) {
         this.clearSegments();
      }

      this._currentSize = 0;
   }

   public char[] getBufferWithoutReset() {
      return this._currentSegment;
   }

   private char[] buf(int needed) {
      return this._allocator != null ? this._allocator.allocCharBuffer(2, needed) : new char[Math.max(needed, 500)];
   }

   private void clearSegments() {
      this._hasSegments = false;
      this._segments.clear();
      this._currentSize = this._segmentSize = 0;
   }

   public int size() {
      if (this._inputStart >= 0) {
         return this._inputLen;
      } else if (this._resultArray != null) {
         return this._resultArray.length;
      } else {
         return this._resultString != null ? this._resultString.length() : this._segmentSize + this._currentSize;
      }
   }

   public int getTextOffset() {
      return this._inputStart >= 0 ? this._inputStart : 0;
   }

   public boolean hasTextAsCharacters() {
      return this._inputStart >= 0 || this._resultArray != null ? true : this._resultString == null;
   }

   public char[] getTextBuffer() {
      if (this._inputStart >= 0) {
         return this._inputBuffer;
      } else if (this._resultArray != null) {
         return this._resultArray;
      } else if (this._resultString != null) {
         return this._resultArray = this._resultString.toCharArray();
      } else if (!this._hasSegments) {
         return this._currentSegment == null ? NO_CHARS : this._currentSegment;
      } else {
         return this.contentsAsArray();
      }
   }

   public String contentsAsString() {
      if (this._resultString == null) {
         if (this._resultArray != null) {
            this._resultString = new String(this._resultArray);
         } else if (this._inputStart >= 0) {
            if (this._inputLen < 1) {
               return this._resultString = "";
            }

            this._resultString = new String(this._inputBuffer, this._inputStart, this._inputLen);
         } else {
            int segLen = this._segmentSize;
            int currLen = this._currentSize;
            if (segLen == 0) {
               this._resultString = currLen == 0 ? "" : new String(this._currentSegment, 0, currLen);
            } else {
               StringBuilder sb = new StringBuilder(segLen + currLen);
               if (this._segments != null) {
                  int i = 0;

                  for (int len = this._segments.size(); i < len; i++) {
                     char[] curr = this._segments.get(i);
                     sb.append(curr, 0, curr.length);
                  }
               }

               sb.append(this._currentSegment, 0, this._currentSize);
               this._resultString = sb.toString();
            }
         }
      }

      return this._resultString;
   }

   public char[] contentsAsArray() {
      char[] result = this._resultArray;
      if (result == null) {
         this._resultArray = result = this.resultArray();
      }

      return result;
   }

   public BigDecimal contentsAsDecimal() throws NumberFormatException {
      if (this._resultArray != null) {
         return NumberInput.parseBigDecimal(this._resultArray);
      } else if (this._inputStart >= 0 && this._inputBuffer != null) {
         return NumberInput.parseBigDecimal(this._inputBuffer, this._inputStart, this._inputLen);
      } else {
         return this._segmentSize == 0 && this._currentSegment != null
            ? NumberInput.parseBigDecimal(this._currentSegment, 0, this._currentSize)
            : NumberInput.parseBigDecimal(this.contentsAsArray());
      }
   }

   @Deprecated
   public double contentsAsDouble() throws NumberFormatException {
      return this.contentsAsDouble(false);
   }

   public double contentsAsDouble(boolean useFastParser) throws NumberFormatException {
      return NumberInput.parseDouble(this.contentsAsString(), useFastParser);
   }

   @Deprecated
   public float contentsAsFloat() throws NumberFormatException {
      return this.contentsAsFloat(false);
   }

   public float contentsAsFloat(boolean useFastParser) throws NumberFormatException {
      return NumberInput.parseFloat(this.contentsAsString(), useFastParser);
   }

   public int contentsAsInt(boolean neg) {
      if (this._inputStart >= 0 && this._inputBuffer != null) {
         return neg
            ? -NumberInput.parseInt(this._inputBuffer, this._inputStart + 1, this._inputLen - 1)
            : NumberInput.parseInt(this._inputBuffer, this._inputStart, this._inputLen);
      } else {
         return neg ? -NumberInput.parseInt(this._currentSegment, 1, this._currentSize - 1) : NumberInput.parseInt(this._currentSegment, 0, this._currentSize);
      }
   }

   public long contentsAsLong(boolean neg) {
      if (this._inputStart >= 0 && this._inputBuffer != null) {
         return neg
            ? -NumberInput.parseLong(this._inputBuffer, this._inputStart + 1, this._inputLen - 1)
            : NumberInput.parseLong(this._inputBuffer, this._inputStart, this._inputLen);
      } else {
         return neg
            ? -NumberInput.parseLong(this._currentSegment, 1, this._currentSize - 1)
            : NumberInput.parseLong(this._currentSegment, 0, this._currentSize);
      }
   }

   public int contentsToWriter(Writer w) throws IOException {
      if (this._resultArray != null) {
         w.write(this._resultArray);
         return this._resultArray.length;
      } else if (this._resultString != null) {
         w.write(this._resultString);
         return this._resultString.length();
      } else if (this._inputStart >= 0) {
         int len = this._inputLen;
         if (len > 0) {
            w.write(this._inputBuffer, this._inputStart, len);
         }

         return len;
      } else {
         int total = 0;
         if (this._segments != null) {
            int i = 0;

            for (int end = this._segments.size(); i < end; i++) {
               char[] curr = this._segments.get(i);
               int currLen = curr.length;
               w.write(curr, 0, currLen);
               total += currLen;
            }
         }

         int len = this._currentSize;
         if (len > 0) {
            w.write(this._currentSegment, 0, len);
            total += len;
         }

         return total;
      }
   }

   public void ensureNotShared() {
      if (this._inputStart >= 0) {
         this.unshare(16);
      }
   }

   public void append(char c) {
      if (this._inputStart >= 0) {
         this.unshare(16);
      }

      this._resultString = null;
      this._resultArray = null;
      char[] curr = this._currentSegment;
      if (this._currentSize >= curr.length) {
         this.expand(1);
         curr = this._currentSegment;
      }

      curr[this._currentSize++] = c;
   }

   public void append(char[] c, int start, int len) {
      if (this._inputStart >= 0) {
         this.unshare(len);
      }

      this._resultString = null;
      this._resultArray = null;
      char[] curr = this._currentSegment;
      int max = curr.length - this._currentSize;
      if (max >= len) {
         System.arraycopy(c, start, curr, this._currentSize, len);
         this._currentSize += len;
      } else {
         if (max > 0) {
            System.arraycopy(c, start, curr, this._currentSize, max);
            start += max;
            len -= max;
         }

         do {
            this.expand(len);
            int amount = Math.min(this._currentSegment.length, len);
            System.arraycopy(c, start, this._currentSegment, 0, amount);
            this._currentSize += amount;
            start += amount;
            len -= amount;
         } while (len > 0);
      }
   }

   public void append(String str, int offset, int len) {
      if (this._inputStart >= 0) {
         this.unshare(len);
      }

      this._resultString = null;
      this._resultArray = null;
      char[] curr = this._currentSegment;
      int max = curr.length - this._currentSize;
      if (max >= len) {
         str.getChars(offset, offset + len, curr, this._currentSize);
         this._currentSize += len;
      } else {
         if (max > 0) {
            str.getChars(offset, offset + max, curr, this._currentSize);
            len -= max;
            offset += max;
         }

         do {
            this.expand(len);
            int amount = Math.min(this._currentSegment.length, len);
            str.getChars(offset, offset + amount, this._currentSegment, 0);
            this._currentSize += amount;
            offset += amount;
            len -= amount;
         } while (len > 0);
      }
   }

   public char[] getCurrentSegment() {
      if (this._inputStart >= 0) {
         this.unshare(1);
      } else {
         char[] curr = this._currentSegment;
         if (curr == null) {
            this._currentSegment = this.buf(0);
         } else if (this._currentSize >= curr.length) {
            this.expand(1);
         }
      }

      return this._currentSegment;
   }

   public char[] emptyAndGetCurrentSegment() {
      this._inputStart = -1;
      this._currentSize = 0;
      this._inputLen = 0;
      this._inputBuffer = null;
      this._resultString = null;
      this._resultArray = null;
      if (this._hasSegments) {
         this.clearSegments();
      }

      char[] curr = this._currentSegment;
      if (curr == null) {
         this._currentSegment = curr = this.buf(0);
      }

      return curr;
   }

   public int getCurrentSegmentSize() {
      return this._currentSize;
   }

   public void setCurrentLength(int len) {
      this._currentSize = len;
   }

   public String setCurrentAndReturn(int len) {
      this._currentSize = len;
      if (this._segmentSize > 0) {
         return this.contentsAsString();
      } else {
         int currLen = this._currentSize;
         String str = currLen == 0 ? "" : new String(this._currentSegment, 0, currLen);
         this._resultString = str;
         return str;
      }
   }

   public char[] finishCurrentSegment() {
      if (this._segments == null) {
         this._segments = new ArrayList<>();
      }

      this._hasSegments = true;
      this._segments.add(this._currentSegment);
      int oldLen = this._currentSegment.length;
      this._segmentSize += oldLen;
      this._currentSize = 0;
      int newLen = oldLen + (oldLen >> 1);
      if (newLen < 500) {
         newLen = 500;
      } else if (newLen > 65536) {
         newLen = 65536;
      }

      char[] curr = this.carr(newLen);
      this._currentSegment = curr;
      return curr;
   }

   public char[] expandCurrentSegment() {
      char[] curr = this._currentSegment;
      int len = curr.length;
      int newLen = len + (len >> 1);
      if (newLen > 65536) {
         newLen = len + (len >> 2);
      }

      return this._currentSegment = Arrays.copyOf(curr, newLen);
   }

   public char[] expandCurrentSegment(int minSize) {
      char[] curr = this._currentSegment;
      if (curr.length >= minSize) {
         return curr;
      } else {
         char[] var3;
         this._currentSegment = var3 = Arrays.copyOf(curr, minSize);
         return var3;
      }
   }

   @Override
   public String toString() {
      return this.contentsAsString();
   }

   private void unshare(int needExtra) {
      int sharedLen = this._inputLen;
      this._inputLen = 0;
      char[] inputBuf = this._inputBuffer;
      this._inputBuffer = null;
      int start = this._inputStart;
      this._inputStart = -1;
      int needed = sharedLen + needExtra;
      if (this._currentSegment == null || needed > this._currentSegment.length) {
         this._currentSegment = this.buf(needed);
      }

      if (sharedLen > 0) {
         System.arraycopy(inputBuf, start, this._currentSegment, 0, sharedLen);
      }

      this._segmentSize = 0;
      this._currentSize = sharedLen;
   }

   private void expand(int minNewSegmentSize) {
      if (this._segments == null) {
         this._segments = new ArrayList<>();
      }

      char[] curr = this._currentSegment;
      this._hasSegments = true;
      this._segments.add(curr);
      this._segmentSize += curr.length;
      this._currentSize = 0;
      int oldLen = curr.length;
      int newLen = oldLen + (oldLen >> 1);
      if (newLen < 500) {
         newLen = 500;
      } else if (newLen > 65536) {
         newLen = 65536;
      }

      this._currentSegment = this.carr(newLen);
   }

   private char[] resultArray() {
      if (this._resultString != null) {
         return this._resultString.toCharArray();
      } else if (this._inputStart >= 0) {
         int len = this._inputLen;
         if (len < 1) {
            return NO_CHARS;
         } else {
            int start = this._inputStart;
            return start == 0 ? Arrays.copyOf(this._inputBuffer, len) : Arrays.copyOfRange(this._inputBuffer, start, start + len);
         }
      } else {
         int size = this.size();
         if (size < 1) {
            return NO_CHARS;
         } else {
            int offset = 0;
            char[] result = this.carr(size);
            if (this._segments != null) {
               int i = 0;

               for (int len = this._segments.size(); i < len; i++) {
                  char[] curr = this._segments.get(i);
                  int currLen = curr.length;
                  System.arraycopy(curr, 0, result, offset, currLen);
                  offset += currLen;
               }
            }

            System.arraycopy(this._currentSegment, 0, result, offset, this._currentSize);
            return result;
         }
      }
   }

   private char[] carr(int len) {
      return new char[len];
   }
}
