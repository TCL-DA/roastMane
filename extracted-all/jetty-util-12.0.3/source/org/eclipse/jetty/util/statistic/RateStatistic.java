package org.eclipse.jetty.util.statistic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.thread.AutoLock;

public class RateStatistic {
   private final AutoLock _lock = new AutoLock();
   private final Deque<Long> _samples = new ArrayDeque<>();
   private final long _nanoPeriod;
   private final TimeUnit _units;
   private long _max;
   private long _count;

   public RateStatistic(long period, TimeUnit units) {
      this._nanoPeriod = TimeUnit.NANOSECONDS.convert(period, units);
      this._units = units;
   }

   public long getPeriod() {
      return this._units.convert(this._nanoPeriod, TimeUnit.NANOSECONDS);
   }

   public TimeUnit getUnits() {
      return this._units;
   }

   public void reset() {
      try (AutoLock l = this._lock.lock()) {
         this._samples.clear();
         this._max = 0L;
         this._count = 0L;
      }
   }

   private void update() {
      this.update(NanoTime.now());
   }

   private void update(long now) {
      for (Long head = this._samples.peekFirst(); head != null && NanoTime.elapsed(head, now) > this._nanoPeriod; head = this._samples.peekFirst()) {
         this._samples.removeFirst();
      }
   }

   protected void age(long period, TimeUnit units) {
      long increment = TimeUnit.NANOSECONDS.convert(period, units);

      try (AutoLock l = this._lock.lock()) {
         int size = this._samples.size();

         for (int i = 0; i < size; i++) {
            this._samples.addLast(this._samples.removeFirst() - increment);
         }

         this.update();
      }
   }

   public int record() {
      long now = NanoTime.now();

      int var5;
      try (AutoLock l = this._lock.lock()) {
         this._count++;
         this._samples.add(now);
         this.update(now);
         int rate = this._samples.size();
         if ((long)rate > this._max) {
            this._max = (long)rate;
         }

         var5 = rate;
      }

      return var5;
   }

   public int getRate() {
      int var2;
      try (AutoLock l = this._lock.lock()) {
         this.update();
         var2 = this._samples.size();
      }

      return var2;
   }

   public long getMax() {
      long var2;
      try (AutoLock l = this._lock.lock()) {
         var2 = this._max;
      }

      return var2;
   }

   public long getOldest(TimeUnit units) {
      long var4;
      try (AutoLock l = this._lock.lock()) {
         Long head = this._samples.peekFirst();
         if (head == null) {
            return -1L;
         }

         var4 = units.convert(NanoTime.since(head), TimeUnit.NANOSECONDS);
      }

      return var4;
   }

   public long getCount() {
      long var2;
      try (AutoLock l = this._lock.lock()) {
         var2 = this._count;
      }

      return var2;
   }

   public String dump() {
      return this.dump(TimeUnit.MINUTES);
   }

   public String dump(TimeUnit units) {
      long now = NanoTime.now();

      String var6;
      try (AutoLock l = this._lock.lock()) {
         String samples = this._samples
            .stream()
            .mapToLong(t -> units.convert(NanoTime.elapsed(t, now), TimeUnit.NANOSECONDS))
            .mapToObj(Long::toString)
            .collect(Collectors.joining(System.lineSeparator()));
         var6 = String.format("%s%n%s", this.toString(now), samples);
      }

      return var6;
   }

   @Override
   public String toString() {
      return this.toString(NanoTime.now());
   }

   private String toString(long nanoTime) {
      String var4;
      try (AutoLock l = this._lock.lock()) {
         this.update(nanoTime);
         var4 = String.format(
            "%s@%x{count=%d,max=%d,rate=%d per %d %s}",
            this.getClass().getSimpleName(),
            this.hashCode(),
            this._count,
            this._max,
            this._samples.size(),
            this._units.convert(this._nanoPeriod, TimeUnit.NANOSECONDS),
            this._units
         );
      }

      return var4;
   }
}
