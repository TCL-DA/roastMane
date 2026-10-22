package kotlin.properties

import kotlin.reflect.KProperty

fun interface ReadOnlyProperty<T, V> {
   public abstract operator fun getValue(thisRef: Any, property: KProperty<*>): Any {
   }
}
