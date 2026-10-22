package kotlin

import kotlin.internal.InlineOnly

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun ULongArray(size: Int, init: (Int) -> ULong): ULongArray {
   var var2: Int = 0;

   var var3: LongArray;
   for (var3 = new long[size]; var2 < size; var2++) {
      var3[var2] = (init.invoke(var2) as ULong).unbox-impl();
   }

   return ULongArray.constructor-impl(var3);
}

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun ulongArrayOf(elements: ULongArray): ULongArray {
   return elements;
}

