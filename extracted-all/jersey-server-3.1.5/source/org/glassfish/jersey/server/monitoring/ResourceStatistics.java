package org.glassfish.jersey.server.monitoring;

import java.util.Map;
import org.glassfish.jersey.server.model.ResourceMethod;

public interface ResourceStatistics {
   ExecutionStatistics getResourceMethodExecutionStatistics();

   ExecutionStatistics getRequestExecutionStatistics();

   Map<ResourceMethod, ResourceMethodStatistics> getResourceMethodStatistics();

   @Deprecated
   ResourceStatistics snapshot();
}
