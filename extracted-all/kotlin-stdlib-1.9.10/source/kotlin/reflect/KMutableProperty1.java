package kotlin.reflect

import kotlin.jvm.functions.Function2

interface KMutableProperty1<T, V> : KProperty1<T, V>, KMutableProperty<V> {
   public val setter: KMutableProperty1.Setter<Any, Any>

   public abstract fun set(receiver: Any, value: Any) {
   }

   interface Setter<T, V> : KMutableProperty.Setter<V>, Function2<T, V, Unit> {
   }
}
