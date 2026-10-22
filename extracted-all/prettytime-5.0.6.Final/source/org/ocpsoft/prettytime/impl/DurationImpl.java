package org.ocpsoft.prettytime.impl;

import java.util.Objects;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.TimeUnit;

public class DurationImpl implements Duration {
   private long quantity;
   private long delta;
   private TimeUnit unit;

   @Override
   public long getQuantity() {
      return this.quantity;
   }

   public void setQuantity(long quantity) {
      this.quantity = quantity;
   }

   @Override
   public TimeUnit getUnit() {
      return this.unit;
   }

   public void setUnit(TimeUnit unit) {
      this.unit = unit;
   }

   @Override
   public long getDelta() {
      return this.delta;
   }

   public void setDelta(long delta) {
      this.delta = delta;
   }

   @Override
   public boolean isInPast() {
      return this.getQuantity() < 0L;
   }

   @Override
   public boolean isInFuture() {
      return !this.isInPast();
   }

   @Override
   public long getQuantityRounded(int tolerance) {
      long quantity = Math.abs(this.getQuantity());
      if (this.getDelta() != 0L) {
         double threshold = Math.abs((double)this.getDelta() / (double)this.getUnit().getMillisPerUnit() * 100.0);
         if (threshold > (double)tolerance) {
            quantity++;
         }
      }

      return quantity;
   }

   @Override
   public String toString() {
      return "DurationImpl [" + this.quantity + " " + this.unit + ", delta=" + this.delta + "]";
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + Long.hashCode(this.delta);
      result = 31 * result + Long.hashCode(this.quantity);
      return 31 * result + Objects.hashCode(this.unit);
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
         DurationImpl other = (DurationImpl)obj;
         if (this.delta != other.delta) {
            return false;
         } else {
            return this.quantity != other.quantity ? false : Objects.equals(this.unit, other.unit);
         }
      }
   }
}
