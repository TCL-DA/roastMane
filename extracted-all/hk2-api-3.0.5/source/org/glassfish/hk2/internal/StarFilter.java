package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;

public class StarFilter implements Filter {
   private static StarFilter INSTANCE = new StarFilter();

   public static StarFilter getDescriptorFilter() {
      return INSTANCE;
   }

   @Override
   public boolean matches(Descriptor d) {
      return true;
   }
}
