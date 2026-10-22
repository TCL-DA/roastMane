package kotlin.text

data class MatchGroup(value: String, range: IntRange) {
   public final val range: IntRange
   public final val value: String

   init {
      this.value = value;
      this.range = range;
   }

   public operator fun component1(): String {
      return this.value;
   }

   public operator fun component2(): IntRange {
      return this.range;
   }

   public fun copy(value: String, range: IntRange): MatchGroup {
      return new MatchGroup(value, range);
   }

   public override fun toString(): String {
      return "MatchGroup(value=" + this.value + ", range=" + this.range + ')';
   }

   public override fun hashCode(): Int {
      return this.value.hashCode() * 31 + this.range.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is MatchGroup) {
         return false;
      } else {
         var var2: MatchGroup = other as MatchGroup;
         if (!(this.value == (other as MatchGroup).value)) {
            return false;
         } else {
            return this.range == var2.range;
         }
      }
   }
}
