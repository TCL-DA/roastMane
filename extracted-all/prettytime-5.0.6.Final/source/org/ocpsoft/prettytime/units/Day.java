package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Day extends ResourcesTimeUnit {
   public Day() {
      this.setMillisPerUnit(86400000L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Day";
   }
}
