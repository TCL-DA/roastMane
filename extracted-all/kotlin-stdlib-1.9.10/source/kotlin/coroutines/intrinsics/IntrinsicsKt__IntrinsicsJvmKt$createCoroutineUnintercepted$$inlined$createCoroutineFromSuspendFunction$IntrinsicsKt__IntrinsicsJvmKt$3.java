package kotlin.coroutines.intrinsics

import kotlin.coroutines.Continuation
import kotlin.coroutines.jvm.internal.RestrictedContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics

@SourceDebugExtension(["SMAP\nIntrinsicsJvm.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntrinsicsJvm.kt\nkotlin/coroutines/intrinsics/IntrinsicsKt__IntrinsicsJvmKt$createCoroutineFromSuspendFunction$1\n+ 2 IntrinsicsJvm.kt\nkotlin/coroutines/intrinsics/IntrinsicsKt__IntrinsicsJvmKt\n*L\n1#1,269:1\n166#2:270\n*E\n"])
public class `IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$3`
   : RestrictedContinuationImpl {
   private final var label: Int

   fun `IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$3`(
      `$completion`: Continuation, var2: Function2, var3: Any
   ) {
      super(`$completion`);
      this.$this_createCoroutineUnintercepted$inlined = var2;
      this.$receiver$inlined = var3;
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
