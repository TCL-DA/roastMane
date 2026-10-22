package kotlin.collections

import kotlin.jvm.internal.markers.KMutableMap

private interface MutableMapWithDefault<K, V> : java.util.Map<K, V>, MapWithDefault<K, V>, KMutableMap {
   public val map: MutableMap<Any, Any>
}
