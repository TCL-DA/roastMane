package kotlin.jvm.internal

class FloatSpreadBuilder(size: Int) : PrimitiveSpreadBuilder(size) {
   private final val values: FloatArray

   init {
      this.values = new float[size];
   }

   protected open fun FloatArray.getSize(): Int {
      return `$this$getSize`.length;
   }

   public fun add(value: Float) {
      var var10000: FloatArray = this.values;
      var var2: Int = this.getPosition();
      this.setPosition(var2 + 1);
      var10000[var2] = value;
   }

   public fun toArray(): FloatArray {
      return this.toArray(this.values, new float[this.size()]);
   }
}
