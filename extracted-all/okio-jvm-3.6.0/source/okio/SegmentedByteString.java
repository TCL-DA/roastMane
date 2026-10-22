package okio

import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.InvalidKeyException
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSegmentedByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SegmentedByteString.kt\nokio/SegmentedByteString\n+ 2 SegmentedByteString.kt\nokio/internal/-SegmentedByteString\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,140:1\n63#2,12:141\n63#2,12:153\n104#2,2:165\n106#2,26:168\n135#2,5:194\n142#2:199\n145#2,3:200\n63#2,8:203\n148#2,8:211\n71#2,4:219\n156#2:223\n63#2,12:224\n160#2:236\n85#2,10:237\n161#2,9:247\n95#2,4:256\n170#2,2:260\n179#2,4:262\n85#2,10:266\n183#2,3:276\n95#2,4:279\n186#2:283\n195#2,8:284\n85#2,10:292\n203#2,3:302\n95#2,4:305\n206#2:309\n215#2,5:310\n85#2,10:315\n220#2,3:325\n95#2,4:328\n223#2:332\n226#2,4:333\n234#2,6:337\n63#2,8:343\n240#2,7:351\n71#2,4:358\n247#2,2:362\n1#3:167\n*S KotlinDebug\n*F\n+ 1 SegmentedByteString.kt\nokio/SegmentedByteString\n*L\n54#1:141,12\n66#1:153,12\n78#1:165,2\n78#1:168,26\n80#1:194,5\n82#1:199\n84#1:200,3\n84#1:203,8\n84#1:211,8\n84#1:219,4\n84#1:223\n90#1:224,12\n96#1:236\n96#1:237,10\n96#1:247,9\n96#1:256,4\n96#1:260,2\n103#1:262,4\n103#1:266,10\n103#1:276,3\n103#1:279,4\n103#1:283\n110#1:284,8\n110#1:292,10\n110#1:302,3\n110#1:305,4\n110#1:309\n117#1:310,5\n117#1:315,10\n117#1:325,3\n117#1:328,4\n117#1:332\n131#1:333,4\n133#1:337,6\n133#1:343,8\n133#1:351,7\n133#1:358,4\n133#1:362,2\n78#1:167\n*E\n"])
internal class SegmentedByteString internal constructor(vararg segments: Any, directory: IntArray) : ByteString(ByteString.EMPTY.getData$okio()) {
   @Transient
   private byte[][] segments;

   internal final val directory: IntArray
   internal final val segments: Array<ByteArray>

   init {
      this.segments = segments;
      this.directory = directory;
   }

   public override fun string(charset: Charset): String {
      return this.toByteString().string(charset);
   }

   public override fun base64(): String {
      return this.toByteString().base64();
   }

   public override fun hex(): String {
      return this.toByteString().hex();
   }

   public override fun toAsciiLowercase(): ByteString {
      return this.toByteString().toAsciiLowercase();
   }

   public override fun toAsciiUppercase(): ByteString {
      return this.toByteString().toAsciiUppercase();
   }

   internal override fun digest(algorithm: String): ByteString {
      var `$this$digest_u24lambda_u241`: MessageDigest = MessageDigest.getInstance(algorithm);
      var `$this$forEachSegment$iv`: SegmentedByteString = this;
      var `segmentCount$iv`: Int = (this.getSegments$okio() as Object[]).length;
      var `s$iv`: Int = 0;

      for (int pos$iv = 0; s$iv < segmentCount$iv; s$iv++) {
         var `segmentPos$iv`: Int = `$this$forEachSegment$iv`.getDirectory$okio()[`segmentCount$iv` + `s$iv`];
         var `nextSegmentOffset$iv`: Int = `$this$forEachSegment$iv`.getDirectory$okio()[`s$iv`];
         `$this$digest_u24lambda_u241`.update(`$this$forEachSegment$iv`.getSegments$okio()[`s$iv`], `segmentPos$iv`, `nextSegmentOffset$iv` - `pos$iv`);
         `pos$iv` = `nextSegmentOffset$iv`;
      }

      var digestBytes: ByteArray = `$this$digest_u24lambda_u241`.digest();
      return new ByteString(digestBytes);
   }

   internal override fun hmac(algorithm: String, key: ByteString): ByteString {
      try {
         var e: Mac = Mac.getInstance(algorithm);
         e.init(new SecretKeySpec(key.toByteArray(), algorithm));
         var `$this$forEachSegment$iv`: SegmentedByteString = this;
         var `segmentCount$iv`: Int = (this.getSegments$okio() as Object[]).length;
         var `s$iv`: Int = 0;

         for (int pos$iv = 0; s$iv < segmentCount$iv; s$iv++) {
            var `segmentPos$iv`: Int = `$this$forEachSegment$iv`.getDirectory$okio()[`segmentCount$iv` + `s$iv`];
            var `nextSegmentOffset$iv`: Int = `$this$forEachSegment$iv`.getDirectory$okio()[`s$iv`];
            e.update(`$this$forEachSegment$iv`.getSegments$okio()[`s$iv`], `segmentPos$iv`, `nextSegmentOffset$iv` - `pos$iv`);
            `pos$iv` = `nextSegmentOffset$iv`;
         }

         var var10002: ByteArray = e.doFinal();
         return new ByteString(var10002);
      } catch (InvalidKeyException var15) {
         throw new IllegalArgumentException(var15);
      }
   }

   public override fun base64Url(): String {
      return this.toByteString().base64Url();
   }

   public override fun substring(beginIndex: Int, endIndex: Int): ByteString {
      var `$this$commonSubstring$iv`: SegmentedByteString = this;
      var `endIndex$iv`: Int = -SegmentedByteString.resolveDefaultParameter(this, endIndex);
      if (beginIndex < 0) {
         throw new IllegalArgumentException(("beginIndex=" + beginIndex + " < 0").toString());
      } else if (`endIndex$iv` > this.size()) {
         throw new IllegalArgumentException(("endIndex=" + `endIndex$iv` + " > length(" + this.size() + ')').toString());
      } else {
         var `subLen$iv`: Int = `endIndex$iv` - beginIndex;
         if (`endIndex$iv` - beginIndex < 0) {
            throw new IllegalArgumentException(("endIndex=" + `endIndex$iv` + " < beginIndex=" + beginIndex).toString());
         } else {
            var var10000: ByteString;
            if (beginIndex == 0 && `endIndex$iv` == this.size()) {
               var10000 = this;
            } else if (beginIndex == `endIndex$iv`) {
               var10000 = ByteString.EMPTY;
            } else {
               var `beginSegment$iv`: Int = okio.internal.-SegmentedByteString.segment(this, beginIndex);
               var `endSegment$iv`: Int = okio.internal.-SegmentedByteString.segment(this, `endIndex$iv` - 1);
               var `newSegments$iv`: Array<ByteArray> = ArraysKt.copyOfRange(this.getSegments$okio() as Object[], `beginSegment$iv`, `endSegment$iv` + 1) as byte[][];
               var var20: IntArray = new int[(`newSegments$iv` as Object[]).length * 2];
               var var21: Int = 0;
               var `segmentOffset$iv`: Int = `beginSegment$iv`;
               if (`beginSegment$iv` <= `endSegment$iv`) {
                  while (true) {
                     var20[var21] = Math.min(`$this$commonSubstring$iv`.getDirectory$okio()[`segmentOffset$iv`] - beginIndex, `subLen$iv`);
                     var20[var21++ + (`newSegments$iv` as Object[]).length] = `$this$commonSubstring$iv`.getDirectory$okio()[`segmentOffset$iv`
                        + (`$this$commonSubstring$iv`.getSegments$okio() as Object[]).length];
                     if (`segmentOffset$iv` == `endSegment$iv`) {
                        break;
                     }

                     `segmentOffset$iv`++;
                  }
               }

               var20[(`newSegments$iv` as Object[]).length] = var20[(`newSegments$iv` as Object[]).length]
                  + (beginIndex - (if (`beginSegment$iv` == 0) 0 else `$this$commonSubstring$iv`.getDirectory$okio()[`beginSegment$iv` - 1]));
               var10000 = new SegmentedByteString(`newSegments$iv`, var20);
            }

            return var10000;
         }
      }
   }

   internal override fun internalGet(pos: Int): Byte {
      -SegmentedByteString.checkOffsetAndCount((long)this.getDirectory$okio()[(this.getSegments$okio() as Object[]).length - 1], (long)pos, 1L);
      var `segment$iv`: Int = okio.internal.-SegmentedByteString.segment(this, pos);
      return this.getSegments$okio()[`segment$iv`][pos
         - (if (`segment$iv` == 0) 0 else this.getDirectory$okio()[`segment$iv` - 1])
         + this.getDirectory$okio()[`segment$iv` + (this.getSegments$okio() as Object[]).length]];
   }

   internal override fun getSize(): Int {
      return this.getDirectory$okio()[(this.getSegments$okio() as Object[]).length - 1];
   }

   public override fun toByteArray(): ByteArray {
      var `result$iv`: ByteArray = new byte[this.size()];
      var `resultPos$iv`: Int = 0;
      var `$this$forEachSegment$iv$iv`: SegmentedByteString = this;
      var `segmentCount$iv$iv`: Int = (this.getSegments$okio() as Object[]).length;
      var `s$iv$iv`: Int = 0;

      for (int pos$iv$iv = 0; s$iv$iv < segmentCount$iv$iv; s$iv$iv++) {
         var `segmentPos$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[`segmentCount$iv$iv` + `s$iv$iv`];
         var `nextSegmentOffset$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[`s$iv$iv`];
         var var10000: ByteArray = `$this$forEachSegment$iv$iv`.getSegments$okio()[`s$iv$iv`];
         var `byteCount$iv`: Int = `nextSegmentOffset$iv$iv` - `pos$iv$iv`;
         ArraysKt.copyInto(var10000, `result$iv`, `resultPos$iv`, `segmentPos$iv$iv`, `segmentPos$iv$iv` + (`nextSegmentOffset$iv$iv` - `pos$iv$iv`));
         `resultPos$iv` += `byteCount$iv`;
         `pos$iv$iv` = `nextSegmentOffset$iv$iv`;
      }

      return `result$iv`;
   }

   public override fun asByteBuffer(): ByteBuffer {
      var var10000: ByteBuffer = ByteBuffer.wrap(this.toByteArray()).asReadOnlyBuffer();
      return var10000;
   }

   @Throws(java/io/IOException::class)
   public override fun write(out: OutputStream) {
      var `$this$forEachSegment$iv`: SegmentedByteString = this;
      var `segmentCount$iv`: Int = (this.getSegments$okio() as Object[]).length;
      var `s$iv`: Int = 0;

      for (int pos$iv = 0; s$iv < segmentCount$iv; s$iv++) {
         var `segmentPos$iv`: Int = `$this$forEachSegment$iv`.getDirectory$okio()[`segmentCount$iv` + `s$iv`];
         var `nextSegmentOffset$iv`: Int = `$this$forEachSegment$iv`.getDirectory$okio()[`s$iv`];
         out.write(`$this$forEachSegment$iv`.getSegments$okio()[`s$iv`], `segmentPos$iv`, `nextSegmentOffset$iv` - `pos$iv`);
         `pos$iv` = `nextSegmentOffset$iv`;
      }
   }

   internal override fun write(buffer: Buffer, offset: Int, byteCount: Int) {
      var `$this$forEachSegment$iv$iv`: SegmentedByteString = this;
      var `endIndex$iv$iv`: Int = offset + byteCount;
      var `s$iv$iv`: Int = okio.internal.-SegmentedByteString.segment(this, offset);

      for (int pos$iv$iv = offset; pos$iv$iv < endIndex$iv$iv; s$iv$iv++) {
         var `segmentOffset$iv$iv`: Int = if (`s$iv$iv` == 0) 0 else `$this$forEachSegment$iv$iv`.getDirectory$okio()[`s$iv$iv` - 1];
         var `segmentSize$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[`s$iv$iv`] - `segmentOffset$iv$iv`;
         var `segmentPos$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[(`$this$forEachSegment$iv$iv`.getSegments$okio() as Object[]).length
            + `s$iv$iv`];
         var `byteCount$iv$iv`: Int = Math.min(`endIndex$iv$iv`, `segmentOffset$iv$iv` + `segmentSize$iv$iv`) - `pos$iv$iv`;
         var `segment$iv`: Segment = new Segment(
            `$this$forEachSegment$iv$iv`.getSegments$okio()[`s$iv$iv`],
            `segmentPos$iv$iv` + (`pos$iv$iv` - `segmentOffset$iv$iv`),
            `segmentPos$iv$iv` + (`pos$iv$iv` - `segmentOffset$iv$iv`) + `byteCount$iv$iv`,
            true,
            false
         );
         if (buffer.head == null) {
            `segment$iv`.prev = `segment$iv`;
            `segment$iv`.next = `segment$iv`.prev;
            buffer.head = `segment$iv`.next;
         } else {
            var var10000: Segment = buffer.head;
            var var21: Segment = var10000.prev;
            var21.push(`segment$iv`);
         }

         `pos$iv$iv` += `byteCount$iv$iv`;
      }

      buffer.setSize$okio(buffer.size() + (long)byteCount);
   }

   public override fun rangeEquals(offset: Int, other: ByteString, otherOffset: Int, byteCount: Int): Boolean {
      var var10000: Boolean;
      if (offset >= 0 && offset <= this.size() - byteCount) {
         var var22: Int = otherOffset;
         var `$this$forEachSegment$iv$iv`: SegmentedByteString = this;
         var `endIndex$iv$iv`: Int = offset + byteCount;
         var `s$iv$iv`: Int = okio.internal.-SegmentedByteString.segment(this, offset);

         for (int pos$iv$iv = offset; pos$iv$iv < endIndex$iv$iv; s$iv$iv++) {
            var `segmentOffset$iv$iv`: Int = if (`s$iv$iv` == 0) 0 else `$this$forEachSegment$iv$iv`.getDirectory$okio()[`s$iv$iv` - 1];
            var `segmentSize$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[`s$iv$iv`] - `segmentOffset$iv$iv`;
            var `segmentPos$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[(`$this$forEachSegment$iv$iv`.getSegments$okio() as Object[]).length
               + `s$iv$iv`];
            var `byteCount$iv$iv`: Int = Math.min(`endIndex$iv$iv`, `segmentOffset$iv$iv` + `segmentSize$iv$iv`) - `pos$iv$iv`;
            if (!other.rangeEquals(
               var22, `$this$forEachSegment$iv$iv`.getSegments$okio()[`s$iv$iv`], `segmentPos$iv$iv` + (`pos$iv$iv` - `segmentOffset$iv$iv`), `byteCount$iv$iv`
            )) {
               return false;
            }

            var22 += `byteCount$iv$iv`;
            `pos$iv$iv` += `byteCount$iv$iv`;
         }

         var10000 = true;
      } else {
         var10000 = false;
      }

      return var10000;
   }

   public override fun rangeEquals(offset: Int, other: ByteArray, otherOffset: Int, byteCount: Int): Boolean {
      var var10000: Boolean;
      if (offset >= 0 && offset <= this.size() - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount) {
         var var22: Int = otherOffset;
         var `$this$forEachSegment$iv$iv`: SegmentedByteString = this;
         var `endIndex$iv$iv`: Int = offset + byteCount;
         var `s$iv$iv`: Int = okio.internal.-SegmentedByteString.segment(this, offset);

         for (int pos$iv$iv = offset; pos$iv$iv < endIndex$iv$iv; s$iv$iv++) {
            var `segmentOffset$iv$iv`: Int = if (`s$iv$iv` == 0) 0 else `$this$forEachSegment$iv$iv`.getDirectory$okio()[`s$iv$iv` - 1];
            var `segmentSize$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[`s$iv$iv`] - `segmentOffset$iv$iv`;
            var `segmentPos$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[(`$this$forEachSegment$iv$iv`.getSegments$okio() as Object[]).length
               + `s$iv$iv`];
            var `byteCount$iv$iv`: Int = Math.min(`endIndex$iv$iv`, `segmentOffset$iv$iv` + `segmentSize$iv$iv`) - `pos$iv$iv`;
            if (!-SegmentedByteString.arrayRangeEquals(
               `$this$forEachSegment$iv$iv`.getSegments$okio()[`s$iv$iv`],
               `segmentPos$iv$iv` + (`pos$iv$iv` - `segmentOffset$iv$iv`),
               other,
               var22,
               `byteCount$iv$iv`
            )) {
               return false;
            }

            var22 += `byteCount$iv$iv`;
            `pos$iv$iv` += `byteCount$iv$iv`;
         }

         var10000 = true;
      } else {
         var10000 = false;
      }

      return var10000;
   }

   public override fun copyInto(offset: Int, target: ByteArray, targetOffset: Int, byteCount: Int) {
      -SegmentedByteString.checkOffsetAndCount((long)this.size(), (long)offset, (long)byteCount);
      -SegmentedByteString.checkOffsetAndCount((long)target.length, (long)targetOffset, (long)byteCount);
      var var22: Int = targetOffset;
      var `$this$forEachSegment$iv$iv`: SegmentedByteString = this;
      var `endIndex$iv$iv`: Int = offset + byteCount;
      var `s$iv$iv`: Int = okio.internal.-SegmentedByteString.segment(this, offset);

      for (int pos$iv$iv = offset; pos$iv$iv < endIndex$iv$iv; s$iv$iv++) {
         var `segmentOffset$iv$iv`: Int = if (`s$iv$iv` == 0) 0 else `$this$forEachSegment$iv$iv`.getDirectory$okio()[`s$iv$iv` - 1];
         var `segmentSize$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[`s$iv$iv`] - `segmentOffset$iv$iv`;
         var `segmentPos$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[(`$this$forEachSegment$iv$iv`.getSegments$okio() as Object[]).length
            + `s$iv$iv`];
         var `byteCount$iv$iv`: Int = Math.min(`endIndex$iv$iv`, `segmentOffset$iv$iv` + `segmentSize$iv$iv`) - `pos$iv$iv`;
         ArraysKt.copyInto(
            `$this$forEachSegment$iv$iv`.getSegments$okio()[`s$iv$iv`],
            target,
            var22,
            `segmentPos$iv$iv` + (`pos$iv$iv` - `segmentOffset$iv$iv`),
            `segmentPos$iv$iv` + (`pos$iv$iv` - `segmentOffset$iv$iv`) + `byteCount$iv$iv`
         );
         var22 += `byteCount$iv$iv`;
         `pos$iv$iv` += `byteCount$iv$iv`;
      }
   }

   public override fun indexOf(other: ByteArray, fromIndex: Int): Int {
      return this.toByteString().indexOf(other, fromIndex);
   }

   public override fun lastIndexOf(other: ByteArray, fromIndex: Int): Int {
      return this.toByteString().lastIndexOf(other, fromIndex);
   }

   private fun toByteString(): ByteString {
      return new ByteString(this.toByteArray());
   }

   internal override fun internalArray(): ByteArray {
      return this.toByteArray();
   }

   public override operator fun equals(other: Any?): Boolean {
      return other === this || other is ByteString && (other as ByteString).size() == this.size() && this.rangeEquals(0, other as ByteString, 0, this.size());
   }

   public override fun hashCode(): Int {
      var var17: Int = this.getHashCode$okio();
      var var10000: Int;
      if (var17 != 0) {
         var10000 = var17;
      } else {
         var17 = 1;
         var `$this$forEachSegment$iv$iv`: SegmentedByteString = this;
         var `segmentCount$iv$iv`: Int = (this.getSegments$okio() as Object[]).length;
         var `s$iv$iv`: Int = 0;

         for (int pos$iv$iv = 0; s$iv$iv < segmentCount$iv$iv; s$iv$iv++) {
            var `segmentPos$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[`segmentCount$iv$iv` + `s$iv$iv`];
            var `nextSegmentOffset$iv$iv`: Int = `$this$forEachSegment$iv$iv`.getDirectory$okio()[`s$iv$iv`];
            var var19: ByteArray = `$this$forEachSegment$iv$iv`.getSegments$okio()[`s$iv$iv`];
            var `byteCount$iv`: Int = `nextSegmentOffset$iv$iv` - `pos$iv$iv`;
            var `data$iv`: ByteArray = var19;
            var `i$iv`: Int = `segmentPos$iv$iv`;

            for (int limit$iv = segmentPos$iv$iv + byteCount$iv; i$iv < limit$iv; i$iv++) {
               var17 = 31 * var17 + `data$iv`[`i$iv`];
            }

            `pos$iv$iv` = `nextSegmentOffset$iv$iv`;
         }

         this.setHashCode$okio(var17);
         var10000 = var17;
      }

      return var10000;
   }

   public override fun toString(): String {
      return this.toByteString().toString();
   }

   private fun writeReplace(): Object {
      var var10000: ByteString = this.toByteString();
      return var10000;
   }
}
