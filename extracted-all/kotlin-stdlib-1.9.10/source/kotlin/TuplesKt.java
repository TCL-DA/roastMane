@file:JvmName(name = "TuplesKt")

package kotlin

public infix fun <A, B> A.to(that: B): Pair<A, B> {
   return (Pair<A, B>)(new Pair<>(`$this$to`, that));
}

public fun <T> Pair<T, T>.toList(): List<T> {
   return (java.util.List<T>)CollectionsKt.listOf(new Object[]{`$this$toList`.getFirst(), `$this$toList`.getSecond()});
}

public fun <T> Triple<T, T, T>.toList(): List<T> {
   return (java.util.List<T>)CollectionsKt.listOf(new Object[]{`$this$toList`.getFirst(), `$this$toList`.getSecond(), `$this$toList`.getThird()});
}

