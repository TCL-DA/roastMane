package org.glassfish.jersey.server.monitoring;

import java.util.Map;

public interface ResponseStatistics {
   Integer getLastResponseCode();

   Map<Integer, Long> getResponseCodes();

   @Deprecated
   ResponseStatistics snapshot();
}
