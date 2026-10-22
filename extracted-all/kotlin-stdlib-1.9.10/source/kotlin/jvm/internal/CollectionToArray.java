@file:JvmName(name = "CollectionToArray")

@file:SourceDebugExtension(["SMAP\nCollectionToArray.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CollectionToArray.kt\nkotlin/jvm/internal/CollectionToArray\n+ 2 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,82:1\n57#1,22:83\n57#1,22:105\n26#2:127\n*S KotlinDebug\n*F\n+ 1 CollectionToArray.kt\nkotlin/jvm/internal/CollectionToArray\n*L\n19#1:83,22\n31#1:105,22\n14#1:127\n*E\n"])

package kotlin.jvm.internal

import java.util.Arrays

private final val EMPTY: Array<Any?>
private const val MAX_SIZE: Int = 2147483645
private Object[] EMPTY = new Object[0];

@JvmName(name = "toArray")
public fun collectionToArray(collection: Collection<*>): Array<Any?> {
   var `size$iv`: Int = collection.size();
   var var10000: Array<Any>;
   if (`size$iv` == 0) {
      var10000 = EMPTY;
   } else {
      var var10: java.util.Iterator = collection.iterator();
      if (!var10.hasNext()) {
         var10000 = EMPTY;
      } else {
         var var11: Array<Any> = new Object[`size$iv`];
         var var12: Int = 0;

         while (true) {
            var11[var12++] = var10.next();
            if (var12 >= var11.length) {
               if (!var10.hasNext()) {
                  var10000 = var11;
                  break;
               }

               var `newSize$iv`: Int = var12 * 3 + 1 ushr 1;
               if (var12 * 3 + 1 ushr 1 <= var12) {
                  if (var12 >= 2147483645) {
                     throw new OutOfMemoryError();
                  }

                  `newSize$iv` = 2147483645;
               }

               var10000 = Arrays.copyOf(var11, `newSize$iv`);
               var11 = var10000;
            } else if (!var10.hasNext()) {
               var10000 = Arrays.copyOf(var11, var12);
               break;
            }
         }
      }
   }

   return var10000;
}

@JvmName(name = "toArray")
public fun collectionToArray(collection: Collection<*>, a: Array<Any?>?): Array<Any?> {
   if (a == null) {
      throw new NullPointerException();
   } else {
      var `size$iv`: Int = collection.size();
      var var10000: Array<Any>;
      if (`size$iv` == 0) {
         if (a.length > 0) {
            a[0] = null;
         }

         var10000 = a;
      } else {
         var `iter$iv`: java.util.Iterator = collection.iterator();
         if (!`iter$iv`.hasNext()) {
            if (a.length > 0) {
               a[0] = null;
            }

            var10000 = a;
         } else {
            if (`size$iv` <= a.length) {
               var10000 = a;
            } else {
               var10000 = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), `size$iv`);
               var10000 = var10000;
            }

            var `result$iv`: Array<Any> = var10000;
            var var12: Int = 0;

            while (true) {
               `result$iv`[var12++] = `iter$iv`.next();
               if (var12 >= `result$iv`.length) {
                  if (!`iter$iv`.hasNext()) {
                     var10000 = `result$iv`;
                     break;
                  }

                  var var13: Int = var12 * 3 + 1 ushr 1;
                  if (var12 * 3 + 1 ushr 1 <= var12) {
                     if (var12 >= 2147483645) {
                        throw new OutOfMemoryError();
                     }

                     var13 = 2147483645;
                  }

                  var10000 = Arrays.copyOf(`result$iv`, var13);
                  `result$iv` = var10000;
               } else if (!`iter$iv`.hasNext()) {
                  if (`result$iv` === a) {
                     a[var12] = null;
                     var10000 = a;
                  } else {
                     var10000 = Arrays.copyOf(`result$iv`, var12);
                  }
                  break;
               }
            }
         }
      }

      return var10000;
   }
}

private inline fun toArrayImpl(collection: Collection<*>, empty: () -> Array<Any?>, alloc: (Int) -> Array<Any?>, trim: (Array<Any?>, Int) -> Array<Any?>): Array<
      Any?
   > {
   var size: Int = collection.size();
   if (size == 0) {
      return empty.invoke() as Object[];
   } else {
      var iter: java.util.Iterator = collection.iterator();
      if (!iter.hasNext()) {
         return empty.invoke() as Object[];
      } else {
         var result: Array<Any> = alloc.invoke(size) as Object[];
         var i: Int = 0;

         while (true) {
            result[i++] = iter.next();
            if (i >= result.length) {
               if (!iter.hasNext()) {
                  return result;
               }

               var newSize: Int = i * 3 + 1 ushr 1;
               if (i * 3 + 1 ushr 1 <= i) {
                  if (i >= 2147483645) {
                     throw new OutOfMemoryError();
                  }

                  newSize = 2147483645;
               }

               var var10000: Array<Any> = Arrays.copyOf(result, newSize);
               result = var10000;
            } else if (!iter.hasNext()) {
               return trim.invoke(result, i) as Object[];
            }
         }
      }
   }
}

