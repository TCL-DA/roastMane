@file:JvmName(name = "ConsoleKt")

package kotlin.io

import java.io.InputStream
import java.nio.charset.Charset
import kotlin.internal.InlineOnly

@InlineOnly
public inline fun print(message: Any?) {
   System.out.print(message);
}

@InlineOnly
public inline fun print(message: Int) {
   System.out.print(message);
}

@InlineOnly
public inline fun print(message: Long) {
   System.out.print(message);
}

@InlineOnly
public inline fun print(message: Byte) {
   System.out.print(java.lang.Byte.valueOf(message));
}

@InlineOnly
public inline fun print(message: Short) {
   System.out.print(java.lang.Short.valueOf(message));
}

@InlineOnly
public inline fun print(message: Char) {
   System.out.print(message);
}

@InlineOnly
public inline fun print(message: Boolean) {
   System.out.print(message);
}

@InlineOnly
public inline fun print(message: Float) {
   System.out.print(message);
}

@InlineOnly
public inline fun print(message: Double) {
   System.out.print(message);
}

@InlineOnly
public inline fun print(message: CharArray) {
   System.out.print(message);
}

@InlineOnly
public inline fun println(message: Any?) {
   System.out.println(message);
}

@InlineOnly
public inline fun println(message: Int) {
   System.out.println(message);
}

@InlineOnly
public inline fun println(message: Long) {
   System.out.println(message);
}

@InlineOnly
public inline fun println(message: Byte) {
   System.out.println(java.lang.Byte.valueOf(message));
}

@InlineOnly
public inline fun println(message: Short) {
   System.out.println(java.lang.Short.valueOf(message));
}

@InlineOnly
public inline fun println(message: Char) {
   System.out.println(message);
}

@InlineOnly
public inline fun println(message: Boolean) {
   System.out.println(message);
}

@InlineOnly
public inline fun println(message: Float) {
   System.out.println(message);
}

@InlineOnly
public inline fun println(message: Double) {
   System.out.println(message);
}

@InlineOnly
public inline fun println(message: CharArray) {
   System.out.println(message);
}

@InlineOnly
public inline fun println() {
   System.out.println();
}

@SinceKotlin(version = "1.6")
public fun readln(): String {
   var var10000: java.lang.String = readlnOrNull();
   if (var10000 == null) {
      throw new ReadAfterEOFException("EOF has already been reached");
   } else {
      return var10000;
   }
}

@SinceKotlin(version = "1.6")
public fun readlnOrNull(): String? {
   return readLine();
}

public fun readLine(): String? {
   var var10000: LineReader = LineReader.INSTANCE;
   var var10001: InputStream = System.in;
   var var10002: Charset = Charset.defaultCharset();
   return var10000.readLine(var10001, var10002);
}

