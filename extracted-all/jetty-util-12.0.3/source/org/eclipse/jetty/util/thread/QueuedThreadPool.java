package org.eclipse.jetty.util.thread;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.jetty.util.AtomicBiInteger;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.VirtualThreads;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("A thread pool")
public class QueuedThreadPool
   extends ContainerLifeCycle
   implements ThreadFactory,
   ThreadPool.SizedThreadPool,
   Dumpable,
   TryExecutor,
   VirtualThreads.Configurable {
   private static final Logger LOG = LoggerFactory.getLogger(QueuedThreadPool.class);
   private static final Runnable NOOP = () -> {
   };
   private final AtomicBiInteger _counts = new AtomicBiInteger(Integer.MIN_VALUE, 0);
   private final AtomicLong _evictThreshold = new AtomicLong();
   private final Set<Thread> _threads = ConcurrentHashMap.newKeySet();
   private final AutoLock.WithCondition _joinLock = new AutoLock.WithCondition();
   private final BlockingQueue<Runnable> _jobs;
   private final ThreadGroup _threadGroup;
   private final ThreadFactory _threadFactory;
   private String _name = "qtp" + this.hashCode();
   private int _idleTimeout;
   private int _maxThreads;
   private int _minThreads;
   private int _reservedThreads = -1;
   private TryExecutor _tryExecutor = TryExecutor.NO_TRY;
   private int _priority = 5;
   private boolean _daemon = false;
   private boolean _detailedDump = false;
   private int _lowThreadsThreshold = 1;
   private ThreadPoolBudget _budget;
   private long _stopTimeout;
   private Executor _virtualThreadsExecutor;
   private int _maxEvictCount = 1;
   private final Runnable _runnable = new QueuedThreadPool.Runner();

   public QueuedThreadPool() {
      this(200);
   }

   public QueuedThreadPool(@Name("maxThreads") int maxThreads) {
      this(maxThreads, Math.min(8, maxThreads));
   }

   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads) {
      this(maxThreads, minThreads, 60000);
   }

   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads, @Name("queue") BlockingQueue<Runnable> queue) {
      this(maxThreads, minThreads, 60000, -1, queue, null);
   }

   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads, @Name("idleTimeout") int idleTimeout) {
      this(maxThreads, minThreads, idleTimeout, null);
   }

   public QueuedThreadPool(
      @Name("maxThreads") int maxThreads,
      @Name("minThreads") int minThreads,
      @Name("idleTimeout") int idleTimeout,
      @Name("queue") BlockingQueue<Runnable> queue
   ) {
      this(maxThreads, minThreads, idleTimeout, queue, null);
   }

   public QueuedThreadPool(
      @Name("maxThreads") int maxThreads,
      @Name("minThreads") int minThreads,
      @Name("idleTimeout") int idleTimeout,
      @Name("queue") BlockingQueue<Runnable> queue,
      @Name("threadGroup") ThreadGroup threadGroup
   ) {
      this(maxThreads, minThreads, idleTimeout, -1, queue, threadGroup);
   }

   public QueuedThreadPool(
      @Name("maxThreads") int maxThreads,
      @Name("minThreads") int minThreads,
      @Name("idleTimeout") int idleTimeout,
      @Name("reservedThreads") int reservedThreads,
      @Name("queue") BlockingQueue<Runnable> queue,
      @Name("threadGroup") ThreadGroup threadGroup
   ) {
      this(maxThreads, minThreads, idleTimeout, reservedThreads, queue, threadGroup, null);
   }

   public QueuedThreadPool(
      @Name("maxThreads") int maxThreads,
      @Name("minThreads") int minThreads,
      @Name("idleTimeout") int idleTimeout,
      @Name("reservedThreads") int reservedThreads,
      @Name("queue") BlockingQueue<Runnable> queue,
      @Name("threadGroup") ThreadGroup threadGroup,
      @Name("threadFactory") ThreadFactory threadFactory
   ) {
      if (maxThreads < minThreads) {
         throw new IllegalArgumentException("max threads (" + maxThreads + ") less than min threads (" + minThreads + ")");
      } else {
         this.setMinThreads(minThreads);
         this.setMaxThreads(maxThreads);
         this.setIdleTimeout(idleTimeout);
         this.setStopTimeout(5000L);
         this.setReservedThreads(reservedThreads);
         if (queue == null) {
            int capacity = Math.max(this._minThreads, 8) * 1024;
            queue = new BlockingArrayQueue<>(capacity, capacity);
         }

         this._jobs = queue;
         this._threadGroup = threadGroup;
         this.setThreadPoolBudget(new ThreadPoolBudget(this));
         this._threadFactory = (ThreadFactory)(threadFactory == null ? this : threadFactory);
      }
   }

   @Override
   public ThreadPoolBudget getThreadPoolBudget() {
      return this._budget;
   }

   public void setThreadPoolBudget(ThreadPoolBudget budget) {
      if (budget != null && budget.getSizedThreadPool() != this) {
         throw new IllegalArgumentException();
      } else {
         this.updateBean(this._budget, budget);
         this._budget = budget;
      }
   }

   public void setStopTimeout(long stopTimeout) {
      this._stopTimeout = stopTimeout;
   }

   public long getStopTimeout() {
      return this._stopTimeout;
   }

   @Override
   protected void doStart() throws Exception {
      if (this._reservedThreads == 0) {
         this._tryExecutor = NO_TRY;
      } else {
         ReservedThreadExecutor reserved = new ReservedThreadExecutor(this, this._reservedThreads);
         reserved.setIdleTimeout((long)this._idleTimeout, TimeUnit.MILLISECONDS);
         this._tryExecutor = reserved;
      }

      this.addBean(this._tryExecutor);
      this._evictThreshold.set(NanoTime.now());
      super.doStart();
      this._counts.set(0, 0);
      this.ensureThreads();
   }

   @Override
   protected void doStop() throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Stopping {}", this);
      }

      super.doStop();
      this.removeBean(this._tryExecutor);
      this._tryExecutor = TryExecutor.NO_TRY;
      int threads = this._counts.getAndSetHi(Integer.MIN_VALUE);
      long timeout = this.getStopTimeout();
      BlockingQueue<Runnable> jobs = this.getQueue();
      if (timeout > 0L) {
         int i = 0;

         while (i < threads && jobs.offer(NOOP)) {
            i++;
         }

         this.joinThreads(NanoTime.now() + TimeUnit.MILLISECONDS.toNanos(timeout) / 2L);

         for (Thread thread : this._threads) {
            if (thread != Thread.currentThread()) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Interrupting {}", thread);
               }

               thread.interrupt();
            }
         }

         this.joinThreads(NanoTime.now() + TimeUnit.MILLISECONDS.toNanos(timeout) / 2L);
         Thread.yield();

         for (Thread unstopped : this._threads) {
            if (unstopped != Thread.currentThread()) {
               String stack = "";
               if (LOG.isDebugEnabled()) {
                  StringBuilder dmp = new StringBuilder();

                  for (StackTraceElement element : unstopped.getStackTrace()) {
                     dmp.append(System.lineSeparator()).append("\tat ").append(element);
                  }

                  stack = dmp.toString();
               }

               LOG.warn("Couldn't stop {}{}", unstopped, stack);
            }
         }
      }

      while (true) {
         Runnable job = this._jobs.poll();
         if (job == null) {
            if (this._budget != null) {
               this._budget.reset();
            }

            try (AutoLock.WithCondition l = this._joinLock.lock()) {
               l.signalAll();
            }

            return;
         }

         if (job instanceof Closeable) {
            try {
               ((Closeable)job).close();
            } catch (Throwable var14) {
               LOG.warn("Unable to close job: {}", job, var14);
            }
         } else if (job != NOOP) {
            LOG.warn("Stopped without executing or closing {}", job);
         }
      }
   }

   private void joinThreads(long stopByNanos) {
      label29:
      while (true) {
         for (Thread thread : this._threads) {
            if (thread != Thread.currentThread()) {
               long canWait = NanoTime.millisUntil(stopByNanos);
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Waiting for {} for {}", thread, canWait);
               }

               if (canWait <= 0L) {
                  return;
               }

               try {
                  thread.join(canWait);
               } catch (InterruptedException var8) {
                  continue label29;
               }
            }
         }

         return;
      }
   }

   @ManagedAttribute("maximum time a thread may be idle in ms")
   public int getIdleTimeout() {
      return this._idleTimeout;
   }

   public void setIdleTimeout(int idleTimeout) {
      this._idleTimeout = idleTimeout;
      ReservedThreadExecutor reserved = this.getBean(ReservedThreadExecutor.class);
      if (reserved != null) {
         reserved.setIdleTimeout((long)idleTimeout, TimeUnit.MILLISECONDS);
      }
   }

   @ManagedAttribute("maximum number of threads in the pool")
   @Override
   public int getMaxThreads() {
      return this._maxThreads;
   }

   @Override
   public void setMaxThreads(int maxThreads) {
      if (this._budget != null) {
         this._budget.check(maxThreads);
      }

      this._maxThreads = maxThreads;
      if (this._minThreads > this._maxThreads) {
         this._minThreads = this._maxThreads;
      }
   }

   @ManagedAttribute("minimum number of threads in the pool")
   @Override
   public int getMinThreads() {
      return this._minThreads;
   }

   @Override
   public void setMinThreads(int minThreads) {
      this._minThreads = minThreads;
      if (this._minThreads > this._maxThreads) {
         this._maxThreads = this._minThreads;
      }

      if (this.isStarted()) {
         this.ensureThreads();
      }
   }

   @ManagedAttribute("number of configured reserved threads or -1 for heuristic")
   public int getReservedThreads() {
      return this._reservedThreads;
   }

   public void setReservedThreads(int reservedThreads) {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._reservedThreads = reservedThreads;
      }
   }

   @ManagedAttribute("name of the thread pool")
   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._name = name;
      }
   }

   @ManagedAttribute("priority of threads in the pool")
   public int getThreadsPriority() {
      return this._priority;
   }

   public void setThreadsPriority(int priority) {
      this._priority = priority;
   }

   @ManagedAttribute("thread pool uses daemon threads")
   public boolean isDaemon() {
      return this._daemon;
   }

   public void setDaemon(boolean daemon) {
      this._daemon = daemon;
   }

   @ManagedAttribute("reports additional details in the dump")
   public boolean isDetailedDump() {
      return this._detailedDump;
   }

   public void setDetailedDump(boolean detailedDump) {
      this._detailedDump = detailedDump;
   }

   @ManagedAttribute("threshold at which the pool is low on threads")
   public int getLowThreadsThreshold() {
      return this._lowThreadsThreshold;
   }

   public void setLowThreadsThreshold(int lowThreadsThreshold) {
      this._lowThreadsThreshold = lowThreadsThreshold;
   }

   @Override
   public Executor getVirtualThreadsExecutor() {
      return this._virtualThreadsExecutor;
   }

   @Override
   public void setVirtualThreadsExecutor(Executor executor) {
      try {
         VirtualThreads.Configurable.super.setVirtualThreadsExecutor(executor);
         this._virtualThreadsExecutor = executor;
      } catch (UnsupportedOperationException var3) {
      }
   }

   public void setMaxEvictCount(int evictCount) {
      if (evictCount < 1) {
         throw new IllegalArgumentException("Invalid evict count " + evictCount);
      } else {
         this._maxEvictCount = evictCount;
      }
   }

   @ManagedAttribute("maximum number of idle threads to evict in one idle timeout period")
   public int getMaxEvictCount() {
      return this._maxEvictCount;
   }

   @ManagedAttribute("size of the job queue")
   public int getQueueSize() {
      int idle = this._counts.getLo();
      return Math.max(0, -idle);
   }

   @ManagedAttribute("maximum number (capacity) of reserved threads")
   public int getMaxReservedThreads() {
      return this._tryExecutor instanceof ReservedThreadExecutor reservedThreadExecutor ? reservedThreadExecutor.getCapacity() : 0;
   }

   @ManagedAttribute("number of available reserved threads")
   public int getAvailableReservedThreads() {
      return this._tryExecutor instanceof ReservedThreadExecutor reservedThreadExecutor ? reservedThreadExecutor.getAvailable() : 0;
   }

   @ManagedAttribute("number of threads in the pool")
   @Override
   public int getThreads() {
      int threads = this._counts.getHi();
      return Math.max(0, threads);
   }

   @ManagedAttribute("number of threads ready to execute transient jobs")
   public int getReadyThreads() {
      return this.getIdleThreads() + this.getAvailableReservedThreads();
   }

   @ManagedAttribute("number of threads used by internal components")
   public int getLeasedThreads() {
      return this.getMaxLeasedThreads() - this.getMaxReservedThreads();
   }

   @ManagedAttribute("maximum number of threads leased to internal components")
   public int getMaxLeasedThreads() {
      ThreadPoolBudget budget = this._budget;
      return budget == null ? 0 : budget.getLeasedThreads();
   }

   @ManagedAttribute("number of idle threads but not reserved")
   @Override
   public int getIdleThreads() {
      int idle = this._counts.getLo();
      return Math.max(0, idle);
   }

   @ManagedAttribute("number of threads executing internal and transient jobs")
   public int getBusyThreads() {
      return this.getThreads() - this.getReadyThreads();
   }

   @ManagedAttribute("number of threads executing transient jobs")
   public int getUtilizedThreads() {
      return this.getThreads() - this.getLeasedThreads() - this.getReadyThreads();
   }

   @ManagedAttribute("maximum number of threads available to run transient jobs")
   public int getMaxAvailableThreads() {
      return this.getMaxThreads() - this.getLeasedThreads();
   }

   @ManagedAttribute("utilization rate of threads executing transient jobs")
   public double getUtilizationRate() {
      return (double)this.getUtilizedThreads() / (double)this.getMaxAvailableThreads();
   }

   @ManagedAttribute(
      value = "thread pool is low on threads",
      readonly = true
   )
   @Override
   public boolean isLowOnThreads() {
      return this.getMaxThreads() - this.getThreads() + this.getReadyThreads() - this.getQueueSize() <= this.getLowThreadsThreshold();
   }

   @Override
   public void execute(Runnable job) {
      int startThread;
      long counts;
      int threads;
      int idle;
      do {
         counts = this._counts.get();
         threads = AtomicBiInteger.getHi(counts);
         if (threads == Integer.MIN_VALUE) {
            throw new RejectedExecutionException(job.toString());
         }

         idle = AtomicBiInteger.getLo(counts);
         startThread = idle <= 0 && threads < this._maxThreads ? 1 : 0;
      } while (!this._counts.compareAndSet(counts, threads + startThread, idle + startThread - 1));

      if (!this._jobs.offer(job)) {
         if (this.addCounts(-startThread, 1 - startThread)) {
            LOG.warn("{} rejected {}", this, job);
         }

         throw new RejectedExecutionException(job.toString());
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("queue {} startThread={}", job, startThread);
         }

         while (startThread-- > 0) {
            this.startThread();
         }
      }
   }

   @Override
   public boolean tryExecute(Runnable task) {
      TryExecutor tryExecutor = this._tryExecutor;
      return tryExecutor != null && tryExecutor.tryExecute(task);
   }

   @Override
   public void join() throws InterruptedException {
      try (AutoLock.WithCondition l = this._joinLock.lock()) {
         while (this.isRunning()) {
            l.await();
         }
      }

      while (this.isStopping()) {
         Thread.sleep(1L);
      }
   }

   private void ensureThreads() {
      while (true) {
         long counts = this._counts.get();
         int threads = AtomicBiInteger.getHi(counts);
         if (threads == Integer.MIN_VALUE) {
            break;
         }

         int idle = AtomicBiInteger.getLo(counts);
         if (threads >= this._minThreads && (idle >= 0 || threads >= this._maxThreads)) {
            break;
         }

         if (this._counts.compareAndSet(counts, threads + 1, idle + 1)) {
            this.startThread();
         }
      }
   }

   protected void startThread() {
      boolean started = false;

      try {
         Thread thread = this._threadFactory.newThread(this._runnable);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Starting {}", thread);
         }

         this._threads.add(thread);
         this._evictThreshold.set(NanoTime.now() + TimeUnit.MILLISECONDS.toNanos((long)this._idleTimeout));
         thread.start();
         started = true;
      } finally {
         if (!started) {
            this.addCounts(-1, -1);
         }
      }
   }

   private boolean addCounts(int deltaThreads, int deltaIdle) {
      while (true) {
         long encoded = this._counts.get();
         int threads = AtomicBiInteger.getHi(encoded);
         int idle = AtomicBiInteger.getLo(encoded);
         if (threads == Integer.MIN_VALUE) {
            long update = AtomicBiInteger.encode(threads, idle + deltaIdle);
            if (this._counts.compareAndSet(encoded, update)) {
               return false;
            }
         } else {
            long update = AtomicBiInteger.encode(threads + deltaThreads, idle + deltaIdle);
            if (this._counts.compareAndSet(encoded, update)) {
               return true;
            }
         }
      }
   }

   @Override
   public Thread newThread(Runnable runnable) {
      return PrivilegedThreadFactory.newThread(() -> {
         Thread thread = new Thread(this._threadGroup, runnable);
         thread.setDaemon(this.isDaemon());
         thread.setPriority(this.getThreadsPriority());
         thread.setName(this._name + "-" + thread.getId());
         thread.setContextClassLoader(this.getClass().getClassLoader());
         return thread;
      });
   }

   protected void removeThread(Thread thread) {
      this._threads.remove(thread);
   }

   @Override
   public void dump(Appendable out, String indent) throws IOException {
      List<Object> threads = new ArrayList<>(this.getMaxThreads());

      for (Thread thread : this._threads) {
         StackTraceElement[] trace = thread.getStackTrace();
         String stackTag = this.getCompressedStackTag(trace);
         String baseThreadInfo = String.format("%s %s tid=%d prio=%d", thread.getName(), thread.getState(), thread.getId(), thread.getPriority());
         if (!StringUtil.isBlank(stackTag)) {
            threads.add(baseThreadInfo + " " + stackTag);
         } else if (this.isDetailedDump()) {
            threads.add((Dumpable)(o, i) -> Dumpable.dumpObjects(o, i, baseThreadInfo, trace));
         } else {
            threads.add(baseThreadInfo + " @ " + (trace.length > 0 ? trace[0].toString() : "???"));
         }
      }

      DumpableCollection threadsDump = new DumpableCollection("threads", threads);
      if (this.isDetailedDump()) {
         this.dumpObjects(out, indent, new Object[]{threadsDump, new DumpableCollection("jobs", new ArrayList<>(this.getQueue()))});
      } else {
         this.dumpObjects(out, indent, new Object[]{threadsDump});
      }
   }

   private String getCompressedStackTag(StackTraceElement[] trace) {
      for (StackTraceElement t : trace) {
         if ("idleJobPoll".equals(t.getMethodName()) && t.getClassName().equals(QueuedThreadPool.Runner.class.getName())) {
            return "IDLE";
         }

         if ("reservedWait".equals(t.getMethodName()) && t.getClassName().endsWith("ReservedThread")) {
            return "RESERVED";
         }

         if ("select".equals(t.getMethodName()) && t.getClassName().endsWith("SelectorProducer")) {
            return "SELECTING";
         }

         if ("accept".equals(t.getMethodName()) && t.getClassName().contains("ServerConnector")) {
            return "ACCEPTING";
         }
      }

      return "";
   }

   protected void runJob(Runnable job) {
      job.run();
   }

   protected boolean evict() {
      long idleTimeoutNanos = TimeUnit.MILLISECONDS.toNanos((long)this.getIdleTimeout());
      int threads = this.getThreads();
      int minThreads = this.getMinThreads();

      for (int threadsToEvict = threads - minThreads; threadsToEvict > 0; threadsToEvict--) {
         long now = NanoTime.now();
         long evictPeriod = idleTimeoutNanos / (long)this.getMaxEvictCount();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Evict check, period={}ms {}", TimeUnit.NANOSECONDS.toMillis(evictPeriod), this);
         }

         long evictThreshold = this._evictThreshold.get();
         long threshold = evictThreshold;
         if (NanoTime.elapsed(evictThreshold, now) > idleTimeoutNanos) {
            threshold = now - idleTimeoutNanos;
         }

         threshold += evictPeriod;
         if (NanoTime.isBefore(now, threshold)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Evict skipped, threshold={}ms in the future {}", NanoTime.millisElapsed(now, threshold), this);
            }

            return false;
         }

         if (this._evictThreshold.compareAndSet(evictThreshold, threshold)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Evicted, threshold={}ms in the past {}", NanoTime.millisElapsed(threshold, now), this);
            }

            return true;
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Evict skipped, no excess threads {}", this);
      }

      return false;
   }

   protected BlockingQueue<Runnable> getQueue() {
      return this._jobs;
   }

   @ManagedOperation("interrupts a pool thread")
   public boolean interruptThread(@Name("id") long id) {
      for (Thread thread : this._threads) {
         if (thread.getId() == id) {
            thread.interrupt();
            return true;
         }
      }

      return false;
   }

   @ManagedOperation("dumps a pool thread stack")
   public String dumpThread(@Name("id") long id) {
      for (Thread thread : this._threads) {
         if (thread.getId() == id) {
            StringBuilder buf = new StringBuilder();
            buf.append(thread.getId()).append(" ").append(thread.getName()).append(" ");
            buf.append(thread.getState()).append(":").append(System.lineSeparator());

            for (StackTraceElement element : thread.getStackTrace()) {
               buf.append("  at ").append(element.toString()).append(System.lineSeparator());
            }

            return buf.toString();
         }
      }

      return null;
   }

   @Override
   public String toString() {
      long count = this._counts.get();
      int threads = Math.max(0, AtomicBiInteger.getHi(count));
      int idle = Math.max(0, AtomicBiInteger.getLo(count));
      int queue = this.getQueueSize();
      return String.format(
         "%s[%s]@%x{%s,%d<=%d<=%d,i=%d,r=%d,t=%dms,q=%d}[%s]",
         this.getClass().getSimpleName(),
         this._name,
         this.hashCode(),
         this.getState(),
         this.getMinThreads(),
         threads,
         this.getMaxThreads(),
         idle,
         this.getReservedThreads(),
         NanoTime.millisUntil(this._evictThreshold.get()),
         queue,
         this._tryExecutor
      );
   }

   private class Runner implements Runnable {
      private Runnable idleJobPoll(long idleTimeoutNanos) throws InterruptedException {
         return idleTimeoutNanos <= 0L ? QueuedThreadPool.this._jobs.take() : QueuedThreadPool.this._jobs.poll(idleTimeoutNanos, TimeUnit.NANOSECONDS);
      }

      @Override
      public void run() {
         if (QueuedThreadPool.LOG.isDebugEnabled()) {
            QueuedThreadPool.LOG.debug("Runner started for {}", QueuedThreadPool.this);
         }

         boolean idle = true;

         try {
            while (QueuedThreadPool.this._counts.getHi() != Integer.MIN_VALUE) {
               try {
                  long idleTimeoutNanos = TimeUnit.MILLISECONDS.toNanos((long)QueuedThreadPool.this.getIdleTimeout());
                  Runnable job = this.idleJobPoll(idleTimeoutNanos);

                  while (true) {
                     if (job != null) {
                        idle = false;
                        if (QueuedThreadPool.LOG.isDebugEnabled()) {
                           QueuedThreadPool.LOG.debug("run {} in {}", job, QueuedThreadPool.this);
                        }

                        this.doRunJob(job);
                        if (QueuedThreadPool.LOG.isDebugEnabled()) {
                           QueuedThreadPool.LOG.debug("ran {} in {}", job, QueuedThreadPool.this);
                        }

                        if (QueuedThreadPool.this.addCounts(0, 1)) {
                           idle = true;
                           job = QueuedThreadPool.this._jobs.poll();
                           continue;
                        }
                     }

                     if (QueuedThreadPool.this.evict()) {
                        return;
                     }
                     break;
                  }
               } catch (InterruptedException var9) {
                  QueuedThreadPool.LOG.trace("IGNORED", var9);
               }
            }
         } finally {
            Thread thread = Thread.currentThread();
            QueuedThreadPool.this.removeThread(thread);
            QueuedThreadPool.this.addCounts(-1, idle ? -1 : 0);
            if (QueuedThreadPool.LOG.isDebugEnabled()) {
               QueuedThreadPool.LOG.debug("{} exited for {}", thread, QueuedThreadPool.this);
            }

            QueuedThreadPool.this.ensureThreads();
         }
      }

      private void doRunJob(Runnable job) {
         try {
            QueuedThreadPool.this.runJob(job);
         } catch (Throwable var6) {
            QueuedThreadPool.LOG.warn("Job failed", var6);
         } finally {
            Thread.interrupted();
         }
      }
   }
}
