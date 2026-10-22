package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class JustNow extends ResourcesTimeUnit {
   public JustNow() {
      this.setMaxQuantity(60000L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "JustNow";
   }

   @Override
   public boolean isPrecise() {
      return false;
   }
}
