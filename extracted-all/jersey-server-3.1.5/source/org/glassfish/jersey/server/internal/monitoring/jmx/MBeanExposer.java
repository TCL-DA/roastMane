package org.glassfish.jersey.server.internal.monitoring.jmx;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.ProcessingException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.monitoring.ApplicationInfo;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import org.glassfish.jersey.server.monitoring.MonitoringStatisticsListener;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;
import org.glassfish.jersey.server.spi.Container;

public class MBeanExposer extends AbstractContainerLifecycleListener implements MonitoringStatisticsListener {
   private static final Logger LOGGER = Logger.getLogger(MBeanExposer.class.getName());
   private static final String PROPERTY_SUBTYPE_GLOBAL = "Global";
   static final String PROPERTY_EXECUTION_TIMES_REQUESTS = "RequestTimes";
   static final String PROPERTY_EXECUTION_TIMES_METHODS = "MethodTimes";
   private volatile ExecutionStatisticsDynamicBean requestMBean;
   private volatile ResponseMXBeanImpl responseMXBean;
   private volatile ResourcesMBeanGroup uriStatsGroup;
   private volatile ResourcesMBeanGroup resourceClassStatsGroup;
   private volatile ExceptionMapperMXBeanImpl exceptionMapperMXBean;
   private final AtomicBoolean destroyed = new AtomicBoolean(false);
   private final Object LOCK = new Object();
   private volatile String domain;
   @Inject
   private Provider<ApplicationInfo> applicationInfoProvider;

   private Map<String, ResourceStatistics> transformToStringKeys(Map<Class<?>, ResourceStatistics> stats) {
      Map<String, ResourceStatistics> newMap = new HashMap<>();

      for (Entry<Class<?>, ResourceStatistics> entry : stats.entrySet()) {
         newMap.put(entry.getKey().getName(), entry.getValue());
      }

      return newMap;
   }

   static String convertToObjectName(String name, boolean isUri) {
      if (!isUri) {
         return name;
      } else {
         String str = name.replace("\\", "\\\\");
         str = str.replace("?", "\\?");
         str = str.replace("*", "\\*");
         return "\"" + str + "\"";
      }
   }

   void registerMBean(Object mbean, String namePostfix) {
      MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
      String name = this.domain + namePostfix;

      try {
         synchronized (this.LOCK) {
            if (!this.destroyed.get()) {
               ObjectName objectName = new ObjectName(name);
               if (mBeanServer.isRegistered(objectName)) {
                  LOGGER.log(Level.WARNING, LocalizationMessages.WARNING_MONITORING_MBEANS_BEAN_ALREADY_REGISTERED(objectName));
                  mBeanServer.unregisterMBean(objectName);
               }

               mBeanServer.registerMBean(mbean, objectName);
            }
         }
      } catch (JMException var9) {
         throw new ProcessingException(LocalizationMessages.ERROR_MONITORING_MBEANS_REGISTRATION(name), var9);
      }
   }

   private void unregisterJerseyMBeans(boolean destroy) {
      MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

      try {
         synchronized (this.LOCK) {
            if (destroy) {
               this.destroyed.set(true);
            }

            if (this.domain != null) {
               for (ObjectName name : mBeanServer.queryNames(new ObjectName(this.domain + ",*"), null)) {
                  mBeanServer.unregisterMBean(name);
               }
            }
         }
      } catch (Exception var9) {
         throw new ProcessingException(LocalizationMessages.ERROR_MONITORING_MBEANS_UNREGISTRATION_DESTROY(), var9);
      }
   }

   @Override
   public void onStatistics(MonitoringStatistics statistics) {
      if (this.domain == null) {
         String globalSubType = ",subType=Global";
         ApplicationInfo appStats = (ApplicationInfo)this.applicationInfoProvider.get();
         String appName = appStats.getResourceConfig().getApplicationName();
         if (appName == null) {
            appName = "App_" + Integer.toHexString(appStats.getResourceConfig().hashCode());
         }

         this.domain = "org.glassfish.jersey:type=" + appName;
         this.unregisterJerseyMBeans(false);
         this.uriStatsGroup = new ResourcesMBeanGroup(statistics.getUriStatistics(), true, this, ",subType=Uris");
         Map<String, ResourceStatistics> newMap = this.transformToStringKeys(statistics.getResourceClassStatistics());
         this.resourceClassStatsGroup = new ResourcesMBeanGroup(newMap, false, this, ",subType=Resources");
         this.responseMXBean = new ResponseMXBeanImpl();
         this.registerMBean(this.responseMXBean, ",subType=Global,global=Responses");
         this.requestMBean = new ExecutionStatisticsDynamicBean(statistics.getRequestStatistics(), this, ",subType=Global", "AllRequestTimes");
         this.exceptionMapperMXBean = new ExceptionMapperMXBeanImpl(statistics.getExceptionMapperStatistics(), this, ",subType=Global");
         new ApplicationMXBeanImpl(appStats, this, ",subType=Global");
      }

      this.requestMBean.updateExecutionStatistics(statistics.getRequestStatistics());
      this.uriStatsGroup.updateResourcesStatistics(statistics.getUriStatistics());
      this.responseMXBean.updateResponseStatistics(statistics.getResponseStatistics());
      this.exceptionMapperMXBean.updateExceptionMapperStatistics(statistics.getExceptionMapperStatistics());
      this.resourceClassStatsGroup.updateResourcesStatistics(this.transformToStringKeys(statistics.getResourceClassStatistics()));
   }

   @Override
   public void onShutdown(Container container) {
      this.unregisterJerseyMBeans(true);
   }
}
