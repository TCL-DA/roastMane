package kotlin.jvm.internal

public fun <T> iterator(array: Array<T>): Iterator<T> {
   return (java.util.Iterator<T>)(new ArrayIterator<>(array));
}

