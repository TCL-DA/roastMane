package okio

import java.io.Closeable
import java.io.EOFException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel
import java.nio.charset.Charset
import java.security.InvalidKeyException
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.jvm.internal.SourceDebugExtension
import okio.internal.-Buffer
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nBuffer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Buffer.kt\nokio/Buffer\n+ 2 Util.kt\nokio/-SegmentedByteString\n+ 3 Buffer.kt\nokio/internal/-Buffer\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,641:1\n89#2:642\n86#2:675\n86#2:677\n74#2:737\n74#2:763\n83#2:802\n77#2:813\n89#2:1003\n74#2:1018\n86#2:1122\n89#2:1615\n244#3,32:643\n279#3,10:678\n292#3,18:688\n414#3,2:706\n112#3:708\n416#3:709\n114#3,18:710\n313#3,9:728\n322#3,15:738\n340#3,10:753\n350#3,3:764\n348#3,25:767\n376#3,10:792\n386#3:803\n384#3,9:804\n393#3,7:814\n391#3,20:821\n682#3,60:841\n745#3,56:901\n803#3:957\n806#3:958\n807#3,6:960\n817#3,7:966\n827#3,6:973\n835#3,5:979\n867#3,6:984\n877#3:990\n878#3,11:992\n889#3,5:1004\n898#3,9:1009\n908#3,61:1019\n633#3:1080\n636#3:1081\n637#3,5:1083\n644#3:1088\n647#3,7:1089\n656#3,20:1096\n420#3:1116\n423#3,5:1117\n428#3,10:1123\n439#3,7:1133\n444#3,2:1140\n973#3:1142\n974#3,87:1144\n1064#3,48:1231\n603#3:1279\n610#3,21:1280\n1115#3,7:1301\n1125#3,7:1308\n1135#3,4:1315\n1142#3,8:1319\n1153#3,10:1327\n1166#3,14:1337\n449#3,91:1351\n543#3,40:1442\n586#3:1482\n588#3,13:1484\n1183#3:1497\n1234#3:1498\n1235#3,39:1500\n1276#3,2:1539\n1278#3,4:1542\n1285#3,3:1546\n1289#3,4:1550\n112#3:1554\n1293#3,22:1555\n114#3,18:1577\n1319#3,2:1595\n1321#3,3:1598\n112#3:1601\n1324#3,13:1602\n1337#3,13:1616\n114#3,18:1629\n1354#3,2:1647\n1357#3:1650\n112#3:1651\n1358#3,50:1652\n114#3,18:1702\n1417#3,14:1720\n1434#3,32:1734\n1469#3,12:1766\n1484#3,18:1778\n1506#3:1796\n1507#3:1798\n1512#3,34:1799\n1#4:676\n1#4:959\n1#4:991\n1#4:1082\n1#4:1143\n1#4:1483\n1#4:1499\n1#4:1541\n1#4:1549\n1#4:1597\n1#4:1649\n1#4:1797\n*S KotlinDebug\n*F\n+ 1 Buffer.kt\nokio/Buffer\n*L\n167#1:642\n197#1:675\n235#1:677\n261#1:737\n264#1:763\n267#1:802\n267#1:813\n335#1:1003\n338#1:1018\n374#1:1122\n483#1:1615\n181#1:643,32\n252#1:678,10\n255#1:688,18\n258#1:706,2\n258#1:708\n258#1:709\n258#1:710,18\n261#1:728,9\n261#1:738,15\n264#1:753,10\n264#1:764,3\n264#1:767,25\n267#1:792,10\n267#1:803\n267#1:804,9\n267#1:814,7\n267#1:821,20\n279#1:841,60\n282#1:901,56\n284#1:957\n287#1:958\n287#1:960,6\n289#1:966,7\n292#1:973,6\n295#1:979,5\n329#1:984,6\n335#1:990\n335#1:992,11\n335#1:1004,5\n338#1:1009,9\n338#1:1019,61\n340#1:1080\n343#1:1081\n343#1:1083,5\n345#1:1088\n348#1:1089,7\n351#1:1096,20\n371#1:1116\n374#1:1117,5\n374#1:1123,10\n376#1:1133,7\n379#1:1140,2\n384#1:1142\n384#1:1144,87\n387#1:1231,48\n410#1:1279\n416#1:1280,21\n437#1:1301,7\n441#1:1308,7\n443#1:1315,4\n445#1:1319,8\n449#1:1327,10\n453#1:1337,14\n457#1:1351,91\n460#1:1442,40\n463#1:1482\n463#1:1484,13\n465#1:1497\n465#1:1498\n465#1:1500,39\n467#1:1539,2\n467#1:1542,4\n477#1:1546,3\n477#1:1550,4\n477#1:1554\n477#1:1555,22\n477#1:1577,18\n483#1:1595,2\n483#1:1598,3\n483#1:1601\n483#1:1602,13\n483#1:1616,13\n483#1:1629,18\n488#1:1647,2\n488#1:1650\n488#1:1651\n488#1:1652,50\n488#1:1702,18\n498#1:1720,14\n568#1:1734,32\n570#1:1766,12\n578#1:1778,18\n586#1:1796\n586#1:1798\n588#1:1799,34\n287#1:959\n335#1:991\n343#1:1082\n384#1:1143\n463#1:1483\n465#1:1499\n467#1:1541\n477#1:1549\n483#1:1597\n488#1:1649\n586#1:1797\n*E\n"])
class Buffer : BufferedSource, BufferedSink, Cloneable, ByteChannel {
   public open val buffer: Buffer
      public open get() {
         return this;
      }

   internal final var head: Segment?
      private set
   public final var size: Long
      public final get() {
         return this.size;
      }

      public final set(<set-?>) {
         this.size = var1;
      }



   public override fun buffer(): Buffer {
      return this;
   }

   public override fun outputStream(): OutputStream {
      return new OutputStream(this) {
         {
            this.this$0 = `$receiver`;
         }

         @Override
         public void write(int b) {
            this.this$0.writeByte(b);
         }

         @Override
         public void write(@NotNull byte[] data, int offset, int byteCount) {
            this.this$0.write(data, offset, byteCount);
         }

         @Override
         public void flush() {
         }

         @Override
         public void close() {
         }

         @NotNull
         @Override
         public java.lang.String toString() {
            return this.this$0 + ".outputStream()";
         }
      };
   }

   public open fun emitCompleteSegments(): Buffer {
      return this;
   }

   public open fun emit(): Buffer {
      return this;
   }

   public override fun exhausted(): Boolean {
      return this.size == 0L;
   }

   @Throws(java/io/EOFException::class)
   public override fun require(byteCount: Long) {
      if (this.size < byteCount) {
         throw new EOFException();
      }
   }

   public override fun request(byteCount: Long): Boolean {
      return this.size >= byteCount;
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
            return if (this.this$0.size() > 0L) this.this$0.readByte() and 255 else -1;
         }

         @Override
         public int read(@NotNull byte[] sink, int offset, int byteCount) {
            return this.this$0.read(sink, offset, byteCount);
         }

         @Override
         public int available() {
            return (int)Math.min(this.this$0.size(), (long)Integer.MAX_VALUE);
         }

         @Override
         public void close() {
         }

         @NotNull
         @Override
         public java.lang.String toString() {
            return this.this$0 + ".inputStream()";
         }
      };
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   public fun copyTo(out: OutputStream, offset: Long, byteCount: Long): Buffer {
      var offsetx: Long = offset;
      var byteCountx: Long = byteCount;
      -SegmentedByteString.checkOffsetAndCount(this.size, offset, byteCount);
      if (byteCount == 0L) {
         return this;
      } else {
         var s: Segment = this.head;

         while (true) {
            if (offsetx < (long)(s.limit - s.pos)) {
               while (byteCountx > 0L) {
                  var pos: Int = (int)((long)s.pos + offsetx);
                  var toCopy: Int = (int)Math.min((long)(s.limit - (int)((long)s.pos + offsetx)), byteCountx);
                  out.write(s.data, pos, toCopy);
                  byteCountx -= (long)toCopy;
                  offsetx = 0L;
                  s = s.next;
               }

               return this;
            }

            offsetx -= (long)(s.limit - s.pos);
            s = s.next;
         }
      }
   }

   public fun copyTo(out: Buffer, offset: Long, byteCount: Long): Buffer {
      var `offset$iv`: Long = offset;
      var `byteCount$iv`: Long = byteCount;
      -SegmentedByteString.checkOffsetAndCount(this.size(), offset, byteCount);
      var var10000: Buffer;
      if (byteCount == 0L) {
         var10000 = this;
      } else {
         out.setSize$okio(out.size() + byteCount);
         var `s$iv`: Segment = this.head;

         while (true) {
            if (`offset$iv` < (long)(`s$iv`.limit - `s$iv`.pos)) {
               while (byteCount$iv > 0L) {
                  var `copy$iv`: Segment = `s$iv`.sharedCopy();
                  `copy$iv`.pos += (int)`offset$iv`;
                  `copy$iv`.limit = Math.min(`copy$iv`.pos + (int)`byteCount$iv`, `copy$iv`.limit);
                  if (out.head == null) {
                     `copy$iv`.prev = `copy$iv`;
                     `copy$iv`.next = `copy$iv`.prev;
                     out.head = `copy$iv`.next;
                  } else {
                     var var14: Segment = out.head;
                     var var15: Segment = var14.prev;
                     var15.push(`copy$iv`);
                  }

                  `byteCount$iv` -= (long)(`copy$iv`.limit - `copy$iv`.pos);
                  `offset$iv` = 0L;
                  `s$iv` = `s$iv`.next;
               }

               var10000 = this;
               break;
            }

            `offset$iv` -= (long)(`s$iv`.limit - `s$iv`.pos);
            `s$iv` = `s$iv`.next;
         }
      }

      return var10000;
   }

   public fun copyTo(out: Buffer, offset: Long): Buffer {
      return this.copyTo(out, offset, this.size - offset);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   public fun writeTo(out: OutputStream, byteCount: Long): Buffer {
      var byteCountx: Long = byteCount;
      -SegmentedByteString.checkOffsetAndCount(this.size, 0L, byteCount);
      var s: Segment = this.head;

      while (byteCountx > 0L) {
         var toCopy: Int = (int)Math.min(byteCountx, (long)(s.limit - s.pos));
         out.write(s.data, s.pos, toCopy);
         s.pos += toCopy;
         this.size -= (long)toCopy;
         byteCountx -= (long)toCopy;
         if (s.pos == s.limit) {
            var var10: Segment = s;
            s = s.pop();
            this.head = s;
            SegmentPool.recycle(var10);
         }
      }

      return this;
   }

   @Throws(java/io/IOException::class)
   public fun readFrom(input: InputStream): Buffer {
      this.readFrom(input, java.lang.Long.MAX_VALUE, true);
      return this;
   }

   @Throws(java/io/IOException::class)
   public fun readFrom(input: InputStream, byteCount: Long): Buffer {
      if (byteCount < 0L) {
         throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
      } else {
         this.readFrom(input, byteCount, false);
         return this;
      }
   }

   @Throws(java/io/IOException::class)
   private fun readFrom(input: InputStream, byteCount: Long, forever: Boolean) {
      var byteCountx: Long = byteCount;

      while (byteCountx > 0L || forever) {
         var tail: Segment = this.writableSegment$okio(1);
         var var11: Int = input.read(tail.data, tail.limit, (int)Math.min(byteCountx, (long)(8192 - tail.limit)));
         if (var11 == -1) {
            if (tail.pos == tail.limit) {
               this.head = tail.pop();
               SegmentPool.recycle(tail);
            }

            if (forever) {
               return;
            }

            throw new EOFException();
         }

         tail.limit += var11;
         this.size += (long)var11;
         byteCountx -= (long)var11;
      }
   }

   public fun completeSegmentByteCount(): Long {
      var `result$iv`: Long = this.size();
      var var10000: Long;
      if (`result$iv` == 0L) {
         var10000 = 0L;
      } else {
         var var6: Segment = this.head;
         var var7: Segment = var6.prev;
         if (var7.limit < 8192 && var7.owner) {
            `result$iv` -= (long)(var7.limit - var7.pos);
         }

         var10000 = `result$iv`;
      }

      return var10000;
   }

   @Throws(java/io/EOFException::class)
   public override fun readByte(): Byte {
      if (this.size() == 0L) {
         throw new EOFException();
      } else {
         var var10000: Segment = this.head;
         var `limit$iv`: Int = var10000.limit;
         var `b$iv`: Byte = var10000.data[var10000.pos++];
         this.setSize$okio(this.size() - 1L);
         var `pos$iv`: Int;
         if (`pos$iv` == `limit$iv`) {
            this.head = var10000.pop();
            SegmentPool.recycle(var10000);
         } else {
            var10000.pos = `pos$iv`;
         }

         return `b$iv`;
      }
   }

   @JvmName(name = "getByte")
   public operator fun get(pos: Long): Byte {
      -SegmentedByteString.checkOffsetAndCount(this.size(), pos, 1L);
      var var10000: Byte;
      if (this.head == null) {
         var10000 = ((Segment)null).data[(int)((long)((Segment)null).pos + pos - -1L)];
      } else {
         var `s$iv$iv`: Segment = this.head;
         if (this.size() - pos < pos) {
            var `offset$iv$iv`: Long;
            for (offset$iv$iv = this.size(); offset$iv$iv > pos; offset$iv$iv -= (long)(var23.limit - var23.pos)) {
               var23 = `s$iv$iv`.prev;
               `s$iv$iv` = var23;
            }

            var10000 = `s$iv$iv`.data[(int)((long)`s$iv$iv`.pos + pos - `offset$iv$iv`)];
         } else {
            var var22: Long = 0L;

            while (true) {
               var var21: Long = var22 + (long)(`s$iv$iv`.limit - `s$iv$iv`.pos);
               if (var22 + (long)(`s$iv$iv`.limit - `s$iv$iv`.pos) > pos) {
                  var10000 = `s$iv$iv`.data[(int)((long)`s$iv$iv`.pos + pos - var22)];
                  break;
               }

               var var24: Segment = `s$iv$iv`.next;
               `s$iv$iv` = var24;
               var22 = var21;
            }
         }
      }

      return var10000;
   }

   @Throws(java/io/EOFException::class)
   public override fun readShort(): Short {
      if (this.size() < 2L) {
         throw new EOFException();
      } else {
         var var10000: Segment = this.head;
         var `limit$iv`: Int = var10000.limit;
         var var24: Short;
         if (var10000.limit - var10000.pos < 2) {
            var24 = (short)((this.readByte() and 255) shl 8 or this.readByte() and 255);
         } else {
            var `pos$iv`: Int;
            var var14: Int = (var10000.data[var10000.pos++] and 255) shl 8 or var10000.data[`pos$iv`++] and 255;
            this.setSize$okio(this.size() - 2L);
            if (`pos$iv` == `limit$iv`) {
               this.head = var10000.pop();
               SegmentPool.recycle(var10000);
            } else {
               var10000.pos = `pos$iv`;
            }

            var24 = (short)var14;
         }

         return var24;
      }
   }

   @Throws(java/io/EOFException::class)
   public override fun readInt(): Int {
      if (this.size() < 4L) {
         throw new EOFException();
      } else {
         var var10000: Segment = this.head;
         var `limit$iv`: Int = var10000.limit;
         var var39: Int;
         if ((long)(var10000.limit - var10000.pos) < 4L) {
            var39 = (this.readByte() and 255) shl 24 or (this.readByte() and 255) shl 16 or (this.readByte() and 255) shl 8 or this.readByte() and 255;
         } else {
            var `pos$iv`: Int;
            var var22: Int = (var10000.data[var10000.pos++] and 255) shl 24 or (var10000.data[`pos$iv`++] and 255) shl 16 or (var10000.data[`pos$iv`++] and 255) shl 8 or var10000.data[`pos$iv`++] and 255;
            this.setSize$okio(this.size() - 4L);
            if (`pos$iv` == `limit$iv`) {
               this.head = var10000.pop();
               SegmentPool.recycle(var10000);
            } else {
               var10000.pos = `pos$iv`;
            }

            var39 = var22;
         }

         return var39;
      }
   }

   @Throws(java/io/EOFException::class)
   public override fun readLong(): Long {
      if (this.size() < 8L) {
         throw new EOFException();
      } else {
         var var10000: Segment = this.head;
         var `limit$iv`: Int = var10000.limit;
         var var49: Long;
         if ((long)(var10000.limit - var10000.pos) < 8L) {
            var49 = ((long)this.readInt() and 4294967295L) shl 32 or (long)this.readInt() and 4294967295L;
         } else {
            var `pos$iv`: Int;
            var var24: Long = ((long)var10000.data[var10000.pos++] and 255L) shl 56 or ((long)var10000.data[`pos$iv`++] and 255L) shl 48 or (
               (long)var10000.data[`pos$iv`++] and 255L
            ) shl 40 or ((long)var10000.data[`pos$iv`++] and 255L) shl 32 or ((long)var10000.data[`pos$iv`++] and 255L) shl 24 or (
               (long)var10000.data[`pos$iv`++] and 255L
            ) shl 16 or ((long)var10000.data[`pos$iv`++] and 255L) shl 8 or (long)var10000.data[`pos$iv`++] and 255L;
            this.setSize$okio(this.size() - 8L);
            if (`pos$iv` == `limit$iv`) {
               this.head = var10000.pop();
               SegmentPool.recycle(var10000);
            } else {
               var10000.pos = `pos$iv`;
            }

            var49 = var24;
         }

         return var49;
      }
   }

   @Throws(java/io/EOFException::class)
   public override fun readShortLe(): Short {
      return -SegmentedByteString.reverseBytes(this.readShort());
   }

   @Throws(java/io/EOFException::class)
   public override fun readIntLe(): Int {
      return -SegmentedByteString.reverseBytes(this.readInt());
   }

   @Throws(java/io/EOFException::class)
   public override fun readLongLe(): Long {
      return -SegmentedByteString.reverseBytes(this.readLong());
   }

   @Throws(java/io/EOFException::class)
   public override fun readDecimalLong(): Long {
      var `$this$commonReadDecimalLong$iv`: Buffer = this;
      if (this.size() == 0L) {
         throw new EOFException();
      } else {
         var `value$iv`: Long = 0L;
         var `seen$iv`: Int = 0;
         var `negative$iv`: Boolean = false;
         var `done$iv`: Boolean = false;
         var `overflowDigit$iv`: Long = -7L;

         while (true) {
            var var10000: Segment = `$this$commonReadDecimalLong$iv`.head;
            var `expected$iv`: ByteArray = var10000.data;
            var `pos$iv`: Int = var10000.pos;
            var `limit$iv`: Int = var10000.limit;

            while (true) {
               label86: {
                  if (`pos$iv` < `limit$iv`) {
                     var `b$iv`: Byte = `expected$iv`[`pos$iv`];
                     if (`expected$iv`[`pos$iv`] >= 48 && `expected$iv`[`pos$iv`] <= 57) {
                        var `digit$iv`: Int = 48 - `b$iv`;
                        if (`value$iv` < -922337203685477580L || `value$iv` == -922337203685477580L && (long)(48 - `b$iv`) < `overflowDigit$iv`) {
                           var `buffer$iv`: Buffer = new Buffer().writeDecimalLong(`value$iv`).writeByte(`b$iv`);
                           if (!`negative$iv`) {
                              `buffer$iv`.readByte();
                           }

                           throw new NumberFormatException("Number too large: " + `buffer$iv`.readUtf8());
                        }

                        `value$iv` = `value$iv` * 10L + (long)`digit$iv`;
                        break label86;
                     }

                     if (`b$iv` == 45 && `seen$iv` == 0) {
                        `negative$iv` = true;
                        `overflowDigit$iv`--;
                        break label86;
                     }

                     `done$iv` = true;
                  }

                  if (`pos$iv` == `limit$iv`) {
                     `$this$commonReadDecimalLong$iv`.head = var10000.pop();
                     SegmentPool.recycle(var10000);
                  } else {
                     var10000.pos = `pos$iv`;
                  }

                  if (!`done$iv` && `$this$commonReadDecimalLong$iv`.head != null) {
                     break;
                  }

                  `$this$commonReadDecimalLong$iv`.setSize$okio(`$this$commonReadDecimalLong$iv`.size() - (long)`seen$iv`);
                  if (`seen$iv` < (if (`negative$iv`) 2 else 1)) {
                     if (`$this$commonReadDecimalLong$iv`.size() == 0L) {
                        throw new EOFException();
                     }

                     throw new NumberFormatException(
                        (if (`negative$iv`) "Expected a digit" else "Expected a digit or '-'")
                           + " but was 0x"
                           + -SegmentedByteString.toHexString(`$this$commonReadDecimalLong$iv`.getByte(0L))
                     );
                  }

                  return if (`negative$iv`) `value$iv` else -`value$iv`;
               }

               `pos$iv`++;
               `seen$iv`++;
            }
         }
      }
   }

   @Throws(java/io/EOFException::class)
   public override fun readHexadecimalUnsignedLong(): Long {
      var `$this$commonReadHexadecimalUnsignedLong$iv`: Buffer = this;
      if (this.size() == 0L) {
         throw new EOFException();
      } else {
         var `value$iv`: Long = 0L;
         var `seen$iv`: Int = 0;
         var `done$iv`: Boolean = false;

         do {
            var var10000: Segment = `$this$commonReadHexadecimalUnsignedLong$iv`.head;
            var `data$iv`: ByteArray = var10000.data;
            var `pos$iv`: Int = var10000.pos;

            var `limit$iv`: Int;
            for (limit$iv = var10000.limit; pos$iv < limit$iv; seen$iv++) {
               var `b$iv`: Byte = `data$iv`[`pos$iv`];
               var var15: Int;
               if (`data$iv`[`pos$iv`] >= 48 && `data$iv`[`pos$iv`] <= 57) {
                  var15 = `b$iv` - 48;
               } else if (`b$iv` >= 97 && `b$iv` <= 102) {
                  var15 = `b$iv` - 97 + 10;
               } else {
                  if (`b$iv` < 65 || `b$iv` > 70) {
                     if (`seen$iv` == 0) {
                        throw new NumberFormatException("Expected leading [0-9a-fA-F] character but was 0x" + -SegmentedByteString.toHexString(`b$iv`));
                     }

                     `done$iv` = true;
                     break;
                  }

                  var15 = `b$iv` - 65 + 10;
               }

               if ((`value$iv` and -1152921504606846976L) != 0L) {
                  throw new NumberFormatException("Number too large: " + new Buffer().writeHexadecimalUnsignedLong(`value$iv`).writeByte(`b$iv`).readUtf8());
               }

               `value$iv` = `value$iv` shl 4 or (long)var15;
               `pos$iv`++;
            }

            if (`pos$iv` == `limit$iv`) {
               `$this$commonReadHexadecimalUnsignedLong$iv`.head = var10000.pop();
               SegmentPool.recycle(var10000);
            } else {
               var10000.pos = `pos$iv`;
            }
         } while (!done$iv && $this$commonReadHexadecimalUnsignedLong$iv.head != null);

         `$this$commonReadHexadecimalUnsignedLong$iv`.setSize$okio(`$this$commonReadHexadecimalUnsignedLong$iv`.size() - (long)`seen$iv`);
         return `value$iv`;
      }
   }

   public override fun readByteString(): ByteString {
      return this.readByteString(this.size());
   }

   @Throws(java/io/EOFException::class)
   public override fun readByteString(byteCount: Long): ByteString {
      if (byteCount < 0L || byteCount > 2147483647L) {
         throw new IllegalArgumentException(("byteCount: " + byteCount).toString());
      } else if (this.size() < byteCount) {
         throw new EOFException();
      } else {
         var var10000: ByteString;
         if (byteCount >= 4096L) {
            var var6: ByteString = this.snapshot((int)byteCount);
            this.skip(byteCount);
            var10000 = var6;
         } else {
            var10000 = new ByteString(this.readByteArray(byteCount));
         }

         return var10000;
      }
   }

   public override fun select(options: Options): Int {
      var `index$iv`: Int = -Buffer.selectPrefix$default(this, options, false, 2, null);
      var var10000: Int;
      if (`index$iv` == -1) {
         var10000 = -1;
      } else {
         this.skip((long)options.getByteStrings$okio()[`index$iv`].size());
         var10000 = `index$iv`;
      }

      return var10000;
   }

   @Throws(java/io/EOFException::class)
   public override fun readFully(sink: Buffer, byteCount: Long) {
      if (this.size() < byteCount) {
         sink.write(this, this.size());
         throw new EOFException();
      } else {
         sink.write(this, byteCount);
      }
   }

   @Throws(java/io/IOException::class)
   public override fun readAll(sink: Sink): Long {
      var `byteCount$iv`: Long = this.size();
      if (`byteCount$iv` > 0L) {
         sink.write(this, `byteCount$iv`);
      }

      return `byteCount$iv`;
   }

   public override fun readUtf8(): String {
      return this.readString(this.size, Charsets.UTF_8);
   }

   @Throws(java/io/EOFException::class)
   public override fun readUtf8(byteCount: Long): String {
      return this.readString(byteCount, Charsets.UTF_8);
   }

   public override fun readString(charset: Charset): String {
      return this.readString(this.size, charset);
   }

   @Throws(java/io/EOFException::class)
   public override fun readString(byteCount: Long, charset: Charset): String {
      if (byteCount < 0L || byteCount > 2147483647L) {
         throw new IllegalArgumentException(("byteCount: " + byteCount).toString());
      } else if (this.size < byteCount) {
         throw new EOFException();
      } else if (byteCount == 0L) {
         return "";
      } else {
         var var10000: Segment = this.head;
         if ((long)var10000.pos + byteCount > (long)var10000.limit) {
            return new java.lang.String(this.readByteArray(byteCount), charset);
         } else {
            var result: java.lang.String = new java.lang.String(var10000.data, var10000.pos, (int)byteCount, charset);
            var10000.pos += (int)byteCount;
            this.size -= byteCount;
            if (var10000.pos == var10000.limit) {
               this.head = var10000.pop();
               SegmentPool.recycle(var10000);
            }

            return result;
         }
      }
   }

   @Throws(java/io/EOFException::class)
   public override fun readUtf8Line(): String? {
      var `newline$iv`: Long = this.indexOf((byte)10);
      return if (`newline$iv` != -1L) -Buffer.readUtf8Line(this, `newline$iv`) else (if (this.size() != 0L) this.readUtf8(this.size()) else null);
   }

   @Throws(java/io/EOFException::class)
   public override fun readUtf8LineStrict(): String {
      return this.readUtf8LineStrict(java.lang.Long.MAX_VALUE);
   }

   @Throws(java/io/EOFException::class)
   public override fun readUtf8LineStrict(limit: Long): String {
      if (limit < 0L) {
         throw new IllegalArgumentException(("limit < 0: " + limit).toString());
      } else {
         var `scanLength$iv`: Long = if (limit == java.lang.Long.MAX_VALUE) java.lang.Long.MAX_VALUE else limit + 1L;
         var `newline$iv`: Long = this.indexOf((byte)10, 0L, if (limit == java.lang.Long.MAX_VALUE) java.lang.Long.MAX_VALUE else limit + 1L);
         var var10000: java.lang.String;
         if (`newline$iv` != -1L) {
            var10000 = -Buffer.readUtf8Line(this, `newline$iv`);
         } else {
            if (`scanLength$iv` >= this.size() || this.getByte(`scanLength$iv` - 1L) != 13 || this.getByte(`scanLength$iv`) != 10) {
               var `data$iv`: Buffer = new Buffer();
               this.copyTo(`data$iv`, 0L, Math.min((long)32, this.size()));
               throw new EOFException("\\n not found: limit=" + Math.min(this.size(), limit) + " content=" + `data$iv`.readByteString().hex() + '…');
            }

            var10000 = -Buffer.readUtf8Line(this, `scanLength$iv`);
         }

         return var10000;
      }
   }

   @Throws(java/io/EOFException::class)
   public override fun readUtf8CodePoint(): Int {
      var `$this$commonReadUtf8CodePoint$iv`: Buffer = this;
      if (this.size() == 0L) {
         throw new EOFException();
      } else {
         var `b0$iv`: Byte = this.getByte(0L);
         var var12: Int;
         var var14: Byte;
         var var15: Int;
         if ((`b0$iv` and 128) == 0) {
            var12 = `b0$iv` and 127;
            var14 = 1;
            var15 = 0;
         } else if ((`b0$iv` and 224) == 192) {
            var12 = `b0$iv` and 31;
            var14 = 2;
            var15 = 128;
         } else if ((`b0$iv` and 240) == 224) {
            var12 = `b0$iv` and 15;
            var14 = 3;
            var15 = 2048;
         } else {
            if ((`b0$iv` and 248) != 240) {
               this.skip(1L);
               return 65533;
            }

            var12 = `b0$iv` and 7;
            var14 = 4;
            var15 = 65536;
         }

         if (this.size() < (long)var14) {
            throw new EOFException("size < " + var14 + ": " + this.size() + " (to read code point prefixed 0x" + -SegmentedByteString.toHexString(`b0$iv`) + 41);
         } else {
            var `i$iv`: Int = 1;

            var var10000: Int;
            while (true) {
               if (`i$iv` >= var14) {
                  `$this$commonReadUtf8CodePoint$iv`.skip((long)var14);
                  var10000 = if (var12 > 1114111) '�' else (if (55296 <= var12 && var12 < 57344) '�' else (if (var12 < var15) '�' else var12));
                  break;
               }

               var var23: Byte = `$this$commonReadUtf8CodePoint$iv`.getByte((long)`i$iv`);
               if ((var23 and 192) != 128) {
                  `$this$commonReadUtf8CodePoint$iv`.skip((long)`i$iv`);
                  var10000 = 65533;
                  break;
               }

               var12 = var12 shl 6 or var23 and 63;
               `i$iv`++;
            }

            return var10000;
         }
      }
   }

   public override fun readByteArray(): ByteArray {
      return this.readByteArray(this.size());
   }

   @Throws(java/io/EOFException::class)
   public override fun readByteArray(byteCount: Long): ByteArray {
      if (byteCount < 0L || byteCount > 2147483647L) {
         throw new IllegalArgumentException(("byteCount: " + byteCount).toString());
      } else if (this.size() < byteCount) {
         throw new EOFException();
      } else {
         var `result$iv`: ByteArray = new byte[(int)byteCount];
         this.readFully(`result$iv`);
         return `result$iv`;
      }
   }

   public override fun read(sink: ByteArray): Int {
      return this.read(sink, 0, sink.length);
   }

   @Throws(java/io/EOFException::class)
   public override fun readFully(sink: ByteArray) {
      var `$this$commonReadFully$iv`: Buffer = this;
      var `offset$iv`: Int = 0;

      while (offset$iv < sink.length) {
         var `read$iv`: Int = `$this$commonReadFully$iv`.read(sink, `offset$iv`, sink.length - `offset$iv`);
         if (`read$iv` == -1) {
            throw new EOFException();
         }

         `offset$iv` += `read$iv`;
      }
   }

   public override fun read(sink: ByteArray, offset: Int, byteCount: Int): Int {
      -SegmentedByteString.checkOffsetAndCount((long)sink.length, (long)offset, (long)byteCount);
      var var10000: Int;
      if (this.head == null) {
         var10000 = -1;
      } else {
         var `s$iv`: Segment = this.head;
         var `toCopy$iv`: Int = Math.min(byteCount, this.head.limit - this.head.pos);
         ArraysKt.copyInto(`s$iv`.data, sink, offset, `s$iv`.pos, `s$iv`.pos + `toCopy$iv`);
         `s$iv`.pos += `toCopy$iv`;
         this.setSize$okio(this.size() - (long)`toCopy$iv`);
         if (`s$iv`.pos == `s$iv`.limit) {
            this.head = `s$iv`.pop();
            SegmentPool.recycle(`s$iv`);
         }

         var10000 = `toCopy$iv`;
      }

      return var10000;
   }

   @Throws(java/io/IOException::class)
   public override fun read(sink: ByteBuffer): Int {
      if (this.head == null) {
         return -1;
      } else {
         var s: Segment = this.head;
         var toCopy: Int = Math.min(sink.remaining(), s.limit - s.pos);
         sink.put(s.data, s.pos, toCopy);
         s.pos += toCopy;
         this.size -= (long)toCopy;
         if (s.pos == s.limit) {
            this.head = s.pop();
            SegmentPool.recycle(s);
         }

         return toCopy;
      }
   }

   public fun clear() {
      this.skip(this.size());
   }

   @Throws(java/io/EOFException::class)
   public override fun skip(byteCount: Long) {
      var `$this$commonSkip$iv`: Buffer = this;
      var `byteCount$iv`: Long = byteCount;

      while (byteCount$iv > 0L) {
         if (`$this$commonSkip$iv`.head == null) {
            throw new EOFException();
         }

         var `head$iv`: Segment = `$this$commonSkip$iv`.head;
         var `toSkip$iv`: Int = (int)Math.min(`byteCount$iv`, (long)(`$this$commonSkip$iv`.head.limit - `$this$commonSkip$iv`.head.pos));
         `$this$commonSkip$iv`.setSize$okio(`$this$commonSkip$iv`.size() - (long)`toSkip$iv`);
         `byteCount$iv` -= (long)`toSkip$iv`;
         `head$iv`.pos += `toSkip$iv`;
         if (`head$iv`.pos == `head$iv`.limit) {
            `$this$commonSkip$iv`.head = `head$iv`.pop();
            SegmentPool.recycle(`head$iv`);
         }
      }
   }

   public open fun write(byteString: ByteString): Buffer {
      byteString.write$okio(this, 0, byteString.size());
      return this;
   }

   public open fun write(byteString: ByteString, offset: Int, byteCount: Int): Buffer {
      byteString.write$okio(this, offset, byteCount);
      return this;
   }

   public open fun writeUtf8(string: String): Buffer {
      return this.writeUtf8(string, 0, string.length());
   }

   public open fun writeUtf8(string: String, beginIndex: Int, endIndex: Int): Buffer {
      var `$this$commonWriteUtf8$iv`: Buffer = this;
      if (beginIndex < 0) {
         throw new IllegalArgumentException(("beginIndex < 0: " + beginIndex).toString());
      } else if (endIndex < beginIndex) {
         throw new IllegalArgumentException(("endIndex < beginIndex: " + endIndex + " < " + beginIndex).toString());
      } else if (endIndex > string.length()) {
         throw new IllegalArgumentException(("endIndex > string.length: " + endIndex + " > " + string.length()).toString());
      } else {
         var `i$iv`: Int = beginIndex;

         while (i$iv < endIndex) {
            var `c$iv`: Int = string.charAt(`i$iv`);
            if (`c$iv` < 128) {
               var var22: Segment = `$this$commonWriteUtf8$iv`.writableSegment$okio(1);
               var var23: ByteArray = var22.data;
               var var24: Int = var22.limit - `i$iv`;
               var `runLimit$iv`: Int = Math.min(endIndex, 8192 - (var22.limit - `i$iv`));
               var23[var24 + `i$iv`++] = (byte)`c$iv`;

               while (i$iv < runLimit$iv) {
                  var var13: Char = string.charAt(`i$iv`);
                  if (var13 >= 128) {
                     break;
                  }

                  var23[var24 + `i$iv`++] = (byte)var13;
               }

               var `runSize$iv`: Int = `i$iv` + var24 - var22.limit;
               var22.limit = var22.limit + (`i$iv` + var24 - var22.limit);
               `$this$commonWriteUtf8$iv`.setSize$okio(`$this$commonWriteUtf8$iv`.size() + (long)`runSize$iv`);
            } else if (`c$iv` < 2048) {
               var var21: Segment = `$this$commonWriteUtf8$iv`.writableSegment$okio(2);
               var21.data[var21.limit] = (byte)(`c$iv` shr 6 or 192);
               var21.data[var21.limit + 1] = (byte)(`c$iv` and 63 or 128);
               var21.limit += 2;
               `$this$commonWriteUtf8$iv`.setSize$okio(`$this$commonWriteUtf8$iv`.size() + 2L);
               `i$iv`++;
            } else if (`c$iv` >= 55296 && `c$iv` <= 57343) {
               var var20: Int = if (`i$iv` + 1 < endIndex) string.charAt(`i$iv` + 1) else 0;
               if (`c$iv` <= 56319 && 56320 <= var20 && var20 < 57344) {
                  var `codePoint$iv`: Int = 65536 + ((`c$iv` and 1023) shl 10 or var20 and 1023);
                  var `tail$iv`: Segment = `$this$commonWriteUtf8$iv`.writableSegment$okio(4);
                  `tail$iv`.data[`tail$iv`.limit] = (byte)(`codePoint$iv` shr 18 or 240);
                  `tail$iv`.data[`tail$iv`.limit + 1] = (byte)(`codePoint$iv` shr 12 and 63 or 128);
                  `tail$iv`.data[`tail$iv`.limit + 2] = (byte)(`codePoint$iv` shr 6 and 63 or 128);
                  `tail$iv`.data[`tail$iv`.limit + 3] = (byte)(`codePoint$iv` and 63 or 128);
                  `tail$iv`.limit += 4;
                  `$this$commonWriteUtf8$iv`.setSize$okio(`$this$commonWriteUtf8$iv`.size() + 4L);
                  `i$iv` += 2;
               } else {
                  `$this$commonWriteUtf8$iv`.writeByte(63);
                  `i$iv`++;
               }
            } else {
               var `low$iv`: Segment = `$this$commonWriteUtf8$iv`.writableSegment$okio(3);
               `low$iv`.data[`low$iv`.limit] = (byte)(`c$iv` shr 12 or 224);
               `low$iv`.data[`low$iv`.limit + 1] = (byte)(`c$iv` shr 6 and 63 or 128);
               `low$iv`.data[`low$iv`.limit + 2] = (byte)(`c$iv` and 63 or 128);
               `low$iv`.limit += 3;
               `$this$commonWriteUtf8$iv`.setSize$okio(`$this$commonWriteUtf8$iv`.size() + 3L);
               `i$iv`++;
            }
         }

         return `$this$commonWriteUtf8$iv`;
      }
   }

   public open fun writeUtf8CodePoint(codePoint: Int): Buffer {
      if (codePoint < 128) {
         this.writeByte(codePoint);
      } else if (codePoint < 2048) {
         var `tail$iv`: Segment = this.writableSegment$okio(2);
         `tail$iv`.data[`tail$iv`.limit] = (byte)(codePoint shr 6 or 192);
         `tail$iv`.data[`tail$iv`.limit + 1] = (byte)(codePoint and 63 or 128);
         `tail$iv`.limit += 2;
         this.setSize$okio(this.size() + 2L);
      } else if (55296 <= codePoint && codePoint < 57344) {
         this.writeByte(63);
      } else if (codePoint < 65536) {
         var var5: Segment = this.writableSegment$okio(3);
         var5.data[var5.limit] = (byte)(codePoint shr 12 or 224);
         var5.data[var5.limit + 1] = (byte)(codePoint shr 6 and 63 or 128);
         var5.data[var5.limit + 2] = (byte)(codePoint and 63 or 128);
         var5.limit += 3;
         this.setSize$okio(this.size() + 3L);
      } else {
         if (codePoint > 1114111) {
            throw new IllegalArgumentException("Unexpected code point: 0x" + -SegmentedByteString.toHexString(codePoint));
         }

         var var6: Segment = this.writableSegment$okio(4);
         var6.data[var6.limit] = (byte)(codePoint shr 18 or 240);
         var6.data[var6.limit + 1] = (byte)(codePoint shr 12 and 63 or 128);
         var6.data[var6.limit + 2] = (byte)(codePoint shr 6 and 63 or 128);
         var6.data[var6.limit + 3] = (byte)(codePoint and 63 or 128);
         var6.limit += 4;
         this.setSize$okio(this.size() + 4L);
      }

      return this;
   }

   public open fun writeString(string: String, charset: Charset): Buffer {
      return this.writeString(string, 0, string.length(), charset);
   }

   public open fun writeString(string: String, beginIndex: Int, endIndex: Int, charset: Charset): Buffer {
      if (beginIndex < 0) {
         throw new IllegalArgumentException(("beginIndex < 0: " + beginIndex).toString());
      } else if (endIndex < beginIndex) {
         throw new IllegalArgumentException(("endIndex < beginIndex: " + endIndex + " < " + beginIndex).toString());
      } else if (endIndex > string.length()) {
         throw new IllegalArgumentException(("endIndex > string.length: " + endIndex + " > " + string.length()).toString());
      } else if (charset == Charsets.UTF_8) {
         return this.writeUtf8(string, beginIndex, endIndex);
      } else {
         var var10000: java.lang.String = string.substring(beginIndex, endIndex);
         var var12: ByteArray = var10000.getBytes(charset);
         return this.write(var12, 0, var12.length);
      }
   }

   public open fun write(source: ByteArray): Buffer {
      return this.write(source, 0, source.length);
   }

   public open fun write(source: ByteArray, offset: Int, byteCount: Int): Buffer {
      var `$this$commonWrite$iv`: Buffer = this;
      var `offset$iv`: Int = offset;
      -SegmentedByteString.checkOffsetAndCount((long)source.length, (long)offset, (long)byteCount);
      var `limit$iv`: Int = offset + byteCount;

      while (offset$iv < limit$iv) {
         var `tail$iv`: Segment = `$this$commonWrite$iv`.writableSegment$okio(1);
         var `toCopy$iv`: Int = Math.min(`limit$iv` - `offset$iv`, 8192 - `tail$iv`.limit);
         ArraysKt.copyInto(source, `tail$iv`.data, `tail$iv`.limit, `offset$iv`, `offset$iv` + `toCopy$iv`);
         `offset$iv` += `toCopy$iv`;
         `tail$iv`.limit += `toCopy$iv`;
      }

      `$this$commonWrite$iv`.setSize$okio(`$this$commonWrite$iv`.size() + (long)byteCount);
      return `$this$commonWrite$iv`;
   }

   @Throws(java/io/IOException::class)
   public override fun write(source: ByteBuffer): Int {
      var byteCount: Int = source.remaining();
      var remaining: Int = byteCount;

      while (remaining > 0) {
         var tail: Segment = this.writableSegment$okio(1);
         var toCopy: Int = Math.min(remaining, 8192 - tail.limit);
         source.get(tail.data, tail.limit, toCopy);
         remaining -= toCopy;
         tail.limit += toCopy;
      }

      this.size += (long)byteCount;
      return byteCount;
   }

   @Throws(java/io/IOException::class)
   public override fun writeAll(source: Source): Long {
      var `$this$commonWriteAll$iv`: Buffer = this;
      var `totalBytesRead$iv`: Long = 0L;

      while (true) {
         var `readCount$iv`: Long = source.read(`$this$commonWriteAll$iv`, 8192L);
         if (`readCount$iv` == -1L) {
            return `totalBytesRead$iv`;
         }

         `totalBytesRead$iv` += `readCount$iv`;
      }
   }

   @Throws(java/io/IOException::class)
   public open fun write(source: Source, byteCount: Long): Buffer {
      var `$this$commonWrite$iv`: Buffer = this;
      var `byteCount$iv`: Long = byteCount;

      while (byteCount$iv > 0L) {
         var `read$iv`: Long = source.read(`$this$commonWrite$iv`, `byteCount$iv`);
         if (`read$iv` == -1L) {
            throw new EOFException();
         }

         `byteCount$iv` -= `read$iv`;
      }

      return `$this$commonWrite$iv`;
   }

   public open fun writeByte(b: Int): Buffer {
      var `tail$iv`: Segment = this.writableSegment$okio(1);
      `tail$iv`.data[`tail$iv`.limit++] = (byte)b;
      this.setSize$okio(this.size() + 1L);
      return this;
   }

   public open fun writeShort(s: Int): Buffer {
      var `tail$iv`: Segment = this.writableSegment$okio(2);
      `tail$iv`.data[`tail$iv`.limit++] = (byte)(s ushr 8 and 255);
      var `limit$iv`: Int;
      `tail$iv`.data[`limit$iv`++] = (byte)(s and 255);
      `tail$iv`.limit = `limit$iv`;
      this.setSize$okio(this.size() + 2L);
      return this;
   }

   public open fun writeShortLe(s: Int): Buffer {
      return this.writeShort(-SegmentedByteString.reverseBytes((short)s));
   }

   public open fun writeInt(i: Int): Buffer {
      var `tail$iv`: Segment = this.writableSegment$okio(4);
      `tail$iv`.data[`tail$iv`.limit++] = (byte)(i ushr 24 and 255);
      var `limit$iv`: Int;
      `tail$iv`.data[`limit$iv`++] = (byte)(i ushr 16 and 255);
      `tail$iv`.data[`limit$iv`++] = (byte)(i ushr 8 and 255);
      `tail$iv`.data[`limit$iv`++] = (byte)(i and 255);
      `tail$iv`.limit = `limit$iv`;
      this.setSize$okio(this.size() + 4L);
      return this;
   }

   public open fun writeIntLe(i: Int): Buffer {
      return this.writeInt(-SegmentedByteString.reverseBytes(i));
   }

   public open fun writeLong(v: Long): Buffer {
      var `tail$iv`: Segment = this.writableSegment$okio(8);
      `tail$iv`.data[`tail$iv`.limit++] = (byte)((int)(v ushr 56 and 255L));
      var `limit$iv`: Int;
      `tail$iv`.data[`limit$iv`++] = (byte)((int)(v ushr 48 and 255L));
      `tail$iv`.data[`limit$iv`++] = (byte)((int)(v ushr 40 and 255L));
      `tail$iv`.data[`limit$iv`++] = (byte)((int)(v ushr 32 and 255L));
      `tail$iv`.data[`limit$iv`++] = (byte)((int)(v ushr 24 and 255L));
      `tail$iv`.data[`limit$iv`++] = (byte)((int)(v ushr 16 and 255L));
      `tail$iv`.data[`limit$iv`++] = (byte)((int)(v ushr 8 and 255L));
      `tail$iv`.data[`limit$iv`++] = (byte)((int)(v and 255L));
      `tail$iv`.limit = `limit$iv`;
      this.setSize$okio(this.size() + 8L);
      return this;
   }

   public open fun writeLongLe(v: Long): Buffer {
      return this.writeLong(-SegmentedByteString.reverseBytes(v));
   }

   public open fun writeDecimalLong(v: Long): Buffer {
      var `v$iv`: Long = v;
      var var10000: Buffer;
      if (v == 0L) {
         var10000 = this.writeByte(48);
      } else {
         var `negative$iv`: Boolean = false;
         if (v < 0L) {
            `v$iv` = -v;
            if (-v < 0L) {
               return this.writeUtf8("-9223372036854775808");
            }

            `negative$iv` = true;
         }

         var `width$iv`: Int = if (`v$iv` < 100000000L)
            (
               if (`v$iv` < 10000L)
                  (if (`v$iv` < 100L) (if (`v$iv` < 10L) 1 else 2) else (if (`v$iv` < 1000L) 3 else 4))
                  else
                  (if (`v$iv` < 1000000L) (if (`v$iv` < 100000L) 5 else 6) else (if (`v$iv` < 10000000L) 7 else 8))
            )
            else
            (
               if (`v$iv` < 1000000000000L)
                  (if (`v$iv` < 10000000000L) (if (`v$iv` < 1000000000L) 9 else 10) else (if (`v$iv` < 100000000000L) 11 else 12))
                  else
                  (
                     if (`v$iv` < 1000000000000000L)
                        (if (`v$iv` < 10000000000000L) 13 else (if (`v$iv` < 100000000000000L) 14 else 15))
                        else
                        (if (`v$iv` < 100000000000000000L) (if (`v$iv` < 10000000000000000L) 16 else 17) else (if (`v$iv` < 1000000000000000000L) 18 else 19))
                  )
            );
         if (`negative$iv`) {
            `width$iv`++;
         }

         var `tail$iv`: Segment = this.writableSegment$okio(`width$iv`);
         var `data$iv`: ByteArray = `tail$iv`.data;

         var `pos$iv`: Int;
         for (pos$iv = tail$iv.limit + width$iv; v$iv != 0L; v$iv /= (long)10) {
            var `digit$iv`: Int = (int)(`v$iv` % (long)10);
            `pos$iv`--;
            `data$iv`[`pos$iv`] = -Buffer.getHEX_DIGIT_BYTES()[`digit$iv`];
         }

         if (`negative$iv`) {
            `pos$iv`--;
            `data$iv`[`pos$iv`] = 45;
         }

         `tail$iv`.limit += `width$iv`;
         this.setSize$okio(this.size() + (long)`width$iv`);
         var10000 = this;
      }

      return var10000;
   }

   public open fun writeHexadecimalUnsignedLong(v: Long): Buffer {
      var `v$iv`: Long = v;
      var var10000: Buffer;
      if (v == 0L) {
         var10000 = this.writeByte(48);
      } else {
         var `width$iv`: Int = (int)(
            (
                  (
                        (
                              (
                                    (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) ushr 2 and 3689348814741910323L
                                       )
                                       + (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) and 3689348814741910323L
                                       ) ushr 4
                                 )
                                 + (
                                    (
                                          v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                          ) ushr 8 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8
                                          ) ushr 16 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16
                                          ) ushr 32
                                       )
                                       - (
                                          (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          ) ushr 1 and 6148914691236517205L
                                       ) ushr 2 and 3689348814741910323L
                                 )
                                 + (
                                    (
                                          v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                          ) ushr 8 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8
                                          ) ushr 16 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16
                                          ) ushr 32
                                       )
                                       - (
                                          (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          ) ushr 1 and 6148914691236517205L
                                       ) and 3689348814741910323L
                                 ) and 1085102592571150095L
                           )
                           + (
                              (
                                 (
                                       (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) ushr 2 and 3689348814741910323L
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) and 3689348814741910323L
                                          ) ushr 4
                                    )
                                    + (
                                       (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          )
                                          - (
                                             (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             ) ushr 1 and 6148914691236517205L
                                          ) ushr 2 and 3689348814741910323L
                                    )
                                    + (
                                       (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          )
                                          - (
                                             (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             ) ushr 1 and 6148914691236517205L
                                          ) and 3689348814741910323L
                                    ) and 1085102592571150095L
                              ) ushr 8
                           )
                           + (
                              (
                                    (
                                          (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) ushr 2 and 3689348814741910323L
                                             )
                                             + (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) and 3689348814741910323L
                                             ) ushr 4
                                       )
                                       + (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) ushr 2 and 3689348814741910323L
                                       )
                                       + (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) and 3689348814741910323L
                                       ) and 1085102592571150095L
                                 )
                                 + (
                                    (
                                       (
                                             (
                                                   (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      )
                                                      - (
                                                         (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         ) ushr 1 and 6148914691236517205L
                                                      ) ushr 2 and 3689348814741910323L
                                                )
                                                + (
                                                   (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      )
                                                      - (
                                                         (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         ) ushr 1 and 6148914691236517205L
                                                      ) and 3689348814741910323L
                                                ) ushr 4
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) ushr 2 and 3689348814741910323L
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) and 3689348814741910323L
                                          ) and 1085102592571150095L
                                    ) ushr 8
                                 ) ushr 16
                           ) and 63L
                     )
                     + (
                        (
                              (
                                    (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) ushr 2 and 3689348814741910323L
                                       )
                                       + (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) and 3689348814741910323L
                                       ) ushr 4
                                 )
                                 + (
                                    (
                                          v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                          ) ushr 8 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8
                                          ) ushr 16 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16
                                          ) ushr 32
                                       )
                                       - (
                                          (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          ) ushr 1 and 6148914691236517205L
                                       ) ushr 2 and 3689348814741910323L
                                 )
                                 + (
                                    (
                                          v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                          ) ushr 8 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8
                                          ) ushr 16 or (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16
                                          ) ushr 32
                                       )
                                       - (
                                          (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          ) ushr 1 and 6148914691236517205L
                                       ) and 3689348814741910323L
                                 ) and 1085102592571150095L
                           )
                           + (
                              (
                                 (
                                       (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) ushr 2 and 3689348814741910323L
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) and 3689348814741910323L
                                          ) ushr 4
                                    )
                                    + (
                                       (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          )
                                          - (
                                             (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             ) ushr 1 and 6148914691236517205L
                                          ) ushr 2 and 3689348814741910323L
                                    )
                                    + (
                                       (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          )
                                          - (
                                             (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             ) ushr 1 and 6148914691236517205L
                                          ) and 3689348814741910323L
                                    ) and 1085102592571150095L
                              ) ushr 8
                           )
                           + (
                              (
                                    (
                                          (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) ushr 2 and 3689348814741910323L
                                             )
                                             + (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) and 3689348814741910323L
                                             ) ushr 4
                                       )
                                       + (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) ushr 2 and 3689348814741910323L
                                       )
                                       + (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) and 3689348814741910323L
                                       ) and 1085102592571150095L
                                 )
                                 + (
                                    (
                                       (
                                             (
                                                   (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      )
                                                      - (
                                                         (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         ) ushr 1 and 6148914691236517205L
                                                      ) ushr 2 and 3689348814741910323L
                                                )
                                                + (
                                                   (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      )
                                                      - (
                                                         (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         ) ushr 1 and 6148914691236517205L
                                                      ) and 3689348814741910323L
                                                ) ushr 4
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) ushr 2 and 3689348814741910323L
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) and 3689348814741910323L
                                          ) and 1085102592571150095L
                                    ) ushr 8
                                 ) ushr 16
                           ) ushr 32 and 63L
                     )
                     + (long)3
               )
               / (long)4
         );
         var `tail$iv`: Segment = this.writableSegment$okio(
            (int)(
               (
                     (
                           (
                                 (
                                       (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) ushr 2 and 3689348814741910323L
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) and 3689348814741910323L
                                          ) ushr 4
                                    )
                                    + (
                                       (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          )
                                          - (
                                             (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             ) ushr 1 and 6148914691236517205L
                                          ) ushr 2 and 3689348814741910323L
                                    )
                                    + (
                                       (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          )
                                          - (
                                             (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             ) ushr 1 and 6148914691236517205L
                                          ) and 3689348814741910323L
                                    ) and 1085102592571150095L
                              )
                              + (
                                 (
                                    (
                                          (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) ushr 2 and 3689348814741910323L
                                             )
                                             + (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) and 3689348814741910323L
                                             ) ushr 4
                                       )
                                       + (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) ushr 2 and 3689348814741910323L
                                       )
                                       + (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) and 3689348814741910323L
                                       ) and 1085102592571150095L
                                 ) ushr 8
                              )
                              + (
                                 (
                                       (
                                             (
                                                   (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      )
                                                      - (
                                                         (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         ) ushr 1 and 6148914691236517205L
                                                      ) ushr 2 and 3689348814741910323L
                                                )
                                                + (
                                                   (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      )
                                                      - (
                                                         (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         ) ushr 1 and 6148914691236517205L
                                                      ) and 3689348814741910323L
                                                ) ushr 4
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) ushr 2 and 3689348814741910323L
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) and 3689348814741910323L
                                          ) and 1085102592571150095L
                                    )
                                    + (
                                       (
                                          (
                                                (
                                                      (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         )
                                                         - (
                                                            (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                        v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                     ) ushr 8
                                                                  ) ushr 16
                                                               ) ushr 32
                                                            ) ushr 1 and 6148914691236517205L
                                                         ) ushr 2 and 3689348814741910323L
                                                   )
                                                   + (
                                                      (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         )
                                                         - (
                                                            (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                        v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                     ) ushr 8
                                                                  ) ushr 16
                                                               ) ushr 32
                                                            ) ushr 1 and 6148914691236517205L
                                                         ) and 3689348814741910323L
                                                   ) ushr 4
                                             )
                                             + (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) ushr 2 and 3689348814741910323L
                                             )
                                             + (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) and 3689348814741910323L
                                             ) and 1085102592571150095L
                                       ) ushr 8
                                    ) ushr 16
                              ) and 63L
                        )
                        + (
                           (
                                 (
                                       (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) ushr 2 and 3689348814741910323L
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) and 3689348814741910323L
                                          ) ushr 4
                                    )
                                    + (
                                       (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          )
                                          - (
                                             (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             ) ushr 1 and 6148914691236517205L
                                          ) ushr 2 and 3689348814741910323L
                                    )
                                    + (
                                       (
                                             v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                             ) ushr 8 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8
                                             ) ushr 16 or (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16
                                             ) ushr 32
                                          )
                                          - (
                                             (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             ) ushr 1 and 6148914691236517205L
                                          ) and 3689348814741910323L
                                    ) and 1085102592571150095L
                              )
                              + (
                                 (
                                    (
                                          (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) ushr 2 and 3689348814741910323L
                                             )
                                             + (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) and 3689348814741910323L
                                             ) ushr 4
                                       )
                                       + (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) ushr 2 and 3689348814741910323L
                                       )
                                       + (
                                          (
                                                v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                ) ushr 8 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8
                                                ) ushr 16 or (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16
                                                ) ushr 32
                                             )
                                             - (
                                                (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                ) ushr 1 and 6148914691236517205L
                                             ) and 3689348814741910323L
                                       ) and 1085102592571150095L
                                 ) ushr 8
                              )
                              + (
                                 (
                                       (
                                             (
                                                   (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      )
                                                      - (
                                                         (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         ) ushr 1 and 6148914691236517205L
                                                      ) ushr 2 and 3689348814741910323L
                                                )
                                                + (
                                                   (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      )
                                                      - (
                                                         (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         ) ushr 1 and 6148914691236517205L
                                                      ) and 3689348814741910323L
                                                ) ushr 4
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) ushr 2 and 3689348814741910323L
                                          )
                                          + (
                                             (
                                                   v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                   ) ushr 8 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8
                                                   ) ushr 16 or (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16
                                                   ) ushr 32
                                                )
                                                - (
                                                   (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   ) ushr 1 and 6148914691236517205L
                                                ) and 3689348814741910323L
                                          ) and 1085102592571150095L
                                    )
                                    + (
                                       (
                                          (
                                                (
                                                      (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         )
                                                         - (
                                                            (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                        v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                     ) ushr 8
                                                                  ) ushr 16
                                                               ) ushr 32
                                                            ) ushr 1 and 6148914691236517205L
                                                         ) ushr 2 and 3689348814741910323L
                                                   )
                                                   + (
                                                      (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16
                                                            ) ushr 32
                                                         )
                                                         - (
                                                            (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8
                                                               ) ushr 16 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                  ) ushr 8 or (
                                                                     v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                        v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                                     ) ushr 8
                                                                  ) ushr 16
                                                               ) ushr 32
                                                            ) ushr 1 and 6148914691236517205L
                                                         ) and 3689348814741910323L
                                                   ) ushr 4
                                             )
                                             + (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) ushr 2 and 3689348814741910323L
                                             )
                                             + (
                                                (
                                                      v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                      ) ushr 8 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8
                                                      ) ushr 16 or (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16
                                                      ) ushr 32
                                                   )
                                                   - (
                                                      (
                                                         v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                         ) ushr 8 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8
                                                         ) ushr 16 or (
                                                            v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                            ) ushr 8 or (
                                                               v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4 or (
                                                                  v or v ushr 1 or (v or v ushr 1) ushr 2 or (v or v ushr 1 or (v or v ushr 1) ushr 2) ushr 4
                                                               ) ushr 8
                                                            ) ushr 16
                                                         ) ushr 32
                                                      ) ushr 1 and 6148914691236517205L
                                                   ) and 3689348814741910323L
                                             ) and 1085102592571150095L
                                       ) ushr 8
                                    ) ushr 16
                              ) ushr 32 and 63L
                        )
                        + (long)3
                  )
                  / (long)4
            )
         );
         var `data$iv`: ByteArray = `tail$iv`.data;
         var `pos$iv`: Int = `tail$iv`.limit + `width$iv` - 1;

         for (int start$iv = tail$iv.limit; pos$iv >= start$iv; pos$iv--) {
            `data$iv`[`pos$iv`] = -Buffer.getHEX_DIGIT_BYTES()[(int)(`v$iv` and 15L)];
            `v$iv` >>>= 4;
         }

         `tail$iv`.limit += `width$iv`;
         this.setSize$okio(this.size() + (long)`width$iv`);
         var10000 = this;
      }

      return var10000;
   }

   internal fun writableSegment(minimumCapacity: Int): Segment {
      if (minimumCapacity < 1 || minimumCapacity > 8192) {
         throw new IllegalArgumentException("unexpected capacity".toString());
      } else {
         var var10000: Segment;
         if (this.head == null) {
            var `tail$iv`: Segment = SegmentPool.take();
            this.head = `tail$iv`;
            `tail$iv`.prev = `tail$iv`;
            `tail$iv`.next = `tail$iv`;
            var10000 = `tail$iv`;
         } else {
            var10000 = this.head;
            var var7: Segment = var10000.prev;
            if (var7.limit + minimumCapacity > 8192 || !var7.owner) {
               var7 = var7.push(SegmentPool.take());
            }

            var10000 = var7;
         }

         return var10000;
      }
   }

   public override fun write(source: Buffer, byteCount: Long) {
      var `$this$commonWrite$iv`: Buffer = this;
      var `byteCount$iv`: Long = byteCount;
      if (source === this) {
         throw new IllegalArgumentException("source == this".toString());
      } else {
         -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);

         while (byteCount$iv > 0L) {
            var var10001: Segment = source.head;
            var var19: Int = var10001.limit;
            var var10002: Segment = source.head;
            if (`byteCount$iv` < (long)(var19 - var10002.pos)) {
               var var16: Segment;
               if (`$this$commonWrite$iv`.head != null) {
                  var16 = `$this$commonWrite$iv`.head;
                  var16 = var16.prev;
               } else {
                  var16 = null;
               }

               if (var16 != null && var16.owner && `byteCount$iv` + (long)var16.limit - (long)(if (var16.shared) 0 else var16.pos) <= 8192L) {
                  var var18: Segment = source.head;
                  var18.writeTo(var16, (int)`byteCount$iv`);
                  source.setSize$okio(source.size() - `byteCount$iv`);
                  `$this$commonWrite$iv`.setSize$okio(`$this$commonWrite$iv`.size() + `byteCount$iv`);
                  break;
               }

               var10001 = source.head;
               source.head = var10001.split((int)`byteCount$iv`);
            }

            var var14: Segment = source.head;
            var `movedByteCount$iv`: Long = (long)(var14.limit - var14.pos);
            source.head = var14.pop();
            if (`$this$commonWrite$iv`.head == null) {
               `$this$commonWrite$iv`.head = var14;
               var14.prev = var14;
               var14.next = var14.prev;
            } else {
               var var17: Segment = `$this$commonWrite$iv`.head;
               var `tail$iv`: Segment = var17.prev;
               `tail$iv`.push(var14).compact();
            }

            source.setSize$okio(source.size() - `movedByteCount$iv`);
            `$this$commonWrite$iv`.setSize$okio(`$this$commonWrite$iv`.size() + `movedByteCount$iv`);
            `byteCount$iv` -= `movedByteCount$iv`;
         }
      }
   }

   public override fun read(sink: Buffer, byteCount: Long): Long {
      var var10: Long = byteCount;
      if (byteCount < 0L) {
         throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
      } else {
         var var10000: Long;
         if (this.size() == 0L) {
            var10000 = -1L;
         } else {
            if (byteCount > this.size()) {
               var10 = this.size();
            }

            sink.write(this, var10);
            var10000 = var10;
         }

         return var10000;
      }
   }

   public override fun indexOf(b: Byte): Long {
      return this.indexOf(b, 0L, java.lang.Long.MAX_VALUE);
   }

   public override fun indexOf(b: Byte, fromIndex: Long): Long {
      return this.indexOf(b, fromIndex, java.lang.Long.MAX_VALUE);
   }

   public override fun indexOf(b: Byte, fromIndex: Long, toIndex: Long): Long {
      var var45: Long = fromIndex;
      var var46: Long = toIndex;
      if (0L > fromIndex || fromIndex > toIndex) {
         throw new IllegalArgumentException(("size=" + this.size() + " fromIndex=" + fromIndex + " toIndex=" + toIndex).toString());
      } else {
         if (toIndex > this.size()) {
            var46 = this.size();
         }

         var var10000: Long;
         if (fromIndex == var46) {
            var10000 = -1L;
         } else {
            var `fromIndex$iv$iv`: Long = fromIndex;
            if (this.head == null) {
               var10000 = -1L;
            } else {
               var `s$iv$iv`: Segment = this.head;
               if (this.size() - fromIndex < fromIndex) {
                  var `offset$iv$iv`: Long;
                  for (offset$iv$iv = this.size(); offset$iv$iv > fromIndex$iv$iv; offset$iv$iv -= (long)(var47.limit - var47.pos)) {
                     var47 = `s$iv$iv`.prev;
                     `s$iv$iv` = var47;
                  }

                  if (`s$iv$iv` == null) {
                     var10000 = -1L;
                  } else {
                     var `s$iv`: Segment = `s$iv$iv`;
                     var `offset$iv`: Long = `offset$iv$iv`;

                     while (offset$iv < var46) {
                        var `data$iv`: ByteArray = `s$iv`.data;
                        var `limit$iv`: Int = (int)Math.min((long)`s$iv`.limit, (long)`s$iv`.pos + var46 - `offset$iv`);

                        for (int pos$iv = (int)((long)s$iv.pos + var45 - offset$iv); pos$iv < limit$iv; pos$iv++) {
                           if (`data$iv`[`data$iv`] == b) {
                              return (long)(`data$iv` - `s$iv`.pos) + `offset$iv`;
                           }
                        }

                        `offset$iv` += (long)(`s$iv`.limit - `s$iv`.pos);
                        var45 = `offset$iv`;
                        var var48: Segment = `s$iv`.next;
                        `s$iv` = var48;
                     }

                     var10000 = -1L;
                  }
               } else {
                  var `offset$iv$ivx`: Long = 0L;

                  while (true) {
                     var `nextOffset$iv$iv`: Long = `offset$iv$ivx` + (long)(`s$iv$iv`.limit - `s$iv$iv`.pos);
                     if (`offset$iv$ivx` + (long)(`s$iv$iv`.limit - `s$iv$iv`.pos) > `fromIndex$iv$iv`) {
                        if (`s$iv$iv` == null) {
                           var10000 = -1L;
                        } else {
                           var `s$iv`: Segment = `s$iv$iv`;
                           var `offset$iv`: Long = `offset$iv$ivx`;

                           while (offset$iv < var46) {
                              var var43: ByteArray = `s$iv`.data;
                              var `limit$iv`: Int = (int)Math.min((long)`s$iv`.limit, (long)`s$iv`.pos + var46 - `offset$iv`);

                              for (int pos$ivx = (int)((long)s$iv.pos + var45 - offset$iv); pos$ivx < limit$iv; pos$ivx++) {
                                 if (var43[`pos$ivx`] == b) {
                                    return (long)(`pos$ivx` - `s$iv`.pos) + `offset$iv`;
                                 }
                              }

                              `offset$iv` += (long)(`s$iv`.limit - `s$iv`.pos);
                              var45 = `offset$iv`;
                              var var50: Segment = `s$iv`.next;
                              `s$iv` = var50;
                           }

                           var10000 = -1L;
                        }
                        break;
                     }

                     var var49: Segment = `s$iv$iv`.next;
                     `s$iv$iv` = var49;
                     `offset$iv$ivx` = `nextOffset$iv$iv`;
                  }
               }
            }
         }

         return var10000;
      }
   }

   @Throws(java/io/IOException::class)
   public override fun indexOf(bytes: ByteString): Long {
      return this.indexOf(bytes, 0L);
   }

   @Throws(java/io/IOException::class)
   public override fun indexOf(bytes: ByteString, fromIndex: Long): Long {
      var var61: Long = fromIndex;
      if (bytes.size() <= 0) {
         throw new IllegalArgumentException("bytes is empty".toString());
      } else if (fromIndex < 0L) {
         throw new IllegalArgumentException(("fromIndex < 0: " + fromIndex).toString());
      } else {
         var `fromIndex$iv$iv`: Long = fromIndex;
         var var10000: Long;
         if (this.head == null) {
            var10000 = -1L;
         } else {
            var `s$iv$iv`: Segment = this.head;
            if (this.size() - fromIndex < fromIndex) {
               var `offset$iv$iv`: Long;
               for (offset$iv$iv = this.size(); offset$iv$iv > fromIndex$iv$iv; offset$iv$iv -= (long)(var62.limit - var62.pos)) {
                  var62 = `s$iv$iv`.prev;
                  `s$iv$iv` = var62;
               }

               if (`s$iv$iv` == null) {
                  var10000 = -1L;
               } else {
                  var `s$iv`: Segment = `s$iv$iv`;
                  var `offset$iv`: Long = `offset$iv$iv`;
                  var `targetByteArray$iv`: ByteArray = bytes.internalArray$okio();
                  var `b0$iv`: Byte = `targetByteArray$iv`[0];
                  var `targetByteArray$ivx`: Int = bytes.size();
                  var `resultLimit$iv`: Long = this.size() - (long)`targetByteArray$ivx` + 1L;

                  while (offset$iv < resultLimit$iv) {
                     var `data$iv`: ByteArray = `s$iv`.data;
                     var `segmentLimit$iv`: Int = (int)Math.min((long)`s$iv`.limit, (long)`s$iv`.pos + `resultLimit$iv` - `offset$iv`);

                     for (int pos$iv = (int)((long)s$iv.pos + var61 - offset$iv); pos$iv < segmentLimit$iv; pos$iv++) {
                        if (`data$iv`[var58] == `b0$iv` && -Buffer.rangeEquals(`s$iv`, var58 + 1, `targetByteArray$iv`, 1, `targetByteArray$ivx`)) {
                           return (long)(var58 - `s$iv`.pos) + `offset$iv`;
                        }
                     }

                     `offset$iv` += (long)(`s$iv`.limit - `s$iv`.pos);
                     var61 = `offset$iv`;
                     var var63: Segment = `s$iv`.next;
                     `s$iv` = var63;
                  }

                  var10000 = -1L;
               }
            } else {
               var `offset$iv$ivx`: Long = 0L;

               while (true) {
                  var `nextOffset$iv$iv`: Long = `offset$iv$ivx` + (long)(`s$iv$iv`.limit - `s$iv$iv`.pos);
                  if (`offset$iv$ivx` + (long)(`s$iv$iv`.limit - `s$iv$iv`.pos) > `fromIndex$iv$iv`) {
                     if (`s$iv$iv` == null) {
                        var10000 = -1L;
                     } else {
                        var `s$iv`: Segment = `s$iv$iv`;
                        var `offset$iv`: Long = `offset$iv$ivx`;
                        var var57: ByteArray = bytes.internalArray$okio();
                        var `b0$iv`: Byte = var57[0];
                        var `bytesSize$iv`: Int = bytes.size();
                        var `resultLimit$iv`: Long = this.size() - (long)`bytesSize$iv` + 1L;

                        while (offset$iv < resultLimit$iv) {
                           var `data$iv`: ByteArray = `s$iv`.data;
                           var `segmentLimit$iv`: Int = (int)Math.min((long)`s$iv`.limit, (long)`s$iv`.pos + `resultLimit$iv` - `offset$iv`);

                           for (int pos$ivx = (int)((long)s$iv.pos + var61 - offset$iv); pos$ivx < segmentLimit$iv; pos$ivx++) {
                              if (`data$iv`[`pos$ivx`] == `b0$iv` && -Buffer.rangeEquals(`s$iv`, `pos$ivx` + 1, var57, 1, `bytesSize$iv`)) {
                                 return (long)(`pos$ivx` - `s$iv`.pos) + `offset$iv`;
                              }
                           }

                           `offset$iv` += (long)(`s$iv`.limit - `s$iv`.pos);
                           var61 = `offset$iv`;
                           var var65: Segment = `s$iv`.next;
                           `s$iv` = var65;
                        }

                        var10000 = -1L;
                     }
                     break;
                  }

                  var var64: Segment = `s$iv$iv`.next;
                  `s$iv$iv` = var64;
                  `offset$iv$ivx` = `nextOffset$iv$iv`;
               }
            }
         }

         return var10000;
      }
   }

   public override fun indexOfElement(targetBytes: ByteString): Long {
      return this.indexOfElement(targetBytes, 0L);
   }

   public override fun indexOfElement(targetBytes: ByteString, fromIndex: Long): Long {
      var `$this$commonIndexOfElement$iv`: Buffer = this;
      var var63: Long = fromIndex;
      if (fromIndex < 0L) {
         throw new IllegalArgumentException(("fromIndex < 0: " + fromIndex).toString());
      } else {
         var `fromIndex$iv$iv`: Long = fromIndex;
         var var10000: Long;
         if (this.head == null) {
            var10000 = -1L;
         } else {
            var `s$iv$iv`: Segment = this.head;
            if (this.size() - fromIndex < fromIndex) {
               var `offset$iv$iv`: Long;
               for (offset$iv$iv = this.size(); offset$iv$iv > fromIndex$iv$iv; offset$iv$iv -= (long)(var64.limit - var64.pos)) {
                  var64 = `s$iv$iv`.prev;
                  `s$iv$iv` = var64;
               }

               if (`s$iv$iv` == null) {
                  var10000 = -1L;
               } else {
                  var `s$iv`: Segment = `s$iv$iv`;
                  var `offset$iv`: Long = `offset$iv$iv`;
                  if (targetBytes.size() != 2) {
                     var var44: ByteArray = targetBytes.internalArray$okio();

                     while (offset$iv < $this$commonIndexOfElement$iv.size()) {
                        var var45: ByteArray = `s$iv`.data;
                        var var46: Int = (int)((long)`s$iv`.pos + var63 - `offset$iv`);

                        for (int limit$iv = s$iv.limit; pos$iv < limit$iv; pos$iv++) {
                           var var52: Int = var45[var46];

                           for (byte t$iv : targetByteArray$iv) {
                              if (var52 == `b$iv`) {
                                 return (long)(var46 - `s$iv`.pos) + `offset$iv`;
                              }
                           }
                        }

                        `offset$iv` += (long)(`s$iv`.limit - `s$iv`.pos);
                        var63 = `offset$iv`;
                        var var66: Segment = `s$iv`.next;
                        `s$iv` = var66;
                     }
                  } else {
                     var `targetByteArray$iv`: Byte = targetBytes.getByte(0);
                     var `data$iv`: Byte = targetBytes.getByte(1);

                     while (offset$iv < $this$commonIndexOfElement$iv.size()) {
                        var `targetByteArray$ivx`: ByteArray = `s$iv`.data;
                        var `data$ivx`: Int = (int)((long)`s$iv`.pos + var63 - `offset$iv`);

                        for (int limit$iv = s$iv.limit; data$ivx < limit$iv; data$ivx++) {
                           if (`targetByteArray$ivx`[`data$ivx`] == `targetByteArray$iv` || `targetByteArray$ivx`[`data$ivx`] == `data$iv`) {
                              return (long)(`data$ivx` - `s$iv`.pos) + `offset$iv`;
                           }
                        }

                        `offset$iv` += (long)(`s$iv`.limit - `s$iv`.pos);
                        var63 = `offset$iv`;
                        var var65: Segment = `s$iv`.next;
                        `s$iv` = var65;
                     }
                  }

                  var10000 = -1L;
               }
            } else {
               var `offset$iv$ivx`: Long = 0L;

               while (true) {
                  var `nextOffset$iv$iv`: Long = `offset$iv$ivx` + (long)(`s$iv$iv`.limit - `s$iv$iv`.pos);
                  if (`offset$iv$ivx` + (long)(`s$iv$iv`.limit - `s$iv$iv`.pos) > `fromIndex$iv$iv`) {
                     if (`s$iv$iv` == null) {
                        var10000 = -1L;
                     } else {
                        var `s$iv`: Segment = `s$iv$iv`;
                        var `offset$iv`: Long = `offset$iv$ivx`;
                        if (targetBytes.size() != 2) {
                           var var48: ByteArray = targetBytes.internalArray$okio();

                           while (offset$iv < $this$commonIndexOfElement$iv.size()) {
                              var var51: ByteArray = `s$iv`.data;
                              var var54: Int = (int)((long)`s$iv`.pos + var63 - `offset$iv`);

                              for (int limit$ivx = s$iv.limit; pos$iv < limit$ivx; pos$iv++) {
                                 var var59: Int = var51[var54];

                                 for (byte t$ivx : targetByteArray$iv) {
                                    if (var59 == `t$ivx`) {
                                       return (long)(var54 - `s$iv`.pos) + `offset$iv`;
                                    }
                                 }
                              }

                              `offset$iv` += (long)(`s$iv`.limit - `s$iv`.pos);
                              var63 = `offset$iv`;
                              var var69: Segment = `s$iv`.next;
                              `s$iv` = var69;
                           }
                        } else {
                           var var47: Byte = targetBytes.getByte(0);
                           var var50: Byte = targetBytes.getByte(1);

                           while (offset$iv < $this$commonIndexOfElement$iv.size()) {
                              var var53: ByteArray = `s$iv`.data;
                              var var56: Int = (int)((long)`s$iv`.pos + var63 - `offset$iv`);

                              for (int limit$ivx = s$iv.limit; pos$iv < limit$ivx; pos$iv++) {
                                 if (var53[var56] == var47 || var53[var56] == var50) {
                                    return (long)(var56 - `s$iv`.pos) + `offset$iv`;
                                 }
                              }

                              `offset$iv` += (long)(`s$iv`.limit - `s$iv`.pos);
                              var63 = `offset$iv`;
                              var var68: Segment = `s$iv`.next;
                              `s$iv` = var68;
                           }
                        }

                        var10000 = -1L;
                     }
                     break;
                  }

                  var var67: Segment = `s$iv$iv`.next;
                  `s$iv$iv` = var67;
                  `offset$iv$ivx` = `nextOffset$iv$iv`;
               }
            }
         }

         return var10000;
      }
   }

   public override fun rangeEquals(offset: Long, bytes: ByteString): Boolean {
      return this.rangeEquals(offset, bytes, 0, bytes.size());
   }

   public override fun rangeEquals(offset: Long, bytes: ByteString, bytesOffset: Int, byteCount: Int): Boolean {
      var `$this$commonRangeEquals$iv`: Buffer = this;
      var var10000: Boolean;
      if (offset >= 0L && bytesOffset >= 0 && byteCount >= 0 && this.size() - offset >= (long)byteCount && bytes.size() - bytesOffset >= byteCount) {
         var `i$iv`: Int = 0;

         while (true) {
            if (`i$iv` >= byteCount) {
               var10000 = true;
               break;
            }

            if (`$this$commonRangeEquals$iv`.getByte(offset + (long)`i$iv`) != bytes.getByte(bytesOffset + `i$iv`)) {
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

   public override fun flush() {
   }

   public override fun isOpen(): Boolean {
      return true;
   }

   public override fun close() {
   }

   public override fun timeout(): Timeout {
      return Timeout.NONE;
   }

   public fun md5(): ByteString {
      return this.digest("MD5");
   }

   public fun sha1(): ByteString {
      return this.digest("SHA-1");
   }

   public fun sha256(): ByteString {
      return this.digest("SHA-256");
   }

   public fun sha512(): ByteString {
      return this.digest("SHA-512");
   }

   private fun digest(algorithm: String): ByteString {
      var messageDigest: MessageDigest = MessageDigest.getInstance(algorithm);
      if (this.head != null) {
         var head: Segment = this.head;
         messageDigest.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
         var var10000: Segment = head.next;
         var s: Segment = var10000;

         while (s != head) {
            messageDigest.update(s.data, s.pos, s.limit - s.pos);
            var10000 = s.next;
            s = var10000;
         }
      }

      var var10002: ByteArray = messageDigest.digest();
      return new ByteString(var10002);
   }

   public fun hmacSha1(key: ByteString): ByteString {
      return this.hmac("HmacSHA1", key);
   }

   public fun hmacSha256(key: ByteString): ByteString {
      return this.hmac("HmacSHA256", key);
   }

   public fun hmacSha512(key: ByteString): ByteString {
      return this.hmac("HmacSHA512", key);
   }

   private fun hmac(algorithm: String, key: ByteString): ByteString {
      try {
         var e: Mac = Mac.getInstance(algorithm);
         e.init(new SecretKeySpec(key.internalArray$okio(), algorithm));
         if (this.head != null) {
            var head: Segment = this.head;
            e.update(this.head.data, this.head.pos, this.head.limit - this.head.pos);
            var var10000: Segment = head.next;
            var s: Segment = var10000;

            while (s != head) {
               e.update(s.data, s.pos, s.limit - s.pos);
               var10000 = s.next;
               s = var10000;
            }
         }

         var var10002: ByteArray = e.doFinal();
         return new ByteString(var10002);
      } catch (InvalidKeyException var7) {
         throw new IllegalArgumentException(var7);
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      var `$this$commonEquals$iv`: Buffer = this;
      var var10000: Boolean;
      if (this === other) {
         var10000 = true;
      } else if (other !is Buffer) {
         var10000 = false;
      } else if (this.size() != (other as Buffer).size()) {
         var10000 = false;
      } else if (this.size() == 0L) {
         var10000 = true;
      } else {
         var var17: Segment = this.head;
         var `sa$iv`: Segment = var17;
         var var18: Segment = (other as Buffer).head;
         var `sb$iv`: Segment = var18;
         var `posA$iv`: Int = var17.pos;
         var `posB$iv`: Int = var18.pos;
         var `pos$iv`: Long = 0L;

         while (pos$iv < $this$commonEquals$iv.size()) {
            var var16: Long = (long)Math.min(`sa$iv`.limit - `posA$iv`, `sb$iv`.limit - `posB$iv`);
            var `i$iv`: Long = 0L;

            for (long var14 = var16; i$iv < var14; i$iv++) {
               if (`sa$iv`.data[`posA$iv`++] != `sb$iv`.data[`posB$iv`++]) {
                  return false;
               }
            }

            if (`posA$iv` == `sa$iv`.limit) {
               var var20: Segment = `sa$iv`.next;
               `sa$iv` = var20;
               `posA$iv` = var20.pos;
            }

            if (`posB$iv` == `sb$iv`.limit) {
               var var21: Segment = `sb$iv`.next;
               `sb$iv` = var21;
               `posB$iv` = var21.pos;
            }

            `pos$iv` += var16;
         }

         var10000 = true;
      }

      return var10000;
   }

   public override fun hashCode(): Int {
      var `$this$commonHashCode$iv`: Buffer = this;
      var var10000: Int;
      if (this.head == null) {
         var10000 = 0;
      } else {
         var `s$iv`: Segment = this.head;
         var `result$iv`: Int = 1;

         do {
            var `pos$iv`: Int = `s$iv`.pos;

            for (int limit$iv = s$iv.limit; pos$iv < limit$iv; pos$iv++) {
               `result$iv` = 31 * `result$iv` + `s$iv`.data[`pos$iv`];
            }

            var7 = `s$iv`.next;
            `s$iv` = var7;
         } while (var7 != $this$commonHashCode$iv.head);

         var10000 = `result$iv`;
      }

      return var10000;
   }

   public override fun toString(): String {
      return this.snapshot().toString();
   }

   public fun copy(): Buffer {
      var `result$iv`: Buffer = new Buffer();
      var var10000: Buffer;
      if (this.size() == 0L) {
         var10000 = `result$iv`;
      } else {
         var var7: Segment = this.head;
         var `head$iv`: Segment = var7;
         var `headCopy$iv`: Segment = var7.sharedCopy();
         `result$iv`.head = `headCopy$iv`;
         `headCopy$iv`.prev = `result$iv`.head;
         `headCopy$iv`.next = `headCopy$iv`.prev;

         for (Segment s$iv = var7.next; s$iv != head$iv; s$iv = s$iv.next) {
            var var8: Segment = `headCopy$iv`.prev;
            var8.push(`s$iv`.sharedCopy());
         }

         `result$iv`.setSize$okio(this.size());
         var10000 = `result$iv`;
      }

      return var10000;
   }

   public open fun clone(): Buffer {
      return this.copy();
   }

   public fun snapshot(): ByteString {
      if (this.size() > 2147483647L) {
         throw new IllegalStateException(("size > Int.MAX_VALUE: " + this.size()).toString());
      } else {
         return this.snapshot((int)this.size());
      }
   }

   public fun snapshot(byteCount: Int): ByteString {
      var var10000: ByteString;
      if (byteCount == 0) {
         var10000 = ByteString.EMPTY;
      } else {
         -SegmentedByteString.checkOffsetAndCount(this.size(), 0L, (long)byteCount);
         var `offset$iv`: Int = 0;
         var `segmentCount$iv`: Int = 0;

         for (Segment s$iv = this.head; offset$iv < byteCount; s$iv = s$iv.next) {
            if (`s$iv`.limit == `s$iv`.pos) {
               throw new AssertionError("s.limit == s.pos");
            }

            `offset$iv` += `s$iv`.limit - `s$iv`.pos;
            `segmentCount$iv`++;
         }

         var `segments$iv`: Array<ByteArray> = new byte[`segmentCount$iv`][];
         var `directory$iv`: IntArray = new int[`segmentCount$iv` * 2];
         `offset$iv` = 0;
         `segmentCount$iv` = 0;

         for (Segment var11 = this.head; offset$iv < byteCount; var11 = var11.next) {
            `segments$iv`[`segmentCount$iv`] = var11.data;
            `offset$iv` += var11.limit - var11.pos;
            `directory$iv`[`segmentCount$iv`] = Math.min(`offset$iv`, byteCount);
            `directory$iv`[`segmentCount$iv` + (`segments$iv` as Object[]).length] = var11.pos;
            var11.shared = true;
            `segmentCount$iv`++;
         }

         var10000 = new SegmentedByteString(`segments$iv`, `directory$iv`);
      }

      return var10000;
   }

   @JvmOverloads
   public fun readUnsafe(unsafeCursor: okio.Buffer.UnsafeCursor): okio.Buffer.UnsafeCursor {
      return -Buffer.commonReadUnsafe(this, unsafeCursor);
   }

   @JvmOverloads
   public fun readAndWriteUnsafe(unsafeCursor: okio.Buffer.UnsafeCursor): okio.Buffer.UnsafeCursor {
      return -Buffer.commonReadAndWriteUnsafe(this, unsafeCursor);
   }

   @Deprecated(message = "moved to operator function", replaceWith = @ReplaceWith(expression = "this[index]", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_getByte")
   public fun getByte(index: Long): Byte {
      return this.getByte(index);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "size", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_size")
   public fun size(): Long {
      return this.size;
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun copyTo(out: OutputStream, offset: Long): Buffer {
      return copyTo$default(this, out, offset, 0L, 4, null);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun copyTo(out: OutputStream): Buffer {
      return copyTo$default(this, out, 0L, 0L, 6, null);
   }

   @JvmOverloads
   @Throws(java/io/IOException::class)
   fun writeTo(out: OutputStream): Buffer {
      return writeTo$default(this, out, 0L, 2, null);
   }

   @JvmOverloads
   fun readUnsafe(): Buffer.UnsafeCursor {
      return readUnsafe$default(this, null, 1, null);
   }

   @JvmOverloads
   fun readAndWriteUnsafe(): Buffer.UnsafeCursor {
      return readAndWriteUnsafe$default(this, null, 1, null);
   }

   @SourceDebugExtension(["SMAP\nBuffer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Buffer.kt\nokio/Buffer$UnsafeCursor\n+ 2 Buffer.kt\nokio/internal/-Buffer\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,641:1\n1567#2:642\n1568#2:644\n1572#2:645\n1573#2,68:647\n1644#2:715\n1645#2,32:717\n1677#2,18:750\n1698#2:768\n1699#2,18:770\n1721#2:788\n1723#2,7:790\n1#3:643\n1#3:646\n1#3:716\n1#3:769\n1#3:789\n86#4:749\n*S KotlinDebug\n*F\n+ 1 Buffer.kt\nokio/Buffer$UnsafeCursor\n*L\n628#1:642\n628#1:644\n630#1:645\n630#1:647,68\n632#1:715\n632#1:717,32\n632#1:750,18\n634#1:768\n634#1:770,18\n637#1:788\n637#1:790,7\n628#1:643\n630#1:646\n632#1:716\n634#1:769\n637#1:789\n632#1:749\n*E\n"])
   class UnsafeCursor : Closeable {
      public final var buffer: Buffer?
         private set
      public final var data: ByteArray?
         private set
      public final var end: Int = -1
         private set
      public final var offset: Long = -1L
         private set
      public final var readWrite: Boolean
         private set
      internal final var segment: Segment?
      public final var start: Int = -1
         private set


      public fun next(): Int {
         var var10000: Long = this.offset;
         var var10001: Buffer = this.buffer;
         if (var10000 == var10001.size()) {
            throw new IllegalStateException("no more bytes".toString());
         } else {
            return if (this.offset == -1L) this.seek(0L) else this.seek(this.offset + (long)(this.end - this.start));
         }
      }

      public fun seek(offset: Long): Int {
         if (this.buffer == null) {
            throw new IllegalStateException("not attached to a buffer".toString());
         } else {
            var `buffer$iv`: Buffer = this.buffer;
            if (offset >= -1L && offset <= this.buffer.size()) {
               var var10000: Int;
               if (offset != -1L && offset != `buffer$iv`.size()) {
                  var `min$iv`: Long = 0L;
                  var `max$iv`: Long = `buffer$iv`.size();
                  var `head$iv`: Segment = `buffer$iv`.head;
                  var `tail$iv`: Segment = `buffer$iv`.head;
                  if (this.getSegment$okio() != null) {
                     var var22: Long = this.offset;
                     var var10001: Int = this.start;
                     var var10002: Segment = this.getSegment$okio();
                     var `segmentOffset$iv`: Long = var22 - (long)(var10001 - var10002.pos);
                     if (var22 - (long)(var10001 - var10002.pos) > offset) {
                        `max$iv` = `segmentOffset$iv`;
                        `tail$iv` = this.getSegment$okio();
                     } else {
                        `min$iv` = `segmentOffset$iv`;
                        `head$iv` = this.getSegment$okio();
                     }
                  }

                  var var20: Segment;
                  var var21: Long;
                  if (`max$iv` - offset > offset - `min$iv`) {
                     var20 = `head$iv`;
                     var21 = `min$iv`;

                     while (true) {
                        if (offset < var21 + (long)(var20.limit - var20.pos)) {
                           break;
                        }

                        var21 += (long)(var20.limit - var20.pos);
                        var20 = var20.next;
                     }
                  } else {
                     var20 = `tail$iv`;

                     for (var21 = max$iv; var21 > offset; var21 -= (long)(var20.limit - var20.pos)) {
                        var20 = var20.prev;
                     }
                  }

                  if (this.readWrite) {
                     if (var20.shared) {
                        var `unsharedNext$iv`: Segment = var20.unsharedCopy();
                        if (`buffer$iv`.head === var20) {
                           `buffer$iv`.head = `unsharedNext$iv`;
                        }

                        var20 = var20.push(`unsharedNext$iv`);
                        var var23: Segment = var20.prev;
                        var23.pop();
                     }
                  }

                  this.setSegment$okio(var20);
                  this.offset = offset;
                  this.data = var20.data;
                  this.start = var20.pos + (int)(offset - var21);
                  this.end = var20.limit;
                  var10000 = this.end - this.start;
               } else {
                  this.setSegment$okio(null);
                  this.offset = offset;
                  this.data = null;
                  this.start = -1;
                  this.end = -1;
                  var10000 = -1;
               }

               return var10000;
            } else {
               throw new ArrayIndexOutOfBoundsException("offset=" + offset + " > size=" + `buffer$iv`.size());
            }
         }
      }

      public fun resizeBuffer(newSize: Long): Long {
         var `$this$commonResizeBuffer$iv`: Buffer.UnsafeCursor = this;
         if (this.buffer == null) {
            throw new IllegalStateException("not attached to a buffer".toString());
         } else {
            var `buffer$iv`: Buffer = this.buffer;
            if (!this.readWrite) {
               throw new IllegalStateException("resizeBuffer() only permitted for read/write buffers".toString());
            } else {
               var `oldSize$iv`: Long = this.buffer.size();
               if (newSize <= `oldSize$iv`) {
                  if (newSize < 0L) {
                     throw new IllegalArgumentException(("newSize < 0: " + newSize).toString());
                  }

                  var `bytesToSubtract$iv`: Long = `oldSize$iv` - newSize;

                  while (bytesToSubtract$iv > 0L) {
                     var var10000: Segment = `buffer$iv`.head;
                     var `tail$iv`: Segment = var10000.prev;
                     var `tail$ivx`: Int = `tail$iv`.limit - `tail$iv`.pos;
                     if ((long)(`tail$iv`.limit - `tail$iv`.pos) > `bytesToSubtract$iv`) {
                        `tail$iv`.limit -= (int)`bytesToSubtract$iv`;
                        break;
                     }

                     `buffer$iv`.head = `tail$iv`.pop();
                     SegmentPool.recycle(`tail$iv`);
                     `bytesToSubtract$iv` -= (long)`tail$ivx`;
                  }

                  this.setSegment$okio(null);
                  this.offset = newSize;
                  this.data = null;
                  this.start = -1;
                  this.end = -1;
               } else if (newSize > `oldSize$iv`) {
                  var `needsToSeek$iv`: Boolean = true;
                  var `bytesToAdd$iv`: Long = newSize - `oldSize$iv`;

                  while (bytesToAdd$iv > 0L) {
                     var var24: Segment = `buffer$iv`.writableSegment$okio(1);
                     var `segmentBytesToAdd$iv`: Int = (int)Math.min(`bytesToAdd$iv`, (long)(8192 - var24.limit));
                     var24.limit += `segmentBytesToAdd$iv`;
                     `bytesToAdd$iv` -= (long)`segmentBytesToAdd$iv`;
                     if (`needsToSeek$iv`) {
                        `$this$commonResizeBuffer$iv`.setSegment$okio(var24);
                        `$this$commonResizeBuffer$iv`.offset = `oldSize$iv`;
                        `$this$commonResizeBuffer$iv`.data = var24.data;
                        `$this$commonResizeBuffer$iv`.start = var24.limit - `segmentBytesToAdd$iv`;
                        `$this$commonResizeBuffer$iv`.end = var24.limit;
                        `needsToSeek$iv` = false;
                     }
                  }
               }

               `buffer$iv`.setSize$okio(newSize);
               return `oldSize$iv`;
            }
         }
      }

      public fun expandBuffer(minByteCount: Int): Long {
         if (minByteCount <= 0) {
            throw new IllegalArgumentException(("minByteCount <= 0: " + minByteCount).toString());
         } else if (minByteCount > 8192) {
            throw new IllegalArgumentException(("minByteCount > Segment.SIZE: " + minByteCount).toString());
         } else if (this.buffer == null) {
            throw new IllegalStateException("not attached to a buffer".toString());
         } else {
            var `buffer$iv`: Buffer = this.buffer;
            if (!this.readWrite) {
               throw new IllegalStateException("expandBuffer() only permitted for read/write buffers".toString());
            } else {
               var `oldSize$iv`: Long = this.buffer.size();
               var `tail$iv`: Segment = `buffer$iv`.writableSegment$okio(minByteCount);
               var `result$iv`: Int = 8192 - `tail$iv`.limit;
               `tail$iv`.limit = 8192;
               `buffer$iv`.setSize$okio(`oldSize$iv` + (long)`result$iv`);
               this.setSegment$okio(`tail$iv`);
               this.offset = `oldSize$iv`;
               this.data = `tail$iv`.data;
               this.start = 8192 - `result$iv`;
               this.end = 8192;
               return (long)`result$iv`;
            }
         }
      }

      public override fun close() {
         if (this.buffer == null) {
            throw new IllegalStateException("not attached to a buffer".toString());
         } else {
            this.buffer = null;
            this.setSegment$okio(null);
            this.offset = -1L;
            this.data = null;
            this.start = -1;
            this.end = -1;
         }
      }
   }
}
