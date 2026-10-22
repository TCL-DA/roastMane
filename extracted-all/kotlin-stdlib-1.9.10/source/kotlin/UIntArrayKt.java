package kotlin

import kotlin.internal.InlineOnly

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun UIntArray(size: Int, init: (Int) -> UInt): UIntArray {
   var var2: Int = 0;

   var var3: IntArray;
   for (var3 = new int[size]; var2 < size; var2++) {
      var3[var2] = (init.invoke(var2) as UInt).unbox-impl();
   }

   return UIntArray.constructor-impl(var3);
}

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun uintArrayOf(elements: UIntArray): UIntArray {
   return elements;
}

