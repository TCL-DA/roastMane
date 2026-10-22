package kotlin.collections

import java.util.Collections
import java.util.Comparator
import java.util.TreeSet
import kotlin.collections.builders.SetBuilder
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class SetsKt__SetsJVMKt {
   @JvmStatic
   public fun <T> setOf(element: T): Set<T> {
      var var10000: java.util.Set = Collections.singleton(element);
      return var10000;
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   internal inline fun <E> buildSetInternal(builderAction: (MutableSet<E>) -> Unit): Set<E> {
      var var1: java.util.Set = SetsKt.createSetBuilder();
      builderAction.invoke(var1);
      return SetsKt.build(var1);
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   internal inline fun <E> buildSetInternal(capacity: Int, builderAction: (MutableSet<E>) -> Unit): Set<E> {
      var var2: java.util.Set = SetsKt.createSetBuilder(capacity);
      builderAction.invoke(var2);
      return SetsKt.build(var2);
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun <E> createSetBuilder(): MutableSet<E> {
      return new SetBuilder();
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun <E> createSetBuilder(capacity: Int): MutableSet<E> {
      return new SetBuilder(capacity);
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun <E> build(builder: MutableSet<E>): Set<E> {
      return (builder as SetBuilder).build();
   }

   @JvmStatic
   public fun <T> sortedSetOf(vararg elements: T): TreeSet<T> {
      return ArraysKt.toCollection(elements, new TreeSet()) as TreeSet<T>;
   }

   @JvmStatic
   public fun <T> sortedSetOf(comparator: Comparator<in T>, vararg elements: T): TreeSet<T> {
      return ArraysKt.toCollection(elements, new TreeSet(comparator)) as TreeSet<T>;
   }

   open fun SetsKt__SetsJVMKt() {
   }
}
