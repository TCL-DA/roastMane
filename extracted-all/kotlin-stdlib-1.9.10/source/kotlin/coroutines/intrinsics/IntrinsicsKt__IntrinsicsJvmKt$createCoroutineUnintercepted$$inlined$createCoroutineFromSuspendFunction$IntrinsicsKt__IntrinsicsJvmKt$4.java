package kotlin.coroutines.intrinsics

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics

@SourceDebugExtension(["SMAP\nIntrinsicsJvm.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntrinsicsJvm.kt\nkotlin/coroutines/intrinsics/IntrinsicsKt__IntrinsicsJvmKt$createCoroutineFromSuspendFunction$2\n+ 2 IntrinsicsJvm.kt\nkotlin/coroutines/intrinsics/IntrinsicsKt__IntrinsicsJvmKt\n*L\n1#1,269:1\n166#2:270\n*E\n"])
public class `IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$4`
   : ContinuationImpl {
   private final var label: Int

   fun `IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$4`(
      `$completion`: Continuation, `$context`: CoroutineContext, var3: Function2, var4: Any
   ) {
      super(`$completion`, `$context`);
      this.$this_createCoroutineUnintercepted$inlined = var3;
      this.$receiver$inlined = var4;
   }

   protected override fun invokeSuspend(result: Result<Any?>): Any? {
      var var10000: Any;
      switch (this.label) {
         case 0:
            this.label = 1;
            ResultKt.throwOnFailure(result);
            var it: Continuation = this;
            var10000 = (TypeIntrinsics.beforeCheckcastToFunctionOfArity(this.$this_createCoroutineUnintercepted$inlined, 2) as Function2)
               .invoke(this.$receiver$inlined, it);
            break;
         case 1:
            this.label = 2;
            ResultKt.throwOnFailure(result);
            var10000 = result;
            break;
         default:
            throw new IllegalStateException("This coroutine had already completed".toString());
      }

      return var10000;
   }
}
