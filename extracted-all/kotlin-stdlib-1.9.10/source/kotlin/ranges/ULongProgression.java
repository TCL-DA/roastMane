package kotlin.ranges

import kotlin.internal.UProgressionUtilKt
import kotlin.jvm.internal.markers.KMappedMarker

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
open class ULongProgression internal constructor(start: ULong, endInclusive: ULong, step: Long) : ULongProgression(start, endInclusive, step),
   java.lang.Iterable<ULong>,
   KMappedMarker {
   @JvmStatic
   public ULongProgression.Companion Companion = new ULongProgression.Companion(null);
   private long first;
   private long last;

   public final val first: ULong
   public final val last: ULong
   public final val step: Long

   fun ULongProgression(start: Long, endInclusive: Long, step: Long) {
      if (step == 0L) {
         throw new IllegalArgumentException("Step must be non-zero.");
      } else if (step == java.lang.Long.MIN_VALUE) {
         throw new IllegalArgumentException("Step must be greater than Long.MIN_VALUE to avoid overflow on negation.");
      } else {
         this.first = start;
         this.last = UProgressionUtilKt.getProgressionLastElement-7ftBX0g(start, endInclusive, step);
         this.step = step;
      }
   }

   public override operator fun iterator(): Iterator<ULong> {
      return new ULongProgressionIterator(this.first, this.last, this.step, null);
   }

   public open fun isEmpty(): Boolean {
      return if (this.step > 0L) java.lang.Long.compareUnsigned(this.first, this.last) > 0 else java.lang.Long.compareUnsigned(this.first, this.last) < 0;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is ULongProgression
         && (
            this.isEmpty() && (other as ULongProgression).isEmpty()
               || this.first == (other as ULongProgression).first
                  && this.last == (other as ULongProgression).last
                  && this.step == (other as ULongProgression).step
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty())
         -1
         else
         31
               * (
                  31 * (int)ULong.constructor-impl(this.first xor ULong.constructor-impl(this.first ushr 32))
                     + (int)ULong.constructor-impl(this.last xor ULong.constructor-impl(this.last ushr 32))
               )
            + (int)(this.step xor this.step ushr 32);
   }

   public override fun toString(): String {
      return if (this.step > 0L)
         ULong.toString-impl(this.first) + ".." + ULong.toString-impl(this.last) + " step " + this.step
         else
         ULong.toString-impl(this.first) + " downTo " + ULong.toString-impl(this.last) + " step " + -this.step;
   }

   companion object Companion private constructor() {
      public fun fromClosedRange(rangeStart: ULong, rangeEnd: ULong, step: Long): ULongProgression {
         return new ULongProgression(rangeStart, rangeEnd, step, null);
      }
   }
}
