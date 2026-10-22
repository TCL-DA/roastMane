@file:JvmName(name = "KotlinExtensions")

@file:SourceDebugExtension(["SMAP\nKotlinExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 KotlinExtensions.kt\nretrofit2/KotlinExtensions\n+ 2 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n*L\n1#1,126:1\n310#2,11:127\n310#2,11:138\n310#2,11:149\n*S KotlinDebug\n*F\n+ 1 KotlinExtensions.kt\nretrofit2/KotlinExtensions\n*L\n32#1:127,11\n66#1:138,11\n93#1:149,11\n*E\n"])

package retrofit2

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellableContinuationImpl
import kotlinx.coroutines.Dispatchers
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@JvmSynthetic
public inline fun <reified T : Any> Retrofit.create(): T {
   Intrinsics.reifiedOperationMarker(4, "T");
   var var10000: Any = `$this$create`.create(Object::class.java);
   return (T)var10000;
}

public suspend fun <T : Any> Call<T>.await(): T {
   var `cancellable$iv`: CancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1);
   `cancellable$iv`.initCancellability();
   var continuation: CancellableContinuation = `cancellable$iv` as CancellableContinuation;
   (`cancellable$iv` as CancellableContinuation).invokeOnCancellation((new Function1<java.lang.Throwable, Unit>(`$this$await`) {
      {
         super(1);
         this.$this_await = `$receiver`;
      }

      public final void invoke(@Nullable java.lang.Throwable it) {
         this.$this_await.cancel();
      }
   }) as Function1);
   `$this$await`.enqueue(
      new Callback<T>(continuation) {
         {
            this.$continuation = `$continuation`;
         }

         @Override
         public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
            if (response.isSuccessful()) {
               var body: Any = response.body();
               if (body == null) {
                  var var10000: Any = call.request().tag(Invocation.class);
                  (this.$continuation as Continuation)
                     .resumeWith(
                        Result.constructor-impl(
                           ResultKt.createFailure(
                              (
                                 new KotlinNullPointerException(
                                    "Response from "
                                       + (var10000 as Invocation).service().getName()
                                       + '.'
                                       + (var10000 as Invocation).method().getName()
                                       + " was null but response body type was declared as non-null"
                                 )
                              ) as java.lang.Throwable
                           )
                        )
                     );
               } else {
                  (this.$continuation as Continuation).resumeWith(Result.constructor-impl(body));
               }
            } else {
               (this.$continuation as Continuation).resumeWith(Result.constructor-impl(ResultKt.createFailure(new HttpException(response))));
            }
         }

         @Override
         public void onFailure(@NotNull Call<T> call, @NotNull java.lang.Throwable t) {
            (this.$continuation as Continuation).resumeWith(Result.constructor-impl(ResultKt.createFailure(t)));
         }
      }
   );
   var var10000: Any = `cancellable$iv`.getResult();
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`);
   }

   return var10000;
}

@JvmName(name = "awaitNullable")
public suspend fun <T : Any> Call<T?>.await(): T? {
   var `cancellable$iv`: CancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1);
   `cancellable$iv`.initCancellability();
   var continuation: CancellableContinuation = `cancellable$iv` as CancellableContinuation;
   (`cancellable$iv` as CancellableContinuation).invokeOnCancellation((new Function1<java.lang.Throwable, Unit>(`$this$await`) {
      {
         super(1);
         this.$this_await = `$receiver`;
      }

      public final void invoke(@Nullable java.lang.Throwable it) {
         this.$this_await.cancel();
      }
   }) as Function1);
   `$this$await`.enqueue(new Callback<T>(continuation) {
      {
         this.$continuation = `$continuation`;
      }

      @Override
      public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
         if (response.isSuccessful()) {
            (this.$continuation as Continuation).resumeWith(Result.constructor-impl(response.body()));
         } else {
            (this.$continuation as Continuation).resumeWith(Result.constructor-impl(ResultKt.createFailure(new HttpException(response))));
         }
      }

      @Override
      public void onFailure(@NotNull Call<T> call, @NotNull java.lang.Throwable t) {
         (this.$continuation as Continuation).resumeWith(Result.constructor-impl(ResultKt.createFailure(t)));
      }
   });
   var var10000: Any = `cancellable$iv`.getResult();
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`);
   }

   return var10000;
}

@JvmName(name = "awaitUnit")
public suspend fun Call<Unit>.await() {
   return awaitNullable(`$this$await`, `$completion`);
}

public suspend fun <T> Call<T>.awaitResponse(): Response<T> {
   var `cancellable$iv`: CancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1);
   `cancellable$iv`.initCancellability();
   var continuation: CancellableContinuation = `cancellable$iv` as CancellableContinuation;
   (`cancellable$iv` as CancellableContinuation).invokeOnCancellation((new Function1<java.lang.Throwable, Unit>(`$this$awaitResponse`) {
      {
         super(1);
         this.$this_awaitResponse = `$receiver`;
      }

      public final void invoke(@Nullable java.lang.Throwable it) {
         this.$this_awaitResponse.cancel();
      }
   }) as Function1);
   `$this$awaitResponse`.enqueue(new Callback<T>(continuation) {
      {
         this.$continuation = `$continuation`;
      }

      @Override
      public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
         (this.$continuation as Continuation).resumeWith(Result.constructor-impl(response));
      }

      @Override
      public void onFailure(@NotNull Call<T> call, @NotNull java.lang.Throwable t) {
         (this.$continuation as Continuation).resumeWith(Result.constructor-impl(ResultKt.createFailure(t)));
      }
   });
   var var10000: Any = `cancellable$iv`.getResult();
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`);
   }

   return var10000;
}

internal suspend fun Throwable.suspendAndThrow(): Nothing {
   var `$continuation`: Continuation;
   label24: {
      if (var1 is <unrepresentable>) {
         `$continuation` = var1 as <unrepresentable>;
         if (((var1 as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
            `$continuation`.label -= Integer.MIN_VALUE;
            break label24;
         }
      }

      `$continuation` = new ContinuationImpl(var1) {
         Object L$0;
         int label;

         {
            super(`$completion`);
         }

         @Nullable
         public final Object invokeSuspend(@NotNull Object $result) {
            this.result = `$result`;
            this.label |= Integer.MIN_VALUE;
            return KotlinExtensions.suspendAndThrow(null, this as Continuation<?>);
         }
      };
   }

   var `$result`: Any = `$continuation`.result;
   var var6: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
   switch ($continuation.label) {
      case 0:
         ResultKt.throwOnFailure(`$result`);
         `$continuation`.L$0 = `$this$suspendAndThrow`;
         `$continuation`.label = 1;
         var continuation: Continuation = `$continuation`;
         Dispatchers.getDefault().dispatch(continuation.getContext(), new Runnable(continuation, `$this$suspendAndThrow`) {
            {
               this.$continuation = `$continuation`;
               this.$this_suspendAndThrow = `$receiver`;
            }

            @Override
            public final void run() {
               IntrinsicsKt.intercepted(this.$continuation).resumeWith(Result.constructor-impl(ResultKt.createFailure(this.$this_suspendAndThrow)));
            }
         });
         var var10000: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(`$continuation`);
         }

         if (var10000 === var6) {
            return var6;
         }
         break;
      case 1:
         ResultKt.throwOnFailure(`$result`);
         break;
      default:
         throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
   }

   throw new KotlinNothingValueException();
}

