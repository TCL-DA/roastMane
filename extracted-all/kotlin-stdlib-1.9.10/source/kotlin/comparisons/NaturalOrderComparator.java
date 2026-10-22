package kotlin.comparisons

import java.util.Comparator

private object NaturalOrderComparator private constructor() : Comparator<java.lang.Comparable<? super Object>> {
   @JvmStatic
   public NaturalOrderComparator INSTANCE = new NaturalOrderComparator();


   public open fun compare(a: Comparable<Any>, b: Comparable<Any>): Int {
      return a.compareTo(b);
   }

   public override fun reversed(): Comparator<Comparable<Any>> {
      return ReverseOrderComparator.INSTANCE;
   }
}
