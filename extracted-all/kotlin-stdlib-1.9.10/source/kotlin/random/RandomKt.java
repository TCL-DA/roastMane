@file:SourceDebugExtension(["SMAP\nRandom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Random.kt\nkotlin/random/RandomKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,383:1\n1#2:384\n*E\n"])

package kotlin.random

import kotlin.jvm.internal.SourceDebugExtension

@SinceKotlin(version = "1.3")
public fun Random(seed: Int): Random {
   return new XorWowRandom(seed, seed shr 31);
}

@SinceKotlin(version = "1.3")
public fun Random(seed: Long): Random {
   return new XorWowRandom((int)seed, (int)(seed shr 32));
}

@SinceKotlin(version = "1.3")
public fun Random.nextInt(range: IntRange): Int {
   if (range.isEmpty()) {
      throw new IllegalArgumentException("Cannot get random in empty range: " + range);
   } else {
      return if (range.getLast() < Integer.MAX_VALUE)
         `$this$nextInt`.nextInt(range.getFirst(), range.getLast() + 1)
         else
         (if (range.getFirst() > Integer.MIN_VALUE) `$this$nextInt`.nextInt(range.getFirst() - 1, range.getLast()) + 1 else `$this$nextInt`.nextInt());
   }
}

@SinceKotlin(version = "1.3")
public fun Random.nextLong(range: LongRange): Long {
   if (range.isEmpty()) {
      throw new IllegalArgumentException("Cannot get random in empty range: " + range);
   } else {
      return if (range.getLast() < java.lang.Long.MAX_VALUE)
         `$this$nextLong`.nextLong(range.getFirst(), range.getLast() + 1L)
         else
         (
            if (range.getFirst() > java.lang.Long.MIN_VALUE)
               `$this$nextLong`.nextLong(range.getFirst() - 1L, range.getLast()) + 1L
               else
               `$this$nextLong`.nextLong()
         );
   }
}

internal fun fastLog2(value: Int): Int {
   return 31 - Integer.numberOfLeadingZeros(value);
}

internal fun Int.takeUpperBits(bitCount: Int): Int {
   return `$this$takeUpperBits` ushr 32 - bitCount and -bitCount shr 31;
}

internal fun checkRangeBounds(from: Int, until: Int) {
   if (until <= from) {
      throw new IllegalArgumentException(boundsErrorMessage(from, until).toString());
   }
}

internal fun checkRangeBounds(from: Long, until: Long) {
   if (until <= from) {
      throw new IllegalArgumentException(boundsErrorMessage(from, until).toString());
   }
}

internal fun checkRangeBounds(from: Double, until: Double) {
   if (!(until > from)) {
      throw new IllegalArgumentException(boundsErrorMessage(from, until).toString());
   }
}

internal fun boundsErrorMessage(from: Any, until: Any): String {
   return "Random range is empty: [" + from + ", " + until + ").";
}

