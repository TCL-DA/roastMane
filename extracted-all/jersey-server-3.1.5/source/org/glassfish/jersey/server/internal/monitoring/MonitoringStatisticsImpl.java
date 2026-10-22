package org.glassfish.jersey.server.internal.monitoring;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import org.glassfish.jersey.internal.util.collection.Views;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.monitoring.ExceptionMapperStatistics;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;
import org.glassfish.jersey.server.monitoring.ResponseStatistics;

final class MonitoringStatisticsImpl implements MonitoringStatistics {
   private final ExecutionStatistics requestStatistics;
   private final ResponseStatistics responseStatistics;
   private final ExceptionMapperStatistics exceptionMapperStatistics;
   private final Map<String, ResourceStatistics> uriStatistics;
   private final Map<Class<?>, ResourceStatistics> resourceClassStatistics;

   private MonitoringStatisticsImpl(
      Map<String, ResourceStatistics> uriStatistics,
      Map<Class<?>, ResourceStatistics> resourceClassStatistics,
      ExecutionStatistics requestStatistics,
      ResponseStatistics responseStatistics,
      ExceptionMapperStatistics exceptionMapperStatistics
   ) {
      this.uriStatistics = uriStatistics;
      this.resourceClassStatistics = resourceClassStatistics;
      this.requestStatistics = requestStatistics;
      this.responseStatistics = responseStatistics;
      this.exceptionMapperStatistics = exceptionMapperStatistics;
   }

   @Override
   public ExecutionStatistics getRequestStatistics() {
      return this.requestStatistics;
   }

   @Override
   public ResponseStatistics getResponseStatistics() {
      return this.responseStatistics;
   }

   @Override
   public Map<String, ResourceStatistics> getUriStatistics() {
      return this.uriStatistics;
   }

   @Override
   public Map<Class<?>, ResourceStatistics> getResourceClassStatistics() {
      return this.resourceClassStatistics;
   }

   @Override
   public ExceptionMapperStatistics getExceptionMapperStatistics() {
      return this.exceptionMapperStatistics;
   }

   @Override
   public MonitoringStatistics snapshot() {
      return this;
   }

   static class Builder {
      private static final Function<ResourceStatisticsImpl.Builder, ResourceStatistics> BUILDING_FUNCTION = ResourceStatisticsImpl.Builder::build;
      private final ResponseStatisticsImpl.Builder responseStatisticsBuilder;
      private final ExceptionMapperStatisticsImpl.Builder exceptionMapperStatisticsBuilder;
      private final ResourceMethodStatisticsImpl.Factory methodFactory = new ResourceMethodStatisticsImpl.Factory();
      private final SortedMap<String, ResourceStatisticsImpl.Builder> uriStatistics = new TreeMap<>();
      private final SortedMap<Class<?>, ResourceStatisticsImpl.Builder> resourceClassStatistics = new TreeMap<>(
         (o1, o2) -> o1.getName().compareTo(o2.getName())
      );
      private ExecutionStatisticsImpl.Builder executionStatisticsBuilder;

      Builder() {
         this.responseStatisticsBuilder = new ResponseStatisticsImpl.Builder();
         this.exceptionMapperStatisticsBuilder = new ExceptionMapperStatisticsImpl.Builder();
      }

      Builder(ResourceModel resourceModel) {
         this();

         for (Resource resource : resourceModel.getRootResources()) {
            this.processResource(resource, "");

            for (Resource child : resource.getChildResources()) {
               String path = resource.getPath();
               this.processResource(child, path.startsWith("/") ? path : "/" + path);
            }
         }
      }

      private void processResource(Resource resource, String pathPrefix) {
         StringBuilder pathSB = new StringBuilder(pathPrefix);
         if (!pathPrefix.endsWith("/") && !resource.getPath().startsWith("/")) {
            pathSB.append("/");
         }

         pathSB.append(resource.getPath());
         this.uriStatistics.put(pathSB.toString(), new ResourceStatisticsImpl.Builder(resource, this.methodFactory));

         for (ResourceMethod resourceMethod : resource.getResourceMethods()) {
            this.getOrCreateResourceBuilder(resourceMethod).addMethod(resourceMethod);
         }
      }

      private ResourceStatisticsImpl.Builder getOrCreateResourceBuilder(ResourceMethod resourceMethod) {
         Class<?> clazz = resourceMethod.getInvocable().getHandler().getHandlerClass();
         ResourceStatisticsImpl.Builder builder = this.resourceClassStatistics.get(clazz);
         if (builder == null) {
            builder = new ResourceStatisticsImpl.Builder(this.methodFactory);
            this.resourceClassStatistics.put(clazz, builder);
         }

         return builder;
      }

      ExceptionMapperStatisticsImpl.Builder getExceptionMapperStatisticsBuilder() {
         return this.exceptionMapperStatisticsBuilder;
      }

      void addRequestExecution(long startTime, long duration) {
         if (this.executionStatisticsBuilder == null) {
            this.executionStatisticsBuilder = new ExecutionStatisticsImpl.Builder();
         }

         this.executionStatisticsBuilder.addExecution(startTime, duration);
      }

      void addExecution(String uri, ResourceMethod resourceMethod, long methodTime, long methodDuration, long requestTime, long requestDuration) {
         ResourceStatisticsImpl.Builder uriStatsBuilder = this.uriStatistics.get(uri);
         if (uriStatsBuilder == null) {
            uriStatsBuilder = new ResourceStatisticsImpl.Builder(resourceMethod.getParent(), this.methodFactory);
            this.uriStatistics.put(uri, uriStatsBuilder);
         }

         uriStatsBuilder.addExecution(resourceMethod, methodTime, methodDuration, requestTime, requestDuration);
         ResourceStatisticsImpl.Builder classStatsBuilder = this.getOrCreateResourceBuilder(resourceMethod);
         classStatsBuilder.addExecution(resourceMethod, methodTime, methodDuration, requestTime, requestDuration);
         this.methodFactory.getOrCreate(resourceMethod).addResourceMethodExecution(methodTime, methodDuration, requestTime, requestDuration);
      }

      void addResponseCode(int responseCode) {
         this.responseStatisticsBuilder.addResponseCode(responseCode);
      }

      MonitoringStatisticsImpl build() {
         Map<String, ResourceStatistics> uriStats = Collections.unmodifiableMap(Views.mapView(this.uriStatistics, BUILDING_FUNCTION));
         Map<Class<?>, ResourceStatistics> classStats = Collections.unmodifiableMap(Views.mapView(this.resourceClassStatistics, BUILDING_FUNCTION));
         ExecutionStatistics requestStats = (ExecutionStatistics)(this.executionStatisticsBuilder == null
            ? ExecutionStatisticsImpl.EMPTY
            : this.executionStatisticsBuilder.build());
         return new MonitoringStatisticsImpl(
            uriStats, classStats, requestStats, this.responseStatisticsBuilder.build(), this.exceptionMapperStatisticsBuilder.build()
         );
      }
   }
}
