package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public class NewtonSolver extends AbstractDifferentiableUnivariateSolver {
   private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;

   public NewtonSolver() {
      this(1.0E-6);
   }

   public NewtonSolver(double absoluteAccuracy) {
      super(absoluteAccuracy);
   }

   public double solve(int maxEval, DifferentiableUnivariateFunction f, double min, double max) throws TooManyEvaluationsException {
      return super.solve(maxEval, f, UnivariateSolverUtils.midpoint(min, max));
   }

   @Override
   protected double doSolve() throws TooManyEvaluationsException {
      double startValue = this.getStartValue();
      double absoluteAccuracy = this.getAbsoluteAccuracy();
      double x0 = startValue;

      while (true) {
         double x1 = x0 - this.computeObjectiveValue(x0) / this.computeDerivativeObjectiveValue(x0);
         if (FastMath.abs(x1 - x0) <= absoluteAccuracy) {
            return x1;
         }

         x0 = x1;
      }
   }
}
