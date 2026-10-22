package kotlin.coroutines.jvm.internal

import kotlin.coroutines.Continuation

@SinceKotlin(version = "1.3")
internal fun <T> probeCoroutineCreated(completion: Continuation<T>): Continuation<T> {
   return completion;
}

@SinceKotlin(version = "1.3")
internal fun probeCoroutineResumed(frame: Continuation<*>) {
}

@SinceKotlin(version = "1.3")
internal fun probeCoroutineSuspended(frame: Continuation<*>) {
}

