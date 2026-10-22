package kotlin.coroutines.jvm.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private class RunSuspend : Continuation<Unit> {
   public open val context: CoroutineContext
      public open get() {
         return EmptyCoroutineContext.INSTANCE;
      }

   public final var result: Result<Unit>?
      internal set


   public override fun resumeWith(result: Result<Unit>) {
      synchronized (this) {
         this.result = Result.box-impl(result);
         this.notifyAll();
      }
   }

   public fun await() {
      synchronized (this) {
         while (true) {
            if (this.result != null) {
               ResultKt.throwOnFailure(this.result.unbox-impl());
               return;
            }

            this.wait();
         }
      }
   }
}
