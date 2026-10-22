package org.eclipse.jetty.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import org.eclipse.jetty.io.internal.CompoundPool;
import org.eclipse.jetty.io.internal.QueuedPool;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.ConcurrentPool;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.ConcurrentPool.StrategyType;
import org.eclipse.jetty.util.Pool.Entry;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class ArrayByteBufferPool implements ByteBufferPool, Dumpable {
   private static final Logger LOG = LoggerFactory.getLogger(ArrayByteBufferPool.class);
   static final int DEFAULT_FACTOR = 4096;
   static final int DEFAULT_MAX_CAPACITY_BY_FACTOR = 16;
   private final ArrayByteBufferPool.RetainedBucket[] _direct;
   private final ArrayByteBufferPool.RetainedBucket[] _indirect;
   private final int _minCapacity;
   private final int _maxCapacity;
   private final long _maxHeapMemory;
   private final long _maxDirectMemory;
   private final AtomicLong _currentHeapMemory = new AtomicLong();
   private final AtomicLong _currentDirectMemory = new AtomicLong();
   private final IntUnaryOperator _bucketIndexFor;

   public ArrayByteBufferPool() {
      this(0, -1, -1);
   }

   public ArrayByteBufferPool(int minCapacity, int factor, int maxCapacity) {
      this(minCapacity, factor, maxCapacity, Integer.MAX_VALUE);
   }

   public ArrayByteBufferPool(int minCapacity, int factor, int maxCapacity, int maxBucketSize) {
      this(minCapacity, factor, maxCapacity, maxBucketSize, 0L, 0L);
   }

   public ArrayByteBufferPool(int minCapacity, int factor, int maxCapacity, int maxBucketSize, long maxHeapMemory, long maxDirectMemory) {
      this(minCapacity, factor, maxCapacity, maxBucketSize, maxHeapMemory, maxDirectMemory, null, null);
   }

   protected ArrayByteBufferPool(
      int minCapacity,
      int factor,
      int maxCapacity,
      int maxBucketSize,
      long maxHeapMemory,
      long maxDirectMemory,
      IntUnaryOperator bucketIndexFor,
      IntUnaryOperator bucketCapacity
   ) {
      if (minCapacity <= 0) {
         minCapacity = 0;
      }

      factor = factor <= 0 ? 4096 : factor;
      if (maxCapacity <= 0) {
         maxCapacity = 16 * factor;
      }

      if (maxCapacity % factor == 0 && factor < maxCapacity) {
         if (bucketIndexFor == null) {
            bucketIndexFor = c -> (c - 1) / factor;
         }

         if (bucketCapacity == null) {
            bucketCapacity = ix -> (ix + 1) * factor;
         }

         int length = bucketIndexFor.applyAsInt(maxCapacity) + 1;
         ArrayByteBufferPool.RetainedBucket[] directArray = new ArrayByteBufferPool.RetainedBucket[length];
         ArrayByteBufferPool.RetainedBucket[] indirectArray = new ArrayByteBufferPool.RetainedBucket[length];

         for (int i = 0; i < directArray.length; i++) {
            int capacity = Math.min(bucketCapacity.applyAsInt(i), maxCapacity);
            directArray[i] = new ArrayByteBufferPool.RetainedBucket(capacity, maxBucketSize);
            indirectArray[i] = new ArrayByteBufferPool.RetainedBucket(capacity, maxBucketSize);
         }

         this._minCapacity = minCapacity;
         this._maxCapacity = maxCapacity;
         this._direct = directArray;
         this._indirect = indirectArray;
         this._maxHeapMemory = this.maxMemory(maxHeapMemory);
         this._maxDirectMemory = this.maxMemory(maxDirectMemory);
         this._bucketIndexFor = bucketIndexFor;
      } else {
         throw new IllegalArgumentException(String.format("The capacity factor(%d) must be a divisor of maxCapacity(%d)", factor, maxCapacity));
      }
   }

   private long maxMemory(long maxMemory) {
      if (maxMemory < 0L) {
         return -1L;
      } else {
         return maxMemory == 0L ? Runtime.getRuntime().maxMemory() / 8L : maxMemory;
      }
   }

   @ManagedAttribute("The minimum pooled buffer capacity")
   public int getMinCapacity() {
      return this._minCapacity;
   }

   @ManagedAttribute("The maximum pooled buffer capacity")
   public int getMaxCapacity() {
      return this._maxCapacity;
   }

   @Override
   public RetainableByteBuffer acquire(int size, boolean direct) {
      ArrayByteBufferPool.RetainedBucket bucket = this.bucketFor(size, direct);
      if (bucket == null) {
         return this.newRetainableByteBuffer(size, direct, this::removed);
      } else {
         Entry<RetainableByteBuffer> entry = bucket.getPool().acquire();
         RetainableByteBuffer buffer;
         if (entry == null) {
            Entry<RetainableByteBuffer> reservedEntry = bucket.getPool().reserve();
            if (reservedEntry != null) {
               buffer = this.newRetainableByteBuffer(bucket._capacity, direct, retainedBuffer -> {
                  BufferUtil.reset(retainedBuffer.getByteBuffer());
                  if (!reservedEntry.release()) {
                     reservedEntry.remove();
                  }
               });
               reservedEntry.enable(buffer, true);
               if (direct) {
                  this._currentDirectMemory.addAndGet((long)buffer.capacity());
               } else {
                  this._currentHeapMemory.addAndGet((long)buffer.capacity());
               }

               this.releaseExcessMemory(direct);
            } else {
               buffer = this.newRetainableByteBuffer(size, direct, this::removed);
            }
         } else {
            buffer = (RetainableByteBuffer)entry.getPooled();
            ((ArrayByteBufferPool.Buffer)buffer).acquire();
         }

         return buffer;
      }
   }

   protected ByteBuffer allocate(int capacity) {
      return ByteBuffer.allocate(capacity);
   }

   protected ByteBuffer allocateDirect(int capacity) {
      return ByteBuffer.allocateDirect(capacity);
   }

   protected void removed(RetainableByteBuffer retainedBuffer) {
   }

   private RetainableByteBuffer newRetainableByteBuffer(int capacity, boolean direct, Consumer<RetainableByteBuffer> releaser) {
      ByteBuffer buffer = BufferUtil.allocate(capacity, direct);
      ArrayByteBufferPool.Buffer retainableByteBuffer = new ArrayByteBufferPool.Buffer(buffer, releaser);
      retainableByteBuffer.acquire();
      return retainableByteBuffer;
   }

   public Pool<RetainableByteBuffer> poolFor(int capacity, boolean direct) {
      ArrayByteBufferPool.RetainedBucket bucket = this.bucketFor(capacity, direct);
      return bucket == null ? null : bucket.getPool();
   }

   private ArrayByteBufferPool.RetainedBucket bucketFor(int capacity, boolean direct) {
      if (capacity < this._minCapacity) {
         return null;
      } else {
         int idx = this._bucketIndexFor.applyAsInt(capacity);
         ArrayByteBufferPool.RetainedBucket[] buckets = direct ? this._direct : this._indirect;
         return idx >= buckets.length ? null : buckets[idx];
      }
   }

   @ManagedAttribute("The number of pooled direct ByteBuffers")
   public long getDirectByteBufferCount() {
      return this.getByteBufferCount(true);
   }

   @ManagedAttribute("The number of pooled heap ByteBuffers")
   public long getHeapByteBufferCount() {
      return this.getByteBufferCount(false);
   }

   private long getByteBufferCount(boolean direct) {
      ArrayByteBufferPool.RetainedBucket[] buckets = direct ? this._direct : this._indirect;
      return Arrays.stream(buckets).mapToLong(bucket -> (long)bucket.getPool().size()).sum();
   }

   @ManagedAttribute("The number of pooled direct ByteBuffers that are available")
   public long getAvailableDirectByteBufferCount() {
      return this.getAvailableByteBufferCount(true);
   }

   @ManagedAttribute("The number of pooled heap ByteBuffers that are available")
   public long getAvailableHeapByteBufferCount() {
      return this.getAvailableByteBufferCount(false);
   }

   private long getAvailableByteBufferCount(boolean direct) {
      ArrayByteBufferPool.RetainedBucket[] buckets = direct ? this._direct : this._indirect;
      return Arrays.stream(buckets).mapToLong(bucket -> (long)bucket.getPool().getIdleCount()).sum();
   }

   @ManagedAttribute("The bytes retained by direct ByteBuffers")
   public long getDirectMemory() {
      return this.getMemory(true);
   }

   @ManagedAttribute("The bytes retained by heap ByteBuffers")
   public long getHeapMemory() {
      return this.getMemory(false);
   }

   private long getMemory(boolean direct) {
      return direct ? this._currentDirectMemory.get() : this._currentHeapMemory.get();
   }

   @ManagedAttribute("The available bytes retained by direct ByteBuffers")
   public long getAvailableDirectMemory() {
      return this.getAvailableMemory(true);
   }

   @ManagedAttribute("The available bytes retained by heap ByteBuffers")
   public long getAvailableHeapMemory() {
      return this.getAvailableMemory(false);
   }

   private long getAvailableMemory(boolean direct) {
      ArrayByteBufferPool.RetainedBucket[] buckets = direct ? this._direct : this._indirect;
      long total = 0L;

      for (ArrayByteBufferPool.RetainedBucket bucket : buckets) {
         long capacity = (long)bucket._capacity;
         total += (long)bucket.getPool().getIdleCount() * capacity;
      }

      return total;
   }

   @ManagedOperation(
      value = "Clears this ByteBufferPool",
      impact = "ACTION"
   )
   @Override
   public void clear() {
      this.clearArray(this._direct, this._currentDirectMemory);
      this.clearArray(this._indirect, this._currentHeapMemory);
   }

   private void clearArray(ArrayByteBufferPool.RetainedBucket[] poolArray, AtomicLong memoryCounter) {
      for (ArrayByteBufferPool.RetainedBucket bucket : poolArray) {
         bucket.getPool().stream().forEach(entry -> {
            if (entry.remove()) {
               RetainableByteBuffer pooled = (RetainableByteBuffer)entry.getPooled();
               if (pooled != null) {
                  memoryCounter.addAndGet((long)(-pooled.capacity()));
                  this.removed(pooled);
               }
            }
         });
      }
   }

   private void releaseExcessMemory(boolean direct) {
      long maxMemory = direct ? this._maxDirectMemory : this._maxHeapMemory;
      if (maxMemory > 0L) {
         long excess = this.getMemory(direct) - maxMemory;
         if (excess > 0L) {
            this.evict(direct, excess);
         }
      }
   }

   private void evict(boolean direct, long excess) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("evicting {} bytes from {} pools", excess, direct ? "direct" : "heap");
      }

      long now = NanoTime.now();
      long totalClearedCapacity = 0L;
      ArrayByteBufferPool.RetainedBucket[] buckets = direct ? this._direct : this._indirect;

      while (totalClearedCapacity < excess) {
         for (ArrayByteBufferPool.RetainedBucket bucket : buckets) {
            Entry<RetainableByteBuffer> oldestEntry = this.findOldestEntry(now, bucket.getPool());
            if (oldestEntry != null && oldestEntry.remove()) {
               RetainableByteBuffer buffer = (RetainableByteBuffer)oldestEntry.getPooled();
               int clearedCapacity = buffer.capacity();
               if (direct) {
                  this._currentDirectMemory.addAndGet((long)(-clearedCapacity));
               } else {
                  this._currentHeapMemory.addAndGet((long)(-clearedCapacity));
               }

               totalClearedCapacity += (long)clearedCapacity;
               this.removed(buffer);
            }
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("eviction done, cleared {} bytes from {} pools", totalClearedCapacity, direct ? "direct" : "heap");
      }
   }

   @Override
   public String toString() {
      return String.format(
         "%s{min=%d,max=%d,buckets=%d,heap=%d/%d,direct=%d/%d}",
         super.toString(),
         this._minCapacity,
         this._maxCapacity,
         this._direct.length,
         this._currentHeapMemory.get(),
         this._maxHeapMemory,
         this._currentDirectMemory.get(),
         this._maxDirectMemory
      );
   }

   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(
         out, indent, this, new Object[]{DumpableCollection.fromArray("direct", this._direct), DumpableCollection.fromArray("indirect", this._indirect)}
      );
   }

   private Entry<RetainableByteBuffer> findOldestEntry(long now, Pool<RetainableByteBuffer> bucket) {
      Entry<RetainableByteBuffer> oldestEntry = null;
      RetainableByteBuffer oldestBuffer = null;
      long oldestAge = 0L;

      for (Entry<RetainableByteBuffer> entry : bucket.stream().toList()) {
         ArrayByteBufferPool.Buffer buffer = (ArrayByteBufferPool.Buffer)entry.getPooled();
         if (buffer != null) {
            long age = NanoTime.elapsed(buffer.getLastNanoTime(), now);
            if (oldestBuffer == null || age > oldestAge) {
               oldestEntry = entry;
               oldestBuffer = buffer;
               oldestAge = age;
            }
         }
      }

      return oldestEntry;
   }

   private static class Buffer extends AbstractRetainableByteBuffer {
      private final Consumer<RetainableByteBuffer> releaser;
      private final AtomicLong lastNanoTime = new AtomicLong(NanoTime.now());

      private Buffer(ByteBuffer buffer, Consumer<RetainableByteBuffer> releaser) {
         super(buffer);
         this.releaser = releaser;
      }

      @Override
      public boolean release() {
         boolean released = super.release();
         if (released) {
            this.lastNanoTime.setOpaque(NanoTime.now());
            this.releaser.accept(this);
         }

         return released;
      }

      public long getLastNanoTime() {
         return this.lastNanoTime.getOpaque();
      }
   }

   public static class Quadratic extends ArrayByteBufferPool {
      public Quadratic() {
         this(0, -1, Integer.MAX_VALUE);
      }

      public Quadratic(int minCapacity, int maxCapacity, int maxBucketSize) {
         this(minCapacity, maxCapacity, maxBucketSize, -1L, -1L);
      }

      public Quadratic(int minCapacity, int maxCapacity, int maxBucketSize, long maxHeapMemory, long maxDirectMemory) {
         super(minCapacity, -1, maxCapacity, maxBucketSize, maxHeapMemory, maxDirectMemory, c -> 32 - Integer.numberOfLeadingZeros(c - 1), i -> 1 << i);
      }
   }

   private static class RetainedBucket {
      private final Pool<RetainableByteBuffer> _pool;
      private final int _capacity;

      private RetainedBucket(int capacity, int poolSize) {
         if (poolSize <= 256) {
            this._pool = new ConcurrentPool(StrategyType.THREAD_ID, poolSize, false);
         } else {
            this._pool = new CompoundPool<>(new ConcurrentPool(StrategyType.THREAD_ID, 256, false), new QueuedPool<>(poolSize - 256));
         }

         this._capacity = capacity;
      }

      private Pool<RetainableByteBuffer> getPool() {
         return this._pool;
      }

      @Override
      public String toString() {
         int entries = 0;
         int inUse = 0;

         for (Entry<RetainableByteBuffer> entry : this.getPool().stream().toList()) {
            entries++;
            if (entry.isInUse()) {
               inUse++;
            }
         }

         return String.format("%s{capacity=%d,inuse=%d(%d%%)}", super.toString(), this._capacity, inUse, entries > 0 ? inUse * 100 / entries : 0);
      }
   }

   public static class Tracking extends ArrayByteBufferPool {
      private static final Logger LOG = LoggerFactory.getLogger(ArrayByteBufferPool.Tracking.class);
      private final Set<ArrayByteBufferPool.Tracking.Buffer> buffers = ConcurrentHashMap.newKeySet();

      public Tracking() {
         this(0, -1, Integer.MAX_VALUE);
      }

      public Tracking(int minCapacity, int maxCapacity, int maxBucketSize) {
         this(minCapacity, maxCapacity, maxBucketSize, -1L, -1L);
      }

      public Tracking(int minCapacity, int maxCapacity, int maxBucketSize, long maxHeapMemory, long maxDirectMemory) {
         super(minCapacity, -1, maxCapacity, maxBucketSize, maxHeapMemory, maxDirectMemory);
      }

      @Override
      public RetainableByteBuffer acquire(int size, boolean direct) {
         RetainableByteBuffer buffer = super.acquire(size, direct);
         ArrayByteBufferPool.Tracking.Buffer wrapper = new ArrayByteBufferPool.Tracking.Buffer(buffer, size);
         if (LOG.isDebugEnabled()) {
            LOG.debug("acquired {}", wrapper);
         }

         this.buffers.add(wrapper);
         return wrapper;
      }

      public Set<ArrayByteBufferPool.Tracking.Buffer> getLeaks() {
         return this.buffers;
      }

      public String dumpLeaks() {
         return this.getLeaks().stream().map(ArrayByteBufferPool.Tracking.Buffer::dump).collect(Collectors.joining(System.lineSeparator()));
      }

      public class Buffer extends RetainableByteBuffer.Wrapper {
         private final int size;
         private final Instant acquireInstant;
         private final Throwable acquireStack;
         private final List<Throwable> retainStacks = new CopyOnWriteArrayList<>();
         private final List<Throwable> releaseStacks = new CopyOnWriteArrayList<>();
         private final List<Throwable> overReleaseStacks = new CopyOnWriteArrayList<>();

         private Buffer(RetainableByteBuffer wrapped, int size) {
            super(wrapped);
            this.size = size;
            this.acquireInstant = Instant.now();
            this.acquireStack = new Throwable();
         }

         public int getSize() {
            return this.size;
         }

         public Instant getAcquireInstant() {
            return this.acquireInstant;
         }

         public Throwable getAcquireStack() {
            return this.acquireStack;
         }

         @Override
         public void retain() {
            super.retain();
            this.retainStacks.add(new Throwable());
         }

         @Override
         public boolean release() {
            try {
               boolean released = super.release();
               if (released) {
                  Tracking.this.buffers.remove(this);
                  if (ArrayByteBufferPool.Tracking.LOG.isDebugEnabled()) {
                     ArrayByteBufferPool.Tracking.LOG.debug("released {}", this);
                  }
               }

               this.releaseStacks.add(new Throwable());
               return released;
            } catch (IllegalStateException var2) {
               Tracking.this.buffers.add(this);
               this.overReleaseStacks.add(new Throwable());
               throw var2;
            }
         }

         public String dump() {
            StringWriter w = new StringWriter();
            PrintWriter pw = new PrintWriter(w);
            this.getAcquireStack().printStackTrace(pw);
            pw.println("\n" + this.retainStacks.size() + " retain(s)");

            for (Throwable retainStack : this.retainStacks) {
               retainStack.printStackTrace(pw);
            }

            pw.println("\n" + this.releaseStacks.size() + " release(s)");

            for (Throwable releaseStack : this.releaseStacks) {
               releaseStack.printStackTrace(pw);
            }

            pw.println("\n" + this.overReleaseStacks.size() + " over-release(s)");

            for (Throwable overReleaseStack : this.overReleaseStacks) {
               overReleaseStack.printStackTrace(pw);
            }

            return "%s@%x of %d bytes on %s wrapping %s acquired at %s"
               .formatted(this.getClass().getSimpleName(), this.hashCode(), this.getSize(), this.getAcquireInstant(), this.getWrapped(), w);
         }
      }
   }
}
