package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Millennium extends ResourcesTimeUnit {
   public Millennium() {
      this.setMillisPerUnit(31556926000000L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Millennium";
   }
}
