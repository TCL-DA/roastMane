package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class BisectionSolver extends AbstractUnivariateSolver {
   private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;

   public BisectionSolver() {
      this(1.0E-6);
   }

   public BisectionSolver(double absoluteAccuracy) {
      super(absoluteAccuracy);
   }

   public BisectionSolver(double relativeAccuracy, double absoluteAccuracy) {
      super(relativeAccuracy, absoluteAccuracy);
   }

   @Override
   protected double doSolve() throws TooManyEvaluationsException {
      double min = this.getMin();
      double max = this.getMax();
      this.verifyInterval(min, max);
      double absoluteAccuracy = this.getAbsoluteAccuracy();

      do {
         double m = UnivariateSolverUtils.midpoint(min, max);
         double fmin = this.computeObjectiveValue(min);
         double fm = this.computeObjectiveValue(m);
         if (fm * fmin > 0.0) {
            min = m;
         } else {
            max = m;
         }
      } while (!(FastMath.abs(max - min) <= absoluteAccuracy));

      return UnivariateSolverUtils.midpoint(min, max);
   }
}
