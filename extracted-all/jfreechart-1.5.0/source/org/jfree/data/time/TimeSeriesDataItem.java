package org.jfree.data.time;

import java.io.Serializable;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;

public class TimeSeriesDataItem implements Cloneable, Comparable, Serializable {
   private static final long serialVersionUID = -2235346966016401302L;
   private RegularTimePeriod period;
   private Number value;

   public TimeSeriesDataItem(RegularTimePeriod period, Number value) {
      Args.nullNotPermitted(period, "period");
      this.period = period;
      this.value = value;
   }

   public TimeSeriesDataItem(RegularTimePeriod period, double value) {
      this(period, new Double(value));
   }

   public RegularTimePeriod getPeriod() {
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
      } else if (!(obj instanceof TimeSeriesDataItem)) {
         return false;
      } else {
         TimeSeriesDataItem that = (TimeSeriesDataItem)obj;
         return !ObjectUtils.equal(this.period, that.period) ? false : ObjectUtils.equal(this.value, that.value);
      }
   }

   @Override
   public int hashCode() {
      int result = this.period != null ? this.period.hashCode() : 0;
      return 29 * result + (this.value != null ? this.value.hashCode() : 0);
   }

   @Override
   public int compareTo(Object o1) {
      int result;
      if (o1 instanceof TimeSeriesDataItem) {
         TimeSeriesDataItem datapair = (TimeSeriesDataItem)o1;
         result = this.getPeriod().compareTo(datapair.getPeriod());
      } else {
         result = 1;
      }

      return result;
   }

   @Override
   public Object clone() {
      Object clone = null;

      try {
         clone = super.clone();
      } catch (CloneNotSupportedException var3) {
         var3.printStackTrace();
      }

      return clone;
   }
}
