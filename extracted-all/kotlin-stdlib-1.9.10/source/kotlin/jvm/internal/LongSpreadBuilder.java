package kotlin.jvm.internal

class LongSpreadBuilder(size: Int) : PrimitiveSpreadBuilder(size) {
   private final val values: LongArray

   init {
      this.values = new long[size];
   }

   protected open fun LongArray.getSize(): Int {
      return `$this$getSize`.length;
   }

   public fun add(value: Long) {
      var var10000: LongArray = this.values;
      var var3: Int = this.getPosition();
      this.setPosition(var3 + 1);
      var10000[var3] = value;
   }

   public fun toArray(): LongArray {
      return this.toArray(this.values, new long[this.size()]);
   }
}
