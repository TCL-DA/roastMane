package kotlin.ranges

class LongRange(start: Long, endInclusive: Long) : LongProgression(start, endInclusive, 1L), ClosedRange<java.lang.Long>, OpenEndRange<java.lang.Long> {
   @JvmStatic
   public LongRange.Companion Companion = new LongRange.Companion(null);
   @JvmStatic
   private LongRange EMPTY = new LongRange(1L, 0L);

   public open val endExclusive: Long
      public open get() {
         if (this.getLast() == java.lang.Long.MAX_VALUE) {
            throw new IllegalStateException("Cannot return the exclusive upper bound of a range that includes MAX_VALUE.".toString());
         } else {
            return this.getLast() + 1L;
         }
      }

   public open val endInclusive: Long
      public open get() {
         return this.getLast();
      }

   public open val start: Long
      public open get() {
         return this.getFirst();
      }



   public open operator fun contains(value: Long): Boolean {
      return this.getFirst() <= value && value <= this.getLast();
   }

   public override fun isEmpty(): Boolean {
      return this.getFirst() > this.getLast();
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is LongRange
         && (
            this.isEmpty() && (other as LongRange).isEmpty()
               || this.getFirst() == (other as LongRange).getFirst() && this.getLast() == (other as LongRange).getLast()
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty()) -1 else (int)((long)31 * (this.getFirst() xor this.getFirst() ushr 32) + (this.getLast() xor this.getLast() ushr 32));
   }

   public override fun toString(): String {
      return this.getFirst() + ".." + this.getLast();
   }

   companion object Companion private constructor() {
      public final val EMPTY: LongRange

   }
}
