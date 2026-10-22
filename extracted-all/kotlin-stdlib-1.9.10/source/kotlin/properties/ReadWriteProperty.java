package kotlin.properties

import kotlin.reflect.KProperty

interface ReadWriteProperty<T, V> : ReadOnlyProperty<T, V> {
   public abstract override operator fun getValue(thisRef: Any, property: KProperty<*>): Any {
   }

   public abstract operator fun setValue(thisRef: Any, property: KProperty<*>, value: Any) {
   }
}
