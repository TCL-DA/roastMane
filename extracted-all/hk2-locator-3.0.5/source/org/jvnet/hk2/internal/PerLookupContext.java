package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;

public class PerLookupContext implements Context<PerLookup> {
   public Class<? extends Annotation> getScope() {
      return PerLookup.class;
   }

   public <T> T findOrCreate(ActiveDescriptor<T> activeDescriptor, ServiceHandle<?> root) {
      return (T)activeDescriptor.create(root);
   }

   public boolean containsKey(ActiveDescriptor<?> descriptor) {
      return false;
   }

   public boolean isActive() {
      return true;
   }

   public boolean supportsNullCreation() {
      return true;
   }

   public void shutdown() {
   }

   public void destroyOne(ActiveDescriptor<?> descriptor) {
   }
}
