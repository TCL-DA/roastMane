package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

class OptimumImpl implements LeastSquaresOptimizer.Optimum {
   private final LeastSquaresProblem.Evaluation value;
   private final int evaluations;
   private final int iterations;

   OptimumImpl(LeastSquaresProblem.Evaluation value, int evaluations, int iterations) {
      this.value = value;
      this.evaluations = evaluations;
      this.iterations = iterations;
   }

   @Override
   public int getEvaluations() {
      return this.evaluations;
   }

   @Override
   public int getIterations() {
      return this.iterations;
   }

   @Override
   public RealMatrix getCovariances(double threshold) {
      return this.value.getCovariances(threshold);
   }

   @Override
   public RealVector getSigma(double covarianceSingularityThreshold) {
      return this.value.getSigma(covarianceSingularityThreshold);
   }

   @Override
   public double getRMS() {
      return this.value.getRMS();
   }

   @Override
   public RealMatrix getJacobian() {
      return this.value.getJacobian();
   }

   @Override
   public double getCost() {
      return this.value.getCost();
   }

   @Override
   public RealVector getResiduals() {
      return this.value.getResiduals();
   }

   @Override
   public RealVector getPoint() {
      return this.value.getPoint();
   }
}
