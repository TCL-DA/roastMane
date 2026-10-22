package kotlin.math

import kotlin.internal.InlineOnly

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun min(a: UInt, b: UInt): UInt {
   return UComparisonsKt.minOf-J1ME1BU(a, b);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun min(a: ULong, b: ULong): ULong {
   return UComparisonsKt.minOf-eb3DHEI(a, b);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun max(a: UInt, b: UInt): UInt {
   return UComparisonsKt.maxOf-J1ME1BU(a, b);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
@InlineOnly
public inline fun max(a: ULong, b: ULong): ULong {
   return UComparisonsKt.maxOf-eb3DHEI(a, b);
}

