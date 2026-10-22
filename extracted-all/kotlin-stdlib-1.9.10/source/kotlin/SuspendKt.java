package kotlin

import kotlin.coroutines.Continuation
import kotlin.internal.InlineOnly

@InlineOnly
@SinceKotlin(version = "1.2")
public inline fun <R> suspend(noinline block: (Continuation<R>) -> Any?): (Continuation<R>) -> Any? {
   return block;
}

