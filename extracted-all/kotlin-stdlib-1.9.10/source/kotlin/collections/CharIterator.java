package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

abstract class CharIterator : java.util.Iterator<Character>, KMappedMarker {
   public operator fun next(): Char {
      return this.nextChar();
   }

   public abstract fun nextChar(): Char {
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
