package kotlin.reflect

import kotlin.jvm.functions.Function0

interface KProperty0<V> : KProperty<V>, Function0<V> {
   public val getter: KProperty0.Getter<Any>

   public abstract fun get(): Any {
   }

   @SinceKotlin(version = "1.1")
   public abstract fun getDelegate(): Any? {
   }

   interface Getter<V> : KProperty.Getter<V>, Function0<V> {
   }
}
