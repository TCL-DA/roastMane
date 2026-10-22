package org.apache.commons.math3.analysis.polynomials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;

public class PolynomialsUtils {
   private static final List<BigFraction> CHEBYSHEV_COEFFICIENTS = new ArrayList<>();
   private static final List<BigFraction> HERMITE_COEFFICIENTS = new ArrayList<>();
   private static final List<BigFraction> LAGUERRE_COEFFICIENTS = new ArrayList<>();
   private static final List<BigFraction> LEGENDRE_COEFFICIENTS = new ArrayList<>();
   private static final Map<PolynomialsUtils.JacobiKey, List<BigFraction>> JACOBI_COEFFICIENTS = new HashMap<>();

   private PolynomialsUtils() {
   }

   public static PolynomialFunction createChebyshevPolynomial(int degree) {
      return buildPolynomial(degree, CHEBYSHEV_COEFFICIENTS, new PolynomialsUtils.RecurrenceCoefficientsGenerator() {
         private final BigFraction[] coeffs = new BigFraction[]{BigFraction.ZERO, BigFraction.TWO, BigFraction.ONE};

         @Override
         public BigFraction[] generate(int k) {
            return this.coeffs;
         }
      });
   }

   public static PolynomialFunction createHermitePolynomial(int degree) {
      return buildPolynomial(degree, HERMITE_COEFFICIENTS, new PolynomialsUtils.RecurrenceCoefficientsGenerator() {
         @Override
         public BigFraction[] generate(int k) {
            return new BigFraction[]{BigFraction.ZERO, BigFraction.TWO, new BigFraction(2 * k)};
         }
      });
   }

   public static PolynomialFunction createLaguerrePolynomial(int degree) {
      return buildPolynomial(degree, LAGUERRE_COEFFICIENTS, new PolynomialsUtils.RecurrenceCoefficientsGenerator() {
         @Override
         public BigFraction[] generate(int k) {
            int kP1 = k + 1;
            return new BigFraction[]{new BigFraction(2 * k + 1, kP1), new BigFraction(-1, kP1), new BigFraction(k, kP1)};
         }
      });
   }

   public static PolynomialFunction createLegendrePolynomial(int degree) {
      return buildPolynomial(degree, LEGENDRE_COEFFICIENTS, new PolynomialsUtils.RecurrenceCoefficientsGenerator() {
         @Override
         public BigFraction[] generate(int k) {
            int kP1 = k + 1;
            return new BigFraction[]{BigFraction.ZERO, new BigFraction(k + kP1, kP1), new BigFraction(k, kP1)};
         }
      });
   }

   public static PolynomialFunction createJacobiPolynomial(int degree, final int v, final int w) {
      PolynomialsUtils.JacobiKey key = new PolynomialsUtils.JacobiKey(v, w);
      if (!JACOBI_COEFFICIENTS.containsKey(key)) {
         List<BigFraction> list = new ArrayList<>();
         JACOBI_COEFFICIENTS.put(key, list);
         list.add(BigFraction.ONE);
         list.add(new BigFraction(v - w, 2));
         list.add(new BigFraction(2 + v + w, 2));
      }

      return buildPolynomial(
         degree,
         JACOBI_COEFFICIENTS.get(key),
         new PolynomialsUtils.RecurrenceCoefficientsGenerator() {
            @Override
            public BigFraction[] generate(int k) {
               int kvw = ++k + v + w;
               int twoKvw = kvw + k;
               int twoKvwM1 = twoKvw - 1;
               int twoKvwM2 = twoKvw - 2;
               int den = 2 * k * kvw * twoKvwM2;
               return new BigFraction[]{
                  new BigFraction(twoKvwM1 * (v * v - w * w), den),
                  new BigFraction(twoKvwM1 * twoKvw * twoKvwM2, den),
                  new BigFraction(2 * (k + v - 1) * (k + w - 1) * twoKvw, den)
               };
            }
         }
      );
   }

   public static double[] shift(double[] coefficients, double shift) {
      int dp1 = coefficients.length;
      double[] newCoefficients = new double[dp1];
      int[][] coeff = new int[dp1][dp1];

      for (int i = 0; i < dp1; i++) {
         for (int j = 0; j <= i; j++) {
            coeff[i][j] = (int)CombinatoricsUtils.binomialCoefficient(i, j);
         }
      }

      for (int i = 0; i < dp1; i++) {
         newCoefficients[0] += coefficients[i] * FastMath.pow(shift, i);
      }

      int d = dp1 - 1;

      for (int i = 0; i < d; i++) {
         for (int j = i; j < d; j++) {
            newCoefficients[i + 1] = newCoefficients[i + 1] + (double)coeff[j + 1][j - i] * coefficients[j + 1] * FastMath.pow(shift, j - i);
         }
      }

      return newCoefficients;
   }

   private static PolynomialFunction buildPolynomial(int degree, List<BigFraction> coefficients, PolynomialsUtils.RecurrenceCoefficientsGenerator generator) {
      synchronized (coefficients) {
         int maxDegree = (int)FastMath.floor(FastMath.sqrt((double)(2 * coefficients.size()))) - 1;
         if (degree > maxDegree) {
            computeUpToDegree(degree, maxDegree, generator, coefficients);
         }
      }

      int start = degree * (degree + 1) / 2;
      double[] a = new double[degree + 1];

      for (int i = 0; i <= degree; i++) {
         a[i] = coefficients.get(start + i).doubleValue();
      }

      return new PolynomialFunction(a);
   }

   private static void computeUpToDegree(int degree, int maxDegree, PolynomialsUtils.RecurrenceCoefficientsGenerator generator, List<BigFraction> coefficients) {
      int startK = (maxDegree - 1) * maxDegree / 2;

      for (int k = maxDegree; k < degree; k++) {
         int startKm1 = startK;
         startK += k;
         BigFraction[] ai = generator.generate(k);
         BigFraction ck = coefficients.get(startK);
         BigFraction ckm1 = coefficients.get(startKm1);
         coefficients.add(ck.multiply(ai[0]).subtract(ckm1.multiply(ai[2])));

         for (int i = 1; i < k; i++) {
            BigFraction ckPrev = ck;
            ck = coefficients.get(startK + i);
            ckm1 = coefficients.get(startKm1 + i);
            coefficients.add(ck.multiply(ai[0]).add(ckPrev.multiply(ai[1])).subtract(ckm1.multiply(ai[2])));
         }

         BigFraction var12 = coefficients.get(startK + k);
         coefficients.add(var12.multiply(ai[0]).add(ck.multiply(ai[1])));
         coefficients.add(var12.multiply(ai[1]));
      }
   }

   static {
      CHEBYSHEV_COEFFICIENTS.add(BigFraction.ONE);
      CHEBYSHEV_COEFFICIENTS.add(BigFraction.ZERO);
      CHEBYSHEV_COEFFICIENTS.add(BigFraction.ONE);
      HERMITE_COEFFICIENTS.add(BigFraction.ONE);
      HERMITE_COEFFICIENTS.add(BigFraction.ZERO);
      HERMITE_COEFFICIENTS.add(BigFraction.TWO);
      LAGUERRE_COEFFICIENTS.add(BigFraction.ONE);
      LAGUERRE_COEFFICIENTS.add(BigFraction.ONE);
      LAGUERRE_COEFFICIENTS.add(BigFraction.MINUS_ONE);
      LEGENDRE_COEFFICIENTS.add(BigFraction.ONE);
      LEGENDRE_COEFFICIENTS.add(BigFraction.ZERO);
      LEGENDRE_COEFFICIENTS.add(BigFraction.ONE);
   }

   private static class JacobiKey {
      private final int v;
      private final int w;

      JacobiKey(int v, int w) {
         this.v = v;
         this.w = w;
      }

      @Override
      public int hashCode() {
         return this.v << 16 ^ this.w;
      }

      @Override
      public boolean equals(Object key) {
         if (key != null && key instanceof PolynomialsUtils.JacobiKey) {
            PolynomialsUtils.JacobiKey otherK = (PolynomialsUtils.JacobiKey)key;
            return this.v == otherK.v && this.w == otherK.w;
         } else {
            return false;
         }
      }
   }

   private interface RecurrenceCoefficientsGenerator {
      BigFraction[] generate(int var1);
   }
}
