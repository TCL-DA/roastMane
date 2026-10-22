package org.glassfish.hk2.utilities;

import org.glassfish.hk2.internal.InheritableThreadContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class InheritableThreadScopeModule extends AbstractBinder {
   @Override
   protected void configure() {
      this.addActiveDescriptor(InheritableThreadContext.class);
   }
}
