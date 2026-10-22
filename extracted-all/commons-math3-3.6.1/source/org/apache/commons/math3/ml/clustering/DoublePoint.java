package org.apache.commons.math3.ml.clustering;

import java.io.Serializable;
import java.util.Arrays;

public class DoublePoint implements Clusterable, Serializable {
   private static final long serialVersionUID = 3946024775784901369L;
   private final double[] point;

   public DoublePoint(double[] point) {
      this.point = point;
   }

   public DoublePoint(int[] point) {
      this.point = new double[point.length];

      for (int i = 0; i < point.length; i++) {
         this.point[i] = (double)point[i];
      }
   }

   @Override
   public double[] getPoint() {
      return this.point;
   }

   @Override
   public boolean equals(Object other) {
      return !(other instanceof DoublePoint) ? false : Arrays.equals(this.point, ((DoublePoint)other).point);
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.point);
   }

   @Override
   public String toString() {
      return Arrays.toString(this.point);
   }
}
