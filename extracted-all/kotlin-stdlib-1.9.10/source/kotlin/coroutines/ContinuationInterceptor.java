package kotlin.coroutines

import kotlin.coroutines.CoroutineContext.Element

@SinceKotlin(version = "1.3")
interface ContinuationInterceptor : CoroutineContext.Element {
   @JvmStatic
   ContinuationInterceptor.Key Key = ContinuationInterceptor.Key.$$INSTANCE;

   public abstract fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
   }

   public open fun releaseInterceptedContinuation(continuation: Continuation<*>) {
   }

   public override operator fun <E : Element> get(key: kotlin.coroutines.CoroutineContext.Key<E>): E? {
   }

   public override fun minusKey(key: kotlin.coroutines.CoroutineContext.Key<*>): CoroutineContext {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun releaseInterceptedContinuation(`$this`: ContinuationInterceptor, continuation: Continuation<?>) {
      }

      @JvmStatic
      fun <E extends CoroutineContext.Element> get(`$this`: ContinuationInterceptor, key: CoroutineContextKey<E>): E? {
         if (key is AbstractCoroutineContextKey) {
            var var3: CoroutineContext.Element;
            if ((key as AbstractCoroutineContextKey).isSubKey$kotlin_stdlib(`$this`.getKey())) {
               var var2: CoroutineContext.Element = (key as AbstractCoroutineContextKey).tryCast$kotlin_stdlib(`$this`);
               var3 = if (var2 is CoroutineContext.Element) var2 else null;
            } else {
               var3 = null;
            }

            return (E)var3;
         } else {
            var var10000: CoroutineContext.Element;
            if (ContinuationInterceptor.Key === key) {
               var10000 = `$this`;
            } else {
               var10000 = null;
            }

            return (E)var10000;
         }
      }

      @JvmStatic
      fun minusKey(`$this`: ContinuationInterceptor, key: CoroutineContextKey<?>): CoroutineContext {
         if (key !is AbstractCoroutineContextKey) {
            return if (ContinuationInterceptor.Key === key) EmptyCoroutineContext.INSTANCE else `$this`;
         } else {
            return if ((key as AbstractCoroutineContextKey).isSubKey$kotlin_stdlib(`$this`.getKey())
                  && (key as AbstractCoroutineContextKey).tryCast$kotlin_stdlib(`$this`) != null)
               EmptyCoroutineContext.INSTANCE
               else
               `$this`;
         }
      }

      @JvmStatic
      fun <R> fold(`$this`: ContinuationInterceptor, initial: R, operation: (R?, CoroutineContext.Element?) -> R): R {
         return CoroutineContext.Element.DefaultImpls.fold(`$this`, (R)initial, operation);
      }

      @JvmStatic
      fun plus(`$this`: ContinuationInterceptor, context: CoroutineContext): CoroutineContext {
         return CoroutineContext.Element.DefaultImpls.plus(`$this`, context);
      }
   }

   companion object Key private constructor() : CoroutineContext.Key<ContinuationInterceptor> {
   }
}
