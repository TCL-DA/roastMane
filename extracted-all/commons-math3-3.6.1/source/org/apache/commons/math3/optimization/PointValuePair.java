package org.apache.commons.math3.optimization;

import java.io.Serializable;
import org.apache.commons.math3.util.Pair;

@Deprecated
public class PointValuePair extends Pair<double[], Double> implements Serializable {
   private static final long serialVersionUID = 20120513L;

   public PointValuePair(double[] point, double value) {
      this(point, value, true);
   }

   public PointValuePair(double[] point, double value, boolean copyArray) {
      super(copyArray ? (point == null ? null : (double[])point.clone()) : point, value);
   }

   public double[] getPoint() {
      double[] p = this.getKey();
      return p == null ? null : (double[])p.clone();
   }

   public double[] getPointRef() {
      return this.getKey();
   }

   private Object writeReplace() {
      return new PointValuePair.DataTransferObject(this.getKey(), this.getValue());
   }

   private static class DataTransferObject implements Serializable {
      private static final long serialVersionUID = 20120513L;
      private final double[] point;
      private final double value;

      DataTransferObject(double[] point, double value) {
         this.point = (double[])point.clone();
         this.value = value;
      }

      private Object readResolve() {
         return new PointValuePair(this.point, this.value, false);
      }
   }
}
