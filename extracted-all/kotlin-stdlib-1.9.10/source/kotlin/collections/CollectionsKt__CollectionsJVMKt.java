package kotlin.collections

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.Enumeration
import java.util.Random
import kotlin.collections.builders.ListBuilder
import kotlin.internal.InlineOnly
import kotlin.internal.PlatformImplementationsKt
import kotlin.jvm.internal.CollectionToArray
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nCollectionsJVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CollectionsJVM.kt\nkotlin/collections/CollectionsKt__CollectionsJVMKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,122:1\n1#2:123\n*E\n"])
// $VF: Class flags could not be determined
internal class CollectionsKt__CollectionsJVMKt {
   @JvmStatic
   public fun <T> listOf(element: T): List<T> {
      var var10000: java.util.List = Collections.singletonList(element);
      return var10000;
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   internal inline fun <E> buildListInternal(builderAction: (MutableList<E>) -> Unit): List<E> {
      var var1: java.util.List = CollectionsKt.createListBuilder();
      builderAction.invoke(var1);
      return CollectionsKt.build(var1);
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   internal inline fun <E> buildListInternal(capacity: Int, builderAction: (MutableList<E>) -> Unit): List<E> {
      var var2: java.util.List = CollectionsKt.createListBuilder(capacity);
      builderAction.invoke(var2);
      return CollectionsKt.build(var2);
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun <E> createListBuilder(): MutableList<E> {
      return new ListBuilder();
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun <E> createListBuilder(capacity: Int): MutableList<E> {
      return new ListBuilder(capacity);
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun <E> build(builder: MutableList<E>): List<E> {
      return (builder as ListBuilder).build();
   }

   @InlineOnly
   @JvmStatic
   public inline fun <T> Enumeration<T>.toList(): List<T> {
      var var10000: ArrayList = Collections.list(`$this$toList`);
      return var10000;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T> Iterable<T>.shuffled(): List<T> {
      var var1: java.util.List = CollectionsKt.toMutableList(`$this$shuffled`);
      Collections.shuffle(var1);
      return var1;
   }

   @SinceKotlin(version = "1.2")
   @JvmStatic
   public fun <T> Iterable<T>.shuffled(random: Random): List<T> {
      var var2: java.util.List = CollectionsKt.toMutableList(`$this$shuffled`);
      Collections.shuffle(var2, random);
      return var2;
   }

   @InlineOnly
   @JvmStatic
   internal inline fun copyToArrayImpl(collection: Collection<*>): Array<Any?> {
      return CollectionToArray.toArray(collection);
   }

   @InlineOnly
   @JvmStatic
   internal inline fun <T> copyToArrayImpl(collection: Collection<*>, array: Array<T>): Array<T> {
      return (T[])CollectionToArray.toArray(collection, array);
   }

   @JvmStatic
   internal fun <T> Array<out T>.copyToArrayOfAny(isVarargs: Boolean): Array<out Any?> {
      var var10000: Array<Any>;
      if (isVarargs && `$this$copyToArrayOfAny`.getClass() == Object[]::class.java) {
         var10000 = `$this$copyToArrayOfAny`;
      } else {
         var10000 = Arrays.copyOf(`$this$copyToArrayOfAny`, `$this$copyToArrayOfAny`.length, Object[].class);
      }

      return var10000;
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   internal inline fun checkIndexOverflow(index: Int): Int {
      if (index < 0) {
         if (!PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
            throw new ArithmeticException("Index overflow has happened.");
         }

         CollectionsKt.throwIndexOverflow();
      }

      return index;
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   internal inline fun checkCountOverflow(count: Int): Int {
      if (count < 0) {
         if (!PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
            throw new ArithmeticException("Count overflow has happened.");
         }

         CollectionsKt.throwCountOverflow();
      }

      return count;
   }

   open fun CollectionsKt__CollectionsJVMKt() {
   }
}
