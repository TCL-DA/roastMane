package org.jvnet.hk2.internal;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.UnsatisfiedDependencyException;

@Named("SystemInjectResolver")
public class ThreeThirtyResolver implements InjectionResolver<Inject> {
   private final ServiceLocatorImpl locator;

   ThreeThirtyResolver(ServiceLocatorImpl locator) {
      this.locator = locator;
   }

   public Object resolve(Injectee injectee, ServiceHandle<?> root) {
      ActiveDescriptor<?> ad = this.locator.getInjecteeDescriptor(injectee);
      if (ad == null) {
         if (injectee.isOptional()) {
            return null;
         } else {
            throw new MultiException(new UnsatisfiedDependencyException(injectee, this.locator.getName()));
         }
      } else {
         return this.locator.getService(ad, root, injectee);
      }
   }

   public boolean isConstructorParameterIndicator() {
      return false;
   }

   public boolean isMethodParameterIndicator() {
      return false;
   }

   @Override
   public String toString() {
      return "ThreeThirtyResolver(" + this.locator + "," + System.identityHashCode(this) + ")";
   }
}
