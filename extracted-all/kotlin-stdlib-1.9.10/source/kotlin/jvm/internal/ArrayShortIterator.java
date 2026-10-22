package kotlin.jvm.internal

import java.util.NoSuchElementException

private class ArrayShortIterator(array: ShortArray) : ShortIterator {
   private final val array: ShortArray
   private final var index: Int

   init {
      this.array = array;
   }

   public override operator fun hasNext(): Boolean {
      return this.index < this.array.length;
   }

   public override fun nextShort(): Short {
      try {
         return this.array[this.index++];
      } catch (ArrayIndexOutOfBoundsException var3) {
         this.index--;
         throw new NoSuchElementException(var3.getMessage());
      }
   }
}
