package org.glassfish.jersey.spi;

import java.util.concurrent.ScheduledExecutorService;

@Contract
public interface ScheduledExecutorServiceProvider extends ExecutorServiceProvider {
   ScheduledExecutorService getExecutorService();
}
