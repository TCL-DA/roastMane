package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

abstract class ShortIterator : java.util.Iterator<java.lang.Short>, KMappedMarker {
   public operator fun next(): Short {
      return this.nextShort();
   }

   public abstract fun nextShort(): Short {
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
