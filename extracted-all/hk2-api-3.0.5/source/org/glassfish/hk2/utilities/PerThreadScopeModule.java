package org.glassfish.hk2.utilities;

import org.glassfish.hk2.internal.PerThreadContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class PerThreadScopeModule extends AbstractBinder {
   @Override
   protected void configure() {
      this.addActiveDescriptor(PerThreadContext.class);
   }
}
