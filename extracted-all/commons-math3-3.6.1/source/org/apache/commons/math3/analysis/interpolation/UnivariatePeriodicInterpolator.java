package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class UnivariatePeriodicInterpolator implements UnivariateInterpolator {
   public static final int DEFAULT_EXTEND = 5;
   private final UnivariateInterpolator interpolator;
   private final double period;
   private final int extend;

   public UnivariatePeriodicInterpolator(UnivariateInterpolator interpolator, double period, int extend) {
      this.interpolator = interpolator;
      this.period = period;
      this.extend = extend;
   }

   public UnivariatePeriodicInterpolator(UnivariateInterpolator interpolator, double period) {
      this(interpolator, period, 5);
   }

   @Override
   public UnivariateFunction interpolate(double[] xval, double[] yval) throws NumberIsTooSmallException, NonMonotonicSequenceException {
      if (xval.length < this.extend) {
         throw new NumberIsTooSmallException(xval.length, this.extend, true);
      } else {
         MathArrays.checkOrder(xval);
         final double offset = xval[0];
         int len = xval.length + this.extend * 2;
         double[] x = new double[len];
         double[] y = new double[len];

         for (int i = 0; i < xval.length; i++) {
            int index = i + this.extend;
            x[index] = MathUtils.reduce(xval[i], this.period, offset);
            y[index] = yval[i];
         }

         for (int i = 0; i < this.extend; i++) {
            int index = xval.length - this.extend + i;
            x[i] = MathUtils.reduce(xval[index], this.period, offset) - this.period;
            y[i] = yval[index];
            index = len - this.extend + i;
            x[index] = MathUtils.reduce(xval[i], this.period, offset) + this.period;
            y[index] = yval[i];
         }

         MathArrays.sortInPlace(x, y);
         final UnivariateFunction f = this.interpolator.interpolate(x, y);
         return new UnivariateFunction() {
            @Override
            public double value(double x) throws MathIllegalArgumentException {
               return f.value(MathUtils.reduce(x, UnivariatePeriodicInterpolator.this.period, offset));
            }
         };
      }
   }
}
