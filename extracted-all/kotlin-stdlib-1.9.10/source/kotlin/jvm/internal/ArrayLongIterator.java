package kotlin.jvm.internal

import java.util.NoSuchElementException

private class ArrayLongIterator(array: LongArray) : LongIterator {
   private final val array: LongArray
   private final var index: Int

   init {
      this.array = array;
   }

   public override operator fun hasNext(): Boolean {
      return this.index < this.array.length;
   }

   public override fun nextLong(): Long {
      try {
         return this.array[this.index++];
      } catch (ArrayIndexOutOfBoundsException var4) {
         this.index--;
         throw new NoSuchElementException(var4.getMessage());
      }
   }
}
