package kotlin.properties

import kotlin.reflect.KProperty

abstract class ObservableProperty<V> : ReadWriteProperty<Object, V> {
   private final var value: Any

   open fun ObservableProperty(initialValue: V) {
      this.value = (V)initialValue;
   }

   protected open fun beforeChange(property: KProperty<*>, oldValue: Any, newValue: Any): Boolean {
      return true;
   }

   protected open fun afterChange(property: KProperty<*>, oldValue: Any, newValue: Any) {
   }

   public override operator fun getValue(thisRef: Any?, property: KProperty<*>): Any {
      return this.value;
   }

   public override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Any) {
      var oldValue: Any = this.value;
      if (this.beforeChange(property, this.value, value)) {
         this.value = (V)value;
         this.afterChange(property, (V)oldValue, (V)value);
      }
   }

   public override fun toString(): String {
      return "ObservableProperty(value=" + this.value + ')';
   }
}
