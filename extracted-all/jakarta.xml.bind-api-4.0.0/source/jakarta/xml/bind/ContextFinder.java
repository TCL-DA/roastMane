package jakarta.xml.bind;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

class ContextFinder {
   private static final Logger logger = Logger.getLogger("jakarta.xml.bind");
   static final String DEFAULT_FACTORY_CLASS = "org.glassfish.jaxb.runtime.v2.ContextFactory";
   private static ServiceLoaderUtil.ExceptionHandler<JAXBException> EXCEPTION_HANDLER;

   private static Throwable handleInvocationTargetException(InvocationTargetException x) throws JAXBException {
      Throwable t = x.getTargetException();
      if (t != null) {
         if (t instanceof JAXBException) {
            throw (JAXBException)t;
         } else if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
         } else if (t instanceof Error) {
            throw (Error)t;
         } else {
            return t;
         }
      } else {
         return x;
      }
   }

   private static JAXBException handleClassCastException(Class<?> originalType, Class<?> targetType) {
      URL targetTypeURL = which(targetType);
      return new JAXBException(
         Messages.format("JAXBContext.IllegalCast", getClassClassLoader(originalType).getResource("jakarta/xml/bind/JAXBContext.class"), targetTypeURL)
      );
   }

   static JAXBContext newInstance(String contextPath, Class<?>[] contextPathClasses, String className, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
      try {
         Class<?> spFactory = ServiceLoaderUtil.safeLoadClass(className, "org.glassfish.jaxb.runtime.v2.ContextFactory", classLoader);
         return newInstance(contextPath, contextPathClasses, spFactory, classLoader, properties);
      } catch (ClassNotFoundException var6) {
         throw new JAXBException(Messages.format("ContextFinder.DefaultProviderNotFound"), var6);
      } catch (JAXBException | RuntimeException var7) {
         throw var7;
      } catch (Exception var8) {
         throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", className, var8), var8);
      }
   }

   static JAXBContext newInstance(String contextPath, Class<?>[] contextPathClasses, Class<?> spFactory, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
      try {
         ModuleUtil.delegateAddOpensToImplModule(contextPathClasses, spFactory);
         Object context = null;

         try {
            Method m = spFactory.getMethod("createContext", String.class, ClassLoader.class, Map.class);
            Object obj = instantiateProviderIfNecessary(spFactory);
            context = m.invoke(obj, contextPath, classLoader, properties);
         } catch (NoSuchMethodException var8) {
         }

         if (context == null) {
            Method m = spFactory.getMethod("createContext", String.class, ClassLoader.class);
            Object obj = instantiateProviderIfNecessary(spFactory);
            context = m.invoke(obj, contextPath, classLoader);
         }

         if (!(context instanceof JAXBContext)) {
            throw handleClassCastException(context.getClass(), JAXBContext.class);
         } else {
            return (JAXBContext)context;
         }
      } catch (InvocationTargetException var9) {
         Throwable e = handleInvocationTargetException(var9);
         throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", spFactory, e), e);
      } catch (Exception var10) {
         throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", spFactory, var10), var10);
      }
   }

   private static Object instantiateProviderIfNecessary(final Class<?> implClass) throws JAXBException {
      try {
         return JAXBContextFactory.class.isAssignableFrom(implClass) ? AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
               return implClass.getConstructor().newInstance();
            }
         }) : null;
      } catch (PrivilegedActionException var3) {
         Throwable e = (Throwable)(var3.getCause() == null ? var3 : var3.getCause());
         throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", implClass, e), e);
      }
   }

   private static JAXBContext newInstance(Class<?>[] classes, Map<String, ?> properties, String className) throws JAXBException {
      return newInstance(classes, properties, className, getContextClassLoader());
   }

   private static JAXBContext newInstance(Class<?>[] classes, Map<String, ?> properties, String className, ClassLoader loader) throws JAXBException {
      Class<?> spi;
      try {
         spi = ServiceLoaderUtil.safeLoadClass(className, "org.glassfish.jaxb.runtime.v2.ContextFactory", loader);
      } catch (ClassNotFoundException var6) {
         throw new JAXBException(Messages.format("ContextFinder.DefaultProviderNotFound"), var6);
      }

      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, "loaded {0} from {1}", new Object[]{className, which(spi)});
      }

      return newInstance(classes, properties, spi);
   }

   static JAXBContext newInstance(Class<?>[] classes, Map<String, ?> properties, Class<?> spFactory) throws JAXBException {
      try {
         ModuleUtil.delegateAddOpensToImplModule(classes, spFactory);
         Method m = spFactory.getMethod("createContext", Class[].class, Map.class);
         Object obj = instantiateProviderIfNecessary(spFactory);
         Object context = m.invoke(obj, classes, properties);
         if (!(context instanceof JAXBContext)) {
            throw handleClassCastException(context.getClass(), JAXBContext.class);
         } else {
            return (JAXBContext)context;
         }
      } catch (IllegalAccessException | NoSuchMethodException var6) {
         throw new JAXBException(var6);
      } catch (InvocationTargetException var7) {
         Throwable x = handleInvocationTargetException(var7);
         throw new JAXBException(x);
      }
   }

   static JAXBContext find(String factoryId, String contextPath, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
      if (contextPath != null && !contextPath.isEmpty()) {
         Class<?>[] contextPathClasses = ModuleUtil.getClassesFromContextPath(contextPath, classLoader);
         String factoryName = classNameFromSystemProperties();
         if (factoryName != null) {
            return newInstance(contextPath, contextPathClasses, factoryName, classLoader, properties);
         } else {
            if (properties != null) {
               Object factory = properties.get(factoryId);
               if (factory != null) {
                  if (!(factory instanceof String)) {
                     throw new JAXBException(Messages.format("JAXBContext.IllegalCast", factory.getClass().getName(), "String"));
                  }

                  factoryName = (String)factory;
               }

               if (factoryName != null) {
                  return newInstance(contextPath, contextPathClasses, factoryName, classLoader, properties);
               }
            }

            JAXBContextFactory obj = ServiceLoaderUtil.firstByServiceLoader(JAXBContextFactory.class, logger, EXCEPTION_HANDLER);
            if (obj != null) {
               ModuleUtil.delegateAddOpensToImplModule(contextPathClasses, obj.getClass());
               return obj.createContext(contextPath, classLoader, properties);
            } else {
               Iterable<Class<? extends JAXBContextFactory>> ctxFactories = ServiceLoaderUtil.lookupsUsingOSGiServiceLoader(
                  "jakarta.xml.bind.JAXBContextFactory", logger
               );
               if (ctxFactories != null) {
                  for (Class<? extends JAXBContextFactory> ctxFactory : ctxFactories) {
                     try {
                        return newInstance(contextPath, contextPathClasses, ctxFactory, classLoader, properties);
                     } catch (Throwable var11) {
                        logger.log(Level.FINE, var11, () -> "Error instantiating provivder " + ctxFactory);
                     }
                  }
               }

               logger.fine("Trying to create the platform default provider");
               return newInstance(contextPath, contextPathClasses, "org.glassfish.jaxb.runtime.v2.ContextFactory", classLoader, properties);
            }
         }
      } else {
         throw new JAXBException(Messages.format("ContextFinder.NoPackageInContextPath"));
      }
   }

   static JAXBContext find(Class<?>[] classes, Map<String, ?> properties) throws JAXBException {
      String factoryClassName = classNameFromSystemProperties();
      if (factoryClassName != null) {
         return newInstance(classes, properties, factoryClassName);
      } else {
         if (properties != null) {
            Object ctxFactory = properties.get("jakarta.xml.bind.JAXBContextFactory");
            if (ctxFactory != null) {
               if (!(ctxFactory instanceof String)) {
                  throw new JAXBException(Messages.format("JAXBContext.IllegalCast", ctxFactory.getClass().getName(), "String"));
               }

               factoryClassName = (String)ctxFactory;
            }

            if (factoryClassName != null) {
               return newInstance(classes, properties, factoryClassName);
            }
         }

         JAXBContextFactory factory = ServiceLoaderUtil.firstByServiceLoader(JAXBContextFactory.class, logger, EXCEPTION_HANDLER);
         if (factory != null) {
            ModuleUtil.delegateAddOpensToImplModule(classes, factory.getClass());
            return factory.createContext(classes, properties);
         } else {
            logger.fine("Trying to create the platform default provider");
            Class<?> ctxFactoryClass = ServiceLoaderUtil.lookupUsingOSGiServiceLoader("jakarta.xml.bind.JAXBContextFactory", logger);
            if (ctxFactoryClass != null) {
               return newInstance(classes, properties, ctxFactoryClass);
            } else {
               logger.fine("Trying to create the platform default provider");
               return newInstance(classes, properties, "org.glassfish.jaxb.runtime.v2.ContextFactory");
            }
         }
      }
   }

   private static String classNameFromSystemProperties() throws JAXBException {
      String factoryClassName = getSystemProperty("jakarta.xml.bind.JAXBContextFactory");
      return factoryClassName != null ? factoryClassName : null;
   }

   private static String getSystemProperty(String property) {
      logger.log(Level.FINE, "Checking system property {0}", property);
      String value = AccessController.doPrivileged(new GetPropertyAction(property));
      if (value != null) {
         logger.log(Level.FINE, "  found {0}", value);
      } else {
         logger.log(Level.FINE, "  not found");
      }

      return value;
   }

   static URL which(Class<?> clazz, ClassLoader loader) {
      String classnameAsResource = clazz.getName().replace('.', '/') + ".class";
      if (loader == null) {
         loader = getSystemClassLoader();
      }

      return loader.getResource(classnameAsResource);
   }

   static URL which(Class<?> clazz) {
      return which(clazz, getClassClassLoader(clazz));
   }

   private static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null
         ? Thread.currentThread().getContextClassLoader()
         : AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
               return Thread.currentThread().getContextClassLoader();
            }
         });
   }

   private static ClassLoader getClassClassLoader(final Class<?> c) {
      return System.getSecurityManager() == null ? c.getClassLoader() : AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return c.getClassLoader();
         }
      });
   }

   private static ClassLoader getSystemClassLoader() {
      return System.getSecurityManager() == null ? ClassLoader.getSystemClassLoader() : AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return ClassLoader.getSystemClassLoader();
         }
      });
   }

   static {
      try {
         if (AccessController.doPrivileged(new GetPropertyAction("jaxb.debug")) != null) {
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
            logger.addHandler(handler);
         }
      } catch (Throwable var1) {
      }

      EXCEPTION_HANDLER = new ServiceLoaderUtil.ExceptionHandler<JAXBException>() {
         public JAXBException createException(Throwable throwable, String message) {
            return new JAXBException(message, throwable);
         }
      };
   }
}
