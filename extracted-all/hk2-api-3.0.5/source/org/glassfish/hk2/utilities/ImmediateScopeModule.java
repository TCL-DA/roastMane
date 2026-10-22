package org.glassfish.hk2.utilities;

import org.glassfish.hk2.internal.ImmediateHelper;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ImmediateScopeModule extends AbstractBinder {
   @Override
   protected void configure() {
      this.addActiveDescriptor(ImmediateContext.class);
      this.addActiveDescriptor(ImmediateHelper.class);
   }
}
