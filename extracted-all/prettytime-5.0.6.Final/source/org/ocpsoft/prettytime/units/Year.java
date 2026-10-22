package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Year extends ResourcesTimeUnit {
   public Year() {
      this.setMillisPerUnit(31556925960L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Year";
   }
}
