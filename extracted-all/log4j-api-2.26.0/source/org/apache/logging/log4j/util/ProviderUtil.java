package org.apache.logging.log4j.util;

import aQute.bnd.annotation.spi.ServiceConsumer;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.simple.internal.SimpleProvider;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;

@InternalApi
@ServiceConsumer(
   value = Provider.class,
   resolution = "optional",
   cardinality = "multiple"
)
public final class ProviderUtil {
   static final String PROVIDER_RESOURCE = "META-INF/log4j-provider.properties";
   static final Collection<Provider> PROVIDERS = new HashSet<>();
   static final Lock STARTUP_LOCK = new ReentrantLock();
   private static final String[] COMPATIBLE_API_VERSIONS = new String[]{"2.6.0"};
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static volatile Provider PROVIDER;

   private ProviderUtil() {
   }

   static void addProvider(final Provider provider) {
      if (validVersion(provider.getVersions())) {
         PROVIDERS.add(provider);
         LOGGER.debug("Loaded provider:\n{}", provider);
      } else {
         LOGGER.warn("Ignoring provider for incompatible version {}:\n{}", provider.getVersions(), provider);
      }
   }

   @SuppressFBWarnings(
      value = {"URLCONNECTION_SSRF_FD"},
      justification = "Uses a fixed URL that ends in 'META-INF/log4j-provider.properties'."
   )
   static void loadProvider(final URL url, final ClassLoader cl) {
      try {
         Properties props = PropertiesUtil.loadClose(url.openStream(), url);
         addProvider(new Provider(props, url, cl));
      } catch (IOException var3) {
         LOGGER.error("Unable to open {}", url, var3);
      }
   }

   @Deprecated
   static void loadProviders(final Enumeration<URL> urls, final ClassLoader cl) {
      if (urls != null) {
         while (urls.hasMoreElements()) {
            loadProvider(urls.nextElement(), cl);
         }
      }
   }

   public static Provider getProvider() {
      lazyInit();
      return PROVIDER;
   }

   public static Iterable<Provider> getProviders() {
      lazyInit();
      return PROVIDERS;
   }

   public static boolean hasProviders() {
      lazyInit();
      return !PROVIDERS.isEmpty();
   }

   static void lazyInit() {
      if (PROVIDER == null) {
         try {
            STARTUP_LOCK.lockInterruptibly();

            try {
               if (PROVIDER == null) {
                  ServiceLoaderUtil.safeStream(Provider.class, ServiceLoader.load(Provider.class, ProviderUtil.class.getClassLoader()), LOGGER)
                     .filter(provider -> validVersion(provider.getVersions()))
                     .forEach(ProviderUtil::addProvider);

                  for (LoaderUtil.UrlResource resource : LoaderUtil.findUrlResources("META-INF/log4j-provider.properties", false)) {
                     loadProvider(resource.getUrl(), resource.getClassLoader());
                  }

                  PROVIDER = selectProvider(PropertiesUtil.getProperties(), PROVIDERS, LOGGER);
               }
            } finally {
               STARTUP_LOCK.unlock();
            }
         } catch (InterruptedException var6) {
            LOGGER.fatal("Interrupted before Log4j Providers could be loaded.", (Throwable)var6);
            Thread.currentThread().interrupt();
         }
      }
   }

   static Provider selectProvider(final PropertiesUtil properties, final Collection<Provider> providers, final Logger statusLogger) {
      Provider selected = null;
      String providerClass = properties.getStringProperty("log4j.provider");
      if (providerClass != null) {
         if (SimpleProvider.class.getName().equals(providerClass)) {
            selected = new SimpleProvider();
         } else {
            try {
               selected = LoaderUtil.newInstanceOf(providerClass);
            } catch (Exception var9) {
               statusLogger.error("Unable to create provider {}.\nFalling back to default selection process.", PROVIDER, var9);
            }
         }
      }

      String factoryClassName = properties.getStringProperty("log4j2.loggerContextFactory");
      if (factoryClassName != null) {
         if (selected != null) {
            statusLogger.warn("Ignoring {} system property, since {} was set.", "log4j2.loggerContextFactory", "log4j.provider");
         } else {
            statusLogger.warn("Usage of the {} property is deprecated. Use the {} property instead.", "log4j2.loggerContextFactory", "log4j.provider");

            for (Provider provider : providers) {
               if (factoryClassName.equals(provider.getClassName())) {
                  selected = provider;
                  break;
               }
            }
         }

         if (selected == null) {
            statusLogger.warn("No provider found using {} as logger context factory. The factory will be instantiated directly.", factoryClassName);

            try {
               Class<?> clazz = LoaderUtil.loadClass(factoryClassName);
               if (LoggerContextFactory.class.isAssignableFrom(clazz)) {
                  selected = new Provider(null, "", clazz.asSubclass(LoggerContextFactory.class));
               } else {
                  statusLogger.error(
                     "Class {} specified in the {} system property does not extend {}",
                     factoryClassName,
                     "log4j2.loggerContextFactory",
                     LoggerContextFactory.class.getName()
                  );
               }
            } catch (Exception var8) {
               statusLogger.error("Unable to create class {} specified in the {} system property", factoryClassName, "log4j2.loggerContextFactory", var8);
            }
         }
      }

      if (selected == null) {
         Comparator<Provider> comparator = Comparator.comparing(Provider::getPriority);
         switch (providers.size()) {
            case 0:
               statusLogger.error("Log4j API could not find a logging provider.");
            case 1:
               break;
            default:
               statusLogger.warn(
                  providers.stream()
                     .sorted(comparator)
                     .map(Provider::toString)
                     .collect(Collectors.joining("\n", "Log4j API found multiple logging providers:\n", ""))
               );
         }

         selected = providers.stream().max(comparator).orElseGet(SimpleProvider::new);
      }

      statusLogger.info("Using provider:\n{}", selected);
      return selected;
   }

   public static ClassLoader findClassLoader() {
      return LoaderUtil.getThreadContextClassLoader();
   }

   private static boolean validVersion(final String version) {
      for (String v : COMPATIBLE_API_VERSIONS) {
         if (version.startsWith(v)) {
            return true;
         }
      }

      return false;
   }
}
