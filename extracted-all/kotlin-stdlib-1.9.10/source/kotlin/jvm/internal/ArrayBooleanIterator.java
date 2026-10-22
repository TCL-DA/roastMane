package kotlin.jvm.internal

import java.util.NoSuchElementException

private class ArrayBooleanIterator(array: BooleanArray) : BooleanIterator {
   private final val array: BooleanArray
   private final var index: Int

   init {
      this.array = array;
   }

   public override operator fun hasNext(): Boolean {
      return this.index < this.array.length;
   }

   public override fun nextBoolean(): Boolean {
      try {
         return this.array[this.index++];
      } catch (ArrayIndexOutOfBoundsException var3) {
         this.index--;
         throw new NoSuchElementException(var3.getMessage());
      }
   }
}
