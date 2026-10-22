package kotlin.jvm.internal

import java.util.NoSuchElementException
import kotlin.jvm.internal.markers.KMappedMarker

private class ArrayIterator<T>(vararg array: Any) : java.util.Iterator<T>, KMappedMarker {
   private T[] array;

   public final val array: Array<Any>
   private final var index: Int

   init {
      this.array = (T[])array;
   }

   public override operator fun hasNext(): Boolean {
      return this.index < this.array.length;
   }

   public override operator fun next(): Any {
      try {
         return this.array[this.index++];
      } catch (ArrayIndexOutOfBoundsException var3) {
         this.index--;
         throw new NoSuchElementException(var3.getMessage());
      }
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
