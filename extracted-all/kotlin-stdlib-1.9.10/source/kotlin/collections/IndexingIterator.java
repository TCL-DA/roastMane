package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

internal class IndexingIterator<T>(iterator: Iterator<Any>) : java.util.Iterator<IndexedValue<? extends T>>, KMappedMarker {
   private final var index: Int
   private final val iterator: Iterator<Any>

   init {
      this.iterator = iterator;
   }

   public override operator fun hasNext(): Boolean {
      return this.iterator.hasNext();
   }

   public operator fun next(): IndexedValue<Any> {
      var var10000: IndexedValue = new IndexedValue;
      var var1: Int = this.index++;
      if (var1 < 0) {
         CollectionsKt.throwIndexOverflow();
      }

      var10000./* $VF: Unable to resugar constructor */<init>(var1, this.iterator.next());
      return var10000;
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
