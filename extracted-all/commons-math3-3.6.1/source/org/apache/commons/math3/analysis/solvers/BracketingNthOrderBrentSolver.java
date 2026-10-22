package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class BracketingNthOrderBrentSolver extends AbstractUnivariateSolver implements BracketedUnivariateSolver<UnivariateFunction> {
   private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
   private static final int DEFAULT_MAXIMAL_ORDER = 5;
   private static final int MAXIMAL_AGING = 2;
   private static final double REDUCTION_FACTOR = 0.0625;
   private final int maximalOrder;
   private AllowedSolution allowed;

   public BracketingNthOrderBrentSolver() {
      this(1.0E-6, 5);
   }

   public BracketingNthOrderBrentSolver(double absoluteAccuracy, int maximalOrder) throws NumberIsTooSmallException {
      super(absoluteAccuracy);
      if (maximalOrder < 2) {
         throw new NumberIsTooSmallException(maximalOrder, 2, true);
      } else {
         this.maximalOrder = maximalOrder;
         this.allowed = AllowedSolution.ANY_SIDE;
      }
   }

   public BracketingNthOrderBrentSolver(double relativeAccuracy, double absoluteAccuracy, int maximalOrder) throws NumberIsTooSmallException {
      super(relativeAccuracy, absoluteAccuracy);
      if (maximalOrder < 2) {
         throw new NumberIsTooSmallException(maximalOrder, 2, true);
      } else {
         this.maximalOrder = maximalOrder;
         this.allowed = AllowedSolution.ANY_SIDE;
      }
   }

   public BracketingNthOrderBrentSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy, int maximalOrder) throws NumberIsTooSmallException {
      super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
      if (maximalOrder < 2) {
         throw new NumberIsTooSmallException(maximalOrder, 2, true);
      } else {
         this.maximalOrder = maximalOrder;
         this.allowed = AllowedSolution.ANY_SIDE;
      }
   }

   public int getMaximalOrder() {
      return this.maximalOrder;
   }

   @Override
   protected double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
      double[] x = new double[this.maximalOrder + 1];
      double[] y = new double[this.maximalOrder + 1];
      x[0] = this.getMin();
      x[1] = this.getStartValue();
      x[2] = this.getMax();
      this.verifySequence(x[0], x[1], x[2]);
      y[1] = this.computeObjectiveValue(x[1]);
      if (Precision.equals(y[1], 0.0, 1)) {
         return x[1];
      } else {
         y[0] = this.computeObjectiveValue(x[0]);
         if (Precision.equals(y[0], 0.0, 1)) {
            return x[0];
         } else {
            int nbPoints;
            int signChangeIndex;
            if (y[0] * y[1] < 0.0) {
               nbPoints = 2;
               signChangeIndex = 1;
            } else {
               y[2] = this.computeObjectiveValue(x[2]);
               if (Precision.equals(y[2], 0.0, 1)) {
                  return x[2];
               }

               if (!(y[1] * y[2] < 0.0)) {
                  throw new NoBracketingException(x[0], x[2], y[0], y[2]);
               }

               nbPoints = 3;
               signChangeIndex = 2;
            }

            double[] tmpX = new double[x.length];
            double xA = x[signChangeIndex - 1];
            double yA = y[signChangeIndex - 1];
            double absYA = FastMath.abs(yA);
            int agingA = 0;
            double xB = x[signChangeIndex];
            double yB = y[signChangeIndex];
            double absYB = FastMath.abs(yB);
            int agingB = 0;

            while (true) {
               double xTol = this.getAbsoluteAccuracy() + this.getRelativeAccuracy() * FastMath.max(FastMath.abs(xA), FastMath.abs(xB));
               if (xB - xA <= xTol || FastMath.max(absYA, absYB) < this.getFunctionValueAccuracy()) {
                  switch (this.allowed) {
                     case ANY_SIDE:
                        return absYA < absYB ? xA : xB;
                     case LEFT_SIDE:
                        return xA;
                     case RIGHT_SIDE:
                        return xB;
                     case BELOW_SIDE:
                        return yA <= 0.0 ? xA : xB;
                     case ABOVE_SIDE:
                        return yA < 0.0 ? xB : xA;
                     default:
                        throw new MathInternalError();
                  }
               }

               double targetY;
               if (agingA >= 2) {
                  int p = agingA - 2;
                  double weightA = (double)((1 << p) - 1);
                  double weightB = (double)(p + 1);
                  targetY = (weightA * yA - weightB * 0.0625 * yB) / (weightA + weightB);
               } else if (agingB >= 2) {
                  int p = agingB - 2;
                  double weightA = (double)(p + 1);
                  double weightB = (double)((1 << p) - 1);
                  targetY = (weightB * yB - weightA * 0.0625 * yA) / (weightA + weightB);
               } else {
                  targetY = 0.0;
               }

               int start = 0;
               int end = nbPoints;

               double nextX;
               do {
                  System.arraycopy(x, start, tmpX, start, end - start);
                  nextX = this.guessX(targetY, tmpX, y, start, end);
                  if (!(nextX > xA) || !(nextX < xB)) {
                     if (signChangeIndex - start >= end - signChangeIndex) {
                        start++;
                     } else {
                        end--;
                     }

                     nextX = Double.NaN;
                  }
               } while (Double.isNaN(nextX) && end - start > 1);

               if (Double.isNaN(nextX)) {
                  nextX = xA + 0.5 * (xB - xA);
                  start = signChangeIndex - 1;
                  end = signChangeIndex;
               }

               double nextY = this.computeObjectiveValue(nextX);
               if (Precision.equals(nextY, 0.0, 1)) {
                  return nextX;
               }

               if (nbPoints > 2 && end - start != nbPoints) {
                  nbPoints = end - start;
                  System.arraycopy(x, start, x, 0, nbPoints);
                  System.arraycopy(y, start, y, 0, nbPoints);
                  signChangeIndex -= start;
               } else if (nbPoints == x.length) {
                  nbPoints--;
                  if (signChangeIndex >= (x.length + 1) / 2) {
                     System.arraycopy(x, 1, x, 0, nbPoints);
                     System.arraycopy(y, 1, y, 0, nbPoints);
                     signChangeIndex--;
                  }
               }

               System.arraycopy(x, signChangeIndex, x, signChangeIndex + 1, nbPoints - signChangeIndex);
               x[signChangeIndex] = nextX;
               System.arraycopy(y, signChangeIndex, y, signChangeIndex + 1, nbPoints - signChangeIndex);
               y[signChangeIndex] = nextY;
               nbPoints++;
               if (nextY * yA <= 0.0) {
                  xB = nextX;
                  yB = nextY;
                  absYB = FastMath.abs(nextY);
                  agingA++;
                  agingB = 0;
               } else {
                  xA = nextX;
                  yA = nextY;
                  absYA = FastMath.abs(nextY);
                  agingA = 0;
                  agingB++;
                  signChangeIndex++;
               }
            }
         }
      }
   }

   private double guessX(double targetY, double[] x, double[] y, int start, int end) {
      for (int i = start; i < end - 1; i++) {
         int delta = i + 1 - start;

         for (int j = end - 1; j > i; j--) {
            x[j] = (x[j] - x[j - 1]) / (y[j] - y[j - delta]);
         }
      }

      double x0 = 0.0;

      for (int j = end - 1; j >= start; j--) {
         x0 = x[j] + x0 * (targetY - y[j]);
      }

      return x0;
   }

   @Override
   public double solve(int maxEval, UnivariateFunction f, double min, double max, AllowedSolution allowedSolution) throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
      this.allowed = allowedSolution;
      return super.solve(maxEval, f, min, max);
   }

   @Override
   public double solve(int maxEval, UnivariateFunction f, double min, double max, double startValue, AllowedSolution allowedSolution) throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
      this.allowed = allowedSolution;
      return super.solve(maxEval, f, min, max, startValue);
   }
}
