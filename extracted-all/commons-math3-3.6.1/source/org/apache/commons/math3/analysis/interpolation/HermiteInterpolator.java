package org.apache.commons.math3.analysis.interpolation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableVectorFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class HermiteInterpolator implements UnivariateDifferentiableVectorFunction {
   private final List<Double> abscissae = new ArrayList<>();
   private final List<double[]> topDiagonal = new ArrayList<>();
   private final List<double[]> bottomDiagonal = new ArrayList<>();

   public void addSamplePoint(double x, double[]... value) throws ZeroException, MathArithmeticException {
      for (int i = 0; i < value.length; i++) {
         double[] y = (double[])value[i].clone();
         if (i > 1) {
            double inv = 1.0 / (double)CombinatoricsUtils.factorial(i);

            for (int j = 0; j < y.length; j++) {
               y[j] *= inv;
            }
         }

         int n = this.abscissae.size();
         this.bottomDiagonal.add(n - i, y);
         double[] bottom0 = y;

         for (int j = i; j < n; j++) {
            double[] bottom1 = this.bottomDiagonal.get(n - (j + 1));
            double inv = 1.0 / (x - this.abscissae.get(n - (j + 1)));
            if (Double.isInfinite(inv)) {
               throw new ZeroException(LocalizedFormats.DUPLICATED_ABSCISSA_DIVISION_BY_ZERO, x);
            }

            for (int k = 0; k < y.length; k++) {
               bottom1[k] = inv * (bottom0[k] - bottom1[k]);
            }

            bottom0 = bottom1;
         }

         this.topDiagonal.add((double[])bottom0.clone());
         this.abscissae.add(x);
      }
   }

   public PolynomialFunction[] getPolynomials() throws NoDataException {
      this.checkInterpolation();
      PolynomialFunction zero = this.polynomial(0.0);
      PolynomialFunction[] polynomials = new PolynomialFunction[((double[])this.topDiagonal.get(0)).length];

      for (int i = 0; i < polynomials.length; i++) {
         polynomials[i] = zero;
      }

      PolynomialFunction coeff = this.polynomial(1.0);

      for (int i = 0; i < this.topDiagonal.size(); i++) {
         double[] tdi = this.topDiagonal.get(i);

         for (int k = 0; k < polynomials.length; k++) {
            polynomials[k] = polynomials[k].add(coeff.multiply(this.polynomial(tdi[k])));
         }

         coeff = coeff.multiply(this.polynomial(-this.abscissae.get(i), 1.0));
      }

      return polynomials;
   }

   @Override
   public double[] value(double x) throws NoDataException {
      this.checkInterpolation();
      double[] value = new double[((double[])this.topDiagonal.get(0)).length];
      double valueCoeff = 1.0;

      for (int i = 0; i < this.topDiagonal.size(); i++) {
         double[] dividedDifference = this.topDiagonal.get(i);

         for (int k = 0; k < value.length; k++) {
            value[k] += dividedDifference[k] * valueCoeff;
         }

         double deltaX = x - this.abscissae.get(i);
         valueCoeff *= deltaX;
      }

      return value;
   }

   @Override
   public DerivativeStructure[] value(DerivativeStructure x) throws NoDataException {
      this.checkInterpolation();
      DerivativeStructure[] value = new DerivativeStructure[((double[])this.topDiagonal.get(0)).length];
      Arrays.fill(value, x.getField().getZero());
      DerivativeStructure valueCoeff = x.getField().getOne();

      for (int i = 0; i < this.topDiagonal.size(); i++) {
         double[] dividedDifference = this.topDiagonal.get(i);

         for (int k = 0; k < value.length; k++) {
            value[k] = value[k].add(valueCoeff.multiply(dividedDifference[k]));
         }

         DerivativeStructure deltaX = x.subtract(this.abscissae.get(i).doubleValue());
         valueCoeff = valueCoeff.multiply(deltaX);
      }

      return value;
   }

   private void checkInterpolation() throws NoDataException {
      if (this.abscissae.isEmpty()) {
         throw new NoDataException(LocalizedFormats.EMPTY_INTERPOLATION_SAMPLE);
      }
   }

   private PolynomialFunction polynomial(double... c) {
      return new PolynomialFunction(c);
   }
}
