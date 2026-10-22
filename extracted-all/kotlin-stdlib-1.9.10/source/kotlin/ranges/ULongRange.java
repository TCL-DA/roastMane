package kotlin.ranges

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
class ULongRange(start: ULong, endInclusive: ULong) : ULongRange(start, endInclusive), ClosedRange<ULong>, OpenEndRange<ULong> {
   @JvmStatic
   public ULongRange.Companion Companion = new ULongRange.Companion(null);
   @JvmStatic
   private ULongRange EMPTY = new ULongRange(-1L, 0L, null);

   public open val endExclusive: ULong
      public open get() {
         if (this.getLast-s-VKNKU() == -1L) {
            throw new IllegalStateException("Cannot return the exclusive upper bound of a range that includes MAX_VALUE.".toString());
         } else {
            return ULong.constructor-impl(this.getLast-s-VKNKU() + ULong.constructor-impl((long)1 and 4294967295L));
         }
      }

   public open val endInclusive: ULong
      public open get() {
         return this.getLast-s-VKNKU();
      }

   public open val start: ULong
      public open get() {
         return this.getFirst-s-VKNKU();
      }


   fun ULongRange(start: Long, endInclusive: Long) {
      super(start, endInclusive, 1L, null);
   }

   public open operator fun contains(value: ULong): Boolean {
      return java.lang.Long.compareUnsigned(this.getFirst-s-VKNKU(), value) <= 0 && java.lang.Long.compareUnsigned(value, this.getLast-s-VKNKU()) <= 0;
   }

   public override fun isEmpty(): Boolean {
      return java.lang.Long.compareUnsigned(this.getFirst-s-VKNKU(), this.getLast-s-VKNKU()) > 0;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is ULongRange
         && (
            this.isEmpty() && (other as ULongRange).isEmpty()
               || this.getFirst-s-VKNKU() == (other as ULongRange).getFirst-s-VKNKU() && this.getLast-s-VKNKU() == (other as ULongRange).getLast-s-VKNKU()
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty())
         -1
         else
         31 * (int)ULong.constructor-impl(this.getFirst-s-VKNKU() xor ULong.constructor-impl(this.getFirst-s-VKNKU() ushr 32))
            + (int)ULong.constructor-impl(this.getLast-s-VKNKU() xor ULong.constructor-impl(this.getLast-s-VKNKU() ushr 32));
   }

   public override fun toString(): String {
      return ULong.toString-impl(this.getFirst-s-VKNKU()) + ".." + ULong.toString-impl(this.getLast-s-VKNKU());
   }

   companion object Companion private constructor() {
      public final val EMPTY: ULongRange

   }
}
