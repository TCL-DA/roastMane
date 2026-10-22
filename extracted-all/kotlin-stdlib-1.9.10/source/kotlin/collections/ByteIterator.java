package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

abstract class ByteIterator : java.util.Iterator<java.lang.Byte>, KMappedMarker {
   public operator fun next(): Byte {
      return this.nextByte();
   }

   public abstract fun nextByte(): Byte {
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
