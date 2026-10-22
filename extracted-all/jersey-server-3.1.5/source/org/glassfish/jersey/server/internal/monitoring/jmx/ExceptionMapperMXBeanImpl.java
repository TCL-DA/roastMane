package org.glassfish.jersey.server.internal.monitoring.jmx;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.glassfish.jersey.server.monitoring.ExceptionMapperMXBean;
import org.glassfish.jersey.server.monitoring.ExceptionMapperStatistics;

public class ExceptionMapperMXBeanImpl implements ExceptionMapperMXBean {
   private volatile ExceptionMapperStatistics mapperStatistics;
   private volatile Map<String, Long> mapperExcecutions = new HashMap<>();

   public ExceptionMapperMXBeanImpl(ExceptionMapperStatistics mapperStatistics, MBeanExposer mBeanExposer, String parentName) {
      mBeanExposer.registerMBean(this, parentName + ",exceptions=ExceptionMapper");
      this.updateExceptionMapperStatistics(mapperStatistics);
   }

   public void updateExceptionMapperStatistics(ExceptionMapperStatistics mapperStatistics) {
      this.mapperStatistics = mapperStatistics;

      for (Entry<Class<?>, Long> entry : mapperStatistics.getExceptionMapperExecutions().entrySet()) {
         this.mapperExcecutions.put(entry.getKey().getName(), entry.getValue());
      }
   }

   @Override
   public Map<String, Long> getExceptionMapperCount() {
      return this.mapperExcecutions;
   }

   @Override
   public long getSuccessfulMappings() {
      return this.mapperStatistics.getSuccessfulMappings();
   }

   @Override
   public long getUnsuccessfulMappings() {
      return this.mapperStatistics.getUnsuccessfulMappings();
   }

   @Override
   public long getTotalMappings() {
      return this.mapperStatistics.getTotalMappings();
   }
}
