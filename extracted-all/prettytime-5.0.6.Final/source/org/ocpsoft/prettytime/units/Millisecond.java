package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Millisecond extends ResourcesTimeUnit {
   public Millisecond() {
      this.setMillisPerUnit(1L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Millisecond";
   }
}
