package org.eclipse.jetty.util;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class ConcurrentPool<P> implements Pool<P>, Dumpable {
   public static final int OPTIMAL_MAX_SIZE = 256;
   private static final Logger LOG = LoggerFactory.getLogger(ConcurrentPool.class);
   private final List<Pool.Entry<P>> entries = new CopyOnWriteArrayList<>();
   private final int maxSize;
   private final ConcurrentPool.StrategyType strategyType;
   private final ThreadLocal<Pool.Entry<P>> cache;
   private final AutoLock lock = new AutoLock();
   private final AtomicInteger nextIndex;
   private final ToIntFunction<P> maxMultiplex;
   private volatile boolean terminated;

   public ConcurrentPool(ConcurrentPool.StrategyType strategyType, int maxSize) {
      this(strategyType, maxSize, false);
   }

   public ConcurrentPool(ConcurrentPool.StrategyType strategyType, int maxSize, boolean cache) {
      this(strategyType, maxSize, cache, pooled -> 1);
   }

   public ConcurrentPool(ConcurrentPool.StrategyType strategyType, int maxSize, boolean cache, ToIntFunction<P> maxMultiplex) {
      if (maxSize > 256 && LOG.isDebugEnabled()) {
         LOG.debug("{} configured with max size {} which is above the recommended value {}", new Object[]{this.getClass().getSimpleName(), maxSize, 256});
      }

      this.maxSize = maxSize;
      this.strategyType = Objects.requireNonNull(strategyType);
      this.cache = cache ? new ThreadLocal<>() : null;
      this.nextIndex = strategyType == ConcurrentPool.StrategyType.ROUND_ROBIN ? new AtomicInteger() : null;
      this.maxMultiplex = Objects.requireNonNull(maxMultiplex);
   }

   @Override
   public int getTerminatedCount() {
      return (int)this.entries.stream().filter(Pool.Entry::isTerminated).count();
   }

   private int getMaxMultiplex(P pooled) {
      return this.maxMultiplex.applyAsInt(pooled);
   }

   @Override
   public Pool.Entry<P> reserve() {
      ConcurrentPool.ConcurrentEntry var4;
      try (AutoLock ignored = this.lock.lock()) {
         if (this.terminated) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("terminated, cannot reserve entry for {}", this);
            }

            return null;
         }

         int entriesSize = this.entries.size();
         if (this.maxSize > 0 && entriesSize >= this.maxSize) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("no space: {} >= {}, cannot reserve entry for {}", new Object[]{entriesSize, this.maxSize, this});
            }

            return null;
         }

         ConcurrentPool.ConcurrentEntry<P> entry = new ConcurrentPool.ConcurrentEntry<>(this);
         this.entries.add(entry);
         if (LOG.isDebugEnabled()) {
            LOG.debug("returning reserved entry {} for {}", entry, this);
         }

         var4 = entry;
      }

      return var4;
   }

   @Override
   public Pool.Entry<P> acquire() {
      if (this.terminated) {
         return null;
      } else {
         int size = this.entries.size();
         if (size == 0) {
            return null;
         } else {
            if (this.cache != null) {
               Pool.Entry<P> entry = this.cache.get();
               if (entry != null && ((ConcurrentPool.ConcurrentEntry)entry).tryAcquire()) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("returning cached entry {} for {}", entry, this);
                  }

                  return entry;
               }
            }

            int index = this.startIndex(size);
            int tries = size;

            while (tries-- > 0) {
               try {
                  ConcurrentPool.ConcurrentEntry<P> entry = (ConcurrentPool.ConcurrentEntry<P>)this.entries.get(index);
                  if (entry != null && entry.tryAcquire()) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("returning entry {} for {}", entry, this);
                     }

                     return entry;
                  }
               } catch (IndexOutOfBoundsException var5) {
                  LOG.trace("IGNORED", var5);
                  size = this.entries.size();
                  if (size == 0) {
                     break;
                  }
               }

               if (++index == size) {
                  index = 0;
               }
            }

            return null;
         }
      }
   }

   private int startIndex(int size) {
      return switch (this.strategyType) {
         case FIRST -> 0;
         case RANDOM -> ThreadLocalRandom.current().nextInt(size);
         case THREAD_ID -> (int)(Thread.currentThread().getId() * 31L % (long)size);
         case ROUND_ROBIN -> this.nextIndex.getAndUpdate(c -> Math.max(0, c + 1)) % size;
      };
   }

   private boolean release(Pool.Entry<P> entry) {
      boolean released = ((ConcurrentPool.ConcurrentEntry)entry).tryRelease();
      if (released && this.cache != null) {
         this.cache.set(entry);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("released {} {} for {}", new Object[]{released, entry, this});
      }

      return released;
   }

   private boolean remove(Pool.Entry<P> entry) {
      boolean removed = ((ConcurrentPool.ConcurrentEntry)entry).tryRemove();
      if (this.cache != null) {
         this.cache.set(null);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("removed {} {} for {}", new Object[]{removed, entry, this});
      }

      if (!removed) {
         return false;
      } else {
         boolean evicted = this.entries.remove(entry);
         if (LOG.isDebugEnabled()) {
            LOG.debug("evicted {} {} for {}", new Object[]{evicted, entry, this});
         }

         return true;
      }
   }

   @Override
   public boolean isTerminated() {
      return this.terminated;
   }

   @Override
   public Collection<Pool.Entry<P>> terminate() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("terminating {}", this);
      }

      List<Pool.Entry<P>> copy;
      try (AutoLock ignored = this.lock.lock()) {
         this.terminated = true;
         copy = List.copyOf(this.entries);
         this.entries.clear();
      }

      copy.forEach(entry -> ((ConcurrentPool.ConcurrentEntry)entry).terminate());
      return copy;
   }

   private boolean terminate(Pool.Entry<P> entry) {
      boolean terminated = ((ConcurrentPool.ConcurrentEntry)entry).tryTerminate();
      if (this.cache != null) {
         this.cache.set(null);
      }

      if (!terminated && LOG.isDebugEnabled()) {
         LOG.debug("entry still in use or already terminated {} for {}", entry, this);
      }

      return terminated;
   }

   @Override
   public int size() {
      return this.entries.size();
   }

   @Override
   public int getMaxSize() {
      return this.maxSize;
   }

   @Override
   public Stream<Pool.Entry<P>> stream() {
      return this.entries.stream();
   }

   @Override
   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(out, indent, this, new DumpableCollection("entries", this.entries));
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x[inUse=%d,size=%d,max=%d,terminated=%b]",
         this.getClass().getSimpleName(),
         this.hashCode(),
         this.getInUseCount(),
         this.size(),
         this.getMaxSize(),
         this.isTerminated()
      );
   }

   public static class ConcurrentEntry<E> implements Pool.Entry<E> {
      private final AtomicBiInteger state = new AtomicBiInteger(0, -1);
      private final ConcurrentPool<E> pool;
      private E pooled;

      public ConcurrentEntry(ConcurrentPool<E> pool) {
         this.pool = pool;
      }

      @Override
      public boolean enable(E pooled, boolean acquire) {
         Objects.requireNonNull(pooled);
         if (!this.isReserved()) {
            if (this.isTerminated()) {
               return false;
            } else {
               throw new IllegalStateException("Entry already enabled " + this + " for " + this.pool);
            }
         } else {
            this.pooled = pooled;
            if (this.tryEnable(acquire)) {
               if (ConcurrentPool.LOG.isDebugEnabled()) {
                  ConcurrentPool.LOG.debug("enabled {} for {}", this, this.pool);
               }

               return true;
            } else {
               this.pooled = null;
               if (this.isTerminated()) {
                  return false;
               } else {
                  throw new IllegalStateException("Entry already enabled " + this + " for " + this.pool);
               }
            }
         }
      }

      @Override
      public E getPooled() {
         return this.pooled;
      }

      @Override
      public boolean release() {
         return this.pool.release(this);
      }

      @Override
      public boolean remove() {
         return this.pool.remove(this);
      }

      private boolean terminate() {
         return this.pool.terminate(this);
      }

      private boolean tryEnable(boolean acquire) {
         return this.state.compareAndSet(0, 0, -1, acquire ? 1 : 0);
      }

      private boolean tryAcquire() {
         long encoded;
         int newMultiplexCount;
         do {
            encoded = this.state.get();
            if (AtomicBiInteger.getHi(encoded) < 0) {
               return false;
            }

            int multiplexCount = AtomicBiInteger.getLo(encoded);
            if (multiplexCount < 0) {
               return false;
            }

            int maxMultiplexed = this.pool.getMaxMultiplex(this.pooled);
            if (maxMultiplexed > 0 && multiplexCount >= maxMultiplexed) {
               return false;
            }

            newMultiplexCount = multiplexCount + 1;
            if (newMultiplexCount < 0) {
               return false;
            }
         } while (!this.state.compareAndSet(encoded, 0, newMultiplexCount));

         return true;
      }

      private boolean tryRelease() {
         long encoded;
         int newMultiplexCount;
         do {
            encoded = this.state.get();
            if (AtomicBiInteger.getHi(encoded) < 0) {
               return false;
            }

            int multiplexCount = AtomicBiInteger.getLo(encoded);
            if (multiplexCount <= 0) {
               return false;
            }

            newMultiplexCount = multiplexCount - 1;
         } while (!this.state.compareAndSet(encoded, 0, newMultiplexCount));

         return true;
      }

      private boolean tryRemove() {
         long encoded;
         int newMultiplexCount;
         boolean result;
         int removed;
         do {
            encoded = this.state.get();
            removed = AtomicBiInteger.getHi(encoded);
            int multiplexCount = AtomicBiInteger.getLo(encoded);
            if (removed == -2) {
               return false;
            }

            if (multiplexCount <= 0) {
               newMultiplexCount = multiplexCount;
            } else {
               newMultiplexCount = multiplexCount - 1;
            }

            result = newMultiplexCount <= 0;
            removed = result ? -2 : -1;
         } while (!this.state.compareAndSet(encoded, removed, newMultiplexCount));

         return result;
      }

      private boolean tryTerminate() {
         long encoded;
         int multiplexCount;
         do {
            encoded = this.state.get();
            if (AtomicBiInteger.getHi(encoded) < 0) {
               return false;
            }

            multiplexCount = AtomicBiInteger.getLo(encoded);
         } while (!this.state.compareAndSet(encoded, -1, multiplexCount));

         return multiplexCount <= 0;
      }

      @Override
      public boolean isTerminated() {
         return this.state.getHi() < 0;
      }

      @Override
      public boolean isReserved() {
         return this.state.getLo() < 0;
      }

      @Override
      public boolean isIdle() {
         return this.state.getLo() == 0;
      }

      @Override
      public boolean isInUse() {
         return this.state.getLo() > 0;
      }

      @Override
      public String toString() {
         long encoded = this.state.get();
         return String.format(
            "%s@%x{terminated=%b,multiplex=%d,pooled=%s}",
            this.getClass().getSimpleName(),
            this.hashCode(),
            AtomicBiInteger.getHi(encoded) < 0,
            AtomicBiInteger.getLo(encoded),
            this.getPooled()
         );
      }
   }

   public static enum StrategyType {
      FIRST,
      RANDOM,
      THREAD_ID,
      ROUND_ROBIN;
   }
}
