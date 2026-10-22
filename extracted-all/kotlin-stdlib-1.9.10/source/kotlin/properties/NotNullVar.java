package kotlin.properties

import kotlin.reflect.KProperty

private class NotNullVar<T> : ReadWriteProperty<Object, T> {
   private final var value: Any?


   public override operator fun getValue(thisRef: Any?, property: KProperty<*>): Any {
      if (this.value == null) {
         throw new IllegalStateException("Property " + property.getName() + " should be initialized before get.");
      } else {
         return this.value;
      }
   }

   public override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Any) {
      this.value = (T)value;
   }

   public override fun toString(): String {
      return "NotNullProperty(" + (if (this.value != null) "value=" + this.value else "value not initialized yet") + ')';
   }
}
