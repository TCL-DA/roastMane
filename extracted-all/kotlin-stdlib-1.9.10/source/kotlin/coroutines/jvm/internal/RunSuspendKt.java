package kotlin.coroutines.jvm.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationKt

@SinceKotlin(version = "1.3")
internal fun runSuspend(block: (Continuation<Unit>) -> Any?) {
   var run: RunSuspend = new RunSuspend();
   ContinuationKt.startCoroutine(block, run);
   run.await();
}

