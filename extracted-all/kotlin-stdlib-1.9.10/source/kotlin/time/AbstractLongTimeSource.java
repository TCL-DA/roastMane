package kotlin.time

import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt
import org.jetbrains.annotations.NotNull

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalTime::class])
abstract class AbstractLongTimeSource : TimeSource.WithComparableMarks {
   protected final val unit: DurationUnit
   private final val zero: Long
      private final get() {
         return (this.zero$delegate.getValue() as java.lang.Number).longValue();
      }


   open fun AbstractLongTimeSource(unit: DurationUnit) {
      this.unit = unit;
      this.zero$delegate = LazyKt.lazy((new Function0<java.lang.Long>(this) {
         {
            super(0);
            this.this$0 = `$receiver`;
         }

         @NotNull
         public final java.lang.Long invoke() {
            return this.this$0.read();
         }
      }) as Function0);
   }

   protected abstract fun read(): Long {
   }

   private fun adjustedRead(): Long {
      return this.read() - this.getZero();
   }

   public override fun markNow(): ComparableTimeMark {
      return new AbstractLongTimeSource.LongTimeMark(this.adjustedRead(), this, Duration.Companion.getZERO-UwyO8pc(), null);
   }

   @SourceDebugExtension(["SMAP\nTimeSources.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TimeSources.kt\nkotlin/time/AbstractLongTimeSource$LongTimeMark\n+ 2 longSaturatedMath.kt\nkotlin/time/LongSaturatedMathKt\n*L\n1#1,199:1\n80#2:200\n*S KotlinDebug\n*F\n+ 1 TimeSources.kt\nkotlin/time/AbstractLongTimeSource$LongTimeMark\n*L\n67#1:200\n*E\n"])
   private class LongTimeMark(startedAt: Long, timeSource: AbstractLongTimeSource, offset: Duration) : AbstractLongTimeSource.LongTimeMark(
            startedAt, timeSource, offset
         ),
      ComparableTimeMark {
      private long offset;

      private final val offset: Duration
      private final val startedAt: Long
      private final val timeSource: AbstractLongTimeSource

      fun LongTimeMark(startedAt: Long, timeSource: AbstractLongTimeSource, offset: Long) {
         this.startedAt = startedAt;
         this.timeSource = timeSource;
         this.offset = offset;
      }

      public override fun elapsedNow(): Duration {
         return Duration.minus-LRDsOJo(
            LongSaturatedMathKt.saturatingOriginsDiff(AbstractLongTimeSource.access$adjustedRead(this.timeSource), this.startedAt, this.timeSource.getUnit()),
            this.offset
         );
      }

      public override operator fun plus(duration: Duration): ComparableTimeMark {
         var unit: DurationUnit = this.timeSource.getUnit();
         if (Duration.isInfinite-impl(duration)) {
            return new AbstractLongTimeSource.LongTimeMark(
               LongSaturatedMathKt.saturatingAdd-NuflL3o(this.startedAt, unit, duration), this.timeSource, Duration.Companion.getZERO-UwyO8pc(), null
            );
         } else {
            var durationInUnit: Long = Duration.truncateTo-UwyO8pc$kotlin_stdlib(duration, unit);
            var rest: Long = Duration.plus-LRDsOJo(Duration.minus-LRDsOJo(duration, durationInUnit), this.offset);
            var sum: Long = LongSaturatedMathKt.saturatingAdd-NuflL3o(this.startedAt, unit, durationInUnit);
            var restInUnit: Long = Duration.truncateTo-UwyO8pc$kotlin_stdlib(rest, unit);
            sum = LongSaturatedMathKt.saturatingAdd-NuflL3o(sum, unit, restInUnit);
            var restUnderUnit: Long = Duration.minus-LRDsOJo(rest, restInUnit);
            var restUnderUnitNs: Long = Duration.getInWholeNanoseconds-impl(restUnderUnit);
            if (sum != 0L && restUnderUnitNs != 0L && (sum xor restUnderUnitNs) < 0L) {
               var newValue: Long = DurationKt.toDuration(MathKt.getSign(restUnderUnitNs), unit);
               sum = LongSaturatedMathKt.saturatingAdd-NuflL3o(sum, unit, newValue);
               restUnderUnit = Duration.minus-LRDsOJo(restUnderUnit, newValue);
            }

            return new AbstractLongTimeSource.LongTimeMark(
               sum, this.timeSource, if ((sum - 1L or 1L) == java.lang.Long.MAX_VALUE) Duration.Companion.getZERO-UwyO8pc() else restUnderUnit, null
            );
         }
      }

      public override operator fun minus(other: ComparableTimeMark): Duration {
         if (other is AbstractLongTimeSource.LongTimeMark && this.timeSource == (other as AbstractLongTimeSource.LongTimeMark).timeSource) {
            return Duration.plus-LRDsOJo(
               LongSaturatedMathKt.saturatingOriginsDiff(this.startedAt, (other as AbstractLongTimeSource.LongTimeMark).startedAt, this.timeSource.getUnit()),
               Duration.minus-LRDsOJo(this.offset, (other as AbstractLongTimeSource.LongTimeMark).offset)
            );
         } else {
            throw new IllegalArgumentException("Subtracting or comparing time marks from different time sources is not possible: " + this + " and " + other);
         }
      }

      public override operator fun equals(other: Any?): Boolean {
         return other is AbstractLongTimeSource.LongTimeMark
            && this.timeSource == (other as AbstractLongTimeSource.LongTimeMark).timeSource
            && Duration.equals-impl0(this.minus-UwyO8pc(other as ComparableTimeMark), Duration.Companion.getZERO-UwyO8pc());
      }

      public override fun hashCode(): Int {
         return Duration.hashCode-impl(this.offset) * 37 + java.lang.Long.hashCode(this.startedAt);
      }

      public override fun toString(): String {
         return "LongTimeMark("
            + this.startedAt
            + DurationUnitKt.shortName(this.timeSource.getUnit())
            + " + "
            + Duration.toString-impl(this.offset)
            + ", "
            + this.timeSource
            + ')';
      }

      override fun `minus-LRDsOJo`(duration: Long): ComparableTimeMark {
         return ComparableTimeMark.DefaultImpls.minus-LRDsOJo(this, duration);
      }

      override fun compareTo(other: ComparableTimeMark): Int {
         return ComparableTimeMark.DefaultImpls.compareTo(this, other);
      }

      override fun hasPassedNow(): Boolean {
         return ComparableTimeMark.DefaultImpls.hasPassedNow(this);
      }

      override fun hasNotPassedNow(): Boolean {
         return ComparableTimeMark.DefaultImpls.hasNotPassedNow(this);
      }
   }
}
