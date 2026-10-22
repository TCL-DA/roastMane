package kotlin.ranges

private class OpenEndDoubleRange(start: Double, endExclusive: Double) : OpenEndRange<java.lang.Double> {
   private final val _endExclusive: Double
   private final val _start: Double
   public open val endExclusive: Double
      public open get() {
         return this._endExclusive;
      }

   public open val start: Double
      public open get() {
         return this._start;
      }


   init {
      this._start = start;
      this._endExclusive = endExclusive;
   }

   private fun lessThanOrEquals(a: Double, b: Double): Boolean {
      return a <= b;
   }

   public open operator fun contains(value: Double): Boolean {
      return value >= this._start && value < this._endExclusive;
   }

   public override fun isEmpty(): Boolean {
      return !(this._start < this._endExclusive);
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is OpenEndDoubleRange
         && (
            this.isEmpty() && (other as OpenEndDoubleRange).isEmpty()
               || this._start == (other as OpenEndDoubleRange)._start && this._endExclusive == (other as OpenEndDoubleRange)._endExclusive
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty()) -1 else 31 * java.lang.Double.hashCode(this._start) + java.lang.Double.hashCode(this._endExclusive);
   }

   public override fun toString(): String {
      return this._start + "..<" + this._endExclusive;
   }
}
