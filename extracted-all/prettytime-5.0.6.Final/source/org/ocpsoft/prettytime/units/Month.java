package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Month extends ResourcesTimeUnit {
   public Month() {
      this.setMillisPerUnit(2629743830L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Month";
   }
}
