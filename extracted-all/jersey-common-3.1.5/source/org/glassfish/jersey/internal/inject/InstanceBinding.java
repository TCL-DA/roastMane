package org.glassfish.jersey.internal.inject;

import java.lang.reflect.Type;

public class InstanceBinding<T> extends Binding<T, InstanceBinding<T>> {
   private final T service;

   InstanceBinding(T service) {
      this(service, null);
   }

   InstanceBinding(T service, Type contractType) {
      this.service = service;
      if (contractType != null) {
         this.to(contractType);
      }

      this.asType(service.getClass());
   }

   public T getService() {
      return this.service;
   }
}
