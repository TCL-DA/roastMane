package org.glassfish.jersey.server.internal.monitoring.jmx;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;

public class ResourcesMBeanGroup {
   private final Map<String, ResourceMxBeanImpl> exposedResourceMBeans = new HashMap<>();
   private final String parentName;
   private final boolean uriResource;
   private final MBeanExposer exposer;

   public ResourcesMBeanGroup(Map<String, ResourceStatistics> resourceStatistics, boolean uriResource, MBeanExposer mBeanExposer, String parentName) {
      this.uriResource = uriResource;
      this.exposer = mBeanExposer;
      this.parentName = parentName;
      this.updateResourcesStatistics(resourceStatistics);
   }

   public void updateResourcesStatistics(Map<String, ResourceStatistics> resourceStatistics) {
      for (Entry<String, ResourceStatistics> entry : resourceStatistics.entrySet()) {
         ResourceMxBeanImpl resourceMxBean = this.exposedResourceMBeans.get(entry.getKey());
         if (resourceMxBean == null) {
            resourceMxBean = new ResourceMxBeanImpl(entry.getValue(), entry.getKey(), this.uriResource, this.exposer, this.parentName);
            this.exposedResourceMBeans.put(entry.getKey(), resourceMxBean);
         }

         resourceMxBean.updateResourceStatistics(entry.getValue());
      }
   }
}
