package org.ocpsoft.prettytime.impl;

import org.ocpsoft.prettytime.TimeUnit;

public abstract class ResourcesTimeUnit implements TimeUnit {
   private static long ID = 0L;
   private long id = 0L;
   private long maxQuantity = 0L;
   private long millisPerUnit = 1L;

   public ResourcesTimeUnit() {
      this.id = ID++;
   }

   protected abstract String getResourceKeyPrefix();

   protected final String getResourceBundleName() {
      return "org.ocpsoft.prettytime.i18n.Resources";
   }

   @Override
   public long getMaxQuantity() {
      return this.maxQuantity;
   }

   public void setMaxQuantity(long maxQuantity) {
      this.maxQuantity = maxQuantity;
   }

   @Override
   public long getMillisPerUnit() {
      return this.millisPerUnit;
   }

   public void setMillisPerUnit(long millisPerUnit) {
      this.millisPerUnit = millisPerUnit;
   }

   @Override
   public boolean isPrecise() {
      return true;
   }

   @Override
   public String toString() {
      return this.getResourceKeyPrefix();
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      return 31 * result + Long.hashCode(this.id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         ResourcesTimeUnit other = (ResourcesTimeUnit)obj;
         return this.maxQuantity != other.maxQuantity ? false : this.millisPerUnit == other.millisPerUnit;
      }
   }
}
