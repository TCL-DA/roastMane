package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class MullerSolver2 extends AbstractUnivariateSolver {
   private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;

   public MullerSolver2() {
      this(1.0E-6);
   }

   public MullerSolver2(double absoluteAccuracy) {
      super(absoluteAccuracy);
   }

   public MullerSolver2(double relativeAccuracy, double absoluteAccuracy) {
      super(relativeAccuracy, absoluteAccuracy);
   }

   @Override
   protected double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
      double min = this.getMin();
      double max = this.getMax();
      this.verifyInterval(min, max);
      double relativeAccuracy = this.getRelativeAccuracy();
      double absoluteAccuracy = this.getAbsoluteAccuracy();
      double functionValueAccuracy = this.getFunctionValueAccuracy();
      double x0 = min;
      double y0 = this.computeObjectiveValue(min);
      if (FastMath.abs(y0) < functionValueAccuracy) {
         return min;
      } else {
         double x1 = max;
         double y1 = this.computeObjectiveValue(max);
         if (FastMath.abs(y1) < functionValueAccuracy) {
            return max;
         } else if (y0 * y1 > 0.0) {
            throw new NoBracketingException(min, max, y0, y1);
         } else {
            double x2 = 0.5 * (min + max);
            double y2 = this.computeObjectiveValue(x2);
            double oldx = Double.POSITIVE_INFINITY;

            while (true) {
               double q = (x2 - x1) / (x1 - x0);
               double a = q * (y2 - (1.0 + q) * y1 + q * y0);
               double b = (2.0 * q + 1.0) * y2 - (1.0 + q) * (1.0 + q) * y1 + q * q * y0;
               double c = (1.0 + q) * y2;
               double delta = b * b - 4.0 * a * c;
               double denominator;
               if (delta >= 0.0) {
                  double dplus = b + FastMath.sqrt(delta);
                  double dminus = b - FastMath.sqrt(delta);
                  denominator = FastMath.abs(dplus) > FastMath.abs(dminus) ? dplus : dminus;
               } else {
                  denominator = FastMath.sqrt(b * b - delta);
               }

               double x;
               if (denominator == 0.0) {
                  x = min + FastMath.random() * (max - min);
                  oldx = Double.POSITIVE_INFINITY;
               } else {
                  x = x2 - 2.0 * c * (x2 - x1) / denominator;

                  while (x == x1 || x == x2) {
                     x += absoluteAccuracy;
                  }
               }

               double y = this.computeObjectiveValue(x);
               double tolerance = FastMath.max(relativeAccuracy * FastMath.abs(x), absoluteAccuracy);
               if (FastMath.abs(x - oldx) <= tolerance || FastMath.abs(y) <= functionValueAccuracy) {
                  return x;
               }

               x0 = x1;
               y0 = y1;
               x1 = x2;
               y1 = y2;
               x2 = x;
               y2 = y;
               oldx = x;
            }
         }
      }
   }
}
