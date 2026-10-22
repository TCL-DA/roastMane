package org.glassfish.jersey.internal.inject;

import jakarta.inject.Provider;
import jakarta.ws.rs.core.GenericType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.LocalizationMessages;

public abstract class AbstractBinder implements Binder {
   private List<Binding> internalBindings = new ArrayList<>();
   private List<AbstractBinder> installed = new ArrayList<>();
   private InjectionManager injectionManager;
   private boolean configured = false;

   protected abstract void configure();

   void setInjectionManager(InjectionManager injectionManager) {
      this.injectionManager = injectionManager;
   }

   protected final <T> Provider<T> createManagedInstanceProvider(Class<T> clazz) {
      return () -> {
         if (this.injectionManager == null) {
            throw new IllegalStateException(LocalizationMessages.INJECTION_MANAGER_NOT_PROVIDED());
         } else {
            return this.injectionManager.getInstance(clazz);
         }
      };
   }

   public <T> ClassBinding<T> bind(Class<T> serviceType) {
      ClassBinding<T> binding = Bindings.service(serviceType);
      this.internalBindings.add(binding);
      return binding;
   }

   public Binding bind(Binding binding) {
      this.internalBindings.add(binding);
      return binding;
   }

   public <T> ClassBinding<T> bindAsContract(Class<T> serviceType) {
      ClassBinding<T> binding = Bindings.serviceAsContract(serviceType);
      this.internalBindings.add(binding);
      return binding;
   }

   public <T> ClassBinding<T> bindAsContract(GenericType<T> serviceType) {
      ClassBinding<T> binding = Bindings.service(serviceType);
      this.internalBindings.add(binding);
      return binding;
   }

   public ClassBinding<Object> bindAsContract(Type serviceType) {
      ClassBinding<Object> binding = Bindings.serviceAsContract(serviceType);
      this.internalBindings.add(binding);
      return binding;
   }

   public <T> InstanceBinding<T> bind(T service) {
      InstanceBinding<T> binding = Bindings.service(service);
      this.internalBindings.add(binding);
      return binding;
   }

   public <T> SupplierClassBinding<T> bindFactory(Class<? extends Supplier<T>> supplierType, Class<? extends Annotation> supplierScope) {
      SupplierClassBinding<T> binding = Bindings.supplier(supplierType, supplierScope);
      this.internalBindings.add(binding);
      return binding;
   }

   public <T> SupplierClassBinding<T> bindFactory(Class<? extends Supplier<T>> supplierType) {
      SupplierClassBinding<T> binding = Bindings.supplier(supplierType);
      this.internalBindings.add(binding);
      return binding;
   }

   public <T> SupplierInstanceBinding<T> bindFactory(Supplier<T> factory) {
      SupplierInstanceBinding<T> binding = Bindings.supplier(factory);
      this.internalBindings.add(binding);
      return binding;
   }

   public <T extends InjectionResolver> InjectionResolverBinding<T> bind(T resolver) {
      InjectionResolverBinding<T> binding = Bindings.injectionResolver(resolver);
      this.internalBindings.add(binding);
      return binding;
   }

   public final void install(AbstractBinder... binders) {
      Arrays.stream(binders).filter(Objects::nonNull).forEach(this.installed::add);
   }

   @Override
   public Collection<Binding> getBindings() {
      this.invokeConfigure();
      List<Binding> bindings = this.installed
         .stream()
         .flatMap(binder -> Bindings.getBindings(this.injectionManager, binder).stream())
         .collect(Collectors.toList());
      bindings.addAll(this.internalBindings);
      return bindings;
   }

   private void invokeConfigure() {
      if (!this.configured) {
         this.configure();
         this.configured = true;
      }
   }
}
