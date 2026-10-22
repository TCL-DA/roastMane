package org.glassfish.jersey.server.monitoring;

public interface ResourceMethodMXBean {
   String getMethodName();

   String getPath();

   String getHttpMethod();

   String getDeclaringClassName();

   String getConsumesMediaType();

   String getProducesMediaType();
}
