package kotlin.collections.unsigned

import java.math.BigDecimal
import java.math.BigInteger
import java.util.RandomAccess
import kotlin.internal.InlineOnly

// $VF: Class flags could not be determined
internal class UArraysKt___UArraysJvmKt {
   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.elementAt(index: Int): UInt {
      return UIntArray.get-pVg5ArA(`$this$elementAt_u2dqFRl0hI`, index);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.elementAt(index: Int): ULong {
      return ULongArray.get-s-VKNKU(`$this$elementAt_u2dr7IrZao`, index);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.elementAt(index: Int): UByte {
      return UByteArray.get-w2LRezQ(`$this$elementAt_u2dPpDY95g`, index);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.elementAt(index: Int): UShort {
      return UShortArray.get-Mh2AYeg(`$this$elementAt_u2dnggk6HY`, index);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.asList(): List<UInt> {
      return new RandomAccess(`$this$asList_u2d_u2dajY_u2d9A`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return UIntArray.getSize-impl(this.$this_asList);
         }

         @Override
         public boolean isEmpty() {
            return UIntArray.isEmpty-impl(this.$this_asList);
         }

         public boolean contains_WZ4Q5Ns/* $VF was: contains-WZ4Q5Ns*/(int element) {
            return UIntArray.contains-WZ4Q5Ns(this.$this_asList, element);
         }

         public int get_pVg5ArA/* $VF was: get-pVg5ArA*/(int index) {
            return UIntArray.get-pVg5ArA(this.$this_asList, index);
         }

         public int indexOf_WZ4Q5Ns/* $VF was: indexOf-WZ4Q5Ns*/(int element) {
            return ArraysKt.indexOf(this.$this_asList, element);
         }

         public int lastIndexOf_WZ4Q5Ns/* $VF was: lastIndexOf-WZ4Q5Ns*/(int element) {
            return ArraysKt.lastIndexOf(this.$this_asList, element);
         }
      };
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.asList(): List<ULong> {
      return new RandomAccess(`$this$asList_u2dQwZRm1k`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return ULongArray.getSize-impl(this.$this_asList);
         }

         @Override
         public boolean isEmpty() {
            return ULongArray.isEmpty-impl(this.$this_asList);
         }

         public boolean contains_VKZWuLQ/* $VF was: contains-VKZWuLQ*/(long element) {
            return ULongArray.contains-VKZWuLQ(this.$this_asList, element);
         }

         public long get_s_VKNKU/* $VF was: get-s-VKNKU*/(int index) {
            return ULongArray.get-s-VKNKU(this.$this_asList, index);
         }

         public int indexOf_VKZWuLQ/* $VF was: indexOf-VKZWuLQ*/(long element) {
            return ArraysKt.indexOf(this.$this_asList, element);
         }

         public int lastIndexOf_VKZWuLQ/* $VF was: lastIndexOf-VKZWuLQ*/(long element) {
            return ArraysKt.lastIndexOf(this.$this_asList, element);
         }
      };
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.asList(): List<UByte> {
      return new RandomAccess(`$this$asList_u2dGBYM_sE`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return UByteArray.getSize-impl(this.$this_asList);
         }

         @Override
         public boolean isEmpty() {
            return UByteArray.isEmpty-impl(this.$this_asList);
         }

         public boolean contains_7apg3OU/* $VF was: contains-7apg3OU*/(byte element) {
            return UByteArray.contains-7apg3OU(this.$this_asList, element);
         }

         public byte get_w2LRezQ/* $VF was: get-w2LRezQ*/(int index) {
            return UByteArray.get-w2LRezQ(this.$this_asList, index);
         }

         public int indexOf_7apg3OU/* $VF was: indexOf-7apg3OU*/(byte element) {
            return ArraysKt.indexOf(this.$this_asList, element);
         }

         public int lastIndexOf_7apg3OU/* $VF was: lastIndexOf-7apg3OU*/(byte element) {
            return ArraysKt.lastIndexOf(this.$this_asList, element);
         }
      };
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.asList(): List<UShort> {
      return new RandomAccess(`$this$asList_u2drL5Bavg`) {
         {
            this.$this_asList = `$receiver`;
         }

         @Override
         public int getSize() {
            return UShortArray.getSize-impl(this.$this_asList);
         }

         @Override
         public boolean isEmpty() {
            return UShortArray.isEmpty-impl(this.$this_asList);
         }

         public boolean contains_xj2QHRw/* $VF was: contains-xj2QHRw*/(short element) {
            return UShortArray.contains-xj2QHRw(this.$this_asList, element);
         }

         public short get_Mh2AYeg/* $VF was: get-Mh2AYeg*/(int index) {
            return UShortArray.get-Mh2AYeg(this.$this_asList, index);
         }

         public int indexOf_xj2QHRw/* $VF was: indexOf-xj2QHRw*/(short element) {
            return ArraysKt.indexOf(this.$this_asList, element);
         }

         public int lastIndexOf_xj2QHRw/* $VF was: lastIndexOf-xj2QHRw*/(short element) {
            return ArraysKt.lastIndexOf(this.$this_asList, element);
         }
      };
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UIntArray.binarySearch(element: UInt, fromIndex: Int, toIndex: Int): Int {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, UIntArray.getSize-impl(`$this$binarySearch_u2d2fe2U9s`));
      var signedElement: Int = element;
      var low: Int = fromIndex;
      var high: Int = toIndex - 1;

      while (low <= high) {
         var mid: Int = low + high ushr 1;
         var cmp: Int = UnsignedKt.uintCompare(`$this$binarySearch_u2d2fe2U9s`[low + high ushr 1], signedElement);
         if (cmp < 0) {
            low = mid + 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun ULongArray.binarySearch(element: ULong, fromIndex: Int, toIndex: Int): Int {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, ULongArray.getSize-impl(`$this$binarySearch_u2dK6DWlUc`));
      var signedElement: Long = element;
      var low: Int = fromIndex;
      var high: Int = toIndex - 1;

      while (low <= high) {
         var mid: Int = low + high ushr 1;
         var cmp: Int = UnsignedKt.ulongCompare(`$this$binarySearch_u2dK6DWlUc`[low + high ushr 1], signedElement);
         if (cmp < 0) {
            low = mid + 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UByteArray.binarySearch(element: UByte, fromIndex: Int, toIndex: Int): Int {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, UByteArray.getSize-impl(`$this$binarySearch_u2dWpHrYlw`));
      var signedElement: Int = element and 255;
      var low: Int = fromIndex;
      var high: Int = toIndex - 1;

      while (low <= high) {
         var mid: Int = low + high ushr 1;
         var cmp: Int = UnsignedKt.uintCompare(`$this$binarySearch_u2dWpHrYlw`[low + high ushr 1], signedElement);
         if (cmp < 0) {
            low = mid + 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   @SinceKotlin(version = "1.3")
   @ExperimentalUnsignedTypes
   @JvmStatic
   public fun UShortArray.binarySearch(element: UShort, fromIndex: Int, toIndex: Int): Int {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, UShortArray.getSize-impl(`$this$binarySearch_u2dEtDCXyQ`));
      var signedElement: Int = element and '\uffff';
      var low: Int = fromIndex;
      var high: Int = toIndex - 1;

      while (low <= high) {
         var mid: Int = low + high ushr 1;
         var cmp: Int = UnsignedKt.uintCompare(`$this$binarySearch_u2dEtDCXyQ`[low + high ushr 1], signedElement);
         if (cmp < 0) {
            low = mid + 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            high = mid - 1;
         }
      }

      return -(low + 1);
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.sumOf(selector: (UInt) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$sumOf_u2djgv0xPQ); var3 < var4; var3++) {
         var10000 = sum.add(selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$sumOf_u2djgv0xPQ`, var3))) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.sumOf(selector: (ULong) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$sumOf_u2dMShoTSo); var3 < var4; var3++) {
         var10000 = sum.add(selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$sumOf_u2dMShoTSo`, var3))) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.sumOf(selector: (UByte) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$sumOf_u2dJOV_ifY); var3 < var4; var3++) {
         var10000 = sum.add(selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$sumOf_u2dJOV_ifY`, var3))) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigDecimal")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.sumOf(selector: (UShort) -> BigDecimal): BigDecimal {
      var var10000: BigDecimal = BigDecimal.valueOf(0L);
      var sum: BigDecimal = var10000;
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$sumOf_u2dxTcfx_M); var3 < var4; var3++) {
         var10000 = sum.add(selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$sumOf_u2dxTcfx_M`, var3))) as BigDecimal);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UIntArray.sumOf(selector: (UInt) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;
      var var3: Int = 0;

      for (int var4 = UIntArray.getSize-impl($this$sumOf_u2djgv0xPQ); var3 < var4; var3++) {
         var10000 = sum.add(selector.invoke(UInt.box-impl(UIntArray.get-pVg5ArA(`$this$sumOf_u2djgv0xPQ`, var3))) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun ULongArray.sumOf(selector: (ULong) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;
      var var3: Int = 0;

      for (int var4 = ULongArray.getSize-impl($this$sumOf_u2dMShoTSo); var3 < var4; var3++) {
         var10000 = sum.add(selector.invoke(ULong.box-impl(ULongArray.get-s-VKNKU(`$this$sumOf_u2dMShoTSo`, var3))) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UByteArray.sumOf(selector: (UByte) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;
      var var3: Int = 0;

      for (int var4 = UByteArray.getSize-impl($this$sumOf_u2dJOV_ifY); var3 < var4; var3++) {
         var10000 = sum.add(selector.invoke(UByte.box-impl(UByteArray.get-w2LRezQ(`$this$sumOf_u2dJOV_ifY`, var3))) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   @SinceKotlin(version = "1.4")
   @OverloadResolutionByLambdaReturnType
   @JvmName(name = "sumOfBigInteger")
   @ExperimentalUnsignedTypes
   @InlineOnly
   @JvmStatic
   public inline fun UShortArray.sumOf(selector: (UShort) -> BigInteger): BigInteger {
      var var10000: BigInteger = BigInteger.valueOf(0L);
      var sum: BigInteger = var10000;
      var var3: Int = 0;

      for (int var4 = UShortArray.getSize-impl($this$sumOf_u2dxTcfx_M); var3 < var4; var3++) {
         var10000 = sum.add(selector.invoke(UShort.box-impl(UShortArray.get-Mh2AYeg(`$this$sumOf_u2dxTcfx_M`, var3))) as BigInteger);
         sum = var10000;
      }

      return sum;
   }

   open fun UArraysKt___UArraysJvmKt() {
   }
}
