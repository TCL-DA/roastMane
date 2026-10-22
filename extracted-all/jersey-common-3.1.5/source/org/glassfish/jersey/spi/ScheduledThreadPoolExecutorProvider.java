package org.glassfish.jersey.spi;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class ScheduledThreadPoolExecutorProvider extends AbstractThreadPoolProvider<ScheduledThreadPoolExecutor> implements ScheduledExecutorServiceProvider {
   public ScheduledThreadPoolExecutorProvider(String name) {
      super(name);
   }

   @Override
   public ScheduledExecutorService getExecutorService() {
      return (ScheduledExecutorService)super.getExecutor();
   }

   protected ScheduledThreadPoolExecutor createExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
      return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, handler);
   }

   @Override
   public void dispose(ExecutorService executorService) {
   }

   @PreDestroy
   public void preDestroy() {
      this.close();
   }
}
