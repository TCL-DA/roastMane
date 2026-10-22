package okio

import java.util.ArrayList
import java.util.Arrays
import java.util.RandomAccess
import kotlin.jvm.internal.SourceDebugExtension

class Options private constructor(vararg byteStrings: Any, trie: IntArray) : AbstractList<ByteString>, RandomAccess {
   @JvmStatic
   public Options.Companion Companion = new Options.Companion(null);
   private ByteString[] byteStrings;

   internal final val byteStrings: Array<out ByteString>
   public open val size: Int
      public open get() {
         return this.byteStrings.length;
      }

   internal final val trie: IntArray

   init {
      this.byteStrings = byteStrings;
      this.trie = trie;
   }

   public open operator fun get(index: Int): ByteString {
      return this.byteStrings[index];
   }

   @JvmStatic
   fun of(vararg byteStrings: ByteString): Options {
      return Companion.of(byteStrings);
   }

   @SourceDebugExtension(["SMAP\nOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Options.kt\nokio/Options$Companion\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,236:1\n11065#2:237\n11400#2,3:238\n13374#2,3:243\n37#3,2:241\n1#4:246\n74#5:247\n74#5:248\n*S KotlinDebug\n*F\n+ 1 Options.kt\nokio/Options$Companion\n*L\n43#1:237\n43#1:238,3\n44#1:243,3\n43#1:241,2\n151#1:247\n208#1:248\n*E\n"])
   companion object Companion private constructor() {
      private final val intCount: Long
         private final get() {
            return `$this$intCount`.size() / (long)4;
         }



      public fun of(vararg byteStrings: ByteString): Options {
         if (byteStrings.length == 0) {
            return new Options(new ByteString[0], new int[]{0, -1}, null);
         } else {
            var list: java.util.List = ArraysKt.toMutableList(byteStrings);
            CollectionsKt.sort(list);
            var `destination$iv$iv`: java.util.Collection = new ArrayList(byteStrings.length);

            for (Object item$iv$iv : byteStrings) {
               `destination$iv$iv`.add(-1);
            }

            var a: Array<Int> = (`destination$iv$iv` as java.util.List).toArray(new Integer[0]);
            var indexes: java.util.List = CollectionsKt.mutableListOf(Arrays.copyOf(a, a.length));
            var var25: Int = 0;

            for (Object item$iv : byteStrings) {
               indexes.set(CollectionsKt.binarySearch$default(list, var32 as java.lang.Comparable, 0, 0, 6, null), var25++);
            }

            if ((list.get(0) as ByteString).size() <= 0) {
               throw new IllegalArgumentException("the empty byte string is not a supported option".toString());
            } else {
               for (int ax = 0; ax < list.size(); ax++) {
                  var var20: ByteString = list.get(ax) as ByteString;
                  var25 = ax + 1;

                  while (index$iv < list.size()) {
                     var var29: ByteString = list.get(var25) as ByteString;
                     if (!var29.startsWith(var20)) {
                        break;
                     }

                     if (var29.size() == var20.size()) {
                        throw new IllegalArgumentException(("duplicate option: " + var29).toString());
                     }

                     if ((indexes.get(var25) as java.lang.Number).intValue() > (indexes.get(ax) as java.lang.Number).intValue()) {
                        list.remove(var25);
                        indexes.remove(var25);
                     } else {
                        var25++;
                     }
                  }
               }

               var var21: Buffer = new Buffer();
               buildTrieRecursive$default(this, 0L, var21, 0, list, 0, 0, indexes, 53, null);
               var var27: IntArray = new int[(int)this.getIntCount(var21)];
               var var30: Int = 0;

               while (!trieBytes.exhausted()) {
                  var27[var30++] = var21.readInt();
               }

               var var10002: Array<Any> = Arrays.copyOf(byteStrings, byteStrings.length);
               return new Options(var10002 as ByteString[], var27, null);
            }
         }
      }

      private fun buildTrieRecursive(
         nodeOffset: Long,
         node: Buffer,
         byteStringOffset: Int,
         byteStrings: List<ByteString>,
         fromIndex: Int,
         toIndex: Int,
         indexes: List<Int>
      ) {
         if (fromIndex >= toIndex) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else {
            for (int i = fromIndex; i < toIndex; i++) {
               if ((byteStrings.get(fromIndex) as ByteString).size() < byteStringOffset) {
                  throw new IllegalArgumentException("Failed requirement.".toString());
               }
            }

            var var21: Int = fromIndex;
            var from: ByteString = byteStrings.get(fromIndex) as ByteString;
            var var23: ByteString = byteStrings.get(toIndex - 1) as ByteString;
            var prefixIndex: Int = -1;
            if (byteStringOffset == from.size()) {
               prefixIndex = (indexes.get(fromIndex) as java.lang.Number).intValue();
               var21 = fromIndex + 1;
               from = byteStrings.get(fromIndex + 1) as ByteString;
            }

            if (from.getByte(byteStringOffset) != var23.getByte(byteStringOffset)) {
               var scanByteCount: Int = 1;

               for (int ix = fromIndex + 1; ix < toIndex; ix++) {
                  if ((byteStrings.get(ix - 1) as ByteString).getByte(byteStringOffset) != (byteStrings.get(ix) as ByteString).getByte(byteStringOffset)) {
                     scanByteCount++;
                  }
               }

               var var25: Long = nodeOffset + this.getIntCount(node) + (long)2 + (long)(scanByteCount * 2);
               node.writeInt(scanByteCount);
               node.writeInt(prefixIndex);

               for (int ixx = fromIndex; ixx < toIndex; ixx++) {
                  var rangeStart: Byte = (byteStrings.get(ixx) as ByteString).getByte(byteStringOffset);
                  if (ixx == var21 || rangeStart != (byteStrings.get(ixx - 1) as ByteString).getByte(byteStringOffset)) {
                     node.writeInt(rangeStart and 255);
                  }
               }

               var var28: Buffer = new Buffer();
               var var31: Int = var21;

               while (rangeStart < toIndex) {
                  var `$this$and$iv`: Byte = (byteStrings.get(var31) as ByteString).getByte(byteStringOffset);
                  var var35: Int = toIndex;

                  for (int ixxx = rangeStart + 1; ixxx < toIndex; ixxx++) {
                     if (`$this$and$iv` != (byteStrings.get(ixxx) as ByteString).getByte(byteStringOffset)) {
                        var35 = ixxx;
                        break;
                     }
                  }

                  if (var31 + 1 == var35 && byteStringOffset + 1 == (byteStrings.get(var31) as ByteString).size()) {
                     node.writeInt((indexes.get(var31) as java.lang.Number).intValue());
                  } else {
                     node.writeInt(-1 * (int)(var25 + this.getIntCount(var28)));
                     this.buildTrieRecursive(var25, var28, byteStringOffset + 1, byteStrings, var31, var35, indexes);
                  }

                  var31 = var35;
               }

               node.writeAll(var28);
            } else {
               var var24: Int = 0;
               var ixxxx: Int = byteStringOffset;

               for (int var15 = Math.min(from.size(), to.size()); ixxxx < var15 && from.getByte(ixxxx) == to.getByte(ixxxx); ixxxx++) {
                  var24++;
               }

               var var27: Long = nodeOffset + this.getIntCount(node) + (long)2 + (long)var24 + 1L;
               node.writeInt(-var24);
               node.writeInt(prefixIndex);
               var ixxxxx: Int = byteStringOffset;

               for (int var32 = byteStringOffset + scanByteCount; ixxxxx < var32; ixxxxx++) {
                  node.writeInt(from.getByte(ixxxxx) and 255);
               }

               if (var21 + 1 == toIndex) {
                  if (byteStringOffset + var24 != (byteStrings.get(var21) as ByteString).size()) {
                     throw new IllegalStateException("Check failed.".toString());
                  }

                  node.writeInt((indexes.get(var21) as java.lang.Number).intValue());
               } else {
                  var var30: Buffer = new Buffer();
                  node.writeInt(-1 * (int)(var27 + this.getIntCount(var30)));
                  this.buildTrieRecursive(var27, var30, byteStringOffset + var24, byteStrings, var21, toIndex, indexes);
                  node.writeAll(var30);
               }
            }
         }
      }
   }
}
