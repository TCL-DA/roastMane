package kotlin.collections

import java.util.ArrayList
import java.util.Arrays
import kotlin.collections.unsigned.UArraysKt
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nArrays.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Arrays.kt\nkotlin/collections/ArraysKt__ArraysKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,161:1\n1#2:162\n*E\n"])
// $VF: Class flags could not be determined
internal class ArraysKt__ArraysKt : ArraysKt__ArraysJVMKt {
   @JvmStatic
   public fun <T> Array<out Array<out T>>.flatten(): List<T> {
      var var2: Array<Any> = `$this$flatten` as Object[];
      var var3: Int = 0;

      for (Object var6 : var2) {
         var3 += (var6 as Object[]).length;
      }

      var result: ArrayList = new ArrayList(var3);

      for (Object[] element : (Object[])$this$flatten) {
         CollectionsKt.addAll(result, var14);
      }

      return result;
   }

   @JvmStatic
   public fun <T, R> Array<out Pair<T, R>>.unzip(): Pair<List<T>, List<R>> {
      var listT: ArrayList = new ArrayList(`$this$unzip`.length);
      var listR: ArrayList = new ArrayList(`$this$unzip`.length);

      for (Pair pair : $this$unzip) {
         listT.add(pair.getFirst());
         listR.add(pair.getSecond());
      }

      return TuplesKt.to(listT, listR);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun Array<*>?.isNullOrEmpty(): Boolean {
      contract {
         returns(false) implies (this != null)
      }

      return `$this$isNullOrEmpty` == null || `$this$isNullOrEmpty`.length == 0;
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <C, R> C.ifEmpty(defaultValue: () -> R): R where C : Array<*>, C : R {
      return (R)(if (`$this$ifEmpty`.length == 0) defaultValue.invoke() else `$this$ifEmpty`);
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "contentDeepEquals")
   @JvmStatic
   internal fun <T> Array<out T>?.contentDeepEqualsImpl(other: Array<out T>?): Boolean {
      if (`$this$contentDeepEqualsImpl` === other) {
         return true;
      } else if (`$this$contentDeepEqualsImpl` != null && other != null && `$this$contentDeepEqualsImpl`.length == other.length) {
         var i: Int = 0;

         for (int var3 = $this$contentDeepEqualsImpl.length; i < var3; i++) {
            var v1: Any = `$this$contentDeepEqualsImpl`[i];
            var v2: Any = other[i];
            if (v1 != other[i]) {
               if (v1 == null || v2 == null) {
                  return false;
               }

               if (v1 is Object[] && v2 is Object[]) {
                  if (!ArraysKt.contentDeepEquals(v1 as Object[], v2 as Object[])) {
                     return false;
                  }
               } else if (v1 is byte[] && v2 is byte[]) {
                  if (!Arrays.equals(v1 as byte[], v2 as byte[])) {
                     return false;
                  }
               } else if (v1 is short[] && v2 is short[]) {
                  if (!Arrays.equals(v1 as short[], v2 as short[])) {
                     return false;
                  }
               } else if (v1 is int[] && v2 is int[]) {
                  if (!Arrays.equals(v1 as int[], v2 as int[])) {
                     return false;
                  }
               } else if (v1 is long[] && v2 is long[]) {
                  if (!Arrays.equals(v1 as long[], v2 as long[])) {
                     return false;
                  }
               } else if (v1 is float[] && v2 is float[]) {
                  if (!Arrays.equals(v1 as float[], v2 as float[])) {
                     return false;
                  }
               } else if (v1 is double[] && v2 is double[]) {
                  if (!Arrays.equals(v1 as double[], v2 as double[])) {
                     return false;
                  }
               } else if (v1 is char[] && v2 is char[]) {
                  if (!Arrays.equals(v1 as char[], v2 as char[])) {
                     return false;
                  }
               } else if (v1 is boolean[] && v2 is boolean[]) {
                  if (!Arrays.equals(v1 as boolean[], v2 as boolean[])) {
                     return false;
                  }
               } else if (v1 is UByteArray && v2 is UByteArray) {
                  if (!UArraysKt.contentEquals-kV0jMPg((v1 as UByteArray).unbox-impl(), (v2 as UByteArray).unbox-impl())) {
                     return false;
                  }
               } else if (v1 is UShortArray && v2 is UShortArray) {
                  if (!UArraysKt.contentEquals-FGO6Aew((v1 as UShortArray).unbox-impl(), (v2 as UShortArray).unbox-impl())) {
                     return false;
                  }
               } else if (v1 is UIntArray && v2 is UIntArray) {
                  if (!UArraysKt.contentEquals-KJPZfPQ((v1 as UIntArray).unbox-impl(), (v2 as UIntArray).unbox-impl())) {
                     return false;
                  }
               } else if (v1 is ULongArray && v2 is ULongArray) {
                  if (!UArraysKt.contentEquals-lec5QzE((v1 as ULongArray).unbox-impl(), (v2 as ULongArray).unbox-impl())) {
                     return false;
                  }
               } else if (!(v1 == v2)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @SinceKotlin(version = "1.3")
   @PublishedApi
   @JvmName(name = "contentDeepToString")
   @JvmStatic
   internal fun <T> Array<out T>?.contentDeepToStringImpl(): String {
      if (`$this$contentDeepToStringImpl` == null) {
         return "null";
      } else {
         var var2: StringBuilder = new StringBuilder(RangesKt.coerceAtMost(`$this$contentDeepToStringImpl`.length, 429496729) * 5 + 2);
         contentDeepToStringInternal$ArraysKt__ArraysKt(`$this$contentDeepToStringImpl`, var2, new ArrayList<>());
         var var10000: java.lang.String = var2.toString();
         return var10000;
      }
   }

   @JvmStatic
   private fun <T> Array<out T>.contentDeepToStringInternal(result: StringBuilder, processed: MutableList<Array<*>>) {
      if (processed.contains(`$this$contentDeepToStringInternal`)) {
         result.append("[...]");
      } else {
         processed.add(`$this$contentDeepToStringInternal`);
         result.append('[');
         var i: Int = 0;

         for (int var4 = $this$contentDeepToStringInternal.length; i < var4; i++) {
            if (i != 0) {
               result.append(", ");
            }

            var element: Any = `$this$contentDeepToStringInternal`[i];
            if (`$this$contentDeepToStringInternal`[i] == null) {
               result.append("null");
            } else if (element is Object[]) {
               contentDeepToStringInternal$ArraysKt__ArraysKt(element as Object[], result, processed);
            } else if (element is byte[]) {
               var var10001: java.lang.String = Arrays.toString(element as byte[]);
               result.append(var10001);
            } else if (element is short[]) {
               var var7: java.lang.String = Arrays.toString(element as short[]);
               result.append(var7);
            } else if (element is int[]) {
               var var8: java.lang.String = Arrays.toString(element as int[]);
               result.append(var8);
            } else if (element is long[]) {
               var var9: java.lang.String = Arrays.toString(element as long[]);
               result.append(var9);
            } else if (element is float[]) {
               var var10: java.lang.String = Arrays.toString(element as float[]);
               result.append(var10);
            } else if (element is double[]) {
               var var11: java.lang.String = Arrays.toString(element as double[]);
               result.append(var11);
            } else if (element is char[]) {
               var var12: java.lang.String = Arrays.toString(element as char[]);
               result.append(var12);
            } else if (element is boolean[]) {
               var var13: java.lang.String = Arrays.toString(element as boolean[]);
               result.append(var13);
            } else if (element is UByteArray) {
               result.append(UArraysKt.contentToString-2csIQuQ(if (element as UByteArray != null) (element as UByteArray).unbox-impl() else null));
            } else if (element is UShortArray) {
               result.append(UArraysKt.contentToString-d-6D3K8(if (element as UShortArray != null) (element as UShortArray).unbox-impl() else null));
            } else if (element is UIntArray) {
               result.append(UArraysKt.contentToString-XUkPCBk(if (element as UIntArray != null) (element as UIntArray).unbox-impl() else null));
            } else if (element is ULongArray) {
               result.append(UArraysKt.contentToString-uLth9ew(if (element as ULongArray != null) (element as ULongArray).unbox-impl() else null));
            } else {
               result.append(element.toString());
            }
         }

         result.append(']');
         processed.remove(CollectionsKt.getLastIndex(processed));
      }
   }

   open fun ArraysKt__ArraysKt() {
   }
}
