package kotlin.jvm.internal

class ShortSpreadBuilder(size: Int) : PrimitiveSpreadBuilder(size) {
   private final val values: ShortArray

   init {
      this.values = new short[size];
   }

   protected open fun ShortArray.getSize(): Int {
      return `$this$getSize`.length;
   }

   public fun add(value: Short) {
      var var10000: ShortArray = this.values;
      var var2: Int = this.getPosition();
      this.setPosition(var2 + 1);
      var10000[var2] = value;
   }

   public fun toArray(): ShortArray {
      return this.toArray(this.values, new short[this.size()]);
   }
}
