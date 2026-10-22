package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

public class CholeskyDecomposition {
   public static final double DEFAULT_RELATIVE_SYMMETRY_THRESHOLD = 1.0E-15;
   public static final double DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD = 1.0E-10;
   private double[][] lTData;
   private RealMatrix cachedL;
   private RealMatrix cachedLT;

   public CholeskyDecomposition(RealMatrix matrix) {
      this(matrix, 1.0E-15, 1.0E-10);
   }

   public CholeskyDecomposition(RealMatrix matrix, double relativeSymmetryThreshold, double absolutePositivityThreshold) {
      if (!matrix.isSquare()) {
         throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
      } else {
         int order = matrix.getRowDimension();
         this.lTData = matrix.getData();
         this.cachedL = null;
         this.cachedLT = null;

         for (int i = 0; i < order; i++) {
            double[] lI = this.lTData[i];

            for (int j = i + 1; j < order; j++) {
               double[] lJ = this.lTData[j];
               double lIJ = lI[j];
               double lJI = lJ[i];
               double maxDelta = relativeSymmetryThreshold * FastMath.max(FastMath.abs(lIJ), FastMath.abs(lJI));
               if (FastMath.abs(lIJ - lJI) > maxDelta) {
                  throw new NonSymmetricMatrixException(i, j, relativeSymmetryThreshold);
               }

               lJ[i] = 0.0;
            }
         }

         for (int i = 0; i < order; i++) {
            double[] ltI = this.lTData[i];
            if (ltI[i] <= absolutePositivityThreshold) {
               throw new NonPositiveDefiniteMatrixException(ltI[i], i, absolutePositivityThreshold);
            }

            ltI[i] = FastMath.sqrt(ltI[i]);
            double inverse = 1.0 / ltI[i];

            for (int q = order - 1; q > i; q--) {
               ltI[q] *= inverse;
               double[] ltQ = this.lTData[q];

               for (int p = q; p < order; p++) {
                  ltQ[p] -= ltI[q] * ltI[p];
               }
            }
         }
      }
   }

   public RealMatrix getL() {
      if (this.cachedL == null) {
         this.cachedL = this.getLT().transpose();
      }

      return this.cachedL;
   }

   public RealMatrix getLT() {
      if (this.cachedLT == null) {
         this.cachedLT = MatrixUtils.createRealMatrix(this.lTData);
      }

      return this.cachedLT;
   }

   public double getDeterminant() {
      double determinant = 1.0;

      for (int i = 0; i < this.lTData.length; i++) {
         double lTii = this.lTData[i][i];
         determinant *= lTii * lTii;
      }

      return determinant;
   }

   public DecompositionSolver getSolver() {
      return new CholeskyDecomposition.Solver(this.lTData);
   }

   private static class Solver implements DecompositionSolver {
      private final double[][] lTData;

      private Solver(double[][] lTData) {
         this.lTData = lTData;
      }

      @Override
      public boolean isNonSingular() {
         return true;
      }

      @Override
      public RealVector solve(RealVector b) {
         int m = this.lTData.length;
         if (b.getDimension() != m) {
            throw new DimensionMismatchException(b.getDimension(), m);
         } else {
            double[] x = b.toArray();

            for (int j = 0; j < m; j++) {
               double[] lJ = this.lTData[j];
               x[j] /= lJ[j];
               double xJ = x[j];

               for (int i = j + 1; i < m; i++) {
                  x[i] -= xJ * lJ[i];
               }
            }

            for (int j = m - 1; j >= 0; j--) {
               x[j] /= this.lTData[j][j];
               double xJ = x[j];

               for (int i = 0; i < j; i++) {
                  x[i] -= xJ * this.lTData[i][j];
               }
            }

            return new ArrayRealVector(x, false);
         }
      }

      @Override
      public RealMatrix solve(RealMatrix b) {
         int m = this.lTData.length;
         if (b.getRowDimension() != m) {
            throw new DimensionMismatchException(b.getRowDimension(), m);
         } else {
            int nColB = b.getColumnDimension();
            double[][] x = b.getData();

            for (int j = 0; j < m; j++) {
               double[] lJ = this.lTData[j];
               double lJJ = lJ[j];
               double[] xJ = x[j];

               for (int k = 0; k < nColB; k++) {
                  xJ[k] /= lJJ;
               }

               for (int i = j + 1; i < m; i++) {
                  double[] xI = x[i];
                  double lJI = lJ[i];

                  for (int k = 0; k < nColB; k++) {
                     xI[k] -= xJ[k] * lJI;
                  }
               }
            }

            for (int j = m - 1; j >= 0; j--) {
               double lJJ = this.lTData[j][j];
               double[] xJ = x[j];

               for (int k = 0; k < nColB; k++) {
                  xJ[k] /= lJJ;
               }

               for (int i = 0; i < j; i++) {
                  double[] xI = x[i];
                  double lIJ = this.lTData[i][j];

                  for (int k = 0; k < nColB; k++) {
                     xI[k] -= xJ[k] * lIJ;
                  }
               }
            }

            return new Array2DRowRealMatrix(x);
         }
      }

      @Override
      public RealMatrix getInverse() {
         return this.solve(MatrixUtils.createRealIdentityMatrix(this.lTData.length));
      }
   }
}
