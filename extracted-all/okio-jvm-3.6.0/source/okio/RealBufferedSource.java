package okio

import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.jvm.internal.SourceDebugExtension
import okio.internal.-Buffer
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nRealBufferedSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSource.kt\nokio/RealBufferedSource\n+ 2 RealBufferedSource.kt\nokio/internal/-RealBufferedSource\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,185:1\n62#1:191\n62#1:201\n62#1:208\n62#1:214\n62#1:216\n62#1:220\n62#1:225\n62#1:240\n62#1:244\n62#1:251\n62#1:264\n62#1:272\n62#1:273\n62#1:274\n62#1:280\n62#1:288\n62#1:301\n62#1:305\n62#1:306\n62#1:307\n62#1:308\n62#1:313\n62#1:325\n62#1:341\n62#1:351\n62#1:354\n62#1:357\n62#1:360\n62#1:363\n62#1:366\n62#1:372\n62#1:389\n62#1:409\n62#1:424\n62#1:441\n62#1:454\n62#1:475\n62#1:482\n38#2:186\n39#2,3:188\n42#2,6:192\n51#2:198\n52#2:200\n56#2,2:202\n60#2:204\n61#2,2:206\n63#2,3:209\n69#2,2:212\n74#2:215\n75#2:217\n79#2,2:218\n84#2:221\n86#2,2:223\n88#2,13:226\n107#2:239\n108#2:241\n112#2,2:242\n117#2,6:245\n123#2,9:252\n134#2,3:261\n137#2,5:265\n142#2:271\n146#2,5:275\n151#2,5:281\n158#2,2:286\n160#2,11:289\n174#2:300\n175#2:302\n179#2,2:303\n184#2,4:309\n188#2,6:314\n198#2:320\n199#2,3:322\n202#2,8:326\n210#2,3:335\n217#2,3:338\n220#2,7:342\n230#2,2:349\n235#2,2:352\n240#2,2:355\n245#2,2:358\n250#2,2:361\n255#2,2:364\n260#2,5:367\n265#2,11:373\n279#2,5:384\n284#2,14:390\n301#2,2:404\n303#2,2:407\n305#2,7:410\n314#2,2:417\n316#2,4:420\n320#2,11:425\n334#2,2:436\n337#2,2:439\n339#2,7:442\n350#2,2:449\n353#2,2:452\n355#2,7:455\n371#2:462\n373#2,11:464\n385#2:476\n389#2:477\n393#2,4:478\n397#2:483\n399#2:484\n401#2:485\n1#3:187\n1#3:199\n1#3:205\n1#3:222\n1#3:321\n1#3:406\n1#3:419\n1#3:438\n1#3:451\n1#3:463\n89#4:270\n89#4:334\n*S KotlinDebug\n*F\n+ 1 RealBufferedSource.kt\nokio/RealBufferedSource\n*L\n66#1:191\n67#1:201\n69#1:208\n70#1:214\n71#1:216\n72#1:220\n73#1:225\n74#1:240\n75#1:244\n77#1:251\n79#1:264\n82#1:272\n83#1:273\n87#1:274\n90#1:280\n91#1:288\n92#1:301\n93#1:305\n96#1:306\n97#1:307\n102#1:308\n105#1:313\n107#1:325\n108#1:341\n109#1:351\n110#1:354\n111#1:357\n112#1:360\n113#1:363\n114#1:366\n115#1:372\n116#1:389\n117#1:409\n121#1:424\n124#1:441\n127#1:454\n141#1:475\n181#1:482\n66#1:186\n66#1:188,3\n66#1:192,6\n67#1:198\n67#1:200\n68#1:202,2\n69#1:204\n69#1:206,2\n69#1:209,3\n70#1:212,2\n71#1:215\n71#1:217\n72#1:218,2\n73#1:221\n73#1:223,2\n73#1:226,13\n74#1:239\n74#1:241\n75#1:242,2\n77#1:245,6\n77#1:252,9\n79#1:261,3\n79#1:265,5\n79#1:271\n90#1:275,5\n90#1:281,5\n91#1:286,2\n91#1:289,11\n92#1:300\n92#1:302\n93#1:303,2\n105#1:309,4\n105#1:314,6\n107#1:320\n107#1:322,3\n107#1:326,8\n107#1:335,3\n108#1:338,3\n108#1:342,7\n109#1:349,2\n110#1:352,2\n111#1:355,2\n112#1:358,2\n113#1:361,2\n114#1:364,2\n115#1:367,5\n115#1:373,11\n116#1:384,5\n116#1:390,14\n117#1:404,2\n117#1:407,2\n117#1:410,7\n121#1:417,2\n121#1:420,4\n121#1:425,11\n124#1:436,2\n124#1:439,2\n124#1:442,7\n127#1:449,2\n127#1:452,2\n127#1:455,7\n141#1:462\n141#1:464,11\n141#1:476\n143#1:477\n181#1:478,4\n181#1:483\n182#1:484\n183#1:485\n66#1:187\n67#1:199\n69#1:205\n73#1:222\n107#1:321\n117#1:406\n121#1:419\n124#1:438\n127#1:451\n141#1:463\n79#1:270\n107#1:334\n*E\n"])
internal class RealBufferedSource(source: Source) : BufferedSource {
   public open val buffer: Buffer
      public open inline get() {
         return this.bufferField;
      }

   public final val bufferField: Buffer
   public final var closed: Boolean
      private set
   public final val source: Source

   init {
      this.source = source;
      this.bufferField = new Buffer();
   }

   public override fun buffer(): Buffer {
      return this.bufferField;
   }

   public override fun read(sink: Buffer, byteCount: Long): Long {
      if (byteCount < 0L) {
         throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
      } else {
         label19:
         if (this.closed) {
            throw new IllegalStateException("closed".toString());
         } else {
            return if (this.bufferField.size() == 0L && this.source.read(this.bufferField, 8192L) == -1L)
               -1L
               else
               this.bufferField.read(sink, Math.min(byteCount, this.bufferField.size()));
         }
      }
   }

   public override fun exhausted(): Boolean {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         return this.bufferField.exhausted() && this.source.read(this.bufferField, 8192L) == -1L;
      }
   }

   public override fun require(byteCount: Long) {
      if (!this.request(byteCount)) {
         throw new EOFException();
      }
   }

   public override fun request(byteCount: Long): Boolean {
      var `$this$commonRequest$iv`: RealBufferedSource = this;
      if (byteCount < 0L) {
         throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
      } else if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         var var10000: Boolean;
         while (true) {
            if (`$this$commonRequest$iv`.bufferField.size() < byteCount) {
               if (`$this$commonRequest$iv`.source.read(`$this$commonRequest$iv`.bufferField, 8192L) != -1L) {
                  continue;
               }

               var10000 = false;
               break;
            }

            var10000 = true;
            break;
         }

         return var10000;
      }
   }

   public override fun readByte(): Byte {
      this.require(1L);
      return this.bufferField.readByte();
   }

   public override fun readByteString(): ByteString {
      this.bufferField.writeAll(this.source);
      return this.bufferField.readByteString();
   }

   public override fun readByteString(byteCount: Long): ByteString {
      this.require(byteCount);
      return this.bufferField.readByteString(byteCount);
   }

   public override fun select(options: Options): Int {
      var `$this$commonSelect$iv`: RealBufferedSource = this;
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         while (true) {
            var `index$iv`: Int = -Buffer.selectPrefix(`$this$commonSelect$iv`.bufferField, options, true);
            switch (index$iv) {
               case -2:
                  if (`$this$commonSelect$iv`.source.read(`$this$commonSelect$iv`.bufferField, 8192L) != -1L) {
                     break;
                  }

                  return -1;
               case -1:
                  return -1;
               default:
                  `$this$commonSelect$iv`.bufferField.skip((long)options.getByteStrings$okio()[`index$iv`].size());
                  return `index$iv`;
            }
         }
      }
   }

   public override fun readByteArray(): ByteArray {
      this.bufferField.writeAll(this.source);
      return this.bufferField.readByteArray();
   }

   public override fun readByteArray(byteCount: Long): ByteArray {
      this.require(byteCount);
      return this.bufferField.readByteArray(byteCount);
   }

   public override fun read(sink: ByteArray): Int {
      return this.read(sink, 0, sink.length);
   }

   public override fun readFully(sink: ByteArray) {
      var `$this$commonReadFully$iv`: RealBufferedSource = this;

      try {
         `$this$commonReadFully$iv`.require((long)sink.length);
      } catch (EOFException var9) {
         var `$i$f$getBuffer`: Int = 0;

         while (true) {
            if (`$this$commonReadFully$iv`.bufferField.size() <= 0L) {
               throw var9;
            }

            var `read$iv`: Int = `$this$commonReadFully$iv`.bufferField.read(sink, `$i$f$getBuffer`, (int)`$this$commonReadFully$iv`.bufferField.size());
            if (`read$iv` == -1) {
               throw new AssertionError();
            }

            `$i$f$getBuffer` += `read$iv`;
         }
      }

      this.bufferField.readFully(sink);
   }

   public override fun read(sink: ByteArray, offset: Int, byteCount: Int): Int {
      -SegmentedByteString.checkOffsetAndCount((long)sink.length, (long)offset, (long)byteCount);
      return if (this.bufferField.size() == 0L && this.source.read(this.bufferField, 8192L) == -1L)
         -1
         else
         this.bufferField.read(sink, offset, (int)Math.min((long)byteCount, this.bufferField.size()));
   }

   public override fun read(sink: ByteBuffer): Int {
      return if (this.bufferField.size() == 0L && this.source.read(this.bufferField, 8192L) == -1L) -1 else this.bufferField.read(sink);
   }

   public override fun readFully(sink: Buffer, byteCount: Long) {
      var `$this$commonReadFully$iv`: RealBufferedSource = this;

      try {
         `$this$commonReadFully$iv`.require(byteCount);
      } catch (EOFException var9) {
         sink.writeAll(this.bufferField);
         throw var9;
      }

      this.bufferField.readFully(sink, byteCount);
   }

   public override fun readAll(sink: Sink): Long {
      var `$this$commonReadAll$iv`: RealBufferedSource = this;
      var `totalBytesWritten$iv`: Long = 0L;

      while (true) {
         if (`$this$commonReadAll$iv`.source.read(`$this$commonReadAll$iv`.bufferField, 8192L) == -1L) {
            if (`$this$commonReadAll$iv`.bufferField.size() > 0L) {
               `totalBytesWritten$iv` += `$this$commonReadAll$iv`.bufferField.size();
               sink.write(`$this$commonReadAll$iv`.bufferField, `$this$commonReadAll$iv`.bufferField.size());
            }

            return `totalBytesWritten$iv`;
         }

         var `emitByteCount$iv`: Long = `$this$commonReadAll$iv`.bufferField.completeSegmentByteCount();
         if (`emitByteCount$iv` > 0L) {
            `totalBytesWritten$iv` += `emitByteCount$iv`;
            sink.write(`$this$commonReadAll$iv`.bufferField, `emitByteCount$iv`);
         }
      }
   }

   public override fun readUtf8(): String {
      this.bufferField.writeAll(this.source);
      return this.bufferField.readUtf8();
   }

   public override fun readUtf8(byteCount: Long): String {
      this.require(byteCount);
      return this.bufferField.readUtf8(byteCount);
   }

   public override fun readString(charset: Charset): String {
      this.bufferField.writeAll(this.source);
      return this.bufferField.readString(charset);
   }

   public override fun readString(byteCount: Long, charset: Charset): String {
      this.require(byteCount);
      return this.bufferField.readString(byteCount, charset);
   }

   public override fun readUtf8Line(): String? {
      var `newline$iv`: Long = this.indexOf((byte)10);
      return if (`newline$iv` == -1L)
         (if (this.bufferField.size() != 0L) this.readUtf8(this.bufferField.size()) else null)
         else
         -Buffer.readUtf8Line(this.bufferField, `newline$iv`);
   }

   public override fun readUtf8LineStrict(): String {
      return this.readUtf8LineStrict(java.lang.Long.MAX_VALUE);
   }

   public override fun readUtf8LineStrict(limit: Long): String {
      if (limit < 0L) {
         throw new IllegalArgumentException(("limit < 0: " + limit).toString());
      } else {
         label37: {
            var `scanLength$iv`: Long = if (limit == java.lang.Long.MAX_VALUE) java.lang.Long.MAX_VALUE else limit + 1L;
            var `newline$iv`: Long = this.indexOf((byte)10, 0L, if (limit == java.lang.Long.MAX_VALUE) java.lang.Long.MAX_VALUE else limit + 1L);
            var var10000: java.lang.String;
            if (`newline$iv` != -1L) {
               var10000 = -Buffer.readUtf8Line(this.bufferField, `newline$iv`);
            } else {
               if (`scanLength$iv` >= java.lang.Long.MAX_VALUE
                  || !this.request(`scanLength$iv`)
                  || this.bufferField.getByte(`scanLength$iv` - 1L) != 13
                  || !this.request(`scanLength$iv` + 1L)
                  || this.bufferField.getByte(`scanLength$iv`) != 10) {
                  break label37;
               }

               var10000 = -Buffer.readUtf8Line(this.bufferField, `scanLength$iv`);
            }

            return var10000;
         }

         var `data$iv`: Buffer = new Buffer();
         this.bufferField.copyTo(`data$iv`, 0L, Math.min((long)32, this.bufferField.size()));
         throw new EOFException("\\n not found: limit=" + Math.min(this.bufferField.size(), limit) + " content=" + `data$iv`.readByteString().hex() + '…');
      }
   }

   public override fun readUtf8CodePoint(): Int {
      this.require(1L);
      var `b0$iv`: Int = this.bufferField.getByte(0L);
      if ((`b0$iv` and 224) == 192) {
         this.require(2L);
      } else if ((`b0$iv` and 240) == 224) {
         this.require(3L);
      } else if ((`b0$iv` and 248) == 240) {
         this.require(4L);
      }

      return this.bufferField.readUtf8CodePoint();
   }

   public override fun readShort(): Short {
      this.require(2L);
      return this.bufferField.readShort();
   }

   public override fun readShortLe(): Short {
      this.require(2L);
      return this.bufferField.readShortLe();
   }

   public override fun readInt(): Int {
      this.require(4L);
      return this.bufferField.readInt();
   }

   public override fun readIntLe(): Int {
      this.require(4L);
      return this.bufferField.readIntLe();
   }

   public override fun readLong(): Long {
      this.require(8L);
      return this.bufferField.readLong();
   }

   public override fun readLongLe(): Long {
      this.require(8L);
      return this.bufferField.readLongLe();
   }

   public override fun readDecimalLong(): Long {
      var `$this$commonReadDecimalLong$iv`: RealBufferedSource = this;
      this.require(1L);

      for (long pos$iv = 0L; $this$commonReadDecimalLong$iv.request(pos$iv + 1L); pos$iv++) {
         var `this_$iv$iv`: Byte = `$this$commonReadDecimalLong$iv`.bufferField.getByte(`pos$iv`);
         if ((`this_$iv$iv` < 48 || `this_$iv$iv` > 57) && (`pos$iv` != 0L || `this_$iv$iv` != 45)) {
            if (`pos$iv` == 0L) {
               var var10002: StringBuilder = new StringBuilder().append("Expected a digit or '-' but was 0x");
               var var10003: java.lang.String = Integer.toString(`this_$iv$iv`, CharsKt.checkRadix(CharsKt.checkRadix(16)));
               throw new NumberFormatException(var10002.append(var10003).toString());
            }
            break;
         }
      }

      return `$this$commonReadDecimalLong$iv`.bufferField.readDecimalLong();
   }

   public override fun readHexadecimalUnsignedLong(): Long {
      var `$this$commonReadHexadecimalUnsignedLong$iv`: RealBufferedSource = this;
      this.require(1L);

      for (int pos$iv = 0; $this$commonReadHexadecimalUnsignedLong$iv.request((long)(pos$iv + 1)); pos$iv++) {
         var `this_$iv$iv`: Byte = `$this$commonReadHexadecimalUnsignedLong$iv`.bufferField.getByte((long)`pos$iv`);
         if ((`this_$iv$iv` < 48 || `this_$iv$iv` > 57) && (`this_$iv$iv` < 97 || `this_$iv$iv` > 102) && (`this_$iv$iv` < 65 || `this_$iv$iv` > 70)) {
            if (`pos$iv` == 0) {
               var var10002: StringBuilder = new StringBuilder().append("Expected leading [0-9a-fA-F] character but was 0x");
               var var10003: java.lang.String = Integer.toString(`this_$iv$iv`, CharsKt.checkRadix(CharsKt.checkRadix(16)));
               throw new NumberFormatException(var10002.append(var10003).toString());
            }
            break;
         }
      }

      return `$this$commonReadHexadecimalUnsignedLong$iv`.bufferField.readHexadecimalUnsignedLong();
   }

   public override fun skip(byteCount: Long) {
      var `$this$commonSkip$iv`: RealBufferedSource = this;
      var `byteCount$iv`: Long = byteCount;
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         while (byteCount$iv > 0L) {
            if (`$this$commonSkip$iv`.bufferField.size() == 0L && `$this$commonSkip$iv`.source.read(`$this$commonSkip$iv`.bufferField, 8192L) == -1L) {
               throw new EOFException();
            }

            var `toSkip$iv`: Long = Math.min(`byteCount$iv`, `$this$commonSkip$iv`.bufferField.size());
            `$this$commonSkip$iv`.bufferField.skip(`toSkip$iv`);
            `byteCount$iv` -= `toSkip$iv`;
         }
      }
   }

   public override fun indexOf(b: Byte): Long {
      return this.indexOf(b, 0L, java.lang.Long.MAX_VALUE);
   }

   public override fun indexOf(b: Byte, fromIndex: Long): Long {
      return this.indexOf(b, fromIndex, java.lang.Long.MAX_VALUE);
   }

   public override fun indexOf(b: Byte, fromIndex: Long, toIndex: Long): Long {
      var `$this$commonIndexOf$iv`: RealBufferedSource = this;
      var var23: Long = fromIndex;
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else if (0L > fromIndex || fromIndex > toIndex) {
         throw new IllegalArgumentException(("fromIndex=" + fromIndex + " toIndex=" + toIndex).toString());
      } else {
         var var10000: Long;
         while (true) {
            if (var23 < toIndex) {
               var `result$iv`: Long = `$this$commonIndexOf$iv`.bufferField.indexOf(b, var23, toIndex);
               if (`result$iv` != -1L) {
                  var10000 = `result$iv`;
                  break;
               }

               var `lastBufferSize$iv`: Long = `$this$commonIndexOf$iv`.bufferField.size();
               if (`lastBufferSize$iv` < toIndex && `$this$commonIndexOf$iv`.source.read(`$this$commonIndexOf$iv`.bufferField, 8192L) != -1L) {
                  var23 = Math.max(var23, `lastBufferSize$iv`);
                  continue;
               }

               var10000 = -1L;
               break;
            }

            var10000 = -1L;
            break;
         }

         return var10000;
      }
   }

   public override fun indexOf(bytes: ByteString): Long {
      return this.indexOf(bytes, 0L);
   }

   public override fun indexOf(bytes: ByteString, fromIndex: Long): Long {
      var `$this$commonIndexOf$iv`: RealBufferedSource = this;
      var `fromIndex$iv`: Long = fromIndex;
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         var var10000: Long;
         while (true) {
            var `result$iv`: Long = `$this$commonIndexOf$iv`.bufferField.indexOf(bytes, `fromIndex$iv`);
            if (`result$iv` != -1L) {
               var10000 = `result$iv`;
               break;
            }

            var `lastBufferSize$iv`: Long = `$this$commonIndexOf$iv`.bufferField.size();
            if (`$this$commonIndexOf$iv`.source.read(`$this$commonIndexOf$iv`.bufferField, 8192L) == -1L) {
               var10000 = -1L;
               break;
            }

            `fromIndex$iv` = Math.max(`fromIndex$iv`, `lastBufferSize$iv` - (long)bytes.size() + 1L);
         }

         return var10000;
      }
   }

   public override fun indexOfElement(targetBytes: ByteString): Long {
      return this.indexOfElement(targetBytes, 0L);
   }

   public override fun indexOfElement(targetBytes: ByteString, fromIndex: Long): Long {
      var `$this$commonIndexOfElement$iv`: RealBufferedSource = this;
      var `fromIndex$iv`: Long = fromIndex;
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         var var10000: Long;
         while (true) {
            var `result$iv`: Long = `$this$commonIndexOfElement$iv`.bufferField.indexOfElement(targetBytes, `fromIndex$iv`);
            if (`result$iv` != -1L) {
               var10000 = `result$iv`;
               break;
            }

            var `lastBufferSize$iv`: Long = `$this$commonIndexOfElement$iv`.bufferField.size();
            if (`$this$commonIndexOfElement$iv`.source.read(`$this$commonIndexOfElement$iv`.bufferField, 8192L) == -1L) {
               var10000 = -1L;
               break;
            }

            `fromIndex$iv` = Math.max(`fromIndex$iv`, `lastBufferSize$iv`);
         }

         return var10000;
      }
   }

   public override fun rangeEquals(offset: Long, bytes: ByteString): Boolean {
      return this.rangeEquals(offset, bytes, 0, bytes.size());
   }

   public override fun rangeEquals(offset: Long, bytes: ByteString, bytesOffset: Int, byteCount: Int): Boolean {
      var `$this$commonRangeEquals$iv`: RealBufferedSource = this;
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         var var10000: Boolean;
         if (offset >= 0L && bytesOffset >= 0 && byteCount >= 0 && bytes.size() - bytesOffset >= byteCount) {
            var `i$iv`: Int = 0;

            while (true) {
               if (`i$iv` >= byteCount) {
                  var10000 = true;
                  break;
               }

               var `bufferOffset$iv`: Long = offset + (long)`i$iv`;
               if (!`$this$commonRangeEquals$iv`.request(offset + (long)`i$iv` + 1L)) {
                  var10000 = false;
                  break;
               }

               if (`$this$commonRangeEquals$iv`.bufferField.getByte(`bufferOffset$iv`) != bytes.getByte(bytesOffset + `i$iv`)) {
                  var10000 = false;
                  break;
               }

               `i$iv`++;
            }
         } else {
            var10000 = false;
         }

         return var10000;
      }
   }

   public override fun peek(): BufferedSource {
      return Okio.buffer(new PeekSource(this));
   }

   public override fun inputStream(): InputStream {
      return new InputStream(this) {
         {
            this.this$0 = `$receiver`;
         }

         @Override
         public int read() {
            label13:
            if (this.this$0.closed) {
               throw new IOException("closed");
            } else {
               return if (this.this$0.bufferField.size() == 0L && this.this$0.source.read(this.this$0.bufferField, 8192L) == -1L)
                  -1
                  else
                  this.this$0.bufferField.readByte() and 255;
            }
         }

         @Override
         public int read(@NotNull byte[] data, int offset, int byteCount) {
            label13:
            if (this.this$0.closed) {
               throw new IOException("closed");
            } else {
               -SegmentedByteString.checkOffsetAndCount((long)data.length, (long)offset, (long)byteCount);
               return if (this.this$0.bufferField.size() == 0L && this.this$0.source.read(this.this$0.bufferField, 8192L) == -1L)
                  -1
                  else
                  this.this$0.bufferField.read(data, offset, byteCount);
            }
         }

         @Override
         public int available() {
            if (this.this$0.closed) {
               throw new IOException("closed");
            } else {
               return (int)Math.min(this.this$0.bufferField.size(), (long)Integer.MAX_VALUE);
            }
         }

         @Override
         public void close() {
            this.this$0.close();
         }

         @NotNull
         @Override
         public java.lang.String toString() {
            return this.this$0 + ".inputStream()";
         }
      };
   }

   public override fun isOpen(): Boolean {
      return !this.closed;
   }

   public override fun close() {
      if (!this.closed) {
         this.closed = true;
         this.source.close();
         this.bufferField.clear();
      }
   }

   public override fun timeout(): Timeout {
      return this.source.timeout();
   }

   public override fun toString(): String {
      return "buffer(" + this.source + ')';
   }
}
