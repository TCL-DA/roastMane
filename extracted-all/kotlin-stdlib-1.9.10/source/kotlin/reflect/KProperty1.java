package kotlin.reflect

import kotlin.jvm.functions.Function1

interface KProperty1<T, V> : KProperty<V>, Function1<T, V> {
   public val getter: KProperty1.Getter<Any, Any>

   public abstract fun get(receiver: Any): Any {
   }

   @SinceKotlin(version = "1.1")
   public abstract fun getDelegate(receiver: Any): Any? {
   }

   interface Getter<T, V> : KProperty.Getter<V>, Function1<T, V> {
   }
}
