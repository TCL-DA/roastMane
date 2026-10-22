package kotlin.reflect

import kotlin.jvm.functions.Function1

interface KMutableProperty0<V> : KProperty0<V>, KMutableProperty<V> {
   public val setter: KMutableProperty0.Setter<Any>

   public abstract fun set(value: Any) {
   }

   interface Setter<V> : KMutableProperty.Setter<V>, Function1<V, Unit> {
   }
}
