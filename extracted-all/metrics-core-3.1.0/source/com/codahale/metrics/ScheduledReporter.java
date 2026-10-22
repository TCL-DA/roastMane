package com.codahale.metrics;

import java.io.Closeable;
import java.util.Locale;
import java.util.SortedMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ScheduledReporter implements Closeable, Reporter {
   private static final Logger LOG = LoggerFactory.getLogger(ScheduledReporter.class);
   private static final AtomicInteger FACTORY_ID = new AtomicInteger();
   private final MetricRegistry registry;
   private final ScheduledExecutorService executor;
   private final MetricFilter filter;
   private final double durationFactor;
   private final String durationUnit;
   private final double rateFactor;
   private final String rateUnit;

   protected ScheduledReporter(MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit) {
      this(
         registry,
         name,
         filter,
         rateUnit,
         durationUnit,
         Executors.newSingleThreadScheduledExecutor(new ScheduledReporter.NamedThreadFactory(name + '-' + FACTORY_ID.incrementAndGet()))
      );
   }

   protected ScheduledReporter(
      MetricRegistry registry, String name, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit, ScheduledExecutorService executor
   ) {
      this.registry = registry;
      this.filter = filter;
      this.executor = executor;
      this.rateFactor = (double)rateUnit.toSeconds(1L);
      this.rateUnit = this.calculateRateUnit(rateUnit);
      this.durationFactor = 1.0 / (double)durationUnit.toNanos(1L);
      this.durationUnit = durationUnit.toString().toLowerCase(Locale.US);
   }

   public void start(long period, TimeUnit unit) {
      this.executor
         .scheduleAtFixedRate(
            new Runnable() {
               @Override
               public void run() {
                  try {
                     ScheduledReporter.this.report();
                  } catch (RuntimeException var2) {
                     ScheduledReporter.LOG
                        .error("RuntimeException thrown from {}#report. Exception was suppressed.", ScheduledReporter.this.getClass().getSimpleName(), var2);
                  }
               }
            },
            period,
            period,
            unit
         );
   }

   public void stop() {
      this.executor.shutdown();

      try {
         if (!this.executor.awaitTermination(1L, TimeUnit.SECONDS)) {
            this.executor.shutdownNow();
            if (!this.executor.awaitTermination(1L, TimeUnit.SECONDS)) {
               System.err.println(this.getClass().getSimpleName() + ": ScheduledExecutorService did not terminate");
            }
         }
      } catch (InterruptedException var2) {
         this.executor.shutdownNow();
         Thread.currentThread().interrupt();
      }
   }

   @Override
   public void close() {
      this.stop();
   }

   public void report() {
      synchronized (this) {
         this.report(
            this.registry.getGauges(this.filter),
            this.registry.getCounters(this.filter),
            this.registry.getHistograms(this.filter),
            this.registry.getMeters(this.filter),
            this.registry.getTimers(this.filter)
         );
      }
   }

   public abstract void report(
      SortedMap<String, Gauge> var1,
      SortedMap<String, Counter> var2,
      SortedMap<String, Histogram> var3,
      SortedMap<String, Meter> var4,
      SortedMap<String, Timer> var5
   );

   protected String getRateUnit() {
      return this.rateUnit;
   }

   protected String getDurationUnit() {
      return this.durationUnit;
   }

   protected double convertDuration(double duration) {
      return duration * this.durationFactor;
   }

   protected double convertRate(double rate) {
      return rate * this.rateFactor;
   }

   private String calculateRateUnit(TimeUnit unit) {
      String s = unit.toString().toLowerCase(Locale.US);
      return s.substring(0, s.length() - 1);
   }

   private static class NamedThreadFactory implements ThreadFactory {
      private final ThreadGroup group;
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      private NamedThreadFactory(String name) {
         SecurityManager s = System.getSecurityManager();
         this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
         this.namePrefix = "metrics-" + name + "-thread-";
      }

      @Override
      public Thread newThread(Runnable r) {
         Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
         t.setDaemon(true);
         if (t.getPriority() != 5) {
            t.setPriority(5);
         }

         return t;
      }
   }
}
