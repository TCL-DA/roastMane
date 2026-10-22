package kotlin.ranges

private class ClosedFloatRange(start: Float, endInclusive: Float) : ClosedFloatingPointRange<java.lang.Float> {
   private final val _endInclusive: Float
   private final val _start: Float
   public open val endInclusive: Float
      public open get() {
         return this._endInclusive;
      }

   public open val start: Float
      public open get() {
         return this._start;
      }


   init {
      this._start = start;
      this._endInclusive = endInclusive;
   }

   public open fun lessThanOrEquals(a: Float, b: Float): Boolean {
      return a <= b;
   }

   public open operator fun contains(value: Float): Boolean {
      return value >= this._start && value <= this._endInclusive;
   }

   public override fun isEmpty(): Boolean {
      return !(this._start <= this._endInclusive);
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is ClosedFloatRange
         && (
            this.isEmpty() && (other as ClosedFloatRange).isEmpty()
               || this._start == (other as ClosedFloatRange)._start && this._endInclusive == (other as ClosedFloatRange)._endInclusive
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty()) -1 else 31 * java.lang.Float.hashCode(this._start) + java.lang.Float.hashCode(this._endInclusive);
   }

   public override fun toString(): String {
      return this._start + ".." + this._endInclusive;
   }
}
