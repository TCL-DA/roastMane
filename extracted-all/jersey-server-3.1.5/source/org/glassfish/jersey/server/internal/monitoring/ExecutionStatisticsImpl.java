package org.glassfish.jersey.server.internal.monitoring;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.glassfish.jersey.server.internal.monitoring.core.UniformTimeReservoir;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;

final class ExecutionStatisticsImpl implements ExecutionStatistics {
   static final ExecutionStatistics EMPTY = new ExecutionStatisticsImpl.Builder().build();
   private final long lastStartTime;
   private final Map<Long, TimeWindowStatistics> timeWindowStatistics;

   @Override
   public Date getLastStartTime() {
      return new Date(this.lastStartTime);
   }

   @Override
   public Map<Long, TimeWindowStatistics> getTimeWindowStatistics() {
      return this.timeWindowStatistics;
   }

   @Override
   public ExecutionStatistics snapshot() {
      return this;
   }

   private ExecutionStatisticsImpl(long lastStartTime, Map<Long, TimeWindowStatistics> timeWindowStatistics) {
      this.lastStartTime = lastStartTime;
      this.timeWindowStatistics = Collections.unmodifiableMap(timeWindowStatistics);
   }

   static class Builder {
      private volatile long lastStartTime;
      private final Map<Long, TimeWindowStatisticsImpl.Builder> intervalStatistics;
      private final Collection<TimeWindowStatisticsImpl.Builder<Long>> updatableIntervalStatistics;

      public Builder() {
         long nowMillis = System.currentTimeMillis();
         AggregatingTrimmer trimmer = new AggregatingTrimmer(nowMillis, TimeUnit.MILLISECONDS, 1L, TimeUnit.SECONDS);
         TimeWindowStatisticsImpl.Builder<Long> oneSecondIntervalWindowBuilder = new TimeWindowStatisticsImpl.Builder<>(
            new SlidingWindowTimeReservoir(1L, TimeUnit.SECONDS, nowMillis, TimeUnit.MILLISECONDS, trimmer)
         );
         TimeWindowStatisticsImpl.Builder<Long> infiniteIntervalWindowBuilder = new TimeWindowStatisticsImpl.Builder<>(
            new UniformTimeReservoir(nowMillis, TimeUnit.MILLISECONDS)
         );
         this.updatableIntervalStatistics = Arrays.asList(infiniteIntervalWindowBuilder, oneSecondIntervalWindowBuilder);
         HashMap<Long, TimeWindowStatisticsImpl.Builder> tmpIntervalStatistics = new HashMap<>(6);
         tmpIntervalStatistics.put(0L, infiniteIntervalWindowBuilder);
         tmpIntervalStatistics.put(TimeUnit.SECONDS.toMillis(1L), oneSecondIntervalWindowBuilder);
         addAggregatedInterval(tmpIntervalStatistics, nowMillis, 15L, TimeUnit.SECONDS, trimmer);
         addAggregatedInterval(tmpIntervalStatistics, nowMillis, 1L, TimeUnit.MINUTES, trimmer);
         addAggregatedInterval(tmpIntervalStatistics, nowMillis, 15L, TimeUnit.MINUTES, trimmer);
         addAggregatedInterval(tmpIntervalStatistics, nowMillis, 1L, TimeUnit.HOURS, trimmer);
         this.intervalStatistics = Collections.unmodifiableMap(tmpIntervalStatistics);
      }

      private static void addAggregatedInterval(
         Map<Long, TimeWindowStatisticsImpl.Builder> intervalStatisticsMap, long nowMillis, long interval, TimeUnit timeUnit, AggregatingTrimmer notifier
      ) {
         long intervalInMillis = timeUnit.toMillis(interval);
         intervalStatisticsMap.put(
            intervalInMillis,
            new TimeWindowStatisticsImpl.Builder<>(
               new AggregatedSlidingWindowTimeReservoir(intervalInMillis, TimeUnit.MILLISECONDS, nowMillis, TimeUnit.MILLISECONDS, notifier)
            )
         );
      }

      void addExecution(long startTime, long duration) {
         for (TimeWindowStatisticsImpl.Builder<Long> statBuilder : this.updatableIntervalStatistics) {
            statBuilder.addRequest(startTime, duration);
         }

         this.lastStartTime = startTime;
      }

      public ExecutionStatisticsImpl build() {
         Map<Long, TimeWindowStatistics> newIntervalStatistics = new HashMap<>();

         for (Entry<Long, TimeWindowStatisticsImpl.Builder> builderEntry : this.intervalStatistics.entrySet()) {
            newIntervalStatistics.put(builderEntry.getKey(), builderEntry.getValue().build());
         }

         return new ExecutionStatisticsImpl(this.lastStartTime, newIntervalStatistics);
      }
   }
}
