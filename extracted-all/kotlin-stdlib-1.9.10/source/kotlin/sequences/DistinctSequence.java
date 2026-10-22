package kotlin.sequences

internal class DistinctSequence<T, K>(source: Sequence<Any>, keySelector: (Any) -> Any) : Sequence<T> {
   private final val keySelector: (Any) -> Any
   private final val source: Sequence<Any>

   init {
      this.source = source;
      this.keySelector = keySelector;
   }

   public override operator fun iterator(): Iterator<Any> {
      return (new DistinctIterator<>(this.source.iterator(), this.keySelector)) as java.util.Iterator<T>;
   }
}
