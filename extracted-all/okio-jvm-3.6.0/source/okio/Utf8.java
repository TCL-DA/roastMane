@file:JvmName(name = "Utf8")

@file:SourceDebugExtension(["SMAP\nUtf8.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Utf8.kt\nokio/Utf8\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,559:1\n397#1,9:563\n127#1:572\n406#1,20:574\n440#1,4:595\n127#1:599\n446#1,10:601\n127#1:611\n456#1,5:612\n127#1:617\n461#1,24:618\n500#1,4:643\n127#1:647\n506#1,2:649\n127#1:651\n510#1,10:652\n127#1:662\n520#1,5:663\n127#1:668\n525#1,5:669\n127#1:674\n530#1,28:675\n397#1,9:704\n127#1:713\n406#1,20:715\n440#1,4:736\n127#1:740\n446#1,10:742\n127#1:752\n456#1,5:753\n127#1:758\n461#1,24:759\n500#1,4:784\n127#1:788\n506#1,2:790\n127#1:792\n510#1,10:793\n127#1:803\n520#1,5:804\n127#1:809\n525#1,5:810\n127#1:815\n530#1,28:816\n127#1:844\n127#1:846\n127#1:848\n127#1:850\n127#1:852\n127#1:854\n127#1:856\n127#1:858\n127#1:860\n1#2:560\n74#3:561\n68#3:562\n74#3:573\n68#3:594\n74#3:600\n68#3:642\n74#3:648\n68#3:703\n74#3:714\n68#3:735\n74#3:741\n68#3:783\n74#3:789\n74#3:845\n74#3:847\n74#3:849\n74#3:851\n74#3:853\n74#3:855\n74#3:857\n74#3:859\n74#3:861\n*S KotlinDebug\n*F\n+ 1 Utf8.kt\nokio/Utf8\n*L\n228#1:563,9\n228#1:572\n228#1:574,20\n232#1:595,4\n232#1:599\n232#1:601,10\n232#1:611\n232#1:612,5\n232#1:617\n232#1:618,24\n236#1:643,4\n236#1:647\n236#1:649,2\n236#1:651\n236#1:652,10\n236#1:662\n236#1:663,5\n236#1:668\n236#1:669,5\n236#1:674\n236#1:675,28\n277#1:704,9\n277#1:713\n277#1:715,20\n281#1:736,4\n281#1:740\n281#1:742,10\n281#1:752\n281#1:753,5\n281#1:758\n281#1:759,24\n285#1:784,4\n285#1:788\n285#1:790,2\n285#1:792\n285#1:793,10\n285#1:803\n285#1:804,5\n285#1:809\n285#1:810,5\n285#1:815\n285#1:816,28\n405#1:844\n443#1:846\n455#1:848\n460#1:850\n503#1:852\n507#1:854\n519#1:856\n524#1:858\n529#1:860\n127#1:561\n226#1:562\n228#1:573\n230#1:594\n232#1:600\n234#1:642\n236#1:648\n275#1:703\n277#1:714\n279#1:735\n281#1:741\n283#1:783\n285#1:789\n405#1:845\n443#1:847\n455#1:849\n460#1:851\n503#1:853\n507#1:855\n519#1:857\n524#1:859\n529#1:861\n*E\n"])

package okio

import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension

internal const val HIGH_SURROGATE_HEADER: Int = 55232
internal const val LOG_SURROGATE_HEADER: Int = 56320
internal const val MASK_2BYTES: Int = 3968
internal const val MASK_3BYTES: Int = -123008
internal const val MASK_4BYTES: Int = 3678080
internal const val REPLACEMENT_BYTE: Byte = 63
internal const val REPLACEMENT_CHARACTER: Char = '�'
internal const val REPLACEMENT_CODE_POINT: Int = 65533

@JvmOverloads
@JvmName(name = "size")
public fun String.utf8Size(beginIndex: Int, endIndex: Int): Long {
   if (beginIndex < 0) {
      throw new IllegalArgumentException(("beginIndex < 0: " + beginIndex).toString());
   } else if (endIndex < beginIndex) {
      throw new IllegalArgumentException(("endIndex < beginIndex: " + endIndex + " < " + beginIndex).toString());
   } else if (endIndex > `$this$utf8Size`.length()) {
      throw new IllegalArgumentException(("endIndex > string.length: " + endIndex + " > " + `$this$utf8Size`.length()).toString());
   } else {
      var result: Long = 0L;
      var i: Int = beginIndex;

      while (i < endIndex) {
         var c: Int = `$this$utf8Size`.charAt(i);
         if (c < 128) {
            result++;
            i++;
         } else if (c < 2048) {
            result += (long)2;
            i++;
         } else if (c >= 55296 && c <= 57343) {
            var low: Int = if (i + 1 < endIndex) `$this$utf8Size`.charAt(i + 1) else 0;
            if (c <= 56319 && low >= 56320 && low <= 57343) {
               result += (long)4;
               i += 2;
            } else {
               result++;
               i++;
            }
         } else {
            result += (long)3;
            i++;
         }
      }

      return result;
   }
}

@JvmSynthetic
fun `size$default`(var0: java.lang.String, var1: Int, var2: Int, var3: Int, var4: Any): Long {
   if ((var3 and 1) != 0) {
      var1 = 0;
   }

   if ((var3 and 2) != 0) {
      var2 = var0.length();
   }

   return size(var0, var1, var2);
}

internal inline fun isIsoControl(codePoint: Int): Boolean {
   return 0 <= codePoint && codePoint < 32 || 127 <= codePoint && codePoint < 160;
}

internal inline fun isUtf8Continuation(byte: Byte): Boolean {
   return (var0 and 192) == 128;
}

internal inline fun String.processUtf8Bytes(beginIndex: Int, endIndex: Int, yield: (Byte) -> Unit) {
   var index: Int = beginIndex;

   while (index < endIndex) {
      var c: Char = `$this$processUtf8Bytes`.charAt(index);
      if (Intrinsics.compare(c, 128) < 0) {
         yield.invoke((byte)c);
         index++;

         while (index < endIndex && Intrinsics.compare($this$processUtf8Bytes.charAt(index), 128) < 0) {
            yield.invoke((byte)`$this$processUtf8Bytes`.charAt(index++));
         }
      } else if (Intrinsics.compare(c, 2048) < 0) {
         yield.invoke((byte)(c shr 6 or 192));
         yield.invoke((byte)(c and 63 or 128));
         index++;
      } else if ('\ud800' > c || c >= '\ue000') {
         yield.invoke((byte)(c shr 12 or 224));
         yield.invoke((byte)(c shr 6 and 63 or 128));
         yield.invoke((byte)(c and 63 or 128));
         index++;
      } else {
         if (Intrinsics.compare(c, 56319) <= 0 && endIndex > index + 1) {
            var codePoint: Int = `$this$processUtf8Bytes`.charAt(index + 1);
            if (56320 <= codePoint && codePoint < 57344) {
               codePoint = (c shl 10) + `$this$processUtf8Bytes`.charAt(index + 1) + -56613888;
               yield.invoke((byte)(codePoint shr 18 or 240));
               yield.invoke((byte)(codePoint shr 12 and 63 or 128));
               yield.invoke((byte)(codePoint shr 6 and 63 or 128));
               yield.invoke((byte)(codePoint and 63 or 128));
               index += 2;
               continue;
            }
         }

         yield.invoke((byte)63);
         index++;
      }
   }
}

internal inline fun ByteArray.processUtf8CodePoints(beginIndex: Int, endIndex: Int, yield: (Int) -> Unit) {
   var index: Int = beginIndex;

   while (index < endIndex) {
      var b0: Byte = `$this$processUtf8CodePoints`[index];
      if (`$this$processUtf8CodePoints`[index] >= 0) {
         yield.invoke(Integer.valueOf(b0));
         index++;

         while (index < endIndex && $this$processUtf8CodePoints[index] >= 0) {
            yield.invoke(Integer.valueOf(`$this$processUtf8CodePoints`[index++]));
         }
      } else if (b0 shr 5 == -2) {
         var var112: Int;
         var var114: Byte;
         if (endIndex <= index + 1) {
            yield.invoke(65533);
            var112 = index;
            var114 = 1;
         } else {
            var var64: Byte = `$this$processUtf8CodePoints`[index];
            var var69: Byte = `$this$processUtf8CodePoints`[index + 1];
            if ((`$this$processUtf8CodePoints`[index + 1] and 192) != 128) {
               yield.invoke(65533);
               var112 = index;
               var114 = 1;
            } else {
               var var74: Int = 3968 xor var69 xor var64 shl 6;
               if ((3968 xor var69 xor var64 shl 6) < 128) {
                  yield.invoke(65533);
                  var112 = index;
               } else {
                  yield.invoke(var74);
                  var112 = index;
               }

               var114 = 2;
            }
         }

         index = var112 + var114;
      } else if (b0 shr 4 == -2) {
         var var111: Int;
         var var113: Byte;
         label151:
         if (endIndex <= index + 2) {
            yield.invoke(65533);
            var111 = index;
            var113 = (byte)(if (endIndex > index + 1 && (`$this$processUtf8CodePoints`[index + 1] and 192) == 128) 2 else 1);
            break label151;
         } else {
            var var63: Byte = `$this$processUtf8CodePoints`[index];
            var var68: Byte = `$this$processUtf8CodePoints`[index + 1];
            if ((`$this$processUtf8CodePoints`[index + 1] and 192) != 128) {
               yield.invoke(65533);
               var111 = index;
               var113 = 1;
            } else {
               var var72: Byte = `$this$processUtf8CodePoints`[index + 2];
               if ((`$this$processUtf8CodePoints`[index + 2] and 192) != 128) {
                  yield.invoke(65533);
                  var111 = index;
                  var113 = 2;
               } else {
                  var var80: Int = -123008 xor var72 xor var68 shl 6 xor var63 shl 12;
                  if ((-123008 xor var72 xor var68 shl 6 xor var63 shl 12) < 2048) {
                     yield.invoke(65533);
                     var111 = index;
                  } else if (55296 <= var80 && var80 < 57344) {
                     yield.invoke(65533);
                     var111 = index;
                  } else {
                     yield.invoke(var80);
                     var111 = index;
                  }

                  var113 = 3;
               }
            }
         }

         index = var111 + var113;
      } else if (b0 shr 3 != -2) {
         yield.invoke(65533);
         index++;
      } else {
         var var10000: Int;
         var var10001: Byte;
         label163:
         if (endIndex <= index + 3) {
            yield.invoke(65533);
            var10000 = index;
            var10001 = (byte)(if (endIndex <= index + 1 || (`$this$processUtf8CodePoints`[index + 1] and 192) != 128)
               1
               else
               (if (endIndex > index + 2 && (`$this$processUtf8CodePoints`[index + 2] and 192) == 128) 3 else 2));
            break label163;
         } else {
            var var61: Byte = `$this$processUtf8CodePoints`[index];
            var var66: Byte = `$this$processUtf8CodePoints`[index + 1];
            if ((`$this$processUtf8CodePoints`[index + 1] and 192) != 128) {
               yield.invoke(65533);
               var10000 = index;
               var10001 = 1;
            } else {
               var var70: Byte = `$this$processUtf8CodePoints`[index + 2];
               if ((`$this$processUtf8CodePoints`[index + 2] and 192) != 128) {
                  yield.invoke(65533);
                  var10000 = index;
                  var10001 = 2;
               } else {
                  var var77: Byte = `$this$processUtf8CodePoints`[index + 3];
                  if ((`$this$processUtf8CodePoints`[index + 3] and 192) != 128) {
                     yield.invoke(65533);
                     var10000 = index;
                     var10001 = 3;
                  } else {
                     var var84: Int = 3678080 xor var77 xor var70 shl 6 xor var66 shl 12 xor var61 shl 18;
                     if ((3678080 xor var77 xor var70 shl 6 xor var66 shl 12 xor var61 shl 18) > 1114111) {
                        yield.invoke(65533);
                        var10000 = index;
                     } else if (55296 <= var84 && var84 < 57344) {
                        yield.invoke(65533);
                        var10000 = index;
                     } else if (var84 < 65536) {
                        yield.invoke(65533);
                        var10000 = index;
                     } else {
                        yield.invoke(var84);
                        var10000 = index;
                     }

                     var10001 = 4;
                  }
               }
            }
         }

         index = var10000 + var10001;
      }
   }
}

internal inline fun ByteArray.processUtf16Chars(beginIndex: Int, endIndex: Int, yield: (Char) -> Unit) {
   var index: Int = beginIndex;

   while (index < endIndex) {
      var b0: Byte = `$this$processUtf16Chars`[index];
      if (`$this$processUtf16Chars`[index] >= 0) {
         yield.invoke((char)b0);
         index++;

         while (index < endIndex && $this$processUtf16Chars[index] >= 0) {
            yield.invoke((char)`$this$processUtf16Chars`[index++]);
         }
      } else if (b0 shr 5 == -2) {
         var var112: Int;
         var var114: Byte;
         if (endIndex <= index + 1) {
            yield.invoke((char)'�');
            var112 = index;
            var114 = 1;
         } else {
            var var64: Byte = `$this$processUtf16Chars`[index];
            var var69: Byte = `$this$processUtf16Chars`[index + 1];
            if ((`$this$processUtf16Chars`[index + 1] and 192) != 128) {
               yield.invoke((char)'�');
               var112 = index;
               var114 = 1;
            } else {
               var var74: Int = 3968 xor var69 xor var64 shl 6;
               if ((3968 xor var69 xor var64 shl 6) < 128) {
                  yield.invoke((char)'�');
                  var112 = index;
               } else {
                  yield.invoke((char)var74);
                  var112 = index;
               }

               var114 = 2;
            }
         }

         index = var112 + var114;
      } else if (b0 shr 4 == -2) {
         var var111: Int;
         var var113: Byte;
         label190:
         if (endIndex <= index + 2) {
            yield.invoke((char)'�');
            var111 = index;
            var113 = (byte)(if (endIndex > index + 1 && (`$this$processUtf16Chars`[index + 1] and 192) == 128) 2 else 1);
            break label190;
         } else {
            var var63: Byte = `$this$processUtf16Chars`[index];
            var var68: Byte = `$this$processUtf16Chars`[index + 1];
            if ((`$this$processUtf16Chars`[index + 1] and 192) != 128) {
               yield.invoke((char)'�');
               var111 = index;
               var113 = 1;
            } else {
               var var72: Byte = `$this$processUtf16Chars`[index + 2];
               if ((`$this$processUtf16Chars`[index + 2] and 192) != 128) {
                  yield.invoke((char)'�');
                  var111 = index;
                  var113 = 2;
               } else {
                  var var80: Int = -123008 xor var72 xor var68 shl 6 xor var63 shl 12;
                  if ((-123008 xor var72 xor var68 shl 6 xor var63 shl 12) < 2048) {
                     yield.invoke((char)'�');
                     var111 = index;
                  } else if (55296 <= var80 && var80 < 57344) {
                     yield.invoke((char)'�');
                     var111 = index;
                  } else {
                     yield.invoke((char)var80);
                     var111 = index;
                  }

                  var113 = 3;
               }
            }
         }

         index = var111 + var113;
      } else if (b0 shr 3 != -2) {
         yield.invoke('�');
         index++;
      } else {
         var var10000: Int;
         var var10001: Byte;
         label203:
         if (endIndex <= index + 3) {
            if ('�' != '�') {
               yield.invoke((char)(('�' ushr 10) + 55232));
               yield.invoke((char)(('�' and 1023) + 56320));
            } else {
               yield.invoke('�');
            }

            var10000 = index;
            var10001 = (byte)(if (endIndex <= index + 1 || (`$this$processUtf16Chars`[index + 1] and 192) != 128)
               1
               else
               (if (endIndex > index + 2 && (`$this$processUtf16Chars`[index + 2] and 192) == 128) 3 else 2));
            break label203;
         } else {
            var var61: Byte = `$this$processUtf16Chars`[index];
            var var66: Byte = `$this$processUtf16Chars`[index + 1];
            if ((`$this$processUtf16Chars`[index + 1] and 192) != 128) {
               if ('�' != '�') {
                  yield.invoke((char)(('�' ushr 10) + 55232));
                  yield.invoke((char)(('�' and 1023) + 56320));
               } else {
                  yield.invoke('�');
               }

               var10000 = index;
               var10001 = 1;
            } else {
               var var70: Byte = `$this$processUtf16Chars`[index + 2];
               if ((`$this$processUtf16Chars`[index + 2] and 192) != 128) {
                  if ('�' != '�') {
                     yield.invoke((char)(('�' ushr 10) + 55232));
                     yield.invoke((char)(('�' and 1023) + 56320));
                  } else {
                     yield.invoke('�');
                  }

                  var10000 = index;
                  var10001 = 2;
               } else {
                  var var77: Byte = `$this$processUtf16Chars`[index + 3];
                  if ((`$this$processUtf16Chars`[index + 3] and 192) != 128) {
                     if ('�' != '�') {
                        yield.invoke((char)(('�' ushr 10) + 55232));
                        yield.invoke((char)(('�' and 1023) + 56320));
                     } else {
                        yield.invoke('�');
                     }

                     var10000 = index;
                     var10001 = 3;
                  } else {
                     var var84: Int = 3678080 xor var77 xor var70 shl 6 xor var66 shl 12 xor var61 shl 18;
                     if ((3678080 xor var77 xor var70 shl 6 xor var66 shl 12 xor var61 shl 18) > 1114111) {
                        if ('�' != '�') {
                           yield.invoke((char)(('�' ushr 10) + 55232));
                           yield.invoke((char)(('�' and 1023) + 56320));
                        } else {
                           yield.invoke('�');
                        }

                        var10000 = index;
                     } else if (55296 <= var84 && var84 < 57344) {
                        if ('�' != '�') {
                           yield.invoke((char)(('�' ushr 10) + 55232));
                           yield.invoke((char)(('�' and 1023) + 56320));
                        } else {
                           yield.invoke('�');
                        }

                        var10000 = index;
                     } else if (var84 < 65536) {
                        if ('�' != '�') {
                           yield.invoke((char)(('�' ushr 10) + 55232));
                           yield.invoke((char)(('�' and 1023) + 56320));
                        } else {
                           yield.invoke('�');
                        }

                        var10000 = index;
                     } else {
                        if (var84 != 65533) {
                           yield.invoke((char)((var84 ushr 10) + 55232));
                           yield.invoke((char)((var84 and 1023) + 56320));
                        } else {
                           yield.invoke('�');
                        }

                        var10000 = index;
                     }

                     var10001 = 4;
                  }
               }
            }
         }

         index = var10000 + var10001;
      }
   }
}

internal inline fun ByteArray.process2Utf8Bytes(beginIndex: Int, endIndex: Int, yield: (Int) -> Unit): Int {
   if (endIndex <= beginIndex + 1) {
      yield.invoke(65533);
      return 1;
   } else {
      var b0: Byte = `$this$process2Utf8Bytes`[beginIndex];
      var b1: Byte = `$this$process2Utf8Bytes`[beginIndex + 1];
      if ((`$this$process2Utf8Bytes`[beginIndex + 1] and 192) != 128) {
         yield.invoke(65533);
         return 1;
      } else {
         var var11: Int = 3968 xor b1 xor b0 shl 6;
         if ((3968 xor b1 xor b0 shl 6) < 128) {
            yield.invoke(65533);
         } else {
            yield.invoke(var11);
         }

         return 2;
      }
   }
}

internal inline fun ByteArray.process3Utf8Bytes(beginIndex: Int, endIndex: Int, yield: (Int) -> Unit): Int {
   label52:
   if (endIndex <= beginIndex + 2) {
      yield.invoke(65533);
      return if (endIndex > beginIndex + 1 && (`$this$process3Utf8Bytes`[beginIndex + 1] and 192) == 128) 2 else 1;
   } else {
      var b0: Byte = `$this$process3Utf8Bytes`[beginIndex];
      var b1: Byte = `$this$process3Utf8Bytes`[beginIndex + 1];
      if ((`$this$process3Utf8Bytes`[beginIndex + 1] and 192) != 128) {
         yield.invoke(65533);
         return 1;
      } else {
         var var14: Byte = `$this$process3Utf8Bytes`[beginIndex + 2];
         if ((`$this$process3Utf8Bytes`[beginIndex + 2] and 192) != 128) {
            yield.invoke(65533);
            return 2;
         } else {
            var var15: Int = -123008 xor var14 xor b1 shl 6 xor b0 shl 12;
            if ((-123008 xor var14 xor b1 shl 6 xor b0 shl 12) < 2048) {
               yield.invoke(65533);
            } else if (55296 <= var15 && var15 < 57344) {
               yield.invoke(65533);
            } else {
               yield.invoke(var15);
            }

            return 3;
         }
      }
   }
}

internal inline fun ByteArray.process4Utf8Bytes(beginIndex: Int, endIndex: Int, yield: (Int) -> Unit): Int {
   label75:
   if (endIndex <= beginIndex + 3) {
      yield.invoke(65533);
      if (endIndex <= beginIndex + 1 || (`$this$process4Utf8Bytes`[beginIndex + 1] and 192) != 128) {
         return 1;
      } else {
         return if (endIndex > beginIndex + 2 && (`$this$process4Utf8Bytes`[beginIndex + 2] and 192) == 128) 3 else 2;
      }
   } else {
      var b0: Byte = `$this$process4Utf8Bytes`[beginIndex];
      var b1: Byte = `$this$process4Utf8Bytes`[beginIndex + 1];
      if ((`$this$process4Utf8Bytes`[beginIndex + 1] and 192) != 128) {
         yield.invoke(65533);
         return 1;
      } else {
         var var17: Byte = `$this$process4Utf8Bytes`[beginIndex + 2];
         if ((`$this$process4Utf8Bytes`[beginIndex + 2] and 192) != 128) {
            yield.invoke(65533);
            return 2;
         } else {
            var var18: Byte = `$this$process4Utf8Bytes`[beginIndex + 3];
            if ((`$this$process4Utf8Bytes`[beginIndex + 3] and 192) != 128) {
               yield.invoke(65533);
               return 3;
            } else {
               var var22: Int = 3678080 xor var18 xor var17 shl 6 xor b1 shl 12 xor b0 shl 18;
               if ((3678080 xor var18 xor var17 shl 6 xor b1 shl 12 xor b0 shl 18) > 1114111) {
                  yield.invoke(65533);
               } else if (55296 <= var22 && var22 < 57344) {
                  yield.invoke(65533);
               } else if (var22 < 65536) {
                  yield.invoke(65533);
               } else {
                  yield.invoke(var22);
               }

               return 4;
            }
         }
      }
   }
}

@JvmOverloads
@JvmName(name = "size")
fun java.lang.String.size(beginIndex: Int): Long {
   return size$default(`$this$utf8Size`, beginIndex, 0, 2, null);
}

@JvmOverloads
@JvmName(name = "size")
fun java.lang.String.size(): Long {
   return size$default(`$this$utf8Size`, 0, 0, 3, null);
}

