package org.glassfish.jersey.server.monitoring;

import java.util.Date;
import java.util.Map;

public interface ExecutionStatistics {
   Date getLastStartTime();

   Map<Long, TimeWindowStatistics> getTimeWindowStatistics();

   @Deprecated
   ExecutionStatistics snapshot();
}
