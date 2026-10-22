package kotlin.time

import kotlin.time.TimeSource.Monotonic.ValueTimeMark

@SinceKotlin(version = "1.3")
internal object MonotonicTimeSource private constructor() : TimeSource.WithComparableMarks {
   @JvmStatic
   public MonotonicTimeSource INSTANCE = new MonotonicTimeSource();

   private final val zero: Long = System.nanoTime()


   private fun read(): Long {
      return System.nanoTime() - zero;
   }

   public override fun toString(): String {
      return "TimeSource(System.nanoTime())";
   }

   public open fun markNow(): ValueTimeMark {
      return TimeSource.Monotonic.ValueTimeMark.constructor-impl(this.read());
   }

   public fun elapsedFrom(timeMark: ValueTimeMark): Duration {
      return LongSaturatedMathKt.saturatingDiff(this.read(), timeMark, DurationUnit.NANOSECONDS);
   }

   public fun differenceBetween(one: ValueTimeMark, another: ValueTimeMark): Duration {
      return LongSaturatedMathKt.saturatingOriginsDiff(one, another, DurationUnit.NANOSECONDS);
   }

   public fun adjustReading(timeMark: ValueTimeMark, duration: Duration): ValueTimeMark {
      return TimeSource.Monotonic.ValueTimeMark.constructor-impl(LongSaturatedMathKt.saturatingAdd-NuflL3o(timeMark, DurationUnit.NANOSECONDS, duration));
   }
}
