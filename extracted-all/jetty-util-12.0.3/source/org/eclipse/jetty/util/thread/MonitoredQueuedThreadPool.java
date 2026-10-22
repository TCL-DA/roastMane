package org.eclipse.jetty.util.thread;

import java.util.concurrent.BlockingQueue;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.statistic.CounterStatistic;
import org.eclipse.jetty.util.statistic.SampleStatistic;

@ManagedObject
public class MonitoredQueuedThreadPool extends QueuedThreadPool {
   private final CounterStatistic queueStats = new CounterStatistic();
   private final SampleStatistic queueLatencyStats = new SampleStatistic();
   private final SampleStatistic taskLatencyStats = new SampleStatistic();
   private final CounterStatistic threadStats = new CounterStatistic();

   public MonitoredQueuedThreadPool() {
      this(256);
   }

   public MonitoredQueuedThreadPool(int maxThreads) {
      this(maxThreads, maxThreads, 86400000, new BlockingArrayQueue<>(maxThreads, 256));
   }

   public MonitoredQueuedThreadPool(int maxThreads, int minThreads, int idleTimeOut, BlockingQueue<Runnable> queue) {
      super(maxThreads, minThreads, idleTimeOut, queue);
      this.addBean(this.queueStats);
      this.addBean(this.queueLatencyStats);
      this.addBean(this.taskLatencyStats);
      this.addBean(this.threadStats);
   }

   @Override
   public void execute(final Runnable job) {
      this.queueStats.increment();
      final long begin = NanoTime.now();
      super.execute(new Runnable() {
         @Override
         public void run() {
            long queueLatency = NanoTime.since(begin);
            MonitoredQueuedThreadPool.this.queueStats.decrement();
            MonitoredQueuedThreadPool.this.threadStats.increment();
            MonitoredQueuedThreadPool.this.queueLatencyStats.record(queueLatency);
            long start = NanoTime.now();

            try {
               job.run();
            } finally {
               long taskLatency = NanoTime.since(start);
               MonitoredQueuedThreadPool.this.threadStats.decrement();
               MonitoredQueuedThreadPool.this.taskLatencyStats.record(taskLatency);
            }
         }

         @Override
         public String toString() {
            return job.toString();
         }
      });
   }

   @ManagedOperation(
      value = "resets the statistics",
      impact = "ACTION"
   )
   public void reset() {
      this.queueStats.reset();
      this.queueLatencyStats.reset();
      this.taskLatencyStats.reset();
      this.threadStats.reset(0L);
   }

   @ManagedAttribute("the number of tasks executed")
   public long getTasks() {
      return this.taskLatencyStats.getCount();
   }

   @ManagedAttribute("the maximum number of busy threads")
   public int getMaxBusyThreads() {
      return (int)this.threadStats.getMax();
   }

   @ManagedAttribute("the maximum task queue size")
   public int getMaxQueueSize() {
      return (int)this.queueStats.getMax();
   }

   @ManagedAttribute("the average time a task remains in the queue, in nanoseconds")
   public long getAverageQueueLatency() {
      return (long)this.queueLatencyStats.getMean();
   }

   @ManagedAttribute("the maximum time a task remains in the queue, in nanoseconds")
   public long getMaxQueueLatency() {
      return this.queueLatencyStats.getMax();
   }

   @ManagedAttribute("the average task execution time, in nanoseconds")
   public long getAverageTaskLatency() {
      return (long)this.taskLatencyStats.getMean();
   }

   @ManagedAttribute("the maximum task execution time, in nanoseconds")
   public long getMaxTaskLatency() {
      return this.taskLatencyStats.getMax();
   }
}
