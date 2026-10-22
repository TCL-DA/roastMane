package org.glassfish.jersey.inject.hk2;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.jersey.internal.inject.DisposableSupplier;

public class SupplierFactoryBridge<T> implements Factory<T> {
   private final ServiceLocator locator;
   private final ParameterizedType beanType;
   private final String beanName;
   private final boolean disposable;
   private Map<Object, DisposableSupplier<T>> disposableSuppliers = Collections.synchronizedMap(new IdentityHashMap<>());

   SupplierFactoryBridge(ServiceLocator locator, Type beanType, String beanName, boolean disposable) {
      this.locator = locator;
      this.beanType = new ParameterizedTypeImpl(Supplier.class, new Type[]{beanType});
      this.beanName = beanName;
      this.disposable = disposable;
   }

   public T provide() {
      if (this.beanType != null) {
         Supplier<T> supplier = (Supplier<T>)this.locator.getService(this.beanType, this.beanName, new Annotation[0]);
         T instance = supplier.get();
         if (this.disposable) {
            this.disposableSuppliers.put(instance, (DisposableSupplier<T>)supplier);
         }

         return instance;
      } else {
         return null;
      }
   }

   public void dispose(T instance) {
      if (this.disposable) {
         DisposableSupplier<T> disposableSupplier = this.disposableSuppliers.remove(instance);
         disposableSupplier.dispose(instance);
      }
   }
}
