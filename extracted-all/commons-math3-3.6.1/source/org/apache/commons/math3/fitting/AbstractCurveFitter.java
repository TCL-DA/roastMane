package org.apache.commons.math3.fitting;

import java.util.Collection;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

public abstract class AbstractCurveFitter {
   public double[] fit(Collection<WeightedObservedPoint> points) {
      return this.getOptimizer().optimize(this.getProblem(points)).getPoint().toArray();
   }

   protected LeastSquaresOptimizer getOptimizer() {
      return new LevenbergMarquardtOptimizer();
   }

   protected abstract LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> var1);

   protected static class TheoreticalValuesFunction {
      private final ParametricUnivariateFunction f;
      private final double[] points;

      public TheoreticalValuesFunction(ParametricUnivariateFunction f, Collection<WeightedObservedPoint> observations) {
         this.f = f;
         int len = observations.size();
         this.points = new double[len];
         int i = 0;

         for (WeightedObservedPoint obs : observations) {
            this.points[i++] = obs.getX();
         }
      }

      public MultivariateVectorFunction getModelFunction() {
         return new MultivariateVectorFunction() {
            @Override
            public double[] value(double[] p) {
               int len = TheoreticalValuesFunction.this.points.length;
               double[] values = new double[len];

               for (int i = 0; i < len; i++) {
                  values[i] = TheoreticalValuesFunction.this.f.value(TheoreticalValuesFunction.this.points[i], p);
               }

               return values;
            }
         };
      }

      public MultivariateMatrixFunction getModelFunctionJacobian() {
         return new MultivariateMatrixFunction() {
            @Override
            public double[][] value(double[] p) {
               int len = TheoreticalValuesFunction.this.points.length;
               double[][] jacobian = new double[len][];

               for (int i = 0; i < len; i++) {
                  jacobian[i] = TheoreticalValuesFunction.this.f.gradient(TheoreticalValuesFunction.this.points[i], p);
               }

               return jacobian;
            }
         };
      }
   }
}
