package kotlin.collections

import java.util.Collections
import java.util.Comparator
import java.util.Random
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class CollectionsKt__MutableCollectionsJVMKt : CollectionsKt__IteratorsKt {
   @Deprecated(message = "Use sortWith(comparator) instead.", replaceWith = @ReplaceWith(expression = "this.sortWith(comparator)", imports = []), level = DeprecationLevel.ERROR)
   @InlineOnly
   @JvmStatic
   public inline fun <T> MutableList<T>.sort(comparator: Comparator<in T>) {
      throw new NotImplementedError(null, 1, null);
   }

   @Deprecated(message = "Use sortWith(Comparator(comparison)) instead.", replaceWith = @ReplaceWith(expression = "this.sortWith(Comparator(comparison))", imports = []), level = DeprecationLevel.ERROR)
   @InlineOnly
   @JvmStatic
   public inline fun <T> MutableList<T>.sort(comparison: (T, T) -> Int) {
      throw new NotImplementedError(null, 1, null);
   }

   @JvmStatic
   public fun <T : Comparable<T>> MutableList<T>.sort() {
      if (`$this$sort`.size() > 1) {
         Collections.sort(`$this$sort`);
      }
   }

   @JvmStatic
   public fun <T> MutableList<T>.sortWith(comparator: Comparator<in T>) {
      if (`$this$sortWith`.size() > 1) {
         Collections.sort(`$this$sortWith`, comparator);
      }
   }

   @InlineOnly
   @SinceKotlin(version = "1.2")
   @JvmStatic
   public inline fun <T> MutableList<T>.fill(value: T) {
      Collections.fill(`$this$fill`, value);
   }

   @InlineOnly
   @SinceKotlin(version = "1.2")
   @JvmStatic
   public inline fun <T> MutableList<T>.shuffle() {
      Collections.shuffle(`$this$shuffle`);
   }

   @InlineOnly
   @SinceKotlin(version = "1.2")
   @JvmStatic
   public inline fun <T> MutableList<T>.shuffle(random: Random) {
      Collections.shuffle(`$this$shuffle`, random);
   }

   open fun CollectionsKt__MutableCollectionsJVMKt() {
   }
}
