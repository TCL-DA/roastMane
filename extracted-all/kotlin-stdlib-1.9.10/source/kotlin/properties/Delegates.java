package kotlin.properties

import kotlin.reflect.KProperty
import org.jetbrains.annotations.NotNull

object Delegates private constructor() {
   @JvmStatic
   public Delegates INSTANCE = new Delegates();


   public fun <T : Any> notNull(): ReadWriteProperty<Any?, T> {
      return new NotNullVar();
   }

   public inline fun <T> observable(initialValue: T, crossinline onChange: (KProperty<*>, T, T) -> Unit): ReadWriteProperty<Any?, T> {
      return (new ObservableProperty<T>(initialValue, onChange) {
         {
            super(`$initialValue`);
            this.$onChange = `$onChange`;
         }

         @Override
         protected void afterChange(@NotNull KProperty<?> property, T oldValue, T newValue) {
            this.$onChange.invoke(property, oldValue, newValue);
         }
      }) as ReadWriteProperty<Object, T>;
   }

   public inline fun <T> vetoable(initialValue: T, crossinline onChange: (KProperty<*>, T, T) -> Boolean): ReadWriteProperty<Any?, T> {
      return (new ObservableProperty<T>(initialValue, onChange) {
         {
            super(`$initialValue`);
            this.$onChange = `$onChange`;
         }

         @Override
         protected boolean beforeChange(@NotNull KProperty<?> property, T oldValue, T newValue) {
            return this.$onChange.invoke(property, oldValue, newValue);
         }
      }) as ReadWriteProperty<Object, T>;
   }
}
