package org.apache.commons.math3.analysis.function;

import java.util.Arrays;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathArrays;

public class StepFunction implements UnivariateFunction {
   private final double[] abscissa;
   private final double[] ordinate;

   public StepFunction(double[] x, double[] y) throws NullArgumentException, NoDataException, DimensionMismatchException, NonMonotonicSequenceException {
      if (x == null || y == null) {
         throw new NullArgumentException();
      } else if (x.length != 0 && y.length != 0) {
         if (y.length != x.length) {
            throw new DimensionMismatchException(y.length, x.length);
         } else {
            MathArrays.checkOrder(x);
            this.abscissa = MathArrays.copyOf(x);
            this.ordinate = MathArrays.copyOf(y);
         }
      } else {
         throw new NoDataException();
      }
   }

   @Override
   public double value(double x) {
      int index = Arrays.binarySearch(this.abscissa, x);
      double fx = 0.0;
      if (index < -1) {
         fx = this.ordinate[-index - 2];
      } else if (index >= 0) {
         fx = this.ordinate[index];
      } else {
         fx = this.ordinate[0];
      }

      return fx;
   }
}
