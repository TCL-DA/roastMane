@file:SourceDebugExtension(["SMAP\nCancellationException.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CancellationException.kt\nkotlin/coroutines/cancellation/CancellationExceptionKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,22:1\n1#2:23\n*E\n"])

package kotlin.coroutines.cancellation

import java.util.concurrent.CancellationException
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@InlineOnly
@SinceKotlin(version = "1.4")
public inline fun CancellationException(message: String?, cause: Throwable?): CancellationException {
   var var2: CancellationException = new CancellationException(message);
   var2.initCause(cause);
   return var2;
}

@InlineOnly
@SinceKotlin(version = "1.4")
public inline fun CancellationException(cause: Throwable?): CancellationException {
   var var1: CancellationException = new CancellationException(if (cause != null) cause.toString() else null);
   var1.initCause(cause);
   return var1;
}

/** @deprecated */
@SinceKotlin(version = "1.4")
@JvmSynthetic
fun `CancellationException$annotations`() {
}

