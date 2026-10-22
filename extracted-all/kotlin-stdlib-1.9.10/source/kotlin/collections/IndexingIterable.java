package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

internal class IndexingIterable<T>(iteratorFactory: () -> Iterator<Any>) : java.lang.Iterable<IndexedValue<? extends T>>, KMappedMarker {
   private final val iteratorFactory: () -> Iterator<Any>

   init {
      this.iteratorFactory = iteratorFactory;
   }

   public override operator fun iterator(): Iterator<IndexedValue<Any>> {
      return new IndexingIterator<>(this.iteratorFactory.invoke());
   }
}
