package kotlin.collections

import kotlin.jvm.internal.markers.KMappedMarker

@SinceKotlin(version = "1.1")
abstract class AbstractSet<E> : AbstractCollection<E>, java.util.Set<E>, KMappedMarker {
   @JvmStatic
   public AbstractSet.Companion Companion = new AbstractSet.Companion(null);

   open fun AbstractSet() {
   }

   public override operator fun equals(other: Any?): Boolean {
      if (other === this) {
         return true;
      } else {
         return other is java.util.Set && Companion.setEquals$kotlin_stdlib(this, other as java.util.Set<?>);
      }
   }

   public override fun hashCode(): Int {
      return Companion.unorderedHashCode$kotlin_stdlib(this);
   }

   override fun iterator(): MutableIterator<E> {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   internal companion object Companion private constructor() {
      internal fun unorderedHashCode(c: Collection<*>): Int {
         var hashCode: Int = 0;

         for (Object element : c) {
            hashCode += if (element != null) element.hashCode() else 0;
         }

         return hashCode;
      }

      internal fun setEquals(c: Set<*>, other: Set<*>): Boolean {
         return c.size() == other.size() && c.containsAll(other);
      }
   }
}
