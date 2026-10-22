package org.apache.commons.math3.analysis.polynomials;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class PolynomialFunctionLagrangeForm implements UnivariateFunction {
   private double[] coefficients;
   private final double[] x;
   private final double[] y;
   private boolean coefficientsComputed;

   public PolynomialFunctionLagrangeForm(double[] x, double[] y) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
      this.x = new double[x.length];
      this.y = new double[y.length];
      System.arraycopy(x, 0, this.x, 0, x.length);
      System.arraycopy(y, 0, this.y, 0, y.length);
      this.coefficientsComputed = false;
      if (!verifyInterpolationArray(x, y, false)) {
         MathArrays.sortInPlace(this.x, this.y);
         verifyInterpolationArray(this.x, this.y, true);
      }
   }

   @Override
   public double value(double z) {
      return evaluateInternal(this.x, this.y, z);
   }

   public int degree() {
      return this.x.length - 1;
   }

   public double[] getInterpolatingPoints() {
      double[] out = new double[this.x.length];
      System.arraycopy(this.x, 0, out, 0, this.x.length);
      return out;
   }

   public double[] getInterpolatingValues() {
      double[] out = new double[this.y.length];
      System.arraycopy(this.y, 0, out, 0, this.y.length);
      return out;
   }

   public double[] getCoefficients() {
      if (!this.coefficientsComputed) {
         this.computeCoefficients();
      }

      double[] out = new double[this.coefficients.length];
      System.arraycopy(this.coefficients, 0, out, 0, this.coefficients.length);
      return out;
   }

   public static double evaluate(double[] x, double[] y, double z) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
      if (verifyInterpolationArray(x, y, false)) {
         return evaluateInternal(x, y, z);
      } else {
         double[] xNew = new double[x.length];
         double[] yNew = new double[y.length];
         System.arraycopy(x, 0, xNew, 0, x.length);
         System.arraycopy(y, 0, yNew, 0, y.length);
         MathArrays.sortInPlace(xNew, yNew);
         verifyInterpolationArray(xNew, yNew, true);
         return evaluateInternal(xNew, yNew, z);
      }
   }

   private static double evaluateInternal(double[] x, double[] y, double z) {
      int nearest = 0;
      int n = x.length;
      double[] c = new double[n];
      double[] d = new double[n];
      double min_dist = Double.POSITIVE_INFINITY;

      for (int i = 0; i < n; i++) {
         c[i] = y[i];
         d[i] = y[i];
         double dist = FastMath.abs(z - x[i]);
         if (dist < min_dist) {
            nearest = i;
            min_dist = dist;
         }
      }

      double value = y[nearest];

      for (int ix = 1; ix < n; ix++) {
         for (int j = 0; j < n - ix; j++) {
            double tc = x[j] - z;
            double td = x[ix + j] - z;
            double divider = x[j] - x[ix + j];
            double w = (c[j + 1] - d[j]) / divider;
            c[j] = tc * w;
            d[j] = td * w;
         }

         if ((double)nearest < 0.5 * (double)(n - ix + 1)) {
            value += c[nearest];
         } else {
            value += d[--nearest];
         }
      }

      return value;
   }

   protected void computeCoefficients() {
      int n = this.degree() + 1;
      this.coefficients = new double[n];

      for (int i = 0; i < n; i++) {
         this.coefficients[i] = 0.0;
      }

      double[] c = new double[n + 1];
      c[0] = 1.0;

      for (int i = 0; i < n; i++) {
         for (int j = i; j > 0; j--) {
            c[j] = c[j - 1] - c[j] * this.x[i];
         }

         c[0] *= -this.x[i];
         c[i + 1] = 1.0;
      }

      double[] tc = new double[n];

      for (int i = 0; i < n; i++) {
         double d = 1.0;

         for (int j = 0; j < n; j++) {
            if (i != j) {
               d *= this.x[i] - this.x[j];
            }
         }

         double t = this.y[i] / d;
         tc[n - 1] = c[n];
         this.coefficients[n - 1] = this.coefficients[n - 1] + t * tc[n - 1];

         for (int jx = n - 2; jx >= 0; jx--) {
            tc[jx] = c[jx + 1] + tc[jx + 1] * this.x[i];
            this.coefficients[jx] = this.coefficients[jx] + t * tc[jx];
         }
      }

      this.coefficientsComputed = true;
   }

   public static boolean verifyInterpolationArray(double[] x, double[] y, boolean abort) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
      if (x.length != y.length) {
         throw new DimensionMismatchException(x.length, y.length);
      } else if (x.length < 2) {
         throw new NumberIsTooSmallException(LocalizedFormats.WRONG_NUMBER_OF_POINTS, 2, x.length, true);
      } else {
         return MathArrays.checkOrder(x, MathArrays.OrderDirection.INCREASING, true, abort);
      }
   }
}
