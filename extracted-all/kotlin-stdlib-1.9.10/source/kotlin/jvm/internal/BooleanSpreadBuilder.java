package kotlin.jvm.internal

class BooleanSpreadBuilder(size: Int) : PrimitiveSpreadBuilder(size) {
   private final val values: BooleanArray

   init {
      this.values = new boolean[size];
   }

   protected open fun BooleanArray.getSize(): Int {
      return `$this$getSize`.length;
   }

   public fun add(value: Boolean) {
      var var10000: BooleanArray = this.values;
      var var2: Int = this.getPosition();
      this.setPosition(var2 + 1);
      var10000[var2] = value;
   }

   public fun toArray(): BooleanArray {
      return this.toArray(this.values, new boolean[this.size()]);
   }
}
