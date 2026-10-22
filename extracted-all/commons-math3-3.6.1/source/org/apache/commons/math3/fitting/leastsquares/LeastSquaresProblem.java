package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.OptimizationProblem;

public interface LeastSquaresProblem extends OptimizationProblem<LeastSquaresProblem.Evaluation> {
   RealVector getStart();

   int getObservationSize();

   int getParameterSize();

   LeastSquaresProblem.Evaluation evaluate(RealVector var1);

   public interface Evaluation {
      RealMatrix getCovariances(double var1);

      RealVector getSigma(double var1);

      double getRMS();

      RealMatrix getJacobian();

      double getCost();

      RealVector getResiduals();

      RealVector getPoint();
   }
}
