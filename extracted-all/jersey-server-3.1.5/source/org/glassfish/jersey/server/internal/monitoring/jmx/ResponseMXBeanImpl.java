package org.glassfish.jersey.server.internal.monitoring.jmx;

import java.util.Map;
import org.glassfish.jersey.server.monitoring.ResponseMXBean;
import org.glassfish.jersey.server.monitoring.ResponseStatistics;

public class ResponseMXBeanImpl implements ResponseMXBean {
   private volatile ResponseStatistics responseStatistics;

   public void updateResponseStatistics(ResponseStatistics responseStatistics) {
      this.responseStatistics = responseStatistics;
   }

   @Override
   public Map<Integer, Long> getResponseCodesToCountMap() {
      return this.responseStatistics.getResponseCodes();
   }

   @Override
   public Integer getLastResponseCode() {
      return this.responseStatistics.getLastResponseCode();
   }
}
