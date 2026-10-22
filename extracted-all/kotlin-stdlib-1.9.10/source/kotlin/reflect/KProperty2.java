package kotlin.reflect

import kotlin.jvm.functions.Function2

interface KProperty2<D, E, V> : KProperty<V>, Function2<D, E, V> {
   public val getter: KProperty2.Getter<Any, Any, Any>

   public abstract fun get(receiver1: Any, receiver2: Any): Any {
   }

   @SinceKotlin(version = "1.1")
   public abstract fun getDelegate(receiver1: Any, receiver2: Any): Any? {
   }

   interface Getter<D, E, V> : KProperty.Getter<V>, Function2<D, E, V> {
   }
}
