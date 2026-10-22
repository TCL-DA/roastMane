package kotlin.collections

import java.util.NoSuchElementException
import java.util.RandomAccess
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.markers.KMappedMarker

@SinceKotlin(version = "1.1")
@SourceDebugExtension(["SMAP\nAbstractList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AbstractList.kt\nkotlin/collections/AbstractList\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,157:1\n350#2,7:158\n378#2,7:165\n*S KotlinDebug\n*F\n+ 1 AbstractList.kt\nkotlin/collections/AbstractList\n*L\n27#1:158,7\n29#1:165,7\n*E\n"])
abstract class AbstractList<E> : AbstractCollection<E>, java.util.List<E>, KMappedMarker {
   @JvmStatic
   public AbstractList.Companion Companion = new AbstractList.Companion(null);

   public abstract val size: Int

   open fun AbstractList() {
   }

   public abstract override operator fun get(index: Int): Any {
   }

   public override operator fun iterator(): Iterator<Any> {
      return new AbstractList.IteratorImpl(this);
   }

   public override fun indexOf(element: Any): Int {
      var `$this$indexOfFirst$iv`: java.util.List = this;
      var `index$iv`: Int = 0;
      var var5: java.util.Iterator = `$this$indexOfFirst$iv`.iterator();

      var var10000: Int;
      while (true) {
         if (!var5.hasNext()) {
            var10000 = -1;
            break;
         }

         if (var5.next() == element) {
            var10000 = `index$iv`;
            break;
         }

         `index$iv`++;
      }

      return var10000;
   }

   public override fun lastIndexOf(element: Any): Int {
      var `iterator$iv`: java.util.ListIterator = this.listIterator(this.size());

      var var10000: Int;
      while (true) {
         if (`iterator$iv`.hasPrevious()) {
            if (!(`iterator$iv`.previous() == element)) {
               continue;
            }

            var10000 = `iterator$iv`.nextIndex();
            break;
         }

         var10000 = -1;
         break;
      }

      return var10000;
   }

   public override fun listIterator(): ListIterator<Any> {
      return new AbstractList.ListIteratorImpl((int)this, 0);
   }

   public override fun listIterator(index: Int): ListIterator<Any> {
      return new AbstractList.ListIteratorImpl((int)this, index);
   }

   public override fun subList(fromIndex: Int, toIndex: Int): List<Any> {
      return new AbstractList.SubList<>(this, fromIndex, toIndex);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (other === this) {
         return true;
      } else {
         return other is java.util.List && Companion.orderedEquals$kotlin_stdlib(this, other as java.util.Collection<?>);
      }
   }

   public override fun hashCode(): Int {
      return Companion.orderedHashCode$kotlin_stdlib(this);
   }

   override fun add(index: Int, element: E) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun addAll(index: Int, elements: MutableCollection<E>): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun remove(index: Int): E {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun set(index: Int, element: E): E {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   internal companion object Companion private constructor() {
      internal fun checkElementIndex(index: Int, size: Int) {
         if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
         }
      }

      internal fun checkPositionIndex(index: Int, size: Int) {
         if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
         }
      }

      internal fun checkRangeIndexes(fromIndex: Int, toIndex: Int, size: Int) {
         if (fromIndex < 0 || toIndex > size) {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex + ", toIndex: " + toIndex + ", size: " + size);
         } else if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex: " + fromIndex + " > toIndex: " + toIndex);
         }
      }

      internal fun checkBoundsIndexes(startIndex: Int, endIndex: Int, size: Int) {
         if (startIndex < 0 || endIndex > size) {
            throw new IndexOutOfBoundsException("startIndex: " + startIndex + ", endIndex: " + endIndex + ", size: " + size);
         } else if (startIndex > endIndex) {
            throw new IllegalArgumentException("startIndex: " + startIndex + " > endIndex: " + endIndex);
         }
      }

      internal fun orderedHashCode(c: Collection<*>): Int {
         var hashCode: Int = 1;

         for (Object e : c) {
            hashCode = 31 * hashCode + (if (e != null) e.hashCode() else 0);
         }

         return hashCode;
      }

      internal fun orderedEquals(c: Collection<*>, other: Collection<*>): Boolean {
         if (c.size() != other.size()) {
            return false;
         } else {
            var otherIterator: java.util.Iterator = other.iterator();

            for (Object elem : c) {
               if (!(elem == otherIterator.next())) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private open inner class IteratorImpl : java.util.Iterator<E>, KMappedMarker {
      protected final var index: Int
         internal set

      init {
         this.this$0 = `this$0`;
      }

      public override operator fun hasNext(): Boolean {
         return this.index < this.this$0.size();
      }

      public override operator fun next(): Any {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.this$0.get(this.index++);
         }
      }

      override fun remove() {
         throw new UnsupportedOperationException("Operation is not supported for read-only collection");
      }
   }

   private open inner class ListIteratorImpl(index: Int) : AbstractList.IteratorImpl(`this$0`), java.util.ListIterator<E>, KMappedMarker {
      init {
         this.this$0 = `this$0`;
         AbstractList.Companion.checkPositionIndex$kotlin_stdlib(index, this.this$0.size());
         this.setIndex(index);
      }

      public override fun hasPrevious(): Boolean {
         return this.getIndex() > 0;
      }

      public override fun nextIndex(): Int {
         return this.getIndex();
      }

      public override fun previous(): Any {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            var var10000: AbstractList = this.this$0;
            this.setIndex(this.getIndex() + -1);
            return (E)var10000.get(this.getIndex());
         }
      }

      public override fun previousIndex(): Int {
         return this.getIndex() - 1;
      }

      override fun add(element: E) {
         throw new UnsupportedOperationException("Operation is not supported for read-only collection");
      }

      override fun set(element: E) {
         throw new UnsupportedOperationException("Operation is not supported for read-only collection");
      }
   }

   private class SubList<E>(list: AbstractList<Any>, fromIndex: Int, toIndex: Int) : AbstractList<E>, RandomAccess {
      private final var _size: Int
      private final val fromIndex: Int
      private final val list: AbstractList<Any>
      public open val size: Int
         public open get() {
            return this._size;
         }


      init {
         this.list = list;
         this.fromIndex = fromIndex;
         AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(this.fromIndex, toIndex, this.list.size());
         this._size = toIndex - this.fromIndex;
      }

      public override operator fun get(index: Int): Any {
         AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, this._size);
         return this.list.get(this.fromIndex + index);
      }
   }
}
