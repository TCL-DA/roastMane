package kotlin.properties

import kotlin.reflect.KProperty

@SinceKotlin(version = "1.4")
fun interface PropertyDelegateProvider<T, D> {
   public abstract operator fun provideDelegate(thisRef: Any, property: KProperty<*>): Any {
   }
}
