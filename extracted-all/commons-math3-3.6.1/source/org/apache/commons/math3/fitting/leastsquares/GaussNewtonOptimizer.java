package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.util.Pair;

public class GaussNewtonOptimizer implements LeastSquaresOptimizer {
   private static final double SINGULARITY_THRESHOLD = 1.0E-11;
   private final GaussNewtonOptimizer.Decomposition decomposition;

   public GaussNewtonOptimizer() {
      this(GaussNewtonOptimizer.Decomposition.QR);
   }

   public GaussNewtonOptimizer(GaussNewtonOptimizer.Decomposition decomposition) {
      this.decomposition = decomposition;
   }

   public GaussNewtonOptimizer.Decomposition getDecomposition() {
      return this.decomposition;
   }

   public GaussNewtonOptimizer withDecomposition(GaussNewtonOptimizer.Decomposition newDecomposition) {
      return new GaussNewtonOptimizer(newDecomposition);
   }

   @Override
   public LeastSquaresOptimizer.Optimum optimize(LeastSquaresProblem lsp) {
      Incrementor evaluationCounter = lsp.getEvaluationCounter();
      Incrementor iterationCounter = lsp.getIterationCounter();
      ConvergenceChecker<LeastSquaresProblem.Evaluation> checker = lsp.getConvergenceChecker();
      if (checker == null) {
         throw new NullArgumentException();
      } else {
         RealVector currentPoint = lsp.getStart();
         LeastSquaresProblem.Evaluation current = null;

         while (true) {
            iterationCounter.incrementCount();
            LeastSquaresProblem.Evaluation previous = current;
            evaluationCounter.incrementCount();
            current = lsp.evaluate(currentPoint);
            RealVector currentResiduals = current.getResiduals();
            RealMatrix weightedJacobian = current.getJacobian();
            currentPoint = current.getPoint();
            if (previous != null && checker.converged(iterationCounter.getCount(), previous, current)) {
               return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount());
            }

            RealVector dX = this.decomposition.solve(weightedJacobian, currentResiduals);
            currentPoint = currentPoint.add(dX);
         }
      }
   }

   @Override
   public String toString() {
      return "GaussNewtonOptimizer{decomposition=" + this.decomposition + '}';
   }

   private static Pair<RealMatrix, RealVector> computeNormalMatrix(RealMatrix jacobian, RealVector residuals) {
      int nR = jacobian.getRowDimension();
      int nC = jacobian.getColumnDimension();
      RealMatrix normal = MatrixUtils.createRealMatrix(nC, nC);
      RealVector jTr = new ArrayRealVector(nC);

      for (int i = 0; i < nR; i++) {
         for (int j = 0; j < nC; j++) {
            jTr.setEntry(j, jTr.getEntry(j) + residuals.getEntry(i) * jacobian.getEntry(i, j));
         }

         for (int k = 0; k < nC; k++) {
            for (int l = k; l < nC; l++) {
               normal.setEntry(k, l, normal.getEntry(k, l) + jacobian.getEntry(i, k) * jacobian.getEntry(i, l));
            }
         }
      }

      for (int i = 0; i < nC; i++) {
         for (int j = 0; j < i; j++) {
            normal.setEntry(i, j, normal.getEntry(j, i));
         }
      }

      return new Pair<>(normal, jTr);
   }

   public static enum Decomposition {
      LU {
         @Override
         protected RealVector solve(RealMatrix jacobian, RealVector residuals) {
            try {
               Pair<RealMatrix, RealVector> normalEquation = GaussNewtonOptimizer.computeNormalMatrix(jacobian, residuals);
               RealMatrix normal = normalEquation.getFirst();
               RealVector jTr = normalEquation.getSecond();
               return new LUDecomposition(normal, 1.0E-11).getSolver().solve(jTr);
            } catch (SingularMatrixException var6) {
               throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, var6);
            }
         }
      },
      QR {
         @Override
         protected RealVector solve(RealMatrix jacobian, RealVector residuals) {
            try {
               return new QRDecomposition(jacobian, 1.0E-11).getSolver().solve(residuals);
            } catch (SingularMatrixException var4) {
               throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, var4);
            }
         }
      },
      CHOLESKY {
         @Override
         protected RealVector solve(RealMatrix jacobian, RealVector residuals) {
            try {
               Pair<RealMatrix, RealVector> normalEquation = GaussNewtonOptimizer.computeNormalMatrix(jacobian, residuals);
               RealMatrix normal = normalEquation.getFirst();
               RealVector jTr = normalEquation.getSecond();
               return new CholeskyDecomposition(normal, 1.0E-11, 1.0E-11).getSolver().solve(jTr);
            } catch (NonPositiveDefiniteMatrixException var6) {
               throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, var6);
            }
         }
      },
      SVD {
         @Override
         protected RealVector solve(RealMatrix jacobian, RealVector residuals) {
            return new SingularValueDecomposition(jacobian).getSolver().solve(residuals);
         }
      };

      private Decomposition() {
      }

      protected abstract RealVector solve(RealMatrix var1, RealVector var2);
   }
}
