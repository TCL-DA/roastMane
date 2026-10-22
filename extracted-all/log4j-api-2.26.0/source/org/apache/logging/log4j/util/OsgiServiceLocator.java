package org.apache.logging.log4j.util;

import java.lang.invoke.MethodHandles.Lookup;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleRevision;

public class OsgiServiceLocator {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final boolean OSGI_AVAILABLE = checkOsgiAvailable();

   private static boolean checkOsgiAvailable() {
      try {
         Class<?> clazz = Class.forName("org.osgi.framework.FrameworkUtil");
         return clazz.getMethod("getBundle", Class.class).invoke(null, OsgiServiceLocator.class) != null;
      } catch (NoSuchMethodException | LinkageError | ClassNotFoundException var1) {
         return false;
      } catch (Throwable var2) {
         LOGGER.error("Unknown error checking OSGI environment.", var2);
         return false;
      }
   }

   public static boolean isAvailable() {
      return OSGI_AVAILABLE;
   }

   public static <T> Stream<T> loadServices(final Class<T> serviceType, final Lookup lookup) {
      return loadServices(serviceType, lookup, true);
   }

   public static <T> Stream<T> loadServices(final Class<T> serviceType, final Lookup lookup, final boolean verbose) {
      Class<?> lookupClass = Objects.requireNonNull(lookup, "lookup").lookupClass();
      return loadServices(serviceType, lookupClass, StatusLogger.getLogger());
   }

   static <T> Stream<T> loadServices(final Class<T> serviceType, final Class<?> callerClass, final Logger logger) {
      Bundle bundle = FrameworkUtil.getBundle(callerClass);
      if (bundle != null && !isFragment(bundle)) {
         BundleContext ctx = bundle.getBundleContext();
         if (ctx == null) {
            logger.warn(
               "Unable to load OSGi services for service {}: bundle {} (state {}) does not have a valid BundleContext",
               serviceType::getName,
               bundle::getSymbolicName,
               () -> {
                  switch (bundle.getState()) {
                     case 1:
                        return "UNINSTALLED";
                     case 2:
                        return "INSTALLED";
                     case 4:
                        return "RESOLVED";
                     case 8:
                        return "STARTING";
                     case 16:
                        return "STOPPING";
                     case 32:
                        return "ACTIVE";
                     default:
                        return "UNKNOWN";
                  }
               }
            );
         } else {
            try {
               return ctx.getServiceReferences(serviceType, null).stream().map(ctx::getService);
            } catch (Exception var6) {
               logger.error("Unable to load OSGI services for service {}", serviceType, var6);
            }
         }
      }

      return Stream.empty();
   }

   private static boolean isFragment(final Bundle bundle) {
      try {
         return (((BundleRevision)bundle.adapt(BundleRevision.class)).getTypes() & 1) != 0;
      } catch (SecurityException var2) {
         return false;
      }
   }
}
