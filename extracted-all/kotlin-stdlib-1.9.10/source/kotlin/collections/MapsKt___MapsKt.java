package kotlin.collections

import java.util.ArrayList
import java.util.Comparator
import java.util.NoSuchElementException
import kotlin.collections.Map.Entry
import kotlin.internal.HidesMembers
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\n_Maps.kt\nKotlin\n*S Kotlin\n*F\n+ 1 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,594:1\n96#1,5:595\n111#1,5:600\n152#1,3:605\n143#1:608\n215#1:609\n216#1:611\n144#1:612\n215#1:613\n216#1:615\n1#2:610\n1#2:614\n1940#3,14:616\n1963#3,14:630\n2310#3,14:644\n2333#3,14:658\n1864#3,3:672\n*S KotlinDebug\n*F\n+ 1 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n76#1:595,5\n89#1:600,5\n125#1:605,3\n135#1:608\n135#1:609\n135#1:611\n135#1:612\n143#1:613\n143#1:615\n135#1:610\n230#1:616,14\n241#1:630,14\n390#1:644,14\n401#1:658,14\n574#1:672,3\n*E\n"])
// $VF: Class flags could not be determined
internal class MapsKt___MapsKt : MapsKt___MapsJvmKt {
   @SinceKotlin(version = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R : Any> Map<out K, V>.firstNotNullOf(transform: (Entry<K, V>) -> R?): R {
      var var2: java.util.Iterator = `$this$firstNotNullOf`.entrySet().iterator();

      var var10000: Any;
      do {
         if (!var2.hasNext()) {
            var10000 = null;
            break;
         }

         var10000 = transform.invoke(var2.next() as java.util.Map.Entry);
      } while (var10000 == null);

      if (var10000 == null) {
         throw new NoSuchElementException("No element of the map was transformed to a non-null value.");
      } else {
         return (R)var10000;
      }
   }

   @SinceKotlin(version = "1.5")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R : Any> Map<out K, V>.firstNotNullOfOrNull(transform: (Entry<K, V>) -> R?): R? {
      for (java.util.Map.Entry element : $this$firstNotNullOfOrNull.entrySet()) {
         var result: Any = transform.invoke(element);
         if (result != null) {
            return (R)result;
         }
      }

      return null;
   }

   @JvmStatic
   public fun <K, V> Map<out K, V>.toList(): List<Pair<K, V>> {
      if (`$this$toList`.size() == 0) {
         return CollectionsKt.emptyList();
      } else {
         var iterator: java.util.Iterator = `$this$toList`.entrySet().iterator();
         if (!iterator.hasNext()) {
            return CollectionsKt.emptyList();
         } else {
            var first: java.util.Map.Entry = iterator.next() as java.util.Map.Entry;
            if (!iterator.hasNext()) {
               return CollectionsKt.listOf(new Pair<>(first.getKey(), first.getValue()));
            } else {
               var result: ArrayList = new ArrayList(`$this$toList`.size());
               result.add(new Pair<>(first.getKey(), first.getValue()));

               do {
                  var var4: java.util.Map.Entry = iterator.next() as java.util.Map.Entry;
                  result.add(new Pair<>(var4.getKey(), var4.getValue()));
               } while (iterator.hasNext());

               return result;
            }
         }
      }
   }

   @JvmStatic
   public inline fun <K, V, R> Map<out K, V>.flatMap(transform: (Entry<K, V>) -> Iterable<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (java.util.Map.Entry element$iv : $this$flatMap.entrySet()) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as java.lang.Iterable);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapSequence")
   @JvmStatic
   public inline fun <K, V, R> Map<out K, V>.flatMap(transform: (Entry<K, V>) -> Sequence<R>): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (java.util.Map.Entry element$iv : $this$flatMap.entrySet()) {
         CollectionsKt.addAll(`destination$iv`, transform.invoke(`element$iv`) as Sequence);
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.flatMapTo(destination: C, transform: (Entry<K, V>) -> Iterable<R>): C {
      for (java.util.Map.Entry element : $this$flatMapTo.entrySet()) {
         CollectionsKt.addAll(destination, transform.invoke(element) as java.lang.Iterable);
      }

      return (C)destination;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "flatMapSequenceTo")
   @JvmStatic
   public inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.flatMapTo(destination: C, transform: (Entry<K, V>) -> Sequence<R>): C {
      for (java.util.Map.Entry element : $this$flatMapTo.entrySet()) {
         CollectionsKt.addAll(destination, transform.invoke(element) as Sequence);
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <K, V, R> Map<out K, V>.map(transform: (Entry<K, V>) -> R): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList(`$this$map`.size());

      for (java.util.Map.Entry item$iv : $this$map.entrySet()) {
         `destination$iv`.add(transform.invoke(`item$iv`));
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <K, V, R : Any> Map<out K, V>.mapNotNull(transform: (Entry<K, V>) -> R?): List<R> {
      var `destination$iv`: java.util.Collection = new ArrayList();

      for (java.util.Map.Entry element$iv$iv : $this$mapNotNull.entrySet()) {
         var var10000: Any = transform.invoke(`element$iv$iv`);
         if (var10000 != null) {
            `destination$iv`.add(var10000);
         }
      }

      return `destination$iv` as java.util.List<R>;
   }

   @JvmStatic
   public inline fun <K, V, R : Any, C : MutableCollection<in R>> Map<out K, V>.mapNotNullTo(destination: C, transform: (Entry<K, V>) -> R?): C {
      for (java.util.Map.Entry element$iv : $this$mapNotNullTo.entrySet()) {
         var var10000: Any = transform.invoke(`element$iv`);
         if (var10000 != null) {
            destination.add(var10000);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.mapTo(destination: C, transform: (Entry<K, V>) -> R): C {
      for (java.util.Map.Entry item : $this$mapTo.entrySet()) {
         destination.add(transform.invoke(item));
      }

      return (C)destination;
   }

   @JvmStatic
   public inline fun <K, V> Map<out K, V>.all(predicate: (Entry<K, V>) -> Boolean): Boolean {
      if (`$this$all`.isEmpty()) {
         return true;
      } else {
         for (java.util.Map.Entry element : $this$all.entrySet()) {
            if (!predicate.invoke(element) as java.lang.Boolean) {
               return false;
            }
         }

         return true;
      }
   }

   @JvmStatic
   public fun <K, V> Map<out K, V>.any(): Boolean {
      return !`$this$any`.isEmpty();
   }

   @JvmStatic
   public inline fun <K, V> Map<out K, V>.any(predicate: (Entry<K, V>) -> Boolean): Boolean {
      if (`$this$any`.isEmpty()) {
         return false;
      } else {
         for (java.util.Map.Entry element : $this$any.entrySet()) {
            if (predicate.invoke(element) as java.lang.Boolean) {
               return true;
            }
         }

         return false;
      }
   }

   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.count(): Int {
      return `$this$count`.size();
   }

   @JvmStatic
   public inline fun <K, V> Map<out K, V>.count(predicate: (Entry<K, V>) -> Boolean): Int {
      if (`$this$count`.isEmpty()) {
         return 0;
      } else {
         var count: Int = 0;

         for (java.util.Map.Entry element : $this$count.entrySet()) {
            if (predicate.invoke(element) as java.lang.Boolean) {
               count++;
            }
         }

         return count;
      }
   }

   @HidesMembers
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.forEach(action: (Entry<K, V>) -> Unit) {
      for (java.util.Map.Entry element : $this$forEach.entrySet()) {
         action.invoke(element);
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxByOrThrow")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R : Comparable<R>> Map<out K, V>.maxBy(selector: (Entry<K, V>) -> R): Entry<K, V> {
      var `iterator$iv`: java.util.Iterator = `$this$maxBy`.entrySet().iterator();
      if (!`iterator$iv`.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var `maxElem$iv`: Any = `iterator$iv`.next();
         var var10000: Any;
         if (!`iterator$iv`.hasNext()) {
            var10000 = `maxElem$iv`;
         } else {
            var `maxValue$iv`: java.lang.Comparable = selector.invoke(`maxElem$iv`) as java.lang.Comparable;

            do {
               var `e$iv`: Any = `iterator$iv`.next();
               var `v$iv`: java.lang.Comparable = selector.invoke(`e$iv`) as java.lang.Comparable;
               if (`maxValue$iv`.compareTo(`v$iv`) < 0) {
                  `maxElem$iv` = `e$iv`;
                  `maxValue$iv` = `v$iv`;
               }
            } while (iterator$iv.hasNext());

            var10000 = `maxElem$iv`;
         }

         return var10000 as java.util.Map.Entry<K, V>;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R : Comparable<R>> Map<out K, V>.maxByOrNull(selector: (Entry<K, V>) -> R): Entry<K, V>? {
      var `iterator$iv`: java.util.Iterator = `$this$maxByOrNull`.entrySet().iterator();
      var var10000: Any;
      if (!`iterator$iv`.hasNext()) {
         var10000 = null;
      } else {
         var `maxElem$iv`: Any = `iterator$iv`.next();
         if (!`iterator$iv`.hasNext()) {
            var10000 = `maxElem$iv`;
         } else {
            var `maxValue$iv`: java.lang.Comparable = selector.invoke(`maxElem$iv`) as java.lang.Comparable;

            do {
               var `e$iv`: Any = `iterator$iv`.next();
               var `v$iv`: java.lang.Comparable = selector.invoke(`e$iv`) as java.lang.Comparable;
               if (`maxValue$iv`.compareTo(`v$iv`) < 0) {
                  `maxElem$iv` = `e$iv`;
                  `maxValue$iv` = `v$iv`;
               }
            } while (iterator$iv.hasNext());

            var10000 = `maxElem$iv`;
         }
      }

      return var10000 as java.util.Map.Entry<K, V>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.maxOf(selector: (Entry<K, V>) -> Double): Double {
      var var2: java.util.Iterator = `$this$maxOf`.entrySet().iterator();
      if (!var2.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var var3: Double = (selector.invoke(var2.next()) as java.lang.Number).doubleValue();

         while (var2.hasNext()) {
            var3 = Math.max(var3, (selector.invoke(var2.next()) as java.lang.Number).doubleValue());
         }

         return var3;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.maxOf(selector: (Entry<K, V>) -> Float): Float {
      var var2: java.util.Iterator = `$this$maxOf`.entrySet().iterator();
      if (!var2.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var var3: Float = (selector.invoke(var2.next()) as java.lang.Number).floatValue();

         while (var2.hasNext()) {
            var3 = Math.max(var3, (selector.invoke(var2.next()) as java.lang.Number).floatValue());
         }

         return var3;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R : Comparable<R>> Map<out K, V>.maxOf(selector: (Entry<K, V>) -> R): R {
      var var2: java.util.Iterator = `$this$maxOf`.entrySet().iterator();
      if (!var2.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var var3: java.lang.Comparable = selector.invoke(var2.next()) as java.lang.Comparable;

         while (var2.hasNext()) {
            var var4: java.lang.Comparable = selector.invoke(var2.next()) as java.lang.Comparable;
            if (var3.compareTo(var4) < 0) {
               var3 = var4;
            }
         }

         return (R)var3;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.maxOfOrNull(selector: (Entry<K, V>) -> Double): Double? {
      var var2: java.util.Iterator = `$this$maxOfOrNull`.entrySet().iterator();
      var var10000: java.lang.Double;
      if (!var2.hasNext()) {
         var10000 = null;
      } else {
         var var3: Double = (selector.invoke(var2.next()) as java.lang.Number).doubleValue();

         while (var2.hasNext()) {
            var3 = Math.max(var3, (selector.invoke(var2.next()) as java.lang.Number).doubleValue());
         }

         var10000 = var3;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.maxOfOrNull(selector: (Entry<K, V>) -> Float): Float? {
      var var2: java.util.Iterator = `$this$maxOfOrNull`.entrySet().iterator();
      var var10000: java.lang.Float;
      if (!var2.hasNext()) {
         var10000 = null;
      } else {
         var var3: Float = (selector.invoke(var2.next()) as java.lang.Number).floatValue();

         while (var2.hasNext()) {
            var3 = Math.max(var3, (selector.invoke(var2.next()) as java.lang.Number).floatValue());
         }

         var10000 = var3;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R : Comparable<R>> Map<out K, V>.maxOfOrNull(selector: (Entry<K, V>) -> R): R? {
      var var2: java.util.Iterator = `$this$maxOfOrNull`.entrySet().iterator();
      var var10000: java.lang.Comparable;
      if (!var2.hasNext()) {
         var10000 = null;
      } else {
         var var3: java.lang.Comparable = selector.invoke(var2.next()) as java.lang.Comparable;

         while (var2.hasNext()) {
            var var4: java.lang.Comparable = selector.invoke(var2.next()) as java.lang.Comparable;
            if (var3.compareTo(var4) < 0) {
               var3 = var4;
            }
         }

         var10000 = var3;
      }

      return (R)var10000;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R> Map<out K, V>.maxOfWith(comparator: Comparator<in R>, selector: (Entry<K, V>) -> R): R {
      var var3: java.util.Iterator = `$this$maxOfWith`.entrySet().iterator();
      if (!var3.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var var4: Any = selector.invoke(var3.next());

         while (var3.hasNext()) {
            var var5: Any = selector.invoke(var3.next());
            if (comparator.compare(var4, var5) < 0) {
               var4 = var5;
            }
         }

         return (R)var4;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R> Map<out K, V>.maxOfWithOrNull(comparator: Comparator<in R>, selector: (Entry<K, V>) -> R): R? {
      var var3: java.util.Iterator = `$this$maxOfWithOrNull`.entrySet().iterator();
      var var10000: Any;
      if (!var3.hasNext()) {
         var10000 = null;
      } else {
         var var4: Any = selector.invoke(var3.next());

         while (var3.hasNext()) {
            var var5: Any = selector.invoke(var3.next());
            if (comparator.compare(var4, var5) < 0) {
               var4 = var5;
            }
         }

         var10000 = var4;
      }

      return (R)var10000;
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "maxWithOrThrow")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.maxWith(comparator: Comparator<in Entry<K, V>>): Entry<K, V> {
      return CollectionsKt.maxWithOrThrow(`$this$maxWith`.entrySet(), comparator) as java.util.Map.Entry<K, V>;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.maxWithOrNull(comparator: Comparator<in Entry<K, V>>): Entry<K, V>? {
      return CollectionsKt.maxWithOrNull(`$this$maxWithOrNull`.entrySet(), comparator) as java.util.Map.Entry<K, V>;
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minByOrThrow")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R : Comparable<R>> Map<out K, V>.minBy(selector: (Entry<K, V>) -> R): Entry<K, V> {
      var `iterator$iv`: java.util.Iterator = `$this$minBy`.entrySet().iterator();
      if (!`iterator$iv`.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var `minElem$iv`: Any = `iterator$iv`.next();
         var var10000: Any;
         if (!`iterator$iv`.hasNext()) {
            var10000 = `minElem$iv`;
         } else {
            var `minValue$iv`: java.lang.Comparable = selector.invoke(`minElem$iv`) as java.lang.Comparable;

            do {
               var `e$iv`: Any = `iterator$iv`.next();
               var `v$iv`: java.lang.Comparable = selector.invoke(`e$iv`) as java.lang.Comparable;
               if (`minValue$iv`.compareTo(`v$iv`) > 0) {
                  `minElem$iv` = `e$iv`;
                  `minValue$iv` = `v$iv`;
               }
            } while (iterator$iv.hasNext());

            var10000 = `minElem$iv`;
         }

         return var10000 as java.util.Map.Entry<K, V>;
      }
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R : Comparable<R>> Map<out K, V>.minByOrNull(selector: (Entry<K, V>) -> R): Entry<K, V>? {
      var `iterator$iv`: java.util.Iterator = `$this$minByOrNull`.entrySet().iterator();
      var var10000: Any;
      if (!`iterator$iv`.hasNext()) {
         var10000 = null;
      } else {
         var `minElem$iv`: Any = `iterator$iv`.next();
         if (!`iterator$iv`.hasNext()) {
            var10000 = `minElem$iv`;
         } else {
            var `minValue$iv`: java.lang.Comparable = selector.invoke(`minElem$iv`) as java.lang.Comparable;

            do {
               var `e$iv`: Any = `iterator$iv`.next();
               var `v$iv`: java.lang.Comparable = selector.invoke(`e$iv`) as java.lang.Comparable;
               if (`minValue$iv`.compareTo(`v$iv`) > 0) {
                  `minElem$iv` = `e$iv`;
                  `minValue$iv` = `v$iv`;
               }
            } while (iterator$iv.hasNext());

            var10000 = `minElem$iv`;
         }
      }

      return var10000 as java.util.Map.Entry<K, V>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.minOf(selector: (Entry<K, V>) -> Double): Double {
      var var2: java.util.Iterator = `$this$minOf`.entrySet().iterator();
      if (!var2.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var var3: Double = (selector.invoke(var2.next()) as java.lang.Number).doubleValue();

         while (var2.hasNext()) {
            var3 = Math.min(var3, (selector.invoke(var2.next()) as java.lang.Number).doubleValue());
         }

         return var3;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.minOf(selector: (Entry<K, V>) -> Float): Float {
      var var2: java.util.Iterator = `$this$minOf`.entrySet().iterator();
      if (!var2.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var var3: Float = (selector.invoke(var2.next()) as java.lang.Number).floatValue();

         while (var2.hasNext()) {
            var3 = Math.min(var3, (selector.invoke(var2.next()) as java.lang.Number).floatValue());
         }

         return var3;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R : Comparable<R>> Map<out K, V>.minOf(selector: (Entry<K, V>) -> R): R {
      var var2: java.util.Iterator = `$this$minOf`.entrySet().iterator();
      if (!var2.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var var3: java.lang.Comparable = selector.invoke(var2.next()) as java.lang.Comparable;

         while (var2.hasNext()) {
            var var4: java.lang.Comparable = selector.invoke(var2.next()) as java.lang.Comparable;
            if (var3.compareTo(var4) > 0) {
               var3 = var4;
            }
         }

         return (R)var3;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.minOfOrNull(selector: (Entry<K, V>) -> Double): Double? {
      var var2: java.util.Iterator = `$this$minOfOrNull`.entrySet().iterator();
      var var10000: java.lang.Double;
      if (!var2.hasNext()) {
         var10000 = null;
      } else {
         var var3: Double = (selector.invoke(var2.next()) as java.lang.Number).doubleValue();

         while (var2.hasNext()) {
            var3 = Math.min(var3, (selector.invoke(var2.next()) as java.lang.Number).doubleValue());
         }

         var10000 = var3;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.minOfOrNull(selector: (Entry<K, V>) -> Float): Float? {
      var var2: java.util.Iterator = `$this$minOfOrNull`.entrySet().iterator();
      var var10000: java.lang.Float;
      if (!var2.hasNext()) {
         var10000 = null;
      } else {
         var var3: Float = (selector.invoke(var2.next()) as java.lang.Number).floatValue();

         while (var2.hasNext()) {
            var3 = Math.min(var3, (selector.invoke(var2.next()) as java.lang.Number).floatValue());
         }

         var10000 = var3;
      }

      return var10000;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R : Comparable<R>> Map<out K, V>.minOfOrNull(selector: (Entry<K, V>) -> R): R? {
      var var2: java.util.Iterator = `$this$minOfOrNull`.entrySet().iterator();
      var var10000: java.lang.Comparable;
      if (!var2.hasNext()) {
         var10000 = null;
      } else {
         var var3: java.lang.Comparable = selector.invoke(var2.next()) as java.lang.Comparable;

         while (var2.hasNext()) {
            var var4: java.lang.Comparable = selector.invoke(var2.next()) as java.lang.Comparable;
            if (var3.compareTo(var4) > 0) {
               var3 = var4;
            }
         }

         var10000 = var3;
      }

      return (R)var10000;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R> Map<out K, V>.minOfWith(comparator: Comparator<in R>, selector: (Entry<K, V>) -> R): R {
      var var3: java.util.Iterator = `$this$minOfWith`.entrySet().iterator();
      if (!var3.hasNext()) {
         throw new NoSuchElementException();
      } else {
         var var4: Any = selector.invoke(var3.next());

         while (var3.hasNext()) {
            var var5: Any = selector.invoke(var3.next());
            if (comparator.compare(var4, var5) > 0) {
               var4 = var5;
            }
         }

         return (R)var4;
      }
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @InlineOnly
   @JvmStatic
   public inline fun <K, V, R> Map<out K, V>.minOfWithOrNull(comparator: Comparator<in R>, selector: (Entry<K, V>) -> R): R? {
      var var3: java.util.Iterator = `$this$minOfWithOrNull`.entrySet().iterator();
      var var10000: Any;
      if (!var3.hasNext()) {
         var10000 = null;
      } else {
         var var4: Any = selector.invoke(var3.next());

         while (var3.hasNext()) {
            var var5: Any = selector.invoke(var3.next());
            if (comparator.compare(var4, var5) > 0) {
               var4 = var5;
            }
         }

         var10000 = var4;
      }

      return (R)var10000;
   }

   @SinceKotlin(version = "1.7")
   @JvmName(name = "minWithOrThrow")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.minWith(comparator: Comparator<in Entry<K, V>>): Entry<K, V> {
      return CollectionsKt.minWithOrThrow(`$this$minWith`.entrySet(), comparator) as java.util.Map.Entry<K, V>;
   }

   @SinceKotlin(version = "1.4")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.minWithOrNull(comparator: Comparator<in Entry<K, V>>): Entry<K, V>? {
      return CollectionsKt.minWithOrNull(`$this$minWithOrNull`.entrySet(), comparator) as java.util.Map.Entry<K, V>;
   }

   @JvmStatic
   public fun <K, V> Map<out K, V>.none(): Boolean {
      return `$this$none`.isEmpty();
   }

   @JvmStatic
   public inline fun <K, V> Map<out K, V>.none(predicate: (Entry<K, V>) -> Boolean): Boolean {
      if (`$this$none`.isEmpty()) {
         return true;
      } else {
         for (java.util.Map.Entry element : $this$none.entrySet()) {
            if (predicate.invoke(element) as java.lang.Boolean) {
               return false;
            }
         }

         return true;
      }
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <K, V, M : Map<out K, V>> M.onEach(action: (Entry<K, V>) -> Unit): M {
      for (java.util.Map.Entry element : $this$onEach.entrySet()) {
         action.invoke(element);
      }

      return (M)`$this$onEach`;
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public inline fun <K, V, M : Map<out K, V>> M.onEachIndexed(action: (Int, Entry<K, V>) -> Unit): M {
      var `$this$forEachIndexed$iv`: java.lang.Iterable = `$this$onEachIndexed`.entrySet();
      var `index$iv`: Int = 0;

      for (Object item$iv : $this$forEachIndexed$iv) {
         var var11: Int = `index$iv`++;
         if (var11 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         action.invoke(var11, `item$iv`);
      }

      return (M)`$this$onEachIndexed`;
   }

   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.asIterable(): Iterable<Entry<K, V>> {
      return `$this$asIterable`.entrySet();
   }

   @JvmStatic
   public fun <K, V> Map<out K, V>.asSequence(): Sequence<Entry<K, V>> {
      return CollectionsKt.asSequence(`$this$asSequence`.entrySet());
   }

   open fun MapsKt___MapsKt() {
   }
}
