package kotlin.collections

import java.util.Collections
import java.util.Comparator
import java.util.Properties
import java.util.SortedMap
import java.util.TreeMap
import java.util.Map.Entry
import java.util.concurrent.ConcurrentMap
import kotlin.collections.builders.MapBuilder
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nMapsJVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,157:1\n1#2:158\n*E\n"])
// $VF: Class flags could not be determined
internal class MapsKt__MapsJVMKt : MapsKt__MapWithDefaultKt {
   private const val INT_MAX_POWER_OF_TWO: Int = 1073741824

   @JvmStatic
   public fun <K, V> mapOf(pair: Pair<K, V>): Map<K, V> {
      var var10000: java.util.Map = Collections.singletonMap(pair.getFirst(), pair.getSecond());
      return var10000;
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   internal inline fun <K, V> buildMapInternal(builderAction: (MutableMap<K, V>) -> Unit): Map<K, V> {
      var var1: java.util.Map = MapsKt.createMapBuilder();
      builderAction.invoke(var1);
      return MapsKt.build(var1);
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   internal inline fun <K, V> buildMapInternal(capacity: Int, builderAction: (MutableMap<K, V>) -> Unit): Map<K, V> {
      var var2: java.util.Map = MapsKt.createMapBuilder(capacity);
      builderAction.invoke(var2);
      return MapsKt.build(var2);
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun <K, V> createMapBuilder(): MutableMap<K, V> {
      return new MapBuilder();
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun <K, V> createMapBuilder(capacity: Int): MutableMap<K, V> {
      return new MapBuilder(capacity);
   }

   @PublishedApi
   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun <K, V> build(builder: MutableMap<K, V>): Map<K, V> {
      return (builder as MapBuilder).build();
   }

   @JvmStatic
   public inline fun <K, V> ConcurrentMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
      var var10000: Any = `$this$getOrPut`.get(key);
      if (var10000 == null) {
         var var4: Any = defaultValue.invoke();
         var10000 = `$this$getOrPut`.putIfAbsent(key, var4);
         if (var10000 == null) {
            var10000 = var4;
         }
      }

      return (V)var10000;
   }

   @JvmStatic
   public fun <K : Comparable<K>, V> Map<out K, V>.toSortedMap(): SortedMap<K, V> {
      return new TreeMap(`$this$toSortedMap`);
   }

   @JvmStatic
   public fun <K, V> Map<out K, V>.toSortedMap(comparator: Comparator<in K>): SortedMap<K, V> {
      var var2: TreeMap = new TreeMap(comparator);
      var2.putAll(`$this$toSortedMap`);
      return var2;
   }

   @JvmStatic
   public fun <K : Comparable<K>, V> sortedMapOf(vararg pairs: Pair<K, V>): SortedMap<K, V> {
      var var1: TreeMap = new TreeMap();
      MapsKt.putAll(var1, pairs);
      return var1;
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <K, V> sortedMapOf(comparator: Comparator<in K>, vararg pairs: Pair<K, V>): SortedMap<K, V> {
      var var2: TreeMap = new TreeMap(comparator);
      MapsKt.putAll(var2, pairs);
      return var2;
   }

   @InlineOnly
   @JvmStatic
   public inline fun Map<String, String>.toProperties(): Properties {
      var var1: Properties = new Properties();
      var1.putAll(`$this$toProperties`);
      return var1;
   }

   @InlineOnly
   @JvmStatic
   internal inline fun <K, V> Map<K, V>.toSingletonMapOrSelf(): Map<K, V> {
      return MapsKt.toSingletonMap(`$this$toSingletonMapOrSelf`);
   }

   @JvmStatic
   internal fun <K, V> Map<out K, V>.toSingletonMap(): Map<K, V> {
      var `$this$toSingletonMap_u24lambda_u245`: Entry = `$this$toSingletonMap`.entrySet().iterator().next() as Entry;
      var var10000: java.util.Map = Collections.singletonMap(`$this$toSingletonMap_u24lambda_u245`.getKey(), `$this$toSingletonMap_u24lambda_u245`.getValue());
      return var10000;
   }

   @PublishedApi
   @JvmStatic
   internal fun mapCapacity(expectedSize: Int): Int {
      return if (expectedSize < 0)
         expectedSize
         else
         (if (expectedSize < 3) expectedSize + 1 else (if (expectedSize < 1073741824) (int)((float)expectedSize / 0.75F + 1.0F) else Integer.MAX_VALUE));
   }

   open fun MapsKt__MapsJVMKt() {
   }
}
