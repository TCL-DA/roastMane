package kotlin.collections

import java.nio.charset.Charset
import java.util.Arrays
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nArraysJVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 2 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,61:1\n26#2:62\n*S KotlinDebug\n*F\n+ 1 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n18#1:62\n*E\n"])
// $VF: Class flags could not be determined
internal class ArraysKt__ArraysJVMKt {
   @InlineOnly
   @JvmStatic
   public inline fun ByteArray.toString(charset: Charset): String {
      return new java.lang.String(`$this$toString`, charset);
   }

   @JvmStatic
   internal fun <T> arrayOfNulls(reference: Array<T>, size: Int): Array<T> {
      var var10000: Any = java.lang.reflect.Array.newInstance(reference.getClass().getComponentType(), size);
      return (T[])(var10000 as Object[]);
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun copyOfRangeToIndexCheck(toIndex: Int, size: Int) {
      if (toIndex > size) {
         throw new IndexOutOfBoundsException("toIndex (" + toIndex + ") is greater than size (" + size + ").");
      }
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "contentDeepHashCode")
   @JvmStatic
   internal fun <T> Array<out T>?.contentDeepHashCodeImpl(): Int {
      return Arrays.deepHashCode(`$this$contentDeepHashCodeImpl`);
   }

   open fun ArraysKt__ArraysJVMKt() {
   }
}
