package kotlin

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class NumbersKt__BigDecimalsKt {
   @InlineOnly
   @JvmStatic
   public inline operator fun BigDecimal.plus(other: BigDecimal): BigDecimal {
      var var10000: BigDecimal = `$this$plus`.add(other);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun BigDecimal.minus(other: BigDecimal): BigDecimal {
      var var10000: BigDecimal = `$this$minus`.subtract(other);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun BigDecimal.times(other: BigDecimal): BigDecimal {
      var var10000: BigDecimal = `$this$times`.multiply(other);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun BigDecimal.div(other: BigDecimal): BigDecimal {
      var var10000: BigDecimal = `$this$div`.divide(other, RoundingMode.HALF_EVEN);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun BigDecimal.rem(other: BigDecimal): BigDecimal {
      var var10000: BigDecimal = `$this$rem`.remainder(other);
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun BigDecimal.unaryMinus(): BigDecimal {
      var var10000: BigDecimal = `$this$unaryMinus`.negate();
      return var10000;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline operator fun BigDecimal.inc(): BigDecimal {
      var var10000: BigDecimal = `$this$inc`.add(BigDecimal.ONE);
      return var10000;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline operator fun BigDecimal.dec(): BigDecimal {
      var var10000: BigDecimal = `$this$dec`.subtract(BigDecimal.ONE);
      return var10000;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Int.toBigDecimal(): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf((long)`$this$toBigDecimal`);
      return var10000;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Int.toBigDecimal(mathContext: MathContext): BigDecimal {
      return new BigDecimal(`$this$toBigDecimal`, mathContext);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Long.toBigDecimal(): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(`$this$toBigDecimal`);
      return var10000;
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Long.toBigDecimal(mathContext: MathContext): BigDecimal {
      return new BigDecimal(`$this$toBigDecimal`, mathContext);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.toBigDecimal(): BigDecimal {
      return new BigDecimal(java.lang.String.valueOf(`$this$toBigDecimal`));
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Float.toBigDecimal(mathContext: MathContext): BigDecimal {
      return new BigDecimal(java.lang.String.valueOf(`$this$toBigDecimal`), mathContext);
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.toBigDecimal(): BigDecimal {
      return new BigDecimal(java.lang.String.valueOf(`$this$toBigDecimal`));
   }

   @SinceKotlin(version = "1.2")
   @InlineOnly
   @JvmStatic
   public inline fun Double.toBigDecimal(mathContext: MathContext): BigDecimal {
      return new BigDecimal(java.lang.String.valueOf(`$this$toBigDecimal`), mathContext);
   }

   open fun NumbersKt__BigDecimalsKt() {
   }
}
