package org.eclipse.jetty.util.thread;

import java.io.Closeable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class ThreadPoolBudget {
   private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolBudget.class);
   private static final ThreadPoolBudget.Lease NOOP_LEASE = new ThreadPoolBudget.Lease() {
      @Override
      public void close() {
      }

      @Override
      public int getThreads() {
         return 0;
      }
   };
   private final Set<ThreadPoolBudget.Leased> leases = new CopyOnWriteArraySet<>();
   private final AtomicBoolean warned = new AtomicBoolean();
   private final ThreadPool.SizedThreadPool pool;
   private final int warnAt;

   public ThreadPoolBudget(ThreadPool.SizedThreadPool pool) {
      this.pool = pool;
      this.warnAt = -1;
   }

   public ThreadPool.SizedThreadPool getSizedThreadPool() {
      return this.pool;
   }

   @ManagedAttribute("the number of threads leased to components")
   public int getLeasedThreads() {
      return this.leases.stream().mapToInt(ThreadPoolBudget.Lease::getThreads).sum();
   }

   public void reset() {
      this.leases.clear();
      this.warned.set(false);
   }

   public ThreadPoolBudget.Lease leaseTo(Object leasee, int threads) {
      ThreadPoolBudget.Leased lease = new ThreadPoolBudget.Leased(leasee, threads);
      this.leases.add(lease);

      try {
         this.check(this.pool.getMaxThreads());
         return lease;
      } catch (IllegalStateException var5) {
         lease.close();
         throw var5;
      }
   }

   public boolean check(int maxThreads) throws IllegalStateException {
      int required = this.getLeasedThreads();
      int left = maxThreads - required;
      if (left <= 0) {
         this.printInfoOnLeases();
         throw new IllegalStateException(String.format("Insufficient configured threads: required=%d < max=%d for %s", required, maxThreads, this.pool));
      } else if (left < this.warnAt) {
         if (this.warned.compareAndSet(false, true)) {
            this.printInfoOnLeases();
            LOG.info("Low configured threads: (max={} - required={})={} < warnAt={} for {}", new Object[]{maxThreads, required, left, this.warnAt, this.pool});
         }

         return false;
      } else {
         return true;
      }
   }

   private void printInfoOnLeases() {
      this.leases.forEach(lease -> LOG.info("{} requires {} threads from {}", new Object[]{lease.leasee, lease.getThreads(), this.pool}));
   }

   public static ThreadPoolBudget.Lease leaseFrom(Executor executor, Object leasee, int threads) {
      if (executor instanceof ThreadPool.SizedThreadPool) {
         ThreadPoolBudget budget = ((ThreadPool.SizedThreadPool)executor).getThreadPoolBudget();
         if (budget != null) {
            return budget.leaseTo(leasee, threads);
         }
      }

      return NOOP_LEASE;
   }

   public interface Lease extends Closeable {
      int getThreads();
   }

   public class Leased implements ThreadPoolBudget.Lease {
      private final Object leasee;
      private final int threads;

      private Leased(Object leasee, int threads) {
         this.leasee = leasee;
         this.threads = threads;
      }

      @Override
      public int getThreads() {
         return this.threads;
      }

      @Override
      public void close() {
         ThreadPoolBudget.this.leases.remove(this);
         ThreadPoolBudget.this.warned.set(false);
      }
   }
}
