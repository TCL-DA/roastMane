package org.apache.commons.math3.ml.distance;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathArrays;

public class ManhattanDistance implements DistanceMeasure {
   private static final long serialVersionUID = -9108154600539125566L;

   @Override
   public double compute(double[] a, double[] b) throws DimensionMismatchException {
      return MathArrays.distance1(a, b);
   }
}
