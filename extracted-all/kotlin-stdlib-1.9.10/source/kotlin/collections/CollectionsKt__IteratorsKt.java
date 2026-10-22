package kotlin.collections

import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class CollectionsKt__IteratorsKt : CollectionsKt__IteratorsJVMKt {
   @InlineOnly
   @JvmStatic
   public inline operator fun <T> Iterator<T>.iterator(): Iterator<T> {
      return `$this$iterator`;
   }

   @JvmStatic
   public fun <T> Iterator<T>.withIndex(): Iterator<IndexedValue<T>> {
      return new IndexingIterator(`$this$withIndex`);
   }

   @JvmStatic
   public inline fun <T> Iterator<T>.forEach(operation: (T) -> Unit) {
      var var3: java.util.Iterator = `$this$forEach`;

      while (var3.hasNext()) {
         operation.invoke(var3.next());
      }
   }

   open fun CollectionsKt__IteratorsKt() {
   }
}
