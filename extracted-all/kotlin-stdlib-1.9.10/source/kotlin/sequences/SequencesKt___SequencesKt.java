package kotlin.sequences

import java.util.ArrayList
import java.util.Comparator
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.NoSuchElementException
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Ref
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\n_Sequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,3112:1\n179#1,2:3113\n316#1,7:3115\n1324#1,3:3123\n739#1,4:3126\n704#1,4:3130\n722#1,4:3134\n775#1,4:3138\n1017#1,3:3142\n1020#1,3:3152\n1037#1,3:3155\n1040#1,3:3165\n1324#1,3:3182\n1313#1,2:3185\n1#2:3122\n372#3,7:3145\n372#3,7:3158\n372#3,7:3168\n372#3,7:3175\n*S KotlinDebug\n*F\n+ 1 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n87#1:3113,2\n99#1:3115,7\n458#1:3123,3\n658#1:3126,4\n674#1:3130,4\n689#1:3134,4\n760#1:3138,4\n988#1:3142,3\n988#1:3152,3\n1003#1:3155,3\n1003#1:3165,3\n1106#1:3182,3\n1144#1:3185,2\n988#1:3145,7\n1003#1:3158,7\n1019#1:3168,7\n1039#1:3175,7\n*E\n"])
// $VF: Class flags could not be determined
internal class SequencesKt___SequencesKt : SequencesKt___SequencesJvmKt {
   @JvmStatic
   public operator fun <T> Sequence<T>.contains(element: T): Boolean {
      return SequencesKt.indexOf(`$this$contains`, element) >= 0;
   }

   @JvmStatic
   public fun <T> Sequence<T>.elementAt(index: Int): T {
      return (T)SequencesKt.elementAtOrElse(`$this$elementAt`, index, (new Function1<Integer, T>(index) {
         {
            super(1);
            this.$index = `$index`;
         }

         public final T invoke(int it) {
            throw new IndexOutOfBoundsException("Sequence doesn't contain element at index " + this.$index + '.');
         }
      }) as Function1);
   }

   @JvmStatic
   public fun <T> Sequence<T>.elementAtOrElse(index: Int, defaultValue: (Int) -> T): T {
      if (index < 0) {
         return (T)defaultValue.invoke(index);
      } else {
         var iterator: java.util.Iterator = `$this$elementAtOrElse`.iterator();
         var count: Int = 0;

         while (iterator.hasNext()) {
            var element: Any = iterator.next();
            if (index == count++) {
               return (T)element;
            }
         }

         return (T)defaultValue.invoke(index);
      }
   }

   @JvmStatic
   public fun <T> Sequence<T>.elementAtOrNull(index: Int): T? {
      if (index < 0) {
         return null;
      } else {
         var iterator: java.util.Iterator = `$this$elementAtOrNull`.iterator();
         var count: Int = 0;

         while (iterator.hasNext()) {
            var element: Any = iterator.next();
            if (index == count++) {
               return (T)element;
            }
         }

         return null;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.find(predicate: (T) -> Boolean): T? {
      var var4: java.util.Iterator = `$this$find`.iterator();

      var var10000: Any;
      while (true) {
         if (var4.hasNext()) {
            var `element$iv`: Any = var4.next();
            if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
               continue;
            }

            var10000 = `element$iv`;
            break;
         }

         var10000 = null;
         break;
      }

      return (T)var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.findLast(predicate: (T) -> Boolean): T? {
      var `last$iv`: Any = null;

      for (Object element$iv : $this$findLast) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `last$iv` = `element$iv`;
         }
      }

      return (T)`last$iv`;
   }

   @JvmStatic
   public fun <T> Sequence<T>.first(): T {
      var iterator: java.util.Iterator = `$this$first`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException("Sequence is empty.");
      } else {
         return (T)iterator.next();
      }
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.first(predicate: (T) -> Boolean): T {
      for (Object element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return (T)element;
         }
      }

      throw new NoSuchElementException("Sequence contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Any> Sequence<T>.firstNotNullOf(transform: (T) -> R?): R {
      var var2: java.util.Iterator = `$this$firstNotNullOf`.iterator();

      var var10000: Any;
      do {
         if (!var2.hasNext()) {
            var10000 = null;
            break;
         }

         var10000 = transform.invoke(var2.next());
      } while (var10000 == null);

      if (var10000 == null) {
         throw new NoSuchElementException("No element of the sequence was transformed to a non-null value.");
      } else {
         return (R)var10000;
      }
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Any> Sequence<T>.firstNotNullOfOrNull(transform: (T) -> R?): R? {
      for (Object element : $this$firstNotNullOfOrNull) {
         var result: Any = transform.invoke(element);
         if (result != null) {
            return (R)result;
         }
      }

      return null;
   }

   @JvmStatic
   public fun <T> Sequence<T>.firstOrNull(): T? {
      var iterator: java.util.Iterator = `$this$firstOrNull`.iterator();
      return (T)(if (!iterator.hasNext()) null else iterator.next());
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.firstOrNull(predicate: (T) -> Boolean): T? {
      for (Object element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return (T)element;
         }
      }

      return null;
   }

   @JvmStatic
   public fun <T> Sequence<T>.indexOf(element: T): Int {
      var index: Int = 0;

      for (Object item : $this$indexOf) {
         if (index < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         if (element == item) {
            return index;
         }

         index++;
      }

      return -1;
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.indexOfFirst(predicate: (T) -> Boolean): Int {
      var index: Int = 0;

      for (Object item : $this$indexOfFirst) {
         if (index < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         if (predicate.invoke(item) as java.lang.Boolean) {
            return index;
         }

         index++;
      }

      return -1;
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.indexOfLast(predicate: (T) -> Boolean): Int {
      var lastIndex: Int = -1;
      var index: Int = 0;

      for (Object item : $this$indexOfLast) {
         if (index < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         if (predicate.invoke(item) as java.lang.Boolean) {
            lastIndex = index;
         }

         index++;
      }

      return lastIndex;
   }

   @JvmStatic
   public fun <T> Sequence<T>.last(): T {
      var iterator: java.util.Iterator = `$this$last`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException("Sequence is empty.");
      } else {
         var last: Any = iterator.next();

         while (iterator.hasNext()) {
            last = iterator.next();
         }

         return (T)last;
      }
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.last(predicate: (T) -> Boolean): T {
      var last: Any = null;
      var found: Boolean = false;

      for (Object element : $this$last) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            last = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Sequence contains no element matching the predicate.");
      } else {
         return (T)last;
      }
   }

   @JvmStatic
   public fun <T> Sequence<T>.lastIndexOf(element: T): Int {
      var lastIndex: Int = -1;
      var index: Int = 0;

      for (Object item : $this$lastIndexOf) {
         if (index < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         if (element == item) {
            lastIndex = index;
         }

         index++;
      }

      return lastIndex;
   }

   @JvmStatic
   public fun <T> Sequence<T>.lastOrNull(): T? {
      var iterator: java.util.Iterator = `$this$lastOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var last: Any = iterator.next();

         while (iterator.hasNext()) {
            last = iterator.next();
         }

         return (T)last;
      }
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.lastOrNull(predicate: (T) -> Boolean): T? {
      var last: Any = null;

      for (Object element : $this$lastOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            last = element;
         }
      }

      return (T)last;
   }

   @JvmStatic
   public fun <T> Sequence<T>.single(): T {
      var iterator: java.util.Iterator = `$this$single`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException("Sequence is empty.");
      } else {
         var single: Any = iterator.next();
         if (iterator.hasNext()) {
            throw new IllegalArgumentException("Sequence has more than one element.");
         } else {
            return (T)single;
         }
      }
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.single(predicate: (T) -> Boolean): T {
      var single: Any = null;
      var found: Boolean = false;

      for (Object element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Sequence contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Sequence contains no element matching the predicate.");
      } else {
         return (T)single;
      }
   }

   @JvmStatic
   public fun <T> Sequence<T>.singleOrNull(): T? {
      var iterator: java.util.Iterator = `$this$singleOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         return (T)(if (iterator.hasNext()) null else iterator.next());
      }
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.singleOrNull(predicate: (T) -> Boolean): T? {
      var single: Any = null;
      var found: Boolean = false;

      for (Object element : $this$singleOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               return null;
            }

            single = element;
            found = true;
         }
      }

      return (T)(if (!found) null else single);
   }

   @JvmStatic
   public fun <T> Sequence<T>.drop(n: Int): Sequence<T> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return (Sequence<T>)(if (n == 0)
            `$this$drop`
            else
            (if (`$this$drop` is DropTakeSequence) (`$this$drop` as DropTakeSequence).drop(n) else new DropSequence(`$this$drop`, n)));
      }
   }

   @JvmStatic
   public fun <T> Sequence<T>.dropWhile(predicate: (T) -> Boolean): Sequence<T> {
      return new DropWhileSequence(`$this$dropWhile`, predicate);
   }

   @JvmStatic
   public fun <T> Sequence<T>.filter(predicate: (T) -> Boolean): Sequence<T> {
      return new FilteringSequence(`$this$filter`, true, predicate);
   }

   @JvmStatic
   public fun <T> Sequence<T>.filterIndexed(predicate: (Int, T) -> Boolean): Sequence<T> {
      return new TransformingSequence<>(
         new FilteringSequence<>(new IndexingSequence(`$this$filterIndexed`), true, (new Function1<IndexedValue<? extends T>, java.lang.Boolean>(predicate) {
            {
               super(1);
               this.$predicate = `$predicate`;
            }

            @NotNull
            public final java.lang.Boolean invoke(@NotNull IndexedValue<? extends T> it) {
               return this.$predicate.invoke(it.getIndex(), it.getValue());
            }
         }) as Function1), <unrepresentable>.INSTANCE
      );
   }

   @JvmStatic
   public inline fun <T, C : MutableCollection<in T>> Sequence<T>.filterIndexedTo(destination: C, predicate: (Int, T) -> Boolean): C {
      var `index$iv`: Int = 0;

      for (Object item$iv : $this$filterIndexedTo) {
         var var9: Int = `index$iv`++;
         if (var9 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         if (predicate.invoke(var9, `item$iv`) as java.lang.Boolean) {
            destination.add(`item$iv`);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> Sequence<T>.filterNot(predicate: (T) -> Boolean): Sequence<T> {
      return new FilteringSequence(`$this$filterNot`, false, predicate);
   }

   @JvmStatic
   public fun <T : Any> Sequence<T?>.filterNotNull(): Sequence<T> {
      var var10000: Sequence = SequencesKt.filterNot(`$this$filterNotNull`, <unrepresentable>.INSTANCE);
      return var10000;
   }

   @JvmStatic
   public fun <C : MutableCollection<in T>, T : Any> Sequence<T?>.filterNotNullTo(destination: C): C {
      for (Object element : $this$filterNotNullTo) {
         if (element != null) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, C : MutableCollection<in T>> Sequence<T>.filterNotTo(destination: C, predicate: (T) -> Boolean): C {
      for (Object element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, C : MutableCollection<in T>> Sequence<T>.filterTo(destination: C, predicate: (T) -> Boolean): C {
      for (Object element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> Sequence<T>.take(n: Int): Sequence<T> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return (Sequence<T>)(if (n == 0)
            SequencesKt.emptySequence()
            else
            (if (`$this$take` is DropTakeSequence) (`$this$take` as DropTakeSequence).take(n) else new TakeSequence(`$this$take`, n)));
      }
   }

   @JvmStatic
   public fun <T> Sequence<T>.takeWhile(predicate: (T) -> Boolean): Sequence<T> {
      return new TakeWhileSequence(`$this$takeWhile`, predicate);
   }

   @JvmStatic
   public fun <T : Comparable<T>> Sequence<T>.sorted(): Sequence<T> {
      return new Sequence<T>(`$this$sorted`) {
         {
            this.$this_sorted = `$receiver`;
         }

         @NotNull
         @Override
         public java.util.Iterator<T> iterator() {
            var sortedList: java.util.List = SequencesKt.toMutableList(this.$this_sorted);
            CollectionsKt.sort(sortedList);
            return sortedList.iterator();
         }
      };
   }

   @JvmStatic
   public inline fun <T, R : Comparable<R>> Sequence<T>.sortedBy(crossinline selector: (T) -> R?): Sequence<T> {
      return SequencesKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)a) as java.lang.Comparable<?>, var3.invoke(b) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public inline fun <T, R : Comparable<R>> Sequence<T>.sortedByDescending(crossinline selector: (T) -> R?): Sequence<T> {
      return SequencesKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
         {
            this.$selector = `$selector`;
         }

         @Override
         public final int compare(T a, T b) {
            var var3: Function1 = this.$selector;
            return ComparisonsKt.compareValues(this.$selector.invoke((T)b) as java.lang.Comparable<?>, var3.invoke(a) as java.lang.Comparable<?>);
         }
      });
   }

   @JvmStatic
   public fun <T : Comparable<T>> Sequence<T>.sortedDescending(): Sequence<T> {
      return SequencesKt.sortedWith(`$this$sortedDescending`, ComparisonsKt.reverseOrder());
   }

   @JvmStatic
   public fun <T> Sequence<T>.sortedWith(comparator: Comparator<in T>): Sequence<T> {
      return new Sequence<T>(`$this$sortedWith`, comparator) {
         {
            this.$this_sortedWith = `$receiver`;
            this.$comparator = `$comparator`;
         }

         @NotNull
         @Override
         public java.util.Iterator<T> iterator() {
            var sortedList: java.util.List = SequencesKt.toMutableList(this.$this_sortedWith);
            CollectionsKt.sortWith(sortedList, this.$comparator);
            return sortedList.iterator();
         }
      };
   }

   @JvmStatic
   public inline fun <T, K, V> Sequence<T>.associate(transform: (T) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (Object element$iv : $this$associate) {
         var var9: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var9.getFirst(), var9.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K> Sequence<T>.associateBy(keySelector: (T) -> K): Map<K, T> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (Object element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K, V> Sequence<T>.associateBy(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (Object element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K, M : MutableMap<in K, in T>> Sequence<T>.associateByTo(destination: M, keySelector: (T) -> K): M {
      for (Object element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associateByTo(destination: M, keySelector: (T) -> K, valueTransform: (T) -> V): M {
      for (Object element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associateTo(destination: M, transform: (T) -> Pair<K, V>): M {
      for (Object element : $this$associateTo) {
         var var7: Pair = transform.invoke(element) as Pair;
         destination.put(var7.getFirst(), var7.getSecond());
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public inline fun <K, V> Sequence<K>.associateWith(valueSelector: (K) -> V): Map<K, V> {
      var result: LinkedHashMap = new LinkedHashMap();

      for (Object element$iv : $this$associateWith) {
         result.put(`element$iv`, valueSelector.invoke(`element$iv`));
      }

      return result;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> Sequence<K>.associateWithTo(destination: M, valueSelector: (K) -> V): M {
      for (Object element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public fun <T, C : MutableCollection<in T>> Sequence<T>.toCollection(destination: C): C {
      for (Object item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> Sequence<T>.toHashSet(): HashSet<T> {
      return SequencesKt.toCollection(`$this$toHashSet`, new HashSet()) as HashSet<T>;
   }

   @JvmStatic
   public fun <T> Sequence<T>.toList(): List<T> {
      var it: java.util.Iterator = `$this$toList`.iterator();
      if (!it.hasNext()) {
         return CollectionsKt.emptyList();
      } else {
         var element: Any = it.next();
         if (!it.hasNext()) {
            return (java.util.List<T>)CollectionsKt.listOf(element);
         } else {
            var dst: ArrayList = new ArrayList();
            dst.add(element);

            while (it.hasNext()) {
               dst.add(it.next());
            }

            return dst;
         }
      }
   }

   @JvmStatic
   public fun <T> Sequence<T>.toMutableList(): MutableList<T> {
      return SequencesKt.toCollection(`$this$toMutableList`, new ArrayList()) as java.util.List<T>;
   }

   @JvmStatic
   public fun <T> Sequence<T>.toSet(): Set<T> {
      var it: java.util.Iterator = `$this$toSet`.iterator();
      if (!it.hasNext()) {
         return SetsKt.emptySet();
      } else {
         var element: Any = it.next();
         if (!it.hasNext()) {
            return (java.util.Set<T>)SetsKt.setOf(element);
         } else {
            var dst: LinkedHashSet = new LinkedHashSet();
            dst.add(element);

            while (it.hasNext()) {
               dst.add(it.next());
            }

            return dst;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIterable")
   @JvmStatic
   public fun <T, R> Sequence<T>.flatMap(transform: (T) -> Iterable<R>): Sequence<R> {
      return new FlatteningSequence<>(`$this$flatMap`, transform, <unrepresentable>.INSTANCE);
   }

   @JvmStatic
   public fun <T, R> Sequence<T>.flatMap(transform: (T) -> Sequence<R>): Sequence<R> {
      return new FlatteningSequence<>(`$this$flatMap`, transform, <unrepresentable>.INSTANCE);
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @JvmStatic
   public fun <T, R> Sequence<T>.flatMapIndexed(transform: (Int, T) -> Iterable<R>): Sequence<R> {
      return SequencesKt.flatMapIndexed(`$this$flatMapIndexed`, transform, <unrepresentable>.INSTANCE);
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedSequence")
   @JvmStatic
   public fun <T, R> Sequence<T>.flatMapIndexed(transform: (Int, T) -> Sequence<R>): Sequence<R> {
      return SequencesKt.flatMapIndexed(`$this$flatMapIndexed`, transform, <unrepresentable>.INSTANCE);
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.flatMapIndexedTo(destination: C, transform: (Int, T) -> Iterable<R>): C {
      var index: Int = 0;

      for (Object element : $this$flatMapIndexedTo) {
         var var7: Int = index++;
         if (var7 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         CollectionsKt.addAll(destination, transform.invoke(var7, element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedSequenceTo")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.flatMapIndexedTo(destination: C, transform: (Int, T) -> Sequence<R>): C {
      var index: Int = 0;

      for (Object element : $this$flatMapIndexedTo) {
         var var7: Int = index++;
         if (var7 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         CollectionsKt.addAll(destination, transform.invoke(var7, element) as Sequence);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIterableTo")
   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.flatMapTo(destination: C, transform: (T) -> Iterable<R>): C {
      for (Object element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.flatMapTo(destination: C, transform: (T) -> Sequence<R>): C {
      for (Object element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as Sequence);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, K> Sequence<T>.groupBy(keySelector: (T) -> K): Map<K, List<T>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (Object element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var14: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(`element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K, V> Sequence<T>.groupBy(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, List<V>> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (Object element$iv : $this$groupBy) {
         var `key$iv`: Any = keySelector.invoke(`element$iv`);
         var `value$iv$iv`: Any = `destination$iv`.get(`key$iv`);
         var var10000: Any;
         if (`value$iv$iv` == null) {
            var var15: Any = new ArrayList();
            `destination$iv`.put(`key$iv`, var15);
            var10000 = var15;
         } else {
            var10000 = `value$iv$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K, M : MutableMap<in K, MutableList<T>>> Sequence<T>.groupByTo(destination: M, keySelector: (T) -> K): M {
      for (Object element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var12: Any = new ArrayList();
            destination.put(key, var12);
            var10000 = var12;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Sequence<T>.groupByTo(destination: M, keySelector: (T) -> K, valueTransform: (T) -> V): M {
      for (Object element : $this$groupByTo) {
         var key: Any = keySelector.invoke(element);
         var `value$iv`: Any = destination.get(key);
         var var10000: Any;
         if (`value$iv` == null) {
            var var13: Any = new ArrayList();
            destination.put(key, var13);
            var10000 = var13;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T, K> Sequence<T>.groupingBy(crossinline keySelector: (T) -> K): Grouping<T, K> {
      return new Grouping<T, K>(`$this$groupingBy`, keySelector) {
         {
            this.$this_groupingBy = `$receiver`;
            this.$keySelector = `$keySelector`;
         }

         @NotNull
         @Override
         public java.util.Iterator<T> sourceIterator() {
            return this.$this_groupingBy.iterator();
         }

         @Override
         public K keyOf(T element) {
            return (K)this.$keySelector.invoke(element);
         }
      };
   }

   @JvmStatic
   public fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {
      return new TransformingSequence(`$this$map`, transform);
   }

   @JvmStatic
   public fun <T, R> Sequence<T>.mapIndexed(transform: (Int, T) -> R): Sequence<R> {
      return new TransformingIndexedSequence(`$this$mapIndexed`, transform);
   }

   @JvmStatic
   public fun <T, R : Any> Sequence<T>.mapIndexedNotNull(transform: (Int, T) -> R?): Sequence<R> {
      return SequencesKt.filterNotNull(new TransformingIndexedSequence(`$this$mapIndexedNotNull`, transform));
   }

   @JvmStatic
   public inline fun <T, R : Any, C : MutableCollection<in R>> Sequence<T>.mapIndexedNotNullTo(destination: C, transform: (Int, T) -> R?): C {
      var `index$iv`: Int = 0;

      for (Object item$iv : $this$mapIndexedNotNullTo) {
         var var9: Int = `index$iv`++;
         if (var9 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         var var16: Any = transform.invoke(var9, `item$iv`);
         if (var16 != null) {
            destination.add(var16);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.mapIndexedTo(destination: C, transform: (Int, T) -> R): C {
      var index: Int = 0;

      for (Object item : $this$mapIndexedTo) {
         var var7: Int = index++;
         if (var7 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         destination.add(transform.invoke(var7, item));
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T, R : Any> Sequence<T>.mapNotNull(transform: (T) -> R?): Sequence<R> {
      return SequencesKt.filterNotNull(new TransformingSequence(`$this$mapNotNull`, transform));
   }

   @JvmStatic
   public inline fun <T, R : Any, C : MutableCollection<in R>> Sequence<T>.mapNotNullTo(destination: C, transform: (T) -> R?): C {
      for (Object element$iv : $this$mapNotNullTo) {
         var var10000: Any = transform.invoke(`element$iv`);
         if (var10000 != null) {
            destination.add(var10000);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.mapTo(destination: C, transform: (T) -> R): C {
      for (Object item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> Sequence<T>.withIndex(): Sequence<IndexedValue<T>> {
      return new IndexingSequence(`$this$withIndex`);
   }

   @JvmStatic
   public fun <T> Sequence<T>.distinct(): Sequence<T> {
      return SequencesKt.distinctBy(`$this$distinct`, <unrepresentable>.INSTANCE);
   }

   @JvmStatic
   public fun <T, K> Sequence<T>.distinctBy(selector: (T) -> K): Sequence<T> {
      return new DistinctSequence(`$this$distinctBy`, selector);
   }

   @JvmStatic
   public fun <T> Sequence<T>.toMutableSet(): MutableSet<T> {
      var set: LinkedHashSet = new LinkedHashSet();

      for (Object item : $this$toMutableSet) {
         set.add(item);
      }

      return set;
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.all(predicate: (T) -> Boolean): Boolean {
      for (Object element : $this$all) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @JvmStatic
   public fun <T> Sequence<T>.any(): Boolean {
      return `$this$any`.iterator().hasNext();
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.any(predicate: (T) -> Boolean): Boolean {
      for (Object element : $this$any) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return true;
         }
      }

      return false;
   }

   @JvmStatic
   public fun <T> Sequence<T>.count(): Int {
      var count: Int = 0;

      for (Object element : $this$count) {
         if (++count < 0) {
            CollectionsKt.throwCountOverflow();
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.count(predicate: (T) -> Boolean): Int {
      var count: Int = 0;

      for (Object element : $this$count) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (++count < 0) {
               CollectionsKt.throwCountOverflow();
            }
         }
      }

      return count;
   }

   @JvmStatic
   public inline fun <T, R> Sequence<T>.fold(initial: R, operation: (R, T) -> R): R {
      var accumulator: Any = initial;

      for (Object element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <T, R> Sequence<T>.foldIndexed(initial: R, operation: (Int, R, T) -> R): R {
      var index: Int = 0;
      var accumulator: Any = initial;

      for (Object element : $this$foldIndexed) {
         var var8: Int = index++;
         if (var8 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         accumulator = operation.invoke(var8, accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.forEach(action: (T) -> Unit) {
      for (Object element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.forEachIndexed(action: (Int, T) -> Unit) {
      var index: Int = 0;

      for (Object item : $this$forEachIndexed) {
         var var6: Int = index++;
         if (var6 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         action.invoke(var6, item);
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun Sequence<Double>.max(): Double {
      var iterator: java.util.Iterator = `$this$max`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var max: Double = (iterator.next() as java.lang.Number).doubleValue();

         while (iterator.hasNext()) {
            max = Math.max(max, (iterator.next() as java.lang.Number).doubleValue());
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun Sequence<Float>.max(): Float {
      var iterator: java.util.Iterator = `$this$max`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var max: Float = (iterator.next() as java.lang.Number).floatValue();

         while (iterator.hasNext()) {
            max = Math.max(max, (iterator.next() as java.lang.Number).floatValue());
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxOrThrow")
   @JvmStatic
   public fun <T : Comparable<T>> Sequence<T>.max(): T {
      var iterator: java.util.Iterator = `$this$max`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var max: java.lang.Comparable = iterator.next() as java.lang.Comparable;

         while (iterator.hasNext()) {
            var e: java.lang.Comparable = iterator.next() as java.lang.Comparable;
            if (max.compareTo(e) < 0) {
               max = e;
            }
         }

         return (T)max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxByOrThrow")
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Sequence<T>.maxBy(selector: (T) -> R): T {
      var iterator: java.util.Iterator = `$this$maxBy`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var maxElem: Any = iterator.next();
         if (!iterator.hasNext()) {
            return (T)maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;

            do {
               var e: Any = iterator.next();
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            } while (iterator.hasNext());

            return (T)maxElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Sequence<T>.maxByOrNull(selector: (T) -> R): T? {
      var iterator: java.util.Iterator = `$this$maxByOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var maxElem: Any = iterator.next();
         if (!iterator.hasNext()) {
            return (T)maxElem;
         } else {
            var maxValue: java.lang.Comparable = selector.invoke(maxElem) as java.lang.Comparable;

            do {
               var e: Any = iterator.next();
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (maxValue.compareTo(v) < 0) {
                  maxElem = e;
                  maxValue = v;
               }
            } while (iterator.hasNext());

            return (T)maxElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.maxOf(selector: (T) -> Double): Double {
      var iterator: java.util.Iterator = `$this$maxOf`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Double = (selector.invoke(iterator.next()) as java.lang.Number).doubleValue();

         while (iterator.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(iterator.next()) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.maxOf(selector: (T) -> Float): Float {
      var iterator: java.util.Iterator = `$this$maxOf`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Float = (selector.invoke(iterator.next()) as java.lang.Number).floatValue();

         while (iterator.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(iterator.next()) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Sequence<T>.maxOf(selector: (T) -> R): R {
      var iterator: java.util.Iterator = `$this$maxOf`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(iterator.next()) as java.lang.Comparable;

         while (iterator.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(iterator.next()) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.maxOfOrNull(selector: (T) -> Double): Double? {
      var iterator: java.util.Iterator = `$this$maxOfOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var maxValue: Double = (selector.invoke(iterator.next()) as java.lang.Number).doubleValue();

         while (iterator.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(iterator.next()) as java.lang.Number).doubleValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.maxOfOrNull(selector: (T) -> Float): Float? {
      var iterator: java.util.Iterator = `$this$maxOfOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var maxValue: Float = (selector.invoke(iterator.next()) as java.lang.Number).floatValue();

         while (iterator.hasNext()) {
            maxValue = Math.max(maxValue, (selector.invoke(iterator.next()) as java.lang.Number).floatValue());
         }

         return maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Sequence<T>.maxOfOrNull(selector: (T) -> R): R? {
      var iterator: java.util.Iterator = `$this$maxOfOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var maxValue: java.lang.Comparable = selector.invoke(iterator.next()) as java.lang.Comparable;

         while (iterator.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(iterator.next()) as java.lang.Comparable;
            if (maxValue.compareTo(v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Sequence<T>.maxOfWith(comparator: Comparator<in R>, selector: (T) -> R): R {
      var iterator: java.util.Iterator = `$this$maxOfWith`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var maxValue: Any = selector.invoke(iterator.next());

         while (iterator.hasNext()) {
            var v: Any = selector.invoke(iterator.next());
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Sequence<T>.maxOfWithOrNull(comparator: Comparator<in R>, selector: (T) -> R): R? {
      var iterator: java.util.Iterator = `$this$maxOfWithOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var maxValue: Any = selector.invoke(iterator.next());

         while (iterator.hasNext()) {
            var v: Any = selector.invoke(iterator.next());
            if (comparator.compare(maxValue, v) < 0) {
               maxValue = v;
            }
         }

         return (R)maxValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun Sequence<Double>.maxOrNull(): Double? {
      var iterator: java.util.Iterator = `$this$maxOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var max: Double = (iterator.next() as java.lang.Number).doubleValue();

         while (iterator.hasNext()) {
            max = Math.max(max, (iterator.next() as java.lang.Number).doubleValue());
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun Sequence<Float>.maxOrNull(): Float? {
      var iterator: java.util.Iterator = `$this$maxOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var max: Float = (iterator.next() as java.lang.Number).floatValue();

         while (iterator.hasNext()) {
            max = Math.max(max, (iterator.next() as java.lang.Number).floatValue());
         }

         return max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T : Comparable<T>> Sequence<T>.maxOrNull(): T? {
      var iterator: java.util.Iterator = `$this$maxOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var max: java.lang.Comparable = iterator.next() as java.lang.Comparable;

         while (iterator.hasNext()) {
            var e: java.lang.Comparable = iterator.next() as java.lang.Comparable;
            if (max.compareTo(e) < 0) {
               max = e;
            }
         }

         return (T)max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @JvmStatic
   public fun <T> Sequence<T>.maxWith(comparator: Comparator<in T>): T {
      var iterator: java.util.Iterator = `$this$maxWith`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var max: Any = iterator.next();

         while (iterator.hasNext()) {
            var e: Any = iterator.next();
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return (T)max;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> Sequence<T>.maxWithOrNull(comparator: Comparator<in T>): T? {
      var iterator: java.util.Iterator = `$this$maxWithOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var max: Any = iterator.next();

         while (iterator.hasNext()) {
            var e: Any = iterator.next();
            if (comparator.compare(max, e) < 0) {
               max = e;
            }
         }

         return (T)max;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun Sequence<Double>.min(): Double {
      var iterator: java.util.Iterator = `$this$min`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var min: Double = (iterator.next() as java.lang.Number).doubleValue();

         while (iterator.hasNext()) {
            min = Math.min(min, (iterator.next() as java.lang.Number).doubleValue());
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun Sequence<Float>.min(): Float {
      var iterator: java.util.Iterator = `$this$min`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var min: Float = (iterator.next() as java.lang.Number).floatValue();

         while (iterator.hasNext()) {
            min = Math.min(min, (iterator.next() as java.lang.Number).floatValue());
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minOrThrow")
   @JvmStatic
   public fun <T : Comparable<T>> Sequence<T>.min(): T {
      var iterator: java.util.Iterator = `$this$min`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var min: java.lang.Comparable = iterator.next() as java.lang.Comparable;

         while (iterator.hasNext()) {
            var e: java.lang.Comparable = iterator.next() as java.lang.Comparable;
            if (min.compareTo(e) > 0) {
               min = e;
            }
         }

         return (T)min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minByOrThrow")
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Sequence<T>.minBy(selector: (T) -> R): T {
      var iterator: java.util.Iterator = `$this$minBy`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var minElem: Any = iterator.next();
         if (!iterator.hasNext()) {
            return (T)minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;

            do {
               var e: Any = iterator.next();
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            } while (iterator.hasNext());

            return (T)minElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Sequence<T>.minByOrNull(selector: (T) -> R): T? {
      var iterator: java.util.Iterator = `$this$minByOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var minElem: Any = iterator.next();
         if (!iterator.hasNext()) {
            return (T)minElem;
         } else {
            var minValue: java.lang.Comparable = selector.invoke(minElem) as java.lang.Comparable;

            do {
               var e: Any = iterator.next();
               var v: java.lang.Comparable = selector.invoke(e) as java.lang.Comparable;
               if (minValue.compareTo(v) > 0) {
                  minElem = e;
                  minValue = v;
               }
            } while (iterator.hasNext());

            return (T)minElem;
         }
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.minOf(selector: (T) -> Double): Double {
      var iterator: java.util.Iterator = `$this$minOf`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var minValue: Double = (selector.invoke(iterator.next()) as java.lang.Number).doubleValue();

         while (iterator.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(iterator.next()) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.minOf(selector: (T) -> Float): Float {
      var iterator: java.util.Iterator = `$this$minOf`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var minValue: Float = (selector.invoke(iterator.next()) as java.lang.Number).floatValue();

         while (iterator.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(iterator.next()) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Sequence<T>.minOf(selector: (T) -> R): R {
      var iterator: java.util.Iterator = `$this$minOf`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var minValue: java.lang.Comparable = selector.invoke(iterator.next()) as java.lang.Comparable;

         while (iterator.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(iterator.next()) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.minOfOrNull(selector: (T) -> Double): Double? {
      var iterator: java.util.Iterator = `$this$minOfOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var minValue: Double = (selector.invoke(iterator.next()) as java.lang.Number).doubleValue();

         while (iterator.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(iterator.next()) as java.lang.Number).doubleValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.minOfOrNull(selector: (T) -> Float): Float? {
      var iterator: java.util.Iterator = `$this$minOfOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var minValue: Float = (selector.invoke(iterator.next()) as java.lang.Number).floatValue();

         while (iterator.hasNext()) {
            minValue = Math.min(minValue, (selector.invoke(iterator.next()) as java.lang.Number).floatValue());
         }

         return minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Comparable<R>> Sequence<T>.minOfOrNull(selector: (T) -> R): R? {
      var iterator: java.util.Iterator = `$this$minOfOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var minValue: java.lang.Comparable = selector.invoke(iterator.next()) as java.lang.Comparable;

         while (iterator.hasNext()) {
            var v: java.lang.Comparable = selector.invoke(iterator.next()) as java.lang.Comparable;
            if (minValue.compareTo(v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Sequence<T>.minOfWith(comparator: Comparator<in R>, selector: (T) -> R): R {
      var iterator: java.util.Iterator = `$this$minOfWith`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var minValue: Any = selector.invoke(iterator.next());

         while (iterator.hasNext()) {
            var v: Any = selector.invoke(iterator.next());
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Sequence<T>.minOfWithOrNull(comparator: Comparator<in R>, selector: (T) -> R): R? {
      var iterator: java.util.Iterator = `$this$minOfWithOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var minValue: Any = selector.invoke(iterator.next());

         while (iterator.hasNext()) {
            var v: Any = selector.invoke(iterator.next());
            if (comparator.compare(minValue, v) > 0) {
               minValue = v;
            }
         }

         return (R)minValue;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun Sequence<Double>.minOrNull(): Double? {
      var iterator: java.util.Iterator = `$this$minOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var min: Double = (iterator.next() as java.lang.Number).doubleValue();

         while (iterator.hasNext()) {
            min = Math.min(min, (iterator.next() as java.lang.Number).doubleValue());
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun Sequence<Float>.minOrNull(): Float? {
      var iterator: java.util.Iterator = `$this$minOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var min: Float = (iterator.next() as java.lang.Number).floatValue();

         while (iterator.hasNext()) {
            min = Math.min(min, (iterator.next() as java.lang.Number).floatValue());
         }

         return min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T : Comparable<T>> Sequence<T>.minOrNull(): T? {
      var iterator: java.util.Iterator = `$this$minOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var min: java.lang.Comparable = iterator.next() as java.lang.Comparable;

         while (iterator.hasNext()) {
            var e: java.lang.Comparable = iterator.next() as java.lang.Comparable;
            if (min.compareTo(e) > 0) {
               min = e;
            }
         }

         return (T)min;
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @JvmStatic
   public fun <T> Sequence<T>.minWith(comparator: Comparator<in T>): T {
      var iterator: java.util.Iterator = `$this$minWith`.iterator();
      if (!iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var min: Any = iterator.next();

         while (iterator.hasNext()) {
            var e: Any = iterator.next();
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return (T)min;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> Sequence<T>.minWithOrNull(comparator: Comparator<in T>): T? {
      var iterator: java.util.Iterator = `$this$minWithOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var min: Any = iterator.next();

         while (iterator.hasNext()) {
            var e: Any = iterator.next();
            if (comparator.compare(min, e) > 0) {
               min = e;
            }
         }

         return (T)min;
      }
   }

   @JvmStatic
   public fun <T> Sequence<T>.none(): Boolean {
      return !`$this$none`.iterator().hasNext();
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.none(predicate: (T) -> Boolean): Boolean {
      for (Object element : $this$none) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return false;
         }
      }

      return true;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <T> Sequence<T>.onEach(action: (T) -> Unit): Sequence<T> {
      return SequencesKt.map(`$this$onEach`, (new Function1<T, T>(action) {
         {
            super(1);
            this.$action = `$action`;
         }

         @Override
         public final T invoke(T it) {
            this.$action.invoke(it);
            return (T)it;
         }
      }) as Function1);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> Sequence<T>.onEachIndexed(action: (Int, T) -> Unit): Sequence<T> {
      return SequencesKt.mapIndexed(`$this$onEachIndexed`, (new Function2<Integer, T, T>(action) {
         {
            super(2);
            this.$action = `$action`;
         }

         public final T invoke(int index, T element) {
            this.$action.invoke(index, element);
            return (T)element;
         }
      }) as Function2);
   }

   @JvmStatic
   public inline fun <S, T : S> Sequence<T>.reduce(operation: (S, T) -> S): S {
      var iterator: java.util.Iterator = `$this$reduce`.iterator();
      if (!iterator.hasNext()) {
         throw new UnsupportedOperationException("Empty sequence can't be reduced.");
      } else {
         var accumulator: Any = iterator.next();

         while (iterator.hasNext()) {
            accumulator = operation.invoke(accumulator, iterator.next());
         }

         return (S)accumulator;
      }
   }

   @JvmStatic
   public inline fun <S, T : S> Sequence<T>.reduceIndexed(operation: (Int, S, T) -> S): S {
      var iterator: java.util.Iterator = `$this$reduceIndexed`.iterator();
      if (!iterator.hasNext()) {
         throw new UnsupportedOperationException("Empty sequence can't be reduced.");
      } else {
         var index: Int = 1;
         var accumulator: Any = iterator.next();

         while (iterator.hasNext()) {
            var var6: Int = index++;
            if (var6 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            accumulator = operation.invoke(var6, accumulator, iterator.next());
         }

         return (S)accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <S, T : S> Sequence<T>.reduceIndexedOrNull(operation: (Int, S, T) -> S): S? {
      var iterator: java.util.Iterator = `$this$reduceIndexedOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var index: Int = 1;
         var accumulator: Any = iterator.next();

         while (iterator.hasNext()) {
            var var6: Int = index++;
            if (var6 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            accumulator = operation.invoke(var6, accumulator, iterator.next());
         }

         return (S)accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <S, T : S> Sequence<T>.reduceOrNull(operation: (S, T) -> S): S? {
      var iterator: java.util.Iterator = `$this$reduceOrNull`.iterator();
      if (!iterator.hasNext()) {
         return null;
      } else {
         var accumulator: Any = iterator.next();

         while (iterator.hasNext()) {
            accumulator = operation.invoke(accumulator, iterator.next());
         }

         return (S)accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T, R> Sequence<T>.runningFold(initial: R, operation: (R, T) -> R): Sequence<R> {
      return SequencesKt.sequence((new Function2<SequenceScope<? super R>, Continuation<? super Unit>, Object>(initial, `$this$runningFold`, operation, null) {
         Object L$1;
         Object L$2;
         int label;

         {
            super(2, `$completion`);
            this.$initial = (R)`$initial`;
            this.$this_runningFold = `$receiver`;
            this.$operation = `$operation`;
         }

         // $VF: Irreducible bytecode was duplicated to produce valid code
         @Nullable
         @Override
         public final Object invokeSuspend(@NotNull Object $result) {
            var `$this$sequence`: SequenceScope;
            var accumulator: Any;
            var var4: java.util.Iterator;
            var var6: Any;
            label28: {
               var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);
                     `$this$sequence` = this.L$0 as SequenceScope;
                     var var10001: Any = this.$initial;
                     var var10002: Continuation = this;
                     this.L$0 = `$this$sequence`;
                     this.label = 1;
                     if (`$this$sequence`.yield(var10001, var10002) === var6) {
                        return var6;
                     }
                     break;
                  case 1:
                     `$this$sequence` = this.L$0 as SequenceScope;
                     ResultKt.throwOnFailure(`$result`);
                     break;
                  case 2:
                     var4 = this.L$2 as java.util.Iterator;
                     accumulator = this.L$1;
                     `$this$sequence` = this.L$0 as SequenceScope;
                     ResultKt.throwOnFailure(`$result`);
                     break label28;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               accumulator = this.$initial;
               var4 = this.$this_runningFold.iterator();
            }

            while (var4.hasNext()) {
               accumulator = this.$operation.invoke(accumulator, var4.next());
               var var7: Continuation = this;
               this.L$0 = `$this$sequence`;
               this.L$1 = accumulator;
               this.L$2 = var4;
               this.label = 2;
               if (`$this$sequence`.yield(accumulator, var7) === var6) {
                  return var6;
               }
            }

            return Unit.INSTANCE;
         }

         @NotNull
         @Override
         public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
            var var3: Function2 = new <anonymous constructor>(this.$initial, this.$this_runningFold, this.$operation, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         @Nullable
         public final Object invoke(@NotNull SequenceScope<? super R> p1, @Nullable Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T, R> Sequence<T>.runningFoldIndexed(initial: R, operation: (Int, R, T) -> R): Sequence<R> {
      return SequencesKt.sequence(
         (new Function2<SequenceScope<? super R>, Continuation<? super Unit>, Object>(initial, `$this$runningFoldIndexed`, operation, null) {
            Object L$1;
            Object L$2;
            int I$0;
            int label;

            {
               super(2, `$completion`);
               this.$initial = (R)`$initial`;
               this.$this_runningFoldIndexed = `$receiver`;
               this.$operation = `$operation`;
            }

            // $VF: Irreducible bytecode was duplicated to produce valid code
            @Nullable
            @Override
            public final Object invokeSuspend(@NotNull Object $result) {
               var `$this$sequence`: SequenceScope;
               var index: Int;
               var accumulator: Any;
               var var5: java.util.Iterator;
               var var8: Any;
               label32: {
                  var8 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        `$this$sequence` = this.L$0 as SequenceScope;
                        var var10001: Any = this.$initial;
                        var var10002: Continuation = this;
                        this.L$0 = `$this$sequence`;
                        this.label = 1;
                        if (`$this$sequence`.yield(var10001, var10002) === var8) {
                           return var8;
                        }
                        break;
                     case 1:
                        `$this$sequence` = this.L$0 as SequenceScope;
                        ResultKt.throwOnFailure(`$result`);
                        break;
                     case 2:
                        index = this.I$0;
                        var5 = this.L$2 as java.util.Iterator;
                        accumulator = this.L$1;
                        `$this$sequence` = this.L$0 as SequenceScope;
                        ResultKt.throwOnFailure(`$result`);
                        break label32;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }

                  index = 0;
                  accumulator = this.$initial;
                  var5 = this.$this_runningFoldIndexed.iterator();
               }

               while (var5.hasNext()) {
                  var element: Any = var5.next();
                  var var7: Int = index++;
                  if (var7 < 0) {
                     CollectionsKt.throwIndexOverflow();
                  }

                  accumulator = this.$operation.invoke(Boxing.boxInt(var7), accumulator, element);
                  var var10: Continuation = this;
                  this.L$0 = `$this$sequence`;
                  this.L$1 = accumulator;
                  this.L$2 = var5;
                  this.I$0 = index;
                  this.label = 2;
                  if (`$this$sequence`.yield(accumulator, var10) === var8) {
                     return var8;
                  }
               }

               return Unit.INSTANCE;
            }

            @NotNull
            @Override
            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
               var var3: Function2 = new <anonymous constructor>(this.$initial, this.$this_runningFoldIndexed, this.$operation, `$completion`);
               var3.L$0 = value;
               return var3 as Continuation<Unit>;
            }

            @Nullable
            public final Object invoke(@NotNull SequenceScope<? super R> p1, @Nullable Continuation<? super Unit> p2) {
               return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
            }
         }) as Function2
      );
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun <S, T : S> Sequence<T>.runningReduce(operation: (S, T) -> S): Sequence<S> {
      return SequencesKt.sequence((new Function2<SequenceScope<? super S>, Continuation<? super Unit>, Object>(`$this$runningReduce`, operation, null) {
         Object L$1;
         Object L$2;
         int label;

         {
            super(2, `$completion`);
            this.$this_runningReduce = `$receiver`;
            this.$operation = `$operation`;
         }

         // $VF: Irreducible bytecode was duplicated to produce valid code
         @Nullable
         @Override
         public final Object invokeSuspend(@NotNull Object $result) {
            var var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            var `$this$sequence`: SequenceScope;
            var iterator: java.util.Iterator;
            var accumulator: Any;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  `$this$sequence` = this.L$0 as SequenceScope;
                  iterator = this.$this_runningReduce.iterator();
                  if (!iterator.hasNext()) {
                     return Unit.INSTANCE;
                  }

                  accumulator = iterator.next();
                  var var10002: Continuation = this;
                  this.L$0 = `$this$sequence`;
                  this.L$1 = iterator;
                  this.L$2 = accumulator;
                  this.label = 1;
                  if (`$this$sequence`.yield(accumulator, var10002) === var5) {
                     return var5;
                  }
                  break;
               case 1:
                  accumulator = this.L$2;
                  iterator = this.L$1 as java.util.Iterator;
                  `$this$sequence` = this.L$0 as SequenceScope;
                  ResultKt.throwOnFailure(`$result`);
                  break;
               case 2:
                  accumulator = this.L$2;
                  iterator = this.L$1 as java.util.Iterator;
                  `$this$sequence` = this.L$0 as SequenceScope;
                  ResultKt.throwOnFailure(`$result`);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            var var6: Continuation;
            do {
               if (!iterator.hasNext()) {
                  return Unit.INSTANCE;
               }

               accumulator = this.$operation.invoke(accumulator, iterator.next());
               var6 = this;
               this.L$0 = `$this$sequence`;
               this.L$1 = iterator;
               this.L$2 = accumulator;
               this.label = 2;
            } while ($this$sequence.yield(accumulator, var6) != var5);

            return var5;
         }

         @NotNull
         @Override
         public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
            var var3: Function2 = new <anonymous constructor>(this.$this_runningReduce, this.$operation, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         @Nullable
         public final Object invoke(@NotNull SequenceScope<? super S> p1, @Nullable Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <S, T : S> Sequence<T>.runningReduceIndexed(operation: (Int, S, T) -> S): Sequence<S> {
      return SequencesKt.sequence((new Function2<SequenceScope<? super S>, Continuation<? super Unit>, Object>(`$this$runningReduceIndexed`, operation, null) {
         Object L$1;
         Object L$2;
         int I$0;
         int label;

         {
            super(2, `$completion`);
            this.$this_runningReduceIndexed = `$receiver`;
            this.$operation = `$operation`;
         }

         // $VF: Irreducible bytecode was duplicated to produce valid code
         @Nullable
         @Override
         public final Object invokeSuspend(@NotNull Object $result) {
            var `$this$sequence`: SequenceScope;
            var iterator: java.util.Iterator;
            var accumulator: Any;
            var index: Int;
            var var7: Any;
            label36: {
               var7 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);
                     `$this$sequence` = this.L$0 as SequenceScope;
                     iterator = this.$this_runningReduceIndexed.iterator();
                     if (!iterator.hasNext()) {
                        return Unit.INSTANCE;
                     }

                     accumulator = iterator.next();
                     var var10002: Continuation = this;
                     this.L$0 = `$this$sequence`;
                     this.L$1 = iterator;
                     this.L$2 = accumulator;
                     this.label = 1;
                     if (`$this$sequence`.yield(accumulator, var10002) === var7) {
                        return var7;
                     }
                     break;
                  case 1:
                     accumulator = this.L$2;
                     iterator = this.L$1 as java.util.Iterator;
                     `$this$sequence` = this.L$0 as SequenceScope;
                     ResultKt.throwOnFailure(`$result`);
                     break;
                  case 2:
                     index = this.I$0;
                     accumulator = this.L$2;
                     iterator = this.L$1 as java.util.Iterator;
                     `$this$sequence` = this.L$0 as SequenceScope;
                     ResultKt.throwOnFailure(`$result`);
                     break label36;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               index = 1;
            }

            var var8: Continuation;
            do {
               if (!iterator.hasNext()) {
                  return Unit.INSTANCE;
               }

               var var6: Int = index++;
               if (var6 < 0) {
                  CollectionsKt.throwIndexOverflow();
               }

               accumulator = this.$operation.invoke(Boxing.boxInt(var6), accumulator, iterator.next());
               var8 = this;
               this.L$0 = `$this$sequence`;
               this.L$1 = iterator;
               this.L$2 = accumulator;
               this.I$0 = index;
               this.label = 2;
            } while ($this$sequence.yield(accumulator, var8) != var7);

            return var7;
         }

         @NotNull
         @Override
         public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
            var var3: Function2 = new <anonymous constructor>(this.$this_runningReduceIndexed, this.$operation, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         @Nullable
         public final Object invoke(@NotNull SequenceScope<? super S> p1, @Nullable Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun <T, R> Sequence<T>.scan(initial: R, operation: (R, T) -> R): Sequence<R> {
      return (Sequence<R>)SequencesKt.runningFold(`$this$scan`, initial, operation);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun <T, R> Sequence<T>.scanIndexed(initial: R, operation: (Int, R, T) -> R): Sequence<R> {
      return (Sequence<R>)SequencesKt.runningFoldIndexed(`$this$scanIndexed`, initial, operation);
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun <T> Sequence<T>.sumBy(selector: (T) -> Int): Int {
      var sum: Int = 0;

      for (Object element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun <T> Sequence<T>.sumByDouble(selector: (T) -> Double): Double {
      var sum: Double = 0.0;

      for (Object element : $this$sumByDouble) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfDouble")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.sumOf(selector: (T) -> Double): Double {
      var sum: Double = 0.0;

      for (Object element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).doubleValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfInt")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.sumOf(selector: (T) -> Int): Int {
      var sum: Int = 0;

      for (Object element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfLong")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.sumOf(selector: (T) -> Long): Long {
      var sum: Long = 0L;

      for (Object element : $this$sumOf) {
         sum += (selector.invoke(element) as java.lang.Number).longValue();
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfUInt")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.sumOf(selector: (T) -> UInt): UInt {
      var sum: Int = UInt.constructor-impl(0);

      for (Object element : $this$sumOf) {
         sum = UInt.constructor-impl(sum + (selector.invoke(element) as UInt).unbox-impl());
      }

      return sum;
   }

   @SinceKotlin(version = "1.5")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfULong")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.sumOf(selector: (T) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (Object element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @JvmStatic
   public fun <T : Any> Sequence<T?>.requireNoNulls(): Sequence<T> {
      return SequencesKt.map(`$this$requireNoNulls`, (new Function1<T, T>(`$this$requireNoNulls`) {
         {
            super(1);
            this.$this_requireNoNulls = `$receiver`;
         }

         @NotNull
         @Override
         public final T invoke(@Nullable T it) {
            if (it == null) {
               throw new IllegalArgumentException("null element found in " + this.$this_requireNoNulls + '.');
            } else {
               return (T)it;
            }
         }
      }) as Function1);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T> Sequence<T>.chunked(size: Int): Sequence<List<T>> {
      return SequencesKt.windowed(`$this$chunked`, size, size, true);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T, R> Sequence<T>.chunked(size: Int, transform: (List<T>) -> R): Sequence<R> {
      return SequencesKt.windowed(`$this$chunked`, size, size, true, transform);
   }

   @JvmStatic
   public operator fun <T> Sequence<T>.minus(element: T): Sequence<T> {
      return new Sequence<T>(`$this$minus`, element) {
         {
            this.$this_minus = `$receiver`;
            this.$element = (T)`$element`;
         }

         @NotNull
         @Override
         public java.util.Iterator<T> iterator() {
            var removed: Ref.BooleanRef = new Ref.BooleanRef();
            return SequencesKt.filter(this.$this_minus, (new Function1<T, java.lang.Boolean>(removed, this.$element) {
               {
                  super(1);
                  this.$removed = `$removed`;
                  this.$element = (T)`$element`;
               }

               @NotNull
               public final java.lang.Boolean invoke(T it) {
                  var var10000: Boolean;
                  if (!this.$removed.element && it == this.$element) {
                     this.$removed.element = true;
                     var10000 = false;
                  } else {
                     var10000 = true;
                  }

                  return var10000;
               }
            }) as Function1).iterator();
         }
      };
   }

   @JvmStatic
   public operator fun <T> Sequence<T>.minus(elements: Array<out T>): Sequence<T> {
      return if (elements.length == 0) `$this$minus` else new Sequence<T>(`$this$minus`, elements) {
         {
            this.$this_minus = `$receiver`;
            this.$elements = (T[])`$elements`;
         }

         @NotNull
         @Override
         public java.util.Iterator<T> iterator() {
            return SequencesKt.filterNot(this.$this_minus, (new Function1<T, java.lang.Boolean>(this.$elements) {
               {
                  super(1);
                  this.$elements = (T[])`$elements`;
               }

               @NotNull
               public final java.lang.Boolean invoke(T it) {
                  return ArraysKt.contains(this.$elements, it);
               }
            }) as Function1).iterator();
         }
      };
   }

   @JvmStatic
   public operator fun <T> Sequence<T>.minus(elements: Iterable<T>): Sequence<T> {
      return new Sequence<T>(elements, `$this$minus`) {
         {
            this.$elements = `$elements`;
            this.$this_minus = `$receiver`;
         }

         @NotNull
         @Override
         public java.util.Iterator<T> iterator() {
            var other: java.util.Collection = CollectionsKt.convertToListIfNotCollection(this.$elements);
            return if (other.isEmpty()) this.$this_minus.iterator() else SequencesKt.filterNot(this.$this_minus, (new Function1<T, java.lang.Boolean>(other) {
               {
                  super(1);
                  this.$other = `$other`;
               }

               @NotNull
               public final java.lang.Boolean invoke(T it) {
                  return this.$other.contains(it);
               }
            }) as Function1).iterator();
         }
      };
   }

   @JvmStatic
   public operator fun <T> Sequence<T>.minus(elements: Sequence<T>): Sequence<T> {
      return new Sequence<T>(elements, `$this$minus`) {
         {
            this.$elements = `$elements`;
            this.$this_minus = `$receiver`;
         }

         @NotNull
         @Override
         public java.util.Iterator<T> iterator() {
            var other: java.util.List = SequencesKt.toList(this.$elements);
            return if (other.isEmpty()) this.$this_minus.iterator() else SequencesKt.filterNot(this.$this_minus, (new Function1<T, java.lang.Boolean>(other) {
               {
                  super(1);
                  this.$other = `$other`;
               }

               @NotNull
               public final java.lang.Boolean invoke(T it) {
                  return this.$other.contains(it);
               }
            }) as Function1).iterator();
         }
      };
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.minusElement(element: T): Sequence<T> {
      return (Sequence<T>)SequencesKt.minus(`$this$minusElement`, element);
   }

   @JvmStatic
   public inline fun <T> Sequence<T>.partition(predicate: (T) -> Boolean): Pair<List<T>, List<T>> {
      var first: ArrayList = new ArrayList();
      var second: ArrayList = new ArrayList();

      for (Object element : $this$partition) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      return new Pair<>(first, second);
   }

   @JvmStatic
   public operator fun <T> Sequence<T>.plus(element: T): Sequence<T> {
      return SequencesKt.flatten(SequencesKt.sequenceOf(new Sequence[]{`$this$plus`, SequencesKt.sequenceOf(new Object[]{element})}));
   }

   @JvmStatic
   public operator fun <T> Sequence<T>.plus(elements: Array<out T>): Sequence<T> {
      return (Sequence<T>)SequencesKt.plus(`$this$plus`, ArraysKt.asList(elements));
   }

   @JvmStatic
   public operator fun <T> Sequence<T>.plus(elements: Iterable<T>): Sequence<T> {
      return SequencesKt.flatten(SequencesKt.sequenceOf(new Sequence[]{`$this$plus`, CollectionsKt.asSequence(elements)}));
   }

   @JvmStatic
   public operator fun <T> Sequence<T>.plus(elements: Sequence<T>): Sequence<T> {
      return SequencesKt.flatten(SequencesKt.sequenceOf(new Sequence[]{`$this$plus`, elements}));
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.plusElement(element: T): Sequence<T> {
      return (Sequence<T>)SequencesKt.plus(`$this$plusElement`, element);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T> Sequence<T>.windowed(size: Int, step: Int, partialWindows: Boolean): Sequence<List<T>> {
      return SlidingWindowKt.windowedSequence(`$this$windowed`, size, step, partialWindows, false);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T, R> Sequence<T>.windowed(size: Int, step: Int, partialWindows: Boolean, transform: (List<T>) -> R): Sequence<R> {
      return SequencesKt.map(SlidingWindowKt.windowedSequence(`$this$windowed`, size, step, partialWindows, true), transform);
   }

   @JvmStatic
   public infix fun <T, R> Sequence<T>.zip(other: Sequence<R>): Sequence<Pair<T, R>> {
      return new MergingSequence<>(`$this$zip`, other, <unrepresentable>.INSTANCE);
   }

   @JvmStatic
   public fun <T, R, V> Sequence<T>.zip(other: Sequence<R>, transform: (T, R) -> V): Sequence<V> {
      return new MergingSequence(`$this$zip`, other, transform);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T> Sequence<T>.zipWithNext(): Sequence<Pair<T, T>> {
      return SequencesKt.zipWithNext(`$this$zipWithNext`, <unrepresentable>.INSTANCE);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T, R> Sequence<T>.zipWithNext(transform: (T, T) -> R): Sequence<R> {
      return SequencesKt.sequence((new Function2<SequenceScope<? super R>, Continuation<? super Unit>, Object>(`$this$zipWithNext`, transform, null) {
         Object L$1;
         Object L$2;
         int label;

         {
            super(2, `$completion`);
            this.$this_zipWithNext = `$receiver`;
            this.$transform = `$transform`;
         }

         // $VF: Irreducible bytecode was duplicated to produce valid code
         @Nullable
         @Override
         public final Object invokeSuspend(@NotNull Object $result) {
            var var6: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            var `$this$result`: SequenceScope;
            var iterator: java.util.Iterator;
            var current: Any;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  `$this$result` = this.L$0 as SequenceScope;
                  iterator = this.$this_zipWithNext.iterator();
                  if (!iterator.hasNext()) {
                     return Unit.INSTANCE;
                  }

                  current = iterator.next();
                  break;
               case 1:
                  var next: Any = this.L$2;
                  iterator = this.L$1 as java.util.Iterator;
                  `$this$result` = this.L$0 as SequenceScope;
                  ResultKt.throwOnFailure(`$result`);
                  current = next;
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            while (iterator.hasNext()) {
               var var7: Any = iterator.next();
               var var10001: Any = this.$transform.invoke(current, var7);
               var var10002: Continuation = this;
               this.L$0 = `$this$result`;
               this.L$1 = iterator;
               this.L$2 = var7;
               this.label = 1;
               if (`$this$result`.yield(var10001, var10002) === var6) {
                  return var6;
               }

               current = var7;
            }

            return Unit.INSTANCE;
         }

         @NotNull
         @Override
         public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
            var var3: Function2 = new <anonymous constructor>(this.$this_zipWithNext, this.$transform, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         @Nullable
         public final Object invoke(@NotNull SequenceScope<? super R> p1, @Nullable Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2);
   }

   @JvmStatic
   public fun <T, A : Appendable> Sequence<T>.joinTo(
      buffer: A,
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((T) -> CharSequence)?
   ): A {
      buffer.append(prefix);
      var count: Int = 0;

      for (Object element : $this$joinTo) {
         if (++count > 1) {
            buffer.append(separator);
         }

         if (limit >= 0 && count > limit) {
            break;
         }

         StringsKt.appendElement(buffer, element, transform);
      }

      if (limit >= 0 && count > limit) {
         buffer.append(truncated);
      }

      buffer.append(postfix);
      return (A)buffer;
   }

   @JvmStatic
   public fun <T> Sequence<T>.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((T) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = SequencesKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @JvmStatic
   public fun <T> Sequence<T>.asIterable(): Iterable<T> {
      return new SequencesKt___SequencesKt$asIterable$$inlined$Iterable$1(`$this$asIterable`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>.asSequence(): Sequence<T> {
      return `$this$asSequence`;
   }

   @JvmName(name = "averageOfByte")
   @JvmStatic
   public fun Sequence<Byte>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: java.util.Iterator = `$this$average`.iterator();

      while (var4.hasNext()) {
         sum += (double)(var4.next() as java.lang.Number).byteValue();
         if (++count < 0) {
            CollectionsKt.throwCountOverflow();
         }
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "averageOfShort")
   @JvmStatic
   public fun Sequence<Short>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: java.util.Iterator = `$this$average`.iterator();

      while (var4.hasNext()) {
         sum += (double)(var4.next() as java.lang.Number).shortValue();
         if (++count < 0) {
            CollectionsKt.throwCountOverflow();
         }
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "averageOfInt")
   @JvmStatic
   public fun Sequence<Int>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: java.util.Iterator = `$this$average`.iterator();

      while (var4.hasNext()) {
         sum += (double)(var4.next() as java.lang.Number).intValue();
         if (++count < 0) {
            CollectionsKt.throwCountOverflow();
         }
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "averageOfLong")
   @JvmStatic
   public fun Sequence<Long>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: java.util.Iterator = `$this$average`.iterator();

      while (var4.hasNext()) {
         sum += (double)(var4.next() as java.lang.Number).longValue();
         if (++count < 0) {
            CollectionsKt.throwCountOverflow();
         }
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "averageOfFloat")
   @JvmStatic
   public fun Sequence<Float>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: java.util.Iterator = `$this$average`.iterator();

      while (var4.hasNext()) {
         sum += (double)(var4.next() as java.lang.Number).floatValue();
         if (++count < 0) {
            CollectionsKt.throwCountOverflow();
         }
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "averageOfDouble")
   @JvmStatic
   public fun Sequence<Double>.average(): Double {
      var sum: Double = 0.0;
      var count: Int = 0;
      var var4: java.util.Iterator = `$this$average`.iterator();

      while (var4.hasNext()) {
         sum += (var4.next() as java.lang.Number).doubleValue();
         if (++count < 0) {
            CollectionsKt.throwCountOverflow();
         }
      }

      return if (count == 0) java.lang.Double.NaN else sum / (double)count;
   }

   @JvmName(name = "sumOfByte")
   @JvmStatic
   public fun Sequence<Byte>.sum(): Int {
      var sum: Int = 0;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum += (var2.next() as java.lang.Number).byteValue();
      }

      return sum;
   }

   @JvmName(name = "sumOfShort")
   @JvmStatic
   public fun Sequence<Short>.sum(): Int {
      var sum: Int = 0;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum += (var2.next() as java.lang.Number).shortValue();
      }

      return sum;
   }

   @JvmName(name = "sumOfInt")
   @JvmStatic
   public fun Sequence<Int>.sum(): Int {
      var sum: Int = 0;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum += (var2.next() as java.lang.Number).intValue();
      }

      return sum;
   }

   @JvmName(name = "sumOfLong")
   @JvmStatic
   public fun Sequence<Long>.sum(): Long {
      var sum: Long = 0L;
      var var3: java.util.Iterator = `$this$sum`.iterator();

      while (var3.hasNext()) {
         sum += (var3.next() as java.lang.Number).longValue();
      }

      return sum;
   }

   @JvmName(name = "sumOfFloat")
   @JvmStatic
   public fun Sequence<Float>.sum(): Float {
      var sum: Float = 0.0F;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum += (var2.next() as java.lang.Number).floatValue();
      }

      return sum;
   }

   @JvmName(name = "sumOfDouble")
   @JvmStatic
   public fun Sequence<Double>.sum(): Double {
      var sum: Double = 0.0;
      var var3: java.util.Iterator = `$this$sum`.iterator();

      while (var3.hasNext()) {
         sum += (var3.next() as java.lang.Number).doubleValue();
      }

      return sum;
   }

   open fun SequencesKt___SequencesKt() {
   }
}
