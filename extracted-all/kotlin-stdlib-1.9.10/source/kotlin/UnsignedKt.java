@file:JvmName(name = "UnsignedKt")

package kotlin

import kotlin.jvm.internal.Intrinsics

@PublishedApi
internal fun uintCompare(v1: Int, v2: Int): Int {
   return Intrinsics.compare(v1 xor Integer.MIN_VALUE, v2 xor Integer.MIN_VALUE);
}

@PublishedApi
internal fun ulongCompare(v1: Long, v2: Long): Int {
   return Intrinsics.compare(v1 xor java.lang.Long.MIN_VALUE, v2 xor java.lang.Long.MIN_VALUE);
}

@PublishedApi
internal fun uintDivide(v1: UInt, v2: UInt): UInt {
   return UInt.constructor-impl((int)(((long)v1 and 4294967295L) / ((long)v2 and 4294967295L)));
}

@PublishedApi
internal fun uintRemainder(v1: UInt, v2: UInt): UInt {
   return UInt.constructor-impl((int)(((long)v1 and 4294967295L) % ((long)v2 and 4294967295L)));
}

@PublishedApi
internal fun ulongDivide(v1: ULong, v2: ULong): ULong {
   label21:
   if (v2 < 0L) {
      return if (java.lang.Long.compareUnsigned(v1, v2) < 0) ULong.constructor-impl(0L) else ULong.constructor-impl(1L);
   } else {
      return if (v1 >= 0L)
         ULong.constructor-impl(v1 / v2)
         else
         ULong.constructor-impl(
            ((v1 ushr 1) / v2 shl 1)
               + (long)(
                  if (java.lang.Long.compareUnsigned(ULong.constructor-impl(v1 - ((v1 ushr 1) / v2 shl 1) * v2), ULong.constructor-impl(v2)) >= 0) 1 else 0
               )
         );
   }
}

@PublishedApi
internal fun ulongRemainder(v1: ULong, v2: ULong): ULong {
   label21:
   if (v2 < 0L) {
      return if (java.lang.Long.compareUnsigned(v1, v2) < 0) v1 else ULong.constructor-impl(v1 - v2);
   } else {
      return if (v1 >= 0L)
         ULong.constructor-impl(v1 % v2)
         else
         ULong.constructor-impl(
            v1
               - ((v1 ushr 1) / v2 shl 1) * v2
               - (if (java.lang.Long.compareUnsigned(ULong.constructor-impl(v1 - ((v1 ushr 1) / v2 shl 1) * v2), ULong.constructor-impl(v2)) >= 0) v2 else 0L)
         );
   }
}

@PublishedApi
internal fun doubleToUInt(v: Double): UInt {
   return if (java.lang.Double.isNaN(v))
      0
      else
      (
         if (v <= uintToDouble(0))
            0
            else
            (
               if (v >= uintToDouble(-1))
                  -1
                  else
                  (
                     if (v <= 2.147483647E9)
                        UInt.constructor-impl((int)v)
                        else
                        UInt.constructor-impl(UInt.constructor-impl((int)(v - (double)Integer.MAX_VALUE)) + UInt.constructor-impl(Integer.MAX_VALUE))
                  )
            )
      );
}

@PublishedApi
internal fun doubleToULong(v: Double): ULong {
   return if (java.lang.Double.isNaN(v))
      0L
      else
      (
         if (v <= ulongToDouble(0L))
            0L
            else
            (
               if (v >= ulongToDouble(-1L))
                  -1L
                  else
                  (
                     if (v < 9.223372E18F)
                        ULong.constructor-impl((long)v)
                        else
                        ULong.constructor-impl(ULong.constructor-impl((long)(v - 9.223372E18F)) + java.lang.Long.MIN_VALUE)
                  )
            )
      );
}

@PublishedApi
internal fun uintToDouble(v: Int): Double {
   return (double)(v and Integer.MAX_VALUE) + (double)((v ushr 31) shl 30) * (double)2;
}

@PublishedApi
internal fun ulongToDouble(v: Long): Double {
   return (double)(v ushr 11) * (double)2048 + (double)(v and 2047L);
}

internal fun ulongToString(v: Long): String {
   return ulongToString(v, 10);
}

internal fun ulongToString(v: Long, base: Int): String {
   if (v >= 0L) {
      var var8: java.lang.String = java.lang.Long.toString(v, CharsKt.checkRadix(base));
      return var8;
   } else {
      var quotient: Long = (v ushr 1) / (long)base shl 1;
      var rem: Long = v - ((v ushr 1) / (long)base shl 1) * (long)base;
      if (v - ((v ushr 1) / (long)base shl 1) * (long)base >= (long)base) {
         rem -= (long)base;
         quotient++;
      }

      var var10000: StringBuilder = new StringBuilder();
      var var10001: java.lang.String = java.lang.Long.toString(quotient, CharsKt.checkRadix(base));
      var10000 = var10000.append(var10001);
      var10001 = java.lang.Long.toString(rem, CharsKt.checkRadix(base));
      return var10000.append(var10001).toString();
   }
}

