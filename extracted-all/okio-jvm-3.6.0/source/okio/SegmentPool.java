package okio

import java.util.concurrent.atomic.AtomicReference

internal object SegmentPool private constructor() {
   @JvmStatic
   public SegmentPool INSTANCE = new SegmentPool();
   @JvmStatic
   private AtomicReference<Segment>[] hashBuckets;

   private final val HASH_BUCKET_COUNT: Int = Integer.highestOneBit(Runtime.getRuntime().availableProcessors() * 2 - 1)
   private final val LOCK: Segment = new Segment(new byte[0], 0, 0, false, false)
   public final val MAX_SIZE: Int = 65536
   public final val byteCount: Int
      public final get() {
         var var10000: Segment = this.firstRef().get();
         return if (var10000 == null) 0 else var10000.limit;
      }

   private final val hashBuckets: Array<AtomicReference<Segment?>>


   @JvmStatic
   public fun take(): Segment {
      var firstRef: AtomicReference = INSTANCE.firstRef();
      var first: Segment = firstRef.getAndSet(LOCK);
      if (first === LOCK) {
         return new Segment();
      } else if (first == null) {
         firstRef.set(null);
         return new Segment();
      } else {
         firstRef.set(first.next);
         first.next = null;
         first.limit = 0;
         return first;
      }
   }

   @JvmStatic
   public fun recycle(segment: Segment) {
      if (segment.next != null || segment.prev != null) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else if (!segment.shared) {
         var firstRef: AtomicReference = INSTANCE.firstRef();
         var first: Segment = firstRef.getAndSet(LOCK);
         if (first != LOCK) {
            var firstLimit: Int = if (first != null) first.limit else 0;
            if ((if (first != null) first.limit else 0) >= MAX_SIZE) {
               firstRef.set(first);
            } else {
               segment.next = first;
               segment.pos = 0;
               segment.limit = firstLimit + 8192;
               firstRef.set(segment);
            }
         }
      }
   }

   private fun firstRef(): AtomicReference<Segment?> {
      return hashBuckets[(int)(Thread.currentThread().getId() and (long)HASH_BUCKET_COUNT - 1L)];
   }

   @JvmStatic
   fun {
      var var0: Int = 0;
      var var1: Int = HASH_BUCKET_COUNT;

      var var2: Array<AtomicReference>;
      for (var2 = new AtomicReference[HASH_BUCKET_COUNT]; var0 < var1; var0++) {
         var2[var0] = new AtomicReference();
      }

      hashBuckets = var2;
   }
}
