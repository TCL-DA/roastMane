package org.apache.commons.math3.stat.clustering;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.math3.util.MathArrays;

@Deprecated
public class EuclideanDoublePoint implements Clusterable<EuclideanDoublePoint>, Serializable {
   private static final long serialVersionUID = 8026472786091227632L;
   private final double[] point;

   public EuclideanDoublePoint(double[] point) {
      this.point = point;
   }

   public EuclideanDoublePoint centroidOf(Collection<EuclideanDoublePoint> points) {
      double[] centroid = new double[this.getPoint().length];

      for (EuclideanDoublePoint p : points) {
         for (int i = 0; i < centroid.length; i++) {
            centroid[i] += p.getPoint()[i];
         }
      }

      for (int i = 0; i < centroid.length; i++) {
         centroid[i] /= (double)points.size();
      }

      return new EuclideanDoublePoint(centroid);
   }

   public double distanceFrom(EuclideanDoublePoint p) {
      return MathArrays.distance(this.point, p.getPoint());
   }

   @Override
   public boolean equals(Object other) {
      return !(other instanceof EuclideanDoublePoint) ? false : Arrays.equals(this.point, ((EuclideanDoublePoint)other).point);
   }

   public double[] getPoint() {
      return this.point;
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
