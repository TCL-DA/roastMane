package jakarta.validation;

import jakarta.validation.bootstrap.GenericBootstrap;
import jakarta.validation.bootstrap.ProviderSpecificBootstrap;
import jakarta.validation.spi.BootstrapState;
import jakarta.validation.spi.ValidationProvider;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.WeakHashMap;

public class Validation {
   public static ValidatorFactory buildDefaultValidatorFactory() {
      return byDefaultProvider().configure().buildValidatorFactory();
   }

   public static GenericBootstrap byDefaultProvider() {
      return new Validation.GenericBootstrapImpl();
   }

   public static <T extends Configuration<T>, U extends ValidationProvider<T>> ProviderSpecificBootstrap<T> byProvider(Class<U> providerType) {
      return new Validation.ProviderSpecificBootstrapImpl<>(providerType);
   }

   private static void clearDefaultValidationProviderResolverCache() {
      Validation.GetValidationProviderListAction.clearCache();
   }

   private static class DefaultValidationProviderResolver implements ValidationProviderResolver {
      private DefaultValidationProviderResolver() {
      }

      @Override
      public List<ValidationProvider<?>> getValidationProviders() {
         return Validation.GetValidationProviderListAction.getValidationProviderList();
      }
   }

   private static class GenericBootstrapImpl implements GenericBootstrap, BootstrapState {
      private ValidationProviderResolver resolver;
      private ValidationProviderResolver defaultResolver;

      private GenericBootstrapImpl() {
      }

      @Override
      public GenericBootstrap providerResolver(ValidationProviderResolver resolver) {
         this.resolver = resolver;
         return this;
      }

      @Override
      public ValidationProviderResolver getValidationProviderResolver() {
         return this.resolver;
      }

      @Override
      public ValidationProviderResolver getDefaultValidationProviderResolver() {
         if (this.defaultResolver == null) {
            this.defaultResolver = new Validation.DefaultValidationProviderResolver();
         }

         return this.defaultResolver;
      }

      @Override
      public Configuration<?> configure() {
         ValidationProviderResolver resolver = this.resolver == null ? this.getDefaultValidationProviderResolver() : this.resolver;

         List<ValidationProvider<?>> validationProviders;
         try {
            validationProviders = resolver.getValidationProviders();
         } catch (ValidationException var6) {
            throw var6;
         } catch (RuntimeException var7) {
            throw new ValidationException("Unable to get available provider resolvers.", var7);
         }

         if (validationProviders.isEmpty()) {
            String msg = "Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.";
            throw new NoProviderFoundException(msg);
         } else {
            try {
               return resolver.getValidationProviders().get(0).createGenericConfiguration(this);
            } catch (RuntimeException var5) {
               throw new ValidationException("Unable to instantiate Configuration.", var5);
            }
         }
      }
   }

   private static class GetValidationProviderListAction implements PrivilegedAction<List<ValidationProvider<?>>> {
      private static final Validation.GetValidationProviderListAction INSTANCE = new Validation.GetValidationProviderListAction();
      private final WeakHashMap<ClassLoader, SoftReference<List<ValidationProvider<?>>>> providersPerClassloader = new WeakHashMap<>();

      public static synchronized List<ValidationProvider<?>> getValidationProviderList() {
         return System.getSecurityManager() != null ? AccessController.doPrivileged(INSTANCE) : INSTANCE.run();
      }

      public static synchronized void clearCache() {
         INSTANCE.providersPerClassloader.clear();
      }

      public List<ValidationProvider<?>> run() {
         ClassLoader classloader = Thread.currentThread().getContextClassLoader();
         List<ValidationProvider<?>> cachedContextClassLoaderProviderList = this.getCachedValidationProviders(classloader);
         if (cachedContextClassLoaderProviderList != null) {
            return cachedContextClassLoaderProviderList;
         } else {
            List<ValidationProvider<?>> validationProviderList = this.loadProviders(classloader);
            if (validationProviderList.isEmpty()) {
               classloader = Validation.DefaultValidationProviderResolver.class.getClassLoader();
               List<ValidationProvider<?>> cachedCurrentClassLoaderProviderList = this.getCachedValidationProviders(classloader);
               if (cachedCurrentClassLoaderProviderList != null) {
                  return cachedCurrentClassLoaderProviderList;
               }

               validationProviderList = this.loadProviders(classloader);
            }

            this.cacheValidationProviders(classloader, validationProviderList);
            return validationProviderList;
         }
      }

      private List<ValidationProvider<?>> loadProviders(ClassLoader classloader) {
         ServiceLoader<ValidationProvider> loader = ServiceLoader.load(ValidationProvider.class, classloader);
         Iterator<ValidationProvider> providerIterator = loader.iterator();
         List<ValidationProvider<?>> validationProviderList = new ArrayList<>();

         while (providerIterator.hasNext()) {
            try {
               validationProviderList.add(providerIterator.next());
            } catch (ServiceConfigurationError var6) {
            }
         }

         return validationProviderList;
      }

      private synchronized List<ValidationProvider<?>> getCachedValidationProviders(ClassLoader classLoader) {
         SoftReference<List<ValidationProvider<?>>> ref = this.providersPerClassloader.get(classLoader);
         return ref != null ? ref.get() : null;
      }

      private synchronized void cacheValidationProviders(ClassLoader classLoader, List<ValidationProvider<?>> providers) {
         this.providersPerClassloader.put(classLoader, new SoftReference<>(providers));
      }
   }

   private static class NewProviderInstance<T extends ValidationProvider<?>> implements PrivilegedAction<T> {
      private final Class<T> clazz;

      public static <T extends ValidationProvider<?>> Validation.NewProviderInstance<T> action(Class<T> clazz) {
         return new Validation.NewProviderInstance<>(clazz);
      }

      private NewProviderInstance(Class<T> clazz) {
         this.clazz = clazz;
      }

      public T run() {
         try {
            return this.clazz.newInstance();
         } catch (IllegalAccessException | RuntimeException | InstantiationException var2) {
            throw new ValidationException("Cannot instantiate provider type: " + this.clazz, var2);
         }
      }
   }

   private static class ProviderSpecificBootstrapImpl<T extends Configuration<T>, U extends ValidationProvider<T>> implements ProviderSpecificBootstrap<T> {
      private final Class<U> validationProviderClass;
      private ValidationProviderResolver resolver;

      public ProviderSpecificBootstrapImpl(Class<U> validationProviderClass) {
         this.validationProviderClass = validationProviderClass;
      }

      @Override
      public ProviderSpecificBootstrap<T> providerResolver(ValidationProviderResolver resolver) {
         this.resolver = resolver;
         return this;
      }

      @Override
      public T configure() {
         if (this.validationProviderClass == null) {
            throw new ValidationException("builder is mandatory. Use Validation.byDefaultProvider() to use the generic provider discovery mechanism");
         } else {
            Validation.GenericBootstrapImpl state = new Validation.GenericBootstrapImpl();
            if (this.resolver == null) {
               U provider = this.run(Validation.NewProviderInstance.action(this.validationProviderClass));
               return provider.createSpecializedConfiguration(state);
            } else {
               state.providerResolver(this.resolver);

               List<ValidationProvider<?>> resolvers;
               try {
                  resolvers = this.resolver.getValidationProviders();
               } catch (RuntimeException var6) {
                  throw new ValidationException("Unable to get available provider resolvers.", var6);
               }

               for (ValidationProvider<?> provider : resolvers) {
                  if (this.validationProviderClass.isAssignableFrom(provider.getClass())) {
                     ValidationProvider<T> specificProvider = this.validationProviderClass.cast(provider);
                     return specificProvider.createSpecializedConfiguration(state);
                  }
               }

               throw new ValidationException("Unable to find provider: " + this.validationProviderClass);
            }
         }
      }

      private <P> P run(PrivilegedAction<P> action) {
         return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
      }
   }
}
