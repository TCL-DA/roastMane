package kotlin.ranges

private class OpenEndFloatRange(start: Float, endExclusive: Float) : OpenEndRange<java.lang.Float> {
   private final val _endExclusive: Float
   private final val _start: Float
   public open val endExclusive: Float
      public open get() {
         return this._endExclusive;
      }

   public open val start: Float
      public open get() {
         return this._start;
      }


   init {
      this._start = start;
      this._endExclusive = endExclusive;
   }

   private fun lessThanOrEquals(a: Float, b: Float): Boolean {
      return a <= b;
   }

   public open operator fun contains(value: Float): Boolean {
      return value >= this._start && value < this._endExclusive;
   }

   public override fun isEmpty(): Boolean {
      return !(this._start < this._endExclusive);
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is OpenEndFloatRange
         && (
            this.isEmpty() && (other as OpenEndFloatRange).isEmpty()
               || this._start == (other as OpenEndFloatRange)._start && this._endExclusive == (other as OpenEndFloatRange)._endExclusive
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty()) -1 else 31 * java.lang.Float.hashCode(this._start) + java.lang.Float.hashCode(this._endExclusive);
   }

   public override fun toString(): String {
      return this._start + "..<" + this._endExclusive;
   }
}
