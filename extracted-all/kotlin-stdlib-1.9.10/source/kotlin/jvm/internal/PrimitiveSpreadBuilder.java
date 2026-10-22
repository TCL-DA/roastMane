package kotlin.jvm.internal

abstract class PrimitiveSpreadBuilder<T> {
   private T[] spreads;

   protected final var position: Int
      internal set
   private final val size: Int
   private final val spreads: Array<Any?>

   open fun PrimitiveSpreadBuilder(size: Int) {
      this.size = size;
      this.spreads = (T[])(new Object[this.size]);
   }

   protected abstract fun Any.getSize(): Int {
   }

   public fun addSpread(spreadArgument: Any) {
      this.spreads[this.position++] = (T)spreadArgument;
   }

   protected fun size(): Int {
      var totalLength: Int = 0;
      var var2: IntIterator = new IntRange(0, this.size - 1).iterator();

      while (var2.hasNext()) {
         var i: Int = var2.nextInt();
         totalLength += if (this.spreads[i] != null) this.getSize(this.spreads[i]) else 1;
      }

      return totalLength;
   }

   protected fun toArray(values: Any, result: Any): Any {
      var dstIndex: Int = 0;
      var copyValuesFrom: Int = 0;
      var var5: IntIterator = new IntRange(0, this.size - 1).iterator();

      while (var5.hasNext()) {
         var i: Int = var5.nextInt();
         var spreadArgument: Any = this.spreads[i];
         if (this.spreads[i] != null) {
            if (copyValuesFrom < i) {
               System.arraycopy(values, copyValuesFrom, result, dstIndex, i - copyValuesFrom);
               dstIndex += i - copyValuesFrom;
            }

            var spreadSize: Int = this.getSize((T)spreadArgument);
            System.arraycopy(spreadArgument, 0, result, dstIndex, spreadSize);
            dstIndex += spreadSize;
            copyValuesFrom = i + 1;
         }
      }

      if (copyValuesFrom < this.size) {
         System.arraycopy(values, copyValuesFrom, result, dstIndex, this.size - copyValuesFrom);
      }

      return (T)result;
   }
}
