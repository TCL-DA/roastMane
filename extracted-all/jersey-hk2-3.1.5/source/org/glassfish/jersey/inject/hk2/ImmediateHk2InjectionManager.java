package org.glassfish.jersey.inject.hk2;

import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.Binding;

public class ImmediateHk2InjectionManager extends AbstractHk2InjectionManager {
   ImmediateHk2InjectionManager(Object parent) {
      super(parent);
   }

   public void completeRegistration() throws IllegalStateException {
   }

   public void register(Binding binding) {
      Hk2Helper.bind(this.getServiceLocator(), binding);
   }

   public void register(Iterable<Binding> descriptors) {
      Hk2Helper.bind(this.getServiceLocator(), descriptors);
   }

   public void register(Binder binder) {
      Hk2Helper.bind(this, binder);
   }

   public void register(Object provider) {
      if (this.isRegistrable(provider.getClass())) {
         ServiceLocatorUtilities.bind(this.getServiceLocator(), new org.glassfish.hk2.utilities.Binder[]{(org.glassfish.hk2.utilities.Binder)provider});
      } else {
         if (!Class.class.isInstance(provider) || !this.isRegistrable((Class)provider)) {
            throw new IllegalArgumentException(LocalizationMessages.HK_2_PROVIDER_NOT_REGISTRABLE(provider.getClass()));
         }

         ServiceLocatorUtilities.bind(
            this.getServiceLocator(), new org.glassfish.hk2.utilities.Binder[]{(org.glassfish.hk2.utilities.Binder)this.createAndInitialize((Class)provider)}
         );
      }
   }
}
