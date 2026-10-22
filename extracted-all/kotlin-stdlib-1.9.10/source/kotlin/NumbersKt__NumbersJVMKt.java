package kotlin

import kotlin.Double.Companion
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class NumbersKt__NumbersJVMKt : NumbersKt__FloorDivModKt {
   @InlineOnly
   @JvmStatic
   public inline fun Double.isNaN(): Boolean {
      return java.lang.Double.isNaN(`$this$isNaN`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Float.isNaN(): Boolean {
      return java.lang.Float.isNaN(`$this$isNaN`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Double.isInfinite(): Boolean {
      return java.lang.Double.isInfinite(`$this$isInfinite`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Float.isInfinite(): Boolean {
      return java.lang.Float.isInfinite(`$this$isInfinite`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Double.isFinite(): Boolean {
      return !java.lang.Double.isInfinite(`$this$isFinite`) && !java.lang.Double.isNaN(`$this$isFinite`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun Float.isFinite(): Boolean {
      return !java.lang.Float.isInfinite(`$this$isFinite`) && !java.lang.Float.isNaN(`$this$isFinite`);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.toBits(): Long {
      return java.lang.Double.doubleToLongBits(`$this$toBits`);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.toRawBits(): Long {
      return java.lang.Double.doubleToRawLongBits(`$this$toRawBits`);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Companion.fromBits(bits: Long): Double {
      return java.lang.Double.longBitsToDouble(bits);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.toBits(): Int {
      return java.lang.Float.floatToIntBits(`$this$toBits`);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.toRawBits(): Int {
      return java.lang.Float.floatToRawIntBits(`$this$toRawBits`);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun kotlin.Float.Companion.fromBits(bits: Int): Float {
      return java.lang.Float.intBitsToFloat(bits);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Int.countOneBits(): Int {
      return Integer.bitCount(`$this$countOneBits`);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Int.countLeadingZeroBits(): Int {
      return Integer.numberOfLeadingZeros(`$this$countLeadingZeroBits`);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Int.countTrailingZeroBits(): Int {
      return Integer.numberOfTrailingZeros(`$this$countTrailingZeroBits`);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Int.takeHighestOneBit(): Int {
      return Integer.highestOneBit(`$this$takeHighestOneBit`);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Int.takeLowestOneBit(): Int {
      return Integer.lowestOneBit(`$this$takeLowestOneBit`);
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Int.rotateLeft(bitCount: Int): Int {
      return Integer.rotateLeft(`$this$rotateLeft`, bitCount);
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Int.rotateRight(bitCount: Int): Int {
      return Integer.rotateRight(`$this$rotateRight`, bitCount);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Long.countOneBits(): Int {
      return java.lang.Long.bitCount(`$this$countOneBits`);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Long.countLeadingZeroBits(): Int {
      return java.lang.Long.numberOfLeadingZeros(`$this$countLeadingZeroBits`);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Long.countTrailingZeroBits(): Int {
      return java.lang.Long.numberOfTrailingZeros(`$this$countTrailingZeroBits`);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Long.takeHighestOneBit(): Long {
      return java.lang.Long.highestOneBit(`$this$takeHighestOneBit`);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Long.takeLowestOneBit(): Long {
      return java.lang.Long.lowestOneBit(`$this$takeLowestOneBit`);
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Long.rotateLeft(bitCount: Int): Long {
      return java.lang.Long.rotateLeft(`$this$rotateLeft`, bitCount);
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Long.rotateRight(bitCount: Int): Long {
      return java.lang.Long.rotateRight(`$this$rotateRight`, bitCount);
   }

   open fun NumbersKt__NumbersJVMKt() {
   }
}
