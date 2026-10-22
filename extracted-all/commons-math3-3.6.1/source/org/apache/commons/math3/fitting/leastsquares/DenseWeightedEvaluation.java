package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

class DenseWeightedEvaluation extends AbstractEvaluation {
   private final LeastSquaresProblem.Evaluation unweighted;
   private final RealMatrix weightSqrt;

   DenseWeightedEvaluation(LeastSquaresProblem.Evaluation unweighted, RealMatrix weightSqrt) {
      super(weightSqrt.getColumnDimension());
      this.unweighted = unweighted;
      this.weightSqrt = weightSqrt;
   }

   @Override
   public RealMatrix getJacobian() {
      return this.weightSqrt.multiply(this.unweighted.getJacobian());
   }

   @Override
   public RealVector getResiduals() {
      return this.weightSqrt.operate(this.unweighted.getResiduals());
   }

   @Override
   public RealVector getPoint() {
      return this.unweighted.getPoint();
   }
}
