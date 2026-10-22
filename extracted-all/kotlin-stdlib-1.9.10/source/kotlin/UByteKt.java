package kotlin

import kotlin.internal.InlineOnly

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Byte.toUByte(): UByte {
   return UByte.constructor-impl(`$this$toUByte`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Short.toUByte(): UByte {
   return UByte.constructor-impl((byte)`$this$toUByte`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Int.toUByte(): UByte {
   return UByte.constructor-impl((byte)`$this$toUByte`);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun Long.toUByte(): UByte {
   return UByte.constructor-impl((byte)((int)`$this$toUByte`));
}

