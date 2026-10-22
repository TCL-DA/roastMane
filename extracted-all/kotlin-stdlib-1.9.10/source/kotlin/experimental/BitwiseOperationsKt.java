package kotlin.experimental

import kotlin.internal.InlineOnly

@SinceKotlin(version = "1.1")
@InlineOnly
public inline infix fun Byte.and(other: Byte): Byte {
   return (byte)(`$this$and` and other);
}

@SinceKotlin(version = "1.1")
@InlineOnly
public inline infix fun Byte.or(other: Byte): Byte {
   return (byte)(`$this$or` or other);
}

@SinceKotlin(version = "1.1")
@InlineOnly
public inline infix fun Byte.xor(other: Byte): Byte {
   return (byte)(`$this$xor` xor other);
}

@SinceKotlin(version = "1.1")
@InlineOnly
public inline fun Byte.inv(): Byte {
   return (byte)(`$this$inv`.inv());
}

@SinceKotlin(version = "1.1")
@InlineOnly
public inline infix fun Short.and(other: Short): Short {
   return (short)(`$this$and` and other);
}

@SinceKotlin(version = "1.1")
@InlineOnly
public inline infix fun Short.or(other: Short): Short {
   return (short)(`$this$or` or other);
}

@SinceKotlin(version = "1.1")
@InlineOnly
public inline infix fun Short.xor(other: Short): Short {
   return (short)(`$this$xor` xor other);
}

@SinceKotlin(version = "1.1")
@InlineOnly
public inline fun Short.inv(): Short {
   return (short)(`$this$inv`.inv());
}

