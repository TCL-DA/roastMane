package kotlin.comparisons

import java.util.Comparator

private object ReverseOrderComparator private constructor() : Comparator<java.lang.Comparable<? super Object>> {
   @JvmStatic
   public ReverseOrderComparator INSTANCE = new ReverseOrderComparator();


   public open fun compare(a: Comparable<Any>, b: Comparable<Any>): Int {
      return b.compareTo(a);
   }

   public override fun reversed(): Comparator<Comparable<Any>> {
      return NaturalOrderComparator.INSTANCE;
   }
}
