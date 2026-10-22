package kotlin

import kotlin.jvm.internal.Intrinsics

@JvmSynthetic
public inline fun <reified T> emptyArray(): Array<T> {
   Intrinsics.reifiedOperationMarker(0, "T?");
   return (T[])(new Object[0]);
}

