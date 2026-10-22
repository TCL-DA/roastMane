package org.glassfish.jersey.server.monitoring;

import org.glassfish.jersey.server.model.ResourceMethod;

public interface ResourceMethodStatistics {
   ExecutionStatistics getMethodStatistics();

   ExecutionStatistics getRequestStatistics();

   ResourceMethod getResourceMethod();

   @Deprecated
   ResourceMethodStatistics snapshot();
}
