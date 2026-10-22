package kotlin.enums

import kotlin.jvm.internal.markers.KMappedMarker

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
sealed interface EnumEntries<E extends java.lang.Enum<E>> : java.util.List<E>, KMappedMarker {
}
