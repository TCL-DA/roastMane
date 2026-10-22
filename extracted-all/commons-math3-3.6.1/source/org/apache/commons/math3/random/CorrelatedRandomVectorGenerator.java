package org.apache.commons.math3.random;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RectangularCholeskyDecomposition;

public class CorrelatedRandomVectorGenerator implements RandomVectorGenerator {
   private final double[] mean;
   private final NormalizedRandomGenerator generator;
   private final double[] normalized;
   private final RealMatrix root;

   public CorrelatedRandomVectorGenerator(double[] mean, RealMatrix covariance, double small, NormalizedRandomGenerator generator) {
      int order = covariance.getRowDimension();
      if (mean.length != order) {
         throw new DimensionMismatchException(mean.length, order);
      } else {
         this.mean = (double[])mean.clone();
         RectangularCholeskyDecomposition decomposition = new RectangularCholeskyDecomposition(covariance, small);
         this.root = decomposition.getRootMatrix();
         this.generator = generator;
         this.normalized = new double[decomposition.getRank()];
      }
   }

   public CorrelatedRandomVectorGenerator(RealMatrix covariance, double small, NormalizedRandomGenerator generator) {
      int order = covariance.getRowDimension();
      this.mean = new double[order];

      for (int i = 0; i < order; i++) {
         this.mean[i] = 0.0;
      }

      RectangularCholeskyDecomposition decomposition = new RectangularCholeskyDecomposition(covariance, small);
      this.root = decomposition.getRootMatrix();
      this.generator = generator;
      this.normalized = new double[decomposition.getRank()];
   }

   public NormalizedRandomGenerator getGenerator() {
      return this.generator;
   }

   public int getRank() {
      return this.normalized.length;
   }

   public RealMatrix getRootMatrix() {
      return this.root;
   }

   @Override
   public double[] nextVector() {
      for (int i = 0; i < this.normalized.length; i++) {
         this.normalized[i] = this.generator.nextNormalizedDouble();
      }

      double[] correlated = new double[this.mean.length];

      for (int i = 0; i < correlated.length; i++) {
         correlated[i] = this.mean[i];

         for (int j = 0; j < this.root.getColumnDimension(); j++) {
            correlated[i] += this.root.getEntry(i, j) * this.normalized[j];
         }
      }

      return correlated;
   }
}
