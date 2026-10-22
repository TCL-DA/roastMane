package org.ocpsoft.prettytime.units;

import org.ocpsoft.prettytime.impl.ResourcesTimeUnit;

public class Century extends ResourcesTimeUnit {
   public Century() {
      this.setMillisPerUnit(3155692597470L);
   }

   @Override
   protected String getResourceKeyPrefix() {
      return "Century";
   }
}
