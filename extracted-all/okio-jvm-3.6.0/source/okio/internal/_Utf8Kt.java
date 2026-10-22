@file:SourceDebugExtension(["SMAP\n-Utf8.kt\nKotlin\n*S Kotlin\n*F\n+ 1 -Utf8.kt\nokio/internal/_Utf8Kt\n+ 2 Utf8.kt\nokio/Utf8\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,60:1\n260#2,16:61\n277#2:78\n397#2,9:79\n127#2:88\n406#2,20:90\n279#2,3:110\n440#2,4:113\n127#2:117\n446#2,10:118\n127#2:128\n456#2,5:129\n127#2:134\n461#2,24:135\n283#2,3:159\n500#2,3:162\n286#2,12:165\n503#2:177\n127#2:178\n506#2,2:179\n127#2:181\n510#2,10:182\n127#2:192\n520#2,5:193\n127#2:198\n525#2,5:199\n127#2:204\n530#2,28:205\n302#2,6:233\n138#2,67:239\n68#3:77\n74#3:89\n*S KotlinDebug\n*F\n+ 1 -Utf8.kt\nokio/internal/_Utf8Kt\n*L\n34#1:61,16\n34#1:78\n34#1:79,9\n34#1:88\n34#1:90,20\n34#1:110,3\n34#1:113,4\n34#1:117\n34#1:118,10\n34#1:128\n34#1:129,5\n34#1:134\n34#1:135,24\n34#1:159,3\n34#1:162,3\n34#1:165,12\n34#1:177\n34#1:178\n34#1:179,2\n34#1:181\n34#1:182,10\n34#1:192\n34#1:193,5\n34#1:198\n34#1:199,5\n34#1:204\n34#1:205,28\n34#1:233,6\n50#1:239,67\n34#1:77\n34#1:89\n*E\n"])

package okio.internal

import java.util.Arrays
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension

public fun ByteArray.commonToUtf8String(beginIndex: Int, endIndex: Int): String {
   if (beginIndex >= 0 && endIndex <= `$this$commonToUtf8String`.length && beginIndex <= endIndex) {
      var chars: CharArray = new char[endIndex - beginIndex];
      var length: Int = 0;
      var `$this$processUtf16Chars$iv`: ByteArray = `$this$commonToUtf8String`;
      var `index$iv`: Int = beginIndex;

      while (index$iv < endIndex) {
         var `b0$iv`: Byte = `$this$processUtf16Chars$iv`[`index$iv`];
         if (`$this$processUtf16Chars$iv`[`index$iv`] >= 0) {
            chars[length++] = (char)`b0$iv`;
            `index$iv`++;

            while (index$iv < endIndex && $this$processUtf16Chars$iv[index$iv] >= 0) {
               chars[length++] = (char)`$this$processUtf16Chars$iv`[`index$iv`++];
            }
         } else if (`b0$iv` shr 5 == -2) {
            var var233: Int;
            var var235: Byte;
            if (endIndex <= `index$iv` + 1) {
               chars[length++] = (char)'�';
               var233 = `index$iv`;
               var235 = 1;
            } else {
               var var202: Byte = `$this$processUtf16Chars$iv`[`index$iv`];
               var var207: Byte = `$this$processUtf16Chars$iv`[`index$iv` + 1];
               if ((`$this$processUtf16Chars$iv`[`index$iv` + 1] and 192) != 128) {
                  chars[length++] = (char)'�';
                  var233 = `index$iv`;
                  var235 = 1;
               } else {
                  var var212: Int = 3968 xor var207 xor var202 shl 6;
                  if ((3968 xor var207 xor var202 shl 6) < 128) {
                     chars[length++] = (char)'�';
                     var233 = `index$iv`;
                  } else {
                     chars[length++] = (char)var212;
                     var233 = `index$iv`;
                  }

                  var235 = 2;
               }
            }

            `index$iv` = var233 + var235;
         } else if (`b0$iv` shr 4 == -2) {
            var var232: Int;
            var var234: Byte;
            label194:
            if (endIndex <= `index$iv` + 2) {
               chars[length++] = (char)'�';
               var232 = `index$iv`;
               var234 = (byte)(if (endIndex > `index$iv` + 1 && (`$this$processUtf16Chars$iv`[`index$iv` + 1] and 192) == 128) 2 else 1);
               break label194;
            } else {
               var var201: Byte = `$this$processUtf16Chars$iv`[`index$iv`];
               var var206: Byte = `$this$processUtf16Chars$iv`[`index$iv` + 1];
               if ((`$this$processUtf16Chars$iv`[`index$iv` + 1] and 192) != 128) {
                  chars[length++] = (char)'�';
                  var232 = `index$iv`;
                  var234 = 1;
               } else {
                  var var210: Byte = `$this$processUtf16Chars$iv`[`index$iv` + 2];
                  if ((`$this$processUtf16Chars$iv`[`index$iv` + 2] and 192) != 128) {
                     chars[length++] = (char)'�';
                     var232 = `index$iv`;
                     var234 = 2;
                  } else {
                     var var218: Int = -123008 xor var210 xor var206 shl 6 xor var201 shl 12;
                     if ((-123008 xor var210 xor var206 shl 6 xor var201 shl 12) < 2048) {
                        chars[length++] = (char)'�';
                        var232 = `index$iv`;
                     } else if (55296 <= var218 && var218 < 57344) {
                        chars[length++] = (char)'�';
                        var232 = `index$iv`;
                     } else {
                        chars[length++] = (char)var218;
                        var232 = `index$iv`;
                     }

                     var234 = 3;
                  }
               }
            }

            `index$iv` = var232 + var234;
         } else if (`b0$iv` shr 3 != -2) {
            chars[length++] = '�';
            `index$iv`++;
         } else {
            var var10000: Int;
            var var10001: Byte;
            label207:
            if (endIndex <= `index$iv` + 3) {
               if ('�' != '�') {
                  chars[length++] = (char)(('�' ushr 10) + 55232);
                  chars[length++] = (char)(('�' and 1023) + 56320);
               } else {
                  chars[length++] = '�';
               }

               var10000 = `index$iv`;
               var10001 = (byte)(if (endIndex <= `index$iv` + 1 || (`$this$processUtf16Chars$iv`[`index$iv` + 1] and 192) != 128)
                  1
                  else
                  (if (endIndex > `index$iv` + 2 && (`$this$processUtf16Chars$iv`[`index$iv` + 2] and 192) == 128) 3 else 2));
               break label207;
            } else {
               var var199: Byte = `$this$processUtf16Chars$iv`[`index$iv`];
               var var204: Byte = `$this$processUtf16Chars$iv`[`index$iv` + 1];
               if ((`$this$processUtf16Chars$iv`[`index$iv` + 1] and 192) != 128) {
                  if ('�' != '�') {
                     chars[length++] = (char)(('�' ushr 10) + 55232);
                     chars[length++] = (char)(('�' and 1023) + 56320);
                  } else {
                     chars[length++] = '�';
                  }

                  var10000 = `index$iv`;
                  var10001 = 1;
               } else {
                  var var208: Byte = `$this$processUtf16Chars$iv`[`index$iv` + 2];
                  if ((`$this$processUtf16Chars$iv`[`index$iv` + 2] and 192) != 128) {
                     if ('�' != '�') {
                        chars[length++] = (char)(('�' ushr 10) + 55232);
                        chars[length++] = (char)(('�' and 1023) + 56320);
                     } else {
                        chars[length++] = '�';
                     }

                     var10000 = `index$iv`;
                     var10001 = 2;
                  } else {
                     var var215: Byte = `$this$processUtf16Chars$iv`[`index$iv` + 3];
                     if ((`$this$processUtf16Chars$iv`[`index$iv` + 3] and 192) != 128) {
                        if ('�' != '�') {
                           chars[length++] = (char)(('�' ushr 10) + 55232);
                           chars[length++] = (char)(('�' and 1023) + 56320);
                        } else {
                           chars[length++] = '�';
                        }

                        var10000 = `index$iv`;
                        var10001 = 3;
                     } else {
                        var var222: Int = 3678080 xor var215 xor var208 shl 6 xor var204 shl 12 xor var199 shl 18;
                        if ((3678080 xor var215 xor var208 shl 6 xor var204 shl 12 xor var199 shl 18) > 1114111) {
                           if ('�' != '�') {
                              chars[length++] = (char)(('�' ushr 10) + 55232);
                              chars[length++] = (char)(('�' and 1023) + 56320);
                           } else {
                              chars[length++] = '�';
                           }

                           var10000 = `index$iv`;
                        } else if (55296 <= var222 && var222 < 57344) {
                           if ('�' != '�') {
                              chars[length++] = (char)(('�' ushr 10) + 55232);
                              chars[length++] = (char)(('�' and 1023) + 56320);
                           } else {
                              chars[length++] = '�';
                           }

                           var10000 = `index$iv`;
                        } else if (var222 < 65536) {
                           if ('�' != '�') {
                              chars[length++] = (char)(('�' ushr 10) + 55232);
                              chars[length++] = (char)(('�' and 1023) + 56320);
                           } else {
                              chars[length++] = '�';
                           }

                           var10000 = `index$iv`;
                        } else {
                           if (var222 != 65533) {
                              chars[length++] = (char)((var222 ushr 10) + 55232);
                              chars[length++] = (char)((var222 and 1023) + 56320);
                           } else {
                              chars[length++] = '�';
                           }

                           var10000 = `index$iv`;
                        }

                        var10001 = 4;
                     }
                  }
               }
            }

            `index$iv` = var10000 + var10001;
         }
      }

      return StringsKt.concatToString(chars, 0, length);
   } else {
      throw new ArrayIndexOutOfBoundsException("size=" + `$this$commonToUtf8String`.length + " beginIndex=" + beginIndex + " endIndex=" + endIndex);
   }
}

@JvmSynthetic
fun `commonToUtf8String$default`(var0: ByteArray, var1: Int, var2: Int, var3: Int, var4: Any): java.lang.String {
   if ((var3 and 1) != 0) {
      var1 = 0;
   }

   if ((var3 and 2) != 0) {
      var2 = var0.length;
   }

   return commonToUtf8String(var0, var1, var2);
}

public fun String.commonAsUtf8ToByteArray(): ByteArray {
   var bytes: ByteArray = new byte[4 * `$this$commonAsUtf8ToByteArray`.length()];
   var index: Int = 0;

   for (int var3 = $this$commonAsUtf8ToByteArray.length(); index < var3; index++) {
      var b0: Char = `$this$commonAsUtf8ToByteArray`.charAt(index);
      if (Intrinsics.compare(b0, 128) >= 0) {
         var var15: Int = index;
         var `$this$processUtf8Bytes$iv`: java.lang.String = `$this$commonAsUtf8ToByteArray`;
         var `endIndex$iv`: Int = `$this$commonAsUtf8ToByteArray`.length();
         var `index$iv`: Int = index;

         while (index$iv < endIndex$iv) {
            var `c$iv`: Char = `$this$processUtf8Bytes$iv`.charAt(`index$iv`);
            if (Intrinsics.compare(`c$iv`, 128) < 0) {
               bytes[var15++] = (byte)`c$iv`;
               `index$iv`++;

               while (index$iv < endIndex$iv && Intrinsics.compare($this$processUtf8Bytes$iv.charAt(index$iv), 128) < 0) {
                  bytes[var15++] = (byte)`$this$processUtf8Bytes$iv`.charAt(`index$iv`++);
               }
            } else if (Intrinsics.compare(`c$iv`, 2048) < 0) {
               bytes[var15++] = (byte)(`c$iv` shr 6 or 192);
               bytes[var15++] = (byte)(`c$iv` and 63 or 128);
               `index$iv`++;
            } else if ('\ud800' > `c$iv` || `c$iv` >= '\ue000') {
               bytes[var15++] = (byte)(`c$iv` shr 12 or 224);
               bytes[var15++] = (byte)(`c$iv` shr 6 and 63 or 128);
               bytes[var15++] = (byte)(`c$iv` and 63 or 128);
               `index$iv`++;
            } else {
               if (Intrinsics.compare(`c$iv`, 56319) <= 0 && `endIndex$iv` > `index$iv` + 1) {
                  var `codePoint$iv`: Int = `$this$processUtf8Bytes$iv`.charAt(`index$iv` + 1);
                  if (56320 <= `codePoint$iv` && `codePoint$iv` < 57344) {
                     `codePoint$iv` = (`c$iv` shl 10) + `$this$processUtf8Bytes$iv`.charAt(`index$iv` + 1) + -56613888;
                     bytes[var15++] = (byte)(`codePoint$iv` shr 18 or 240);
                     bytes[var15++] = (byte)(`codePoint$iv` shr 12 and 63 or 128);
                     bytes[var15++] = (byte)(`codePoint$iv` shr 6 and 63 or 128);
                     bytes[var15++] = (byte)(`codePoint$iv` and 63 or 128);
                     `index$iv` += 2;
                     continue;
                  }
               }

               bytes[var15++] = 63;
               `index$iv`++;
            }
         }

         var var10000: ByteArray = Arrays.copyOf(bytes, var15);
         return var10000;
      }

      bytes[index] = (byte)b0;
   }

   var var56: ByteArray = Arrays.copyOf(bytes, `$this$commonAsUtf8ToByteArray`.length());
   return var56;
}

