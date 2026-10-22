package kotlin.comparisons

import java.util.Comparator

// $VF: Class flags could not be determined
internal class ComparisonsKt___ComparisonsKt : ComparisonsKt___ComparisonsJvmKt {
   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <T> maxOf(a: T, b: T, c: T, comparator: Comparator<in T>): T {
      return (T)ComparisonsKt.maxOf(a, ComparisonsKt.maxOf(b, c, comparator), comparator);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <T> maxOf(a: T, b: T, comparator: Comparator<in T>): T {
      return (T)(if (comparator.compare(a, b) >= 0) a else b);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> maxOf(a: T, vararg other: T, comparator: Comparator<in T>): T {
      var max: Any = a;

      for (Object e : other) {
         if (comparator.compare(max, e) < 0) {
            max = e;
         }
      }

      return (T)max;
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <T> minOf(a: T, b: T, c: T, comparator: Comparator<in T>): T {
      return (T)ComparisonsKt.minOf(a, ComparisonsKt.minOf(b, c, comparator), comparator);
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <T> minOf(a: T, b: T, comparator: Comparator<in T>): T {
      return (T)(if (comparator.compare(a, b) <= 0) a else b);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> minOf(a: T, vararg other: T, comparator: Comparator<in T>): T {
      var min: Any = a;

      for (Object e : other) {
         if (comparator.compare(min, e) > 0) {
            min = e;
         }
      }

      return (T)min;
   }

   open fun ComparisonsKt___ComparisonsKt() {
   }
}
