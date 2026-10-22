package org.eclipse.jetty.io;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CyclicTimeouts<T extends CyclicTimeouts.Expirable> implements Destroyable {
   private static final Logger LOG = LoggerFactory.getLogger(CyclicTimeouts.class);
   private final AtomicLong earliestNanoTime = new AtomicLong(Long.MAX_VALUE);
   private final CyclicTimeout cyclicTimeout;

   public CyclicTimeouts(Scheduler scheduler) {
      this.cyclicTimeout = new CyclicTimeouts.Timeouts(scheduler);
   }

   protected abstract Iterator<T> iterator();

   protected abstract boolean onExpired(T var1);

   private void onTimeoutExpired() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Timeouts check for {}", this);
      }

      long now = NanoTime.now();
      long earliest = Long.MAX_VALUE;
      this.earliestNanoTime.set(now + Long.MAX_VALUE);
      Iterator<T> iterator = this.iterator();
      if (iterator != null) {
         while (iterator.hasNext()) {
            T expirable = iterator.next();
            long expiresAt = expirable.getExpireNanoTime();
            if (expiresAt == Long.MAX_VALUE) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Entity {} does not expire for {}", expirable, this);
               }
            } else {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Entity {} expires in {} ms for {}", new Object[]{expirable, NanoTime.millisElapsed(now, expiresAt), this});
               }

               if (NanoTime.isBeforeOrSame(expiresAt, now)) {
                  boolean remove = this.onExpired(expirable);
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Entity {} expired, remove={} for {}", new Object[]{expirable, remove, this});
                  }

                  if (remove) {
                     iterator.remove();
                     continue;
                  }

                  long newExpiresAt = expirable.getExpireNanoTime();
                  if (newExpiresAt == expiresAt) {
                     continue;
                  }

                  expiresAt = newExpiresAt;
               }

               earliest = Math.min(earliest, NanoTime.elapsed(now, expiresAt));
            }
         }

         if (earliest != Long.MAX_VALUE) {
            this.schedule(now + earliest);
         }
      }
   }

   public void schedule(T expirable) {
      long expiresAt = expirable.getExpireNanoTime();
      if (expiresAt != Long.MAX_VALUE) {
         this.schedule(expiresAt);
      }
   }

   private void schedule(long expiresAt) {
      long prevEarliest = this.earliestNanoTime.getAndUpdate(t -> NanoTime.isBefore(t, expiresAt) ? t : expiresAt);

      for (long expires = expiresAt; NanoTime.isBefore(expires, prevEarliest); expires = this.earliestNanoTime.get()) {
         long delay = Math.max(0L, NanoTime.until(expires));
         if (LOG.isDebugEnabled()) {
            LOG.debug("Scheduling timeout in {} ms for {}", TimeUnit.NANOSECONDS.toMillis(delay), this);
         }

         this.schedule(this.cyclicTimeout, delay, TimeUnit.NANOSECONDS);
         prevEarliest = expires;
      }
   }

   public void destroy() {
      this.cyclicTimeout.destroy();
   }

   boolean schedule(CyclicTimeout cyclicTimeout, long delay, TimeUnit unit) {
      return cyclicTimeout.schedule(delay, unit);
   }

   public interface Expirable {
      long getExpireNanoTime();
   }

   private class Timeouts extends CyclicTimeout {
      private Timeouts(Scheduler scheduler) {
         super(scheduler);
      }

      @Override
      public void onTimeoutExpired() {
         CyclicTimeouts.this.onTimeoutExpired();
      }
   }
}
