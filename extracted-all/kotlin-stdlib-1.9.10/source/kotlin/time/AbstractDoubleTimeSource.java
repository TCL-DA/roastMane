package kotlin.time

/** @deprecated */
@Deprecated(message = "Using AbstractDoubleTimeSource is no longer recommended, use AbstractLongTimeSource instead.")
@SinceKotlin(version = "1.3")
@ExperimentalTime
abstract class AbstractDoubleTimeSource : TimeSource.WithComparableMarks {
   protected final val unit: DurationUnit

   open fun AbstractDoubleTimeSource(unit: DurationUnit) {
      this.unit = unit;
   }

   protected abstract fun read(): Double {
   }

   public override fun markNow(): ComparableTimeMark {
      return new AbstractDoubleTimeSource.DoubleTimeMark(this.read(), this, Duration.Companion.getZERO-UwyO8pc(), null);
   }

   private class DoubleTimeMark(startedAt: Double, timeSource: AbstractDoubleTimeSource, offset: Duration) : AbstractDoubleTimeSource.DoubleTimeMark(
            startedAt, timeSource, offset
         ),
      ComparableTimeMark {
      private long offset;

      private final val offset: Duration
      private final val startedAt: Double
      private final val timeSource: AbstractDoubleTimeSource

      fun DoubleTimeMark(startedAt: Double, timeSource: AbstractDoubleTimeSource, offset: Long) {
         this.startedAt = startedAt;
         this.timeSource = timeSource;
         this.offset = offset;
      }

      public override fun elapsedNow(): Duration {
         return Duration.minus-LRDsOJo(DurationKt.toDuration(this.timeSource.read() - this.startedAt, this.timeSource.getUnit()), this.offset);
      }

      public override operator fun plus(duration: Duration): ComparableTimeMark {
         return new AbstractDoubleTimeSource.DoubleTimeMark(this.startedAt, this.timeSource, Duration.plus-LRDsOJo(this.offset, duration), null);
      }

      public override operator fun minus(other: ComparableTimeMark): Duration {
         if (other !is AbstractDoubleTimeSource.DoubleTimeMark || !(this.timeSource == (other as AbstractDoubleTimeSource.DoubleTimeMark).timeSource)) {
            throw new IllegalArgumentException("Subtracting or comparing time marks from different time sources is not possible: " + this + " and " + other);
         } else if (Duration.equals-impl0(this.offset, (other as AbstractDoubleTimeSource.DoubleTimeMark).offset) && Duration.isInfinite-impl(this.offset)) {
            return Duration.Companion.getZERO-UwyO8pc();
         } else {
            var offsetDiff: Long = Duration.minus-LRDsOJo(this.offset, (other as AbstractDoubleTimeSource.DoubleTimeMark).offset);
            var startedAtDiff: Long = DurationKt.toDuration(
               this.startedAt - (other as AbstractDoubleTimeSource.DoubleTimeMark).startedAt, this.timeSource.getUnit()
            );
            return if (Duration.equals-impl0(startedAtDiff, Duration.unaryMinus-UwyO8pc(offsetDiff)))
               Duration.Companion.getZERO-UwyO8pc()
               else
               Duration.plus-LRDsOJo(startedAtDiff, offsetDiff);
         }
      }

      public override operator fun equals(other: Any?): Boolean {
         return other is AbstractDoubleTimeSource.DoubleTimeMark
            && this.timeSource == (other as AbstractDoubleTimeSource.DoubleTimeMark).timeSource
            && Duration.equals-impl0(this.minus-UwyO8pc(other as ComparableTimeMark), Duration.Companion.getZERO-UwyO8pc());
      }

      public override fun hashCode(): Int {
         return Duration.hashCode-impl(Duration.plus-LRDsOJo(DurationKt.toDuration(this.startedAt, this.timeSource.getUnit()), this.offset));
      }

      public override fun toString(): String {
         return "DoubleTimeMark("
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
