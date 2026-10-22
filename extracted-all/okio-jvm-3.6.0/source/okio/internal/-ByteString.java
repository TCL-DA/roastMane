@file:JvmName(name = "-ByteString")

@file:SourceDebugExtension(["SMAP\nByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ByteString.kt\nokio/internal/-ByteString\n+ 2 Util.kt\nokio/-SegmentedByteString\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Utf8.kt\nokio/Utf8\n*L\n1#1,363:1\n131#1,2:369\n133#1,9:372\n68#2:364\n74#2:365\n74#2:367\n74#2:368\n68#2:396\n74#2:408\n1#3:366\n1#3:371\n212#4,7:381\n122#4:388\n219#4,5:389\n122#4:394\n226#4:395\n228#4:397\n397#4,2:398\n122#4:400\n400#4,6:401\n127#4:407\n406#4:409\n122#4:410\n407#4,13:411\n122#4:424\n422#4:425\n122#4:426\n425#4:427\n230#4,3:428\n440#4,3:431\n122#4:434\n443#4:435\n127#4:436\n446#4,10:437\n127#4:447\n456#4:448\n122#4:449\n457#4,4:450\n127#4:454\n461#4:455\n122#4:456\n462#4,14:457\n122#4:471\n477#4,2:472\n122#4:474\n481#4:475\n122#4:476\n484#4:477\n234#4,3:478\n500#4,3:481\n122#4:484\n503#4:485\n127#4:486\n506#4,2:487\n127#4:489\n510#4,10:490\n127#4:500\n520#4:501\n122#4:502\n521#4,4:503\n127#4:507\n525#4:508\n122#4:509\n526#4,4:510\n127#4:514\n530#4:515\n122#4:516\n531#4,15:517\n122#4:532\n547#4,2:533\n122#4:535\n550#4,2:536\n122#4:538\n554#4:539\n122#4:540\n557#4:541\n241#4:542\n122#4:543\n242#4,5:544\n*S KotlinDebug\n*F\n+ 1 ByteString.kt\nokio/internal/-ByteString\n*L\n329#1:369,2\n329#1:372,9\n67#1:364\n68#1:365\n258#1:367\n259#1:368\n348#1:396\n348#1:408\n329#1:371\n348#1:381,7\n353#1:388\n348#1:389,5\n353#1:394\n348#1:395\n348#1:397\n348#1:398,2\n353#1:400\n348#1:401,6\n348#1:407\n348#1:409\n353#1:410\n348#1:411,13\n353#1:424\n348#1:425\n353#1:426\n348#1:427\n348#1:428,3\n348#1:431,3\n353#1:434\n348#1:435\n348#1:436\n348#1:437,10\n348#1:447\n348#1:448\n353#1:449\n348#1:450,4\n348#1:454\n348#1:455\n353#1:456\n348#1:457,14\n353#1:471\n348#1:472,2\n353#1:474\n348#1:475\n353#1:476\n348#1:477\n348#1:478,3\n348#1:481,3\n353#1:484\n348#1:485\n348#1:486\n348#1:487,2\n348#1:489\n348#1:490,10\n348#1:500\n348#1:501\n353#1:502\n348#1:503,4\n348#1:507\n348#1:508\n353#1:509\n348#1:510,4\n348#1:514\n348#1:515\n353#1:516\n348#1:517,15\n353#1:532\n348#1:533,2\n353#1:535\n348#1:536,2\n353#1:538\n348#1:539\n353#1:540\n348#1:541\n348#1:542\n353#1:543\n348#1:544,5\n*E\n"])

package okio.internal

import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension
import okio.-Base64
import okio.Buffer
import okio.ByteString
import okio._JvmPlatformKt

internal final val HEX_DIGIT_CHARS: CharArray = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}

internal inline fun ByteString.commonUtf8(): String {
   var result: java.lang.String = `$this$commonUtf8`.getUtf8$okio();
   if (result == null) {
      result = _JvmPlatformKt.toUtf8String(`$this$commonUtf8`.internalArray$okio());
      `$this$commonUtf8`.setUtf8$okio(result);
   }

   return result;
}

internal inline fun ByteString.commonBase64(): String {
   return -Base64.encodeBase64$default(`$this$commonBase64`.getData$okio(), null, 1, null);
}

internal inline fun ByteString.commonBase64Url(): String {
   return -Base64.encodeBase64(`$this$commonBase64Url`.getData$okio(), -Base64.getBASE64_URL_SAFE());
}

/** @deprecated */
@JvmSynthetic
fun `getHEX_DIGIT_CHARS$annotations`() {
}

internal inline fun ByteString.commonHex(): String {
   var result: CharArray = new char[`$this$commonHex`.getData$okio().length * 2];
   var c: Int = 0;

   for (byte b : $this$commonHex.getData$okio()) {
      result[c++] = getHEX_DIGIT_CHARS()[b shr 4 and 15];
      result[c++] = getHEX_DIGIT_CHARS()[b and 15];
   }

   return StringsKt.concatToString(result);
}

internal inline fun ByteString.commonToAsciiLowercase(): ByteString {
   for (int i = 0; i < $this$commonToAsciiLowercase.getData$okio().length; i++) {
      var c: Byte = `$this$commonToAsciiLowercase`.getData$okio()[i];
      if (c >= 65 && c <= 90) {
         var var10000: ByteArray = `$this$commonToAsciiLowercase`.getData$okio();
         var10000 = Arrays.copyOf(var10000, var10000.length);
         var lowercase: ByteArray = var10000;
         var10000[i++] = (byte)(c - -32);

         while (i < lowercase.length) {
            c = lowercase[i];
            if (lowercase[i] >= 65 && lowercase[i] <= 90) {
               lowercase[i] = (byte)(c - -32);
               i++;
            } else {
               i++;
            }
         }

         return new ByteString(lowercase);
      }
   }

   return `$this$commonToAsciiLowercase`;
}

internal inline fun ByteString.commonToAsciiUppercase(): ByteString {
   for (int i = 0; i < $this$commonToAsciiUppercase.getData$okio().length; i++) {
      var c: Byte = `$this$commonToAsciiUppercase`.getData$okio()[i];
      if (c >= 97 && c <= 122) {
         var var10000: ByteArray = `$this$commonToAsciiUppercase`.getData$okio();
         var10000 = Arrays.copyOf(var10000, var10000.length);
         var lowercase: ByteArray = var10000;
         var10000[i++] = (byte)(c - 32);

         while (i < lowercase.length) {
            c = lowercase[i];
            if (lowercase[i] >= 97 && lowercase[i] <= 122) {
               lowercase[i] = (byte)(c - 32);
               i++;
            } else {
               i++;
            }
         }

         return new ByteString(lowercase);
      }
   }

   return `$this$commonToAsciiUppercase`;
}

internal inline fun ByteString.commonSubstring(beginIndex: Int, endIndex: Int): ByteString {
   var endIndexx: Int = okio.-SegmentedByteString.resolveDefaultParameter(`$this$commonSubstring`, endIndex);
   if (beginIndex < 0) {
      throw new IllegalArgumentException("beginIndex < 0".toString());
   } else if (endIndexx > `$this$commonSubstring`.getData$okio().length) {
      throw new IllegalArgumentException(("endIndex > length(" + `$this$commonSubstring`.getData$okio().length + ')').toString());
   } else {
      label26:
      if (endIndexx - beginIndex < 0) {
         throw new IllegalArgumentException("endIndex < beginIndex".toString());
      } else {
         return if (beginIndex == 0 && endIndexx == `$this$commonSubstring`.getData$okio().length)
            `$this$commonSubstring`
            else
            new ByteString(ArraysKt.copyOfRange(`$this$commonSubstring`.getData$okio(), beginIndex, endIndexx));
      }
   }
}

internal inline fun ByteString.commonGetByte(pos: Int): Byte {
   return `$this$commonGetByte`.getData$okio()[pos];
}

internal inline fun ByteString.commonGetSize(): Int {
   return `$this$commonGetSize`.getData$okio().length;
}

internal inline fun ByteString.commonToByteArray(): ByteArray {
   var var10000: ByteArray = `$this$commonToByteArray`.getData$okio();
   var10000 = Arrays.copyOf(var10000, var10000.length);
   return var10000;
}

internal inline fun ByteString.commonInternalArray(): ByteArray {
   return `$this$commonInternalArray`.getData$okio();
}

internal inline fun ByteString.commonRangeEquals(offset: Int, other: ByteString, otherOffset: Int, byteCount: Int): Boolean {
   return other.rangeEquals(otherOffset, `$this$commonRangeEquals`.getData$okio(), offset, byteCount);
}

internal inline fun ByteString.commonRangeEquals(offset: Int, other: ByteArray, otherOffset: Int, byteCount: Int): Boolean {
   return offset >= 0
      && offset <= `$this$commonRangeEquals`.getData$okio().length - byteCount
      && otherOffset >= 0
      && otherOffset <= other.length - byteCount
      && okio.-SegmentedByteString.arrayRangeEquals(`$this$commonRangeEquals`.getData$okio(), offset, other, otherOffset, byteCount);
}

internal inline fun ByteString.commonCopyInto(offset: Int, target: ByteArray, targetOffset: Int, byteCount: Int) {
   ArraysKt.copyInto(`$this$commonCopyInto`.getData$okio(), target, targetOffset, offset, offset + byteCount);
}

internal inline fun ByteString.commonStartsWith(prefix: ByteString): Boolean {
   return `$this$commonStartsWith`.rangeEquals(0, prefix, 0, prefix.size());
}

internal inline fun ByteString.commonStartsWith(prefix: ByteArray): Boolean {
   return `$this$commonStartsWith`.rangeEquals(0, prefix, 0, prefix.length);
}

internal inline fun ByteString.commonEndsWith(suffix: ByteString): Boolean {
   return `$this$commonEndsWith`.rangeEquals(`$this$commonEndsWith`.size() - suffix.size(), suffix, 0, suffix.size());
}

internal inline fun ByteString.commonEndsWith(suffix: ByteArray): Boolean {
   return `$this$commonEndsWith`.rangeEquals(`$this$commonEndsWith`.size() - suffix.length, suffix, 0, suffix.length);
}

internal inline fun ByteString.commonIndexOf(other: ByteArray, fromIndex: Int): Int {
   var limit: Int = `$this$commonIndexOf`.getData$okio().length - other.length;
   var i: Int = Math.max(fromIndex, 0);
   if (i <= limit) {
      while (true) {
         if (okio.-SegmentedByteString.arrayRangeEquals(`$this$commonIndexOf`.getData$okio(), i, other, 0, other.length)) {
            return i;
         }

         if (i == limit) {
            break;
         }

         i++;
      }
   }

   return -1;
}

internal inline fun ByteString.commonLastIndexOf(other: ByteString, fromIndex: Int): Int {
   return `$this$commonLastIndexOf`.lastIndexOf(other.internalArray$okio(), fromIndex);
}

internal inline fun ByteString.commonLastIndexOf(other: ByteArray, fromIndex: Int): Int {
   for (int i = Math.min(
         okio.-SegmentedByteString.resolveDefaultParameter($this$commonLastIndexOf, fromIndex), $this$commonLastIndexOf.getData$okio().length - other.length
      );
      -1 < i;
      i--
   ) {
      if (okio.-SegmentedByteString.arrayRangeEquals(`$this$commonLastIndexOf`.getData$okio(), i, other, 0, other.length)) {
         return i;
      }
   }

   return -1;
}

internal inline fun ByteString.commonEquals(other: Any?): Boolean {
   return other === `$this$commonEquals`
      || other is ByteString
         && (other as ByteString).size() == `$this$commonEquals`.getData$okio().length
         && (other as ByteString).rangeEquals(0, `$this$commonEquals`.getData$okio(), 0, `$this$commonEquals`.getData$okio().length);
}

internal inline fun ByteString.commonHashCode(): Int {
   var result: Int = `$this$commonHashCode`.getHashCode$okio();
   if (result != 0) {
      return result;
   } else {
      var var3: Int = Arrays.hashCode(`$this$commonHashCode`.getData$okio());
      `$this$commonHashCode`.setHashCode$okio(var3);
      return var3;
   }
}

internal inline fun ByteString.commonCompareTo(other: ByteString): Int {
   var sizeA: Int = `$this$commonCompareTo`.size();
   var sizeB: Int = other.size();
   var i: Int = 0;

   for (int size = Math.min(sizeA, sizeB); i < size; i++) {
      var byteA: Int = `$this$commonCompareTo`.getByte(i) and 255;
      var var13: Byte = other.getByte(i);
      var var12: Int = var13 and 255;
      if (byteA != (var13 and 255)) {
         return if (byteA < var12) -1 else 1;
      }
   }

   if (sizeA == sizeB) {
      return 0;
   } else {
      return if (sizeA < sizeB) -1 else 1;
   }
}

internal inline fun commonOf(data: ByteArray): ByteString {
   var var10002: ByteArray = Arrays.copyOf(data, data.length);
   return new ByteString(var10002);
}

internal inline fun ByteArray.commonToByteString(offset: Int, byteCount: Int): ByteString {
   var byteCountx: Int = okio.-SegmentedByteString.resolveDefaultParameter(`$this$commonToByteString`, byteCount);
   okio.-SegmentedByteString.checkOffsetAndCount((long)`$this$commonToByteString`.length, (long)offset, (long)byteCountx);
   return new ByteString(ArraysKt.copyOfRange(`$this$commonToByteString`, offset, offset + byteCountx));
}

internal inline fun String.commonEncodeUtf8(): ByteString {
   var byteString: ByteString = new ByteString(_JvmPlatformKt.asUtf8ToByteArray(`$this$commonEncodeUtf8`));
   byteString.setUtf8$okio(`$this$commonEncodeUtf8`);
   return byteString;
}

internal inline fun String.commonDecodeBase64(): ByteString? {
   var decoded: ByteArray = -Base64.decodeBase64ToArray(`$this$commonDecodeBase64`);
   return if (decoded != null) new ByteString(decoded) else null;
}

internal inline fun String.commonDecodeHex(): ByteString {
   if (`$this$commonDecodeHex`.length() % 2 != 0) {
      throw new IllegalArgumentException(("Unexpected hex string: " + `$this$commonDecodeHex`).toString());
   } else {
      var result: ByteArray = new byte[`$this$commonDecodeHex`.length() / 2];
      var i: Int = 0;

      for (int var4 = result.length; i < var4; i++) {
         result[i] = (byte)(
            (access$decodeHexDigit(`$this$commonDecodeHex`.charAt(i * 2)) shl 4) + access$decodeHexDigit(`$this$commonDecodeHex`.charAt(i * 2 + 1))
         );
      }

      return new ByteString(result);
   }
}

internal fun ByteString.commonWrite(buffer: Buffer, offset: Int, byteCount: Int) {
   buffer.write(`$this$commonWrite`.getData$okio(), offset, byteCount);
}

private fun decodeHexDigit(c: Char): Int {
   var var10000: Int;
   if ('0' <= c && c < ':') {
      var10000 = c - '0';
   } else if ('a' <= c && c < 'g') {
      var10000 = c - 'a' + 10;
   } else {
      if ('A' > c || c >= 'G') {
         throw new IllegalArgumentException("Unexpected hex digit: " + c);
      }

      var10000 = c - 'A' + 10;
   }

   return var10000;
}

internal inline fun ByteString.commonToString(): String {
   if (`$this$commonToString`.getData$okio().length == 0) {
      return "[size=0]";
   } else {
      var i: Int = access$codePointIndexToCharIndex(`$this$commonToString`.getData$okio(), 64);
      if (i == -1) {
         var var15: java.lang.String;
         if (`$this$commonToString`.getData$okio().length <= 64) {
            var15 = "[hex=" + `$this$commonToString`.hex() + ']';
         } else {
            var var16: StringBuilder = new StringBuilder().append("[size=").append(`$this$commonToString`.getData$okio().length).append(" hex=");
            var `endIndex$iv`: Int = okio.-SegmentedByteString.resolveDefaultParameter(`$this$commonToString`, 64);
            if (`endIndex$iv` > `$this$commonToString`.getData$okio().length) {
               throw new IllegalArgumentException(("endIndex > length(" + `$this$commonToString`.getData$okio().length + ')').toString());
            }

            if (`endIndex$iv` - 0 < 0) {
               throw new IllegalArgumentException("endIndex < beginIndex".toString());
            }

            var15 = var16.append(
                  (if (`endIndex$iv` == `$this$commonToString`.getData$okio().length)
                        `$this$commonToString`
                        else
                        new ByteString(ArraysKt.copyOfRange(`$this$commonToString`.getData$okio(), 0, `endIndex$iv`)))
                     .hex()
               )
               .append("…]")
               .toString();
         }

         return var15;
      } else {
         var text: java.lang.String = `$this$commonToString`.utf8();
         var var10000: java.lang.String = text.substring(0, i);
         var safeText: java.lang.String = StringsKt.replace$default(
            StringsKt.replace$default(StringsKt.replace$default(var10000, "\\", "\\\\", false, 4, null), "\n", "\\n", false, 4, null),
            "\r",
            "\\r",
            false,
            4,
            null
         );
         return if (i < text.length()) "[size=" + `$this$commonToString`.getData$okio().length + " text=" + safeText + "…]" else "[text=" + safeText + ']';
      }
   }
}

private fun codePointIndexToCharIndex(s: ByteArray, codePointCount: Int): Int {
   var charCount: Int = 0;
   var j: Int = 0;
   var `$this$processUtf8CodePoints$iv`: ByteArray = s;
   var `endIndex$iv`: Int = s.length;
   var `index$iv`: Int = 0;

   while (index$iv < endIndex$iv) {
      var `b0$iv`: Byte = `$this$processUtf8CodePoints$iv`[`index$iv`];
      if (`$this$processUtf8CodePoints$iv`[`index$iv`] >= 0) {
         if (j++ == codePointCount) {
            return charCount;
         }

         if (`b0$iv` != 10 && `b0$iv` != 13 && (0 <= `b0$iv` && `b0$iv` < 32 || 127 <= `b0$iv` && `b0$iv` < 160)) {
            return -1;
         }

         if (`b0$iv` == '�') {
            return -1;
         }

         charCount += if (`b0$iv` < 65536) 1 else 2;
         `index$iv`++;

         while (index$iv < endIndex$iv && $this$processUtf8CodePoints$iv[index$iv] >= 0) {
            var var27: Int = `$this$processUtf8CodePoints$iv`[`index$iv`++];
            if (j++ == codePointCount) {
               return charCount;
            }

            if (var27 != 10 && var27 != 13 && (0 <= var27 && var27 < 32 || 127 <= var27 && var27 < 160) || var27 == 65533) {
               return -1;
            }

            charCount += if (var27 < 65536) 1 else 2;
         }
      } else if (`b0$iv` shr 5 == -2) {
         var var180: Int;
         var var182: Byte;
         if (`endIndex$iv` <= `index$iv` + 1) {
            if (j++ == codePointCount) {
               return charCount;
            }

            if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
               return -1;
            }

            if ('�' == '�') {
               return -1;
            }

            charCount += if ('�' < 65536) 1 else 2;
            var180 = `index$iv`;
            var182 = 1;
         } else {
            var var149: Byte = `$this$processUtf8CodePoints$iv`[`index$iv`];
            var var154: Byte = `$this$processUtf8CodePoints$iv`[`index$iv` + 1];
            if ((`$this$processUtf8CodePoints$iv`[`index$iv` + 1] and 192) != 128) {
               if (j++ == codePointCount) {
                  return charCount;
               }

               if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                  return -1;
               }

               if ('�' == '�') {
                  return -1;
               }

               charCount += if ('�' < 65536) 1 else 2;
               var180 = `index$iv`;
               var182 = 1;
            } else {
               var var159: Int = 3968 xor var154 xor var149 shl 6;
               if ((3968 xor var154 xor var149 shl 6) < 128) {
                  if (j++ == codePointCount) {
                     return charCount;
                  }

                  if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                     return -1;
                  }

                  if ('�' == '�') {
                     return -1;
                  }

                  charCount += if ('�' < 65536) 1 else 2;
                  var180 = `index$iv`;
               } else {
                  if (j++ == codePointCount) {
                     return charCount;
                  }

                  if (var159 != 10 && var159 != 13 && (0 <= var159 && var159 < 32 || 127 <= var159 && var159 < 160)) {
                     return -1;
                  }

                  if (var159 == 65533) {
                     return -1;
                  }

                  charCount += if (var159 < 65536) 1 else 2;
                  var180 = `index$iv`;
               }

               var182 = 2;
            }
         }

         `index$iv` = var180 + var182;
      } else if (`b0$iv` shr 4 == -2) {
         var var179: Int;
         var var181: Byte;
         label993:
         if (`endIndex$iv` <= `index$iv` + 2) {
            if (j++ == codePointCount) {
               return charCount;
            }

            if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
               return -1;
            }

            if ('�' == '�') {
               return -1;
            }

            charCount += if ('�' < 65536) 1 else 2;
            var179 = `index$iv`;
            var181 = (byte)(if (`endIndex$iv` > `index$iv` + 1 && (`$this$processUtf8CodePoints$iv`[`index$iv` + 1] and 192) == 128) 2 else 1);
            break label993;
         } else {
            var var148: Byte = `$this$processUtf8CodePoints$iv`[`index$iv`];
            var var153: Byte = `$this$processUtf8CodePoints$iv`[`index$iv` + 1];
            if ((`$this$processUtf8CodePoints$iv`[`index$iv` + 1] and 192) != 128) {
               if (j++ == codePointCount) {
                  return charCount;
               }

               if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                  return -1;
               }

               if ('�' == '�') {
                  return -1;
               }

               charCount += if ('�' < 65536) 1 else 2;
               var179 = `index$iv`;
               var181 = 1;
            } else {
               var var157: Byte = `$this$processUtf8CodePoints$iv`[`index$iv` + 2];
               if ((`$this$processUtf8CodePoints$iv`[`index$iv` + 2] and 192) != 128) {
                  if (j++ == codePointCount) {
                     return charCount;
                  }

                  if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                     return -1;
                  }

                  if ('�' == '�') {
                     return -1;
                  }

                  charCount += if ('�' < 65536) 1 else 2;
                  var179 = `index$iv`;
                  var181 = 2;
               } else {
                  var var165: Int = -123008 xor var157 xor var153 shl 6 xor var148 shl 12;
                  if ((-123008 xor var157 xor var153 shl 6 xor var148 shl 12) < 2048) {
                     if (j++ == codePointCount) {
                        return charCount;
                     }

                     if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                        return -1;
                     }

                     if ('�' == '�') {
                        return -1;
                     }

                     charCount += if ('�' < 65536) 1 else 2;
                     var179 = `index$iv`;
                  } else if (55296 <= var165 && var165 < 57344) {
                     if (j++ == codePointCount) {
                        return charCount;
                     }

                     if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                        return -1;
                     }

                     if ('�' == '�') {
                        return -1;
                     }

                     charCount += if ('�' < 65536) 1 else 2;
                     var179 = `index$iv`;
                  } else {
                     if (j++ == codePointCount) {
                        return charCount;
                     }

                     if (var165 != 10 && var165 != 13 && (0 <= var165 && var165 < 32 || 127 <= var165 && var165 < 160)) {
                        return -1;
                     }

                     if (var165 == 65533) {
                        return -1;
                     }

                     charCount += if (var165 < 65536) 1 else 2;
                     var179 = `index$iv`;
                  }

                  var181 = 3;
               }
            }
         }

         `index$iv` = var179 + var181;
      } else if (`b0$iv` shr 3 == -2) {
         var var10000: Int;
         var var10001: Byte;
         label1154:
         if (`endIndex$iv` <= `index$iv` + 3) {
            if (j++ == codePointCount) {
               return charCount;
            }

            if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
               return -1;
            }

            if ('�' == '�') {
               return -1;
            }

            charCount += if ('�' < 65536) 1 else 2;
            var10000 = `index$iv`;
            var10001 = (byte)(if (`endIndex$iv` <= `index$iv` + 1 || (`$this$processUtf8CodePoints$iv`[`index$iv` + 1] and 192) != 128)
               1
               else
               (if (`endIndex$iv` > `index$iv` + 2 && (`$this$processUtf8CodePoints$iv`[`index$iv` + 2] and 192) == 128) 3 else 2));
            break label1154;
         } else {
            var var146: Byte = `$this$processUtf8CodePoints$iv`[`index$iv`];
            var var151: Byte = `$this$processUtf8CodePoints$iv`[`index$iv` + 1];
            if ((`$this$processUtf8CodePoints$iv`[`index$iv` + 1] and 192) != 128) {
               if (j++ == codePointCount) {
                  return charCount;
               }

               if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                  return -1;
               }

               if ('�' == '�') {
                  return -1;
               }

               charCount += if ('�' < 65536) 1 else 2;
               var10000 = `index$iv`;
               var10001 = 1;
            } else {
               var var155: Byte = `$this$processUtf8CodePoints$iv`[`index$iv` + 2];
               if ((`$this$processUtf8CodePoints$iv`[`index$iv` + 2] and 192) != 128) {
                  if (j++ == codePointCount) {
                     return charCount;
                  }

                  if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                     return -1;
                  }

                  if ('�' == '�') {
                     return -1;
                  }

                  charCount += if ('�' < 65536) 1 else 2;
                  var10000 = `index$iv`;
                  var10001 = 2;
               } else {
                  var var162: Byte = `$this$processUtf8CodePoints$iv`[`index$iv` + 3];
                  if ((`$this$processUtf8CodePoints$iv`[`index$iv` + 3] and 192) != 128) {
                     if (j++ == codePointCount) {
                        return charCount;
                     }

                     if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                        return -1;
                     }

                     if ('�' == '�') {
                        return -1;
                     }

                     charCount += if ('�' < 65536) 1 else 2;
                     var10000 = `index$iv`;
                     var10001 = 3;
                  } else {
                     var var169: Int = 3678080 xor var162 xor var155 shl 6 xor var151 shl 12 xor var146 shl 18;
                     if ((3678080 xor var162 xor var155 shl 6 xor var151 shl 12 xor var146 shl 18) > 1114111) {
                        if (j++ == codePointCount) {
                           return charCount;
                        }

                        if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                           return -1;
                        }

                        if ('�' == '�') {
                           return -1;
                        }

                        charCount += if ('�' < 65536) 1 else 2;
                        var10000 = `index$iv`;
                     } else if (55296 <= var169 && var169 < 57344) {
                        if (j++ == codePointCount) {
                           return charCount;
                        }

                        if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                           return -1;
                        }

                        if ('�' == '�') {
                           return -1;
                        }

                        charCount += if ('�' < 65536) 1 else 2;
                        var10000 = `index$iv`;
                     } else if (var169 < 65536) {
                        if (j++ == codePointCount) {
                           return charCount;
                        }

                        if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
                           return -1;
                        }

                        if ('�' == '�') {
                           return -1;
                        }

                        charCount += if ('�' < 65536) 1 else 2;
                        var10000 = `index$iv`;
                     } else {
                        if (j++ == codePointCount) {
                           return charCount;
                        }

                        if (var169 != 10 && var169 != 13 && (0 <= var169 && var169 < 32 || 127 <= var169 && var169 < 160)) {
                           return -1;
                        }

                        if (var169 == 65533) {
                           return -1;
                        }

                        charCount += if (var169 < 65536) 1 else 2;
                        var10000 = `index$iv`;
                     }

                     var10001 = 4;
                  }
               }
            }
         }

         `index$iv` = var10000 + var10001;
      } else {
         if (j++ == codePointCount) {
            return charCount;
         }

         if ('�' != '\n' && '�' != '\r' && (0 <= '�' && '�' < ' ' || 127 <= '�' && '�' < 160)) {
            return -1;
         }

         if ('�' == '�') {
            return -1;
         }

         charCount += if ('�' < 65536) 1 else 2;
         `index$iv`++;
      }
   }

   return charCount;
}

@JvmSynthetic
fun `access$decodeHexDigit`(c: Char): Int {
   return decodeHexDigit(c);
}

@JvmSynthetic
fun `access$codePointIndexToCharIndex`(s: ByteArray, codePointCount: Int): Int {
   return codePointIndexToCharIndex(s, codePointCount);
}

