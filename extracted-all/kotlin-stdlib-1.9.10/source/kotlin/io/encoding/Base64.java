package kotlin.io.encoding

import java.nio.charset.Charset

@SinceKotlin(version = "1.8")
@ExperimentalEncodingApi
open class Base64 private constructor(isUrlSafe: Boolean, isMimeScheme: Boolean) {
   @JvmStatic
   public Base64.Default Default = new Base64.Default(null);
   @JvmStatic
   private int bitsPerByte = 8;
   @JvmStatic
   private int bitsPerSymbol = 6;
   @JvmStatic
   public int bytesPerGroup = 3;
   @JvmStatic
   public int symbolsPerGroup = 4;
   @JvmStatic
   public byte padSymbol = 61;
   @JvmStatic
   public int mimeLineLength = 76;
   @JvmStatic
   private int mimeGroupsPerLine = 19;
   @JvmStatic
   private byte[] mimeLineSeparatorSymbols = new byte[]{13, 10};
   @JvmStatic
   private Base64 UrlSafe = new Base64(true, false);
   @JvmStatic
   private Base64 Mime = new Base64(false, true);

   internal final val isMimeScheme: Boolean
   internal final val isUrlSafe: Boolean

   init {
      this.isUrlSafe = isUrlSafe;
      this.isMimeScheme = isMimeScheme;
      if (this.isUrlSafe && this.isMimeScheme) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      }
   }

   public fun encodeToByteArray(source: ByteArray, startIndex: Int, endIndex: Int): ByteArray {
      return this.encodeToByteArrayImpl$kotlin_stdlib(source, startIndex, endIndex);
   }

   public fun encodeIntoByteArray(source: ByteArray, destination: ByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int): Int {
      return this.encodeIntoByteArrayImpl$kotlin_stdlib(source, destination, destinationOffset, startIndex, endIndex);
   }

   public fun encode(source: ByteArray, startIndex: Int, endIndex: Int): String {
      return new java.lang.String(this.encodeToByteArrayImpl$kotlin_stdlib(source, startIndex, endIndex), Charsets.ISO_8859_1);
   }

   public fun <A : Appendable> encodeToAppendable(source: ByteArray, destination: A, startIndex: Int, endIndex: Int): A {
      destination.append(new java.lang.String(this.encodeToByteArrayImpl$kotlin_stdlib(source, startIndex, endIndex), Charsets.ISO_8859_1));
      return (A)destination;
   }

   public fun decode(source: ByteArray, startIndex: Int, endIndex: Int): ByteArray {
      this.checkSourceBounds$kotlin_stdlib(source.length, startIndex, endIndex);
      var destination: ByteArray = new byte[this.decodeSize(source, startIndex, endIndex)];
      if (this.decodeImpl(source, destination, 0, startIndex, endIndex) != destination.length) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         return destination;
      }
   }

   public fun decodeIntoByteArray(source: ByteArray, destination: ByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int): Int {
      this.checkSourceBounds$kotlin_stdlib(source.length, startIndex, endIndex);
      this.checkDestinationBounds(destination.length, destinationOffset, this.decodeSize(source, startIndex, endIndex));
      return this.decodeImpl(source, destination, destinationOffset, startIndex, endIndex);
   }

   public fun decode(source: CharSequence, startIndex: Int, endIndex: Int): ByteArray {
      var var8: ByteArray;
      if (source is java.lang.String) {
         this.checkSourceBounds$kotlin_stdlib(source.length(), startIndex, endIndex);
         var var10000: java.lang.String = (source as java.lang.String).substring(startIndex, endIndex);
         var var7: Charset = Charsets.ISO_8859_1;
         var8 = var10000.getBytes(var7);
      } else {
         var8 = this.charsToBytesImpl$kotlin_stdlib(source, startIndex, endIndex);
      }

      return decode$default(this, var8, 0, 0, 6, null);
   }

   public fun decodeIntoByteArray(source: CharSequence, destination: ByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int): Int {
      var var10: ByteArray;
      if (source is java.lang.String) {
         this.checkSourceBounds$kotlin_stdlib(source.length(), startIndex, endIndex);
         var var10000: java.lang.String = (source as java.lang.String).substring(startIndex, endIndex);
         var var9: Charset = Charsets.ISO_8859_1;
         var10 = var10000.getBytes(var9);
      } else {
         var10 = this.charsToBytesImpl$kotlin_stdlib(source, startIndex, endIndex);
      }

      return decodeIntoByteArray$default(this, var10, destination, destinationOffset, 0, 0, 24, null);
   }

   internal fun encodeToByteArrayImpl(source: ByteArray, startIndex: Int, endIndex: Int): ByteArray {
      this.checkSourceBounds$kotlin_stdlib(source.length, startIndex, endIndex);
      var destination: ByteArray = new byte[this.encodeSize(endIndex - startIndex)];
      this.encodeIntoByteArrayImpl$kotlin_stdlib(source, destination, 0, startIndex, endIndex);
      return destination;
   }

   internal fun encodeIntoByteArrayImpl(source: ByteArray, destination: ByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int): Int {
      this.checkSourceBounds$kotlin_stdlib(source.length, startIndex, endIndex);
      this.checkDestinationBounds(destination.length, destinationOffset, this.encodeSize(endIndex - startIndex));
      var encodeMap: ByteArray = if (this.isUrlSafe) Base64Kt.access$getBase64UrlEncodeMap$p() else Base64Kt.access$getBase64EncodeMap$p();
      var sourceIndex: Int = startIndex;
      var destinationIndex: Int = destinationOffset;
      var groupsPerLine: Int = if (this.isMimeScheme) 19 else Integer.MAX_VALUE;

      while (sourceIndex + 2 < endIndex) {
         var groups: Int = Math.min((endIndex - sourceIndex) / 3, groupsPerLine);

         for (int i = 0; i < groups; i++) {
            var bits: Int = (source[sourceIndex++] and 255) shl 16 or (source[sourceIndex++] and 255) shl 8 or source[sourceIndex++] and 255;
            destination[destinationIndex++] = encodeMap[bits ushr 18];
            destination[destinationIndex++] = encodeMap[bits ushr 12 and 63];
            destination[destinationIndex++] = encodeMap[bits ushr 6 and 63];
            destination[destinationIndex++] = encodeMap[bits and 63];
         }

         if (groups == groupsPerLine && sourceIndex != endIndex) {
            destination[destinationIndex++] = mimeLineSeparatorSymbols[0];
            destination[destinationIndex++] = mimeLineSeparatorSymbols[1];
         }
      }

      switch (endIndex - sourceIndex) {
         case 1: {
            var var34: Int = (source[sourceIndex++] and 255) shl 4;
            destination[destinationIndex++] = encodeMap[var34 ushr 6];
            destination[destinationIndex++] = encodeMap[var34 and 63];
            destination[destinationIndex++] = 61;
            destination[destinationIndex++] = 61;
            break;
         }
         case 2: {
            var var35: Int = (source[sourceIndex++] and 255) shl 10 or (source[sourceIndex++] and 255) shl 2;
            destination[destinationIndex++] = encodeMap[var35 ushr 12];
            destination[destinationIndex++] = encodeMap[var35 ushr 6 and 63];
            destination[destinationIndex++] = encodeMap[var35 and 63];
            destination[destinationIndex++] = 61;
         }
         default:
      }

      if (sourceIndex != endIndex) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         return destinationIndex - destinationOffset;
      }
   }

   private fun encodeSize(sourceSize: Int): Int {
      var groups: Int = (sourceSize + 3 - 1) / 3;
      var size: Int = (sourceSize + 3 - 1) / 3 * 4 + (if (this.isMimeScheme) ((sourceSize + 3 - 1) / 3 - 1) / 19 else 0) * 2;
      if (groups * 4 + (if (this.isMimeScheme) ((sourceSize + 3 - 1) / 3 - 1) / 19 else 0) * 2 < 0) {
         throw new IllegalArgumentException("Input is too big");
      } else {
         return size;
      }
   }

   private fun decodeImpl(source: ByteArray, destination: ByteArray, destinationOffset: Int, startIndex: Int, endIndex: Int): Int {
      var decodeMap: IntArray = if (this.isUrlSafe) Base64Kt.access$getBase64UrlDecodeMap$p() else Base64Kt.access$getBase64DecodeMap$p();
      var payload: Int = 0;
      var byteStart: Int = -8;
      var sourceIndex: Int = startIndex;
      var destinationIndex: Int = destinationOffset;

      while (sourceIndex < endIndex) {
         if (byteStart == -8 && sourceIndex + 3 < endIndex) {
            var bits: Int = decodeMap[source[sourceIndex++] and 255] shl 18 or decodeMap[source[sourceIndex++] and 255] shl 12 or decodeMap[source[sourceIndex++] and 255] shl 6 or decodeMap[source[sourceIndex++] and 255];
            if (bits >= 0) {
               destination[destinationIndex++] = (byte)(bits shr 16);
               destination[destinationIndex++] = (byte)(bits shr 8);
               destination[destinationIndex++] = (byte)bits;
               continue;
            }

            sourceIndex -= 4;
         }

         var var22: Int = source[sourceIndex] and 255;
         var var24: Int = decodeMap[source[sourceIndex] and 255];
         if (decodeMap[source[sourceIndex] and 255] < 0) {
            if (var24 == -2) {
               sourceIndex = this.handlePaddingSymbol(source, sourceIndex, endIndex, byteStart);
               break;
            }

            if (!this.isMimeScheme) {
               var var31: StringBuilder = new StringBuilder().append("Invalid symbol '").append((char)var22).append("'(");
               var var10003: java.lang.String = Integer.toString(var22, CharsKt.checkRadix(8));
               throw new IllegalArgumentException(var31.append(var10003).append(") at index ").append(sourceIndex).toString());
            }

            sourceIndex++;
         } else {
            sourceIndex++;
            payload = payload shl 6 or var24;
            byteStart += 6;
            if (byteStart >= 0) {
               destination[destinationIndex++] = (byte)(payload ushr byteStart);
               payload &= (1 shl byteStart) - 1;
               byteStart -= 8;
            }
         }
      }

      if (byteStart == -2) {
         throw new IllegalArgumentException("The last unit of input does not have enough bits");
      } else {
         sourceIndex = this.skipIllegalSymbolsIfMime(source, sourceIndex, endIndex);
         if (sourceIndex < endIndex) {
            var var32: StringBuilder = new StringBuilder().append("Symbol '").append((char)(source[sourceIndex] and 255)).append("'(");
            var var33: java.lang.String = Integer.toString(source[sourceIndex] and 255, CharsKt.checkRadix(8));
            throw new IllegalArgumentException(
               var32.append(var33).append(") at index ").append(sourceIndex - 1).append(" is prohibited after the pad character").toString()
            );
         } else {
            return destinationIndex - destinationOffset;
         }
      }
   }

   private fun decodeSize(source: ByteArray, startIndex: Int, endIndex: Int): Int {
      var symbols: Int = endIndex - startIndex;
      if (endIndex - startIndex == 0) {
         return 0;
      } else if (symbols == 1) {
         throw new IllegalArgumentException("Input should have at list 2 symbols for Base64 decoding, startIndex: " + startIndex + ", endIndex: " + endIndex);
      } else {
         if (this.isMimeScheme) {
            for (int index = startIndex; index < endIndex; index++) {
               var symbolBits: Int = Base64Kt.access$getBase64DecodeMap$p()[source[index] and 255];
               if (symbolBits < 0) {
                  if (symbolBits == -2) {
                     symbols -= endIndex - index;
                     break;
                  }

                  symbols--;
               }
            }
         } else if (source[endIndex - 1] == 61) {
            symbols--;
            if (source[endIndex - 2] == 61) {
               symbols--;
            }
         }

         return (int)((long)symbols * (long)6 / (long)8);
      }
   }

   internal fun charsToBytesImpl(source: CharSequence, startIndex: Int, endIndex: Int): ByteArray {
      this.checkSourceBounds$kotlin_stdlib(source.length(), startIndex, endIndex);
      var byteArray: ByteArray = new byte[endIndex - startIndex];
      var length: Int = 0;

      for (int index = startIndex; index < endIndex; index++) {
         var symbol: Int = source.charAt(index);
         if (symbol <= 255) {
            byteArray[length++] = (byte)symbol;
         } else {
            byteArray[length++] = 63;
         }
      }

      return byteArray;
   }

   internal fun bytesToStringImpl(source: ByteArray): String {
      var stringBuilder: StringBuilder = new StringBuilder(source.length);

      for (byte byte : source) {
         stringBuilder.append((char)var5);
      }

      var var10000: java.lang.String = stringBuilder.toString();
      return var10000;
   }

   private fun handlePaddingSymbol(source: ByteArray, padIndex: Int, endIndex: Int, byteStart: Int): Int {
      var var10000: Int;
      switch (byteStart) {
         case -8:
            throw new IllegalArgumentException("Redundant pad character at index " + padIndex);
         case -7:
         case -5:
         case -3:
         default:
            throw new IllegalStateException("Unreachable".toString());
         case -6:
            var10000 = padIndex + 1;
            break;
         case -4:
            var secondPadIndex: Int = this.skipIllegalSymbolsIfMime(source, padIndex + 1, endIndex);
            if (secondPadIndex == endIndex || source[secondPadIndex] != 61) {
               throw new IllegalArgumentException("Missing one pad character at index " + secondPadIndex);
            }

            var10000 = secondPadIndex + 1;
            break;
         case -2:
            var10000 = padIndex + 1;
      }

      return var10000;
   }

   private fun skipIllegalSymbolsIfMime(source: ByteArray, startIndex: Int, endIndex: Int): Int {
      if (!this.isMimeScheme) {
         return startIndex;
      } else {
         var sourceIndex: Int;
         for (sourceIndex = startIndex; sourceIndex < endIndex; sourceIndex++) {
            if (Base64Kt.access$getBase64DecodeMap$p()[source[sourceIndex] and 255] != -1) {
               return sourceIndex;
            }
         }

         return sourceIndex;
      }
   }

   internal fun checkSourceBounds(sourceSize: Int, startIndex: Int, endIndex: Int) {
      AbstractList.Companion.checkBoundsIndexes$kotlin_stdlib(startIndex, endIndex, sourceSize);
   }

   private fun checkDestinationBounds(destinationSize: Int, destinationOffset: Int, capacityNeeded: Int) {
      if (destinationOffset >= 0 && destinationOffset <= destinationSize) {
         if (destinationOffset + capacityNeeded < 0 || destinationOffset + capacityNeeded > destinationSize) {
            throw new IndexOutOfBoundsException(
               "The destination array does not have enough capacity, destination offset: "
                  + destinationOffset
                  + ", destination size: "
                  + destinationSize
                  + ", capacity needed: "
                  + capacityNeeded
            );
         }
      } else {
         throw new IndexOutOfBoundsException("destination offset: " + destinationOffset + ", destination size: " + destinationSize);
      }
   }

   companion object Default private constructor() : Base64(false, false) {
      public final val Mime: Base64
      public final val UrlSafe: Base64
      private const val bitsPerByte: Int
      private const val bitsPerSymbol: Int
      internal const val bytesPerGroup: Int
      private const val mimeGroupsPerLine: Int
      internal const val mimeLineLength: Int
      internal final val mimeLineSeparatorSymbols: ByteArray
      internal const val padSymbol: Byte
      internal const val symbolsPerGroup: Int

   }
}
