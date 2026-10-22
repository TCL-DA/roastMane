package kotlin.io.encoding

import java.io.IOException
import java.io.InputStream

@ExperimentalEncodingApi
private class DecodeInputStream(input: InputStream, base64: Base64) : InputStream {
   private final val base64: Base64
   private final val byteBuffer: ByteArray
   private final var byteBufferEndIndex: Int
   private final val byteBufferLength: Int
      private final get() {
         return this.byteBufferEndIndex - this.byteBufferStartIndex;
      }

   private final var byteBufferStartIndex: Int
   private final val input: InputStream
   private final var isClosed: Boolean
   private final var isEOF: Boolean
   private final val singleByteBuffer: ByteArray
   private final val symbolBuffer: ByteArray

   init {
      this.input = input;
      this.base64 = base64;
      this.singleByteBuffer = new byte[1];
      this.symbolBuffer = new byte[1024];
      this.byteBuffer = new byte[1024];
   }

   public override fun read(): Int {
      if (this.byteBufferStartIndex < this.byteBufferEndIndex) {
         var var1: Int = this.byteBuffer[this.byteBufferStartIndex] and 255;
         this.byteBufferStartIndex++;
         this.resetByteBufferIfEmpty();
         return var1;
      } else {
         var var10000: Int;
         switch (this.read(this.singleByteBuffer, 0, 1)) {
            case -1:
               var10000 = -1;
               break;
            case 0:
            default:
               throw new IllegalStateException("Unreachable".toString());
            case 1:
               var10000 = this.singleByteBuffer[0] and 255;
         }

         return var10000;
      }
   }

   public override fun read(destination: ByteArray, offset: Int, length: Int): Int {
      if (offset < 0 || length < 0 || offset + length > destination.length) {
         throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + ", buffer size: " + destination.length);
      } else if (this.isClosed) {
         throw new IOException("The input stream is closed.");
      } else if (this.isEOF) {
         return -1;
      } else if (length == 0) {
         return 0;
      } else if (this.getByteBufferLength() >= length) {
         this.copyByteBufferInto(destination, offset, length);
         return length;
      } else {
         var symbolsNeeded: Int = (length - this.getByteBufferLength() + 3 - 1) / 3 * 4;
         var dstOffset: Int = offset;

         while (!this.isEOF && symbolsNeeded > 0) {
            var symbolBufferLength: Int = 0;
            var symbolsToRead: Int = Math.min(this.symbolBuffer.length, symbolsNeeded);

            while (!this.isEOF && symbolBufferLength < symbolsToRead) {
               var symbol: Int = this.readNextSymbol();
               switch (symbol) {
                  case -1:
                     this.isEOF = true;
                     break;
                  case 61:
                     symbolBufferLength = this.handlePaddingSymbol(symbolBufferLength);
                     this.isEOF = true;
                     break;
                  default:
                     this.symbolBuffer[symbolBufferLength] = (byte)symbol;
                     symbolBufferLength++;
               }
            }

            if (!this.isEOF && symbolBufferLength != symbolsToRead) {
               throw new IllegalStateException("Check failed.".toString());
            }

            symbolsNeeded -= symbolBufferLength;
            dstOffset += this.decodeSymbolBufferInto(destination, dstOffset, length + offset, symbolBufferLength);
         }

         return if (dstOffset == offset && this.isEOF) -1 else dstOffset - offset;
      }
   }

   public override fun close() {
      if (!this.isClosed) {
         this.isClosed = true;
         this.input.close();
      }
   }

   private fun decodeSymbolBufferInto(dst: ByteArray, dstOffset: Int, dstEndIndex: Int, symbolBufferLength: Int): Int {
      this.byteBufferEndIndex = this.byteBufferEndIndex
         + this.base64.decodeIntoByteArray(this.symbolBuffer, this.byteBuffer, this.byteBufferEndIndex, 0, symbolBufferLength);
      var bytesToCopy: Int = Math.min(this.getByteBufferLength(), dstEndIndex - dstOffset);
      this.copyByteBufferInto(dst, dstOffset, bytesToCopy);
      this.shiftByteBufferToStartIfNeeded();
      return bytesToCopy;
   }

   private fun copyByteBufferInto(dst: ByteArray, dstOffset: Int, length: Int) {
      ArraysKt.copyInto(this.byteBuffer, dst, dstOffset, this.byteBufferStartIndex, this.byteBufferStartIndex + length);
      this.byteBufferStartIndex += length;
      this.resetByteBufferIfEmpty();
   }

   private fun resetByteBufferIfEmpty() {
      if (this.byteBufferStartIndex == this.byteBufferEndIndex) {
         this.byteBufferStartIndex = 0;
         this.byteBufferEndIndex = 0;
      }
   }

   private fun shiftByteBufferToStartIfNeeded() {
      if (this.symbolBuffer.length / 4 * 3 > this.byteBuffer.length - this.byteBufferEndIndex) {
         ArraysKt.copyInto(this.byteBuffer, this.byteBuffer, 0, this.byteBufferStartIndex, this.byteBufferEndIndex);
         this.byteBufferEndIndex = this.byteBufferEndIndex - this.byteBufferStartIndex;
         this.byteBufferStartIndex = 0;
      }
   }

   private fun handlePaddingSymbol(symbolBufferLength: Int): Int {
      this.symbolBuffer[symbolBufferLength] = 61;
      var var10000: Int;
      if ((symbolBufferLength and 3) == 2) {
         var secondPad: Int = this.readNextSymbol();
         if (secondPad >= 0) {
            this.symbolBuffer[symbolBufferLength + 1] = (byte)secondPad;
         }

         var10000 = symbolBufferLength + 2;
      } else {
         var10000 = symbolBufferLength + 1;
      }

      return var10000;
   }

   private fun readNextSymbol(): Int {
      if (!this.base64.isMimeScheme$kotlin_stdlib()) {
         return this.input.read();
      } else {
         var var2: Int;
         do {
            var2 = this.input.read();
         } while (var2 != -1 && !Base64Kt.isInMimeAlphabet(var2));

         return var2;
      }
   }
}
