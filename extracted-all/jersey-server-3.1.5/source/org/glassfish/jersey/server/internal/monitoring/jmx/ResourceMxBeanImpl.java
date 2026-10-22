package org.glassfish.jersey.server.internal.monitoring.jmx;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.glassfish.jersey.server.internal.monitoring.MonitoringUtils;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.ResourceMXBean;
import org.glassfish.jersey.server.monitoring.ResourceMethodStatistics;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;

public class ResourceMxBeanImpl implements ResourceMXBean {
   private final String name;
   private volatile ExecutionStatisticsDynamicBean methodsExecutionStatisticsBean;
   private volatile ExecutionStatisticsDynamicBean requestExecutionStatisticsBean;
   private final Map<String, ResourceMethodMXBeanImpl> resourceMethods = new HashMap<>();
   private final String resourcePropertyName;
   private final boolean uriResource;
   private final MBeanExposer mBeanExposer;

   public ResourceMxBeanImpl(ResourceStatistics resourceStatistics, String name, boolean uriResource, MBeanExposer mBeanExposer, String parentName) {
      this.name = name;
      this.uriResource = uriResource;
      this.mBeanExposer = mBeanExposer;
      this.resourcePropertyName = parentName + ",resource=" + MBeanExposer.convertToObjectName(name, uriResource);
      mBeanExposer.registerMBean(this, this.resourcePropertyName);
      this.methodsExecutionStatisticsBean = new ExecutionStatisticsDynamicBean(
         resourceStatistics.getResourceMethodExecutionStatistics(), mBeanExposer, this.resourcePropertyName, "MethodTimes"
      );
      this.requestExecutionStatisticsBean = new ExecutionStatisticsDynamicBean(
         resourceStatistics.getRequestExecutionStatistics(), mBeanExposer, this.resourcePropertyName, "RequestTimes"
      );
      this.updateResourceStatistics(resourceStatistics);
   }

   public void updateResourceStatistics(ResourceStatistics resourceStatistics) {
      this.methodsExecutionStatisticsBean.updateExecutionStatistics(resourceStatistics.getResourceMethodExecutionStatistics());
      this.requestExecutionStatisticsBean.updateExecutionStatistics(resourceStatistics.getRequestExecutionStatistics());

      for (Entry<ResourceMethod, ResourceMethodStatistics> entry : resourceStatistics.getResourceMethodStatistics().entrySet()) {
         ResourceMethodStatistics methodStats = entry.getValue();
         ResourceMethod method = entry.getKey();
         String methodId = MonitoringUtils.getMethodUniqueId(method);
         ResourceMethodMXBeanImpl methodMXBean = this.resourceMethods.get(methodId);
         if (methodMXBean == null) {
            methodMXBean = new ResourceMethodMXBeanImpl(methodStats, this.uriResource, this.mBeanExposer, this.resourcePropertyName, methodId);
            this.resourceMethods.put(methodId, methodMXBean);
         }

         methodMXBean.updateResourceMethodStatistics(methodStats);
      }
   }

   @Override
   public String getName() {
      return this.name;
   }
}
