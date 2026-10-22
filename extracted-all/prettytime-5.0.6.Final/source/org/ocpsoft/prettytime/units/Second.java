package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Second extends ResourcesTimeUnit {
   public Second() {
      this.setMillisPerUnit(1000L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Second";
   }
}
