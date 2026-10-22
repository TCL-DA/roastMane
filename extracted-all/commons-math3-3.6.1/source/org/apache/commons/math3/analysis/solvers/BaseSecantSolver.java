package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.util.FastMath;

public abstract class BaseSecantSolver extends AbstractUnivariateSolver implements BracketedUnivariateSolver<UnivariateFunction> {
   protected static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
   private AllowedSolution allowed = AllowedSolution.ANY_SIDE;
   private final BaseSecantSolver.Method method;

   protected BaseSecantSolver(double absoluteAccuracy, BaseSecantSolver.Method method) {
      super(absoluteAccuracy);
      this.method = method;
   }

   protected BaseSecantSolver(double relativeAccuracy, double absoluteAccuracy, BaseSecantSolver.Method method) {
      super(relativeAccuracy, absoluteAccuracy);
      this.method = method;
   }

   protected BaseSecantSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy, BaseSecantSolver.Method method) {
      super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
      this.method = method;
   }

   @Override
   public double solve(int maxEval, UnivariateFunction f, double min, double max, AllowedSolution allowedSolution) {
      return this.solve(maxEval, f, min, max, min + 0.5 * (max - min), allowedSolution);
   }

   @Override
   public double solve(int maxEval, UnivariateFunction f, double min, double max, double startValue, AllowedSolution allowedSolution) {
      this.allowed = allowedSolution;
      return super.solve(maxEval, f, min, max, startValue);
   }

   @Override
   public double solve(int maxEval, UnivariateFunction f, double min, double max, double startValue) {
      return this.solve(maxEval, f, min, max, startValue, AllowedSolution.ANY_SIDE);
   }

   @Override
   protected final double doSolve() throws ConvergenceException {
      double x0 = this.getMin();
      double x1 = this.getMax();
      double f0 = this.computeObjectiveValue(x0);
      double f1 = this.computeObjectiveValue(x1);
      if (f0 == 0.0) {
         return x0;
      } else if (f1 == 0.0) {
         return x1;
      } else {
         this.verifyBracketing(x0, x1);
         double ftol = this.getFunctionValueAccuracy();
         double atol = this.getAbsoluteAccuracy();
         double rtol = this.getRelativeAccuracy();
         boolean inverted = false;

         double x;
         double fx;
         do {
            x = x1 - f1 * (x1 - x0) / (f1 - f0);
            fx = this.computeObjectiveValue(x);
            if (fx == 0.0) {
               return x;
            }

            if (f1 * fx < 0.0) {
               x0 = x1;
               f0 = f1;
               inverted = !inverted;
            } else {
               switch (this.method) {
                  case ILLINOIS:
                     f0 *= 0.5;
                     break;
                  case PEGASUS:
                     f0 *= f1 / (f1 + fx);
                     break;
                  case REGULA_FALSI:
                     if (x == x1) {
                        throw new ConvergenceException();
                     }
                     break;
                  default:
                     throw new MathInternalError();
               }
            }

            x1 = x;
            f1 = fx;
            if (FastMath.abs(fx) <= ftol) {
               switch (this.allowed) {
                  case ANY_SIDE:
                     return x;
                  case LEFT_SIDE:
                     if (inverted) {
                        return x;
                     }
                     break;
                  case RIGHT_SIDE:
                     if (!inverted) {
                        return x;
                     }
                     break;
                  case BELOW_SIDE:
                     if (fx <= 0.0) {
                        return x;
                     }
                     break;
                  case ABOVE_SIDE:
                     if (fx >= 0.0) {
                        return x;
                     }
                     break;
                  default:
                     throw new MathInternalError();
               }
            }
         } while (!(FastMath.abs(x - x0) < FastMath.max(rtol * FastMath.abs(x), atol)));

         switch (this.allowed) {
            case ANY_SIDE:
               return x;
            case LEFT_SIDE:
               return inverted ? x : x0;
            case RIGHT_SIDE:
               return inverted ? x0 : x;
            case BELOW_SIDE:
               return fx <= 0.0 ? x : x0;
            case ABOVE_SIDE:
               return fx >= 0.0 ? x : x0;
            default:
               throw new MathInternalError();
         }
      }
   }

   protected static enum Method {
      REGULA_FALSI,
      ILLINOIS,
      PEGASUS;
   }
}
