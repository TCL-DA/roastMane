package org.eclipse.jetty.util.thread;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;

@ManagedObject
public class ScheduledExecutorScheduler extends AbstractLifeCycle implements Scheduler, Dumpable {
   private final String name;
   private final boolean daemon;
   private final ClassLoader classloader;
   private final ThreadGroup threadGroup;
   private final int threads;
   private final AtomicInteger count = new AtomicInteger();
   private volatile ScheduledExecutorService scheduler;
   private volatile Thread thread;

   public ScheduledExecutorScheduler() {
      this(null, false);
   }

   public ScheduledExecutorScheduler(String name, boolean daemon) {
      this(name, daemon, null);
   }

   public ScheduledExecutorScheduler(@Name("name") String name, @Name("daemon") boolean daemon, @Name("threads") int threads) {
      this(name, daemon, null, null, threads);
   }

   public ScheduledExecutorScheduler(String name, boolean daemon, ClassLoader classLoader) {
      this(name, daemon, classLoader, null);
   }

   public ScheduledExecutorScheduler(String name, boolean daemon, ClassLoader classLoader, ThreadGroup threadGroup) {
      this(name, daemon, classLoader, threadGroup, -1);
   }

   public ScheduledExecutorScheduler(
      @Name("name") String name,
      @Name("daemon") boolean daemon,
      @Name("classLoader") ClassLoader classLoader,
      @Name("threadGroup") ThreadGroup threadGroup,
      @Name("threads") int threads
   ) {
      this.name = StringUtil.isBlank(name) ? "Scheduler-" + this.hashCode() : name;
      this.daemon = daemon;
      this.classloader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
      this.threadGroup = threadGroup;
      this.threads = threads;
   }

   public ScheduledExecutorScheduler(ScheduledExecutorService scheduledExecutorService) {
      this.name = null;
      this.daemon = false;
      this.classloader = null;
      this.threadGroup = null;
      this.threads = 0;
      this.scheduler = scheduledExecutorService;
   }

   @Override
   protected void doStart() throws Exception {
      if (this.scheduler == null) {
         int size = this.threads > 0 ? this.threads : 1;
         ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(size, r -> {
            Thread thread = this.thread = new Thread(this.threadGroup, r, this.name + "-" + this.count.incrementAndGet());
            thread.setDaemon(this.daemon);
            thread.setContextClassLoader(this.classloader);
            return thread;
         });
         scheduler.setRemoveOnCancelPolicy(true);
         this.scheduler = scheduler;
      }

      super.doStart();
   }

   @Override
   protected void doStop() throws Exception {
      if (this.name != null) {
         this.scheduler.shutdownNow();
         this.scheduler = null;
      }

      super.doStop();
   }

   @Override
   public Scheduler.Task schedule(Runnable task, long delay, TimeUnit unit) {
      ScheduledExecutorService s = this.scheduler;
      if (s == null) {
         return () -> false;
      } else {
         ScheduledFuture<?> result = s.schedule(task, delay, unit);
         return new ScheduledExecutorScheduler.ScheduledFutureTask(result);
      }
   }

   @Override
   public String dump() {
      return Dumpable.dump(this);
   }

   @Override
   public void dump(Appendable out, String indent) throws IOException {
      Thread thread = this.thread;
      if (thread == null) {
         Dumpable.dumpObject(out, this);
      } else {
         Dumpable.dumpObjects(out, indent, this, thread.getStackTrace());
      }
   }

   @ManagedAttribute("The name of the scheduler")
   public String getName() {
      return this.name;
   }

   @ManagedAttribute("Whether the scheduler uses daemon threads")
   public boolean isDaemon() {
      return this.daemon;
   }

   @ManagedAttribute("The number of scheduler threads")
   public int getThreads() {
      return this.threads;
   }

   private static class ScheduledFutureTask implements Scheduler.Task {
      private final ScheduledFuture<?> scheduledFuture;

      ScheduledFutureTask(ScheduledFuture<?> scheduledFuture) {
         this.scheduledFuture = scheduledFuture;
      }

      @Override
      public boolean cancel() {
         return this.scheduledFuture.cancel(false);
      }
   }
}
