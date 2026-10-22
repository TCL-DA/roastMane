package kotlin.ranges

import kotlin.jvm.internal.markers.KMappedMarker

open class IntProgression internal constructor(start: Int, endInclusive: Int, step: Int) : java.lang.Iterable<Integer>, KMappedMarker {
   @JvmStatic
   public IntProgression.Companion Companion = new IntProgression.Companion(null);

   public final val first: Int
   public final val last: Int
   public final val step: Int


   public open operator fun iterator(): IntIterator {
      return new IntProgressionIterator(this.first, this.last, this.step);
   }

   public open fun isEmpty(): Boolean {
      return if (this.step > 0) this.first > this.last else this.first < this.last;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is IntProgression
         && (
            this.isEmpty() && (other as IntProgression).isEmpty()
               || this.first == (other as IntProgression).first && this.last == (other as IntProgression).last && this.step == (other as IntProgression).step
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty()) -1 else 31 * (31 * this.first + this.last) + this.step;
   }

   public override fun toString(): String {
      return if (this.step > 0) this.first + ".." + this.last + " step " + this.step else this.first + " downTo " + this.last + " step " + -this.step;
   }

   companion object Companion private constructor() {
      public fun fromClosedRange(rangeStart: Int, rangeEnd: Int, step: Int): IntProgression {
         return new IntProgression(rangeStart, rangeEnd, step);
      }
   }
}
