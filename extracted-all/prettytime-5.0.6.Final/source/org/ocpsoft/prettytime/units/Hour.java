package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Hour extends ResourcesTimeUnit {
   public Hour() {
      this.setMillisPerUnit(3600000L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Hour";
   }
}
