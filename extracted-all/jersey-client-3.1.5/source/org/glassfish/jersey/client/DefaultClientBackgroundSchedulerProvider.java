package org.glassfish.jersey.client;

import org.glassfish.jersey.spi.ScheduledThreadPoolExecutorProvider;

@ClientBackgroundScheduler
class DefaultClientBackgroundSchedulerProvider extends ScheduledThreadPoolExecutorProvider {
   DefaultClientBackgroundSchedulerProvider() {
      super("jersey-client-background-scheduler");
   }

   protected int getCorePoolSize() {
      return 1;
   }
}
