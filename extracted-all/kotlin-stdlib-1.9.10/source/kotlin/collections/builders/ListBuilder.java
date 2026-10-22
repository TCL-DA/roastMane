package kotlin.collections.builders

import java.io.NotSerializableException
import java.io.Serializable
import java.util.Arrays
import java.util.NoSuchElementException
import java.util.RandomAccess
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.markers.KMutableList
import kotlin.jvm.internal.markers.KMutableListIterator

@SourceDebugExtension(["SMAP\nListBuilder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ListBuilder.kt\nkotlin/collections/builders/ListBuilder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,432:1\n1#2:433\n*E\n"])
internal class ListBuilder<E> private constructor(vararg array: Any,
      offset: Int,
      length: Int,
      isReadOnly: Boolean,
      backing: ListBuilder<Any>?,
      root: ListBuilder<Any>?
   )
   : AbstractMutableList<E>,
   java.util.List<E>,
   RandomAccess,
   Serializable,
   KMutableList {
   @JvmStatic
   private ListBuilder.Companion Companion = new ListBuilder.Companion(null);
   private E[] array;
   @JvmStatic
   private ListBuilder Empty;

   private final var array: Array<Any>
   private final val backing: ListBuilder<Any>?
   private final val isEffectivelyReadOnly: Boolean
      private final get() {
         return this.isReadOnly || this.root != null && this.root.isReadOnly;
      }

   private final var isReadOnly: Boolean
   private final var length: Int
   private final var offset: Int
   private final val root: ListBuilder<Any>?
   public open val size: Int
      public open get() {
         return this.length;
      }


   init {
      this.array = (E[])array;
      this.offset = offset;
      this.length = length;
      this.isReadOnly = isReadOnly;
      this.backing = backing;
      this.root = root;
   }

   public constructor() : this(10)
   public constructor(initialCapacity: Int) : this((E[])ListBuilderKt.arrayOfUninitializedElements(initialCapacity), 0, 0, false, null, null)
   public fun build(): List<Any> {
      if (this.backing != null) {
         throw new IllegalStateException();
      } else {
         this.checkIsMutable();
         this.isReadOnly = true;
         return if (this.length > 0) this else Empty;
      }
   }

   private fun writeReplace(): Any {
      if (this.isEffectivelyReadOnly()) {
         return new SerializedCollection(this, 0);
      } else {
         throw new NotSerializableException("The list cannot be serialized while it is being built.");
      }
   }

   public override fun isEmpty(): Boolean {
      return this.length == 0;
   }

   public override operator fun get(index: Int): Any {
      AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, this.length);
      return this.array[this.offset + index];
   }

   public override operator fun set(index: Int, element: Any): Any {
      this.checkIsMutable();
      AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, this.length);
      var old: Any = this.array[this.offset + index];
      this.array[this.offset + index] = (E)element;
      return (E)old;
   }

   public override fun indexOf(element: Any): Int {
      for (int i = 0; i < this.length; i++) {
         if (this.array[this.offset + i] == element) {
            return i;
         }
      }

      return -1;
   }

   public override fun lastIndexOf(element: Any): Int {
      for (int i = this.length - 1; i >= 0; i--) {
         if (this.array[this.offset + i] == element) {
            return i;
         }
      }

      return -1;
   }

   public override operator fun iterator(): MutableIterator<Any> {
      return new ListBuilder.Itr<>(this, 0);
   }

   public override fun listIterator(): MutableListIterator<Any> {
      return new ListBuilder.Itr<>(this, 0);
   }

   public override fun listIterator(index: Int): MutableListIterator<Any> {
      AbstractList.Companion.checkPositionIndex$kotlin_stdlib(index, this.length);
      return new ListBuilder.Itr<>(this, index);
   }

   public override fun add(element: Any): Boolean {
      this.checkIsMutable();
      this.addAtInternal(this.offset + this.length, (E)element);
      return true;
   }

   public override fun add(index: Int, element: Any) {
      this.checkIsMutable();
      AbstractList.Companion.checkPositionIndex$kotlin_stdlib(index, this.length);
      this.addAtInternal(this.offset + index, (E)element);
   }

   public override fun addAll(elements: Collection<Any>): Boolean {
      this.checkIsMutable();
      var n: Int = elements.size();
      this.addAllInternal(this.offset + this.length, elements, n);
      return n > 0;
   }

   public override fun addAll(index: Int, elements: Collection<Any>): Boolean {
      this.checkIsMutable();
      AbstractList.Companion.checkPositionIndex$kotlin_stdlib(index, this.length);
      var n: Int = elements.size();
      this.addAllInternal(this.offset + index, elements, n);
      return n > 0;
   }

   public override fun clear() {
      this.checkIsMutable();
      this.removeRangeInternal(this.offset, this.length);
   }

   public override fun removeAt(index: Int): Any {
      this.checkIsMutable();
      AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, this.length);
      return this.removeAtInternal(this.offset + index);
   }

   public override fun remove(element: Any): Boolean {
      this.checkIsMutable();
      var i: Int = this.indexOf(element);
      if (i >= 0) {
         this.remove(i);
      }

      return i >= 0;
   }

   public override fun removeAll(elements: Collection<Any>): Boolean {
      this.checkIsMutable();
      return this.retainOrRemoveAllInternal(this.offset, this.length, elements, false) > 0;
   }

   public override fun retainAll(elements: Collection<Any>): Boolean {
      this.checkIsMutable();
      return this.retainOrRemoveAllInternal(this.offset, this.length, elements, true) > 0;
   }

   public override fun subList(fromIndex: Int, toIndex: Int): MutableList<Any> {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, this.length);
      var var10000: ListBuilder = new ListBuilder;
      var var10003: Int = this.offset + fromIndex;
      var var10004: Int = toIndex - fromIndex;
      var var10007: ListBuilder = this.root;
      if (this.root == null) {
         var10007 = this;
      }

      var10000./* $VF: Unable to resugar constructor */<init>(this.array, var10003, var10004, this.isReadOnly, this, var10007);
      return var10000;
   }

   public override fun <T> toArray(destination: Array<T>): Array<T> {
      if (destination.length < this.length) {
         var var10000: Array<Any> = Arrays.copyOfRange(this.array, this.offset, this.offset + this.length, (Class<? extends Object[]>)destination.getClass());
         return (T[])var10000;
      } else {
         ArraysKt.copyInto(this.array, (E[])destination, 0, this.offset, this.offset + this.length);
         if (destination.length > this.length) {
            destination[this.length] = null;
         }

         return (T[])destination;
      }
   }

   public override fun toArray(): Array<Any?> {
      return ArraysKt.copyOfRange(this.array, this.offset, this.offset + this.length);
   }

   public override operator fun equals(other: Any?): Boolean {
      return other === this || other is java.util.List && this.contentEquals(other as java.util.List<?>);
   }

   public override fun hashCode(): Int {
      return ListBuilderKt.access$subarrayContentHashCode(this.array, this.offset, this.length);
   }

   public override fun toString(): String {
      return ListBuilderKt.access$subarrayContentToString(this.array, this.offset, this.length);
   }

   private fun ensureCapacity(minCapacity: Int) {
      if (this.backing != null) {
         throw new IllegalStateException();
      } else if (minCapacity < 0) {
         throw new OutOfMemoryError();
      } else {
         if (minCapacity > this.array.length) {
            this.array = ListBuilderKt.copyOfUninitializedElements(this.array, ArrayDeque.Companion.newCapacity$kotlin_stdlib(this.array.length, minCapacity));
         }
      }
   }

   private fun checkIsMutable() {
      if (this.isEffectivelyReadOnly()) {
         throw new UnsupportedOperationException();
      }
   }

   private fun ensureExtraCapacity(n: Int) {
      this.ensureCapacity(this.length + n);
   }

   private fun contentEquals(other: List<*>): Boolean {
      return ListBuilderKt.access$subarrayContentEquals(this.array, this.offset, this.length, other);
   }

   private fun insertAtInternal(i: Int, n: Int) {
      this.ensureExtraCapacity(n);
      ArraysKt.copyInto(this.array, this.array, i + n, i, this.offset + this.length);
      this.length += n;
   }

   private fun addAtInternal(i: Int, element: Any) {
      if (this.backing != null) {
         this.backing.addAtInternal(i, (E)element);
         this.array = this.backing.array;
         var var3: Int = this.length++;
      } else {
         this.insertAtInternal(i, 1);
         this.array[i] = (E)element;
      }
   }

   private fun addAllInternal(i: Int, elements: Collection<Any>, n: Int) {
      if (this.backing != null) {
         this.backing.addAllInternal(i, elements, n);
         this.array = this.backing.array;
         this.length += n;
      } else {
         this.insertAtInternal(i, n);
         var j: Int = 0;

         for (java.util.Iterator it = elements.iterator(); j < n; j++) {
            this.array[i + j] = (E)it.next();
         }
      }
   }

   private fun removeAtInternal(i: Int): Any {
      if (this.backing != null) {
         var var7: Any = this.backing.removeAtInternal(i);
         this.length += -1;
         return (E)var7;
      } else {
         var old: Any = this.array[i];
         ArraysKt.copyInto(this.array, this.array, i, i + 1, this.offset + this.length);
         ListBuilderKt.resetAt(this.array, this.offset + this.length - 1);
         this.length += -1;
         return (E)old;
      }
   }

   private fun removeRangeInternal(rangeOffset: Int, rangeLength: Int) {
      if (this.backing != null) {
         this.backing.removeRangeInternal(rangeOffset, rangeLength);
      } else {
         ArraysKt.copyInto(this.array, this.array, rangeOffset, rangeOffset + rangeLength, this.length);
         ListBuilderKt.resetRange(this.array, this.length - rangeLength, this.length);
      }

      this.length -= rangeLength;
   }

   private fun retainOrRemoveAllInternal(rangeOffset: Int, rangeLength: Int, elements: Collection<Any>, retain: Boolean): Int {
      if (this.backing != null) {
         var var13: Int = this.backing.retainOrRemoveAllInternal(rangeOffset, rangeLength, elements, retain);
         this.length -= var13;
         return var13;
      } else {
         var i: Int = 0;
         var j: Int = 0;

         while (i < rangeLength) {
            if (elements.contains(this.array[rangeOffset + i]) == retain) {
               this.array[rangeOffset + j++] = this.array[rangeOffset + i++];
            } else {
               i++;
            }
         }

         var removed: Int = rangeLength - j;
         ArraysKt.copyInto(this.array, this.array, rangeOffset + j, rangeOffset + rangeLength, this.length);
         ListBuilderKt.resetRange(this.array, this.length - removed, this.length);
         this.length -= removed;
         return removed;
      }
   }

   @JvmStatic
   fun {
      var var0: ListBuilder = new ListBuilder(0);
      var0.isReadOnly = true;
      Empty = var0;
   }

   private companion object Companion private constructor() {
      private final val Empty: ListBuilder<Nothing>

   }

   @SourceDebugExtension(["SMAP\nListBuilder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ListBuilder.kt\nkotlin/collections/builders/ListBuilder$Itr\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,432:1\n1#2:433\n*E\n"])
   private class Itr<E> : java.util.ListIterator<E>, KMutableListIterator {
      private final var index: Int
      private final var lastIndex: Int
      private final val list: ListBuilder<Any>

      public constructor(list: ListBuilder<Any>, index: Int)  {
         this.list = list;
         this.index = index;
         this.lastIndex = -1;
      }

      public override fun hasPrevious(): Boolean {
         return this.index > 0;
      }

      public override operator fun hasNext(): Boolean {
         return this.index < ListBuilder.access$getLength$p(this.list);
      }

      public override fun previousIndex(): Int {
         return this.index - 1;
      }

      public override fun nextIndex(): Int {
         return this.index;
      }

      public override fun previous(): Any {
         if (this.index <= 0) {
            throw new NoSuchElementException();
         } else {
            this.index += -1;
            this.lastIndex = this.index;
            return (E)ListBuilder.access$getArray$p(this.list)[ListBuilder.access$getOffset$p(this.list) + this.lastIndex];
         }
      }

      public override operator fun next(): Any {
         if (this.index >= ListBuilder.access$getLength$p(this.list)) {
            throw new NoSuchElementException();
         } else {
            this.lastIndex = this.index++;
            return (E)ListBuilder.access$getArray$p(this.list)[ListBuilder.access$getOffset$p(this.list) + this.lastIndex];
         }
      }

      public override fun set(element: Any) {
         if (this.lastIndex == -1) {
            throw new IllegalStateException("Call next() or previous() before replacing element from the iterator.".toString());
         } else {
            this.list.set(this.lastIndex, (E)element);
         }
      }

      public override fun add(element: Any) {
         this.list.add(this.index++, (E)element);
         this.lastIndex = -1;
      }

      public override fun remove() {
         if (this.lastIndex == -1) {
            throw new IllegalStateException("Call next() or previous() before removing element from the iterator.".toString());
         } else {
            this.list.remove(this.lastIndex);
            this.index = this.lastIndex;
            this.lastIndex = -1;
         }
      }
   }
}
