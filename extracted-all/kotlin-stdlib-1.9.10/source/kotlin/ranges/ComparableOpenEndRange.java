package kotlin.ranges

private open class ComparableOpenEndRange<T extends java.lang.Comparable<? super T>>(start: Any, endExclusive: Any) : OpenEndRange<T> {
   private T start;
   private T endExclusive;

   public open val endExclusive: Any
   public open val start: Any

   init {
      this.start = (T)start;
      this.endExclusive = (T)endExclusive;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is ComparableOpenEndRange
         && (
            this.isEmpty() && (other as ComparableOpenEndRange).isEmpty()
               || this.getStart() == (other as ComparableOpenEndRange).getStart()
                  && this.getEndExclusive() == (other as ComparableOpenEndRange).getEndExclusive()
         );
   }

   public override fun hashCode(): Int {
      return if (this.isEmpty()) -1 else 31 * this.getStart().hashCode() + this.getEndExclusive().hashCode();
   }

   public override fun toString(): String {
      return this.getStart() + "..<" + this.getEndExclusive();
   }

   override fun contains(value: T): Boolean {
      return OpenEndRange.DefaultImpls.contains(this, (T)value);
   }

   override fun isEmpty(): Boolean {
      return OpenEndRange.DefaultImpls.isEmpty(this);
   }
}
