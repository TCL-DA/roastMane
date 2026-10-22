package org.glassfish.jersey.server.monitoring;

import java.util.Map;

public interface ExceptionMapperStatistics {
   Map<Class<?>, Long> getExceptionMapperExecutions();

   long getSuccessfulMappings();

   long getUnsuccessfulMappings();

   long getTotalMappings();

   @Deprecated
   ExceptionMapperStatistics snapshot();
}
