package kotlin

import java.util.Arrays
import java.util.NoSuchElementException
import kotlin.jvm.internal.CollectionToArray
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.markers.KMappedMarker

@JvmInline
@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
@SourceDebugExtension(["SMAP\nUByteArray.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UByteArray.kt\nkotlin/UByteArray\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,86:1\n1726#2,3:87\n*S KotlinDebug\n*F\n+ 1 UByteArray.kt\nkotlin/UByteArray\n*L\n62#1:87,3\n*E\n"])
inline class UByteArray : java.util.Collection<UByte>, KMappedMarker {
   public open val size: Int
      public open get() {
         return arg0.length;
      }

   internal final val storage: ByteArray

   @JvmStatic
   fun `constructor-impl`(size: Int): ByteArray {
      return constructor-impl(new byte[size]);
   }

   @JvmStatic
   public operator fun get(index: Int): UByte {
      return UByte.constructor-impl(arg0[index]);
   }

   @JvmStatic
   public operator fun set(index: Int, value: UByte) {
      arg0[index] = value;
   }

   fun getSize(): Int {
      return getSize-impl(this.storage);
   }

   @JvmStatic
   public open operator fun iterator(): kotlin.collections.Iterator<UByte> {
      return new UByteArray.Iterator(arg0);
   }

   override fun iterator(): MutableIterator<UByte> {
      return iterator-impl(this.storage);
   }

   @JvmStatic
   public open operator fun contains(element: UByte): Boolean {
      return ArraysKt.contains(arg0, element);
   }

   fun `contains-7apg3OU`(element: Byte): Boolean {
      return contains-7apg3OU(this.storage, element);
   }

   @JvmStatic
   public open fun containsAll(elements: Collection<UByte>): Boolean {
      var `$this$all$iv`: java.lang.Iterable = elements;
      var var10000: Boolean;
      if ((elements as java.util.Collection).isEmpty()) {
         var10000 = true;
      } else {
         var var4: java.util.Iterator = `$this$all$iv`.iterator();

         while (true) {
            if (!var4.hasNext()) {
               var10000 = true;
               break;
            }

            var `element$iv`: Any = var4.next();
            if (`element$iv` !is UByte || !ArraysKt.contains(arg0, (`element$iv` as UByte).unbox-impl())) {
               var10000 = false;
               break;
            }
         }
      }

      return var10000;
   }

   override fun containsAll(elements: MutableCollection<Any>): Boolean {
      return containsAll-impl(this.storage, elements);
   }

   @JvmStatic
   public open fun isEmpty(): Boolean {
      return arg0.length == 0;
   }

   override fun isEmpty(): Boolean {
      return isEmpty-impl(this.storage);
   }

   @JvmStatic
   public open fun toString(): String {
      return "UByteArray(storage=" + Arrays.toString(arg0) + ')';
   }

   override fun toString(): java.lang.String {
      return toString-impl(this.storage);
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return Arrays.hashCode(arg0);
   }

   override fun hashCode(): Int {
      return hashCode-impl(this.storage);
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      if (other !is UByteArray) {
         return false;
      } else {
         return arg0 == (other as UByteArray).unbox-impl();
      }
   }

   override fun equals(other: Any): Boolean {
      return equals-impl(this.storage, other);
   }

   fun `add-7apg3OU`(element: Byte): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun addAll(elements: MutableCollection<UByte>): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun clear() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun remove(element: Any): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun removeAll(elements: MutableCollection<Any>): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   override fun retainAll(elements: MutableCollection<Any>): Boolean {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @PublishedApi
   @JvmStatic
   fun `constructor-impl`(storage: ByteArray): ByteArray {
      return storage;
   }

   @JvmStatic
   fun `equals-impl0`(p1: ByteArray, p2: ByteArray): Boolean {
      return p1 == p2;
   }

   override fun <T> toArray(array: Array<T>): Array<T> {
      return (T[])CollectionToArray.toArray(this as java.util.Collection<?>, array);
   }

   override fun toArray(): Array<Any> {
      return CollectionToArray.toArray(this as java.util.Collection<?>);
   }

   private class Iterator(array: ByteArray) : java.util.Iterator<UByte>, KMappedMarker {
      private final val array: ByteArray
      private final var index: Int

      init {
         this.array = array;
      }

      public override operator fun hasNext(): Boolean {
         return this.index < this.array.length;
      }

      public open operator fun next(): UByte {
         if (this.index < this.array.length) {
            return UByte.constructor-impl(this.array[this.index++]);
         } else {
            throw new NoSuchElementException(java.lang.String.valueOf(this.index));
         }
      }

      override fun remove() {
         throw new UnsupportedOperationException("Operation is not supported for read-only collection");
      }
   }
}
