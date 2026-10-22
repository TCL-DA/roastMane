package kotlin

import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.InlineMarker

// $VF: Class flags could not be determined
internal class StandardKt__SynchronizedKt : StandardKt__StandardKt {
   @InlineOnly
   @JvmStatic
   public inline fun <R> synchronized(lock: Any, block: () -> R): R {
      contract {
         callsInPlace(block, InvocationKind.EXACTLY_ONCE)
      }

      synchronized (lock) {
         var var3: Any = block.invoke();
         InlineMarker.finallyStart(1);
         InlineMarker.finallyEnd(1);
         return (R)var3;
      }
   }

   open fun StandardKt__SynchronizedKt() {
   }
}
