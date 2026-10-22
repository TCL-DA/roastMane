package org.glassfish.jersey.server.internal.monitoring;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.DestroyListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.uri.UriTemplate;

@Priority(1100)
public final class MonitoringEventListener implements ApplicationEventListener {
   private static final Logger LOGGER = Logger.getLogger(MonitoringEventListener.class.getName());
   private static final int EVENT_QUEUE_SIZE = 500000;
   @Inject
   private InjectionManager injectionManager;
   private final Queue<MonitoringEventListener.RequestStats> requestQueuedItems = new ArrayBlockingQueue<>(500000);
   private final Queue<Integer> responseStatuses = new ArrayBlockingQueue<>(500000);
   private final Queue<RequestEvent> exceptionMapperEvents = new ArrayBlockingQueue<>(500000);
   private volatile MonitoringStatisticsProcessor monitoringStatisticsProcessor;
   private final AtomicBoolean processorFailed = new AtomicBoolean(false);

   public MonitoringEventListener.ReqEventListener onRequest(RequestEvent requestEvent) {
      switch (requestEvent.getType()) {
         case START:
            return new MonitoringEventListener.ReqEventListener();
         default:
            return null;
      }
   }

   @Override
   public void onEvent(ApplicationEvent event) {
      ApplicationEvent.Type type = event.getType();
      switch (type) {
         case INITIALIZATION_START:
         default:
            break;
         case RELOAD_FINISHED:
         case INITIALIZATION_FINISHED:
            this.monitoringStatisticsProcessor = new MonitoringStatisticsProcessor(this.injectionManager, this);
            this.processorFailed.set(false);
            this.monitoringStatisticsProcessor.startMonitoringWorker();
            break;
         case DESTROY_FINISHED:
            if (this.monitoringStatisticsProcessor != null) {
               try {
                  this.monitoringStatisticsProcessor.shutDown();
               } catch (InterruptedException var8) {
                  Thread.currentThread().interrupt();
                  throw new ProcessingException(LocalizationMessages.ERROR_MONITORING_SHUTDOWN_INTERRUPTED(), var8);
               }
            }

            for (DestroyListener listener : this.injectionManager.getAllInstances(DestroyListener.class)) {
               try {
                  listener.onDestroy();
               } catch (Exception var7) {
                  LOGGER.log(Level.WARNING, LocalizationMessages.ERROR_MONITORING_STATISTICS_LISTENER_DESTROY(listener.getClass()), (Throwable)var7);
               }
            }
      }
   }

   private <T> boolean offer(Queue<T> queue, T event) {
      return !this.processorFailed.get() ? queue.offer(event) : true;
   }

   void processorFailed() {
      this.processorFailed.set(true);
   }

   Queue<RequestEvent> getExceptionMapperEvents() {
      return this.exceptionMapperEvents;
   }

   Queue<MonitoringEventListener.RequestStats> getRequestQueuedItems() {
      return this.requestQueuedItems;
   }

   Queue<Integer> getResponseStatuses() {
      return this.responseStatuses;
   }

   static class MethodStats extends MonitoringEventListener.TimeStats {
      private final ResourceMethod method;

      private MethodStats(ResourceMethod method, long startTime, long requestDuration) {
         super(startTime, requestDuration);
         this.method = method;
      }

      ResourceMethod getMethod() {
         return this.method;
      }
   }

   private class ReqEventListener implements RequestEventListener {
      private final long requestTimeStart = System.currentTimeMillis();
      private volatile long methodTimeStart;
      private volatile MonitoringEventListener.MethodStats methodStats;

      public ReqEventListener() {
      }

      @Override
      public void onEvent(RequestEvent event) {
         long now = System.currentTimeMillis();
         switch (event.getType()) {
            case RESOURCE_METHOD_START:
               this.methodTimeStart = now;
               break;
            case RESOURCE_METHOD_FINISHED:
               ResourceMethod method = event.getUriInfo().getMatchedResourceMethod();
               this.methodStats = new MonitoringEventListener.MethodStats(method, this.methodTimeStart, now - this.methodTimeStart);
               break;
            case EXCEPTION_MAPPING_FINISHED:
               if (!MonitoringEventListener.this.offer(MonitoringEventListener.this.exceptionMapperEvents, event)) {
                  MonitoringEventListener.LOGGER.warning(LocalizationMessages.ERROR_MONITORING_QUEUE_MAPPER());
               }
               break;
            case FINISHED:
               if (event.isResponseWritten()
                  && !MonitoringEventListener.this.offer(MonitoringEventListener.this.responseStatuses, event.getContainerResponse().getStatus())) {
                  MonitoringEventListener.LOGGER.warning(LocalizationMessages.ERROR_MONITORING_QUEUE_RESPONSE());
               }

               StringBuilder sb = new StringBuilder();

               for (UriTemplate uriTemplate : event.getUriInfo()
                  .getMatchedTemplates()
                  .stream()
                  .collect(Collectors.collectingAndThen(Collectors.toList(), uriTemplates -> {
                     Collections.reverse(uriTemplates);
                     return uriTemplates;
                  }))) {
                  sb.append(uriTemplate.getTemplate());
                  if (!uriTemplate.endsWithSlash()) {
                     sb.append("/");
                  }

                  sb.setLength(sb.length() - 1);
               }

               if (!MonitoringEventListener.this.offer(
                  MonitoringEventListener.this.requestQueuedItems,
                  new MonitoringEventListener.RequestStats(
                     new MonitoringEventListener.TimeStats(this.requestTimeStart, now - this.requestTimeStart), this.methodStats, sb.toString()
                  )
               )) {
                  MonitoringEventListener.LOGGER.warning(LocalizationMessages.ERROR_MONITORING_QUEUE_REQUEST());
               }
         }
      }
   }

   static class RequestStats {
      private final MonitoringEventListener.TimeStats requestStats;
      private final MonitoringEventListener.MethodStats methodStats;
      private final String requestUri;

      private RequestStats(MonitoringEventListener.TimeStats requestStats, MonitoringEventListener.MethodStats methodStats, String requestUri) {
         this.requestStats = requestStats;
         this.methodStats = methodStats;
         this.requestUri = requestUri;
      }

      MonitoringEventListener.TimeStats getRequestStats() {
         return this.requestStats;
      }

      MonitoringEventListener.MethodStats getMethodStats() {
         return this.methodStats;
      }

      String getRequestUri() {
         return this.requestUri;
      }
   }

   static class TimeStats {
      private final long duration;
      private final long startTime;

      private TimeStats(long startTime, long requestDuration) {
         this.duration = requestDuration;
         this.startTime = startTime;
      }

      long getDuration() {
         return this.duration;
      }

      long getStartTime() {
         return this.startTime;
      }
   }
}
