package org.apache.logging.log4j.core.config.plugins.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Strings;

public class PluginManager {
   private static final CopyOnWriteArrayList<String> PACKAGES = new CopyOnWriteArrayList<>();
   private static final String LOG4J_PACKAGES = "org.apache.logging.log4j.core";
   private static final String DEPRECATION_WARNING = "The use of package scanning to locate Log4j plugins is deprecated.\nPlease remove the deprecated `{}` method call from `{}`.\nSee https://logging.apache.org/log4j/2.x/faq.html#package-scanning for details.";
   private static final String PLUGIN_DESCRIPTOR_DOC = "See https://logging.apache.org/log4j/2.x/faq.html#plugin-descriptors for details.";
   private static final String PLUGIN_REGISTRY_DOC = "See https://logging.apache.org/log4j/2.x/manual/plugins.html#plugin-registry for details.";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private Map<String, PluginType<?>> plugins = new HashMap<>();
   private final String category;

   public PluginManager(final String category) {
      this.category = category;
   }

   @Deprecated
   public static void main(final String[] args) {
      System.err.println("ERROR: this tool is superseded by the annotation processor included in log4j-core.");
      System.err.println("If the annotation processor does not work for you, please see the manual page:");
      System.err.println("https://logging.apache.org/log4j/2.x/manual/configuration.html#ConfigurationSyntax");
      System.exit(-1);
   }

   @Deprecated
   public static void addPackage(final String p) {
      LOGGER.warn(
         "The use of package scanning to locate Log4j plugins is deprecated.\nPlease remove the deprecated `{}` method call from `{}`.\nSee https://logging.apache.org/log4j/2.x/faq.html#package-scanning for details.",
         "PluginManager.addPackage()",
         StackLocatorUtil.getStackTraceElement(2)
      );
      if (!Strings.isBlank(p)) {
         PACKAGES.addIfAbsent(p);
      }
   }

   @Deprecated
   public static void addPackages(final Collection<String> packages) {
      LOGGER.warn(
         "The use of package scanning to locate Log4j plugins is deprecated.\nPlease remove the deprecated `{}` method call from `{}`.\nSee https://logging.apache.org/log4j/2.x/faq.html#package-scanning for details.",
         "PluginManager.addPackages()",
         StackLocatorUtil.getStackTraceElement(2)
      );

      for (String pkg : packages) {
         if (Strings.isNotBlank(pkg)) {
            PACKAGES.addIfAbsent(pkg);
         }
      }
   }

   static void clearPackages() {
      PACKAGES.clear();
   }

   public PluginType<?> getPluginType(final String name) {
      return this.plugins.get(Strings.toRootLowerCase(name));
   }

   public Map<String, PluginType<?>> getPlugins() {
      return this.plugins;
   }

   public void collectPlugins() {
      this.collectPlugins(null);
   }

   public void collectPlugins(final List<String> packages) {
      String categoryLowerCase = Strings.toRootLowerCase(this.category);
      Map<String, PluginType<?>> newPlugins = new LinkedHashMap<>();
      Map<String, List<PluginType<?>>> builtInPlugins = PluginRegistry.getInstance().loadFromMainClassLoader();
      if (builtInPlugins.isEmpty()) {
         LOGGER.warn(
            "No Log4j plugin descriptor was found in the classpath.\nFalling back to scanning the `{}` package.\n{}",
            "org.apache.logging.log4j.core",
            "See https://logging.apache.org/log4j/2.x/faq.html#plugin-descriptors for details."
         );
         builtInPlugins = PluginRegistry.getInstance().loadFromPackage("org.apache.logging.log4j.core");
      }

      mergeByName(newPlugins, builtInPlugins.get(categoryLowerCase), null);

      for (Map<String, List<PluginType<?>>> pluginsByCategory : PluginRegistry.getInstance().getPluginsByCategoryByBundleId().values()) {
         mergeByName(newPlugins, pluginsByCategory.get(categoryLowerCase), null);
      }

      List<String> scannedPluginClassNames = new ArrayList<>();

      for (String pkg : PACKAGES) {
         mergeByName(newPlugins, PluginRegistry.getInstance().loadFromPackage(pkg).get(categoryLowerCase), scannedPluginClassNames);
      }

      if (packages != null) {
         for (String pkg : packages) {
            mergeByName(newPlugins, PluginRegistry.getInstance().loadFromPackage(pkg).get(categoryLowerCase), scannedPluginClassNames);
         }
      }

      if (!scannedPluginClassNames.isEmpty()) {
         Predicate<String> customPluginPredicate = PluginManager::isCustomPlugin;
         String standardPlugins = scannedPluginClassNames.stream().filter(customPluginPredicate.negate()).collect(Collectors.joining("\n\t"));
         if (!standardPlugins.isEmpty()) {
            LOGGER.warn(
               "The Log4j plugin descriptors for the following `{}` plugins are missing:\n\t{}\n{}",
               this.category,
               standardPlugins,
               "See https://logging.apache.org/log4j/2.x/faq.html#plugin-descriptors for details."
            );
         }

         String customPlugins = scannedPluginClassNames.stream().filter(customPluginPredicate).collect(Collectors.joining("\n\t"));
         if (!customPlugins.isEmpty()) {
            LOGGER.warn(
               "Some custom `{}` Log4j plugins are not properly registered:\n\t{}\nPlease consider reporting this to the maintainers of these plugins.\n{}",
               this.category,
               customPlugins,
               "See https://logging.apache.org/log4j/2.x/manual/plugins.html#plugin-registry for details."
            );
         }
      }

      LOGGER.debug("PluginManager '{}' found {} plugins", this.category, newPlugins.size());
      this.plugins = newPlugins;
   }

   private static boolean isCustomPlugin(final String className) {
      return !className.startsWith("org.apache.logging.log4j") && !className.startsWith("org.apache.log4j");
   }

   private static void mergeByName(final Map<String, PluginType<?>> newPlugins, final List<PluginType<?>> plugins, final List<String> mergedPluginClassNames) {
      if (plugins != null) {
         for (PluginType<?> pluginType : plugins) {
            String key = pluginType.getKey();
            PluginType<?> existing = newPlugins.get(key);
            if (existing == null) {
               newPlugins.put(key, pluginType);
               if (mergedPluginClassNames != null) {
                  mergedPluginClassNames.add(pluginType.getPluginClass().getName());
               }
            } else if (!existing.getPluginClass().equals(pluginType.getPluginClass())) {
               LOGGER.warn("Plugin [{}] is already mapped to {}, ignoring {}", key, existing.getPluginClass(), pluginType.getPluginClass());
            }
         }
      }
   }
}
