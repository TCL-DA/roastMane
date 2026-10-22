package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class SecantSolver extends AbstractUnivariateSolver {
   protected static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;

   public SecantSolver() {
      super(1.0E-6);
   }

   public SecantSolver(double absoluteAccuracy) {
      super(absoluteAccuracy);
   }

   public SecantSolver(double relativeAccuracy, double absoluteAccuracy) {
      super(relativeAccuracy, absoluteAccuracy);
   }

   @Override
   protected final double doSolve() throws TooManyEvaluationsException, NoBracketingException {
      double x0 = this.getMin();
      double x1 = this.getMax();
      double f0 = this.computeObjectiveValue(x0);
      double f1 = this.computeObjectiveValue(x1);
      if (f0 == 0.0) {
         return x0;
      } else if (f1 == 0.0) {
         return x1;
      } else {
         this.verifyBracketing(x0, x1);
         double ftol = this.getFunctionValueAccuracy();
         double atol = this.getAbsoluteAccuracy();
         double rtol = this.getRelativeAccuracy();

         double x;
         do {
            x = x1 - f1 * (x1 - x0) / (f1 - f0);
            double fx = this.computeObjectiveValue(x);
            if (fx == 0.0) {
               return x;
            }

            x0 = x1;
            f0 = f1;
            x1 = x;
            f1 = fx;
            if (FastMath.abs(fx) <= ftol) {
               return x;
            }
         } while (!(FastMath.abs(x - x0) < FastMath.max(rtol * FastMath.abs(x), atol)));

         return x;
      }
   }
}
