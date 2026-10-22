package org.glassfish.jersey.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.ReflectPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.util.ReflectionHelper;

public final class ServiceFinder<T> implements Iterable<T> {
   private static final Logger LOGGER = Logger.getLogger(ServiceFinder.class.getName());
   private static final String PREFIX = "META-INF/services/";
   private final Class<T> serviceClass;
   private final String serviceName;
   private final ClassLoader classLoader;
   private final boolean ignoreOnClassNotFound;

   private static Enumeration<URL> getResources(ClassLoader loader, String name) throws IOException {
      if (loader == null) {
         return getResources(name);
      } else {
         Enumeration<URL> resources = loader.getResources(name);
         return resources != null && resources.hasMoreElements() ? resources : getResources(name);
      }
   }

   private static Enumeration<URL> getResources(String name) throws IOException {
      return ServiceFinder.class.getClassLoader() != null ? ServiceFinder.class.getClassLoader().getResources(name) : ClassLoader.getSystemResources(name);
   }

   private static ClassLoader _getContextClassLoader() {
      return AccessController.doPrivileged(ReflectionHelper.getContextClassLoaderPA());
   }

   public static <T> ServiceFinder<T> find(Class<T> service, ClassLoader loader) throws ServiceConfigurationError {
      return find(service, loader, false);
   }

   public static <T> ServiceFinder<T> find(Class<T> service, ClassLoader loader, boolean ignoreOnClassNotFound) throws ServiceConfigurationError {
      return new ServiceFinder<>(service, loader, ignoreOnClassNotFound);
   }

   public static <T> ServiceFinder<T> find(Class<T> service) throws ServiceConfigurationError {
      return find(service, _getContextClassLoader(), false);
   }

   public static <T> ServiceFinder<T> find(Class<T> service, boolean ignoreOnClassNotFound) throws ServiceConfigurationError {
      return find(service, _getContextClassLoader(), ignoreOnClassNotFound);
   }

   public static ServiceFinder<?> find(String serviceName) throws ServiceConfigurationError {
      return new ServiceFinder<>(Object.class, serviceName, _getContextClassLoader(), false);
   }

   public static void setIteratorProvider(ServiceFinder.ServiceIteratorProvider sip) throws SecurityException {
      ServiceFinder.ServiceIteratorProvider.setInstance(sip);
   }

   private ServiceFinder(Class<T> service, ClassLoader loader, boolean ignoreOnClassNotFound) {
      this(service, service.getName(), loader, ignoreOnClassNotFound);
   }

   private ServiceFinder(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
      this.serviceClass = service;
      this.serviceName = serviceName;
      this.classLoader = loader;
      this.ignoreOnClassNotFound = ignoreOnClassNotFound;
   }

   @Override
   public Iterator<T> iterator() {
      return ServiceFinder.ServiceIteratorProvider.getInstance()
         .createIterator(this.serviceClass, this.serviceName, this.classLoader, this.ignoreOnClassNotFound);
   }

   public T[] toArray() throws ServiceConfigurationError {
      List<T> result = new ArrayList<>();

      for (T t : this) {
         result.add(t);
      }

      return (T[])result.toArray((T[])((Object[])Array.newInstance(this.serviceClass, result.size())));
   }

   public Class<T>[] toClassArray() throws ServiceConfigurationError {
      List<Class<T>> result = new ArrayList<>();
      ServiceFinder.ServiceIteratorProvider iteratorProvider = ServiceFinder.ServiceIteratorProvider.getInstance();
      Iterator<Class<T>> i = iteratorProvider.createClassIterator(this.serviceClass, this.serviceName, this.classLoader, this.ignoreOnClassNotFound);

      while (i.hasNext()) {
         result.add(i.next());
      }

      return result.toArray((Class<T>[])Array.newInstance(Class.class, result.size()));
   }

   private static void fail(String serviceName, String msg, Throwable cause) throws ServiceConfigurationError {
      ServiceConfigurationError sce = new ServiceConfigurationError(serviceName + ": " + msg);
      sce.initCause(cause);
      throw sce;
   }

   private static void fail(String serviceName, String msg) throws ServiceConfigurationError {
      throw new ServiceConfigurationError(serviceName + ": " + msg);
   }

   private static void fail(String serviceName, URL u, int line, String msg) throws ServiceConfigurationError {
      fail(serviceName, u + ":" + line + ": " + msg);
   }

   private static int parseLine(String serviceName, URL u, BufferedReader r, int lc, List<String> names, Set<String> returned) throws IOException, ServiceConfigurationError {
      String ln = r.readLine();
      if (ln == null) {
         return -1;
      } else {
         int ci = ln.indexOf(35);
         if (ci >= 0) {
            ln = ln.substring(0, ci);
         }

         ln = ln.trim();
         int n = ln.length();
         if (n != 0) {
            if (ln.indexOf(32) >= 0 || ln.indexOf(9) >= 0) {
               fail(serviceName, u, lc, LocalizationMessages.ILLEGAL_CONFIG_SYNTAX());
            }

            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
               fail(serviceName, u, lc, LocalizationMessages.ILLEGAL_PROVIDER_CLASS_NAME(ln));
            }

            int i = Character.charCount(cp);

            while (i < n) {
               cp = ln.codePointAt(i);
               if (!Character.isJavaIdentifierPart(cp) && cp != 46) {
                  fail(serviceName, u, lc, LocalizationMessages.ILLEGAL_PROVIDER_CLASS_NAME(ln));
               }

               i += Character.charCount(cp);
            }

            if (!returned.contains(ln)) {
               names.add(ln);
               returned.add(ln);
            }
         }

         return lc + 1;
      }
   }

   private static Iterator<String> parse(String serviceName, URL u, Set<String> returned) throws ServiceConfigurationError {
      InputStream in = null;
      BufferedReader r = null;
      ArrayList<String> names = new ArrayList<>();

      try {
         URLConnection uConn = u.openConnection();
         uConn.setUseCaches(false);
         in = uConn.getInputStream();
         r = new BufferedReader(new InputStreamReader(in, "utf-8"));
         int lc = 1;

         while ((lc = parseLine(serviceName, u, r, lc, names, returned)) >= 0) {
         }
      } catch (IOException var16) {
         fail(serviceName, ": " + var16);
      } finally {
         try {
            if (r != null) {
               r.close();
            }

            if (in != null) {
               in.close();
            }
         } catch (IOException var15) {
            fail(serviceName, ": " + var15);
         }
      }

      return names.iterator();
   }

   static {
      OsgiRegistry osgiRegistry = ReflectionHelper.getOsgiRegistryInstance();
      if (osgiRegistry != null) {
         LOGGER.log(Level.CONFIG, "Running in an OSGi environment");
         osgiRegistry.hookUp();
      } else {
         LOGGER.log(Level.CONFIG, "Running in a non-OSGi environment");
      }
   }

   private static class AbstractLazyIterator<T> {
      final Class<T> service;
      final String serviceName;
      final ClassLoader loader;
      final boolean ignoreOnClassNotFound;
      Enumeration<URL> configs = null;
      Iterator<String> pending = null;
      Set<String> returned = new TreeSet<>();
      String nextName = null;

      private AbstractLazyIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
         this.service = service;
         this.serviceName = serviceName;
         this.loader = loader;
         this.ignoreOnClassNotFound = ignoreOnClassNotFound;
      }

      protected final void setConfigs() {
         if (this.configs == null) {
            try {
               String fullName = "META-INF/services/" + this.serviceName;
               this.configs = ServiceFinder.getResources(this.loader, fullName);
            } catch (IOException var2) {
               ServiceFinder.fail(this.serviceName, ": " + var2);
            }
         }
      }

      public boolean hasNext() throws ServiceConfigurationError {
         if (this.nextName != null) {
            return true;
         } else {
            this.setConfigs();

            while (this.nextName == null) {
               while (this.pending == null || !this.pending.hasNext()) {
                  if (!this.configs.hasMoreElements()) {
                     return false;
                  }

                  this.pending = ServiceFinder.parse(this.serviceName, this.configs.nextElement(), this.returned);
               }

               this.nextName = this.pending.next();
               if (this.ignoreOnClassNotFound) {
                  try {
                     AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(this.nextName, this.loader));
                  } catch (ClassNotFoundException var3) {
                     this.handleClassNotFoundException();
                  } catch (PrivilegedActionException var4) {
                     Throwable thrown = var4.getException();
                     if (thrown instanceof ClassNotFoundException) {
                        this.handleClassNotFoundException();
                     } else if (thrown instanceof NoClassDefFoundError) {
                        if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                           ServiceFinder.LOGGER
                              .log(
                                 Level.CONFIG,
                                 LocalizationMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(thrown.getLocalizedMessage(), this.nextName, this.service)
                              );
                        }

                        this.nextName = null;
                     } else {
                        if (!(thrown instanceof ClassFormatError)) {
                           if (thrown instanceof RuntimeException) {
                              throw (RuntimeException)thrown;
                           }

                           throw new IllegalStateException(thrown);
                        }

                        if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                           ServiceFinder.LOGGER
                              .log(
                                 Level.CONFIG,
                                 LocalizationMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(thrown.getLocalizedMessage(), this.nextName, this.service)
                              );
                        }

                        this.nextName = null;
                     }
                  }
               }
            }

            return true;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      private void handleClassNotFoundException() {
         if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
            ServiceFinder.LOGGER.log(Level.CONFIG, LocalizationMessages.PROVIDER_NOT_FOUND(this.nextName, this.service));
         }

         this.nextName = null;
      }
   }

   public static final class DefaultServiceIteratorProvider extends ServiceFinder.ServiceIteratorProvider {
      @Override
      public <T> Iterator<T> createIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
         return new ServiceFinder.LazyObjectIterator<>(service, serviceName, loader, ignoreOnClassNotFound);
      }

      @Override
      public <T> Iterator<Class<T>> createClassIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
         return new ServiceFinder.LazyClassIterator<>(service, serviceName, loader, ignoreOnClassNotFound);
      }
   }

   private static final class LazyClassIterator<T> extends ServiceFinder.AbstractLazyIterator<T> implements Iterator<Class<T>> {
      private LazyClassIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
         super(service, serviceName, loader, ignoreOnClassNotFound);
      }

      public Class<T> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            String cn = this.nextName;
            this.nextName = null;

            try {
               Class<T> tClass = AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(cn, this.loader));
               if (ServiceFinder.LOGGER.isLoggable(Level.FINEST)) {
                  ServiceFinder.LOGGER.log(Level.FINEST, "Loading next class: " + tClass.getName());
               }

               return tClass;
            } catch (ClassNotFoundException var4) {
               ServiceFinder.fail(this.serviceName, LocalizationMessages.PROVIDER_NOT_FOUND(cn, this.service));
            } catch (PrivilegedActionException var5) {
               Throwable thrown = var5.getCause();
               if (thrown instanceof ClassNotFoundException) {
                  ServiceFinder.fail(this.serviceName, LocalizationMessages.PROVIDER_NOT_FOUND(cn, this.service));
               } else if (thrown instanceof NoClassDefFoundError) {
                  ServiceFinder.fail(
                     this.serviceName, LocalizationMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(thrown.getLocalizedMessage(), cn, this.service)
                  );
               } else if (thrown instanceof ClassFormatError) {
                  ServiceFinder.fail(
                     this.serviceName, LocalizationMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(thrown.getLocalizedMessage(), cn, this.service)
                  );
               } else {
                  ServiceFinder.fail(
                     this.serviceName, LocalizationMessages.PROVIDER_CLASS_COULD_NOT_BE_LOADED(cn, this.service, thrown.getLocalizedMessage()), thrown
                  );
               }
            }

            return null;
         }
      }
   }

   private static final class LazyObjectIterator<T> extends ServiceFinder.AbstractLazyIterator<T> implements Iterator<T> {
      private T t;

      private LazyObjectIterator(Class<T> service, String serviceName, ClassLoader loader, boolean ignoreOnClassNotFound) {
         super(service, serviceName, loader, ignoreOnClassNotFound);
      }

      @Override
      public boolean hasNext() throws ServiceConfigurationError {
         if (this.nextName != null) {
            return true;
         } else {
            this.setConfigs();

            while (this.nextName == null) {
               while (this.pending == null || !this.pending.hasNext()) {
                  if (!this.configs.hasMoreElements()) {
                     return false;
                  }

                  this.pending = ServiceFinder.parse(this.serviceName, this.configs.nextElement(), this.returned);
               }

               this.nextName = this.pending.next();

               try {
                  this.t = this.service
                     .cast(AccessController.<Class>doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(this.nextName, this.loader)).newInstance());
               } catch (InstantiationException var3) {
                  if (this.ignoreOnClassNotFound) {
                     if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                        ServiceFinder.LOGGER
                           .log(Level.CONFIG, LocalizationMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, var3.getLocalizedMessage()));
                     }

                     this.nextName = null;
                  } else {
                     ServiceFinder.fail(
                        this.serviceName, LocalizationMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, var3.getLocalizedMessage()), var3
                     );
                  }
               } catch (IllegalAccessException var4) {
                  ServiceFinder.fail(
                     this.serviceName, LocalizationMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, var4.getLocalizedMessage()), var4
                  );
               } catch (ClassNotFoundException var5) {
                  this.handleClassNotFoundException();
               } catch (NoClassDefFoundError var6) {
                  if (this.ignoreOnClassNotFound) {
                     if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                        ServiceFinder.LOGGER
                           .log(
                              Level.CONFIG, LocalizationMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(var6.getLocalizedMessage(), this.nextName, this.service)
                           );
                     }

                     this.nextName = null;
                  } else {
                     ServiceFinder.fail(
                        this.serviceName,
                        LocalizationMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(var6.getLocalizedMessage(), this.nextName, this.service),
                        var6
                     );
                  }
               } catch (PrivilegedActionException var7) {
                  Throwable cause = var7.getCause();
                  if (cause instanceof ClassNotFoundException) {
                     this.handleClassNotFoundException();
                  } else if (cause instanceof ClassFormatError) {
                     if (this.ignoreOnClassNotFound) {
                        if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                           ServiceFinder.LOGGER
                              .log(
                                 Level.CONFIG,
                                 LocalizationMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(cause.getLocalizedMessage(), this.nextName, this.service)
                              );
                        }

                        this.nextName = null;
                     } else {
                        ServiceFinder.fail(
                           this.serviceName,
                           LocalizationMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(cause.getLocalizedMessage(), this.nextName, this.service),
                           cause
                        );
                     }
                  } else {
                     ServiceFinder.fail(
                        this.serviceName, LocalizationMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, cause.getLocalizedMessage()), cause
                     );
                  }
               }
            }

            return true;
         }
      }

      @Override
      public T next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.nextName = null;
            if (ServiceFinder.LOGGER.isLoggable(Level.FINEST)) {
               ServiceFinder.LOGGER.log(Level.FINEST, "Loading next object: " + this.t.getClass().getName());
            }

            return this.t;
         }
      }

      private void handleClassNotFoundException() throws ServiceConfigurationError {
         if (this.ignoreOnClassNotFound) {
            if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
               ServiceFinder.LOGGER.log(Level.CONFIG, LocalizationMessages.PROVIDER_NOT_FOUND(this.nextName, this.service));
            }

            this.nextName = null;
         } else {
            ServiceFinder.fail(this.serviceName, LocalizationMessages.PROVIDER_NOT_FOUND(this.nextName, this.service));
         }
      }
   }

   public abstract static class ServiceIteratorProvider {
      private static volatile ServiceFinder.ServiceIteratorProvider sip;
      private static final Object sipLock = new Object();

      private static ServiceFinder.ServiceIteratorProvider getInstance() {
         ServiceFinder.ServiceIteratorProvider result = sip;
         if (result == null) {
            synchronized (sipLock) {
               result = sip;
               if (result == null) {
                  sip = result = new ServiceFinder.DefaultServiceIteratorProvider();
               }
            }
         }

         return result;
      }

      private static void setInstance(ServiceFinder.ServiceIteratorProvider sip) throws SecurityException {
         SecurityManager security = System.getSecurityManager();
         if (security != null) {
            ReflectPermission rp = new ReflectPermission("suppressAccessChecks");
            security.checkPermission(rp);
         }

         synchronized (sipLock) {
            ServiceFinder.ServiceIteratorProvider.sip = sip;
         }
      }

      public abstract <T> Iterator<T> createIterator(Class<T> var1, String var2, ClassLoader var3, boolean var4);

      public abstract <T> Iterator<Class<T>> createClassIterator(Class<T> var1, String var2, ClassLoader var3, boolean var4);
   }
}
