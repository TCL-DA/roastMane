@file:JvmName(name = "CollectionsJDK8Kt")

package kotlin.collections.jdk8

import kotlin.internal.InlineOnly
import kotlin.jvm.internal.TypeIntrinsics

@SinceKotlin(version = "1.2")
@InlineOnly
public inline fun <K, V> Map<out K, V>.getOrDefault(key: K, defaultValue: V): V {
   return (V)`$this$getOrDefault`.getOrDefault(key, defaultValue);
}

@SinceKotlin(version = "1.2")
@InlineOnly
public inline fun <K, V> MutableMap<out K, out V>.remove(key: K, value: V): Boolean {
   return TypeIntrinsics.asMutableMap(`$this$remove`).remove(key, value);
}

