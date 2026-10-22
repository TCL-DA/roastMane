package kotlin

import kotlin.internal.InlineOnly

@InlineOnly
@SinceKotlin(version = "1.6")
public inline infix fun <T> Comparable<T>.compareTo(other: T): Int {
   return `$this$compareTo`.compareTo(other);
}

