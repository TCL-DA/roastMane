package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Precision;

public class EvaluationRmsChecker implements ConvergenceChecker<LeastSquaresProblem.Evaluation> {
   private final double relTol;
   private final double absTol;

   public EvaluationRmsChecker(double tol) {
      this(tol, tol);
   }

   public EvaluationRmsChecker(double relTol, double absTol) {
      this.relTol = relTol;
      this.absTol = absTol;
   }

   public boolean converged(int iteration, LeastSquaresProblem.Evaluation previous, LeastSquaresProblem.Evaluation current) {
      double prevRms = previous.getRMS();
      double currRms = current.getRMS();
      return Precision.equals(prevRms, currRms, this.absTol) || Precision.equalsWithRelativeTolerance(prevRms, currRms, this.relTol);
   }
}
