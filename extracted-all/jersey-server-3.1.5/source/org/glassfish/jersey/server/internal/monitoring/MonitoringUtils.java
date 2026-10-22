package org.glassfish.jersey.server.internal.monitoring;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;

public final class MonitoringUtils {
   private static final double CACHEABLE_REQUEST_RATE_LIMIT = 0.001;

   public static String getMethodUniqueId(ResourceMethod method) {
      String path = method.getParent() != null ? createPath(method.getParent()) : "null";
      return method.getProducedTypes().toString()
         + "|"
         + method.getConsumedTypes().toString()
         + "|"
         + method.getHttpMethod()
         + "|"
         + path
         + "|"
         + method.getInvocable().getHandlingMethod().getName();
   }

   private static String createPath(Resource resource) {
      return appendPath(resource, new StringBuilder()).toString();
   }

   private static StringBuilder appendPath(Resource resource, StringBuilder path) {
      return resource.getParent() == null ? path.append(resource.getPath()) : appendPath(resource.getParent(), path).append(".").append(resource.getPath());
   }

   static boolean isCacheable(ExecutionStatistics stats) {
      for (TimeWindowStatistics window : stats.getTimeWindowStatistics().values()) {
         if (window.getRequestsPerSecond() >= 0.001) {
            return false;
         }
      }

      return true;
   }

   private MonitoringUtils() {
   }
}
