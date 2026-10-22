package kotlin.coroutines.jvm.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

internal object CompletedContinuation private constructor() : Continuation<Object> {
   @JvmStatic
   public CompletedContinuation INSTANCE = new CompletedContinuation();

   public open val context: CoroutineContext
      public open get() {
         throw new IllegalStateException("This continuation is already complete".toString());
      }



   public override fun resumeWith(result: Result<Any?>) {
      throw new IllegalStateException("This continuation is already complete".toString());
   }

   public override fun toString(): String {
      return "This continuation is already complete";
   }
}
