package org.apache.commons.math3.fitting.leastsquares;

public interface LeastSquaresOptimizer {
   LeastSquaresOptimizer.Optimum optimize(LeastSquaresProblem var1);

   public interface Optimum extends LeastSquaresProblem.Evaluation {
      int getEvaluations();

      int getIterations();
   }
}
