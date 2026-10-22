package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

abstract class FloatIterator : java.util.Iterator<java.lang.Float>, KMappedMarker {
   public operator fun next(): Float {
      return this.nextFloat();
   }

   public abstract fun nextFloat(): Float {
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
