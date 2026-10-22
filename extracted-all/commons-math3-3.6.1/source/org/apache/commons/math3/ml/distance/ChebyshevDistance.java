package org.apache.commons.math3.ml.distance;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathArrays;

public class ChebyshevDistance implements DistanceMeasure {
   private static final long serialVersionUID = -4694868171115238296L;

   @Override
   public double compute(double[] a, double[] b) throws DimensionMismatchException {
      return MathArrays.distanceInf(a, b);
   }
}
