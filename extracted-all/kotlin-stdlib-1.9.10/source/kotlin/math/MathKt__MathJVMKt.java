package kotlin.math

import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class MathKt__MathJVMKt : MathKt__MathHKt {
   public final val absoluteValue: Double
      public final inline get() {
         return Math.abs(`$this$absoluteValue`);
      }

   public final val absoluteValue: Float
      public final inline get() {
         return Math.abs(`$this$absoluteValue`);
      }

   public final val absoluteValue: Int
      public final inline get() {
         return Math.abs(`$this$absoluteValue`);
      }

   public final val absoluteValue: Long
      public final inline get() {
         return Math.abs(`$this$absoluteValue`);
      }

   public final val sign: Double
      public final inline get() {
         return Math.signum(`$this$sign`);
      }

   public final val sign: Float
      public final inline get() {
         return Math.signum(`$this$sign`);
      }

   public final val sign: Int
      public final get() {
         return if (`$this$sign` < 0) -1 else (if (`$this$sign` > 0) 1 else 0);
      }

   public final val sign: Int
      public final get() {
         return if (`$this$sign` < 0L) -1 else (if (`$this$sign` > 0L) 1 else 0);
      }

   public final val ulp: Double
      public final inline get() {
         return Math.ulp(`$this$ulp`);
      }

   public final val ulp: Float
      public final inline get() {
         return Math.ulp(`$this$ulp`);
      }


   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun sin(x: Double): Double {
      return Math.sin(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun cos(x: Double): Double {
      return Math.cos(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun tan(x: Double): Double {
      return Math.tan(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun asin(x: Double): Double {
      return Math.asin(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun acos(x: Double): Double {
      return Math.acos(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun atan(x: Double): Double {
      return Math.atan(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun atan2(y: Double, x: Double): Double {
      return Math.atan2(y, x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun sinh(x: Double): Double {
      return Math.sinh(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun cosh(x: Double): Double {
      return Math.cosh(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun tanh(x: Double): Double {
      return Math.tanh(x);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun asinh(x: Double): Double {
      var var10000: Double;
      if (x >= Constants.taylor_n_bound) {
         var10000 = if (x > Constants.upper_taylor_n_bound)
            (if (x > Constants.upper_taylor_2_bound) Math.log(x) + Constants.LN2 else Math.log(x * (double)2 + (double)1 / (x * (double)2)))
            else
            Math.log(x + Math.sqrt(x * x + (double)1));
      } else if (x <= -Constants.taylor_n_bound) {
         var10000 = -MathKt.asinh(-x);
      } else {
         var result: Double = x;
         if (Math.abs(x) >= Constants.taylor_2_bound) {
            result = x - x * x * x / (double)6;
         }

         var10000 = result;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun acosh(x: Double): Double {
      var var10000: Double;
      if (x < 1.0) {
         var10000 = java.lang.Double.NaN;
      } else if (x > Constants.upper_taylor_2_bound) {
         var10000 = Math.log(x) + Constants.LN2;
      } else if (x - (double)1 >= Constants.taylor_n_bound) {
         var10000 = Math.log(x + Math.sqrt(x * x - (double)1));
      } else {
         var y: Double = Math.sqrt(x - (double)1);
         var result: Double = y;
         if (y >= Constants.taylor_2_bound) {
            result = y - y * y * y / (double)12;
         }

         var10000 = Math.sqrt(2.0) * result;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun atanh(x: Double): Double {
      if (Math.abs(x) < Constants.taylor_n_bound) {
         var result: Double = x;
         if (Math.abs(x) > Constants.taylor_2_bound) {
            result = x + x * x * x / (double)3;
         }

         return result;
      } else {
         return Math.log(((double)1 + x) / ((double)1 - x)) / (double)2;
      }
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun hypot(x: Double, y: Double): Double {
      return Math.hypot(x, y);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun sqrt(x: Double): Double {
      return Math.sqrt(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun exp(x: Double): Double {
      return Math.exp(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun expm1(x: Double): Double {
      return Math.expm1(x);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun log(x: Double, base: Double): Double {
      return if (!(base <= 0.0) && base != 1.0) Math.log(x) / Math.log(base) else java.lang.Double.NaN;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun ln(x: Double): Double {
      return Math.log(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun log10(x: Double): Double {
      return Math.log10(x);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun log2(x: Double): Double {
      return Math.log(x) / Constants.LN2;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun ln1p(x: Double): Double {
      return Math.log1p(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun ceil(x: Double): Double {
      return Math.ceil(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun floor(x: Double): Double {
      return Math.floor(x);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun truncate(x: Double): Double {
      return if (!java.lang.Double.isNaN(x) && !java.lang.Double.isInfinite(x)) (if (x > 0.0) Math.floor(x) else Math.ceil(x)) else x;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun round(x: Double): Double {
      return Math.rint(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun abs(x: Double): Double {
      return Math.abs(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun sign(x: Double): Double {
      return Math.signum(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun min(a: Double, b: Double): Double {
      return Math.min(a, b);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun max(a: Double, b: Double): Double {
      return Math.max(a, b);
   }

   @SinceKotlin(version = "1.8")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun cbrt(x: Double): Double {
      return Math.cbrt(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.pow(x: Double): Double {
      return Math.pow(`$this$pow`, x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.pow(n: Int): Double {
      return Math.pow(`$this$pow`, (double)n);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.IEEErem(divisor: Double): Double {
      return Math.IEEEremainder(`$this$IEEErem`, divisor);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.withSign(sign: Double): Double {
      return Math.copySign(`$this$withSign`, sign);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.withSign(sign: Int): Double {
      return Math.copySign(`$this$withSign`, (double)sign);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.nextUp(): Double {
      return Math.nextUp(`$this$nextUp`);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.nextDown(): Double {
      return Math.nextAfter(`$this$nextDown`, java.lang.Double.NEGATIVE_INFINITY);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.nextTowards(to: Double): Double {
      return Math.nextAfter(`$this$nextTowards`, to);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun Double.roundToInt(): Int {
      if (java.lang.Double.isNaN(`$this$roundToInt`)) {
         throw new IllegalArgumentException("Cannot round NaN value.");
      } else {
         return if (`$this$roundToInt` > 2.147483647E9)
            Integer.MAX_VALUE
            else
            (if (`$this$roundToInt` < -2.1474836E9F) Integer.MIN_VALUE else (int)Math.round(`$this$roundToInt`));
      }
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun Double.roundToLong(): Long {
      if (java.lang.Double.isNaN(`$this$roundToLong`)) {
         throw new IllegalArgumentException("Cannot round NaN value.");
      } else {
         return Math.round(`$this$roundToLong`);
      }
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun sin(x: Float): Float {
      return (float)Math.sin((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun cos(x: Float): Float {
      return (float)Math.cos((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun tan(x: Float): Float {
      return (float)Math.tan((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun asin(x: Float): Float {
      return (float)Math.asin((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun acos(x: Float): Float {
      return (float)Math.acos((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun atan(x: Float): Float {
      return (float)Math.atan((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun atan2(y: Float, x: Float): Float {
      return (float)Math.atan2((double)y, (double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun sinh(x: Float): Float {
      return (float)Math.sinh((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun cosh(x: Float): Float {
      return (float)Math.cosh((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun tanh(x: Float): Float {
      return (float)Math.tanh((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun asinh(x: Float): Float {
      return (float)MathKt.asinh((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun acosh(x: Float): Float {
      return (float)MathKt.acosh((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun atanh(x: Float): Float {
      return (float)MathKt.atanh((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun hypot(x: Float, y: Float): Float {
      return (float)Math.hypot((double)x, (double)y);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun sqrt(x: Float): Float {
      return (float)Math.sqrt((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun exp(x: Float): Float {
      return (float)Math.exp((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun expm1(x: Float): Float {
      return (float)Math.expm1((double)x);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun log(x: Float, base: Float): Float {
      return if (!(base <= 0.0F) && base != 1.0F) (float)(Math.log((double)x) / Math.log((double)base)) else java.lang.Float.NaN;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun ln(x: Float): Float {
      return (float)Math.log((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun log10(x: Float): Float {
      return (float)Math.log10((double)x);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun log2(x: Float): Float {
      return (float)(Math.log((double)x) / Constants.LN2);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun ln1p(x: Float): Float {
      return (float)Math.log1p((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun ceil(x: Float): Float {
      return (float)Math.ceil((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun floor(x: Float): Float {
      return (float)Math.floor((double)x);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun truncate(x: Float): Float {
      return if (!java.lang.Float.isNaN(x) && !java.lang.Float.isInfinite(x))
         (if (x > 0.0F) (float)Math.floor((double)x) else (float)Math.ceil((double)x))
         else
         x;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun round(x: Float): Float {
      return (float)Math.rint((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun abs(x: Float): Float {
      return Math.abs(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun sign(x: Float): Float {
      return Math.signum(x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun min(a: Float, b: Float): Float {
      return Math.min(a, b);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun max(a: Float, b: Float): Float {
      return Math.max(a, b);
   }

   @SinceKotlin(version = "1.8")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun cbrt(x: Float): Float {
      return (float)Math.cbrt((double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.pow(x: Float): Float {
      return (float)Math.pow((double)`$this$pow`, (double)x);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.pow(n: Int): Float {
      return (float)Math.pow((double)`$this$pow`, (double)n);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.IEEErem(divisor: Float): Float {
      return (float)Math.IEEEremainder((double)`$this$IEEErem`, (double)divisor);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.withSign(sign: Float): Float {
      return Math.copySign(`$this$withSign`, sign);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.withSign(sign: Int): Float {
      return Math.copySign(`$this$withSign`, (float)sign);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.nextUp(): Float {
      return Math.nextUp(`$this$nextUp`);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.nextDown(): Float {
      return Math.nextAfter(`$this$nextDown`, java.lang.Double.NEGATIVE_INFINITY);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.nextTowards(to: Float): Float {
      return Math.nextAfter(`$this$nextTowards`, (double)to);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun Float.roundToInt(): Int {
      if (java.lang.Float.isNaN(`$this$roundToInt`)) {
         throw new IllegalArgumentException("Cannot round NaN value.");
      } else {
         return Math.round(`$this$roundToInt`);
      }
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun Float.roundToLong(): Long {
      return MathKt.roundToLong((double)`$this$roundToLong`);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun abs(n: Int): Int {
      return Math.abs(n);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun min(a: Int, b: Int): Int {
      return Math.min(a, b);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun max(a: Int, b: Int): Int {
      return Math.max(a, b);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun abs(n: Long): Long {
      return Math.abs(n);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun min(a: Long, b: Long): Long {
      return Math.min(a, b);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun max(a: Long, b: Long): Long {
      return Math.max(a, b);
   }

   open fun MathKt__MathJVMKt() {
   }
}
