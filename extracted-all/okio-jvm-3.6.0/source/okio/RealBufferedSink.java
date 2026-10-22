package okio

import java.io.EOFException
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nRealBufferedSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSink.kt\nokio/RealBufferedSink\n+ 2 RealBufferedSink.kt\nokio/internal/-RealBufferedSink\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,142:1\n51#1:146\n51#1:152\n51#1:157\n51#1:162\n51#1:167\n51#1:172\n51#1:175\n51#1:176\n51#1:180\n51#1:185\n51#1:188\n51#1:192\n51#1:201\n51#1:210\n51#1:215\n51#1:220\n51#1:225\n51#1:230\n51#1:235\n51#1:240\n51#1:245\n51#1:250\n51#1:255\n51#1:261\n51#1:267\n51#1:279\n34#2:143\n35#2:145\n36#2,2:147\n40#2:149\n41#2:151\n42#2:153\n50#2:154\n51#2:156\n52#2:158\n56#2:159\n57#2:161\n58#2:163\n66#2:164\n67#2:166\n68#2:168\n72#2:169\n73#2:171\n74#2:173\n78#2:177\n79#2:179\n80#2:181\n88#2:182\n89#2:184\n90#2:186\n94#2,3:189\n97#2,5:193\n105#2,3:198\n108#2,5:202\n116#2:207\n117#2:209\n118#2:211\n122#2:212\n123#2:214\n124#2:216\n128#2:217\n129#2:219\n130#2:221\n134#2:222\n135#2:224\n136#2:226\n140#2:227\n141#2:229\n142#2:231\n146#2:232\n147#2:234\n148#2:236\n152#2:237\n153#2:239\n154#2:241\n158#2:242\n159#2:244\n160#2:246\n164#2:247\n165#2:249\n166#2:251\n170#2:252\n171#2:254\n172#2,2:256\n177#2:258\n178#2:260\n179#2,2:262\n184#2:264\n185#2:266\n186#2,4:268\n192#2,7:272\n199#2,16:280\n216#2:296\n218#2:297\n1#3:144\n1#3:150\n1#3:155\n1#3:160\n1#3:165\n1#3:170\n1#3:174\n1#3:178\n1#3:183\n1#3:187\n1#3:208\n1#3:213\n1#3:218\n1#3:223\n1#3:228\n1#3:233\n1#3:238\n1#3:243\n1#3:248\n1#3:253\n1#3:259\n1#3:265\n*S KotlinDebug\n*F\n+ 1 RealBufferedSink.kt\nokio/RealBufferedSink\n*L\n55#1:146\n56#1:152\n58#1:157\n59#1:162\n61#1:167\n63#1:172\n67#1:175\n78#1:176\n82#1:180\n84#1:185\n88#1:188\n93#1:192\n94#1:201\n95#1:210\n96#1:215\n97#1:220\n98#1:225\n99#1:230\n100#1:235\n101#1:240\n102#1:245\n103#1:250\n104#1:255\n105#1:261\n134#1:267\n138#1:279\n55#1:143\n55#1:145\n55#1:147,2\n56#1:149\n56#1:151\n56#1:153\n58#1:154\n58#1:156\n58#1:158\n59#1:159\n59#1:161\n59#1:163\n61#1:164\n61#1:166\n61#1:168\n63#1:169\n63#1:171\n63#1:173\n82#1:177\n82#1:179\n82#1:181\n84#1:182\n84#1:184\n84#1:186\n93#1:189,3\n93#1:193,5\n94#1:198,3\n94#1:202,5\n95#1:207\n95#1:209\n95#1:211\n96#1:212\n96#1:214\n96#1:216\n97#1:217\n97#1:219\n97#1:221\n98#1:222\n98#1:224\n98#1:226\n99#1:227\n99#1:229\n99#1:231\n100#1:232\n100#1:234\n100#1:236\n101#1:237\n101#1:239\n101#1:241\n102#1:242\n102#1:244\n102#1:246\n103#1:247\n103#1:249\n103#1:251\n104#1:252\n104#1:254\n104#1:256,2\n105#1:258\n105#1:260\n105#1:262,2\n134#1:264\n134#1:266\n134#1:268,4\n138#1:272,7\n138#1:280,16\n139#1:296\n140#1:297\n55#1:144\n56#1:150\n58#1:155\n59#1:160\n61#1:165\n63#1:170\n82#1:178\n84#1:183\n95#1:208\n96#1:213\n97#1:218\n98#1:223\n99#1:228\n100#1:233\n101#1:238\n102#1:243\n103#1:248\n104#1:253\n105#1:259\n134#1:265\n*E\n"])
internal class RealBufferedSink(sink: Sink) : BufferedSink {
   public open val buffer: Buffer
      public open inline get() {
         return this.bufferField;
      }

   public final val bufferField: Buffer
   public final var closed: Boolean
      private set
   public final val sink: Sink

   init {
      this.sink = sink;
      this.bufferField = new Buffer();
   }

   public override fun buffer(): Buffer {
      return this.bufferField;
   }

   public override fun write(source: Buffer, byteCount: Long) {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.write(source, byteCount);
         this.emitCompleteSegments();
      }
   }

   public override fun write(byteString: ByteString): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.write(byteString);
         return this.emitCompleteSegments();
      }
   }

   public override fun write(byteString: ByteString, offset: Int, byteCount: Int): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.write(byteString, offset, byteCount);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeUtf8(string: String): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeUtf8(string);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeUtf8(string: String, beginIndex: Int, endIndex: Int): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeUtf8(string, beginIndex, endIndex);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeUtf8CodePoint(codePoint: Int): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeUtf8CodePoint(codePoint);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeString(string: String, charset: Charset): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeString(string, charset);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeString(string: String, beginIndex: Int, endIndex: Int, charset: Charset): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeString(string, beginIndex, endIndex, charset);
         return this.emitCompleteSegments();
      }
   }

   public override fun write(source: ByteArray): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.write(source);
         return this.emitCompleteSegments();
      }
   }

   public override fun write(source: ByteArray, offset: Int, byteCount: Int): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.write(source, offset, byteCount);
         return this.emitCompleteSegments();
      }
   }

   public override fun write(source: ByteBuffer): Int {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         var result: Int = this.bufferField.write(source);
         this.emitCompleteSegments();
         return result;
      }
   }

   public override fun writeAll(source: Source): Long {
      var `$this$commonWriteAll$iv`: RealBufferedSink = this;
      var `totalBytesRead$iv`: Long = 0L;

      while (true) {
         var `readCount$iv`: Long = source.read(`$this$commonWriteAll$iv`.bufferField, 8192L);
         if (`readCount$iv` == -1L) {
            return `totalBytesRead$iv`;
         }

         `totalBytesRead$iv` += `readCount$iv`;
         `$this$commonWriteAll$iv`.emitCompleteSegments();
      }
   }

   public override fun write(source: Source, byteCount: Long): BufferedSink {
      var `$this$commonWrite$iv`: RealBufferedSink = this;
      var `byteCount$iv`: Long = byteCount;

      while (byteCount$iv > 0L) {
         var `read$iv`: Long = source.read(`$this$commonWrite$iv`.bufferField, `byteCount$iv`);
         if (`read$iv` == -1L) {
            throw new EOFException();
         }

         `byteCount$iv` -= `read$iv`;
         `$this$commonWrite$iv`.emitCompleteSegments();
      }

      return `$this$commonWrite$iv`;
   }

   public override fun writeByte(b: Int): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeByte(b);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeShort(s: Int): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeShort(s);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeShortLe(s: Int): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeShortLe(s);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeInt(i: Int): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeInt(i);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeIntLe(i: Int): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeIntLe(i);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeLong(v: Long): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeLong(v);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeLongLe(v: Long): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeLongLe(v);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeDecimalLong(v: Long): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeDecimalLong(v);
         return this.emitCompleteSegments();
      }
   }

   public override fun writeHexadecimalUnsignedLong(v: Long): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         this.bufferField.writeHexadecimalUnsignedLong(v);
         return this.emitCompleteSegments();
      }
   }

   public override fun emitCompleteSegments(): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         var `byteCount$iv`: Long = this.bufferField.completeSegmentByteCount();
         if (`byteCount$iv` > 0L) {
            this.sink.write(this.bufferField, `byteCount$iv`);
         }

         return this;
      }
   }

   public override fun emit(): BufferedSink {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         var `byteCount$iv`: Long = this.bufferField.size();
         if (`byteCount$iv` > 0L) {
            this.sink.write(this.bufferField, `byteCount$iv`);
         }

         return this;
      }
   }

   public override fun outputStream(): OutputStream {
      return new OutputStream(this) {
         {
            this.this$0 = `$receiver`;
         }

         @Override
         public void write(int b) {
            if (this.this$0.closed) {
               throw new IOException("closed");
            } else {
               this.this$0.bufferField.writeByte((byte)b);
               this.this$0.emitCompleteSegments();
            }
         }

         @Override
         public void write(@NotNull byte[] data, int offset, int byteCount) {
            if (this.this$0.closed) {
               throw new IOException("closed");
            } else {
               this.this$0.bufferField.write(data, offset, byteCount);
               this.this$0.emitCompleteSegments();
            }
         }

         @Override
         public void flush() {
            if (!this.this$0.closed) {
               this.this$0.flush();
            }
         }

         @Override
         public void close() {
            this.this$0.close();
         }

         @NotNull
         @Override
         public java.lang.String toString() {
            return this.this$0 + ".outputStream()";
         }
      };
   }

   public override fun flush() {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else {
         if (this.bufferField.size() > 0L) {
            this.sink.write(this.bufferField, this.bufferField.size());
         }

         this.sink.flush();
      }
   }

   public override fun isOpen(): Boolean {
      return !this.closed;
   }

   public override fun close() {
      var `$this$commonClose$iv`: RealBufferedSink = this;
      if (!this.closed) {
         var `thrown$iv`: java.lang.Throwable = null;

         try {
            if (`$this$commonClose$iv`.bufferField.size() > 0L) {
               `$this$commonClose$iv`.sink.write(`$this$commonClose$iv`.bufferField, `$this$commonClose$iv`.bufferField.size());
            }
         } catch (java.lang.Throwable var6) {
            `thrown$iv` = var6;
         }

         try {
            `$this$commonClose$iv`.sink.close();
         } catch (java.lang.Throwable var7) {
            if (`thrown$iv` == null) {
               `thrown$iv` = var7;
            }
         }

         this.closed = true;
         if (`thrown$iv` != null) {
            throw `thrown$iv`;
         }
      }
   }

   public override fun timeout(): Timeout {
      return this.sink.timeout();
   }

   public override fun toString(): String {
      return "buffer(" + this.sink + ')';
   }
}
