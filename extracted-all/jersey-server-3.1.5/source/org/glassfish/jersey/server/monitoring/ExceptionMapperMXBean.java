package org.glassfish.jersey.server.monitoring;

import java.util.Map;

public interface ExceptionMapperMXBean {
   Map<String, Long> getExceptionMapperCount();

   long getSuccessfulMappings();

   long getUnsuccessfulMappings();

   long getTotalMappings();
}
