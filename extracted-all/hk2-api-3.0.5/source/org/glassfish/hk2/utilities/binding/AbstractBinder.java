package org.glassfish.hk2.utilities.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.TwoPhaseResource;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.DescriptorImpl;

public abstract class AbstractBinder implements Binder, DynamicConfiguration {
   private transient DynamicConfiguration configuration;
   private transient AbstractBindingBuilder<?> currentBuilder;
   private transient HK2Loader defaultLoader;

   public <T> ServiceBindingBuilder<T> bind(Class<T> serviceType) {
      return this.resetBuilder(AbstractBindingBuilder.create(serviceType, false));
   }

   public <T> ServiceBindingBuilder<T> bindAsContract(Class<T> serviceType) {
      return this.resetBuilder(AbstractBindingBuilder.create(serviceType, true));
   }

   public <T> ServiceBindingBuilder<T> bindAsContract(TypeLiteral<T> serviceType) {
      return this.resetBuilder(AbstractBindingBuilder.create(serviceType, true));
   }

   public <T> ServiceBindingBuilder<T> bindAsContract(Type serviceType) {
      return this.resetBuilder(AbstractBindingBuilder.create(serviceType, true));
   }

   public <T> ScopedBindingBuilder<T> bind(T service) {
      return this.resetBuilder(AbstractBindingBuilder.create(service));
   }

   public <T> ServiceBindingBuilder<T> bindFactory(Class<? extends Factory<T>> factoryType, Class<? extends Annotation> factoryScope) {
      return this.resetBuilder(AbstractBindingBuilder.createFactoryBinder(factoryType, factoryScope));
   }

   public <T> ServiceBindingBuilder<T> bindFactory(Class<? extends Factory<T>> factoryType) {
      return this.resetBuilder(AbstractBindingBuilder.createFactoryBinder(factoryType, null));
   }

   public <T> ServiceBindingBuilder<T> bindFactory(Factory<T> factory) {
      return this.resetBuilder(AbstractBindingBuilder.createFactoryBinder(factory));
   }

   @Override
   public void bind(DynamicConfiguration configuration) {
      if (this.configuration != null) {
         throw new IllegalArgumentException("Recursive configuration call detected.");
      } else if (configuration == null) {
         throw new NullPointerException("configuration");
      } else {
         this.configuration = configuration;

         try {
            this.configure();
         } finally {
            this.complete();
         }
      }
   }

   private <T> AbstractBindingBuilder<T> resetBuilder(AbstractBindingBuilder<T> newBuilder) {
      if (this.currentBuilder != null) {
         this.currentBuilder.complete(this.configuration(), this.getDefaultBinderLoader());
      }

      this.currentBuilder = newBuilder;
      return newBuilder;
   }

   private void complete() {
      try {
         this.resetBuilder(null);
      } finally {
         this.configuration = null;
      }
   }

   protected abstract void configure();

   private DynamicConfiguration configuration() {
      if (this.configuration == null) {
         throw new IllegalArgumentException("Dynamic configuration accessed from outside of an active binder configuration scope.");
      } else {
         return this.configuration;
      }
   }

   @Override
   public <T> ActiveDescriptor<T> bind(Descriptor descriptor) {
      return this.bind(descriptor, true);
   }

   @Override
   public <T> ActiveDescriptor<T> bind(Descriptor descriptor, boolean requiresDeepCopy) {
      this.setLoader(descriptor);
      return this.configuration().bind(descriptor, requiresDeepCopy);
   }

   @Override
   public FactoryDescriptors bind(FactoryDescriptors factoryDescriptors) {
      return this.bind(factoryDescriptors, true);
   }

   @Override
   public FactoryDescriptors bind(FactoryDescriptors factoryDescriptors, boolean requiresDeepCopy) {
      this.setLoader(factoryDescriptors.getFactoryAsAService());
      this.setLoader(factoryDescriptors.getFactoryAsAFactory());
      return this.configuration().bind(factoryDescriptors, requiresDeepCopy);
   }

   @Override
   public <T> ActiveDescriptor<T> addActiveDescriptor(ActiveDescriptor<T> activeDescriptor) throws IllegalArgumentException {
      return this.addActiveDescriptor(activeDescriptor, true);
   }

   @Override
   public <T> ActiveDescriptor<T> addActiveDescriptor(ActiveDescriptor<T> activeDescriptor, boolean requiresDeepCopy) throws IllegalArgumentException {
      return this.configuration().addActiveDescriptor(activeDescriptor, requiresDeepCopy);
   }

   @Override
   public <T> ActiveDescriptor<T> addActiveDescriptor(Class<T> rawClass) throws MultiException, IllegalArgumentException {
      return this.configuration().addActiveDescriptor(rawClass);
   }

   @Override
   public <T> FactoryDescriptors addActiveFactoryDescriptor(Class<? extends Factory<T>> rawFactoryClass) throws MultiException, IllegalArgumentException {
      return this.configuration().addActiveFactoryDescriptor(rawFactoryClass);
   }

   @Override
   public void addUnbindFilter(Filter unbindFilter) throws IllegalArgumentException {
      this.configuration().addUnbindFilter(unbindFilter);
   }

   @Override
   public void addIdempotentFilter(Filter... unbindFilter) throws IllegalArgumentException {
      this.configuration().addIdempotentFilter(unbindFilter);
   }

   @Override
   public void registerTwoPhaseResources(TwoPhaseResource... resources) {
      this.configuration().registerTwoPhaseResources(resources);
   }

   @Override
   public void commit() throws MultiException {
      this.configuration().commit();
   }

   public final void install(Binder... binders) {
      for (Binder binder : binders) {
         binder.bind(this);
      }
   }

   private void setLoader(Descriptor descriptor) {
      if (descriptor.getLoader() == null && descriptor instanceof DescriptorImpl) {
         ((DescriptorImpl)descriptor).setLoader(this.getDefaultBinderLoader());
      }
   }

   private HK2Loader getDefaultBinderLoader() {
      if (this.defaultLoader == null) {
         final ClassLoader binderClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
               ClassLoader loader = this.getClass().getClassLoader();
               return loader == null ? ClassLoader.getSystemClassLoader() : loader;
            }
         });
         this.defaultLoader = new HK2Loader() {
            @Override
            public Class<?> loadClass(String className) throws MultiException {
               try {
                  return binderClassLoader.loadClass(className);
               } catch (ClassNotFoundException var3) {
                  throw new MultiException(var3);
               }
            }
         };
      }

      return this.defaultLoader;
   }
}
