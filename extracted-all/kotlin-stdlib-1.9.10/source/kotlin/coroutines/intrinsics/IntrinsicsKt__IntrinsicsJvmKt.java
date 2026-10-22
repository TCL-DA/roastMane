package kotlin.coroutines.intrinsics

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.jvm.internal.BaseContinuationImpl
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.coroutines.jvm.internal.RestrictedContinuationImpl
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nIntrinsicsJvm.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntrinsicsJvm.kt\nkotlin/coroutines/intrinsics/IntrinsicsKt__IntrinsicsJvmKt\n*L\n1#1,269:1\n204#1,4:270\n225#1:274\n204#1,4:275\n225#1:279\n*S KotlinDebug\n*F\n+ 1 IntrinsicsJvm.kt\nkotlin/coroutines/intrinsics/IntrinsicsKt__IntrinsicsJvmKt\n*L\n130#1:270,4\n130#1:274\n165#1:275,4\n165#1:279\n*E\n"])
// $VF: Class flags could not be determined
internal class IntrinsicsKt__IntrinsicsJvmKt {
   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <T> ((Continuation<T>) -> Any?).startCoroutineUninterceptedOrReturn(completion: Continuation<T>): Any? {
      return if (`$this$startCoroutineUninterceptedOrReturn` !is BaseContinuationImpl)
         IntrinsicsKt.wrapWithContinuationImpl(`$this$startCoroutineUninterceptedOrReturn`, completion)
         else
         (TypeIntrinsics.beforeCheckcastToFunctionOfArity(`$this$startCoroutineUninterceptedOrReturn`, 1) as Function1).invoke(completion);
   }

   @PublishedApi
   @JvmStatic
   internal fun <T> ((Continuation<T>) -> Any?).wrapWithContinuationImpl(completion: Continuation<T>): Any? {
      return (TypeIntrinsics.beforeCheckcastToFunctionOfArity(`$this$wrapWithContinuationImpl`, 1) as Function1)
         .invoke(createSimpleCoroutineForSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt(DebugProbesKt.probeCoroutineCreated(completion)));
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <R, T> ((R, Continuation<T>) -> Any?).startCoroutineUninterceptedOrReturn(receiver: R, completion: Continuation<T>): Any? {
      return if (`$this$startCoroutineUninterceptedOrReturn` !is BaseContinuationImpl)
         IntrinsicsKt.wrapWithContinuationImpl(`$this$startCoroutineUninterceptedOrReturn`, receiver, completion)
         else
         (TypeIntrinsics.beforeCheckcastToFunctionOfArity(`$this$startCoroutineUninterceptedOrReturn`, 2) as Function2).invoke(receiver, completion);
   }

   @PublishedApi
   @JvmStatic
   internal fun <R, T> ((R, Continuation<T>) -> Any?).wrapWithContinuationImpl(receiver: R, completion: Continuation<T>): Any? {
      return (TypeIntrinsics.beforeCheckcastToFunctionOfArity(`$this$wrapWithContinuationImpl`, 2) as Function2)
         .invoke(receiver, createSimpleCoroutineForSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt(DebugProbesKt.probeCoroutineCreated(completion)));
   }

   @InlineOnly
   @JvmStatic
   internal inline fun <R, P, T> ((R, P, Continuation<T>) -> Any?).startCoroutineUninterceptedOrReturn(receiver: R, param: P, completion: Continuation<T>): Any? {
      return if (`$this$startCoroutineUninterceptedOrReturn` !is BaseContinuationImpl)
         IntrinsicsKt.wrapWithContinuationImpl(`$this$startCoroutineUninterceptedOrReturn`, receiver, param, completion)
         else
         (TypeIntrinsics.beforeCheckcastToFunctionOfArity(`$this$startCoroutineUninterceptedOrReturn`, 3) as Function3).invoke(receiver, param, completion);
   }

   @PublishedApi
   @JvmStatic
   internal fun <R, P, T> ((R, P, Continuation<T>) -> Any?).wrapWithContinuationImpl(receiver: R, param: P, completion: Continuation<T>): Any? {
      return (TypeIntrinsics.beforeCheckcastToFunctionOfArity(`$this$wrapWithContinuationImpl`, 3) as Function3)
         .invoke(receiver, param, createSimpleCoroutineForSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt(DebugProbesKt.probeCoroutineCreated(completion)));
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <T> ((Continuation<T>) -> Any?).createCoroutineUnintercepted(completion: Continuation<T>): Continuation<Unit> {
      var probeCompletion: Continuation = DebugProbesKt.probeCoroutineCreated(completion);
      var var10000: Continuation;
      if (`$this$createCoroutineUnintercepted` is BaseContinuationImpl) {
         var10000 = (`$this$createCoroutineUnintercepted` as BaseContinuationImpl).create(probeCompletion);
      } else {
         var `context$iv`: CoroutineContext = probeCompletion.getContext();
         var10000 = if (`context$iv` === EmptyCoroutineContext.INSTANCE)
            new IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$1(
               probeCompletion, `$this$createCoroutineUnintercepted`
            )
            else
            new IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$2(
               probeCompletion, `context$iv`, `$this$createCoroutineUnintercepted`
            );
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <R, T> ((R, Continuation<T>) -> Any?).createCoroutineUnintercepted(receiver: R, completion: Continuation<T>): Continuation<Unit> {
      var probeCompletion: Continuation = DebugProbesKt.probeCoroutineCreated(completion);
      var var10000: Continuation;
      if (`$this$createCoroutineUnintercepted` is BaseContinuationImpl) {
         var10000 = (`$this$createCoroutineUnintercepted` as BaseContinuationImpl).create(receiver, probeCompletion);
      } else {
         var `context$iv`: CoroutineContext = probeCompletion.getContext();
         var10000 = if (`context$iv` === EmptyCoroutineContext.INSTANCE)
            new IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$3(
               probeCompletion, `$this$createCoroutineUnintercepted`, receiver
            )
            else
            new IntrinsicsKt__IntrinsicsJvmKt$createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$IntrinsicsKt__IntrinsicsJvmKt$4(
               probeCompletion, `context$iv`, `$this$createCoroutineUnintercepted`, receiver
            );
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <T> Continuation<T>.intercepted(): Continuation<T> {
      var var10000: ContinuationImpl = `$this$intercepted` as? ContinuationImpl;
      if ((`$this$intercepted` as? ContinuationImpl) != null) {
         var var1: Continuation = var10000.intercepted();
         if (var1 != null) {
            return var1;
         }
      }

      return `$this$intercepted`;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   private inline fun <T> createCoroutineFromSuspendFunction(completion: Continuation<T>, crossinline block: (Continuation<T>) -> Any?): Continuation<Unit> {
      var context: CoroutineContext = completion.getContext();
      return (Continuation<Unit>)(if (context === EmptyCoroutineContext.INSTANCE) new RestrictedContinuationImpl(completion, block) {
         private int label;

         {
            super(`$completion`);
            this.$block = `$block`;
         }

         @Nullable
         @Override
         protected Object invokeSuspend(@NotNull Object result) {
            var var10000: Any;
            switch (this.label) {
               case 0:
                  this.label = 1;
                  ResultKt.throwOnFailure(result);
                  var10000 = this.$block.invoke(this);
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
      } else new ContinuationImpl(completion, context, block) {
         private int label;

         {
            super(`$completion`, `$context`);
            this.$block = `$block`;
         }

         @Nullable
         @Override
         protected Object invokeSuspend(@NotNull Object result) {
            var var10000: Any;
            switch (this.label) {
               case 0:
                  this.label = 1;
                  ResultKt.throwOnFailure(result);
                  var10000 = this.$block.invoke(this);
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
      });
   }

   @JvmStatic
   private fun <T> createSimpleCoroutineForSuspendFunction(completion: Continuation<T>): Continuation<T> {
      var context: CoroutineContext = completion.getContext();
      return (Continuation<T>)(if (context === EmptyCoroutineContext.INSTANCE) new RestrictedContinuationImpl(completion) {
         {
            super(`$completion`);
         }

         @Nullable
         @Override
         protected Object invokeSuspend(@NotNull Object result) {
            ResultKt.throwOnFailure(result);
            return result;
         }
      } else new ContinuationImpl(completion, context) {
         {
            super(`$completion`, `$context`);
         }

         @Nullable
         @Override
         protected Object invokeSuspend(@NotNull Object result) {
            ResultKt.throwOnFailure(result);
            return result;
         }
      });
   }

   open fun IntrinsicsKt__IntrinsicsJvmKt() {
   }
}
