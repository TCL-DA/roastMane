package org.glassfish.jersey.internal.inject;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

public class SupplierClassBinding<T> extends Binding<Supplier<T>, SupplierClassBinding<T>> {
   private final Class<? extends Supplier<T>> supplierClass;
   private final Class<? extends Annotation> supplierScope;

   SupplierClassBinding(Class<? extends Supplier<T>> supplierClass, Class<? extends Annotation> scope) {
      this.supplierClass = supplierClass;
      this.supplierScope = scope;
   }

   public Class<? extends Supplier<T>> getSupplierClass() {
      return this.supplierClass;
   }

   public Class<? extends Annotation> getSupplierScope() {
      return this.supplierScope;
   }
}
