package kotlin.collections.builders

import kotlin.jvm.internal.markers.KMutableSet

internal class MapBuilderKeys<E> internal constructor(backing: MapBuilder<Any, *>) : AbstractMutableSet<E>, java.util.Set<E>, KMutableSet {
   private final val backing: MapBuilder<Any, *>
   public open val size: Int
      public open get() {
         return this.backing.size();
      }


   init {
      this.backing = backing;
   }

   public override fun isEmpty(): Boolean {
      return this.backing.isEmpty();
   }

   public override operator fun contains(element: Any): Boolean {
      return this.backing.containsKey(element);
   }

   public override fun clear() {
      this.backing.clear();
   }

   public override fun add(element: Any): Boolean {
      throw new UnsupportedOperationException();
   }

   public override fun addAll(elements: Collection<Any>): Boolean {
      throw new UnsupportedOperationException();
   }

   public override fun remove(element: Any): Boolean {
      return this.backing.removeKey$kotlin_stdlib((E)element) >= 0;
   }

   public override operator fun iterator(): MutableIterator<Any> {
      return this.backing.keysIterator$kotlin_stdlib() as java.util.Iterator<E>;
   }

   public override fun removeAll(elements: Collection<Any>): Boolean {
      this.backing.checkIsMutable$kotlin_stdlib();
      return super.removeAll(elements);
   }

   public override fun retainAll(elements: Collection<Any>): Boolean {
      this.backing.checkIsMutable$kotlin_stdlib();
      return super.retainAll(elements);
   }
}
