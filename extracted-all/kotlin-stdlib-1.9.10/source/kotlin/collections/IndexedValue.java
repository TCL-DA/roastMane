package kotlin.collections

data class IndexedValue<T>(index: Int, value: Any) {
   public final val index: Int
   public final val value: Any

   init {
      this.index = index;
      this.value = (T)value;
   }

   public operator fun component1(): Int {
      return this.index;
   }

   public operator fun component2(): Any {
      return this.value;
   }

   public fun copy(index: Int, value: Any): IndexedValue<Any> {
      return new IndexedValue<>(index, (T)value);
   }

   public override fun toString(): String {
      return "IndexedValue(index=" + this.index + ", value=" + this.value + ')';
   }

   public override fun hashCode(): Int {
      return Integer.hashCode(this.index) * 31 + (if (this.value == null) 0 else this.value.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is IndexedValue) {
         return false;
      } else {
         var var2: IndexedValue = other as IndexedValue;
         if (this.index != (other as IndexedValue).index) {
            return false;
         } else {
            return this.value == var2.value;
         }
      }
   }
}
