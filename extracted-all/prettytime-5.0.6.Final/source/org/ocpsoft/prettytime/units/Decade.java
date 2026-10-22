package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Decade extends ResourcesTimeUnit {
   public Decade() {
      this.setMillisPerUnit(315569259747L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Decade";
   }
}
