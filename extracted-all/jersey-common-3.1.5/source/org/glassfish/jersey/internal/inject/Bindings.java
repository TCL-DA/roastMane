package org.glassfish.jersey.internal.inject;

import jakarta.ws.rs.core.GenericType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.util.ReflectionHelper;

public final class Bindings {
   private Bindings() {
      throw new AssertionError("Utility class instantiation forbidden.");
   }

   public static Collection<Binding> getBindings(InjectionManager injectionManager, Binder binder) {
      if (binder instanceof AbstractBinder) {
         ((AbstractBinder)binder).setInjectionManager(injectionManager);
      }

      return binder.getBindings();
   }

   public static <T> ClassBinding<T> service(Class<T> serviceType) {
      return new ClassBinding<>(serviceType);
   }

   public static <T> ClassBinding<T> serviceAsContract(Class<T> serviceType) {
      return new ClassBinding<>(serviceType).to(serviceType);
   }

   public static <T> ClassBinding<T> service(GenericType<T> serviceType) {
      return new ClassBinding(serviceType.getRawType()).asType((Class)serviceType.getType());
   }

   public static <T> ClassBinding<T> serviceAsContract(GenericType<T> serviceType) {
      return new ClassBinding(serviceType.getRawType()).asType((Class)serviceType.getType()).to(serviceType.getType());
   }

   public static <T> ClassBinding<T> serviceAsContract(Type serviceType) {
      return (ClassBinding<T>)new ClassBinding<>(ReflectionHelper.getRawClass(serviceType)).asType((Class)serviceType).to(serviceType);
   }

   public static <T> InstanceBinding<T> service(T service) {
      return new InstanceBinding<>(service);
   }

   public static <T> InstanceBinding<T> serviceAsContract(T service) {
      return new InstanceBinding<>(service, service.getClass());
   }

   public static <T> SupplierClassBinding<T> supplier(Class<? extends Supplier<T>> supplierType, Class<? extends Annotation> supplierScope) {
      return new SupplierClassBinding<>(supplierType, supplierScope);
   }

   public static <T> SupplierClassBinding<T> supplier(Class<? extends Supplier<T>> supplierType) {
      return new SupplierClassBinding<>(supplierType, null);
   }

   public static <T> SupplierInstanceBinding<T> supplier(Supplier<T> supplier) {
      return new SupplierInstanceBinding<>(supplier);
   }

   public static <T extends InjectionResolver> InjectionResolverBinding<T> injectionResolver(T resolver) {
      return new InjectionResolverBinding<>(resolver);
   }
}
