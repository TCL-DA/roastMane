package org.eclipse.jetty.util.thread;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sweeper extends AbstractLifeCycle implements Runnable {
   private static final Logger LOG = LoggerFactory.getLogger(Sweeper.class);
   private final AtomicReference<List<Sweeper.Sweepable>> items = new AtomicReference<>();
   private final AtomicReference<Scheduler.Task> task = new AtomicReference<>();
   private final Scheduler scheduler;
   private final long period;

   public Sweeper(Scheduler scheduler, long period) {
      this.scheduler = scheduler;
      this.period = period;
   }

   @Override
   protected void doStart() throws Exception {
      super.doStart();
      this.items.set(new CopyOnWriteArrayList<>());
      this.activate();
   }

   @Override
   protected void doStop() throws Exception {
      this.deactivate();
      this.items.set(null);
      super.doStop();
   }

   public int getSize() {
      List<Sweeper.Sweepable> refs = this.items.get();
      return refs == null ? 0 : refs.size();
   }

   public boolean offer(Sweeper.Sweepable sweepable) {
      List<Sweeper.Sweepable> refs = this.items.get();
      if (refs == null) {
         return false;
      } else {
         refs.add(sweepable);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Resource offered {}", sweepable);
         }

         return true;
      }
   }

   public boolean remove(Sweeper.Sweepable sweepable) {
      List<Sweeper.Sweepable> refs = this.items.get();
      return refs != null && refs.remove(sweepable);
   }

   @Override
   public void run() {
      List<Sweeper.Sweepable> refs = this.items.get();
      if (refs != null) {
         for (Sweeper.Sweepable sweepable : refs) {
            try {
               if (sweepable.sweep()) {
                  refs.remove(sweepable);
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Resource swept {}", sweepable);
                  }
               }
            } catch (Throwable var5) {
               LOG.info("Exception while sweeping {}", sweepable, var5);
            }
         }

         this.activate();
      }
   }

   private void activate() {
      if (this.isRunning()) {
         Scheduler.Task t = this.scheduler.schedule(this, this.period, TimeUnit.MILLISECONDS);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Scheduled in {} ms sweep task {}", this.period, t);
         }

         this.task.set(t);
      } else if (LOG.isDebugEnabled()) {
         LOG.debug("Skipping sweep task scheduling");
      }
   }

   private void deactivate() {
      Scheduler.Task t = this.task.getAndSet(null);
      if (t != null) {
         boolean cancelled = t.cancel();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Cancelled ({}) sweep task {}", cancelled, t);
         }
      }
   }

   public interface Sweepable {
      boolean sweep();
   }
}
