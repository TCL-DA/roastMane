package kotlin

import kotlin.internal.InlineOnly
import kotlin.internal.IntrinsicConstEvaluation
import kotlin.jvm.internal.Intrinsics

@JvmInline
@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
inline class UShort : java.lang.Comparable<UShort> {
   @JvmStatic
   public UShort.Companion Companion = new UShort.Companion(null);
   @JvmStatic
   public short MIN_VALUE = 0;
   @JvmStatic
   public short MAX_VALUE = -1;
   @JvmStatic
   public int SIZE_BYTES = 2;
   @JvmStatic
   public int SIZE_BITS = 16;

   internal final val data: Short

   @InlineOnly
   @JvmStatic
   public inline operator fun compareTo(other: UByte): Int {
      return Intrinsics.compare(arg0 and 65535, other and 255);
   }

   @InlineOnly
   @JvmStatic
   public open inline operator fun compareTo(other: UShort): Int {
      return Intrinsics.compare(arg0 and 65535, other and 65535);
   }

   @InlineOnly
   fun `compareTo-xj2QHRw`(other: Short): Int {
      return Intrinsics.compare(this.unbox-impl() and 65535, other and 65535);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun compareTo(other: UInt): Int {
      return Integer.compareUnsigned(UInt.constructor-impl(arg0 and 65535), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun compareTo(other: ULong): Int {
      return java.lang.Long.compareUnsigned(ULong.constructor-impl((long)arg0 and 65535L), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: UByte): UInt {
      return UInt.constructor-impl(UInt.constructor-impl(arg0 and 65535) + UInt.constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: UShort): UInt {
      return UInt.constructor-impl(UInt.constructor-impl(arg0 and 65535) + UInt.constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: UInt): UInt {
      return UInt.constructor-impl(UInt.constructor-impl(arg0 and 65535) + other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: ULong): ULong {
      return ULong.constructor-impl(ULong.constructor-impl((long)arg0 and 65535L) + other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: UByte): UInt {
      return UInt.constructor-impl(UInt.constructor-impl(arg0 and 65535) - UInt.constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: UShort): UInt {
      return UInt.constructor-impl(UInt.constructor-impl(arg0 and 65535) - UInt.constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: UInt): UInt {
      return UInt.constructor-impl(UInt.constructor-impl(arg0 and 65535) - other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: ULong): ULong {
      return ULong.constructor-impl(ULong.constructor-impl((long)arg0 and 65535L) - other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: UByte): UInt {
      return UInt.constructor-impl(UInt.constructor-impl(arg0 and 65535) * UInt.constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: UShort): UInt {
      return UInt.constructor-impl(UInt.constructor-impl(arg0 and 65535) * UInt.constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: UInt): UInt {
      return UInt.constructor-impl(UInt.constructor-impl(arg0 and 65535) * other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: ULong): ULong {
      return ULong.constructor-impl(ULong.constructor-impl((long)arg0 and 65535L) * other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: UByte): UInt {
      return Integer.divideUnsigned(UInt.constructor-impl(arg0 and 65535), UInt.constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: UShort): UInt {
      return Integer.divideUnsigned(UInt.constructor-impl(arg0 and 65535), UInt.constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: UInt): UInt {
      return Integer.divideUnsigned(UInt.constructor-impl(arg0 and 65535), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: ULong): ULong {
      return java.lang.Long.divideUnsigned(ULong.constructor-impl((long)arg0 and 65535L), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: UByte): UInt {
      return Integer.remainderUnsigned(UInt.constructor-impl(arg0 and 65535), UInt.constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: UShort): UInt {
      return Integer.remainderUnsigned(UInt.constructor-impl(arg0 and 65535), UInt.constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: UInt): UInt {
      return Integer.remainderUnsigned(UInt.constructor-impl(arg0 and 65535), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: ULong): ULong {
      return java.lang.Long.remainderUnsigned(ULong.constructor-impl((long)arg0 and 65535L), other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: UByte): UInt {
      return Integer.divideUnsigned(UInt.constructor-impl(arg0 and 65535), UInt.constructor-impl(other and 255));
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: UShort): UInt {
      return Integer.divideUnsigned(UInt.constructor-impl(arg0 and 65535), UInt.constructor-impl(other and 65535));
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: UInt): UInt {
      return Integer.divideUnsigned(UInt.constructor-impl(arg0 and 65535), other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: ULong): ULong {
      return java.lang.Long.divideUnsigned(ULong.constructor-impl((long)arg0 and 65535L), other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: UByte): UByte {
      return UByte.constructor-impl((byte)Integer.remainderUnsigned(UInt.constructor-impl(arg0 and '\uffff'), UInt.constructor-impl(other and 255)));
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: UShort): UShort {
      return constructor-impl((short)Integer.remainderUnsigned(UInt.constructor-impl(arg0 and '\uffff'), UInt.constructor-impl(other and '\uffff')));
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: UInt): UInt {
      return Integer.remainderUnsigned(UInt.constructor-impl(arg0 and 65535), other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: ULong): ULong {
      return java.lang.Long.remainderUnsigned(ULong.constructor-impl((long)arg0 and 65535L), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun inc(): UShort {
      return constructor-impl((short)(arg0 + 1));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun dec(): UShort {
      return constructor-impl((short)(arg0 + -1));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rangeTo(other: UShort): UIntRange {
      return new UIntRange(UInt.constructor-impl(arg0 and '\uffff'), UInt.constructor-impl(other and '\uffff'), null);
   }

   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline operator fun rangeUntil(other: UShort): UIntRange {
      return URangesKt.until-J1ME1BU(UInt.constructor-impl(arg0 and '\uffff'), UInt.constructor-impl(other and '\uffff'));
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun and(other: UShort): UShort {
      return constructor-impl((short)(arg0 and other));
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun or(other: UShort): UShort {
      return constructor-impl((short)(arg0 or other));
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun xor(other: UShort): UShort {
      return constructor-impl((short)(arg0 xor other));
   }

   @InlineOnly
   @JvmStatic
   public inline fun inv(): UShort {
      return constructor-impl((short)(arg0.inv()));
   }

   @InlineOnly
   @JvmStatic
   public inline fun toByte(): Byte {
      return (byte)arg0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toShort(): Short {
      return arg0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toInt(): Int {
      return arg0 and 65535;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toLong(): Long {
      return (long)arg0 and 65535L;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toUByte(): UByte {
      return UByte.constructor-impl((byte)arg0);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toUShort(): UShort {
      return arg0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toUInt(): UInt {
      return UInt.constructor-impl(arg0 and 65535);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toULong(): ULong {
      return ULong.constructor-impl((long)arg0 and 65535L);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toFloat(): Float {
      return (float)(arg0 and '\uffff');
   }

   @InlineOnly
   @JvmStatic
   public inline fun toDouble(): Double {
      return (double)(arg0 and '\uffff');
   }

   @JvmStatic
   public open fun toString(): String {
      return java.lang.String.valueOf(arg0 and '\uffff');
   }

   override fun toString(): java.lang.String {
      return toString-impl(this.data);
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return java.lang.Short.hashCode(arg0);
   }

   override fun hashCode(): Int {
      return hashCode-impl(this.data);
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      if (other !is UShort) {
         return false;
      } else {
         return arg0 == (other as UShort).unbox-impl();
      }
   }

   override fun equals(other: Any): Boolean {
      return equals-impl(this.data, other);
   }

   @IntrinsicConstEvaluation
   @PublishedApi
   @JvmStatic
   fun `constructor-impl`(data: Short): Short {
      return data;
   }

   @JvmStatic
   fun `equals-impl0`(p1: Short, p2: Short): Boolean {
      return p1 == p2;
   }

   companion object Companion private constructor() {
      public const val MAX_VALUE: UShort
      public const val MIN_VALUE: UShort
      public const val SIZE_BITS: Int
      public const val SIZE_BYTES: Int

   }
}
