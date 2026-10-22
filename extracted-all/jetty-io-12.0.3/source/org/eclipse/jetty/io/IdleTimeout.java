package org.eclipse.jetty.io;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.Scheduler.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IdleTimeout {
   private static final Logger LOG = LoggerFactory.getLogger(IdleTimeout.class);
   private final Scheduler _scheduler;
   private final AtomicReference<Task> _timeout = new AtomicReference<>();
   private volatile long _idleTimeout;
   private volatile long _idleNanoTime = NanoTime.now();

   public IdleTimeout(Scheduler scheduler) {
      this._scheduler = scheduler;
   }

   public Scheduler getScheduler() {
      return this._scheduler;
   }

   public long getIdleFor() {
      return NanoTime.millisSince(this._idleNanoTime);
   }

   public long getIdleTimeout() {
      return this._idleTimeout;
   }

   public void setIdleTimeout(long idleTimeout) {
      long old = this._idleTimeout;
      this._idleTimeout = idleTimeout;
      if (LOG.isDebugEnabled()) {
         LOG.debug("Setting idle timeout {} -> {} on {}", new Object[]{old, idleTimeout, this});
      }

      if (old > 0L) {
         if (old <= idleTimeout) {
            return;
         }

         this.deactivate();
      }

      if (this.isOpen()) {
         this.activate();
      }
   }

   public void notIdle() {
      this._idleNanoTime = NanoTime.now();
   }

   private void idleCheck() {
      long idleLeft = this.checkIdleTimeout();
      if (idleLeft >= 0L) {
         this.scheduleIdleTimeout(idleLeft > 0L ? idleLeft : this.getIdleTimeout());
      }
   }

   private void scheduleIdleTimeout(long delay) {
      Task newTimeout = null;
      if (this.isOpen() && delay > 0L && this._scheduler != null) {
         newTimeout = this._scheduler.schedule(this::idleCheck, delay, TimeUnit.MILLISECONDS);
      }

      Task oldTimeout = this._timeout.getAndSet(newTimeout);
      if (oldTimeout != null) {
         oldTimeout.cancel();
      }
   }

   public void onOpen() {
      this.activate();
   }

   private void activate() {
      if (this._idleTimeout > 0L) {
         this.idleCheck();
      }
   }

   public void onClose() {
      this.deactivate();
   }

   private void deactivate() {
      Task oldTimeout = this._timeout.getAndSet(null);
      if (oldTimeout != null) {
         oldTimeout.cancel();
      }
   }

   protected long checkIdleTimeout() {
      if (this.isOpen()) {
         long idleNanoTime = this._idleNanoTime;
         long idleElapsed = NanoTime.millisSince(idleNanoTime);
         long idleTimeout = this.getIdleTimeout();
         long idleLeft = idleTimeout - idleElapsed;
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} idle timeout check, elapsed: {} ms, remaining: {} ms", new Object[]{this, idleElapsed, idleLeft});
         }

         if (idleTimeout > 0L && idleLeft <= 0L) {
            TimeoutException timeout = new TimeoutException("Idle timeout expired: " + idleElapsed + "/" + idleTimeout + " ms");
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} idle timeout expired", this, timeout);
            }

            try {
               this.onIdleExpired(timeout);
            } finally {
               this.notIdle();
            }
         }

         return idleLeft >= 0L ? idleLeft : 0L;
      } else {
         return -1L;
      }
   }

   protected abstract void onIdleExpired(TimeoutException var1);

   public abstract boolean isOpen();
}
