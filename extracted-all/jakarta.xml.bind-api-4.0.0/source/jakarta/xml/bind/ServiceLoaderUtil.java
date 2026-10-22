package jakarta.xml.bind;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

class ServiceLoaderUtil {
   private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "org.glassfish.hk2.osgiresourcelocator.ServiceLoader";
   private static final String OSGI_SERVICE_LOADER_METHOD_NAME = "lookupProviderClasses";

   static <P, T extends Exception> P firstByServiceLoader(Class<P> spiClass, Logger logger, ServiceLoaderUtil.ExceptionHandler<T> handler) throws T {
      try {
         ServiceLoader<P> serviceLoader = ServiceLoader.load(spiClass);
         Iterator var4 = serviceLoader.iterator();
         if (var4.hasNext()) {
            P impl = (P)var4.next();
            logger.log(Level.FINE, "ServiceProvider loading Facility used; returning object [{0}]", impl.getClass().getName());
            return impl;
         } else {
            return null;
         }
      } catch (Throwable var6) {
         throw handler.createException(var6, "Error while searching for service [" + spiClass.getName() + "]");
      }
   }

   static <T> T lookupUsingOSGiServiceLoader(String factoryId, Logger logger) {
      try {
         Class<? extends T> serviceClass = (Class<? extends T>)Class.forName(factoryId);
         Class<?> target = Class.forName("org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
         Method m = target.getMethod("lookupProviderClasses", Class.class);
         Iterator<? extends T> iter = ((Iterable)m.invoke(null, serviceClass)).iterator();
         if (iter.hasNext()) {
            T next = (T)iter.next();
            logger.log(Level.FINE, "Found implementation using OSGi facility; returning object [{0}].", next.getClass().getName());
            return next;
         } else {
            return null;
         }
      } catch (InvocationTargetException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException var7) {
         logger.log(Level.FINE, "Unable to find from OSGi: [" + factoryId + "]", (Throwable)var7);
         return null;
      }
   }

   static <T> Iterable<T> lookupsUsingOSGiServiceLoader(String factoryId, Logger logger) {
      try {
         return (Iterable<T>)Class.forName("org.glassfish.hk2.osgiresourcelocator.ServiceLoader")
            .getMethod("lookupProviderClasses", Class.class)
            .invoke(null, Class.forName(factoryId));
      } catch (InvocationTargetException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException var3) {
         logger.log(Level.FINE, var3, () -> "Unable to find from OSGi: [" + factoryId + "]");
         return null;
      }
   }

   static void checkPackageAccess(String className) {
      SecurityManager s = System.getSecurityManager();
      if (s != null) {
         int i = className.lastIndexOf(46);
         if (i != -1) {
            s.checkPackageAccess(className.substring(0, i));
         }
      }
   }

   static Class<?> nullSafeLoadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
      return classLoader == null ? Class.forName(className) : classLoader.loadClass(className);
   }

   static <T extends Exception> Object newInstance(String className, String defaultImplClassName, ServiceLoaderUtil.ExceptionHandler<T> handler) throws T {
      try {
         return safeLoadClass(className, defaultImplClassName, contextClassLoader(handler)).getConstructor().newInstance();
      } catch (ClassNotFoundException var4) {
         throw handler.createException(var4, "Provider " + className + " not found");
      } catch (Exception var5) {
         throw handler.createException(var5, "Provider " + className + " could not be instantiated: " + var5);
      }
   }

   static Class<?> safeLoadClass(String className, String defaultImplClassName, ClassLoader classLoader) throws ClassNotFoundException {
      try {
         checkPackageAccess(className);
      } catch (SecurityException var4) {
         if (defaultImplClassName != null && defaultImplClassName.equals(className)) {
            return Class.forName(className);
         }

         throw var4;
      }

      return nullSafeLoadClass(className, classLoader);
   }

   static <T extends Exception> ClassLoader contextClassLoader(ServiceLoaderUtil.ExceptionHandler<T> exceptionHandler) throws T {
      try {
         return Thread.currentThread().getContextClassLoader();
      } catch (Exception var2) {
         throw exceptionHandler.createException(var2, var2.toString());
      }
   }

   abstract static class ExceptionHandler<T extends Exception> {
      public abstract T createException(Throwable var1, String var2);
   }
}
