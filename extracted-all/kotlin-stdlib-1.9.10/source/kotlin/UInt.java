package kotlin

import kotlin.internal.InlineOnly
import kotlin.internal.IntrinsicConstEvaluation

@JvmInline
@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
inline class UInt : java.lang.Comparable<UInt> {
   @JvmStatic
   public UInt.Companion Companion = new UInt.Companion(null);
   @JvmStatic
   public int MIN_VALUE = 0;
   @JvmStatic
   public int MAX_VALUE = -1;
   @JvmStatic
   public int SIZE_BYTES = 4;
   @JvmStatic
   public int SIZE_BITS = 32;

   internal final val data: Int

   @InlineOnly
   @JvmStatic
   public inline operator fun compareTo(other: UByte): Int {
      return Integer.compareUnsigned(arg0, constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun compareTo(other: UShort): Int {
      return Integer.compareUnsigned(arg0, constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public open inline operator fun compareTo(other: UInt): Int {
      return UnsignedKt.uintCompare(arg0, other);
   }

   @InlineOnly
   fun `compareTo-WZ4Q5Ns`(other: Int): Int {
      return UnsignedKt.uintCompare(this.unbox-impl(), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun compareTo(other: ULong): Int {
      return java.lang.Long.compareUnsigned(ULong.constructor-impl((long)arg0 and 4294967295L), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: UByte): UInt {
      return constructor-impl(arg0 + constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: UShort): UInt {
      return constructor-impl(arg0 + constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: UInt): UInt {
      return constructor-impl(arg0 + other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: ULong): ULong {
      return ULong.constructor-impl(ULong.constructor-impl((long)arg0 and 4294967295L) + other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: UByte): UInt {
      return constructor-impl(arg0 - constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: UShort): UInt {
      return constructor-impl(arg0 - constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: UInt): UInt {
      return constructor-impl(arg0 - other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: ULong): ULong {
      return ULong.constructor-impl(ULong.constructor-impl((long)arg0 and 4294967295L) - other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: UByte): UInt {
      return constructor-impl(arg0 * constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: UShort): UInt {
      return constructor-impl(arg0 * constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: UInt): UInt {
      return constructor-impl(arg0 * other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: ULong): ULong {
      return ULong.constructor-impl(ULong.constructor-impl((long)arg0 and 4294967295L) * other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: UByte): UInt {
      return Integer.divideUnsigned(arg0, constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: UShort): UInt {
      return Integer.divideUnsigned(arg0, constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: UInt): UInt {
      return UnsignedKt.uintDivide-J1ME1BU(arg0, other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: ULong): ULong {
      return java.lang.Long.divideUnsigned(ULong.constructor-impl((long)arg0 and 4294967295L), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: UByte): UInt {
      return Integer.remainderUnsigned(arg0, constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: UShort): UInt {
      return Integer.remainderUnsigned(arg0, constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: UInt): UInt {
      return UnsignedKt.uintRemainder-J1ME1BU(arg0, other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: ULong): ULong {
      return java.lang.Long.remainderUnsigned(ULong.constructor-impl((long)arg0 and 4294967295L), other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: UByte): UInt {
      return Integer.divideUnsigned(arg0, constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: UShort): UInt {
      return Integer.divideUnsigned(arg0, constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: UInt): UInt {
      return Integer.divideUnsigned(arg0, other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: ULong): ULong {
      return java.lang.Long.divideUnsigned(ULong.constructor-impl((long)arg0 and 4294967295L), other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: UByte): UByte {
      return UByte.constructor-impl((byte)Integer.remainderUnsigned(arg0, constructor-impl(other and 255)));
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: UShort): UShort {
      return UShort.constructor-impl((short)Integer.remainderUnsigned(arg0, constructor-impl(other and '\uffff')));
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: UInt): UInt {
      return Integer.remainderUnsigned(arg0, other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: ULong): ULong {
      return java.lang.Long.remainderUnsigned(ULong.constructor-impl((long)arg0 and 4294967295L), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun inc(): UInt {
      return constructor-impl(arg0 + 1);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun dec(): UInt {
      return constructor-impl(arg0 + -1);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rangeTo(other: UInt): UIntRange {
      return new UIntRange(arg0, other, null);
   }

   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline operator fun rangeUntil(other: UInt): UIntRange {
      return URangesKt.until-J1ME1BU(arg0, other);
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun shl(bitCount: Int): UInt {
      return constructor-impl(arg0 shl bitCount);
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun shr(bitCount: Int): UInt {
      return constructor-impl(arg0 ushr bitCount);
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun and(other: UInt): UInt {
      return constructor-impl(arg0 and other);
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun or(other: UInt): UInt {
      return constructor-impl(arg0 or other);
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun xor(other: UInt): UInt {
      return constructor-impl(arg0 xor other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun inv(): UInt {
      return constructor-impl(arg0.inv());
   }

   @InlineOnly
   @JvmStatic
   public inline fun toByte(): Byte {
      return (byte)arg0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toShort(): Short {
      return (short)arg0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toInt(): Int {
      return arg0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toLong(): Long {
      return (long)arg0 and 4294967295L;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toUByte(): UByte {
      return UByte.constructor-impl((byte)arg0);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toUShort(): UShort {
      return UShort.constructor-impl((short)arg0);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toUInt(): UInt {
      return arg0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toULong(): ULong {
      return ULong.constructor-impl((long)arg0 and 4294967295L);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toFloat(): Float {
      return (float)UnsignedKt.uintToDouble(arg0);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toDouble(): Double {
      return UnsignedKt.uintToDouble(arg0);
   }

   @JvmStatic
   public open fun toString(): String {
      return java.lang.String.valueOf((long)arg0 and 4294967295L);
   }

   override fun toString(): java.lang.String {
      return toString-impl(this.data);
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return Integer.hashCode(arg0);
   }

   override fun hashCode(): Int {
      return hashCode-impl(this.data);
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      if (other !is UInt) {
         return false;
      } else {
         return arg0 == (other as UInt).unbox-impl();
      }
   }

   override fun equals(other: Any): Boolean {
      return equals-impl(this.data, other);
   }

   @IntrinsicConstEvaluation
   @PublishedApi
   @JvmStatic
   fun `constructor-impl`(data: Int): Int {
      return data;
   }

   @JvmStatic
   fun `equals-impl0`(p1: Int, p2: Int): Boolean {
      return p1 == p2;
   }

   companion object Companion private constructor() {
      public const val MAX_VALUE: UInt
      public const val MIN_VALUE: UInt
      public const val SIZE_BITS: Int
      public const val SIZE_BYTES: Int

   }
}
