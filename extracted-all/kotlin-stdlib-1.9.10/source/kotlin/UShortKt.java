package kotlin

import kotlin.internal.InlineOnly

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Byte.toUShort(): UShort {
   return UShort.constructor-impl((short)`$this$toUShort`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Short.toUShort(): UShort {
   return UShort.constructor-impl(`$this$toUShort`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Int.toUShort(): UShort {
   return UShort.constructor-impl((short)`$this$toUShort`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Long.toUShort(): UShort {
   return UShort.constructor-impl((short)((int)`$this$toUShort`));
}

