package kotlin

import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class NumbersKt__NumbersKt : NumbersKt__NumbersJVMKt {
   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Byte.countOneBits(): Int {
      return Integer.bitCount(`$this$countOneBits` and 255);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Byte.countLeadingZeroBits(): Int {
      return Integer.numberOfLeadingZeros(`$this$countLeadingZeroBits` and 255) - 24;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Byte.countTrailingZeroBits(): Int {
      return Integer.numberOfTrailingZeros(`$this$countTrailingZeroBits` or 256);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Byte.takeHighestOneBit(): Byte {
      return (byte)Integer.highestOneBit(`$this$takeHighestOneBit` and 255);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Byte.takeLowestOneBit(): Byte {
      return (byte)Integer.lowestOneBit(`$this$takeLowestOneBit`);
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Byte.rotateLeft(bitCount: Int): Byte {
      return (byte)(`$this$rotateLeft` shl (bitCount and 7) or (`$this$rotateLeft` and 255) ushr 8 - (bitCount and 7));
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Byte.rotateRight(bitCount: Int): Byte {
      return (byte)(`$this$rotateRight` shl 8 - (bitCount and 7) or (`$this$rotateRight` and 255) ushr (bitCount and 7));
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Short.countOneBits(): Int {
      return Integer.bitCount(`$this$countOneBits` and 65535);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Short.countLeadingZeroBits(): Int {
      return Integer.numberOfLeadingZeros(`$this$countLeadingZeroBits` and 65535) - 16;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Short.countTrailingZeroBits(): Int {
      return Integer.numberOfTrailingZeros(`$this$countTrailingZeroBits` or 65536);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Short.takeHighestOneBit(): Short {
      return (short)Integer.highestOneBit(`$this$takeHighestOneBit` and '\uffff');
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun Short.takeLowestOneBit(): Short {
      return (short)Integer.lowestOneBit(`$this$takeLowestOneBit`);
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Short.rotateLeft(bitCount: Int): Short {
      return (short)(`$this$rotateLeft` shl (bitCount and 15) or (`$this$rotateLeft` and '\uffff') ushr 16 - (bitCount and 15));
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun Short.rotateRight(bitCount: Int): Short {
      return (short)(`$this$rotateRight` shl 16 - (bitCount and 15) or (`$this$rotateRight` and '\uffff') ushr (bitCount and 15));
   }

   open fun NumbersKt__NumbersKt() {
   }
}
