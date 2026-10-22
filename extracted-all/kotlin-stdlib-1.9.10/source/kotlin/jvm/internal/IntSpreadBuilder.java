package kotlin.jvm.internal

class IntSpreadBuilder(size: Int) : PrimitiveSpreadBuilder(size) {
   private final val values: IntArray

   init {
      this.values = new int[size];
   }

   protected open fun IntArray.getSize(): Int {
      return `$this$getSize`.length;
   }

   public fun add(value: Int) {
      var var10000: IntArray = this.values;
      var var2: Int = this.getPosition();
      this.setPosition(var2 + 1);
      var10000[var2] = value;
   }

   public fun toArray(): IntArray {
      return this.toArray(this.values, new int[this.size()]);
   }
}
