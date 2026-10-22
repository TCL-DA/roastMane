package org.apache.commons.math3.optim;

import java.io.Serializable;
import org.apache.commons.math3.util.Pair;

public class PointVectorValuePair extends Pair<double[], double[]> implements Serializable {
   private static final long serialVersionUID = 20120513L;

   public PointVectorValuePair(double[] point, double[] value) {
      this(point, value, true);
   }

   public PointVectorValuePair(double[] point, double[] value, boolean copyArray) {
      super(copyArray ? (point == null ? null : (double[])point.clone()) : point, copyArray ? (value == null ? null : (double[])value.clone()) : value);
   }

   public double[] getPoint() {
      double[] p = this.getKey();
      return p == null ? null : (double[])p.clone();
   }

   public double[] getPointRef() {
      return this.getKey();
   }

   public double[] getValue() {
      double[] v = (double[])super.getValue();
      return v == null ? null : (double[])v.clone();
   }

   public double[] getValueRef() {
      return (double[])super.getValue();
   }

   private Object writeReplace() {
      return new PointVectorValuePair.DataTransferObject(this.getKey(), this.getValue());
   }

   private static class DataTransferObject implements Serializable {
      private static final long serialVersionUID = 20120513L;
      private final double[] point;
      private final double[] value;

      DataTransferObject(double[] point, double[] value) {
         this.point = (double[])point.clone();
         this.value = (double[])value.clone();
      }

      private Object readResolve() {
         return new PointVectorValuePair(this.point, this.value, false);
      }
   }
}
