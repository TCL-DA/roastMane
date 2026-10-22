package kotlin

import kotlin.internal.InlineOnly
import kotlin.reflect.KProperty

// $VF: Class flags could not be determined
internal class LazyKt__LazyKt : LazyKt__LazyJVMKt {
   @JvmStatic
   public fun <T> lazyOf(value: T): Lazy<T> {
      return (Lazy<T>)(new InitializedLazyImpl<>(value));
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> Lazy<T>.getValue(thisRef: Any?, property: KProperty<*>): T {
      return (T)`$this$getValue`.getValue();
   }

   open fun LazyKt__LazyKt() {
   }
}
