package kotlin

import kotlin.internal.InlineOnly
import kotlin.internal.IntrinsicConstEvaluation

public final val code: Int
   public final inline get() {
      return `$this$code`;
   }

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
@InlineOnly
public inline fun Char(code: Int): Char {
   if (code >= 0 && code <= 65535) {
      return (char)code;
   } else {
      throw new IllegalArgumentException("Invalid Char code: " + code);
   }
}

/** @deprecated */
@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
@InlineOnly
@IntrinsicConstEvaluation
@JvmSynthetic
fun `getCode$annotations`(var0: Char) {
}

