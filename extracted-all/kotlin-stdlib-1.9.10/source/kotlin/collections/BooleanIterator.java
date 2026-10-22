package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

abstract class BooleanIterator : java.util.Iterator<java.lang.Boolean>, KMappedMarker {
   public operator fun next(): Boolean {
      return this.nextBoolean();
   }

   public abstract fun nextBoolean(): Boolean {
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
