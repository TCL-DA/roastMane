package org.apache.commons.math3.optim.nonlinear.vector.jacobian;

import java.util.Arrays;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

@Deprecated
public class LevenbergMarquardtOptimizer extends AbstractLeastSquaresOptimizer {
   private static final double TWO_EPS = 2.0 * Precision.EPSILON;
   private int solvedCols;
   private double[] diagR;
   private double[] jacNorm;
   private double[] beta;
   private int[] permutation;
   private int rank;
   private double lmPar;
   private double[] lmDir;
   private final double initialStepBoundFactor;
   private final double costRelativeTolerance;
   private final double parRelativeTolerance;
   private final double orthoTolerance;
   private final double qrRankingThreshold;
   private double[] weightedResidual;
   private double[][] weightedJacobian;

   public LevenbergMarquardtOptimizer() {
      this(100.0, 1.0E-10, 1.0E-10, 1.0E-10, Precision.SAFE_MIN);
   }

   public LevenbergMarquardtOptimizer(ConvergenceChecker<PointVectorValuePair> checker) {
      this(100.0, checker, 1.0E-10, 1.0E-10, 1.0E-10, Precision.SAFE_MIN);
   }

   public LevenbergMarquardtOptimizer(
      double initialStepBoundFactor,
      ConvergenceChecker<PointVectorValuePair> checker,
      double costRelativeTolerance,
      double parRelativeTolerance,
      double orthoTolerance,
      double threshold
   ) {
      super(checker);
      this.initialStepBoundFactor = initialStepBoundFactor;
      this.costRelativeTolerance = costRelativeTolerance;
      this.parRelativeTolerance = parRelativeTolerance;
      this.orthoTolerance = orthoTolerance;
      this.qrRankingThreshold = threshold;
   }

   public LevenbergMarquardtOptimizer(double costRelativeTolerance, double parRelativeTolerance, double orthoTolerance) {
      this(100.0, costRelativeTolerance, parRelativeTolerance, orthoTolerance, Precision.SAFE_MIN);
   }

   public LevenbergMarquardtOptimizer(
      double initialStepBoundFactor, double costRelativeTolerance, double parRelativeTolerance, double orthoTolerance, double threshold
   ) {
      super(null);
      this.initialStepBoundFactor = initialStepBoundFactor;
      this.costRelativeTolerance = costRelativeTolerance;
      this.parRelativeTolerance = parRelativeTolerance;
      this.orthoTolerance = orthoTolerance;
      this.qrRankingThreshold = threshold;
   }

   protected PointVectorValuePair doOptimize() {
      this.checkParameters();
      int nR = this.getTarget().length;
      double[] currentPoint = this.getStartPoint();
      int nC = currentPoint.length;
      this.solvedCols = FastMath.min(nR, nC);
      this.diagR = new double[nC];
      this.jacNorm = new double[nC];
      this.beta = new double[nC];
      this.permutation = new int[nC];
      this.lmDir = new double[nC];
      double delta = 0.0;
      double xNorm = 0.0;
      double[] diag = new double[nC];
      double[] oldX = new double[nC];
      double[] oldRes = new double[nR];
      double[] oldObj = new double[nR];
      double[] qtf = new double[nR];
      double[] work1 = new double[nC];
      double[] work2 = new double[nC];
      double[] work3 = new double[nC];
      RealMatrix weightMatrixSqrt = this.getWeightSquareRoot();
      double[] currentObjective = this.computeObjectiveValue(currentPoint);
      double[] currentResiduals = this.computeResiduals(currentObjective);
      PointVectorValuePair current = new PointVectorValuePair(currentPoint, currentObjective);
      double currentCost = this.computeCost(currentResiduals);
      this.lmPar = 0.0;
      boolean firstIteration = true;
      ConvergenceChecker<PointVectorValuePair> checker = this.getConvergenceChecker();

      while (true) {
         this.incrementIterationCount();
         PointVectorValuePair previous = current;
         this.qrDecomposition(this.computeWeightedJacobian(currentPoint));
         this.weightedResidual = weightMatrixSqrt.operate(currentResiduals);

         for (int i = 0; i < nR; i++) {
            qtf[i] = this.weightedResidual[i];
         }

         this.qTy(qtf);

         for (int k = 0; k < this.solvedCols; k++) {
            int pk = this.permutation[k];
            this.weightedJacobian[k][pk] = this.diagR[pk];
         }

         if (firstIteration) {
            xNorm = 0.0;

            for (int k = 0; k < nC; k++) {
               double dk = this.jacNorm[k];
               if (dk == 0.0) {
                  dk = 1.0;
               }

               double xk = dk * currentPoint[k];
               xNorm += xk * xk;
               diag[k] = dk;
            }

            xNorm = FastMath.sqrt(xNorm);
            delta = xNorm == 0.0 ? this.initialStepBoundFactor : this.initialStepBoundFactor * xNorm;
         }

         double maxCosine = 0.0;
         if (currentCost != 0.0) {
            for (int j = 0; j < this.solvedCols; j++) {
               int pj = this.permutation[j];
               double s = this.jacNorm[pj];
               if (s != 0.0) {
                  double sum = 0.0;

                  for (int i = 0; i <= j; i++) {
                     sum += this.weightedJacobian[i][pj] * qtf[i];
                  }

                  maxCosine = FastMath.max(maxCosine, FastMath.abs(sum) / (s * currentCost));
               }
            }
         }

         if (maxCosine <= this.orthoTolerance) {
            this.setCost(currentCost);
            return current;
         }

         for (int jx = 0; jx < nC; jx++) {
            diag[jx] = FastMath.max(diag[jx], this.jacNorm[jx]);
         }

         double ratio = 0.0;

         while (ratio < 1.0E-4) {
            for (int jx = 0; jx < this.solvedCols; jx++) {
               int pj = this.permutation[jx];
               oldX[pj] = currentPoint[pj];
            }

            double previousCost = currentCost;
            double[] tmpVec = this.weightedResidual;
            this.weightedResidual = oldRes;
            oldRes = tmpVec;
            oldObj = currentObjective;
            this.determineLMParameter(qtf, delta, diag, work1, work2, work3);
            double lmNorm = 0.0;

            for (int jx = 0; jx < this.solvedCols; jx++) {
               int pj = this.permutation[jx];
               this.lmDir[pj] = -this.lmDir[pj];
               currentPoint[pj] = oldX[pj] + this.lmDir[pj];
               double s = diag[pj] * this.lmDir[pj];
               lmNorm += s * s;
            }

            lmNorm = FastMath.sqrt(lmNorm);
            if (firstIteration) {
               delta = FastMath.min(delta, lmNorm);
            }

            currentObjective = this.computeObjectiveValue(currentPoint);
            currentResiduals = this.computeResiduals(currentObjective);
            current = new PointVectorValuePair(currentPoint, currentObjective);
            currentCost = this.computeCost(currentResiduals);
            double actRed = -1.0;
            if (0.1 * currentCost < previousCost) {
               double r = currentCost / previousCost;
               actRed = 1.0 - r * r;
            }

            for (int jx = 0; jx < this.solvedCols; jx++) {
               int pj = this.permutation[jx];
               double dirJ = this.lmDir[pj];
               work1[jx] = 0.0;

               for (int i = 0; i <= jx; i++) {
                  work1[i] += this.weightedJacobian[i][pj] * dirJ;
               }
            }

            double coeff1 = 0.0;

            for (int jx = 0; jx < this.solvedCols; jx++) {
               coeff1 += work1[jx] * work1[jx];
            }

            double pc2 = previousCost * previousCost;
            coeff1 /= pc2;
            double coeff2 = this.lmPar * lmNorm * lmNorm / pc2;
            double preRed = coeff1 + 2.0 * coeff2;
            double dirDer = -(coeff1 + coeff2);
            ratio = preRed == 0.0 ? 0.0 : actRed / preRed;
            if (ratio <= 0.25) {
               double tmp = actRed < 0.0 ? 0.5 * dirDer / (dirDer + 0.5 * actRed) : 0.5;
               if (0.1 * currentCost >= previousCost || tmp < 0.1) {
                  tmp = 0.1;
               }

               delta = tmp * FastMath.min(delta, 10.0 * lmNorm);
               this.lmPar /= tmp;
            } else if (this.lmPar == 0.0 || ratio >= 0.75) {
               delta = 2.0 * lmNorm;
               this.lmPar *= 0.5;
            }

            if (ratio >= 1.0E-4) {
               firstIteration = false;
               xNorm = 0.0;

               for (int k = 0; k < nC; k++) {
                  double xK = diag[k] * currentPoint[k];
                  xNorm += xK * xK;
               }

               xNorm = FastMath.sqrt(xNorm);
               if (checker != null && checker.converged(this.getIterations(), previous, current)) {
                  this.setCost(currentCost);
                  return current;
               }
            } else {
               currentCost = previousCost;

               for (int jx = 0; jx < this.solvedCols; jx++) {
                  int pj = this.permutation[jx];
                  currentPoint[pj] = oldX[pj];
               }

               double[] var62 = this.weightedResidual;
               this.weightedResidual = tmpVec;
               oldRes = var62;
               currentObjective = oldObj;
               current = new PointVectorValuePair(currentPoint, oldObj);
            }

            if (FastMath.abs(actRed) <= this.costRelativeTolerance && preRed <= this.costRelativeTolerance && ratio <= 2.0
               || delta <= this.parRelativeTolerance * xNorm) {
               this.setCost(currentCost);
               return current;
            }

            if (FastMath.abs(actRed) <= TWO_EPS && preRed <= TWO_EPS && ratio <= 2.0) {
               throw new ConvergenceException(LocalizedFormats.TOO_SMALL_COST_RELATIVE_TOLERANCE, this.costRelativeTolerance);
            }

            if (delta <= TWO_EPS * xNorm) {
               throw new ConvergenceException(LocalizedFormats.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE, this.parRelativeTolerance);
            }

            if (maxCosine <= TWO_EPS) {
               throw new ConvergenceException(LocalizedFormats.TOO_SMALL_ORTHOGONALITY_TOLERANCE, this.orthoTolerance);
            }
         }
      }
   }

   private void determineLMParameter(double[] qy, double delta, double[] diag, double[] work1, double[] work2, double[] work3) {
      int nC = this.weightedJacobian[0].length;

      for (int j = 0; j < this.rank; j++) {
         this.lmDir[this.permutation[j]] = qy[j];
      }

      for (int j = this.rank; j < nC; j++) {
         this.lmDir[this.permutation[j]] = 0.0;
      }

      for (int k = this.rank - 1; k >= 0; k--) {
         int pk = this.permutation[k];
         double ypk = this.lmDir[pk] / this.diagR[pk];

         for (int i = 0; i < k; i++) {
            this.lmDir[this.permutation[i]] = this.lmDir[this.permutation[i]] - ypk * this.weightedJacobian[i][pk];
         }

         this.lmDir[pk] = ypk;
      }

      double dxNorm = 0.0;

      for (int j = 0; j < this.solvedCols; j++) {
         int pj = this.permutation[j];
         double s = diag[pj] * this.lmDir[pj];
         work1[pj] = s;
         dxNorm += s * s;
      }

      dxNorm = FastMath.sqrt(dxNorm);
      double fp = dxNorm - delta;
      if (fp <= 0.1 * delta) {
         this.lmPar = 0.0;
      } else {
         double parl = 0.0;
         if (this.rank == this.solvedCols) {
            for (int j = 0; j < this.solvedCols; j++) {
               int pj = this.permutation[j];
               work1[pj] *= diag[pj] / dxNorm;
            }

            double sum2 = 0.0;

            for (int j = 0; j < this.solvedCols; j++) {
               int pj = this.permutation[j];
               double sum = 0.0;

               for (int i = 0; i < j; i++) {
                  sum += this.weightedJacobian[i][pj] * work1[this.permutation[i]];
               }

               double s = (work1[pj] - sum) / this.diagR[pj];
               work1[pj] = s;
               sum2 += s * s;
            }

            parl = fp / (delta * sum2);
         }

         double sum2 = 0.0;

         for (int j = 0; j < this.solvedCols; j++) {
            int pj = this.permutation[j];
            double sum = 0.0;

            for (int i = 0; i <= j; i++) {
               sum += this.weightedJacobian[i][pj] * qy[i];
            }

            sum /= diag[pj];
            sum2 += sum * sum;
         }

         double gNorm = FastMath.sqrt(sum2);
         double paru = gNorm / delta;
         if (paru == 0.0) {
            paru = Precision.SAFE_MIN / FastMath.min(delta, 0.1);
         }

         this.lmPar = FastMath.min(paru, FastMath.max(this.lmPar, parl));
         if (this.lmPar == 0.0) {
            this.lmPar = gNorm / dxNorm;
         }

         for (int countdown = 10; countdown >= 0; countdown--) {
            if (this.lmPar == 0.0) {
               this.lmPar = FastMath.max(Precision.SAFE_MIN, 0.001 * paru);
            }

            double sPar = FastMath.sqrt(this.lmPar);

            for (int j = 0; j < this.solvedCols; j++) {
               int pj = this.permutation[j];
               work1[pj] = sPar * diag[pj];
            }

            this.determineLMDirection(qy, work1, work2, work3);
            dxNorm = 0.0;

            for (int j = 0; j < this.solvedCols; j++) {
               int pj = this.permutation[j];
               double s = diag[pj] * this.lmDir[pj];
               work3[pj] = s;
               dxNorm += s * s;
            }

            dxNorm = FastMath.sqrt(dxNorm);
            double previousFP = fp;
            fp = dxNorm - delta;
            if (FastMath.abs(fp) <= 0.1 * delta || parl == 0.0 && fp <= previousFP && previousFP < 0.0) {
               return;
            }

            for (int j = 0; j < this.solvedCols; j++) {
               int pj = this.permutation[j];
               work1[pj] = work3[pj] * diag[pj] / dxNorm;
            }

            for (int j = 0; j < this.solvedCols; j++) {
               int pj = this.permutation[j];
               work1[pj] /= work2[j];
               double tmp = work1[pj];

               for (int i = j + 1; i < this.solvedCols; i++) {
                  work1[this.permutation[i]] = work1[this.permutation[i]] - this.weightedJacobian[i][pj] * tmp;
               }
            }

            sum2 = 0.0;

            for (int j = 0; j < this.solvedCols; j++) {
               double s = work1[this.permutation[j]];
               sum2 += s * s;
            }

            double correction = fp / (delta * sum2);
            if (fp > 0.0) {
               parl = FastMath.max(parl, this.lmPar);
            } else if (fp < 0.0) {
               paru = FastMath.min(paru, this.lmPar);
            }

            this.lmPar = FastMath.max(parl, this.lmPar + correction);
         }
      }
   }

   private void determineLMDirection(double[] qy, double[] diag, double[] lmDiag, double[] work) {
      for (int j = 0; j < this.solvedCols; j++) {
         int pj = this.permutation[j];

         for (int i = j + 1; i < this.solvedCols; i++) {
            this.weightedJacobian[i][pj] = this.weightedJacobian[j][this.permutation[i]];
         }

         this.lmDir[j] = this.diagR[pj];
         work[j] = qy[j];
      }

      for (int j = 0; j < this.solvedCols; j++) {
         int pj = this.permutation[j];
         double dpj = diag[pj];
         if (dpj != 0.0) {
            Arrays.fill(lmDiag, j + 1, lmDiag.length, 0.0);
         }

         lmDiag[j] = dpj;
         double qtbpj = 0.0;

         for (int k = j; k < this.solvedCols; k++) {
            int pk = this.permutation[k];
            if (lmDiag[k] != 0.0) {
               double rkk = this.weightedJacobian[k][pk];
               double sin;
               double cos;
               if (FastMath.abs(rkk) < FastMath.abs(lmDiag[k])) {
                  double cotan = rkk / lmDiag[k];
                  sin = 1.0 / FastMath.sqrt(1.0 + cotan * cotan);
                  cos = sin * cotan;
               } else {
                  double tan = lmDiag[k] / rkk;
                  cos = 1.0 / FastMath.sqrt(1.0 + tan * tan);
                  sin = cos * tan;
               }

               this.weightedJacobian[k][pk] = cos * rkk + sin * lmDiag[k];
               double temp = cos * work[k] + sin * qtbpj;
               qtbpj = -sin * work[k] + cos * qtbpj;
               work[k] = temp;

               for (int i = k + 1; i < this.solvedCols; i++) {
                  double rik = this.weightedJacobian[i][pk];
                  double temp2 = cos * rik + sin * lmDiag[i];
                  lmDiag[i] = -sin * rik + cos * lmDiag[i];
                  this.weightedJacobian[i][pk] = temp2;
               }
            }
         }

         lmDiag[j] = this.weightedJacobian[j][this.permutation[j]];
         this.weightedJacobian[j][this.permutation[j]] = this.lmDir[j];
      }

      int nSing = this.solvedCols;

      for (int j = 0; j < this.solvedCols; j++) {
         if (lmDiag[j] == 0.0 && nSing == this.solvedCols) {
            nSing = j;
         }

         if (nSing < this.solvedCols) {
            work[j] = 0.0;
         }
      }

      if (nSing > 0) {
         for (int j = nSing - 1; j >= 0; j--) {
            int pj = this.permutation[j];
            double sum = 0.0;

            for (int i = j + 1; i < nSing; i++) {
               sum += this.weightedJacobian[i][pj] * work[i];
            }

            work[j] = (work[j] - sum) / lmDiag[j];
         }
      }

      for (int j = 0; j < this.lmDir.length; j++) {
         this.lmDir[this.permutation[j]] = work[j];
      }
   }

   private void qrDecomposition(RealMatrix jacobian) throws ConvergenceException {
      this.weightedJacobian = jacobian.scalarMultiply(-1.0).getData();
      int nR = this.weightedJacobian.length;
      int nC = this.weightedJacobian[0].length;

      for (int k = 0; k < nC; k++) {
         this.permutation[k] = k;
         double norm2 = 0.0;

         for (int i = 0; i < nR; i++) {
            double akk = this.weightedJacobian[i][k];
            norm2 += akk * akk;
         }

         this.jacNorm[k] = FastMath.sqrt(norm2);
      }

      for (int k = 0; k < nC; k++) {
         int nextColumn = -1;
         double ak2 = Double.NEGATIVE_INFINITY;

         for (int i = k; i < nC; i++) {
            double norm2 = 0.0;

            for (int j = k; j < nR; j++) {
               double aki = this.weightedJacobian[j][this.permutation[i]];
               norm2 += aki * aki;
            }

            if (Double.isInfinite(norm2) || Double.isNaN(norm2)) {
               throw new ConvergenceException(LocalizedFormats.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, nR, nC);
            }

            if (norm2 > ak2) {
               nextColumn = i;
               ak2 = norm2;
            }
         }

         if (ak2 <= this.qrRankingThreshold) {
            this.rank = k;
            return;
         }

         int pk = this.permutation[nextColumn];
         this.permutation[nextColumn] = this.permutation[k];
         this.permutation[k] = pk;
         double akk = this.weightedJacobian[k][pk];
         double alpha = akk > 0.0 ? -FastMath.sqrt(ak2) : FastMath.sqrt(ak2);
         double betak = 1.0 / (ak2 - akk * alpha);
         this.beta[pk] = betak;
         this.diagR[pk] = alpha;
         this.weightedJacobian[k][pk] = this.weightedJacobian[k][pk] - alpha;

         for (int dk = nC - 1 - k; dk > 0; dk--) {
            double gamma = 0.0;

            for (int j = k; j < nR; j++) {
               gamma += this.weightedJacobian[j][pk] * this.weightedJacobian[j][this.permutation[k + dk]];
            }

            gamma *= betak;

            for (int j = k; j < nR; j++) {
               this.weightedJacobian[j][this.permutation[k + dk]] = this.weightedJacobian[j][this.permutation[k + dk]] - gamma * this.weightedJacobian[j][pk];
            }
         }
      }

      this.rank = this.solvedCols;
   }

   private void qTy(double[] y) {
      int nR = this.weightedJacobian.length;
      int nC = this.weightedJacobian[0].length;

      for (int k = 0; k < nC; k++) {
         int pk = this.permutation[k];
         double gamma = 0.0;

         for (int i = k; i < nR; i++) {
            gamma += this.weightedJacobian[i][pk] * y[i];
         }

         gamma *= this.beta[pk];

         for (int i = k; i < nR; i++) {
            y[i] -= gamma * this.weightedJacobian[i][pk];
         }
      }
   }

   private void checkParameters() {
      if (this.getLowerBound() != null || this.getUpperBound() != null) {
         throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT);
      }
   }
}
