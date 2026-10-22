package kotlin.sequences

interface Sequence<T> {
   public abstract operator fun iterator(): Iterator<Any> {
   }
}
