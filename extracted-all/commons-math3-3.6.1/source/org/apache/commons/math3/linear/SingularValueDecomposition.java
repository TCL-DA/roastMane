package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class SingularValueDecomposition {
   private static final double EPS = 2.220446E-16F;
   private static final double TINY = 1.6033346880071782E-291;
   private final double[] singularValues;
   private final int m;
   private final int n;
   private final boolean transposed;
   private final RealMatrix cachedU;
   private RealMatrix cachedUt;
   private RealMatrix cachedS;
   private final RealMatrix cachedV;
   private RealMatrix cachedVt;
   private final double tol;

   public SingularValueDecomposition(RealMatrix matrix) {
      double[][] A;
      if (matrix.getRowDimension() < matrix.getColumnDimension()) {
         this.transposed = true;
         A = matrix.transpose().getData();
         this.m = matrix.getColumnDimension();
         this.n = matrix.getRowDimension();
      } else {
         this.transposed = false;
         A = matrix.getData();
         this.m = matrix.getRowDimension();
         this.n = matrix.getColumnDimension();
      }

      this.singularValues = new double[this.n];
      double[][] U = new double[this.m][this.n];
      double[][] V = new double[this.n][this.n];
      double[] e = new double[this.n];
      double[] work = new double[this.m];
      int nct = FastMath.min(this.m - 1, this.n);
      int nrt = FastMath.max(0, this.n - 2);

      for (int k = 0; k < FastMath.max(nct, nrt); k++) {
         if (k < nct) {
            this.singularValues[k] = 0.0;

            for (int i = k; i < this.m; i++) {
               this.singularValues[k] = FastMath.hypot(this.singularValues[k], A[i][k]);
            }

            if (this.singularValues[k] != 0.0) {
               if (A[k][k] < 0.0) {
                  this.singularValues[k] = -this.singularValues[k];
               }

               for (int i = k; i < this.m; i++) {
                  A[i][k] = A[i][k] / this.singularValues[k];
               }

               A[k][k]++;
            }

            this.singularValues[k] = -this.singularValues[k];
         }

         for (int j = k + 1; j < this.n; j++) {
            if (k < nct && this.singularValues[k] != 0.0) {
               double t = 0.0;

               for (int i = k; i < this.m; i++) {
                  t += A[i][k] * A[i][j];
               }

               t = -t / A[k][k];

               for (int i = k; i < this.m; i++) {
                  A[i][j] = A[i][j] + t * A[i][k];
               }
            }

            e[j] = A[k][j];
         }

         if (k < nct) {
            for (int i = k; i < this.m; i++) {
               U[i][k] = A[i][k];
            }
         }

         if (k < nrt) {
            e[k] = 0.0;

            for (int i = k + 1; i < this.n; i++) {
               e[k] = FastMath.hypot(e[k], e[i]);
            }

            if (e[k] != 0.0) {
               if (e[k + 1] < 0.0) {
                  e[k] = -e[k];
               }

               for (int i = k + 1; i < this.n; i++) {
                  e[i] /= e[k];
               }

               e[k + 1]++;
            }

            e[k] = -e[k];
            if (k + 1 < this.m && e[k] != 0.0) {
               for (int i = k + 1; i < this.m; i++) {
                  work[i] = 0.0;
               }

               for (int j = k + 1; j < this.n; j++) {
                  for (int i = k + 1; i < this.m; i++) {
                     work[i] += e[j] * A[i][j];
                  }
               }

               for (int j = k + 1; j < this.n; j++) {
                  double t = -e[j] / e[k + 1];

                  for (int i = k + 1; i < this.m; i++) {
                     A[i][j] = A[i][j] + t * work[i];
                  }
               }
            }

            for (int i = k + 1; i < this.n; i++) {
               V[i][k] = e[i];
            }
         }
      }

      int p = this.n;
      if (nct < this.n) {
         this.singularValues[nct] = A[nct][nct];
      }

      if (this.m < p) {
         this.singularValues[p - 1] = 0.0;
      }

      if (nrt + 1 < p) {
         e[nrt] = A[nrt][p - 1];
      }

      e[p - 1] = 0.0;

      for (int j = nct; j < this.n; j++) {
         for (int i = 0; i < this.m; i++) {
            U[i][j] = 0.0;
         }

         U[j][j] = 1.0;
      }

      for (int k = nct - 1; k >= 0; k--) {
         if (this.singularValues[k] != 0.0) {
            for (int j = k + 1; j < this.n; j++) {
               double t = 0.0;

               for (int i = k; i < this.m; i++) {
                  t += U[i][k] * U[i][j];
               }

               t = -t / U[k][k];

               for (int i = k; i < this.m; i++) {
                  U[i][j] = U[i][j] + t * U[i][k];
               }
            }

            for (int i = k; i < this.m; i++) {
               U[i][k] = -U[i][k];
            }

            U[k][k]++;

            for (int i = 0; i < k - 1; i++) {
               U[i][k] = 0.0;
            }
         } else {
            for (int i = 0; i < this.m; i++) {
               U[i][k] = 0.0;
            }

            U[k][k] = 1.0;
         }
      }

      for (int kx = this.n - 1; kx >= 0; kx--) {
         if (kx < nrt && e[kx] != 0.0) {
            for (int j = kx + 1; j < this.n; j++) {
               double t = 0.0;

               for (int i = kx + 1; i < this.n; i++) {
                  t += V[i][kx] * V[i][j];
               }

               t = -t / V[kx + 1][kx];

               for (int i = kx + 1; i < this.n; i++) {
                  V[i][j] = V[i][j] + t * V[i][kx];
               }
            }
         }

         for (int i = 0; i < this.n; i++) {
            V[i][kx] = 0.0;
         }

         V[kx][kx] = 1.0;
      }

      int pp = p - 1;

      label338:
      while (p > 0) {
         int kx;
         for (kx = p - 2; kx >= 0; kx--) {
            double threshold = 1.6033346880071782E-291 + 2.220446E-16F * (FastMath.abs(this.singularValues[kx]) + FastMath.abs(this.singularValues[kx + 1]));
            if (!(FastMath.abs(e[kx]) > threshold)) {
               e[kx] = 0.0;
               break;
            }
         }

         int kase;
         if (kx == p - 2) {
            kase = 4;
         } else {
            int ks;
            for (ks = p - 1; ks >= kx && ks != kx; ks--) {
               double t = (ks != p ? FastMath.abs(e[ks]) : 0.0) + (ks != kx + 1 ? FastMath.abs(e[ks - 1]) : 0.0);
               if (FastMath.abs(this.singularValues[ks]) <= 1.6033346880071782E-291 + 2.220446E-16F * t) {
                  this.singularValues[ks] = 0.0;
                  break;
               }
            }

            if (ks == kx) {
               kase = 3;
            } else if (ks == p - 1) {
               kase = 1;
            } else {
               kase = 2;
               kx = ks;
            }
         }

         kx++;
         switch (kase) {
            case 1:
               double f = e[p - 2];
               e[p - 2] = 0.0;
               int j = p - 2;

               while (true) {
                  if (j < kx) {
                     continue label338;
                  }

                  double t = FastMath.hypot(this.singularValues[j], f);
                  double cs = this.singularValues[j] / t;
                  double sn = f / t;
                  this.singularValues[j] = t;
                  if (j != kx) {
                     f = -sn * e[j - 1];
                     e[j - 1] = cs * e[j - 1];
                  }

                  for (int i = 0; i < this.n; i++) {
                     t = cs * V[i][j] + sn * V[i][p - 1];
                     V[i][p - 1] = -sn * V[i][j] + cs * V[i][p - 1];
                     V[i][j] = t;
                  }

                  j--;
               }
            case 2:
               double f = e[kx - 1];
               e[kx - 1] = 0.0;
               int j = kx;

               while (true) {
                  if (j >= p) {
                     continue label338;
                  }

                  double t = FastMath.hypot(this.singularValues[j], f);
                  double cs = this.singularValues[j] / t;
                  double sn = f / t;
                  this.singularValues[j] = t;
                  f = -sn * e[j];
                  e[j] = cs * e[j];

                  for (int i = 0; i < this.m; i++) {
                     t = cs * U[i][j] + sn * U[i][kx - 1];
                     U[i][kx - 1] = -sn * U[i][j] + cs * U[i][kx - 1];
                     U[i][j] = t;
                  }

                  j++;
               }
            case 3:
               double maxPm1Pm2 = FastMath.max(FastMath.abs(this.singularValues[p - 1]), FastMath.abs(this.singularValues[p - 2]));
               double scale = FastMath.max(
                  FastMath.max(FastMath.max(maxPm1Pm2, FastMath.abs(e[p - 2])), FastMath.abs(this.singularValues[kx])), FastMath.abs(e[kx])
               );
               double sp = this.singularValues[p - 1] / scale;
               double spm1 = this.singularValues[p - 2] / scale;
               double epm1 = e[p - 2] / scale;
               double sk = this.singularValues[kx] / scale;
               double ek = e[kx] / scale;
               double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
               double c = sp * epm1 * sp * epm1;
               double shift = 0.0;
               if (b != 0.0 || c != 0.0) {
                  shift = FastMath.sqrt(b * b + c);
                  if (b < 0.0) {
                     shift = -shift;
                  }

                  shift = c / (b + shift);
               }

               double f = (sk + sp) * (sk - sp) + shift;
               double g = sk * ek;

               for (int j = kx; j < p - 1; j++) {
                  double t = FastMath.hypot(f, g);
                  double cs = f / t;
                  double sn = g / t;
                  if (j != kx) {
                     e[j - 1] = t;
                  }

                  f = cs * this.singularValues[j] + sn * e[j];
                  e[j] = cs * e[j] - sn * this.singularValues[j];
                  g = sn * this.singularValues[j + 1];
                  this.singularValues[j + 1] = cs * this.singularValues[j + 1];

                  for (int i = 0; i < this.n; i++) {
                     t = cs * V[i][j] + sn * V[i][j + 1];
                     V[i][j + 1] = -sn * V[i][j] + cs * V[i][j + 1];
                     V[i][j] = t;
                  }

                  t = FastMath.hypot(f, g);
                  cs = f / t;
                  sn = g / t;
                  this.singularValues[j] = t;
                  f = cs * e[j] + sn * this.singularValues[j + 1];
                  this.singularValues[j + 1] = -sn * e[j] + cs * this.singularValues[j + 1];
                  g = sn * e[j + 1];
                  e[j + 1] = cs * e[j + 1];
                  if (j < this.m - 1) {
                     for (int i = 0; i < this.m; i++) {
                        t = cs * U[i][j] + sn * U[i][j + 1];
                        U[i][j + 1] = -sn * U[i][j] + cs * U[i][j + 1];
                        U[i][j] = t;
                     }
                  }
               }

               e[p - 2] = f;
               continue;
         }

         if (this.singularValues[kx] <= 0.0) {
            this.singularValues[kx] = this.singularValues[kx] < 0.0 ? -this.singularValues[kx] : 0.0;

            for (int i = 0; i <= pp; i++) {
               V[i][kx] = -V[i][kx];
            }
         }

         for (; kx < pp && !(this.singularValues[kx] >= this.singularValues[kx + 1]); kx++) {
            double tx = this.singularValues[kx];
            this.singularValues[kx] = this.singularValues[kx + 1];
            this.singularValues[kx + 1] = tx;
            if (kx < this.n - 1) {
               for (int i = 0; i < this.n; i++) {
                  tx = V[i][kx + 1];
                  V[i][kx + 1] = V[i][kx];
                  V[i][kx] = tx;
               }
            }

            if (kx < this.m - 1) {
               for (int i = 0; i < this.m; i++) {
                  tx = U[i][kx + 1];
                  U[i][kx + 1] = U[i][kx];
                  U[i][kx] = tx;
               }
            }
         }

         p--;
      }

      this.tol = FastMath.max((double)this.m * this.singularValues[0] * 2.220446E-16F, FastMath.sqrt(Precision.SAFE_MIN));
      if (!this.transposed) {
         this.cachedU = MatrixUtils.createRealMatrix(U);
         this.cachedV = MatrixUtils.createRealMatrix(V);
      } else {
         this.cachedU = MatrixUtils.createRealMatrix(V);
         this.cachedV = MatrixUtils.createRealMatrix(U);
      }
   }

   public RealMatrix getU() {
      return this.cachedU;
   }

   public RealMatrix getUT() {
      if (this.cachedUt == null) {
         this.cachedUt = this.getU().transpose();
      }

      return this.cachedUt;
   }

   public RealMatrix getS() {
      if (this.cachedS == null) {
         this.cachedS = MatrixUtils.createRealDiagonalMatrix(this.singularValues);
      }

      return this.cachedS;
   }

   public double[] getSingularValues() {
      return (double[])this.singularValues.clone();
   }

   public RealMatrix getV() {
      return this.cachedV;
   }

   public RealMatrix getVT() {
      if (this.cachedVt == null) {
         this.cachedVt = this.getV().transpose();
      }

      return this.cachedVt;
   }

   public RealMatrix getCovariance(double minSingularValue) {
      int p = this.singularValues.length;
      int dimension = 0;

      while (dimension < p && this.singularValues[dimension] >= minSingularValue) {
         dimension++;
      }

      if (dimension == 0) {
         throw new NumberIsTooLargeException(LocalizedFormats.TOO_LARGE_CUTOFF_SINGULAR_VALUE, minSingularValue, this.singularValues[0], true);
      } else {
         final double[][] data = new double[dimension][p];
         this.getVT().walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            @Override
            public void visit(int row, int column, double value) {
               data[row][column] = value / SingularValueDecomposition.this.singularValues[row];
            }
         }, 0, dimension - 1, 0, p - 1);
         RealMatrix jv = new Array2DRowRealMatrix(data, false);
         return jv.transpose().multiply(jv);
      }
   }

   public double getNorm() {
      return this.singularValues[0];
   }

   public double getConditionNumber() {
      return this.singularValues[0] / this.singularValues[this.n - 1];
   }

   public double getInverseConditionNumber() {
      return this.singularValues[this.n - 1] / this.singularValues[0];
   }

   public int getRank() {
      int r = 0;

      for (int i = 0; i < this.singularValues.length; i++) {
         if (this.singularValues[i] > this.tol) {
            r++;
         }
      }

      return r;
   }

   public DecompositionSolver getSolver() {
      return new SingularValueDecomposition.Solver(this.singularValues, this.getUT(), this.getV(), this.getRank() == this.m, this.tol);
   }

   private static class Solver implements DecompositionSolver {
      private final RealMatrix pseudoInverse;
      private boolean nonSingular;

      private Solver(double[] singularValues, RealMatrix uT, RealMatrix v, boolean nonSingular, double tol) {
         double[][] suT = uT.getData();

         for (int i = 0; i < singularValues.length; i++) {
            double a;
            if (singularValues[i] > tol) {
               a = 1.0 / singularValues[i];
            } else {
               a = 0.0;
            }

            double[] suTi = suT[i];

            for (int j = 0; j < suTi.length; j++) {
               suTi[j] *= a;
            }
         }

         this.pseudoInverse = v.multiply(new Array2DRowRealMatrix(suT, false));
         this.nonSingular = nonSingular;
      }

      @Override
      public RealVector solve(RealVector b) {
         return this.pseudoInverse.operate(b);
      }

      @Override
      public RealMatrix solve(RealMatrix b) {
         return this.pseudoInverse.multiply(b);
      }

      @Override
      public boolean isNonSingular() {
         return this.nonSingular;
      }

      @Override
      public RealMatrix getInverse() {
         return this.pseudoInverse;
      }
   }
}
