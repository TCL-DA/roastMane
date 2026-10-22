package org.eclipse.jetty.server;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.io.SelectorManager.AcceptListener;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.statistic.RateStatistic;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Scheduler.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class AcceptRateLimit extends AbstractLifeCycle implements AcceptListener, Runnable {
   private static final Logger LOG = LoggerFactory.getLogger(AcceptRateLimit.class);
   private final AutoLock _lock = new AutoLock();
   private final Server _server;
   private final List<AbstractConnector> _connectors = new ArrayList<>();
   private final AcceptRateLimit.Rate _rate;
   private final int _acceptRateLimit;
   private boolean _limiting;
   private Task _task;

   public AcceptRateLimit(
      @Name("acceptRateLimit") int acceptRateLimit, @Name("period") long period, @Name("units") TimeUnit units, @Name("server") Server server
   ) {
      this._server = server;
      this._acceptRateLimit = acceptRateLimit;
      this._rate = new AcceptRateLimit.Rate(period, units);
   }

   public AcceptRateLimit(@Name("limit") int limit, @Name("period") long period, @Name("units") TimeUnit units, @Name("connectors") Connector... connectors) {
      this(limit, period, units, (Server)null);

      for (Connector c : connectors) {
         if (c instanceof AbstractConnector) {
            this._connectors.add((AbstractConnector)c);
         } else {
            LOG.warn("Connector {} is not an AbstractConnector. Connections not limited", c);
         }
      }
   }

   @ManagedAttribute("The accept rate limit")
   public int getAcceptRateLimit() {
      return this._acceptRateLimit;
   }

   @ManagedAttribute("The accept rate period")
   public long getPeriod() {
      return this._rate.getPeriod();
   }

   @ManagedAttribute("The accept rate period units")
   public TimeUnit getUnits() {
      return this._rate.getUnits();
   }

   @ManagedAttribute("The current accept rate")
   public int getRate() {
      return this._rate.getRate();
   }

   @ManagedAttribute("The maximum accept rate achieved")
   public long getMaxRate() {
      return this._rate.getMax();
   }

   @ManagedOperation(
      value = "Resets the accept rate",
      impact = "ACTION"
   )
   public void reset() {
      AutoLock l = this._lock.lock();

      try {
         this._rate.reset();
         if (this._limiting) {
            this._limiting = false;
            this.unlimit();
         }
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }
   }

   protected void age(long period, TimeUnit units) {
      this._rate.age(period, units);
   }

   protected void doStart() throws Exception {
      AutoLock l = this._lock.lock();

      try {
         if (this._server != null) {
            for (Connector c : this._server.getConnectors()) {
               if (c instanceof AbstractConnector) {
                  this._connectors.add((AbstractConnector)c);
               } else {
                  LOG.warn("Connector {} is not an AbstractConnector. Connections not limited", c);
               }
            }
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("AcceptLimit accept<{} rate<{} in {}", new Object[]{this._acceptRateLimit, this._rate, this._connectors});
         }

         for (AbstractConnector cx : this._connectors) {
            cx.addBean(this);
         }
      } catch (Throwable var7) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (l != null) {
         l.close();
      }
   }

   protected void doStop() throws Exception {
      AutoLock l = this._lock.lock();

      try {
         if (this._task != null) {
            this._task.cancel();
         }

         this._task = null;

         for (AbstractConnector c : this._connectors) {
            c.removeBean(this);
         }

         if (this._server != null) {
            this._connectors.clear();
         }

         this._limiting = false;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }
   }

   protected void limit() {
      for (AbstractConnector c : this._connectors) {
         c.setAccepting(false);
      }

      this.schedule();
   }

   protected void unlimit() {
      for (AbstractConnector c : this._connectors) {
         c.setAccepting(true);
      }
   }

   public void onAccepting(SelectableChannel channel) {
      AutoLock l = this._lock.lock();

      try {
         int rate = this._rate.record();
         if (LOG.isDebugEnabled()) {
            LOG.debug("onAccepting rate {}/{} for {} {}", new Object[]{rate, this._acceptRateLimit, this._rate, channel});
         }

         if (rate > this._acceptRateLimit && !this._limiting) {
            this._limiting = true;
            LOG.warn("AcceptLimit rate exceeded {}>{} on {}", new Object[]{rate, this._acceptRateLimit, this._connectors});
            this.limit();
         }
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   private void schedule() {
      long oldest = this._rate.getOldest(TimeUnit.MILLISECONDS);
      long period = TimeUnit.MILLISECONDS.convert(this._rate.getPeriod(), this._rate.getUnits());
      long delay = period - (oldest > 0L ? oldest : 0L);
      if (delay < 0L) {
         delay = 0L;
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("schedule {} {}", delay, TimeUnit.MILLISECONDS);
      }

      this._task = this._connectors.get(0).getScheduler().schedule(this, delay, TimeUnit.MILLISECONDS);
   }

   @Override
   public void run() {
      AutoLock l = this._lock.lock();

      label56: {
         label57: {
            try {
               this._task = null;
               if (!this.isRunning()) {
                  break label56;
               }

               int rate = this._rate.getRate();
               if (rate > this._acceptRateLimit) {
                  this.schedule();
                  break label57;
               }

               if (this._limiting) {
                  this._limiting = false;
                  LOG.warn("AcceptLimit rate OK {}<={} on {}", new Object[]{rate, this._acceptRateLimit, this._connectors});
                  this.unlimit();
               }
            } catch (Throwable var5) {
               if (l != null) {
                  try {
                     l.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (l != null) {
               l.close();
            }

            return;
         }

         if (l != null) {
            l.close();
         }

         return;
      }

      if (l != null) {
         l.close();
      }
   }

   private static final class Rate extends RateStatistic {
      private Rate(long period, TimeUnit units) {
         super(period, units);
      }

      protected void age(long period, TimeUnit units) {
         super.age(period, units);
      }
   }
}
