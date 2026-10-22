package kotlin

import kotlin.internal.InlineOnly
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

@SinceKotlin(version = "1.4")
@InlineOnly
public inline operator fun <V> KProperty0<V>.getValue(thisRef: Any?, property: KProperty<*>): V {
   return (V)`$this$getValue`.get();
}

@SinceKotlin(version = "1.4")
@InlineOnly
public inline operator fun <V> KMutableProperty0<V>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
   `$this$setValue`.set(value);
}

@SinceKotlin(version = "1.4")
@InlineOnly
public inline operator fun <T, V> KProperty1<T, V>.getValue(thisRef: T, property: KProperty<*>): V {
   return (V)`$this$getValue`.get(thisRef);
}

@SinceKotlin(version = "1.4")
@InlineOnly
public inline operator fun <T, V> KMutableProperty1<T, V>.setValue(thisRef: T, property: KProperty<*>, value: V) {
   `$this$setValue`.set(thisRef, value);
}

