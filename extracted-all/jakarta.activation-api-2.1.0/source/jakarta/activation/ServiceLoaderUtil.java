package jakarta.activation;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

class ServiceLoaderUtil {
   static <P, T extends Exception> P firstByServiceLoader(Class<P> spiClass, Logger logger, ServiceLoaderUtil.ExceptionHandler<T> handler) throws T {
      logger.log(Level.FINE, "Using java.util.ServiceLoader to find {0}", spiClass.getName());

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

   static void checkPackageAccess(String className) {
      SecurityManager s = System.getSecurityManager();
      if (s != null) {
         int i = className.lastIndexOf(46);
         if (i != -1) {
            s.checkPackageAccess(className.substring(0, i));
         }
      }
   }

   static <P> Class<P> nullSafeLoadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
      return (Class<P>)(classLoader == null ? Class.forName(className) : classLoader.loadClass(className));
   }

   static <P, T extends Exception> P newInstance(
      String className, String defaultImplClassName, ClassLoader classLoader, ServiceLoaderUtil.ExceptionHandler<T> handler
   ) throws T {
      try {
         Class<P> cls = safeLoadClass(className, defaultImplClassName, classLoader);
         return cls.getConstructor().newInstance();
      } catch (ClassNotFoundException var5) {
         throw handler.createException(var5, "Provider " + className + " not found");
      } catch (Exception var6) {
         throw handler.createException(var6, "Provider " + className + " could not be instantiated: " + var6);
      }
   }

   static <P> Class<P> safeLoadClass(String className, String defaultImplClassName, ClassLoader classLoader) throws ClassNotFoundException {
      try {
         checkPackageAccess(className);
      } catch (SecurityException var4) {
         if (defaultImplClassName != null && defaultImplClassName.equals(className)) {
            return (Class<P>)Class.forName(className);
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
