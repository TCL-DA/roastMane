package org.glassfish.jersey.server.monitoring;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface ApplicationMXBean {
   String getApplicationName();

   String getApplicationClass();

   Map<String, String> getProperties();

   Date getStartTime();

   Set<String> getRegisteredClasses();

   Set<String> getRegisteredInstances();

   Set<String> getProviderClasses();
}
