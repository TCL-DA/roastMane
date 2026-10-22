package org.apache.commons.math3.linear;

import org.apache.commons.math3.util.FastMath;

public class RectangularCholeskyDecomposition {
   private final RealMatrix root;
   private int rank;

   public RectangularCholeskyDecomposition(RealMatrix matrix) throws NonPositiveDefiniteMatrixException {
      this(matrix, 0.0);
   }

   public RectangularCholeskyDecomposition(RealMatrix matrix, double small) throws NonPositiveDefiniteMatrixException {
      int order = matrix.getRowDimension();
      double[][] c = matrix.getData();
      double[][] b = new double[order][order];
      int[] index = new int[order];
      int i = 0;

      while (i < order) {
         index[i] = i++;
      }

      i = 0;
      boolean loop = true;

      while (loop) {
         int swapR = i;

         for (int ix = i + 1; ix < order; ix++) {
            int ii = index[ix];
            int isr = index[swapR];
            if (c[ii][ii] > c[isr][isr]) {
               swapR = ix;
            }
         }

         if (swapR != i) {
            int tmpIndex = index[i];
            index[i] = index[swapR];
            index[swapR] = tmpIndex;
            double[] tmpRow = b[i];
            b[i] = b[swapR];
            b[swapR] = tmpRow;
         }

         int ir = index[i];
         if (c[ir][ir] <= small) {
            if (i == 0) {
               throw new NonPositiveDefiniteMatrixException(c[ir][ir], ir, small);
            }

            for (int ixx = i; ixx < order; ixx++) {
               if (c[index[ixx]][index[ixx]] < -small) {
                  throw new NonPositiveDefiniteMatrixException(c[index[ixx]][index[ixx]], ixx, small);
               }
            }

            loop = false;
         } else {
            double sqrt = FastMath.sqrt(c[ir][ir]);
            b[i][i] = sqrt;
            double inverse = 1.0 / sqrt;
            double inverse2 = 1.0 / c[ir][ir];

            for (int ixxx = i + 1; ixxx < order; ixxx++) {
               int ii = index[ixxx];
               double e = inverse * c[ii][ir];
               b[ixxx][i] = e;
               c[ii][ii] = c[ii][ii] - c[ii][ir] * c[ii][ir] * inverse2;

               for (int j = i + 1; j < ixxx; j++) {
                  int ij = index[j];
                  double f = c[ii][ij] - e * b[j][i];
                  c[ii][ij] = f;
                  c[ij][ii] = f;
               }
            }

            i++;
            loop = i < order;
         }
      }

      this.rank = i;
      this.root = MatrixUtils.createRealMatrix(order, i);

      for (int ixxx = 0; ixxx < order; ixxx++) {
         for (int j = 0; j < i; j++) {
            this.root.setEntry(index[ixxx], j, b[ixxx][j]);
         }
      }
   }

   public RealMatrix getRootMatrix() {
      return this.root;
   }

   public int getRank() {
      return this.rank;
   }
}
