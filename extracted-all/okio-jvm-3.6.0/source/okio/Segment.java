package okio

import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSegment.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Segment.kt\nokio/Segment\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,187:1\n1#2:188\n*E\n"])
internal class Segment {
   @JvmStatic
   public Segment.Companion Companion = new Segment.Companion(null);
   @JvmStatic
   public int SIZE = 8192;
   @JvmStatic
   public int SHARE_MINIMUM = 1024;

   public final val data: ByteArray
   public final var limit: Int
      private set
   public final var next: Segment?
      private set
   public final var owner: Boolean
      private set
   public final var pos: Int
      private set
   public final var prev: Segment?
      private set
   public final var shared: Boolean
      private set

   public constructor()  {
      this.data = new byte[8192];
      this.owner = true;
      this.shared = false;
   }

   public constructor(data: ByteArray, pos: Int, limit: Int, shared: Boolean, owner: Boolean)  {
      this.data = data;
      this.pos = pos;
      this.limit = limit;
      this.shared = shared;
      this.owner = owner;
   }

   public fun sharedCopy(): Segment {
      this.shared = true;
      return new Segment(this.data, this.pos, this.limit, true, false);
   }

   public fun unsharedCopy(): Segment {
      var var10002: ByteArray = Arrays.copyOf(this.data, this.data.length);
      return new Segment(var10002, this.pos, this.limit, false, true);
   }

   public fun pop(): Segment? {
      var result: Segment = if (this.next != this) this.next else null;
      var var10000: Segment = this.prev;
      var10000.next = this.next;
      var10000 = this.next;
      var10000.prev = this.prev;
      this.next = null;
      this.prev = null;
      return result;
   }

   public fun push(segment: Segment): Segment {
      segment.prev = this;
      segment.next = this.next;
      var var10000: Segment = this.next;
      var10000.prev = segment;
      this.next = segment;
      return segment;
   }

   public fun split(byteCount: Int): Segment {
      if (byteCount <= 0 || byteCount > this.limit - this.pos) {
         throw new IllegalArgumentException("byteCount out of range".toString());
      } else {
         var var4: Segment;
         if (byteCount >= 1024) {
            var4 = this.sharedCopy();
         } else {
            var4 = SegmentPool.take();
            ArraysKt.copyInto$default(this.data, var4.data, 0, this.pos, this.pos + byteCount, 2, null);
         }

         var4.limit = var4.pos + byteCount;
         this.pos += byteCount;
         var var10000: Segment = this.prev;
         var10000.push(var4);
         return var4;
      }
   }

   public fun compact() {
      if (this.prev === this) {
         throw new IllegalStateException("cannot compact".toString());
      } else {
         var var10000: Segment = this.prev;
         if (var10000.owner) {
            var byteCount: Int = this.limit - this.pos;
            var var10001: Segment = this.prev;
            var var5: Int = 8192 - var10001.limit;
            var10001 = this.prev;
            var var7: Int;
            if (var10001.shared) {
               var7 = 0;
            } else {
               var10001 = this.prev;
               var7 = var10001.pos;
            }

            if (byteCount <= var5 + var7) {
               var10001 = this.prev;
               this.writeTo(var10001, byteCount);
               this.pop();
               SegmentPool.recycle(this);
            }
         }
      }
   }

   public fun writeTo(sink: Segment, byteCount: Int) {
      if (!sink.owner) {
         throw new IllegalStateException("only owner can write".toString());
      } else {
         if (sink.limit + byteCount > 8192) {
            if (sink.shared) {
               throw new IllegalArgumentException();
            }

            if (sink.limit + byteCount - sink.pos > 8192) {
               throw new IllegalArgumentException();
            }

            ArraysKt.copyInto$default(sink.data, sink.data, 0, sink.pos, sink.limit, 2, null);
            sink.limit = sink.limit - sink.pos;
            sink.pos = 0;
         }

         ArraysKt.copyInto(this.data, sink.data, sink.limit, this.pos, this.pos + byteCount);
         sink.limit += byteCount;
         this.pos += byteCount;
      }
   }

   companion object Companion private constructor() {
      public const val SHARE_MINIMUM: Int
      public const val SIZE: Int

   }
}
