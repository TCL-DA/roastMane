package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

private interface MapWithDefault<K, V> : java.util.Map<K, V>, KMappedMarker {
   public val map: Map<Any, Any>

   public abstract fun getOrImplicitDefault(key: Any): Any {
   }
}
