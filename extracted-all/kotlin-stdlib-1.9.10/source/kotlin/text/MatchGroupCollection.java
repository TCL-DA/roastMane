package kotlin.text

import kotlin.jvm.internal.markers.KMappedMarker

interface MatchGroupCollection : java.util.Collection<MatchGroup>, KMappedMarker {
   public abstract operator fun get(index: Int): MatchGroup? {
   }
}
