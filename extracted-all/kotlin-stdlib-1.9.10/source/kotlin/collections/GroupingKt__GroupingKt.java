package kotlin.collections

import java.util.LinkedHashMap
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nGrouping.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Grouping.kt\nkotlin/collections/GroupingKt__GroupingKt\n*L\n1#1,291:1\n80#1,6:292\n53#1:298\n80#1,6:299\n80#1,6:305\n53#1:311\n80#1,6:312\n80#1,6:318\n53#1:324\n80#1,6:325\n80#1,6:331\n189#1:337\n80#1,6:338\n*S KotlinDebug\n*F\n+ 1 Grouping.kt\nkotlin/collections/GroupingKt__GroupingKt\n*L\n53#1:292,6\n112#1:298\n112#1:299,6\n143#1:305,6\n164#1:311\n164#1:312,6\n189#1:318,6\n211#1:324\n211#1:325,6\n239#1:331,6\n257#1:337\n257#1:338,6\n*E\n"])
// $VF: Class flags could not be determined
internal class GroupingKt__GroupingKt : GroupingKt__GroupingJVMKt {
   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T, K, R> Grouping<T, K>.aggregate(operation: (K, R?, T, Boolean) -> R): Map<K, R> {
      var `$this$aggregateTo$iv`: Grouping = `$this$aggregate`;
      var `destination$iv`: java.util.Map = new LinkedHashMap();
      var var6: java.util.Iterator = `$this$aggregate`.sourceIterator();

      while (var6.hasNext()) {
         var `e$iv`: Any = var6.next();
         var `key$iv`: Any = `$this$aggregateTo$iv`.keyOf(`e$iv`);
         var `accumulator$iv`: Any = `destination$iv`.get(`key$iv`);
         `destination$iv`.put(
            `key$iv`, operation.invoke(`key$iv`, `accumulator$iv`, `e$iv`, `accumulator$iv` == null && !`destination$iv`.containsKey(`key$iv`))
         );
      }

      return `destination$iv`;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T, K, R, M : MutableMap<in K, R>> Grouping<T, K>.aggregateTo(destination: M, operation: (K, R?, T, Boolean) -> R): M {
      var var4: java.util.Iterator = `$this$aggregateTo`.sourceIterator();

      while (var4.hasNext()) {
         var e: Any = var4.next();
         var key: Any = `$this$aggregateTo`.keyOf(e);
         var accumulator: Any = destination.get(key);
         destination.put(key, operation.invoke(key, accumulator, e, accumulator == null && !destination.containsKey(key)));
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T, K, R> Grouping<T, K>.fold(initialValueSelector: (K, T) -> R, operation: (K, R, T) -> R): Map<K, R> {
      var `$this$aggregateTo$iv$iv`: Grouping = `$this$fold`;
      var `destination$iv$iv`: java.util.Map = new LinkedHashMap();
      var var9: java.util.Iterator = `$this$fold`.sourceIterator();

      while (var9.hasNext()) {
         var `e$iv$iv`: Any = var9.next();
         var `key$iv$iv`: Any = `$this$aggregateTo$iv$iv`.keyOf(`e$iv$iv`);
         var `accumulator$iv$iv`: Any = `destination$iv$iv`.get(`key$iv$iv`);
         `destination$iv$iv`.put(
            `key$iv$iv`,
            operation.invoke(
               `key$iv$iv`,
               if (`accumulator$iv$iv` == null && !`destination$iv$iv`.containsKey(`key$iv$iv`))
                  initialValueSelector.invoke(`key$iv$iv`, `e$iv$iv`)
                  else
                  `accumulator$iv$iv`,
               `e$iv$iv`
            )
         );
      }

      return `destination$iv$iv`;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T, K, R, M : MutableMap<in K, R>> Grouping<T, K>.foldTo(destination: M, initialValueSelector: (K, T) -> R, operation: (K, R, T) -> R): M {
      var `$this$aggregateTo$iv`: Grouping = `$this$foldTo`;
      var var7: java.util.Iterator = `$this$foldTo`.sourceIterator();

      while (var7.hasNext()) {
         var `e$iv`: Any = var7.next();
         var `key$iv`: Any = `$this$aggregateTo$iv`.keyOf(`e$iv`);
         var `accumulator$iv`: Any = destination.get(`key$iv`);
         destination.put(
            `key$iv`,
            operation.invoke(
               `key$iv`,
               if (`accumulator$iv` == null && !destination.containsKey(`key$iv`)) initialValueSelector.invoke(`key$iv`, `e$iv`) else `accumulator$iv`,
               `e$iv`
            )
         );
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T, K, R> Grouping<T, K>.fold(initialValue: R, operation: (R, T) -> R): Map<K, R> {
      var `$this$aggregateTo$iv$iv`: Grouping = `$this$fold`;
      var `destination$iv$iv`: java.util.Map = new LinkedHashMap();
      var var9: java.util.Iterator = `$this$fold`.sourceIterator();

      while (var9.hasNext()) {
         var `e$iv$iv`: Any = var9.next();
         var `key$iv$iv`: Any = `$this$aggregateTo$iv$iv`.keyOf(`e$iv$iv`);
         var `accumulator$iv$iv`: Any = `destination$iv$iv`.get(`key$iv$iv`);
         `destination$iv$iv`.put(
            `key$iv$iv`,
            operation.invoke(if (`accumulator$iv$iv` == null && !`destination$iv$iv`.containsKey(`key$iv$iv`)) initialValue else `accumulator$iv$iv`, `e$iv$iv`)
         );
      }

      return `destination$iv$iv`;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <T, K, R, M : MutableMap<in K, R>> Grouping<T, K>.foldTo(destination: M, initialValue: R, operation: (R, T) -> R): M {
      var `$this$aggregateTo$iv`: Grouping = `$this$foldTo`;
      var var7: java.util.Iterator = `$this$foldTo`.sourceIterator();

      while (var7.hasNext()) {
         var `e$iv`: Any = var7.next();
         var `key$iv`: Any = `$this$aggregateTo$iv`.keyOf(`e$iv`);
         var `accumulator$iv`: Any = destination.get(`key$iv`);
         destination.put(
            `key$iv`, operation.invoke(if (`accumulator$iv` == null && !destination.containsKey(`key$iv`)) initialValue else `accumulator$iv`, `e$iv`)
         );
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <S, T : S, K> Grouping<T, K>.reduce(operation: (K, S, T) -> S): Map<K, S> {
      var `$this$aggregateTo$iv$iv`: Grouping = `$this$reduce`;
      var `destination$iv$iv`: java.util.Map = new LinkedHashMap();
      var var8: java.util.Iterator = `$this$reduce`.sourceIterator();

      while (var8.hasNext()) {
         var `e$iv$iv`: Any = var8.next();
         var `key$iv$iv`: Any = `$this$aggregateTo$iv$iv`.keyOf(`e$iv$iv`);
         var `accumulator$iv$iv`: Any = `destination$iv$iv`.get(`key$iv$iv`);
         `destination$iv$iv`.put(
            `key$iv$iv`,
            if (`accumulator$iv$iv` == null && !`destination$iv$iv`.containsKey(`key$iv$iv`))
               `e$iv$iv`
               else
               operation.invoke(`key$iv$iv`, `accumulator$iv$iv`, `e$iv$iv`)
         );
      }

      return `destination$iv$iv`;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public inline fun <S, T : S, K, M : MutableMap<in K, S>> Grouping<T, K>.reduceTo(destination: M, operation: (K, S, T) -> S): M {
      var `$this$aggregateTo$iv`: Grouping = `$this$reduceTo`;
      var var6: java.util.Iterator = `$this$reduceTo`.sourceIterator();

      while (var6.hasNext()) {
         var `e$iv`: Any = var6.next();
         var `key$iv`: Any = `$this$aggregateTo$iv`.keyOf(`e$iv`);
         var `accumulator$iv`: Any = destination.get(`key$iv`);
         destination.put(
            `key$iv`, if (`accumulator$iv` == null && !destination.containsKey(`key$iv`)) `e$iv` else operation.invoke(`key$iv`, `accumulator$iv`, `e$iv`)
         );
      }

      return (M)destination;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <T, K, M : MutableMap<in K, Int>> Grouping<T, K>.eachCountTo(destination: M): M {
      var `initialValue$iv`: Any = 0;
      var `$this$aggregateTo$iv$iv`: Grouping = `$this$eachCountTo`;
      var var7: java.util.Iterator = `$this$eachCountTo`.sourceIterator();

      while (var7.hasNext()) {
         var `key$iv$iv`: Any = `$this$aggregateTo$iv$iv`.keyOf(var7.next());
         var `accumulator$iv$iv`: Any = destination.get(`key$iv$iv`);
         destination.put(
            `key$iv$iv`,
            ((if (`accumulator$iv$iv` == null && !destination.containsKey(`key$iv$iv`)) `initialValue$iv` else `accumulator$iv$iv`) as java.lang.Number)
                  .intValue()
               + 1
         );
      }

      return (M)destination;
   }

   open fun GroupingKt__GroupingKt() {
   }
}
