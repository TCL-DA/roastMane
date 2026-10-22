package org.glassfish.jersey.inject.hk2;

import java.util.function.Supplier;
import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.internal.inject.DisposableSupplier;

public class InstanceSupplierFactoryBridge<T> implements Factory<T> {
   private Supplier<T> supplier;
   private boolean disposable;

   InstanceSupplierFactoryBridge(Supplier<T> supplier, boolean disposable) {
      this.supplier = supplier;
      this.disposable = disposable;
   }

   public T provide() {
      return this.supplier.get();
   }

   public void dispose(T instance) {
      if (this.disposable) {
         ((DisposableSupplier)this.supplier).dispose(instance);
      }
   }
}
