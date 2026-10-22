package org.glassfish.jersey.internal.inject;

import java.util.function.Consumer;

public interface ForeignDescriptor {
   Object get();

   void dispose(Object var1);

   static ForeignDescriptor wrap(Object descriptor) {
      return new ForeignDescriptorImpl(descriptor);
   }

   static ForeignDescriptor wrap(Object descriptor, Consumer<Object> disposeInstance) {
      return new ForeignDescriptorImpl(descriptor, disposeInstance);
   }
}
