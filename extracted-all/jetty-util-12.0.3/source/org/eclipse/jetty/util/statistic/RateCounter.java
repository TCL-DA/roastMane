package org.eclipse.jetty.util.statistic;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.util.NanoTime;

public class RateCounter {
   private final LongAdder _total = new LongAdder();
   private final AtomicLong _nanoTime = new AtomicLong(NanoTime.now());

   public void add(long l) {
      this._total.add(l);
   }

   public long getRate() {
      long elapsed = NanoTime.millisSince(this._nanoTime.get());
      return elapsed == 0L ? 0L : this._total.sum() * 1000L / elapsed;
   }

   public void reset() {
      this._nanoTime.set(NanoTime.now());
      this._total.reset();
   }
}
