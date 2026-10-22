package kotlin

import kotlin.internal.InlineOnly

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Byte.toULong(): ULong {
   return ULong.constructor-impl((long)`$this$toULong`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Short.toULong(): ULong {
   return ULong.constructor-impl((long)`$this$toULong`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Int.toULong(): ULong {
   return ULong.constructor-impl((long)`$this$toULong`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Long.toULong(): ULong {
   return ULong.constructor-impl(`$this$toULong`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Float.toULong(): ULong {
   return UnsignedKt.doubleToULong((double)`$this$toULong`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Double.toULong(): ULong {
   return UnsignedKt.doubleToULong(`$this$toULong`);
}

