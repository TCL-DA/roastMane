package jakarta.activation;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

class FactoryFinder {
   private static final Logger logger = Logger.getLogger("jakarta.activation");
   private static final ServiceLoaderUtil.ExceptionHandler<RuntimeException> EXCEPTION_HANDLER = new ServiceLoaderUtil.ExceptionHandler<RuntimeException>() {
      public RuntimeException createException(Throwable throwable, String message) {
         return new RuntimeException(message, throwable);
      }
   };
   private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "org.glassfish.hk2.osgiresourcelocator.ServiceLoader";

   static <T> T find(Class<T> factoryClass, String defaultClassName, boolean tryFallback) throws RuntimeException {
      ClassLoader tccl = ServiceLoaderUtil.contextClassLoader(EXCEPTION_HANDLER);
      String factoryId = factoryClass.getName();
      String className = fromSystemProperty(factoryId);
      if (className != null) {
         T result = newInstance(className, defaultClassName, tccl);
         if (result != null) {
            return result;
         }
      }

      T factory = ServiceLoaderUtil.firstByServiceLoader(factoryClass, logger, EXCEPTION_HANDLER);
      if (factory != null) {
         return factory;
      } else {
         if (isOsgi()) {
            T result = lookupUsingOSGiServiceLoader(factoryId);
            if (result != null) {
               return result;
            }
         }

         if (!tryFallback) {
            throw new RuntimeException("Provider for " + factoryId + " cannot be found", null);
         } else if (defaultClassName == null) {
            throw new RuntimeException("Provider for " + factoryId + " cannot be found", null);
         } else {
            return newInstance(defaultClassName, defaultClassName, tccl);
         }
      }
   }

   private static <T> T newInstance(String className, String defaultClassName, ClassLoader tccl) throws RuntimeException {
      return ServiceLoaderUtil.newInstance(className, defaultClassName, tccl, EXCEPTION_HANDLER);
   }

   private static String fromSystemProperty(String factoryId) {
      return getSystemProperty(factoryId);
   }

   private static String getSystemProperty(final String property) {
      logger.log(Level.FINE, "Checking system property {0}", property);
      String value = AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(property);
         }
      });
      logFound(value);
      return value;
   }

   private static void logFound(String value) {
      if (value != null) {
         logger.log(Level.FINE, "  found {0}", value);
      } else {
         logger.log(Level.FINE, "  not found");
      }
   }

   private static boolean isOsgi() {
      try {
         Class.forName("org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   private static <T> T lookupUsingOSGiServiceLoader(String factoryId) {
      try {
         Class<?> serviceClass = Class.forName(factoryId);
         Class<?>[] args = new Class[]{serviceClass};
         Class<?> target = Class.forName("org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
         Method m = target.getMethod("lookupProviderInstances", Class.class);
         Iterator<?> iter = ((Iterable)m.invoke(null, args)).iterator();
         return (T)(iter.hasNext() ? iter.next() : null);
      } catch (Exception var6) {
         return null;
      }
   }
}
