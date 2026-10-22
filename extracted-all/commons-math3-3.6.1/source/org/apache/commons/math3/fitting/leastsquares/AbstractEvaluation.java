package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.FastMath;

public abstract class AbstractEvaluation implements LeastSquaresProblem.Evaluation {
   private final int observationSize;

   AbstractEvaluation(int observationSize) {
      this.observationSize = observationSize;
   }

   @Override
   public RealMatrix getCovariances(double threshold) {
      RealMatrix j = this.getJacobian();
      RealMatrix jTj = j.transpose().multiply(j);
      DecompositionSolver solver = new QRDecomposition(jTj, threshold).getSolver();
      return solver.getInverse();
   }

   @Override
   public RealVector getSigma(double covarianceSingularityThreshold) {
      RealMatrix cov = this.getCovariances(covarianceSingularityThreshold);
      int nC = cov.getColumnDimension();
      RealVector sig = new ArrayRealVector(nC);

      for (int i = 0; i < nC; i++) {
         sig.setEntry(i, FastMath.sqrt(cov.getEntry(i, i)));
      }

      return sig;
   }

   @Override
   public double getRMS() {
      double cost = this.getCost();
      return FastMath.sqrt(cost * cost / (double)this.observationSize);
   }

   @Override
   public double getCost() {
      ArrayRealVector r = new ArrayRealVector(this.getResiduals());
      return FastMath.sqrt(r.dotProduct(r));
   }
}
