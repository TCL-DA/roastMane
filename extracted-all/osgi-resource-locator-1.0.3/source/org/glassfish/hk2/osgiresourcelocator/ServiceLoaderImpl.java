package org.glassfish.hk2.osgiresourcelocator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.BundleReference;

public final class ServiceLoaderImpl extends ServiceLoader {
   private ReadWriteLock rwLock = new ReentrantReadWriteLock();
   private BundleListener bundleTracker;
   private BundleContext bundleContext;
   private ServiceLoaderImpl.ProvidersList providersList = new ServiceLoaderImpl.ProvidersList();

   public ServiceLoaderImpl() {
      ClassLoader cl = this.getClass().getClassLoader();
      if (cl instanceof BundleReference) {
         this.bundleContext = this.getBundleContextSecured(BundleReference.class.cast(cl).getBundle());
      }

      if (this.bundleContext == null) {
         throw new RuntimeException("There is no bundle context available yet. Instatiate this class in STARTING or ACTIVE state only");
      }
   }

   private BundleContext getBundleContextSecured(final Bundle bundle) {
      return System.getSecurityManager() != null ? AccessController.doPrivileged(new PrivilegedAction<BundleContext>() {
         public BundleContext run() {
            return bundle.getBundleContext();
         }
      }) : bundle.getBundleContext();
   }

   public void trackBundles() {
      assert this.bundleTracker == null;

      this.bundleTracker = new ServiceLoaderImpl.BundleTracker();
      this.bundleContext.addBundleListener(this.bundleTracker);

      for (Bundle bundle : this.bundleContext.getBundles()) {
         this.addProviders(bundle);
      }
   }

   @Override
   <T> Iterable<? extends T> lookupProviderInstances1(Class<T> serviceClass, ServiceLoader.ProviderFactory<T> factory) {
      if (factory == null) {
         factory = new ServiceLoaderImpl.DefaultFactory<>();
      }

      List<T> providers = new ArrayList<>();

      for (Class c : this.lookupProviderClasses1(serviceClass)) {
         try {
            T providerInstance = factory.make(c, serviceClass);
            if (providerInstance != null) {
               providers.add(providerInstance);
            } else {
               this.debug(factory + " returned null provider instance!!!");
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }

      return providers;
   }

   @Override
   <T> Iterable<Class> lookupProviderClasses1(Class<T> serviceClass) {
      List<Class> providerClasses = new ArrayList<>();
      this.rwLock.readLock().lock();

      Object var16;
      try {
         String serviceName = serviceClass.getName();

         for (ServiceLoaderImpl.ProvidersPerBundle providersPerBundle : this.providersList.getAllProviders()) {
            Bundle bundle = this.bundleContext.getBundle(providersPerBundle.getBundleId());
            if (bundle != null) {
               List<String> providerNames = providersPerBundle.getServiceToProvidersMap().get(serviceName);
               if (providerNames != null) {
                  for (String providerName : providerNames) {
                     try {
                        Class providerClass = this.loadClassSecured(bundle, providerName);
                        if (this.isCompatible(providerClass, serviceClass)) {
                           providerClasses.add(providerClass);
                        }
                     } catch (ClassNotFoundException var14) {
                        var14.printStackTrace();
                     }
                  }
               }
            }
         }

         var16 = providerClasses;
      } finally {
         this.rwLock.readLock().unlock();
      }

      return (Iterable<Class>)var16;
   }

   private Class loadClassSecured(final Bundle bundle, final String name) throws ClassNotFoundException {
      if (System.getSecurityManager() != null) {
         try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Class>() {
               public Class run() throws ClassNotFoundException {
                  return bundle.loadClass(name);
               }
            });
         } catch (PrivilegedActionException var4) {
            throw (ClassNotFoundException)ClassNotFoundException.class.cast(var4.getException());
         }
      } else {
         return bundle.loadClass(name);
      }
   }

   private boolean isCompatible(Class providerClass, Class serviceClass) {
      try {
         Class<?> serviceClassSeenByProviderClass = Class.forName(serviceClass.getName(), false, providerClass.getClassLoader());
         boolean isCompatible = serviceClassSeenByProviderClass == serviceClass;
         if (!isCompatible) {
            this.debug(
               providerClass
                  + " loaded by "
                  + providerClass.getClassLoader()
                  + " sees "
                  + serviceClass
                  + " from "
                  + serviceClassSeenByProviderClass.getClassLoader()
                  + ", where as caller uses "
                  + serviceClass
                  + " loaded by "
                  + serviceClass.getClassLoader()
            );
         }

         return isCompatible;
      } catch (ClassNotFoundException var5) {
         this.debug("Unable to reach " + serviceClass + " from " + providerClass + ", which is loaded by " + providerClass.getClassLoader(), var5);
         return true;
      }
   }

   private List<String> load(InputStream is) throws IOException {
      List<String> providerNames = new ArrayList<>();

      try {
         Scanner scanner = new Scanner(is);
         String commentPattern = "#";

         while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.startsWith("#")) {
               StringTokenizer st = new StringTokenizer(line);
               if (st.hasMoreTokens()) {
                  providerNames.add(st.nextToken());
               }
            }
         }
      } finally {
         is.close();
      }

      return providerNames;
   }

   private void addProviders(Bundle bundle) {
      this.rwLock.writeLock().lock();

      try {
         String SERVICE_LOCATION = "META-INF/services";
         if (bundle.getEntry("META-INF/services") != null) {
            Enumeration<String> entries = bundle.getEntryPaths("META-INF/services");
            if (entries == null) {
               return;
            }

            ServiceLoaderImpl.ProvidersPerBundle providers = new ServiceLoaderImpl.ProvidersPerBundle(bundle.getBundleId());

            while (entries.hasMoreElements()) {
               String entry = entries.nextElement();
               String serviceName = entry.substring("META-INF/services".length() + 1);
               URL url = bundle.getEntry(entry);

               try {
                  InputStream is = url.openStream();
                  List<String> providerNames = this.load(is);
                  this.debug("Bundle = " + bundle + ", serviceName = " + serviceName + ", providerNames = " + providerNames);
                  providers.put(serviceName, providerNames);
               } catch (IOException var13) {
               }
            }

            this.providersList.addProviders(providers);
            return;
         }
      } finally {
         this.rwLock.writeLock().unlock();
      }
   }

   private synchronized void removeProviders(Bundle bundle) {
      this.rwLock.writeLock().lock();

      try {
         this.providersList.removeProviders(bundle.getBundleId());
      } finally {
         this.rwLock.writeLock().unlock();
      }
   }

   private void debug(String s) {
      if (Boolean.valueOf(this.bundleContext.getProperty("org.glassfish.hk2.osgiresourcelocator.debug"))) {
         System.out.println("org.glassfish.hk2.osgiresourcelocator:DEBUG: " + s);
      }
   }

   private void debug(String s, Throwable t) {
      if (Boolean.valueOf(this.bundleContext.getProperty("org.glassfish.hk2.osgiresourcelocator.debug"))) {
         System.out.println("org.glassfish.hk2.osgiresourcelocator:DEBUG: " + s);
         t.printStackTrace(System.out);
      }
   }

   private class BundleTracker implements BundleListener {
      private BundleTracker() {
      }

      public void bundleChanged(BundleEvent event) {
         Bundle bundle = event.getBundle();
         switch (event.getType()) {
            case 1:
               ServiceLoaderImpl.this.addProviders(bundle);
               break;
            case 8:
               ServiceLoaderImpl.this.removeProviders(bundle);
               ServiceLoaderImpl.this.addProviders(bundle);
               break;
            case 16:
               ServiceLoaderImpl.this.removeProviders(bundle);
         }
      }
   }

   private static class DefaultFactory<T> implements ServiceLoader.ProviderFactory<T> {
      private DefaultFactory() {
      }

      @Override
      public T make(Class providerClass, Class<T> serviceClass) throws Exception {
         return (T)(serviceClass.isAssignableFrom(providerClass) ? providerClass.newInstance() : null);
      }
   }

   private static class ProvidersList {
      private List<ServiceLoaderImpl.ProvidersPerBundle> allProviders = new LinkedList<>();

      private ProvidersList() {
      }

      void addProviders(ServiceLoaderImpl.ProvidersPerBundle providers) {
         long bundleId = providers.getBundleId();
         int idx = 0;

         for (ServiceLoaderImpl.ProvidersPerBundle providersPerBundle : this.getAllProviders()) {
            if (providersPerBundle.getBundleId() > bundleId) {
               this.getAllProviders().add(idx, providers);
               return;
            }
         }

         this.getAllProviders().add(providers);
      }

      void removeProviders(long bundleId) {
         Iterator<ServiceLoaderImpl.ProvidersPerBundle> iterator = this.getAllProviders().iterator();

         while (iterator.hasNext()) {
            ServiceLoaderImpl.ProvidersPerBundle providersPerBundle = iterator.next();
            if (providersPerBundle.getBundleId() == bundleId) {
               iterator.remove();
               return;
            }
         }
      }

      public List<ServiceLoaderImpl.ProvidersPerBundle> getAllProviders() {
         return this.allProviders;
      }
   }

   private static class ProvidersPerBundle {
      private long bundleId;
      Map<String, List<String>> serviceToProvidersMap = new HashMap<>();

      private ProvidersPerBundle(long bundleId) {
         this.bundleId = bundleId;
      }

      public long getBundleId() {
         return this.bundleId;
      }

      public void put(String serviceName, List<String> providerNames) {
         this.serviceToProvidersMap.put(serviceName, providerNames);
      }

      public Map<String, List<String>> getServiceToProvidersMap() {
         return this.serviceToProvidersMap;
      }
   }
}
