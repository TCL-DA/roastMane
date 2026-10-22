package kotlin.collections

import java.math.BigDecimal
import java.math.BigInteger
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.SortedSet
import java.util.TreeSet
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\n_CollectionsJvm.kt\nKotlin\n*S Kotlin\n*F\n+ 1 _CollectionsJvm.kt\nkotlin/collections/CollectionsKt___CollectionsJvmKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,168:1\n1963#2,14:169\n2333#2,14:183\n*S KotlinDebug\n*F\n+ 1 _CollectionsJvm.kt\nkotlin/collections/CollectionsKt___CollectionsJvmKt\n*L\n89#1:169,14\n126#1:183,14\n*E\n"])
// $VF: Class flags could not be determined
internal class CollectionsKt___CollectionsJvmKt : CollectionsKt__ReversedViewsKt {
   @JvmStatic
   public fun <R> Iterable<*>.filterIsInstance(klass: Class<R>): List<R> {
      return CollectionsKt.filterIsInstanceTo(`$this$filterIsInstance`, new ArrayList(), klass) as java.util.List<R>;
   }

   @JvmStatic
   public fun <C : MutableCollection<in R>, R> Iterable<*>.filterIsInstanceTo(destination: C, klass: Class<R>): C {
      for (Object element : $this$filterIsInstanceTo) {
         if (klass.isInstance(element)) {
            destination.add(element);
         }
      }

      return (C)destination;
   }

   @JvmStatic
   public fun <T> MutableList<T>.reverse() {
      Collections.reverse(`$this$reverse`);
   }

   @JvmStatic
   public fun <T : Comparable<T>> Iterable<T>.toSortedSet(): SortedSet<T> {
      return CollectionsKt.toCollection(`$this$toSortedSet`, new TreeSet()) as SortedSet<T>;
   }

   @JvmStatic
   public fun <T> Iterable<T>.toSortedSet(comparator: Comparator<in T>): SortedSet<T> {
      return CollectionsKt.toCollection(`$this$toSortedSet`, new TreeSet(comparator)) as SortedSet<T>;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Iterable<T>.sumOf(selector: (T) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;

      for (Object element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Iterable<T>.sumOf(selector: (T) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;

      for (Object element : $this$sumOf) {
         var10000 = sum.add(selector.invoke(element) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   open fun CollectionsKt___CollectionsJvmKt() {
   }
}
