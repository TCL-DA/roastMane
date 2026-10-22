package kotlin

import kotlin.internal.InlineOnly
import kotlin.internal.IntrinsicConstEvaluation

// $VF: Class flags could not be determined
internal class NumbersKt__FloorDivModKt : NumbersKt__BigIntegersKt {
   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Byte.floorDiv(other: Byte): Int {
      var var4: Int = `$this$floorDiv` / other;
      if ((`$this$floorDiv` xor other) < 0 && `$this$floorDiv` / other * other != `$this$floorDiv`) {
         var4--;
      }

      return var4;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Byte.mod(other: Byte): Byte {
      return (byte)(`$this$mod` % other + (other and ((`$this$mod` % other xor other) and (`$this$mod` % other or -(`$this$mod` % other))) shr 31));
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Byte.floorDiv(other: Short): Int {
      var var4: Int = `$this$floorDiv` / other;
      if ((`$this$floorDiv` xor other) < 0 && `$this$floorDiv` / other * other != `$this$floorDiv`) {
         var4--;
      }

      return var4;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Byte.mod(other: Short): Short {
      return (short)(`$this$mod` % other + (other and ((`$this$mod` % other xor other) and (`$this$mod` % other or -(`$this$mod` % other))) shr 31));
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Byte.floorDiv(other: Int): Int {
      var var3: Int = `$this$floorDiv` / other;
      if ((`$this$floorDiv` xor other) < 0 && `$this$floorDiv` / other * other != `$this$floorDiv`) {
         var3--;
      }

      return var3;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Byte.mod(other: Int): Int {
      return `$this$mod` % other + (other and ((`$this$mod` % other xor other) and (`$this$mod` % other or -(`$this$mod` % other))) shr 31);
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Byte.floorDiv(other: Long): Long {
      var var3: Long = (long)`$this$floorDiv`;
      var var5: Long = (long)`$this$floorDiv` / other;
      if ((var3 xor other) < 0L && (long)`$this$floorDiv` / other * other != var3) {
         var5 += -1L;
      }

      return var5;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Byte.mod(other: Long): Long {
      return (long)`$this$mod` % other
         + (other and (((long)`$this$mod` % other xor other) and ((long)`$this$mod` % other or -((long)`$this$mod` % other))) shr 63);
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Short.floorDiv(other: Byte): Int {
      var var4: Int = `$this$floorDiv` / other;
      if ((`$this$floorDiv` xor other) < 0 && `$this$floorDiv` / other * other != `$this$floorDiv`) {
         var4--;
      }

      return var4;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Short.mod(other: Byte): Byte {
      return (byte)(`$this$mod` % other + (other and ((`$this$mod` % other xor other) and (`$this$mod` % other or -(`$this$mod` % other))) shr 31));
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Short.floorDiv(other: Short): Int {
      var var4: Int = `$this$floorDiv` / other;
      if ((`$this$floorDiv` xor other) < 0 && `$this$floorDiv` / other * other != `$this$floorDiv`) {
         var4--;
      }

      return var4;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Short.mod(other: Short): Short {
      return (short)(`$this$mod` % other + (other and ((`$this$mod` % other xor other) and (`$this$mod` % other or -(`$this$mod` % other))) shr 31));
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Short.floorDiv(other: Int): Int {
      var var3: Int = `$this$floorDiv` / other;
      if ((`$this$floorDiv` xor other) < 0 && `$this$floorDiv` / other * other != `$this$floorDiv`) {
         var3--;
      }

      return var3;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Short.mod(other: Int): Int {
      return `$this$mod` % other + (other and ((`$this$mod` % other xor other) and (`$this$mod` % other or -(`$this$mod` % other))) shr 31);
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Short.floorDiv(other: Long): Long {
      var var3: Long = (long)`$this$floorDiv`;
      var var5: Long = (long)`$this$floorDiv` / other;
      if ((var3 xor other) < 0L && (long)`$this$floorDiv` / other * other != var3) {
         var5 += -1L;
      }

      return var5;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Short.mod(other: Long): Long {
      return (long)`$this$mod` % other
         + (other and (((long)`$this$mod` % other xor other) and ((long)`$this$mod` % other or -((long)`$this$mod` % other))) shr 63);
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Int.floorDiv(other: Byte): Int {
      var var4: Int = `$this$floorDiv` / other;
      if ((`$this$floorDiv` xor other) < 0 && `$this$floorDiv` / other * other != `$this$floorDiv`) {
         var4--;
      }

      return var4;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Int.mod(other: Byte): Byte {
      return (byte)(`$this$mod` % other + (other and ((`$this$mod` % other xor other) and (`$this$mod` % other or -(`$this$mod` % other))) shr 31));
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Int.floorDiv(other: Short): Int {
      var var4: Int = `$this$floorDiv` / other;
      if ((`$this$floorDiv` xor other) < 0 && `$this$floorDiv` / other * other != `$this$floorDiv`) {
         var4--;
      }

      return var4;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Int.mod(other: Short): Short {
      return (short)(`$this$mod` % other + (other and ((`$this$mod` % other xor other) and (`$this$mod` % other or -(`$this$mod` % other))) shr 31));
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Int.floorDiv(other: Int): Int {
      var q: Int = `$this$floorDiv` / other;
      if ((`$this$floorDiv` xor other) < 0 && `$this$floorDiv` / other * other != `$this$floorDiv`) {
         q--;
      }

      return q;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Int.mod(other: Int): Int {
      return `$this$mod` % other + (other and ((`$this$mod` % other xor other) and (`$this$mod` % other or -(`$this$mod` % other))) shr 31);
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Int.floorDiv(other: Long): Long {
      var var3: Long = (long)`$this$floorDiv`;
      var var5: Long = (long)`$this$floorDiv` / other;
      if ((var3 xor other) < 0L && (long)`$this$floorDiv` / other * other != var3) {
         var5 += -1L;
      }

      return var5;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Int.mod(other: Long): Long {
      return (long)`$this$mod` % other
         + (other and (((long)`$this$mod` % other xor other) and ((long)`$this$mod` % other or -((long)`$this$mod` % other))) shr 63);
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Long.floorDiv(other: Byte): Long {
      var var5: Long = (long)other;
      var var7: Long = `$this$floorDiv` / (long)other;
      if ((`$this$floorDiv` xor var5) < 0L && `$this$floorDiv` / (long)other * var5 != `$this$floorDiv`) {
         var7 += -1L;
      }

      return var7;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Long.mod(other: Byte): Byte {
      return (byte)(
         (int)(
            `$this$mod` % (long)other
               + ((long)other and ((`$this$mod` % (long)other xor (long)other) and (`$this$mod` % (long)other or -(`$this$mod` % (long)other))) shr 63)
         )
      );
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Long.floorDiv(other: Short): Long {
      var var5: Long = (long)other;
      var var7: Long = `$this$floorDiv` / (long)other;
      if ((`$this$floorDiv` xor var5) < 0L && `$this$floorDiv` / (long)other * var5 != `$this$floorDiv`) {
         var7 += -1L;
      }

      return var7;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Long.mod(other: Short): Short {
      return (short)(
         (int)(
            `$this$mod` % (long)other
               + ((long)other and ((`$this$mod` % (long)other xor (long)other) and (`$this$mod` % (long)other or -(`$this$mod` % (long)other))) shr 63)
         )
      );
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Long.floorDiv(other: Int): Long {
      var var5: Long = (long)other;
      var var7: Long = `$this$floorDiv` / (long)other;
      if ((`$this$floorDiv` xor var5) < 0L && `$this$floorDiv` / (long)other * var5 != `$this$floorDiv`) {
         var7 += -1L;
      }

      return var7;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Long.mod(other: Int): Int {
      return (int)(
         `$this$mod` % (long)other
            + ((long)other and ((`$this$mod` % (long)other xor (long)other) and (`$this$mod` % (long)other or -(`$this$mod` % (long)other))) shr 63)
      );
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Long.floorDiv(other: Long): Long {
      var q: Long = `$this$floorDiv` / other;
      if ((`$this$floorDiv` xor other) < 0L && `$this$floorDiv` / other * other != `$this$floorDiv`) {
         q += -1L;
      }

      return q;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Long.mod(other: Long): Long {
      return `$this$mod` % other + (other and ((`$this$mod` % other xor other) and (`$this$mod` % other or -(`$this$mod` % other))) shr 63);
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Float.mod(other: Float): Float {
      var r: Float = `$this$mod` % other;
      return if (`$this$mod` % other != 0.0F && Math.signum(`$this$mod` % other) != Math.signum(other)) `$this$mod` % other + other else `$this$mod` % other;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Float.mod(other: Double): Double {
      var var3: Double = (double)`$this$mod` % other;
      return if ((double)`$this$mod` % other != 0.0 && Math.signum((double)`$this$mod` % other) != Math.signum(other))
         (double)`$this$mod` % other + other
         else
         (double)`$this$mod` % other;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Double.mod(other: Float): Double {
      var var7: Double = `$this$mod` % (double)other;
      return if (`$this$mod` % (double)other != 0.0 && Math.signum(`$this$mod` % (double)other) != Math.signum((double)other))
         `$this$mod` % (double)other + (double)other
         else
         `$this$mod` % (double)other;
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @IntrinsicConstEvaluation
   @JvmStatic
   public inline fun Double.mod(other: Double): Double {
      var r: Double = `$this$mod` % other;
      return if (`$this$mod` % other != 0.0 && Math.signum(`$this$mod` % other) != Math.signum(other)) `$this$mod` % other + other else `$this$mod` % other;
   }

   open fun NumbersKt__FloorDivModKt() {
   }
}
