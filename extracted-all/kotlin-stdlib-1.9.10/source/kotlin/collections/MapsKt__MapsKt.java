package kotlin.collections

import java.util.HashMap
import java.util.LinkedHashMap
import kotlin.collections.Map.Entry
import kotlin.collections.MutableMap.MutableEntry
import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics

@SourceDebugExtension(["SMAP\nMaps.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,804:1\n403#1:814\n414#1:819\n511#1,6:824\n536#1,6:830\n1#2:805\n1238#3,4:806\n1238#3,4:810\n1238#3,4:815\n1238#3,4:820\n*S KotlinDebug\n*F\n+ 1 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n453#1:814\n468#1:819\n526#1:824,6\n551#1:830,6\n403#1:806,4\n414#1:810,4\n453#1:815,4\n468#1:820,4\n*E\n"])
// $VF: Class flags could not be determined
internal class MapsKt__MapsKt : MapsKt__MapsJVMKt {
   @JvmStatic
   public fun <K, V> emptyMap(): Map<K, V> {
      var var10000: EmptyMap = EmptyMap.INSTANCE;
      return var10000;
   }

   @JvmStatic
   public fun <K, V> mapOf(vararg pairs: Pair<K, V>): Map<K, V> {
      return if (pairs.length > 0) MapsKt.toMap(pairs, new LinkedHashMap(MapsKt.mapCapacity(pairs.length))) else MapsKt.emptyMap();
   }

   @InlineOnly
   @JvmStatic
   public inline fun <K, V> mapOf(): Map<K, V> {
      return MapsKt.emptyMap();
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> mutableMapOf(): MutableMap<K, V> {
      return new LinkedHashMap();
   }

   @JvmStatic
   public fun <K, V> mutableMapOf(vararg pairs: Pair<K, V>): MutableMap<K, V> {
      var var1: LinkedHashMap = new LinkedHashMap(MapsKt.mapCapacity(pairs.length));
      MapsKt.putAll(var1, pairs);
      return var1;
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> hashMapOf(): HashMap<K, V> {
      return new HashMap();
   }

   @JvmStatic
   public fun <K, V> hashMapOf(vararg pairs: Pair<K, V>): HashMap<K, V> {
      var var1: HashMap = new HashMap(MapsKt.mapCapacity(pairs.length));
      MapsKt.putAll(var1, pairs);
      return var1;
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> linkedMapOf(): LinkedHashMap<K, V> {
      return new LinkedHashMap();
   }

   @JvmStatic
   public fun <K, V> linkedMapOf(vararg pairs: Pair<K, V>): LinkedHashMap<K, V> {
      return MapsKt.toMap(pairs, new LinkedHashMap(MapsKt.mapCapacity(pairs.length))) as LinkedHashMap<K, V>;
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> buildMap(builderAction: (MutableMap<K, V>) -> Unit): Map<K, V> {
      contract {
         callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
      }

      var var1: java.util.Map = MapsKt.createMapBuilder();
      builderAction.invoke(var1);
      return MapsKt.build(var1);
   }

   @SinceKotlin(version = "1.6")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> buildMap(capacity: Int, builderAction: (MutableMap<K, V>) -> Unit): Map<K, V> {
      contract {
         callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE)
      }

      var var2: java.util.Map = MapsKt.createMapBuilder(capacity);
      builderAction.invoke(var2);
      return MapsKt.build(var2);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>.isNotEmpty(): Boolean {
      return !`$this$isNotEmpty`.isEmpty();
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<out K, V>?.isNullOrEmpty(): Boolean {
      contract {
         returns(false) implies (this != null)
      }

      return `$this$isNullOrEmpty` == null || `$this$isNullOrEmpty`.isEmpty();
   }

   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<K, V>?.orEmpty(): Map<K, V> {
      var var10000: java.util.Map = `$this$orEmpty`;
      if (`$this$orEmpty` == null) {
         var10000 = MapsKt.emptyMap();
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <M, R> M.ifEmpty(defaultValue: () -> R): R where M : Map<*, *>, M : R {
      return (R)(if (`$this$ifEmpty`.isEmpty()) defaultValue.invoke() else `$this$ifEmpty`);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> Map<out K, V>.contains(key: K): Boolean {
      return `$this$contains`.containsKey(key);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> Map<out K, V>.get(key: K): V? {
      return (V)`$this$get`.get(key);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<K, V>.set(key: K, value: V) {
      `$this$set`.put(key, value);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <K> Map<out K, *>.containsKey(key: K): Boolean {
      return `$this$containsKey`.containsKey(key);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<K, V>.containsValue(value: V): Boolean {
      return `$this$containsValue`.containsValue(value);
   }

   @InlineOnly
   @JvmStatic
   public inline fun <K, V> MutableMap<out K, V>.remove(key: K): V? {
      return (V)TypeIntrinsics.asMutableMap(`$this$remove`).remove(key);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> Entry<K, V>.component1(): K {
      return (K)`$this$component1`.getKey();
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> Entry<K, V>.component2(): V {
      return (V)`$this$component2`.getValue();
   }

   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Entry<K, V>.toPair(): Pair<K, V> {
      return (Pair<K, V>)(new Pair<>(`$this$toPair`.getKey(), `$this$toPair`.getValue()));
   }

   @InlineOnly
   @JvmStatic
   public inline fun <K, V> Map<K, V>.getOrElse(key: K, defaultValue: () -> V): V {
      var var10000: Any = `$this$getOrElse`.get(key);
      if (var10000 == null) {
         var10000 = defaultValue.invoke();
      }

      return (V)var10000;
   }

   @JvmStatic
   internal inline fun <K, V> Map<K, V>.getOrElseNullable(key: K, defaultValue: () -> V): V {
      var value: Any = `$this$getOrElseNullable`.get(key);
      return (V)(if (value == null && !`$this$getOrElseNullable`.containsKey(key)) defaultValue.invoke() else value);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <K, V> Map<K, V>.getValue(key: K): V {
      return (V)MapsKt.getOrImplicitDefaultNullable(`$this$getValue`, key);
   }

   @JvmStatic
   public inline fun <K, V> MutableMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
      var value: Any = `$this$getOrPut`.get(key);
      var var10000: Any;
      if (value == null) {
         var answer: Any = defaultValue.invoke();
         `$this$getOrPut`.put(key, answer);
         var10000 = answer;
      } else {
         var10000 = value;
      }

      return (V)var10000;
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> Map<out K, V>.iterator(): Iterator<Entry<K, V>> {
      return `$this$iterator`.entrySet().iterator();
   }

   @JvmName(name = "mutableIterator")
   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<K, V>.iterator(): MutableIterator<MutableEntry<K, V>> {
      return `$this$iterator`.entrySet().iterator();
   }

   @JvmStatic
   public inline fun <K, V, R, M : MutableMap<in K, in R>> Map<out K, V>.mapValuesTo(destination: M, transform: (Entry<K, V>) -> R): M {
      var `$this$associateByTo$iv`: java.lang.Iterable;
      for (Object element$iv : $this$associateByTo$iv) {
         destination.put((`element$iv` as java.util.Map.Entry).getKey(), transform.invoke(`element$iv`));
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V, R, M : MutableMap<in R, in V>> Map<out K, V>.mapKeysTo(destination: M, transform: (Entry<K, V>) -> R): M {
      var `$this$associateByTo$iv`: java.lang.Iterable;
      for (Object element$iv : $this$associateByTo$iv) {
         destination.put(transform.invoke(`element$iv`), (`element$iv` as java.util.Map.Entry).getValue());
      }

      return (M)destination;
   }

   @JvmStatic
   public fun <K, V> MutableMap<in K, in V>.putAll(pairs: Array<out Pair<K, V>>) {
      for (Pair var4 : pairs) {
         `$this$putAll`.put(var4.component1(), var4.component2());
      }
   }

   @JvmStatic
   public fun <K, V> MutableMap<in K, in V>.putAll(pairs: Iterable<Pair<K, V>>) {
      for (Pair var3 : pairs) {
         `$this$putAll`.put(var3.component1(), var3.component2());
      }
   }

   @JvmStatic
   public fun <K, V> MutableMap<in K, in V>.putAll(pairs: Sequence<Pair<K, V>>) {
      for (Pair var3 : pairs) {
         `$this$putAll`.put(var3.component1(), var3.component2());
      }
   }

   @JvmStatic
   public inline fun <K, V, R> Map<out K, V>.mapValues(transform: (Entry<K, V>) -> R): Map<K, R> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(MapsKt.mapCapacity(`$this$mapValues`.size()));

      var `$this$associateByTo$iv$iv`: java.lang.Iterable;
      for (Object element$iv$iv : $this$associateByTo$iv$iv) {
         `destination$iv`.put((`element$iv$iv` as java.util.Map.Entry).getKey(), transform.invoke(`element$iv$iv`));
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V, R> Map<out K, V>.mapKeys(transform: (Entry<K, V>) -> R): Map<R, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap(MapsKt.mapCapacity(`$this$mapKeys`.size()));

      var `$this$associateByTo$iv$iv`: java.lang.Iterable;
      for (Object element$iv$iv : $this$associateByTo$iv$iv) {
         `destination$iv`.put(transform.invoke(`element$iv$iv`), (`element$iv$iv` as java.util.Map.Entry).getValue());
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V> Map<out K, V>.filterKeys(predicate: (K) -> Boolean): Map<K, V> {
      var result: LinkedHashMap = new LinkedHashMap();

      for (java.util.Map.Entry entry : $this$filterKeys.entrySet()) {
         if (predicate.invoke(entry.getKey()) as java.lang.Boolean) {
            result.put(entry.getKey(), entry.getValue());
         }
      }

      return result;
   }

   @JvmStatic
   public inline fun <K, V> Map<out K, V>.filterValues(predicate: (V) -> Boolean): Map<K, V> {
      var result: LinkedHashMap = new LinkedHashMap();

      for (java.util.Map.Entry entry : $this$filterValues.entrySet()) {
         if (predicate.invoke(entry.getValue()) as java.lang.Boolean) {
            result.put(entry.getKey(), entry.getValue());
         }
      }

      return result;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> Map<out K, V>.filterTo(destination: M, predicate: (Entry<K, V>) -> Boolean): M {
      for (java.util.Map.Entry element : $this$filterTo.entrySet()) {
         if (predicate.invoke(element) as java.lang.Boolean) {
            destination.put(element.getKey(), element.getValue());
         }
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V> Map<out K, V>.filter(predicate: (Entry<K, V>) -> Boolean): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (java.util.Map.Entry element$iv : $this$filter.entrySet()) {
         if (predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.put(`element$iv`.getKey(), `element$iv`.getValue());
         }
      }

      return `destination$iv`;
   }

   @JvmStatic
   public inline fun <K, V, M : MutableMap<in K, in V>> Map<out K, V>.filterNotTo(destination: M, predicate: (Entry<K, V>) -> Boolean): M {
      for (java.util.Map.Entry element : $this$filterNotTo.entrySet()) {
         if (!predicate.invoke(element) as java.lang.Boolean) {
            destination.put(element.getKey(), element.getValue());
         }
      }

      return (M)destination;
   }

   @JvmStatic
   public inline fun <K, V> Map<out K, V>.filterNot(predicate: (Entry<K, V>) -> Boolean): Map<K, V> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();

      for (java.util.Map.Entry element$iv : $this$filterNot.entrySet()) {
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            `destination$iv`.put(`element$iv`.getKey(), `element$iv`.getValue());
         }
      }

      return `destination$iv`;
   }

   @JvmStatic
   public fun <K, V> Iterable<Pair<K, V>>.toMap(): Map<K, V> {
      if (`$this$toMap` is java.util.Collection) {
         var var10000: java.util.Map;
         switch (((java.util.Collection)$this$toMap).size()) {
            case 0:
               var10000 = MapsKt.emptyMap();
               break;
            case 1:
               var10000 = MapsKt.mapOf(
                  if (`$this$toMap` is java.util.List) (`$this$toMap` as java.util.List).get(0) as Pair else `$this$toMap`.iterator().next() as Pair
               );
               break;
            default:
               var10000 = MapsKt.toMap(`$this$toMap`, new LinkedHashMap(MapsKt.mapCapacity((`$this$toMap` as java.util.Collection).size())));
         }

         return var10000;
      } else {
         return MapsKt.optimizeReadOnlyMap(MapsKt.toMap(`$this$toMap`, new LinkedHashMap()));
      }
   }

   @JvmStatic
   public fun <K, V, M : MutableMap<in K, in V>> Iterable<Pair<K, V>>.toMap(destination: M): M {
      MapsKt.putAll(destination, `$this$toMap`);
      return (M)destination;
   }

   @JvmStatic
   public fun <K, V> Array<out Pair<K, V>>.toMap(): Map<K, V> {
      var var10000: java.util.Map;
      switch ($this$toMap.length) {
         case 0:
            var10000 = MapsKt.emptyMap();
            break;
         case 1:
            var10000 = MapsKt.mapOf(`$this$toMap`[0]);
            break;
         default:
            var10000 = MapsKt.toMap(`$this$toMap`, new LinkedHashMap(MapsKt.mapCapacity(`$this$toMap`.length)));
      }

      return var10000;
   }

   @JvmStatic
   public fun <K, V, M : MutableMap<in K, in V>> Array<out Pair<K, V>>.toMap(destination: M): M {
      MapsKt.putAll(destination, `$this$toMap`);
      return (M)destination;
   }

   @JvmStatic
   public fun <K, V> Sequence<Pair<K, V>>.toMap(): Map<K, V> {
      return MapsKt.optimizeReadOnlyMap(MapsKt.toMap(`$this$toMap`, new LinkedHashMap()));
   }

   @JvmStatic
   public fun <K, V, M : MutableMap<in K, in V>> Sequence<Pair<K, V>>.toMap(destination: M): M {
      MapsKt.putAll(destination, `$this$toMap`);
      return (M)destination;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <K, V> Map<out K, V>.toMap(): Map<K, V> {
      var var10000: java.util.Map;
      switch ($this$toMap.size()) {
         case 0:
            var10000 = MapsKt.emptyMap();
            break;
         case 1:
            var10000 = MapsKt.toSingletonMap(`$this$toMap`);
            break;
         default:
            var10000 = MapsKt.toMutableMap(`$this$toMap`);
      }

      return var10000;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <K, V> Map<out K, V>.toMutableMap(): MutableMap<K, V> {
      return new LinkedHashMap(`$this$toMutableMap`);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <K, V, M : MutableMap<in K, in V>> Map<out K, V>.toMap(destination: M): M {
      destination.putAll(`$this$toMap`);
      return (M)destination;
   }

   @JvmStatic
   public operator fun <K, V> Map<out K, V>.plus(pair: Pair<K, V>): Map<K, V> {
      var var10000: java.util.Map;
      if (`$this$plus`.isEmpty()) {
         var10000 = MapsKt.mapOf(pair);
      } else {
         var var2: LinkedHashMap = new LinkedHashMap(`$this$plus`);
         var2.put(pair.getFirst(), pair.getSecond());
         var10000 = var2;
      }

      return var10000;
   }

   @JvmStatic
   public operator fun <K, V> Map<out K, V>.plus(pairs: Iterable<Pair<K, V>>): Map<K, V> {
      var var10000: java.util.Map;
      if (`$this$plus`.isEmpty()) {
         var10000 = MapsKt.toMap(pairs);
      } else {
         var var2: LinkedHashMap = new LinkedHashMap(`$this$plus`);
         MapsKt.putAll(var2, pairs);
         var10000 = var2;
      }

      return var10000;
   }

   @JvmStatic
   public operator fun <K, V> Map<out K, V>.plus(pairs: Array<out Pair<K, V>>): Map<K, V> {
      var var10000: java.util.Map;
      if (`$this$plus`.isEmpty()) {
         var10000 = MapsKt.toMap(pairs);
      } else {
         var var2: LinkedHashMap = new LinkedHashMap(`$this$plus`);
         MapsKt.putAll(var2, pairs);
         var10000 = var2;
      }

      return var10000;
   }

   @JvmStatic
   public operator fun <K, V> Map<out K, V>.plus(pairs: Sequence<Pair<K, V>>): Map<K, V> {
      var var2: LinkedHashMap = new LinkedHashMap(`$this$plus`);
      MapsKt.putAll(var2, pairs);
      return MapsKt.optimizeReadOnlyMap(var2);
   }

   @JvmStatic
   public operator fun <K, V> Map<out K, V>.plus(map: Map<out K, V>): Map<K, V> {
      var var2: LinkedHashMap = new LinkedHashMap(`$this$plus`);
      var2.putAll(map);
      return var2;
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<in K, in V>.plusAssign(pair: Pair<K, V>) {
      `$this$plusAssign`.put(pair.getFirst(), pair.getSecond());
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<in K, in V>.plusAssign(pairs: Iterable<Pair<K, V>>) {
      MapsKt.putAll(`$this$plusAssign`, pairs);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<in K, in V>.plusAssign(pairs: Array<out Pair<K, V>>) {
      MapsKt.putAll(`$this$plusAssign`, pairs);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<in K, in V>.plusAssign(pairs: Sequence<Pair<K, V>>) {
      MapsKt.putAll(`$this$plusAssign`, pairs);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<in K, in V>.plusAssign(map: Map<K, V>) {
      `$this$plusAssign`.putAll(map);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public operator fun <K, V> Map<out K, V>.minus(key: K): Map<K, V> {
      var var2: java.util.Map = MapsKt.toMutableMap(`$this$minus`);
      var2.remove(key);
      return MapsKt.optimizeReadOnlyMap(var2);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public operator fun <K, V> Map<out K, V>.minus(keys: Iterable<K>): Map<K, V> {
      var var2: java.util.Map = MapsKt.toMutableMap(`$this$minus`);
      CollectionsKt.removeAll(var2.keySet(), keys);
      return MapsKt.optimizeReadOnlyMap(var2);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public operator fun <K, V> Map<out K, V>.minus(keys: Array<out K>): Map<K, V> {
      var var2: java.util.Map = MapsKt.toMutableMap(`$this$minus`);
      CollectionsKt.removeAll(var2.keySet(), keys);
      return MapsKt.optimizeReadOnlyMap(var2);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public operator fun <K, V> Map<out K, V>.minus(keys: Sequence<K>): Map<K, V> {
      var var2: java.util.Map = MapsKt.toMutableMap(`$this$minus`);
      CollectionsKt.removeAll(var2.keySet(), keys);
      return MapsKt.optimizeReadOnlyMap(var2);
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<K, V>.minusAssign(key: K) {
      `$this$minusAssign`.remove(key);
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<K, V>.minusAssign(keys: Iterable<K>) {
      CollectionsKt.removeAll(`$this$minusAssign`.keySet(), keys);
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<K, V>.minusAssign(keys: Array<out K>) {
      CollectionsKt.removeAll(`$this$minusAssign`.keySet(), keys);
   }

   @SinceKotlin(version = "1.1")
   @InlineOnly
   @JvmStatic
   public inline operator fun <K, V> MutableMap<K, V>.minusAssign(keys: Sequence<K>) {
      CollectionsKt.removeAll(`$this$minusAssign`.keySet(), keys);
   }

   @JvmStatic
   internal fun <K, V> Map<K, V>.optimizeReadOnlyMap(): Map<K, V> {
      var var10000: java.util.Map;
      switch ($this$optimizeReadOnlyMap.size()) {
         case 0:
            var10000 = MapsKt.emptyMap();
            break;
         case 1:
            var10000 = MapsKt.toSingletonMap(`$this$optimizeReadOnlyMap`);
            break;
         default:
            var10000 = `$this$optimizeReadOnlyMap`;
      }

      return var10000;
   }

   open fun MapsKt__MapsKt() {
   }
}
