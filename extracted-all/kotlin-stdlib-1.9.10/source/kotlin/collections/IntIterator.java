package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

abstract class IntIterator : java.util.Iterator<Integer>, KMappedMarker {
   public operator fun next(): Int {
      return this.nextInt();
   }

   public abstract fun nextInt(): Int {
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
