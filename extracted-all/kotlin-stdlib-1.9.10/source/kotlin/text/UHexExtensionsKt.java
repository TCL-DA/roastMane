package kotlin.text

import kotlin.internal.InlineOnly

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun UByteArray.toHexString(format: HexFormat): String {
   return HexExtensionsKt.toHexString(`$this$toHexString_u2dzHuV2wU`, format);
}

@JvmSynthetic
fun ByteArray.`toHexString-zHuV2wU$default`(format: HexFormat, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return HexExtensionsKt.toHexString(`$this$toHexString_u2dzHuV2wU_u24default`, format);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun UByteArray.toHexString(startIndex: Int, endIndex: Int, format: HexFormat): String {
   return HexExtensionsKt.toHexString(`$this$toHexString_u2dlZCiFrA`, startIndex, endIndex, format);
}

@JvmSynthetic
fun ByteArray.`toHexString-lZCiFrA$default`(startIndex: Int, endIndex: Int, format: HexFormat, var4: Int, var5: Any): java.lang.String {
   if ((var4 and 1) != 0) {
      startIndex = 0;
   }

   if ((var4 and 2) != 0) {
      endIndex = UByteArray.getSize-impl(`$this$toHexString_u2dlZCiFrA_u24default`);
   }

   if ((var4 and 4) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return HexExtensionsKt.toHexString(`$this$toHexString_u2dlZCiFrA_u24default`, startIndex, endIndex, format);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@ExperimentalUnsignedTypes
@InlineOnly
public inline fun String.hexToUByteArray(format: HexFormat): UByteArray {
   return UByteArray.constructor-impl(HexExtensionsKt.hexToByteArray(`$this$hexToUByteArray`, format));
}

@JvmSynthetic
fun java.lang.String.`hexToUByteArray$default`(format: HexFormat, var2: Int, var3: Any): ByteArray {
   if ((var2 and 1) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return UByteArray.constructor-impl(HexExtensionsKt.hexToByteArray(`$this$hexToUByteArray_u24default`, format));
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@InlineOnly
public inline fun UByte.toHexString(format: HexFormat): String {
   return HexExtensionsKt.toHexString(`$this$toHexString_u2dZQbaR00`, format);
}

@JvmSynthetic
fun Byte.`toHexString-ZQbaR00$default`(format: HexFormat, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return HexExtensionsKt.toHexString(`$this$toHexString_u2dZQbaR00_u24default`, format);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@InlineOnly
public inline fun String.hexToUByte(format: HexFormat): UByte {
   return UByte.constructor-impl(HexExtensionsKt.hexToByte(`$this$hexToUByte`, format));
}

@JvmSynthetic
fun java.lang.String.`hexToUByte$default`(format: HexFormat, var2: Int, var3: Any): Byte {
   if ((var2 and 1) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return UByte.constructor-impl(HexExtensionsKt.hexToByte(`$this$hexToUByte_u24default`, format));
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@InlineOnly
public inline fun UShort.toHexString(format: HexFormat): String {
   return HexExtensionsKt.toHexString(`$this$toHexString_u2dr3ox_E0`, format);
}

@JvmSynthetic
fun Short.`toHexString-r3ox_E0$default`(format: HexFormat, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return HexExtensionsKt.toHexString(`$this$toHexString_u2dr3ox_E0_u24default`, format);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@InlineOnly
public inline fun String.hexToUShort(format: HexFormat): UShort {
   return UShort.constructor-impl(HexExtensionsKt.hexToShort(`$this$hexToUShort`, format));
}

@JvmSynthetic
fun java.lang.String.`hexToUShort$default`(format: HexFormat, var2: Int, var3: Any): Short {
   if ((var2 and 1) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return UShort.constructor-impl(HexExtensionsKt.hexToShort(`$this$hexToUShort_u24default`, format));
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@InlineOnly
public inline fun UInt.toHexString(format: HexFormat): String {
   return HexExtensionsKt.toHexString(`$this$toHexString_u2d8M7LxHw`, format);
}

@JvmSynthetic
fun Int.`toHexString-8M7LxHw$default`(format: HexFormat, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return HexExtensionsKt.toHexString(`$this$toHexString_u2d8M7LxHw_u24default`, format);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@InlineOnly
public inline fun String.hexToUInt(format: HexFormat): UInt {
   return UInt.constructor-impl(HexExtensionsKt.hexToInt(`$this$hexToUInt`, format));
}

@JvmSynthetic
fun java.lang.String.`hexToUInt$default`(format: HexFormat, var2: Int, var3: Any): Int {
   if ((var2 and 1) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return UInt.constructor-impl(HexExtensionsKt.hexToInt(`$this$hexToUInt_u24default`, format));
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@InlineOnly
public inline fun ULong.toHexString(format: HexFormat): String {
   return HexExtensionsKt.toHexString(`$this$toHexString_u2d8UJCm_u2dI`, format);
}

@JvmSynthetic
fun Long.`toHexString-8UJCm-I$default`(format: HexFormat, var3: Int, var4: Any): java.lang.String {
   if ((var3 and 1) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return HexExtensionsKt.toHexString(`$this$toHexString_u2d8UJCm_u2dI_u24default`, format);
}

@ExperimentalStdlibApi
@SinceKotlin(version = "1.9")
@InlineOnly
public inline fun String.hexToULong(format: HexFormat): ULong {
   return ULong.constructor-impl(HexExtensionsKt.hexToLong(`$this$hexToULong`, format));
}

@JvmSynthetic
fun java.lang.String.`hexToULong$default`(format: HexFormat, var2: Int, var3: Any): Long {
   if ((var2 and 1) != 0) {
      format = HexFormat.Companion.getDefault();
   }

   return ULong.constructor-impl(HexExtensionsKt.hexToLong(`$this$hexToULong_u24default`, format));
}

