package kotlin.collections

import java.io.Serializable
import java.util.RandomAccess
import kotlin.jvm.internal.CollectionToArray
import kotlin.jvm.internal.markers.KMappedMarker

internal object EmptyList private constructor() : java.util.List, Serializable, RandomAccess, KMappedMarker {
   @JvmStatic
   public EmptyList INSTANCE = new EmptyList();

   private const val serialVersionUID: Long = -7390468764508069838L
   public open val size: Int
      public open get() {
         return 0;
      }



   public override operator fun equals(other: Any?): Boolean {
      return other is java.util.List && (other as java.util.List).isEmpty();
   }

   public override fun hashCode(): Int {
      return 1;
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

   public open operator fun get(index: Int): Nothing {
      throw new IndexOutOfBoundsException("Empty list doesn't contain element at index " + index + '.');
   }

   public open fun indexOf(element: Nothing): Int {
      return -1;
   }

   public open fun lastIndexOf(element: Nothing): Int {
      return -1;
   }

   public override operator fun iterator(): Iterator<Nothing> {
      return EmptyIterator.INSTANCE;
   }

   public override fun listIterator(): ListIterator<Nothing> {
      return EmptyIterator.INSTANCE;
   }

   public override fun listIterator(index: Int): ListIterator<Nothing> {
      if (index != 0) {
         throw new IndexOutOfBoundsException("Index: " + index);
      } else {
         return EmptyIterator.INSTANCE;
      }
   }

   public override fun subList(fromIndex: Int, toIndex: Int): List<Nothing> {
      if (fromIndex == 0 && toIndex == 0) {
         return this;
      } else {
         throw new IndexOutOfBoundsException("fromIndex: " + fromIndex + ", toIndex: " + toIndex);
      }
   }

   private fun readResolve(): Any {
      return INSTANCE;
   }

   fun add(element: Void): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   fun add(index: Int, element: Void) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun addAll(index: Int, elements: java.util.Collection): Boolean {
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

   fun remove(index: Int): Void {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun retainAll(elements: java.util.Collection): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   fun set(index: Int, element: Void): Void {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun <T> toArray(array: Array<T>): Array<T> {
      return (T[])CollectionToArray.toArray(this, array);
   }

   override fun toArray(): Array<Any> {
      return CollectionToArray.toArray(this);
   }
}
