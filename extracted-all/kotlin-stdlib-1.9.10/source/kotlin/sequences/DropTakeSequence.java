package kotlin.sequences

internal interface DropTakeSequence<T> : Sequence<T> {
   public abstract fun drop(n: Int): Sequence<Any> {
   }

   public abstract fun take(n: Int): Sequence<Any> {
   }
}
