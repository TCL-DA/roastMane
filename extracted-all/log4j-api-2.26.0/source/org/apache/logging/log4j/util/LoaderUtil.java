package org.apache.logging.log4j.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

@InternalApi
public final class LoaderUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   public static final String IGNORE_TCCL_PROPERTY = "log4j.ignoreTCL";
   private static Boolean ignoreTCCL;
   static final RuntimePermission GET_CLASS_LOADER = new RuntimePermission("getClassLoader");
   static final LazyBoolean GET_CLASS_LOADER_DISABLED = new LazyBoolean(() -> {
      if (System.getSecurityManager() == null) {
         return false;
      } else {
         try {
            AccessController.checkPermission(GET_CLASS_LOADER);
            return false;
         } catch (SecurityException var3) {
            try {
               AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
                  AccessController.checkPermission(GET_CLASS_LOADER);
                  return null;
               }));
               return false;
            } catch (SecurityException var2) {
               return true;
            }
         }
      }
   });
   private static final PrivilegedAction<ClassLoader> TCCL_GETTER = new LoaderUtil.ThreadContextClassLoaderGetter();

   private LoaderUtil() {
   }

   public static ClassLoader getClassLoader() {
      return getClassLoader(LoaderUtil.class, null);
   }

   public static ClassLoader getClassLoader(final Class<?> class1, final Class<?> class2) {
      PrivilegedAction<ClassLoader> action = () -> {
         ClassLoader loader1 = class1 == null ? null : class1.getClassLoader();
         ClassLoader loader2 = class2 == null ? null : class2.getClassLoader();
         ClassLoader referenceLoader = GET_CLASS_LOADER_DISABLED.getAsBoolean() ? getThisClassLoader() : Thread.currentThread().getContextClassLoader();
         if (isChild(referenceLoader, loader1)) {
            return isChild(referenceLoader, loader2) ? referenceLoader : loader2;
         } else {
            return isChild(loader1, loader2) ? loader1 : loader2;
         }
      };
      return runPrivileged(action);
   }

   private static boolean isChild(final ClassLoader loader1, final ClassLoader loader2) {
      if (loader1 != null && loader2 != null) {
         ClassLoader parent = loader1.getParent();

         while (parent != null && parent != loader2) {
            parent = parent.getParent();
         }

         return parent != null;
      } else {
         return loader1 != null;
      }
   }

   public static ClassLoader getThreadContextClassLoader() {
      try {
         return GET_CLASS_LOADER_DISABLED.getAsBoolean() ? getThisClassLoader() : runPrivileged(TCCL_GETTER);
      } catch (SecurityException var1) {
         return null;
      }
   }

   private static ClassLoader getThisClassLoader() {
      return LoaderUtil.class.getClassLoader();
   }

   private static <T> T runPrivileged(final PrivilegedAction<T> action) {
      return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
   }

   public static boolean isClassAvailable(final String className) {
      try {
         loadClass(className);
         return true;
      } catch (LinkageError | ClassNotFoundException var2) {
         return false;
      } catch (Throwable var3) {
         LOGGER.error("Unknown error while checking existence of class `{}`", className, var3);
         return false;
      }
   }

   public static Class<?> loadClass(final String className) throws ClassNotFoundException {
      ClassLoader classLoader = isIgnoreTccl() ? getThisClassLoader() : getThreadContextClassLoader();
      if (classLoader == null) {
         classLoader = getThisClassLoader();
      }

      return Class.forName(className, true, classLoader);
   }

   public static Class<?> loadClassUnchecked(final String className) {
      try {
         return loadClass(className);
      } catch (ClassNotFoundException var3) {
         NoClassDefFoundError error = new NoClassDefFoundError(var3.getMessage());
         error.initCause(var3);
         throw error;
      }
   }

   public static <T> T newInstanceOf(final Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      Constructor<T> constructor = clazz.getDeclaredConstructor();
      return constructor.newInstance();
   }

   public static <T> T newInstanceOfUnchecked(final Class<T> clazz) {
      try {
         return newInstanceOf(clazz);
      } catch (NoSuchMethodException var3) {
         NoSuchMethodError error = new NoSuchMethodError(var3.getMessage());
         error.initCause(var3);
         throw error;
      } catch (InvocationTargetException var4) {
         Throwable cause = var4.getCause();
         throw new InternalException(cause);
      } catch (InstantiationException var5) {
         InstantiationError errorx = new InstantiationError(var5.getMessage());
         errorx.initCause(var5);
         throw errorx;
      } catch (IllegalAccessException var6) {
         IllegalAccessError errorxx = new IllegalAccessError(var6.getMessage());
         errorxx.initCause(var6);
         throw errorxx;
      }
   }

   public static <T> T newInstanceOf(final String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
      Class<T> clazz = Cast.cast(loadClass(className));
      return newInstanceOf(clazz);
   }

   public static <T> T newCheckedInstanceOfProperty(final String propertyName, final Class<T> clazz) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
      return newCheckedInstanceOfProperty(propertyName, clazz, () -> null);
   }

   public static <T> T newCheckedInstanceOfProperty(final String propertyName, final Class<T> clazz, final java.util.function.Supplier<T> defaultSupplier) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
      String className = PropertiesUtil.getProperties().getStringProperty(propertyName);
      return className == null ? defaultSupplier.get() : newCheckedInstanceOf(className, clazz);
   }

   public static <T> T newInstanceOfUnchecked(final String className) {
      Class<T> clazz = Cast.cast(loadClassUnchecked(className));
      return newInstanceOfUnchecked(clazz);
   }

   public static <T> T newCheckedInstanceOf(final String className, final Class<T> clazz) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
      return newInstanceOf((Class<T>)loadClass(className).asSubclass(clazz));
   }

   public static <T> T newInstanceOfUnchecked(final String className, final Class<T> supertype) {
      Class<? extends T> clazz = loadClassUnchecked(className).asSubclass(supertype);
      return newInstanceOfUnchecked((Class<T>)clazz);
   }

   private static boolean isIgnoreTccl() {
      if (ignoreTCCL == null) {
         String ignoreTccl = PropertiesUtil.getProperties().getStringProperty("log4j.ignoreTCL", null);
         ignoreTCCL = ignoreTccl != null && !"false".equalsIgnoreCase(ignoreTccl.trim());
      }

      return ignoreTCCL;
   }

   public static Collection<URL> findResources(final String resource) {
      return findResources(resource, true);
   }

   static Collection<URL> findResources(final String resource, final boolean useTccl) {
      Collection<LoaderUtil.UrlResource> urlResources = findUrlResources(resource, useTccl);
      Collection<URL> resources = new LinkedHashSet<>(urlResources.size());

      for (LoaderUtil.UrlResource urlResource : urlResources) {
         resources.add(urlResource.getUrl());
      }

      return resources;
   }

   static Collection<LoaderUtil.UrlResource> findUrlResources(final String resource, final boolean useTccl) {
      ClassLoader[] candidates = new ClassLoader[]{
         useTccl ? getThreadContextClassLoader() : null,
         LoaderUtil.class.getClassLoader(),
         GET_CLASS_LOADER_DISABLED.getAsBoolean() ? null : ClassLoader.getSystemClassLoader()
      };
      Collection<LoaderUtil.UrlResource> resources = new LinkedHashSet<>();

      for (ClassLoader cl : candidates) {
         if (cl != null) {
            try {
               Enumeration<URL> resourceEnum = cl.getResources(resource);

               while (resourceEnum.hasMoreElements()) {
                  resources.add(new LoaderUtil.UrlResource(cl, resourceEnum.nextElement()));
               }
            } catch (IOException var9) {
               LOGGER.error("failed to collect resources of name `{}`", resource, var9);
            }
         }
      }

      return resources;
   }

   private static class ThreadContextClassLoaderGetter implements PrivilegedAction<ClassLoader> {
      private ThreadContextClassLoaderGetter() {
      }

      public ClassLoader run() {
         ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
         if (contextClassLoader != null) {
            return contextClassLoader;
         } else {
            ClassLoader thisClassLoader = LoaderUtil.getThisClassLoader();
            return thisClassLoader == null && !LoaderUtil.GET_CLASS_LOADER_DISABLED.getAsBoolean() ? ClassLoader.getSystemClassLoader() : thisClassLoader;
         }
      }
   }

   static class UrlResource {
      private final ClassLoader classLoader;
      private final URL url;

      UrlResource(final ClassLoader classLoader, final URL url) {
         this.classLoader = classLoader;
         this.url = url;
      }

      public ClassLoader getClassLoader() {
         return this.classLoader;
      }

      public URL getUrl() {
         return this.url;
      }

      @Override
      public boolean equals(final Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof LoaderUtil.UrlResource)) {
            return false;
         } else {
            LoaderUtil.UrlResource that = (LoaderUtil.UrlResource)o;
            return Objects.equals(this.classLoader, that.classLoader) && Objects.equals(this.url, that.url);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.classLoader) + Objects.hashCode(this.url);
      }
   }
}
