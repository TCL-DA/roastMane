package kotlin

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nContinuation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Continuation.kt\nkotlin/coroutines/ContinuationKt$Continuation$1\n+ 2 DeepRecursive.kt\nkotlin/DeepRecursiveScopeImpl\n*L\n1#1,161:1\n184#2,6:162\n*E\n"])
public class `DeepRecursiveScopeImpl$crossFunctionCompletion$$inlined$Continuation$1` : Continuation<Object> {
   public open val context: CoroutineContext
      public open get() {
         return this.$context;
      }


   fun `DeepRecursiveScopeImpl$crossFunctionCompletion$$inlined$Continuation$1`(
      `$context`: CoroutineContext, var2: DeepRecursiveScopeImpl, var3: Function3, var4: Continuation
   ) {
      this.$context = `$context`;
      this.this$0 = var2;
      this.$currentFunction$inlined = var3;
      this.$cont$inlined = var4;
   }

   public override fun resumeWith(result: Result<Any>) {
      DeepRecursiveScopeImpl.access$setFunction$p(this.this$0, this.$currentFunction$inlined);
      DeepRecursiveScopeImpl.access$setCont$p(this.this$0, this.$cont$inlined);
      DeepRecursiveScopeImpl.access$setResult$p(this.this$0, result);
   }
}
