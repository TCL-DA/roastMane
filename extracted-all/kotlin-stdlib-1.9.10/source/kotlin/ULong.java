package kotlin

import kotlin.internal.InlineOnly
import kotlin.internal.IntrinsicConstEvaluation

@JvmInline
@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
inline class ULong : java.lang.Comparable<ULong> {
   @JvmStatic
   public ULong.Companion Companion = new ULong.Companion(null);
   @JvmStatic
   public long MIN_VALUE = 0L;
   @JvmStatic
   public long MAX_VALUE = -1L;
   @JvmStatic
   public int SIZE_BYTES = 8;
   @JvmStatic
   public int SIZE_BITS = 64;

   internal final val data: Long

   @InlineOnly
   @JvmStatic
   public inline operator fun compareTo(other: UByte): Int {
      return java.lang.Long.compareUnsigned(arg0, constructor-impl((long)other and 255L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun compareTo(other: UShort): Int {
      return java.lang.Long.compareUnsigned(arg0, constructor-impl((long)other and 65535L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun compareTo(other: UInt): Int {
      return java.lang.Long.compareUnsigned(arg0, constructor-impl((long)other and 4294967295L));
   }

   @InlineOnly
   @JvmStatic
   public open inline operator fun compareTo(other: ULong): Int {
      return UnsignedKt.ulongCompare(arg0, other);
   }

   @InlineOnly
   fun `compareTo-VKZWuLQ`(other: Long): Int {
      return UnsignedKt.ulongCompare(this.unbox-impl(), other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: UByte): ULong {
      return constructor-impl(arg0 + constructor-impl((long)other and 255L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: UShort): ULong {
      return constructor-impl(arg0 + constructor-impl((long)other and 65535L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: UInt): ULong {
      return constructor-impl(arg0 + constructor-impl((long)other and 4294967295L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun plus(other: ULong): ULong {
      return constructor-impl(arg0 + other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: UByte): ULong {
      return constructor-impl(arg0 - constructor-impl((long)other and 255L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: UShort): ULong {
      return constructor-impl(arg0 - constructor-impl((long)other and 65535L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: UInt): ULong {
      return constructor-impl(arg0 - constructor-impl((long)other and 4294967295L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun minus(other: ULong): ULong {
      return constructor-impl(arg0 - other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: UByte): ULong {
      return constructor-impl(arg0 * constructor-impl((long)other and 255L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: UShort): ULong {
      return constructor-impl(arg0 * constructor-impl((long)other and 65535L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: UInt): ULong {
      return constructor-impl(arg0 * constructor-impl((long)other and 4294967295L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun times(other: ULong): ULong {
      return constructor-impl(arg0 * other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: UByte): ULong {
      return java.lang.Long.divideUnsigned(arg0, constructor-impl((long)other and 255L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: UShort): ULong {
      return java.lang.Long.divideUnsigned(arg0, constructor-impl((long)other and 65535L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: UInt): ULong {
      return java.lang.Long.divideUnsigned(arg0, constructor-impl((long)other and 4294967295L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun div(other: ULong): ULong {
      return UnsignedKt.ulongDivide-eb3DHEI(arg0, other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: UByte): ULong {
      return java.lang.Long.remainderUnsigned(arg0, constructor-impl((long)other and 255L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: UShort): ULong {
      return java.lang.Long.remainderUnsigned(arg0, constructor-impl((long)other and 65535L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: UInt): ULong {
      return java.lang.Long.remainderUnsigned(arg0, constructor-impl((long)other and 4294967295L));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rem(other: ULong): ULong {
      return UnsignedKt.ulongRemainder-eb3DHEI(arg0, other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: UByte): ULong {
      return java.lang.Long.divideUnsigned(arg0, constructor-impl((long)other and 255L));
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: UShort): ULong {
      return java.lang.Long.divideUnsigned(arg0, constructor-impl((long)other and 65535L));
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: UInt): ULong {
      return java.lang.Long.divideUnsigned(arg0, constructor-impl((long)other and 4294967295L));
   }

   @InlineOnly
   @JvmStatic
   public inline fun floorDiv(other: ULong): ULong {
      return java.lang.Long.divideUnsigned(arg0, other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: UByte): UByte {
      return UByte.constructor-impl((byte)((int)java.lang.Long.remainderUnsigned(arg0, constructor-impl((long)other and 255L))));
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: UShort): UShort {
      return UShort.constructor-impl((short)((int)java.lang.Long.remainderUnsigned(arg0, constructor-impl((long)other and 65535L))));
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: UInt): UInt {
      return UInt.constructor-impl((int)java.lang.Long.remainderUnsigned(arg0, constructor-impl((long)other and 4294967295L)));
   }

   @InlineOnly
   @JvmStatic
   public inline fun mod(other: ULong): ULong {
      return java.lang.Long.remainderUnsigned(arg0, other);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun inc(): ULong {
      return constructor-impl(arg0 + 1L);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun dec(): ULong {
      return constructor-impl(arg0 + -1L);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun rangeTo(other: ULong): ULongRange {
      return new ULongRange(arg0, other, null);
   }

   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline operator fun rangeUntil(other: ULong): ULongRange {
      return URangesKt.until-eb3DHEI(arg0, other);
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun shl(bitCount: Int): ULong {
      return constructor-impl(arg0 shl bitCount);
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun shr(bitCount: Int): ULong {
      return constructor-impl(arg0 ushr bitCount);
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun and(other: ULong): ULong {
      return constructor-impl(arg0 and other);
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun or(other: ULong): ULong {
      return constructor-impl(arg0 or other);
   }

   @InlineOnly
   @JvmStatic
   public inline infix fun xor(other: ULong): ULong {
      return constructor-impl(arg0 xor other);
   }

   @InlineOnly
   @JvmStatic
   public inline fun inv(): ULong {
      return constructor-impl(arg0.inv());
   }

   @InlineOnly
   @JvmStatic
   public inline fun toByte(): Byte {
      return (byte)((int)arg0);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toShort(): Short {
      return (short)((int)arg0);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toInt(): Int {
      return (int)arg0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toLong(): Long {
      return arg0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toUByte(): UByte {
      return UByte.constructor-impl((byte)((int)arg0));
   }

   @InlineOnly
   @JvmStatic
   public inline fun toUShort(): UShort {
      return UShort.constructor-impl((short)((int)arg0));
   }

   @InlineOnly
   @JvmStatic
   public inline fun toUInt(): UInt {
      return UInt.constructor-impl((int)arg0);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toULong(): ULong {
      return arg0;
   }

   @InlineOnly
   @JvmStatic
   public inline fun toFloat(): Float {
      return (float)UnsignedKt.ulongToDouble(arg0);
   }

   @InlineOnly
   @JvmStatic
   public inline fun toDouble(): Double {
      return UnsignedKt.ulongToDouble(arg0);
   }

   @JvmStatic
   public open fun toString(): String {
      return UnsignedKt.ulongToString(arg0);
   }

   override fun toString(): java.lang.String {
      return toString-impl(this.data);
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return java.lang.Long.hashCode(arg0);
   }

   override fun hashCode(): Int {
      return hashCode-impl(this.data);
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      if (other !is ULong) {
         return false;
      } else {
         return arg0 == (other as ULong).unbox-impl();
      }
   }

   override fun equals(other: Any): Boolean {
      return equals-impl(this.data, other);
   }

   @IntrinsicConstEvaluation
   @PublishedApi
   @JvmStatic
   fun `constructor-impl`(data: Long): Long {
      return data;
   }

   @JvmStatic
   fun `equals-impl0`(p1: Long, p2: Long): Boolean {
      return p1 == p2;
   }

   companion object Companion private constructor() {
      public const val MAX_VALUE: ULong
      public const val MIN_VALUE: ULong
      public const val SIZE_BITS: Int
      public const val SIZE_BYTES: Int

   }
}
