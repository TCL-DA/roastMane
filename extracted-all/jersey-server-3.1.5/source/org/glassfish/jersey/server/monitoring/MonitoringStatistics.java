package org.glassfish.jersey.server.monitoring;

import java.util.Map;

public interface MonitoringStatistics {
   Map<String, ResourceStatistics> getUriStatistics();

   Map<Class<?>, ResourceStatistics> getResourceClassStatistics();

   ExecutionStatistics getRequestStatistics();

   ResponseStatistics getResponseStatistics();

   ExceptionMapperStatistics getExceptionMapperStatistics();

   @Deprecated
   MonitoringStatistics snapshot();
}
