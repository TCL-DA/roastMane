package org.eclipse.jetty.io.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.Pool.Entry;

public class QueuedPool<P> implements Pool<P> {
   private final int maxSize;
   private final Queue<Entry<P>> queue = new ConcurrentLinkedQueue<>();
   private final AtomicInteger queueSize = new AtomicInteger();
   private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
   private boolean terminated;

   public QueuedPool(int maxSize) {
      this.maxSize = maxSize;
   }

   public Entry<P> reserve() {
      this.rwLock.readLock().lock();

      Object var1;
      try {
         if (!this.terminated && this.queueSize.get() != this.maxSize) {
            return new QueuedPool.QueuedEntry<>(this);
         }

         var1 = null;
      } finally {
         this.rwLock.readLock().unlock();
      }

      return (Entry<P>)var1;
   }

   private boolean requeue(Entry<P> entry) {
      this.rwLock.readLock().lock();

      try {
         while (true) {
            int size = this.queueSize.get();
            if (!this.terminated && size != this.maxSize) {
               if (!this.queueSize.compareAndSet(size, size + 1)) {
                  continue;
               }

               this.queue.add(entry);
               return true;
            }

            return false;
         }
      } finally {
         this.rwLock.readLock().unlock();
      }
   }

   public Entry<P> acquire() {
      this.rwLock.readLock().lock();

      QueuedPool.QueuedEntry<P> entry;
      try {
         if (!this.terminated) {
            entry = (QueuedPool.QueuedEntry<P>)this.queue.poll();
            if (entry != null) {
               this.queueSize.decrementAndGet();
               entry.acquire();
            }

            return entry;
         }

         entry = null;
      } finally {
         this.rwLock.readLock().unlock();
      }

      return entry;
   }

   public boolean isTerminated() {
      this.rwLock.readLock().lock();

      boolean var1;
      try {
         var1 = this.terminated;
      } finally {
         this.rwLock.readLock().unlock();
      }

      return var1;
   }

   public Collection<Entry<P>> terminate() {
      this.rwLock.writeLock().lock();

      Object var2;
      try {
         this.terminated = true;
         Collection<Entry<P>> copy = new ArrayList<>(this.queue);
         this.queue.clear();
         this.queueSize.set(0);
         var2 = copy;
      } finally {
         this.rwLock.writeLock().unlock();
      }

      return (Collection<Entry<P>>)var2;
   }

   public int size() {
      return this.queueSize.get();
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public Stream<Entry<P>> stream() {
      return this.queue.stream();
   }

   private static class QueuedEntry<P> implements Entry<P> {
      private final QueuedPool<P> pool;
      private final AtomicMarkableReference<P> pooled = new AtomicMarkableReference<>(null, false);

      private QueuedEntry(QueuedPool<P> pool) {
         this.pool = pool;
      }

      public boolean enable(P pooled, boolean acquire) {
         boolean[] inUse = new boolean[1];
         P p = this.pooled.get(inUse);
         if (p != null) {
            if (this.pool.isTerminated()) {
               return false;
            } else {
               throw new IllegalStateException("Entry already enabled " + this + " for " + this.pool);
            }
         } else if (inUse[0]) {
            return false;
         } else {
            this.pooled.set(pooled, acquire);
            if (acquire) {
               if (this.pool.isTerminated()) {
                  this.pooled.set(null, false);
                  return false;
               } else {
                  return true;
               }
            } else {
               return this.pool.requeue(this);
            }
         }
      }

      public P getPooled() {
         return this.pooled.getReference();
      }

      void acquire() {
         boolean[] inUse = new boolean[1];
         P p = this.pooled.get(inUse);
         if (p != null && !inUse[0]) {
            this.pooled.set(p, true);
         }
      }

      public boolean release() {
         boolean[] inUse = new boolean[1];
         P p = this.pooled.get(inUse);
         if (p != null && inUse[0]) {
            this.pooled.set(p, false);
            return this.pool.requeue(this);
         } else {
            return false;
         }
      }

      public boolean remove() {
         boolean[] inUse = new boolean[1];
         P p = this.pooled.get(inUse);
         if (p == null && inUse[0]) {
            return false;
         } else {
            this.pooled.set(null, true);
            return true;
         }
      }

      public boolean isReserved() {
         return this.pooled.getReference() == null;
      }

      public boolean isIdle() {
         return !this.pooled.isMarked();
      }

      public boolean isInUse() {
         return this.pooled.isMarked();
      }

      public boolean isTerminated() {
         boolean[] inUse = new boolean[1];
         P p = this.pooled.get(inUse);
         return p == null && inUse[0];
      }
   }
}
