package jakarta.ws.rs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

final class FactoryFinder {
   private static final Logger LOGGER = Logger.getLogger(FactoryFinder.class.getName());

   private FactoryFinder() {
   }

   static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null
         ? Thread.currentThread().getContextClassLoader()
         : AccessController.doPrivileged((PrivilegedAction<ClassLoader>)(() -> {
            ClassLoader cl = null;

            try {
               cl = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException var2) {
               LOGGER.log(Level.WARNING, "Unable to get context classloader instance.", (Throwable)var2);
            }

            return cl;
         }));
   }

   private static Object newInstance(String className, ClassLoader classLoader) throws ClassNotFoundException {
      try {
         Class<?> spiClass;
         if (classLoader == null) {
            spiClass = Class.forName(className);
         } else {
            try {
               spiClass = Class.forName(className, false, classLoader);
            } catch (ClassNotFoundException var4) {
               LOGGER.log(
                  Level.FINE,
                  "Unable to load provider class "
                     + className
                     + " using custom classloader "
                     + classLoader.getClass().getName()
                     + " trying again with current classloader.",
                  (Throwable)var4
               );
               spiClass = Class.forName(className);
            }
         }

         return spiClass.getDeclaredConstructor().newInstance();
      } catch (ClassNotFoundException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new ClassNotFoundException("Provider " + className + " could not be instantiated: " + var6, var6);
      }
   }

   static <T> Object find(String factoryId, Class<T> service) throws ClassNotFoundException {
      ClassLoader classLoader = getContextClassLoader();
      Object result = findFirstService(factoryId, classLoader, service);
      if (result != null) {
         return result;
      } else {
         result = findFirstService(factoryId, getClassLoader(), service);
         if (result != null) {
            return result;
         } else {
            FileInputStream inputStream = null;
            String configFile = null;

            try {
               String javah = System.getProperty("java.home");
               configFile = javah + File.separator + "lib" + File.separator + "jaxrs.properties";
               File f = new File(configFile);
               if (f.exists()) {
                  Properties props = new Properties();
                  inputStream = new FileInputStream(f);
                  props.load(inputStream);
                  String factoryClassName = props.getProperty(factoryId);
                  return newInstance(factoryClassName, classLoader);
               }
            } catch (Exception var23) {
               LOGGER.log(Level.FINER, "Failed to load service " + factoryId + " from $java.home/lib/jaxrs.properties", (Throwable)var23);
            } finally {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (IOException var21) {
                     LOGGER.log(Level.FINER, String.format("Error closing %s file.", configFile), (Throwable)var21);
                  }
               }
            }

            try {
               String systemProp = System.getProperty(factoryId);
               if (systemProp != null) {
                  return newInstance(systemProp, classLoader);
               }
            } catch (SecurityException var22) {
               LOGGER.log(Level.FINER, "Failed to load service " + factoryId + " from a system property", (Throwable)var22);
            }

            throw new ClassNotFoundException("Provider for " + factoryId + " cannot be found", null);
         }
      }
   }

   private static ClassLoader getClassLoader() {
      return System.getSecurityManager() == null ? FactoryFinder.class.getClassLoader() : AccessController.doPrivileged(FactoryFinder.class::getClassLoader);
   }

   private static <T> T findFirstService(String factoryId, ClassLoader cl, Class<T> service) {
      PrivilegedAction<T> action = () -> {
         try {
            ServiceLoader<T> loader = ServiceLoader.load(service, cl);
            if (loader.iterator().hasNext()) {
               return loader.iterator().next();
            }
         } catch (Exception var4) {
            LOGGER.log(Level.FINER, "Failed to load service " + factoryId + ".", (Throwable)var4);
         }

         return null;
      };
      return System.getSecurityManager() == null ? action.run() : AccessController.doPrivileged(action);
   }
}
