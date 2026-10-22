@file:JvmName(name = "-RealBufferedSink")

@file:SourceDebugExtension(["SMAP\nRealBufferedSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSink.kt\nokio/internal/-RealBufferedSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 RealBufferedSink.kt\nokio/RealBufferedSink\n*L\n1#1,219:1\n1#2:220\n51#3:221\n51#3:222\n51#3:223\n51#3:224\n51#3:225\n51#3:226\n51#3:227\n51#3:228\n51#3:229\n51#3:230\n51#3:231\n51#3:232\n51#3:233\n51#3:234\n51#3:235\n51#3:236\n51#3:237\n51#3:238\n51#3:239\n51#3:240\n51#3:241\n51#3:242\n51#3:243\n51#3:244\n51#3:245\n51#3:246\n51#3:247\n*S KotlinDebug\n*F\n+ 1 RealBufferedSink.kt\nokio/internal/-RealBufferedSink\n*L\n35#1:221\n41#1:222\n51#1:223\n57#1:224\n67#1:225\n73#1:226\n79#1:227\n89#1:228\n96#1:229\n107#1:230\n117#1:231\n123#1:232\n129#1:233\n135#1:234\n141#1:235\n147#1:236\n153#1:237\n159#1:238\n165#1:239\n171#1:240\n172#1:241\n178#1:242\n179#1:243\n185#1:244\n186#1:245\n198#1:246\n199#1:247\n*E\n"])

package okio.internal

import java.io.EOFException
import kotlin.jvm.internal.SourceDebugExtension
import okio.Buffer
import okio.BufferedSink
import okio.ByteString
import okio.RealBufferedSink
import okio.Source
import okio.Timeout

internal inline fun RealBufferedSink.commonWrite(source: Buffer, byteCount: Long) {
   if (`$this$commonWrite`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWrite`.bufferField.write(source, byteCount);
      `$this$commonWrite`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWrite(byteString: ByteString): BufferedSink {
   if (`$this$commonWrite`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWrite`.bufferField.write(byteString);
      return `$this$commonWrite`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWrite(byteString: ByteString, offset: Int, byteCount: Int): BufferedSink {
   if (`$this$commonWrite`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWrite`.bufferField.write(byteString, offset, byteCount);
      return `$this$commonWrite`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteUtf8(string: String): BufferedSink {
   if (`$this$commonWriteUtf8`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteUtf8`.bufferField.writeUtf8(string);
      return `$this$commonWriteUtf8`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteUtf8(string: String, beginIndex: Int, endIndex: Int): BufferedSink {
   if (`$this$commonWriteUtf8`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteUtf8`.bufferField.writeUtf8(string, beginIndex, endIndex);
      return `$this$commonWriteUtf8`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteUtf8CodePoint(codePoint: Int): BufferedSink {
   if (`$this$commonWriteUtf8CodePoint`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteUtf8CodePoint`.bufferField.writeUtf8CodePoint(codePoint);
      return `$this$commonWriteUtf8CodePoint`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWrite(source: ByteArray): BufferedSink {
   if (`$this$commonWrite`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWrite`.bufferField.write(source);
      return `$this$commonWrite`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWrite(source: ByteArray, offset: Int, byteCount: Int): BufferedSink {
   if (`$this$commonWrite`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWrite`.bufferField.write(source, offset, byteCount);
      return `$this$commonWrite`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteAll(source: Source): Long {
   var totalBytesRead: Long = 0L;

   while (true) {
      var readCount: Long = source.read(`$this$commonWriteAll`.bufferField, 8192L);
      if (readCount == -1L) {
         return totalBytesRead;
      }

      totalBytesRead += readCount;
      `$this$commonWriteAll`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWrite(source: Source, byteCount: Long): BufferedSink {
   var byteCountx: Long = byteCount;

   while (byteCountx > 0L) {
      var read: Long = source.read(`$this$commonWrite`.bufferField, byteCountx);
      if (read == -1L) {
         throw new EOFException();
      }

      byteCountx -= read;
      `$this$commonWrite`.emitCompleteSegments();
   }

   return `$this$commonWrite`;
}

internal inline fun RealBufferedSink.commonWriteByte(b: Int): BufferedSink {
   if (`$this$commonWriteByte`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteByte`.bufferField.writeByte(b);
      return `$this$commonWriteByte`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteShort(s: Int): BufferedSink {
   if (`$this$commonWriteShort`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteShort`.bufferField.writeShort(s);
      return `$this$commonWriteShort`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteShortLe(s: Int): BufferedSink {
   if (`$this$commonWriteShortLe`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteShortLe`.bufferField.writeShortLe(s);
      return `$this$commonWriteShortLe`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteInt(i: Int): BufferedSink {
   if (`$this$commonWriteInt`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteInt`.bufferField.writeInt(i);
      return `$this$commonWriteInt`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteIntLe(i: Int): BufferedSink {
   if (`$this$commonWriteIntLe`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteIntLe`.bufferField.writeIntLe(i);
      return `$this$commonWriteIntLe`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteLong(v: Long): BufferedSink {
   if (`$this$commonWriteLong`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteLong`.bufferField.writeLong(v);
      return `$this$commonWriteLong`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteLongLe(v: Long): BufferedSink {
   if (`$this$commonWriteLongLe`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteLongLe`.bufferField.writeLongLe(v);
      return `$this$commonWriteLongLe`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteDecimalLong(v: Long): BufferedSink {
   if (`$this$commonWriteDecimalLong`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteDecimalLong`.bufferField.writeDecimalLong(v);
      return `$this$commonWriteDecimalLong`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonWriteHexadecimalUnsignedLong(v: Long): BufferedSink {
   if (`$this$commonWriteHexadecimalUnsignedLong`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      `$this$commonWriteHexadecimalUnsignedLong`.bufferField.writeHexadecimalUnsignedLong(v);
      return `$this$commonWriteHexadecimalUnsignedLong`.emitCompleteSegments();
   }
}

internal inline fun RealBufferedSink.commonEmitCompleteSegments(): BufferedSink {
   if (`$this$commonEmitCompleteSegments`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      var byteCount: Long = `$this$commonEmitCompleteSegments`.bufferField.completeSegmentByteCount();
      if (byteCount > 0L) {
         `$this$commonEmitCompleteSegments`.sink.write(`$this$commonEmitCompleteSegments`.bufferField, byteCount);
      }

      return `$this$commonEmitCompleteSegments`;
   }
}

internal inline fun RealBufferedSink.commonEmit(): BufferedSink {
   if (`$this$commonEmit`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      var byteCount: Long = `$this$commonEmit`.bufferField.size();
      if (byteCount > 0L) {
         `$this$commonEmit`.sink.write(`$this$commonEmit`.bufferField, byteCount);
      }

      return `$this$commonEmit`;
   }
}

internal inline fun RealBufferedSink.commonFlush() {
   if (`$this$commonFlush`.closed) {
      throw new IllegalStateException("closed".toString());
   } else {
      if (`$this$commonFlush`.bufferField.size() > 0L) {
         `$this$commonFlush`.sink.write(`$this$commonFlush`.bufferField, `$this$commonFlush`.bufferField.size());
      }

      `$this$commonFlush`.sink.flush();
   }
}

internal inline fun RealBufferedSink.commonClose() {
   if (!`$this$commonClose`.closed) {
      var thrown: java.lang.Throwable = null;

      try {
         if (`$this$commonClose`.bufferField.size() > 0L) {
            `$this$commonClose`.sink.write(`$this$commonClose`.bufferField, `$this$commonClose`.bufferField.size());
         }
      } catch (java.lang.Throwable var5) {
         thrown = var5;
      }

      try {
         `$this$commonClose`.sink.close();
      } catch (java.lang.Throwable var6) {
         if (thrown == null) {
            thrown = var6;
         }
      }

      `$this$commonClose`.closed = true;
      if (thrown != null) {
         throw thrown;
      }
   }
}

internal inline fun RealBufferedSink.commonTimeout(): Timeout {
   return `$this$commonTimeout`.sink.timeout();
}

internal inline fun RealBufferedSink.commonToString(): String {
   return "buffer(" + `$this$commonToString`.sink + ')';
}

