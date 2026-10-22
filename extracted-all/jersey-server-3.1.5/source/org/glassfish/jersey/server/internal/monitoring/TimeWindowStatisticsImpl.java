package org.glassfish.jersey.server.internal.monitoring;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.glassfish.jersey.server.internal.monitoring.core.TimeReservoir;
import org.glassfish.jersey.server.internal.monitoring.core.UniformTimeSnapshot;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;

final class TimeWindowStatisticsImpl implements TimeWindowStatistics {
   private static final ConcurrentHashMap<Long, TimeWindowStatisticsImpl> EMPTY = new ConcurrentHashMap<>(6);
   private final long interval;
   private final long minimumDuration;
   private final long maximumDuration;
   private final long averageDuration;
   private final long totalCount;
   private final double requestsPerSecond;

   private TimeWindowStatisticsImpl(long interval, double requestsPerSecond, long minimumDuration, long maximumDuration, long averageDuration, long totalCount) {
      this.interval = interval;
      this.requestsPerSecond = requestsPerSecond;
      this.minimumDuration = minimumDuration;
      this.maximumDuration = maximumDuration;
      this.averageDuration = averageDuration;
      this.totalCount = totalCount;
   }

   private TimeWindowStatisticsImpl(long interval, UniformTimeSnapshot snapshot) {
      this(interval, snapshot.getRate(TimeUnit.SECONDS), snapshot.getMin(), snapshot.getMax(), (long)snapshot.getMean(), snapshot.size());
   }

   @Override
   public long getTimeWindow() {
      return this.interval;
   }

   @Override
   public double getRequestsPerSecond() {
      return this.requestsPerSecond;
   }

   @Override
   public long getMinimumDuration() {
      return this.minimumDuration;
   }

   @Override
   public long getMaximumDuration() {
      return this.maximumDuration;
   }

   @Override
   public long getRequestCount() {
      return this.totalCount;
   }

   @Override
   public TimeWindowStatistics snapshot() {
      return this;
   }

   @Override
   public long getAverageDuration() {
      return this.averageDuration;
   }

   static {
      EMPTY.putIfAbsent(0L, new TimeWindowStatisticsImpl(0L, 0.0, 0L, 0L, 0L, 0L));
   }

   static class Builder<V> {
      private final long interval;
      private final TimeReservoir<V> timeReservoir;

      Builder(TimeReservoir<V> timeReservoir) {
         this.interval = timeReservoir.interval(TimeUnit.MILLISECONDS);
         this.timeReservoir = timeReservoir;
      }

      void addRequest(long requestTime, V duration) {
         this.timeReservoir.update(duration, requestTime, TimeUnit.MILLISECONDS);
      }

      TimeWindowStatisticsImpl build() {
         return this.build(System.currentTimeMillis());
      }

      TimeWindowStatisticsImpl build(long currentTime) {
         UniformTimeSnapshot durationReservoirSnapshot = this.timeReservoir.getSnapshot(currentTime, TimeUnit.MILLISECONDS);
         return durationReservoirSnapshot.size() == 0L
            ? this.getOrCreateEmptyStats(this.interval)
            : new TimeWindowStatisticsImpl(this.interval, durationReservoirSnapshot);
      }

      private TimeWindowStatisticsImpl getOrCreateEmptyStats(long interval) {
         if (!TimeWindowStatisticsImpl.EMPTY.containsKey(interval)) {
            TimeWindowStatisticsImpl.EMPTY.putIfAbsent(interval, new TimeWindowStatisticsImpl(interval, 0.0, -1L, -1L, -1L, 0L));
         }

         return TimeWindowStatisticsImpl.EMPTY.get(interval);
      }

      public long getInterval() {
         return this.interval;
      }
   }
}
