package kotlin.collections

import java.util.NoSuchElementException
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nMapWithDefault.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MapWithDefault.kt\nkotlin/collections/MapsKt__MapWithDefaultKt\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,104:1\n341#2,6:105\n*S KotlinDebug\n*F\n+ 1 MapWithDefault.kt\nkotlin/collections/MapsKt__MapWithDefaultKt\n*L\n24#1:105,6\n*E\n"])
// $VF: Class flags could not be determined
internal class MapsKt__MapWithDefaultKt {
   @JvmName(name = "getOrImplicitDefaultNullable")
   @PublishedApi
   @JvmStatic
   internal fun <K, V> Map<K, V>.getOrImplicitDefault(key: K): V {
      if (`$this$getOrImplicitDefault` is MapWithDefault) {
         return (V)(`$this$getOrImplicitDefault` as MapWithDefault).getOrImplicitDefault(key);
      } else {
         var `value$iv`: Any = `$this$getOrImplicitDefault`.get(key);
         if (`value$iv` == null && !`$this$getOrImplicitDefault`.containsKey(key)) {
            throw new NoSuchElementException("Key " + key + " is missing in the map.");
         } else {
            return (V)`value$iv`;
         }
      }
   }

   @JvmStatic
   public fun <K, V> Map<K, V>.withDefault(defaultValue: (K) -> V): Map<K, V> {
      return (java.util.Map<K, V>)(if (`$this$withDefault` is MapWithDefault)
         MapsKt.withDefault((`$this$withDefault` as MapWithDefault).getMap(), defaultValue)
         else
         new MapWithDefaultImpl(`$this$withDefault`, defaultValue));
   }

   @JvmName(name = "withDefaultMutable")
   @JvmStatic
   public fun <K, V> MutableMap<K, V>.withDefault(defaultValue: (K) -> V): MutableMap<K, V> {
      return (java.util.Map<K, V>)(if (`$this$withDefault` is MutableMapWithDefault)
         MapsKt.withDefaultMutable((`$this$withDefault` as MutableMapWithDefault).getMap(), defaultValue)
         else
         new MutableMapWithDefaultImpl(`$this$withDefault`, defaultValue));
   }

   open fun MapsKt__MapWithDefaultKt() {
   }
}
