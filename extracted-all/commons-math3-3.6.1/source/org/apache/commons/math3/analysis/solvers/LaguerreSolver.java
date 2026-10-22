package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;

public class LaguerreSolver extends AbstractPolynomialSolver {
   private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
   private final LaguerreSolver.ComplexSolver complexSolver = new LaguerreSolver.ComplexSolver();

   public LaguerreSolver() {
      this(1.0E-6);
   }

   public LaguerreSolver(double absoluteAccuracy) {
      super(absoluteAccuracy);
   }

   public LaguerreSolver(double relativeAccuracy, double absoluteAccuracy) {
      super(relativeAccuracy, absoluteAccuracy);
   }

   public LaguerreSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy) {
      super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
   }

   @Override
   public double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
      double min = this.getMin();
      double max = this.getMax();
      double initial = this.getStartValue();
      double functionValueAccuracy = this.getFunctionValueAccuracy();
      this.verifySequence(min, initial, max);
      double yInitial = this.computeObjectiveValue(initial);
      if (FastMath.abs(yInitial) <= functionValueAccuracy) {
         return initial;
      } else {
         double yMin = this.computeObjectiveValue(min);
         if (FastMath.abs(yMin) <= functionValueAccuracy) {
            return min;
         } else if (yInitial * yMin < 0.0) {
            return this.laguerre(min, initial, yMin, yInitial);
         } else {
            double yMax = this.computeObjectiveValue(max);
            if (FastMath.abs(yMax) <= functionValueAccuracy) {
               return max;
            } else if (yInitial * yMax < 0.0) {
               return this.laguerre(initial, max, yInitial, yMax);
            } else {
               throw new NoBracketingException(min, max, yMin, yMax);
            }
         }
      }
   }

   @Deprecated
   public double laguerre(double lo, double hi, double fLo, double fHi) {
      Complex[] c = ComplexUtils.convertToComplex(this.getCoefficients());
      Complex initial = new Complex(0.5 * (lo + hi), 0.0);
      Complex z = this.complexSolver.solve(c, initial);
      if (this.complexSolver.isRoot(lo, hi, z)) {
         return z.getReal();
      } else {
         double r = Double.NaN;
         Complex[] root = this.complexSolver.solveAll(c, initial);

         for (int i = 0; i < root.length; i++) {
            if (this.complexSolver.isRoot(lo, hi, root[i])) {
               r = root[i].getReal();
               break;
            }
         }

         return r;
      }
   }

   public Complex[] solveAllComplex(double[] coefficients, double initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
      return this.solveAllComplex(coefficients, initial, Integer.MAX_VALUE);
   }

   public Complex[] solveAllComplex(double[] coefficients, double initial, int maxEval) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
      this.setup(maxEval, new PolynomialFunction(coefficients), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, initial);
      return this.complexSolver.solveAll(ComplexUtils.convertToComplex(coefficients), new Complex(initial, 0.0));
   }

   public Complex solveComplex(double[] coefficients, double initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
      return this.solveComplex(coefficients, initial, Integer.MAX_VALUE);
   }

   public Complex solveComplex(double[] coefficients, double initial, int maxEval) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
      this.setup(maxEval, new PolynomialFunction(coefficients), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, initial);
      return this.complexSolver.solve(ComplexUtils.convertToComplex(coefficients), new Complex(initial, 0.0));
   }

   private class ComplexSolver {
      private ComplexSolver() {
      }

      public boolean isRoot(double min, double max, Complex z) {
         if (!LaguerreSolver.this.isSequence(min, z.getReal(), max)) {
            return false;
         } else {
            double tolerance = FastMath.max(LaguerreSolver.this.getRelativeAccuracy() * z.abs(), LaguerreSolver.this.getAbsoluteAccuracy());
            return FastMath.abs(z.getImaginary()) <= tolerance || z.abs() <= LaguerreSolver.this.getFunctionValueAccuracy();
         }
      }

      public Complex[] solveAll(Complex[] coefficients, Complex initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
         if (coefficients == null) {
            throw new NullArgumentException();
         } else {
            int n = coefficients.length - 1;
            if (n == 0) {
               throw new NoDataException(LocalizedFormats.POLYNOMIAL);
            } else {
               Complex[] c = new Complex[n + 1];

               for (int i = 0; i <= n; i++) {
                  c[i] = coefficients[i];
               }

               Complex[] root = new Complex[n];

               for (int i = 0; i < n; i++) {
                  Complex[] subarray = new Complex[n - i + 1];
                  System.arraycopy(c, 0, subarray, 0, subarray.length);
                  root[i] = this.solve(subarray, initial);
                  Complex newc = c[n - i];
                  Complex oldc = null;

                  for (int j = n - i - 1; j >= 0; j--) {
                     oldc = c[j];
                     c[j] = newc;
                     newc = oldc.add(newc.multiply(root[i]));
                  }
               }

               return root;
            }
         }
      }

      public Complex solve(Complex[] coefficients, Complex initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
         if (coefficients == null) {
            throw new NullArgumentException();
         } else {
            int n = coefficients.length - 1;
            if (n == 0) {
               throw new NoDataException(LocalizedFormats.POLYNOMIAL);
            } else {
               double absoluteAccuracy = LaguerreSolver.this.getAbsoluteAccuracy();
               double relativeAccuracy = LaguerreSolver.this.getRelativeAccuracy();
               double functionValueAccuracy = LaguerreSolver.this.getFunctionValueAccuracy();
               Complex nC = new Complex((double)n, 0.0);
               Complex n1C = new Complex((double)(n - 1), 0.0);
               Complex z = initial;
               Complex oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

               while (true) {
                  Complex pv = coefficients[n];
                  Complex dv = Complex.ZERO;
                  Complex d2v = Complex.ZERO;

                  for (int j = n - 1; j >= 0; j--) {
                     d2v = dv.add(z.multiply(d2v));
                     dv = pv.add(z.multiply(dv));
                     pv = coefficients[j].add(z.multiply(pv));
                  }

                  d2v = d2v.multiply(new Complex(2.0, 0.0));
                  double tolerance = FastMath.max(relativeAccuracy * z.abs(), absoluteAccuracy);
                  if (z.subtract(oldz).abs() <= tolerance) {
                     return z;
                  }

                  if (pv.abs() <= functionValueAccuracy) {
                     return z;
                  }

                  Complex G = dv.divide(pv);
                  Complex G2 = G.multiply(G);
                  Complex H = G2.subtract(d2v.divide(pv));
                  Complex delta = n1C.multiply(nC.multiply(H).subtract(G2));
                  Complex deltaSqrt = delta.sqrt();
                  Complex dplus = G.add(deltaSqrt);
                  Complex dminus = G.subtract(deltaSqrt);
                  Complex denominator = dplus.abs() > dminus.abs() ? dplus : dminus;
                  if (denominator.equals(new Complex(0.0, 0.0))) {
                     z = z.add(new Complex(absoluteAccuracy, absoluteAccuracy));
                     oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                  } else {
                     oldz = z;
                     z = z.subtract(nC.divide(denominator));
                  }

                  LaguerreSolver.this.incrementEvaluationCount();
               }
            }
         }
      }
   }
}
