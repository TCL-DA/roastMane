package okio

import java.io.EOFException
import java.io.IOException
import java.util.zip.DataFormatException
import java.util.zip.Inflater
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nInflaterSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InflaterSource.kt\nokio/InflaterSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,147:1\n1#2:148\n86#3:149\n*S KotlinDebug\n*F\n+ 1 InflaterSource.kt\nokio/InflaterSource\n*L\n73#1:149\n*E\n"])
class InflaterSource internal constructor(source: BufferedSource, inflater: Inflater) : Source {
   private final var bufferBytesHeldByInflater: Int
   private final var closed: Boolean
   private final val inflater: Inflater
   private final val source: BufferedSource

   init {
      this.source = source;
      this.inflater = inflater;
   }

   public constructor(source: Source, inflater: Inflater) : this(Okio.buffer(source), inflater)
   @Throws(java/io/IOException::class)
   public override fun read(sink: Buffer, byteCount: Long): Long {
      do {
         var bytesInflated: Long = this.readOrInflate(sink, byteCount);
         if (bytesInflated > 0L) {
            return bytesInflated;
         }

         if (this.inflater.finished() || this.inflater.needsDictionary()) {
            return -1L;
         }
      } while (!this.source.exhausted());

      throw new EOFException("source exhausted prematurely");
   }

   @Throws(java/io/IOException::class)
   public fun readOrInflate(sink: Buffer, byteCount: Long): Long {
      if (byteCount < 0L) {
         throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
      } else if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else if (byteCount == 0L) {
         return 0L;
      } else {
         try {
            var e: Segment = sink.writableSegment$okio(1);
            var toRead: Int = (int)Math.min(byteCount, (long)(8192 - e.limit));
            this.refill();
            var var13: Int = this.inflater.inflate(e.data, e.limit, toRead);
            this.releaseBytesAfterInflate();
            if (var13 > 0) {
               e.limit += var13;
               sink.setSize$okio(sink.size() + (long)var13);
               return (long)var13;
            } else {
               if (e.pos == e.limit) {
                  sink.head = e.pop();
                  SegmentPool.recycle(e);
               }

               return 0L;
            }
         } catch (DataFormatException var8) {
            throw new IOException(var8);
         }
      }
   }

   @Throws(java/io/IOException::class)
   public fun refill(): Boolean {
      if (!this.inflater.needsInput()) {
         return false;
      } else if (this.source.exhausted()) {
         return true;
      } else {
         var var10000: Segment = this.source.getBuffer().head;
         this.bufferBytesHeldByInflater = var10000.limit - var10000.pos;
         this.inflater.setInput(var10000.data, var10000.pos, this.bufferBytesHeldByInflater);
         return false;
      }
   }

   private fun releaseBytesAfterInflate() {
      if (this.bufferBytesHeldByInflater != 0) {
         var toRelease: Int = this.bufferBytesHeldByInflater - this.inflater.getRemaining();
         this.bufferBytesHeldByInflater -= toRelease;
         this.source.skip((long)toRelease);
      }
   }

   public override fun timeout(): Timeout {
      return this.source.timeout();
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      if (!this.closed) {
         this.inflater.end();
         this.closed = true;
         this.source.close();
      }
   }
}
