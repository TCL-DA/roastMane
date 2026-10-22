package io.netty.buffer;

import io.netty.util.internal.LongCounter;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

abstract class PoolArena<T> extends SizeClasses implements PoolArenaMetric {
   static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();
   final PooledByteBufAllocator parent;
   final int numSmallSubpagePools;
   final int directMemoryCacheAlignment;
   private final PoolSubpage<T>[] smallSubpagePools;
   private final PoolChunkList<T> q050;
   private final PoolChunkList<T> q025;
   private final PoolChunkList<T> q000;
   private final PoolChunkList<T> qInit;
   private final PoolChunkList<T> q075;
   private final PoolChunkList<T> q100;
   private final List<PoolChunkListMetric> chunkListMetrics;
   private long allocationsNormal;
   private final LongCounter allocationsSmall = PlatformDependent.newLongCounter();
   private final LongCounter allocationsHuge = PlatformDependent.newLongCounter();
   private final LongCounter activeBytesHuge = PlatformDependent.newLongCounter();
   private long deallocationsSmall;
   private long deallocationsNormal;
   private final LongCounter deallocationsHuge = PlatformDependent.newLongCounter();
   final AtomicInteger numThreadCaches = new AtomicInteger();

   protected PoolArena(PooledByteBufAllocator parent, int pageSize, int pageShifts, int chunkSize, int cacheAlignment) {
      super(pageSize, pageShifts, chunkSize, cacheAlignment);
      this.parent = parent;
      this.directMemoryCacheAlignment = cacheAlignment;
      this.numSmallSubpagePools = this.nSubpages;
      this.smallSubpagePools = this.newSubpagePoolArray(this.numSmallSubpagePools);

      for (int i = 0; i < this.smallSubpagePools.length; i++) {
         this.smallSubpagePools[i] = this.newSubpagePoolHead();
      }

      this.q100 = new PoolChunkList<>(this, null, 100, Integer.MAX_VALUE, chunkSize);
      this.q075 = new PoolChunkList<>(this, this.q100, 75, 100, chunkSize);
      this.q050 = new PoolChunkList<>(this, this.q075, 50, 100, chunkSize);
      this.q025 = new PoolChunkList<>(this, this.q050, 25, 75, chunkSize);
      this.q000 = new PoolChunkList<>(this, this.q025, 1, 50, chunkSize);
      this.qInit = new PoolChunkList<>(this, this.q000, Integer.MIN_VALUE, 25, chunkSize);
      this.q100.prevList(this.q075);
      this.q075.prevList(this.q050);
      this.q050.prevList(this.q025);
      this.q025.prevList(this.q000);
      this.q000.prevList(null);
      this.qInit.prevList(this.qInit);
      List<PoolChunkListMetric> metrics = new ArrayList<>(6);
      metrics.add(this.qInit);
      metrics.add(this.q000);
      metrics.add(this.q025);
      metrics.add(this.q050);
      metrics.add(this.q075);
      metrics.add(this.q100);
      this.chunkListMetrics = Collections.unmodifiableList(metrics);
   }

   private PoolSubpage<T> newSubpagePoolHead() {
      PoolSubpage<T> head = new PoolSubpage<>();
      head.prev = head;
      head.next = head;
      return head;
   }

   private PoolSubpage<T>[] newSubpagePoolArray(int size) {
      return new PoolSubpage[size];
   }

   abstract boolean isDirect();

   PooledByteBuf<T> allocate(PoolThreadCache cache, int reqCapacity, int maxCapacity) {
      PooledByteBuf<T> buf = this.newByteBuf(maxCapacity);
      this.allocate(cache, buf, reqCapacity);
      return buf;
   }

   private void allocate(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity) {
      int sizeIdx = this.size2SizeIdx(reqCapacity);
      if (sizeIdx <= this.smallMaxSizeIdx) {
         this.tcacheAllocateSmall(cache, buf, reqCapacity, sizeIdx);
      } else if (sizeIdx < this.nSizes) {
         this.tcacheAllocateNormal(cache, buf, reqCapacity, sizeIdx);
      } else {
         int normCapacity = this.directMemoryCacheAlignment > 0 ? this.normalizeSize(reqCapacity) : reqCapacity;
         this.allocateHuge(buf, normCapacity);
      }
   }

   private void tcacheAllocateSmall(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity, int sizeIdx) {
      if (!cache.allocateSmall(this, buf, reqCapacity, sizeIdx)) {
         PoolSubpage<T> head = this.smallSubpagePools[sizeIdx];
         boolean needsNormalAllocation;
         synchronized (head) {
            PoolSubpage<T> s = head.next;
            needsNormalAllocation = s == head;
            if (!needsNormalAllocation) {
               assert s.doNotDestroy && s.elemSize == this.sizeIdx2size(sizeIdx);

               long handle = s.allocate();

               assert handle >= 0L;

               s.chunk.initBufWithSubpage(buf, null, handle, reqCapacity, cache);
            }
         }

         if (needsNormalAllocation) {
            synchronized (this) {
               this.allocateNormal(buf, reqCapacity, sizeIdx, cache);
            }
         }

         this.incSmallAllocation();
      }
   }

   private void tcacheAllocateNormal(PoolThreadCache cache, PooledByteBuf<T> buf, int reqCapacity, int sizeIdx) {
      if (!cache.allocateNormal(this, buf, reqCapacity, sizeIdx)) {
         synchronized (this) {
            this.allocateNormal(buf, reqCapacity, sizeIdx, cache);
            this.allocationsNormal++;
         }
      }
   }

   private void allocateNormal(PooledByteBuf<T> buf, int reqCapacity, int sizeIdx, PoolThreadCache threadCache) {
      if (!this.q050.allocate(buf, reqCapacity, sizeIdx, threadCache)
         && !this.q025.allocate(buf, reqCapacity, sizeIdx, threadCache)
         && !this.q000.allocate(buf, reqCapacity, sizeIdx, threadCache)
         && !this.qInit.allocate(buf, reqCapacity, sizeIdx, threadCache)
         && !this.q075.allocate(buf, reqCapacity, sizeIdx, threadCache)) {
         PoolChunk<T> c = this.newChunk(this.pageSize, this.nPSizes, this.pageShifts, this.chunkSize);
         boolean success = c.allocate(buf, reqCapacity, sizeIdx, threadCache);

         assert success;

         this.qInit.add(c);
      }
   }

   private void incSmallAllocation() {
      this.allocationsSmall.increment();
   }

   private void allocateHuge(PooledByteBuf<T> buf, int reqCapacity) {
      PoolChunk<T> chunk = this.newUnpooledChunk(reqCapacity);
      this.activeBytesHuge.add((long)chunk.chunkSize());
      buf.initUnpooled(chunk, reqCapacity);
      this.allocationsHuge.increment();
   }

   void free(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolThreadCache cache) {
      if (chunk.unpooled) {
         int size = chunk.chunkSize();
         this.destroyChunk(chunk);
         this.activeBytesHuge.add((long)(-size));
         this.deallocationsHuge.increment();
      } else {
         PoolArena.SizeClass sizeClass = sizeClass(handle);
         if (cache != null && cache.add(this, chunk, nioBuffer, handle, normCapacity, sizeClass)) {
            return;
         }

         this.freeChunk(chunk, handle, normCapacity, sizeClass, nioBuffer, false);
      }
   }

   private static PoolArena.SizeClass sizeClass(long handle) {
      return PoolChunk.isSubpage(handle) ? PoolArena.SizeClass.Small : PoolArena.SizeClass.Normal;
   }

   void freeChunk(PoolChunk<T> chunk, long handle, int normCapacity, PoolArena.SizeClass sizeClass, ByteBuffer nioBuffer, boolean finalizer) {
      boolean destroyChunk;
      synchronized (this) {
         if (!finalizer) {
            switch (sizeClass) {
               case Normal:
                  this.deallocationsNormal++;
                  break;
               case Small:
                  this.deallocationsSmall++;
                  break;
               default:
                  throw new Error();
            }
         }

         destroyChunk = !chunk.parent.free(chunk, handle, normCapacity, nioBuffer);
      }

      if (destroyChunk) {
         this.destroyChunk(chunk);
      }
   }

   PoolSubpage<T> findSubpagePoolHead(int sizeIdx) {
      return this.smallSubpagePools[sizeIdx];
   }

   void reallocate(PooledByteBuf<T> buf, int newCapacity, boolean freeOldMemory) {
      assert newCapacity >= 0 && newCapacity <= buf.maxCapacity();

      int oldCapacity = buf.length;
      if (oldCapacity != newCapacity) {
         PoolChunk<T> oldChunk = buf.chunk;
         ByteBuffer oldNioBuffer = buf.tmpNioBuf;
         long oldHandle = buf.handle;
         T oldMemory = buf.memory;
         int oldOffset = buf.offset;
         int oldMaxLength = buf.maxLength;
         this.allocate(this.parent.threadCache(), buf, newCapacity);
         int bytesToCopy;
         if (newCapacity > oldCapacity) {
            bytesToCopy = oldCapacity;
         } else {
            buf.trimIndicesToCapacity(newCapacity);
            bytesToCopy = newCapacity;
         }

         this.memoryCopy(oldMemory, oldOffset, buf, bytesToCopy);
         if (freeOldMemory) {
            this.free(oldChunk, oldNioBuffer, oldHandle, oldMaxLength, buf.cache);
         }
      }
   }

   @Override
   public int numThreadCaches() {
      return this.numThreadCaches.get();
   }

   @Override
   public int numTinySubpages() {
      return 0;
   }

   @Override
   public int numSmallSubpages() {
      return this.smallSubpagePools.length;
   }

   @Override
   public int numChunkLists() {
      return this.chunkListMetrics.size();
   }

   @Override
   public List<PoolSubpageMetric> tinySubpages() {
      return Collections.emptyList();
   }

   @Override
   public List<PoolSubpageMetric> smallSubpages() {
      return subPageMetricList(this.smallSubpagePools);
   }

   @Override
   public List<PoolChunkListMetric> chunkLists() {
      return this.chunkListMetrics;
   }

   private static List<PoolSubpageMetric> subPageMetricList(PoolSubpage<?>[] pages) {
      List<PoolSubpageMetric> metrics = new ArrayList<>();

      for (PoolSubpage<?> head : pages) {
         if (head.next != head) {
            PoolSubpage<?> s = head.next;

            while (true) {
               metrics.add(s);
               s = s.next;
               if (s == head) {
                  break;
               }
            }
         }
      }

      return metrics;
   }

   @Override
   public long numAllocations() {
      long allocsNormal;
      synchronized (this) {
         allocsNormal = this.allocationsNormal;
      }

      return this.allocationsSmall.value() + allocsNormal + this.allocationsHuge.value();
   }

   @Override
   public long numTinyAllocations() {
      return 0L;
   }

   @Override
   public long numSmallAllocations() {
      return this.allocationsSmall.value();
   }

   @Override
   public synchronized long numNormalAllocations() {
      return this.allocationsNormal;
   }

   @Override
   public long numDeallocations() {
      long deallocs;
      synchronized (this) {
         deallocs = this.deallocationsSmall + this.deallocationsNormal;
      }

      return deallocs + this.deallocationsHuge.value();
   }

   @Override
   public long numTinyDeallocations() {
      return 0L;
   }

   @Override
   public synchronized long numSmallDeallocations() {
      return this.deallocationsSmall;
   }

   @Override
   public synchronized long numNormalDeallocations() {
      return this.deallocationsNormal;
   }

   @Override
   public long numHugeAllocations() {
      return this.allocationsHuge.value();
   }

   @Override
   public long numHugeDeallocations() {
      return this.deallocationsHuge.value();
   }

   @Override
   public long numActiveAllocations() {
      long val = this.allocationsSmall.value() + this.allocationsHuge.value() - this.deallocationsHuge.value();
      synchronized (this) {
         val += this.allocationsNormal - (this.deallocationsSmall + this.deallocationsNormal);
      }

      return Math.max(val, 0L);
   }

   @Override
   public long numActiveTinyAllocations() {
      return 0L;
   }

   @Override
   public long numActiveSmallAllocations() {
      return Math.max(this.numSmallAllocations() - this.numSmallDeallocations(), 0L);
   }

   @Override
   public long numActiveNormalAllocations() {
      long val;
      synchronized (this) {
         val = this.allocationsNormal - this.deallocationsNormal;
      }

      return Math.max(val, 0L);
   }

   @Override
   public long numActiveHugeAllocations() {
      return Math.max(this.numHugeAllocations() - this.numHugeDeallocations(), 0L);
   }

   @Override
   public long numActiveBytes() {
      long val = this.activeBytesHuge.value();
      synchronized (this) {
         for (int i = 0; i < this.chunkListMetrics.size(); i++) {
            for (PoolChunkMetric m : this.chunkListMetrics.get(i)) {
               val += (long)m.chunkSize();
            }
         }
      }

      return Math.max(0L, val);
   }

   public long numPinnedBytes() {
      long val = this.activeBytesHuge.value();
      synchronized (this) {
         for (int i = 0; i < this.chunkListMetrics.size(); i++) {
            for (PoolChunkMetric m : this.chunkListMetrics.get(i)) {
               val += (long)((PoolChunk)m).pinnedBytes();
            }
         }
      }

      return Math.max(0L, val);
   }

   protected abstract PoolChunk<T> newChunk(int var1, int var2, int var3, int var4);

   protected abstract PoolChunk<T> newUnpooledChunk(int var1);

   protected abstract PooledByteBuf<T> newByteBuf(int var1);

   protected abstract void memoryCopy(T var1, int var2, PooledByteBuf<T> var3, int var4);

   protected abstract void destroyChunk(PoolChunk<T> var1);

   @Override
   public synchronized String toString() {
      StringBuilder buf = new StringBuilder()
         .append("Chunk(s) at 0~25%:")
         .append(StringUtil.NEWLINE)
         .append(this.qInit)
         .append(StringUtil.NEWLINE)
         .append("Chunk(s) at 0~50%:")
         .append(StringUtil.NEWLINE)
         .append(this.q000)
         .append(StringUtil.NEWLINE)
         .append("Chunk(s) at 25~75%:")
         .append(StringUtil.NEWLINE)
         .append(this.q025)
         .append(StringUtil.NEWLINE)
         .append("Chunk(s) at 50~100%:")
         .append(StringUtil.NEWLINE)
         .append(this.q050)
         .append(StringUtil.NEWLINE)
         .append("Chunk(s) at 75~100%:")
         .append(StringUtil.NEWLINE)
         .append(this.q075)
         .append(StringUtil.NEWLINE)
         .append("Chunk(s) at 100%:")
         .append(StringUtil.NEWLINE)
         .append(this.q100)
         .append(StringUtil.NEWLINE)
         .append("small subpages:");
      appendPoolSubPages(buf, this.smallSubpagePools);
      buf.append(StringUtil.NEWLINE);
      return buf.toString();
   }

   private static void appendPoolSubPages(StringBuilder buf, PoolSubpage<?>[] subpages) {
      for (int i = 0; i < subpages.length; i++) {
         PoolSubpage<?> head = subpages[i];
         if (head.next != head) {
            buf.append(StringUtil.NEWLINE).append(i).append(": ");
            PoolSubpage<?> s = head.next;

            while (true) {
               buf.append(s);
               s = s.next;
               if (s == head) {
                  break;
               }
            }
         }
      }
   }

   @Override
   protected final void finalize() throws Throwable {
      try {
         super.finalize();
      } finally {
         destroyPoolSubPages(this.smallSubpagePools);
         this.destroyPoolChunkLists(this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100);
      }
   }

   private static void destroyPoolSubPages(PoolSubpage<?>[] pages) {
      for (PoolSubpage<?> page : pages) {
         page.destroy();
      }
   }

   private void destroyPoolChunkLists(PoolChunkList<T>... chunkLists) {
      for (PoolChunkList<T> chunkList : chunkLists) {
         chunkList.destroy(this);
      }
   }

   static final class DirectArena extends PoolArena<ByteBuffer> {
      DirectArena(PooledByteBufAllocator parent, int pageSize, int pageShifts, int chunkSize, int directMemoryCacheAlignment) {
         super(parent, pageSize, pageShifts, chunkSize, directMemoryCacheAlignment);
      }

      @Override
      boolean isDirect() {
         return true;
      }

      @Override
      protected PoolChunk<ByteBuffer> newChunk(int pageSize, int maxPageIdx, int pageShifts, int chunkSize) {
         if (this.directMemoryCacheAlignment == 0) {
            ByteBuffer memory = allocateDirect(chunkSize);
            return new PoolChunk<>(this, memory, memory, pageSize, pageShifts, chunkSize, maxPageIdx);
         } else {
            ByteBuffer base = allocateDirect(chunkSize + this.directMemoryCacheAlignment);
            ByteBuffer memory = PlatformDependent.alignDirectBuffer(base, this.directMemoryCacheAlignment);
            return new PoolChunk<>(this, base, memory, pageSize, pageShifts, chunkSize, maxPageIdx);
         }
      }

      @Override
      protected PoolChunk<ByteBuffer> newUnpooledChunk(int capacity) {
         if (this.directMemoryCacheAlignment == 0) {
            ByteBuffer memory = allocateDirect(capacity);
            return new PoolChunk<>(this, memory, memory, capacity);
         } else {
            ByteBuffer base = allocateDirect(capacity + this.directMemoryCacheAlignment);
            ByteBuffer memory = PlatformDependent.alignDirectBuffer(base, this.directMemoryCacheAlignment);
            return new PoolChunk<>(this, base, memory, capacity);
         }
      }

      private static ByteBuffer allocateDirect(int capacity) {
         return PlatformDependent.useDirectBufferNoCleaner() ? PlatformDependent.allocateDirectNoCleaner(capacity) : ByteBuffer.allocateDirect(capacity);
      }

      @Override
      protected void destroyChunk(PoolChunk<ByteBuffer> chunk) {
         if (PlatformDependent.useDirectBufferNoCleaner()) {
            PlatformDependent.freeDirectNoCleaner((ByteBuffer)chunk.base);
         } else {
            PlatformDependent.freeDirectBuffer((ByteBuffer)chunk.base);
         }
      }

      @Override
      protected PooledByteBuf<ByteBuffer> newByteBuf(int maxCapacity) {
         return (PooledByteBuf<ByteBuffer>)(HAS_UNSAFE ? PooledUnsafeDirectByteBuf.newInstance(maxCapacity) : PooledDirectByteBuf.newInstance(maxCapacity));
      }

      protected void memoryCopy(ByteBuffer src, int srcOffset, PooledByteBuf<ByteBuffer> dstBuf, int length) {
         if (length != 0) {
            if (HAS_UNSAFE) {
               PlatformDependent.copyMemory(
                  PlatformDependent.directBufferAddress(src) + (long)srcOffset,
                  PlatformDependent.directBufferAddress(dstBuf.memory) + (long)dstBuf.offset,
                  (long)length
               );
            } else {
               src = src.duplicate();
               ByteBuffer dst = dstBuf.internalNioBuffer();
               ((Buffer)src).position(srcOffset).limit(srcOffset + length);
               ((Buffer)dst).position(dstBuf.offset);
               dst.put(src);
            }
         }
      }
   }

   static final class HeapArena extends PoolArena<byte[]> {
      HeapArena(PooledByteBufAllocator parent, int pageSize, int pageShifts, int chunkSize, int directMemoryCacheAlignment) {
         super(parent, pageSize, pageShifts, chunkSize, directMemoryCacheAlignment);
      }

      private static byte[] newByteArray(int size) {
         return PlatformDependent.allocateUninitializedArray(size);
      }

      @Override
      boolean isDirect() {
         return false;
      }

      @Override
      protected PoolChunk<byte[]> newChunk(int pageSize, int maxPageIdx, int pageShifts, int chunkSize) {
         return new PoolChunk<>(this, null, newByteArray(chunkSize), pageSize, pageShifts, chunkSize, maxPageIdx);
      }

      @Override
      protected PoolChunk<byte[]> newUnpooledChunk(int capacity) {
         return new PoolChunk<>(this, null, newByteArray(capacity), capacity);
      }

      @Override
      protected void destroyChunk(PoolChunk<byte[]> chunk) {
      }

      @Override
      protected PooledByteBuf<byte[]> newByteBuf(int maxCapacity) {
         return (PooledByteBuf<byte[]>)(HAS_UNSAFE ? PooledUnsafeHeapByteBuf.newUnsafeInstance(maxCapacity) : PooledHeapByteBuf.newInstance(maxCapacity));
      }

      protected void memoryCopy(byte[] src, int srcOffset, PooledByteBuf<byte[]> dst, int length) {
         if (length != 0) {
            System.arraycopy(src, srcOffset, dst.memory, dst.offset, length);
         }
      }
   }

   static enum SizeClass {
      Small,
      Normal;
   }
}
