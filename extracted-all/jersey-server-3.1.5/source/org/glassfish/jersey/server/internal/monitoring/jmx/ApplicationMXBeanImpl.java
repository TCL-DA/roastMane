package org.glassfish.jersey.server.internal.monitoring.jmx;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.monitoring.ApplicationInfo;
import org.glassfish.jersey.server.monitoring.ApplicationMXBean;

public class ApplicationMXBeanImpl implements ApplicationMXBean {
   private final String applicationName;
   private final String applicationClass;
   private final Map<String, String> configurationProperties;
   private final Date startTime;
   private final Set<String> providers = new HashSet<>();
   private final Set<String> registeredClasses = new HashSet<>();
   private final Set<String> registeredInstances = new HashSet<>();

   public ApplicationMXBeanImpl(ApplicationInfo applicationInfo, MBeanExposer mBeanExposer, String parentName) {
      for (Class<?> provider : applicationInfo.getProviders()) {
         this.providers.add(provider.getName());
      }

      for (Class<?> registeredClass : applicationInfo.getRegisteredClasses()) {
         this.registeredClasses.add(registeredClass.toString());
      }

      for (Object registeredInstance : applicationInfo.getRegisteredInstances()) {
         this.registeredInstances.add(registeredInstance.getClass().getName());
      }

      ResourceConfig resourceConfig = applicationInfo.getResourceConfig();
      this.applicationName = resourceConfig.getApplicationName();
      this.applicationClass = resourceConfig.getApplication().getClass().getName();
      this.configurationProperties = new HashMap<>();

      for (Entry<String, Object> entry : resourceConfig.getProperties().entrySet()) {
         Object value = entry.getValue();

         String stringValue;
         try {
            stringValue = value == null ? "[null]" : value.toString();
         } catch (Exception var10) {
            stringValue = LocalizationMessages.PROPERTY_VALUE_TOSTRING_THROWS_EXCEPTION(var10.getClass().getName(), var10.getMessage());
         }

         this.configurationProperties.put(entry.getKey(), stringValue);
      }

      this.startTime = new Date(applicationInfo.getStartTime().getTime());
      mBeanExposer.registerMBean(this, parentName + ",global=Configuration");
   }

   @Override
   public String getApplicationName() {
      return this.applicationName;
   }

   @Override
   public String getApplicationClass() {
      return this.applicationClass;
   }

   @Override
   public Map<String, String> getProperties() {
      return this.configurationProperties;
   }

   @Override
   public Date getStartTime() {
      return this.startTime;
   }

   @Override
   public Set<String> getRegisteredClasses() {
      return this.registeredClasses;
   }

   @Override
   public Set<String> getRegisteredInstances() {
      return this.registeredInstances;
   }

   @Override
   public Set<String> getProviderClasses() {
      return this.providers;
   }
}
