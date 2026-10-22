package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

abstract class LongIterator : java.util.Iterator<java.lang.Long>, KMappedMarker {
   public operator fun next(): Long {
      return this.nextLong();
   }

   public abstract fun nextLong(): Long {
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
