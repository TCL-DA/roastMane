package kotlin.reflect

interface KMutableProperty<V> : KProperty<V> {
   public val setter: kotlin.reflect.KMutableProperty.Setter<Any>

   interface Setter<V> : KProperty.Accessor<V>, KFunction<Unit> {
   }
}
