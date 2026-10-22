package kotlin.collections

import java.io.Serializable
import kotlin.jvm.internal.CollectionToArray
import kotlin.jvm.internal.markers.KMappedMarker

internal object EmptySet private constructor() : java.util.Set, Serializable, KMappedMarker {
   @JvmStatic
   public EmptySet INSTANCE = new EmptySet();

   private const val serialVersionUID: Long = 3406603774387020532L
   public open val size: Int
      public open get() {
         return 0;
      }



   public override operator fun equals(other: Any?): Boolean {
      return other is java.util.Set && (other as java.util.Set).isEmpty();
   }

   public override fun hashCode(): Int {
      return 0;
   }

   public override fun toString(): String {
      return "[]";
   }

   public override fun isEmpty(): Boolean {
      return true;
   }

   public open operator fun contains(element: Nothing): Boolean {
      return false;
   }

   public override fun containsAll(elements: Collection<Nothing>): Boolean {
      return elements.isEmpty();
   }

   public override operator fun iterator(): Iterator<Nothing> {
      return EmptyIterator.INSTANCE;
   }

   private fun readResolve(): Any {
      return INSTANCE;
   }

   fun add(element: Void): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun addAll(elements: java.util.Collection): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun clear() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun remove(element: Any): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun removeAll(elements: java.util.Collection): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun retainAll(elements: java.util.Collection): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun <T> toArray(array: Array<T>): Array<T> {
      return (T[])CollectionToArray.toArray(this, array);
   }

   override fun toArray(): Array<Any> {
      return CollectionToArray.toArray(this);
   }
}
