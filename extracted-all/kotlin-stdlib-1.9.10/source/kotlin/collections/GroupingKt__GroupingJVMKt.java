package kotlin.collections

import java.util.LinkedHashMap
import java.util.Map.Entry
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.Ref
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics

@SourceDebugExtension(["SMAP\nGroupingJVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GroupingJVM.kt\nkotlin/collections/GroupingKt__GroupingJVMKt\n+ 2 Grouping.kt\nkotlin/collections/GroupingKt__GroupingKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,52:1\n143#2:53\n80#2,4:54\n85#2:59\n1#3:58\n1855#4,2:60\n*S KotlinDebug\n*F\n+ 1 GroupingJVM.kt\nkotlin/collections/GroupingKt__GroupingJVMKt\n*L\n22#1:53\n22#1:54,4\n22#1:59\n48#1:60,2\n*E\n"])
// $VF: Class flags could not be determined
internal class GroupingKt__GroupingJVMKt {
   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <T, K> Grouping<T, K>.eachCount(): Map<K, Int> {
      var `destination$iv`: java.util.Map = new LinkedHashMap();
      var `$this$aggregateTo$iv$iv`: Grouping = `$this$eachCount`;
      var var6: java.util.Iterator = `$this$eachCount`.sourceIterator();

      while (var6.hasNext()) {
         var `key$iv$iv`: Any = `$this$aggregateTo$iv$iv`.keyOf(var6.next());
         var `accumulator$iv$iv`: Any = `destination$iv`.get(`key$iv$iv`);
         var acc: Ref.IntRef = (if (`accumulator$iv$iv` == null && !`destination$iv`.containsKey(`key$iv$iv`)) new Ref.IntRef() else `accumulator$iv$iv`) as Ref.IntRef;
         acc.element++;
         `destination$iv`.put(`key$iv$iv`, acc);
      }

      for (Entry var27 : destination$iv.entrySet()) {
         TypeIntrinsics.asMutableMapEntry(var27).setValue((var27.getValue() as Ref.IntRef).element);
      }

      return TypeIntrinsics.asMutableMap(`destination$iv`);
   }

   @PublishedApi
   @InlineOnly
   @JvmStatic
   internal inline fun <K, V, R> MutableMap<K, V>.mapValuesInPlace(f: (kotlin.collections.Map.Entry<K, V>) -> R): MutableMap<K, R> {
      var `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         var it: Entry = `element$iv` as Entry;
         TypeIntrinsics.asMutableMapEntry(it).setValue(f.invoke(it));
      }

      return TypeIntrinsics.asMutableMap(`$this$mapValuesInPlace`);
   }

   open fun GroupingKt__GroupingJVMKt() {
   }
}
