package kotlin.coroutines.intrinsics

import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class IntrinsicsKt__IntrinsicsKt : IntrinsicsKt__IntrinsicsJvmKt {
   public final val COROUTINE_SUSPENDED: Any
      public final get() {
         return CoroutineSingletons.COROUTINE_SUSPENDED;
      }


   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public suspend inline fun <T> suspendCoroutineUninterceptedOrReturn(crossinline block: (Continuation<T>) -> Any?): T {
      contract {
         callsInPlace(block, InvocationKind.EXACTLY_ONCE)
      }

      throw new NotImplementedError("Implementation of suspendCoroutineUninterceptedOrReturn is intrinsic");
   }

   open fun IntrinsicsKt__IntrinsicsKt() {
   }
}
