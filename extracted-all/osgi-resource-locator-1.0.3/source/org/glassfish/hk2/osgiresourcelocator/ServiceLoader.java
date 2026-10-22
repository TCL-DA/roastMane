package org.glassfish.hk2.osgiresourcelocator;

public abstract class ServiceLoader {
   private static ServiceLoader _me;

   ServiceLoader() {
   }

   public static synchronized void initialize(ServiceLoader singleton) {
      if (singleton == null) {
         throw new NullPointerException("Did you intend to call reset()?");
      } else if (_me != null) {
         throw new IllegalStateException("Already initialzed with [" + _me + "]");
      } else {
         _me = singleton;
      }
   }

   public static synchronized void reset() {
      if (_me == null) {
         throw new IllegalStateException("Not yet initialized");
      } else {
         _me = null;
      }
   }

   public static <T> Iterable<? extends T> lookupProviderInstances(Class<T> serviceClass) {
      return lookupProviderInstances(serviceClass, null);
   }

   public static <T> Iterable<? extends T> lookupProviderInstances(Class<T> serviceClass, ServiceLoader.ProviderFactory<T> factory) {
      return _me == null ? null : _me.lookupProviderInstances1(serviceClass, factory);
   }

   public static <T> Iterable<Class> lookupProviderClasses(Class<T> serviceClass) {
      return _me.lookupProviderClasses1(serviceClass);
   }

   abstract <T> Iterable<? extends T> lookupProviderInstances1(Class<T> var1, ServiceLoader.ProviderFactory<T> var2);

   abstract <T> Iterable<Class> lookupProviderClasses1(Class<T> var1);

   public interface ProviderFactory<T> {
      T make(Class var1, Class<T> var2) throws Exception;
   }
}
