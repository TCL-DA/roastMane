package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

abstract class DoubleIterator : java.util.Iterator<java.lang.Double>, KMappedMarker {
   public operator fun next(): Double {
      return this.nextDouble();
   }

   public abstract fun nextDouble(): Double {
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
