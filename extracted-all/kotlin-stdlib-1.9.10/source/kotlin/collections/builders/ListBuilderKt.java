@file:SourceDebugExtension(["SMAP\nListBuilder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ListBuilder.kt\nkotlin/collections/builders/ListBuilderKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,432:1\n1#2:433\n*E\n"])

package kotlin.collections.builders

import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension

internal fun <E> arrayOfUninitializedElements(size: Int): Array<E> {
   if (size < 0) {
      throw new IllegalArgumentException("capacity must be non-negative.".toString());
   } else {
      return (E[])(new Object[size]);
   }
}

private fun <T> Array<out T>.subarrayContentToString(offset: Int, length: Int): String {
   var sb: StringBuilder = new StringBuilder(2 + length * 3);
   sb.append("[");

   for (int i = 0; i < length; i++) {
      if (i > 0) {
         sb.append(", ");
      }

      sb.append(`$this$subarrayContentToString`[offset + i]);
   }

   sb.append("]");
   var var10000: java.lang.String = sb.toString();
   return var10000;
}

private fun <T> Array<T>.subarrayContentHashCode(offset: Int, length: Int): Int {
   var result: Int = 1;

   for (int i = 0; i < length; i++) {
      result = result * 31 + (if (`$this$subarrayContentHashCode`[offset + i] != null) `$this$subarrayContentHashCode`[offset + i].hashCode() else 0);
   }

   return result;
}

private fun <T> Array<T>.subarrayContentEquals(offset: Int, length: Int, other: List<*>): Boolean {
   if (length != other.size()) {
      return false;
   } else {
      for (int i = 0; i < length; i++) {
         if (!(`$this$subarrayContentEquals`[offset + i] == other.get(i))) {
            return false;
         }
      }

      return true;
   }
}

internal fun <T> Array<T>.copyOfUninitializedElements(newSize: Int): Array<T> {
   var var10000: Array<Any> = Arrays.copyOf(`$this$copyOfUninitializedElements`, newSize);
   return (T[])var10000;
}

internal fun <E> Array<E>.resetAt(index: Int) {
   `$this$resetAt`[index] = null;
}

internal fun <E> Array<E>.resetRange(fromIndex: Int, toIndex: Int) {
   for (int index = fromIndex; index < toIndex; index++) {
      resetAt(`$this$resetRange`, index);
   }
}

@JvmSynthetic
fun `access$subarrayContentHashCode`(`$receiver`: Array<Any>, offset: Int, length: Int): Int {
   return subarrayContentHashCode(`$receiver`, offset, length);
}

@JvmSynthetic
fun `access$subarrayContentToString`(`$receiver`: Array<Any>, offset: Int, length: Int): java.lang.String {
   return subarrayContentToString(`$receiver`, offset, length);
}

@JvmSynthetic
fun `access$subarrayContentEquals`(`$receiver`: Array<Any>, offset: Int, length: Int, other: java.util.List): Boolean {
   return subarrayContentEquals(`$receiver`, offset, length, other);
}

