package org.eclipse.jetty.util.thread;

import java.util.concurrent.Executor;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Pool of Threads")
public interface ThreadPool extends Executor {
   void join() throws InterruptedException;

   @ManagedAttribute("number of threads in pool")
   int getThreads();

   @ManagedAttribute("number of idle threads in pool")
   int getIdleThreads();

   @ManagedAttribute("indicates the pool is low on available threads")
   boolean isLowOnThreads();

   public interface SizedThreadPool extends ThreadPool {
      int getMinThreads();

      int getMaxThreads();

      void setMinThreads(int var1);

      void setMaxThreads(int var1);

      default ThreadPoolBudget getThreadPoolBudget() {
         return null;
      }
   }
}
