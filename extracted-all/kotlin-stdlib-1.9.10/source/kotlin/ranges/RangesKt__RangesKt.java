package kotlin.ranges

import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class RangesKt__RangesKt {
   @JvmStatic
   public operator fun <T : Comparable<T>> T.rangeTo(that: T): ClosedRange<T> {
      return new ComparableRange(`$this$rangeTo`, that);
   }

   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun <T : Comparable<T>> T.rangeUntil(that: T): OpenEndRange<T> {
      return new ComparableOpenEndRange(`$this$rangeUntil`, that);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public operator fun Double.rangeTo(that: Double): ClosedFloatingPointRange<Double> {
      return new ClosedDoubleRange(`$this$rangeTo`, that);
   }

   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun Double.rangeUntil(that: Double): OpenEndRange<Double> {
      return new OpenEndDoubleRange(`$this$rangeUntil`, that);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public operator fun Float.rangeTo(that: Float): ClosedFloatingPointRange<Float> {
      return new ClosedFloatRange(`$this$rangeTo`, that);
   }

   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun Float.rangeUntil(that: Float): OpenEndRange<Float> {
      return new OpenEndFloatRange(`$this$rangeUntil`, that);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline operator fun <T : Any, R> R.contains(element: T?): Boolean where R : ClosedRange<T>, R : Iterable<T> {
      return element != null && `$this$contains`.contains(element as java.lang.Comparable);
   }

   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline operator fun <T : Any, R> R.contains(element: T?): Boolean where R : OpenEndRange<T>, R : Iterable<T> {
      return element != null && `$this$contains`.contains(element as java.lang.Comparable);
   }

   @JvmStatic
   internal fun checkStepIsPositive(isPositive: Boolean, step: Number) {
      if (!isPositive) {
         throw new IllegalArgumentException("Step must be positive, was: " + step + '.');
      }
   }

   open fun RangesKt__RangesKt() {
   }
}
