@file:JvmName(name = "MapAccessorsKt")

package kotlin.collections

import kotlin.internal.InlineOnly
import kotlin.reflect.KProperty

@InlineOnly
public inline operator fun <V, V1 : V> Map<in String, V>.getValue(thisRef: Any?, property: KProperty<*>): V1 {
   return (V1)MapsKt.getOrImplicitDefaultNullable(`$this$getValue`, property.getName());
}

@JvmName(name = "getVar")
@InlineOnly
public inline operator fun <V, V1 : V> MutableMap<in String, out V>.getValue(thisRef: Any?, property: KProperty<*>): V1 {
   return (V1)MapsKt.getOrImplicitDefaultNullable(`$this$getValue`, property.getName());
}

@InlineOnly
public inline operator fun <V> MutableMap<in String, in V>.setValue(thisRef: Any?, property: KProperty<*>, value: V) {
   `$this$setValue`.put(property.getName(), value);
}

