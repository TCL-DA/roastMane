@file:JvmName(name = "-SegmentedByteString")

@file:SourceDebugExtension(["SMAP\nUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,187:1\n68#1:188\n74#1:189\n*S KotlinDebug\n*F\n+ 1 Util.kt\nokio/-SegmentedByteString\n*L\n106#1:188\n107#1:189\n*E\n"])

package okio

import kotlin.jvm.internal.SourceDebugExtension
import okio.Buffer.UnsafeCursor
import okio.internal.-ByteString

internal final val DEFAULT__ByteString_size: Int = -1234567890
internal final val DEFAULT__new_UnsafeCursor: UnsafeCursor = new Buffer.UnsafeCursor()

internal fun checkOffsetAndCount(size: Long, offset: Long, byteCount: Long) {
   if ((offset or byteCount) < 0L || offset > size || size - offset < byteCount) {
      throw new ArrayIndexOutOfBoundsException("size=" + size + " offset=" + offset + " byteCount=" + byteCount);
   }
}

internal fun Short.reverseBytes(): Short {
   return (short)((`$this$reverseBytes` and '\uffff' and '\uff00') ushr 8 or (`$this$reverseBytes` and '\uffff' and 255) shl 8);
}

internal fun Int.reverseBytes(): Int {
   return (`$this$reverseBytes` and -16777216) ushr 24 or (`$this$reverseBytes` and 16711680) ushr 8 or (`$this$reverseBytes` and 65280) shl 8 or (
      `$this$reverseBytes` and 255
   ) shl 24;
}

internal fun Long.reverseBytes(): Long {
   return (`$this$reverseBytes` and -72057594037927936L) ushr 56 or (`$this$reverseBytes` and 71776119061217280L) ushr 40 or (
      `$this$reverseBytes` and 280375465082880L
   ) ushr 24 or (`$this$reverseBytes` and 1095216660480L) ushr 8 or (`$this$reverseBytes` and 4278190080L) shl 8 or (`$this$reverseBytes` and 16711680L) shl 24 or (
      `$this$reverseBytes` and 65280L
   ) shl 40 or (`$this$reverseBytes` and 255L) shl 56;
}

internal inline infix fun Int.leftRotate(bitCount: Int): Int {
   return `$this$leftRotate` shl bitCount or `$this$leftRotate` ushr 32 - bitCount;
}

internal inline infix fun Long.rightRotate(bitCount: Int): Long {
   return `$this$rightRotate` ushr bitCount or `$this$rightRotate` shl 64 - bitCount;
}

internal inline infix fun Byte.shr(other: Int): Int {
   return `$this$shr` shr other;
}

internal inline infix fun Byte.shl(other: Int): Int {
   return `$this$shl` shl other;
}

internal inline infix fun Byte.and(other: Int): Int {
   return `$this$and` and other;
}

internal inline infix fun Byte.and(other: Long): Long {
   return (long)`$this$and` and other;
}

internal inline infix fun Byte.xor(other: Byte): Byte {
   return (byte)(`$this$xor` xor other);
}

internal inline infix fun Int.and(other: Long): Long {
   return (long)`$this$and` and other;
}

internal inline fun minOf(a: Long, b: Int): Long {
   return Math.min(a, (long)b);
}

internal inline fun minOf(a: Int, b: Long): Long {
   return Math.min((long)a, b);
}

internal fun arrayRangeEquals(a: ByteArray, aOffset: Int, b: ByteArray, bOffset: Int, byteCount: Int): Boolean {
   for (int i = 0; i < byteCount; i++) {
      if (a[i + aOffset] != b[i + bOffset]) {
         return false;
      }
   }

   return true;
}

internal fun Byte.toHexString(): String {
   return StringsKt.concatToString(
      new char[]{-ByteString.getHEX_DIGIT_CHARS()[`$this$toHexString` shr 4 and 15], -ByteString.getHEX_DIGIT_CHARS()[`$this$toHexString` and 15]}
   );
}

internal fun Int.toHexString(): String {
   if (`$this$toHexString` == 0) {
      return "0";
   } else {
      var result: CharArray = new char[]{
         -ByteString.getHEX_DIGIT_CHARS()[`$this$toHexString` shr 28 and 15],
         -ByteString.getHEX_DIGIT_CHARS()[`$this$toHexString` shr 24 and 15],
         -ByteString.getHEX_DIGIT_CHARS()[`$this$toHexString` shr 20 and 15],
         -ByteString.getHEX_DIGIT_CHARS()[`$this$toHexString` shr 16 and 15],
         -ByteString.getHEX_DIGIT_CHARS()[`$this$toHexString` shr 12 and 15],
         -ByteString.getHEX_DIGIT_CHARS()[`$this$toHexString` shr 8 and 15],
         -ByteString.getHEX_DIGIT_CHARS()[`$this$toHexString` shr 4 and 15],
         -ByteString.getHEX_DIGIT_CHARS()[`$this$toHexString` and 15]
      };
      var i: Int = 0;

      while (i < result.length && result[i] == '0') {
         i++;
      }

      return StringsKt.concatToString(result, i, result.length);
   }
}

internal fun Long.toHexString(): String {
   if (`$this$toHexString` == 0L) {
      return "0";
   } else {
      var result: CharArray = new char[]{
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 60 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 56 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 52 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 48 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 44 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 40 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 36 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 32 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 28 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 24 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 20 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 16 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 12 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 8 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` shr 4 and 15L)],
         -ByteString.getHEX_DIGIT_CHARS()[(int)(`$this$toHexString` and 15L)]
      };
      var i: Int = 0;

      while (i < result.length && result[i] == '0') {
         i++;
      }

      return StringsKt.concatToString(result, i, result.length);
   }
}

/** @deprecated */
@JvmSynthetic
fun `getDEFAULT__new_UnsafeCursor$annotations`() {
}

internal fun resolveDefaultParameter(unsafeCursor: UnsafeCursor): UnsafeCursor {
   return if (unsafeCursor === DEFAULT__new_UnsafeCursor) new Buffer.UnsafeCursor() else unsafeCursor;
}

internal fun ByteString.resolveDefaultParameter(position: Int): Int {
   return if (position == DEFAULT__ByteString_size) `$this$resolveDefaultParameter`.size() else position;
}

internal fun ByteArray.resolveDefaultParameter(sizeParam: Int): Int {
   return if (sizeParam == DEFAULT__ByteString_size) `$this$resolveDefaultParameter`.length else sizeParam;
}

