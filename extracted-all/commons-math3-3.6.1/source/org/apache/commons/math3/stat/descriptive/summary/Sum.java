package org.apache.commons.math3.stat.descriptive.summary;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;

public class Sum extends AbstractStorelessUnivariateStatistic implements Serializable {
   private static final long serialVersionUID = -8231831954703408316L;
   private long n;
   private double value;

   public Sum() {
      this.n = 0L;
      this.value = 0.0;
   }

   public Sum(Sum original) throws NullArgumentException {
      copy(original, this);
   }

   @Override
   public void increment(double d) {
      this.value += d;
      this.n++;
   }

   @Override
   public double getResult() {
      return this.value;
   }

   @Override
   public long getN() {
      return this.n;
   }

   @Override
   public void clear() {
      this.value = 0.0;
      this.n = 0L;
   }

   @Override
   public double evaluate(double[] values, int begin, int length) throws MathIllegalArgumentException {
      double sum = Double.NaN;
      if (this.test(values, begin, length, true)) {
         sum = 0.0;

         for (int i = begin; i < begin + length; i++) {
            sum += values[i];
         }
      }

      return sum;
   }

   public double evaluate(double[] values, double[] weights, int begin, int length) throws MathIllegalArgumentException {
      double sum = Double.NaN;
      if (this.test(values, weights, begin, length, true)) {
         sum = 0.0;

         for (int i = begin; i < begin + length; i++) {
            sum += values[i] * weights[i];
         }
      }

      return sum;
   }

   public double evaluate(double[] values, double[] weights) throws MathIllegalArgumentException {
      return this.evaluate(values, weights, 0, values.length);
   }

   public Sum copy() {
      Sum result = new Sum();
      copy(this, result);
      return result;
   }

   public static void copy(Sum source, Sum dest) throws NullArgumentException {
      MathUtils.checkNotNull(source);
      MathUtils.checkNotNull(dest);
      dest.setData(source.getDataRef());
      dest.n = source.n;
      dest.value = source.value;
   }
}
