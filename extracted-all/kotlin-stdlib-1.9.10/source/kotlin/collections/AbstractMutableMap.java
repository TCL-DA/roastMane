package kotlin.collections

import kotlin.jvm.internal.markers.KMutableMap

@SinceKotlin(version = "1.1")
abstract class AbstractMutableMap<K, V> : java.util.AbstractMap<K, V>, java.util.Map<K, V>, KMutableMap {
   open fun AbstractMutableMap() {
   }

   public abstract override fun put(key: Any, value: Any): Any? {
   }

   abstract fun getEntries(): java.util.Set
}
