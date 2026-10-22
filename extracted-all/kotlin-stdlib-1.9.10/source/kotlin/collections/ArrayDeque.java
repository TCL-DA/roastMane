package kotlin.collections

import java.util.NoSuchElementException
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SinceKotlin(version = "1.4")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
@SourceDebugExtension(["SMAP\nArrayDeque.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArrayDeque.kt\nkotlin/collections/ArrayDeque\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 3 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,583:1\n467#1,51:586\n467#1,51:637\n37#2,2:584\n26#3:688\n*S KotlinDebug\n*F\n+ 1 ArrayDeque.kt\nkotlin/collections/ArrayDeque\n*L\n462#1:586,51\n464#1:637,51\n47#1:584,2\n562#1:688\n*E\n"])
class ArrayDeque<E> : AbstractMutableList<E> {
   @JvmStatic
   public ArrayDeque.Companion Companion = new ArrayDeque.Companion(null);
   private Object[] elementData;
   @JvmStatic
   private Object[] emptyElementData = new Object[0];
   @JvmStatic
   private int maxArraySize = 2147483639;
   @JvmStatic
   private int defaultMinCapacity = 10;

   private final var elementData: Array<Any?>
   private final var head: Int
   public open var size: Int
      private set

   public constructor(initialCapacity: Int)  {
      var var10001: Array<Any>;
      if (initialCapacity == 0) {
         var10001 = emptyElementData;
      } else {
         if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
         }

         var10001 = new Object[initialCapacity];
      }

      this.elementData = var10001;
   }

   public constructor()  {
      this.elementData = emptyElementData;
   }

   public constructor(elements: Collection<Any>)  {
      this.elementData = elements.toArray(new Object[0]);
      this.size = this.elementData.length;
      if (this.elementData.length == 0) {
         this.elementData = emptyElementData;
      }
   }

   private fun ensureCapacity(minCapacity: Int) {
      if (minCapacity < 0) {
         throw new IllegalStateException("Deque is too big.");
      } else if (minCapacity > this.elementData.length) {
         if (this.elementData === emptyElementData) {
            this.elementData = new Object[RangesKt.coerceAtLeast(minCapacity, 10)];
         } else {
            this.copyElements(Companion.newCapacity$kotlin_stdlib(this.elementData.length, minCapacity));
         }
      }
   }

   private fun copyElements(newCapacity: Int) {
      var newElements: Array<Any> = new Object[newCapacity];
      ArraysKt.copyInto(this.elementData, newElements, 0, this.head, this.elementData.length);
      ArraysKt.copyInto(this.elementData, newElements, this.elementData.length - this.head, 0, this.head);
      this.head = 0;
      this.elementData = newElements;
   }

   @InlineOnly
   private inline fun internalGet(internalIndex: Int): Any {
      return (E)this.elementData[internalIndex];
   }

   private fun positiveMod(index: Int): Int {
      return if (index >= this.elementData.length) index - this.elementData.length else index;
   }

   private fun negativeMod(index: Int): Int {
      return if (index < 0) index + this.elementData.length else index;
   }

   @InlineOnly
   private inline fun internalIndex(index: Int): Int {
      return this.positiveMod(this.head + index);
   }

   private fun incremented(index: Int): Int {
      return if (index == ArraysKt.getLastIndex(this.elementData)) 0 else index + 1;
   }

   private fun decremented(index: Int): Int {
      return if (index == 0) ArraysKt.getLastIndex(this.elementData) else index - 1;
   }

   public override fun isEmpty(): Boolean {
      return this.size() == 0;
   }

   public fun first(): Any {
      if (this.isEmpty()) {
         throw new NoSuchElementException("ArrayDeque is empty.");
      } else {
         return (E)this.elementData[this.head];
      }
   }

   public fun firstOrNull(): Any? {
      return (E)(if (this.isEmpty()) null else this.elementData[this.head]);
   }

   public fun last(): Any {
      if (this.isEmpty()) {
         throw new NoSuchElementException("ArrayDeque is empty.");
      } else {
         return (E)this.elementData[this.positiveMod(this.head + CollectionsKt.getLastIndex(this))];
      }
   }

   public fun lastOrNull(): Any? {
      return (E)(if (this.isEmpty()) null else this.elementData[this.positiveMod(this.head + CollectionsKt.getLastIndex(this))]);
   }

   public override fun addFirst(element: Any) {
      this.ensureCapacity(this.size() + 1);
      this.head = this.decremented(this.head);
      this.elementData[this.head] = element;
      this.size = this.size() + 1;
   }

   public override fun addLast(element: Any) {
      this.ensureCapacity(this.size() + 1);
      this.elementData[this.positiveMod(this.head + this.size())] = element;
      this.size = this.size() + 1;
   }

   public override fun removeFirst(): Any {
      if (this.isEmpty()) {
         throw new NoSuchElementException("ArrayDeque is empty.");
      } else {
         var element: Any = this.elementData[this.head];
         this.elementData[this.head] = null;
         this.head = this.incremented(this.head);
         this.size = this.size() - 1;
         return (E)element;
      }
   }

   public fun removeFirstOrNull(): Any? {
      return if (this.isEmpty()) null else this.removeFirst();
   }

   public override fun removeLast(): Any {
      if (this.isEmpty()) {
         throw new NoSuchElementException("ArrayDeque is empty.");
      } else {
         var internalLastIndex: Int = this.positiveMod(this.head + CollectionsKt.getLastIndex(this));
         var element: Any = this.elementData[internalLastIndex];
         this.elementData[internalLastIndex] = null;
         this.size = this.size() - 1;
         return (E)element;
      }
   }

   public fun removeLastOrNull(): Any? {
      return if (this.isEmpty()) null else this.removeLast();
   }

   public override fun add(element: Any): Boolean {
      this.addLast((E)element);
      return true;
   }

   public override fun add(index: Int, element: Any) {
      AbstractList.Companion.checkPositionIndex$kotlin_stdlib(index, this.size());
      if (index == this.size()) {
         this.addLast((E)element);
      } else if (index == 0) {
         this.addFirst((E)element);
      } else {
         this.ensureCapacity(this.size() + 1);
         var internalIndex: Int = this.positiveMod(this.head + index);
         if (index < this.size() + 1 shr 1) {
            var tail: Int = this.decremented(internalIndex);
            var decrementedHead: Int = this.decremented(this.head);
            if (tail >= this.head) {
               this.elementData[decrementedHead] = this.elementData[this.head];
               ArraysKt.copyInto(this.elementData, this.elementData, this.head, this.head + 1, tail + 1);
            } else {
               ArraysKt.copyInto(this.elementData, this.elementData, this.head - 1, this.head, this.elementData.length);
               this.elementData[this.elementData.length - 1] = this.elementData[0];
               ArraysKt.copyInto(this.elementData, this.elementData, 0, 1, tail + 1);
            }

            this.elementData[tail] = element;
            this.head = decrementedHead;
         } else {
            var var6: Int = this.positiveMod(this.head + this.size());
            if (internalIndex < var6) {
               ArraysKt.copyInto(this.elementData, this.elementData, internalIndex + 1, internalIndex, var6);
            } else {
               ArraysKt.copyInto(this.elementData, this.elementData, 1, 0, var6);
               this.elementData[0] = this.elementData[this.elementData.length - 1];
               ArraysKt.copyInto(this.elementData, this.elementData, internalIndex + 1, internalIndex, this.elementData.length - 1);
            }

            this.elementData[internalIndex] = element;
         }

         this.size = this.size() + 1;
      }
   }

   private fun copyCollectionElements(internalIndex: Int, elements: Collection<Any>) {
      var iterator: java.util.Iterator = elements.iterator();
      var index: Int = internalIndex;

      for (int var5 = this.elementData.length; index < var5 && iterator.hasNext(); index++) {
         this.elementData[index] = iterator.next();
      }

      index = 0;

      for (int var7 = this.head; index < var7 && iterator.hasNext(); index++) {
         this.elementData[index] = iterator.next();
      }

      this.size = this.size() + elements.size();
   }

   public override fun addAll(elements: Collection<Any>): Boolean {
      if (elements.isEmpty()) {
         return false;
      } else {
         this.ensureCapacity(this.size() + elements.size());
         this.copyCollectionElements(this.positiveMod(this.head + this.size()), elements);
         return true;
      }
   }

   public override fun addAll(index: Int, elements: Collection<Any>): Boolean {
      AbstractList.Companion.checkPositionIndex$kotlin_stdlib(index, this.size());
      if (elements.isEmpty()) {
         return false;
      } else if (index == this.size()) {
         return this.addAll(elements);
      } else {
         this.ensureCapacity(this.size() + elements.size());
         var tail: Int = this.positiveMod(this.head + this.size());
         var internalIndex: Int = this.positiveMod(this.head + index);
         var elementsSize: Int = elements.size();
         if (index < this.size() + 1 shr 1) {
            var shiftedInternalIndex: Int = this.head - elementsSize;
            if (internalIndex >= this.head) {
               if (shiftedInternalIndex >= 0) {
                  ArraysKt.copyInto(this.elementData, this.elementData, shiftedInternalIndex, this.head, internalIndex);
               } else {
                  shiftedInternalIndex += this.elementData.length;
                  var shiftToFront: Int = internalIndex - this.head;
                  var shiftToBack: Int = this.elementData.length - shiftedInternalIndex;
                  if (this.elementData.length - shiftedInternalIndex >= shiftToFront) {
                     ArraysKt.copyInto(this.elementData, this.elementData, shiftedInternalIndex, this.head, internalIndex);
                  } else {
                     ArraysKt.copyInto(this.elementData, this.elementData, shiftedInternalIndex, this.head, this.head + shiftToBack);
                     ArraysKt.copyInto(this.elementData, this.elementData, 0, this.head + shiftToBack, internalIndex);
                  }
               }
            } else {
               ArraysKt.copyInto(this.elementData, this.elementData, shiftedInternalIndex, this.head, this.elementData.length);
               if (elementsSize >= internalIndex) {
                  ArraysKt.copyInto(this.elementData, this.elementData, this.elementData.length - elementsSize, 0, internalIndex);
               } else {
                  ArraysKt.copyInto(this.elementData, this.elementData, this.elementData.length - elementsSize, 0, elementsSize);
                  ArraysKt.copyInto(this.elementData, this.elementData, 0, elementsSize, internalIndex);
               }
            }

            this.head = shiftedInternalIndex;
            this.copyCollectionElements(this.negativeMod(internalIndex - elementsSize), elements);
         } else {
            var var9: Int = internalIndex + elementsSize;
            if (internalIndex < tail) {
               if (tail + elementsSize <= this.elementData.length) {
                  ArraysKt.copyInto(this.elementData, this.elementData, var9, internalIndex, tail);
               } else if (var9 >= this.elementData.length) {
                  ArraysKt.copyInto(this.elementData, this.elementData, var9 - this.elementData.length, internalIndex, tail);
               } else {
                  var var10: Int = tail + elementsSize - this.elementData.length;
                  ArraysKt.copyInto(this.elementData, this.elementData, 0, tail - (tail + elementsSize - this.elementData.length), tail);
                  ArraysKt.copyInto(this.elementData, this.elementData, var9, internalIndex, tail - var10);
               }
            } else {
               ArraysKt.copyInto(this.elementData, this.elementData, elementsSize, 0, tail);
               if (var9 >= this.elementData.length) {
                  ArraysKt.copyInto(this.elementData, this.elementData, var9 - this.elementData.length, internalIndex, this.elementData.length);
               } else {
                  ArraysKt.copyInto(this.elementData, this.elementData, 0, this.elementData.length - elementsSize, this.elementData.length);
                  ArraysKt.copyInto(this.elementData, this.elementData, var9, internalIndex, this.elementData.length - elementsSize);
               }
            }

            this.copyCollectionElements(internalIndex, elements);
         }

         return true;
      }
   }

   public override operator fun get(index: Int): Any {
      AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, this.size());
      return (E)this.elementData[this.positiveMod(this.head + index)];
   }

   public override operator fun set(index: Int, element: Any): Any {
      AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, this.size());
      var internalIndex: Int = this.positiveMod(this.head + index);
      var oldElement: Any = this.elementData[internalIndex];
      this.elementData[internalIndex] = element;
      return (E)oldElement;
   }

   public override operator fun contains(element: Any): Boolean {
      return this.indexOf(element) != -1;
   }

   public override fun indexOf(element: Any): Int {
      var tail: Int = this.positiveMod(this.head + this.size());
      if (this.head < tail) {
         for (int index = this.head; index < tail; index++) {
            if (element == this.elementData[index]) {
               return index - this.head;
            }
         }
      } else if (this.head >= tail) {
         var indexx: Int = this.head;

         for (int var4 = this.elementData.length; indexx < var4; indexx++) {
            if (element == this.elementData[indexx]) {
               return indexx - this.head;
            }
         }

         for (int indexx = 0; indexx < tail; indexx++) {
            if (element == this.elementData[indexx]) {
               return indexx + this.elementData.length - this.head;
            }
         }
      }

      return -1;
   }

   public override fun lastIndexOf(element: Any): Int {
      var tail: Int = this.positiveMod(this.head + this.size());
      if (this.head < tail) {
         var index: Int = tail - 1;
         var var4: Int = this.head;
         if (this.head <= index) {
            while (true) {
               if (element == this.elementData[index]) {
                  return index - this.head;
               }

               if (index == var4) {
                  break;
               }

               index--;
            }
         }
      } else if (this.head > tail) {
         for (int index = tail - 1; -1 < index; index--) {
            if (element == this.elementData[var5]) {
               return var5 + this.elementData.length - this.head;
            }
         }

         var indexx: Int = ArraysKt.getLastIndex(this.elementData);
         var var7: Int = this.head;
         if (this.head <= indexx) {
            while (true) {
               if (element == this.elementData[indexx]) {
                  return indexx - this.head;
               }

               if (indexx == var7) {
                  break;
               }

               indexx--;
            }
         }
      }

      return -1;
   }

   public override fun remove(element: Any): Boolean {
      var index: Int = this.indexOf(element);
      if (index == -1) {
         return false;
      } else {
         this.remove(index);
         return true;
      }
   }

   public override fun removeAt(index: Int): Any {
      AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, this.size());
      if (index == CollectionsKt.getLastIndex(this)) {
         return this.removeLast();
      } else if (index == 0) {
         return this.removeFirst();
      } else {
         var internalIndex: Int = this.positiveMod(this.head + index);
         var element: Any = this.elementData[internalIndex];
         if (index < this.size() shr 1) {
            if (internalIndex >= this.head) {
               ArraysKt.copyInto(this.elementData, this.elementData, this.head + 1, this.head, internalIndex);
            } else {
               ArraysKt.copyInto(this.elementData, this.elementData, 1, 0, internalIndex);
               this.elementData[0] = this.elementData[this.elementData.length - 1];
               ArraysKt.copyInto(this.elementData, this.elementData, this.head + 1, this.head, this.elementData.length - 1);
            }

            this.elementData[this.head] = null;
            this.head = this.incremented(this.head);
         } else {
            var internalLastIndex: Int = this.positiveMod(this.head + CollectionsKt.getLastIndex(this));
            if (internalIndex <= internalLastIndex) {
               ArraysKt.copyInto(this.elementData, this.elementData, internalIndex, internalIndex + 1, internalLastIndex + 1);
            } else {
               ArraysKt.copyInto(this.elementData, this.elementData, internalIndex, internalIndex + 1, this.elementData.length);
               this.elementData[this.elementData.length - 1] = this.elementData[0];
               ArraysKt.copyInto(this.elementData, this.elementData, 0, 1, internalLastIndex + 1);
            }

            this.elementData[internalLastIndex] = null;
         }

         this.size = this.size() - 1;
         return (E)element;
      }
   }

   public override fun removeAll(elements: Collection<Any>): Boolean {
      var `this_$iv`: ArrayDeque = this;
      var var10000: Boolean;
      if (!this.isEmpty() && this.elementData.length != 0) {
         var `tail$iv`: Int = this.positiveMod(this.head + this.size());
         var `newTail$iv`: Int = this.head;
         var `modified$iv`: Boolean = false;
         if (this.head < `tail$iv`) {
            for (int index$iv = this.head; index$iv < tail$iv; index$iv++) {
               var var15: Any = `this_$iv`.elementData[var13];
               if (!elements.contains(`this_$iv`.elementData[var13])) {
                  `this_$iv`.elementData[`newTail$iv`++] = var15;
               } else {
                  `modified$iv` = true;
               }
            }

            ArraysKt.fill(`this_$iv`.elementData, null, `newTail$iv`, `tail$iv`);
         } else {
            var `index$ivx`: Int = this.head;

            for (int element$iv = this.elementData.length; index$ivx < element$iv; index$ivx++) {
               var `element$ivx`: Any = `this_$iv`.elementData[`index$ivx`];
               `this_$iv`.elementData[`index$ivx`] = null;
               if (!elements.contains(`element$ivx`)) {
                  `this_$iv`.elementData[`newTail$iv`++] = `element$ivx`;
               } else {
                  `modified$iv` = true;
               }
            }

            `newTail$iv` = `this_$iv`.positiveMod(`newTail$iv`);

            for (int index$ivxx = 0; index$ivxx < tail$iv; index$ivxx++) {
               var var14: Any = `this_$iv`.elementData[`index$ivxx`];
               `this_$iv`.elementData[`index$ivxx`] = null;
               if (!elements.contains(var14)) {
                  `this_$iv`.elementData[`newTail$iv`] = var14;
                  `newTail$iv` = `this_$iv`.incremented(`newTail$iv`);
               } else {
                  `modified$iv` = true;
               }
            }
         }

         if (`modified$iv`) {
            `this_$iv`.size = `this_$iv`.negativeMod(`newTail$iv` - `this_$iv`.head);
         }

         var10000 = `modified$iv`;
      } else {
         var10000 = false;
      }

      return var10000;
   }

   public override fun retainAll(elements: Collection<Any>): Boolean {
      var `this_$iv`: ArrayDeque = this;
      var var10000: Boolean;
      if (!this.isEmpty() && this.elementData.length != 0) {
         var `tail$iv`: Int = this.positiveMod(this.head + this.size());
         var `newTail$iv`: Int = this.head;
         var `modified$iv`: Boolean = false;
         if (this.head < `tail$iv`) {
            for (int index$iv = this.head; index$iv < tail$iv; index$iv++) {
               var var15: Any = `this_$iv`.elementData[var13];
               if (elements.contains(`this_$iv`.elementData[var13])) {
                  `this_$iv`.elementData[`newTail$iv`++] = var15;
               } else {
                  `modified$iv` = true;
               }
            }

            ArraysKt.fill(`this_$iv`.elementData, null, `newTail$iv`, `tail$iv`);
         } else {
            var `index$ivx`: Int = this.head;

            for (int element$iv = this.elementData.length; index$ivx < element$iv; index$ivx++) {
               var `element$ivx`: Any = `this_$iv`.elementData[`index$ivx`];
               `this_$iv`.elementData[`index$ivx`] = null;
               if (elements.contains(`element$ivx`)) {
                  `this_$iv`.elementData[`newTail$iv`++] = `element$ivx`;
               } else {
                  `modified$iv` = true;
               }
            }

            `newTail$iv` = `this_$iv`.positiveMod(`newTail$iv`);

            for (int index$ivxx = 0; index$ivxx < tail$iv; index$ivxx++) {
               var var14: Any = `this_$iv`.elementData[`index$ivxx`];
               `this_$iv`.elementData[`index$ivxx`] = null;
               if (elements.contains(var14)) {
                  `this_$iv`.elementData[`newTail$iv`] = var14;
                  `newTail$iv` = `this_$iv`.incremented(`newTail$iv`);
               } else {
                  `modified$iv` = true;
               }
            }
         }

         if (`modified$iv`) {
            `this_$iv`.size = `this_$iv`.negativeMod(`newTail$iv` - `this_$iv`.head);
         }

         var10000 = `modified$iv`;
      } else {
         var10000 = false;
      }

      return var10000;
   }

   private inline fun filterInPlace(predicate: (Any) -> Boolean): Boolean {
      if (!this.isEmpty() && this.elementData.length != 0) {
         var tail: Int = this.positiveMod(this.head + this.size());
         var newTail: Int = this.head;
         var modified: Boolean = false;
         if (this.head < tail) {
            for (int index = this.head; index < tail; index++) {
               var element: Any = this.elementData[index];
               if (predicate.invoke(this.elementData[index]) as java.lang.Boolean) {
                  this.elementData[newTail++] = element;
               } else {
                  modified = true;
               }
            }

            ArraysKt.fill(this.elementData, null, newTail, tail);
         } else {
            var indexx: Int = this.head;

            for (int var11 = this.elementData.length; indexx < var11; indexx++) {
               var element: Any = this.elementData[indexx];
               this.elementData[indexx] = null;
               if (predicate.invoke(element) as java.lang.Boolean) {
                  this.elementData[newTail++] = element;
               } else {
                  modified = true;
               }
            }

            newTail = this.positiveMod(newTail);

            for (int indexx = 0; indexx < tail; indexx++) {
               var var12: Any = this.elementData[indexx];
               this.elementData[indexx] = null;
               if (predicate.invoke(var12) as java.lang.Boolean) {
                  this.elementData[newTail] = var12;
                  newTail = this.incremented(newTail);
               } else {
                  modified = true;
               }
            }
         }

         if (modified) {
            this.size = this.negativeMod(newTail - this.head);
         }

         return modified;
      } else {
         return false;
      }
   }

   public override fun clear() {
      var tail: Int = this.positiveMod(this.head + this.size());
      if (this.head < tail) {
         ArraysKt.fill(this.elementData, null, this.head, tail);
      } else if (!this.isEmpty()) {
         ArraysKt.fill(this.elementData, null, this.head, this.elementData.length);
         ArraysKt.fill(this.elementData, null, 0, tail);
      }

      this.head = 0;
      this.size = 0;
   }

   public override fun <T> toArray(array: Array<T>): Array<T> {
      var dest: Array<Any> = if (array.length >= this.size()) array else ArraysKt.arrayOfNulls(array, this.size());
      var tail: Int = this.positiveMod(this.head + this.size());
      if (this.head < tail) {
         ArraysKt.copyInto$default(this.elementData, dest, 0, this.head, tail, 2, null);
      } else if (!this.isEmpty()) {
         ArraysKt.copyInto(this.elementData, dest, 0, this.head, this.elementData.length);
         ArraysKt.copyInto(this.elementData, dest, this.elementData.length - this.head, 0, tail);
      }

      if (dest.length > this.size()) {
         dest[this.size()] = null;
      }

      return (T[])dest;
   }

   public override fun toArray(): Array<Any?> {
      return this.toArray(new Object[this.size()]);
   }

   internal fun <T> testToArray(array: Array<T>): Array<T> {
      return (T[])this.toArray(array);
   }

   internal fun testToArray(): Array<Any?> {
      return this.toArray();
   }

   internal fun internalStructure(structure: (Int, Array<Any?>) -> Unit) {
      structure.invoke(
         if (!this.isEmpty() && this.head >= this.positiveMod(this.head + this.size())) this.head - this.elementData.length else this.head, this.toArray()
      );
   }

   internal companion object Companion private constructor() {
      private const val defaultMinCapacity: Int
      private final val emptyElementData: Array<Any?>
      private const val maxArraySize: Int


      internal fun newCapacity(oldCapacity: Int, minCapacity: Int): Int {
         var newCapacity: Int = oldCapacity + (oldCapacity shr 1);
         if (oldCapacity + (oldCapacity shr 1) - minCapacity < 0) {
            newCapacity = minCapacity;
         }

         if (newCapacity - 2147483639 > 0) {
            newCapacity = if (minCapacity > 2147483639) Integer.MAX_VALUE else 2147483639;
         }

         return newCapacity;
      }
   }
}
