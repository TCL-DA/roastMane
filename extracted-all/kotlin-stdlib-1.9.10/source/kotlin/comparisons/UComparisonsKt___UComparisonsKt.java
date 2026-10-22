package kotlin.comparisons

import kotlin.internal.InlineOnly
import kotlin.jvm.internal.Intrinsics

// $VF: Class flags could not be determined
internal class UComparisonsKt___UComparisonsKt {
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun maxOf(a: UInt, b: UInt): UInt {
      return if (Integer.compareUnsigned(a, b) >= 0) a else b;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun maxOf(a: ULong, b: ULong): ULong {
      return if (java.lang.Long.compareUnsigned(a, b) >= 0) a else b;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun maxOf(a: UByte, b: UByte): UByte {
      return if (Intrinsics.compare(a and 255, b and 255) >= 0) a else b;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun maxOf(a: UShort, b: UShort): UShort {
      return if (Intrinsics.compare(a and '\uffff', b and '\uffff') >= 0) a else b;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun maxOf(a: UInt, b: UInt, c: UInt): UInt {
      return UComparisonsKt.maxOf-J1ME1BU(a, UComparisonsKt.maxOf-J1ME1BU(b, c));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun maxOf(a: ULong, b: ULong, c: ULong): ULong {
      return UComparisonsKt.maxOf-eb3DHEI(a, UComparisonsKt.maxOf-eb3DHEI(b, c));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun maxOf(a: UByte, b: UByte, c: UByte): UByte {
      return UComparisonsKt.maxOf-Kr8caGY(a, UComparisonsKt.maxOf-Kr8caGY(b, c));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun maxOf(a: UShort, b: UShort, c: UShort): UShort {
      return UComparisonsKt.maxOf-5PvTz6A(a, UComparisonsKt.maxOf-5PvTz6A(b, c));
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun maxOf(a: UInt, other: UIntArray): UInt {
      var max: Int = a;
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl(other); var3 < var4; var3++) {
         max = UComparisonsKt.maxOf-J1ME1BU(max, UIntArray.get-pVg5ArA(other, var3));
      }

      return max;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun maxOf(a: ULong, other: ULongArray): ULong {
      var max: Long = a;
      var var5: Int = 0;

      for (int var6 = ULongArray.getSize-impl(other); var5 < var6; var5++) {
         max = UComparisonsKt.maxOf-eb3DHEI(max, ULongArray.get-s-VKNKU(other, var5));
      }

      return max;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun maxOf(a: UByte, other: UByteArray): UByte {
      var max: Byte = a;
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl(other); var3 < var4; var3++) {
         max = UComparisonsKt.maxOf-Kr8caGY(max, UByteArray.get-w2LRezQ(other, var3));
      }

      return max;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun maxOf(a: UShort, other: UShortArray): UShort {
      var max: Short = a;
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl(other); var3 < var4; var3++) {
         max = UComparisonsKt.maxOf-5PvTz6A(max, UShortArray.get-Mh2AYeg(other, var3));
      }

      return max;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun minOf(a: UInt, b: UInt): UInt {
      return if (Integer.compareUnsigned(a, b) <= 0) a else b;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun minOf(a: ULong, b: ULong): ULong {
      return if (java.lang.Long.compareUnsigned(a, b) <= 0) a else b;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun minOf(a: UByte, b: UByte): UByte {
      return if (Intrinsics.compare(a and 255, b and 255) <= 0) a else b;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun minOf(a: UShort, b: UShort): UShort {
      return if (Intrinsics.compare(a and '\uffff', b and '\uffff') <= 0) a else b;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun minOf(a: UInt, b: UInt, c: UInt): UInt {
      return UComparisonsKt.minOf-J1ME1BU(a, UComparisonsKt.minOf-J1ME1BU(b, c));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun minOf(a: ULong, b: ULong, c: ULong): ULong {
      return UComparisonsKt.minOf-eb3DHEI(a, UComparisonsKt.minOf-eb3DHEI(b, c));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun minOf(a: UByte, b: UByte, c: UByte): UByte {
      return UComparisonsKt.minOf-Kr8caGY(a, UComparisonsKt.minOf-Kr8caGY(b, c));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun minOf(a: UShort, b: UShort, c: UShort): UShort {
      return UComparisonsKt.minOf-5PvTz6A(a, UComparisonsKt.minOf-5PvTz6A(b, c));
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun minOf(a: UInt, other: UIntArray): UInt {
      var min: Int = a;
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl(other); var3 < var4; var3++) {
         min = UComparisonsKt.minOf-J1ME1BU(min, UIntArray.get-pVg5ArA(other, var3));
      }

      return min;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun minOf(a: ULong, other: ULongArray): ULong {
      var min: Long = a;
      var var5: Int = 0;

      for (int var6 = ULongArray.getSize-impl(other); var5 < var6; var5++) {
         min = UComparisonsKt.minOf-eb3DHEI(min, ULongArray.get-s-VKNKU(other, var5));
      }

      return min;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun minOf(a: UByte, other: UByteArray): UByte {
      var min: Byte = a;
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl(other); var3 < var4; var3++) {
         min = UComparisonsKt.minOf-Kr8caGY(min, UByteArray.get-w2LRezQ(other, var3));
      }

      return min;
   }

   @SinceKotlin(version = "1.4")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun minOf(a: UShort, other: UShortArray): UShort {
      var min: Short = a;
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl(other); var3 < var4; var3++) {
         min = UComparisonsKt.minOf-5PvTz6A(min, UShortArray.get-Mh2AYeg(other, var3));
      }

      return min;
   }

   open fun UComparisonsKt___UComparisonsKt() {
   }
}
