package kotlin.ranges

import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.markers.KMappedMarker

open class CharProgression internal constructor(start: Char, endInclusive: Char, step: Int) : java.lang.Iterable<Character>, KMappedMarker {
   @JvmStatic
   public CharProgression.Companion Companion = new CharProgression.Companion(null);

   public final val first: Char
   public final val last: Char
   public final val step: Int


   public open operator fun iterator(): CharIterator {
      return new CharProgressionIterator(this.first, this.last, this.step);
   }

   public open fun isEmpty(): Boolean {
      return if (this.step > 0) Intrinsics.compare(this.first, this.last) > 0 else Intrinsics.compare(this.first, this.last) < 0;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is CharProgression
         && (
            this.isEmpty() && (other as CharProgression).isEmpty()
               || this.first == (other as CharProgression).first
                  && this.last == (other as CharProgression).last
                  && this.step == (other as CharProgression).step
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty()) -1 else 31 * (31 * this.first + this.last) + this.step;
   }

   public override fun toString(): String {
      return if (this.step > 0) this.first + ".." + this.last + " step " + this.step else this.first + " downTo " + this.last + " step " + -this.step;
   }

   companion object Companion private constructor() {
      public fun fromClosedRange(rangeStart: Char, rangeEnd: Char, step: Int): CharProgression {
         return new CharProgression(rangeStart, rangeEnd, step);
      }
   }
}
