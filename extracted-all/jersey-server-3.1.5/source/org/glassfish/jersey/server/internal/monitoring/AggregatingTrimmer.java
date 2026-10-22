package org.glassfish.jersey.server.internal.monitoring;

import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.glassfish.jersey.internal.guava.TreeMultimap;
import org.glassfish.jersey.server.internal.monitoring.core.ReservoirConstants;
import org.glassfish.jersey.server.internal.monitoring.core.SlidingWindowTrimmer;
import org.glassfish.jersey.server.internal.monitoring.core.TimeReservoir;

class AggregatingTrimmer implements SlidingWindowTrimmer<Long> {
   private final List<TimeReservoir<AggregatedValueObject>> aggregatedReservoirListeners = new CopyOnWriteArrayList<>();
   private TimeReservoir<Long> timeReservoirNotifier;
   private final long startTime;
   private final TimeUnit startUnitTime;
   private final long chunkSize;
   private final AtomicBoolean locked = new AtomicBoolean(false);

   public AggregatingTrimmer(long startTime, TimeUnit startUnitTime, long chunkTimeSize, TimeUnit chunkTimeSizeUnit) {
      this.startTime = startTime;
      this.startUnitTime = startUnitTime;
      this.chunkSize = TimeUnit.NANOSECONDS.convert(chunkTimeSize, chunkTimeSizeUnit) << ReservoirConstants.COLLISION_BUFFER_POWER;
   }

   @Override
   public void trim(ConcurrentNavigableMap<Long, Long> map, long key) {
      if (this.locked.compareAndSet(false, true)) {
         TreeMultimap<Long, Long> trimMultiMap = TreeMultimap.create();
         NavigableMap<Long, Collection<Long>> trimMap = trimMultiMap.asMap();

         try {
            ConcurrentNavigableMap<Long, Long> headMap = map.headMap(key);

            while (!headMap.isEmpty()) {
               Entry<Long, Long> entry = headMap.pollFirstEntry();
               trimMultiMap.put(entry.getKey(), entry.getValue());
            }
         } finally {
            this.locked.set(false);
         }

         for (Entry var17 = trimMap.firstEntry(); var17 != null; var17 = trimMap.firstEntry()) {
            long chunkLowerBound = this.lowerBound((Long)var17.getKey());
            long chunkUpperBound = this.upperBound(chunkLowerBound, key);
            SortedMap<Long, Collection<Long>> chunkMap = trimMap.headMap(chunkUpperBound);
            AggregatedValueObject aggregatedValueObject = AggregatedValueObject.createFromMultiValues(chunkMap.values());

            for (TimeReservoir<AggregatedValueObject> aggregatedReservoir : this.aggregatedReservoirListeners) {
               aggregatedReservoir.update(aggregatedValueObject, chunkLowerBound >> ReservoirConstants.COLLISION_BUFFER_POWER, TimeUnit.NANOSECONDS);
            }

            chunkMap.clear();
         }
      }
   }

   private long upperBound(long chunkLowerBound, long key) {
      long chunkUpperBoundCandidate = chunkLowerBound + this.chunkSize;
      return chunkUpperBoundCandidate < key ? chunkUpperBoundCandidate : key;
   }

   private long lowerBound(Long key) {
      return lowerBound(key, TimeUnit.NANOSECONDS.convert(this.startTime, this.startUnitTime), this.chunkSize, ReservoirConstants.COLLISION_BUFFER_POWER);
   }

   static long lowerBound(long key, long startTime, long chunkSize, int power) {
      long offset = startTime % chunkSize << power;
      return key - offset >= 0L ? (key - offset) / chunkSize * chunkSize + offset : (key - offset - chunkSize + 1L) / chunkSize * chunkSize + offset;
   }

   public void register(TimeReservoir<AggregatedValueObject> timeReservoirListener) {
      this.aggregatedReservoirListeners.add(timeReservoirListener);
   }

   @Override
   public void setTimeReservoir(TimeReservoir<Long> timeReservoirNotifier) {
      this.timeReservoirNotifier = timeReservoirNotifier;
   }

   public TimeReservoir<Long> getTimeReservoirNotifier() {
      return this.timeReservoirNotifier;
   }
}
