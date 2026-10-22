package kotlin.coroutines

import kotlin.coroutines.CoroutineContext.Element
import kotlin.coroutines.CoroutineContext.Key

@SinceKotlin(version = "1.3")
@ExperimentalStdlibApi
abstract class AbstractCoroutineContextKey<B extends CoroutineContext.Element, E extends B> : CoroutineContext.Key<E> {
   private final val safeCast: (Element) -> Any?
   private final val topmostKey: Key<*>

   open fun AbstractCoroutineContextKey(baseKey: CoroutineContextKey<B>, safeCast: (CoroutineContext.Element?) -> E) {
      this.safeCast = safeCast;
      this.topmostKey = if (baseKey is AbstractCoroutineContextKey) (baseKey as AbstractCoroutineContextKey).topmostKey else baseKey;
   }

   internal fun tryCast(element: Element): Any? {
      return (E)(this.safeCast.invoke(element) as CoroutineContext.Element);
   }

   internal fun isSubKey(key: Key<*>): Boolean {
      return key === this || this.topmostKey === key;
   }
}
