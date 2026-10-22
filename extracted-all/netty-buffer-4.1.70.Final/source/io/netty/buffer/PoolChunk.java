package io.netty.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

final class PoolChunk<T> implements PoolChunkMetric {
   private static final int SIZE_BIT_LENGTH = 15;
   private static final int INUSED_BIT_LENGTH = 1;
   private static final int SUBPAGE_BIT_LENGTH = 1;
   private static final int BITMAP_IDX_BIT_LENGTH = 32;
   static final int IS_SUBPAGE_SHIFT = 32;
   static final int IS_USED_SHIFT = 33;
   static final int SIZE_SHIFT = 34;
   static final int RUN_OFFSET_SHIFT = 49;
   final PoolArena<T> arena;
   final Object base;
   final T memory;
   final boolean unpooled;
   private final LongLongHashMap runsAvailMap;
   private final LongPriorityQueue[] runsAvail;
   private final PoolSubpage<T>[] subpages;
   private final int pageSize;
   private final int pageShifts;
   private final int chunkSize;
   private final Deque<ByteBuffer> cachedNioBuffers;
   int freeBytes;
   int pinnedBytes;
   PoolChunkList<T> parent;
   PoolChunk<T> prev;
   PoolChunk<T> next;

   PoolChunk(PoolArena<T> arena, Object base, T memory, int pageSize, int pageShifts, int chunkSize, int maxPageIdx) {
      this.unpooled = false;
      this.arena = arena;
      this.base = base;
      this.memory = memory;
      this.pageSize = pageSize;
      this.pageShifts = pageShifts;
      this.chunkSize = chunkSize;
      this.freeBytes = chunkSize;
      this.runsAvail = newRunsAvailqueueArray(maxPageIdx);
      this.runsAvailMap = new LongLongHashMap(-1L);
      this.subpages = new PoolSubpage[chunkSize >> pageShifts];
      int pages = chunkSize >> pageShifts;
      long initHandle = (long)pages << 34;
      this.insertAvailRun(0, pages, initHandle);
      this.cachedNioBuffers = new ArrayDeque<>(8);
   }

   PoolChunk(PoolArena<T> arena, Object base, T memory, int size) {
      this.unpooled = true;
      this.arena = arena;
      this.base = base;
      this.memory = memory;
      this.pageSize = 0;
      this.pageShifts = 0;
      this.runsAvailMap = null;
      this.runsAvail = null;
      this.subpages = null;
      this.chunkSize = size;
      this.cachedNioBuffers = null;
   }

   private static LongPriorityQueue[] newRunsAvailqueueArray(int size) {
      LongPriorityQueue[] queueArray = new LongPriorityQueue[size];

      for (int i = 0; i < queueArray.length; i++) {
         queueArray[i] = new LongPriorityQueue();
      }

      return queueArray;
   }

   private void insertAvailRun(int runOffset, int pages, long handle) {
      int pageIdxFloor = this.arena.pages2pageIdxFloor(pages);
      LongPriorityQueue queue = this.runsAvail[pageIdxFloor];
      queue.offer(handle);
      this.insertAvailRun0(runOffset, handle);
      if (pages > 1) {
         this.insertAvailRun0(lastPage(runOffset, pages), handle);
      }
   }

   private void insertAvailRun0(int runOffset, long handle) {
      long pre = this.runsAvailMap.put((long)runOffset, handle);

      assert pre == -1L;
   }

   private void removeAvailRun(long handle) {
      int pageIdxFloor = this.arena.pages2pageIdxFloor(runPages(handle));
      LongPriorityQueue queue = this.runsAvail[pageIdxFloor];
      this.removeAvailRun(queue, handle);
   }

   private void removeAvailRun(LongPriorityQueue queue, long handle) {
      queue.remove(handle);
      int runOffset = runOffset(handle);
      int pages = runPages(handle);
      this.runsAvailMap.remove((long)runOffset);
      if (pages > 1) {
         this.runsAvailMap.remove((long)lastPage(runOffset, pages));
      }
   }

   private static int lastPage(int runOffset, int pages) {
      return runOffset + pages - 1;
   }

   private long getAvailRunByOffset(int runOffset) {
      return this.runsAvailMap.get((long)runOffset);
   }

   @Override
   public int usage() {
      int freeBytes;
      synchronized (this.arena) {
         freeBytes = this.freeBytes;
      }

      return this.usage(freeBytes);
   }

   private int usage(int freeBytes) {
      if (freeBytes == 0) {
         return 100;
      } else {
         int freePercentage = (int)((long)freeBytes * 100L / (long)this.chunkSize);
         return freePercentage == 0 ? 99 : 100 - freePercentage;
      }
   }

   boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int sizeIdx, PoolThreadCache cache) {
      long handle;
      if (sizeIdx <= this.arena.smallMaxSizeIdx) {
         handle = this.allocateSubpage(sizeIdx);
         if (handle < 0L) {
            return false;
         }

         assert isSubpage(handle);
      } else {
         int runSize = this.arena.sizeIdx2size(sizeIdx);
         handle = this.allocateRun(runSize);
         if (handle < 0L) {
            return false;
         }
      }

      ByteBuffer nioBuffer = this.cachedNioBuffers != null ? this.cachedNioBuffers.pollLast() : null;
      this.initBuf(buf, nioBuffer, handle, reqCapacity, cache);
      return true;
   }

   private long allocateRun(int runSize) {
      int pages = runSize >> this.pageShifts;
      int pageIdx = this.arena.pages2pageIdx(pages);
      synchronized (this.runsAvail) {
         int queueIdx = this.runFirstBestFit(pageIdx);
         if (queueIdx == -1) {
            return -1L;
         } else {
            LongPriorityQueue queue = this.runsAvail[queueIdx];
            long handle = queue.poll();

            assert handle != -1L && !isUsed(handle) : "invalid handle: " + handle;

            this.removeAvailRun(queue, handle);
            if (handle != -1L) {
               handle = this.splitLargeRun(handle, pages);
            }

            int pinnedSize = runSize(this.pageShifts, handle);
            this.freeBytes -= pinnedSize;
            this.pinnedBytes += pinnedSize;
            return handle;
         }
      }
   }

   private int calculateRunSize(int sizeIdx) {
      int maxElements = 1 << this.pageShifts - 4;
      int runSize = 0;
      int elemSize = this.arena.sizeIdx2size(sizeIdx);

      int nElements;
      do {
         runSize += this.pageSize;
         nElements = runSize / elemSize;
      } while (nElements < maxElements && runSize != nElements * elemSize);

      while (nElements > maxElements) {
         runSize -= this.pageSize;
         nElements = runSize / elemSize;
      }

      assert nElements > 0;

      assert runSize <= this.chunkSize;

      assert runSize >= elemSize;

      return runSize;
   }

   private int runFirstBestFit(int pageIdx) {
      if (this.freeBytes == this.chunkSize) {
         return this.arena.nPSizes - 1;
      } else {
         for (int i = pageIdx; i < this.arena.nPSizes; i++) {
            LongPriorityQueue queue = this.runsAvail[i];
            if (queue != null && !queue.isEmpty()) {
               return i;
            }
         }

         return -1;
      }
   }

   private long splitLargeRun(long handle, int needPages) {
      assert needPages > 0;

      int totalPages = runPages(handle);

      assert needPages <= totalPages;

      int remPages = totalPages - needPages;
      if (remPages > 0) {
         int runOffset = runOffset(handle);
         int availOffset = runOffset + needPages;
         long availRun = toRunHandle(availOffset, remPages, 0);
         this.insertAvailRun(availOffset, remPages, availRun);
         return toRunHandle(runOffset, needPages, 1);
      } else {
         return handle | 8589934592L;
      }
   }

   private long allocateSubpage(int sizeIdx) {
      PoolSubpage<T> head = this.arena.findSubpagePoolHead(sizeIdx);
      synchronized (head) {
         int runSize = this.calculateRunSize(sizeIdx);
         long runHandle = this.allocateRun(runSize);
         if (runHandle < 0L) {
            return -1L;
         } else {
            int runOffset = runOffset(runHandle);

            assert this.subpages[runOffset] == null;

            int elemSize = this.arena.sizeIdx2size(sizeIdx);
            PoolSubpage<T> subpage = new PoolSubpage<>(head, this, this.pageShifts, runOffset, runSize(this.pageShifts, runHandle), elemSize);
            this.subpages[runOffset] = subpage;
            return subpage.allocate();
         }
      }
   }

   void free(long handle, int normCapacity, ByteBuffer nioBuffer) {
      int runSize = runSize(this.pageShifts, handle);
      this.pinnedBytes -= runSize;
      if (isSubpage(handle)) {
         int sizeIdx = this.arena.size2SizeIdx(normCapacity);
         PoolSubpage<T> head = this.arena.findSubpagePoolHead(sizeIdx);
         int sIdx = runOffset(handle);
         PoolSubpage<T> subpage = this.subpages[sIdx];

         assert subpage != null && subpage.doNotDestroy;

         synchronized (head) {
            if (subpage.free(head, bitmapIdx(handle))) {
               return;
            }

            assert !subpage.doNotDestroy;

            this.subpages[sIdx] = null;
         }
      }

      synchronized (this.runsAvail) {
         long finalRun = this.collapseRuns(handle);
         finalRun &= -8589934593L;
         finalRun &= -4294967297L;
         this.insertAvailRun(runOffset(finalRun), runPages(finalRun), finalRun);
         this.freeBytes += runSize;
      }

      if (nioBuffer != null && this.cachedNioBuffers != null && this.cachedNioBuffers.size() < PooledByteBufAllocator.DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK) {
         this.cachedNioBuffers.offer(nioBuffer);
      }
   }

   private long collapseRuns(long handle) {
      return this.collapseNext(this.collapsePast(handle));
   }

   private long collapsePast(long handle) {
      while (true) {
         int runOffset = runOffset(handle);
         int runPages = runPages(handle);
         long pastRun = this.getAvailRunByOffset(runOffset - 1);
         if (pastRun == -1L) {
            return handle;
         }

         int pastOffset = runOffset(pastRun);
         int pastPages = runPages(pastRun);
         if (pastRun == handle || pastOffset + pastPages != runOffset) {
            return handle;
         }

         this.removeAvailRun(pastRun);
         handle = toRunHandle(pastOffset, pastPages + runPages, 0);
      }
   }

   private long collapseNext(long handle) {
      while (true) {
         int runOffset = runOffset(handle);
         int runPages = runPages(handle);
         long nextRun = this.getAvailRunByOffset(runOffset + runPages);
         if (nextRun == -1L) {
            return handle;
         }

         int nextOffset = runOffset(nextRun);
         int nextPages = runPages(nextRun);
         if (nextRun == handle || runOffset + runPages != nextOffset) {
            return handle;
         }

         this.removeAvailRun(nextRun);
         handle = toRunHandle(runOffset, runPages + nextPages, 0);
      }
   }

   private static long toRunHandle(int runOffset, int runPages, int inUsed) {
      return (long)runOffset << 49 | (long)runPages << 34 | (long)inUsed << 33;
   }

   void initBuf(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity, PoolThreadCache threadCache) {
      if (isRun(handle)) {
         buf.init(this, nioBuffer, handle, runOffset(handle) << this.pageShifts, reqCapacity, runSize(this.pageShifts, handle), this.arena.parent.threadCache());
      } else {
         this.initBufWithSubpage(buf, nioBuffer, handle, reqCapacity, threadCache);
      }
   }

   void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity, PoolThreadCache threadCache) {
      int runOffset = runOffset(handle);
      int bitmapIdx = bitmapIdx(handle);
      PoolSubpage<T> s = this.subpages[runOffset];

      assert s.doNotDestroy;

      assert reqCapacity <= s.elemSize;

      int offset = (runOffset << this.pageShifts) + bitmapIdx * s.elemSize;
      buf.init(this, nioBuffer, handle, offset, reqCapacity, s.elemSize, threadCache);
   }

   @Override
   public int chunkSize() {
      return this.chunkSize;
   }

   @Override
   public int freeBytes() {
      synchronized (this.arena) {
         return this.freeBytes;
      }
   }

   public int pinnedBytes() {
      synchronized (this.arena) {
         return this.pinnedBytes;
      }
   }

   @Override
   public String toString() {
      int freeBytes;
      synchronized (this.arena) {
         freeBytes = this.freeBytes;
      }

      return "Chunk("
         + Integer.toHexString(System.identityHashCode(this))
         + ": "
         + this.usage(freeBytes)
         + "%, "
         + (this.chunkSize - freeBytes)
         + '/'
         + this.chunkSize
         + ')';
   }

   void destroy() {
      this.arena.destroyChunk(this);
   }

   static int runOffset(long handle) {
      return (int)(handle >> 49);
   }

   static int runSize(int pageShifts, long handle) {
      return runPages(handle) << pageShifts;
   }

   static int runPages(long handle) {
      return (int)(handle >> 34 & 32767L);
   }

   static boolean isUsed(long handle) {
      return (handle >> 33 & 1L) == 1L;
   }

   static boolean isRun(long handle) {
      return !isSubpage(handle);
   }

   static boolean isSubpage(long handle) {
      return (handle >> 32 & 1L) == 1L;
   }

   static int bitmapIdx(long handle) {
      return (int)handle;
   }
}
