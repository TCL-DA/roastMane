package kotlin.random

import kotlin.internal.InlineOnly
import kotlin.internal.PlatformImplementationsKt

@SinceKotlin(version = "1.3")
public fun Random.asJavaRandom(): java.util.Random {
   var var10000: AbstractPlatformRandom = `$this$asJavaRandom` as? AbstractPlatformRandom;
   if ((`$this$asJavaRandom` as? AbstractPlatformRandom) != null) {
      var var1: java.util.Random = var10000.getImpl();
      if (var1 != null) {
         return var1;
      }
   }

   return new KotlinRandom(`$this$asJavaRandom`);
}

@SinceKotlin(version = "1.3")
public fun java.util.Random.asKotlinRandom(): Random {
   var var10000: KotlinRandom = `$this$asKotlinRandom` as? KotlinRandom;
   if ((`$this$asKotlinRandom` as? KotlinRandom) != null) {
      var var1: Random = var10000.getImpl();
      if (var1 != null) {
         return var1;
      }
   }

   return new PlatformRandom(`$this$asKotlinRandom`);
}

@InlineOnly
internal inline fun defaultPlatformRandom(): Random {
   return PlatformImplementationsKt.IMPLEMENTATIONS.defaultPlatformRandom();
}

internal fun doubleFromParts(hi26: Int, low27: Int): Double {
   return (double)(((long)hi26 shl 27) + (long)low27) / 9.007199E15F;
}

