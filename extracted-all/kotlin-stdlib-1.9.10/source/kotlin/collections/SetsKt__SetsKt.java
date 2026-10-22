package kotlin.collections

import java.util.HashSet
import java.util.LinkedHashSet
import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class SetsKt__SetsKt : SetsKt__SetsJVMKt {
   @JvmStatic
   public fun <T> emptySet(): Set<T> {
      return EmptySet.INSTANCE;
   }

   @JvmStatic
   public fun <T> setOf(vararg elements: T): Set<T> {
      return (java.util.Set<T>)(if (elements.length > 0) ArraysKt.toSet(elements) else SetsKt.emptySet());
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> setOf(): Set<T> {
      return SetsKt.emptySet();
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun <T> mutableSetOf(): MutableSet<T> {
      return new LinkedHashSet();
   }

   @JvmStatic
   public fun <T> mutableSetOf(vararg elements: T): MutableSet<T> {
      return ArraysKt.toCollection(elements, new LinkedHashSet(MapsKt.mapCapacity(elements.length))) as java.util.Set<T>;
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun <T> hashSetOf(): HashSet<T> {
      return new HashSet();
   }

   @JvmStatic
   public fun <T> hashSetOf(vararg elements: T): HashSet<T> {
      return ArraysKt.toCollection(elements, new HashSet(MapsKt.mapCapacity(elements.length))) as HashSet<T>;
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun <T> linkedSetOf(): LinkedHashSet<T> {
      return new LinkedHashSet();
   }

   @JvmStatic
   public fun <T> linkedSetOf(vararg elements: T): LinkedHashSet<T> {
      return ArraysKt.toCollection(elements, new LinkedHashSet(MapsKt.mapCapacity(elements.length))) as LinkedHashSet<T>;
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T : Any> setOfNotNull(element: T?): Set<T> {
      return (java.util.Set<T>)(if (element != null) SetsKt.setOf(element) else SetsKt.emptySet());
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T : Any> setOfNotNull(vararg elements: T?): Set<T> {
      return ArraysKt.filterNotNullTo(elements, new LinkedHashSet()) as java.util.Set<T>;
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <E> buildSet(builderAction: (MutableSet<E>) -> Unit): Set<E> {
      contract {
         callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
      }

      var var1: java.util.Set = SetsKt.createSetBuilder();
      builderAction.invoke(var1);
      return SetsKt.build(var1);
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <E> buildSet(capacity: Int, builderAction: (MutableSet<E>) -> Unit): Set<E> {
      contract {
         callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
      }

      var var2: java.util.Set = SetsKt.createSetBuilder(capacity);
      builderAction.invoke(var2);
      return SetsKt.build(var2);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Set<T>?.orEmpty(): Set<T> {
      var var10000: java.util.Set = `$this$orEmpty`;
      if (`$this$orEmpty` == null) {
         var10000 = SetsKt.emptySet();
      }

      return var10000;
   }

   @JvmStatic
   internal fun <T> Set<T>.optimizeReadOnlySet(): Set<T> {
      var var10000: java.util.Set;
      switch ($this$optimizeReadOnlySet.size()) {
         case 0:
            var10000 = SetsKt.emptySet();
            break;
         case 1:
            var10000 = SetsKt.setOf(`$this$optimizeReadOnlySet`.iterator().next());
            break;
         default:
            var10000 = `$this$optimizeReadOnlySet`;
      }

      return var10000;
   }

   open fun SetsKt__SetsKt() {
   }
}
