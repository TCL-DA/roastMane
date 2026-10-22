@file:JvmName(name = "-RealBufferedSource")

@file:SourceDebugExtension(["SMAP\nRealBufferedSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSource.kt\nokio/internal/-RealBufferedSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 RealBufferedSource.kt\nokio/RealBufferedSource\n+ 4 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,402:1\n1#2:403\n62#3:404\n62#3:405\n62#3:406\n62#3:407\n62#3:408\n62#3:409\n62#3:410\n62#3:411\n62#3:412\n62#3:413\n62#3:414\n62#3:415\n62#3:416\n62#3:417\n62#3:418\n62#3:419\n62#3:420\n62#3:421\n62#3:422\n62#3:423\n62#3:424\n62#3:425\n62#3:426\n62#3:428\n62#3:429\n62#3:430\n62#3:431\n62#3:432\n62#3:433\n62#3:434\n62#3:435\n62#3:436\n62#3:437\n62#3:438\n62#3:439\n62#3:440\n62#3:441\n62#3:442\n62#3:443\n62#3:444\n62#3:445\n62#3:446\n62#3:447\n62#3:449\n62#3:450\n62#3:451\n62#3:452\n62#3:453\n62#3:454\n62#3:455\n62#3:456\n62#3:457\n62#3:458\n62#3:459\n62#3:460\n62#3:461\n62#3:462\n62#3:463\n62#3:464\n62#3:465\n62#3:466\n62#3:467\n62#3:468\n62#3:469\n62#3:470\n62#3:471\n62#3:472\n62#3:473\n62#3:474\n62#3:475\n89#4:427\n89#4:448\n*S KotlinDebug\n*F\n+ 1 RealBufferedSource.kt\nokio/internal/-RealBufferedSource\n*L\n41#1:404\n42#1:405\n46#1:406\n47#1:407\n52#1:408\n62#1:409\n63#1:410\n70#1:411\n74#1:412\n75#1:413\n80#1:414\n87#1:415\n94#1:416\n99#1:417\n107#1:418\n108#1:419\n113#1:420\n122#1:421\n123#1:422\n130#1:423\n136#1:424\n137#1:425\n141#1:426\n142#1:428\n150#1:429\n154#1:430\n159#1:431\n160#1:432\n163#1:433\n166#1:434\n167#1:435\n168#1:436\n174#1:437\n175#1:438\n180#1:439\n187#1:440\n188#1:441\n193#1:442\n201#1:443\n203#1:444\n204#1:445\n206#1:446\n209#1:447\n211#1:449\n219#1:450\n226#1:451\n231#1:452\n236#1:453\n241#1:454\n246#1:455\n251#1:456\n256#1:457\n264#1:458\n275#1:459\n283#1:460\n297#1:461\n304#1:462\n307#1:463\n308#1:464\n319#1:465\n324#1:466\n325#1:467\n338#1:468\n341#1:469\n342#1:470\n354#1:471\n357#1:472\n358#1:473\n383#1:474\n396#1:475\n141#1:427\n209#1:448\n*E\n"])

package okio.internal

import java.io.EOFException
import kotlin.jvm.internal.SourceDebugExtension
import okio.Buffer
import okio.BufferedSource
import okio.ByteString
import okio.Okio
import okio.Options
import okio.PeekSource
import okio.RealBufferedSource
import okio.Sink
import okio.Timeout

internal inline fun RealBufferedSource.commonRead(sink: Buffer, byteCount: Long): Long {
   if (byteCount < 0L) {
      throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
   } else {
      label21:
      if (`$this$commonRead`.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         return if (`$this$commonRead`.bufferField.size() == 0L && `$this$commonRead`.source.read(`$this$commonRead`.bufferField, 8192L) == -1L)
            -1L
            else
            `$this$commonRead`.bufferField.read(sink, Math.min(byteCount, `$this$commonRead`.bufferField.size()));
      }
   }
}

internal inline fun RealBufferedSource.commonExhausted(): Boolean {
   if (`$this$commonExhausted`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      return `$this$commonExhausted`.bufferField.exhausted() && `$this$commonExhausted`.source.read(`$this$commonExhausted`.bufferField, 8192L) == -1L;
   }
}

internal inline fun RealBufferedSource.commonRequire(byteCount: Long) {
   if (!`$this$commonRequire`.request(byteCount)) {
      throw new EOFException();
   }
}

internal inline fun RealBufferedSource.commonRequest(byteCount: Long): Boolean {
   if (byteCount < 0L) {
      throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
   } else if (`$this$commonRequest`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      do {
         if (`$this$commonRequest`.bufferField.size() >= byteCount) {
            return true;
         }
      } while ($this$commonRequest.source.read($this$commonRequest.bufferField, 8192L) != -1L);

      return false;
   }
}

internal inline fun RealBufferedSource.commonReadByte(): Byte {
   `$this$commonReadByte`.require(1L);
   return `$this$commonReadByte`.bufferField.readByte();
}

internal inline fun RealBufferedSource.commonReadByteString(): ByteString {
   `$this$commonReadByteString`.bufferField.writeAll(`$this$commonReadByteString`.source);
   return `$this$commonReadByteString`.bufferField.readByteString();
}

internal inline fun RealBufferedSource.commonReadByteString(byteCount: Long): ByteString {
   `$this$commonReadByteString`.require(byteCount);
   return `$this$commonReadByteString`.bufferField.readByteString(byteCount);
}

internal inline fun RealBufferedSource.commonSelect(options: Options): Int {
   if (`$this$commonSelect`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      label18:
      while (true) {
         var index: Int = -Buffer.selectPrefix(`$this$commonSelect`.bufferField, options, true);
         switch (index) {
            case -2:
               if (`$this$commonSelect`.source.read(`$this$commonSelect`.bufferField, 8192L) == -1L) {
                  break label18;
               }
               break;
            case -1:
               return -1;
            default:
               `$this$commonSelect`.bufferField.skip((long)options.getByteStrings$okio()[index].size());
               return index;
         }
      }

      return -1;
   }
}

internal inline fun RealBufferedSource.commonReadByteArray(): ByteArray {
   `$this$commonReadByteArray`.bufferField.writeAll(`$this$commonReadByteArray`.source);
   return `$this$commonReadByteArray`.bufferField.readByteArray();
}

internal inline fun RealBufferedSource.commonReadByteArray(byteCount: Long): ByteArray {
   `$this$commonReadByteArray`.require(byteCount);
   return `$this$commonReadByteArray`.bufferField.readByteArray(byteCount);
}

internal inline fun RealBufferedSource.commonReadFully(sink: ByteArray) {
   try {
      `$this$commonReadFully`.require((long)sink.length);
   } catch (EOFException var8) {
      var `$i$f$getBuffer`: Int = 0;

      while (true) {
         if (`$this$commonReadFully`.bufferField.size() <= 0L) {
            throw var8;
         }

         var read: Int = `$this$commonReadFully`.bufferField.read(sink, `$i$f$getBuffer`, (int)`$this$commonReadFully`.bufferField.size());
         if (read == -1) {
            throw new AssertionError();
         }

         `$i$f$getBuffer` += read;
      }
   }

   `$this$commonReadFully`.bufferField.readFully(sink);
}

internal inline fun RealBufferedSource.commonRead(sink: ByteArray, offset: Int, byteCount: Int): Int {
   okio.-SegmentedByteString.checkOffsetAndCount((long)sink.length, (long)offset, (long)byteCount);
   return if (`$this$commonRead`.bufferField.size() == 0L && `$this$commonRead`.source.read(`$this$commonRead`.bufferField, 8192L) == -1L)
      -1
      else
      `$this$commonRead`.bufferField.read(sink, offset, (int)Math.min((long)byteCount, `$this$commonRead`.bufferField.size()));
}

internal inline fun RealBufferedSource.commonReadFully(sink: Buffer, byteCount: Long) {
   try {
      `$this$commonReadFully`.require(byteCount);
   } catch (EOFException var8) {
      sink.writeAll(`$this$commonReadFully`.bufferField);
      throw var8;
   }

   `$this$commonReadFully`.bufferField.readFully(sink, byteCount);
}

internal inline fun RealBufferedSource.commonReadAll(sink: Sink): Long {
   var totalBytesWritten: Long = 0L;

   while (true) {
      if (`$this$commonReadAll`.source.read(`$this$commonReadAll`.bufferField, 8192L) == -1L) {
         if (`$this$commonReadAll`.bufferField.size() > 0L) {
            totalBytesWritten += `$this$commonReadAll`.bufferField.size();
            sink.write(`$this$commonReadAll`.bufferField, `$this$commonReadAll`.bufferField.size());
         }

         return totalBytesWritten;
      }

      var `this_$iv`: Long = `$this$commonReadAll`.bufferField.completeSegmentByteCount();
      if (`this_$iv` > 0L) {
         totalBytesWritten += `this_$iv`;
         sink.write(`$this$commonReadAll`.bufferField, `this_$iv`);
      }
   }
}

internal inline fun RealBufferedSource.commonReadUtf8(): String {
   `$this$commonReadUtf8`.bufferField.writeAll(`$this$commonReadUtf8`.source);
   return `$this$commonReadUtf8`.bufferField.readUtf8();
}

internal inline fun RealBufferedSource.commonReadUtf8(byteCount: Long): String {
   `$this$commonReadUtf8`.require(byteCount);
   return `$this$commonReadUtf8`.bufferField.readUtf8(byteCount);
}

internal inline fun RealBufferedSource.commonReadUtf8Line(): String? {
   var newline: Long = `$this$commonReadUtf8Line`.indexOf((byte)10);
   return if (newline == -1L)
      (if (`$this$commonReadUtf8Line`.bufferField.size() != 0L) `$this$commonReadUtf8Line`.readUtf8(`$this$commonReadUtf8Line`.bufferField.size()) else null)
      else
      -Buffer.readUtf8Line(`$this$commonReadUtf8Line`.bufferField, newline);
}

internal inline fun RealBufferedSource.commonReadUtf8LineStrict(limit: Long): String {
   if (limit < 0L) {
      throw new IllegalArgumentException(("limit < 0: " + limit).toString());
   } else {
      var scanLength: Long = if (limit == java.lang.Long.MAX_VALUE) java.lang.Long.MAX_VALUE else limit + 1L;
      var newline: Long = `$this$commonReadUtf8LineStrict`.indexOf(
         (byte)10, 0L, if (limit == java.lang.Long.MAX_VALUE) java.lang.Long.MAX_VALUE else limit + 1L
      );
      if (newline != -1L) {
         return -Buffer.readUtf8Line(`$this$commonReadUtf8LineStrict`.bufferField, newline);
      } else if (scanLength < java.lang.Long.MAX_VALUE
         && `$this$commonReadUtf8LineStrict`.request(scanLength)
         && `$this$commonReadUtf8LineStrict`.bufferField.getByte(scanLength - 1L) == 13
         && `$this$commonReadUtf8LineStrict`.request(scanLength + 1L)
         && `$this$commonReadUtf8LineStrict`.bufferField.getByte(scanLength) == 10) {
         return -Buffer.readUtf8Line(`$this$commonReadUtf8LineStrict`.bufferField, scanLength);
      } else {
         var data: Buffer = new Buffer();
         `$this$commonReadUtf8LineStrict`.bufferField.copyTo(data, 0L, Math.min((long)32, `$this$commonReadUtf8LineStrict`.bufferField.size()));
         throw new EOFException(
            "\\n not found: limit=" + Math.min(`$this$commonReadUtf8LineStrict`.bufferField.size(), limit) + " content=" + data.readByteString().hex() + '…'
         );
      }
   }
}

internal inline fun RealBufferedSource.commonReadUtf8CodePoint(): Int {
   `$this$commonReadUtf8CodePoint`.require(1L);
   var b0: Int = `$this$commonReadUtf8CodePoint`.bufferField.getByte(0L);
   if ((b0 and 224) == 192) {
      `$this$commonReadUtf8CodePoint`.require(2L);
   } else if ((b0 and 240) == 224) {
      `$this$commonReadUtf8CodePoint`.require(3L);
   } else if ((b0 and 248) == 240) {
      `$this$commonReadUtf8CodePoint`.require(4L);
   }

   return `$this$commonReadUtf8CodePoint`.bufferField.readUtf8CodePoint();
}

internal inline fun RealBufferedSource.commonReadShort(): Short {
   `$this$commonReadShort`.require(2L);
   return `$this$commonReadShort`.bufferField.readShort();
}

internal inline fun RealBufferedSource.commonReadShortLe(): Short {
   `$this$commonReadShortLe`.require(2L);
   return `$this$commonReadShortLe`.bufferField.readShortLe();
}

internal inline fun RealBufferedSource.commonReadInt(): Int {
   `$this$commonReadInt`.require(4L);
   return `$this$commonReadInt`.bufferField.readInt();
}

internal inline fun RealBufferedSource.commonReadIntLe(): Int {
   `$this$commonReadIntLe`.require(4L);
   return `$this$commonReadIntLe`.bufferField.readIntLe();
}

internal inline fun RealBufferedSource.commonReadLong(): Long {
   `$this$commonReadLong`.require(8L);
   return `$this$commonReadLong`.bufferField.readLong();
}

internal inline fun RealBufferedSource.commonReadLongLe(): Long {
   `$this$commonReadLongLe`.require(8L);
   return `$this$commonReadLongLe`.bufferField.readLongLe();
}

internal inline fun RealBufferedSource.commonReadDecimalLong(): Long {
   `$this$commonReadDecimalLong`.require(1L);

   for (long pos = 0L; $this$commonReadDecimalLong.request(pos + 1L); pos++) {
      var `this_$iv`: Byte = `$this$commonReadDecimalLong`.bufferField.getByte(pos);
      if ((`this_$iv` < 48 || `this_$iv` > 57) && (pos != 0L || `this_$iv` != 45)) {
         if (pos == 0L) {
            var var10002: StringBuilder = new StringBuilder().append("Expected a digit or '-' but was 0x");
            var var10003: java.lang.String = Integer.toString(`this_$iv`, CharsKt.checkRadix(CharsKt.checkRadix(16)));
            throw new NumberFormatException(var10002.append(var10003).toString());
         }
         break;
      }
   }

   return `$this$commonReadDecimalLong`.bufferField.readDecimalLong();
}

internal inline fun RealBufferedSource.commonReadHexadecimalUnsignedLong(): Long {
   `$this$commonReadHexadecimalUnsignedLong`.require(1L);

   for (int pos = 0; $this$commonReadHexadecimalUnsignedLong.request((long)(pos + 1)); pos++) {
      var `this_$iv`: Byte = `$this$commonReadHexadecimalUnsignedLong`.bufferField.getByte((long)pos);
      if ((`this_$iv` < 48 || `this_$iv` > 57) && (`this_$iv` < 97 || `this_$iv` > 102) && (`this_$iv` < 65 || `this_$iv` > 70)) {
         if (pos == 0) {
            var var10002: StringBuilder = new StringBuilder().append("Expected leading [0-9a-fA-F] character but was 0x");
            var var10003: java.lang.String = Integer.toString(`this_$iv`, CharsKt.checkRadix(CharsKt.checkRadix(16)));
            throw new NumberFormatException(var10002.append(var10003).toString());
         }
         break;
      }
   }

   return `$this$commonReadHexadecimalUnsignedLong`.bufferField.readHexadecimalUnsignedLong();
}

internal inline fun RealBufferedSource.commonSkip(byteCount: Long) {
   var byteCountx: Long = byteCount;
   if (`$this$commonSkip`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      while (byteCountx > 0L) {
         if (`$this$commonSkip`.bufferField.size() == 0L && `$this$commonSkip`.source.read(`$this$commonSkip`.bufferField, 8192L) == -1L) {
            throw new EOFException();
         }

         var toSkip: Long = Math.min(byteCountx, `$this$commonSkip`.bufferField.size());
         `$this$commonSkip`.bufferField.skip(toSkip);
         byteCountx -= toSkip;
      }
   }
}

internal inline fun RealBufferedSource.commonIndexOf(b: Byte, fromIndex: Long, toIndex: Long): Long {
   var var19: Long = fromIndex;
   if (`$this$commonIndexOf`.closed) {
      throw new IllegalStateException("closed".toString());
   } else if (0L > fromIndex || fromIndex > toIndex) {
      throw new IllegalArgumentException(("fromIndex=" + fromIndex + " toIndex=" + toIndex).toString());
   } else {
      while (true) {
         if (var19 >= toIndex) {
            return -1L;
         }

         var result: Long = `$this$commonIndexOf`.bufferField.indexOf(b, var19, toIndex);
         if (result != -1L) {
            return result;
         }

         var lastBufferSize: Long = `$this$commonIndexOf`.bufferField.size();
         if (lastBufferSize >= toIndex || `$this$commonIndexOf`.source.read(`$this$commonIndexOf`.bufferField, 8192L) == -1L) {
            break;
         }

         var19 = Math.max(var19, lastBufferSize);
      }

      return -1L;
   }
}

internal inline fun RealBufferedSource.commonIndexOf(bytes: ByteString, fromIndex: Long): Long {
   var fromIndexx: Long = fromIndex;
   if (`$this$commonIndexOf`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      while (true) {
         var result: Long = `$this$commonIndexOf`.bufferField.indexOf(bytes, fromIndexx);
         if (result != -1L) {
            return result;
         }

         var lastBufferSize: Long = `$this$commonIndexOf`.bufferField.size();
         if (`$this$commonIndexOf`.source.read(`$this$commonIndexOf`.bufferField, 8192L) == -1L) {
            return -1L;
         }

         fromIndexx = Math.max(fromIndexx, lastBufferSize - (long)bytes.size() + 1L);
      }
   }
}

internal inline fun RealBufferedSource.commonIndexOfElement(targetBytes: ByteString, fromIndex: Long): Long {
   var fromIndexx: Long = fromIndex;
   if (`$this$commonIndexOfElement`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      while (true) {
         var result: Long = `$this$commonIndexOfElement`.bufferField.indexOfElement(targetBytes, fromIndexx);
         if (result != -1L) {
            return result;
         }

         var lastBufferSize: Long = `$this$commonIndexOfElement`.bufferField.size();
         if (`$this$commonIndexOfElement`.source.read(`$this$commonIndexOfElement`.bufferField, 8192L) == -1L) {
            return -1L;
         }

         fromIndexx = Math.max(fromIndexx, lastBufferSize);
      }
   }
}

internal inline fun RealBufferedSource.commonRangeEquals(offset: Long, bytes: ByteString, bytesOffset: Int, byteCount: Int): Boolean {
   if (`$this$commonRangeEquals`.closed) {
      throw new IllegalStateException("closed".toString());
   } else if (offset >= 0L && bytesOffset >= 0 && byteCount >= 0 && bytes.size() - bytesOffset >= byteCount) {
      for (int i = 0; i < byteCount; i++) {
         var bufferOffset: Long = offset + (long)i;
         if (!`$this$commonRangeEquals`.request(offset + (long)i + 1L)) {
            return false;
         }

         if (`$this$commonRangeEquals`.bufferField.getByte(bufferOffset) != bytes.getByte(bytesOffset + i)) {
            return false;
         }
      }

      return true;
   } else {
      return false;
   }
}

internal inline fun RealBufferedSource.commonPeek(): BufferedSource {
   return Okio.buffer(new PeekSource(`$this$commonPeek`));
}

internal inline fun RealBufferedSource.commonClose() {
   if (!`$this$commonClose`.closed) {
      `$this$commonClose`.closed = true;
      `$this$commonClose`.source.close();
      `$this$commonClose`.bufferField.clear();
   }
}

internal inline fun RealBufferedSource.commonTimeout(): Timeout {
   return `$this$commonTimeout`.source.timeout();
}

internal inline fun RealBufferedSource.commonToString(): String {
   return "buffer(" + `$this$commonToString`.source + ')';
}

