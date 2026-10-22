package kotlin.jvm.internal

class CharSpreadBuilder(size: Int) : PrimitiveSpreadBuilder(size) {
   private final val values: CharArray

   init {
      this.values = new char[size];
   }

   protected open fun CharArray.getSize(): Int {
      return `$this$getSize`.length;
   }

   public fun add(value: Char) {
      var var10000: CharArray = this.values;
      var var2: Int = this.getPosition();
      this.setPosition(var2 + 1);
      var10000[var2] = value;
   }

   public fun toArray(): CharArray {
      return this.toArray(this.values, new char[this.size()]);
   }
}
