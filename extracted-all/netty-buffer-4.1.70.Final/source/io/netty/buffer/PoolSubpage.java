package io.netty.buffer;

final class PoolSubpage<T> implements PoolSubpageMetric {
   final PoolChunk<T> chunk;
   private final int pageShifts;
   private final int runOffset;
   private final int runSize;
   private final long[] bitmap;
   PoolSubpage<T> prev;
   PoolSubpage<T> next;
   boolean doNotDestroy;
   int elemSize;
   private int maxNumElems;
   private int bitmapLength;
   private int nextAvail;
   private int numAvail;

   PoolSubpage() {
      this.chunk = null;
      this.pageShifts = -1;
      this.runOffset = -1;
      this.elemSize = -1;
      this.runSize = -1;
      this.bitmap = null;
   }

   PoolSubpage(PoolSubpage<T> head, PoolChunk<T> chunk, int pageShifts, int runOffset, int runSize, int elemSize) {
      this.chunk = chunk;
      this.pageShifts = pageShifts;
      this.runOffset = runOffset;
      this.runSize = runSize;
      this.elemSize = elemSize;
      this.bitmap = new long[runSize >>> 10];
      this.doNotDestroy = true;
      if (elemSize != 0) {
         this.maxNumElems = this.numAvail = runSize / elemSize;
         this.nextAvail = 0;
         this.bitmapLength = this.maxNumElems >>> 6;
         if ((this.maxNumElems & 63) != 0) {
            this.bitmapLength++;
         }

         for (int i = 0; i < this.bitmapLength; i++) {
            this.bitmap[i] = 0L;
         }
      }

      this.addToPool(head);
   }

   long allocate() {
      if (this.numAvail != 0 && this.doNotDestroy) {
         int bitmapIdx = this.getNextAvail();
         int q = bitmapIdx >>> 6;
         int r = bitmapIdx & 63;

         assert (this.bitmap[q] >>> r & 1L) == 0L;

         this.bitmap[q] = this.bitmap[q] | 1L << r;
         if (--this.numAvail == 0) {
            this.removeFromPool();
         }

         return this.toHandle(bitmapIdx);
      } else {
         return -1L;
      }
   }

   boolean free(PoolSubpage<T> head, int bitmapIdx) {
      if (this.elemSize == 0) {
         return true;
      } else {
         int q = bitmapIdx >>> 6;
         int r = bitmapIdx & 63;

         assert (this.bitmap[q] >>> r & 1L) != 0L;

         this.bitmap[q] = this.bitmap[q] ^ 1L << r;
         this.setNextAvail(bitmapIdx);
         if (this.numAvail++ == 0) {
            this.addToPool(head);
            if (this.maxNumElems > 1) {
               return true;
            }
         }

         if (this.numAvail != this.maxNumElems) {
            return true;
         } else if (this.prev == this.next) {
            return true;
         } else {
            this.doNotDestroy = false;
            this.removeFromPool();
            return false;
         }
      }
   }

   private void addToPool(PoolSubpage<T> head) {
      assert this.prev == null && this.next == null;

      this.prev = head;
      this.next = head.next;
      this.next.prev = this;
      head.next = this;
   }

   private void removeFromPool() {
      assert this.prev != null && this.next != null;

      this.prev.next = this.next;
      this.next.prev = this.prev;
      this.next = null;
      this.prev = null;
   }

   private void setNextAvail(int bitmapIdx) {
      this.nextAvail = bitmapIdx;
   }

   private int getNextAvail() {
      int nextAvail = this.nextAvail;
      if (nextAvail >= 0) {
         this.nextAvail = -1;
         return nextAvail;
      } else {
         return this.findNextAvail();
      }
   }

   private int findNextAvail() {
      long[] bitmap = this.bitmap;
      int bitmapLength = this.bitmapLength;

      for (int i = 0; i < bitmapLength; i++) {
         long bits = bitmap[i];
         if (~bits != 0L) {
            return this.findNextAvail0(i, bits);
         }
      }

      return -1;
   }

   private int findNextAvail0(int i, long bits) {
      int maxNumElems = this.maxNumElems;
      int baseVal = i << 6;

      for (int j = 0; j < 64; j++) {
         if ((bits & 1L) == 0L) {
            int val = baseVal | j;
            if (val < maxNumElems) {
               return val;
            }
            break;
         }

         bits >>>= 1;
      }

      return -1;
   }

   private long toHandle(int bitmapIdx) {
      int pages = this.runSize >> this.pageShifts;
      return (long)this.runOffset << 49 | (long)pages << 34 | 8589934592L | 4294967296L | (long)bitmapIdx;
   }

   @Override
   public String toString() {
      boolean doNotDestroy;
      int maxNumElems;
      int numAvail;
      int elemSize;
      if (this.chunk == null) {
         doNotDestroy = true;
         maxNumElems = 0;
         numAvail = 0;
         elemSize = -1;
      } else {
         synchronized (this.chunk.arena) {
            if (!this.doNotDestroy) {
               doNotDestroy = false;
               elemSize = -1;
               numAvail = -1;
               maxNumElems = -1;
            } else {
               doNotDestroy = true;
               maxNumElems = this.maxNumElems;
               numAvail = this.numAvail;
               elemSize = this.elemSize;
            }
         }
      }

      return !doNotDestroy
         ? "(" + this.runOffset + ": not in use)"
         : "("
            + this.runOffset
            + ": "
            + (maxNumElems - numAvail)
            + '/'
            + maxNumElems
            + ", offset: "
            + this.runOffset
            + ", length: "
            + this.runSize
            + ", elemSize: "
            + elemSize
            + ')';
   }

   @Override
   public int maxNumElements() {
      if (this.chunk == null) {
         return 0;
      } else {
         synchronized (this.chunk.arena) {
            return this.maxNumElems;
         }
      }
   }

   @Override
   public int numAvailable() {
      if (this.chunk == null) {
         return 0;
      } else {
         synchronized (this.chunk.arena) {
            return this.numAvail;
         }
      }
   }

   @Override
   public int elementSize() {
      if (this.chunk == null) {
         return -1;
      } else {
         synchronized (this.chunk.arena) {
            return this.elemSize;
         }
      }
   }

   @Override
   public int pageSize() {
      return 1 << this.pageShifts;
   }

   void destroy() {
      if (this.chunk != null) {
         this.chunk.destroy();
      }
   }
}
