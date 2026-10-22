package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;

public class JacobianFunction implements MultivariateMatrixFunction {
   private final MultivariateDifferentiableVectorFunction f;

   public JacobianFunction(MultivariateDifferentiableVectorFunction f) {
      this.f = f;
   }

   @Override
   public double[][] value(double[] point) {
      DerivativeStructure[] dsX = new DerivativeStructure[point.length];

      for (int i = 0; i < point.length; i++) {
         dsX[i] = new DerivativeStructure(point.length, 1, i, point[i]);
      }

      DerivativeStructure[] dsY = this.f.value(dsX);
      double[][] y = new double[dsY.length][point.length];
      int[] orders = new int[point.length];

      for (int i = 0; i < dsY.length; i++) {
         for (int j = 0; j < point.length; j++) {
            orders[j] = 1;
            y[i][j] = dsY[i].getPartialDerivative(orders);
            orders[j] = 0;
         }
      }

      return y;
   }
}
