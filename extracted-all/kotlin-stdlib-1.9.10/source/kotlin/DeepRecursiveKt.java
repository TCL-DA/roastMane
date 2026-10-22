package kotlin

import kotlin.coroutines.intrinsics.IntrinsicsKt

private final val UNDEFINED_RESULT: Result<Any>
private Object UNDEFINED_RESULT = Result.constructor-impl(IntrinsicsKt.getCOROUTINE_SUSPENDED());

@SinceKotlin(version = "1.7")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
public operator fun <T, R> DeepRecursiveFunction<T, R>.invoke(value: T): R {
   return (R)new DeepRecursiveScopeImpl(`$this$invoke`.getBlock$kotlin_stdlib(), value).runCallLoop();
}

/** @deprecated */
@JvmSynthetic
fun `getUNDEFINED_RESULT$annotations`() {
}

@JvmSynthetic
fun `access$getUNDEFINED_RESULT$p`(): Any {
   return UNDEFINED_RESULT;
}

