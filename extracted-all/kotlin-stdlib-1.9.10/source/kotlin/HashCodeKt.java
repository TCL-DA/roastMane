package kotlin

import kotlin.internal.InlineOnly

@SinceKotlin(version = "1.3")
@InlineOnly
public inline fun Any?.hashCode(): Int {
   return if (`$this$hashCode` != null) `$this$hashCode`.hashCode() else 0;
}

