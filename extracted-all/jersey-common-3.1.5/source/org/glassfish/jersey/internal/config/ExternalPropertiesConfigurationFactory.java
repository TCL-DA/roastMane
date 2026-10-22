package org.glassfish.jersey.internal.config;

import jakarta.ws.rs.core.Configurable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import org.glassfish.jersey.JerseyPriorities;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.spi.ExternalConfigurationModel;
import org.glassfish.jersey.spi.ExternalConfigurationProvider;

public class ExternalPropertiesConfigurationFactory {
   private static final List<ExternalConfigurationProvider> EXTERNAL_CONFIGURATION_PROVIDERS = getExternalConfigurations();

   static Map<String, Object> readExternalPropertiesMap() {
      return readExternalPropertiesMap(EXTERNAL_CONFIGURATION_PROVIDERS);
   }

   private static Map<String, Object> readExternalPropertiesMap(List<ExternalConfigurationProvider> externalConfigProviders) {
      ExternalConfigurationProvider provider = mergeConfigs(externalConfigProviders);
      return provider == null ? Collections.emptyMap() : provider.getProperties();
   }

   public static boolean configure(Configurable config) {
      return configure((k, v) -> config.property(k, v), EXTERNAL_CONFIGURATION_PROVIDERS);
   }

   public static boolean configure(BiConsumer<String, Object> config, List<ExternalConfigurationProvider> externalConfigurationProviders) {
      if (config instanceof ExternalConfigurationModel) {
         return false;
      } else {
         Map<String, Object> properties = readExternalPropertiesMap(externalConfigurationProviders);
         properties.forEach((k, v) -> config.accept(k, v));
         return true;
      }
   }

   static ExternalConfigurationModel getConfig() {
      ExternalConfigurationProvider provider = mergeConfigs(getExternalConfigurations());
      return provider == null ? null : provider.getConfiguration();
   }

   private static List<ExternalConfigurationProvider> getExternalConfigurations() {
      List<ExternalConfigurationProvider> providers = new ArrayList<>();
      ServiceFinder<ExternalConfigurationProvider> finder = ServiceFinder.find(ExternalConfigurationProvider.class);
      if (finder.iterator().hasNext()) {
         finder.forEach(providers::add);
      } else {
         providers.add(new SystemPropertiesConfigurationProvider());
      }

      return providers;
   }

   private static ExternalConfigurationProvider mergeConfigs(List<ExternalConfigurationProvider> configurations) {
      Set<ExternalConfigurationProvider> orderedConfigurations = orderConfigs(configurations);
      Iterator<ExternalConfigurationProvider> configurationIterator = orderedConfigurations.iterator();
      if (!configurationIterator.hasNext()) {
         return null;
      } else {
         ExternalConfigurationProvider firstConfig = configurationIterator.next();

         while (configurationIterator.hasNext()) {
            ExternalConfigurationProvider nextConfig = configurationIterator.next();
            firstConfig.merge(nextConfig.getConfiguration());
         }

         return firstConfig;
      }
   }

   private static Set<ExternalConfigurationProvider> orderConfigs(List<ExternalConfigurationProvider> configurations) {
      SortedSet<ExternalConfigurationProvider> sortedSet = new TreeSet<>(new ExternalPropertiesConfigurationFactory.ConfigComparator());
      sortedSet.addAll(configurations);
      return Collections.unmodifiableSortedSet(sortedSet);
   }

   private static class ConfigComparator implements Comparator<ExternalConfigurationProvider> {
      private ConfigComparator() {
      }

      public int compare(ExternalConfigurationProvider config1, ExternalConfigurationProvider config2) {
         int priority1 = JerseyPriorities.getPriorityValue(config1.getClass(), 5000);
         int priority2 = JerseyPriorities.getPriorityValue(config2.getClass(), 5000);
         return priority1 == priority2 ? config1.getClass().getName().compareTo(config2.getClass().getName()) : Integer.compare(priority1, priority2);
      }
   }
}
