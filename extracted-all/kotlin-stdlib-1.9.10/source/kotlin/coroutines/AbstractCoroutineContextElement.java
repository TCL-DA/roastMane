package kotlin.coroutines

import kotlin.coroutines.CoroutineContext.Key

@SinceKotlin(version = "1.3")
abstract class AbstractCoroutineContextElement : CoroutineContext.Element {
   public open val key: Key<*>

   open fun AbstractCoroutineContextElement(key: CoroutineContextKey<?>) {
      this.key = key;
   }

   override fun <E extends CoroutineContext.Element> get(key: CoroutineContextKey<E>): E? {
      return CoroutineContext.Element.DefaultImpls.get(this, key);
   }

   override fun <R> fold(initial: R, operation: (R?, CoroutineContext.Element?) -> R): R {
      return CoroutineContext.Element.DefaultImpls.fold(this, (R)initial, operation);
   }

   override fun minusKey(key: CoroutineContextKey<?>): CoroutineContext {
      return CoroutineContext.Element.DefaultImpls.minusKey(this, key);
   }

   override fun plus(context: CoroutineContext): CoroutineContext {
      return CoroutineContext.Element.DefaultImpls.plus(this, context);
   }
}
