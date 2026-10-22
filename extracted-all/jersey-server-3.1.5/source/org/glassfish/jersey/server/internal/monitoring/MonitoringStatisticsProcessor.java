package org.glassfish.jersey.server.internal.monitoring;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Configuration;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.server.BackgroundSchedulerLiteral;
import org.glassfish.jersey.server.ExtendedResourceContext;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.monitoring.MonitoringStatisticsListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;

final class MonitoringStatisticsProcessor {
   private static final Logger LOGGER = Logger.getLogger(MonitoringStatisticsProcessor.class.getName());
   private static final int DEFAULT_INTERVAL = 500;
   private static final int SHUTDOWN_TIMEOUT = 10;
   private final MonitoringEventListener monitoringEventListener;
   private final MonitoringStatisticsImpl.Builder statisticsBuilder;
   private final List<MonitoringStatisticsListener> statisticsCallbackList;
   private final ScheduledExecutorService scheduler;
   private final int interval;

   MonitoringStatisticsProcessor(InjectionManager injectionManager, MonitoringEventListener monitoringEventListener) {
      this.monitoringEventListener = monitoringEventListener;
      ResourceModel resourceModel = ((ExtendedResourceContext)injectionManager.getInstance(ExtendedResourceContext.class)).getResourceModel();
      this.statisticsBuilder = new MonitoringStatisticsImpl.Builder(resourceModel);
      this.statisticsCallbackList = injectionManager.getAllInstances(MonitoringStatisticsListener.class);
      this.scheduler = (ScheduledExecutorService)injectionManager.getInstance(
         ScheduledExecutorService.class, new Annotation[]{BackgroundSchedulerLiteral.INSTANCE}
      );
      this.interval = (Integer)PropertiesHelper.getValue(
         ((Configuration)injectionManager.getInstance(Configuration.class)).getProperties(),
         "jersey.config.server.monitoring.statistics.refresh.interval",
         500,
         Collections.emptyMap()
      );
   }

   public void startMonitoringWorker() {
      this.scheduler.scheduleWithFixedDelay(new Runnable() {
         @Override
         public void run() {
            try {
               MonitoringStatisticsProcessor.this.processRequestItems();
               MonitoringStatisticsProcessor.this.processResponseCodeEvents();
               MonitoringStatisticsProcessor.this.processExceptionMapperEvents();
            } catch (Throwable var6) {
               MonitoringStatisticsProcessor.this.monitoringEventListener.processorFailed();
               MonitoringStatisticsProcessor.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_MONITORING_STATISTICS_GENERATION(), var6);
               throw new ProcessingException(LocalizationMessages.ERROR_MONITORING_STATISTICS_GENERATION(), var6);
            }

            MonitoringStatisticsImpl immutableStats = MonitoringStatisticsProcessor.this.statisticsBuilder.build();
            Iterator<MonitoringStatisticsListener> iterator = MonitoringStatisticsProcessor.this.statisticsCallbackList.iterator();

            while (iterator.hasNext() && !Thread.currentThread().isInterrupted()) {
               MonitoringStatisticsListener listener = iterator.next();

               try {
                  listener.onStatistics(immutableStats);
               } catch (Throwable var5) {
                  MonitoringStatisticsProcessor.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_MONITORING_STATISTICS_LISTENER(listener.getClass()), var5);
                  iterator.remove();
               }
            }
         }
      }, 0L, (long)this.interval, TimeUnit.MILLISECONDS);
   }

   private void processExceptionMapperEvents() {
      Queue<RequestEvent> eventQueue = this.monitoringEventListener.getExceptionMapperEvents();
      MonitoringStatisticsProcessor.FloodingLogger floodingLogger = new MonitoringStatisticsProcessor.FloodingLogger(eventQueue);
      RequestEvent event = null;

      while ((event = eventQueue.poll()) != null) {
         floodingLogger.conditionallyLogFlooding();
         ExceptionMapperStatisticsImpl.Builder mapperStats = this.statisticsBuilder.getExceptionMapperStatisticsBuilder();
         if (event.getExceptionMapper() != null) {
            mapperStats.addExceptionMapperExecution(event.getExceptionMapper().getClass(), 1);
         }

         mapperStats.addMapping(event.isResponseSuccessfullyMapped(), 1);
      }
   }

   private void processRequestItems() {
      Queue<MonitoringEventListener.RequestStats> requestQueuedItems = this.monitoringEventListener.getRequestQueuedItems();
      MonitoringStatisticsProcessor.FloodingLogger floodingLogger = new MonitoringStatisticsProcessor.FloodingLogger(requestQueuedItems);
      MonitoringEventListener.RequestStats event = null;

      while ((event = requestQueuedItems.poll()) != null) {
         floodingLogger.conditionallyLogFlooding();
         MonitoringEventListener.TimeStats requestStats = event.getRequestStats();
         this.statisticsBuilder.addRequestExecution(requestStats.getStartTime(), requestStats.getDuration());
         MonitoringEventListener.MethodStats methodStat = event.getMethodStats();
         if (methodStat != null) {
            ResourceMethod method = methodStat.getMethod();
            this.statisticsBuilder
               .addExecution(
                  event.getRequestUri(), method, methodStat.getStartTime(), methodStat.getDuration(), requestStats.getStartTime(), requestStats.getDuration()
               );
         }
      }
   }

   private void processResponseCodeEvents() {
      Queue<Integer> responseEvents = this.monitoringEventListener.getResponseStatuses();
      MonitoringStatisticsProcessor.FloodingLogger floodingLogger = new MonitoringStatisticsProcessor.FloodingLogger(responseEvents);
      Integer code = null;

      while ((code = responseEvents.poll()) != null) {
         floodingLogger.conditionallyLogFlooding();
         this.statisticsBuilder.addResponseCode(code);
      }
   }

   void shutDown() throws InterruptedException {
      this.scheduler.shutdown();
      boolean success = this.scheduler.awaitTermination(10L, TimeUnit.SECONDS);
      if (!success) {
         LOGGER.warning(LocalizationMessages.ERROR_MONITORING_SCHEDULER_DESTROY_TIMEOUT());
      }
   }

   private static class FloodingLogger {
      private static final int FLOODING_WARNING_LOG_INTERVAL_MILLIS = 5000;
      private final Collection<?> collection;
      private final long startTime = System.nanoTime();
      private int i = 0;
      private int lastSize;

      public FloodingLogger(Collection<?> collection) {
         this.collection = collection;
         this.lastSize = collection.size();
      }

      public void conditionallyLogFlooding() {
         if ((System.nanoTime() - this.startTime) / TimeUnit.NANOSECONDS.convert(5000L, TimeUnit.MILLISECONDS) > (long)this.i) {
            if (this.collection.size() > this.lastSize) {
               MonitoringStatisticsProcessor.LOGGER.warning(LocalizationMessages.ERROR_MONITORING_QUEUE_FLOODED(this.collection.size()));
            }

            this.i++;
            this.lastSize = this.collection.size();
         }
      }
   }
}
