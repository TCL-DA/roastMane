package kotlin.sequences

// $VF: Class flags could not be determined
internal class USequencesKt___USequencesKt {
   @JvmName(name = "sumOfUInt")
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun Sequence<UInt>.sum(): UInt {
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
   public fun Sequence<ULong>.sum(): ULong {
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
   public fun Sequence<UByte>.sum(): UInt {
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
   public fun Sequence<UShort>.sum(): UInt {
      var sum: Int = 0;
      var var2: java.util.Iterator = `$this$sum`.iterator();

      while (var2.hasNext()) {
         sum = UInt.constructor-impl(sum + UInt.constructor-impl((var2.next() as UShort).unbox-impl() and '\uffff'));
      }

      return sum;
   }

   open fun USequencesKt___USequencesKt() {
   }
}
