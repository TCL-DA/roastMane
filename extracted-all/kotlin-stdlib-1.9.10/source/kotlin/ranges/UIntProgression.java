package kotlin.ranges

import kotlin.internal.UProgressionUtilKt
import kotlin.jvm.internal.markers.KMappedMarker

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
open class UIntProgression internal constructor(start: UInt, endInclusive: UInt, step: Int) : UIntProgression(start, endInclusive, step),
   java.lang.Iterable<UInt>,
   KMappedMarker {
   @JvmStatic
   public UIntProgression.Companion Companion = new UIntProgression.Companion(null);
   private int first;
   private int last;

   public final val first: UInt
   public final val last: UInt
   public final val step: Int

   fun UIntProgression(start: Int, endInclusive: Int, step: Int) {
      if (step == 0) {
         throw new IllegalArgumentException("Step must be non-zero.");
      } else if (step == Integer.MIN_VALUE) {
         throw new IllegalArgumentException("Step must be greater than Int.MIN_VALUE to avoid overflow on negation.");
      } else {
         this.first = start;
         this.last = UProgressionUtilKt.getProgressionLastElement-Nkh28Cs(start, endInclusive, step);
         this.step = step;
      }
   }

   public override operator fun iterator(): Iterator<UInt> {
      return new UIntProgressionIterator(this.first, this.last, this.step, null);
   }

   public open fun isEmpty(): Boolean {
      return if (this.step > 0) Integer.compareUnsigned(this.first, this.last) > 0 else Integer.compareUnsigned(this.first, this.last) < 0;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is UIntProgression
         && (
            this.isEmpty() && (other as UIntProgression).isEmpty()
               || this.first == (other as UIntProgression).first
                  && this.last == (other as UIntProgression).last
                  && this.step == (other as UIntProgression).step
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty()) -1 else 31 * (31 * this.first + this.last) + this.step;
   }

   public override fun toString(): String {
      return if (this.step > 0)
         UInt.toString-impl(this.first) + ".." + UInt.toString-impl(this.last) + " step " + this.step
         else
         UInt.toString-impl(this.first) + " downTo " + UInt.toString-impl(this.last) + " step " + -this.step;
   }

   companion object Companion private constructor() {
      public fun fromClosedRange(rangeStart: UInt, rangeEnd: UInt, step: Int): UIntProgression {
         return new UIntProgression(rangeStart, rangeEnd, step, null);
      }
   }
}
