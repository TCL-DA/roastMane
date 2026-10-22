package kotlin.reflect

interface KProperty<V> : KCallable<V> {
   public val getter: kotlin.reflect.KProperty.Getter<Any>
   public val isConst: Boolean
   public val isLateinit: Boolean

   interface Accessor<V> {
      public val property: KProperty<Any>
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
   }

   interface Getter<V> : KProperty.Accessor<V>, KFunction<V> {
   }
}
