package kotlin.collections

import java.util.NoSuchElementException
import java.util.RandomAccess
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.TypeIntrinsics

// $VF: Class flags could not be determined
internal class CollectionsKt__MutableCollectionsKt : CollectionsKt__MutableCollectionsJVMKt {
   @InlineOnly
   @JvmStatic
   public inline fun <T> MutableCollection<out T>.remove(element: T): Boolean {
      return TypeIntrinsics.asMutableCollection(`$this$remove`).remove(element);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> MutableCollection<out T>.removeAll(elements: Collection<T>): Boolean {
      return TypeIntrinsics.asMutableCollection(`$this$removeAll`).removeAll(elements);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> MutableCollection<out T>.retainAll(elements: Collection<T>): Boolean {
      return TypeIntrinsics.asMutableCollection(`$this$retainAll`).retainAll(elements);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> MutableCollection<in T>.plusAssign(element: T) {
      `$this$plusAssign`.add(element);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> MutableCollection<in T>.plusAssign(elements: Iterable<T>) {
      CollectionsKt.addAll(`$this$plusAssign`, elements);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> MutableCollection<in T>.plusAssign(elements: Array<T>) {
      CollectionsKt.addAll(`$this$plusAssign`, elements);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> MutableCollection<in T>.plusAssign(elements: Sequence<T>) {
      CollectionsKt.addAll(`$this$plusAssign`, elements);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> MutableCollection<in T>.minusAssign(element: T) {
      `$this$minusAssign`.remove(element);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> MutableCollection<in T>.minusAssign(elements: Iterable<T>) {
      CollectionsKt.removeAll(`$this$minusAssign`, elements);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> MutableCollection<in T>.minusAssign(elements: Array<T>) {
      CollectionsKt.removeAll(`$this$minusAssign`, elements);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> MutableCollection<in T>.minusAssign(elements: Sequence<T>) {
      CollectionsKt.removeAll(`$this$minusAssign`, elements);
   }

   @JvmStatic
   public fun <T> MutableCollection<in T>.addAll(elements: Iterable<T>): Boolean {
      if (elements is java.util.Collection) {
         return `$this$addAll`.addAll(elements as java.util.Collection);
      } else {
         var result: Boolean = false;

         for (Object item : elements) {
            if (`$this$addAll`.add(item)) {
               result = true;
            }
         }

         return result;
      }
   }

   @JvmStatic
   public fun <T> MutableCollection<in T>.addAll(elements: Sequence<T>): Boolean {
      var result: Boolean = false;

      for (Object item : elements) {
         if (`$this$addAll`.add(item)) {
            result = true;
         }
      }

      return result;
   }

   @JvmStatic
   public fun <T> MutableCollection<in T>.addAll(elements: Array<out T>): Boolean {
      return `$this$addAll`.addAll(ArraysKt.asList(elements));
   }

   @JvmStatic
   internal fun <T> Iterable<T>.convertToListIfNotCollection(): Collection<T> {
      return (java.util.Collection<T>)(if (`$this$convertToListIfNotCollection` is java.util.Collection)
         `$this$convertToListIfNotCollection` as java.util.Collection
         else
         CollectionsKt.toList(`$this$convertToListIfNotCollection`));
   }

   @JvmStatic
   public fun <T> MutableCollection<in T>.removeAll(elements: Iterable<T>): Boolean {
      return `$this$removeAll`.removeAll(CollectionsKt.convertToListIfNotCollection(elements));
   }

   @JvmStatic
   public fun <T> MutableCollection<in T>.removeAll(elements: Sequence<T>): Boolean {
      var list: java.util.List = SequencesKt.toList(elements);
      return !list.isEmpty() && `$this$removeAll`.removeAll(list);
   }

   @JvmStatic
   public fun <T> MutableCollection<in T>.removeAll(elements: Array<out T>): Boolean {
      return elements.length != 0 && `$this$removeAll`.removeAll(ArraysKt.asList(elements));
   }

   @JvmStatic
   public fun <T> MutableCollection<in T>.retainAll(elements: Iterable<T>): Boolean {
      return `$this$retainAll`.retainAll(CollectionsKt.convertToListIfNotCollection(elements));
   }

   @JvmStatic
   public fun <T> MutableCollection<in T>.retainAll(elements: Array<out T>): Boolean {
      return if (elements.length != 0)
         `$this$retainAll`.retainAll(ArraysKt.asList(elements))
         else
         retainNothing$CollectionsKt__MutableCollectionsKt(`$this$retainAll`);
   }

   @JvmStatic
   public fun <T> MutableCollection<in T>.retainAll(elements: Sequence<T>): Boolean {
      var list: java.util.List = SequencesKt.toList(elements);
      return if (!list.isEmpty()) `$this$retainAll`.retainAll(list) else retainNothing$CollectionsKt__MutableCollectionsKt(`$this$retainAll`);
   }

   @JvmStatic
   private fun MutableCollection<*>.retainNothing(): Boolean {
      var result: Boolean = !`$this$retainNothing`.isEmpty();
      `$this$retainNothing`.clear();
      return result;
   }

   @JvmStatic
   public fun <T> MutableIterable<T>.removeAll(predicate: (T) -> Boolean): Boolean {
      return filterInPlace$CollectionsKt__MutableCollectionsKt(`$this$removeAll`, predicate, true);
   }

   @JvmStatic
   public fun <T> MutableIterable<T>.retainAll(predicate: (T) -> Boolean): Boolean {
      return filterInPlace$CollectionsKt__MutableCollectionsKt(`$this$retainAll`, predicate, false);
   }

   @JvmStatic
   private fun <T> MutableIterable<T>.filterInPlace(predicate: (T) -> Boolean, predicateResultToRemove: Boolean): Boolean {
      var result: Boolean = false;
      var `$this$filterInPlace_u24lambda_u240`: java.util.Iterator = `$this$filterInPlace`.iterator();

      while ($this$filterInPlace_u24lambda_u240.hasNext()) {
         if (predicate.invoke(`$this$filterInPlace_u24lambda_u240`.next()) as java.lang.Boolean == predicateResultToRemove) {
            `$this$filterInPlace_u24lambda_u240`.remove();
            result = true;
         }
      }

      return result;
   }

   @Deprecated(message = "Use removeAt(index) instead.", replaceWith = @ReplaceWith(expression = "removeAt(index)", imports = []), level = DeprecationLevel.ERROR)
   @InlineOnly
   @JvmStatic
   public inline fun <T> MutableList<T>.remove(index: Int): T {
      return (T)`$this$remove`.remove(index);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun <T> MutableList<T>.removeFirst(): T {
      if (`$this$removeFirst`.isEmpty()) {
         throw new NoSuchElementException("List is empty.");
      } else {
         return (T)`$this$removeFirst`.remove(0);
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun <T> MutableList<T>.removeFirstOrNull(): T? {
      return (T)(if (`$this$removeFirstOrNull`.isEmpty()) null else `$this$removeFirstOrNull`.remove(0));
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun <T> MutableList<T>.removeLast(): T {
      if (`$this$removeLast`.isEmpty()) {
         throw new NoSuchElementException("List is empty.");
      } else {
         return (T)`$this$removeLast`.remove(CollectionsKt.getLastIndex(`$this$removeLast`));
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun <T> MutableList<T>.removeLastOrNull(): T? {
      return (T)(if (`$this$removeLastOrNull`.isEmpty()) null else `$this$removeLastOrNull`.remove(CollectionsKt.getLastIndex(`$this$removeLastOrNull`)));
   }

   @JvmStatic
   public fun <T> MutableList<T>.removeAll(predicate: (T) -> Boolean): Boolean {
      return filterInPlace$CollectionsKt__MutableCollectionsKt(`$this$removeAll`, predicate, true);
   }

   @JvmStatic
   public fun <T> MutableList<T>.retainAll(predicate: (T) -> Boolean): Boolean {
      return filterInPlace$CollectionsKt__MutableCollectionsKt(`$this$retainAll`, predicate, false);
   }

   @JvmStatic
   private fun <T> MutableList<T>.filterInPlace(predicate: (T) -> Boolean, predicateResultToRemove: Boolean): Boolean {
      if (`$this$filterInPlace` !is RandomAccess) {
         return filterInPlace$CollectionsKt__MutableCollectionsKt(TypeIntrinsics.asMutableIterable(`$this$filterInPlace`), predicate, predicateResultToRemove);
      } else {
         var writeIndex: Int = 0;
         var removeIndex: IntIterator = new IntRange(0, CollectionsKt.getLastIndex(`$this$filterInPlace`)).iterator();

         while (removeIndex.hasNext()) {
            var readIndex: Int = removeIndex.nextInt();
            var element: Any = `$this$filterInPlace`.get(readIndex);
            if (predicate.invoke(element) as java.lang.Boolean != predicateResultToRemove) {
               if (writeIndex != readIndex) {
                  `$this$filterInPlace`.set(writeIndex, element);
               }

               writeIndex++;
            }
         }

         if (writeIndex >= `$this$filterInPlace`.size()) {
            return false;
         } else {
            var var7: Int = CollectionsKt.getLastIndex(`$this$filterInPlace`);
            var var8: Int = writeIndex;
            if (writeIndex <= var7) {
               while (true) {
                  `$this$filterInPlace`.remove(var7);
                  if (var7 == var8) {
                     break;
                  }

                  var7--;
               }
            }

            return true;
         }
      }
   }

   open fun CollectionsKt__MutableCollectionsKt() {
   }
}
