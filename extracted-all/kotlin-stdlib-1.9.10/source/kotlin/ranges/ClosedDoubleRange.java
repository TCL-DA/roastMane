package kotlin.ranges

private class ClosedDoubleRange(start: Double, endInclusive: Double) : ClosedFloatingPointRange<java.lang.Double> {
   private final val _endInclusive: Double
   private final val _start: Double
   public open val endInclusive: Double
      public open get() {
         return this._endInclusive;
      }

   public open val start: Double
      public open get() {
         return this._start;
      }


   init {
      this._start = start;
      this._endInclusive = endInclusive;
   }

   public open fun lessThanOrEquals(a: Double, b: Double): Boolean {
      return a <= b;
   }

   public open operator fun contains(value: Double): Boolean {
      return value >= this._start && value <= this._endInclusive;
   }

   public override fun isEmpty(): Boolean {
      return !(this._start <= this._endInclusive);
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is ClosedDoubleRange
         && (
            this.isEmpty() && (other as ClosedDoubleRange).isEmpty()
               || this._start == (other as ClosedDoubleRange)._start && this._endInclusive == (other as ClosedDoubleRange)._endInclusive
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty()) -1 else 31 * java.lang.Double.hashCode(this._start) + java.lang.Double.hashCode(this._endInclusive);
   }

   public override fun toString(): String {
      return this._start + ".." + this._endInclusive;
   }
}
