package org.jfree.data.statistics;

import java.io.Serializable;
import org.jfree.chart.util.ObjectUtils;

public class MeanAndStandardDeviation implements Serializable {
   private static final long serialVersionUID = 7413468697315721515L;
   private Number mean;
   private Number standardDeviation;

   public MeanAndStandardDeviation(double mean, double standardDeviation) {
      this(new Double(mean), new Double(standardDeviation));
   }

   public MeanAndStandardDeviation(Number mean, Number standardDeviation) {
      this.mean = mean;
      this.standardDeviation = standardDeviation;
   }

   public Number getMean() {
      return this.mean;
   }

   public double getMeanValue() {
      double result = Double.NaN;
      if (this.mean != null) {
         result = this.mean.doubleValue();
      }

      return result;
   }

   public Number getStandardDeviation() {
      return this.standardDeviation;
   }

   public double getStandardDeviationValue() {
      double result = Double.NaN;
      if (this.standardDeviation != null) {
         result = this.standardDeviation.doubleValue();
      }

      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof MeanAndStandardDeviation)) {
         return false;
      } else {
         MeanAndStandardDeviation that = (MeanAndStandardDeviation)obj;
         return !ObjectUtils.equal(this.mean, that.mean) ? false : ObjectUtils.equal(this.standardDeviation, that.standardDeviation);
      }
   }

   @Override
   public String toString() {
      return "[" + this.mean + ", " + this.standardDeviation + "]";
   }
}
