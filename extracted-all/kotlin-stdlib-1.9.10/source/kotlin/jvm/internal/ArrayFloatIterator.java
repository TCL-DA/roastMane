package kotlin.jvm.internal

import java.util.NoSuchElementException

private class ArrayFloatIterator(array: FloatArray) : FloatIterator {
   private final val array: FloatArray
   private final var index: Int

   init {
      this.array = array;
   }

   public override operator fun hasNext(): Boolean {
      return this.index < this.array.length;
   }

   public override fun nextFloat(): Float {
      try {
         return this.array[this.index++];
      } catch (ArrayIndexOutOfBoundsException var3) {
         this.index--;
         throw new NoSuchElementException(var3.getMessage());
      }
   }
}
