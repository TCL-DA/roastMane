package org.apache.commons.math3.optim.nonlinear.vector;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.BaseMultivariateOptimizer;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointVectorValuePair;

@Deprecated
public abstract class MultivariateVectorOptimizer extends BaseMultivariateOptimizer<PointVectorValuePair> {
   private double[] target;
   private RealMatrix weightMatrix;
   private MultivariateVectorFunction model;

   protected MultivariateVectorOptimizer(ConvergenceChecker<PointVectorValuePair> checker) {
      super(checker);
   }

   protected double[] computeObjectiveValue(double[] params) {
      super.incrementEvaluationCount();
      return this.model.value(params);
   }

   public PointVectorValuePair optimize(OptimizationData... optData) throws TooManyEvaluationsException, DimensionMismatchException {
      return (PointVectorValuePair)super.optimize(optData);
   }

   public RealMatrix getWeight() {
      return this.weightMatrix.copy();
   }

   public double[] getTarget() {
      return (double[])this.target.clone();
   }

   public int getTargetSize() {
      return this.target.length;
   }

   @Override
   protected void parseOptimizationData(OptimizationData... optData) {
      super.parseOptimizationData(optData);

      for (OptimizationData data : optData) {
         if (data instanceof ModelFunction) {
            this.model = ((ModelFunction)data).getModelFunction();
         } else if (data instanceof Target) {
            this.target = ((Target)data).getTarget();
         } else if (data instanceof Weight) {
            this.weightMatrix = ((Weight)data).getWeight();
         }
      }

      this.checkParameters();
   }

   private void checkParameters() {
      if (this.target.length != this.weightMatrix.getColumnDimension()) {
         throw new DimensionMismatchException(this.target.length, this.weightMatrix.getColumnDimension());
      }
   }
}
