package org.glassfish.jersey.internal.inject;

public class InjectionResolverBinding<T extends InjectionResolver> extends Binding<T, InjectionResolverBinding<T>> {
   private final T resolver;

   InjectionResolverBinding(T resolver) {
      this.resolver = resolver;
   }

   public T getResolver() {
      return this.resolver;
   }
}
