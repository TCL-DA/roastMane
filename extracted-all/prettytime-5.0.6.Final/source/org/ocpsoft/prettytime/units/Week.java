package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Week extends ResourcesTimeUnit {
   public Week() {
      this.setMillisPerUnit(604800000L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Week";
   }
}
