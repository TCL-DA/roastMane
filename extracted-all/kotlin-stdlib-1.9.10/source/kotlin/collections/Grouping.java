package kotlin.collections

@SinceKotlin(version = "1.1")
interface Grouping<T, K> {
   public abstract fun sourceIterator(): Iterator<Any> {
   }

   public abstract fun keyOf(element: Any): Any {
   }
}
