package kotlin.ranges

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
class UIntRange(start: UInt, endInclusive: UInt) : UIntRange(start, endInclusive), ClosedRange<UInt>, OpenEndRange<UInt> {
   @JvmStatic
   public UIntRange.Companion Companion = new UIntRange.Companion(null);
   @JvmStatic
   private UIntRange EMPTY = new UIntRange(-1, 0, null);

   public open val endExclusive: UInt
      public open get() {
         if (this.getLast-pVg5ArA() == -1) {
            throw new IllegalStateException("Cannot return the exclusive upper bound of a range that includes MAX_VALUE.".toString());
         } else {
            return UInt.constructor-impl(this.getLast-pVg5ArA() + 1);
         }
      }

   public open val endInclusive: UInt
      public open get() {
         return this.getLast-pVg5ArA();
      }

   public open val start: UInt
      public open get() {
         return this.getFirst-pVg5ArA();
      }


   fun UIntRange(start: Int, endInclusive: Int) {
      super(start, endInclusive, 1, null);
   }

   public open operator fun contains(value: UInt): Boolean {
      return Integer.compareUnsigned(this.getFirst-pVg5ArA(), value) <= 0 && Integer.compareUnsigned(value, this.getLast-pVg5ArA()) <= 0;
   }

   public override fun isEmpty(): Boolean {
      return Integer.compareUnsigned(this.getFirst-pVg5ArA(), this.getLast-pVg5ArA()) > 0;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is UIntRange
         && (
            this.isEmpty() && (other as UIntRange).isEmpty()
               || this.getFirst-pVg5ArA() == (other as UIntRange).getFirst-pVg5ArA() && this.getLast-pVg5ArA() == (other as UIntRange).getLast-pVg5ArA()
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty()) -1 else 31 * this.getFirst-pVg5ArA() + this.getLast-pVg5ArA();
   }

   public override fun toString(): String {
      return UInt.toString-impl(this.getFirst-pVg5ArA()) + ".." + UInt.toString-impl(this.getLast-pVg5ArA());
   }

   companion object Companion private constructor() {
      public final val EMPTY: UIntRange

   }
}
