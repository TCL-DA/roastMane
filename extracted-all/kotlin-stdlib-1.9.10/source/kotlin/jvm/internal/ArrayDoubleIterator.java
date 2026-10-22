package kotlin.jvm.internal

import java.util.NoSuchElementException

private class ArrayDoubleIterator(array: DoubleArray) : DoubleIterator {
   private final val array: DoubleArray
   private final var index: Int

   init {
      this.array = array;
   }

   public override operator fun hasNext(): Boolean {
      return this.index < this.array.length;
   }

   public override fun nextDouble(): Double {
      try {
         return this.array[this.index++];
      } catch (ArrayIndexOutOfBoundsException var4) {
         this.index--;
         throw new NoSuchElementException(var4.getMessage());
      }
   }
}
