package org.jfree.data.time;

import java.io.Serializable;
import org.jfree.chart.util.Args;

public class TimePeriodValue implements Cloneable, Serializable {
   private static final long serialVersionUID = 3390443360845711275L;
   private TimePeriod period;
   private Number value;

   public TimePeriodValue(TimePeriod period, Number value) {
      Args.nullNotPermitted(period, "period");
      this.period = period;
      this.value = value;
   }

   public TimePeriodValue(TimePeriod period, double value) {
      this(period, new Double(value));
   }

   public TimePeriod getPeriod() {
      return this.period;
   }

   public Number getValue() {
      return this.value;
   }

   public void setValue(Number value) {
      this.value = value;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof TimePeriodValue)) {
         return false;
      } else {
         TimePeriodValue timePeriodValue = (TimePeriodValue)obj;
         if (this.period != null ? this.period.equals(timePeriodValue.period) : timePeriodValue.period == null) {
            return this.value != null ? this.value.equals(timePeriodValue.value) : timePeriodValue.value == null;
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int result = this.period != null ? this.period.hashCode() : 0;
      return 29 * result + (this.value != null ? this.value.hashCode() : 0);
   }

   @Override
   public Object clone() {
      Object clone = null;

      try {
         return super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new RuntimeException(var3);
      }
   }

   @Override
   public String toString() {
      return "TimePeriodValue[" + this.getPeriod() + "," + this.getValue() + "]";
   }
}
