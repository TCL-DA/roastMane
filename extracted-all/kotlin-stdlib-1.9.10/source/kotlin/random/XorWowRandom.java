package kotlin.random

import java.io.Serializable
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nXorWowRandom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 XorWowRandom.kt\nkotlin/random/XorWowRandom\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,60:1\n1#2:61\n*E\n"])
internal class XorWowRandom internal constructor(x: Int, y: Int, z: Int, w: Int, v: Int, addend: Int) : Random, Serializable {
   @JvmStatic
   private XorWowRandom.Companion Companion = new XorWowRandom.Companion(null);
   @JvmStatic
   private long serialVersionUID = 0L;

   private final var addend: Int
   private final var v: Int
   private final var w: Int
   private final var x: Int
   private final var y: Int
   private final var z: Int

   init {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
      this.v = v;
      this.addend = addend;
      if ((this.x or this.y or this.z or this.w or this.v) == 0) {
         throw new IllegalArgumentException("Initial state must have at least one non-zero element.".toString());
      } else {
         var var11: Byte = 64;

         for (int var8 = 0; var8 < var11; var8++) {
            this.nextInt();
         }
      }
   }

   internal constructor(seed1: Int, seed2: Int) : this(seed1, seed2, 0, 0, seed1.inv(), seed1 shl 10 xor seed2 ushr 4)
   public override fun nextInt(): Int {
      var var3: Int = this.x xor this.x ushr 2;
      this.x = this.y;
      this.y = this.z;
      this.z = this.w;
      var v0: Int = this.v;
      this.w = this.v;
      var var4: Int = var3 xor var3 shl 1 xor v0 xor v0 shl 4;
      this.v = var3 xor var3 shl 1 xor v0 xor v0 shl 4;
      this.addend += 362437;
      return var4 + this.addend;
   }

   public override fun nextBits(bitCount: Int): Int {
      return RandomKt.takeUpperBits(this.nextInt(), bitCount);
   }

   private companion object Companion private constructor() {
      private const val serialVersionUID: Long

   }
}
