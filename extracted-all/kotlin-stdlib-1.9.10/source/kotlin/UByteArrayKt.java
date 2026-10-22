package kotlin

import kotlin.internal.InlineOnly

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun UByteArray(size: Int, init: (Int) -> UByte): UByteArray {
   var var2: Int = 0;

   var var3: ByteArray;
   for (var3 = new byte[size]; var2 < size; var2++) {
      var3[var2] = (init.invoke(var2) as UByte).unbox-impl();
   }

   return UByteArray.constructor-impl(var3);
}

@SinceKotlin(version = "1.3")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun ubyteArrayOf(elements: UByteArray): UByteArray {
   return elements;
}

