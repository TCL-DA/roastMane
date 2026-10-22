package kotlin.ranges

import kotlin.jvm.internal.markers.KMappedMarker

open class LongProgression internal constructor(start: Long, endInclusive: Long, step: Long) : java.lang.Iterable<java.lang.Long>, KMappedMarker {
   @JvmStatic
   public LongProgression.Companion Companion = new LongProgression.Companion(null);

   public final val first: Long
   public final val last: Long
   public final val step: Long


   public open operator fun iterator(): LongIterator {
      return new LongProgressionIterator(this.first, this.last, this.step);
   }

   public open fun isEmpty(): Boolean {
      return if (this.step > 0L) this.first > this.last else this.first < this.last;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is LongProgression
         && (
            this.isEmpty() && (other as LongProgression).isEmpty()
               || this.first == (other as LongProgression).first
                  && this.last == (other as LongProgression).last
                  && this.step == (other as LongProgression).step
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty())
         -1
         else
         (int)((long)31 * ((long)31 * (this.first xor this.first ushr 32) + (this.last xor this.last ushr 32)) + (this.step xor this.step ushr 32));
   }

   public override fun toString(): String {
      return if (this.step > 0L) this.first + ".." + this.last + " step " + this.step else this.first + " downTo " + this.last + " step " + -this.step;
   }

   companion object Companion private constructor() {
      public fun fromClosedRange(rangeStart: Long, rangeEnd: Long, step: Long): LongProgression {
         return new LongProgression(rangeStart, rangeEnd, step);
      }
   }
}
