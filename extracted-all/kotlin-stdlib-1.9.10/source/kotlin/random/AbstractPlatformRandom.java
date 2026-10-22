package kotlin.random

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nPlatformRandom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PlatformRandom.kt\nkotlin/random/AbstractPlatformRandom\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,93:1\n1#2:94\n*E\n"])
internal abstract class AbstractPlatformRandom : Random {
   public abstract val impl: java.util.Random

   public override fun nextBits(bitCount: Int): Int {
      return RandomKt.takeUpperBits(this.getImpl().nextInt(), bitCount);
   }

   public override fun nextInt(): Int {
      return this.getImpl().nextInt();
   }

   public override fun nextInt(until: Int): Int {
      return this.getImpl().nextInt(until);
   }

   public override fun nextLong(): Long {
      return this.getImpl().nextLong();
   }

   public override fun nextBoolean(): Boolean {
      return this.getImpl().nextBoolean();
   }

   public override fun nextDouble(): Double {
      return this.getImpl().nextDouble();
   }

   public override fun nextFloat(): Float {
      return this.getImpl().nextFloat();
   }

   public override fun nextBytes(array: ByteArray): ByteArray {
      this.getImpl().nextBytes(array);
      return array;
   }
}
