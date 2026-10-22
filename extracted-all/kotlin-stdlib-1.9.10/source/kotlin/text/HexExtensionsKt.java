@file:SourceDebugExtension(["SMAP\nHexExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HexExtensions.kt\nkotlin/text/HexExtensionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,594:1\n1#2:595\n1183#3,3:596\n1183#3,3:599\n*S KotlinDebug\n*F\n+ 1 HexExtensions.kt\nkotlin/text/HexExtensionsKt\n*L\n16#1:596,3\n17#1:599,3\n*E\n"])

package kotlin.text

import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension

private final val HEX_DIGITS_TO_DECIMAL: IntArray
private const val LOWER_CASE_HEX_DIGITS: String = "0123456789abcdef"
private const val UPPER_CASE_HEX_DIGITS: String = "0123456789ABCDEF"

/** @deprecated */
@JvmSynthetic
fun `getHEX_DIGITS_TO_DECIMAL$annotations`() {
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun ByteArray.toHexString(format: HexFormat): String {
   return toHexString(`$this$toHexString`, 0, `$this$toHexString`.length, format);
}

@JvmSynthetic
fun `toHexString$default`(var0: ByteArray, var1: HexFormat, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      var1 = HexFormat.Companion.getDefault();
   }

   return toHexString(var0, var1);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun ByteArray.toHexString(startIndex: Int, endIndex: Int, format: HexFormat): String {
   AbstractList.Companion.checkBoundsIndexes$kotlin_stdlib(startIndex, endIndex, `$this$toHexString`.length);
   if (startIndex == endIndex) {
      return "";
   } else {
      var digits: java.lang.String = if (format.getUpperCase()) "0123456789ABCDEF" else "0123456789abcdef";
      var bytesFormat: HexFormat.BytesHexFormat = format.getBytes();
      var bytesPerLine: Int = bytesFormat.getBytesPerLine();
      var bytesPerGroup: Int = bytesFormat.getBytesPerGroup();
      var bytePrefix: java.lang.String = bytesFormat.getBytePrefix();
      var byteSuffix: java.lang.String = bytesFormat.getByteSuffix();
      var byteSeparator: java.lang.String = bytesFormat.getByteSeparator();
      var groupSeparator: java.lang.String = bytesFormat.getGroupSeparator();
      var formatLength: Int = formattedStringLength(
         endIndex - startIndex, bytesPerLine, bytesPerGroup, groupSeparator.length(), byteSeparator.length(), bytePrefix.length(), byteSuffix.length()
      );
      var indexInLine: Int = 0;
      var indexInGroup: Int = 0;
      var var15: StringBuilder = new StringBuilder(formatLength);
      var `$this$toHexString_u24lambda_u243`: StringBuilder = var15;

      for (int i = startIndex; i < endIndex; i++) {
         var var19: Int = `$this$toHexString`[i] and 255;
         if (indexInLine == bytesPerLine) {
            `$this$toHexString_u24lambda_u243`.append('\n');
            indexInLine = 0;
            indexInGroup = 0;
         } else if (indexInGroup == bytesPerGroup) {
            `$this$toHexString_u24lambda_u243`.append(groupSeparator);
            indexInGroup = 0;
         }

         if (indexInGroup != 0) {
            `$this$toHexString_u24lambda_u243`.append(byteSeparator);
         }

         `$this$toHexString_u24lambda_u243`.append(bytePrefix);
         `$this$toHexString_u24lambda_u243`.append(digits.charAt(var19 shr 4));
         `$this$toHexString_u24lambda_u243`.append(digits.charAt(var19 and 15));
         `$this$toHexString_u24lambda_u243`.append(byteSuffix);
         indexInGroup++;
         indexInLine++;
      }

      if (formatLength != `$this$toHexString_u24lambda_u243`.length()) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         var var10000: java.lang.String = var15.toString();
         return var10000;
      }
   }
}

@JvmSynthetic
fun `toHexString$default`(var0: ByteArray, var1: Int, var2: Int, var3: HexFormat, var4: Int, var5: Any): java.lang.String {
   if ((var4 and 1) != 0) {
      var1 = 0;
   }

   if ((var4 and 2) != 0) {
      var2 = var0.length;
   }

   if ((var4 and 4) != 0) {
      var3 = HexFormat.Companion.getDefault();
   }

   return toHexString(var0, var1, var2, var3);
}

internal fun formattedStringLength(
   totalBytes: Int,
   bytesPerLine: Int,
   bytesPerGroup: Int,
   groupSeparatorLength: Int,
   byteSeparatorLength: Int,
   bytePrefixLength: Int,
   byteSuffixLength: Int
): Int {
   if (totalBytes <= 0) {
      throw new IllegalArgumentException("Failed requirement.".toString());
   } else {
      var var15: Int = (totalBytes - 1) / bytesPerLine;
      var totalLength: Int = (bytesPerLine - 1) / bytesPerGroup;
      var groupSeparators: Int = var15 * ((bytesPerLine - 1) / bytesPerGroup)
         + ((if (totalBytes % bytesPerLine == 0) bytesPerLine else totalBytes % bytesPerLine) - 1) / bytesPerGroup;
      var var18: Long = (long)var15
         + (long)(
               var15 * ((bytesPerLine - 1) / bytesPerGroup)
                  + ((if (totalBytes % bytesPerLine == 0) bytesPerLine else totalBytes % bytesPerLine) - 1) / bytesPerGroup
            )
            * (long)groupSeparatorLength
         + (long)(
               totalBytes
                  - 1
                  - var15
                  - (
                     var15 * ((bytesPerLine - 1) / bytesPerGroup)
                        + ((if (totalBytes % bytesPerLine == 0) bytesPerLine else totalBytes % bytesPerLine) - 1) / bytesPerGroup
                  )
            )
            * (long)byteSeparatorLength
         + (long)totalBytes * ((long)bytePrefixLength + 2L + (long)byteSuffixLength);
      if (!RangesKt.intRangeContains(
         new IntRange(0, Integer.MAX_VALUE),
         (long)var15
            + (long)groupSeparators * (long)groupSeparatorLength
            + (long)(
                  totalBytes
                     - 1
                     - var15
                     - (var15 * totalLength + ((if (totalBytes % bytesPerLine == 0) bytesPerLine else totalBytes % bytesPerLine) - 1) / bytesPerGroup)
               )
               * (long)byteSeparatorLength
            + (long)totalBytes * ((long)bytePrefixLength + 2L + (long)byteSuffixLength)
      )) {
         throw new IllegalArgumentException("The resulting string length is too big: " + ULong.toString-impl(ULong.constructor-impl(var18)));
      } else {
         return (int)var18;
      }
   }
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun String.hexToByteArray(format: HexFormat): ByteArray {
   return hexToByteArray(`$this$hexToByteArray`, 0, `$this$hexToByteArray`.length(), format);
}

@JvmSynthetic
fun `hexToByteArray$default`(var0: java.lang.String, var1: HexFormat, var2: Int, var3: Any): ByteArray {
   if ((var2 and 1) != 0) {
      var1 = HexFormat.Companion.getDefault();
   }

   return hexToByteArray(var0, var1);
}

@ExperimentalStdlibApi
private fun String.hexToByteArray(startIndex: Int, endIndex: Int, format: HexFormat): ByteArray {
   AbstractList.Companion.checkBoundsIndexes$kotlin_stdlib(startIndex, endIndex, `$this$hexToByteArray`.length());
   if (startIndex == endIndex) {
      return new byte[0];
   } else {
      var bytesFormat: HexFormat.BytesHexFormat = format.getBytes();
      var bytesPerLine: Int = bytesFormat.getBytesPerLine();
      var bytesPerGroup: Int = bytesFormat.getBytesPerGroup();
      var bytePrefix: java.lang.String = bytesFormat.getBytePrefix();
      var byteSuffix: java.lang.String = bytesFormat.getByteSuffix();
      var byteSeparator: java.lang.String = bytesFormat.getByteSeparator();
      var groupSeparator: java.lang.String = bytesFormat.getGroupSeparator();
      var result: ByteArray = new byte[parsedByteArrayMaxSize(
         endIndex - startIndex, bytesPerLine, bytesPerGroup, groupSeparator.length(), byteSeparator.length(), bytePrefix.length(), byteSuffix.length()
      )];
      var i: Int = startIndex;
      var byteIndex: Int = 0;
      var indexInLine: Int = 0;
      var indexInGroup: Int = 0;

      while (i < endIndex) {
         if (indexInLine == bytesPerLine) {
            i = checkNewLineAt(`$this$hexToByteArray`, i, endIndex);
            indexInLine = 0;
            indexInGroup = 0;
         } else if (indexInGroup == bytesPerGroup) {
            i = checkContainsAt(`$this$hexToByteArray`, groupSeparator, i, endIndex, "group separator");
            indexInGroup = 0;
         } else if (indexInGroup != 0) {
            i = checkContainsAt(`$this$hexToByteArray`, byteSeparator, i, endIndex, "byte separator");
         }

         indexInLine++;
         indexInGroup++;
         i = checkContainsAt(`$this$hexToByteArray`, bytePrefix, i, endIndex, "byte prefix");
         checkHexLength(`$this$hexToByteArray`, i, RangesKt.coerceAtMost(i + 2, endIndex), 2, true);
         result[byteIndex++] = (byte)(decimalFromHexDigitAt(`$this$hexToByteArray`, i++) shl 4 or decimalFromHexDigitAt(`$this$hexToByteArray`, i++));
         i = checkContainsAt(`$this$hexToByteArray`, byteSuffix, i, endIndex, "byte suffix");
      }

      var var10000: ByteArray;
      if (byteIndex == result.length) {
         var10000 = result;
      } else {
         var10000 = Arrays.copyOf(result, byteIndex);
      }

      return var10000;
   }
}

@JvmSynthetic
fun `hexToByteArray$default`(var0: java.lang.String, var1: Int, var2: Int, var3: HexFormat, var4: Int, var5: Any): ByteArray {
   if ((var4 and 1) != 0) {
      var1 = 0;
   }

   if ((var4 and 2) != 0) {
      var2 = var0.length();
   }

   if ((var4 and 4) != 0) {
      var3 = HexFormat.Companion.getDefault();
   }

   return hexToByteArray(var0, var1, var2, var3);
}

internal fun parsedByteArrayMaxSize(
   stringLength: Int,
   bytesPerLine: Int,
   bytesPerGroup: Int,
   groupSeparatorLength: Int,
   byteSeparatorLength: Int,
   bytePrefixLength: Int,
   byteSuffixLength: Int
): Int {
   if (stringLength <= 0) {
      throw new IllegalArgumentException("Failed requirement.".toString());
   } else {
      var var22: Long = (long)bytePrefixLength + 2L + (long)byteSuffixLength;
      var charsPerGroup: Long = charsPerSet((long)bytePrefixLength + 2L + (long)byteSuffixLength, bytesPerGroup, byteSeparatorLength);
      var var10000: Long;
      if (bytesPerLine <= bytesPerGroup) {
         var10000 = charsPerSet(var22, bytesPerLine, byteSeparatorLength);
      } else {
         var result: Long = charsPerSet(charsPerGroup, bytesPerLine / bytesPerGroup, groupSeparatorLength);
         var bytesPerLastGroupInLine: Int = bytesPerLine % bytesPerGroup;
         if (bytesPerLine % bytesPerGroup != 0) {
            result = result + (long)groupSeparatorLength + charsPerSet(var22, bytesPerLastGroupInLine, byteSeparatorLength);
         }

         var10000 = result;
      }

      var var23: Long = (long)stringLength;
      var wholeLines: Long = wholeElementsPerSet((long)stringLength, var10000, 1);
      var var24: Long = (long)stringLength - wholeLines * (var10000 + 1L);
      var wholeGroupsInLastLine: Long = wholeElementsPerSet(var23 - wholeLines * (var10000 + 1L), charsPerGroup, groupSeparatorLength);
      var wholeBytesInLastGroup: Long = wholeElementsPerSet(
         var24 - wholeGroupsInLastLine * (charsPerGroup + (long)groupSeparatorLength), var22, byteSeparatorLength
      );
      return (int)(
         wholeLines * (long)bytesPerLine
            + wholeGroupsInLastLine * (long)bytesPerGroup
            + wholeBytesInLastGroup
            + (long)(
               if (var24 - wholeGroupsInLastLine * (charsPerGroup + (long)groupSeparatorLength) - wholeBytesInLastGroup * (var22 + (long)byteSeparatorLength)
                     > 0L)
                  1
                  else
                  0
            )
      );
   }
}

private fun charsPerSet(charsPerElement: Long, elementsPerSet: Int, elementSeparatorLength: Int): Long {
   if (elementsPerSet <= 0) {
      throw new IllegalArgumentException("Failed requirement.".toString());
   } else {
      return charsPerElement * (long)elementsPerSet + (long)elementSeparatorLength * ((long)elementsPerSet - 1L);
   }
}

private fun wholeElementsPerSet(charsPerSet: Long, charsPerElement: Long, elementSeparatorLength: Int): Long {
   return if (charsPerSet > 0L && charsPerElement > 0L) (charsPerSet + (long)elementSeparatorLength) / (charsPerElement + (long)elementSeparatorLength) else 0L;
}

private fun String.checkNewLineAt(index: Int, endIndex: Int): Int {
   var var10000: Int;
   if (`$this$checkNewLineAt`.charAt(index) == '\r') {
      var10000 = if (index + 1 < endIndex && `$this$checkNewLineAt`.charAt(index + 1) == '\n') index + 2 else index + 1;
   } else {
      if (`$this$checkNewLineAt`.charAt(index) != '\n') {
         throw new NumberFormatException("Expected a new line at index " + index + ", but was " + `$this$checkNewLineAt`.charAt(index));
      }

      var10000 = index + 1;
   }

   return var10000;
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun Byte.toHexString(format: HexFormat): String {
   return toHexStringImpl((long)`$this$toHexString`, format, 8);
}

@JvmSynthetic
fun `toHexString$default`(var0: Byte, var1: HexFormat, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      var1 = HexFormat.Companion.getDefault();
   }

   return toHexString(var0, var1);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun String.hexToByte(format: HexFormat): Byte {
   return hexToByte(`$this$hexToByte`, 0, `$this$hexToByte`.length(), format);
}

@JvmSynthetic
fun `hexToByte$default`(var0: java.lang.String, var1: HexFormat, var2: Int, var3: Any): Byte {
   if ((var2 and 1) != 0) {
      var1 = HexFormat.Companion.getDefault();
   }

   return hexToByte(var0, var1);
}

@ExperimentalStdlibApi
private fun String.hexToByte(startIndex: Int, endIndex: Int, format: HexFormat): Byte {
   return (byte)((int)hexToLongImpl(`$this$hexToByte`, startIndex, endIndex, format, 2));
}

@JvmSynthetic
fun `hexToByte$default`(var0: java.lang.String, var1: Int, var2: Int, var3: HexFormat, var4: Int, var5: Any): Byte {
   if ((var4 and 1) != 0) {
      var1 = 0;
   }

   if ((var4 and 2) != 0) {
      var2 = var0.length();
   }

   if ((var4 and 4) != 0) {
      var3 = HexFormat.Companion.getDefault();
   }

   return hexToByte(var0, var1, var2, var3);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun Short.toHexString(format: HexFormat): String {
   return toHexStringImpl((long)`$this$toHexString`, format, 16);
}

@JvmSynthetic
fun `toHexString$default`(var0: Short, var1: HexFormat, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      var1 = HexFormat.Companion.getDefault();
   }

   return toHexString(var0, var1);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun String.hexToShort(format: HexFormat): Short {
   return hexToShort(`$this$hexToShort`, 0, `$this$hexToShort`.length(), format);
}

@JvmSynthetic
fun `hexToShort$default`(var0: java.lang.String, var1: HexFormat, var2: Int, var3: Any): Short {
   if ((var2 and 1) != 0) {
      var1 = HexFormat.Companion.getDefault();
   }

   return hexToShort(var0, var1);
}

@ExperimentalStdlibApi
private fun String.hexToShort(startIndex: Int, endIndex: Int, format: HexFormat): Short {
   return (short)((int)hexToLongImpl(`$this$hexToShort`, startIndex, endIndex, format, 4));
}

@JvmSynthetic
fun `hexToShort$default`(var0: java.lang.String, var1: Int, var2: Int, var3: HexFormat, var4: Int, var5: Any): Short {
   if ((var4 and 1) != 0) {
      var1 = 0;
   }

   if ((var4 and 2) != 0) {
      var2 = var0.length();
   }

   if ((var4 and 4) != 0) {
      var3 = HexFormat.Companion.getDefault();
   }

   return hexToShort(var0, var1, var2, var3);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun Int.toHexString(format: HexFormat): String {
   return toHexStringImpl((long)`$this$toHexString`, format, 32);
}

@JvmSynthetic
fun `toHexString$default`(var0: Int, var1: HexFormat, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      var1 = HexFormat.Companion.getDefault();
   }

   return toHexString(var0, var1);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun String.hexToInt(format: HexFormat): Int {
   return hexToInt(`$this$hexToInt`, 0, `$this$hexToInt`.length(), format);
}

@JvmSynthetic
fun `hexToInt$default`(var0: java.lang.String, var1: HexFormat, var2: Int, var3: Any): Int {
   if ((var2 and 1) != 0) {
      var1 = HexFormat.Companion.getDefault();
   }

   return hexToInt(var0, var1);
}

@ExperimentalStdlibApi
private fun String.hexToInt(startIndex: Int, endIndex: Int, format: HexFormat): Int {
   return (int)hexToLongImpl(`$this$hexToInt`, startIndex, endIndex, format, 8);
}

@JvmSynthetic
fun `hexToInt$default`(var0: java.lang.String, var1: Int, var2: Int, var3: HexFormat, var4: Int, var5: Any): Int {
   if ((var4 and 1) != 0) {
      var1 = 0;
   }

   if ((var4 and 2) != 0) {
      var2 = var0.length();
   }

   if ((var4 and 4) != 0) {
      var3 = HexFormat.Companion.getDefault();
   }

   return hexToInt(var0, var1, var2, var3);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun Long.toHexString(format: HexFormat): String {
   return toHexStringImpl(`$this$toHexString`, format, 64);
}

@JvmSynthetic
fun `toHexString$default`(var0: Long, var2: HexFormat, var3: Int, var4: Any): java.lang.String {
   if ((var3 and 1) != 0) {
      var2 = HexFormat.Companion.getDefault();
   }

   return toHexString(var0, var2);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
public fun String.hexToLong(format: HexFormat): Long {
   return hexToLong(`$this$hexToLong`, 0, `$this$hexToLong`.length(), format);
}

@JvmSynthetic
fun `hexToLong$default`(var0: java.lang.String, var1: HexFormat, var2: Int, var3: Any): Long {
   if ((var2 and 1) != 0) {
      var1 = HexFormat.Companion.getDefault();
   }

   return hexToLong(var0, var1);
}

@ExperimentalStdlibApi
private fun String.hexToLong(startIndex: Int, endIndex: Int, format: HexFormat): Long {
   return hexToLongImpl(`$this$hexToLong`, startIndex, endIndex, format, 16);
}

@JvmSynthetic
fun `hexToLong$default`(var0: java.lang.String, var1: Int, var2: Int, var3: HexFormat, var4: Int, var5: Any): Long {
   if ((var4 and 1) != 0) {
      var1 = 0;
   }

   if ((var4 and 2) != 0) {
      var2 = var0.length();
   }

   if ((var4 and 4) != 0) {
      var3 = HexFormat.Companion.getDefault();
   }

   return hexToLong(var0, var1, var2, var3);
}

@ExperimentalStdlibApi
private fun Long.toHexStringImpl(format: HexFormat, bits: Int): String {
   if ((bits and 3) != 0) {
      throw new IllegalArgumentException("Failed requirement.".toString());
   } else {
      var var16: java.lang.String = if (format.getUpperCase()) "0123456789ABCDEF" else "0123456789abcdef";
      var value: Long = `$this$toHexStringImpl`;
      var prefix: java.lang.String = format.getNumber().getPrefix();
      var suffix: java.lang.String = format.getNumber().getSuffix();
      var formatLength: Int = prefix.length() + (bits shr 2) + suffix.length();
      var var18: Boolean = format.getNumber().getRemoveLeadingZeros();
      var var11: StringBuilder = new StringBuilder(formatLength);
      var `$this$toHexStringImpl_u24lambda_u246`: StringBuilder = var11;
      var11.append(prefix);
      var shift: Int = bits;

      while (shift > 0) {
         shift -= 4;
         var decimal: Int = (int)(value shr shift and 15L);
         var18 = var18 && (int)(value shr shift and 15L) == 0 && shift > 0;
         if (!var18) {
            `$this$toHexStringImpl_u24lambda_u246`.append(var16.charAt(decimal));
         }
      }

      `$this$toHexStringImpl_u24lambda_u246`.append(suffix);
      var var10000: java.lang.String = var11.toString();
      return var10000;
   }
}

@ExperimentalStdlibApi
private fun String.hexToLongImpl(startIndex: Int, endIndex: Int, format: HexFormat, maxDigits: Int): Long {
   AbstractList.Companion.checkBoundsIndexes$kotlin_stdlib(startIndex, endIndex, `$this$hexToLongImpl`.length());
   var prefix: java.lang.String = format.getNumber().getPrefix();
   var suffix: java.lang.String = format.getNumber().getSuffix();
   if (prefix.length() + suffix.length() >= endIndex - startIndex) {
      var var10002: StringBuilder = new StringBuilder()
         .append("Expected a hexadecimal number with prefix \"")
         .append(prefix)
         .append("\" and suffix \"")
         .append(suffix)
         .append("\", but was ");
      var var10003: java.lang.String = `$this$hexToLongImpl`.substring(startIndex, endIndex);
      throw new NumberFormatException(var10002.append(var10003).toString());
   } else {
      var digitsStartIndex: Int = checkContainsAt(`$this$hexToLongImpl`, prefix, startIndex, endIndex, "prefix");
      var digitsEndIndex: Int = endIndex - suffix.length();
      checkContainsAt(`$this$hexToLongImpl`, suffix, digitsEndIndex, endIndex, "suffix");
      checkHexLength(`$this$hexToLongImpl`, digitsStartIndex, digitsEndIndex, maxDigits, false);
      var result: Long = 0L;

      for (int i = digitsStartIndex; i < digitsEndIndex; i++) {
         result = result shl 4 or (long)decimalFromHexDigitAt(`$this$hexToLongImpl`, i);
      }

      return result;
   }
}

@JvmSynthetic
fun `hexToLongImpl$default`(var0: java.lang.String, var1: Int, var2: Int, var3: HexFormat, var4: Int, var5: Int, var6: Any): Long {
   if ((var5 and 1) != 0) {
      var1 = 0;
   }

   if ((var5 and 2) != 0) {
      var2 = var0.length();
   }

   return hexToLongImpl(var0, var1, var2, var3, var4);
}

private fun String.checkContainsAt(part: String, index: Int, endIndex: Int, partName: String): Int {
   var end: Int = index + part.length();
   if (end <= endIndex && StringsKt.regionMatches(`$this$checkContainsAt`, index, part, 0, part.length(), true)) {
      return end;
   } else {
      var var10002: StringBuilder = new StringBuilder()
         .append("Expected ")
         .append(partName)
         .append(" \"")
         .append(part)
         .append("\" at index ")
         .append(index)
         .append(", but was ");
      var var7: Int = RangesKt.coerceAtMost(end, endIndex);
      var var10003: java.lang.String = `$this$checkContainsAt`.substring(index, var7);
      throw new NumberFormatException(var10002.append(var10003).toString());
   }
}

private fun String.checkHexLength(startIndex: Int, endIndex: Int, maxDigits: Int, requireMaxLength: Boolean) {
   var digitsLength: Int = endIndex - startIndex;
   if (if (requireMaxLength) endIndex - startIndex != maxDigits else endIndex - startIndex > maxDigits) {
      var specifier: java.lang.String = if (requireMaxLength) "exactly" else "at most";
      var var10000: java.lang.String = `$this$checkHexLength`.substring(startIndex, endIndex);
      throw new NumberFormatException(
         "Expected " + specifier + ' ' + maxDigits + " hexadecimal digits at index " + startIndex + ", but was " + var10000 + " of length " + digitsLength
      );
   }
}

private fun String.decimalFromHexDigitAt(index: Int): Int {
   var code: Int = `$this$decimalFromHexDigitAt`.charAt(index);
   if (code <= 127 && HEX_DIGITS_TO_DECIMAL[code] >= 0) {
      return HEX_DIGITS_TO_DECIMAL[code];
   } else {
      throw new NumberFormatException("Expected a hexadecimal digit at index " + index + ", but was " + `$this$decimalFromHexDigitAt`.charAt(index));
   }
}

