package kotlin.coroutines

import kotlin.contracts.InvocationKind
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.InlineMarker
import org.jetbrains.annotations.NotNull

public final val coroutineContext: CoroutineContext
   public final inline get() {
      throw new NotImplementedError("Implemented as intrinsic");
   }

@SinceKotlin(version = "1.3")
@InlineOnly
public inline fun <T> Continuation<T>.resume(value: T) {
   `$this$resume`.resumeWith(Result.constructor-impl(value));
}

@SinceKotlin(version = "1.3")
@InlineOnly
public inline fun <T> Continuation<T>.resumeWithException(exception: Throwable) {
   `$this$resumeWithException`.resumeWith(Result.constructor-impl(ResultKt.createFailure(exception)));
}

@SinceKotlin(version = "1.3")
@InlineOnly
public inline fun <T> Continuation(context: CoroutineContext, crossinline resumeWith: (Result<T>) -> Unit): Continuation<T> {
   return new Continuation<T>(context, resumeWith) {
      {
         this.$context = `$context`;
         this.$resumeWith = `$resumeWith`;
      }

      @NotNull
      @Override
      public CoroutineContext getContext() {
         return this.$context;
      }

      @Override
      public void resumeWith(@NotNull Object result) {
         this.$resumeWith.invoke(Result.box-impl(result));
      }
   };
}

@SinceKotlin(version = "1.3")
public fun <T> ((Continuation<T>) -> Any?).createCoroutine(completion: Continuation<T>): Continuation<Unit> {
   return new SafeContinuation<>(
      IntrinsicsKt.intercepted(IntrinsicsKt.createCoroutineUnintercepted(`$this$createCoroutine`, completion)), IntrinsicsKt.getCOROUTINE_SUSPENDED()
   );
}

@SinceKotlin(version = "1.3")
public fun <R, T> ((R, Continuation<T>) -> Any?).createCoroutine(receiver: R, completion: Continuation<T>): Continuation<Unit> {
   return new SafeContinuation<>(
      IntrinsicsKt.intercepted(IntrinsicsKt.createCoroutineUnintercepted(`$this$createCoroutine`, receiver, completion)), IntrinsicsKt.getCOROUTINE_SUSPENDED()
   );
}

@SinceKotlin(version = "1.3")
public fun <T> ((Continuation<T>) -> Any?).startCoroutine(completion: Continuation<T>) {
   IntrinsicsKt.intercepted(IntrinsicsKt.createCoroutineUnintercepted(`$this$startCoroutine`, completion)).resumeWith(Result.constructor-impl(Unit.INSTANCE));
}

@SinceKotlin(version = "1.3")
public fun <R, T> ((R, Continuation<T>) -> Any?).startCoroutine(receiver: R, completion: Continuation<T>) {
   IntrinsicsKt.intercepted(IntrinsicsKt.createCoroutineUnintercepted(`$this$startCoroutine`, receiver, completion))
      .resumeWith(Result.constructor-impl(Unit.INSTANCE));
}

@SinceKotlin(version = "1.3")
@InlineOnly
public suspend inline fun <T> suspendCoroutine(crossinline block: (Continuation<T>) -> Unit): T {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   InlineMarker.mark(0);
   var safe: SafeContinuation = new SafeContinuation(IntrinsicsKt.intercepted(`$completion`));
   block.invoke(safe);
   var var10000: Any = safe.getOrThrow();
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`);
   }

   InlineMarker.mark(1);
   return var10000;
}

/** @deprecated */
@SinceKotlin(version = "1.3")
@InlineOnly
@JvmSynthetic
fun `getCoroutineContext$annotations`() {
}

