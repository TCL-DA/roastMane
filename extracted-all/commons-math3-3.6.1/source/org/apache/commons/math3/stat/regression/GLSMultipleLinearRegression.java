package org.apache.commons.math3.stat.regression;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class GLSMultipleLinearRegression extends AbstractMultipleLinearRegression {
   private RealMatrix Omega;
   private RealMatrix OmegaInverse;

   public void newSampleData(double[] y, double[][] x, double[][] covariance) {
      this.validateSampleData(x, y);
      this.newYSampleData(y);
      this.newXSampleData(x);
      this.validateCovarianceData(x, covariance);
      this.newCovarianceData(covariance);
   }

   protected void newCovarianceData(double[][] omega) {
      this.Omega = new Array2DRowRealMatrix(omega);
      this.OmegaInverse = null;
   }

   protected RealMatrix getOmegaInverse() {
      if (this.OmegaInverse == null) {
         this.OmegaInverse = new LUDecomposition(this.Omega).getSolver().getInverse();
      }

      return this.OmegaInverse;
   }

   @Override
   protected RealVector calculateBeta() {
      RealMatrix OI = this.getOmegaInverse();
      RealMatrix XT = this.getX().transpose();
      RealMatrix XTOIX = XT.multiply(OI).multiply(this.getX());
      RealMatrix inverse = new LUDecomposition(XTOIX).getSolver().getInverse();
      return inverse.multiply(XT).multiply(OI).operate(this.getY());
   }

   @Override
   protected RealMatrix calculateBetaVariance() {
      RealMatrix OI = this.getOmegaInverse();
      RealMatrix XTOIX = this.getX().transpose().multiply(OI).multiply(this.getX());
      return new LUDecomposition(XTOIX).getSolver().getInverse();
   }

   @Override
   protected double calculateErrorVariance() {
      RealVector residuals = this.calculateResiduals();
      double t = residuals.dotProduct(this.getOmegaInverse().operate(residuals));
      return t / (double)(this.getX().getRowDimension() - this.getX().getColumnDimension());
   }
}
