package org.apache.commons.math3.analysis.integration.gauss;

import java.math.BigDecimal;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.util.Pair;

public class GaussIntegratorFactory {
   private final BaseRuleFactory<Double> legendre = new LegendreRuleFactory();
   private final BaseRuleFactory<BigDecimal> legendreHighPrecision = new LegendreHighPrecisionRuleFactory();
   private final BaseRuleFactory<Double> hermite = new HermiteRuleFactory();

   public GaussIntegrator legendre(int numberOfPoints) {
      return new GaussIntegrator(getRule(this.legendre, numberOfPoints));
   }

   public GaussIntegrator legendre(int numberOfPoints, double lowerBound, double upperBound) throws NotStrictlyPositiveException {
      return new GaussIntegrator(transform(getRule(this.legendre, numberOfPoints), lowerBound, upperBound));
   }

   public GaussIntegrator legendreHighPrecision(int numberOfPoints) throws NotStrictlyPositiveException {
      return new GaussIntegrator(getRule(this.legendreHighPrecision, numberOfPoints));
   }

   public GaussIntegrator legendreHighPrecision(int numberOfPoints, double lowerBound, double upperBound) throws NotStrictlyPositiveException {
      return new GaussIntegrator(transform(getRule(this.legendreHighPrecision, numberOfPoints), lowerBound, upperBound));
   }

   public SymmetricGaussIntegrator hermite(int numberOfPoints) {
      return new SymmetricGaussIntegrator(getRule(this.hermite, numberOfPoints));
   }

   private static Pair<double[], double[]> getRule(BaseRuleFactory<? extends Number> factory, int numberOfPoints) throws NotStrictlyPositiveException, DimensionMismatchException {
      return factory.getRule(numberOfPoints);
   }

   private static Pair<double[], double[]> transform(Pair<double[], double[]> rule, double a, double b) {
      double[] points = rule.getFirst();
      double[] weights = rule.getSecond();
      double scale = (b - a) / 2.0;
      double shift = a + scale;

      for (int i = 0; i < points.length; i++) {
         points[i] = points[i] * scale + shift;
         weights[i] *= scale;
      }

      return new Pair<>(points, weights);
   }
}
