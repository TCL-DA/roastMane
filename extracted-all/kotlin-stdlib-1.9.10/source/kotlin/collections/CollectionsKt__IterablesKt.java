package kotlin.collections

import java.util.ArrayList
import kotlin.internal.InlineOnly
import org.jetbrains.annotations.NotNull

// $VF: Class flags could not be determined
internal class CollectionsKt__IterablesKt : CollectionsKt__CollectionsKt {
   @InlineOnly
   @JvmStatic
   public inline fun <T> Iterable(crossinline iterator: () -> Iterator<T>): Iterable<T> {
      return new java.lang.Iterable<T>(iterator) {
         {
            this.$iterator = `$iterator`;
         }

         @NotNull
         @Override
         public java.util.Iterator<T> iterator() {
            return this.$iterator.invoke() as java.util.Iterator<T>;
         }
      };
   }

   @PublishedApi
   @JvmStatic
   internal fun <T> Iterable<T>.collectionSizeOrNull(): Int? {
      return if (`$this$collectionSizeOrNull` is java.util.Collection) (`$this$collectionSizeOrNull` as java.util.Collection).size() else null;
   }

   @PublishedApi
   @JvmStatic
   internal fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int {
      return if (`$this$collectionSizeOrDefault` is java.util.Collection) (`$this$collectionSizeOrDefault` as java.util.Collection).size() else var1;
   }

   @JvmStatic
   public fun <T> Iterable<Iterable<T>>.flatten(): List<T> {
      var result: ArrayList = new ArrayList();

      for (java.lang.Iterable element : $this$flatten) {
         CollectionsKt.addAll(result, element);
      }

      return result;
   }

   @JvmStatic
   public fun <T, R> Iterable<Pair<T, R>>.unzip(): Pair<List<T>, List<R>> {
      var expectedSize: Int = CollectionsKt.collectionSizeOrDefault(`$this$unzip`, 10);
      var listT: ArrayList = new ArrayList(expectedSize);
      var listR: ArrayList = new ArrayList(expectedSize);

      for (Pair pair : $this$unzip) {
         listT.add(pair.getFirst());
         listR.add(pair.getSecond());
      }

      return TuplesKt.to(listT, listR);
   }

   open fun CollectionsKt__IterablesKt() {
   }
}
