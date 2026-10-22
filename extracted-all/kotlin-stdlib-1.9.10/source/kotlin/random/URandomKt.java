@file:SourceDebugExtension(["SMAP\nURandom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 URandom.kt\nkotlin/random/URandomKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,155:1\n1#2:156\n*E\n"])

package kotlin.random

import kotlin.jvm.internal.SourceDebugExtension

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
public fun Random.nextUInt(): UInt {
   return UInt.constructor-impl(`$this$nextUInt`.nextInt());
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
public fun Random.nextUInt(until: UInt): UInt {
   return nextUInt-a8DCA5k(`$this$nextUInt_u2dqCasIEU`, 0, until);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
public fun Random.nextUInt(from: UInt, until: UInt): UInt {
   checkUIntRangeBounds-J1ME1BU(from, until);
   return UInt.constructor-impl(`$this$nextUInt_u2da8DCA5k`.nextInt(from xor Integer.MIN_VALUE, until xor Integer.MIN_VALUE) xor Integer.MIN_VALUE);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
public fun Random.nextUInt(range: UIntRange): UInt {
   if (range.isEmpty()) {
      throw new IllegalArgumentException("Cannot get random in empty range: " + range);
   } else {
      return if (Integer.compareUnsigned(range.getLast-pVg5ArA(), -1) < 0)
         nextUInt-a8DCA5k(`$this$nextUInt`, range.getFirst-pVg5ArA(), UInt.constructor-impl(range.getLast-pVg5ArA() + 1))
         else
         (
            if (Integer.compareUnsigned(range.getFirst-pVg5ArA(), 0) > 0)
               UInt.constructor-impl(nextUInt-a8DCA5k(`$this$nextUInt`, UInt.constructor-impl(range.getFirst-pVg5ArA() - 1), range.getLast-pVg5ArA()) + 1)
               else
               nextUInt(`$this$nextUInt`)
         );
   }
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
public fun Random.nextULong(): ULong {
   return ULong.constructor-impl(`$this$nextULong`.nextLong());
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
public fun Random.nextULong(until: ULong): ULong {
   return nextULong-jmpaW-c(`$this$nextULong_u2dV1Xi4fY`, 0L, until);
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
public fun Random.nextULong(from: ULong, until: ULong): ULong {
   checkULongRangeBounds-eb3DHEI(from, until);
   return ULong.constructor-impl(
      `$this$nextULong_u2djmpaW_u2dc`.nextLong(from xor java.lang.Long.MIN_VALUE, until xor java.lang.Long.MIN_VALUE) xor java.lang.Long.MIN_VALUE
   );
}

@SinceKotlin(version = "1.5")
@WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
public fun Random.nextULong(range: ULongRange): ULong {
   if (range.isEmpty()) {
      throw new IllegalArgumentException("Cannot get random in empty range: " + range);
   } else {
      return if (java.lang.Long.compareUnsigned(range.getLast-s-VKNKU(), -1L) < 0)
         nextULong-jmpaW-c(
            `$this$nextULong`, range.getFirst-s-VKNKU(), ULong.constructor-impl(range.getLast-s-VKNKU() + ULong.constructor-impl((long)1 and 4294967295L))
         )
         else
         (
            if (java.lang.Long.compareUnsigned(range.getFirst-s-VKNKU(), 0L) > 0)
               ULong.constructor-impl(
                  nextULong-jmpaW-c(
                        `$this$nextULong`,
                        ULong.constructor-impl(range.getFirst-s-VKNKU() - ULong.constructor-impl((long)1 and 4294967295L)),
                        range.getLast-s-VKNKU()
                     )
                     + ULong.constructor-impl((long)1 and 4294967295L)
               )
               else
               nextULong(`$this$nextULong`)
         );
   }
}

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
public fun Random.nextUBytes(array: UByteArray): UByteArray {
   `$this$nextUBytes_u2dEVgfTAA`.nextBytes(array);
   return array;
}

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
public fun Random.nextUBytes(size: Int): UByteArray {
   return UByteArray.constructor-impl(`$this$nextUBytes`.nextBytes(size));
}

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
public fun Random.nextUBytes(array: UByteArray, fromIndex: Int, toIndex: Int): UByteArray {
   `$this$nextUBytes_u2dWvrt4B4`.nextBytes(array, fromIndex, toIndex);
   return array;
}

@JvmSynthetic
fun `nextUBytes-Wvrt4B4$default`(var0: Random, var1: ByteArray, var2: Int, var3: Int, var4: Int, var5: Any): ByteArray {
   if ((var4 and 2) != 0) {
      var2 = 0;
   }

   if ((var4 and 4) != 0) {
      var3 = UByteArray.getSize-impl(var1);
   }

   return nextUBytes-Wvrt4B4(var0, var1, var2, var3);
}

internal fun checkUIntRangeBounds(from: UInt, until: UInt) {
   if (Integer.compareUnsigned(until, from) <= 0) {
      throw new IllegalArgumentException(RandomKt.boundsErrorMessage(UInt.box-impl(from), UInt.box-impl(until)).toString());
   }
}

internal fun checkULongRangeBounds(from: ULong, until: ULong) {
   if (java.lang.Long.compareUnsigned(until, from) <= 0) {
      throw new IllegalArgumentException(RandomKt.boundsErrorMessage(ULong.box-impl(from), ULong.box-impl(until)).toString());
   }
}

