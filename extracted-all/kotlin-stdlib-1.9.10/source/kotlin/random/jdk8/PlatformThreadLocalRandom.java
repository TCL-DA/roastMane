package kotlin.random.jdk8

import java.util.Random
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.AbstractPlatformRandom

internal class PlatformThreadLocalRandom : AbstractPlatformRandom {
   public open val impl: Random
      public open get() {
         var var10000: ThreadLocalRandom = ThreadLocalRandom.current();
         return var10000;
      }



   public override fun nextInt(from: Int, until: Int): Int {
      return ThreadLocalRandom.current().nextInt(from, until);
   }

   public override fun nextLong(until: Long): Long {
      return ThreadLocalRandom.current().nextLong(until);
   }

   public override fun nextLong(from: Long, until: Long): Long {
      return ThreadLocalRandom.current().nextLong(from, until);
   }

   public override fun nextDouble(until: Double): Double {
      return ThreadLocalRandom.current().nextDouble(until);
   }
}
