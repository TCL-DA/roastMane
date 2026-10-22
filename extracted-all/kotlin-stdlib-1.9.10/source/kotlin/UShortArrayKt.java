package kotlin

import kotlin.internal.InlineOnly

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun UShortArray(size: Int, init: (Int) -> UShort): UShortArray {
   var var2: Int = 0;

   var var3: ShortArray;
   for (var3 = new short[size]; var2 < size; var2++) {
      var3[var2] = (init.invoke(var2) as UShort).unbox-impl();
   }

   return UShortArray.constructor-impl(var3);
}

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun ushortArrayOf(elements: UShortArray): UShortArray {
   return elements;
}

