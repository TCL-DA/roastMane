package kotlin.collections

// $VF: Class flags could not be determined
internal class UCollectionsKt___UCollectionsKt {
   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun Collection<UByte>.toUByteArray(): UByteArray {
      var result: ByteArray = UByteArray.constructor-impl(`$this$toUByteArray`.size());
      var index: Int = 0;
      var var3: java.util.Iterator = `$this$toUByteArray`.iterator();

      while (var3.hasNext()) {
         UByteArray.set-VurrAj0(result, index++, (var3.next() as UByte).unbox-impl());
      }

      return result;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun Collection<UInt>.toUIntArray(): UIntArray {
      var result: IntArray = UIntArray.constructor-impl(`$this$toUIntArray`.size());
      var index: Int = 0;
      var var3: java.util.Iterator = `$this$toUIntArray`.iterator();

      while (var3.hasNext()) {
         UIntArray.set-VXSXFK8(result, index++, (var3.next() as UInt).unbox-impl());
      }

      return result;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun Collection<ULong>.toULongArray(): ULongArray {
      var result: LongArray = ULongArray.constructor-impl(`$this$toULongArray`.size());
      var index: Int = 0;
      var var3: java.util.Iterator = `$this$toULongArray`.iterator();

      while (var3.hasNext()) {
         ULongArray.set-k8EXiF4(result, index++, (var3.next() as ULong).unbox-impl());
      }

      return result;
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun Collection<UShort>.toUShortArray(): UShortArray {
      var result: ShortArray = UShortArray.constructor-impl(`$this$toUShortArray`.size());
      var index: Int = 0;
      var var3: java.util.Iterator = `$this$toUShortArray`.iterator();

      while (var3.hasNext()) {
         UShortArray.set-01HTLdE(result, index++, (var3.next() as UShort).unbox-impl());
      }

      return result;
   }

   @JvmName(name = "sumOfUInt")
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun Iterable<UInt>.sum(): UInt {
      var sum: Int = 0;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum = UInt.constructor-impl(sum + (var2.next() as UInt).unbox-impl());
      }

      return sum;
   }

   @JvmName(name = "sumOfULong")
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun Iterable<ULong>.sum(): ULong {
      var sum: Long = 0L;
      var var3: java.util.Iterator = `$this$sum`.iterator();

      while (var3.hasNext()) {
         sum = ULong.constructor-impl(sum + (var3.next() as ULong).unbox-impl());
      }

      return sum;
   }

   @JvmName(name = "sumOfUByte")
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun Iterable<UByte>.sum(): UInt {
      var sum: Int = 0;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum = UInt.constructor-impl(sum + UInt.constructor-impl((var2.next() as UByte).unbox-impl() and 255));
      }

      return sum;
   }

   @JvmName(name = "sumOfUShort")
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun Iterable<UShort>.sum(): UInt {
      var sum: Int = 0;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum = UInt.constructor-impl(sum + UInt.constructor-impl((var2.next() as UShort).unbox-impl() and '\uffff'));
      }

      return sum;
   }

   open fun UCollectionsKt___UCollectionsKt() {
   }
}
