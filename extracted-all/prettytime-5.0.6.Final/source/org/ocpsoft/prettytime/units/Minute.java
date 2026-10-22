package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Minute extends ResourcesTimeUnit {
   public Minute() {
      this.setMillisPerUnit(60000L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Minute";
   }
}
