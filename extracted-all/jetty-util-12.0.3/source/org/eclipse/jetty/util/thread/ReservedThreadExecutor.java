package org.eclipse.jetty.util.thread;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.AtomicBiInteger;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.VirtualThreads;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("A pool for reserved threads")
public class ReservedThreadExecutor extends AbstractLifeCycle implements TryExecutor, Dumpable {
   private static final Logger LOG = LoggerFactory.getLogger(ReservedThreadExecutor.class);
   private static final long DEFAULT_IDLE_TIMEOUT = TimeUnit.MINUTES.toNanos(1L);
   private static final Runnable STOP = new Runnable() {
      @Override
      public void run() {
      }

      @Override
      public String toString() {
         return "STOP";
      }
   };
   private final Executor _executor;
   private final int _capacity;
   private final Set<ReservedThreadExecutor.ReservedThread> _threads = ConcurrentHashMap.newKeySet();
   private final SynchronousQueue<Runnable> _queue = new SynchronousQueue<>(false);
   private final AtomicBiInteger _count = new AtomicBiInteger();
   private final AtomicLong _lastEmptyNanoTime = new AtomicLong(NanoTime.now());
   private ThreadPoolBudget.Lease _lease;
   private long _idleTimeNanos = DEFAULT_IDLE_TIMEOUT;

   public ReservedThreadExecutor(Executor executor, int capacity) {
      this._executor = executor;
      this._capacity = reservedThreads(executor, capacity);
      if (LOG.isDebugEnabled()) {
         LOG.debug("{}", this);
      }
   }

   private static int reservedThreads(Executor executor, int capacity) {
      if (capacity >= 0) {
         return capacity;
      } else if (VirtualThreads.isUseVirtualThreads(executor)) {
         return 0;
      } else {
         int cpus = ProcessorUtils.availableProcessors();
         if (executor instanceof ThreadPool.SizedThreadPool) {
            int threads = ((ThreadPool.SizedThreadPool)executor).getMaxThreads();
            return Math.max(1, Math.min(cpus, threads / 10));
         } else {
            return cpus;
         }
      }
   }

   public Executor getExecutor() {
      return this._executor;
   }

   @ManagedAttribute(
      value = "max number of reserved threads",
      readonly = true
   )
   public int getCapacity() {
      return this._capacity;
   }

   @ManagedAttribute(
      value = "available reserved threads",
      readonly = true
   )
   public int getAvailable() {
      return this._count.getLo();
   }

   @ManagedAttribute(
      value = "pending reserved threads",
      readonly = true
   )
   public int getPending() {
      return this._count.getHi();
   }

   @ManagedAttribute(
      value = "idle timeout in ms",
      readonly = true
   )
   public long getIdleTimeoutMs() {
      return TimeUnit.NANOSECONDS.toMillis(this._idleTimeNanos);
   }

   public void setIdleTimeout(long idleTime, TimeUnit idleTimeUnit) {
      if (this.isRunning()) {
         throw new IllegalStateException();
      } else {
         this._idleTimeNanos = idleTime > 0L && idleTimeUnit != null ? idleTimeUnit.toNanos(idleTime) : DEFAULT_IDLE_TIMEOUT;
      }
   }

   @Override
   public void doStart() throws Exception {
      this._lease = ThreadPoolBudget.leaseFrom(this.getExecutor(), this, this._capacity);
      this._count.set(0, 0);
      super.doStart();
   }

   @Override
   public void doStop() throws Exception {
      if (this._lease != null) {
         this._lease.close();
      }

      super.doStop();
      int size = this._count.getAndSetLo(-1);

      for (int i = 0; i < size; i++) {
         Thread.yield();
         this._queue.offer(STOP);
      }

      this._threads.stream().filter(ReservedThreadExecutor.ReservedThread::isReserved).map(t -> t._thread).filter(Objects::nonNull).forEach(Thread::interrupt);
      this._threads.clear();
      this._count.getAndSetHi(0);
   }

   @Override
   public void execute(Runnable task) throws RejectedExecutionException {
      this._executor.execute(task);
   }

   @Override
   public boolean tryExecute(Runnable task) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} tryExecute {}", this, task);
      }

      if (task == null) {
         return false;
      } else {
         boolean offered = this._queue.offer(task);
         int size = this._count.getLo();

         while (offered && size > 0 && !this._count.compareAndSetLo(size--, size)) {
            size = this._count.getLo();
         }

         if (size == 0 && task != STOP) {
            this.startReservedThread();
         }

         return offered;
      }
   }

   private void startReservedThread() {
      while (true) {
         long count = this._count.get();
         int pending = AtomicBiInteger.getHi(count);
         int size = AtomicBiInteger.getLo(count);
         if (size >= 0 && pending + size < this._capacity) {
            if (size == 0) {
               this._lastEmptyNanoTime.set(NanoTime.now());
            }

            if (!this._count.compareAndSet(count, pending + 1, size)) {
               continue;
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("{} startReservedThread p={}", this, pending + 1);
            }

            try {
               ReservedThreadExecutor.ReservedThread thread = new ReservedThreadExecutor.ReservedThread();
               this._threads.add(thread);
               this._executor.execute(thread);
            } catch (Throwable var6) {
               this._count.add(-1, 0);
               if (LOG.isDebugEnabled()) {
                  LOG.debug("ignored", var6);
               }
            }

            return;
         }

         return;
      }
   }

   @Override
   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(
         out,
         indent,
         this,
         new DumpableCollection("threads", this._threads.stream().filter(ReservedThreadExecutor.ReservedThread::isReserved).collect(Collectors.toList()))
      );
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x{reserved=%d/%d,pending=%d}", this.getClass().getSimpleName(), this.hashCode(), this._count.getLo(), this._capacity, this._count.getHi()
      );
   }

   private class ReservedThread implements Runnable {
      private volatile ReservedThreadExecutor.State _state = ReservedThreadExecutor.State.PENDING;
      private volatile Thread _thread;

      private boolean isReserved() {
         return this._state == ReservedThreadExecutor.State.RESERVED;
      }

      private Runnable reservedWait() {
         if (ReservedThreadExecutor.LOG.isDebugEnabled()) {
            ReservedThreadExecutor.LOG.debug("{} waiting {}", this, ReservedThreadExecutor.this);
         }

         while (ReservedThreadExecutor.this._count.getLo() >= 0) {
            try {
               Runnable task = ReservedThreadExecutor.this._queue.poll(ReservedThreadExecutor.this._idleTimeNanos, TimeUnit.NANOSECONDS);
               if (ReservedThreadExecutor.LOG.isDebugEnabled()) {
                  ReservedThreadExecutor.LOG.debug("{} task={} {}", new Object[]{this, task, ReservedThreadExecutor.this});
               }

               if (task != null) {
                  return task;
               }

               int size = ReservedThreadExecutor.this._count.getLo();

               while (size > 0 && !ReservedThreadExecutor.this._count.compareAndSetLo(size--, size)) {
                  size = ReservedThreadExecutor.this._count.getLo();
               }

               this._state = size >= 0 ? ReservedThreadExecutor.State.IDLE : ReservedThreadExecutor.State.STOPPED;
               return ReservedThreadExecutor.STOP;
            } catch (InterruptedException var3) {
               if (ReservedThreadExecutor.LOG.isDebugEnabled()) {
                  ReservedThreadExecutor.LOG.debug("ignored", var3);
               }
            }
         }

         this._state = ReservedThreadExecutor.State.STOPPED;
         return ReservedThreadExecutor.STOP;
      }

      @Override
      public void run() {
         this._thread = Thread.currentThread();

         try {
            while (true) {
               long count = ReservedThreadExecutor.this._count.get();
               int pending = AtomicBiInteger.getHi(count) - (this._state == ReservedThreadExecutor.State.PENDING ? 1 : 0);
               int size = AtomicBiInteger.getLo(count);
               ReservedThreadExecutor.State next;
               if (size >= 0 && size < ReservedThreadExecutor.this._capacity) {
                  long now = NanoTime.now();
                  long lastEmpty = ReservedThreadExecutor.this._lastEmptyNanoTime.get();
                  if (size > 0
                     && ReservedThreadExecutor.this._idleTimeNanos < NanoTime.elapsed(lastEmpty, now)
                     && ReservedThreadExecutor.this._lastEmptyNanoTime.compareAndSet(lastEmpty, now)) {
                     next = ReservedThreadExecutor.State.IDLE;
                  } else {
                     next = ReservedThreadExecutor.State.RESERVED;
                     size++;
                  }
               } else {
                  next = ReservedThreadExecutor.State.STOPPED;
               }

               if (ReservedThreadExecutor.this._count.compareAndSet(count, pending, size)) {
                  if (ReservedThreadExecutor.LOG.isDebugEnabled()) {
                     ReservedThreadExecutor.LOG
                        .debug(
                           "{} was={} next={} size={}+{} capacity={}",
                           new Object[]{this, this._state, next, pending, size, ReservedThreadExecutor.this._capacity}
                        );
                  }

                  this._state = next;
                  if (next != ReservedThreadExecutor.State.RESERVED) {
                     break;
                  }

                  Runnable task = this.reservedWait();
                  if (task == ReservedThreadExecutor.STOP) {
                     break;
                  }

                  try {
                     this._state = ReservedThreadExecutor.State.RUNNING;
                     task.run();
                  } catch (Throwable var18) {
                     ReservedThreadExecutor.LOG.warn("Unable to run task", var18);
                  } finally {
                     Thread.interrupted();
                  }
               }
            }
         } finally {
            if (ReservedThreadExecutor.LOG.isDebugEnabled()) {
               ReservedThreadExecutor.LOG.debug("{} exited {}", this, ReservedThreadExecutor.this);
            }

            ReservedThreadExecutor.this._threads.remove(this);
            this._thread = null;
         }
      }

      @Override
      public String toString() {
         return String.format("%s@%x{%s,thread=%s}", this.getClass().getSimpleName(), this.hashCode(), this._state, this._thread);
      }
   }

   private static enum State {
      PENDING,
      RESERVED,
      RUNNING,
      IDLE,
      STOPPED;
   }
}
