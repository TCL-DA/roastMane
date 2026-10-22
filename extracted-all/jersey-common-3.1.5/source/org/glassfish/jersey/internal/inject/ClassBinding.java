package org.glassfish.jersey.internal.inject;

public class ClassBinding<T> extends Binding<T, ClassBinding<T>> {
   private final Class<T> service;

   ClassBinding(Class<T> service) {
      this.service = service;
      this.asType(service);
   }

   public Class<T> getService() {
      return this.service;
   }
}
