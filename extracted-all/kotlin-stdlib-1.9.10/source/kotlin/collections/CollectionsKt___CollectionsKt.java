package kotlin.collections

import java.util.ArrayList
import java.util.Comparator
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.NoSuchElementException
import java.util.RandomAccess
import kotlin.internal.HidesMembers
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\n_Collections.kt\nKotlin\n*S Kotlin\n*F\n+ 1 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 5 Iterators.kt\nkotlin/collections/CollectionsKt__IteratorsKt\n*L\n1#1,3683:1\n288#1,2:3684\n518#1,7:3686\n533#1,6:3693\n857#1,2:3700\n788#1:3702\n1864#1,2:3703\n789#1,2:3705\n1866#1:3707\n791#1:3708\n1864#1,3:3709\n809#1,2:3712\n847#1,2:3714\n1253#1,4:3720\n1222#1,4:3724\n1238#1,4:3728\n1285#1,4:3732\n1446#1,5:3736\n1461#1,5:3741\n1502#1,3:3746\n1505#1,3:3756\n1520#1,3:3759\n1523#1,3:3769\n1620#1,3:3786\n1590#1,4:3789\n1579#1:3793\n1864#1,2:3794\n1866#1:3797\n1580#1:3798\n1864#1,3:3799\n1611#1:3802\n1855#1:3803\n1856#1:3805\n1612#1:3806\n1855#1,2:3807\n1864#1,3:3809\n2847#1,3:3812\n2850#1,6:3816\n2872#1,3:3822\n2875#1,7:3826\n857#1,2:3833\n819#1:3835\n847#1,2:3836\n819#1:3838\n847#1,2:3839\n819#1:3841\n847#1,2:3842\n3405#1,8:3848\n3433#1,7:3856\n3464#1,10:3863\n1#2:3699\n1#2:3796\n1#2:3804\n1#2:3815\n1#2:3825\n37#3,2:3716\n37#3,2:3718\n372#4,7:3749\n372#4,7:3762\n372#4,7:3772\n372#4,7:3779\n32#5,2:3844\n32#5,2:3846\n*S KotlinDebug\n*F\n+ 1 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n167#1:3684,2\n177#1:3686,7\n187#1:3693,6\n766#1:3700,2\n777#1:3702\n777#1:3703,2\n777#1:3705,2\n777#1:3707\n777#1:3708\n788#1:3709,3\n800#1:3712,2\n819#1:3714,2\n1180#1:3720,4\n1195#1:3724,4\n1209#1:3728,4\n1272#1:3732,4\n1360#1:3736,5\n1373#1:3741,5\n1477#1:3746,3\n1477#1:3756,3\n1490#1:3759,3\n1490#1:3769,3\n1549#1:3786,3\n1559#1:3789,4\n1569#1:3793\n1569#1:3794,2\n1569#1:3797\n1569#1:3798\n1579#1:3799,3\n1603#1:3802\n1603#1:3803\n1603#1:3805\n1603#1:3806\n1611#1:3807,2\n2645#1:3809,3\n2949#1:3812,3\n2949#1:3816,6\n2967#1:3822,3\n2967#1:3826,7\n3143#1:3833,2\n3151#1:3835\n3151#1:3836,2\n3161#1:3838\n3161#1:3839,2\n3171#1:3841\n3171#1:3842,2\n3394#1:3848,8\n3422#1:3856,7\n3451#1:3863,10\n1569#1:3796\n1603#1:3804\n2949#1:3815\n2967#1:3825\n1032#1:3716,2\n1075#1:3718,2\n1477#1:3749,7\n1490#1:3762,7\n1504#1:3772,7\n1522#1:3779,7\n3339#1:3844,2\n3381#1:3846,2\n*E\n"])
// $VF: Class flags could not be determined
internal class CollectionsKt___CollectionsKt : CollectionsKt___CollectionsJvmKt {
   @InlineOnly
   @JvmStatic
   public inline operator fun <T> List<T>.component1(): T {
      return (T)`$this$component1`.get(0);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> List<T>.component2(): T {
      return (T)`$this$component2`.get(1);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> List<T>.component3(): T {
      return (T)`$this$component3`.get(2);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> List<T>.component4(): T {
      return (T)`$this$component4`.get(3);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <T> List<T>.component5(): T {
      return (T)`$this$component5`.get(4);
   }

   @JvmStatic
   public operator fun <T> Iterable<T>.contains(element: T): Boolean {
      if (`$this$contains` is java.util.Collection) {
         return (`$this$contains` as java.util.Collection).contains(element);
      } else {
         return CollectionsKt.indexOf(`$this$contains`, element) >= 0;
      }
   }

   @JvmStatic
   public fun <T> Iterable<T>.elementAt(index: Int): T {
      return (T)(if (`$this$elementAt` is java.util.List)
         (`$this$elementAt` as java.util.List).get(index)
         else
         CollectionsKt.elementAtOrElse(`$this$elementAt`, index, (new Function1<Integer, T>(index) {
            {
               super(1);
               this.$index = `$index`;
            }

            public final T invoke(int it) {
               throw new IndexOutOfBoundsException("Collection doesn't contain element at index " + this.$index + '.');
            }
         }) as Function1));
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> List<T>.elementAt(index: Int): T {
      return (T)`$this$elementAt`.get(index);
   }

   @JvmStatic
   public fun <T> Iterable<T>.elementAtOrElse(index: Int, defaultValue: (Int) -> T): T {
      if (`$this$elementAtOrElse` !is java.util.List) {
         if (index < 0) {
            return (T)defaultValue.invoke(index);
         } else {
            var var6: java.util.Iterator = `$this$elementAtOrElse`.iterator();
            var count: Int = 0;

            while (iterator.hasNext()) {
               var element: Any = var6.next();
               if (index == count++) {
                  return (T)element;
               }
            }

            return (T)defaultValue.invoke(index);
         }
      } else {
         return (T)(if (index >= 0 && index <= CollectionsKt.getLastIndex(`$this$elementAtOrElse` as java.util.List))
            (`$this$elementAtOrElse` as java.util.List).get(index)
            else
            defaultValue.invoke(index));
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> List<T>.elementAtOrElse(index: Int, defaultValue: (Int) -> T): T {
      return (T)(if (index >= 0 && index <= CollectionsKt.getLastIndex(`$this$elementAtOrElse`))
         `$this$elementAtOrElse`.get(index)
         else
         defaultValue.invoke(index));
   }

   @JvmStatic
   public fun <T> Iterable<T>.elementAtOrNull(index: Int): T? {
      if (`$this$elementAtOrNull` is java.util.List) {
         return (T)CollectionsKt.getOrNull(`$this$elementAtOrNull` as java.util.List, index);
      } else if (index < 0) {
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
   public inline fun <T> List<T>.elementAtOrNull(index: Int): T? {
      return (T)CollectionsKt.getOrNull(`$this$elementAtOrNull`, index);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Iterable<T>.find(predicate: (T) -> Boolean): T? {
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
   public inline fun <T> Iterable<T>.findLast(predicate: (T) -> Boolean): T? {
      var `last$iv`: Any = null;

      for (Object element$iv : $this$findLast) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `last$iv` = `element$iv`;
         }
      }

      return (T)`last$iv`;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> List<T>.findLast(predicate: (T) -> Boolean): T? {
      var `iterator$iv`: java.util.ListIterator = `$this$findLast`.listIterator(`$this$findLast`.size());

      var var10000: Any;
      while (true) {
         if (`iterator$iv`.hasPrevious()) {
            var `element$iv`: Any = `iterator$iv`.previous();
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

   @JvmStatic
   public fun <T> Iterable<T>.first(): T {
      if (`$this$first` is java.util.List) {
         return (T)CollectionsKt.first(`$this$first` as java.util.List);
      } else {
         var iterator: java.util.Iterator = `$this$first`.iterator();
         if (!iterator.hasNext()) {
            throw new NoSuchElementException("Collection is empty.");
         } else {
            return (T)iterator.next();
         }
      }
   }

   @JvmStatic
   public fun <T> List<T>.first(): T {
      if (`$this$first`.isEmpty()) {
         throw new NoSuchElementException("List is empty.");
      } else {
         return (T)`$this$first`.get(0);
      }
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.first(predicate: (T) -> Boolean): T {
      for (Object element : $this$first) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return (T)element;
         }
      }

      throw new NoSuchElementException("Collection contains no element matching the predicate.");
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Any> Iterable<T>.firstNotNullOf(transform: (T) -> R?): R {
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
         throw new NoSuchElementException("No element of the collection was transformed to a non-null value.");
      } else {
         return (R)var10000;
      }
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R : Any> Iterable<T>.firstNotNullOfOrNull(transform: (T) -> R?): R? {
      for (Object element : $this$firstNotNullOfOrNull) {
         var result: Any = transform.invoke(element);
         if (result != null) {
            return (R)result;
         }
      }

      return null;
   }

   @JvmStatic
   public fun <T> Iterable<T>.firstOrNull(): T? {
      if (`$this$firstOrNull` is java.util.List) {
         return (T)(if ((`$this$firstOrNull` as java.util.List).isEmpty()) null else (`$this$firstOrNull` as java.util.List).get(0));
      } else {
         var iterator: java.util.Iterator = `$this$firstOrNull`.iterator();
         return (T)(if (!iterator.hasNext()) null else iterator.next());
      }
   }

   @JvmStatic
   public fun <T> List<T>.firstOrNull(): T? {
      return (T)(if (`$this$firstOrNull`.isEmpty()) null else `$this$firstOrNull`.get(0));
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.firstOrNull(predicate: (T) -> Boolean): T? {
      for (Object element : $this$firstOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            return (T)element;
         }
      }

      return null;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> List<T>.getOrElse(index: Int, defaultValue: (Int) -> T): T {
      return (T)(if (index >= 0 && index <= CollectionsKt.getLastIndex(`$this$getOrElse`)) `$this$getOrElse`.get(index) else defaultValue.invoke(index));
   }

   @JvmStatic
   public fun <T> List<T>.getOrNull(index: Int): T? {
      return (T)(if (index >= 0 && index <= CollectionsKt.getLastIndex(`$this$getOrNull`)) `$this$getOrNull`.get(index) else null);
   }

   @JvmStatic
   public fun <T> Iterable<T>.indexOf(element: T): Int {
      if (`$this$indexOf` is java.util.List) {
         return (`$this$indexOf` as java.util.List).indexOf(element);
      } else {
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
   }

   @JvmStatic
   public fun <T> List<T>.indexOf(element: T): Int {
      return `$this$indexOf`.indexOf(element);
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.indexOfFirst(predicate: (T) -> Boolean): Int {
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
   public inline fun <T> List<T>.indexOfFirst(predicate: (T) -> Boolean): Int {
      var index: Int = 0;

      for (Object item : $this$indexOfFirst) {
         if (predicate.invoke(item) as java.lang.Boolean) {
            return index;
         }

         index++;
      }

      return -1;
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.indexOfLast(predicate: (T) -> Boolean): Int {
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
   public inline fun <T> List<T>.indexOfLast(predicate: (T) -> Boolean): Int {
      var iterator: java.util.ListIterator = `$this$indexOfLast`.listIterator(`$this$indexOfLast`.size());

      while (iterator.hasPrevious()) {
         if (predicate.invoke(iterator.previous()) as java.lang.Boolean) {
            return iterator.nextIndex();
         }
      }

      return -1;
   }

   @JvmStatic
   public fun <T> Iterable<T>.last(): T {
      if (`$this$last` is java.util.List) {
         return (T)CollectionsKt.last(`$this$last` as java.util.List);
      } else {
         var iterator: java.util.Iterator = `$this$last`.iterator();
         if (!iterator.hasNext()) {
            throw new NoSuchElementException("Collection is empty.");
         } else {
            var last: Any = iterator.next();

            while (iterator.hasNext()) {
               last = iterator.next();
            }

            return (T)last;
         }
      }
   }

   @JvmStatic
   public fun <T> List<T>.last(): T {
      if (`$this$last`.isEmpty()) {
         throw new NoSuchElementException("List is empty.");
      } else {
         return (T)`$this$last`.get(CollectionsKt.getLastIndex(`$this$last`));
      }
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.last(predicate: (T) -> Boolean): T {
      var last: Any = null;
      var found: Boolean = false;

      for (Object element : $this$last) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            last = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Collection contains no element matching the predicate.");
      } else {
         return (T)last;
      }
   }

   @JvmStatic
   public inline fun <T> List<T>.last(predicate: (T) -> Boolean): T {
      var iterator: java.util.ListIterator = `$this$last`.listIterator(`$this$last`.size());

      while (iterator.hasPrevious()) {
         var element: Any = iterator.previous();
         if (predicate.invoke(element) as java.lang.Boolean) {
            return (T)element;
         }
      }

      throw new NoSuchElementException("List contains no element matching the predicate.");
   }

   @JvmStatic
   public fun <T> Iterable<T>.lastIndexOf(element: T): Int {
      if (`$this$lastIndexOf` is java.util.List) {
         return (`$this$lastIndexOf` as java.util.List).lastIndexOf(element);
      } else {
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
   }

   @JvmStatic
   public fun <T> List<T>.lastIndexOf(element: T): Int {
      return `$this$lastIndexOf`.lastIndexOf(element);
   }

   @JvmStatic
   public fun <T> Iterable<T>.lastOrNull(): T? {
      if (`$this$lastOrNull` is java.util.List) {
         return (T)(if ((`$this$lastOrNull` as java.util.List).isEmpty())
            null
            else
            (`$this$lastOrNull` as java.util.List).get((`$this$lastOrNull` as java.util.List).size() - 1));
      } else {
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
   }

   @JvmStatic
   public fun <T> List<T>.lastOrNull(): T? {
      return (T)(if (`$this$lastOrNull`.isEmpty()) null else `$this$lastOrNull`.get(`$this$lastOrNull`.size() - 1));
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.lastOrNull(predicate: (T) -> Boolean): T? {
      var last: Any = null;

      for (Object element : $this$lastOrNull) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            last = element;
         }
      }

      return (T)last;
   }

   @JvmStatic
   public inline fun <T> List<T>.lastOrNull(predicate: (T) -> Boolean): T? {
      var iterator: java.util.ListIterator = `$this$lastOrNull`.listIterator(`$this$lastOrNull`.size());

      while (iterator.hasPrevious()) {
         var element: Any = iterator.previous();
         if (predicate.invoke(element) as java.lang.Boolean) {
            return (T)element;
         }
      }

      return null;
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Collection<T>.random(): T {
      return (T)CollectionsKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <T> Collection<T>.random(random: Random): T {
      if (`$this$random`.isEmpty()) {
         throw new NoSuchElementException("Collection is empty.");
      } else {
         return (T)CollectionsKt.elementAt(`$this$random`, random.nextInt(`$this$random`.size()));
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <T> Collection<T>.randomOrNull(): T? {
      return (T)CollectionsKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun <T> Collection<T>.randomOrNull(random: Random): T? {
      return (T)(if (`$this$randomOrNull`.isEmpty()) null else CollectionsKt.elementAt(`$this$randomOrNull`, random.nextInt(`$this$randomOrNull`.size())));
   }

   @JvmStatic
   public fun <T> Iterable<T>.single(): T {
      if (`$this$single` is java.util.List) {
         return (T)CollectionsKt.single(`$this$single` as java.util.List);
      } else {
         var iterator: java.util.Iterator = `$this$single`.iterator();
         if (!iterator.hasNext()) {
            throw new NoSuchElementException("Collection is empty.");
         } else {
            var single: Any = iterator.next();
            if (iterator.hasNext()) {
               throw new IllegalArgumentException("Collection has more than one element.");
            } else {
               return (T)single;
            }
         }
      }
   }

   @JvmStatic
   public fun <T> List<T>.single(): T {
      switch ($this$single.size()) {
         case 0:
            throw new NoSuchElementException("List is empty.");
         case 1:
            return (T)`$this$single`.get(0);
         default:
            throw new IllegalArgumentException("List has more than one element.");
      }
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.single(predicate: (T) -> Boolean): T {
      var single: Any = null;
      var found: Boolean = false;

      for (Object element : $this$single) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            if (found) {
               throw new IllegalArgumentException("Collection contains more than one matching element.");
            }

            single = element;
            found = true;
         }
      }

      if (!found) {
         throw new NoSuchElementException("Collection contains no element matching the predicate.");
      } else {
         return (T)single;
      }
   }

   @JvmStatic
   public fun <T> Iterable<T>.singleOrNull(): T? {
      if (`$this$singleOrNull` is java.util.List) {
         return (T)(if ((`$this$singleOrNull` as java.util.List).size() == 1) (`$this$singleOrNull` as java.util.List).get(0) else null);
      } else {
         var iterator: java.util.Iterator = `$this$singleOrNull`.iterator();
         if (!iterator.hasNext()) {
            return null;
         } else {
            return (T)(if (iterator.hasNext()) null else iterator.next());
         }
      }
   }

   @JvmStatic
   public fun <T> List<T>.singleOrNull(): T? {
      return (T)(if (`$this$singleOrNull`.size() == 1) `$this$singleOrNull`.get(0) else null);
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.singleOrNull(predicate: (T) -> Boolean): T? {
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
   public fun <T> Iterable<T>.drop(n: Int): List<T> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.toList(`$this$drop`);
      } else {
         var var7: ArrayList;
         if (`$this$drop` is java.util.Collection) {
            var count: Int = (`$this$drop` as java.util.Collection).size() - n;
            if (count <= 0) {
               return CollectionsKt.emptyList();
            }

            if (count == 1) {
               return (java.util.List<T>)CollectionsKt.listOf(CollectionsKt.last(`$this$drop`));
            }

            var7 = new ArrayList(count);
            if (`$this$drop` is java.util.List) {
               if (`$this$drop` is RandomAccess) {
                  var index: Int = n;

                  for (int item = ((java.util.Collection)$this$drop).size(); index < item; index++) {
                     var7.add((`$this$drop` as java.util.List).get(index));
                  }
               } else {
                  var var11: java.util.Iterator = (`$this$drop` as java.util.List).listIterator(n);

                  while (var11.hasNext()) {
                     var7.add(var11.next());
                  }
               }

               return var7;
            }
         } else {
            var7 = new ArrayList();
         }

         var var8: Int = 0;

         for (Object item : $this$drop) {
            if (var8 >= n) {
               var7.add(var14);
            } else {
               var8++;
            }
         }

         return CollectionsKt.optimizeReadOnlyList(var7);
      }
   }

   @JvmStatic
   public fun <T> List<T>.dropLast(n: Int): List<T> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else {
         return CollectionsKt.take(`$this$dropLast`, RangesKt.coerceAtLeast(`$this$dropLast`.size() - n, 0));
      }
   }

   @JvmStatic
   public inline fun <T> List<T>.dropLastWhile(predicate: (T) -> Boolean): List<T> {
      if (!`$this$dropLastWhile`.isEmpty()) {
         var iterator: java.util.ListIterator = `$this$dropLastWhile`.listIterator(`$this$dropLastWhile`.size());

         while (iterator.hasPrevious()) {
            if (!predicate.invoke(iterator.previous()) as java.lang.Boolean) {
               return CollectionsKt.take(`$this$dropLastWhile`, iterator.nextIndex() + 1);
            }
         }
      }

      return CollectionsKt.emptyList();
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.dropWhile(predicate: (T) -> Boolean): List<T> {
      var yielding: Boolean = false;
      var list: ArrayList = new ArrayList();

      for (Object item : $this$dropWhile) {
         if (yielding) {
            list.add(item);
         } else if (!predicate.invoke(item) as java.lang.Boolean) {
            list.add(item);
            yielding = true;
         }
      }

      return list;
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.filter(predicate: (T) -> Boolean): List<T> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$filter) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<T>;
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.filterIndexed(predicate: (Int, T) -> Boolean): List<T> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (Object item$iv$iv : $this$filterIndexed) {
         var var11: Int = `index$iv$iv`++;
         if (var11 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         if (predicate.invoke(var11, `item$iv$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`item$iv$iv`);
         }
      }

      return `destination$iv` as java.util.List<T>;
   }

   @JvmStatic
   public inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterIndexedTo(destination: C, predicate: (Int, T) -> Boolean): C {
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
   public inline fun <T> Iterable<T>.filterNot(predicate: (T) -> Boolean): List<T> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$filterNot) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.add(`element$iv`);
         }
      }

      return `destination$iv` as java.util.List<T>;
   }

   @JvmStatic
   public fun <T : Any> Iterable<T?>.filterNotNull(): List<T> {
      return CollectionsKt.filterNotNullTo(`$this$filterNotNull`, new ArrayList()) as java.util.List<T>;
   }

   @JvmStatic
   public fun <C : MutableCollection<in T>, T : Any> Iterable<T?>.filterNotNullTo(destination: C): C {
      for (Object element : $this$filterNotNullTo) {
         if (element != null) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterNotTo(destination: C, predicate: (T) -> Boolean): C {
      for (Object element : $this$filterNotTo) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterTo(destination: C, predicate: (T) -> Boolean): C {
      for (Object element : $this$filterTo) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> List<T>.slice(indices: IntRange): List<T> {
      return if (indices.isEmpty())
         CollectionsKt.emptyList()
         else
         CollectionsKt.toList(`$this$slice`.subList(indices.getStart(), indices.getEndInclusive() + 1));
   }

   @JvmStatic
   public fun <T> List<T>.slice(indices: Iterable<Int>): List<T> {
      var size: Int = CollectionsKt.collectionSizeOrDefault(indices, 10);
      if (size == 0) {
         return CollectionsKt.emptyList();
      } else {
         var list: ArrayList = new ArrayList(size);
         var var4: java.util.Iterator = indices.iterator();

         while (var4.hasNext()) {
            list.add(`$this$slice`.get((var4.next() as java.lang.Number).intValue()));
         }

         return list;
      }
   }

   @JvmStatic
   public fun <T> Iterable<T>.take(n: Int): List<T> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         if (`$this$take` is java.util.Collection) {
            if (n >= (`$this$take` as java.util.Collection).size()) {
               return CollectionsKt.toList(`$this$take`);
            }

            if (n == 1) {
               return (java.util.List<T>)CollectionsKt.listOf(CollectionsKt.first(`$this$take`));
            }
         }

         var var6: Int = 0;
         var list: ArrayList = new ArrayList(n);

         for (Object item : $this$take) {
            list.add(item);
            if (++var6 == n) {
               break;
            }
         }

         return CollectionsKt.optimizeReadOnlyList(list);
      }
   }

   @JvmStatic
   public fun <T> List<T>.takeLast(n: Int): List<T> {
      if (n < 0) {
         throw new IllegalArgumentException(("Requested element count " + n + " is less than zero.").toString());
      } else if (n == 0) {
         return CollectionsKt.emptyList();
      } else {
         var var6: Int = `$this$takeLast`.size();
         if (n >= var6) {
            return CollectionsKt.toList(`$this$takeLast`);
         } else if (n == 1) {
            return (java.util.List<T>)CollectionsKt.listOf(CollectionsKt.last(`$this$takeLast`));
         } else {
            var list: ArrayList = new ArrayList(n);
            if (`$this$takeLast` is RandomAccess) {
               for (int index = size - n; index < size; index++) {
                  list.add(`$this$takeLast`.get(index));
               }
            } else {
               var var9: java.util.Iterator = `$this$takeLast`.listIterator(var6 - n);

               while (var9.hasNext()) {
                  list.add(var9.next());
               }
            }

            return list;
         }
      }
   }

   @JvmStatic
   public inline fun <T> List<T>.takeLastWhile(predicate: (T) -> Boolean): List<T> {
      if (`$this$takeLastWhile`.isEmpty()) {
         return CollectionsKt.emptyList();
      } else {
         var iterator: java.util.ListIterator = `$this$takeLastWhile`.listIterator(`$this$takeLastWhile`.size());

         while (iterator.hasPrevious()) {
            if (!predicate.invoke(iterator.previous()) as java.lang.Boolean) {
               iterator.next();
               var expectedSize: Int = `$this$takeLastWhile`.size() - iterator.nextIndex();
               if (expectedSize == 0) {
                  return CollectionsKt.emptyList();
               }

               var var5: ArrayList = new ArrayList(expectedSize);
               var `$this$takeLastWhile_u24lambda_u245`: ArrayList = var5;

               while (iterator.hasNext()) {
                  `$this$takeLastWhile_u24lambda_u245`.add(iterator.next());
               }

               return var5;
            }
         }

         return CollectionsKt.toList(`$this$takeLastWhile`);
      }
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.takeWhile(predicate: (T) -> Boolean): List<T> {
      var list: ArrayList = new ArrayList();

      for (Object item : $this$takeWhile) {
         if (!predicate.invoke(item) as java.lang.Boolean) {
            break;
         }

         list.add(item);
      }

      return list;
   }

   @JvmStatic
   public fun <T> Iterable<T>.reversed(): List<T> {
      if (`$this$reversed` is java.util.Collection && (`$this$reversed` as java.util.Collection).size() <= 1) {
         return CollectionsKt.toList(`$this$reversed`);
      } else {
         var list: java.util.List = CollectionsKt.toMutableList(`$this$reversed`);
         CollectionsKt.reverse(list);
         return list;
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <T> MutableList<T>.shuffle(random: Random) {
      for (int i = CollectionsKt.getLastIndex($this$shuffle); 0 < i; i--) {
         var j: Int = random.nextInt(i + 1);
         `$this$shuffle`.set(j, `$this$shuffle`.set(i, `$this$shuffle`.get(j)));
      }
   }

   @JvmStatic
   public inline fun <T, R : Comparable<R>> MutableList<T>.sortBy(crossinline selector: (T) -> R?) {
      if (`$this$sortBy`.size() > 1) {
         CollectionsKt.sortWith(`$this$sortBy`, new Comparator(selector) {
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
   }

   @JvmStatic
   public inline fun <T, R : Comparable<R>> MutableList<T>.sortByDescending(crossinline selector: (T) -> R?) {
      if (`$this$sortByDescending`.size() > 1) {
         CollectionsKt.sortWith(`$this$sortByDescending`, new Comparator(selector) {
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
   }

   @JvmStatic
   public fun <T : Comparable<T>> MutableList<T>.sortDescending() {
      CollectionsKt.sortWith(`$this$sortDescending`, ComparisonsKt.reverseOrder());
   }

   @JvmStatic
   public fun <T : Comparable<T>> Iterable<T>.sorted(): List<T> {
      if (`$this$sorted` is java.util.Collection) {
         if ((`$this$sorted` as java.util.Collection).size() <= 1) {
            return CollectionsKt.toList(`$this$sorted`);
         } else {
            var var5: Array<Any> = (`$this$sorted` as java.util.Collection).toArray(new java.lang.Comparable[0]);
            ArraysKt.sort(var5 as java.lang.Comparable[]);
            return (java.util.List<T>)ArraysKt.asList(var5);
         }
      } else {
         var `$this$toTypedArray$iv`: java.util.List = CollectionsKt.toMutableList(`$this$sorted`);
         CollectionsKt.sort(`$this$toTypedArray$iv`);
         return `$this$toTypedArray$iv`;
      }
   }

   @JvmStatic
   public inline fun <T, R : Comparable<R>> Iterable<T>.sortedBy(crossinline selector: (T) -> R?): List<T> {
      return CollectionsKt.sortedWith(`$this$sortedBy`, new Comparator(selector) {
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
   public inline fun <T, R : Comparable<R>> Iterable<T>.sortedByDescending(crossinline selector: (T) -> R?): List<T> {
      return CollectionsKt.sortedWith(`$this$sortedByDescending`, new Comparator(selector) {
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
   public fun <T : Comparable<T>> Iterable<T>.sortedDescending(): List<T> {
      return CollectionsKt.sortedWith(`$this$sortedDescending`, ComparisonsKt.reverseOrder());
   }

   @JvmStatic
   public fun <T> Iterable<T>.sortedWith(comparator: Comparator<in T>): List<T> {
      if (`$this$sortedWith` is java.util.Collection) {
         if ((`$this$sortedWith` as java.util.Collection).size() <= 1) {
            return CollectionsKt.toList(`$this$sortedWith`);
         } else {
            var var6: Array<Any> = (`$this$sortedWith` as java.util.Collection).toArray(new Object[0]);
            ArraysKt.sortWith(var6, comparator);
            return (java.util.List<T>)ArraysKt.asList(var6);
         }
      } else {
         var `$this$toTypedArray$iv`: java.util.List = CollectionsKt.toMutableList(`$this$sortedWith`);
         CollectionsKt.sortWith(`$this$toTypedArray$iv`, comparator);
         return `$this$toTypedArray$iv`;
      }
   }

   @JvmStatic
   public fun Collection<Boolean>.toBooleanArray(): BooleanArray {
      var result: BooleanArray = new boolean[`$this$toBooleanArray`.size()];
      var index: Int = 0;

      for (boolean element : $this$toBooleanArray) {
         result[index++] = element;
      }

      return result;
   }

   @JvmStatic
   public fun Collection<Byte>.toByteArray(): ByteArray {
      var result: ByteArray = new byte[`$this$toByteArray`.size()];
      var index: Int = 0;
      var var3: java.util.Iterator = `$this$toByteArray`.iterator();

      while (var3.hasNext()) {
         result[index++] = (var3.next() as java.lang.Number).byteValue();
      }

      return result;
   }

   @JvmStatic
   public fun Collection<Char>.toCharArray(): CharArray {
      var result: CharArray = new char[`$this$toCharArray`.size()];
      var index: Int = 0;

      for (char element : $this$toCharArray) {
         result[index++] = element;
      }

      return result;
   }

   @JvmStatic
   public fun Collection<Double>.toDoubleArray(): DoubleArray {
      var result: DoubleArray = new double[`$this$toDoubleArray`.size()];
      var index: Int = 0;
      var var3: java.util.Iterator = `$this$toDoubleArray`.iterator();

      while (var3.hasNext()) {
         result[index++] = (var3.next() as java.lang.Number).doubleValue();
      }

      return result;
   }

   @JvmStatic
   public fun Collection<Float>.toFloatArray(): FloatArray {
      var result: FloatArray = new float[`$this$toFloatArray`.size()];
      var index: Int = 0;
      var var3: java.util.Iterator = `$this$toFloatArray`.iterator();

      while (var3.hasNext()) {
         result[index++] = (var3.next() as java.lang.Number).floatValue();
      }

      return result;
   }

   @JvmStatic
   public fun Collection<Int>.toIntArray(): IntArray {
      var result: IntArray = new int[`$this$toIntArray`.size()];
      var index: Int = 0;
      var var3: java.util.Iterator = `$this$toIntArray`.iterator();

      while (var3.hasNext()) {
         result[index++] = (var3.next() as java.lang.Number).intValue();
      }

      return result;
   }

   @JvmStatic
   public fun Collection<Long>.toLongArray(): LongArray {
      var result: LongArray = new long[`$this$toLongArray`.size()];
      var index: Int = 0;
      var var3: java.util.Iterator = `$this$toLongArray`.iterator();

      while (var3.hasNext()) {
         result[index++] = (var3.next() as java.lang.Number).longValue();
      }

      return result;
   }

   @JvmStatic
   public fun Collection<Short>.toShortArray(): ShortArray {
      var result: ShortArray = new short[`$this$toShortArray`.size()];
      var index: Int = 0;
      var var3: java.util.Iterator = `$this$toShortArray`.iterator();

      while (var3.hasNext()) {
         result[index++] = (var3.next() as java.lang.Number).shortValue();
      }

      return result;
   }

   @JvmStatic
   public inline fun <T, K, V> Iterable<T>.associate(transform: (T) -> Pair<K, V>): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(
         RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associate`, 10)), 16)
      );

      for (Object element$iv : $this$associate) {
         var var10: Pair = transform.invoke(`element$iv`) as Pair;
         `destination$iv`.put(var10.getFirst(), var10.getSecond());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K> Iterable<T>.associateBy(keySelector: (T) -> K): Map<K, T> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(
         RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associateBy`, 10)), 16)
      );

      for (Object element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), `element$iv`);
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K, V> Iterable<T>.associateBy(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(
         RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associateBy`, 10)), 16)
      );

      for (Object element$iv : $this$associateBy) {
         `destination$iv`.put(keySelector.invoke(`element$iv`), valueTransform.invoke(`element$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <T, K, M : MutableMap<in K, in T>> Iterable<T>.associateByTo(destination: M, keySelector: (T) -> K): M {
      for (Object element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), element);
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateByTo(destination: M, keySelector: (T) -> K, valueTransform: (T) -> V): M {
      for (Object element : $this$associateByTo) {
         destination.put(keySelector.invoke(element), valueTransform.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateTo(destination: M, transform: (T) -> Pair<K, V>): M {
      for (Object element : $this$associateTo) {
         var var7: Pair = transform.invoke(element) as Pair;
         destination.put(var7.getFirst(), var7.getSecond());
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public inline fun <K, V> Iterable<K>.associateWith(valueSelector: (K) -> V): Map<K, V> {
      var result: LinkedHashMap = new LinkedHashMap(
         RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associateWith`, 10)), 16)
      );

      for (Object element$iv : $this$associateWith) {
         result.put(`element$iv`, valueSelector.invoke(`element$iv`));
      }

      return result;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> Iterable<K>.associateWithTo(destination: M, valueSelector: (K) -> V): M {
      for (Object element : $this$associateWithTo) {
         destination.put(element, valueSelector.invoke(element));
      }

      return (M)destination;
   }

   @JvmStatic
   public fun <T, C : MutableCollection<in T>> Iterable<T>.toCollection(destination: C): C {
      for (Object item : $this$toCollection) {
         destination.add(item);
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> Iterable<T>.toHashSet(): HashSet<T> {
      return CollectionsKt.toCollection(`$this$toHashSet`, new HashSet(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$toHashSet`, 12)))) as HashSet<T>;
   }

   @JvmStatic
   public fun <T> Iterable<T>.toList(): List<T> {
      if (`$this$toList` is java.util.Collection) {
         var var10000: java.util.List;
         switch (((java.util.Collection)$this$toList).size()) {
            case 0:
               var10000 = CollectionsKt.emptyList();
               break;
            case 1:
               var10000 = CollectionsKt.listOf(
                  if (`$this$toList` is java.util.List) (`$this$toList` as java.util.List).get(0) else `$this$toList`.iterator().next()
               );
               break;
            default:
               var10000 = CollectionsKt.toMutableList(`$this$toList` as java.util.Collection);
         }

         return var10000;
      } else {
         return CollectionsKt.optimizeReadOnlyList(CollectionsKt.toMutableList(`$this$toList`));
      }
   }

   @JvmStatic
   public fun <T> Iterable<T>.toMutableList(): MutableList<T> {
      return if (`$this$toMutableList` is java.util.Collection)
         CollectionsKt.toMutableList(`$this$toMutableList` as java.util.Collection)
         else
         CollectionsKt.toCollection(`$this$toMutableList`, new ArrayList()) as java.util.List;
   }

   @JvmStatic
   public fun <T> Collection<T>.toMutableList(): MutableList<T> {
      return new ArrayList(`$this$toMutableList`);
   }

   @JvmStatic
   public fun <T> Iterable<T>.toSet(): Set<T> {
      if (`$this$toSet` is java.util.Collection) {
         var var10000: java.util.Set;
         switch (((java.util.Collection)$this$toSet).size()) {
            case 0:
               var10000 = SetsKt.emptySet();
               break;
            case 1:
               var10000 = SetsKt.setOf(if (`$this$toSet` is java.util.List) (`$this$toSet` as java.util.List).get(0) else `$this$toSet`.iterator().next());
               break;
            default:
               var10000 = CollectionsKt.toCollection(`$this$toSet`, new LinkedHashSet(MapsKt.mapCapacity((`$this$toSet` as java.util.Collection).size())));
         }

         return var10000;
      } else {
         return SetsKt.optimizeReadOnlySet(CollectionsKt.toCollection(`$this$toSet`, new LinkedHashSet()));
      }
   }

   @JvmStatic
   public inline fun <T, R> Iterable<T>.flatMap(transform: (T) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapSequence")
   @JvmStatic
   public inline fun <T, R> Iterable<T>.flatMap(transform: (T) -> Sequence<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv : $this$flatMap) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as Sequence);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterable")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Iterable<T>.flatMapIndexed(transform: (Int, T) -> Iterable<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (Object var6 : $this$flatMapIndexed) {
         var var7: Int = var4++;
         if (var7 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         CollectionsKt.addAll(var3, transform.invoke(var7, var6) as java.lang.Iterable);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedSequence")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R> Iterable<T>.flatMapIndexed(transform: (Int, T) -> Sequence<R>): List<R> {
      var var3: java.util.Collection = new ArrayList();
      var var4: Int = 0;

      for (Object var6 : $this$flatMapIndexed) {
         var var7: Int = var4++;
         if (var7 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         CollectionsKt.addAll(var3, transform.invoke(var7, var6) as Sequence);
      }

      return var3 as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapIndexedIterableTo")
   @InlineOnly
   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapIndexedTo(destination: C, transform: (Int, T) -> Iterable<R>): C {
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
   public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapIndexedTo(destination: C, transform: (Int, T) -> Sequence<R>): C {
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

   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapTo(destination: C, transform: (T) -> Iterable<R>): C {
      for (Object element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapSequenceTo")
   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapTo(destination: C, transform: (T) -> Sequence<R>): C {
      for (Object element : $this$flatMapTo) {
         CollectionsKt.addAll(destination, transform.invoke(element) as Sequence);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, K> Iterable<T>.groupBy(keySelector: (T) -> K): Map<K, List<T>> {
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
   public inline fun <T, K, V> Iterable<T>.groupBy(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, List<V>> {
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
   public inline fun <T, K, M : MutableMap<in K, MutableList<T>>> Iterable<T>.groupByTo(destination: M, keySelector: (T) -> K): M {
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
   public inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Iterable<T>.groupByTo(destination: M, keySelector: (T) -> K, valueTransform: (T) -> V): M {
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
   public inline fun <T, K> Iterable<T>.groupingBy(crossinline keySelector: (T) -> K): Grouping<T, K> {
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
   public inline fun <T, R> Iterable<T>.map(transform: (T) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map`, 10));

      for (Object item$iv : $this$map) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <T, R> Iterable<T>.mapIndexed(transform: (Int, T) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$mapIndexed`, 10));
      var `index$iv`: Int = 0;

      for (Object item$iv : $this$mapIndexed) {
         var var9: Int = `index$iv`++;
         if (var9 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         `destination$iv`.add(transform.invoke(var9, `item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <T, R : Any> Iterable<T>.mapIndexedNotNull(transform: (Int, T) -> R?): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();
      var `index$iv$iv`: Int = 0;

      for (Object item$iv$iv : $this$mapIndexedNotNull) {
         var var11: Int = `index$iv$iv`++;
         if (var11 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         var var18: Any = transform.invoke(var11, `item$iv$iv`);
         if (var18 != null) {
            `destination$iv`.add(var18);
         }
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <T, R : Any, C : MutableCollection<in R>> Iterable<T>.mapIndexedNotNullTo(destination: C, transform: (Int, T) -> R?): C {
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
   public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapIndexedTo(destination: C, transform: (Int, T) -> R): C {
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
   public inline fun <T, R : Any> Iterable<T>.mapNotNull(transform: (T) -> R?): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$mapNotNull) {
         var var10000: Any = transform.invoke(`element$iv$iv`);
         if (var10000 != null) {
            `destination$iv`.add(var10000);
         }
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <T, R : Any, C : MutableCollection<in R>> Iterable<T>.mapNotNullTo(destination: C, transform: (T) -> R?): C {
      for (Object element$iv : $this$mapNotNullTo) {
         var var10000: Any = transform.invoke(`element$iv`);
         if (var10000 != null) {
            destination.add(var10000);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapTo(destination: C, transform: (T) -> R): C {
      for (Object item : $this$mapTo) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> Iterable<T>.withIndex(): Iterable<IndexedValue<T>> {
      return new IndexingIterable((new Function0<java.util.Iterator<? extends T>>(`$this$withIndex`) {
         {
            super(0);
            this.$this_withIndex = `$receiver`;
         }

         @NotNull
         public final java.util.Iterator<T> invoke() {
            return this.$this_withIndex.iterator();
         }
      }) as Function0);
   }

   @JvmStatic
   public fun <T> Iterable<T>.distinct(): List<T> {
      return CollectionsKt.toList(CollectionsKt.toMutableSet(`$this$distinct`));
   }

   @JvmStatic
   public inline fun <T, K> Iterable<T>.distinctBy(selector: (T) -> K): List<T> {
      var set: HashSet = new HashSet();
      var list: ArrayList = new ArrayList();

      for (Object e : $this$distinctBy) {
         if (set.add(selector.invoke(e))) {
            list.add(e);
         }
      }

      return list;
   }

   @JvmStatic
   public infix fun <T> Iterable<T>.intersect(other: Iterable<T>): Set<T> {
      var set: java.util.Set = CollectionsKt.toMutableSet(`$this$intersect`);
      CollectionsKt.retainAll(set, other);
      return set;
   }

   @JvmStatic
   public infix fun <T> Iterable<T>.subtract(other: Iterable<T>): Set<T> {
      var set: java.util.Set = CollectionsKt.toMutableSet(`$this$subtract`);
      CollectionsKt.removeAll(set, other);
      return set;
   }

   @JvmStatic
   public fun <T> Iterable<T>.toMutableSet(): MutableSet<T> {
      return (java.util.Set<T>)(if (`$this$toMutableSet` is java.util.Collection)
         new LinkedHashSet(`$this$toMutableSet` as java.util.Collection)
         else
         CollectionsKt.toCollection(`$this$toMutableSet`, new LinkedHashSet()) as java.util.Set);
   }

   @JvmStatic
   public infix fun <T> Iterable<T>.union(other: Iterable<T>): Set<T> {
      var set: java.util.Set = CollectionsKt.toMutableSet(`$this$union`);
      CollectionsKt.addAll(set, other);
      return set;
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.all(predicate: (T) -> Boolean): Boolean {
      if (`$this$all` is java.util.Collection && (`$this$all` as java.util.Collection).isEmpty()) {
         return true;
      } else {
         for (Object element : $this$all) {
            if (!predicate.invoke(element) as java.lang.Boolean) {
               return false;
            }
         }

         return true;
      }
   }

   @JvmStatic
   public fun <T> Iterable<T>.any(): Boolean {
      if (`$this$any` is java.util.Collection) {
         return !(`$this$any` as java.util.Collection).isEmpty();
      } else {
         return `$this$any`.iterator().hasNext();
      }
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.any(predicate: (T) -> Boolean): Boolean {
      if (`$this$any` is java.util.Collection && (`$this$any` as java.util.Collection).isEmpty()) {
         return false;
      } else {
         for (Object element : $this$any) {
            if (predicate.invoke(element) as java.lang.Boolean) {
               return true;
            }
         }

         return false;
      }
   }

   @JvmStatic
   public fun <T> Iterable<T>.count(): Int {
      if (`$this$count` is java.util.Collection) {
         return (`$this$count` as java.util.Collection).size();
      } else {
         var count: Int = 0;

         for (Object element : $this$count) {
            if (++count < 0) {
               CollectionsKt.throwCountOverflow();
            }
         }

         return count;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Collection<T>.count(): Int {
      return `$this$count`.size();
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.count(predicate: (T) -> Boolean): Int {
      if (`$this$count` is java.util.Collection && (`$this$count` as java.util.Collection).isEmpty()) {
         return 0;
      } else {
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
   }

   @JvmStatic
   public inline fun <T, R> Iterable<T>.fold(initial: R, operation: (R, T) -> R): R {
      var accumulator: Any = initial;

      for (Object element : $this$fold) {
         accumulator = operation.invoke(accumulator, element);
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <T, R> Iterable<T>.foldIndexed(initial: R, operation: (Int, R, T) -> R): R {
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
   public inline fun <T, R> List<T>.foldRight(initial: R, operation: (T, R) -> R): R {
      var accumulator: Any = initial;
      if (!`$this$foldRight`.isEmpty()) {
         var iterator: java.util.ListIterator = `$this$foldRight`.listIterator(`$this$foldRight`.size());

         while (iterator.hasPrevious()) {
            accumulator = operation.invoke(iterator.previous(), accumulator);
         }
      }

      return (R)accumulator;
   }

   @JvmStatic
   public inline fun <T, R> List<T>.foldRightIndexed(initial: R, operation: (Int, T, R) -> R): R {
      var accumulator: Any = initial;
      if (!`$this$foldRightIndexed`.isEmpty()) {
         var iterator: java.util.ListIterator = `$this$foldRightIndexed`.listIterator(`$this$foldRightIndexed`.size());

         while (iterator.hasPrevious()) {
            accumulator = operation.invoke(iterator.previousIndex(), iterator.previous(), accumulator);
         }
      }

      return (R)accumulator;
   }

   @HidesMembers
   @JvmStatic
   public inline fun <T> Iterable<T>.forEach(action: (T) -> Unit) {
      for (Object element : $this$forEach) {
         action.invoke(element);
      }
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.forEachIndexed(action: (Int, T) -> Unit) {
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
   public fun Iterable<Double>.max(): Double {
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
   public fun Iterable<Float>.max(): Float {
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
   public fun <T : Comparable<T>> Iterable<T>.max(): T {
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
   public inline fun <T, R : Comparable<R>> Iterable<T>.maxBy(selector: (T) -> R): T {
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
   public inline fun <T, R : Comparable<R>> Iterable<T>.maxByOrNull(selector: (T) -> R): T? {
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
   public inline fun <T> Iterable<T>.maxOf(selector: (T) -> Double): Double {
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
   public inline fun <T> Iterable<T>.maxOf(selector: (T) -> Float): Float {
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
   public inline fun <T, R : Comparable<R>> Iterable<T>.maxOf(selector: (T) -> R): R {
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
   public inline fun <T> Iterable<T>.maxOfOrNull(selector: (T) -> Double): Double? {
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
   public inline fun <T> Iterable<T>.maxOfOrNull(selector: (T) -> Float): Float? {
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
   public inline fun <T, R : Comparable<R>> Iterable<T>.maxOfOrNull(selector: (T) -> R): R? {
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
   public inline fun <T, R> Iterable<T>.maxOfWith(comparator: Comparator<in R>, selector: (T) -> R): R {
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
   public inline fun <T, R> Iterable<T>.maxOfWithOrNull(comparator: Comparator<in R>, selector: (T) -> R): R? {
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
   public fun Iterable<Double>.maxOrNull(): Double? {
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
   public fun Iterable<Float>.maxOrNull(): Float? {
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
   public fun <T : Comparable<T>> Iterable<T>.maxOrNull(): T? {
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
   public fun <T> Iterable<T>.maxWith(comparator: Comparator<in T>): T {
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
   public fun <T> Iterable<T>.maxWithOrNull(comparator: Comparator<in T>): T? {
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
   public fun Iterable<Double>.min(): Double {
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
   public fun Iterable<Float>.min(): Float {
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
   public fun <T : Comparable<T>> Iterable<T>.min(): T {
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
   public inline fun <T, R : Comparable<R>> Iterable<T>.minBy(selector: (T) -> R): T {
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
   public inline fun <T, R : Comparable<R>> Iterable<T>.minByOrNull(selector: (T) -> R): T? {
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
   public inline fun <T> Iterable<T>.minOf(selector: (T) -> Double): Double {
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
   public inline fun <T> Iterable<T>.minOf(selector: (T) -> Float): Float {
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
   public inline fun <T, R : Comparable<R>> Iterable<T>.minOf(selector: (T) -> R): R {
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
   public inline fun <T> Iterable<T>.minOfOrNull(selector: (T) -> Double): Double? {
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
   public inline fun <T> Iterable<T>.minOfOrNull(selector: (T) -> Float): Float? {
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
   public inline fun <T, R : Comparable<R>> Iterable<T>.minOfOrNull(selector: (T) -> R): R? {
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
   public inline fun <T, R> Iterable<T>.minOfWith(comparator: Comparator<in R>, selector: (T) -> R): R {
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
   public inline fun <T, R> Iterable<T>.minOfWithOrNull(comparator: Comparator<in R>, selector: (T) -> R): R? {
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
   public fun Iterable<Double>.minOrNull(): Double? {
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
   public fun Iterable<Float>.minOrNull(): Float? {
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
   public fun <T : Comparable<T>> Iterable<T>.minOrNull(): T? {
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
   public fun <T> Iterable<T>.minWith(comparator: Comparator<in T>): T {
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
   public fun <T> Iterable<T>.minWithOrNull(comparator: Comparator<in T>): T? {
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
   public fun <T> Iterable<T>.none(): Boolean {
      if (`$this$none` is java.util.Collection) {
         return (`$this$none` as java.util.Collection).isEmpty();
      } else {
         return !`$this$none`.iterator().hasNext();
      }
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.none(predicate: (T) -> Boolean): Boolean {
      if (`$this$none` is java.util.Collection && (`$this$none` as java.util.Collection).isEmpty()) {
         return true;
      } else {
         for (Object element : $this$none) {
            if (predicate.invoke(element) as java.lang.Boolean) {
               return false;
            }
         }

         return true;
      }
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T, C : Iterable<T>> C.onEach(action: (T) -> Unit): C {
      for (Object element : $this$onEach) {
         action.invoke(element);
      }

      return (C)`$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <T, C : Iterable<T>> C.onEachIndexed(action: (Int, T) -> Unit): C {
      var `index$iv`: Int = 0;

      for (Object item$iv : $this$onEachIndexed) {
         var var11: Int = `index$iv`++;
         if (var11 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         action.invoke(var11, `item$iv`);
      }

      return (C)`$this$onEachIndexed`;
   }

   @JvmStatic
   public inline fun <S, T : S> Iterable<T>.reduce(operation: (S, T) -> S): S {
      var iterator: java.util.Iterator = `$this$reduce`.iterator();
      if (!iterator.hasNext()) {
         throw new UnsupportedOperationException("Empty collection can't be reduced.");
      } else {
         var accumulator: Any = iterator.next();

         while (iterator.hasNext()) {
            accumulator = operation.invoke(accumulator, iterator.next());
         }

         return (S)accumulator;
      }
   }

   @JvmStatic
   public inline fun <S, T : S> Iterable<T>.reduceIndexed(operation: (Int, S, T) -> S): S {
      var iterator: java.util.Iterator = `$this$reduceIndexed`.iterator();
      if (!iterator.hasNext()) {
         throw new UnsupportedOperationException("Empty collection can't be reduced.");
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
   public inline fun <S, T : S> Iterable<T>.reduceIndexedOrNull(operation: (Int, S, T) -> S): S? {
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
   public inline fun <S, T : S> Iterable<T>.reduceOrNull(operation: (S, T) -> S): S? {
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

   @JvmStatic
   public inline fun <S, T : S> List<T>.reduceRight(operation: (T, S) -> S): S {
      var iterator: java.util.ListIterator = `$this$reduceRight`.listIterator(`$this$reduceRight`.size());
      if (!iterator.hasPrevious()) {
         throw new UnsupportedOperationException("Empty list can't be reduced.");
      } else {
         var accumulator: Any = iterator.previous();

         while (iterator.hasPrevious()) {
            accumulator = operation.invoke(iterator.previous(), accumulator);
         }

         return (S)accumulator;
      }
   }

   @JvmStatic
   public inline fun <S, T : S> List<T>.reduceRightIndexed(operation: (Int, T, S) -> S): S {
      var iterator: java.util.ListIterator = `$this$reduceRightIndexed`.listIterator(`$this$reduceRightIndexed`.size());
      if (!iterator.hasPrevious()) {
         throw new UnsupportedOperationException("Empty list can't be reduced.");
      } else {
         var accumulator: Any = iterator.previous();

         while (iterator.hasPrevious()) {
            accumulator = operation.invoke(iterator.previousIndex(), iterator.previous(), accumulator);
         }

         return (S)accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <S, T : S> List<T>.reduceRightIndexedOrNull(operation: (Int, T, S) -> S): S? {
      var iterator: java.util.ListIterator = `$this$reduceRightIndexedOrNull`.listIterator(`$this$reduceRightIndexedOrNull`.size());
      if (!iterator.hasPrevious()) {
         return null;
      } else {
         var accumulator: Any = iterator.previous();

         while (iterator.hasPrevious()) {
            accumulator = operation.invoke(iterator.previousIndex(), iterator.previous(), accumulator);
         }

         return (S)accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <S, T : S> List<T>.reduceRightOrNull(operation: (T, S) -> S): S? {
      var iterator: java.util.ListIterator = `$this$reduceRightOrNull`.listIterator(`$this$reduceRightOrNull`.size());
      if (!iterator.hasPrevious()) {
         return null;
      } else {
         var accumulator: Any = iterator.previous();

         while (iterator.hasPrevious()) {
            accumulator = operation.invoke(iterator.previous(), accumulator);
         }

         return (S)accumulator;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <T, R> Iterable<T>.runningFold(initial: R, operation: (R, T) -> R): List<R> {
      var estimatedSize: Int = CollectionsKt.collectionSizeOrDefault(`$this$runningFold`, 9);
      if (estimatedSize == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var accumulator: ArrayList = new ArrayList(estimatedSize + 1);
         accumulator.add(initial);
         var result: ArrayList = accumulator;
         var var9: Any = initial;

         for (Object element : $this$runningFold) {
            var9 = operation.invoke(var9, var10);
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <T, R> Iterable<T>.runningFoldIndexed(initial: R, operation: (Int, R, T) -> R): List<R> {
      var estimatedSize: Int = CollectionsKt.collectionSizeOrDefault(`$this$runningFoldIndexed`, 9);
      if (estimatedSize == 0) {
         return (java.util.List<R>)CollectionsKt.listOf(initial);
      } else {
         var index: ArrayList = new ArrayList(estimatedSize + 1);
         index.add(initial);
         var result: ArrayList = index;
         var var10: Int = 0;
         var accumulator: Any = initial;

         for (Object element : $this$runningFoldIndexed) {
            accumulator = operation.invoke(var10++, accumulator, element);
            result.add(accumulator);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <S, T : S> Iterable<T>.runningReduce(operation: (S, T) -> S): List<S> {
      var iterator: java.util.Iterator = `$this$runningReduce`.iterator();
      if (!iterator.hasNext()) {
         return CollectionsKt.emptyList();
      } else {
         var var9: Any = iterator.next();
         var var6: ArrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$runningReduce`, 10));
         var6.add(var9);
         var result: ArrayList = var6;

         while (iterator.hasNext()) {
            var9 = operation.invoke(var9, iterator.next());
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <S, T : S> Iterable<T>.runningReduceIndexed(operation: (Int, S, T) -> S): List<S> {
      var iterator: java.util.Iterator = `$this$runningReduceIndexed`.iterator();
      if (!iterator.hasNext()) {
         return CollectionsKt.emptyList();
      } else {
         var var9: Any = iterator.next();
         var index: ArrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$runningReduceIndexed`, 10));
         index.add(var9);
         var result: ArrayList = index;
         var var10: Int = 1;

         while (iterator.hasNext()) {
            var9 = operation.invoke(var10++, var9, iterator.next());
            result.add(var9);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <T, R> Iterable<T>.scan(initial: R, operation: (R, T) -> R): List<R> {
      var `estimatedSize$iv`: Int = CollectionsKt.collectionSizeOrDefault(`$this$scan`, 9);
      var var10000: java.util.List;
      if (`estimatedSize$iv` == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var `accumulator$iv`: ArrayList = new ArrayList(`estimatedSize$iv` + 1);
         `accumulator$iv`.add(initial);
         var `result$iv`: ArrayList = `accumulator$iv`;
         var var11: Any = initial;

         for (Object element$iv : $this$scan) {
            var11 = operation.invoke(var11, var12);
            `result$iv`.add(var11);
         }

         var10000 = `result$iv`;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public inline fun <T, R> Iterable<T>.scanIndexed(initial: R, operation: (Int, R, T) -> R): List<R> {
      var `estimatedSize$iv`: Int = CollectionsKt.collectionSizeOrDefault(`$this$scanIndexed`, 9);
      var var10000: java.util.List;
      if (`estimatedSize$iv` == 0) {
         var10000 = CollectionsKt.listOf(initial);
      } else {
         var `index$iv`: ArrayList = new ArrayList(`estimatedSize$iv` + 1);
         `index$iv`.add(initial);
         var `result$iv`: ArrayList = `index$iv`;
         var var12: Int = 0;
         var `accumulator$iv`: Any = initial;

         for (Object element$iv : $this$scanIndexed) {
            `accumulator$iv` = operation.invoke(var12++, `accumulator$iv`, `element$iv`);
            `result$iv`.add(`accumulator$iv`);
         }

         var10000 = `result$iv`;
      }

      return var10000;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun <T> Iterable<T>.sumBy(selector: (T) -> Int): Int {
      var sum: Int = 0;

      for (Object element : $this$sumBy) {
         sum += (selector.invoke(element) as java.lang.Number).intValue();
      }

      return sum;
   }

   @Deprecated(message = "Use sumOf instead.", replaceWith = @ReplaceWith(expression = "this.sumOf(selector)", imports = []))
   @DeprecatedSinceKotlin(warningSince = "1.5")
   @JvmStatic
   public inline fun <T> Iterable<T>.sumByDouble(selector: (T) -> Double): Double {
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
   public inline fun <T> Iterable<T>.sumOf(selector: (T) -> Double): Double {
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
   public inline fun <T> Iterable<T>.sumOf(selector: (T) -> Int): Int {
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
   public inline fun <T> Iterable<T>.sumOf(selector: (T) -> Long): Long {
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
   public inline fun <T> Iterable<T>.sumOf(selector: (T) -> UInt): UInt {
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
   public inline fun <T> Iterable<T>.sumOf(selector: (T) -> ULong): ULong {
      var sum: Long = ULong.constructor-impl(0L);

      for (Object element : $this$sumOf) {
         sum = ULong.constructor-impl(sum + (selector.invoke(element) as ULong).unbox-impl());
      }

      return sum;
   }

   @JvmStatic
   public fun <T : Any> Iterable<T?>.requireNoNulls(): Iterable<T> {
      for (Object element : $this$requireNoNulls) {
         if (element == null) {
            throw new IllegalArgumentException("null element found in " + `$this$requireNoNulls` + '.');
         }
      }

      return `$this$requireNoNulls`;
   }

   @JvmStatic
   public fun <T : Any> List<T?>.requireNoNulls(): List<T> {
      for (Object element : $this$requireNoNulls) {
         if (element == null) {
            throw new IllegalArgumentException("null element found in " + `$this$requireNoNulls` + '.');
         }
      }

      return `$this$requireNoNulls`;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T> Iterable<T>.chunked(size: Int): List<List<T>> {
      return CollectionsKt.windowed(`$this$chunked`, size, size, true);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T, R> Iterable<T>.chunked(size: Int, transform: (List<T>) -> R): List<R> {
      return CollectionsKt.windowed(`$this$chunked`, size, size, true, transform);
   }

   @JvmStatic
   public operator fun <T> Iterable<T>.minus(element: T): List<T> {
      var result: ArrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$minus`, 10));
      var removed: Boolean = false;

      for (Object element$iv : $this$minus) {
         var var10000: Boolean;
         if (!removed && `element$iv` == element) {
            removed = true;
            var10000 = false;
         } else {
            var10000 = true;
         }

         if (var10000) {
            result.add(`element$iv`);
         }
      }

      return result;
   }

   @JvmStatic
   public operator fun <T> Iterable<T>.minus(elements: Array<out T>): List<T> {
      if (elements.length == 0) {
         return CollectionsKt.toList(`$this$minus`);
      } else {
         var `destination$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$minus) {
            if (!ArraysKt.contains(elements, `element$iv$iv`)) {
               `destination$iv$iv`.add(`element$iv$iv`);
            }
         }

         return `destination$iv$iv` as java.util.List<T>;
      }
   }

   @JvmStatic
   public operator fun <T> Iterable<T>.minus(elements: Iterable<T>): List<T> {
      var other: java.util.Collection = CollectionsKt.convertToListIfNotCollection(elements);
      if (other.isEmpty()) {
         return CollectionsKt.toList(`$this$minus`);
      } else {
         var `destination$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$minus) {
            if (!other.contains(`element$iv$iv`)) {
               `destination$iv$iv`.add(`element$iv$iv`);
            }
         }

         return `destination$iv$iv` as java.util.List<T>;
      }
   }

   @JvmStatic
   public operator fun <T> Iterable<T>.minus(elements: Sequence<T>): List<T> {
      var other: java.util.List = SequencesKt.toList(elements);
      if (other.isEmpty()) {
         return CollectionsKt.toList(`$this$minus`);
      } else {
         var `destination$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$minus) {
            if (!other.contains(`element$iv$iv`)) {
               `destination$iv$iv`.add(`element$iv$iv`);
            }
         }

         return `destination$iv$iv` as java.util.List<T>;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Iterable<T>.minusElement(element: T): List<T> {
      return (java.util.List<T>)CollectionsKt.minus(`$this$minusElement`, element);
   }

   @JvmStatic
   public inline fun <T> Iterable<T>.partition(predicate: (T) -> Boolean): Pair<List<T>, List<T>> {
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
   public operator fun <T> Iterable<T>.plus(element: T): List<T> {
      if (`$this$plus` is java.util.Collection) {
         return (java.util.List<T>)CollectionsKt.plus(`$this$plus` as java.util.Collection<? extends Object>, element);
      } else {
         var result: ArrayList = new ArrayList();
         CollectionsKt.addAll(result, `$this$plus`);
         result.add(element);
         return result;
      }
   }

   @JvmStatic
   public operator fun <T> Collection<T>.plus(element: T): List<T> {
      var result: ArrayList = new ArrayList(`$this$plus`.size() + 1);
      result.addAll(`$this$plus`);
      result.add(element);
      return result;
   }

   @JvmStatic
   public operator fun <T> Iterable<T>.plus(elements: Array<out T>): List<T> {
      if (`$this$plus` is java.util.Collection) {
         return (java.util.List<T>)CollectionsKt.plus(`$this$plus` as java.util.Collection<? extends Object>, elements);
      } else {
         var result: ArrayList = new ArrayList();
         CollectionsKt.addAll(result, `$this$plus`);
         CollectionsKt.addAll(result, elements);
         return result;
      }
   }

   @JvmStatic
   public operator fun <T> Collection<T>.plus(elements: Array<out T>): List<T> {
      var result: ArrayList = new ArrayList(`$this$plus`.size() + elements.length);
      result.addAll(`$this$plus`);
      CollectionsKt.addAll(result, elements);
      return result;
   }

   @JvmStatic
   public operator fun <T> Iterable<T>.plus(elements: Iterable<T>): List<T> {
      if (`$this$plus` is java.util.Collection) {
         return CollectionsKt.plus(`$this$plus` as java.util.Collection, elements);
      } else {
         var result: ArrayList = new ArrayList();
         CollectionsKt.addAll(result, `$this$plus`);
         CollectionsKt.addAll(result, elements);
         return result;
      }
   }

   @JvmStatic
   public operator fun <T> Collection<T>.plus(elements: Iterable<T>): List<T> {
      if (elements is java.util.Collection) {
         var var3: ArrayList = new ArrayList(`$this$plus`.size() + (elements as java.util.Collection).size());
         var3.addAll(`$this$plus`);
         var3.addAll(elements as java.util.Collection);
         return var3;
      } else {
         var result: ArrayList = new ArrayList(`$this$plus`);
         CollectionsKt.addAll(result, elements);
         return result;
      }
   }

   @JvmStatic
   public operator fun <T> Iterable<T>.plus(elements: Sequence<T>): List<T> {
      var result: ArrayList = new ArrayList();
      CollectionsKt.addAll(result, `$this$plus`);
      CollectionsKt.addAll(result, elements);
      return result;
   }

   @JvmStatic
   public operator fun <T> Collection<T>.plus(elements: Sequence<T>): List<T> {
      var result: ArrayList = new ArrayList(`$this$plus`.size() + 10);
      result.addAll(`$this$plus`);
      CollectionsKt.addAll(result, elements);
      return result;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Iterable<T>.plusElement(element: T): List<T> {
      return (java.util.List<T>)CollectionsKt.plus(`$this$plusElement`, element);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Collection<T>.plusElement(element: T): List<T> {
      return (java.util.List<T>)CollectionsKt.plus(`$this$plusElement`, element);
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T> Iterable<T>.windowed(size: Int, step: Int, partialWindows: Boolean): List<List<T>> {
      SlidingWindowKt.checkWindowSizeStep(size, step);
      if (`$this$windowed` is RandomAccess && `$this$windowed` is java.util.List) {
         var var16: Int = (`$this$windowed` as java.util.List).size();
         var var18: ArrayList = new ArrayList(var16 / step + (if (var16 % step == 0) 0 else 1));

         for (int index = 0; 0 <= index && index < thisSize; index += step) {
            var var20: Int = RangesKt.coerceAtMost(size, var16 - var19);
            if (var20 < size && !partialWindows) {
               break;
            }

            var var21: ArrayList = new ArrayList(var20);

            for (int var22 = 0; var22 < windowSize; var22++) {
               var21.add((`$this$windowed` as java.util.List).get(var22 + var19));
            }

            var18.add(var21);
         }

         return var18;
      } else {
         var result: ArrayList = new ArrayList();
         var index: java.util.Iterator = SlidingWindowKt.windowedIterator(`$this$windowed`.iterator(), size, step, partialWindows, false);

         while (index.hasNext()) {
            result.add(index.next() as java.util.List);
         }

         return result;
      }
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T, R> Iterable<T>.windowed(size: Int, step: Int, partialWindows: Boolean, transform: (List<T>) -> R): List<R> {
      SlidingWindowKt.checkWindowSizeStep(size, step);
      if (`$this$windowed` is RandomAccess && `$this$windowed` is java.util.List) {
         var var12: Int = (`$this$windowed` as java.util.List).size();
         var var14: ArrayList = new ArrayList(var12 / step + (if (var12 % step == 0) 0 else 1));
         var var15: MovingSubList = new MovingSubList(`$this$windowed` as java.util.List);

         for (int index = 0; 0 <= index && index < thisSize; index += step) {
            var var17: Int = RangesKt.coerceAtMost(size, var12 - var16);
            if (!partialWindows && var17 < size) {
               break;
            }

            var15.move(var16, var16 + var17);
            var14.add(transform.invoke(var15));
         }

         return var14;
      } else {
         var result: ArrayList = new ArrayList();
         var window: java.util.Iterator = SlidingWindowKt.windowedIterator(`$this$windowed`.iterator(), size, step, partialWindows, true);

         while (window.hasNext()) {
            result.add(transform.invoke(window.next() as java.util.List));
         }

         return result;
      }
   }

   @JvmStatic
   public infix fun <T, R> Iterable<T>.zip(other: Array<out R>): List<Pair<T, R>> {
      var `arraySize$iv`: Int = other.length;
      var `list$iv`: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(`$this$zip`, 10), other.length));
      var `i$iv`: Int = 0;

      for (Object element$iv : $this$zip) {
         if (`i$iv` >= `arraySize$iv`) {
            break;
         }

         `list$iv`.add(TuplesKt.to(`element$iv`, other[`i$iv`++]));
      }

      return `list$iv`;
   }

   @JvmStatic
   public inline fun <T, R, V> Iterable<T>.zip(other: Array<out R>, transform: (T, R) -> V): List<V> {
      var arraySize: Int = other.length;
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(`$this$zip`, 10), other.length));
      var i: Int = 0;

      for (Object element : $this$zip) {
         if (i >= arraySize) {
            break;
         }

         list.add(transform.invoke(element, other[i++]));
      }

      return list;
   }

   @JvmStatic
   public infix fun <T, R> Iterable<T>.zip(other: Iterable<R>): List<Pair<T, R>> {
      var `first$iv`: java.util.Iterator = `$this$zip`.iterator();
      var `second$iv`: java.util.Iterator = other.iterator();
      var `list$iv`: ArrayList = new ArrayList(
         Math.min(CollectionsKt.collectionSizeOrDefault(`$this$zip`, 10), CollectionsKt.collectionSizeOrDefault(other, 10))
      );

      while (first$iv.hasNext() && second$iv.hasNext()) {
         `list$iv`.add(TuplesKt.to(`first$iv`.next(), `second$iv`.next()));
      }

      return `list$iv`;
   }

   @JvmStatic
   public inline fun <T, R, V> Iterable<T>.zip(other: Iterable<R>, transform: (T, R) -> V): List<V> {
      var first: java.util.Iterator = `$this$zip`.iterator();
      var second: java.util.Iterator = other.iterator();
      var list: ArrayList = new ArrayList(Math.min(CollectionsKt.collectionSizeOrDefault(`$this$zip`, 10), CollectionsKt.collectionSizeOrDefault(other, 10)));

      while (first.hasNext() && second.hasNext()) {
         list.add(transform.invoke(first.next(), second.next()));
      }

      return list;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T> Iterable<T>.zipWithNext(): List<Pair<T, T>> {
      var `iterator$iv`: java.util.Iterator = `$this$zipWithNext`.iterator();
      var var10000: java.util.List;
      if (!`iterator$iv`.hasNext()) {
         var10000 = CollectionsKt.emptyList();
      } else {
         var `result$iv`: java.util.List = new ArrayList();
         var `current$iv`: Any = `iterator$iv`.next();

         while (iterator$iv.hasNext()) {
            var `next$iv`: Any = `iterator$iv`.next();
            `result$iv`.add(TuplesKt.to(`current$iv`, `next$iv`));
            `current$iv` = `next$iv`;
         }

         var10000 = `result$iv`;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public inline fun <T, R> Iterable<T>.zipWithNext(transform: (T, T) -> R): List<R> {
      var iterator: java.util.Iterator = `$this$zipWithNext`.iterator();
      if (!iterator.hasNext()) {
         return CollectionsKt.emptyList();
      } else {
         var result: java.util.List = new ArrayList();
         var current: Any = iterator.next();

         while (iterator.hasNext()) {
            var next: Any = iterator.next();
            result.add(transform.invoke(current, next));
            current = next;
         }

         return result;
      }
   }

   @JvmStatic
   public fun <T, A : Appendable> Iterable<T>.joinTo(
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
   public fun <T> Iterable<T>.joinToString(
      separator: CharSequence,
      prefix: CharSequence,
      postfix: CharSequence,
      limit: Int,
      truncated: CharSequence,
      transform: ((T) -> CharSequence)?
   ): String {
      var var10000: java.lang.String = CollectionsKt.joinTo(`$this$joinToString`, new StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
         .toString();
      return var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Iterable<T>.asIterable(): Iterable<T> {
      return `$this$asIterable`;
   }

   @JvmStatic
   public fun <T> Iterable<T>.asSequence(): Sequence<T> {
      return new CollectionsKt___CollectionsKt$asSequence$$inlined$Sequence$1(`$this$asSequence`);
   }

   @JvmName(name = "averageOfByte")
   @JvmStatic
   public fun Iterable<Byte>.average(): Double {
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
   public fun Iterable<Short>.average(): Double {
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
   public fun Iterable<Int>.average(): Double {
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
   public fun Iterable<Long>.average(): Double {
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
   public fun Iterable<Float>.average(): Double {
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
   public fun Iterable<Double>.average(): Double {
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
   public fun Iterable<Byte>.sum(): Int {
      var sum: Int = 0;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum += (var2.next() as java.lang.Number).byteValue();
      }

      return sum;
   }

   @JvmName(name = "sumOfShort")
   @JvmStatic
   public fun Iterable<Short>.sum(): Int {
      var sum: Int = 0;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum += (var2.next() as java.lang.Number).shortValue();
      }

      return sum;
   }

   @JvmName(name = "sumOfInt")
   @JvmStatic
   public fun Iterable<Int>.sum(): Int {
      var sum: Int = 0;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum += (var2.next() as java.lang.Number).intValue();
      }

      return sum;
   }

   @JvmName(name = "sumOfLong")
   @JvmStatic
   public fun Iterable<Long>.sum(): Long {
      var sum: Long = 0L;
      var var3: java.util.Iterator = `$this$sum`.iterator();

      while (var3.hasNext()) {
         sum += (var3.next() as java.lang.Number).longValue();
      }

      return sum;
   }

   @JvmName(name = "sumOfFloat")
   @JvmStatic
   public fun Iterable<Float>.sum(): Float {
      var sum: Float = 0.0F;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum += (var2.next() as java.lang.Number).floatValue();
      }

      return sum;
   }

   @JvmName(name = "sumOfDouble")
   @JvmStatic
   public fun Iterable<Double>.sum(): Double {
      var sum: Double = 0.0;
      var var3: java.util.Iterator = `$this$sum`.iterator();

      while (var3.hasNext()) {
         sum += (var3.next() as java.lang.Number).doubleValue();
      }

      return sum;
   }

   open fun CollectionsKt___CollectionsKt() {
   }
}
